DROP INDEX index_nodi_runtime_1;
ALTER TABLE nodi_runtime DROP CONSTRAINT unique_nodi_runtime_1;
ALTER TABLE nodi_runtime ALTER COLUMN hostname SET DATA TYPE VARCHAR(255);
CALL SYSPROC.ADMIN_CMD ('REORG TABLE nodi_runtime') ;
ALTER TABLE nodi_runtime ADD CONSTRAINT unique_nodi_runtime_1 UNIQUE (hostname);
CREATE UNIQUE INDEX index_nodi_runtime_1 ON nodi_runtime (hostname);

DROP INDEX index_nodi_runtime_2;
ALTER TABLE nodi_runtime DROP CONSTRAINT unique_nodi_runtime_2;
ALTER TABLE nodi_runtime ALTER COLUMN gruppo SET DATA TYPE VARCHAR(255);
CALL SYSPROC.ADMIN_CMD ('REORG TABLE nodi_runtime') ;
ALTER TABLE nodi_runtime ADD CONSTRAINT unique_nodi_runtime_2 UNIQUE (gruppo,id_numerico);
CREATE UNIQUE INDEX index_nodi_runtime_2 ON nodi_runtime (gruppo,id_numerico);
