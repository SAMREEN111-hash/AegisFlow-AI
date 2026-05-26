package com.aegisflow.api.identity.service;

import com.aegisflow.api.identity.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdentityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        com.aegisflow.api.identity.domain.User user = userRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        return new User(
                user.getEmail(),
                user.getPasswordHash(),
                user.canAuthenticate(java.time.Instant.now()),
                true,
                true,
                user.getLockedUntil() == null || user.getLockedUntil().isBefore(java.time.Instant.now()),
                List.of(new SimpleGrantedAuthority("AUTHENTICATED")));
    }
}
