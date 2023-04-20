-- pulizia di 10 giorni di transazioni mantenendo sulle tabelle gli ultimi 30 giorni
-- DEFINE clean= 'INTERVAL ''1'' year'
DEFINE clean= 'INTERVAL ''10'' day'
DEFINE retention= 'INTERVAL ''30'' day'


CREATE TABLE transazioni_da_eliminare AS (
select id from transazioni where 1=0
);

declare
    inizio DATE := CURRENT_DATE - &&retention - &&clean;
    fine DATE := CURRENT_DATE - &&retention;
begin
    INSERT INTO transazioni_da_eliminare SELECT id FROM transazioni WHERE (data_ingresso_richiesta >= CAST(inizio as TIMESTAMP) AND data_ingresso_richiesta < CAST (fine as TIMESTAMP));
end;
/


-- pulizia messaggi diagnostici
DELETE msgdiagnostici m WHERE m.id_transazione in (select te.id from transazioni_da_eliminare te);

-- pulizia notifiche
DELETE notifiche_eventi n WHERE n.id_transazione in (select te.id from transazioni_da_eliminare te);

-- pulizia tracce
CREATE TABLE tracce_da_eliminare  AS (
SELECT tr.id
FROM tracce tr where 1=0
);
INSERT INTO tracce_da_eliminare SELECT tr.id FROM tracce tr JOIN transazioni_da_eliminare te ON te.id=tr.id_transazione;
DELETE tracce_trasmissioni trt WHERE trt.idtraccia in (SELECT tre.id FROM tracce_da_eliminare tre);
DELETE tracce_eccezioni trec WHERE trec.idtraccia in (SELECT tre.id FROM tracce_da_eliminare tre);  
DELETE tracce_riscontri trr WHERE trr.idtraccia in (SELECT tre.id FROM tracce_da_eliminare tre);
DELETE tracce_allegati tra WHERE tra.idtraccia in (SELECT tre.id FROM tracce_da_eliminare tre);
DELETE tracce_ext_protocol_info tepi WHERE tepi.idtraccia in (SELECT tre.id FROM tracce_da_eliminare tre);
DELETE tracce tr WHERE tr.id in (SELECT tre.id FROM tracce_da_eliminare tre);
DROP TABLE tracce_da_eliminare;

-- pulizia dump applicativi
CREATE TABLE dump_da_eliminare AS (
SELECT d.id
FROM dump_messaggi d where 1=0
);
INSERT INTO dump_da_eliminare SELECT d.id FROM dump_messaggi d JOIN transazioni_da_eliminare te ON te.id=d.id_transazione;

DELETE dump_multipart_header dmh WHERE dmh.id_messaggio in (SELECT de.id FROM dump_da_eliminare de);
DELETE dump_header_trasporto dh WHERE dh.id_messaggio in (SELECT de.id FROM dump_da_eliminare de);
DELETE dump_contenuti dc WHERE dc.id_messaggio in (SELECT de.id FROM dump_da_eliminare de);

CREATE TABLE dump_allegati_da_eliminare AS (
SELECT da.id
FROM dump_allegati da where 1=0
);
INSERT INTO dump_allegati_da_eliminare SELECT da.id FROM dump_allegati da JOIN dump_da_eliminare de ON de.id=da.id_messaggio;

DELETE dump_header_allegato dha WHERE dha.id_allegato in (SELECT dae.id FROM dump_allegati_da_eliminare dae);
DROP TABLE dump_allegati_da_eliminare;

DELETE dump_allegati da WHERE da.id_messaggio in (SELECT de.id FROM dump_da_eliminare de);

DELETE dump_messaggi d WHERE d.id in (SELECT de.id FROM dump_da_eliminare de);

DROP TABLE dump_da_eliminare;

-- pulizia transazioni
DELETE transazioni_sa tsa WHERE tsa.id in (SELECT te.id FROM transazioni_da_eliminare te);
DELETE transazioni t WHERE t.id in (SELECT te.id FROM transazioni_da_eliminare te);
DROP TABLE transazioni_da_eliminare;

