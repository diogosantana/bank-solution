package com.example.banksolution.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class GenerateTransferIdService {

	public String generate() {
		return UUID.randomUUID().toString();
	}
}
