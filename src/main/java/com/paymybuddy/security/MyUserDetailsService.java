package com.paymybuddy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

/**
 * Service used to define the strategy used by the
 * {@link DaoAuthenticationProvider}. <br>
 * It implements the {@link UserDetailsService} interface and override the only
 * method it contain.
 */

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email).get();
		if (user == null) {
			throw new UsernameNotFoundException("The user couldn't be found: " + user);
		}

		return new UserPrincipal(user);
	}

}
