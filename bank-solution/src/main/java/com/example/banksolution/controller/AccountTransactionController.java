package com.example.banksolution.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.banksolution.dto.AccountTransactionDto;
import com.example.banksolution.dto.DepositDto;
import com.example.banksolution.dto.TransactionHistoryDto;
import com.example.banksolution.dto.TransactionHistoryRequestDto;
import com.example.banksolution.dto.TransferDto;
import com.example.banksolution.dto.TransferResultDto;
import com.example.banksolution.dto.WithdrawDto;
import com.example.banksolution.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class AccountTransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@PostMapping("/deposit")
	public AccountTransactionDto deposit(@RequestBody DepositDto dto) {
		return transactionService.deposit(dto);
	}
	
	@PostMapping("/withdraw")
	public AccountTransactionDto withdraw(@RequestBody WithdrawDto dto) {
		return transactionService.withdraw(dto);
	}
	
	@PostMapping("/transfer")
	public TransferResultDto transfer(@RequestBody TransferDto transferDto) {
		return transactionService.transfer(transferDto);
	}
	
	@GetMapping("history/{accountId}")
	public TransactionHistoryDto history(
			@PathVariable("accountId") Long accountId,
			@RequestParam("startDate") LocalDate startDate,
			@RequestParam(name = "endDate", required = false) LocalDate endDate) {

		TransactionHistoryRequestDto requestDto = TransactionHistoryRequestDto.builder()
			.accountId(accountId)
			.startDate(startDate)
			.endDate(endDate)
			.build();
		
		return transactionService.history(requestDto);
	}
}
