ALTER TABLE allarmi ADD COLUMN dettaglio_acknowledged LONGTEXT;
ALTER TABLE allarmi MODIFY COLUMN stato_dettaglio LONGTEXT;

