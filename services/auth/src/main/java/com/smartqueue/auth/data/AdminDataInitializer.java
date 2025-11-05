package com.smartqueue.auth.data;

import com.smartqueue.auth.entity.Role;
import com.smartqueue.auth.entity.User;
import com.smartqueue.auth.repository.RoleRepository;
import com.smartqueue.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminDataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${bootstrap.admin.username:admin}")
    private String adminUsername;
    @Value("${bootstrap.admin.password:}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN")));

        if (adminPassword == null || adminPassword.isBlank()) {
            log.warn("Admin password not set; skipping admin user creation. Set bootstrap.admin.password in env to create one.");
            return;
        }

        userRepository.findByUsername(adminUsername).ifPresentOrElse(u -> {
            log.info("Admin user '{}' already exists, skipping creation", adminUsername);
        }, () -> {
            User admin = User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
            log.info("Admin user '{}' created", adminUsername);
        });
    }

}
