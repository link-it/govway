ALTER TABLE servizi_applicativi MODIFY utente VARCHAR2(2800);
ALTER TABLE servizi_applicativi MODIFY subject VARCHAR2(2800);
ALTER TABLE servizi_applicativi ADD cn_subject VARCHAR2(255);
ALTER TABLE servizi_applicativi ADD issuer VARCHAR2(2800);
ALTER TABLE servizi_applicativi ADD cn_issuer VARCHAR2(255);
ALTER TABLE servizi_applicativi ADD certificate BLOB;
ALTER TABLE servizi_applicativi ADD cert_strict_verification NUMBER;
