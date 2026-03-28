package com.example.myproject.Controller;

import com.example.myproject.Model.TaskRun;
import com.example.myproject.Model.WorkflowRun;
import com.example.myproject.Repostiory.TaskRunRepository;
import com.example.myproject.Repostiory.WorkflowRunRepository;
import com.example.myproject.Service.WorkflowExecutionService;
import com.example.myproject.dto.TaskRunResponseDTO;
import com.example.myproject.dto.WorkflowRunSummaryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

    import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
    @RequestMapping("/api/workflow-runs")
    public class WorkflowRunController {

        private final WorkflowRunRepository workflowRunRepo;
        private final TaskRunRepository taskRunRepo;
        private final WorkflowExecutionService executionService;

        public WorkflowRunController(WorkflowRunRepository workflowRunRepo,TaskRunRepository taskRunRepo,WorkflowExecutionService executionService) {
            this.workflowRunRepo = workflowRunRepo;
            this.taskRunRepo=taskRunRepo;
            this.executionService=executionService;
        }

        @GetMapping("/{workflowRunId}")
        public ResponseEntity<WorkflowRunSummaryDTO> getWorkflowRun(@PathVariable Long workflowRunId) {
            WorkflowRun run = workflowRunRepo.findById(workflowRunId)
                    .orElseThrow(() -> new RuntimeException("WorkflowRun not found"));

            WorkflowRunSummaryDTO response = new WorkflowRunSummaryDTO(
                    run.getId(),
                    run.getWorkflow().getId(),
                    run.getStatus().name(),
                    run.getStartedAt(),
                    run.getEndedAt()
            );
            return ResponseEntity.ok(response);
        }
        @GetMapping("/{workflowId}/runs")
        public List<WorkflowRunSummaryDTO> getAllRuns(@PathVariable Long workflowId) {

            return workflowRunRepo.findByWorkflowIdOrderByStartedAtDesc(workflowId)
                    .stream()
                    .map(run -> new WorkflowRunSummaryDTO(
                            run.getId(),
                            run.getWorkflow().getId(),
                            run.getStatus().name(),
                            run.getStartedAt(),
                            run.getEndedAt()
                    ))
                    .toList();
        }
        @GetMapping("/{workflowRunId}/task-runs")
        public List<TaskRunResponseDTO> getTaskRuns(@PathVariable Long workflowRunId) {

            return taskRunRepo.findByWorkflowRunId(workflowRunId)
                    .stream()
                    .map(tr -> new TaskRunResponseDTO(
                            tr.getId(),
                            tr.getWorkflowRun().getId(),
                            tr.getTask().getId(),
                            tr.getTask().getName(),
                            tr.getStatus().name(),
                            tr.getStartedAt(),
                            tr.getEndedAt()
                    ))
                    .toList();
        }
        @GetMapping("/{workflowRunId}/waiting-task")
        public List<TaskRunResponseDTO> getWaitingManualTasks(@PathVariable Long workflowRunId) {

            return executionService.getWaitingManualTasks(workflowRunId)
                    .stream()
                    .map(tr -> new TaskRunResponseDTO(
                            tr.getId(),
                            tr.getWorkflowRun().getId(),
                            tr.getTask().getId(),
                            tr.getTask().getName(),
                            tr.getStatus().name(),
                            tr.getStartedAt(),
                            tr.getEndedAt()
                    ))
                    .toList();
        }

    }


