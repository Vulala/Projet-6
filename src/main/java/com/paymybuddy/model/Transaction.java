package com.paymybuddy.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Transaction {

	@Id
	private int id;
	@ManyToOne
	@JoinColumn(name = "userSender")
	private User userSender;
	@ManyToOne
	@JoinColumn(name = "userReceiver")
	private User userReceiver;
	private Date date;
	private String description;
	private Double amount;

	protected Transaction() {
	}

	public Transaction(User userSender, User userReceiver, Date date, String description, Double amount) {
		this.userSender = userSender;
		this.userReceiver = userReceiver;
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

	public User getUserSender() {
		return userSender;
	}

	public void setUserSender(User userSender) {
		this.userSender = userSender;
	}

	public User getUserReceiver() {
		return userReceiver;
	}

	public void setUserReceiver(User userReceiver) {
		this.userReceiver = userReceiver;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Transaction [id=" + id + ", userSender=" + userSender + ", userReceiver=" + userReceiver + ", date="
				+ date + ", description=" + description + ", amount=" + amount + "]";
	}

}
