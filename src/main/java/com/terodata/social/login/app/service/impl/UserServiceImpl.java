package com.terodata.social.login.app.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terodata.social.login.app.entity.User;
import com.terodata.social.login.app.repository.IUserRepository;
import com.terodata.social.login.app.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserRepository userRepo;

	@Override
	public Optional<User> findByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	@Override
	public boolean existsEmail(String email) {
		return userRepo.existsByEmail(email);
	}

	@Override
	public User save(User user) {
		return userRepo.save(user);
	}

}
