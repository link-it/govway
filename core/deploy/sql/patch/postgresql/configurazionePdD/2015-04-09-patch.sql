ALTER TABLE configurazione ADD COLUMN config_statocache VARCHAR(255); 
ALTER TABLE configurazione ADD COLUMN config_dimensionecache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN config_algoritmocache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN config_idlecache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN config_lifecache VARCHAR(255);

ALTER TABLE configurazione ADD COLUMN auth_statocache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN auth_dimensionecache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN auth_algoritmocache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN auth_idlecache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN auth_lifecache VARCHAR(255);
