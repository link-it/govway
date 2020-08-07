-- ALTER TABLE transazioni ALTER COLUMN credenziali SET DATA TYPE CLOB;
ALTER TABLE transazioni ADD temp CLOB;
UPDATE transazioni SET temp=credenziali;
ALTER TABLE transazioni DROP COLUMN credenziali;
ALTER TABLE transazioni RENAME COLUMN temp TO credenziali; 
CALL SYSPROC.ADMIN_CMD ('REORG TABLE transazioni') ;
