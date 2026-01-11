package com.example.kanban.repository;

import com.example.kanban.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StatusRepository  extends JpaRepository<Status, UUID> {
    java.util.Optional<Status> findByName(String name);
}
