package com.example.demo.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByEmail(String email);
	
	Optional<User> findByUsername(String username);
	
	boolean existsByEmail(String email);
	
	boolean existsByUsername(String username);
	
	List<User> findByRole(Role role);
	
	List<User> findByRoleAndActive(Role role, Boolean active);

}
