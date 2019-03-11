ALTER TABLE transazioni ADD temp VARCHAR(max);
UPDATE transazioni SET temp=credenziali;
ALTER TABLE transazioni DROP COLUMN credenziali;
EXEC sp_rename  'transazioni.temp' , 'credenziali' , 'COLUMN'
