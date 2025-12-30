package com.example.kanban.service;

import com.example.kanban.dto.SprintDTO;
import com.example.kanban.entity.Sprint;
import com.example.kanban.entity.User;
import com.example.kanban.repository.SprintRepository;
import com.example.kanban.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

@Service
public class SprintService {

    @Autowired
    private SprintRepository sprintRepository;
    @Autowired
    private UserRepository userRepository;

    public Sprint createSprint(SprintDTO sprintDTO) {
        User user = userRepository.findById(sprintDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        Sprint sprint = new Sprint();
        sprint.setName(sprintDTO.getName());
        sprint.setCreatedBy("Shiv Kumar");
        sprint.setStartDate(new Date());
        sprint.setEndDate(new Date());
        sprint.setUser(user);
        return  sprintRepository.save(sprint);
    }
}
