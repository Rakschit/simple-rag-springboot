package com.example.rag_qa.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.rag_qa.entity.Chat; 
import com.example.rag_qa.entity.ChatMessage; 
import com.example.rag_qa.entity.Role; 
import com.example.rag_qa.repository.ChatMessageRepository;
import com.example.rag_qa.repository.ChatRepository;
import com.example.rag_qa.service.OllamaService;
import com.example.rag_qa.service.PineconeService;
import com.google.protobuf.Struct;

import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api")
public class QuestionAnswerController {

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private PineconeService pineconeService;

    // --- Add Repositories ---
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @PostMapping("/ask")
    @Transactional // Add Transactional annotation
    public Map<String, String> askQuestion(@RequestParam String userQuery,
                                            @RequestParam String id) { // 'id' is the chatId
        if (userQuery == null || userQuery.isBlank()) {
            throw new IllegalArgumentException("Query cannot be empty.");
        }

        // --- Find the chat entity ---
        Chat currentChat = chatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with id: " + id));

        // --- Save the user's message ---
        ChatMessage userMessage = new ChatMessage();
        userMessage.setChat(currentChat);
        userMessage.setRole(Role.USER);
        userMessage.setContent(userQuery);
        chatMessageRepository.save(userMessage);

        // --- Perform RAG steps ---
        List<Float> queryEmbeddings = ollamaService.createEmbeddings(userQuery);
        String namespace = id;
        List<Struct> metadata = new ArrayList<>(pineconeService.doSimilaritySearch(queryEmbeddings, namespace, 3));

        StringBuilder contextBuilder = new StringBuilder();
        for(Struct struct: metadata){
                String text = struct.getFieldsMap().get("text").getStringValue();
                contextBuilder.append(text);
                contextBuilder.append("\n\n---\n\n");
            }

        // --- Generate the answer ---
        String generatedAnswer = ollamaService.generateAnswer(contextBuilder, userQuery);

        // --- Save the AI's response ---
        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setChat(currentChat);
        assistantMessage.setRole(Role.ASSISTANT);
        assistantMessage.setContent(generatedAnswer);
        chatMessageRepository.save(assistantMessage);
        
        // --- Explicitly update the 'updatedAt' timestamp of the chat ---
        currentChat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(currentChat);

        // --- Return the response to the frontend ---
        Map<String, String> response = new HashMap<>();
        response.put("question", userQuery);
        response.put("answer", generatedAnswer);

        return response;
    }
}