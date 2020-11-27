package com.paymybuddy;

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
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.Buddy;
import com.paymybuddy.repository.BuddyRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BuddyTest {

	@Autowired
	private BuddyRepository buddyRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(buddyRepository).isNotNull();
		assertThat(testEntityManager).isNotNull();
	}

	@Test
	public void givenGettingABuddy_whenFindByEmail_thenItReturnTheRightBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailFindByEmail", "firstNameFindByEmail", "lastNameFindByEmail",
				"descriptionFindByEmail");
		testEntityManager.persist(buddy);

		// ACT
		Optional<Buddy> result = buddyRepository.findByEmailBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertEquals(buddy.getEmailBuddy(), result.get().getEmailBuddy());
		assertEquals(buddy.getFirstName(), result.get().getFirstName());
	}

	@Test
	public void givenGettingAllBuddys_whenFindAll_thenItReturnAllBuddys() {
		// ARRANGE
		Buddy buddy = new Buddy("emailFindAll", "firstNameFindAll", "lastNameFindAll", "descriptionFindAll");
		Buddy buddy2 = new Buddy("emailFindAll2", "firstNameFindAll2", "lastNameFindAll2", "descriptionFindAll2");
		testEntityManager.persist(buddy);
		testEntityManager.persist(buddy2);

		// ACT
		Iterable<Buddy> result = buddyRepository.findAll();

		// ASSERT
		assertThat(result).size().isGreaterThan(1);
	}

	@Test
	public void givenSavingABuddy_whenSaveBuddy_thenItSaveTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailSave", "firstNameSave", "lastNameSave", "descriptionSave");
		testEntityManager.persist(buddy);

		// ACT
		buddyRepository.save(buddy);
		Optional<Buddy> result = buddyRepository.findByEmailBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertEquals(result.isPresent(), true);
		assertEquals(buddy.getEmailBuddy(), result.get().getEmailBuddy());
		assertEquals(buddy.getLastName(), result.get().getLastName());
	}

	@Test
	public void givenUpdatingABuddy_whenFindSetSaveBuddy_thenItUpdateTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailUpdate", "firstNameUpdate", "lastNameUpdate", "descriptionUpdate");
		testEntityManager.persist(buddy);

		// ACT
		Optional<Buddy> buddyToUpdate = buddyRepository.findByEmailBuddy(buddy.getEmailBuddy());
		buddyToUpdate.get().setFirstName("firstNameUpdated");
		buddyToUpdate.get().setLastName("lastNameUpdated");
		buddyToUpdate.get().setDescription("descriptionUpdated");
		buddyRepository.save(buddyToUpdate.get());
		Optional<Buddy> result = buddyRepository.findByEmailBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertEquals(buddyToUpdate.get().getFirstName(), result.get().getFirstName());
	}

	@Test
	public void givenDeletingABuddy_whenDeleteBuddy_thenItDeleteTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailDelete", "firstNameDelete", "lastNameDelete", "descriptionDelete");
		testEntityManager.persist(buddy);

		// ACT
		buddyRepository.delete(buddy);
		Optional<Buddy> result = buddyRepository.findByEmailBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertThat(result).isEmpty();
	}

	@Test
	public void givenGettingAWrongBuddy_whenGetBuddy_thenItThrowsAException() {
		// ACT
		Optional<Buddy> result = buddyRepository.findByEmailBuddy("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getEmailBuddy());
	}

}