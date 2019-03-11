ALTER TABLE soggetti ALTER COLUMN utente SET DATA TYPE VARCHAR(2800);
ALTER TABLE soggetti ALTER COLUMN subject SET DATA TYPE VARCHAR(2800);
ALTER TABLE soggetti ADD cn_subject VARCHAR(255);
ALTER TABLE soggetti ADD issuer VARCHAR(2800);
ALTER TABLE soggetti ADD cn_issuer VARCHAR(255);
ALTER TABLE soggetti ADD certificate BLOB;
ALTER TABLE soggetti ADD cert_strict_verification INT;
