package com.example.myproject.Controller;

import com.example.myproject.Model.Task;
import com.example.myproject.Service.TaskService;
import com.example.myproject.dto.TaskResponseDTO;
import com.example.myproject.dto.TaskUpdateDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflows/{workflowId}/tasks")
public class TaskController {
    private final TaskService taskService;

    TaskController(TaskService taskService){
        this.taskService = taskService;
    }
    @PostMapping
    public Task addTask(@PathVariable Long workflowId,
                        @RequestBody Task task) {
        return taskService.createTask(workflowId, task);
    }

    @GetMapping
    public List<TaskResponseDTO> getTasks(@PathVariable Long workflowId) {
        return taskService.getTasksByWorkflow(workflowId);
    }

    @GetMapping("/{taskId}")
    public TaskResponseDTO getByTaskId(@PathVariable Long taskId, @PathVariable Long workflowId) {
        return taskService.getByTaskId(workflowId, taskId);
    }

    @DeleteMapping("/{taskId}")
    public void deletedTask(@PathVariable Long workflowId, @PathVariable Long taskId) {
        taskService.deleteTask(workflowId, taskId);
    }

    @PutMapping("/{taskId}")
    public TaskResponseDTO updateTasksPut(@PathVariable Long workflowId,
                                          @PathVariable Long taskId,
                                          @RequestBody TaskUpdateDTO dto){
        return taskService.updateTasks(workflowId,taskId, dto);
    }

    @PatchMapping("/{taskId}")
    public TaskResponseDTO updateTasksPatch(@PathVariable Long workflowId,
                                            @PathVariable Long taskId,
                                            @RequestBody TaskUpdateDTO dto){
        return taskService.updateTasks(workflowId,taskId, dto);
    }

}
