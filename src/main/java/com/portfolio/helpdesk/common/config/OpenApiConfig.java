package com.portfolio.helpdesk.common.config;

import com.portfolio.helpdesk.common.security.CurrentUser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import java.util.Arrays;
import java.util.List;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    static {
        // CurrentUser is resolved by DevCurrentUserArgumentResolver, not bound from the request
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(CurrentUser.class);
    }

    @Bean
    OpenAPI helpDeskOpenApi() {
        return new OpenAPI().info(new Info()
                .title("HelpDesk API")
                .version("v1")
                .description("Support tickets with a USER / SUPPORT / ADMIN lifecycle"));
    }

    /** DEV ONLY - remove in Milestone 8 when the JWT Authorize button replaces these headers. */
    @Bean
    OperationCustomizer devUserHeaders() {
        return (operation, handlerMethod) -> {
            boolean needsUser = Arrays.stream(handlerMethod.getMethodParameters())
                    .anyMatch(p -> p.getParameterType().equals(CurrentUser.class));
            if (needsUser) {
                operation.addParametersItem(new HeaderParameter()
                        .name("X-User-Id")
                        .description("DEV ONLY: id of the calling user")
                        .required(true)
                        .schema(new IntegerSchema()));
                operation.addParametersItem(new HeaderParameter()
                        .name("X-User-Role")
                        .description("DEV ONLY: role of the calling user")
                        .required(true)
                        .schema(new StringSchema()._enum(List.of("USER", "SUPPORT", "ADMIN"))));
            }
            return operation;
        };
    }
}