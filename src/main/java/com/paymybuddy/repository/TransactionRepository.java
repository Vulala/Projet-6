package com.paymybuddy.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.paymybuddy.model.Transaction;

/**
 * Interface used to define <b>CRUD</b> operations with the transaction table.
 * <br>
 * It extends the {@link CrudRepository} interface delivered by Spring Data JPA.
 */

@RepositoryRestResource(collectionResourceRel = "transaction", path = "transaction")
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

	Iterable<Transaction> findAllByUserEmail(String userEmail);
}
