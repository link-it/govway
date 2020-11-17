ALTER TABLE tracce_ext_protocol_info ALTER COLUMN ext_value VARCHAR(2800);
ALTER TABLE tracce_ext_protocol_info ADD COLUMN ext_value VARCHAR(65535);
CREATE INDEX TRACCE_EXT_SEARCH ON tracce_ext_protocol_info (name,value);
