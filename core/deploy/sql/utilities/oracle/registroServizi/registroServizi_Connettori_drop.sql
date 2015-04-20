-- Gli indici vengono eliminati automaticamente una volta eliminata la tabella
DROP TRIGGER trg_connettori_properties;
DROP TRIGGER trg_connettori_custom;
DROP TRIGGER trg_connettori;
DROP TABLE connettori_properties;
DROP TABLE connettori_custom;
DROP TABLE connettori;
DROP SEQUENCE seq_connettori_properties;
DROP SEQUENCE seq_connettori_custom;
DROP SEQUENCE seq_connettori;
