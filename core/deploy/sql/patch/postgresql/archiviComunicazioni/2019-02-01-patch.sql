-- ALTER TABLE transazioni ADD COLUMN temp TEXT;
-- UPDATE transazioni SET temp=credenziali;
-- ALTER TABLE transazioni DROP COLUMN credenziali;
-- ALTER TABLE transazioni RENAME temp TO credenziali; 
ALTER TABLE transazioni ALTER COLUMN credenziali TYPE TEXT;
