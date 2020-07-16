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

import com.paymybuddy.model.Transaction;
import com.paymybuddy.repository.TransactionRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TransactionTest {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(transactionRepository).isNotNull();
		assertThat(testEntityManager).isNotNull();
	}

	@Test
	public void givenGettingATransaction_whenFindById_thenItReturnTheRightTransaction() {
		// ARRANGE
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction("userEmailFindById", "userEmailReceiverFindById", date,
				"descriptionFindById", 10);
		testEntityManager.persist(transaction);

		// ACT
		Optional<Transaction> result = transactionRepository.findById(transaction.getUserEmail());

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals(transaction.getUserEmail(), result.get().getUserEmail());
		assertEquals(transaction.getDescription(), result.get().getDescription());
	}

	@Test
	public void givenGettingTransactions_whenFindAll_thenItReturnAllTransactions() {
		// ARRANGE
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction("userEmailFindAll", "userEmailReceiverFindAll", date,
				"descriptionFindAll", 10);
		Transaction transaction2 = new Transaction("userEmailFindAll2", "userEmailReceiverFindAll2", date,
				"descriptionFindAll2", 10);
		testEntityManager.persist(transaction);
		testEntityManager.persist(transaction2);

		// ACT
		Iterable<Transaction> result = transactionRepository.findAll();

		// ASSERT
		assertThat(result).size().isBetween(2, 2);
	}

	@Test
	public void givenSavingATransaction_whenSave_thenItSaveTheTransaction() {
		// ARRANGE
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction("userEmailSave", "userEmailReceiverSave", date, "descriptionSave",
				10);
		// ACT
		transactionRepository.save(transaction);
		Optional<Transaction> result = transactionRepository.findById(transaction.getUserEmail());

		// ASSERT
		assertEquals(transaction.getUserEmail(), result.get().getUserEmail());
	}

	@Test
	public void givenUpdatingATransaction_whenFindSetSave_thenItUpdateTheTransaction() {
		// ARRANGE
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction("userEmailUpdate", "userEmailReceiverUpdate", date,
				"descriptionUpdate", 10);
		testEntityManager.persist(transaction);

		// ACT
		Optional<Transaction> transactionToUpdate = transactionRepository.findById(transaction.getUserEmail());
		transactionToUpdate.get().setDescription("descriptionUpdated");
		transactionRepository.save(transactionToUpdate.get());
		Optional<Transaction> result = transactionRepository.findById(transaction.getUserEmail());

		// ASSERT
		assertEquals(transactionToUpdate.get().getUserEmail(), result.get().getUserEmail());
		assertEquals(transactionToUpdate.get().getDescription(), result.get().getDescription());
	}

	@Test
	public void givenDeletingATransaction_whenDelete_thenItDeleteTheTransaction() {
		// ARRANGE
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction("userEmailDelete", "userEmailReceiverDelete", date,
				"descriptionDelete", 10);
		testEntityManager.persist(transaction);

		// ACT
		transactionRepository.deleteById(transaction.getUserEmail());
		Optional<Transaction> result = transactionRepository.findById(transaction.getUserEmail());

		// ASSERT
		assertThat(result).isEmpty();

	}

	@Test
	public void givenGettingAWrongTransaction_whenFindById_thenItThrowsAnException() {
		// ARRANGE
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction("userEmailFindById", "userEmailReceiverFindById", date,
				"descriptionFindById", 10);
		testEntityManager.persist(transaction);

		// ACT
		Optional<Transaction> result = transactionRepository.findById("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getUserEmail());
	}

}
