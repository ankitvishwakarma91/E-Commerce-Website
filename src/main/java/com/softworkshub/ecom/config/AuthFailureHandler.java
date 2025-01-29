package com.softworkshub.ecom.config;

import com.softworkshub.ecom.model.UserDetails;
import com.softworkshub.ecom.repository.UserRepository;
import com.softworkshub.ecom.services.impl.UserServiceImpl;
import com.softworkshub.ecom.utils.AppConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");

        UserDetails byEmail = userRepository.findByEmail(username);

        if (byEmail != null) {
            if (byEmail.getIsEnabled()) {
                if (byEmail.getAccountNonLocked()) {
                    if (byEmail.getFailedLoginAttempts() < AppConstants.LOGIN_ATTEMPT) {
                        logger.info("Login attempt failed " + byEmail.getFailedLoginAttempts());
                        userService.increaseFailedAttempts(byEmail);
                        logger.info("Login attempt failed " + byEmail.getFailedLoginAttempts());
                    } else {
                        userService.userAccountLock(byEmail);
                        exception = new LockedException("Your account is locked. !! Failed Attempts 5");
                    }
                } else {
                    if (userService.unlockUserAccount(byEmail)) {
                        exception = new LockedException("Your account is Unlock. || Please try to login again ");
                    } else {

                        exception = new LockedException("Your account is Locked. || Please try after some time ! ");
                    }
                }
            } else {
                exception = new LockedException("Your Account is InActive");
            }
        } else {
            exception = new LockedException("Email & password is invalid ");
        }

        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
