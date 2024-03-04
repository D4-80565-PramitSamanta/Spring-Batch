package com.batch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {
	@Id
	@Column(name = "customer_id")
	int id;
	
	@Column(name = "fisrt_name")
	String firstName;
	
	@Column(name = "last_name")
	String lastName;
	
	String eamil;
	
	String gender;
	
	@Column(name = "contact_no")
	String contactNo;
	
	String country;

	String dob;
}
