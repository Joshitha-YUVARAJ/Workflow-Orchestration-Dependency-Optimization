package com.example.myproject.Controller;

import com.example.myproject.Service.TaskDependencyService;
import com.example.myproject.dto.TaskDependencyRequestDTO;
import com.example.myproject.dto.TaskDependencyResponseDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflows/{workflowId}/dependencies")
public class TaskDependencyController {
    private final TaskDependencyService taskDependencyService;
    TaskDependencyController(TaskDependencyService taskDependencyService){
        this.taskDependencyService = taskDependencyService;
    }
    @PostMapping
    public TaskDependencyResponseDTO addDependency(@RequestBody TaskDependencyRequestDTO taskDependencyRequestDTO,
                                                   @PathVariable Long workflowId){
        return taskDependencyService.addDependency(taskDependencyRequestDTO.getSourceTaskId(),
                taskDependencyRequestDTO.getTargetTaskId(),
                workflowId);

    }

    @GetMapping
    public List<TaskDependencyResponseDTO> getAllDependency(@PathVariable Long workflowId){
        return taskDependencyService.getAllDependency(workflowId);
    }

    @GetMapping("/{taskId}")
    public List<TaskDependencyResponseDTO> getDependencyByTaskId(@PathVariable Long workflowId,
                                                                 @PathVariable Long taskId){
        return taskDependencyService.getByTaskId(workflowId, taskId);
    }

    @DeleteMapping("/{dependencyId}")
    public void deleteDependency(@PathVariable Long dependencyId) {
        taskDependencyService.deleteDependency(dependencyId);
    }
}
