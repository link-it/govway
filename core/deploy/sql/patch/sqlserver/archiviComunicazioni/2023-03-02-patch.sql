ALTER TABLE transazioni ADD data_ingresso_richiesta_stream DATETIME2;
ALTER TABLE transazioni ADD data_uscita_richiesta_stream DATETIME2;
ALTER TABLE transazioni ADD data_ingresso_risposta_stream DATETIME2;
ALTER TABLE transazioni ADD data_uscita_risposta_stream DATETIME2;

ALTER TABLE transazioni_sa ADD data_uscita_richiesta_stream DATETIME2;
ALTER TABLE transazioni_sa ADD data_ingresso_risposta_stream DATETIME2;
