-- Create loan_status table
CREATE TABLE loan_status (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE INDEX idx_loan_status_name ON loan_status(name);
