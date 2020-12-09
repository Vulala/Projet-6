package com.paymybuddy.service;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.service.impl.PayMyBuddyServiceImpl;

/**
 * Interface used for the business logic, it is implemented by the corresponding
 * {@link PayMyBuddyServiceImpl} class. <br>
 * It is used to interact with the database, defining method related to the
 * differents entities. <br>
 * Can then be called/autowired in a controller layer.
 */
public interface PayMyBuddyService {

	void addBankAccount(User user, String iban, String description);

	void deleteBankAccount(User user, String iban);

	void createTransaction(User userSendingMoney, User userGettingMoney, String description,
			Double amountOfTheTransaction);

	void makeTransaction(User userSendingMoney, User userGettingMoney, Double amountOfTheTransaction);

	void addFriend(User user, User friend);

	void deleteFriend(User user, User friend);

	void addMoneyOnThePayMyBuddyAccountFromBankAccount(User user, BankAccount bankAccount, Double amountTransfered);

	void transfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount(User user, BankAccount bankAccount,
			Double amountTransfered);

}
