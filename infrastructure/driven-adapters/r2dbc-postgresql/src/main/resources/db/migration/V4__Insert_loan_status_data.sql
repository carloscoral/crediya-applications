-- Insert initial loan status values
INSERT INTO loan_status (id, name, description) VALUES
(gen_random_uuid(), 'Pendiente de revisión', 'La solicitud de préstamo está siendo revisada'),
(gen_random_uuid(), 'Aprobado', 'La solicitud de préstamo ha sido aprobada'),
(gen_random_uuid(), 'Rechazado', 'La solicitud de préstamo ha sido rechazada'),
(gen_random_uuid(), 'Desembolsado', 'El monto del préstamo ha sido desembolsado al solicitante'),
(gen_random_uuid(), 'Cerrado', 'El préstamo ha sido completamente pagado y cerrado'),
(gen_random_uuid(), 'Cancelado', 'La solicitud de préstamo fue cancelada por el solicitante');
