ALTER TABLE configurazione ADD keystore_statocache VARCHAR(255);
ALTER TABLE configurazione ADD keystore_dimensionecache VARCHAR(255);
ALTER TABLE configurazione ADD keystore_algoritmocache VARCHAR(255);
ALTER TABLE configurazione ADD keystore_idlecache VARCHAR(255);
ALTER TABLE configurazione ADD keystore_lifecache VARCHAR(255);
ALTER TABLE configurazione ADD keystore_crl_lifecache VARCHAR(255);

UPDATE configurazione set keystore_statocache='abilitato';
UPDATE configurazione set keystore_dimensionecache='1000';
UPDATE configurazione set keystore_algoritmocache='lru';
UPDATE configurazione set keystore_lifecache='7200';
UPDATE configurazione set keystore_crl_lifecache='1800';
