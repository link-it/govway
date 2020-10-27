ALTER TABLE connettori ALTER COLUMN url TYPE VARCHAR(4000);
ALTER TABLE connettori_custom ALTER COLUMN value TYPE VARCHAR(4000);

ALTER TABLE configurazione ADD COLUMN cors_all_allow_methods VARCHAR(255);
ALTER TABLE configurazione ADD COLUMN cors_all_allow_headers VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN cors_all_allow_methods VARCHAR(255);
ALTER TABLE porte_delegate ADD COLUMN cors_all_allow_headers VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN cors_all_allow_methods VARCHAR(255);
ALTER TABLE porte_applicative ADD COLUMN cors_all_allow_headers VARCHAR(255);
