package com.example.kanban.service;

import com.example.kanban.dto.TaskDTO;
import com.example.kanban.entity.Sprint;
import com.example.kanban.entity.Status;
import com.example.kanban.entity.Task;
import com.example.kanban.entity.User;
import com.example.kanban.repository.SprintRepository;
import com.example.kanban.repository.StatusRepository;
import com.example.kanban.repository.TaskRepository;
import com.example.kanban.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SprintRepository sprintRepository;
    @Autowired
    StatusRepository statusRepository;

    public Task createTask(TaskDTO taskDTO) {

        Task task = new Task();
        task.setDescription(taskDTO.getDescription());
        task.setTitle(taskDTO.getTitle());
        task.setDueDate(taskDTO.getDueDate());
        if (taskDTO.getUserId() != null) {
            userRepository.findById(taskDTO.getUserId()).ifPresent(task::setUser);
        }

        if (taskDTO.getStatusId() != null) {
            statusRepository.findById(taskDTO.getStatusId()).ifPresent(task::setStatus);
        }

        if (taskDTO.getSprintId() != null) {
            sprintRepository.findById(taskDTO.getSprintId()).ifPresent(task::setSprint);
        }

        return  taskRepository.save(task);
    }

}
