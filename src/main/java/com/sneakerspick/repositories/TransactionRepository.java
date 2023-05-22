package com.sneakerspick.repositories;

import com.sneakerspick.domains.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Long, Transaction> {
}