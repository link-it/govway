ALTER TABLE configurazione ADD aa_statocache VARCHAR2(255);
ALTER TABLE configurazione ADD aa_dimensionecache VARCHAR2(255);
ALTER TABLE configurazione ADD aa_algoritmocache VARCHAR2(255);
ALTER TABLE configurazione ADD aa_idlecache VARCHAR2(255);
ALTER TABLE configurazione ADD aa_lifecache VARCHAR2(255);

UPDATE configurazione set aa_statocache='abilitato';
UPDATE configurazione set aa_dimensionecache='5000';
UPDATE configurazione set aa_algoritmocache='lru';
UPDATE configurazione set aa_lifecache='7200';
