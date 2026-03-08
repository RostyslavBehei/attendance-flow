package com.attendance.flow.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schedule", schema = "attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_id", "day_of_week"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private AppGroup group;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lessonNumber ASC")
    @Builder.Default
    private List<Lesson> lessons = new ArrayList<>();

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
        lesson.setSchedule(this);
    }

    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
        lesson.setSchedule(null);
    }
}
