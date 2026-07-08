-- Cache LLM (es. vault PII di sessione): configurazione della cache dei dati LLM
ALTER TABLE configurazione ADD COLUMN llm_cache_statocache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN llm_cache_dimensionecache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN llm_cache_algoritmocache VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN llm_cache_idlecache VARCHAR(255);

-- Sempre abilitata (l'uso effettivo e' demandato al LLM Provider Binding)
UPDATE configurazione set llm_cache_statocache='abilitato';
UPDATE configurazione set llm_cache_dimensionecache='10000';
UPDATE configurazione set llm_cache_algoritmocache='lru';
