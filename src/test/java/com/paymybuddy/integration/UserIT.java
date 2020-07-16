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

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserIT {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(userRepository).isNotNull();
	}

	@Test
	public void givenGettingAnUser_whenFindById_thenItReturnTheRightUser() {
		// ACT
		Optional<User> result = userRepository.findById("emailTest");

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals("emailTest", result.get().getEmail());
		assertEquals("firstNameTest", result.get().getFirstName());
	}

	@Test
	public void givenGettingUsers_whenFindAll_thenItReturnAllUsers() {
		// ACT
		Iterable<User> result = userRepository.findAll();

		// ASSERT
		assertThat(result).size().isBetween(1, 1);
	}

	@Test
	public void givenSavingAnUser_whenSave_thenItSaveTheUser() {
		// ARRANGE
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0, null, null);

		// ACT
		userRepository.save(user);
		Optional<User> result = userRepository.findById(user.getEmail());

		// ASSERT
		assertEquals(user.getEmail(), result.get().getEmail());
	}

	@Test
	public void givenUpdatingAnUser_whenFindSetSave_thenItUpdateTheUser() {
		// ACT
		Optional<User> userToUpdate = userRepository.findById("emailTest");
		userToUpdate.get().setFirstName("firstNameUpdated");
		userRepository.save(userToUpdate.get());
		Optional<User> result = userRepository.findById(userToUpdate.get().getEmail());

		// ASSERT
		assertEquals(userToUpdate.get().getEmail(), result.get().getEmail());
		assertEquals(userToUpdate.get().getFirstName(), result.get().getFirstName());
	}

	@Test
	public void givenDeletingAnUser_whenDelete_thenItDeleteTheUser() {
		// ACT
		userRepository.deleteById("emailTest");
		Optional<User> result = userRepository.findById("emailTest");

		// ASSERT
		assertThat(result).isEmpty();

	}

	@Test
	public void givenGettingAWrongUser_whenFindById_thenItThrowsAnException() {
		// ACT
		Optional<User> result = userRepository.findById("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getEmail());
	}

	@Test
	public void givenSettingANewUser_whenFindById_thenTheUserIsSavedAndThePasswordIsEncrypted() {
		// ARRANGE
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0, null, null);

		// ACT
		userRepository.save(user);
		Optional<User> result = userRepository.findById(user.getEmail());

		// ASSERT
		assertEquals(user.getPassword(), result.get().getPassword());
	}

}