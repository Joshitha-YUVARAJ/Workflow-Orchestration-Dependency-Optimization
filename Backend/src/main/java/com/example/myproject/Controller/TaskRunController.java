package com.example.myproject.Controller;


import com.example.myproject.Model.TaskRun;
import com.example.myproject.Service.WorkflowExecutionService;
import com.example.myproject.dto.TaskRunResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task-runs")
public class TaskRunController {
    private final WorkflowExecutionService executionService;

    public TaskRunController(WorkflowExecutionService executionService) {
        this.executionService = executionService;
    }

    //  User completes a manual task
    @PatchMapping("/{taskRunId}/complete")
    public ResponseEntity<TaskRunResponseDTO> completeManualTask(
            @PathVariable Long taskRunId
    ) {
        TaskRun taskRun = executionService.completeManualTask(taskRunId);

        TaskRunResponseDTO response = new TaskRunResponseDTO(
                taskRun.getId(),
                taskRun.getWorkflowRun().getId(),
                taskRun.getTask().getId(),
                taskRun.getTask().getName(),
                taskRun.getStatus().name(),
                taskRun.getStartedAt(),
                taskRun.getEndedAt()
        );

        return ResponseEntity.ok(response);
    }

}
