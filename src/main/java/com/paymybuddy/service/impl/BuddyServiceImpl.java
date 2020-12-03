package com.paymybuddy.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.model.Buddy;
import com.paymybuddy.repository.BuddyRepository;
import com.paymybuddy.service.BuddyService;

/**
 * Service which implement the {@link BuddyService} interface. <br>
 * It override the methods and define the business logic. <br>
 * It make use of the {@link BuddyRepository} interface.<br>
 * <br>
 * 
 * The class is annotated with {@link Transactional}, rolling back every
 * transactions in case of any Exceptions thrown by the different methods.
 */
@Service
@Transactional(rollbackOn = { Exception.class })
public class BuddyServiceImpl implements BuddyService {

	private final BuddyRepository buddyRepository;

	@Autowired
	public BuddyServiceImpl(BuddyRepository buddyRepository) {
		this.buddyRepository = buddyRepository;
	}

	@Override
	public Optional<Buddy> getBuddy(String email) {
		return buddyRepository.findByEmailBuddy(email);
	}

	@Override
	public Iterable<Buddy> findAllBuddy() {
		return buddyRepository.findAll();
	}

	@Override
	public Buddy createBuddy(Buddy buddy) {
		return buddyRepository.save(buddy);
	}

	@Override
	public Buddy updateBuddy(Buddy buddy) {
		buddy.setEmailBuddy(buddy.getEmailBuddy());
		return buddyRepository.save(buddy);
	}

	@Override
	public void deleteBuddy(Buddy buddy) {
		buddyRepository.delete(buddy);
	}

}
