ALTER TABLE servizi_applicativi MODIFY COLUMN utente VARCHAR(2800);
ALTER TABLE servizi_applicativi MODIFY COLUMN subject VARCHAR(2800);
ALTER TABLE servizi_applicativi ADD COLUMN cn_subject VARCHAR(255);
ALTER TABLE servizi_applicativi ADD COLUMN issuer VARCHAR(2800);
ALTER TABLE servizi_applicativi ADD COLUMN cn_issuer VARCHAR(255);
ALTER TABLE servizi_applicativi ADD COLUMN certificate MEDIUMBLOB;
ALTER TABLE servizi_applicativi ADD COLUMN cert_strict_verification INT;
