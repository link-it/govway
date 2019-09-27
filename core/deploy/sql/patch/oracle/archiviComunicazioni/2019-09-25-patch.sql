ALTER TABLE transazioni ADD tipo_api NUMBER;
ALTER TABLE transazioni ADD gruppi VARCHAR2(20);

ALTER TABLE transazioni ADD client_address VARCHAR2(20);
ALTER TABLE transazioni DROP COLUMN eventi_gestione;
ALTER TABLE transazioni ADD eventi_gestione VARCHAR2(20);

ALTER TABLE statistiche_orarie ADD client_address VARCHAR2(20);
UPDATE statistiche_orarie set client_address='-';
ALTER TABLE statistiche_orarie MODIFY client_address NOT NULL;
ALTER TABLE statistiche_orarie ADD gruppi VARCHAR2(20);
UPDATE statistiche_orarie set gruppi='-';
ALTER TABLE statistiche_orarie MODIFY gruppi NOT NULL;

ALTER TABLE statistiche_giornaliere ADD client_address VARCHAR2(20);
UPDATE statistiche_giornaliere set client_address='-';
ALTER TABLE statistiche_giornaliere MODIFY client_address NOT NULL;
ALTER TABLE statistiche_giornaliere ADD gruppi VARCHAR2(20);
UPDATE statistiche_giornaliere set gruppi='-';
ALTER TABLE statistiche_giornaliere MODIFY gruppi NOT NULL;

ALTER TABLE statistiche_settimanali ADD client_address VARCHAR2(20);
UPDATE statistiche_settimanali set client_address='-';
ALTER TABLE statistiche_settimanali MODIFY client_address NOT NULL;
ALTER TABLE statistiche_settimanali ADD gruppi VARCHAR2(20);
UPDATE statistiche_settimanali set gruppi='-';
ALTER TABLE statistiche_settimanali MODIFY gruppi NOT NULL;

ALTER TABLE statistiche_mensili ADD client_address VARCHAR2(20);
UPDATE statistiche_mensili set client_address='-';
ALTER TABLE statistiche_mensili MODIFY client_address NOT NULL;
ALTER TABLE statistiche_mensili ADD gruppi VARCHAR2(20);
UPDATE statistiche_mensili set gruppi='-';
ALTER TABLE statistiche_mensili MODIFY gruppi NOT NULL;
