ALTER TABLE tracce_ext_protocol_info MODIFY COLUMN value VARCHAR(4000);
ALTER TABLE tracce_ext_protocol_info ADD COLUMN ext_value TEXT;
CREATE INDEX TRACCE_EXT_SEARCH ON tracce_ext_protocol_info (name,value);
