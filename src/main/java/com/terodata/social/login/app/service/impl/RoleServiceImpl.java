package com.terodata.social.login.app.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terodata.social.login.app.entity.Role;
import com.terodata.social.login.app.repository.IRoleRepository;
import com.terodata.social.login.app.service.IRoleService;

@Service
public class RoleServiceImpl implements IRoleService{
	
	@Autowired
	private IRoleRepository roleRepo;

	@Override
	public Optional<Role> findByName(String name) {
		return roleRepo.findByName(name);
	}

	@Override
	public Role save(Role role) {
		return roleRepo.save(role);
	}

}
