package com.website.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import com.website.main.dto.Chat.ChatResponseDTO;
import com.website.main.dto.Message.MessageCreateDTO;
import com.website.main.dto.Message.MessageResponseDTO;
import com.website.main.service.ChatService;
import com.website.main.service.MessageService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
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
        // chatService.sendMessage(chatId, content);

        return messageService.sendMessage(chatId, messageDTO);
    }

    @PostMapping("/crear")
    public String createChat(@RequestParam String type, @RequestParam String participants) {
        // Aquí iría la lógica para crear un nuevo chat
        Integer userId = 1; // POSTERIOR CAMBIAR POR USUARIO REAL
        List<String> participantNames = Arrays.asList(participants.split("\\s,\\s*")); // Separado por comas

        chatService.createChat(type, participantNames, userId);

        return "redirect:/chat";
    }

    @PostMapping("/{chatId}/delete")
    public String deleteChat(@PathVariable Integer chatId) {
        // Aquí iría la lógica para eliminar un chat
        Integer userId = 1; // POSTERIOR CAMBIAR POR USUARIO REAL

        chatService.deleteChat(chatId, userId);
        return "redirect:/chat";
    }
}
