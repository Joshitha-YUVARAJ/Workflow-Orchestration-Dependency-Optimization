package com.example.myproject.Service;

import com.example.myproject.Model.*;
import com.example.myproject.Repostiory.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkflowExecutionService {

    private final WorkflowRepository workflowRepo;
    private final TaskRepository taskRepo;
    private final WorkflowRunRepository workflowRunRepo;
    private final TaskRunRepository taskRunRepo;
    private final WorkflowGraphService graphService;

    public WorkflowExecutionService(
            WorkflowRepository workflowRepo,
            TaskRepository taskRepo,
            WorkflowRunRepository workflowRunRepo,
            TaskRunRepository taskRunRepo,
            WorkflowGraphService graphService
    ) {
        this.workflowRepo = workflowRepo;
        this.taskRepo = taskRepo;
        this.workflowRunRepo = workflowRunRepo;
        this.taskRunRepo = taskRunRepo;
        this.graphService = graphService;
    }

    // ===============================
    // MAIN ENTRY POINT
    // ===============================
    @Transactional
    public WorkflowRun executeWorkflow(Long workflowId) {

        Workflow workflow = workflowRepo.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        //  Validate & get execution order
        List<Long> executionOrder =
                graphService.getExecutionOrder(workflowId).getOrderedTaskIds();

        // Create WorkflowRun
        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflow(workflow);
        workflowRun.setStatus(WorkflowRun.WorkflowRunStatus.RUNNING);
        workflowRun.setStartedAt(LocalDateTime.now());

        workflowRun.setExecutionOrder(
                executionOrder.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","))
        );
        workflowRunRepo.save(workflowRun);

        // Create TaskRuns (initially PENDING)
        for (Long taskId : executionOrder) {
            Task task = taskRepo.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found"));

            TaskRun taskRun = new TaskRun();
            taskRun.setTask(task);
            taskRun.setWorkflowRun(workflowRun);
            taskRun.setStatus(TaskRun.TaskRunStatus.PENDING);

            taskRunRepo.save(taskRun);
        }

        // Execute tasks sequentially
        runExecutionLoop(workflowRun);

        return workflowRunRepo.save(workflowRun);
    }

    // ===============================
    // CORE EXECUTION LOOP
    // ===============================
    private void runExecutionLoop(WorkflowRun workflowRun) {

        List<Long> executionOrder = Arrays.stream(
                        workflowRun.getExecutionOrder().split(","))
                .map(Long::valueOf)
                .toList();

        try {
            for (Long taskId : executionOrder) {

                TaskRun taskRun = taskRunRepo
                        .findByWorkflowRunIdAndTaskId(workflowRun.getId(), taskId)
                        .orElseThrow();

                // Skip already completed tasks (important for resume)
                if (taskRun.getStatus() == TaskRun.TaskRunStatus.COMPLETED) {
                    continue;
                }

                boolean shouldContinue = executeSingleTask(taskRun);

                if (!shouldContinue) {
                    workflowRun.setStatus(WorkflowRun.WorkflowRunStatus.WAITING);
                    return;
                }
            }

            workflowRun.setStatus(WorkflowRun.WorkflowRunStatus.COMPLETED);
            workflowRun.setEndedAt(LocalDateTime.now());

        } catch (Exception e) {
            workflowRun.setStatus(WorkflowRun.WorkflowRunStatus.FAILED);
            workflowRun.setEndedAt(LocalDateTime.now());
            throw e;
        }
    }

    // ===============================
    // EXECUTE SINGLE TASK
    // ===============================
    private boolean executeSingleTask(TaskRun taskRun) {

        Task task = taskRun.getTask();

        // MANUAL TASK → PAUSE
        if (task.getTaskType() == Task.TaskType.MANUAL) {
            taskRun.setStatus(TaskRun.TaskRunStatus.WAITING_FOR_USER);
            taskRun.setStartedAt(LocalDateTime.now());
            taskRunRepo.save(taskRun);
            return false; // pause workflow
        }

        // AUTOMATED TASK
        try {
            taskRun.setStatus(TaskRun.TaskRunStatus.RUNNING);
            taskRun.setStartedAt(LocalDateTime.now());
            taskRunRepo.save(taskRun);

            simulateExecution(task);

            taskRun.setStatus(TaskRun.TaskRunStatus.COMPLETED);
            taskRun.setEndedAt(LocalDateTime.now());
            taskRunRepo.save(taskRun);

            return true;

        } catch (Exception e) {
            taskRun.setStatus(TaskRun.TaskRunStatus.FAILED);
            taskRun.setEndedAt(LocalDateTime.now());
            taskRun.setErrorMessage(e.getMessage());
            taskRunRepo.save(taskRun);
            throw e;
        }
    }

    // ===============================
    // SIMULATION (AUTOMATED TASKS)
    // ===============================
    private void simulateExecution(Task task) {
        try {
            if (task.getEstimatedDuration() != null) {
                Thread.sleep(task.getEstimatedDuration() * 100L);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Execution interrupted");
        }
    }

    // ===============================
    // USER COMPLETES MANUAL TASK
    // ===============================
    @Transactional
    public TaskRun completeManualTask(Long taskRunId) {

        TaskRun taskRun = taskRunRepo.findById(taskRunId)
                .orElseThrow(() -> new RuntimeException("TaskRun not found"));

        if (taskRun.getStatus() != TaskRun.TaskRunStatus.WAITING_FOR_USER) {
            throw new RuntimeException("Task is not waiting for user");
        }

        taskRun.setStatus(TaskRun.TaskRunStatus.COMPLETED);
        taskRun.setEndedAt(LocalDateTime.now());
        taskRunRepo.save(taskRun);

        // Resume workflow
        WorkflowRun workflowRun = taskRun.getWorkflowRun();
        workflowRun.setStatus(WorkflowRun.WorkflowRunStatus.RUNNING);
        workflowRunRepo.save(workflowRun);

        runExecutionLoop(workflowRun);

        return taskRun;
    }
    public List<TaskRun> getWaitingManualTasks(Long workflowRunId) {
        return taskRunRepo.findByWorkflowRunIdAndStatus(
                workflowRunId,
                TaskRun.TaskRunStatus.WAITING_FOR_USER
        );
    }
}
