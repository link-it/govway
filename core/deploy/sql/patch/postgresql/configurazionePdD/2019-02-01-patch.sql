ALTER TABLE servizi_applicativi ALTER COLUMN utente TYPE VARCHAR(2800);
ALTER TABLE servizi_applicativi ALTER COLUMN subject TYPE VARCHAR(2800);
ALTER TABLE servizi_applicativi ADD COLUMN cn_subject VARCHAR(255);
ALTER TABLE servizi_applicativi ADD COLUMN issuer VARCHAR(2800);
ALTER TABLE servizi_applicativi ADD COLUMN cn_issuer VARCHAR(255);
ALTER TABLE servizi_applicativi ADD COLUMN certificate BYTEA;
ALTER TABLE servizi_applicativi ADD COLUMN cert_strict_verification INT;
