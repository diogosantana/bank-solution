package com.example.banksolution.test;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.banksolution.dto.AccountTransactionDto;
import com.example.banksolution.dto.CreateAccountDto;
import com.example.banksolution.dto.DepositDto;
import com.example.banksolution.dto.RegisterCustomerDto;
import com.example.banksolution.dto.TransferDto;
import com.example.banksolution.dto.WithdrawDto;
import com.example.banksolution.model.Account;
import com.example.banksolution.model.AccountTransaction;
import com.example.banksolution.model.Customer;
import com.example.banksolution.repository.AccountTransactionRepository;
import com.example.banksolution.service.AccountService;
import com.example.banksolution.service.CustomerService;
import com.example.banksolution.service.TransactionService;

@Component
public class TestUtil {
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private AccountTransactionRepository accountTransactionRepository;

	public TransferDto transferDto(Account origin, Account destination, Double amount) {
		return TransferDto.builder()
				.originAccountId(origin.getId())
				.destinationAccountId(destination.getId())
				.amount(amount)
				.build();
	}

	public DepositDto depositDto(Account account, Double amount, String identification) {
		return DepositDto.builder()
				.accountId(account.getId())
				.amount(amount)
				.identification(identification)
				.build();
	}
	
	public WithdrawDto withdrawDto(Account account, Double amount) {
		return WithdrawDto.builder()
				.accountId(account.getId())
				.amount(amount)
				.build();
	}

	public Account account(String customerName, Double accountInitialAmount) {
		RegisterCustomerDto registerCustomerDto = RegisterCustomerDto.builder()
				.name(customerName)
				.build();
		Customer customer = customerService.registerCustomer(registerCustomerDto);
		
		CreateAccountDto createAccountDto = CreateAccountDto.builder()
				.customerId(customer.getId())
				.initialAmount(accountInitialAmount)
				.build();
		
		return accountService.createAccount(createAccountDto);
	}
	
	public void depositWithDateTime(DepositDto dto, LocalDateTime dateTime) {
		AccountTransactionDto deposit = transactionService.deposit(dto);
		
		AccountTransaction transaction = accountTransactionRepository.findById(deposit.getId()).orElseThrow();
		transaction.setDateTime(dateTime);
		accountTransactionRepository.saveAndFlush(transaction);
	}
	
	public void withdrawWithDateTime(WithdrawDto dto, LocalDateTime dateTime) {
		AccountTransactionDto withdraw = transactionService.withdraw(dto);
		
		AccountTransaction transaction = accountTransactionRepository.findById(withdraw.getId()).orElseThrow();
		transaction.setDateTime(dateTime);
		accountTransactionRepository.saveAndFlush(transaction);
	}
}
