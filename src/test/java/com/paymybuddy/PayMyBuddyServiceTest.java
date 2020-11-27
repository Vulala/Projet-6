package com.paymybuddy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.repository.BuddyRepository;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.PayMyBuddyServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PayMyBuddyServiceTest {

	@InjectMocks
	private PayMyBuddyServiceImpl payMyBuddyServiceImpl;

	@Mock
	private UserRepository userRepository;

	@Mock
	private BankAccountRepository bankAccountRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private BuddyRepository buddyRepository;

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
		when(bankAccountRepository.save(bankAccount)).thenReturn(bankAccount);
		when(userRepository.save(user)).thenReturn(user);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);

		// ACT
		payMyBuddyServiceImpl.addBankAccount(user, "iban", "description");

		// ASSERT
		verify(bankAccountRepository, times(1)).save(bankAccount);
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).save(user);
	}

}