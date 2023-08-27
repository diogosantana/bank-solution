package com.example.banksolution.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateAccountDto {

	@NotNull
	@Min(1)
	private Long customerId;
	
	private Double initialAmount;
}
