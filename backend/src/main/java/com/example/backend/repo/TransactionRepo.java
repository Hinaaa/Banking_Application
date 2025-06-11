package com.example.backend.repo;

import com.example.backend.model.Account;
import com.example.backend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    Optional<Account> findByUserId(Long userId); //fetch account by user
    List<Transaction> findAllByUser_Id(Long userId);//@Query("SELECT t FROM Transaction t WHERE t.user.id = :userId")
}
