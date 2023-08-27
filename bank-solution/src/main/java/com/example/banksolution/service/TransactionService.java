package com.example.banksolution.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.banksolution.dto.AccountTransactionDto;
import com.example.banksolution.dto.DepositDto;
import com.example.banksolution.dto.TransactionHistoryDto;
import com.example.banksolution.dto.TransactionHistoryDto.DateHistory;
import com.example.banksolution.dto.TransactionHistoryDto.Transaction;
import com.example.banksolution.dto.TransactionHistoryRequestDto;
import com.example.banksolution.dto.TransferDto;
import com.example.banksolution.dto.TransferResultDto;
import com.example.banksolution.dto.WithdrawDto;
import com.example.banksolution.exception.BadRequestException;
import com.example.banksolution.model.Account;
import com.example.banksolution.model.AccountTransaction;
import com.example.banksolution.repository.AccountRepository;
import com.example.banksolution.repository.AccountTransactionRepository;

@Service
public class TransactionService {
	
	@Autowired
	private AccountTransactionRepository accountTransactionRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private LockAccountService lockAccountService;
	
	@Autowired
	private GenerateTransferIdService generateTransferIdService;

	public AccountTransactionDto deposit(DepositDto dto) {
		if (dto.getAmount() <= 0) {
			throw new BadRequestException("Deposit must have a positive amount");
		}
		
		Account account = accountService.getAccount(dto.getAccountId());
		
		accountService.checkAccountNotClosed(account);
		
		String description = "Deposit";
		
		if (StringUtils.hasText(dto.getIdentification())) {
			description = "Deposit from " + dto.getIdentification();
		}
		
		String uuid = generateTransferIdService.generate();
		
		Long lockId = lockAccountService.lock(account);
		
		try {
			AccountTransaction lastTransaction = accountTransactionRepository.getFirstByAccountOrderBySequenceDesc(account);
			
			Long sequence = lastTransaction != null ?
					lastTransaction.getSequence() + 1
					: 1;
			
			AccountTransaction transaction = AccountTransaction.builder()
				.account(account)
				.dateTime(LocalDateTime.now())
				.description(description)
				.value(dto.getAmount())
				.sequence(sequence)
				.uuid(uuid)
				.build();
			
			Double newAmount = account.getCurrentAmount() + transaction.getValue();
			
			account.setCurrentAmount(newAmount);
			
			accountRepository.save(account);
			accountTransactionRepository.saveAndFlush(transaction);

			return AccountTransactionDto.builder()
					.id(transaction.getId())
					.accountId(transaction.getAccount().getId())
					.dateTime(transaction.getDateTime())
					.description(transaction.getDescription())
					.sequence(transaction.getSequence())
					.uuid(transaction.getUuid())
					.value(transaction.getValue())
					.build();
		} finally {
			lockAccountService.unlock(lockId);
		}
	}
	
	public AccountTransactionDto withdraw(WithdrawDto dto) {
		if (dto.getAmount() <= 0) {
			throw new BadRequestException("Withdraw must have a positive amount");
		}
		
		Account account = accountService.getAccount(dto.getAccountId());
		
		accountService.checkAccountNotClosed(account);
		
		String uuid = generateTransferIdService.generate();
		
		Long lockId = lockAccountService.lock(account);
		
		try {
			AccountTransaction lastTransaction = accountTransactionRepository.getFirstByAccountOrderBySequenceDesc(account);

			if (account.getCurrentAmount() < dto.getAmount()) {
				throw new BadRequestException("Not enough funds");
			}

			Long sequence = lastTransaction != null ?
					lastTransaction.getSequence() + 1
					: 1;
			
			AccountTransaction transaction = AccountTransaction.builder()
				.account(account)
				.dateTime(LocalDateTime.now())
				.description("Withdraw")
				.value(dto.getAmount())
				.sequence(sequence)
				.uuid(uuid)
				.build();
			
			Double newAmount = account.getCurrentAmount() - transaction.getValue();
			
			account.setCurrentAmount(newAmount);
			
			accountRepository.save(account);
			accountTransactionRepository.saveAndFlush(transaction);

			return AccountTransactionDto.builder()
					.id(transaction.getId())
					.accountId(transaction.getAccount().getId())
					.dateTime(transaction.getDateTime())
					.description(transaction.getDescription())
					.sequence(transaction.getSequence())
					.uuid(transaction.getUuid())
					.value(transaction.getValue())
					.build();
		} finally {
			lockAccountService.unlock(lockId);
		}
	}
	
