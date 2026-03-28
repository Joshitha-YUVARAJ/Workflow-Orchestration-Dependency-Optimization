package com.example.myproject.Controller;

import com.example.myproject.Model.WorkflowRun;
import com.example.myproject.Service.WorkflowExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowExecutionController {
    private final WorkflowExecutionService executionService;

    public WorkflowExecutionController(WorkflowExecutionService executionService) {
        this.executionService = executionService;
    }

    // Start workflow execution
    @PostMapping("/{workflowId}/execute")
    public ResponseEntity<WorkflowRun> executeWorkflow(
            @PathVariable Long workflowId
    ) {
        WorkflowRun workflowRun = executionService.executeWorkflow(workflowId);
        return ResponseEntity.ok(workflowRun);
    }
}
