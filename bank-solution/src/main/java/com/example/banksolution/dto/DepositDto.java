package com.example.banksolution.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepositDto {

	@NotNull
	private Long accountId;
	
	@NotNull
	@Min(0)
	private Double amount;
	
	private String identification;
}
