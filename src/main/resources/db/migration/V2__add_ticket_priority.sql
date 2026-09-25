ALTER TABLE tickets
    ADD COLUMN priority VARCHAR(20) DEFAULT 'MEDIUM';

ALTER TABLE tickets
    ALTER COLUMN priority SET NOT NULL;

ALTER TABLE tickets
    ADD CONSTRAINT chk_tickets_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'));