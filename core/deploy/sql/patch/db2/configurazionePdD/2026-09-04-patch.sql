ALTER TABLE nodi_runtime DROP CONSTRAINT unique_nodi_runtime_1;
ALTER TABLE nodi_runtime ALTER COLUMN hostname SET DATA TYPE VARCHAR(255);
CALL SYSPROC.ADMIN_CMD ('REORG TABLE nodi_runtime') ;
ALTER TABLE nodi_runtime ADD CONSTRAINT unique_nodi_runtime_1 UNIQUE (hostname);

ALTER TABLE nodi_runtime DROP CONSTRAINT unique_nodi_runtime_2;
ALTER TABLE nodi_runtime ALTER COLUMN gruppo SET DATA TYPE VARCHAR(255);
CALL SYSPROC.ADMIN_CMD ('REORG TABLE nodi_runtime') ;
ALTER TABLE nodi_runtime ADD CONSTRAINT unique_nodi_runtime_2 UNIQUE (gruppo,id_numerico);

RENAME INDEX idx_rt_prop_policy_2 TO idx_rt_prop_policy_1;
RENAME INDEX idx_cong_att_policy_2 TO idx_cong_att_policy_1;
RENAME INDEX index_porte_applicative_2 TO index_porte_applicative_1;
RENAME INDEX index_porte_applicative_3 TO index_porte_applicative_2;
RENAME INDEX index_porte_delegate_2 TO index_porte_delegate_1;
RENAME INDEX index_porte_delegate_3 TO index_porte_delegate_2;
RENAME INDEX index_servizi_applicativi_2 TO index_servizi_applicativi_1;

ALTER TABLE servizi_applicativi ADD CONSTRAINT fk_servizi_applicativi_3 FOREIGN KEY (id_connettore_risp) REFERENCES connettori(id);
