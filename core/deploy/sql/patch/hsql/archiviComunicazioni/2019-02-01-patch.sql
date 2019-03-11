ALTER TABLE transazioni ADD COLUMN temp VARCHAR(65535);
UPDATE transazioni SET temp=credenziali;
ALTER TABLE transazioni DROP COLUMN credenziali;
ALTER TABLE transazioni ALTER COLUMN temp RENAME TO credenziali;
