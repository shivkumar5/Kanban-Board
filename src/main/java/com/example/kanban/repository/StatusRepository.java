package com.example.kanban.repository;

import com.example.kanban.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepository  extends JpaRepository<Status,Long> {
}
