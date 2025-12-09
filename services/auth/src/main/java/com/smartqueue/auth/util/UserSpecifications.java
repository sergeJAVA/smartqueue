package com.smartqueue.auth.util;

import com.smartqueue.auth.dto.request.SearchUsersRequest;
import com.smartqueue.auth.entity.Role;
import com.smartqueue.auth.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

@UtilityClass
public class UserSpecifications {

    public static Specification<User> getSpecification(SearchUsersRequest request) {
        Specification<User> specification = Specification.allOf();

        if (!isUsernameValid(request.getUsername())
                && !isRolesValid(request.getRoles())) {
            return null;
        }

        if (isUsernameValid(request.getUsername())) {
            specification = specification.and(specificationUsername(request));
        }

        if (isRolesValid(request.getRoles())) {
            request.setRoles(rolesToUpperCase(request.getRoles()));
            specification = specification.and(specificationHasAnyRole(request.getRoles()));
        }

        return specification;
    }

    public static Specification<User> specificationUsername(SearchUsersRequest request) {
        return (root, query, cb) ->
                cb.like(root.get("username"), "%" + request.getUsername() + "%");
    }

    public static Specification<User> specificationHasAnyRole(List<String> roleNames) {
        return (root, query, criteriaBuilder) -> {
            if (roleNames == null || roleNames.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Join<User, Role> rolesJoin = root.join("roles", JoinType.INNER);
            return rolesJoin.get("name").in(roleNames);
        };
    }

    private boolean isRolesValid(List<String> roles) {
        return roles != null && !roles.isEmpty();
    }

    private boolean isUsernameValid(String username) {
        return StringUtils.hasText(username);
    }

    private List<String> rolesToUpperCase(List<String> roles) {
        return roles.stream()
                .map(role -> role.toUpperCase(Locale.ROOT))
                .toList();
    }

}
