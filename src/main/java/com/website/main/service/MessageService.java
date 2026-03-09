package com.website.main.service;

import org.springframework.stereotype.Service;

import com.website.main.repository.MessageRepository;

@Service
public class MessageService {
    
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void sendMessage(Integer chatId, String content, Integer userId) {
        // Aquí iría la lógica para enviar un mensaje al chat
        // Por ejemplo, podrías crear un nuevo mensaje y guardarlo en la base de datos
        // Message message = new Message();
        // message.setChatId(chatId);
        // message.setContent(content);
        // message.setUserId(userId);
        // messageRepository.save(message);
    }
}
