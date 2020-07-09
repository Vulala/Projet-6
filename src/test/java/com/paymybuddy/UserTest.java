/**package com.paymybuddy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

	@InjectMocks
	private UserRepository userService; // Test the service layer, deleted.

	@Mock
	private UserRepository userRepository;

	@Disabled
	@Test
	public void givenGettingAnUserWithAWrongId_whenFindById_thenItDoesNotShowTheUserFromTheDB() {
		// ARRANGE
		String email = "email";
		Optional<User> optional = Optional.empty();
		when(userRepository.findById(email)).thenReturn(optional);

		// ACT
		Optional<User> user = userService.findById(email);
		// ASSERT
		assertFalse("email", user.isPresent());
		// assertThrows(NoSuchElementException.class, user.get().getFirstName());
	}

	@Disabled
	@Test
	public void givenGettingAnUser_whenFindById_thenItShowTheUserFromTheDB() {
		// ARRANGE
		String email = "test@email.test";
		User user = new User("", "", "firstNameTest", "", 0, null, null);
		Optional<User> optional = Optional.of(user);
		when(userRepository.findById(email)).thenReturn(optional);

		// ACT
		Optional<User> result = userService.findById(email);
		// ASSERT
		assertTrue(result.isPresent());
		assertEquals("firstNameTest", result.get().getFirstName());
	}

}
*/