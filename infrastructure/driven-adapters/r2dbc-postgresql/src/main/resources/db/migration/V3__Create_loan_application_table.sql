-- Create loan_application table
CREATE TABLE loan_application (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    amount DECIMAL(15,2) NOT NULL,
    months_term INTEGER NOT NULL,
    email VARCHAR(255) NOT NULL,
    loan_type_id UUID NOT NULL,
    loan_status_id UUID NOT NULL,

    CONSTRAINT fk_loan_application_loan_type 
        FOREIGN KEY (loan_type_id) REFERENCES loan_type(id) ON DELETE RESTRICT,
    CONSTRAINT fk_loan_application_loan_status 
        FOREIGN KEY (loan_status_id) REFERENCES loan_status(id) ON DELETE RESTRICT
);

CREATE INDEX idx_loan_application_loan_type_id ON loan_application(loan_type_id);
CREATE INDEX idx_loan_application_loan_status_id ON loan_application(loan_status_id);
CREATE INDEX idx_loan_application_email ON loan_application(email);
