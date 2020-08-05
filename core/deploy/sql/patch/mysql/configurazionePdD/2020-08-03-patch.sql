ALTER TABLE porte_applicative_sa ADD COLUMN connettore_coda VARCHAR(10) DEFAULT 'DEFAULT';
ALTER TABLE porte_applicative_sa ADD COLUMN connettore_priorita VARCHAR(10) DEFAULT 'DEFAULT';
ALTER TABLE porte_applicative_sa ADD COLUMN connettore_max_priorita INT DEFAULT 0;

CREATE INDEX INDEX_PA_SA_CODA ON porte_applicative_sa (connettore_coda,connettore_priorita,id_servizio_applicativo);
CREATE INDEX INDEX_PA_SA_CODA_MAX ON porte_applicative_sa (connettore_coda,connettore_max_priorita,id_servizio_applicativo);
