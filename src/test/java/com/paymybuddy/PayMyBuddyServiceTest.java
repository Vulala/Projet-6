package com.paymybuddy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.PayMyBuddyServiceImpl;

public class PayMyBuddyServiceTest {

	@InjectMocks
	private PayMyBuddyServiceImpl payMyBuddyServiceImpl;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BankAccountRepository bankAccountRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(payMyBuddyServiceImpl).isNotNull();
	}

	@Test
	public void givenAddingABankAccount_whenAddBankAccount_thenItAddTheBankAccountToTheUserAndSaveItInTheBankAccountTable() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANAddBankAccount", "descriptionAddBankAccount");
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, null, null,
				null);
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(userRepository.save(user)).thenReturn(user);
		when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);

		// ACT
		payMyBuddyServiceImpl.addBankAccount(user, bankAccount.getIBAN(), bankAccount.getDescription());

		// ASSERT
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	public void givenAddingABankAccountWithAWrongProvidedUser_whenAddBankAccount_thenItDoesNotAddTheBankAccountToTheUserAndDoesNotSaveItInTheBankAccountTable() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANAddBankAccountWithWrongUSer",
				"descriptionAddBankAccountWithWrongUSer");
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, null, null,
				null);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(user)).thenReturn(user);
		when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class,
				() -> payMyBuddyServiceImpl.addBankAccount(user, bankAccount.getIBAN(), bankAccount.getDescription()));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(bankAccountRepository, times(0)).save(bankAccount);
		verify(userRepository, times(0)).save(user);
	}

	@Test
	public void givenDeletingABankAccount_whenDeleteBankAccount_thenItDeleteTheBankAccountFromTheUserAccountAndDeleteItInTheBankAccountTable() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANDeleteBankAccount", "descriptionDeleteBankAccount");
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, bankAccount,
				null, null);
		Optional<User> userOptional = Optional.of(user);
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccount);

		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(bankAccountOptional);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(userRepository.save(user)).thenReturn(user);
		doNothing().when(bankAccountRepository).delete(bankAccount);

		// ACT
		payMyBuddyServiceImpl.deleteBankAccount(user, bankAccount.getIBAN());

		// ASSERT
		verify(bankAccountRepository, times(1)).delete(bankAccount);
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccount.getIBAN());
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	public void givenDeletingABankAccountWithAWrongProvidedUser_whenDeleteBankAccount_thenItDoesNotDeleteTheBankAccountFromTheUserAccountAndDoesNotDeleteItInTheBankAccountTable() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANDeleteBankAccountWrongProvidedUser",
				"descriptionDeleteBankAccountWrongProvidedUser");
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, null, null,
				null);
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccount);
		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(bankAccountOptional);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(user)).thenReturn(user);
		doNothing().when(bankAccountRepository).delete(bankAccount);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class,
				() -> payMyBuddyServiceImpl.deleteBankAccount(user, bankAccount.getIBAN()));
		verify(bankAccountRepository, times(0)).delete(bankAccount);
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccount.getIBAN());
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	public void givenDeletingABankAccountWithAWrongProvidedIBAN_whenDeleteBankAccount_thenItDoesNotDeleteTheBankAccountFromTheUserAccountAndDoesNotDeleteItInTheBankAccountTable() {
		// ARRANGE
		BankAccount bankAccount = new BankAccount("IBANDeleteBankAccountWrongProvidedIBAN",
				"descriptionDeleteBankAccountWrongProvidedIBAN");
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, null, null,
				null);
		Optional<User> userOptional = Optional.of(user);
		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(Optional.empty());
		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(userRepository.save(user)).thenReturn(user);
		doNothing().when(bankAccountRepository).delete(bankAccount);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(IllegalArgumentException.class,
				() -> payMyBuddyServiceImpl.deleteBankAccount(user, bankAccount.getIBAN()));
		verify(bankAccountRepository, times(0)).delete(bankAccount);
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccount.getIBAN());
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(0)).save(user);
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

		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccountNotAssociated);
		Optional<User> userOptional = Optional.of(user);

		when(bankAccountRepository.findByIBAN(bankAccountNotAssociated.getIBAN())).thenReturn(bankAccountOptional);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(userRepository.save(user)).thenReturn(user);
		doNothing().when(bankAccountRepository).delete(bankAccountNotAssociated);

		// ACT
		// Method used in the assert, because it throw the exception (that is what it is
		// tested) but then it fail the test.

		// ASSERT
		assertThrows(IllegalArgumentException.class,
				() -> payMyBuddyServiceImpl.deleteBankAccount(user, bankAccountNotAssociated.getIBAN()));
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccountNotAssociated.getIBAN());
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(bankAccountRepository, times(0)).delete(bankAccountNotAssociated);
		verify(userRepository, times(0)).save(user);
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
		Transaction transaction = new Transaction(userSender, userReceiver, Date.valueOf(LocalDate.now()),
				"description", amountOfTheTransaction);

		Optional<User> userSenderOptional = Optional.of(userSender);
		Optional<User> userReceiverOptional = Optional.of(userReceiver);
		Optional<User> userPayMyBuddyOptional = Optional.of(userPayMyBuddy);

		when(userRepository.findByEmail(userSender.getEmail())).thenReturn(userSenderOptional);
		when(userRepository.findByEmail(userReceiver.getEmail())).thenReturn(userReceiverOptional);
		when(userRepository.findByEmail(userPayMyBuddy.getEmail())).thenReturn(userPayMyBuddyOptional);
		when(userRepository.save(userSender)).thenReturn(userSender);
		when(userRepository.save(userReceiver)).thenReturn(userReceiver);
		when(userRepository.save(userPayMyBuddy)).thenReturn(userPayMyBuddy);
		when(transactionRepository.save(transaction)).thenReturn(transaction);

		// ACT
		payMyBuddyServiceImpl.createTransaction(userSender, userReceiver, "description", amountOfTheTransaction);

		// ASSERT
		verify(userRepository, times(1)).findByEmail(userSender.getEmail());
		verify(userRepository, times(1)).findByEmail(userReceiver.getEmail());
		verify(userRepository, times(1)).findByEmail(userPayMyBuddy.getEmail());
		verify(userRepository, times(1)).save(userSender);
		verify(userRepository, times(1)).save(userReceiver);
		verify(userRepository, times(1)).save(userPayMyBuddy);
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
		Transaction transaction = new Transaction(userSender, userReceiver, Date.valueOf(LocalDate.now()),
				"description", amountOfTheTransaction);

		Optional<User> userReceiverOptional = Optional.of(userReceiver);
		Optional<User> userPayMyBuddyOptional = Optional.of(userPayMyBuddy);

		when(userRepository.findByEmail(userSender.getEmail())).thenReturn(Optional.empty());
		when(userRepository.findByEmail(userReceiver.getEmail())).thenReturn(userReceiverOptional);
		when(userRepository.findByEmail(userPayMyBuddy.getEmail())).thenReturn(userPayMyBuddyOptional);
		when(userRepository.save(userSender)).thenReturn(userSender);
		when(userRepository.save(userReceiver)).thenReturn(userReceiver);
		when(userRepository.save(userPayMyBuddy)).thenReturn(userPayMyBuddy);
		when(transactionRepository.save(transaction)).thenReturn(transaction);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl.createTransaction(userSender,
				userReceiver, "description", amountOfTheTransaction));
		verify(userRepository, times(1)).findByEmail(userSender.getEmail());
		verify(userRepository, times(1)).findByEmail(userReceiver.getEmail());
		verify(userRepository, times(0)).findByEmail(userPayMyBuddy.getEmail());
		verify(userRepository, times(0)).save(userSender);
		verify(userRepository, times(0)).save(userReceiver);
		verify(userRepository, times(0)).save(userPayMyBuddy);
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
		Transaction transaction = new Transaction(userSender, userReceiver, Date.valueOf(LocalDate.now()),
				"description", amountOfTheTransaction);

		Optional<User> userSenderOptional = Optional.of(userSender);
		Optional<User> userPayMyBuddyOptional = Optional.of(userPayMyBuddy);

		when(userRepository.findByEmail(userSender.getEmail())).thenReturn(userSenderOptional);
		when(userRepository.findByEmail(userReceiver.getEmail())).thenReturn(Optional.empty());
		when(userRepository.findByEmail(userPayMyBuddy.getEmail())).thenReturn(userPayMyBuddyOptional);
		when(userRepository.save(userSender)).thenReturn(userSender);
		when(userRepository.save(userReceiver)).thenReturn(userReceiver);
		when(userRepository.save(userPayMyBuddy)).thenReturn(userPayMyBuddy);
		when(transactionRepository.save(transaction)).thenReturn(transaction);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl.createTransaction(userSender,
				userReceiver, "description", amountOfTheTransaction));
		verify(userRepository, times(1)).findByEmail(userSender.getEmail());
		verify(userRepository, times(1)).findByEmail(userReceiver.getEmail());
		verify(userRepository, times(0)).findByEmail(userPayMyBuddy.getEmail());
		verify(userRepository, times(0)).save(userSender);
		verify(userRepository, times(0)).save(userReceiver);
		verify(userRepository, times(0)).save(userPayMyBuddy);
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
		Transaction transaction = new Transaction(userSender, userReceiver, Date.valueOf(LocalDate.now()),
				"description", amountOfTheTransaction);

		Optional<User> userSenderOptional = Optional.of(userSender);
		Optional<User> userReceiverOptional = Optional.of(userReceiver);
		Optional<User> userPayMyBuddyOptional = Optional.of(userPayMyBuddy);

		when(userRepository.findByEmail(userSender.getEmail())).thenReturn(userSenderOptional);
		when(userRepository.findByEmail(userReceiver.getEmail())).thenReturn(userReceiverOptional);
		when(userRepository.findByEmail(userPayMyBuddy.getEmail())).thenReturn(userPayMyBuddyOptional);
		when(userRepository.save(userSender)).thenReturn(userSender);
		when(userRepository.save(userReceiver)).thenReturn(userReceiver);
		when(userRepository.save(userPayMyBuddy)).thenReturn(userPayMyBuddy);
		when(transactionRepository.save(transaction)).thenReturn(transaction);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(IllegalArgumentException.class, () -> payMyBuddyServiceImpl.createTransaction(userSender,
				userReceiver, "description", amountOfTheTransaction));
		verify(userRepository, times(1)).findByEmail(userSender.getEmail());
		verify(userRepository, times(1)).findByEmail(userReceiver.getEmail());
		verify(userRepository, times(0)).findByEmail(userPayMyBuddy.getEmail());
		verify(userRepository, times(0)).save(userSender);
		verify(userRepository, times(0)).save(userReceiver);
		verify(userRepository, times(0)).save(userPayMyBuddy);
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

		Optional<User> userPayMyBuddyOptional = Optional.of(userPayMyBuddy);

		when(userRepository.findByEmail(userPayMyBuddy.getEmail())).thenReturn(userPayMyBuddyOptional);
		when(userRepository.save(userPayMyBuddy)).thenReturn(userPayMyBuddy);

		// ACT
		payMyBuddyServiceImpl.makeTransaction(userSender, userReceiver, amountOfTheTransaction);

		// ASSERT
		verify(userRepository, times(1)).findByEmail(userPayMyBuddy.getEmail());
		verify(userRepository, times(1)).save(userPayMyBuddy);
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
		Double amountOfTheTransaction = 20.0;

		Optional<User> userPayMyBuddyOptional = Optional.of(userPayMyBuddy);

		when(userRepository.findByEmail(userPayMyBuddy.getEmail())).thenReturn(userPayMyBuddyOptional);
		when(userRepository.save(userPayMyBuddy)).thenReturn(userPayMyBuddy);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(IllegalArgumentException.class,
				() -> payMyBuddyServiceImpl.makeTransaction(userSender, userReceiver, amountOfTheTransaction));
		verify(userRepository, times(0)).findByEmail(userPayMyBuddy.getEmail());
		verify(userRepository, times(0)).save(userPayMyBuddy);
	}

	@Test
	public void givenAddingAFriend_whenAddFriend_thenItAddTheFriendToTheUserFriendList() {
		// ARRANGE
		List<User> friendList = new ArrayList<User>();
		User user = new User("emailAddFriend", "lastNameAddFriend", "firstNameAddFriend", "passwordNotEncrypted", 20.0,
				null, null, friendList);
		User userFriend = new User("emailAddFriend2", "lastNameAddFriend2", "firstNameAddFriend2",
				"passwordNotEncrypted2", 0.0, null, null, null);
		Optional<User> userOptional = Optional.of(user);
		Optional<User> userFriendOptional = Optional.of(userFriend);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(userRepository.findByEmail(userFriend.getEmail())).thenReturn(userFriendOptional);
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		payMyBuddyServiceImpl.addFriend(user, userFriend);

		// ASSERT
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).findByEmail(userFriend.getEmail());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	public void givenAddingAFriendWithAWrongProvidedUser_whenAddFriend_thenItDoesNotAddTheFriendToTheUserFriendList() {
		// ARRANGE
		User user = new User("emailAddFriend", "lastNameAddFriend", "firstNameAddFriend", "passwordNotEncrypted", 20.0,
				null, null, null);
		User friend = new User("emailAddFriend2", "lastNameAddFriend2", "firstNameAddFriend2", "passwordNotEncrypted2",
				0.0, null, null, null);
		Optional<User> friendOptional = Optional.of(friend);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
		when(userRepository.findByEmail(friend.getEmail())).thenReturn(friendOptional);
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl.addFriend(user, friend));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).findByEmail(friend.getEmail());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	public void givenAddingAFriendWithAWrongProvidedUserFriend_whenAddFriend_thenItDoesNotAddTheFriendToTheUserFriendList() {
		// ARRANGE
		User user = new User("emailAddFriend", "lastNameAddFriend", "firstNameAddFriend", "passwordNotEncrypted", 20.0,
				null, null, null);
		User friend = new User("emailAddFriend2", "lastNameAddFriend2", "firstNameAddFriend2", "passwordNotEncrypted2",
				0.0, null, null, null);

		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(userRepository.findByEmail(friend.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl.addFriend(user, friend));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).findByEmail(friend.getEmail());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	public void givenDeletingAFriend_whenDeleteFriend_thenItDeleteTheFriendFromTheUserFriendList() {
		// ARRANGE
		User friend = new User("emailDeleteFriend2", "lastNameDeleteFriend2", "firstNameDeleteFriend2",
				"passwordNotEncrypted", 0.0, null, null, null);
		List<User> friendList = new ArrayList<User>();
		friendList.add(friend);
		User user = new User("emailDeleteFriend", "lastNameDeleteFriend", "firstNameDeleteFriend",
				"passwordNotEncrypted", 20.0, null, null, friendList);
		Optional<User> userOptional = Optional.of(user);
		Optional<User> friendOptional = Optional.of(friend);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(userRepository.findByEmail(friend.getEmail())).thenReturn(friendOptional);
		when(userRepository.save(user)).thenReturn(user);
		when(userRepository.save(friend)).thenReturn(friend);
		doNothing().when(userRepository).delete(friend);

		// ACT
		payMyBuddyServiceImpl.deleteFriend(user, friend);

		// ASSERT
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).findByEmail(friend.getEmail());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	public void givenDeletingAFriendWithAWrongProvidedUser_whenDeleteFriend_thenItDoesNotDeleteTheFriendFromTheUserFriendList() {
		// ARRANGE
		User user = new User("emailDeleteFriend", "lastNameDeleteFriend", "firstNameDeleteFriend",
				"passwordNotEncrypted", 20.0, null, null, null);
		User friend = new User("emailDeleteFriend2", "lastNameDeleteFriend2", "firstNameDeleteFriend2",
				"passwordNotEncrypted", 0.0, null, null, null);
		Optional<User> friendOptional = Optional.of(friend);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
		when(userRepository.findByEmail(friend.getEmail())).thenReturn(friendOptional);
		when(userRepository.save(user)).thenReturn(user);
		when(userRepository.save(friend)).thenReturn(friend);
		doNothing().when(userRepository).delete(friend);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl.deleteFriend(user, friend));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).findByEmail(friend.getEmail());
		verify(userRepository, times(0)).save(user);
		verify(userRepository, times(0)).delete(friend);
	}

	@Test
	public void givenDeletingAFriendWithAWrongProvidedFriend_whenDeleteFriend_thenItDoesNotDeleteTheFriendFromTheUserFriendList() {
		// ARRANGE
		User user = new User("emailDeleteFriend", "lastNameDeleteFriend", "firstNameDeleteFriend",
				"passwordNotEncrypted", 20.0, null, null, null);
		User friend = new User("emailDeleteFriend2", "lastNameDeleteFriend2", "firstNameDeleteFriend2",
				"passwordNotEncrypted", 0.0, null, null, null);
		Optional<User> userOptional = Optional.of(user);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(userRepository.findByEmail(friend.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(user)).thenReturn(user);
		when(userRepository.save(friend)).thenReturn(friend);
		doNothing().when(userRepository).delete(friend);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl.deleteFriend(user, friend));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).findByEmail(friend.getEmail());
		verify(userRepository, times(0)).save(user);
		verify(userRepository, times(0)).delete(friend);
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
		Optional<User> userOptional = Optional.of(user);
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccount);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(bankAccountOptional);
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		payMyBuddyServiceImpl.addMoneyOnThePayMyBuddyAccountFromBankAccount(user, bankAccount, amountTransfered);

		// ASSERT
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccount.getIBAN());
		verify(userRepository, times(1)).save(user);
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
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccount);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(bankAccountOptional);
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl
				.addMoneyOnThePayMyBuddyAccountFromBankAccount(user, bankAccount, amountTransfered));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccount.getIBAN());
		verify(userRepository, times(0)).save(user);
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
		Optional<User> userOptional = Optional.of(user);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(Optional.empty());
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl
				.addMoneyOnThePayMyBuddyAccountFromBankAccount(user, bankAccount, amountTransfered));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccount.getIBAN());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	public void givenTransferingMoneyWithANotAssociatedBankAccount_whenAddMoneyOnThePayMyBuddyAccountFromBankAccount_thenItDoesNotTransfertTheMoney() {
		// ARRANGE
		BankAccount bankAccountNotAssociated = new BankAccount("Void", "Void");
		BankAccount bankAccountAssociated = new BankAccount("IBANAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"descriptionAddMoneyOnThePayMyBuddyAccountFromBankAccount");
		User user = new User("emailAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"lastNameAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"firstNameAddMoneyOnThePayMyBuddyAccountFromBankAccount", "passwordNotEncrypted", 20.0,
				bankAccountAssociated, null, null);
		Double amountTransfered = 10.0;
		bankAccountAssociated.setId(1);
		bankAccountNotAssociated.setId(2);

		Optional<User> userOptional = Optional.of(user);
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccountNotAssociated);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(bankAccountRepository.findByIBAN(bankAccountNotAssociated.getIBAN())).thenReturn(bankAccountOptional);
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl
				.addMoneyOnThePayMyBuddyAccountFromBankAccount(user, bankAccountNotAssociated, amountTransfered));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccountNotAssociated.getIBAN());
		verify(userRepository, times(0)).save(user);
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
		Optional<User> userOptional = Optional.of(user);
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccount);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(bankAccountOptional);
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		payMyBuddyServiceImpl.transfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount(user, bankAccount, amountTransfered);

		// ASSERT
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccount.getIBAN());
		verify(userRepository, times(1)).save(user);
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
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccount);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(bankAccountOptional);
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl
				.transfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount(user, bankAccount, amountTransfered));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccount.getIBAN());
		verify(userRepository, times(0)).save(user);
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
		Optional<User> userOptional = Optional.of(user);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(bankAccountRepository.findByIBAN(bankAccount.getIBAN())).thenReturn(Optional.empty());
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl
				.transfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount(user, bankAccount, amountTransfered));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccount.getIBAN());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	public void givenTransferingMoneyOnTheBankAccountWithANotAssociatedBankAccount_whenTransfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount_thenItDoesNotTransfertTheMoney() {
		// ARRANGE
		BankAccount bankAccountNotAssociated = new BankAccount("Void", "Void");
		BankAccount bankAccountAssociated = new BankAccount("IBANAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"descriptionAddMoneyOnThePayMyBuddyAccountFromBankAccount");
		User user = new User("emailAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"lastNameAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"firstNameAddMoneyOnThePayMyBuddyAccountFromBankAccount", "passwordNotEncrypted", 20.0,
				bankAccountAssociated, null, null);
		Double amountTransfered = 10.0;
		bankAccountAssociated.setId(1);
		bankAccountNotAssociated.setId(2);

		Optional<User> userOptional = Optional.of(user);
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccountNotAssociated);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(bankAccountRepository.findByIBAN(bankAccountNotAssociated.getIBAN())).thenReturn(bankAccountOptional);
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(NoSuchElementException.class, () -> payMyBuddyServiceImpl
				.transfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount(user, bankAccountNotAssociated, amountTransfered));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccountNotAssociated.getIBAN());
		verify(userRepository, times(0)).save(user);
	}

	@Test
	public void givenTransferingMoneyOnTheBankAccountWithNotEnoughMoney_whenTransfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount_thenItDoesNotTransfertTheMoney() {
		// ARRANGE
		BankAccount bankAccountAssociated = new BankAccount("IBANAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"descriptionAddMoneyOnThePayMyBuddyAccountFromBankAccount");
		User user = new User("emailAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"lastNameAddMoneyOnThePayMyBuddyAccountFromBankAccount",
				"firstNameAddMoneyOnThePayMyBuddyAccountFromBankAccount", "passwordNotEncrypted", 20.0,
				bankAccountAssociated, null, null);
		Double amountTransfered = 100.0;

		Optional<User> userOptional = Optional.of(user);
		Optional<BankAccount> bankAccountOptional = Optional.of(bankAccountAssociated);

		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);
		when(bankAccountRepository.findByIBAN(bankAccountAssociated.getIBAN())).thenReturn(bankAccountOptional);
		when(userRepository.save(user)).thenReturn(user);

		// ACT
		// In the assert, because it throw the exception (that is what it is tested) but
		// then it fail the test.

		// ASSERT
		assertThrows(IllegalArgumentException.class, () -> payMyBuddyServiceImpl
				.transfertMoneyFromThePayMyBuddyAccountToTheUserBankAccount(user, bankAccountAssociated, amountTransfered));
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(bankAccountRepository, times(1)).findByIBAN(bankAccountAssociated.getIBAN());
		verify(userRepository, times(0)).save(user);
	}
}