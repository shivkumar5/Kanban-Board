package com.example.kanban.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kanban.entity.StatusTransition;
import com.example.kanban.entity.StatusTransitionId;

@Repository
public interface StatusTransitionRepository extends JpaRepository<StatusTransition, StatusTransitionId> {}
