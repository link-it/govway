ALTER TABLE nodi_runtime MODIFY hostname VARCHAR2(255);

ALTER TABLE nodi_runtime MODIFY gruppo VARCHAR2(255);

ALTER TABLE servizi_applicativi ADD CONSTRAINT fk_servizi_applicativi_3 FOREIGN KEY (id_connettore_risp) REFERENCES connettori(id);
