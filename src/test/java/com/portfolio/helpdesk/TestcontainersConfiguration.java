package com.portfolio.helpdesk;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Starts a throwaway PostgreSQL in Docker for tests.
 * {@code @ServiceConnection} hands its URL, username and password to Spring,
 * so no datasource properties are needed in application-test.yml.
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {

        return new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"));
    }
}

