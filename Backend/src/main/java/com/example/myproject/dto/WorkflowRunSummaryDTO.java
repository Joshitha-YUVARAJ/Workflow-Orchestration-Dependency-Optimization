package com.example.myproject.dto;

import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class WorkflowRunSummaryDTO {

    private Long workflowRunId;
    private Long workflowId;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public WorkflowRunSummaryDTO(
            Long workflowRunId,
            Long workflowId,
            String status,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        this.workflowRunId = workflowRunId;
        this.workflowId =workflowId;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    // getters
}