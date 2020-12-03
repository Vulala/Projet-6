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
import com.paymybuddy.model.Buddy;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.BuddyRepository;
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
	private final BuddyRepository buddyRepository;

	@Autowired
	public PayMyBuddyServiceImpl(UserRepository userRepository, BankAccountRepository bankAccountRepository,
			TransactionRepository transactionRepository, BuddyRepository buddyRepository) {
		this.userRepository = userRepository;
		this.bankAccountRepository = bankAccountRepository;
		this.transactionRepository = transactionRepository;
		this.buddyRepository = buddyRepository;
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

		if (userToUpdate.isPresent()) {
			BankAccount bankAccount = new BankAccount(iban, description);
			user.setBankAccount(bankAccount);
			userRepository.save(user);
			bankAccountRepository.save(bankAccount);
		} else {
			throw new NoSuchElementException("The provided User: << " + user + " >> cannot be found.");
		}

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

		if (userToUpdate.isPresent()) {
			if (bankAccountToDelete.isPresent()) {
				if (userToUpdate.get().getBankAccount().equals(bankAccountToDelete.get())) {
					bankAccountRepository.delete(bankAccountToDelete.get());

					BankAccount bankAccount = new BankAccount(null, null);
					user.setBankAccount(bankAccount);
					userRepository.save(user);
				} else {
					throw new IllegalArgumentException(
							"The provided IBAN: << " + iban + " >> is not associated to this: " + user + " account.");
				}
			} else {
				throw new IllegalArgumentException("The provided IBAN: << " + iban + " >> is not valid.");
			}
		} else {
			throw new NoSuchElementException("The provided User: << " + user + " >> cannot be found.");
		}
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

		if (userSendingToUpdate.isPresent()) {
			if (userGettingToUpdate.isPresent()) {
				if (amountOfTheTransaction >= 1) {

					makeTransaction(userSendingMoney, userGettingMoney, amountOfTheTransaction);

					Transaction transaction = new Transaction(userSendingMoney.getEmail(), userGettingMoney.getEmail(),
							Date.valueOf(LocalDate.now()), description, amountOfTheTransaction);
					transactionRepository.save(transaction);

					List<Transaction> userTransactions = userSendingMoney.getTransaction();
					userTransactions.add(transaction);
					userSendingMoney.setTransaction(userTransactions);

					userRepository.save(userSendingMoney);
					userRepository.save(userGettingMoney);
				} else {
					throw new IllegalArgumentException("The provided amount for the transaction: << "
							+ amountOfTheTransaction + " >> is not valid.");
				}
			} else {
				throw new NoSuchElementException("The provided User: << " + userGettingMoney + " >> cannot be found.");
			}
		} else {
			throw new NoSuchElementException("The provided User: << " + userSendingMoney + " >> cannot be found.");
		}
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
		User userPayMyBuddy = userRepository.findByEmail("paymybuddy@paymybuddy.com").get();
		userPayMyBuddy.setMoneyAvailable(userPayMyBuddy.getMoneyAvailable() + tax);
		userRepository.save(userPayMyBuddy);
	}

	/**
	 * Method used to add a Buddy to an User. <br>
	 * It firstly verify that both the user and the buddy are present in the
	 * database. <br>
	 * Then it add the buddy to the user's list and save the buddy in the
	 * corresponding buddy database table. <be>
	 * 
	 * @param user        : adding a buddy
	 * @param user        : to add as a buddy
	 * @param description : about the buddy
	 */
	@Override
	public void addBuddy(User user, User userAsBuddy, String description) {
		Optional<User> userCheck = userRepository.findByEmail(user.getEmail());
		Optional<User> buddyCheck = userRepository.findByEmail(userAsBuddy.getEmail());

		if (userCheck.isPresent()) {
			if (buddyCheck.isPresent()) {
				Buddy buddyToAdd = new Buddy(userAsBuddy.getEmail(), userAsBuddy.getFirstName(),
						userAsBuddy.getLastName(), description);
				List<Buddy> userListOfBuddy = user.getBuddy();
				userListOfBuddy.add(buddyToAdd);
				user.setBuddy(userListOfBuddy);

				userRepository.save(user);
				buddyRepository.save(buddyToAdd);
			} else {
				throw new NoSuchElementException("The provided Buddy: << " + userAsBuddy + " >> cannot be found.");
			}
		} else {
			throw new NoSuchElementException("The provided User: << " + user + " >> cannot be found.");
		}

	}

	/**
	 * Method used to update a Buddy of an User. <br>
	 * It firstly verify that both the user and the buddy are present in the
	 * database. <br>
	 * Then it update the buddy from the user's list and update the buddy in the
	 * corresponding buddy database table. <be>
	 * 
	 * @param user        : updating a buddy
	 * @param buddy       : to update
	 * @param description : about the buddy
	 */
	@Override
	public void updateBuddy(User user, Buddy buddy, String description) {
		Optional<User> userCheck = userRepository.findByEmail(user.getEmail());
		Optional<Buddy> buddyCheck = buddyRepository.findByEmailBuddy(buddy.getEmailBuddy());

		if (userCheck.isPresent()) {
			if (buddyCheck.isPresent()) {
				Buddy buddyToUpdate = new Buddy(buddy.getEmailBuddy(), buddy.getFirstName(), buddy.getLastName(),
						description);
				user.getBuddy().remove(buddyCheck.get());
				List<Buddy> userListOfBuddy = user.getBuddy();
				userListOfBuddy.add(buddyToUpdate);
				user.setBuddy(userListOfBuddy);

				userRepository.save(user);
				buddyRepository.save(buddyToUpdate);

			} else {
				throw new NoSuchElementException("The provided Buddy: << " + buddy + " >> cannot be found.");
			}
		} else {
			throw new NoSuchElementException("The provided User: << " + user + " >> cannot be found.");
		}
	}

	/**
	 * Method used to delete a Buddy of an User. <br>
	 * It firstly verify that both the user and the buddy are present in the
	 * database. <br>
	 * Then it delete the buddy from the user's list and delete the buddy in the
	 * corresponding buddy database table. <be>
	 * 
	 * @param user        : deleting a buddy
	 * @param buddy       : to delete
	 * @param description : about the buddy
	 */
	@Override
	public void deleteBuddy(User user, Buddy buddy) {
		Optional<User> userCheck = userRepository.findByEmail(user.getEmail());
		Optional<Buddy> buddyCheck = buddyRepository.findByEmailBuddy(buddy.getEmailBuddy());

		if (userCheck.isPresent()) {
			if (buddyCheck.isPresent()) {
				user.getBuddy().remove(buddyCheck.get());
				userRepository.save(user);
				buddyRepository.delete(buddy);
			} else {
				throw new NoSuchElementException("The provided Buddy: << " + buddy + " >> cannot be found.");
			}
		} else {
			throw new NoSuchElementException("The provided User: << " + user + " >> cannot be found.");
		}

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

		if (userCheck.isPresent()) {
			if (bankAccountCheck.isPresent()) {
				if (userCheck.get().getBankAccount().equals(bankAccount)) {
					user.setMoneyAvailable(user.getMoneyAvailable() + amountTransfered);
					userRepository.save(user);
				} else {
					throw new NoSuchElementException("The provided Bank account: << " + bankAccount
							+ " >> is not associated to this: " + user + " account.");
				}
			} else {
				throw new NoSuchElementException(
						"The provided Bank account: << " + bankAccount + " >> cannot be found.");
			}
		} else {
			throw new NoSuchElementException("The provided User: << " + user + " >> cannot be found.");
		}
	}

}
