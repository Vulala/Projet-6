package com.paymybuddy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.paymybuddy.model.Buddy;
import com.paymybuddy.repository.BuddyRepository;
import com.paymybuddy.service.impl.BuddyServiceImpl;

public class BuddyServiceTest {

	@InjectMocks
	private BuddyServiceImpl buddyServiceImpl;

	@Mock
	private BuddyRepository buddyRepository;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(buddyServiceImpl).isNotNull();
	}

	@Test
	public void givenGettingABuddy_whenGetBuddy_thenItReturnTheRightBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailFindByEmail", "firstNameFindByEmail", "lastNameFindByEmail",
				"descriptionFindByEmail");
		Optional<Buddy> buddyOptional = Optional.of(buddy);
		when(buddyRepository.findByEmailBuddy(buddy.getEmailBuddy())).thenReturn(buddyOptional);

		// ACT
		Optional<Buddy> result = buddyServiceImpl.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertEquals(buddy.getEmailBuddy(), result.get().getEmailBuddy());
		assertEquals(buddy.getFirstName(), result.get().getFirstName());
		verify(buddyRepository, times(1)).findByEmailBuddy(buddy.getEmailBuddy());
	}

	@Test
	public void givenGettingAllBuddys_whenFindAllBuddy_thenItReturnAllBuddys() {
		// ARRANGE
		Buddy buddy = new Buddy("emailFindAll", "firstNameFindAll", "lastNameFindAll", "descriptionFindAll");
		Buddy buddy2 = new Buddy("emailFindAll2", "firstNameFindAll2", "lastNameFindAll2", "descriptionFindAll2");
		List<Buddy> buddyIterable = new ArrayList<Buddy>();
		buddyIterable.add(buddy);
		buddyIterable.add(buddy2);
		when(buddyRepository.findAll()).thenReturn(buddyIterable);

		// ACT
		Iterable<Buddy> result = buddyServiceImpl.findAllBuddy();

		// ASSERT
		assertThat(result).size().isGreaterThan(1);
		verify(buddyRepository, times(1)).findAll();
	}

	@Test
	public void givenCreatingABuddy_whenCreateBuddy_thenItSaveTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailSave", "firstNameSave", "lastNameSave", "descriptionSave");
		Optional<Buddy> buddyOptional = Optional.of(buddy);
		when(buddyRepository.save(buddy)).thenReturn(buddy);
		when(buddyRepository.findByEmailBuddy(buddy.getEmailBuddy())).thenReturn(buddyOptional);

		// ACT
		buddyServiceImpl.createBuddy(buddy);
		Optional<Buddy> result = buddyServiceImpl.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertEquals(result.isPresent(), true);
		assertEquals(buddy.getEmailBuddy(), result.get().getEmailBuddy());
		assertEquals(buddy.getLastName(), result.get().getLastName());
		verify(buddyRepository, times(1)).findByEmailBuddy(buddy.getEmailBuddy());
		verify(buddyRepository, times(1)).save(buddy);
	}

	@Test
	public void givenUpdatingABuddy_whenUpdateBuddy_thenItUpdateTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailUpdate", "firstNameUpdate", "lastNameUpdate", "descriptionUpdate");
		Optional<Buddy> buddyOptional = Optional.of(buddy);
		when(buddyRepository.save(buddy)).thenReturn(buddy);
		when(buddyRepository.findByEmailBuddy(buddy.getEmailBuddy())).thenReturn(buddyOptional);

		// ACT
		Optional<Buddy> buddyToUpdate = buddyServiceImpl.getBuddy(buddy.getEmailBuddy());
		buddyToUpdate.get().setFirstName("firstNameUpdated");
		buddyToUpdate.get().setLastName("lastNameUpdated");
		buddyToUpdate.get().setDescription("descriptionUpdated");
		buddyServiceImpl.updateBuddy(buddyToUpdate.get());
		Optional<Buddy> result = buddyServiceImpl.getBuddy(buddy.getEmailBuddy());

		// ASSERT
		assertEquals(buddyToUpdate.get().getFirstName(), result.get().getFirstName());
		verify(buddyRepository, times(2)).findByEmailBuddy(buddy.getEmailBuddy());
		verify(buddyRepository, times(1)).save(buddy);
	}

	@Test
	public void givenDeletingABuddy_whenDeleteBuddy_thenItDeleteTheBuddy() {
		// ARRANGE
		Buddy buddy = new Buddy("emailDelete", "firstNameDelete", "lastNameDelete", "descriptionDelete");

		// ACT
		buddyServiceImpl.deleteBuddy(buddy);

		// ASSERT
		verify(buddyRepository, times(1)).delete(buddy);
	}

	@Test
	public void givenGettingAWrongBuddy_whenGetBuddy_thenItThrowsAException() {
		// ACT
		Optional<Buddy> result = buddyServiceImpl.getBuddy("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getEmailBuddy());
		verify(buddyRepository, times(1)).findByEmailBuddy("Void");
	}

}