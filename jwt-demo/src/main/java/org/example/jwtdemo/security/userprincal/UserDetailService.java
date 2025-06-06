package org.example.jwtdemo.security.userprincal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.jwtdemo.model.User;
import org.example.jwtdemo.repository.IUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
    private final IUserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                ()-> new UsernameNotFoundException("User Not Fount with user name or email: " + username)
        );
        return UserPrinciple.build(user);
    }
}
