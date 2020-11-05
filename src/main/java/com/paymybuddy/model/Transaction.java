package com.paymybuddy.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Transaction {

	@Id
	private int id;
	private String userEmail;
	private String userEmailReceiver;
	private Date date;
	private String description;
	private int amount;

	protected Transaction() {
	}

	public Transaction(String userEmail, String userEmailReceiver, Date date, String description, int amount) {
		this.userEmail = userEmail;
		this.userEmailReceiver = userEmailReceiver;
		this.date = date;
		this.description = description;
		this.amount = amount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserEmailReceiver() {
		return userEmailReceiver;
	}

	public void setUserEmailReceiver(String userEmailReceiver) {
		this.userEmailReceiver = userEmailReceiver;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Transaction [id=" + id + ", userEmail=" + userEmail + ", userEmailReceiver=" + userEmailReceiver
				+ ", date=" + date + ", description=" + description + ", amount=" + amount + "]";
	}

}
