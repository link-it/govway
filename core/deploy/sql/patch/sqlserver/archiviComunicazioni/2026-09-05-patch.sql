ALTER TABLE credenziale_mittente DROP CONSTRAINT unique_credenziale_mittente_1;

UPDATE credenziale_mittente SET credenziale = LEFT(credenziale, 1680) WHERE DATALENGTH(credenziale) > 1680;

ALTER TABLE credenziale_mittente ALTER COLUMN credenziale VARCHAR(1680) NOT NULL;

ALTER TABLE credenziale_mittente ADD CONSTRAINT unique_credenziale_mittente_1 UNIQUE (tipo,credenziale);
