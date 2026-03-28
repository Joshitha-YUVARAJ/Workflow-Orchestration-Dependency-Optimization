package com.example.myproject.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskDependencyRequestDTO {
    private Long sourceTaskId;
    private Long targetTaskId;

}
