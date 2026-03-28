package com.example.myproject.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "workflow")
@Getter
@Setter
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Task> tasks;

    public enum Status {
        DRAFT,
        ACTIVE,
        ARCHIVED
    }
}