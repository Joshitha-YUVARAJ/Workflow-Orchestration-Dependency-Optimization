package com.example.myproject.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_run")
@Getter
@Setter
public class TaskRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which workflow execution this belongs to
    @ManyToOne
    @JoinColumn(name = "workflow_run_id", nullable = false)
    private WorkflowRun workflowRun;

    // Which task definition this run is for
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private TaskRunStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Column(length = 255)
    private String errorMessage;

    public enum TaskRunStatus {
        PENDING,            // Not ready yet
        READY,              // Dependencies satisfied
        RUNNING,            // Automated task running
        WAITING_FOR_USER,   // Manual task waiting for user
        COMPLETED,
        FAILED
    }
}
