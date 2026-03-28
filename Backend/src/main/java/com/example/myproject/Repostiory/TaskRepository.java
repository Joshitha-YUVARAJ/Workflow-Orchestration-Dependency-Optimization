package com.example.myproject.Repostiory;

import com.example.myproject.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByWorkflowId(Long workflowId);
    Optional<Task> findByIdAndWorkflowId(Long taskId, Long workflowId);

}
