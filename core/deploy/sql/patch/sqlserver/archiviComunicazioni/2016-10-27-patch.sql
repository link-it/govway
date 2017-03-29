CREATE INDEX TRACCE_SEARCH_ID_APPL ON tracce (correlazione_applicativa);
CREATE INDEX TRACCE_SEARCH_ID_APPL_RISP ON tracce (correlazione_risposta);

CREATE INDEX TRACCE_SEARCH_GDO ON tracce (gdo DESC);

DROP INDEX MSG_DIAG_GDO ON msgdiagnostici;
CREATE INDEX MSG_DIAG_GDO ON msgdiagnostici (gdo DESC);
 
DROP INDEX MSG_CORR_GDO ON msgdiag_correlazione;
CREATE INDEX MSG_CORR_GDO ON msgdiag_correlazione (gdo DESC);

CREATE INDEX MSG_CORR_APP_RISP ON msgdiag_correlazione (id_correlazione_risposta,delegata);

