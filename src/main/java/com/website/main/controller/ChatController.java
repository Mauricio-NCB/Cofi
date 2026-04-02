package com.website.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import com.website.main.dto.Chat.ChatCreateDTO;
import com.website.main.dto.Chat.ChatResponseDTO;
import com.website.main.dto.Message.MessageCreateDTO;
import com.website.main.dto.Message.MessageResponseDTO;
import com.website.main.service.ChatService;
import com.website.main.service.MessageService;

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
        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        model.addAttribute("username", "Mauricio");
        model.addAttribute("currentPage", "chat");
        model.addAttribute("chats", chatService.viewChatsFromUser(userId));

        return "chat";
    }
    

    @GetMapping("/{chatId}")
    public String viewChat(@PathVariable Integer chatId, Model model) {
        // Aquí iría la lógica para obtener el chat y sus mensajes
        ChatResponseDTO chat = chatService.findById(chatId);
        List<MessageResponseDTO> messages = messageService.viewMessagesFromChat(chatId);

        model.addAttribute("chat", chat);
        model.addAttribute("messages", messages);
        model.addAttribute("username", "Mauricio");
        model.addAttribute("currentPage", "chat");

        return "chat";
    }

    @GetMapping("/{chatId}/messages")
    @ResponseBody
    public List<MessageResponseDTO> viewMessages(@PathVariable Integer chatId) {

        return messageService.viewMessagesFromChat(chatId);
    }

    // Implementacion para enviar mensajes via JS
    @PostMapping("/{chatId}/messages/ajax")
    @ResponseBody
    public MessageResponseDTO sendMessage(@PathVariable Integer chatId, @RequestBody MessageCreateDTO messageDTO) {
        
        // Aquí iría la lógica para enviar un mensaje al chat
        return messageService.sendMessage(chatId, messageDTO);
    }

    @PostMapping("/crear")
    @ResponseBody
    public ResponseEntity<Void> createChat(@RequestBody ChatCreateDTO chatDTO) {

        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        chatService.createChat(chatDTO, userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{chatId}/delete")
    public String deleteChat(@PathVariable Integer chatId) {
        // Aquí iría la lógica para eliminar un chat
        Integer userId = (Integer) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        chatService.deleteChat(chatId, userId);
        return "redirect:/chat";
    }
}
