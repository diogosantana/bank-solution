package com.example.banksolution.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferDto {

	@NotNull
	@Min(0)
	private Long originAccountId;
	
	@NotNull
	@Min(0)
	private Long destinationAccountId;
	
	@NotNull
	@Min(0)
	private Double amount;
}
