ALTER TABLE porte_applicative_sa ADD connettore_coda VARCHAR2(10) DEFAULT 'DEFAULT';
ALTER TABLE porte_applicative_sa ADD connettore_priorita VARCHAR2(10) DEFAULT 'DEFAULT';
ALTER TABLE porte_applicative_sa ADD connettore_max_priorita NUMBER DEFAULT 0;

CREATE INDEX INDEX_PA_SA_CODA ON porte_applicative_sa (connettore_coda,connettore_priorita,id_servizio_applicativo);
CREATE INDEX INDEX_PA_SA_CODA_MAX ON porte_applicative_sa (connettore_coda,connettore_max_priorita,id_servizio_applicativo);
