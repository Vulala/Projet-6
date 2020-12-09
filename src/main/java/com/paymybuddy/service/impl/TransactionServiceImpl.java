package com.paymybuddy.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.service.TransactionService;

/**
 * Service which implement the {@link TransactionService} interface. <br>
 * It override the methods and define the business logic. <br>
 * It make use of the {@link TransactionRepository} interface.<br>
 * <br>
 * 
 * The class is annotated with {@link Transactional}, rolling back every
 * transactions in case of any Exceptions thrown by the different methods.
 */
@Service
@Transactional(rollbackOn = { Exception.class })
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;

	@Autowired
	public TransactionServiceImpl(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@Override
	public Iterable<Transaction> findAllByUserSender(User user) {
		return transactionRepository.findAllByUserSender(user);
	}
}
