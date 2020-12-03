package com.paymybuddy.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.Buddy;
import com.paymybuddy.repository.BuddyRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BuddyIT {

	@Autowired
	private BuddyRepository buddyRepository;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(buddyRepository).isNotNull();
	}

	@Test
	public void givenGettingABuddy_whenFindById_thenItReturnTheRightBuddy() {
		// ACT
		Optional<Buddy> result = buddyRepository.findByEmailBuddy("emailTest2");

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals("emailTest2", result.get().getEmailBuddy());
		assertEquals("firstNameTest", result.get().getFirstName());
	}

	@Test
	public void givenGettingBuddys_whenFindAll_thenItReturnAllBuddys() {
		// ACT
		Iterable<Buddy> result = buddyRepository.findAll();

		// ASSERT
		assertThat(result).hasSizeGreaterThan(0);
	}

	@Test
	public void givenSavingABuddy_whenSave_thenItSaveTheBuddy() {
		// ARRANGE
		Buddy user = new Buddy("emailBuddy", "firstName", "lastName", "description");

		// ACT
		buddyRepository.save(user);
		Optional<Buddy> result = buddyRepository.findByEmailBuddy(user.getEmailBuddy());

		// ASSERT
		assertEquals(user.getEmailBuddy(), result.get().getEmailBuddy());
	}

	@Test
	public void givenUpdatingABuddy_whenFindSetSave_thenItUpdateTheBuddy() {
		// ACT
		Optional<Buddy> userToUpdate = buddyRepository.findByEmailBuddy("emailTest2");
		userToUpdate.get().setFirstName("firstNameUpdated");
		buddyRepository.save(userToUpdate.get());
		Optional<Buddy> result = buddyRepository.findByEmailBuddy(userToUpdate.get().getEmailBuddy());

		// ASSERT
		assertEquals(userToUpdate.get().getEmailBuddy(), result.get().getEmailBuddy());
		assertEquals(userToUpdate.get().getFirstName(), result.get().getFirstName());
	}

	@Test
	public void givenDeletingABuddy_whenDelete_thenItDeleteTheBuddy() {
		// ACT
		buddyRepository.deleteById("emailTest2");
		Optional<Buddy> result = buddyRepository.findByEmailBuddy("emailTest2");

		// ASSERT
		assertThat(result).isEmpty();

	}

	@Test
	public void givenGettingAWrongBuddy_whenFindById_thenItThrowsAnException() {
		// ACT
		Optional<Buddy> result = buddyRepository.findByEmailBuddy("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getEmailBuddy());
	}

}