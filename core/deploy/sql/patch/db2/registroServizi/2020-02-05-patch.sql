CREATE INDEX INDEX_AC_SOG ON accordi_coop_partecipanti (id_soggetto);
CREATE INDEX INDEX_SERV_ACC ON servizi (id_accordo);
CREATE INDEX INDEX_SERV_SOG ON servizi (id_soggetto);
CREATE INDEX INDEX_SERV_FRU_SOG ON servizi_fruitori (id_soggetto);
CREATE INDEX INDEX_AC_SC_SERV ON acc_serv_componenti (id_servizio_componente);

CREATE INDEX idx_plug_ser_com_1 ON plugins_servizi_comp (id_plugin);
CREATE INDEX idx_plug_filtro_com_1 ON plugins_filtro_comp (id_plugin);
