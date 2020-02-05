CREATE INDEX idx_gest_err_trasporto_1 ON gestione_errore_trasporto (id_gestione_errore);
CREATE INDEX idx_gest_err_soap_1 ON gestione_errore_soap (id_gestione_errore);

CREATE INDEX idx_conn_custom_1 ON connettori_custom (id_connettore);

CREATE INDEX index_porte_applicative_2 ON porte_applicative (id_soggetto);
CREATE INDEX INDEX_PA_CORR_REQ ON pa_correlazione (id_porta);
CREATE INDEX INDEX_PA_CORR_RES ON pa_correlazione_risposta (id_porta);
CREATE INDEX INDEX_PA_CACHE ON pa_cache_regole (id_porta);

CREATE INDEX index_porte_delegate_2 ON porte_delegate (id_soggetto);
CREATE INDEX INDEX_PD_CORR_REQ ON pd_correlazione (id_porta);
CREATE INDEX INDEX_PD_CORR_RES ON pd_correlazione_risposta (id_porta);
CREATE INDEX INDEX_PD_CACHE ON pd_cache_regole (id_porta);

CREATE INDEX index_servizi_applicativi_2 ON servizi_applicativi (id_soggetto);

CREATE INDEX index_generic_property_1 ON generic_property (id_props);
