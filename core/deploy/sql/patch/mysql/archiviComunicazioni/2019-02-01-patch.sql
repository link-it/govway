-- ALTER TABLE transazioni ADD COLUMN temp TEXT;
-- UPDATE transazioni SET temp=credenziali;
-- ALTER TABLE transazioni DROP COLUMN credenziali;
-- ALTER TABLE transazioni CHANGE COLUMN temp credenziali TEXT;
ALTER TABLE transazioni MODIFY COLUMN credenziali TEXT;
