package com.example.myproject.Service;

import com.example.myproject.Model.Task;
import com.example.myproject.Model.TaskDependency;
import com.example.myproject.Repostiory.TaskDependencyRepository;
import com.example.myproject.Repostiory.TaskRepository;
import com.example.myproject.dto.ExecutionOrderDTO;
import com.example.myproject.dto.WorkflowValidationDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkflowGraphService {
    private final TaskRepository taskRepo;
    private final TaskDependencyRepository dependencyRepo;

    public WorkflowGraphService(TaskRepository taskRepo,
                                TaskDependencyRepository dependencyRepo) {
        this.taskRepo = taskRepo;
        this.dependencyRepo = dependencyRepo;
    }

    private List<Long> topologicalSort(Long workflowId) {

        List<Task> tasks = taskRepo.findByWorkflowId(workflowId);
        List<TaskDependency> dependencies = dependencyRepo.findByWorkflowId(workflowId);

        // Adjacency list
        Map<Long, List<Long>> graph = new HashMap<>();
        Map<Long, Integer> inDegree = new HashMap<>();

        for (Task task : tasks) {
            graph.put(task.getId(), new ArrayList<>());
            inDegree.put(task.getId(), 0);
        }

        for (TaskDependency dep : dependencies) {
            Long from = dep.getSourceTask().getId();
            Long to = dep.getTargetTask().getId();
            graph.get(from).add(to);
            inDegree.put(to, inDegree.get(to) + 1);
        }

        // Kahn’s Algorithm
        Queue<Long> queue = new LinkedList<>();
        for (Long taskId : inDegree.keySet()) {
            if (inDegree.get(taskId) == 0) {
                queue.add(taskId);
            }
        }

        List<Long> order = new ArrayList<>();

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            order.add(current);

            for (Long neighbor : graph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (order.size() != tasks.size()) {
            throw new RuntimeException("Cycle detected in workflow dependencies");
        }

        return order;
    }
    public WorkflowValidationDTO validateWorkflow(Long workflowId) {
        try {
            topologicalSort(workflowId);
            return new WorkflowValidationDTO(true, "Workflow is valid (no cycles)");
        } catch (Exception e) {
            return new WorkflowValidationDTO(false, e.getMessage());
        }
    }

    public ExecutionOrderDTO getExecutionOrder(Long workflowId) {
        try {
        List<Long> order = topologicalSort(workflowId);
        return new ExecutionOrderDTO(workflowId,"The workflow is valid with the following execution order",order);
        } catch (Exception e) {
            return new ExecutionOrderDTO(workflowId,e.getMessage(),new ArrayList<>());
        }
    }
}

