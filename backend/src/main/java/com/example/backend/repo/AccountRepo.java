package com.example.backend.repo;

import com.example.backend.model.Account;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
    boolean existsByUser(User user); //exists an Account linked to the given User
}
