CREATE SEQUENCE seq_statistiche AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) NO CYCLE;
ALTER TABLE statistiche ADD COLUMN id BIGINT;
UPDATE statistiche SET id = NEXT VALUE FOR seq_statistiche;
ALTER TABLE statistiche ALTER COLUMN id SET NOT NULL;
ALTER TABLE statistiche ADD CONSTRAINT pk_statistiche PRIMARY KEY (id);
CREATE TABLE statistiche_init_seq (id BIGINT);
INSERT INTO statistiche_init_seq VALUES (NEXT VALUE FOR seq_statistiche);

CREATE SEQUENCE seq_transazioni_info AS BIGINT START WITH 1 INCREMENT BY 1 ; -- (Scommentare in hsql 2.x) CYCLE;
ALTER TABLE transazioni_info ADD COLUMN id BIGINT;
UPDATE transazioni_info SET id = NEXT VALUE FOR seq_transazioni_info;
ALTER TABLE transazioni_info ALTER COLUMN id SET NOT NULL;
ALTER TABLE transazioni_info ADD CONSTRAINT pk_transazioni_info PRIMARY KEY (id);
CREATE TABLE transazioni_info_init_seq (id BIGINT);
INSERT INTO transazioni_info_init_seq VALUES (NEXT VALUE FOR seq_transazioni_info);

