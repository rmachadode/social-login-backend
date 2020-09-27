package com.terodata.social.login.app.service;

import java.util.Optional;

import com.terodata.social.login.app.entity.Role;

public interface IRoleService {

	Optional<Role> findByName(String name);
	
	Role save(Role role);
}
