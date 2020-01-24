ALTER TABLE transazioni ADD tipo_api INT;
ALTER TABLE transazioni ADD gruppi VARCHAR(20);

ALTER TABLE transazioni ADD client_address VARCHAR(20);
ALTER TABLE transazioni DROP COLUMN eventi_gestione;
ALTER TABLE transazioni ADD eventi_gestione VARCHAR(20);
