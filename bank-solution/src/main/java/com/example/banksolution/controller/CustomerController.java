package com.example.banksolution.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.banksolution.dto.RegisterCustomerDto;
import com.example.banksolution.exception.NotFoundException;
import com.example.banksolution.model.Customer;
import com.example.banksolution.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@PostMapping
	public Customer post(@RequestBody RegisterCustomerDto dto) {
		return customerService.registerCustomer(dto);
	}
	
	@GetMapping("/{id}")
	public Customer get(@PathVariable Long id) {
		return customerService.getCustomerById(id)
				.orElseThrow(()->new NotFoundException());
	}
}
