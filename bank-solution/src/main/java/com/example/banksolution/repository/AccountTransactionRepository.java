package com.example.banksolution.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.example.banksolution.model.Account;
import com.example.banksolution.model.AccountTransaction;

@Service
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {

	AccountTransaction getFirstByAccountOrderBySequenceDesc(Account account);
	
	List<AccountTransaction> findAllByDateTimeGreaterThanEqualAndDateTimeLessThanOrderBySequenceDesc(LocalDateTime startDate, LocalDateTime endStart);
}
