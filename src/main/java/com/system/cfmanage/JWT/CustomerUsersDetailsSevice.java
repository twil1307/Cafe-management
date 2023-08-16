package com.system.cfmanage.JWT;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.system.cfmanage.dao.UserDao;

@Service
public class CustomerUsersDetailsSevice implements UserDetailsService {
	
	@Autowired
	UserDao userDao;

	private com.system.cfmanage.POJO.User userDetail;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		userDetail = userDao.findByEmailId(username);
		if(!Objects.isNull(userDetail)) {
			return new User(username, userDetail.getPassword(), new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found");
		}
	}

	public com.system.cfmanage.POJO.User getUserDetail() {
//		com.system.cfmanage.POJO.User user = userDetail;
//		user.setPassword(null);
		return userDetail;
	}
}
