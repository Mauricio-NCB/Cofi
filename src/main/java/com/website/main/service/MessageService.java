package com.website.main.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.website.main.model.Message;
import com.website.main.model.Chat;
import com.website.main.model.User;

import com.website.main.repository.ChatRepository;
import com.website.main.repository.MessageRepository;
import com.website.main.repository.UserRepository;



@Service
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository,
                ChatRepository chatRepository,
                UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public Message sendMessage(Integer chatId, String content, Integer userId) {
        // Aquí iría la lógica para enviar un mensaje al chat
        // Por ejemplo, podrías crear un nuevo mensaje y guardarlo en la base de datos
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat no encontrado"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Message message = new Message();
        message.setContent(content);
        message.setUser(user);
        message.setChat(chat);
        message.setDateSent(LocalDateTime.now());

        return messageRepository.save(message); // Return the created message
    }

    public List<Message> viewMessagesFromChat(Integer chatId) {
        // Aquí iría la lógica para obtener los mensajes de un chat específico
        return messageRepository.findByChatIdOrderByDateSentAsc(chatId);
    }


    // OPCIONALES

    public boolean deleteMessage(Integer messageId, Integer userId) {
        // Aquí iría la lógica para eliminar un mensaje
        // Por ejemplo, podrías verificar si el usuario es el autor del mensaje antes de eliminarlo
        // Message message = messageRepository.findById(messageId).orElse(null);
        // if (message != null && message.getUserId().equals(userId)) {
        //     messageRepository.delete(message);
        // }

        return false; // Placeholder
    }
    

    public boolean editMessage(Integer messageId, String newContent, Integer userId) {
        // Aquí iría la lógica para editar un mensaje
        // Por ejemplo, podrías verificar si el usuario es el autor del mensaje antes de editarlo
        // Message message = messageRepository.findById(messageId).orElse(null);
        // if (message != null && message.getUserId().equals(userId)) {
        //     message.setContent(newContent);
        //     messageRepository.save(message);
        // }

        return false; // Placeholder
    }
}
