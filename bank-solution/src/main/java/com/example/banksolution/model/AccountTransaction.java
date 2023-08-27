package com.example.banksolution.model;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "account_id", nullable = false, updatable = false)
	@NotNull
	private Account account;
	
	@Column(nullable = false, updatable = false)
	@NotNull
	@Min(1)
	private Long sequence;
	
	@Column(nullable = false)
	@NotNull
	private String uuid;

	@Column(nullable = false)
	@NotNull
	private LocalDateTime dateTime;
	
	@Column(nullable = false, length = 100)
	@Length(min = 5, max = 100)
	@NotBlank
	private String description;
	
	@Column(name = "amount", nullable = false)
	@NotNull
	private Double value;
}
