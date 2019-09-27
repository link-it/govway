ALTER TABLE transazioni ADD COLUMN tipo_api INT;
ALTER TABLE transazioni ADD COLUMN gruppi VARCHAR(20);

ALTER TABLE transazioni ADD COLUMN client_address VARCHAR(20);
ALTER TABLE transazioni DROP COLUMN eventi_gestione;
ALTER TABLE transazioni ADD COLUMN eventi_gestione VARCHAR(20);

ALTER TABLE statistiche_orarie ADD COLUMN client_address VARCHAR(20);
UPDATE statistiche_orarie set client_address='-';
ALTER TABLE statistiche_orarie MODIFY COLUMN client_address VARCHAR(20) NOT NULL;
ALTER TABLE statistiche_orarie ADD COLUMN gruppi VARCHAR(20);
UPDATE statistiche_orarie set gruppi='-';
ALTER TABLE statistiche_orarie MODIFY COLUMN gruppi VARCHAR(20) NOT NULL;

ALTER TABLE statistiche_giornaliere ADD COLUMN client_address VARCHAR(20);
UPDATE statistiche_giornaliere set client_address='-';
ALTER TABLE statistiche_giornaliere MODIFY COLUMN client_address VARCHAR(20) NOT NULL;
ALTER TABLE statistiche_giornaliere ADD COLUMN gruppi VARCHAR(20);
UPDATE statistiche_giornaliere set gruppi='-';
ALTER TABLE statistiche_giornaliere MODIFY COLUMN gruppi VARCHAR(20) NOT NULL;

ALTER TABLE statistiche_settimanali ADD COLUMN client_address VARCHAR(20);
UPDATE statistiche_settimanali set client_address='-';
ALTER TABLE statistiche_settimanali MODIFY COLUMN client_address VARCHAR(20) NOT NULL;
ALTER TABLE statistiche_settimanali ADD COLUMN gruppi VARCHAR(20);
UPDATE statistiche_settimanali set gruppi='-';
ALTER TABLE statistiche_settimanali MODIFY COLUMN gruppi VARCHAR(20) NOT NULL;

ALTER TABLE statistiche_mensili ADD COLUMN client_address VARCHAR(20);
UPDATE statistiche_mensili set client_address='-';
ALTER TABLE statistiche_mensili MODIFY COLUMN client_address VARCHAR(20) NOT NULL;
ALTER TABLE statistiche_mensili ADD COLUMN gruppi VARCHAR(20);
UPDATE statistiche_mensili set gruppi='-';
ALTER TABLE statistiche_mensili MODIFY COLUMN gruppi VARCHAR(20) NOT NULL;
