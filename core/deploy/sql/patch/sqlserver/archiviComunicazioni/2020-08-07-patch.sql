ALTER TABLE tracce_ext_protocol_info ALTER COLUMN value VARCHAR(2800);
ALTER TABLE tracce_ext_protocol_info ADD ext_value VARCHAR(max);
CREATE INDEX TRACCE_EXT_SEARCH ON tracce_ext_protocol_info (name,value);
