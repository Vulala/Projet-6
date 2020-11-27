package com.paymybuddy.service;

import java.util.Optional;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.service.impl.BankAccountServiceImpl;

/**
 * Interface used for the business logic, it is implemented by the corresponding
 * {@link BankAccountServiceImpl} class. <br>
 * It is used to interact with the database, defining method related to the
 * bank_account entity. <br>
 * Can then be called/autowired in a controller layer.
 */
public interface BankAccountService {

	Optional<BankAccount> getBankAccount(String iban);

	Iterable<BankAccount> findAllBankAccount();

	BankAccount saveBankAccount(BankAccount bankAccount);

	BankAccount updateBankAccount(BankAccount bankAccount);

	void deleteBankAccount(BankAccount bankAccount);

}
