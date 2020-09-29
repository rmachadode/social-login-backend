package com.terodata.social.login.app.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.terodata.social.login.app.entity.Role;
import com.terodata.social.login.app.entity.User;
import com.terodata.social.login.app.enums.RoleNames;
import com.terodata.social.login.app.repository.IRoleRepository;
import com.terodata.social.login.app.repository.IUserRepository;
import com.terodata.social.login.app.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
    private PasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private IUserRepository userRepo;
	
	@Autowired
	private IRoleRepository roleRepo;

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
		 user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		 HashSet<Role> roles = new HashSet<Role>();
		 roles.add(roleRepo.findByName(RoleNames.ROLE_USER.name()).orElse(null));
	     user.setRoles(roles);
		return userRepo.save(user);
	}

	@Override
	public List<User> getAll() {
		return (List<User>) userRepo.findAll();
	}

}
