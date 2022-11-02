ALTER TABLE configurazione ADD COLUMN dati_richieste_statocache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN dati_richieste_dimensionecache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN dati_richieste_algoritmocache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN dati_richieste_idlecache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN dati_richieste_lifecache VARCHAR(255);

UPDATE configurazione set dati_richieste_statocache='abilitato';
UPDATE configurazione set dati_richieste_dimensionecache='15000';
UPDATE configurazione set dati_richieste_algoritmocache='lru';
UPDATE configurazione set dati_richieste_lifecache='7200';
