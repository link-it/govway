ALTER TABLE configurazione ADD aa_statocache VARCHAR(255);
ALTER TABLE configurazione ADD aa_dimensionecache VARCHAR(255);
ALTER TABLE configurazione ADD aa_algoritmocache VARCHAR(255);
ALTER TABLE configurazione ADD aa_idlecache VARCHAR(255);
ALTER TABLE configurazione ADD aa_lifecache VARCHAR(255);

UPDATE configurazione set aa_statocache='abilitato';
UPDATE configurazione set aa_dimensionecache='5000';
UPDATE configurazione set aa_algoritmocache='lru';
UPDATE configurazione set aa_lifecache='7200';
