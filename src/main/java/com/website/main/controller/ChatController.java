package com.website.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import com.website.main.service.ChatService;
import com.website.main.service.MessageService;

@Controller
@RequestMapping("/chats")
public class ChatController {
    
    private final ChatService chatService;
    private final MessageService messageService;

    public ChatController(ChatService chatService, MessageService messageService) {
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @GetMapping("/{chatId}")
    public String viewChat(@PathVariable Integer chatId, Model model) {
        // Aquí iría la lógica para obtener el chat y sus mensajes
        // model.addAttribute("chat", chat);
        // model.addAttribute("messages", messages);
        
        return "chat/view";
    }
    
    // Implementacion para enviar mensajes via JS
    @PostMapping("/{chatId}/messages")
    public String sendMessage(@PathVariable Integer chatId, @RequestParam String content, 
        @RequestParam Integer userId) {
        
        // Aquí iría la lógica para enviar un mensaje al chat
        // chatService.sendMessage(chatId, content);
        messageService.sendMessage(chatId, content, userId);

        return "redirect:/chats/" + chatId;
    }
}
