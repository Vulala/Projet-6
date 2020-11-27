package com.paymybuddy.service;

import java.util.Optional;

import com.paymybuddy.model.User;
import com.paymybuddy.service.impl.UserServiceImpl;

/**
 * Interface used for the business logic, it is implemented by the corresponding
 * {@link UserServiceImpl} class. <br>
 * It is used to interact with the database, defining method related to the user
 * entity. <br>
 * Can then be called/autowired in a controller layer.
 */
public interface UserService {

	Optional<User> getUser(String email);

	Iterable<User> findAllUser();

	User createUser(User user);

	User updateUser(User user);

	void deleteUser(User user);

}
