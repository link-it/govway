ALTER TABLE transazioni ADD tipo_api INT;
ALTER TABLE transazioni ADD gruppi VARCHAR(20);

ALTER TABLE transazioni ADD client_address VARCHAR(20);
ALTER TABLE transazioni DROP COLUMN eventi_gestione;
ALTER TABLE transazioni ADD eventi_gestione VARCHAR(20);

CALL SYSPROC.ADMIN_CMD ('REORG TABLE transazioni') ;

ALTER TABLE statistiche_orarie ADD client_address VARCHAR(20);
UPDATE statistiche_orarie set client_address='-';
ALTER TABLE statistiche_orarie ALTER COLUMN client_address SET NOT NULL;
ALTER TABLE statistiche_orarie ADD gruppi VARCHAR(20);
UPDATE statistiche_orarie set gruppi='-';
ALTER TABLE statistiche_orarie ALTER COLUMN gruppi SET NOT NULL;
CALL SYSPROC.ADMIN_CMD ('REORG TABLE statistiche_orarie') ;

ALTER TABLE statistiche_giornaliere ADD client_address VARCHAR(20);
UPDATE statistiche_giornaliere set client_address='-';
ALTER TABLE statistiche_giornaliere ALTER COLUMN client_address SET NOT NULL;
ALTER TABLE statistiche_giornaliere ADD gruppi VARCHAR(20);
UPDATE statistiche_giornaliere set gruppi='-';
ALTER TABLE statistiche_giornaliere ALTER COLUMN gruppi SET NOT NULL;
CALL SYSPROC.ADMIN_CMD ('REORG TABLE statistiche_giornaliere') ;

ALTER TABLE statistiche_settimanali ADD client_address VARCHAR(20);
UPDATE statistiche_settimanali set client_address='-';
ALTER TABLE statistiche_settimanali ALTER COLUMN client_address SET NOT NULL;
ALTER TABLE statistiche_settimanali ADD gruppi VARCHAR(20);
UPDATE statistiche_settimanali set gruppi='-';
ALTER TABLE statistiche_settimanali ALTER COLUMN gruppi SET NOT NULL;
CALL SYSPROC.ADMIN_CMD ('REORG TABLE statistiche_settimanali') ;

ALTER TABLE statistiche_mensili ADD client_address VARCHAR(20);
UPDATE statistiche_mensili set client_address='-';
ALTER TABLE statistiche_mensili ALTER COLUMN client_address SET NOT NULL;
ALTER TABLE statistiche_mensili ADD gruppi VARCHAR(20);
UPDATE statistiche_mensili set gruppi='-';
ALTER TABLE statistiche_mensili ALTER COLUMN gruppi SET NOT NULL;
CALL SYSPROC.ADMIN_CMD ('REORG TABLE statistiche_mensili') ;
