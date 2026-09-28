package com.portfolio.helpdesk.demo;

import com.portfolio.helpdesk.common.security.Role;
import com.portfolio.helpdesk.user.User;
import com.portfolio.helpdesk.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true")
class DemoAccountsInitializer implements ApplicationRunner {

    private final DemoProperties props;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        ensure(props.support(), Role.SUPPORT);
        ensure(props.user(), Role.USER);
    }

    private void ensure(DemoProperties.Account account, Role role) {
        if (account == null || !StringUtils.hasText(account.email())
                || !StringUtils.hasText(account.password())) {
            throw new IllegalStateException("Demo mode is on but " + role + " demo credentials are missing");
        }
        String email = User.normalizeEmail(account.email());

        userService.findByEmail(email).ifPresentOrElse(existing -> {
            boolean roleDrifted = existing.getRole() != role;
            boolean passwordDrifted = !passwordEncoder.matches(account.password(), existing.getPasswordHash());
            if (roleDrifted || passwordDrifted) {
                userService.resetAccount(existing.getId(), role, passwordEncoder.encode(account.password()));
                log.info("Demo {} account reset", role);
            }
        }, () -> {
            userService.createUser(account.fullName(), email, passwordEncoder.encode(account.password()), role);
            log.info("Demo {} account created", role);
        });
    }
}