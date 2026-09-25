-- Ticket list filtered by status
CREATE INDEX idx_tickets_status ON tickets (status);

-- "My tickets" for a USER (FK, does not automatically index)
CREATE INDEX idx_tickets_created_by ON tickets (created_by);

--  "assigned to me" queries
CREATE INDEX idx_tickets_assigned_to ON tickets (assigned_to);