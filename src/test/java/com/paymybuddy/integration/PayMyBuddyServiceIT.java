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
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.PayMyBuddyService;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.service.impl.BankAccountServiceImpl;
import com.paymybuddy.service.impl.PayMyBuddyServiceImpl;
import com.paymybuddy.service.impl.TransactionServiceImpl;
import com.paymybuddy.service.impl.UserServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import({ PayMyBuddyServiceImpl.class, UserServiceImpl.class, BankAccountServiceImpl.class,
		TransactionServiceImpl.class })
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
	private TestEntityManager testEntityManager;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(payMyBuddyService).isNotNull();
		assertThat(userService).isNotNull();
		assertThat(bankAccountService).isNotNull();
		assertThat(transactionService).isNotNull();
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
		Iterable<Transaction> resultTransaction = transactionService.findAllByUserSender(userSender);

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
		Iterable<Transaction> resultTransaction = transactionService.findAllByUserSender(userSender);
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
		Iterable<Transaction> resultTransaction = transactionService.findAllByUserSender(userSender);
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
		Iterable<Transaction> resultTransaction = transactionService.findAllByUserSender(userSender);
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
		Iterable<Transaction> resultTransaction = transactionService.findAllByUserSender(userSender);

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
		Iterable<Transaction> resultTransaction = transactionService.findAllByUserSender(userSender);

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
	public void givenMakingATransactionWithAWrongProvidedPayMyBuddyAccount_whenMakeTransaction_thenItDoesNotTheTransactionAndDoesNotUpdateThePayMyBuddyUser() {
		// ARRANGE
		User userSender = new User("emailTransaction", "lastNameTransaction", "firstNameTransaction",
				"passwordNotEncrypted", 20.0, null, null, null);
		User userReceiver = new User("emailTransaction2", "lastNameTransaction2", "firstNameTransaction2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		User userPayMyBuddy = new User("Void", "buddy", "paymy", "passwordNotEncrypted", 0.0, null, null, null);
		Double amountOfTheTransaction = 10.0;
		userSender.setId(1);
		userReceiver.setId(2);
		userPayMyBuddy.setId(3);
		testEntityManager.persist(userSender);
		testEntityManager.persist(userReceiver);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUserSender = userService.getUser(userSender.getEmail());
		Optional<User> resultUserReceiver = userService.getUser(userReceiver.getEmail());
		Optional<User> resultUserPayMyBuddy = userService.getUser(userPayMyBuddy.getEmail());
		Iterable<Transaction> resultTransaction = transactionService.findAllByUserSender(userSender);

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService.createTransaction(userSender, userReceiver,
				"description", amountOfTheTransaction));
		assertTrue(resultUserSender.isPresent());
		assertTrue(resultUserReceiver.isPresent());
		assertFalse(resultUserPayMyBuddy.isPresent());
		assertEquals(resultUserSender.get().getMoneyAvailable(), 9.5);
		assertEquals(resultUserReceiver.get().getMoneyAvailable(), 10);
		assertThrows(NoSuchElementException.class, () -> resultUserPayMyBuddy.get().getMoneyAvailable());
		assertThat(resultTransaction).size().isEqualTo(0);
	}

	@Test
	public void givenAddingAFriend_whenAddFriend_thenItAddTheFriendToTheUserFriendList() {
		// ARRANGE
		User user = new User("emailAddFriend", "lastNameAddFriend", "firstNameAddFriend", "passwordNotEncrypted", 20.0,
				null, null, new ArrayList<User>());
		User userFriend = new User("emailAddFriend2", "lastNameAddFriend2", "firstNameAddFriend2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		user.setId(1);
		userFriend.setId(2);
		testEntityManager.persist(user);
		testEntityManager.persist(userFriend);

		// ACT
		payMyBuddyService.addFriend(user, userFriend);
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<User> resultFriend = userService.getUser(userFriend.getEmail());

		// ASSERT
		assertTrue(resultUser.isPresent());
		assertTrue(resultFriend.isPresent());
		assertTrue(resultUser.get().getFriends().get(0).getEmail().contains(userFriend.getEmail()));
	}

	@Test
	public void givenAddingAFriendWithAWrongProvidedUser_whenAddFriend_thenItDoesNotAddTheFriendToTheUserFriendList() {
		// ARRANGE
		User user = new User("emailAddFriend", "lastNameAddFriend", "firstNameAddFriend", "passwordNotEncrypted", 20.0,
				null, null, new ArrayList<User>());
		User userFriend = new User("emailAddFriend2", "lastNameAddFriend2", "firstNameAddFriend2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		userFriend.setId(2);
		testEntityManager.persist(userFriend);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<User> resultFriend = userService.getUser(userFriend.getEmail());

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService.addFriend(user, userFriend));
		assertFalse(resultUser.isPresent());
		assertTrue(resultFriend.isPresent());
	}

	@Test
	public void givenAddingAFriendWithAWrongProvidedFriend_whenAddFriend_thenItDoesNotAddTheFriendToTheUserFriendList() {
		// ARRANGE
		User user = new User("emailAddFriend", "lastNameAddFriend", "firstNameAddFriend", "passwordNotEncrypted", 20.0,
				null, null, new ArrayList<User>());
		User friend = new User("emailAddFriend2", "lastNameAddFriend2", "firstNameAddFriend2", "passwordNotEncrypted2",
				0.0, null, null, null);
		user.setId(1);
		testEntityManager.persist(user);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<User> resultFriend = userService.getUser(friend.getEmail());

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService.addFriend(user, friend));
		assertTrue(resultUser.isPresent());
		assertFalse(resultFriend.isPresent());
		assertTrue(resultUser.get().getFriends().isEmpty());

	}

	@Test
	public void givenDeletingAFriend_whenDeleteFriend_thenItDeleteTheFriendFromTheUserFriendList() {
		// ARRANGE
		User friend = new User("emailDeleteFriend2", "lastNameDeleteFriend", "firstNameDeleteFriend",
				"passwordNotEncrypted", 0.0, null, null, null);
		List<User> friendList = new ArrayList<User>();
		friendList.add(friend);
		User user = new User("emailDeleteFriend", "lastNameDeleteFriend", "firstNameDeleteFriend",
				"passwordNotEncrypted", 20.0, null, null, friendList);
		user.setId(1);
		friend.setId(2);
		testEntityManager.persist(user);
		testEntityManager.persist(friend);

		// ACT
		payMyBuddyService.deleteFriend(user, friend);
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<User> resultFriend = userService.getUser(friend.getEmail());

		// ASSERT
		assertTrue(resultUser.isPresent());
		assertTrue(resultFriend.isPresent());
		assertTrue(resultUser.get().getFriends().isEmpty());
	}

	@Test
	public void givenDeletingAFriendWithAWrongProvidedUser_whenDeleteFriend_thenItDoesNotDeleteTheFriend() {
		// ARRANGE
		User friend = new User("emailDeleteFriend2", "lastNameDeleteFriend", "firstNameDeleteFriend",
				"passwordNotEncrypted", 0.0, null, null, null);
		friend.setId(2);
		testEntityManager.persist(friend);

		List<User> friendList = new ArrayList<User>();
		friendList.add(friend);
		User user = new User("emailDeleteFriend", "lastNameDeleteFriend", "firstNameDeleteFriend",
				"passwordNotEncrypted", 20.0, null, null, friendList);
		user.setId(1);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<User> resultFriend = userService.getUser(friend.getEmail());

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyService.deleteFriend(user, friend));
		assertFalse(resultUser.isPresent());
		assertTrue(resultFriend.isPresent());
	}

	@Test
	public void givenDeletingAFriendWithAWrongProvidedFriend_whenDeleteFriend_thenItDoesNotDeleteTheFriend() {
		// ARRANGE
		User friend = new User("emailDeleteFriend2", "lastNameDeleteFriend", "firstNameDeleteFriend",
				"passwordNotEncrypted", 0.0, null, null, null);
		friend.setId(2);
		testEntityManager.persist(friend);

		List<User> friendList = new ArrayList<User>();
		friendList.add(friend);
		User user = new User("emailDeleteFriend", "lastNameDeleteFriend", "firstNameDeleteFriend",
				"passwordNotEncrypted", 20.0, null, null, friendList);

		user.setId(1);
		testEntityManager.persist(user);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<User> resultFriend = userService.getUser(friend.getEmail());

		// ASSERT
		assertThrows(NoSuchElementException.class,
				() -> payMyBuddyService.deleteFriend(user, new User("", "", "", "", null, null, null, null)));
		assertTrue(resultUser.isPresent());
		assertTrue(resultFriend.isPresent());
		assertFalse(resultUser.get().getFriends().isEmpty());
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

	@Test
	public void givenTransferingMoneyOnTheBankAccount_whenTransfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount_thenItTransfertTheMoney() {
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
		payMyBuddyService.transfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount(user, bankAccount,
				amountTransfered);
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<BankAccount> resultBankAccount = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertTrue(resultUser.isPresent());
		assertTrue(resultBankAccount.isPresent());
		assertEquals(resultUser.get().getMoneyAvailable(), 10);
	}

	@Test
	public void givenTransferingMoneyOnTheBankAccountWithAWrongProvidedUser_whenTransfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount_thenItDoesNotTransfertTheMoney() {
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
				.transfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount(user, bankAccount, amountTransfered));
		assertFalse(resultUser.isPresent());
		assertTrue(resultBankAccount.isPresent());
		assertEquals(user.getMoneyAvailable(), 20);

	}

	@Test
	public void givenTransferingMoneyOnTheBankAccountWithAWrongProvidedBankAccount_whenTransfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount_thenItDoesNotTransfertTheMoney() {
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
				.transfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount(user, bankAccount, amountTransfered));
		assertTrue(resultUser.isPresent());
		assertFalse(resultBankAccount.isPresent());
		assertEquals(resultUser.get().getMoneyAvailable(), 20);

	}

	@Test
	public void givenTransferingMoneyOnTheBankAccountWithANotAssociatedBankAccount_whenTransfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount_thenItDoesNotTransfertTheMoney() {
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
				.transfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount(user, bankAccount, amountTransfered));
		assertTrue(resultUser.isPresent());
		assertTrue(resultBankAccount.isPresent());
		assertNotEquals(resultBankAccount.get(), user.getBankAccount());
		assertEquals(resultUser.get().getMoneyAvailable(), 20);

	}

	@Test
	public void givenTransferingMoneyOnTheBankAccountWithNotEnoughMoney_whenTransfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount_thenItDoesNotTransfertTheMoney() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"descriptionAddMoneyOnThePayMyBuddyAccountFromBankAccount");
		User user = new User("emailAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"lastNameAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"firstNameAddMoneyOnThePayMyBuddyAccountFromBankAccount", "passwordNotEncrypted", 20.0, bankAccount,
				null, null);
		Double amountTransfered = 100.0;
		testEntityManager.persist(user);
		testEntityManager.persist(bankAccount);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.
		Optional<User> resultUser = userService.getUser(user.getEmail());
		Optional<BankAccount> resultBankAccount = bankAccountService.getBankAccount(bankAccount.getIBAN());

		// ASSERT
		assertThrows(IllegalArgumentException.class, () -> payMyBuddyService
				.transfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount(user, bankAccount, amountTransfered));
		assertTrue(resultUser.isPresent());
		assertTrue(resultBankAccount.isPresent());
		assertEquals(resultBankAccount.get(), user.getBankAccount());
		assertTrue(resultUser.get().getMoneyAvailable() < amountTransfered);
		assertEquals(resultUser.get().getMoneyAvailable(), 20);

	}
}