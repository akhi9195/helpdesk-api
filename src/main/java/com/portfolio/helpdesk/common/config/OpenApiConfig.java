package com.portfolio.helpdesk.common.config;

import com.portfolio.helpdesk.common.security.CurrentUser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    static {
        // CurrentUser is built from the JWT by CurrentUserArgumentResolver, not bound from the request
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(CurrentUser.class);
    }

    @Bean
    OpenAPI helpDeskOpenApi() {
        return new OpenAPI().info(new Info()
                .title("HelpDesk API")
                .version("v1")
                .description("Support tickets with a USER / SUPPORT / ADMIN lifecycle"));
    }
}