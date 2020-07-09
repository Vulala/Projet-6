package com.paymybuddy.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.paymybuddy.model.User;

/**
 * Interface used to define <b>CRUD</b> operations with the user table. <br>
 * It extends the {@link CrudRepository} interface delivered by Spring Data JPA.
 */

@RepositoryRestResource(collectionResourceRel = "user", path = "user")
public interface UserRepository extends CrudRepository<User, String> {

}
