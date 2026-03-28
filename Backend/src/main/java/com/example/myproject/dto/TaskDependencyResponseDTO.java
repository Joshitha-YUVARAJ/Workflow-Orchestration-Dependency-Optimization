package com.example.myproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDependencyResponseDTO {
    private Long id;
    private Long sourceTask;
    private Long targetTask;
}
