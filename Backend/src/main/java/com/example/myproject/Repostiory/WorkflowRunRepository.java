package com.example.myproject.Repostiory;

import com.example.myproject.Model.TaskRun;
import com.example.myproject.Model.WorkflowRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowRunRepository extends JpaRepository<WorkflowRun, Long> {
    List<WorkflowRun> findByWorkflowIdOrderByStartedAtDesc(Long workflowId);

}
