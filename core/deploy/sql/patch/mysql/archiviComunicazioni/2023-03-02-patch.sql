ALTER TABLE transazioni ADD COLUMN data_ingresso_richiesta_stream TIMESTAMP(3) DEFAULT 0;
ALTER TABLE transazioni ADD COLUMN data_uscita_richiesta_stream TIMESTAMP(3) DEFAULT 0;
ALTER TABLE transazioni ADD COLUMN data_ingresso_risposta_stream TIMESTAMP(3) DEFAULT 0;
ALTER TABLE transazioni ADD COLUMN data_uscita_risposta_stream TIMESTAMP(3) DEFAULT 0;

ALTER TABLE transazioni_sa ADD COLUMN data_uscita_richiesta_stream TIMESTAMP(3) DEFAULT 0;
ALTER TABLE transazioni_sa ADD COLUMN data_ingresso_risposta_stream TIMESTAMP(3) DEFAULT 0;
