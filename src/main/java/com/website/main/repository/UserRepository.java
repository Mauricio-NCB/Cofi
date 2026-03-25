package com.website.main.repository;

import com.website.main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByName(String name);
    Optional<User> findByNameAndLastname(String name, String lastname);
    Optional<User> findByRefreshToken(String refreshToken);
}
