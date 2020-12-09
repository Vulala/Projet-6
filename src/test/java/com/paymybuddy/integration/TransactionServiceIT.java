package com.paymybuddy.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.impl.TransactionServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(TransactionServiceImpl.class)
public class TransactionServiceIT {

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(transactionService).isNotNull();
		assertThat(testEntityManager).isNotNull();
	}

	@Test
	public void givenGettingTransactions_whenFindAllTransactionByUserEmail_thenItReturnAllTheTransactionsForTheSpecifiedUser() {
		// ARRANGE
		User userSender = new User("emailTransaction", "lastNameTransaction", "firstNameTransaction",
				"passwordNotEncrypted", 20.0, null, null, null);
		User userReceiver = new User("emailTransaction2", "lastNameTransaction2", "firstNameTransaction2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction(userSender, userReceiver, date,
				"descriptionFindAllTransactionByUserEmail", 10.0);
		Transaction transaction2 = new Transaction(userSender, userReceiver, date,
				"descriptionFindAllTransactionByUserEmail2", 10.0);
		transaction.setId(1);
		transaction2.setId(2);
		userSender.setId(1);
		userReceiver.setId(2);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);
		testEntityManager.persist(transaction);
		testEntityManager.persist(transaction2);

		// ACT
		Iterable<Transaction> result = transactionService.findAllByUserSender(transaction.getUserSender());

		// ASSERT
		assertThat(result).size().isGreaterThan(1);
	}

}
