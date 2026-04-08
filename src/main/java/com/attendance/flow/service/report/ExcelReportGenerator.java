package com.attendance.flow.service.report;

import com.attendance.flow.model.*;
import com.attendance.flow.repository.AppGroupRepository;
import com.attendance.flow.repository.AttendanceRepository;
import com.attendance.flow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelReportGenerator implements ReportGenerator {

    private final UserRepository userRepository;
    private final AppGroupRepository appGroupRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public byte[] generateMonthlyReport(int year, int month, Long groupId) {
        AppGroup group = appGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group id " + groupId + " not found"));

        double lessonDurabilityHours = group.getLessonDurationMinutes() / 60.0;

        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Schedule " + month + "-" + year);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Id");
            headerRow.createCell(1).setCellValue("First name, last name");

            for (int day = 1; day <= daysInMonth; day++) {
                int countIndex = day + 1;
                Cell cell = headerRow.createCell(countIndex);
                cell.setCellValue(day);
                cell.setCellStyle(centerStyle);

                sheet.setColumnWidth(countIndex, 1000);
            }

            headerRow.createCell(daysInMonth + 2).setCellValue("Absent count");

            Set<User> users = group.getStudents();

            List<Long> studentIds = users.stream()
                    .map(User::getId)
                    .toList();

            List<Attendance> allGroupAttendance = attendanceRepository.findByStudentIdInAndLessonSession_DateTimeBetween(studentIds, startOfMonth, endOfMonth);

            Map<Long, List<Attendance>> attendanceByStudent = allGroupAttendance.stream()
                    .collect(Collectors.groupingBy(a -> a.getStudent().getId()));

            int rowIndex = 1;

            for (User user : users) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getFirstName() +  " " + user.getLastName());

                List<Attendance> attendances = attendanceByStudent.getOrDefault(user.getId(), Collections.emptyList());

                double totalAbsentCount = 0;

                for (int day = 1; day <= daysInMonth; day++) {
                    int countIndex = day + 1;

                    Cell cell = row.createCell(countIndex);

                    int currentDay = day;

                    long recordInThisDay = attendances.stream()
                            .filter(a -> a.getLessonSession().getDateTime().getDayOfMonth() == currentDay)
                            .filter(a -> a.getStatus().name().equals("ABSENT"))
                            .count();

                    if (recordInThisDay > 0) {
                        double attendanceCountInDay = recordInThisDay * lessonDurabilityHours;
                        cell.setCellValue(attendanceCountInDay);
                        cell.setCellStyle(centerStyle);

                        totalAbsentCount += attendanceCountInDay;
                    } else {
                        cell.setCellValue("");
                    }
                }

                Cell totalCell = row.createCell(daysInMonth + 2);
                totalCell.setCellValue(totalAbsentCount);
                totalCell.setCellStyle(centerStyle);
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel file: " + e.getMessage());
        }
    }

    @Override
    public byte[] generateMonthlyStudentReportBySubject(int year, int month, Long groupId, Long studentId) {
        AppGroup group = appGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group with id " + groupId + " not found"));

        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student with id " + studentId + " not found"));

        if (!group.getStudents().contains(user)) {
            throw new RuntimeException("Student does not belong to the specified group");
        }

        double lessonDurabilityHours = group.getLessonDurationMinutes() / 60.0;

        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        try (Workbook workbook = new XSSFWorkbook();
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Schedule " + month + "-" + year);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Id");
            headerRow.createCell(1).setCellValue("Subject name");

            for (int day = 1; day <= daysInMonth; day++) {
                int countIndex = day + 1;
                Cell cell = headerRow.createCell(countIndex);
                cell.setCellValue(day);
                cell.setCellStyle(centerStyle);

                sheet.setColumnWidth(countIndex, 1000);
            }

            headerRow.createCell(daysInMonth + 2).setCellValue("Absent count");

            List<Subject> subjects = getWeeklySubjects(group);

            List<String> subjectNames = subjects.stream()
                    .map(Subject::getName)
                    .toList();

            List<Attendance> rawAttendances = attendanceRepository.findByStudentIdAndLessonSession_DateTimeBetweenAndLessonSession_Lesson_Subject_NameIn(studentId, startOfMonth, endOfMonth, subjectNames);
            Map<String, List<Attendance>> attendancesBySubject = rawAttendances.stream()
                    .collect(Collectors.groupingBy(a -> a.getLessonSession().getLesson().getSubject().getName()));

            int rowIndex = 1;
            for (Subject subject : subjects) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(subject.getId());
                row.createCell(1).setCellValue(subject.getName());

                List<Attendance> attendances = attendancesBySubject.getOrDefault(subject.getName(), Collections.emptyList());

                double totalAbsentCount = 0;

                for (int day = 1; day <= daysInMonth; day++) {
                    int countIndex = day + 1;

                    Cell cell = row.createCell(countIndex);

                    int currentDay = day;

                    long recordInThisDay = attendances.stream()
                            .filter(a -> a.getLessonSession().getDateTime().getDayOfMonth() == currentDay)
                            .filter(a -> a.getStatus().name().equals("ABSENT"))
                            .count();

                    if (recordInThisDay > 0) {
                        double attendanceCountInDay = recordInThisDay * lessonDurabilityHours;
                        cell.setCellValue(attendanceCountInDay);
                        cell.setCellStyle(centerStyle);

                        totalAbsentCount += attendanceCountInDay;
                    } else {
                        cell.setCellValue("");
                    }
                }

                Cell totalCell = row.createCell(daysInMonth + 2);
                totalCell.setCellValue(totalAbsentCount);
                totalCell.setCellStyle(centerStyle);
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel file: " + e.getMessage());
        }
    }

    private List<Subject> getWeeklySubjects(AppGroup group) {
        return group.getSchedules().stream()
                .flatMap(schedule -> schedule.getLessons().stream())
                .map(Lesson::getSubject)
                .distinct()
                .toList();
    }


    @Override
    public boolean supportsFormat(String format) {
        return "excel".equalsIgnoreCase(format);
    }

    @Override
    public String getContentType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String getFileExtension() {
        return ".xlsx";
    }
}
