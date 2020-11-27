package com.paymybuddy;

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
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BankAccountTest {

	@Autowired
	private BankAccountRepository bankAccountRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(bankAccountRepository).isNotNull();
		assertThat(testEntityManager).isNotNull();
	}

	@Test
	public void givenGettingABankAccount_whenFindByIBAN_thenItReturnTheRightBankAccount() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANFindByIBAN", "descriptionFindByIBAN");
		testEntityManager.persist(bankAccount);

		// ACT
		Optional<BankAccount> result = bankAccountRepository.findByIBAN(bankAccount.getIBAN());

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals(bankAccount.getIBAN(), result.get().getIBAN());
		assertEquals(bankAccount.getDescription(), result.get().getDescription());
	}

	@Test
	public void givenGettingBankAccounts_whenFindAll_thenItReturnAllBankAccounts() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANFindAll", "descriptionFindAll");
		BankAccount bankAccount2 = new BankAccount("IBANFindAll2", "descriptionFindAll2");
		bankAccount2.setId(2);
		testEntityManager.persist(bankAccount);
		testEntityManager.persist(bankAccount2);

		// ACT
		Iterable<BankAccount> result = bankAccountRepository.findAll();

		// ASSERT
		assertThat(result).size().isBetween(2, 2);
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
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANUpdate", "descriptionUpdate");
		testEntityManager.persist(bankAccount);

		// ACT
		Optional<BankAccount> bankAccountToUpdate = bankAccountRepository.findByIBAN(bankAccount.getIBAN());
		bankAccountToUpdate.get().setDescription("descriptionUpdated");
		bankAccountRepository.save(bankAccountToUpdate.get());
		Optional<BankAccount> result = bankAccountRepository.findByIBAN(bankAccount.getIBAN());

		// ASSERT
		assertEquals(bankAccountToUpdate.get().getIBAN(), result.get().getIBAN());
		assertEquals(bankAccountToUpdate.get().getDescription(), result.get().getDescription());
	}

	@Test
	public void givenDeletingABankAccount_whenDelete_thenItDeleteTheBankAccount() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANDelete", "descriptionDelete");
		bankAccount.setId(1);
		testEntityManager.persist(bankAccount);

		// ACT
		bankAccountRepository.deleteById(bankAccount.getId());
		Optional<BankAccount> result = bankAccountRepository.findById(bankAccount.getId());

		// ASSERT
		assertThat(result).isEmpty();

	}

	@Test
	public void givenGettingAWrongBankAccount_whenFindByIBAN_thenItThrowsAnException() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANFindByIBAN", "descriptionFindByIBAN");
		testEntityManager.persist(bankAccount);

		// ACT
		Optional<BankAccount> result = bankAccountRepository.findByIBAN("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getIBAN());
	}
}
