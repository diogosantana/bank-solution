package com.example.banksolution.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
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
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	private Customer customer;
	
	@Column(nullable = false)
	@Min(0)
	@NotNull
	private Double initialAmount;
	
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<AccountTransaction> transactions;
	
	@Column(nullable = false)
	@Min(0)
	@NotNull
	private Double currentAmount;
	
	@NotNull
	@Builder.Default
	private Boolean closed = false;

}
