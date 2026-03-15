package com.website.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import com.website.main.model.Chat;
import com.website.main.model.Message;
import com.website.main.dto.MessageDTO;
import com.website.main.service.ChatService;
import com.website.main.service.MessageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/chat")
public class ChatController {
    
    private final ChatService chatService;
    private final MessageService messageService;

    public ChatController(ChatService chatService, MessageService messageService) {
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @GetMapping
    public String viewChats(Model model) {
        // Aquí iría la lógica para obtener los chats
        model.addAttribute("username", "Mauricio");
        model.addAttribute("currentPage", "chat");
        model.addAttribute("chats", chatService.viewChatsFromUser(1)); // POSTERIOR CAMBIAR POR USUARIO REAL

        return "chat";
    }
    

    @GetMapping("/{chatId}")
    public String viewChat(@PathVariable Integer chatId, Model model) {
        // Aquí iría la lógica para obtener el chat y sus mensajes
        Chat chat = chatService.findById(chatId);
        List<Message> messages = messageService.viewMessagesFromChat(chatId);

        model.addAttribute("chat", chat);
        model.addAttribute("messages", messages);
        model.addAttribute("username", "Mauricio");
        model.addAttribute("currentPage", "chat");

        return "chat";
    }

    @GetMapping("/{chatId}/messages")
    @ResponseBody
    public List<MessageDTO> viewMessages(@PathVariable Integer chatId) {
        
        List<MessageDTO> messages = messageService.viewMessagesFromChat(chatId)
                .stream().map(MessageDTO::fromEntity).toList();

        return messages;
    }

    // Implementacion para enviar mensajes via JS
    @PostMapping("/{chatId}/messages/ajax")
    @ResponseBody
    public MessageDTO sendMessage(@PathVariable Integer chatId, @RequestParam String content, 
        @RequestParam Integer userId) {
        
        // Aquí iría la lógica para enviar un mensaje al chat
        // chatService.sendMessage(chatId, content);
        Message messageCreated = messageService.sendMessage(chatId, content, userId);

        return MessageDTO.fromEntity(messageCreated);
    }
}
