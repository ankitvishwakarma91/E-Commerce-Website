package com.softworkshub.ecom.services;

import com.softworkshub.ecom.model.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    public UserDetails saveUser(UserDetails userDetails);

    public UserDetails findUserByEmail(String email);

    public List<UserDetails> getAllUsers(String role);

    public Boolean updateAccountStatus(Integer id , Boolean status);

    public void increaseFailedAttempts(UserDetails user);

    public void userAccountLock(UserDetails user);

    public Boolean unlockUserAccount(UserDetails user);

    public void resetAttempts(Integer id);

    void updateUserResetToken(String email, String resetToken);

    public UserDetails getUserByToken(String token);

    public UserDetails updateUser(UserDetails userDetails);

    public UserDetails updateUserProfile(UserDetails user, MultipartFile img);
}
