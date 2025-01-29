package com.softworkshub.ecom.config;

import com.softworkshub.ecom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        com.softworkshub.ecom.model.UserDetails byEmail = userRepository.findByEmail(username);

        if (byEmail == null) {
            throw new UsernameNotFoundException(username);
        }

        return new CustomUser(byEmail);
    }
}
