package com.website.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.website.main.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
}
