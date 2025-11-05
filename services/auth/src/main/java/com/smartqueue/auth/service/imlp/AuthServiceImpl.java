package com.smartqueue.auth.service.imlp;

import com.smartqueue.auth.dto.request.LogInRequest;
import com.smartqueue.auth.dto.request.RegistrationRequest;
import com.smartqueue.auth.dto.response.AuthLogInResponse;
import com.smartqueue.auth.dto.response.AuthRegistrationResponse;
import com.smartqueue.auth.entity.Role;
import com.smartqueue.auth.entity.User;
import com.smartqueue.auth.exception.AuthLogInException;
import com.smartqueue.auth.exception.RoleNotFoundException;
import com.smartqueue.auth.exception.UsernameTakenException;
import com.smartqueue.auth.repository.RoleRepository;
import com.smartqueue.auth.repository.UserRepository;
import com.smartqueue.auth.security.CustomUserDetails;
import com.smartqueue.auth.security.service.JwtService;
import com.smartqueue.auth.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthRegistrationResponse registration(RegistrationRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isPresent()) {
            throw new UsernameTakenException("Username <<" + request.getUsername() + ">> is already taken!");
        }
        User user = createUser(request);
        userRepository.save(user);
        return successRegistration();
    }

    @Override
    public AuthLogInResponse logIn(LogInRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return successLogIn(userDetails);
        } catch (AuthenticationException ex) {
            log.error("Authentication failed for user: {}. Error: {}", request.getUsername(), ex.getMessage(), ex);
            throw new AuthLogInException("Incorrect username or password is specified");
        }
    }

    private User createUser(RegistrationRequest request) {
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("The role with name USER not found!"));
        return User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();
    }

    private AuthRegistrationResponse successRegistration() {
        return AuthRegistrationResponse.builder()
                .code(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .state("User has been successfully registered")
                .build();
    }

    private AuthLogInResponse successLogIn(CustomUserDetails userDetails) {
        return AuthLogInResponse.builder()
                .state("User has been authorized")
                .code(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .token(jwtService.generateJwtToken(userDetails))
                .build();
    }

}
