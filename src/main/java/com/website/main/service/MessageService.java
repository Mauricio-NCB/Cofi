package com.website.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.website.main.model.Message;
import com.website.main.repository.MessageRepository;



@Service
public class MessageService {
    
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message sendMessage(Integer chatId, String content, Integer userId) {
        // Aquí iría la lógica para enviar un mensaje al chat
        // Por ejemplo, podrías crear un nuevo mensaje y guardarlo en la base de datos
        // Message message = new Message();
        // message.setChatId(chatId);
        // message.setContent(content);
        // message.setUserId(userId);
        // messageRepository.save(message);

        return null; // Placeholder
    }

    public List<Message> viewMessagesFromChat(Integer chatId) {
        // Aquí iría la lógica para obtener los mensajes de un chat específico
        // return messageRepository.findByChatId(chatId);

        return null; // Placeholder
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
