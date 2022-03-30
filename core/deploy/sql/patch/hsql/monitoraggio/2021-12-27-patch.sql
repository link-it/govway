ALTER TABLE allarmi ADD COLUMN dettaglio_acknowledged LONGVARCHAR;
ALTER TABLE allarmi ALTER COLUMN  stato_dettaglio LONGVARCHAR;

