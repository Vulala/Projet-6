package com.paymybuddy.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.PayMyBuddyService;

/**
 * Service which implement the {@link PayMyBuddyService} interface. <br>
 * It override the methods and define the business logic. <br>
 * It make use of the differents {@link com.paymybuddy.repository} interfaces.
 * <br>
 * <br>
 * The class is annotated with {@link Transactional}, rolling back every
 * transactions in case of any Exceptions thrown by the different methods.
 */
@Service
@Transactional(rollbackOn = { Exception.class })
public class PayMyBuddyServiceImpl implements PayMyBuddyService {

	private final UserRepository userRepository;
	private final BankAccountRepository bankAccountRepository;
	private final TransactionRepository transactionRepository;

	@Autowired
	public PayMyBuddyServiceImpl(UserRepository userRepository, BankAccountRepository bankAccountRepository,
			TransactionRepository transactionRepository) {
		this.userRepository = userRepository;
		this.bankAccountRepository = bankAccountRepository;
		this.transactionRepository = transactionRepository;
	}

	/**
	 * This method allow the user to add a bank account. <br>
	 * The bank account newly created, is also saved in the bank_account database
	 * table. <br>
	 * 
	 * @param user        : the user doing the action
	 * @param iban        : the bank account to add
	 * @param description : the user's description about the bank account
	 */
	@Override
	public void addBankAccount(User user, String iban, String description) {
		Optional<User> userToUpdate = userRepository.findByEmail(user.getEmail());

		if (!userToUpdate.isPresent()) {
			throw new NoSuchElementException("The provided User: << " + user + " >> cannot be found.");
		}

		BankAccount bankAccount = new BankAccount(iban, description);
		user.setBankAccount(bankAccount);
		userRepository.save(user);
		bankAccountRepository.save(bankAccount);
	}

	/**
	 * This method allow the user to delete a bank account. <br>
	 * The bank account deleted, is also deleted in the bank_account database
	 * table.<br>
	 * 
	 * @param user : the user doing the action
	 * @param iban : the bank account to delete
	 */
	@Override
	public void deleteBankAccount(User user, String iban) {
		Optional<BankAccount> bankAccountToDelete = bankAccountRepository.findByIBAN(iban);
		Optional<User> userToUpdate = userRepository.findByEmail(user.getEmail());

		if (!userToUpdate.isPresent()) {
			throw new NoSuchElementException("The provided User: << " + user + " >> cannot be found.");
		}
		if (!bankAccountToDelete.isPresent()) {
			throw new IllegalArgumentException("The provided IBAN: << " + iban + " >> is not valid.");
		}
		if (!userToUpdate.get().getBankAccount().equals(bankAccountToDelete.get())) {
			throw new IllegalArgumentException(
					"The provided IBAN: << " + iban + " >> is not associated to this: " + user + " account.");
		}

		bankAccountRepository.delete(bankAccountToDelete.get());
		BankAccount bankAccount = new BankAccount(null, null);
		user.setBankAccount(bankAccount);
		userRepository.save(user);
	}

	/**
	 * This method allow the user to make a transaction. <br>
	 * The transaction newly created, is also saved in the transaction database
	 * table.<br>
	 * It use the {@link makeTransaction} method.
	 * 
	 * @param {@link Transaction}
	 */
	@Override
	public void createTransaction(User userSendingMoney, User userGettingMoney, String description,
			Double amountOfTheTransaction) {
		Optional<User> userSendingToUpdate = userRepository.findByEmail(userSendingMoney.getEmail());
		Optional<User> userGettingToUpdate = userRepository.findByEmail(userGettingMoney.getEmail());

		if (!userSendingToUpdate.isPresent()) {
			throw new NoSuchElementException("The provided User: << " + userSendingMoney + " >> cannot be found.");
		}
		if (!userGettingToUpdate.isPresent()) {
			throw new NoSuchElementException("The provided User: << " + userGettingMoney + " >> cannot be found.");
		}
		if (amountOfTheTransaction < 1) {
			throw new IllegalArgumentException(
					"The provided amount for the transaction: << " + amountOfTheTransaction + " >> is not valid.");
		}

		makeTransaction(userSendingMoney, userGettingMoney, amountOfTheTransaction);

		Transaction transaction = new Transaction(userSendingMoney, userGettingMoney, Date.valueOf(LocalDate.now()),
				description, amountOfTheTransaction);
		transactionRepository.save(transaction);

		List<Transaction> userTransactions = userSendingMoney.getTransaction();
		userTransactions.add(transaction);
		userSendingMoney.setTransaction(userTransactions);

		userRepository.save(userSendingMoney);
		userRepository.save(userGettingMoney);
	}

