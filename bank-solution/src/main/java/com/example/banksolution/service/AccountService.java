package com.example.banksolution.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.banksolution.dto.CreateAccountDto;
import com.example.banksolution.exception.BadRequestException;
import com.example.banksolution.model.Account;
import com.example.banksolution.model.Customer;
import com.example.banksolution.repository.AccountRepository;

@Service
public class AccountService {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private AccountRepository accountRepository;

	public Account createAccount(CreateAccountDto dto) {
		
		Double initialAmount = dto.getInitialAmount() != null ?
				dto.getInitialAmount() :
				0;
		
		Customer customer = customerService.getCustomerById(dto.getCustomerId())
				.orElseThrow(()-> new BadRequestException("No customer found with id " + dto.getCustomerId()));
		
		Account account = Account.builder()
			.customer(customer)
			.initialAmount(initialAmount)
			.currentAmount(initialAmount)
			.build();
		
		accountRepository.saveAndFlush(account);
		
		return account;
	}
	
	public Double getBalance(Long accountId) {
		Account account = getAccount(accountId);
		return account.getCurrentAmount();
	}

	public Account closeAccount(Long accountId) {
		Account account = getAccount(accountId);
		
		if (account.getCurrentAmount() != 0) {
			throw new BadRequestException("The account should have no credit");
		}
		
		account.setClosed(true);
		
		accountRepository.saveAndFlush(account);
		
		return account;
	}
	
	public Account getAccount(Long accountId) {
		return accountRepository.findById(accountId)
				.orElseThrow(()-> new BadRequestException("No account found with id " + accountId));
	}
	
	public void checkAccountNotClosed(Account account) {
		if (account.getClosed()) {
			throw new BadRequestException("The account is closed");
		}
	}

}
