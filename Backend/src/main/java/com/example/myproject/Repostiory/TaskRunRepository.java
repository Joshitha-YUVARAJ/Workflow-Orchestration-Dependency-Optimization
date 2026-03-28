package com.example.myproject.Repostiory;

import com.example.myproject.Model.TaskRun;
import com.example.myproject.Model.TaskRun.TaskRunStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRunRepository extends JpaRepository<TaskRun, Long> {

    //  Find a specific TaskRun for a workflow execution
    Optional<TaskRun> findByWorkflowRunIdAndTaskId(Long workflowRunId, Long taskId);

    //  Get all TaskRuns for a workflow execution
    List<TaskRun> findByWorkflowRunId(Long workflowRunId);

    //  Find tasks waiting for user action (MANUAL tasks)
    List<TaskRun> findByWorkflowRunIdAndStatus(
            Long workflowRunId,
            TaskRunStatus status
    );

}