	/**
	 * This method do the transaction, it firstly verify that the user sending money
	 * have enough money on his account and can afford the tax. <br>
	 * 
	 * Then, depending of the result, it proceed or not the transaction. <br>
	 * 
	 * To finish, it give the amount of the tax to the paymybuddy account, which is
	 * the enterprise's account. <br>
	 * 
	 * @param userSendingMoney
	 * @param userGettingMoney
	 * @param amountOfTheTransaction
	 */
	@Override
	public void makeTransaction(User userSendingMoney, User userGettingMoney, Double amountOfTheTransaction) {
		Double tax = amountOfTheTransaction * 0.05;
		Double amountOfTheTransactionWithTax = amountOfTheTransaction + tax;
		Double moneyAvailableBeforeTheTransactionUserSending = userSendingMoney.getMoneyAvailable();

		if (moneyAvailableBeforeTheTransactionUserSending < amountOfTheTransactionWithTax) {
			throw new IllegalArgumentException("The money available on the account is not enough to afford the request."
					+ " Money : " + moneyAvailableBeforeTheTransactionUserSending + " Tax : " + tax);
		}

		userSendingMoney
				.setMoneyAvailable(moneyAvailableBeforeTheTransactionUserSending - amountOfTheTransactionWithTax);
		userGettingMoney.setMoneyAvailable(userGettingMoney.getMoneyAvailable() + amountOfTheTransaction);

		// Add the amount of the tax into the paymybuddy account.
		Optional<User> userPayMyBuddyOptional = userRepository.findByEmail("paymybuddy@paymybuddy.com");
		if (!userPayMyBuddyOptional.isPresent()) {
			throw new NoSuchElementException(
					"The provided User: << " + userPayMyBuddyOptional + " >> cannot be found.");
		}

		User userPayMyBuddy = userPayMyBuddyOptional.get();
		userPayMyBuddy.setMoneyAvailable(userPayMyBuddy.getMoneyAvailable() + tax);
		userRepository.save(userPayMyBuddy);
	}

	/**
	 * Method used to add a User as a friend. <br>
	 * It firstly verify that both the user and the friend are present in the
	 * database. <br>
	 * Then it add the friend to the user's friend list. <be>
	 * 
	 * @param user : adding a friend
	 * @param user : to add as a friend
	 */
	@Override
	public void addFriend(User user, User friend) {
		Optional<User> userCheck = userRepository.findByEmail(user.getEmail());
		Optional<User> friendCheck = userRepository.findByEmail(friend.getEmail());

		if (!userCheck.isPresent()) {
			throw new NoSuchElementException("The provided User: << " + user + " >> cannot be found.");
		}
		if (!friendCheck.isPresent()) {
			throw new NoSuchElementException("The provided Friend: << " + friend + " >> cannot be found.");
		}

		List<User> userFriendList = user.getFriends();
		userFriendList.add(friend);
		user.setFriends(userFriendList);
		userRepository.save(user);
	}

	/**
	 * Method used to delete a Friend of a User. <br>
	 * It firstly verify that both the user and the friend are present in the
	 * database. <br>
	 * Then it delete the friend from the user's friend list. <be>
	 * 
	 * @param user : deleting a friend
	 * @param user : to delete
	 */
	@Override
	public void deleteFriend(User user, User friend) {
		Optional<User> userCheck = userRepository.findByEmail(user.getEmail());
		Optional<User> friendCheck = userRepository.findByEmail(friend.getEmail());

		if (!userCheck.isPresent()) {
			throw new NoSuchElementException("The provided User: << " + user + " >> cannot be found.");
		}
		if (!friendCheck.isPresent()) {
			throw new NoSuchElementException("The provided Friend: << " + friend + " >> cannot be found.");
		}

		user.getFriends().remove(friendCheck.get());
		userRepository.save(user);
	}

	/**
	 * Method used to transfert money from the user's bank account to his paymybuddy
	 * account. <br>
	 * <br>
	 * It firstly verify that both the user and the bank account are present in the
	 * database. <br>
	 * Then it verify that the provided bank account is associated to the current
	 * user. <br>
	 * Then it proceed if all the conditions are set to true; the user's money is
	 * updated in the database table. <be>
	 * 
	 * @param user             : transfering money
	 * @param bankAccount      : to get money from
	 * @param amountTransfered : from the bankAccount to the user's paymybuddy
	 *                         account
	 */
	@Override
	public void addMoneyOnThePayMyBuddyAccountFromBankAccount(User user, BankAccount bankAccount,
			Double amountTransfered) {
		Optional<User> userCheck = userRepository.findByEmail(user.getEmail());
		Optional<BankAccount> bankAccountCheck = bankAccountRepository.findByIBAN(bankAccount.getIBAN());

		if (!userCheck.isPresent()) {
			throw new NoSuchElementException("The provided User: << " + user + " >> cannot be found.");
		}
		if (!bankAccountCheck.isPresent()) {
			throw new NoSuchElementException("The provided Bank account: << " + bankAccount + " >> cannot be found.");
		}
		if (!userCheck.get().getBankAccount().equals(bankAccount)) {
			throw new NoSuchElementException("The provided Bank account: << " + bankAccount
					+ " >> is not associated to this: " + user + " account.");
		}

		user.setMoneyAvailable(user.getMoneyAvailable() + amountTransfered);
		userRepository.save(user);
	}

}
