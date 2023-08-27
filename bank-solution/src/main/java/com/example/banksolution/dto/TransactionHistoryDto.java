package com.example.banksolution.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDto {

	@NotNull
	private DateHistory[] dates;
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DateHistory {
		
		@NotNull
		private LocalDate date;
		
		@NotNull
		private Transaction[] transactions;
	}
	
	@Data
	@Builder
	public static class Transaction {
		
		@NotNull
		private String uuid;
		
		@NotNull
		private Long sequence;
		
		@NotNull
		private String description;
		
		@NotNull
		private Double value;
	}
}
