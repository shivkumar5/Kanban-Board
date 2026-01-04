package com.example.kanban.service;

import com.example.kanban.dto.SprintDTO;
import com.example.kanban.entity.Sprint;
import com.example.kanban.entity.User;
import com.example.kanban.repository.SprintRepository;
import com.example.kanban.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Iterable<Sprint> getAllSprints() {
        return sprintRepository.findAll();
    }

    public Sprint getSprintById(java.util.UUID sprintId) {
        return sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found!"));
    }

    public Sprint updateSprint(java.util.UUID sprintId, SprintDTO sprintDTO) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found!"));
        sprint.setName(sprintDTO.getName());
        return sprintRepository.save(sprint);
    }
    

    public Sprint patchSprint(java.util.UUID sprintId, java.util.Map<String, Object> updates) {
        Sprint existingSprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found!"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> existingSprint.setName((String) value);
                case "startDate" -> existingSprint.setStartDate((Date) value);
                case "endDate" -> existingSprint.setEndDate((Date) value);
            }
        });

        return sprintRepository.save(existingSprint);
    }

}
