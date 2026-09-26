package com.portfolio.helpdesk.user;

import com.portfolio.helpdesk.common.security.CurrentUser;
import com.portfolio.helpdesk.user.dto.ChangeRoleRequest;
import com.portfolio.helpdesk.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin: users", description = "Role management")
class AdminUserController {

    private final UserService userService;

    @PatchMapping("/{id}/role")
    @Operation(summary = "Change a user's role",
            description = "Caller: ADMIN. Allowed target roles: USER or SUPPORT. "
                    + "Assigning ADMIN or changing your own role returns 422 INVALID_ROLE_CHANGE.")
    UserResponse changeRole(@Parameter(description = "Id of the user to change", example = "7")
                            @PathVariable @Positive Long id,
                            @Valid @RequestBody ChangeRoleRequest request,
                            CurrentUser currentUser) {
        return userService.changeRole(id, request, currentUser);
    }
}