-- ALTER TABLE transazioni MODIFY credenziali CLOB;
ALTER TABLE transazioni ADD temp CLOB;
UPDATE transazioni SET temp=credenziali;
ALTER TABLE transazioni DROP COLUMN credenziali;
ALTER TABLE transazioni RENAME COLUMN temp TO credenziali;
