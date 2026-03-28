package com.example.myproject.Service;

import com.example.myproject.Model.Workflow;
import com.example.myproject.Repostiory.WorkflowRepository;
import com.example.myproject.dto.WorkflowUpdateDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepo;

    public WorkflowService(WorkflowRepository workflowRepo) {
        this.workflowRepo = workflowRepo;
    }

    public Workflow createWorkflow(Workflow workflow) {
        workflow.setCreatedAt(LocalDate.now());
        workflow.setStatus(Workflow.Status.DRAFT);
        return workflowRepo.save(workflow);
    }

    public List<Workflow> getAllWorkflows() {
        return workflowRepo.findAll();
    }

    public Workflow getByIdWorkflows(Long Id){ return workflowRepo.findById(Id).orElseThrow();}

    public void deleteWorkflows(Long Id){ workflowRepo.deleteById(Id);}

    public Workflow updateWorkflows(Long Id, WorkflowUpdateDTO dto){
        Workflow workflow = workflowRepo.findById(Id).orElseThrow();

        if (dto.getName() != null){
            workflow.setName(dto.getName());
        }
        if (dto.getDescription() != null){
            workflow.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null){
            workflow.setStatus(dto.getStatus());
        }
        return workflowRepo.save(workflow);
    }
}