package com.paymybuddy.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
public class User {

	@Id
	private String email;
	private String lastName;
	private String firstName;
	private String password;
	private int moneyAvailable;
	@OneToMany
	private List<BankAccount> bankAccount; // Hibernate mapping
	@OneToMany
	private List<Transaction> transaction; // Hibernate mapping

	protected User() {
	}

	public User(String email, String lastName, String firstName, String password, int moneyAvailable,
			List<BankAccount> bankAccount, List<Transaction> transaction) {
		this.email = email;
		this.lastName = lastName;
		this.firstName = firstName;
		this.password = BCrypt.hashpw(password, BCrypt.gensalt(10));
		this.moneyAvailable = moneyAvailable;
		this.bankAccount = bankAccount;
		this.transaction = transaction;
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
		String newPasswordToEncrypt = BCrypt.hashpw(password, BCrypt.gensalt(10));
		this.password = newPasswordToEncrypt;
	}

	public int getMoneyAvailable() {
		return moneyAvailable;
	}

	public void setMoneyAvailable(int moneyAvailable) {
		this.moneyAvailable = moneyAvailable;
	}

	public List<BankAccount> getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(List<BankAccount> bankAccount) {
		this.bankAccount = bankAccount;
	}

	public List<Transaction> getTransaction() {
		return transaction;
	}

	public void setTransaction(List<Transaction> transaction) {
		this.transaction = transaction;
	}

	@Override
	public String toString() {
		return "User [email=" + email + ", lastName=" + lastName + ", firstName=" + firstName + ", password=" + password
				+ ", moneyAvailable=" + moneyAvailable + ", bankAccount=" + bankAccount + ", transaction=" + transaction
				+ "]";
	}

}
