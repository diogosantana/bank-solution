package com.example.banksolution.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionHistoryRequestDto {

	@NotNull
	private Long accountId;
	
	@NotNull
	private LocalDate startDate;
	
	private LocalDate endDate;
}
