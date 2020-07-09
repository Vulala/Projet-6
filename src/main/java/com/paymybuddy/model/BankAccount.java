package com.paymybuddy.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class BankAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String IBAN;
	private String description;

	protected BankAccount() {
	}

	public BankAccount(String iBAN, String description) {
		this.IBAN = iBAN;
		this.description = description;
	}

	public String getIBAN() {
		return IBAN;
	}

	public void setIBAN(String iBAN) {
		IBAN = iBAN;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "BankAccount [IBAN=" + IBAN + ", description=" + description + "]";
	}

}