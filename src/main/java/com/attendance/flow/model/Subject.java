package com.attendance.flow.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects", schema = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;
}
