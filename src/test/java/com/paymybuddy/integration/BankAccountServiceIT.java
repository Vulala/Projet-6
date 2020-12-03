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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.impl.BankAccountServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(BankAccountServiceImpl.class)
public class BankAccountServiceIT {

	@Autowired
	private BankAccountService bankAccountService;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(bankAccountService).isNotNull();
		assertThat(testEntityManager).isNotNull();
	}

	@Test
	public void givenGettingABankAccount_whenGetBankAccount_thenItReturnTheRightBankAccount() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANGetBankAccount", "descriptionGetBankAccount");
		testEntityManager.persist(bankAccount);

		// ACT
		Optional<BankAccount> result = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals(bankAccount.getIBAN(), result.get().getIBAN());
		assertEquals(bankAccount.getDescription(), result.get().getDescription());
	}

	@Test
	public void givenGettingBankAccounts_whenFindAllBankAccount_thenItReturnAllBankAccounts() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANFindAllBankAccount", "descriptionFindAllBankAccount");
		BankAccount bankAccount2 = new BankAccount("IBANFindAllBankAccount2", "descriptionFindAllBankAccount2");
		bankAccount2.setId(2);
		testEntityManager.persist(bankAccount);
		testEntityManager.persist(bankAccount2);

		// ACT
		Iterable<BankAccount> result = bankAccountService.findAllBankAccount();

		// ASSERT
		assertThat(result).size().isGreaterThan(1);
	}

	@Test
	public void givenSavingABankAccount_whenSaveBankAccount_thenItSaveTheBankAccount() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANSaveBankAccount", "descriptionSaveBankAccount");

		// ACT
		bankAccountService.saveBankAccount(bankAccount);
		Optional<BankAccount> result = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertEquals(bankAccount.getIBAN(), result.get().getIBAN());
	}

	@Test
	public void givenUpdatingABankAccount_whenUpdateBankAccount_thenItUpdateTheBankAccount() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANUpdateBankAccount", "descriptionUpdateBankAccount");
		testEntityManager.persist(bankAccount);

		// ACT
		Optional<BankAccount> bankAccountToUpdate = bankAccountService.getBankAccount(bankAccount.getIBAN());
		bankAccountToUpdate.get().setDescription("descriptionUpdated");
		bankAccountService.updateBankAccount(bankAccountToUpdate.get());
		Optional<BankAccount> result = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertEquals(bankAccountToUpdate.get().getIBAN(), result.get().getIBAN());
		assertEquals(bankAccountToUpdate.get().getDescription(), result.get().getDescription());
	}

	@Test
	public void givenDeletingABankAccount_whenDeleteBankAccount_thenItDeleteTheBankAccount() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANDeleteBankAccount", "descriptionDeleteBankAccount");
		bankAccount.setId(1);
		testEntityManager.persist(bankAccount);

		// ACT
		bankAccountService.deleteBankAccount(bankAccount);
		Optional<BankAccount> result = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertThat(result).isEmpty();

	}

	@Test
	public void givenGettingAWrongBankAccount_whenGetBankAccount_thenItThrowsAnException() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANGetBankAccount", "descriptionGetBankAccount");
		testEntityManager.persist(bankAccount);

		// ACT
		Optional<BankAccount> result = bankAccountService.getBankAccount("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getIBAN());
	}
}
