ALTER TABLE configurazione ADD keystore_statocache VARCHAR2(255);
ALTER TABLE configurazione ADD keystore_dimensionecache VARCHAR2(255);
ALTER TABLE configurazione ADD keystore_algoritmocache VARCHAR2(255);
ALTER TABLE configurazione ADD keystore_idlecache VARCHAR2(255);
ALTER TABLE configurazione ADD keystore_lifecache VARCHAR2(255);
ALTER TABLE configurazione ADD keystore_crl_lifecache VARCHAR2(255);

UPDATE configurazione set keystore_statocache='abilitato';
UPDATE configurazione set keystore_dimensionecache='1000';
UPDATE configurazione set keystore_algoritmocache='lru';
UPDATE configurazione set keystore_lifecache='7200';
UPDATE configurazione set keystore_crl_lifecache='1800';
