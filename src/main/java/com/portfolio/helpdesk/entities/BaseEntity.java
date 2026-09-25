package com.portfolio.helpdesk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
        * Common id and audit timestamps for every entity.
        * Timestamps are filled by Spring Data JPA auditing ( JpaAuditingConfig).
        */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name="created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name="updated_at",nullable = false)
    private Instant updatedAt;

    /**
     * Id-based equality, safe for Hibernate proxies and for transient entities.
     * Two unsaved entities (id == null) are never equal unless they are the same instance.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || effectiveClass(this) != effectiveClass(other)) {
            return false;
        }
        Long thisId = getId();
        return thisId != null && thisId.equals(((BaseEntity) other).getId());
    }

    /**
     * Class-based hash: stays constant before and after persist (when the id is assigned),
     * so an entity never gets "lost" inside a HashSet.
     */
    @Override
    public int hashCode() {
        return effectiveClass(this).hashCode();
    }
    private static Class<?> effectiveClass(Object o) {
        return o instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
    }
}
