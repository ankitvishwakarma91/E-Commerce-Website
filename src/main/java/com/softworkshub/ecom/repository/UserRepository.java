package com.softworkshub.ecom.repository;

import com.softworkshub.ecom.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserDetails, Integer> {
    UserDetails findByName(String username);
    UserDetails findByEmail(String email);
    List<UserDetails> findByRole(String role);
    UserDetails findByResetToken(String token);
}
