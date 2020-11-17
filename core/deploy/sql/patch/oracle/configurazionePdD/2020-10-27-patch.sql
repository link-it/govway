ALTER TABLE connettori MODIFY url VARCHAR2(4000);

ALTER TABLE connettori_custom DROP CONSTRAINT unique_connettori_custom_1;
ALTER TABLE connettori_custom ADD CONSTRAINT unique_connettori_custom_1 UNIQUE (id_connettore,name);
ALTER TABLE connettori_custom MODIFY value VARCHAR2(4000);

ALTER TABLE configurazione ADD cors_all_allow_methods VARCHAR2(255);
ALTER TABLE configurazione ADD cors_all_allow_headers VARCHAR2(255);
ALTER TABLE porte_delegate ADD cors_all_allow_methods VARCHAR2(255);
ALTER TABLE porte_delegate ADD cors_all_allow_headers VARCHAR2(255);
ALTER TABLE porte_applicative ADD cors_all_allow_methods VARCHAR2(255);
ALTER TABLE porte_applicative ADD cors_all_allow_headers VARCHAR2(255);
