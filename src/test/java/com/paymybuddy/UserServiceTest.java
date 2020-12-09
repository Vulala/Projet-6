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

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.UserServiceImpl;

public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userServiceImpl;

	@Mock
	private UserRepository userRepository;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(userServiceImpl).isNotNull();
	}

	@Test
	public void givenGettingAnUser_whenGetUser_thenItReturnTheRightUser() {
		// ARRANGE
		User user = new User("emailFindByEmail", "lastNameFindByEmail", "firstNameFindByEmail", "passwordNotEncrypted",
				0.0, null, null, null);
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);

		// ACT
		Optional<User> result = userServiceImpl.getUser(user.getEmail());

		// ASSERT
		assertEquals(user.getEmail(), result.get().getEmail());
		assertEquals(user.getFirstName(), result.get().getFirstName());
		verify(userRepository, times(1)).findByEmail(user.getEmail());
	}

	@Test
	public void givenGettingUsers_whenFindAllUser_thenItReturnAllUsers() {
		// ARRANGE
		User user = new User("emailFindAll", "lastNameFindAll", "firstNameFindAll", "passwordNotEncrypted", 0.0, null,
				null, null);
		User user2 = new User("emailFindAll2", "lastNameFindAll2", "firstNameFindAll2", "passwordNotEncrypted2", 0.02,
				null, null, null);
		List<User> userIterable = new ArrayList<User>();
		userIterable.add(user);
		userIterable.add(user2);
		when(userRepository.findAll()).thenReturn(userIterable);

		// ACT
		Iterable<User> result = userServiceImpl.findAllUser();

		// ASSERT
		assertThat(result).size().isGreaterThan(1);
		verify(userRepository, times(1)).findAll();
	}

	@Test
	public void givenCreatingAnUser_whenCreateUser_thenItSaveTheUser() {
		// ARRANGE
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, null, null,
				null);
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.save(user)).thenReturn(user);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);

		// ACT
		userServiceImpl.createUser(user);
		Optional<User> result = userServiceImpl.getUser(user.getEmail());

		// ASSERT
		assertEquals(result.isPresent(), true);
		assertEquals(user.getEmail(), result.get().getEmail());
		assertEquals(user.getLastName(), result.get().getLastName());
		assertEquals(user.getBankAccount(), result.get().getBankAccount());
		assertEquals(user.getTransaction(), result.get().getTransaction());
		verify(userRepository, times(1)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	public void givenUpdatingAnUser_whenUpdateUser_thenItUpdateTheUser() {
		// ARRANGE
		User user = new User("emailUpdate", "lastNameUpdate", "firstNameUpdate", "passwordNotEncrypted", 0.0, null,
				null, null);
		Optional<User> userOptional = Optional.of(user);
		when(userRepository.save(user)).thenReturn(user);
		when(userRepository.findByEmail(user.getEmail())).thenReturn(userOptional);

		// ACT
		Optional<User> userToUpdate = userServiceImpl.getUser(user.getEmail());
		userToUpdate.get().setFirstName("firstNameUpdated");
		userToUpdate.get().setLastName("lastNameUpdated");
		userToUpdate.get().setPassword("passwordUpdated");
		userToUpdate.get().setMoneyAvailable(10.0);
		userToUpdate.get().setBankAccount(null);
		userToUpdate.get().setTransaction(null);
		userToUpdate.get().setFriends(null);
		userServiceImpl.updateUser(userToUpdate.get());
		Optional<User> result = userServiceImpl.getUser(user.getEmail());

		// ASSERT
		assertEquals(userToUpdate.get().getFirstName(), result.get().getFirstName());
		assertEquals(userToUpdate.get().getMoneyAvailable(), result.get().getMoneyAvailable());
		verify(userRepository, times(2)).findByEmail(user.getEmail());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	public void givenDeletingAnUser_whenDeleteUser_thenItDeleteTheUser() {
		// ARRANGE
		User user = new User("emailDelete", "lastNameDelete", "firstNameDelete", "passwordNotEncrypted", 0.0, null,
				null, null);

		// ACT
		userServiceImpl.deleteUser(user);

		// ASSERT
		verify(userRepository, times(1)).delete(user);
	}

	@Test
	public void givenGettingAWrongUser_whenGetUser_thenItThrowsAnException() {
		// ACT
		Optional<User> result = userServiceImpl.getUser("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getEmail());
		verify(userRepository, times(1)).findByEmail("Void");
	}

}