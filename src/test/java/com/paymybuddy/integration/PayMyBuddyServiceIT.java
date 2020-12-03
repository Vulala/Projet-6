package com.paymybuddy.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.Buddy;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.BuddyService;
import com.paymybuddy.service.PayMyBuddyService;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.service.impl.BankAccountServiceImpl;
import com.paymybuddy.service.impl.BuddyServiceImpl;
import com.paymybuddy.service.impl.PayMyBuddyServiceImpl;
import com.paymybuddy.service.impl.TransactionServiceImpl;
import com.paymybuddy.service.impl.UserServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import({ PayMyBuddyServiceImpl.class, UserServiceImpl.class, BankAccountServiceImpl.class,
		TransactionServiceImpl.class, BuddyServiceImpl.class })
public class PayMyBuddyServiceIT {

	@Autowired
	private PayMyBuddyService payMyBuddyService;

	@Autowired
	private UserService userService;

	@Autowired
	private BankAccountService bankAccountService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private BuddyService buddyService;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(payMyBuddyService).isNotNull();
		assertThat(userService).isNotNull();
		assertThat(bankAccountService).isNotNull();
		assertThat(transactionService).isNotNull();
		assertThat(buddyService).isNotNull();
		assertThat(testEntityManager).isNotNull();
	}

	@Test
	public void givenAddingABankAccount_whenAddBankAccount_thenItAddTheBankAccountToTheUserAndSaveItInTheBankAccountTable() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANAddBankAccount", "descriptionAddBankAccount");
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, null, null,
				null);
		testEntityManager.persist(user);

		// ACT
		payMyBuddyService.addBankAccount(user, bankAccount.getIBAN(), bankAccount.getDescription());
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<BankAccount> resultBankAccount = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertTrue(resultUser.isPresent());
		assertEquals(user.getEmail(), resultUser.get().getEmail());
		assertEquals(user.getFirstName(), resultUser.get().getFirstName());
		assertEquals(user.getBankAccount(), resultUser.get().getBankAccount());
		assertTrue(resultBankAccount.isPresent());
		assertEquals(bankAccount.getIBAN(), resultBankAccount.get().getIBAN());
		assertEquals(bankAccount.getDescription(), resultBankAccount.get().getDescription());
	}

	@Test
	public void givenAddingABankAccountWithAWrongProvidedUser_whenAddBankAccount_thenItDoesNotAddTheBankAccountToTheUserAndDoesNotSaveItInTheBankAccountTable() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANAddBankAccountWithWrongUSer",
				"descriptionAddBankAccountWithWrongUSer");
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, bankAccount,
				null, null);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<BankAccount> resultBankAccount = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertThrows(NoSuchElementException.class,
				() -> payMyBuddyService.addBankAccount(user, bankAccount.getIBAN(), bankAccount.getDescription()));
		assertFalse(resultUser.isPresent());
		assertFalse(resultBankAccount.isPresent());

	}

	@Test
	public void givenDeletingABankAccount_whenDeleteBankAccount_thenItDeleteTheBankAccountFromTheUserAccountAndDeleteItInTheBankAccountTable() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANDeleteBankAccount", "descriptionDeleteBankAccount");
		testEntityManager.persist(bankAccount);
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, bankAccount,
				null, null);
		testEntityManager.persist(user);

		// ACT
		payMyBuddyService.deleteBankAccount(user, bankAccount.getIBAN());
		Optional<User> resultUser = userService.getUser(user.getEmail());

		// ASSERT
		assertTrue(resultUser.isPresent());
		assertEquals(resultUser.get().getBankAccount().toString(), new BankAccount(null, null).toString());
	}

	@Test
	public void givenDeletingABankAccountWithAWrongProvidedUser_whenDeleteBankAccount_thenItDoesNotDeleteTheBankAccountFromTheUserAccountAndDoesNotDeleteItInTheBankAccountTable() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANDeleteBankAccountWrongProvidedUser",
				"descriptionDeleteBankAccountWrongProvidedUser");
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, null, null,
				null);
		testEntityManager.persist(bankAccount);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<BankAccount> resultBankAccount = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertThrows(NoSuchElementException.class,
				() -> payMyBuddyService.deleteBankAccount(user, bankAccount.getIBAN()));
		assertTrue(resultBankAccount.isPresent());
	}

	@Test
	public void givenDeletingABankAccountWithAWrongProvidedIBAN_whenDeleteBankAccount_thenItDoesNotDeleteTheBankAccountFromTheUserAccountAndDoesNotDeleteItInTheBankAccountTable() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANDeleteBankAccountWrongProvidedIBAN",
				"descriptionDeleteBankAccountWrongProvidedIBAN");
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, null, null,
				null);
		testEntityManager.persist(user);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());

		// ASSERT
		assertThrows(IllegalArgumentException.class,
				() -> payMyBuddyService.deleteBankAccount(user, bankAccount.getIBAN()));
		assertEquals(resultUser.get().getBankAccount(), user.getBankAccount());
		assertTrue(resultUser.isPresent());
	}

	@Test
	public void givenDeletingABankAccountWithAProvidedIBANNotRelatedToTheUserBankAccount_whenDeleteBankAccount_thenItDoesNotDeleteTheBankAccountFromTheUserAccountAndDoesNotDeleteItInTheBankAccountTable() {
		// ARRANGE
		BankAccount bankAccountNotAssociated = new BankAccount("Void", "Void");
		BankAccount bankAccountAssociated = new BankAccount("IBANDeleteBankAccountWrongProvidedIBAN",
				"descriptionDeleteBankAccountWrongProvidedIBAN");
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0,
				bankAccountAssociated, null, null);
		bankAccountNotAssociated.setId(2);
		testEntityManager.persist(user);
		testEntityManager.persist(bankAccountAssociated);
		testEntityManager.persist(bankAccountNotAssociated);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<BankAccount> resultBankAccount = bankAccountService.getBankAccount(bankAccountAssociated.getIBAN());

		// ASSERT
		assertThrows(IllegalArgumentException.class,
				() -> payMyBuddyService.deleteBankAccount(user, bankAccountNotAssociated.getIBAN()));
		assertTrue(resultUser.isPresent());
		assertTrue(resultBankAccount.isPresent());
		assertEquals(resultUser.get().getBankAccount(), user.getBankAccount());
	}

	@Test
	public void givenCreatingATransaction_whenCreateTransaction_thenItUpdateTheUserAndSaveTheTransactionInTheTransactionTable() {
		// ARRANGE
		List<Transaction> listOfTheUserTransaction = new ArrayList<>();
		User userSender = new User("emailTransaction", "lastNameTransaction", "firstNameTransaction",
				"passwordNotEncrypted", 20.0, null, listOfTheUserTransaction, null);
		User userReceiver = new User("emailTransaction2", "lastNameTransaction2", "firstNameTransaction2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		User userPayMyBuddy = new User("paymybuddy@paymybuddy.com", "buddy", "paymy", "passwordNotEncrypted", 0.0, null,
				null, null);
		Double amountOfTheTransaction = 10.0;
		userSender.setId(1);
		userReceiver.setId(2);
		userPayMyBuddy.setId(3);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);
		testEntityManager.persist(userPayMyBuddy);

		// ACT
		payMyBuddyService.createTransaction(userSender, userReceiver, "description", amountOfTheTransaction);
		Optional<User> resultUserSender = userService.getUser(userSender.getEmail());
		Optional<User> resultUserReceiver = userService.getUser(userReceiver.getEmail());
		Optional<User> resultUserPayMyBuddy = userService.getUser(userPayMyBuddy.getEmail());
		Iterable<Transaction> resultTransaction = transactionService
				.findAllTransactionByUserEmail(userSender.getEmail());

		// ASSERT
		assertTrue(resultUserSender.isPresent());
		assertTrue(resultUserReceiver.isPresent());
		assertTrue(resultUserPayMyBuddy.isPresent());
		assertEquals(resultUserSender.get().getMoneyAvailable(), 9.5);
		assertEquals(resultUserReceiver.get().getMoneyAvailable(), 10);
		assertEquals(resultUserPayMyBuddy.get().getMoneyAvailable(), 0.5);
		assertThat(resultTransaction).size().isGreaterThan(0);
	}

	@Test
	public void givenCreatingATransactionWithAWrongProvidedUserSender_whenCreateTransaction_thenItDoesNotUpdateTheUserAndDoesNotSaveTheTransactionInTheTransactionTable() {
		// ARRANGE
		User userSender = new User("emailTransaction", "lastNameTransaction", "firstNameTransaction",
				"passwordNotEncrypted", 20.0, null, null, null);
		User userReceiver = new User("emailTransaction2", "lastNameTransaction2", "firstNameTransaction2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		User userPayMyBuddy = new User("paymybuddy@paymybuddy.com", "buddy", "paymy", "passwordNotEncrypted", 0.0, null,
				null, null);
		Double amountOfTheTransaction = 10.0;
		userReceiver.setId(2);
		userPayMyBuddy.setId(3);
		testEntityManager.persist(userReceiver);
		testEntityManager.persist(userPayMyBuddy);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUserSender = userService.getUser(userSender.getEmail());
		Optional<User> resultUserReceiver = userService.getUser(userReceiver.getEmail());
		Optional<User> resultUserPayMyBuddy = userService.getUser(userPayMyBuddy.getEmail());
		Iterable<Transaction> resultTransaction = transactionService
				.findAllTransactionByUserEmail(userSender.getEmail());
		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService.createTransaction(userSender, userReceiver,
				"description", amountOfTheTransaction));

		assertFalse(resultUserSender.isPresent());
		assertTrue(resultUserReceiver.isPresent());
		assertTrue(resultUserPayMyBuddy.isPresent());
		assertEquals(resultUserReceiver.get().getMoneyAvailable(), 0);
		assertEquals(resultUserPayMyBuddy.get().getMoneyAvailable(), 0);
		assertThat(resultTransaction).size().isEqualTo(0);
	}

	@Test
	public void givenCreatingATransactionWithAWrongProvidedUserReceiver_whenCreateTransaction_thenItDoesNotUpdateTheUserAndDoesNotSaveTheTransactionInTheTransactionTable() {
		// ARRANGE
		User userSender = new User("emailTransaction", "lastNameTransaction", "firstNameTransaction",
				"passwordNotEncrypted", 20.0, null, null, null);
		User userReceiver = new User("emailTransaction2", "lastNameTransaction2", "firstNameTransaction2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		User userPayMyBuddy = new User("paymybuddy@paymybuddy.com", "buddy", "paymy", "passwordNotEncrypted", 0.0, null,
				null, null);
		Double amountOfTheTransaction = 10.0;
		userSender.setId(1);
		userPayMyBuddy.setId(3);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userPayMyBuddy);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUserSender = userService.getUser(userSender.getEmail());
		Optional<User> resultUserReceiver = userService.getUser(userReceiver.getEmail());
		Optional<User> resultUserPayMyBuddy = userService.getUser(userPayMyBuddy.getEmail());
		Iterable<Transaction> resultTransaction = transactionService
				.findAllTransactionByUserEmail(userSender.getEmail());
		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService.createTransaction(userSender, userReceiver,
				"description", amountOfTheTransaction));

		assertTrue(resultUserSender.isPresent());
		assertFalse(resultUserReceiver.isPresent());
		assertTrue(resultUserPayMyBuddy.isPresent());
		assertEquals(resultUserSender.get().getMoneyAvailable(), 20);
		assertEquals(resultUserPayMyBuddy.get().getMoneyAvailable(), 0);
		assertThat(resultTransaction).size().isEqualTo(0);
	}

	@Test
	public void givenCreatingATransactionWithAWrongProvidedAmount_whenCreateTransaction_thenItDoesNotUpdateTheUserAndDoesNotSaveTheTransactionInTheTransactionTable() {
		// ARRANGE
		User userSender = new User("emailTransaction", "lastNameTransaction", "firstNameTransaction",
				"passwordNotEncrypted", 20.0, null, null, null);
		User userReceiver = new User("emailTransaction2", "lastNameTransaction2", "firstNameTransaction2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		User userPayMyBuddy = new User("paymybuddy@paymybuddy.com", "buddy", "paymy", "passwordNotEncrypted", 0.0, null,
				null, null);
		Double amountOfTheTransaction = 0.99;
		userSender.setId(1);
		userReceiver.setId(2);
		userPayMyBuddy.setId(3);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);
		testEntityManager.persist(userPayMyBuddy);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUserSender = userService.getUser(userSender.getEmail());
		Optional<User> resultUserReceiver = userService.getUser(userReceiver.getEmail());
		Optional<User> resultUserPayMyBuddy = userService.getUser(userPayMyBuddy.getEmail());
		Iterable<Transaction> resultTransaction = transactionService
				.findAllTransactionByUserEmail(userSender.getEmail());
		// ASSERT
		assertThrows(IllegalArgumentException.class, () -> payMyBuddyService.createTransaction(userSender, userReceiver,
				"description", amountOfTheTransaction));

		assertTrue(resultUserSender.isPresent());
		assertTrue(resultUserReceiver.isPresent());
		assertTrue(resultUserPayMyBuddy.isPresent());
		assertEquals(resultUserSender.get().getMoneyAvailable(), 20);
		assertEquals(resultUserReceiver.get().getMoneyAvailable(), 0);
		assertEquals(resultUserPayMyBuddy.get().getMoneyAvailable(), 0);
		assertThat(resultTransaction).size().isEqualTo(0);
	}

	@Test
	public void givenMakingATransaction_whenMakeTransaction_thenItDoTheTransactionAndUpdateThePayMyBuddyUser() {
		// ARRANGE
		User userSender = new User("emailTransaction", "lastNameTransaction", "firstNameTransaction",
				"passwordNotEncrypted", 20.0, null, null, null);
		User userReceiver = new User("emailTransaction2", "lastNameTransaction2", "firstNameTransaction2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		User userPayMyBuddy = new User("paymybuddy@paymybuddy.com", "buddy", "paymy", "passwordNotEncrypted", 0.0, null,
				null, null);
		Double amountOfTheTransaction = 10.0;
		userSender.setId(1);
		userReceiver.setId(2);
		userPayMyBuddy.setId(3);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);
		testEntityManager.persist(userPayMyBuddy);

		// ACT
		payMyBuddyService.makeTransaction(userSender, userReceiver, amountOfTheTransaction);
		Optional<User> resultUserSender = userService.getUser(userSender.getEmail());
		Optional<User> resultUserReceiver = userService.getUser(userReceiver.getEmail());
		Optional<User> resultUserPayMyBuddy = userService.getUser(userPayMyBuddy.getEmail());
		Iterable<Transaction> resultTransaction = transactionService
				.findAllTransactionByUserEmail(userSender.getEmail());

		// ASSERT
		assertTrue(resultUserSender.isPresent());
		assertTrue(resultUserReceiver.isPresent());
		assertTrue(resultUserPayMyBuddy.isPresent());
		assertEquals(resultUserSender.get().getMoneyAvailable(), 9.5);
		assertEquals(resultUserReceiver.get().getMoneyAvailable(), 10);
		assertEquals(resultUserPayMyBuddy.get().getMoneyAvailable(), 0.5);
		assertThat(resultTransaction).size().isEqualTo(0);
	}

	@Test
	public void givenMakingATransactionWithNotEnoughMoneyAvailable_whenMakeTransaction_thenItDoesNotTheTransactionAndDoesNotUpdateThePayMyBuddyUser() {
		// ARRANGE
		User userSender = new User("emailTransaction", "lastNameTransaction", "firstNameTransaction",
				"passwordNotEncrypted", 20.0, null, null, null);
		User userReceiver = new User("emailTransaction2", "lastNameTransaction2", "firstNameTransaction2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		User userPayMyBuddy = new User("paymybuddy@paymybuddy.com", "buddy", "paymy", "passwordNotEncrypted", 0.0, null,
				null, null);
		Double amountOfTheTransaction = 100.0;
		userSender.setId(1);
		userReceiver.setId(2);
		userPayMyBuddy.setId(3);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);
		testEntityManager.persist(userPayMyBuddy);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUserSender = userService.getUser(userSender.getEmail());
		Optional<User> resultUserReceiver = userService.getUser(userReceiver.getEmail());
		Optional<User> resultUserPayMyBuddy = userService.getUser(userPayMyBuddy.getEmail());
		Iterable<Transaction> resultTransaction = transactionService
				.findAllTransactionByUserEmail(userSender.getEmail());

		// ASSERT
		assertThrows(IllegalArgumentException.class, () -> payMyBuddyService.createTransaction(userSender, userReceiver,
				"description", amountOfTheTransaction));
		assertTrue(resultUserSender.isPresent());
		assertTrue(resultUserReceiver.isPresent());
		assertTrue(resultUserPayMyBuddy.isPresent());
		assertEquals(resultUserSender.get().getMoneyAvailable(), 20);
		assertEquals(resultUserReceiver.get().getMoneyAvailable(), 0);
		assertEquals(resultUserPayMyBuddy.get().getMoneyAvailable(), 0);
		assertThat(resultTransaction).size().isEqualTo(0);
	}

	@Test
	public void givenAddingABuddy_whenAddBuddy_thenItAddTheBuddyToTheUser() {
		// ARRANGE
		Buddy buddy = new Buddy("emailAddBuddy2", "firstNameBuddy", "lastNameBuddy", "description");
		List<Buddy> buddyList = new ArrayList<Buddy>();
		buddyList.add(buddy);
		User user = new User("emailAddBuddy", "lastNameAddBuddy", "firstNameAddBuddy", "passwordNotEncrypted", 20.0,
				null, null, new ArrayList<Buddy>());
		User userBuddy = new User("emailAddBuddy2", "lastNameAddBuddy2", "firstNameAddBuddy2", "passwordNotEncrypted2",
				0.0, null, null, null);
		user.setId(1);
		userBuddy.setId(2);
		testEntityManager.persist(user);
		testEntityManager.persist(userBuddy);
		testEntityManager.persist(buddy);

		// ACT
		payMyBuddyService.addBuddy(user, userBuddy, "description");
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<Buddy> resultBuddy = buddyService.getBuddy(userBuddy.getEmail());
		Iterable<Buddy> resultBuddys = buddyService.findAllBuddy();

		// ASSERT
		assertTrue(resultUser.isPresent());
		assertTrue(resultBuddy.isPresent());
		assertTrue(resultUser.get().getBuddy().get(0).getEmailBuddy().contains(buddy.getEmailBuddy()));
		assertThat(resultBuddys).size().isEqualTo(1);
	}

	@Test
	public void givenAddingABuddyWithAWrongProvidedUser_whenAddBuddy_thenItDoesNotAddTheBuddyToTheUser() {
		// ARRANGE
		User user = new User("emailAddBuddy", "lastNameAddBuddy", "firstNameAddBuddy", "passwordNotEncrypted", 20.0,
				null, null, new ArrayList<Buddy>());
		User userBuddy = new User("emailAddBuddy2", "lastNameAddBuddy2", "firstNameAddBuddy2", "passwordNotEncrypted2",
				0.0, null, null, null);
		userBuddy.setId(2);
		testEntityManager.persist(userBuddy);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<Buddy> resultBuddy = buddyService.getBuddy(userBuddy.getEmail());
		Iterable<Buddy> resultBuddys = buddyService.findAllBuddy();

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService.addBuddy(user, userBuddy, "description"));
		assertFalse(resultUser.isPresent());
		assertFalse(resultBuddy.isPresent());
		assertThat(resultBuddys).size().isEqualTo(0);
	}

	@Test
	public void givenAddingABuddyWithAWrongProvidedUserBuddy_whenAddBuddy_thenItDoesNotAddTheBuddyToTheUser() {
		// ARRANGE
		User user = new User("emailAddBuddy", "lastNameAddBuddy", "firstNameAddBuddy", "passwordNotEncrypted", 20.0,
				null, null, new ArrayList<Buddy>());
		User buddy = new User("emailAddBuddy2", "lastNameAddBuddy2", "firstNameAddBuddy2", "passwordNotEncrypted2", 0.0,
				null, null, null);
		user.setId(1);
		testEntityManager.persist(user);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<Buddy> resultBuddy = buddyService.getBuddy(buddy.getEmail());
		Iterable<Buddy> resultBuddys = buddyService.findAllBuddy();

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService.addBuddy(user, buddy, "description"));
		assertTrue(resultUser.isPresent());
		assertFalse(resultBuddy.isPresent());
		assertThat(resultBuddys).size().isEqualTo(0);

	}

	@Test
	public void givenUpdatingABuddy_whenUpdateBuddy_thenItUpdateTheBuddy() {
		Buddy buddy = new Buddy("emailBuddyDeleteBuddy", "firstNameBuddyDeleteBuddy", "lastNameBuddyDeleteBuddy",
				"description");
		List<Buddy> buddyList = new ArrayList<Buddy>();
		buddyList.add(buddy);
		User user = new User("emailDeleteBuddy", "lastNameDeleteBuddy", "firstNameDeleteBuddy", "passwordNotEncrypted",
				20.0, null, null, buddyList);
		testEntityManager.persist(user);
		testEntityManager.persist(buddy);

		// ACT
		payMyBuddyService.updateBuddy(user, buddy, "descriptionUpdated");
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<Buddy> resultBuddy = buddyService.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertTrue(resultUser.isPresent());
		assertTrue(resultBuddy.isPresent());
		assertEquals(resultUser.get().getBuddy().get(0).getDescription(), resultBuddy.get().getDescription());
	}

	@Test
	public void givenUpdatingABuddyWithAWrongProvidedUser_whenUpdateBuddy_thenItDoesNotUpdateTheBuddy() {
		// ARRANGE
		User user = new User("emailUpdateBuddy", "lastNameUpdateBuddy", "firstNameUpdateBuddy", "passwordNotEncrypted",
				20.0, null, null, new ArrayList<Buddy>());
		Buddy buddy = new Buddy("emailBuddyUpdateBuddy", "firstNameBuddyUpdateBuddy", "lastNameBuddyUpdateBuddy",
				"description");

		testEntityManager.persist(buddy);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<Buddy> resultBuddy = buddyService.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService.updateBuddy(user, buddy, "description"));
		assertFalse(resultUser.isPresent());
		assertTrue(resultBuddy.isPresent());
		assertNotEquals(user.getBuddy(), resultBuddy.get());

	}

	@Test
	public void givenUpdatingABuddyWithAWrongProvidedBuddy_whenUpdateBuddy_thenItDoesNotUpdateTheBuddy() {
		// ARRANGE
		User user = new User("emailUpdateBuddy", "lastNameUpdateBuddy", "firstNameUpdateBuddy", "passwordNotEncrypted",
				20.0, null, null, new ArrayList<Buddy>());
		Buddy buddy = new Buddy("emailBuddyUpdateBuddy", "firstNameBuddyUpdateBuddy", "lastNameBuddyUpdateBuddy",
				"description");
		testEntityManager.persist(user);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<Buddy> resultBuddy = buddyService.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService.updateBuddy(user, buddy, "description"));
		assertTrue(resultUser.isPresent());
		assertFalse(resultBuddy.isPresent());
	}

	@Test
	public void givenDeletingABuddy_whenDeleteBuddy_thenItDeleteTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailBuddyDeleteBuddy", "firstNameBuddyDeleteBuddy", "lastNameBuddyDeleteBuddy",
				"description");
		List<Buddy> buddyList = new ArrayList<Buddy>();
		buddyList.add(buddy);
		User user = new User("emailDeleteBuddy", "lastNameDeleteBuddy", "firstNameDeleteBuddy", "passwordNotEncrypted",
				20.0, null, null, buddyList);
		testEntityManager.persist(user);
		testEntityManager.persist(buddy);

		// ACT
		payMyBuddyService.deleteBuddy(user, buddy);
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<Buddy> resultBuddy = buddyService.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertTrue(resultUser.isPresent());
		assertFalse(resultBuddy.isPresent());
		assertTrue(resultUser.get().getBuddy().isEmpty());
	}

	@Test
	public void givenDeletingABuddyWithAWrongProvidedUser_whenDeleteBuddy_thenItDoesNotDeleteTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailBuddyDeleteBuddy", "firstNameBuddyDeleteBuddy", "lastNameBuddyDeleteBuddy",
				"description");
		List<Buddy> buddyList = new ArrayList<Buddy>();
		buddyList.add(buddy);
		User user = new User("emailDeleteBuddy", "lastNameDeleteBuddy", "firstNameDeleteBuddy", "passwordNotEncrypted",
				20.0, null, null, buddyList);
		testEntityManager.persist(buddy);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<Buddy> resultBuddy = buddyService.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService.deleteBuddy(user, buddy));
		assertFalse(resultUser.isPresent());
		assertTrue(resultBuddy.isPresent());
	}

	@Test
	public void givenDeletingABuddyWithAWrongProvidedBuddy_whenDeleteBuddy_thenItDoesNotDeleteTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailBuddyDeleteBuddy", "firstNameBuddyDeleteBuddy", "lastNameBuddyDeleteBuddy",
				"description");
		List<Buddy> buddyList = new ArrayList<Buddy>();
		buddyList.add(buddy);
		User user = new User("emailDeleteBuddy", "lastNameDeleteBuddy", "firstNameDeleteBuddy", "passwordNotEncrypted",
				20.0, null, null, buddyList);
		testEntityManager.persist(user);
		testEntityManager.persist(buddy);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<Buddy> resultBuddy = buddyService.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertThrows(NoSuchElementException.class,
				() -> payMyBuddyService.deleteBuddy(user, new Buddy("", "", "", "")));
		assertTrue(resultUser.isPresent());
		assertTrue(resultBuddy.isPresent());
		assertEquals(resultUser.get().getBuddy().get(0).getDescription(), resultBuddy.get().getDescription());
	}

	@Test
	public void givenTransferingMoney_whenAddMoneyOnThePayMyBuddyAccountFromBankAccount_thenItTransfertTheMoney() {
		// ARRANGE
		User user = new User("emailAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"lastNameAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"firstNameAddMoneyOnThePayMyBuddyAccountFromBankAccount", "passwordNotEncrypted", 20.0, null, null,
				null);
		BankAccount bankAccount = new BankAccount("IBANAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"descriptionAddMoneyOnThePayMyBuddyAccountFromBankAccount");
		Double amountTransfered = 10.0;
		user.setBankAccount(bankAccount);
		testEntityManager.persist(user);
		testEntityManager.persist(bankAccount);

		// ACT
		payMyBuddyService.addMoneyOnThePayMyBuddyAccountFromBankAccount(user, bankAccount, amountTransfered);
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<BankAccount> resultBankAccount = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertTrue(resultUser.isPresent());
		assertTrue(resultBankAccount.isPresent());
		assertEquals(resultUser.get().getMoneyAvailable(), 30);
	}

	@Test
	public void givenTransferingMoneyWithAWrongProvidedUser_whenAddMoneyOnThePayMyBuddyAccountFromBankAccount_thenItDoesNotTransfertTheMoney() {
		// ARRANGE
		User user = new User("emailAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"lastNameAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"firstNameAddMoneyOnThePayMyBuddyAccountFromBankAccount", "passwordNotEncrypted", 20.0, null, null,
				null);
		BankAccount bankAccount = new BankAccount("IBANAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"descriptionAddMoneyOnThePayMyBuddyAccountFromBankAccount");
		Double amountTransfered = 10.0;
		user.setBankAccount(bankAccount);
		testEntityManager.persist(bankAccount);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<BankAccount> resultBankAccount = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService
				.addMoneyOnThePayMyBuddyAccountFromBankAccount(user, bankAccount, amountTransfered));
		assertFalse(resultUser.isPresent());
		assertTrue(resultBankAccount.isPresent());
		assertEquals(user.getMoneyAvailable(), 20);

	}

	@Test
	public void givenTransferingMoneyWithAWrongProvidedBankAccount_whenAddMoneyOnThePayMyBuddyAccountFromBankAccount_thenItDoesNotTransfertTheMoney() {
		// ARRANGE
		User user = new User("emailAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"lastNameAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"firstNameAddMoneyOnThePayMyBuddyAccountFromBankAccount", "passwordNotEncrypted", 20.0, null, null,
				null);
		BankAccount bankAccount = new BankAccount("IBANAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"descriptionAddMoneyOnThePayMyBuddyAccountFromBankAccount");
		Double amountTransfered = 10.0;
		testEntityManager.persist(user);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<BankAccount> resultBankAccount = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService
				.addMoneyOnThePayMyBuddyAccountFromBankAccount(user, bankAccount, amountTransfered));
		assertTrue(resultUser.isPresent());
		assertFalse(resultBankAccount.isPresent());
		assertEquals(resultUser.get().getMoneyAvailable(), 20);

	}

	@Test
	public void givenTransferingMoneyWithANotAssociatedBankAccount_whenAddMoneyOnThePayMyBuddyAccountFromBankAccount_thenItDoesNotTransfertTheMoney() {
		// ARRANGE
		BankAccount bankAccountUser = new BankAccount("Void", "Void");
		User user = new User("emailAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"lastNameAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"firstNameAddMoneyOnThePayMyBuddyAccountFromBankAccount", "passwordNotEncrypted", 20.0, bankAccountUser,
				null, null);
		BankAccount bankAccount = new BankAccount("IBANAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"descriptionAddMoneyOnThePayMyBuddyAccountFromBankAccount");
		Double amountTransfered = 10.0;
		bankAccount.setId(1);
		bankAccountUser.setId(2);
		testEntityManager.persist(user);
		testEntityManager.persist(bankAccount);
		testEntityManager.persist(bankAccountUser);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<BankAccount> resultBankAccount = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService
				.addMoneyOnThePayMyBuddyAccountFromBankAccount(user, bankAccount, amountTransfered));
		assertTrue(resultUser.isPresent());
		assertTrue(resultBankAccount.isPresent());
		assertNotEquals(resultBankAccount.get(), user.getBankAccount());
		assertEquals(resultUser.get().getMoneyAvailable(), 20);

	}

}