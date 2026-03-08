package com.attendance.flow.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lesson_session", schema = "attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"lesson_id", "date_time"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lesson_Id", nullable = false)
    private Lesson lesson;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "topic", length = 255)
    private String topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by_id", nullable = false)
    private User markedBy;

    @OneToMany(mappedBy = "lessonSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Attendance> attendances = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addAttendance(Attendance attendance) {
        attendances.add(attendance);
        attendance.setLessonSession(this);
    }
}
