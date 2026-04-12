package com.website.main.repository;

import com.website.main.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByParticipants_Id(Integer userId);
    
    List<Event> findByCategories_Id(Integer categoryId);

    List<Event> findByPostcode(String postcode);

    Optional<Event> findByChatId(Integer chatId);
}
