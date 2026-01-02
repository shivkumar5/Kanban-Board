package com.example.kanban.entity;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Task extends FullAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"tasks", "handler", "hibernateLazyInitializer"})
    @JsonProperty("userId")
    @JsonIdentityReference(alwaysAsId = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    @JsonIgnoreProperties({"tasks", "handler", "hibernateLazyInitializer"})
    @JsonProperty("statusId")
    @JsonIdentityReference(alwaysAsId = true)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    @JsonIgnoreProperties({"tasks", "handler", "hibernateLazyInitializer"})
    @JsonProperty("sprintId")
    @JsonIdentityReference(alwaysAsId = true)
    private Sprint sprint;
}