-- Adeguamento al limite InnoDB sul record size: VARCHAR(255) -> VARCHAR(256)
--
-- InnoDB con pagine da 16K rifiuta record oltre 8126 byte. In latin1 una colonna
-- VARCHAR(255) vale 255 byte: resta appena sotto la soglia oltre la quale una
-- colonna variabile puo' essere memorizzata su pagina di overflow, quindi conta
-- 256 byte inline nel caso peggiore. Dichiarata VARCHAR(256) diventa candidabile
-- all'overflow e conta 20 byte. Nessun costo in spazio, nessun impatto applicativo:
-- il database accetta un carattere in piu' di quanto l'applicazione scriva.
--
-- Il controllo bloccante è emerso su MySQL 8.4.9
--
-- ATTENZIONE
--   * Il passaggio 255 -> 256 cambia i byte di lunghezza da 1 a 2, quindi non e'
--     eseguibile in-place: richiede ALGORITHM=COPY. La tabella viene ricostruita
--     e le scritture sono bloccate per tutta la durata (LOCK=NONE non e' ammesso:
--     "COPY algorithm requires a lock"). Prevedere una finestra di manutenzione.
--   * Senza questo adeguamento, su MySQL >= 8.4.9 queste tabelle non accettano
--     piu' alcuna ADD COLUMN ne' DROP COLUMN, di nessun tipo.


-- tracce: 35 colonne
ALTER TABLE tracce
	MODIFY pdd_codice VARCHAR(256) NOT NULL,
	MODIFY pdd_tipo_soggetto VARCHAR(256) NOT NULL,
	MODIFY pdd_nome_soggetto VARCHAR(256) NOT NULL,
	MODIFY pdd_ruolo VARCHAR(256) NOT NULL,
	MODIFY tipo_messaggio VARCHAR(256) NOT NULL,
	MODIFY esito_elaborazione VARCHAR(256) NOT NULL,
	MODIFY mittente VARCHAR(256),
	MODIFY tipo_mittente VARCHAR(256),
	MODIFY idporta_mittente VARCHAR(256),
	MODIFY indirizzo_mittente VARCHAR(256),
	MODIFY destinatario VARCHAR(256),
	MODIFY tipo_destinatario VARCHAR(256),
	MODIFY idporta_destinatario VARCHAR(256),
	MODIFY indirizzo_destinatario VARCHAR(256),
	MODIFY profilo_collaborazione VARCHAR(256),
	MODIFY profilo_collaborazione_meta VARCHAR(256),
	MODIFY servizio_correlato VARCHAR(256),
	MODIFY tipo_servizio_correlato VARCHAR(256),
	MODIFY collaborazione VARCHAR(256),
	MODIFY servizio VARCHAR(256),
	MODIFY tipo_servizio VARCHAR(256),
	MODIFY azione VARCHAR(256),
	MODIFY id_messaggio VARCHAR(256),
	MODIFY tipo_ora_reg VARCHAR(256),
	MODIFY tipo_ora_reg_meta VARCHAR(256),
	MODIFY rif_messaggio VARCHAR(256),
	MODIFY inoltro VARCHAR(256),
	MODIFY inoltro_meta VARCHAR(256),
	MODIFY location VARCHAR(256),
	MODIFY correlazione_applicativa VARCHAR(256),
	MODIFY correlazione_risposta VARCHAR(256),
	MODIFY sa_fruitore VARCHAR(256),
	MODIFY sa_erogatore VARCHAR(256),
	MODIFY protocollo VARCHAR(256) NOT NULL,
	MODIFY id_transazione VARCHAR(256) NOT NULL,
	ALGORITHM=COPY, LOCK=SHARED;
