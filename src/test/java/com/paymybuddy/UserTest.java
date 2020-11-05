package com.paymybuddy;

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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(userRepository).isNotNull();
		assertThat(testEntityManager).isNotNull();
	}

	@Test
	public void givenGettingAnUser_whenFindById_thenItReturnTheRightUser() {
		// ARRANGE
		User user = new User("emailFindById", "lastNameFindById", "firstNameFindById", "passwordNotEncrypted", 0, null,
				null);
		testEntityManager.persist(user);

		// ACT
		Optional<User> result = userRepository.findByEmail(user.getEmail());

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals(user.getEmail(), result.get().getEmail());
		assertEquals(user.getFirstName(), result.get().getFirstName());
	}

	@Test
	public void givenGettingUsers_whenFindAll_thenItReturnAllUsers() {
		// ARRANGE
		User user = new User("emailFindAll", "lastNameFindAll", "firstNameFindAll", "passwordNotEncrypted", 0, null,
				null);
		User user2 = new User("emailFindAll2", "lastNameFindAll2", "firstNameFindAll2", "passwordNotEncrypted2", 02,
				null, null);
		user2.setId(2);
		testEntityManager.persist(user);
		testEntityManager.persist(user2);

		// ACT
		Iterable<User> result = userRepository.findAll();

		// ASSERT
		assertThat(result).size().isBetween(2, 2);
	}

	@Test
	public void givenSavingAnUser_whenSave_thenItSaveTheUser() {
		// ARRANGE
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0, null, null);

		// ACT
		userRepository.save(user);
		Optional<User> result = userRepository.findByEmail(user.getEmail());

		// ASSERT
		assertEquals(result.isPresent(), true);
		assertEquals(user.getEmail(), result.get().getEmail());
		assertEquals(user.getLastName(), result.get().getLastName());
		assertEquals(user.getBankAccount(), result.get().getBankAccount());
		assertEquals(user.getTransaction(), result.get().getTransaction());
	}

	@Test
	public void givenUpdatingAnUser_whenFindSetSave_thenItUpdateTheUser() {
		// ARRANGE
		User user = new User("emailUpdate", "lastNameUpdate", "firstNameUpdate", "passwordNotEncrypted", 0, null, null);
		testEntityManager.persist(user);

		// ACT
		Optional<User> userToUpdate = userRepository.findByEmail(user.getEmail());
		userToUpdate.get().setFirstName("firstNameUpdated");
		userToUpdate.get().setLastName("lastNameUpdated");
		userToUpdate.get().setPassword("passwordUpdated");
		userToUpdate.get().setMoneyAvailable(10);
		userToUpdate.get().setBankAccount(null);
		userToUpdate.get().setTransaction(null);
		userRepository.save(userToUpdate.get());
		Optional<User> result = userRepository.findByEmail(user.getEmail());

		// ASSERT
		assertEquals(userToUpdate.get().getFirstName(), result.get().getFirstName());
		assertEquals(userToUpdate.get().getMoneyAvailable(), result.get().getMoneyAvailable());
	}

	@Test
	public void givenDeletingAnUser_whenDelete_thenItDeleteTheUser() {
		// ARRANGE
		User user = new User("emailDelete", "lastNameDelete", "firstNameDelete", "passwordNotEncrypted", 0, null, null);
		user.setId(1);
		testEntityManager.persist(user);

		// ACT
		userRepository.deleteById(user.getId());
		Optional<User> result = userRepository.findByEmail(user.getEmail());

		// ASSERT
		assertThat(result).isEmpty();

	}

	@Test
	public void givenGettingAWrongUser_whenFindById_thenItThrowsAnException() {
		// ARRANGE
		User user = new User("emailFindById", "lastNameFindById", "firstNameFindById", "passwordNotEncrypted", 0, null,
				null);
		testEntityManager.persist(user);

		// ACT
		Optional<User> result = userRepository.findByEmail("Void");

		// ASSERT
		assertFalse(result.isPresent());
		assertThrows(NoSuchElementException.class, () -> result.get().getEmail());
	}

	@Test
	public void givenSettingANewUser_whenFindById_thenTheUserIsSavedAndThePasswordIsEncrypted() {
		// ARRANGE
		User user = new User("emailSave", "lastNameSave", "firstNameSave", "passwordNotEncrypted", 0, null, null);
		testEntityManager.persist(user);

		// ACT
		Optional<User> result = userRepository.findByEmail(user.getEmail());

		// ASSERT
		assertEquals(user.getPassword(), result.get().getPassword());
	}

}