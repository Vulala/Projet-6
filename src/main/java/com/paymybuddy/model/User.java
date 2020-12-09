package com.paymybuddy.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
public class User {

	@Id
	private int id;
	private String email;
	private String lastName;
	private String firstName;
	private String password;
	private Double moneyAvailable;
	@OneToOne
	@JoinColumn(name = "bank_account")
	private BankAccount bankAccount;
	@OneToMany(mappedBy = "userSender")
	private List<Transaction> transaction = new ArrayList<>();
	@ManyToMany
	private List<User> friends = new ArrayList<>();

	protected User() {
	}

	public User(String email, String lastName, String firstName, String password, Double moneyAvailable,
			BankAccount bankAccount, List<Transaction> transaction, List<User> friends) {
		this.email = email;
		this.lastName = lastName;
		this.firstName = firstName;
		this.password = password;
		this.moneyAvailable = moneyAvailable;
		this.bankAccount = bankAccount;
		this.transaction = transaction;
		this.friends = friends;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		String newPasswordToEncrypt = BCrypt.hashpw(password, BCrypt.gensalt(10)); // Crypt the password when set again
		this.password = newPasswordToEncrypt;
	}

	public Double getMoneyAvailable() {
		return moneyAvailable;
	}

	public void setMoneyAvailable(Double moneyAvailable) {
		this.moneyAvailable = moneyAvailable;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public List<Transaction> getTransaction() {
		return transaction;
	}

	public void setTransaction(List<Transaction> transaction) {
		this.transaction = transaction;
	}

	public List<User> getFriends() {
		return friends;
	}

	public void setFriends(List<User> friends) {
		this.friends = friends;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", lastName=" + lastName + ", firstName=" + firstName
				+ ", password=" + password + ", moneyAvailable=" + moneyAvailable + ", bankAccount=" + bankAccount
				+ ", transaction=" + transaction + ", friends=" + friends + "]";
	}

}
