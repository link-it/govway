DROP INDEX index_soggetti_1;
CREATE UNIQUE INDEX index_soggetti_1 ON soggetti (nome_soggetto,tipo_soggetto);
DROP INDEX index_soggetti_2;
CREATE UNIQUE INDEX index_soggetti_2 ON soggetti (codice_ipa);

DROP INDEX index_pdd_1;
CREATE UNIQUE INDEX index_pdd_1 ON pdd (nome);

DROP INDEX index_connettori_1;
CREATE UNIQUE INDEX index_connettori_1 ON connettori (nome_connettore);
DROP INDEX index_connettori_properties_1;
CREATE UNIQUE INDEX index_connettori_properties_1 ON connettori_properties (nome_connettore);

DROP INDEX index_documenti_1;
CREATE UNIQUE INDEX index_documenti_1 ON documenti (ruolo,tipo,nome,id_proprietario,tipo_proprietario);
DROP INDEX index_accordi_1;
CREATE UNIQUE INDEX index_accordi_1 ON accordi (nome,id_referente,versione);
DROP INDEX index_accordi_azioni_1;
CREATE UNIQUE INDEX index_accordi_azioni_1 ON accordi_azioni (id_accordo,nome);
DROP INDEX index_port_type_1;
CREATE UNIQUE INDEX index_port_type_1 ON port_type (id_accordo,nome);
DROP INDEX index_port_type_azioni_1;
CREATE UNIQUE INDEX index_port_type_azioni_1 ON port_type_azioni (id_port_type,nome);
DROP INDEX index_accordi_cooperazione_1;
CREATE UNIQUE INDEX index_accordi_cooperazione_1 ON accordi_cooperazione (nome,versione);
DROP INDEX index_servizi_1;
CREATE UNIQUE INDEX index_servizi_1 ON servizi (nome_servizio,tipo_servizio,id_soggetto);
DROP INDEX index_servizi_fruitori_1;
CREATE UNIQUE INDEX index_servizi_fruitori_1 ON servizi_fruitori (id_servizio,id_soggetto);
DROP INDEX index_servizi_azioni_1;
CREATE UNIQUE INDEX index_servizi_azioni_1 ON servizi_azioni (nome_azione,id_servizio);
DROP INDEX index_acc_serv_composti_1;
CREATE UNIQUE INDEX index_acc_serv_composti_1 ON acc_serv_composti (id_accordo);
 
