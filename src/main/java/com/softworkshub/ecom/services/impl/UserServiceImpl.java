package com.softworkshub.ecom.services.impl;

import com.softworkshub.ecom.model.UserDetails;
import com.softworkshub.ecom.repository.UserRepository;
import com.softworkshub.ecom.services.UserService;
import com.softworkshub.ecom.utils.AppConstants;
import org.aspectj.apache.bcel.util.ClassPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails saveUser(UserDetails userDetails) {
        userDetails.setRole("ROLE_USER");
        userDetails.setIsEnabled(true);
        userDetails.setAccountNonLocked(true);
        userDetails.setFailedLoginAttempts(0);
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        return userRepository.save(userDetails);
    }

    @Override
    public UserDetails findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public List<UserDetails> getAllUsers(String role) {
        return userRepository.findByRole(role);
    }

    public Boolean updateAccountStatus(Integer id, Boolean status) {
        Optional<UserDetails> userById = userRepository.findById(id);

        if (userById.isPresent()) {
            UserDetails userDetails = userById.get();
            userDetails.setIsEnabled(status);
            userRepository.save(userDetails);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedAttempts(UserDetails user) {
        int attempt= user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempt);
        userRepository.save(user);
    }

    @Override
    public void userAccountLock(UserDetails user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public Boolean unlockUserAccount(UserDetails user) {
        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime + AppConstants.UNLOCK_DURATION_TIME;
        long currentTime = System.currentTimeMillis();

        if (currentTime > unlockTime) {
            user.setAccountNonLocked(true);
            user.setFailedLoginAttempts(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resetAttempts(Integer id) {

    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        UserDetails byEmail = userRepository.findByEmail(email);
        byEmail.setResetToken(resetToken);
        userRepository.save(byEmail);
    }

    @Override
    public UserDetails getUserByToken(String token) {
        return userRepository.findByResetToken(token);
    }

    @Override
    public UserDetails updateUser(UserDetails userDetails) {
        return userRepository.save(userDetails);
    }

    @Override
    public UserDetails updateUserProfile(UserDetails user, MultipartFile img) {
        UserDetails userDetails = userRepository.findById(user.getId()).get();

        if (!img.isEmpty()) {
            userDetails.setProfileImage(img.getOriginalFilename());
        }

        if (!ObjectUtils.isEmpty(userDetails)){
            userDetails.setName(user.getName());
            userDetails.setMobileNumber(user.getMobileNumber());
            userDetails.setAddress(user.getAddress());
            userDetails.setCity(user.getCity());
            userDetails.setState(user.getState());
            userDetails.setPincode(user.getPincode());
            userDetails = userRepository.save(userDetails);
        }

        try {
            if (!img.isEmpty()) {
                File file = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(file.getAbsolutePath() + File.separator + "profileFromWeb" + File.separator + img.getOriginalFilename());

                System.out.println(path);
                Files.copy(img.getInputStream(), path , StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userDetails;
    }

}
































