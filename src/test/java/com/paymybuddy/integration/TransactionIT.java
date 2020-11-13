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

import com.paymybuddy.model.Transaction;
import com.paymybuddy.repository.TransactionRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TransactionIT {

	@Autowired
	private TransactionRepository transactionRepository;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(transactionRepository).isNotNull();
	}

	@Test
	public void givenGettingATransaction_whenFindById_thenItReturnTheRightTransaction() {
		// ACT
		Optional<Transaction> result = transactionRepository.findById(1);

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals("emailTest", result.get().getUserEmail());
		assertEquals("descriptionTest", result.get().getDescription());
	}

	@Test
	public void givenGettingTransactions_whenFindAll_thenItReturnAllTransactions() {
		// ACT
		Iterable<Transaction> result = transactionRepository.findAll();

		// ASSERT
		assertThat(result).size().isBetween(1, 2);
	}

	@Test
	public void givenGettingTransactions_whenFindAllByUserEmail_thenItReturnAllTheTransactionsRelatedToTheSpecifiedUser() {
		// ACT
		Iterable<Transaction> result = transactionRepository.findAllByUserEmail("emailTest");

		// ASSERT
		assertThat(result).size().isBetween(1, 2);
	}

	@Test
	public void givenSavingATransaction_whenSave_thenItSaveTheTransaction() {
		// ARRANGE
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction("userEmailSave", "userEmailReceiverSave", date, "descriptionSave",
				10);
		// ACT
		transactionRepository.save(transaction);
		Optional<Transaction> result = transactionRepository.findById(transaction.getId());

		// ASSERT
		assertEquals(transaction.getUserEmail(), result.get().getUserEmail());
	}

	@Test
	public void givenUpdatingATransaction_whenFindSetSave_thenItUpdateTheTransaction() {
		// ACT
		Optional<Transaction> transactionToUpdate = transactionRepository.findById(1);
		transactionToUpdate.get().setDescription("descriptionUpdated");
		transactionRepository.save(transactionToUpdate.get());
		Optional<Transaction> result = transactionRepository.findById(transactionToUpdate.get().getId());

		// ASSERT
		assertEquals(transactionToUpdate.get().getUserEmail(), result.get().getUserEmail());
		assertEquals(transactionToUpdate.get().getDescription(), result.get().getDescription());
	}

	@Test
	public void givenDeletingATransaction_whenDelete_thenItDeleteTheTransaction() {
		// ACT
		transactionRepository.deleteById(1);
		Optional<Transaction> result = transactionRepository.findById(1);

		// ASSERT
		assertThat(result).isEmpty();

	}

	@Test
	public void givenGettingAWrongTransaction_whenFindById_thenItThrowsAnException() {
		// ACT
		Optional<Transaction> result = transactionRepository.findById(0);

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getUserEmail());
	}

}
