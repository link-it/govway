ALTER TABLE msgdiag_correlazione ALTER COLUMN tipo_fruitore DROP NOT NULL;
ALTER TABLE msgdiag_correlazione ALTER COLUMN fruitore DROP NOT NULL;
CALL SYSPROC.ADMIN_CMD ('REORG TABLE msgdiag_correlazione') ; 
