package com.portfolio.helpdesk.auth;

import com.portfolio.helpdesk.auth.JwtTokenService.IssuedToken;
import com.portfolio.helpdesk.auth.dto.LoginRequest;
import com.portfolio.helpdesk.auth.dto.RegisterRequest;
import com.portfolio.helpdesk.auth.dto.TokenResponse;
import com.portfolio.helpdesk.common.security.Role;
import com.portfolio.helpdesk.user.UserService;
import com.portfolio.helpdesk.user.dto.UserResponse;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        String hash = passwordEncoder.encode(request.password());
        return userService.createUser(request.fullName(), email, hash, Role.USER);
    }

    public TokenResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        normalize(request.email()), request.password()));

        SecurityUser principal = (SecurityUser) auth.getPrincipal();
        IssuedToken token = jwtTokenService.issue(principal.id(), principal.role());

        return new TokenResponse(token.value(), "Bearer", token.expiresInSeconds(),
                userService.getUserResponse(principal.id()));
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}