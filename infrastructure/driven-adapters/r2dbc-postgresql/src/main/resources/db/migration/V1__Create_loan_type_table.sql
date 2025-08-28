-- Create loan_type table
CREATE TABLE loan_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    min_amount DECIMAL(15,2) NOT NULL,
    max_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,4) NOT NULL,
    automatic_validation BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_loan_type_name ON loan_type(name);
