-- V1: baseline schema. Applied scripts are never edited; change the schema with V2, V3, ...
-- priority (V2), indexes (V3) and version (V4) are added later on purpose.

CREATE TABLE users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,          -- BCrypt hashes are 60 chars
    role          VARCHAR(20)  NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uk_users_email           UNIQUE (email),
    CONSTRAINT ck_users_email_lowercase CHECK (email = LOWER(email)),   -- BR-7
    CONSTRAINT ck_users_role            CHECK (role IN ('USER', 'SUPPORT', 'ADMIN'))
);

CREATE TABLE tickets (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title       VARCHAR(150) NOT NULL,
    description TEXT         NOT NULL,             -- 4000-char limit enforced by the API
    status      VARCHAR(20)  NOT NULL,
    category    VARCHAR(20)  NOT NULL,
    created_by  BIGINT       NOT NULL,
    assigned_to BIGINT,                            -- NULL until a SUPPORT user starts it
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    resolved_at TIMESTAMPTZ,
    closed_at   TIMESTAMPTZ,

    CONSTRAINT fk_tickets_created_by  FOREIGN KEY (created_by)  REFERENCES users (id),
    CONSTRAINT fk_tickets_assigned_to FOREIGN KEY (assigned_to) REFERENCES users (id),
    CONSTRAINT ck_tickets_status   CHECK (status   IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED')),
    CONSTRAINT ck_tickets_category CHECK (category IN ('NETWORK', 'HARDWARE', 'SOFTWARE', 'ACCESS', 'OTHER'))
);