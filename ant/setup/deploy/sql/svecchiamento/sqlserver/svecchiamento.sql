-- pulizia di 10 giorni di transazioni mantenendo sulle tabelle gli ultimi 30 giorni
-- DECLARE @clean int = 365;

DECLARE @clean int = 10

DECLARE @retention int = 30

DECLARE @start DATE = DATEADD(day, ( 0 - @retention - @clean), GETDATE())

DECLARE @end  DATE = DATEADD(day, ( 0 - @retention), GETDATE())

PRINT 'da: ' 
PRINT CAST (@start as datetime2)

PRINT 'a: ' 
PRINT CAST (@end as datetime2)

SELECT id INTO #transazioni_da_eliminare FROM transazioni WHERE (data_ingresso_richiesta >= CAST (@start as datetime2) AND data_ingresso_richiesta < CAST(@end as datetime2));


-- pulizia messaggi diagnostici
DELETE m FROM msgdiagnostici m JOIN #transazioni_da_eliminare te ON te.id=m.id_transazione;

-- pulizia notifiche
DELETE n FROM notifiche_eventi n JOIN #transazioni_da_eliminare te ON te.id=n.id_transazione;

-- pulizia tracce
SELECT tr.id INTO #tracce_da_eliminare  FROM tracce tr JOIN #transazioni_da_eliminare te ON te.id=tr.id_transazione;

DELETE trt  FROM tracce_trasmissioni trt JOIN #tracce_da_eliminare tre ON tre.id=trt.idtraccia;
DELETE trec FROM tracce_eccezioni trec JOIN #tracce_da_eliminare tre ON tre.id=trec.idtraccia;
DELETE trr FROM tracce_riscontri trr JOIN #tracce_da_eliminare tre ON tre.id=trr.idtraccia;
DELETE tra FROM tracce_allegati tra JOIN #tracce_da_eliminare tre ON tre.id=tra.idtraccia;
DELETE tepi FROM tracce_ext_protocol_info tepi JOIN #tracce_da_eliminare tre ON tre.id=tepi.idtraccia;
DELETE tr FROM tracce tr JOIN #transazioni_da_eliminare te ON te.id=tr.id_transazione;
DROP TABLE #tracce_da_eliminare;

-- pulizia dump applicativi
SELECT d.id INTO #dump_da_eliminare FROM dump_messaggi d JOIN #transazioni_da_eliminare te ON te.id=d.id_transazione;

DELETE dmh FROM dump_multipart_header dmh JOIN #dump_da_eliminare de ON de.id=dmh.id_messaggio;
DELETE dh FROM dump_header_trasporto dh JOIN #dump_da_eliminare de ON de.id=dh.id_messaggio;
DELETE dc FROM dump_contenuti dc JOIN #dump_da_eliminare de ON de.id=dc.id_messaggio;


SELECT da.id INTO #dump_allegati_da_eliminare FROM dump_allegati da JOIN #dump_da_eliminare de ON de.id=da.id_messaggio;

DELETE dha FROM dump_header_allegato dha JOIN #dump_allegati_da_eliminare dae ON dae.id=dha.id_allegato;
DROP TABLE #dump_allegati_da_eliminare;

DELETE da FROM dump_allegati da JOIN #dump_da_eliminare de ON de.id=da.id_messaggio;
DELETE d FROM dump_messaggi d JOIN #dump_da_eliminare de ON de.id=d.id;
DROP TABLE #dump_da_eliminare;

-- pulizia transazioni
DELETE tsa FROM transazioni_sa tsa JOIN #transazioni_da_eliminare te ON te.id=tsa.id_transazione;
DELETE t FROM transazioni t JOIN #transazioni_da_eliminare te ON te.id=t.id;
DROP TABLE #transazioni_da_eliminare;


PRINT 'finished'
