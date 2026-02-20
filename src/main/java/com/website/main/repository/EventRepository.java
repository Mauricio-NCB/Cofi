package com.website.main.repository;

import com.website.main.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByUserId(Integer userId);
    @Query("SELECT e FROM Event e JOIN e.categories c WHERE c.id = :categoryId")
    List<Event> findByCategoryId(@Param("categoryId") Integer categoryId);
}
