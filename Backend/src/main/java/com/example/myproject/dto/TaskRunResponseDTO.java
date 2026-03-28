package com.example.myproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TaskRunResponseDTO {

    private Long taskRunId;
    private Long workflowRunId;
    private Long taskId;
    private String taskName;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;


}
