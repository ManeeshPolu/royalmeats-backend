package com.royalhalalmeats.royalmeats.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.royalhalalmeats.royalmeats.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);
}

