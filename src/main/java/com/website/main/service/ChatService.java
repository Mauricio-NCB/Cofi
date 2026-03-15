package com.website.main.service;

import org.springframework.stereotype.Service;

import com.website.main.model.Chat;
import com.website.main.model.User;
import com.website.main.repository.UserRepository;
import com.website.main.repository.ChatRepository;


import java.util.List;


@Service
public class ChatService {
    
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    
    public ChatService(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    public Chat findById(Integer chatId) {
        // Aquí iría la lógica para obtener un chat específico por su ID
        // return chatRepository.findById(chatId).orElse(null);
        return chatRepository.findById(chatId).orElse(null); // Placeholder
    }

    public Chat createChat(List<Integer> participantIds) {
        
        // Aquí iría la lógica para crear un nuevo chat con los participantes dados
        // Por ejemplo, podrías crear un nuevo chat y guardarlo en la base de datos
        User user = userRepository.findById(participantIds.get(0))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Chat chat = new Chat();
        // chat.setParticipantIds(participantIds);
        // return chatRepository.save(chat);

        return null; // Placeholder
    }

    public List<Chat> viewChatsFromUser(Integer userId) {
        // Aquí iría la lógica para obtener los chats en los que participa el usuario
        return chatRepository.findByUsersId(userId);
    }

    public boolean exitChat(Integer chatId, Integer userId) {
        // Aquí iría la lógica para que un usuario salga de un chat
        // Por ejemplo, podrías eliminar al usuario de la lista de participantes del chat
        // Chat chat = chatRepository.findById(chatId).orElse(null);
        // if (chat != null) {
        //     chat.getParticipantIds().remove(userId);
        //     chatRepository.save(chat);
        // }

        return false; // Placeholder
    }

}
