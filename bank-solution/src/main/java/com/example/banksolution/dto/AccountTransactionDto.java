package com.example.banksolution.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountTransactionDto {

	private Long id;

	private Long accountId;
	
	private Long sequence;
	
	private String uuid;

	private LocalDateTime dateTime;
	
	private String description;
	
	private Double value;
}
