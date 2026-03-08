package com.attendance.flow.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_groups", schema = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    @Column(name = "lesson_duration_minutes", nullable = false)
    @Builder.Default
    private Integer lessonDurationMinutes = 90;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Schedule> schedules = new HashSet<>();

    @ManyToMany(mappedBy = "groups")
    @Builder.Default
    private Set<User> students = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "group_teachers",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    @Builder.Default
    private Set<User> teachers = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addTeacher(User teacher) {
        this.teachers.add(teacher);
        teacher.getGroups().add(this);
    }

    public void addStudent(User student) {
        this.students.add(student);
        student.getGroups().add(this);
    }

    public void removeTeacher(User teacher) {
        this.teachers.remove(teacher);
        teacher.getGroups().remove(this);
    }

    public void removeStudent(User student) {
        this.students.remove(student);
        student.getGroups().remove(this);
    }
}
