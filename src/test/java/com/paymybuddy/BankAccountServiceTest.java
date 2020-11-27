package com.paymybuddy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.service.impl.BankAccountServiceImpl;

public class BankAccountServiceTest {

	@InjectMocks
	private BankAccountServiceImpl bankAccountServiceImpl;

	@Mock
	private BankAccountRepository bankAccountRepository;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(bankAccountServiceImpl).isNotNull();
	}

	@Test
	public void givenGettingABankAccount_whenGetBankAccount_thenItReturnTheRightBankAccount() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANFindByIBAN", "descriptionFindByIBAN");
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccount);
		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(bankAccountOptional);

		// ACT
		Optional<BankAccount> result = bankAccountServiceImpl.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals(bankAccount.getIBAN(), result.get().getIBAN());
		assertEquals(bankAccount.getDescription(), result.get().getDescription());
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccount.getIBAN());
	}

	@Test
	public void givenGettingBankAccounts_whenFindAllBankAccount_thenItReturnAllBankAccounts() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANFindAll", "descriptionFindAll");
		BankAccount bankAccount2 = new BankAccount("IBANFindAll2", "descriptionFindAll2");
		List<BankAccount> bankAccountIterable = new ArrayList<BankAccount>();
		bankAccountIterable.add(bankAccount);
		bankAccountIterable.add(bankAccount2);
		when(bankAccountRepository.findAll()).thenReturn(bankAccountIterable);

		// ACT
		Iterable<BankAccount> result = bankAccountServiceImpl.findAllBankAccount();

		// ASSERT
		assertThat(result).size().isGreaterThan(1);
		verify(bankAccountRepository, times(1)).findAll();
	}

	@Test
	public void givenSavingABankAccount_whenSaveBankAccount_thenItSaveTheBankAccount() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANSave", "descriptionSave");
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccount);
		when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);
		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(bankAccountOptional);

		// ACT
		bankAccountServiceImpl.saveBankAccount(bankAccount);
		Optional<BankAccount> result = bankAccountServiceImpl.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertEquals(bankAccount.getIBAN(), result.get().getIBAN());
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccount.getIBAN());
		verify(bankAccountRepository, times(1)).save(bankAccount);
	}

	@Test
	public void givenUpdatingABankAccount_whenUpdateBankAccount_thenItUpdateTheBankAccount() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANUpdate", "descriptionUpdate");
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccount);
		when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);
		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(bankAccountOptional);

		// ACT
		Optional<BankAccount> bankAccountToUpdate = bankAccountServiceImpl.getBankAccount(bankAccount.getIBAN());
		bankAccountToUpdate.get().setDescription("descriptionUpdated");
		bankAccountServiceImpl.updateBankAccount(bankAccountToUpdate.get());
		Optional<BankAccount> result = bankAccountServiceImpl.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertEquals(bankAccountToUpdate.get().getIBAN(), result.get().getIBAN());
		assertEquals(bankAccountToUpdate.get().getDescription(), result.get().getDescription());
		verify(bankAccountRepository, times(2)).findByIBAN(bankAccount.getIBAN());
		verify(bankAccountRepository, times(1)).save(bankAccount);
	}

	@Test
	public void givenDeletingABankAccount_whenDeleteBankAccount_thenItDeleteTheBankAccount() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANDelete", "descriptionDelete");

		// ACT
		bankAccountServiceImpl.deleteBankAccount(bankAccount);

		// ASSERT
		verify(bankAccountRepository, times(1)).delete(bankAccount);
	}

	@Test
	public void givenGettingAWrongBankAccount_whenGetBankAccount_thenItThrowsAnException() {
		// ACT
		Optional<BankAccount> result = bankAccountServiceImpl.getBankAccount("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getIBAN());
	}
}
