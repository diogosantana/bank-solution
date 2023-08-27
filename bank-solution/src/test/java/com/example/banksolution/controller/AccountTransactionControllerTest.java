package com.example.banksolution.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.banksolution.dto.DepositDto;
import com.example.banksolution.dto.TransferDto;
import com.example.banksolution.dto.WithdrawDto;
import com.example.banksolution.model.Account;
import com.example.banksolution.test.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AccountTransactionControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private TestUtil testUtil;

	@Autowired
	private ObjectMapper mapper;
	
	@Test
	void deposit() throws Exception {
		Account account = testUtil.account("Customer 1", 0.0);
		DepositDto depositDto = DepositDto.builder()
			.accountId(account.getId())
			.amount(10.0)
			.identification("John")
			.build();
	
		String body = mapper.writer().withDefaultPrettyPrinter()
			.writeValueAsString(depositDto);
		
		mvc.perform(post("/api/transactions/deposit")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(content().string(containsString("Deposit from")))
		;
	}

	
	@Test
	void withdraw() throws Exception {
		Account account = testUtil.account("Customer 2", 20.0);
		WithdrawDto withdrawtDto = WithdrawDto.builder()
			.accountId(account.getId())
			.amount(10.0)
			.build();
	
		String body = mapper.writer().withDefaultPrettyPrinter()
			.writeValueAsString(withdrawtDto);
		
		mvc.perform(post("/api/transactions/withdraw")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(content().string(containsString("Withdraw")))
		;
	}	
	
	@Test
	void withdrawNoFunds() throws Exception {
		Account account = testUtil.account("Customer 3", 20.0);
		WithdrawDto withdrawtDto = WithdrawDto.builder()
			.accountId(account.getId())
			.amount(50.0)
			.build();
	
		String body = mapper.writer().withDefaultPrettyPrinter()
			.writeValueAsString(withdrawtDto);
		
		mvc.perform(post("/api/transactions/withdraw")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
			.andExpect(status().isBadRequest())
		;
	}
	
	@Test
	void transfer() throws Exception {
		Account origin = testUtil.account("Customer 4", 150.0);
		Account destination = testUtil.account("Customer 5", 60.0);

		TransferDto transferDto = TransferDto.builder()
			.originAccountId(origin.getId())
			.destinationAccountId(destination.getId())
			.amount(70.0)
			.build();
	
		String body = mapper.writer().withDefaultPrettyPrinter()
			.writeValueAsString(transferDto);
		
		mvc.perform(post("/api/transactions/transfer")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(content().string(containsString("Transfer to")))
			.andExpect(content().string(containsString("Transfer from")))
		;
	}
}
