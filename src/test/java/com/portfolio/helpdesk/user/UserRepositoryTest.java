package com.portfolio.helpdesk.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.portfolio.helpdesk.support.RepositoryTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled("Connection closed after first query – investigate Testcontainers setup")
@RepositoryTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void startsWithMigratedEmptySchema() {
        assertThat(userRepository.count()).isZero();
    }
}