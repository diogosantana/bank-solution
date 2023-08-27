package com.example.banksolution.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.banksolution.dto.RegisterCustomerDto;
import com.example.banksolution.exception.BadRequestException;
import com.example.banksolution.model.Customer;
import com.example.banksolution.repository.CustomerRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	
	public Optional<Customer> getCustomerById(Long customerId) {
		return customerRepository.findById(customerId);
	}
	
	public Customer registerCustomer(RegisterCustomerDto dto) {
		
		if (!StringUtils.hasText(dto.getName())) {
			throw new BadRequestException("Customer name must not be empty.");
		}
		
		Customer customer = Customer.builder()
			.name(dto.getName())
			.build();
		
		customerRepository.saveAndFlush(customer);
		
		return customer;
	}
}
