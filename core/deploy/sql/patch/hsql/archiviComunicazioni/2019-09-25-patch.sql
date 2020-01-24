ALTER TABLE transazioni ADD COLUMN tipo_api INT;
ALTER TABLE transazioni ADD COLUMN gruppi VARCHAR(20);

ALTER TABLE transazioni ADD COLUMN client_address VARCHAR(20);
ALTER TABLE transazioni DROP COLUMN eventi_gestione;
ALTER TABLE transazioni ADD COLUMN eventi_gestione VARCHAR(20);

