package com.system.cfmanage.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.system.cfmanage.wrapper.UserWrapper;

import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

	ResponseEntity<String> signUp(Map<String, String> requestMap);

	ResponseEntity<String> login(Map<String, String> requestmap, HttpServletResponse response);

	ResponseEntity<List<UserWrapper>> getAllUser();

	ResponseEntity<String> updateUser(int id, Map<String, String> requestMap);
	
	ResponseEntity<String> update(Map<String, String> requestMap);

	ResponseEntity<String> checkToken();

	ResponseEntity<String> changePassword(Map<String, String> requestmap);

	ResponseEntity<String> getForgotPasswordOTP(Map<String, String> requestmap);

}
