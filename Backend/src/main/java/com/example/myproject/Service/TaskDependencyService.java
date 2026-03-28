package com.example.myproject.Service;

import com.example.myproject.Model.Task;
import com.example.myproject.Model.TaskDependency;
import com.example.myproject.Model.Workflow;
import com.example.myproject.Repostiory.TaskDependencyRepository;
import com.example.myproject.Repostiory.TaskRepository;
import com.example.myproject.Repostiory.WorkflowRepository;
import com.example.myproject.dto.TaskDependencyResponseDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class TaskDependencyService {

    private final TaskDependencyRepository dependencyRepo;
    private final TaskRepository taskRepo;
    private final WorkflowRepository workflowRepo;

    public TaskDependencyService(TaskDependencyRepository dependencyRepo,
                                 TaskRepository taskRepo, WorkflowRepository workflowRepo) {
        this.dependencyRepo = dependencyRepo;
        this.taskRepo = taskRepo;
        this.workflowRepo = workflowRepo;
    }

    public TaskDependencyResponseDTO addDependency(Long sourceTaskId, Long targetTaskId, Long workflowId) {

        Workflow workflow = workflowRepo.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        TaskDependency dependency=new TaskDependency();
        dependency.setWorkflow(workflow);
        Task source = taskRepo.findById(sourceTaskId)
                .orElseThrow(() -> new RuntimeException("Source task not found"));
        dependency.setSourceTask(source);

        Task target = taskRepo.findById(targetTaskId)
                .orElseThrow(() -> new RuntimeException("Target task not found"));
        dependency.setTargetTask(target);

        TaskDependency dep = new TaskDependency();
        dep.setWorkflow(source.getWorkflow());
        dep.setSourceTask(source);
        dep.setTargetTask(target);
        TaskDependency saved = dependencyRepo.save(dependency);

        TaskDependencyResponseDTO dto = new TaskDependencyResponseDTO();
        dto.setId(saved.getId());
        dto.setSourceTask(source.getId());
        dto.setTargetTask(target.getId());

        return dto;
    }

    public List<TaskDependencyResponseDTO> getAllDependency(Long workflowId){
        return dependencyRepo.findByWorkflowId(workflowId)
                .stream()
                .map(dep -> {
                    TaskDependencyResponseDTO dto = new TaskDependencyResponseDTO();
                    dto.setId(dep.getId());
                    dto.setSourceTask(dep.getSourceTask().getId());
                    dto.setTargetTask(dep.getTargetTask().getId());
                    return dto;
                })
                .toList();

    }

//    public TaskDependencyResponseDTO getByDependencyId(Long dependencyId){
//        TaskDependencyResponseDTO dto = new TaskDependencyResponseDTO();
//        TaskDependency dep = dependencyRepo.findByDependencyId(dependencyId);
//        dto.setId(dep.getId());
//        dto.setSourceTask(dep.getSourceTask().getId());
//        dto.setTargetTask(dep.getTargetTask().getId());
//        return dto;
//
//    }


    public void deleteDependency(Long dependencyId){dependencyRepo.deleteById(dependencyId);}



    public List<TaskDependencyResponseDTO> getByTaskId(Long workflowId, Long taskId){
        List<TaskDependencyResponseDTO> allDependency = new ArrayList<>();
        allDependency.addAll(dependencyRepo.findByWorkflowIdAndTargetTaskId(workflowId, taskId).stream()
                .map(dep -> {
                    TaskDependencyResponseDTO dto = new TaskDependencyResponseDTO();
                    dto.setId(dep.getId());
                    dto.setSourceTask(dep.getSourceTask().getId());
                    dto.setTargetTask(dep.getTargetTask().getId());
                    return dto;
                })
                .toList());
        allDependency.addAll(dependencyRepo.findByWorkflowIdAndSourceTaskId(workflowId, taskId).stream()
                .map(dep -> {
                    TaskDependencyResponseDTO dto = new TaskDependencyResponseDTO();
                    dto.setId(dep.getId());
                    dto.setSourceTask(dep.getSourceTask().getId());
                    dto.setTargetTask(dep.getTargetTask().getId());
                    return dto;
                })
                .toList());

        return allDependency;
    }
}