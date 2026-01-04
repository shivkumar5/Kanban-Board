package com.example.kanban.repository;

import com.example.kanban.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StatusRepository  extends JpaRepository<Status, UUID> {
}
