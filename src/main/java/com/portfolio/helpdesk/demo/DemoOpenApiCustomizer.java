package com.portfolio.helpdesk.demo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.MediaType;   // not Spring's MediaType
import org.springdoc.core.customizers.OpenApiCustomizer;

import java.util.LinkedHashMap;
import java.util.Map;

import com.portfolio.helpdesk.user.AdminProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true")
class DemoOpenApiCustomizer implements OpenApiCustomizer {

    private static final String LOGIN_PATH = "/api/auth/login";

    private final DemoProperties demo;
    private final AdminProperties admin;

    @Override
    public void customise(OpenAPI openApi) {
        Info info = openApi.getInfo();
        String existing = info.getDescription() == null ? "" : info.getDescription() + "\n\n";
        info.setDescription(existing + banner());
        addLoginExamples(openApi);
    }

    private String banner() {
        return """
                ### Try it in 2 minutes

                | Role | Email | Password |
                |---|---|---|
                | ADMIN | `%s` | `%s` |
                | SUPPORT | `%s` | `%s` |
                | USER | `%s` | `%s` |

                1. **POST /api/auth/login**: choose a role in the *Examples* dropdown, Execute, copy `accessToken`.
                2. Click **Authorize** and paste the token. To switch role: Authorize → Logout → log in again.
                3. As **USER**: POST /api/tickets (payload pre-filled). Note the returned `id`.
                4. As **SUPPORT**: PATCH /api/tickets/{id}/status with `IN_PROGRESS`, then `RESOLVED`.
                5. As **USER**: PATCH status to `CLOSED`. Try once more and get 409: the lifecycle is enforced.
                6. As **ADMIN**: register a new account and promote it via PATCH /api/admin/users/{id}/role.

                _Demo data is reset periodically. The first request after idle can take 30–50 s (free hosting)._
                """.formatted(
                admin.email(), admin.password(),
                demo.support().email(), demo.support().password(),
                demo.user().email(), demo.user().password());
    }

    private void addLoginExamples(OpenAPI openApi) {
        PathItem login = openApi.getPaths() == null ? null : openApi.getPaths().get(LOGIN_PATH);
        MediaType json = (login == null || login.getPost() == null || login.getPost().getRequestBody() == null)
                ? null
                : login.getPost().getRequestBody().getContent().get("application/json");
        if (json == null) {
            log.warn("Login operation not found at {}; demo examples not added", LOGIN_PATH);
            return;
        }
        Map<String, Example> examples = new LinkedHashMap<>();
        examples.put("admin", example("Log in as ADMIN", admin.email(), admin.password()));
        examples.put("support", example("Log in as SUPPORT", demo.support().email(), demo.support().password()));
        examples.put("user", example("Log in as USER", demo.user().email(), demo.user().password()));
        json.setExamples(examples);
    }

    private static Example example(String summary, String email, String password) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("email", email);
        body.put("password", password);
        return new Example().summary(summary).value(body);
    }
}