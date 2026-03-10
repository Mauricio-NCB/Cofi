package com.website.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.website.main.model.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    
    List<Chat> findByUsersId(Integer userId);
}
