package com.example.rag_qa.controller;

import java.security.Principal; 
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.rag_qa.entity.Chat;
import com.example.rag_qa.entity.ChatMessage;
import com.example.rag_qa.entity.User;
import com.example.rag_qa.repository.ChatMessageRepository;
import com.example.rag_qa.repository.ChatRepository; 
import com.example.rag_qa.repository.UserRepository; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.rag_qa.service.DocumentChunkingService;
import com.example.rag_qa.service.OllamaService;
import com.example.rag_qa.service.PineconeService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PageController {

    @Autowired
    private DocumentChunkingService documentChunkingService;
    
    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private PineconeService pineconeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;


    @PostMapping("/generate")
    public String fileProcessing(@RequestParam("file") MultipartFile file,
                                    RedirectAttributes redirectAttributes,
                                    Principal principal){ 
                                    
        if (principal == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "You must be logged in to upload a document.");
            return "redirect:/login";
        }

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload.");
            return "redirect:/";
        }

        try{
            String username = principal.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            String chatId = UUID.randomUUID().toString();

            Chat newChat = new Chat();
            newChat.setChatId(chatId);
            newChat.setUser(currentUser);
            newChat.setTitle(file.getOriginalFilename());
            chatRepository.save(newChat);

            Resource filResource = file.getResource();
            List<String> chunks = documentChunkingService.getChunks(filResource);
            List<List<Float>> embeddingList = new ArrayList<>();

            for(String chunk : chunks){
                List<Float> embedding = ollamaService.createEmbeddings(chunk);
                embeddingList.add(embedding);
            }

            pineconeService.upsert(chunks, embeddingList, chatId);

            return "redirect:/qa/" + chatId;
        }catch (Exception e){
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing file: " + e.getMessage());
            return "redirect:/";
        }
    }  

    @GetMapping("/qa/{id}")
    public String getFilePage(@PathVariable String id, ModelMap model, Principal principal) { // <-- Add Principal
        if (principal == null) {
            return "redirect:/login";
        }
        
        // Find the chat and ensure it belongs to the logged-in user
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        if (!chat.getUser().getUsername().equals(principal.getName())) {
            // Or handle as an unauthorized access error
            return "redirect:/";
        }

        List<ChatMessage> messages = chatMessageRepository.findByChatOrderByCreatedAtAsc(chat);

        model.addAttribute("id", id);
        model.addAttribute("title", chat.getTitle());
        model.addAttribute("messages", messages); 
        return "chat";
    }


    @DeleteMapping("/chat/{id}")
    public String deleteChat(@PathVariable String id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login"; // Should not happen if security is configured
        }

        // Find the chat and ensure it belongs to the logged-in user
        Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found"));

        if (!chat.getUser().getUsername().equals(principal.getName())) {
            // This user does not own the chat, deny deletion
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to delete this document.");
            return "redirect:/";
        }

        try {
            // The user is the owner, proceed with deletion
            chatRepository.delete(chat);
            redirectAttributes.addFlashAttribute("successMessage", "Document '" + chat.getTitle() + "' was deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting document: " + e.getMessage());
    }

    return "redirect:/"; // Redirect back to the home page
}
}