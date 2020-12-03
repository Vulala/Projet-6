package com.paymybuddy.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
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

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import com.paymybuddy.service.impl.UserServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(UserServiceImpl.class)
public class UserServiceIT {

	@Autowired
	private UserService userService;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(userService).isNotNull();
		assertThat(testEntityManager).isNotNull();
	}

	@Test
	public void givenGettingAnUser_whenGetUser_thenItReturnTheRightUser() {
		// ARRANGE
		User user = new User("emailGetUser", "lastNameGetUser", "firstNameGetUser", "passwordNotEncrypted", 0.0, null,
				null, null);
		testEntityManager.persist(user);

		// ACT
		Optional<User> result = userService.getUser(user.getEmail());

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals(user.getEmail(), result.get().getEmail());
		assertEquals(user.getFirstName(), result.get().getFirstName());
	}

	@Test
	public void givenGettingAllUser_whenFindAllUser_thenItReturnAllUser() {
		// ARRANGE
		User user = new User("emailFindAllUser", "lastNameFindAllUser", "firstNameFindAllUser", "passwordNotEncrypted",
				0.0, null, null, null);
		User user2 = new User("emailFindAllUser2", "lastNameFindAllUser2", "firstNameFindAllUser2",
				"passwordNotEncryptedUser2", 0.0, null, null, null);
		user.setId(1);
		testEntityManager.persist(user);
		testEntityManager.persist(user2);

		// ACT
		Iterable<User> result = userService.findAllUser();

		// ASSERT
		assertThat(result).size().isGreaterThan(1);
	}

	@Test
	public void givenCreatingAnUser_whenCreateUser_thenItCreateTheUser() {
		// ARRANGE
		User user = new User("emailCreateUser", "lastNameCreateUser", "firstNameCreateUser", "passwordNotEncrypted",
				0.0, null, null, null);

		// ACT
		userService.createUser(user);
		Optional<User> result = userService.getUser(user.getEmail());

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals(user.getEmail(), result.get().getEmail());
		assertEquals(user.getLastName(), result.get().getLastName());
	}

	@Test
	public void givenUpdatingAnUser_whenUpdateUser_thenItUpdateTheUser() {
		// ARRANGE
		User user = new User("emailUpdateUser", "lastNameUpdateUser", "firstNameUpdateUser", "passwordNotEncrypted",
				0.0, null, null, null);
		testEntityManager.persist(user);

		// ACT
		Optional<User> userToUpdate = userService.getUser(user.getEmail());
		userToUpdate.get().setFirstName("firstNameUpdated");
		userToUpdate.get().setLastName("lastNameUpdated");
		userToUpdate.get().setPassword("passwordUpdated");
		userToUpdate.get().setMoneyAvailable(10.0);
		userToUpdate.get().setBankAccount(null);
		userToUpdate.get().setTransaction(null);
		userService.createUser(userToUpdate.get());
		Optional<User> result = userService.getUser(user.getEmail());

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals(userToUpdate.get().getFirstName(), result.get().getFirstName());
		assertEquals(userToUpdate.get().getMoneyAvailable(), result.get().getMoneyAvailable());
	}

	@Test
	public void givenDeletingAnUser_whenDeleteUser_thenItDeleteTheUser() {
		// ARRANGE
		User user = new User("emailDeleteUser", "lastNameDeleteUser", "firstNameDeleteUser", "passwordNotEncrypted",
				0.0, null, null, null);
		user.setId(1);
		testEntityManager.persist(user);

		// ACT
		userService.deleteUser(user);
		Optional<User> result = userService.getUser(user.getEmail());

		// ASSERT
		assertThat(result).isEmpty();
	}

	@Test
	public void givenGettingAWrongUser_whenGetUser_thenItThrowsAnException() {
		// ARRANGE
		User user = new User("emailGetUser", "lastNameGetUser", "firstNameGetUser", "passwordNotEncrypted", 0.0, null,
				null, null);
		testEntityManager.persist(user);

		// ACT
		Optional<User> result = userService.getUser("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getEmail());
	}

	@Test
	public void givenSettingANewUser_whenSave_thenTheUserIsSavedAndThePasswordIsEncrypted() {
		// ARRANGE
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0.0, null, null,
				null);
		testEntityManager.persist(user);

		// ACT
		user.setPassword("passwordEncrypted");
		userService.updateUser(user);
		Optional<User> result = userService.getUser(user.getEmail());

		// ASSERT
		assertNotEquals("passwordEncrypted", result.get().getPassword());
	}

}