CREATE INDEX index_dump_config_1 ON dump_config (proprietario);

ALTER TABLE dump_config_regola ADD payload VARCHAR2(255) ;
UPDATE dump_config_regola set payload=body;
ALTER TABLE dump_config_regola MODIFY payload NOT NULL;

ALTER TABLE dump_config_regola ADD payload_parsing VARCHAR2(255) ;
UPDATE dump_config_regola set payload_parsing='disabilitato';
ALTER TABLE dump_config_regola MODIFY payload_parsing NOT NULL;

