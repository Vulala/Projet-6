package com.paymybuddy.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BankAccountIT {

	@Autowired
	private BankAccountRepository bankAccountRepository;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(bankAccountRepository).isNotNull();
	}

	@Test
	public void givenGettingABankAccount_whenFindById_thenItReturnTheRightBankAccount() {
		// ACT
		Optional<BankAccount> result = bankAccountRepository.findByIBAN("ibanTest");

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals("ibanTest", result.get().getIBAN());
		assertEquals("descriptionTest", result.get().getDescription());
	}

	@Test
	public void givenGettingBankAccounts_whenFindAll_thenItReturnAllBankAccounts() {
		// ACT
		Iterable<BankAccount> result = bankAccountRepository.findAll();

		// ASSERT
		assertThat(result).size().isBetween(1, 2);
	}

	@Test
	public void givenSavingABankAccount_whenSave_thenItSaveTheBankAccount() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANSave", "descriptionSave");
		// ACT
		bankAccountRepository.save(bankAccount);
		Optional<BankAccount> result = bankAccountRepository.findByIBAN(bankAccount.getIBAN());

		// ASSERT
		assertEquals(bankAccount.getIBAN(), result.get().getIBAN());
	}

	@Test
	public void givenUpdatingABankAccount_whenFindSetSave_thenItUpdateTheBankAccount() {
		// ACT
		Optional<BankAccount> bankAccountToUpdate = bankAccountRepository.findByIBAN("ibanTest");
		bankAccountToUpdate.get().setDescription("descriptionUpdated");
		bankAccountRepository.save(bankAccountToUpdate.get());
		Optional<BankAccount> result = bankAccountRepository.findByIBAN(bankAccountToUpdate.get().getIBAN());

		// ASSERT
		assertEquals(bankAccountToUpdate.get().getIBAN(), result.get().getIBAN());
		assertEquals(bankAccountToUpdate.get().getDescription(), result.get().getDescription());
	}

	@Test
	public void givenDeletingABankAccount_whenDelete_thenItDeleteTheBankAccount() {
		// ACT
		bankAccountRepository.deleteById(1);
		Optional<BankAccount> result = bankAccountRepository.findByIBAN("ibanTest");

		// ASSERT
		assertThat(result).isEmpty();

	}

	@Test
	public void givenGettingAWrongBankAccount_whenFindById_thenItThrowsAnException() {
		// ACT
		Optional<BankAccount> result = bankAccountRepository.findByIBAN("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getIBAN());
	}
}
