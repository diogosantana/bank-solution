package com.example.banksolution.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.banksolution.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
