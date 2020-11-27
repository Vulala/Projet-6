package com.paymybuddy.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Buddy {

	@Id
	private String emailBuddy;
	private String firstName;
	private String lastName;
	private String description;
	@ManyToMany(mappedBy = "buddy")
	private List<User> users = new ArrayList<>();

	protected Buddy() {
	}

	public Buddy(String emailBuddy, String firstName, String lastName, String description) {
		this.emailBuddy = emailBuddy;
		this.firstName = firstName;
		this.lastName = lastName;
		this.description = description;
	}

	public String getEmailBuddy() {
		return emailBuddy;
	}

	public void setEmailBuddy(String emailBuddy) {
		this.emailBuddy = emailBuddy;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getUsers() {
		return users;
	}

	@Override
	public String toString() {
		return "Buddy [emailBuddy=" + emailBuddy + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", description=" + description + "]";
	}

}
