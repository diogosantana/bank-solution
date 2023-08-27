package com.example.banksolution.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferResultDto {

	@NotNull
	private AccountTransactionDto origin;
	
	@NotNull
	private AccountTransactionDto destination;
}
