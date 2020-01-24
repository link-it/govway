ALTER TABLE transazioni ADD tipo_api NUMBER;
ALTER TABLE transazioni ADD gruppi VARCHAR2(20);

ALTER TABLE transazioni ADD client_address VARCHAR2(20);
ALTER TABLE transazioni DROP COLUMN eventi_gestione;
ALTER TABLE transazioni ADD eventi_gestione VARCHAR2(20);
