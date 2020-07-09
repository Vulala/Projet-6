package com.paymybuddy.integration;

import org.junit.jupiter.api.Disabled;

@Disabled
public class BankAccountIT {

	// private BankAccountRepository bankAccountDAO = new BankAccountRepository();
/**
	@Test
	public void givenReadingTheBankAccountTable_whenFindAll_thenItShowAllTheBankAccountOfTheDB() {
		assertDoesNotThrow(() -> bankAccountDAO.findAll());
	}

	@Disabled
	@Test
	public void givenReadingTheBankAccountTable_whenFindById_thenItShowASpecificBankAccountOfTheDB() {
		String IBAN = "'IBAN'";
		assertDoesNotThrow(() -> bankAccountDAO.findById(IBAN));
	}

	@Test
	public void givenAddingANewBankAccount_whenSaveBankAccount_thenItSaveTheBankAccountInTheDB() {
		// ARRANGE
		String IBAN = "IBAN";
		String description = "description";
		BankAccountDTO bankAccountDTO = new BankAccountDTO(IBAN, description);
		// ACT
		bankAccountDAO.deleteBankAccount(IBAN);
		boolean request = bankAccountDAO.saveBankAccount(bankAccountDTO);
		// ASSERT
		assertDoesNotThrow(() -> request);
	}

	@Test
	public void givenUpdatingABankAccountDescription_whenUpdateBankAccountDescription_thenItUpdateTheBankAccountInTheDB() {
		// ARRANGE
		String newDescription = "DESCRIPTION";
		String iban = "IBAN";
		// ACT
		boolean request = bankAccountDAO.updateBankAccountDescription(newDescription, iban);
		// ASSERT
		assertDoesNotThrow(() -> request);
	}

	@Test
	public void givenDeletingABankAccount_whenDeleteBankAccount_thenItDeleteTheBankAccountInTheDB() {
		// ARRANGE
		String IBAN = "IBAN";
		// ACT | ASSERT
		assertDoesNotThrow(() -> bankAccountDAO.deleteBankAccount(IBAN));
	}
*/
}
