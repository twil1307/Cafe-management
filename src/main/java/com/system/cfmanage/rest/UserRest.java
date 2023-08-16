package com.system.cfmanage.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.system.cfmanage.wrapper.UserWrapper;

import jakarta.servlet.http.HttpServletResponse;

public interface UserRest {
	
	@PostMapping(path = "/user/signup")
	public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestmap);
	
	@PostMapping(path = "/user/login")
	public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestmap, HttpServletResponse response);
	
	@GetMapping(path = "/user")
	public ResponseEntity<List<UserWrapper>> getAllUser();
	
	@PutMapping(path = "/user/{id}")
	public ResponseEntity<String> updateUser(@PathVariable int id, @RequestBody(required = true) Map<String, String> requestmap);
	
	@PostMapping(path = "/user/update")
	public ResponseEntity<String> update(@RequestBody(required = true) Map<String, String> requestmap);
	
	@GetMapping(path = "/hello")
	public ResponseEntity<String> Hello();
	
	@GetMapping(path = "/user/checktoken")
	public ResponseEntity<String> checkToken();
	
	@PostMapping(path = "/user/changePassword")
	public ResponseEntity<String> changePassword(@RequestBody(required = true) Map<String, String> requestmap);

	@PostMapping(path = "/user/forgotPasswordOtp")
	public ResponseEntity<String> getForgotPasswordOTP(@RequestBody(required = true) Map<String, String> requestmap);

}
