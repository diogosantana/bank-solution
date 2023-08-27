package com.example.banksolution.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.banksolution.dto.AccountTransactionDto;
import com.example.banksolution.dto.DepositDto;
import com.example.banksolution.dto.TransactionHistoryDto;
import com.example.banksolution.dto.TransactionHistoryRequestDto;
import com.example.banksolution.dto.TransferDto;
import com.example.banksolution.dto.TransferResultDto;
import com.example.banksolution.dto.WithdrawDto;
import com.example.banksolution.exception.BadRequestException;
import com.example.banksolution.model.Account;
import com.example.banksolution.test.TestUtil;

import jakarta.transaction.Transactional;

@SpringBootTest
class TransactionServiceTest {

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TestUtil testUtil;

	@Test
	@Transactional
	void testDepositSuccess() {
		Double initialAmount = 0.0;
		Account account = testUtil.account("Customer 1", initialAmount);
		
		String identification = "John";
		Double depositAmount = 10.0;
		
		DepositDto depositDto = testUtil.depositDto(account, depositAmount, identification); 
		
		AccountTransactionDto depositTransaction = transactionService.deposit(depositDto);
		
		account = accountService.getAccount(account.getId());
		
		assertNotNull(depositTransaction);
		assertNotNull(depositTransaction.getAccountId());
		assertNotNull(depositTransaction.getDateTime());
		assertNotNull(depositTransaction.getDescription());
		assertNotNull(depositTransaction.getSequence());
		assertNotNull(depositTransaction.getUuid());
		
		assertEquals(depositDto.getAccountId(), depositTransaction.getAccountId());
		assertEquals(depositDto.getAmount(), depositTransaction.getValue());
		assertEquals(initialAmount + depositAmount , account.getCurrentAmount());

		assertThat(depositTransaction.getDescription()).contains(identification);
		assertThat(depositTransaction.getDescription()).containsIgnoringCase("deposit");
	}

	@Test
	@Transactional
	void testDepositNegativeAmount() {
		Account account = testUtil.account("Customer 2", 0.0);		
		DepositDto depositDto = testUtil.depositDto(account, -10.0, "John"); 
		
		assertThatThrownBy(()->{
			transactionService.deposit(depositDto);			
		}).isInstanceOfAny(BadRequestException.class)
			.hasMessageContaining("positive");
	}

	@Test
	@Transactional
	void testWithdrawSuccess() {
		Double initialAmount = 10.0;
		Double withdrawAmount = 10.0;
		Account account = testUtil.account("Customer 3", initialAmount);
		
		WithdrawDto withdrawDto = testUtil.withdrawDto(account, withdrawAmount); 
		
		AccountTransactionDto withdrawTransaction = transactionService.withdraw(withdrawDto);
		
		account = accountService.getAccount(account.getId());
		
		assertNotNull(withdrawTransaction);
		assertNotNull(withdrawTransaction.getAccountId());
		assertNotNull(withdrawTransaction.getDateTime());
		assertNotNull(withdrawTransaction.getDescription());
		assertNotNull(withdrawTransaction.getSequence());
		assertNotNull(withdrawTransaction.getUuid());
		
		assertEquals(withdrawDto.getAccountId(), withdrawTransaction.getAccountId());
		assertEquals(withdrawDto.getAmount(), withdrawTransaction.getValue());
		assertEquals(initialAmount - withdrawAmount, account.getCurrentAmount());
		
		assertThat(withdrawTransaction.getDescription()).containsIgnoringCase("withdraw");
	}

	@Test
	@Transactional
	void testWithdrawNegativeAmount() {
		Double initialAmount = 10.0;
		Double withdrawAmount = -10.0;
		Account account = testUtil.account("Customer 4", initialAmount);
		
		WithdrawDto withdrawDto = testUtil.withdrawDto(account, withdrawAmount); 
		
		assertThatThrownBy(()->{
			transactionService.withdraw(withdrawDto);			
		}).isInstanceOfAny(BadRequestException.class)
			.hasMessageContaining("positive");
	}


	@Test
	@Transactional
	void testWithdrawNegativeAccountAmount() {
		Double initialAmount = 10.0;
		Double withdrawAmount = 20.0;
		Account account = testUtil.account("Customer 5", initialAmount);
		
		WithdrawDto withdrawDto = testUtil.withdrawDto(account, withdrawAmount); 
		
		assertThatThrownBy(()->{
			transactionService.withdraw(withdrawDto);			
		}).isInstanceOfAny(BadRequestException.class)
			.hasMessageContaining("Not enough funds");
	}

