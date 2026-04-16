package com.motorvault.backend.repository;

import com.motorvault.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByEnabled(boolean enabled);

    List<User> findAllByOrderByCreatedAtDesc();

    List<User> findByEnabledOrderByCreatedAtDesc(boolean enabled);

    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email);

    long countByRole(com.motorvault.backend.entity.enums.Role role);
}
