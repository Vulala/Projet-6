package com.paymybuddy.service;

import java.util.Optional;

import com.paymybuddy.model.Buddy;
import com.paymybuddy.service.impl.BuddyServiceImpl;

/**
 * Interface used for the business logic, it is implemented by the corresponding
 * {@link BuddyServiceImpl} class. <br>
 * It is used to interact with the database, defining method related to the
 * buddy entity. <br>
 * Can then be called/autowired in a controller layer.
 */
public interface BuddyService {

	Optional<Buddy> getBuddy(String email);

	Iterable<Buddy> findAllBuddy();

	Buddy createBuddy(Buddy buddy);

	Buddy updateBuddy(Buddy buddy);

	void deleteBuddy(Buddy buddy);

}
