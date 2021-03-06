package com.terodata.social.login.app.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.terodata.social.login.app.dto.AuthenticationRequest;
import com.terodata.social.login.app.dto.TokenDto;
import com.terodata.social.login.app.entity.Role;
import com.terodata.social.login.app.entity.User;
import com.terodata.social.login.app.enums.RoleNames;
import com.terodata.social.login.app.security.jwt.JwtProvider;
import com.terodata.social.login.app.service.IRoleService;
import com.terodata.social.login.app.service.IUserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

	@Value("${google.clientId}")
	String googleClientId;

	@Value("${secretPsw}")
	String secretPsw;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtProvider jwtProvider;

	@Autowired
	IUserService userService;

	@Autowired
	IRoleService roleService;

	@PostMapping("/google")
	public ResponseEntity<TokenDto> google(@RequestBody TokenDto tokenDto) throws IOException {
		final NetHttpTransport transport = new NetHttpTransport();
		User user = null;
		try {
			final JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
			// Verifico el token recibido con la api de Google para ver si es valido
			GoogleIdTokenVerifier.Builder verifier = new GoogleIdTokenVerifier.Builder(transport, jacksonFactory)
					.setAudience(Collections.singletonList(googleClientId));
			final GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), tokenDto.getValue());
			final GoogleIdToken.Payload payload = googleIdToken.getPayload();
			// Verifico si existe el usuario, sino se crea
			user = userService.findByEmail(payload.getEmail()).orElse(null);
			if (user == null) {
				user = createUser(payload.getEmail());
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		// Se realiza la authenticacion del usuario
		TokenDto tokenRes = login(user, secretPsw);
		return new ResponseEntity<>(tokenRes, HttpStatus.OK);
	}

	@PostMapping("/facebook")
	public ResponseEntity<TokenDto> facebook(@RequestBody TokenDto tokenDto) throws IOException {
		User user = null;
		try {
			Facebook facebook = new FacebookTemplate(tokenDto.getValue());
			final String[] fields = { "email", "picture" };
			org.springframework.social.facebook.api.User userFace = facebook.fetchObject("me",
					org.springframework.social.facebook.api.User.class, fields);
			// Verifico si existe el usuario, sino se crea
			user = userService.findByEmail(userFace.getEmail()).orElse(null);
			if (user == null) {
				user = createUser(userFace.getEmail());
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		TokenDto tokenRes = login(user, secretPsw);
		return new ResponseEntity<>(tokenRes, HttpStatus.OK);
	}

	private TokenDto login(User user, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), password));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtProvider.generateToken(authentication);
		TokenDto tokenDto = new TokenDto();
		tokenDto.setValue(jwt);
		return tokenDto;
	}

	private User createUser(String email) {
		User user = new User(email, passwordEncoder.encode(secretPsw));
		Role roleUser = roleService.findByName(RoleNames.ROLE_USER.name()).orElse(null);
		Set<Role> roles = new HashSet<>();
		roles.add(roleUser);
		user.setRoles(roles);
		return userService.save(user);
	}
	
	@PostMapping("/login")
	public ResponseEntity<Object> createToken(@RequestBody AuthenticationRequest request){
		User user = null;
		try {
			user = userService.findByEmail(request.getUsername()).orElse(null);
			if(user == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
			TokenDto tokenRes = login(user, request.getPassword());
			return new ResponseEntity<>(tokenRes, HttpStatus.OK);
		} catch (BadCredentialsException e) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> create(@RequestBody User user) {
		User savedUser = null;
		try {
			savedUser = userService.save(user);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
	}

}
