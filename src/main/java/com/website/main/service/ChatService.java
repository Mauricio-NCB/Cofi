package com.website.main.service;

import org.springframework.stereotype.Service;

import com.website.main.dto.ChatResponseDTO;
import com.website.main.model.Chat;
import com.website.main.model.User;
import com.website.main.repository.UserRepository;
import com.website.main.repository.ChatRepository;


import java.util.List;
import java.util.ArrayList;


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

    public ChatResponseDTO createChat(String type, List<String> participantNames, Integer creatorId) {
        
        // Aquí iría la lógica para crear un nuevo chat con los participantes dados
        // Por ejemplo, podrías crear un nuevo chat y guardarlo en la base de datos
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<User> participants = new ArrayList<>();
        participants.add(creator);

        for (String nameParticipant : participantNames) {
            User participant = userRepository.findByName(nameParticipant.trim());
            
            if (participant != null && !participants.contains(participant)) {
                participants.add(participant);
            }
        }

        Chat chat = new Chat();
        chat.setType(type); 
        chat.setUsers(participants);

        Chat savedChat = chatRepository.save(chat);

        ChatResponseDTO responseDTO = new ChatResponseDTO();
        responseDTO.setId(savedChat.getId());
        responseDTO.setType(savedChat.getType());
        responseDTO.setParticipantNames(savedChat.getUsers().stream()
                    .map(User::getName).toList());


        return responseDTO;
    }

    public void deleteChat(Integer chatId, Integer userId) {
        // Aquí iría la lógica para eliminar un chat
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (chat.getUsers().contains(user)) {
            chatRepository.delete(chat);
        } else {
            throw new RuntimeException("El usuario no tiene permiso para eliminar este chat");
        }
    }

    public List<ChatResponseDTO> viewChatsFromUser(Integer userId) {
        // Aquí iría la lógica para obtener los chats en los que participa el usuario
        return chatRepository.findByUsersId(userId).stream()
                .map(chat -> {
                    ChatResponseDTO dto = new ChatResponseDTO();
                    dto.setId(chat.getId());
                    dto.setType(chat.getType());
                    dto.setParticipantNames(chat.getUsers().stream()
                            .map(User::getName).toList());
                    return dto;
                })
                .toList();
    }
}
