package com.example.kanban.service;

import com.example.kanban.dto.StatusDTO;
import com.example.kanban.entity.Status;
import com.example.kanban.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return  statusRepository.findAll();
    }
}
