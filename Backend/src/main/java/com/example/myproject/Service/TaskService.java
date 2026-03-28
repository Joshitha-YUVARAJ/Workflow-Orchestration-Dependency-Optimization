package com.example.myproject.Service;

import com.example.myproject.Model.Task;
import com.example.myproject.Model.Workflow;
//import com.example.myproject.Repostiory.TaskDependencyRepository;
import com.example.myproject.Repostiory.TaskRepository;
import com.example.myproject.Repostiory.WorkflowRepository;
//import com.example.myproject.dto.TaskDependencyResponseDTO;
import com.example.myproject.dto.TaskResponseDTO;
import com.example.myproject.dto.TaskUpdateDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepo;
    private final WorkflowRepository workflowRepo;

    public TaskService(TaskRepository taskRepo,
                       WorkflowRepository workflowRepo) {
        this.taskRepo = taskRepo;
        this.workflowRepo = workflowRepo;
    }

    public Task createTask(Long workflowId, Task task) {

        Workflow workflow = workflowRepo.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));

        task.setWorkflow(workflow);
        task.setCreatedAt(LocalDate.now());

        return taskRepo.save(task);
    }

    public List<TaskResponseDTO> getTasksByWorkflow(Long workflowId) {

        return taskRepo.findByWorkflowId(workflowId)
                .stream()
                .map(task -> {
                    TaskResponseDTO dto = new TaskResponseDTO();
                    dto.setId(task.getId());
                    dto.setDescription(task.getDescription());
                    dto.setTaskType(task.getTaskType().name());
                    dto.setName(task.getName());
                    dto.setEstimatedDuration(task.getEstimatedDuration());
                    dto.setIncomingDependencyIds(task.getIncomingDependencies()
                            .stream()
                            .map(dep -> dep.getSourceTask().getId())
                            .toList());
                    dto.setOutgoingDependencyIds(task.getOutgoingDependencies()
                            .stream()
                            .map(dep -> dep.getTargetTask().getId())
                            .toList());

                    return dto;
                })
                .toList();
    }

    public TaskResponseDTO getByTaskId(Long taskId, Long workflowId) {

        Task task = taskRepo.findByIdAndWorkflowId(workflowId, taskId).orElseThrow();
        TaskResponseDTO dto = new TaskResponseDTO();

        dto.setId(task.getId());
        dto.setTaskType(task.getTaskType().name());
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setEstimatedDuration(task.getEstimatedDuration());
        dto.setIncomingDependencyIds(task.getIncomingDependencies()
                .stream()
                .map(dep -> dep.getSourceTask().getId())
                .toList());
        dto.setOutgoingDependencyIds(task.getOutgoingDependencies()
                .stream()
                .map(dep -> dep.getTargetTask().getId())
                .toList());

        return dto;
    }

    public void deleteTask(Long workflowId,Long Id){
        Task task = taskRepo.findByIdAndWorkflowId(Id, workflowId)
                .orElseThrow();
        taskRepo.delete(task);
    }

    public TaskResponseDTO updateTasks(Long workflowId, Long taskId, TaskUpdateDTO dto){
        Task task = taskRepo.findByIdAndWorkflowId(taskId, workflowId).orElseThrow();
        TaskResponseDTO responseDTO = new TaskResponseDTO();

        if(dto.getName() != null){
            task.setName(dto.getName());
        }
        if(dto.getTaskType() != null){
            task.setTaskType(dto.getTaskType());
        }
        if (dto.getDescription() != null){
            task.setDescription(dto.getDescription());
        }
        if(dto.getEstimatedDuration() != null){
            task.setEstimatedDuration(dto.getEstimatedDuration());
        }

        responseDTO.setId(task.getId());
        responseDTO.setTaskType(task.getTaskType().name());
        responseDTO.setName(task.getName());
        responseDTO.setDescription(task.getDescription());
        responseDTO.setEstimatedDuration(task.getEstimatedDuration());
        responseDTO.setIncomingDependencyIds(task.getIncomingDependencies()
                .stream()
                .map(dep -> dep.getSourceTask().getId())
                .toList());
        responseDTO.setOutgoingDependencyIds(task.getOutgoingDependencies()
                .stream()
                .map(dep -> dep.getTargetTask().getId())
                .toList());
        taskRepo.save(task);
        return responseDTO;

    }



}