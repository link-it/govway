-- pulizia di 10 giorni di transazioni mantenendo sulle tabelle gli ultimi 30 giorni
\set clean '\'10 days\''
\set retention '\'31 days\''
\set start 'CURRENT_DATE - interval :retention - interval :clean'
\set end 'CURRENT_DATE - interval :retention '

----- in caso si voglia eliminare tutte le transazioni precendenti ad una certa data usare questo approccio:
-- \set start min(data_ingresso_richiesta) from transazioni;
-- \set end  '\'<DATA> 23:59:59.999\'' 

CREATE TEMPORARY TABLE transazioni_da_eliminare AS (
SELECT id,data_ingresso_richiesta
FROM transazioni
WHERE (data_ingresso_richiesta >= :start AND data_ingresso_richiesta < :end )
);

-- pulizia messaggi diagnostici
DELETE FROM msgdiagnostici m USING transazioni_da_eliminare te WHERE te.id=m.id_transazione;

-- pulizia notifiche
DELETE FROM notifiche_eventi n USING transazioni_da_eliminare te WHERE te.id=n.id_transazione;

-- pulizia tracce
CREATE TEMPORARY TABLE tracce_da_eliminare  AS (
SELECT tr.id
FROM tracce tr
JOIN transazioni_da_eliminare te ON te.id=tr.id_transazione
);

DELETE FROM tracce_trasmissioni trt USING tracce_da_eliminare tre WHERE tre.id=trt.idtraccia;
DELETE FROM tracce_eccezioni trec USING tracce_da_eliminare tre WHERE tre.id=trec.idtraccia;
DELETE FROM tracce_riscontri trr USING tracce_da_eliminare tre WHERE tre.id=trr.idtraccia;
DELETE FROM tracce_allegati tra USING tracce_da_eliminare tre WHERE tre.id=tra.idtraccia;
DELETE FROM tracce_ext_protocol_info tepi USING tracce_da_eliminare tre WHERE tre.id=tepi.idtraccia;
DELETE FROM tracce tr USING transazioni_da_eliminare te WHERE te.id=tr.id_transazione;
DROP TABLE tracce_da_eliminare;

-- pulizia dump applicativi
CREATE TEMPORARY TABLE dump_da_eliminare AS (
SELECT d.id
FROM dump_messaggi d
JOIN transazioni_da_eliminare te ON te.id=d.id_transazione
);

DELETE FROM dump_multipart_header dmh USING dump_da_eliminare de WHERE de.id=dmh.id_messaggio;
DELETE FROM dump_header_trasporto dh USING dump_da_eliminare de WHERE de.id=dh.id_messaggio;
DELETE FROM dump_contenuti dc USING dump_da_eliminare de WHERE de.id=dc.id_messaggio;


CREATE TEMPORARY TABLE dump_allegati_da_eliminare AS (
SELECT da.id 
FROM dump_allegati da 
JOIN dump_da_eliminare de ON de.id=da.id_messaggio
);

DELETE FROM dump_header_allegato dha USING dump_allegati_da_eliminare dae WHERE dae.id=dha.id_allegato;
DROP TABLE dump_allegati_da_eliminare;
DELETE FROM dump_allegati da USING dump_da_eliminare de WHERE de.id=da.id_messaggio;

DELETE FROM dump_messaggi d USING dump_da_eliminare de WHERE de.id=d.id;

DROP TABLE dump_da_eliminare;

-- pulizia transazioni
DELETE FROM transazioni_sa tsa USING transazioni_da_eliminare te WHERE te.id=tsa.id_transazione;
DELETE FROM transazioni t USING transazioni_da_eliminare te WHERE te.id=t.id;
DROP TABLE transazioni_da_eliminare;


