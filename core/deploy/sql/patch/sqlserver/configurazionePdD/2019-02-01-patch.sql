ALTER TABLE servizi_applicativi ALTER COLUMN utente VARCHAR(2800);
ALTER TABLE servizi_applicativi ALTER COLUMN subject VARCHAR(2800);
ALTER TABLE servizi_applicativi ADD cn_subject VARCHAR(255);
ALTER TABLE servizi_applicativi ADD issuer VARCHAR(2800);
ALTER TABLE servizi_applicativi ADD cn_issuer VARCHAR(255);
ALTER TABLE servizi_applicativi ADD certificate VARBINARY(MAX);
ALTER TABLE servizi_applicativi ADD cert_strict_verification INT;
