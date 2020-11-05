package com.paymybuddy.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BankAccount {

	@Id
	private int id;
	private String IBAN;
	private String description;

	protected BankAccount() {
	}

	public BankAccount(String iBAN, String description) {
		this.IBAN = iBAN;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		return "BankAccount [id=" + id + ", IBAN=" + IBAN + ", description=" + description + "]";
	}

}