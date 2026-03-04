package com.example.rag_qa.repository;

import com.example.rag_qa.entity.Chat;
import com.example.rag_qa.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByChatOrderByCreatedAtAsc(Chat chat);
}