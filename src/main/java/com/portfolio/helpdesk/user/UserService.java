package com.portfolio.helpdesk.user;

import com.portfolio.helpdesk.common.exception.DuplicateResourceException;
import com.portfolio.helpdesk.common.exception.InvalidRoleChangeException;
import com.portfolio.helpdesk.common.exception.ResourceNotFoundException;
import com.portfolio.helpdesk.common.security.CurrentUser;
import com.portfolio.helpdesk.common.security.Role;
import com.portfolio.helpdesk.user.dto.ChangeRoleRequest;
import com.portfolio.helpdesk.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /** For other features (ticket) that need a User entity. */
    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    /** FR-7 */
    @Transactional
    public UserResponse changeRole(Long userId, ChangeRoleRequest request, CurrentUser actor) {
        if (actor.id().equals(userId)) {
            throw new InvalidRoleChangeException("Administrators cannot change their own role");
        }
        if (request.role() == Role.ADMIN) {
            throw new InvalidRoleChangeException("Role can only be set to USER or SUPPORT");
        }
        User user = getUserEntity(userId);
        user.setRole(request.role());
        // No save() call: the entity is managed, so dirty checking writes the UPDATE at commit.
        return userMapper.toResponse(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserResponse createUser(String fullName, String email, String passwordHash, Role role) {
        email = User.normalizeEmail(email);
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already registered");
        }
        User user = new User(fullName, email, passwordHash);   // 3-arg constructor, role = USER
        user.setRole(role);
        try {
            return userMapper.toResponse(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("Email is already registered");
        }
    }

    public UserResponse getUserResponse(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}