package com.example.rag_qa.controller;

import com.example.rag_qa.entity.Chat; // Import Chat
import com.example.rag_qa.entity.User;
import com.example.rag_qa.repository.ChatRepository; // Import ChatRepository
import com.example.rag_qa.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Import
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List; // Import List
import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChatRepository chatRepository; 


    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.chatRepository = chatRepository;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping("/register-submit")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Username already exists. Please choose another one.");
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/register";
        }
        
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String getHomePage(Model model, Principal principal) {
         if (principal != null) {
            model.addAttribute("username", principal.getName());
            
            User user = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + principal.getName()));
            List<Chat> chats = chatRepository.findByUserOrderByUpdatedAtDesc(user);
            model.addAttribute("chats", chats);
        }
        return "home";
    }

}