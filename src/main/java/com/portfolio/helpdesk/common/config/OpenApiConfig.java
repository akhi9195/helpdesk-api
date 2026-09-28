package com.portfolio.helpdesk.common.config;

import com.portfolio.helpdesk.common.exception.ErrorResponse;
import com.portfolio.helpdesk.common.web.TraceIdFilter;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "HelpDesk API",
        version = "v1",
        description = "Support ticket REST API. Roles: USER (employees), SUPPORT (IT staff), ADMIN."))
@SecurityScheme(
        name = OpenApiConfig.BEARER,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Paste the accessToken from POST /api/auth/login (without the 'Bearer ' prefix).")
public class OpenApiConfig {

    public static final String BEARER = "bearerAuth";

    @Bean
    OpenApiCustomizer standardErrorResponses() {
        return openApi -> {
            ResolvedSchema error = ModelConverters.getInstance()
                    .resolveAsResolvedSchema(new AnnotatedType(ErrorResponse.class));
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            Components components = openApi.getComponents();
            components.addSchemas("ErrorResponse", error.schema);
            error.referencedSchemas.forEach(components::addSchemas);

            Content errorContent = new Content().addMediaType("application/json",
                    new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")));
            Header traceHeader = new Header()
                    .description("Trace id of this request; matches the server log line")
                    .schema(new StringSchema());

            openApi.getPaths().values().forEach(path -> path.readOperations().forEach(op -> {
                ApiResponses responses = op.getResponses();
                boolean secured = op.getSecurity() != null && !op.getSecurity().isEmpty();

                if (op.getRequestBody() != null) {
                    addIfAbsent(responses, "400", "VALIDATION_FAILED or MALFORMED_REQUEST", errorContent);
                }
                if (secured) {
                    addIfAbsent(responses, "401", "UNAUTHORIZED: missing, invalid or expired token", errorContent);
                    addIfAbsent(responses, "403", "ACCESS_DENIED: role or rule forbids the action", errorContent);
                }
                addIfAbsent(responses, "500", "INTERNAL_ERROR: details only in server logs", errorContent);
                responses.values().forEach(r -> r.addHeaderObject(TraceIdFilter.TRACE_ID_HEADER, traceHeader));
            }));
        };
    }

    private static void addIfAbsent(ApiResponses responses, String code, String description, Content content) {
        responses.computeIfAbsent(code, c -> new ApiResponse().description(description).content(content));
    }
}