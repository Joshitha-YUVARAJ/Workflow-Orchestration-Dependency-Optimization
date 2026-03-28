package com.example.myproject.dto;

import com.example.myproject.Model.Task;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaskUpdateDTO {
    private String name;
    private String description;
    private Integer estimatedDuration;
    private Task.TaskType TaskType;
}

