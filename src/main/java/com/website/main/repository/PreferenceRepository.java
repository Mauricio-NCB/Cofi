package com.website.main.repository;

import com.website.main.model.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, Integer> {
    Optional<Preference> findByUserId(Integer userId);
}
