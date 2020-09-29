package com.terodata.social.login.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.terodata.social.login.app.entity.User;
import com.terodata.social.login.app.service.IUserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private IUserService userService;
	
	
	@GetMapping("/all")
	public ResponseEntity<List<User>> getAll(){
		return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
	}
}
