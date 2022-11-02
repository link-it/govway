ALTER TABLE configurazione ADD dati_richieste_statocache VARCHAR2(255);
ALTER TABLE configurazione ADD dati_richieste_dimensionecache VARCHAR2(255);
ALTER TABLE configurazione ADD dati_richieste_algoritmocache VARCHAR2(255);
ALTER TABLE configurazione ADD dati_richieste_idlecache VARCHAR2(255);
ALTER TABLE configurazione ADD dati_richieste_lifecache VARCHAR2(255);

UPDATE configurazione set dati_richieste_statocache='abilitato';
UPDATE configurazione set dati_richieste_dimensionecache='15000';
UPDATE configurazione set dati_richieste_algoritmocache='lru';
UPDATE configurazione set dati_richieste_lifecache='7200';
