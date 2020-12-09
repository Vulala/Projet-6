package com.paymybuddy.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;

/**
 * This test is used to show how the application work. <br>
 */

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ApplicationDemonstrationIT {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	private String emailSendingMoney = "emailTest";
	private String emailReceivingMoney = "emailTest2";
	private Double amountTransfered = 10.0;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(userRepository).isNotNull();
		assertThat(transactionRepository).isNotNull();
	}

	@DisplayName("Application demonstration")
	@Test
	public void givenProceedingATransaction_whenTransaction_thenItDoesTheEntireTransactionProcessus() {
		// Check whether or not the mails are present in the DB, then proceed or not the
		// transaction.
		Optional<User> userSendingMoneyOptional = userRepository.findByEmail(emailSendingMoney);
		Optional<User> userReceivingMoneyOptional = userRepository.findByEmail(emailReceivingMoney);

		if (!userSendingMoneyOptional.isPresent()) {
			throw new UsernameNotFoundException("The email provided is unknown from the database." + emailSendingMoney);
		}
		if (!userReceivingMoneyOptional.isPresent()) {
			throw new UsernameNotFoundException(
					"The email provided is unknown from the database." + emailReceivingMoney);
		}

		User userSendingMoney = userSendingMoneyOptional.get();
		Double tax = amountTransfered * 0.05;
		Double moneyAvailableBeforeTheTransactionUserSending = userSendingMoney.getMoneyAvailable();
		if (moneyAvailableBeforeTheTransactionUserSending < amountTransfered + tax) {
			throw new IllegalAccessError("The money available on the account is not enough to afford the request."
					+ " Money : " + moneyAvailableBeforeTheTransactionUserSending + " Tax : " + tax);
		}

		// OK to proceed
		userSendingMoney.setMoneyAvailable(moneyAvailableBeforeTheTransactionUserSending - (amountTransfered + tax));
		User userReceivingMoney = userReceivingMoneyOptional.get();
		userReceivingMoney.setMoneyAvailable(userReceivingMoney.getMoneyAvailable() + amountTransfered);

		User userSendingMoneyUpdated = userRepository.save(userSendingMoney);
		User userGettingMoneyUpdated = userRepository.save(userReceivingMoney);

		Transaction transaction = new Transaction(userSendingMoney, userReceivingMoney, Date.valueOf(LocalDate.now()),
				"Application Demonstration", amountTransfered);

		Transaction resultTransaction = transactionRepository.save(transaction);

		assertNotNull(resultTransaction);
		assertEquals(userSendingMoneyUpdated.getMoneyAvailable(), 19.5);
		assertEquals(userGettingMoneyUpdated.getMoneyAvailable(), 10);
		assertEquals(userSendingMoney, resultTransaction.getUserSender());
		assertEquals(userReceivingMoney, resultTransaction.getUserReceiver());
		assertEquals(amountTransfered, resultTransaction.getAmount());
	}
}
