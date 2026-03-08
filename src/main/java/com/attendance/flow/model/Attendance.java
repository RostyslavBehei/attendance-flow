package com.attendance.flow.model;

import com.attendance.flow.model.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attendances", schema = "attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"lesson_session_id", "student_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_session_id", nullable = false)
    private LessonSession lessonSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @Column(name = "comment", length = 255)
    private String comment;
}
