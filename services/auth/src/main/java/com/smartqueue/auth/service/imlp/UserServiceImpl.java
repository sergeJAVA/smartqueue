package com.smartqueue.auth.service.imlp;

import com.smartqueue.auth.dto.UserDto;
import com.smartqueue.auth.dto.mapper.UserMapper;
import com.smartqueue.auth.dto.request.CreateUserRequest;
import com.smartqueue.auth.entity.Role;
import com.smartqueue.auth.entity.User;
import com.smartqueue.auth.exception.RoleNotFoundException;
import com.smartqueue.auth.exception.UsernameTakenException;
import com.smartqueue.auth.repository.RoleRepository;
import com.smartqueue.auth.repository.UserRepository;
import com.smartqueue.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        userRepository.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new UsernameTakenException("Username <<" + user.getUsername() + ">> is already taken!");
        });
        Set<Role> roles = getRoles(request);
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public Boolean deleteUserById(Long id) {
        int count = userRepository.deleteUserById(id);
        return count > 0;
    }

    @Override
    public Page<User> findAll() {
        return null;
    }

    private Set<Role> getRoles(CreateUserRequest request) {
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RoleNotFoundException("The role with name USER not found!"));
            roles.add(userRole);
            return roles;
        }

        for (String roleName : request.getRoles()) {
            Role role = roleRepository.findByName(roleName.toUpperCase(Locale.ROOT))
                    .orElseThrow(() -> new RoleNotFoundException("The role with name <<" + roleName + ">> not found!"));
            roles.add(role);
        }

        return roles;
    }

}
