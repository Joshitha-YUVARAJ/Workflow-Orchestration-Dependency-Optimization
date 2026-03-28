package com.example.myproject.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    @JsonBackReference
    private Workflow workflow;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String description;

    private Integer estimatedDuration;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TaskType taskType;

    private LocalDate createdAt;

    @OneToMany(mappedBy = "sourceTask")
    private List<TaskDependency> outgoingDependencies;

    @OneToMany(mappedBy = "targetTask")
    private List<TaskDependency> incomingDependencies;

    public enum TaskType {
        MANUAL,
        AUTOMATED
    }
    // getters and setters
}
