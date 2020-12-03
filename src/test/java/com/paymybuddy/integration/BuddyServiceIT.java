package com.paymybuddy.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.Buddy;
import com.paymybuddy.service.BuddyService;
import com.paymybuddy.service.impl.BuddyServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(BuddyServiceImpl.class)
public class BuddyServiceIT {

	@Autowired
	private BuddyService buddyService;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(buddyService).isNotNull();
		assertThat(testEntityManager).isNotNull();
	}

	@Test
	public void givenGettingABuddy_whenGetBuddy_thenItReturnTheRightBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailGetBuddy", "firstNameGetBuddy", "lastNameGetBuddy", "descriptionGetBuddy");
		testEntityManager.persist(buddy);

		// ACT
		Optional<Buddy> result = buddyService.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertEquals(buddy.getEmailBuddy(), result.get().getEmailBuddy());
		assertEquals(buddy.getFirstName(), result.get().getFirstName());
	}

	@Test
	public void givenGettingAllBuddys_whenFindAllBuddy_thenItReturnAllBuddys() {
		// ARRANGE
		Buddy buddy = new Buddy("emailFindAllBuddy", "firstNameFindAllBuddy", "lastNameFindAllBuddy",
				"descriptionFindAllBuddy");
		Buddy buddy2 = new Buddy("emailFindAllBuddy2", "firstNameFindAllBuddy2", "lastNameFindAllBuddy2",
				"descriptionFindAllBuddy2");
		testEntityManager.persist(buddy);
		testEntityManager.persist(buddy2);

		// ACT
		Iterable<Buddy> result = buddyService.findAllBuddy();

		// ASSERT
		assertThat(result).size().isGreaterThan(1);
	}

	@Test
	public void givenSavingABuddy_whenCreateBuddy_thenItCreateTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailCreateBuddy", "firstNameCreateBuddy", "lastNameCreateBuddy",
				"descriptionCreateBuddy");

		// ACT
		buddyService.createBuddy(buddy);
		Optional<Buddy> result = buddyService.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertEquals(result.isPresent(), true);
		assertEquals(buddy.getEmailBuddy(), result.get().getEmailBuddy());
		assertEquals(buddy.getLastName(), result.get().getLastName());
	}

	@Test
	public void givenUpdatingABuddy_whenUpdateBuddy_thenItUpdateTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailUpdateBuddy", "firstNameUpdateBuddy", "lastNameUpdateBuddy",
				"descriptionUpdateBuddy");
		testEntityManager.persist(buddy);

		// ACT
		Optional<Buddy> buddyToUpdate = buddyService.getBuddy(buddy.getEmailBuddy());
		buddyToUpdate.get().setFirstName("firstNameUpdated");
		buddyToUpdate.get().setLastName("lastNameUpdated");
		buddyToUpdate.get().setDescription("descriptionUpdated");
		buddyService.updateBuddy(buddyToUpdate.get());
		Optional<Buddy> result = buddyService.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertEquals(buddyToUpdate.get().getFirstName(), result.get().getFirstName());
	}

	@Test
	public void givenDeletingABuddy_whenDeleteBuddy_thenItDeleteTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailDeleteBuddy", "firstNameDeleteBuddy", "lastNameDeleteBuddy",
				"descriptionDeleteBuddy");
		testEntityManager.persist(buddy);

		// ACT
		buddyService.deleteBuddy(buddy);
		Optional<Buddy> result = buddyService.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertThat(result).isEmpty();
	}

	@Test
	public void givenGettingAWrongBuddy_whenGetBuddy_thenItThrowsAException() {
		// ACT
		Optional<Buddy> result = buddyService.getBuddy("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getEmailBuddy());
	}

}