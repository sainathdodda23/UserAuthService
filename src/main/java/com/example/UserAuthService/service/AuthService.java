package com.example.UserAuthService.service;

import com.example.UserAuthService.model.Role;
import com.example.UserAuthService.model.User;
import com.example.UserAuthService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public User signup(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            return null;
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public String login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty() || !passwordEncoder.matches(password, optionalUser.get().getPasswordHash())) {
            return null;
        }
        return jwtService.generateToken(optionalUser.get());
    }
}
