CREATE SEQUENCE seq_statistiche start 1 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1 NO CYCLE;

ALTER TABLE statistiche ADD COLUMN id BIGINT;
UPDATE statistiche SET id = nextval('seq_statistiche');
ALTER TABLE statistiche ALTER COLUMN id SET NOT NULL;
ALTER TABLE statistiche ALTER COLUMN id SET DEFAULT nextval('seq_statistiche');
ALTER TABLE statistiche ADD CONSTRAINT pk_statistiche PRIMARY KEY (id);
