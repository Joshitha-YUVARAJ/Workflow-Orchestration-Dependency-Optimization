package com.example.myproject.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workflow_run")
@Getter
@Setter
public class WorkflowRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which workflow is being executed
    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private WorkflowRunStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Column(name = "execution_order", columnDefinition = "TEXT", nullable = false)
    private String executionOrder;

    // All task executions for this workflow run
    @OneToMany(mappedBy = "workflowRun", cascade = CascadeType.ALL)
    private List<TaskRun> taskRuns;

    public enum WorkflowRunStatus {
        RUNNING,
        COMPLETED,
        WAITING,
        FAILED
    }
}
