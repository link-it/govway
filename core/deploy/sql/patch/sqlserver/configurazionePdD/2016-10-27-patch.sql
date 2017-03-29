DROP INDEX index_soggetti_1 ON soggetti;
CREATE UNIQUE INDEX index_soggetti_1 ON soggetti (nome_soggetto,tipo_soggetto);

DROP INDEX index_servizi_applicativi_1 ON servizi_applicativi;
CREATE UNIQUE INDEX index_servizi_applicativi_1 ON servizi_applicativi (nome,id_soggetto);

DROP INDEX index_porte_delegate_1 ON porte_delegate;
CREATE UNIQUE INDEX index_porte_delegate_1 ON porte_delegate (id_soggetto,nome_porta);

DROP INDEX index_porte_applicative_1 ON porte_applicative;
CREATE UNIQUE INDEX index_porte_applicative_1 ON porte_applicative (id_soggetto,nome_porta);

DROP INDEX index_connettori_1 ON connettori;
CREATE UNIQUE INDEX index_connettori_1 ON connettori (nome_connettore);
DROP INDEX index_connettori_properties_1 ON connettori_properties;
CREATE UNIQUE INDEX index_connettori_properties_1 ON connettori_properties (nome_connettore);

DROP INDEX index_servizi_pdd_1 ON servizi_pdd;
CREATE UNIQUE INDEX index_servizi_pdd_1 ON servizi_pdd (componente);
DROP INDEX index_pdd_sys_props_1 ON pdd_sys_props;
CREATE UNIQUE INDEX index_pdd_sys_props_1 ON pdd_sys_props (nome,valore);

