package com.terodata.social.login.app.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.terodata.social.login.app.entity.User;

@Repository
public interface IUserRepository extends CrudRepository<User, Integer> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}
