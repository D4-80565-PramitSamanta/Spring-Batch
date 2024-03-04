package com.batch.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.batch.entity.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Integer> {
	
}
