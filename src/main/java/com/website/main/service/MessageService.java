package com.website.main.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.website.main.model.Message;
import com.website.main.dto.Message.MessageCreateDTO;
import com.website.main.dto.Message.MessageResponseDTO;
import com.website.main.mapper.MessageMapper;
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

    private final MessageMapper messageMapper;

    private final SimpMessagingTemplate messagingTemplate;

    public MessageService(MessageRepository messageRepository, ChatRepository chatRepository,
                UserRepository userRepository, SimpMessagingTemplate messagingTemplate,
                MessageMapper messageMapper) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.messageMapper = messageMapper;
    }

    public MessageResponseDTO sendMessage(Integer chatId, MessageCreateDTO messageDTO) {
        // Aquí iría la lógica para enviar un mensaje al chat
        // Por ejemplo, podrías crear un nuevo mensaje y guardarlo en la base de datos
        Chat chat = chatRepository.findById(chatId)
            .orElseThrow(() -> new RuntimeException("Chat no encontrado"));
        User user = userRepository.findById(1)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setUser(user);
        message.setChat(chat);
        message.setDateSent(LocalDateTime.now());

        Message savedMessage = messageRepository.save(message);

        MessageResponseDTO responseDTO = messageMapper.toDTO(savedMessage);

        // Enviar el mensaje a través de WebSocket
        messagingTemplate.convertAndSend("/topic/chat." + chatId, responseDTO);

        return responseDTO;
    }

    public List<MessageResponseDTO> viewMessagesFromChat(Integer chatId) {
        // Aquí iría la lógica para obtener los mensajes de un chat específico
        return messageRepository.findByChatIdOrderByDateSentAsc(chatId).stream()
                .map(messageMapper::toDTO)
                .toList();
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
