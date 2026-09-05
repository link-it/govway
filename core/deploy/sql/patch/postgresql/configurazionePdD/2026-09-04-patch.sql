ALTER TABLE nodi_runtime ALTER COLUMN hostname TYPE VARCHAR(255);

ALTER TABLE nodi_runtime ALTER COLUMN gruppo TYPE VARCHAR(255);

ALTER TABLE servizi_applicativi ADD CONSTRAINT fk_servizi_applicativi_3 FOREIGN KEY (id_connettore_risp) REFERENCES connettori(id);
