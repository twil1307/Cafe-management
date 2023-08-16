package com.system.cfmanage.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.system.cfmanage.JWT.CustomerUsersDetailsSevice;
import com.system.cfmanage.JWT.JwtFilter;
import com.system.cfmanage.JWT.JwtUtil;
import com.system.cfmanage.POJO.User;
import com.system.cfmanage.constants.CafeConstants;
import com.system.cfmanage.dao.UserDao;
import com.system.cfmanage.service.UserService;
import com.system.cfmanage.utils.CafeUtils;
import com.system.cfmanage.utils.EmailUtils;
import com.system.cfmanage.wrapper.UserWrapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;

	@Autowired
	private AuthenticationManager authenticationManager;

//	@Autowired
//	private AuthenticationProvider authenticationProvider;

	@Autowired
	private CustomerUsersDetailsSevice customerUsersDetailsSevice;

	@Autowired
	private UserDao dao;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private EmailUtils emailUtils;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		log.info("Inside sign up {}", requestMap);
		try {
			if (validateSignUpMap(requestMap)) {
				User user = userDao.findByEmailId(requestMap.get("email"));

				if (Objects.isNull(user)) {
					User saveUser = getUserFromMap(requestMap);
					userDao.save(saveUser);
					return new ResponseEntity<String>("User sign up successfully", HttpStatus.OK);
				} else {
					return CafeUtils.getResponseEntity(CafeConstants.EXISTED_USER, HttpStatus.BAD_REQUEST);
				}
			} else {
				return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean validateSignUpMap(Map<String, String> requestMap) {
		return (requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
				&& requestMap.containsKey("email") && requestMap.containsKey("password"));
	}

	private User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");
		return user;
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestmap, HttpServletResponse response) {
		log.info("inside login");

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestmap.get("email"), requestmap.get("password")));

//			Authentication authen = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(requestmap.get("email"), requestmap.get("password")));

			if (authentication.isAuthenticated()) {

				if (customerUsersDetailsSevice.getUserDetail().getStatus().equalsIgnoreCase("true")) {

					String token = jwtUtil.generateToken(customerUsersDetailsSevice.getUserDetail().getEmail(),
							customerUsersDetailsSevice.getUserDetail().getRole());

					// create a cookie
					Cookie cookie = new Cookie("Authorization", token);

					// optional properties
					cookie.setSecure(true);
					cookie.setHttpOnly(true);
					cookie.setPath("/");

					// add cookie to response
					response.addCookie(cookie);

					return new ResponseEntity<String>("{\"token\":\"" + token + "\"}", HttpStatus.OK);
				} else {
					return new ResponseEntity<String>("{\"message\":\"" + "Wait for admin approval" + "\"}",
							HttpStatus.BAD_REQUEST);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			return new ResponseEntity<List<UserWrapper>>(dao.getAllUser(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateUser(int id, Map<String, String> requestMap) {

		try {

			/*
			 * Integer currentUserId =
			 * dao.findByEmailId(jwtFilter.getCurrentUser()).getId();
			 * 
			 * if (!currentUserId.equals(id)) { return
			 * CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,
			 * HttpStatus.INTERNAL_SERVER_ERROR); }
			 */

			if (jwtFilter.isAdmin()) {

				User userUpdate = dao.findById(id);

				if (userUpdate == null) {
					return CafeUtils.getResponseEntity(CafeConstants.NOT_EXISTED_USER, HttpStatus.NOT_FOUND);
				}

//				save user info update
				userUpdate.setStatus(requestMap.get("status"));
				dao.save(userUpdate);

//				send email to all admin
				sendEmailToAllAdmin(requestMap.get("status"), userUpdate.getEmail(), dao.getAllAdminEmail());

				return new ResponseEntity<String>("{\"message\":\"" + "Update user successfully" + "\"}",
						HttpStatus.OK);
			} else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {

		try {

			/*
			 * Integer currentUserId =
			 * dao.findByEmailId(jwtFilter.getCurrentUser()).getId();
			 * 
			 * if (!currentUserId.equals(id)) { return
			 * CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,
			 * HttpStatus.INTERNAL_SERVER_ERROR); }
			 */

			if (jwtFilter.isAdmin()) {

				User userUpdate = dao.findById(Integer.parseInt(requestMap.get("id")));

				if (userUpdate == null) {
					return CafeUtils.getResponseEntity(CafeConstants.NOT_EXISTED_USER, HttpStatus.NOT_FOUND);
				}

//				save user info update
				userUpdate.setStatus(requestMap.get("status"));
				dao.save(userUpdate);

//				send email to all admin
				sendEmailToAllAdmin(requestMap.get("status"), userUpdate.getEmail(), dao.getAllAdminEmail());

				return new ResponseEntity<String>("{\"message\":\"" + "Update user successfully" + "\"}",
						HttpStatus.OK);
			} else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void sendEmailToAllAdmin(String status, String email, List<String> allAdminEmail) {
		allAdminEmail.remove(jwtFilter.getCurrentUser());
		if (status != null && status.equalsIgnoreCase("true")) {
			emailUtils.sendSimpleMessage(email, "Account approved",
					"Your account has been approved! Enjoy your journey!!", allAdminEmail);
		} else {
			emailUtils.sendSimpleMessage(email, "Account Disabled", "Your account has been disabled!", allAdminEmail);
		}
	}

	@Override
	public ResponseEntity<String> checkToken() {
		return CafeUtils.getResponseEntity("true", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestmap) {
		try {
			User user = dao.findByEmail(jwtFilter.getCurrentUser());
			if(!user.equals(null)) {
				if(user.getPassword().equals(requestmap.get("oldPassword"))) {
					user.setPassword(requestmap.get("newPassword"));
					dao.save(user);
				} else {
					return CafeUtils.getResponseEntity("Incorrect old password", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> getForgotPasswordOTP(Map<String, String> requestmap) {
		try {
			User user = dao.findByEmail(requestmap.get("email"));
			if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
				emailUtils.forgotPasswordMail(requestmap.get("email"), "Reset password OTP", "d1d12r");
				return CafeUtils.getResponseEntity("Check your mail to reset password", HttpStatus.INTERNAL_SERVER_ERROR); 
			}
			
			return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