	public TransferResultDto transfer(TransferDto dto) {
		if (dto.getAmount() <= 0) {
			throw new BadRequestException("Transfer value must have a positive amount");
		}
		
		Account originAccount = accountService.getAccount(dto.getOriginAccountId());
		Account destinationAccount = accountService.getAccount(dto.getDestinationAccountId());
		
		accountService.checkAccountNotClosed(originAccount);
		accountService.checkAccountNotClosed(destinationAccount);
		
		String uuid = generateTransferIdService.generate();
		
		Long originLockId = lockAccountService.lock(originAccount);
		Long destinatinoLockId = lockAccountService.lock(destinationAccount);
		
		try {
			AccountTransaction originLastTransaction = accountTransactionRepository.getFirstByAccountOrderBySequenceDesc(originAccount);
			AccountTransaction destinationLastTransaction = accountTransactionRepository.getFirstByAccountOrderBySequenceDesc(destinationAccount);

			if (originAccount.getCurrentAmount() < dto.getAmount()) {
				throw new BadRequestException("Not enough funds");
			}

			Long originSequence = originLastTransaction != null ?
					originLastTransaction.getSequence() + 1
					: 1;
			Long destinationSequence = destinationLastTransaction != null ?
					destinationLastTransaction.getSequence() + 1
					: 1;
			
			String originDescription = String.format("Transfer to account id %s", destinationAccount.getId());
			String destinationDescription = String.format("Transfer from account id %s", destinationAccount.getId());
			
			LocalDateTime dateTime = LocalDateTime.now();
			
			AccountTransaction originTransaction = AccountTransaction.builder()
				.account(originAccount)
				.dateTime(dateTime)
				.description(originDescription)
				.value(-dto.getAmount())
				.sequence(originSequence)
				.uuid(uuid)
				.build();

			AccountTransaction destinationTransaction = AccountTransaction.builder()
					.account(destinationAccount)
					.dateTime(dateTime)
					.description(destinationDescription)
					.value(dto.getAmount())
					.sequence(destinationSequence)
					.uuid(uuid)
					.build();

			Double originNewAmount = originAccount.getCurrentAmount() - dto.getAmount();
			Double destinationNewAmount = destinationAccount.getCurrentAmount() + dto.getAmount();
			
			originAccount.setCurrentAmount(originNewAmount);
			destinationAccount.setCurrentAmount(destinationNewAmount);

			accountRepository.save(originAccount);
			accountRepository.save(destinationAccount);
			accountTransactionRepository.save(originTransaction);
			accountTransactionRepository.saveAndFlush(destinationTransaction);

			AccountTransactionDto originTransactionDto = AccountTransactionDto.builder()
					.id(originTransaction.getId())
					.accountId(originTransaction.getAccount().getId())
					.dateTime(originTransaction.getDateTime())
					.description(originTransaction.getDescription())
					.sequence(originTransaction.getSequence())
					.uuid(originTransaction.getUuid())
					.value(originTransaction.getValue())
					.build();

			AccountTransactionDto destinationTransactionDto = AccountTransactionDto.builder()
					.id(destinationTransaction.getId())
					.accountId(destinationTransaction.getAccount().getId())
					.dateTime(destinationTransaction.getDateTime())
					.description(destinationTransaction.getDescription())
					.sequence(destinationTransaction.getSequence())
					.uuid(destinationTransaction.getUuid())
					.value(destinationTransaction.getValue())
					.build();

			
			return TransferResultDto.builder()
					.origin(originTransactionDto)
					.destination(destinationTransactionDto)
					.build();
		} finally {
			lockAccountService.unlock(originLockId);
			lockAccountService.unlock(destinatinoLockId);
		}
	}

	public TransactionHistoryDto history(TransactionHistoryRequestDto dto) {
		Account account = accountService.getAccount(dto.getAccountId());
		
		accountService.checkAccountNotClosed(account);
		
		LocalDateTime startDate = dto.getStartDate().atStartOfDay();
		LocalDateTime endDatePlusOne = dto.getEndDate().plusDays(1).atStartOfDay();
		
		List<AccountTransaction> transactions = accountTransactionRepository
			.findAllByDateTimeGreaterThanEqualAndDateTimeLessThanOrderBySequenceDesc(startDate, endDatePlusOne);
		
		if (transactions != null && transactions.isEmpty()) {
			var dates = new DateHistory[] {};
			return TransactionHistoryDto.builder()
					.dates(dates)
					.build();
		}
		
		LocalDate currentDate = null;
		DateHistory currentDateHistory = null;
		var dates = new ArrayList<DateHistory>();
		var dateTransactions = new ArrayList<Transaction>();

		for (AccountTransaction transaction : transactions) {
			if (!transaction.getDateTime().toLocalDate().equals(currentDate)) {

				if (currentDateHistory != null) {
					currentDateHistory.setTransactions(dateTransactions.toArray(new Transaction[] {}));
				}
				
				currentDate = transaction.getDateTime().toLocalDate();
				currentDateHistory = new DateHistory();
				currentDateHistory.setDate(currentDate);
				dateTransactions = new ArrayList<Transaction>();
				dates.add(currentDateHistory);
			}

			Transaction t = Transaction.builder()
				.description(transaction.getDescription())
				.value(transaction.getValue())
				.sequence(transaction.getSequence())
				.uuid(transaction.getUuid())
				.build();

			dateTransactions.add(t);
		}
		
		currentDateHistory.setTransactions(dateTransactions.toArray(new Transaction[] {}));
		
		var result = new TransactionHistoryDto();
		result.setDates(dates.toArray(new DateHistory[] {}));
		return result;
	}
}
