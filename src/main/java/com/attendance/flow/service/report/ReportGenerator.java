package com.attendance.flow.service.report;

public interface ReportGenerator {

    byte[] generateMonthlyReport(int year, int month, Long groupId);

    byte[] generateMonthlyStudentReportBySubject(int year, int month, Long groupId, Long studentId);

    boolean supportsFormat(String format);

    String getContentType();

    String getFileExtension();
}
