package com.paymybuddy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.service.TransactionService;

/**
 * Service which implement the {@link TransactionService} interface. <br>
 * It override the methods and define the business logic. <br>
 * It make use of the {@link TransactionRepository} interface.
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;

	@Autowired
	public TransactionServiceImpl(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@Override
	public Iterable<Transaction> findAllTransactionByUserEmail(String email) {
		return transactionRepository.findAllByUserEmail(email);
	}
}
