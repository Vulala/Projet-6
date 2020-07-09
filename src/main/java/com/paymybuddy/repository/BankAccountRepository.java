package com.paymybuddy.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.paymybuddy.model.BankAccount;

/**
 * Interface used to define <b>CRUD</b> operations with the bank_account table.
 * <br>
 * It extends the {@link CrudRepository} interface delivered by Spring Data JPA.
 */

@RepositoryRestResource(collectionResourceRel = "bankAccount", path = "bankAccount")
public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {

}
