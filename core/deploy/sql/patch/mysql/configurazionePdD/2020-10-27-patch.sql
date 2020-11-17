ALTER TABLE connettori MODIFY COLUMN url VARCHAR(4000);

ALTER TABLE connettori_custom DROP INDEX unique_connettori_custom_1;
ALTER TABLE connettori_custom ADD CONSTRAINT unique_connettori_custom_1 UNIQUE (id_connettore,name);
ALTER TABLE connettori_custom MODIFY COLUMN value VARCHAR(4000);

ALTER TABLE configurazione ADD COLUMN cors_all_allow_methods VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN cors_all_allow_headers VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN cors_all_allow_methods VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN cors_all_allow_headers VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN cors_all_allow_methods VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN cors_all_allow_headers VARCHAR(255);
