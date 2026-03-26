package com.website.main.service;

import com.website.main.repository.ReactionRepository;
import com.website.main.model.Reaction;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReactionService {
    private final ReactionRepository reactionRepository;

    public ReactionService(ReactionRepository reactionRepository) {
        this.reactionRepository = reactionRepository;
    }

    public List<Reaction> findAll() {
        return reactionRepository.findAll();
    }
}
