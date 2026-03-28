package com.example.myproject.dto;

import com.example.myproject.Model.Task;
import com.example.myproject.Model.Workflow;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkflowUpdateDTO {
    private String name;
    private String description;
    private Workflow.Status status;

}
