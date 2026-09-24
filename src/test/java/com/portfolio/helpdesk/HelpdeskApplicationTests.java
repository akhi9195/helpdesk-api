package com.portfolio.helpdesk;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class HelpdeskApiApplicationTests {

	@Autowired
	JdbcTemplate jdbcTemplate;


	@Test
	void contextLoads() {
	}

	@Test
	void flywayAppliedBaselineMigration() {
		Integer applied = jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM flyway_schema_history WHERE version = '1' AND success",
				Integer.class);
		assertThat(applied).isEqualTo(1);
	}

}
