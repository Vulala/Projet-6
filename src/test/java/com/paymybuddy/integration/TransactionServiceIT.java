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
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction("userEmailFindAllTransactionByUserEmail",
				"userEmailReceiverFindAllTransactionByUserEmail", date, "descriptionFindAllTransactionByUserEmail",
				10.0);
		Transaction transaction2 = new Transaction("userEmailFindAllTransactionByUserEmail",
				"userEmailReceiverFindAllTransactionByUserEmail2", date, "descriptionFindAllTransactionByUserEmail2",
				10.0);
		transaction.setId(1);
		transaction2.setId(2);
		testEntityManager.persist(transaction);
		testEntityManager.persist(transaction2);

		// ACT
		Iterable<Transaction> result = transactionService.findAllTransactionByUserEmail(transaction.getUserEmail());

		// ASSERT
		assertThat(result).size().isGreaterThan(1);
	}

}
