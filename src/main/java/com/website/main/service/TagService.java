package com.website.main.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.website.main.model.Tag;
import com.website.main.repository.TagRepository;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Object[]> findPopularTags() {
        return tagRepository.findPopularTags();
    }

    public Tag findOrCreate(String name) {

        return tagRepository.findByName(name)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(name);
                    return tagRepository.save(tag);
                });
    }
}
