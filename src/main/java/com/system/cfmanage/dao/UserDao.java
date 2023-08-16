package com.system.cfmanage.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.system.cfmanage.POJO.User;
import com.system.cfmanage.wrapper.UserWrapper;

public interface UserDao extends JpaRepository<User, Integer> {
	
//	@Query(name = "User.findByEmailId", nativeQuery=true, value = "select * from users u where u.email=:email")
	User findByEmailId(String email);
	
	User findById(int id);
	
	User findByEmail(String email);
	
	@Query(name = "User.getAllUser", nativeQuery=false, 
			value = "select new com.system.cfmanage.wrapper.UserWrapper(u.id, u.name, u.email, u.contactNumber, u.status) "
			+ "from User u where u.role='user'")
	List<UserWrapper> getAllUser();

	@Query("select u.email from User u where u.role='admin'")
	List<String> getAllAdminEmail();
	
	
}