	@Test
	@Transactional
	void testTransferSuccess() {
		Double initialAmount1 = 100.0;
		Double initialAmount2 = 30.0;
		
		Double transferAmount = 60.0;
		
		Double expectedAmount1 = 40.0;
		Double expectedAmount2 = 90.0;
		
		Account account1 = testUtil.account("Customer 5", initialAmount1);
		Account account2 = testUtil.account("Customer 6", initialAmount2);

		TransferDto transferDto = testUtil.transferDto(account1, account2, transferAmount);
		
		TransferResultDto transferResultDto = transactionService.transfer(transferDto);
		
		assertNotNull(transferResultDto);
		assertNotNull(transferResultDto.getOrigin());
		assertNotNull(transferResultDto.getDestination());
		
		assertEquals(account1.getId(), transferResultDto.getOrigin().getAccountId());
		assertEquals(account2.getId(), transferResultDto.getDestination().getAccountId());

		account1 = accountService.getAccount(account1.getId());
		account2 = accountService.getAccount(account2.getId());
		
		assertEquals(expectedAmount1, account1.getCurrentAmount());
		assertEquals(expectedAmount2, account2.getCurrentAmount());
	}
	
	@Test
	@Transactional
	void testTransferNoFunds() {
		Double initialAmount1 = 50.0;
		Double initialAmount2 = 30.0;
		
		Double transferAmount = 60.0;
		
		Account account1 = testUtil.account("Customer 7", initialAmount1);
		Account account2 = testUtil.account("Customer 8", initialAmount2);

		TransferDto transferDto = testUtil.transferDto(account1, account2, transferAmount);

		assertThatThrownBy(()->{
			transactionService.transfer(transferDto);
		}).isInstanceOfAny(BadRequestException.class)
			.hasMessageContaining("Not enough funds");
	}
	
	@Test
	@Transactional
	void testHistory() {
		Double initialAmount = 10.0;
		Double firstDepositAmount = 30.0;
		Double firstWithdrawAmount = 10.0;
		Double secondWithdrawAmount = 5.0;
		Double secondDepositAmount = 25.0;
		
		Account account = testUtil.account("Customer 9", initialAmount);
		
		DepositDto firstDeposit = testUtil.depositDto(account, firstDepositAmount, null);
		WithdrawDto firstWithdraw = testUtil.withdrawDto(account, firstWithdrawAmount);
		DepositDto secondDeposit = testUtil.depositDto(account, secondDepositAmount, null);
		WithdrawDto secondWithdraw = testUtil.withdrawDto(account, secondWithdrawAmount);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

		LocalDateTime firstDate = LocalDateTime.parse("11/08/2023 13:20", formatter);
		LocalDateTime secondDate = LocalDateTime.parse("12/08/2023 18:30", formatter);
		LocalDateTime thirdDate = LocalDateTime.parse("13/08/2023 09:45", formatter);
		LocalDateTime FourthDate = LocalDateTime.parse("13/08/2023 10:15", formatter);
		
		testUtil.depositWithDateTime(firstDeposit, firstDate);
		testUtil.withdrawWithDateTime(firstWithdraw, secondDate);
		testUtil.depositWithDateTime(secondDeposit, thirdDate);
		testUtil.withdrawWithDateTime(secondWithdraw, FourthDate);
		
		TransactionHistoryRequestDto requestDto = TransactionHistoryRequestDto.builder()
			.accountId(account.getId())
			.startDate(secondDate.toLocalDate())
			.endDate(thirdDate.toLocalDate())
			.build();
		
		TransactionHistoryDto transactionHistoryDto = transactionService.history(requestDto);
		
		account = accountService.getAccount(account.getId());
		
		assertNotNull(transactionHistoryDto);
		assertNotNull(transactionHistoryDto.getDates());
		assertEquals(2, transactionHistoryDto.getDates().length);
			
		Double expectedAmount = initialAmount 
				+ (firstDepositAmount + secondDepositAmount)
				- (firstWithdrawAmount + secondWithdrawAmount);

		assertEquals(expectedAmount, account.getCurrentAmount());
	}
	
}
