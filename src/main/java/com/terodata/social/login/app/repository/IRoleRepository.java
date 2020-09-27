package com.terodata.social.login.app.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.terodata.social.login.app.entity.Role;

@Repository
public interface IRoleRepository extends CrudRepository<Role, Integer>{

	Optional<Role> findByName(String name);

}
