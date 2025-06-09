package com.example.backend.repo;

import com.example.backend.model.Account;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
    boolean existsByUser(User user); //check if account exists. exists an Account linked to the given User
    Optional<Account> findByUserId(Long userId); // fetch account by user

   // Long userId(Long userId);
}
