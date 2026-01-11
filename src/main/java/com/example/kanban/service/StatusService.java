package com.example.kanban.service;

import com.example.kanban.dto.StatusDTO;
import com.example.kanban.entity.Status;
import com.example.kanban.repository.StatusRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    public Status createStatus(StatusDTO statusDTO) {
        Status s = new Status();
        s.setName(statusDTO.getName());
        return statusRepository.save(s);
    }

    public List<Status> getAllStatus() {
        return statusRepository.findAll();
    }

    public Status getStatusById(UUID statusId) {
        return statusRepository.findById(statusId)
                .orElseThrow(() -> new EntityNotFoundException("Status not found with ID: " + statusId));
    }

    public Status updateStatus(UUID statusId, StatusDTO statusDTO) {
        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new EntityNotFoundException("Status not found with ID: " + statusId));
        status.setName(statusDTO.getName());
        return statusRepository.save(status);
    }

    public void deleteStatus(UUID statusId) {
        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new EntityNotFoundException("Status not found with ID: " + statusId));
        statusRepository.delete(status);
    }
}
