-- ALTER TABLE tracce_ext_protocol_info ALTER COLUMN value SET DATA TYPE VARCHAR(4000);
ALTER TABLE tracce_ext_protocol_info ADD temp VARCHAR(4000); 
UPDATE tracce_ext_protocol_info SET temp=value; 
ALTER TABLE tracce_ext_protocol_info DROP COLUMN value ;
CALL SYSPROC.ADMIN_CMD ('REORG TABLE tracce_ext_protocol_info') ;
ALTER TABLE tracce_ext_protocol_info RENAME COLUMN temp TO value;

ALTER TABLE tracce_ext_protocol_info ADD ext_value CLOB;
CREATE INDEX TRACCE_EXT_SEARCH ON tracce_ext_protocol_info (name,value);
