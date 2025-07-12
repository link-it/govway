ALTER TABLE statistiche ADD COLUMN id BIGINT;
SET @id_val = 0;
UPDATE statistiche SET id = (@id_val := @id_val + 1);
ALTER TABLE statistiche MODIFY COLUMN id BIGINT NOT NULL;
ALTER TABLE statistiche ADD CONSTRAINT pk_statistiche PRIMARY KEY (id);
ALTER TABLE statistiche MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

