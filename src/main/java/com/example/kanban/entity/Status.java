package com.example.kanban.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "statuses")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true) // Explicitly tell it to include parent fields
@EqualsAndHashCode(callSuper = true) // Explicitly tell it to include parent fields
public class Status extends BaseDateEntity {

    @Column(unique = true, nullable = false)
    private String name;
}
