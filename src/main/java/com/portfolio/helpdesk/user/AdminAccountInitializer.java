package com.portfolio.helpdesk.user;

import com.portfolio.helpdesk.common.security.Role;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
class AdminAccountInitializer implements ApplicationRunner {

    private static final String PASSWORD_RULE = "^(?=.*[A-Za-z])(?=.*\\d).{8,72}$";

    private final AdminProperties props;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(props.email()) || !StringUtils.hasText(props.password())) {
            log.warn("ADMIN_EMAIL / ADMIN_PASSWORD not set; skipping admin bootstrap");
            return;
        }
        if (!props.password().matches(PASSWORD_RULE)) {
            throw new IllegalStateException(
                    "ADMIN_PASSWORD must be 8-72 characters with at least one letter and one digit");
        }

        String email = User.normalizeEmail(props.email());
        Optional<User> existing = userService.findByEmail(email);

        if (existing.isEmpty()) {
            userService.createUser(props.fullName(), email, passwordEncoder.encode(props.password()), Role.ADMIN);
            log.info("Bootstrap admin account created");
        } else if (existing.get().getRole() != Role.ADMIN) {
            log.warn("ADMIN_EMAIL belongs to an existing non-admin account; it was NOT promoted");
        } else {
            log.debug("Bootstrap admin account already exists; nothing to do");
        }
    }
}