package com.paymybuddy.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;
import com.paymybuddy.service.BankAccountService;

/**
 * Service which implement the {@link BankAccountService} interface. <br>
 * It override the methods and define the business logic. <br>
 * It make use of the {@link BankAccountRepository} interface.
 */
@Service
public class BankAccountServiceImpl implements BankAccountService {

	private final BankAccountRepository bankAccountRepository;

	@Autowired
	public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
		this.bankAccountRepository = bankAccountRepository;
	}

	@Override
	public Optional<BankAccount> getBankAccount(String iban) {
		return bankAccountRepository.findByIBAN(iban);
	}

	@Override
	public Iterable<BankAccount> findAllBankAccount() {
		return bankAccountRepository.findAll();
	}

	@Override
	public BankAccount saveBankAccount(BankAccount bankAccount) {
		return bankAccountRepository.save(bankAccount);
	}

	@Override
	public BankAccount updateBankAccount(BankAccount bankAccount) {
		bankAccount.setId(bankAccount.getId());
		return bankAccountRepository.save(bankAccount);
	}

	@Override
	public void deleteBankAccount(BankAccount bankAccount) {
		bankAccountRepository.delete(bankAccount);
	}

}
