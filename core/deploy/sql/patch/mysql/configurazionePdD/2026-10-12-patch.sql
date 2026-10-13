-- Precisione ai millisecondi supportata dalla versione 5.6.4, se si utilizza una versione precedente non usare il suffisso '(3)'
ALTER TABLE servizi_digest_params ADD COLUMN data_richiesta_rotazione TIMESTAMP(3) DEFAULT 0;
