package com.paymybuddy.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.paymybuddy.model.Buddy;

/**
 * Interface used to define <b>CRUD</b> operations with the buddy table. <br>
 * It extends the {@link CrudRepository} interface delivered by Spring Data JPA.
 */

@RepositoryRestResource(collectionResourceRel = "buddy", path = "buddy")
public interface BuddyRepository extends CrudRepository<Buddy, String> {

	Optional<Buddy> findByEmailBuddy(String emailBuddy);
}
