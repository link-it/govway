-- L'id_configurazione degli eventi del Controllo del Traffico (nome policy + API + raggruppamento) puo' superare i 255 caratteri.
-- La colonna 'id_configurazione' viene rimossa da INDEX_EVENTI e allargata a 4000.

ALTER TABLE notifiche_eventi DROP INDEX INDEX_EVENTI;
ALTER TABLE notifiche_eventi MODIFY COLUMN id_configurazione VARCHAR(4000);
CREATE INDEX INDEX_EVENTI ON notifiche_eventi (ora_registrazione DESC,severita,tipo,codice,cluster_id);
