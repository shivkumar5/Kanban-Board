package com.example.kanban.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true) // Explicitly tell it to include parent fields
@EqualsAndHashCode(callSuper = true) // Explicitly tell it to include parent fields
public class User extends BaseDateEntity {

    @Column(nullable = false)
    private  String name;
    @Column(unique = true,nullable = false)
    private  String email;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Sprint> sprints;

}

