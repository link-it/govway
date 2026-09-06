ALTER TABLE connettori DROP CONSTRAINT unique_connettori_1;
ALTER TABLE connettori ALTER COLUMN nome_connettore VARCHAR(1024) NOT NULL;
ALTER TABLE connettori ADD CONSTRAINT unique_connettori_1 UNIQUE (nome_connettore);
