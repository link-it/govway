ALTER TABLE soggetti MODIFY utente VARCHAR2(2800);
ALTER TABLE soggetti MODIFY subject VARCHAR2(2800);
ALTER TABLE soggetti ADD cn_subject VARCHAR2(255);
ALTER TABLE soggetti ADD issuer VARCHAR2(2800);
ALTER TABLE soggetti ADD cn_issuer VARCHAR2(255);
ALTER TABLE soggetti ADD certificate BLOB;
ALTER TABLE soggetti ADD cert_strict_verification NUMBER;
