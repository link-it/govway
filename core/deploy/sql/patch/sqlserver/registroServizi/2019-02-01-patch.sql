ALTER TABLE soggetti ALTER COLUMN utente VARCHAR(2800);
ALTER TABLE soggetti ALTER COLUMN subject VARCHAR(2800);
ALTER TABLE soggetti ADD cn_subject VARCHAR(255);
ALTER TABLE soggetti ADD issuer VARCHAR(2800);
ALTER TABLE soggetti ADD cn_issuer VARCHAR(255);
ALTER TABLE soggetti ADD certificate VARBINARY(MAX);
ALTER TABLE soggetti ADD cert_strict_verification INT;
