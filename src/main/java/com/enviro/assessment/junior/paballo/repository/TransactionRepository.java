package com.enviro.assessment.junior.paballo.repository;

import com.enviro.assessment.junior.paballo.entity.Transaction;
import com.enviro.assessment.junior.paballo.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByInvestorIdOrderByProcessedAtDesc(Long investorId);

    List<Transaction> findByInvestorIdAndTransactionTypeOrderByProcessedAtDesc(Long investorId, TransactionType transactionType);
}
