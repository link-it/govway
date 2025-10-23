-- pulizia di 10 giorni di transazioni mantenendo sulle tabelle gli ultimi 30 giorni
-- SET @clean = 365;

SET @clean = 10;

SET @retention = 30;

SET @start = DATE_SUB(CURDATE(), INTERVAL (@retention + @clean) DAY);

SET @end = DATE_SUB(CURDATE(), INTERVAL @retention DAY);

SELECT CONCAT('Eliminazione transazioni da: ', CAST(@start AS CHAR), ' a: ', CAST(@end AS CHAR)) AS info;

CREATE TEMPORARY TABLE transazioni_da_eliminare (
    id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO transazioni_da_eliminare
SELECT id
FROM transazioni
WHERE data_ingresso_richiesta >= CAST(@start AS DATETIME)
  AND data_ingresso_richiesta < CAST(@end AS DATETIME);

-- pulizia messaggi diagnostici
DELETE m FROM msgdiagnostici m
INNER JOIN transazioni_da_eliminare te ON te.id = m.id_transazione;

-- pulizia notifiche
DELETE n FROM notifiche_eventi n
INNER JOIN transazioni_da_eliminare te ON te.id = n.id_transazione;

-- pulizia tracce
CREATE TEMPORARY TABLE tracce_da_eliminare (
    id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO tracce_da_eliminare
SELECT tr.id
FROM tracce tr
INNER JOIN transazioni_da_eliminare te ON te.id = tr.id_transazione;

DELETE trt FROM tracce_trasmissioni trt
INNER JOIN tracce_da_eliminare tre ON tre.id = trt.idtraccia;

DELETE trec FROM tracce_eccezioni trec
INNER JOIN tracce_da_eliminare tre ON tre.id = trec.idtraccia;

DELETE trr FROM tracce_riscontri trr
INNER JOIN tracce_da_eliminare tre ON tre.id = trr.idtraccia;

DELETE tra FROM tracce_allegati tra
INNER JOIN tracce_da_eliminare tre ON tre.id = tra.idtraccia;

DELETE tepi FROM tracce_ext_protocol_info tepi
INNER JOIN tracce_da_eliminare tre ON tre.id = tepi.idtraccia;

DELETE tr FROM tracce tr
INNER JOIN transazioni_da_eliminare te ON te.id = tr.id_transazione;

DROP TEMPORARY TABLE tracce_da_eliminare;

-- pulizia dump applicativi
CREATE TEMPORARY TABLE dump_da_eliminare (
    id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO dump_da_eliminare
SELECT d.id
FROM dump_messaggi d
INNER JOIN transazioni_da_eliminare te ON te.id = d.id_transazione;

DELETE dmh FROM dump_multipart_header dmh
INNER JOIN dump_da_eliminare de ON de.id = dmh.id_messaggio;

DELETE dh FROM dump_header_trasporto dh
INNER JOIN dump_da_eliminare de ON de.id = dh.id_messaggio;

DELETE dc FROM dump_contenuti dc
INNER JOIN dump_da_eliminare de ON de.id = dc.id_messaggio;

CREATE TEMPORARY TABLE dump_allegati_da_eliminare (
    id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

INSERT INTO dump_allegati_da_eliminare
SELECT da.id
FROM dump_allegati da
INNER JOIN dump_da_eliminare de ON de.id = da.id_messaggio;

DELETE dha FROM dump_header_allegato dha
INNER JOIN dump_allegati_da_eliminare dae ON dae.id = dha.id_allegato;

DROP TEMPORARY TABLE dump_allegati_da_eliminare;

DELETE da FROM dump_allegati da
INNER JOIN dump_da_eliminare de ON de.id = da.id_messaggio;

DELETE d FROM dump_messaggi d
INNER JOIN dump_da_eliminare de ON de.id = d.id;

DROP TEMPORARY TABLE dump_da_eliminare;

-- pulizia transazioni
DELETE tsa FROM transazioni_sa tsa
INNER JOIN transazioni_da_eliminare te ON te.id = tsa.id_transazione;

DELETE t FROM transazioni t
INNER JOIN transazioni_da_eliminare te ON te.id = t.id;

DROP TEMPORARY TABLE transazioni_da_eliminare;

SELECT 'Pulizia completata con successo' AS status;
