package com.example.authentication.service;

import com.example.authentication.dto.AuthResponse;
import com.example.authentication.dto.LoginRequest;
import com.example.authentication.dto.RegisterRequest;
import com.example.authentication.entity.Role;
import com.example.authentication.entity.RoleName;
import com.example.authentication.entity.User;
import com.example.authentication.repository.RoleRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public String register(@Valid RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Block ADMIN role registration - only USER and SELLER allowed
        String roleInput = request.getRole().toUpperCase();
        if (roleInput.equals("ADMIN")) {
            throw new RuntimeException(
                    "ADMIN role registration is not allowed. Only USER and SELLER roles are permitted.");
        }

        if (!roleInput.equals("USER") && !roleInput.equals("SELLER")) {
            throw new RuntimeException("Invalid role. Only USER and SELLER roles are allowed.");
        }

        Role role = roleRepository
                .findByName(RoleName.valueOf("ROLE_" + roleInput))
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(Set.of(role));

        userRepository.save(user);

        return "User Registered Successfully";
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail()).get();

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}
