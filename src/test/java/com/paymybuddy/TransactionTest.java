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
import com.paymybuddy.model.User;
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
		User userSender = new User("emailFindById", "lastNameFindById", "firstNameFindById", "passwordNotEncrypted",
				20.0, null, null, null);
		User userReceiver = new User("emailFindById2", "lastNameFindById2", "firstNameFindById2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction(userSender, userReceiver, date, "descriptionFindById", 10.0);
		transaction.setId(1);
		userSender.setId(1);
		userReceiver.setId(2);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);
		testEntityManager.persist(transaction);

		// ACT
		Optional<Transaction> result = transactionRepository.findById(1);

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals(transaction.getUserSender().getEmail(), result.get().getUserSender().getEmail());
		assertEquals(transaction.getAmount(), result.get().getAmount());
		assertEquals(transaction.getDescription(), result.get().getDescription());
	}

	@Test
	public void givenGettingTransactions_whenFindAll_thenItReturnAllTheTransactionsForTheSpecifiedUser() {
		// ARRANGE
		User userSender = new User("emailFindAll", "lastNameFindAll", "firstNameFindAll", "passwordNotEncrypted", 20.0,
				null, null, null);
		User userReceiver = new User("emailFindAll2", "lastNameFindAll2", "firstNameFindAll2", "passwordNotEncrypted2",
				0.0, null, null, null);
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction(userSender, userReceiver, date, "descriptionFindById", 10.0);
		Transaction transaction2 = new Transaction(userSender, userReceiver, date, "descriptionFindAll2", 10.0);
		transaction.setId(1);
		transaction2.setId(2);
		userSender.setId(1);
		userReceiver.setId(2);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);
		testEntityManager.persist(transaction);
		testEntityManager.persist(transaction2);

		// ACT
		Iterable<Transaction> result = transactionRepository.findAll();

		// ASSERT
		assertThat(result).size().isBetween(2, 2);
	}

	@Test
	public void givenGettingTransactions_whenFindAllByUserSender_thenItReturnAllTheTransactionsForTheSpecifiedUser() {
		// ARRANGE
		User userSender = new User("emailFindAllByUserSender", "lastNameFindAllByUserSender",
				"firstNameFindAllByUserSender", "passwordNotEncrypted", 20.0, null, null, null);
		User userReceiver = new User("emailFindAllByUserSender2", "lastNameFindAllByUserSender2",
				"firstNameFindAllByUserSender2", "passwordNotEncrypted2", 0.0, null, null, null);
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction(userSender, userReceiver, date, "descriptionFindById", 10.0);
		Transaction transaction2 = new Transaction(userSender, userReceiver, date, "descriptionFindAllByUserEmail2",
				10.0);
		transaction.setId(1);
		transaction2.setId(2);
		userSender.setId(1);
		userReceiver.setId(2);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);
		testEntityManager.persist(transaction);
		testEntityManager.persist(transaction2);

		// ACT
		Iterable<Transaction> result = transactionRepository.findAllByUserSender(transaction.getUserSender());

		// ASSERT
		assertThat(result).size().isBetween(2, 2);
	}

	@Test
	public void givenSavingATransaction_whenSave_thenItSaveTheTransaction() {
		// ARRANGE
		User userSender = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 20.0, null,
				null, null);
		User userReceiver = new User("emailSave2", "lastNameSave2", "firstNameSave2", "passwordNotEncrypted2", 0.0,
				null, null, null);
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction(userSender, userReceiver, date, "descriptionFindById", 10.0);
		transaction.setId(1);
		userSender.setId(1);
		userReceiver.setId(2);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);

		// ACT
		transactionRepository.save(transaction);
		Optional<Transaction> result = transactionRepository.findById(1);

		// ASSERT
		assertEquals(transaction.getUserSender().getEmail(), result.get().getUserSender().getEmail());
		assertEquals(transaction.getDate(), result.get().getDate());
		assertEquals(transaction.getUserReceiver(), result.get().getUserReceiver());
	}

	@Test
	public void givenUpdatingATransaction_whenFindSetSave_thenItUpdateTheTransaction() {
		// ARRANGE
		User userSender = new User("emailFindSetSave", "lastNameFindSetSave", "firstNameFindSetSave",
				"passwordNotEncrypted", 20.0, null, null, null);
		User userReceiver = new User("emailFindSetSave2", "lastNameFindSetSave2", "firstNameFindSetSave2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction(userSender, userReceiver, date, "descriptionFindById", 10.0);
		transaction.setId(1);
		userSender.setId(1);
		userReceiver.setId(2);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);
		testEntityManager.persist(transaction);

		// ACT
		Optional<Transaction> transactionToUpdate = transactionRepository.findById(1);
		transactionToUpdate.get().setDescription("descriptionUpdated");
		transactionToUpdate.get().setAmount(20.0);
		transactionToUpdate.get().setDate(date);
		transactionRepository.save(transactionToUpdate.get());
		Optional<Transaction> result = transactionRepository.findById(1);

		// ASSERT
		assertEquals(transactionToUpdate.get().getUserSender().getEmail(), result.get().getUserSender().getEmail());
		assertEquals(transactionToUpdate.get().getDescription(), result.get().getDescription());
	}

	@Test
	public void givenDeletingATransaction_whenDelete_thenItDeleteTheTransaction() {
		// ARRANGE
		User userSender = new User("emailDelete", "lastNameDelete", "firstNameDelete", "passwordNotEncrypted", 20.0,
				null, null, null);
		User userReceiver = new User("emailDelete2", "lastNameDelete2", "firstNameDelete2", "passwordNotEncrypted2",
				0.0, null, null, null);
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction(userSender, userReceiver, date, "descriptionFindById", 10.0);
		transaction.setId(1);
		userSender.setId(1);
		userReceiver.setId(2);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);
		testEntityManager.persist(transaction);

		// ACT
		transactionRepository.deleteById(1);
		Optional<Transaction> result = transactionRepository.findById(1);

		// ASSERT
		assertThat(result).isEmpty();

	}

	@Test
	public void givenGettingAWrongTransaction_whenFindById_thenItThrowsAnException() {
		// ARRANGE
		User userSender = new User("emailFindById", "lastNameFindById", "firstNameFindById", "passwordNotEncrypted",
				20.0, null, null, null);
		User userReceiver = new User("emailFindById2", "lastNameFindById2", "firstNameFindById2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction(userSender, userReceiver, date, "descriptionFindById", 10.0);
		testEntityManager.persist(transaction);

		// ACT
		Optional<Transaction> result = transactionRepository.findById(999);

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getUserSender().getEmail());
	}

}
