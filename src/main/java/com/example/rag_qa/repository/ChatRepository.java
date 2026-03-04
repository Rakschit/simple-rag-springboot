package com.example.rag_qa.repository;

import com.example.rag_qa.entity.Chat;
import com.example.rag_qa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, String> {
    List<Chat> findByUserOrderByUpdatedAtDesc(User user);
}