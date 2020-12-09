package com.paymybuddy.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.impl.TransactionServiceImpl;

/**
 * Interface used for the business logic, it is implemented by the corresponding
 * {@link TransactionServiceImpl} class. <br>
 * It is used to interact with the database, defining method related to the
 * transaction entity. <br>
 * Can then be called/autowired in a controller layer.
 */
public interface TransactionService {

	Iterable<Transaction> findAllByUserSender(User user);

}
