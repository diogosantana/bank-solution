package com.example.banksolution.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.banksolution.dto.CreateAccountDto;
import com.example.banksolution.model.Account;
import com.example.banksolution.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@PostMapping
	public Account createAccount(@RequestBody CreateAccountDto dto) {
		return accountService.createAccount(dto);
	}
	
	@GetMapping("{accountId}")
	public Double getBalance(@PathVariable("accountId") Long accountId) {
		return accountService.getBalance(accountId);
	}
}
