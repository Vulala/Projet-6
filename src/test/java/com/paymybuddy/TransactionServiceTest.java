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
	public void givenGettingTransactions_whenFindAllTransactionByUserEmail_thenItReturnAllTheTransactionsForTheSpecifiedUser() {
		// ARRANGE
		java.sql.Date date = new java.sql.Date(0);
		Transaction transaction = new Transaction("userEmailFindAllTransactionByUserEmail",
				"userEmailReceiverFindAllTransactionByUserEmail2", date, "descriptionFindAllTransactionByUserEmail",
				10.0);
		Transaction transaction2 = new Transaction("userEmailFindAllTransactionByUserEmail",
				"userEmailReceiverFindAllTransactionByUserEmail2", date, "descriptionFindAllTransactionByUserEmail2",
				10.0);
		List<Transaction> transactionIterable = new ArrayList<Transaction>();
		transactionIterable.add(transaction);
		transactionIterable.add(transaction2);
		when(transactionRepository.findAllByUserEmail(transaction.getUserEmail())).thenReturn(transactionIterable);

		// ACT
		Iterable<Transaction> result = transactionServiceImpl.findAllTransactionByUserEmail(transaction.getUserEmail());

		// ASSERT
		assertThat(result).size().isGreaterThan(1);
		verify(transactionRepository, times(1)).findAllByUserEmail(transaction.getUserEmail());
	}

}
