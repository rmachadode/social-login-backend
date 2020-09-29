package com.terodata.social.login.app.service;

import java.util.List;
import java.util.Optional;

import com.terodata.social.login.app.entity.User;

public interface IUserService {

	Optional<User> findByEmail(String email);

	boolean existsEmail(String email);

	User save(User user);
	
	List<User> getAll();
}
