package com.example.myproject.Repostiory;

import com.example.myproject.Model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
}