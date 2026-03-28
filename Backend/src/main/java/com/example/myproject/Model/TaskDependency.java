package com.example.myproject.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "task_dependency")
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;
    @ManyToOne
    @JoinColumn(name = "source_task_id", nullable = false)

    private Task sourceTask;
    @ManyToOne
    @JoinColumn(name = "target_task_id", nullable = false)
    private Task targetTask;

    // getters and setters
}