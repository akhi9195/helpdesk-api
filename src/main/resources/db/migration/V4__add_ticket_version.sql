-- Optimistic locking: Hibernate increments this on every UPDATE
-- and adds "AND version = ?" to the WHERE clause.
ALTER TABLE tickets
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;