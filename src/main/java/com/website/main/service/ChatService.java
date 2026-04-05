package com.website.main.service;

import org.springframework.stereotype.Service;

import com.website.main.model.Chat;
import com.website.main.model.User;
import com.website.main.repository.UserRepository;
import com.website.main.repository.ChatRepository;
import com.website.main.dto.Chat.ChatCreateDTO;
import com.website.main.dto.Chat.ChatResponseDTO;
import com.website.main.dto.User.UserParticipantDTO;
import com.website.main.mapper.ChatMapper;

import java.util.List;
import java.util.ArrayList;


@Service
public class ChatService {
    
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    private final ChatMapper chatMapper;
    
    public ChatService(ChatRepository chatRepository, UserRepository userRepository, ChatMapper chatMapper) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.chatMapper = chatMapper;
    }

    public ChatResponseDTO findById(Integer chatId) {
        // Aquí iría la lógica para obtener un chat específico por su ID
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));
        
        return chatMapper.toDTO(chat);
    }

    public ChatResponseDTO createChat(ChatCreateDTO chatDTO, Integer creatorId) {
        
        // Aquí iría la lógica para crear un nuevo chat con los participantes dados
        // Por ejemplo, podrías crear un nuevo chat y guardarlo en la base de datos
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creador de chat no encontrado"));
        
        List<User> participants = new ArrayList<>();
        participants.add(creator);

        for (UserParticipantDTO participantDTO : chatDTO.getParticipants()) {
            User newParticipant = userRepository.findByNameAndLastname(participantDTO.getName(), participantDTO.getLastName())
                        .orElseThrow(() -> new RuntimeException("Participante" + participantDTO.getName() + " " + participantDTO.getLastName() + " no encontrado") );
            
            if (newParticipant != null && !participants.contains(newParticipant)) {
                participants.add(newParticipant);
            }
        }

        Chat chat = new Chat();
        chat.setName(chatDTO.getName());
        chat.setUsers(participants);

        Chat savedChat = chatRepository.save(chat);

        return chatMapper.toDTO(savedChat);
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
        List<Chat> chats = chatRepository.findByUsersId(userId);
        
        return chats.stream().map(chatMapper::toDTO).toList();
    }
}
