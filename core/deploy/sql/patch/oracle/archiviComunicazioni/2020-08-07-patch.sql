-- ALTER TABLE tracce_ext_protocol_info MODIFY value VARCHAR2(2800);
ALTER TABLE tracce_ext_protocol_info ADD temp VARCHAR2(2800);
UPDATE tracce_ext_protocol_info SET temp=value;
ALTER TABLE tracce_ext_protocol_info DROP COLUMN value;
ALTER TABLE tracce_ext_protocol_info RENAME COLUMN temp TO value;

ALTER TABLE tracce_ext_protocol_info ADD ext_value CLOB;
CREATE INDEX TRACCE_EXT_SEARCH ON tracce_ext_protocol_info (name,value);
