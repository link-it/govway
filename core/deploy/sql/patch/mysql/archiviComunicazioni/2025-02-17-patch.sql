ALTER TABLE transazioni_info ADD COLUMN id BIGINT;
SET @id_val = 0;
UPDATE transazioni_info SET id = (@id_val := @id_val + 1);
ALTER TABLE transazioni_info MODIFY COLUMN id BIGINT NOT NULL;
ALTER TABLE transazioni_info ADD CONSTRAINT pk_transazioni_info PRIMARY KEY (id);
ALTER TABLE transazioni_info MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

