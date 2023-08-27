package com.example.banksolution.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.example.banksolution.exception.BadRequestException;
import com.example.banksolution.model.Account;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LockAccountService {

	private Random random = new Random();
	
	private Map<Long, Long> lockIds = new HashMap<>();
	
	public Long lock(Account account) {
		if (lockIds.containsKey(account.getId())) {
			throw new BadRequestException("The account is in a locked state");
		}
		Long lockId = random.nextLong();
		
		lockIds.put(account.getId(), lockId);
		
		return lockId;
	}
	
	public void unlock(Long lockId) {
		if (!lockIds.containsValue(lockId)) {
			throw new BadRequestException("The account is not in a locked state");
		}
		for (Map.Entry<Long, Long> entry : lockIds.entrySet()) {
			Long accountId = entry.getKey();
			Long val = entry.getValue();
			if (val == lockId) {
				log.debug("Unlocking account {} with lockId {} ", accountId, lockId);
				lockIds.remove(accountId);
				return;
			}
		}
	}
}
