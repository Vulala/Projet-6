package com.paymybuddy.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.impl.UserServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserServiceIT {

	@InjectMocks
	private UserServiceImpl userServiceImpl;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void injectedComponentsAreRightlySetUp() {
		assertThat(userServiceImpl).isNotNull();
		assertThat(userRepository).isNotNull();
		assertThat(testEntityManager).isNotNull();
	}

	@Test
	public void givenGettingAnUser_whenGetUser_thenItReturnTheRightUser() {
		// ARRANGE
		User user = new User("emailFindByEmail", "lastNameFindByEmail", "firstNameFindByEmail", "passwordNotEncrypted",
				0.0, null, null, null);
		testEntityManager.persist(user);

		// ACT
		Optional<User> result = userServiceImpl.getUser(user.getEmail());

		// ASSERT
		assertTrue(result.isPresent());
		assertEquals(user.getEmail(), result.get().getEmail());
		assertEquals(user.getFirstName(), result.get().getFirstName());
	}

}