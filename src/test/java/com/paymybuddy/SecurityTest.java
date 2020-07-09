package com.paymybuddy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityTest {

	@Test
	public void BCryptPasswordEncoder() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16); // Create an encoder with strength 16
		String result = encoder.encode("myPassword");
		assertTrue(encoder.matches("myPassword", result));
		assertFalse(encoder.matches("Password", result));
		// Do a getPassword(result) from DB, and assert it. (string)
	}
}
