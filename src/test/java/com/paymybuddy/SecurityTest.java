package com.paymybuddy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityTest {

	@Test
	public void BCryptPasswordEncoder() {
		// ARRANGE
		String password = "myPassword";
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16); // Create an encoder with strength 16
		// ACT
		String result = encoder.encode(password);
		// ASSERT
		assertTrue(encoder.matches("myPassword", result));
		assertFalse(encoder.matches("Password", result));
	}
}
