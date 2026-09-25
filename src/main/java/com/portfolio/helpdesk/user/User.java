package com.portfolio.helpdesk.user;

import com.portfolio.helpdesk.common.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;
import java.util.Objects;


@Entity
@Table(name="users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Column(name="full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name="email", nullable = false, length = 150)
    private String email;

    @Column(name="password_hash", nullable = false , length = 100)
    private String passwordHash;


    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false , length = 20)
    private Role role;

    /** Registration always produces a USER; only an ADMIN changes roles later. */
    public User(String fullName, String email, String passwordHash) {
        this.fullName = Objects.requireNonNull(fullName, "fullName");
        this.email = normalizeEmail(Objects.requireNonNull(email, "email"));
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.role = Role.USER;
    }

    /** Single place for the lower-case rule (BR-7); reused for lookups by email. */
    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
