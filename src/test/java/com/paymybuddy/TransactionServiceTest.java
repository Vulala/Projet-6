package com.paymybuddy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.service.impl.TransactionServiceImpl;

public class TransactionServiceTest {

	@InjectMocks
	private TransactionServiceImpl transactionServiceImpl;

	@Mock
	private TransactionRepository transactionRepository;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(transactionServiceImpl).isNotNull();
	}

	@Test
	public void givenGettingTransactions_whenFindAllTransactionByUserSender_thenItReturnAllTheTransactionsForTheSpecifiedUser() {
		// ARRANGE
		User userSender = new User("emailFindAllTransactionByUserSender", "lastNameFindAllTransactionByUserSender",
				"firstNameFindAllTransactionByUserSender", "passwordNotEncrypted", 20.0, null, null, null);
		User userReceiver = new User("emailFindAllTransactionByUserSender2", "lastNameFindAllTransactionByUserSender2",
				"firstNameFindAllTransactionByUserSender2", "passwordNotEncrypted2", 0.0, null, null, null);
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction(userSender, userReceiver, date,
				"descriptionFindAllTransactionByUserEmail", 10.0);
		Transaction transaction2 = new Transaction(userSender, userReceiver, date,
				"descriptionFindAllTransactionByUserEmail2", 10.0);
		List<Transaction> transactionIterable = new ArrayList<Transaction>();
		transactionIterable.add(transaction);
		transactionIterable.add(transaction2);
		when(transactionRepository.findAllByUserSender(transaction.getUserSender())).thenReturn(transactionIterable);

		// ACT
		Iterable<Transaction> result = transactionServiceImpl.findAllByUserSender(transaction.getUserSender());

		// ASSERT
		assertThat(result).size().isGreaterThan(1);
		verify(transactionRepository, times(1)).findAllByUserSender(transaction.getUserSender());
	}

}
