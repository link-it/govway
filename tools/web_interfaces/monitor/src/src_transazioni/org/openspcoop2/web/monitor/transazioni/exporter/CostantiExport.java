/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.web.monitor.transazioni.exporter;

/**
 * CostantiExport
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class CostantiExport {
	
	
	public static final String PATTERN_DATA_TRANSAZIONI = "yyyy-MM-dd HH:mm:ss.SSSXXX";
	public static final String SEPARATORE_TIPO_NOME = "/";
	public static final String WHITE_SPACE = " ";
	public static final String INTEGRATION_MANAGER_LABEL_CON_PARANTISI = "[IM]";
	public static final String DUPLICATA_VALUE = "Duplicata";
	public static final String EMPTY_STRING = "";
	public static final String N_D = "N.D.";
	
	public static final String RUOLO_INTEGRATION_MANAGER_LABEL = "I.M.";
	public static final String RUOLO_FUIZIONE_LABEL = "Fruizione";
	public static final String RUOLO_EROGAZIONE_LABEL = "Erogazione";
	public static final String RUOLO_ROUTER_LABEL = "Router";
	
	public static final String TRANSAZIONI_EXPORTER_SERVLET_NAME = "transazioniexporter";
	public static final String TRANSAZIONI_CSV_EXPORTER_SERVLET_NAME = "transazionicsvexporter";
	
	public static final String FORMATO_XLS_VALUE = "XLS";
	public static final String FORMATO_CSV_VALUE = "CSV";
	public static final String FORMATO_ZIP_VALUE = "ZIP";
	
	public static final String ESPORTAZIONI_VALUE_CONTENUTI = "contenuti";
	public static final String ESPORTAZIONI_VALUE_DIAGNOSTICI = "diagnostici";
	public static final String ESPORTAZIONI_VALUE_TRACCE = "tracce";
	
	public static final String ESPORTAZIONI_LABEL_CONTENUTI = "Contenuti";
	public static final String ESPORTAZIONI_LABEL_DIAGNOSTICI = "Diagnostici";
	public static final String ESPORTAZIONI_LABEL_TRACCE = "Tracce";
	
	public static final String COLONNE_VALUE_PERSONALIZZA = "Personalizza";
	public static final String COLONNE_VALUE_TUTTE = "Tutte";
	public static final String COLONNE_VALUE_VISUALIZZATE_NELLO_STORICO = "Visualizzate nello Storico";
	
	public static final String PARAMETER_EXPORTER = "exporter";
	public static final String PARAMETER_IDS = "ids";
	public static final String PARAMETER_IS_ALL = "isAll";
	public static final String PARAMETER_FORMATO_EXPORT = "fEx";
	public static final String PARAMETER_ID_SELEZIONI = "idSelez";
	
	public static final String PARAMETER_ID_TRANSAZIONI_ORIGINALI = "idTransazioniOriginali";
	public static final String PARAMETER_IS_ALL_ORIGINALE = "isAllOriginale";
	public static final String PARAMETER_TIPI_EXPORT_ORIGINALI = "exporterOriginali";
	public static final String PARAMETER_FORMATO_EXPORT_ORIGINALE = "fExOriginali";
	public static final String PARAMETER_ID_SELEZIONI_ORIGINALI = "idSelezOriginali";
	public static final String PARAMETER_LISTA_SELEZIONI_ORIGINALI = "lstSelezOriginali";
	
	
	/** Chiavi delle colonne */

	public static final String KEY_COL_ID_TRANSAZIONE = "colIdTransazione";
	public static final String KEY_COL_STATO = "colStato";
	public static final String KEY_COL_RUOLO_TRANSAZIONE = "colRuoloTransazione";
	public static final String KEY_COL_ESITO = "colEsito";
	public static final String KEY_COL_ESITO_CONTESTO = "colEsitoContesto";
	public static final String KEY_COL_PROTOCOLLO = "colProtocollo";
	public static final String KEY_COL_DATA_ACCETTAZIONE_RICHIESTA = "colDataAccettazioneRich";
	public static final String KEY_COL_DATA_INGRESSO_RICHIESTA = "colDataIngRich";
	public static final String KEY_COL_DATA_USCITA_RICHIESTA = "colDataUscitaRich";
	public static final String KEY_COL_DATA_ACCETTAZIONE_RISPOSTA = "colDataAccettazioneRisposta";
	public static final String KEY_COL_DATA_INGRESSO_RISPOSTA = "colDataInRisposta";
	public static final String KEY_COL_DATA_USCITA_RISPOSTA = "colDataOutRisposta";
	public static final String KEY_COL_RICHIESTA_INGRESSO_BYTES = "colRichIngrBytes";
	public static final String KEY_COL_RICHIESTA_USCITA_BYTES = "colRichOutBytes";
	public static final String KEY_COL_RISPOSTA_INGRESSO_BYTES = "colRispIngrBytes";
	public static final String KEY_COL_RISPOSTA_USCITA_BYTES = "colRispOutBytes";
	public static final String KEY_COL_PDD_CODICE = "colPdd";
	public static final String KEY_COL_PDD_SOGGETTO = "colSoggetto";
	public static final String KEY_COL_PDD_RUOLO = "colPddRuolo";
	public static final String KEY_COL_FAULT_INTEGRAZIONE = "colFaultIntegrazione";
	public static final String KEY_COL_FAULT_COOPERAZIONE = "colFaultCooperazione";
	public static final String KEY_COL_SOGGETTO_FRUITORE = "colFruitore";
	public static final String KEY_COL_ID_PORTA_SOGGETTO_FRUITORE = "colIdFruitore";
	public static final String KEY_COL_INDIRIZZO_SOGGETTO_FRUITORE = "colIndirizzoSoggettoFruitore";
	public static final String KEY_COL_SOGGETTO_EROGATORE = "colErogatore";
	public static final String KEY_COL_ID_PORTA_SOGGETTO_EROGATORE = "colIdErogatore";
	public static final String KEY_COL_INDIRIZZO_SOGGETTO_EROGATORE = "colIndirizzoSoggettoErogatore";
	public static final String KEY_COL_ID_MESSAGGIO_RICHIESTA = "colIDMessaggioRichiesta";
	public static final String KEY_COL_ID_MESSAGGIO_RISPOSTA = "colIDMessaggioRisposta";
	public static final String KEY_COL_DATA_ID_MSG_RICHIESTA = "colDataIDMsgRichiesta";
	public static final String KEY_COL_DATA_ID_MSG_RISPOSTA = "colDataIDMsgRisposta";
	public static final String KEY_COL_PROFILO_COLLABORAZIONE_OP2 = "colProfCollaborazioneOp2";
	public static final String KEY_COL_PROFILO_COLLABORAZIONE_PROT = "colProfColl";
	public static final String KEY_COL_ID_COLLABORAZIONE = "colIDCollaborazione";
	public static final String KEY_COL_URI_ACCORDO_SERVIZIO = "colUriAccordoServizio";
	public static final String KEY_COL_TIPO_API = "colTipoApi";
	public static final String KEY_COL_TAGS = "colTags";
	public static final String KEY_COL_SERVIZIO = "colServ";
	public static final String KEY_COL_VERSIONE_SERVIZIO = "colVersioneServ";
	public static final String KEY_COL_AZIONE = "colAzione";
	public static final String KEY_COL_ID_ASINCRONO = "colIdAsincrono";
	public static final String KEY_COL_SERVIZIO_CORRELATO = "colServizioCorrelato";
	public static final String KEY_COL_HEADER_PROTOCOLLO_RICHIESTA = "colHeaderProtocolloRichiesta";
	public static final String KEY_COL_DIGEST_RICHIESTA =	"colDigestRichiesta";
	public static final String KEY_COL_PROTOCOLLO_EXT_INFO_RICHIESTA = "colProtocolloExtInfoRichiesta";
	public static final String KEY_COL_HEADER_PROTOCOLLO_RISPOSTA = "colHeaderProtocolloRisposta";
	public static final String KEY_COL_DIGEST_RISPOSTA = "colDigestRisposta";
	public static final String KEY_COL_PROTOCOLLO_EXT_INFO_RISPOSTA = "colProtocolloExtInfoRisposta";
	public static final String KEY_COL_TRACCIA_RICHIESTA = "colTracciaRichiesta";
	public static final String KEY_COL_TRACCIA_RISPOSTA = "colTracciaRisposta";
	public static final String KEY_COL_DIAGNOSTICI = "colDiagnostici";
	public static final String KEY_COL_ID_CORRELAZIONE_APPLICATIVA =	"colIDApplicativo";
	public static final String KEY_COL_ID_CORRELAZIONEAPPLICATIVARISPOSTA = "colIDApplicativoRisp";
	public static final String KEY_COL_SERVIZIO_APPLICATIVO = "colServizioApplicativo";
	public static final String KEY_COL_SERVIZIO_APPLICATIVO_FRUITORE = "colServizioApplicativoFruitore";
	public static final String KEY_COL_SERVIZIO_APPLICATIVO_EROGATORE = "colServizioApplicativoErogatore";
	public static final String KEY_COL_OPERAZIONE_IM = "colOperazioneIm";
	public static final String KEY_COL_LOCATION_RICHIESTA = "colLocationRichiesta";
	public static final String KEY_COL_LOCATION_RISPOSTA = "colLocationRisposta";
	public static final String KEY_COL_NOME_PORTA = "colNomePorta";
	public static final String KEY_COL_CREDENZIALI = "colCredenziali";
	public static final String KEY_COL_LOCATION_CONNETTORE = "colLocationConnettore";
	public static final String KEY_COL_URL_INVOCAZIONE = "colUrlInvocazione";
	public static final String KEY_COL_DUPLICATI_RICHIESTA = "colDuplicatiRichiesta";
	public static final String KEY_COL_DUPLICATI_RISPOSTA = "colDuplicatiRisposta";
	public static final String KEY_COL_CLUSTER_ID = "colIDCluster";
	public static final String KEY_COL_EVENTI_GESTIONE = "colEventi";
	public static final String KEY_COL_LATENZA_PORTA = "colLatenzaPorta";
	public static final String KEY_COL_LATENZA_SERVIZIO = "colLatenzaServizio";
	public static final String KEY_COL_LATENZA_TOTALE = "colLatenzaTotale";
	public static final String KEY_COL_INDIRIZZO_CLIENT = "colIndirizzoClient";
	public static final String KEY_COL_X_FORWARDED_FOR = "colXForwardedFor";
	public static final String KEY_COL_TIPO_RICHIESTA = "colTipoRichiesta";
	public static final String KEY_COL_CODICE_RISPOSTA_INGRESSO = "colCodiceRispostaIngresso";
	public static final String KEY_COL_CODICE_RISPOSTA_USCITA = "colCodiceRispostaUscita";
	
	/** LABEL campi */
	
	public static final String LABEL_COL_ID_TRANSAZIONE = "Id Transazione";
	public static final String LABEL_COL_STATO = "Stato";
	public static final String LABEL_COL_RUOLO_TRANSAZIONE = "Ruolo Transazione";
	public static final String LABEL_COL_ESITO = "Esito";
	public static final String LABEL_COL_ESITO_CONTESTO = "Contesto";
	public static final String LABEL_COL_PROTOCOLLO = "Protocollo";
	public static final String LABEL_COL_DATA_ACCETTAZIONE_RICHIESTA = "Data Accettazione Richiesta";
	public static final String LABEL_COL_DATA_INGRESSO_RICHIESTA = "Data Ingresso Richiesta";
	public static final String LABEL_COL_DATA_USCITA_RICHIESTA = "Data Uscita Richiesta";
	public static final String LABEL_COL_DATA_ACCETTAZIONE_RISPOSTA = "Data Accettazione Risposta";
	public static final String LABEL_COL_DATA_INGRESSO_RISPOSTA = "Data Ingresso Risposta";
	public static final String LABEL_COL_DATA_USCITA_RISPOSTA = "Data Uscita Risposta";
	public static final String LABEL_COL_RICHIESTA_INGRESSO_BYTES = "Dimensione Ingresso Richiesta";
	public static final String LABEL_COL_RICHIESTA_USCITA_BYTES = "Dimensione Uscita Richiesta";
	public static final String LABEL_COL_RISPOSTA_INGRESSO_BYTES = "Dimensione Ingresso Risposta";
	public static final String LABEL_COL_RISPOSTA_USCITA_BYTES = "Dimensione Uscita Riposta";
	public static final String LABEL_COL_PDD_CODICE = "Dominio (ID)";
	public static final String LABEL_COL_PDD_SOGGETTO = "Dominio (Soggetto)";
	public static final String LABEL_COL_PDD_RUOLO = "Tipologia";
	public static final String LABEL_COL_FAULT_INTEGRAZIONE = "Fault Integrazione";
	public static final String LABEL_COL_FAULT_COOPERAZIONE = "Fault Cooperazione";
	public static final String LABEL_COL_SOGGETTO_FRUITORE = "Fruitore (Soggetto)";
	public static final String LABEL_COL_ID_PORTA_SOGGETTO_FRUITORE = "Fruitore (Dominio)";
	public static final String LABEL_COL_INDIRIZZO_SOGGETTO_FRUITORE = "Indirizzo Fruitore";
	public static final String LABEL_COL_SOGGETTO_EROGATORE = "Erogatore (Soggetto)";
	public static final String LABEL_COL_ID_PORTA_SOGGETTO_EROGATORE = "Erogatore (Dominio)";
	public static final String LABEL_COL_INDIRIZZO_SOGGETTO_EROGATORE = "Indirizzo Erogatore";
	public static final String LABEL_COL_ID_MESSAGGIO_RICHIESTA = "ID Messaggio Richiesta";
	public static final String LABEL_COL_ID_MESSAGGIO_RISPOSTA = "ID Messaggio Risposta";
	public static final String LABEL_COL_DATA_ID_MSG_RICHIESTA = "Data ID Messaggio Richiesta";
	public static final String LABEL_COL_DATA_ID_MSG_RISPOSTA = "Data ID Messaggio Risposta";
	public static final String LABEL_COL_PROFILO_COLLABORAZIONE_OP2 = "Profilo Collaborazione Op2";
	public static final String LABEL_COL_PROFILO_COLLABORAZIONE_PROT = "Profilo Collaborazione";
	public static final String LABEL_COL_ID_COLLABORAZIONE = "ID Collaborazione";
	public static final String LABEL_COL_URI_ACCORDO_SERVIZIO = "Uri API";
	public static final String LABEL_COL_TIPO_API = "Tipo API";
	public static final String LABEL_COL_TAGS = "Tags";
	public static final String LABEL_COL_SERVIZIO = "API";
	public static final String LABEL_COL_VERSIONE_SERVIZIO = "Versione API";
	public static final String LABEL_COL_AZIONE = "Azione";
	public static final String LABEL_COL_ID_ASINCRONO = "Id Asincrono";
	public static final String LABEL_COL_SERVIZIO_CORRELATO = "Servizio Correlato";
	public static final String LABEL_COL_HEADER_PROTOCOLLO_RICHIESTA = "Header Protocollo Richiesta";
	public static final String LABEL_COL_DIGEST_RICHIESTA =	"Digest Richiesta";
	public static final String LABEL_COL_PROTOCOLLO_EXT_INFO_RICHIESTA = "Protocollo Ext Info Richiesta";
	public static final String LABEL_COL_HEADER_PROTOCOLLO_RISPOSTA = "Header Protocollo Risposta";
	public static final String LABEL_COL_DIGEST_RISPOSTA = "Digest Risposta";
	public static final String LABEL_COL_PROTOCOLLO_EXT_INFO_RISPOSTA = "Protocollo Ext Info Risposta";
	public static final String LABEL_COL_TRACCIA_RICHIESTA = "Traccia Richiesta";
	public static final String LABEL_COL_TRACCIA_RISPOSTA = "Traccia Risposta";	
	public static final String LABEL_COL_DIAGNOSTICI = "Diagnostici";
	public static final String LABEL_COL_ID_CORRELAZIONE_APPLICATIVA =	"ID Applicativo Richiesta";
	public static final String LABEL_COL_ID_CORRELAZIONEAPPLICATIVARISPOSTA = "ID Applicativo Risposta";
	public static final String LABEL_COL_SERVIZIO_APPLICATIVO = "Applicativo";
	public static final String LABEL_COL_SERVIZIO_APPLICATIVO_FRUITORE = "Applicativo Fruitore";
	public static final String LABEL_COL_SERVIZIO_APPLICATIVO_EROGATORE = "Applicativo Erogatore";
	public static final String LABEL_COL_OPERAZIONE_IM = "Operazione Integration Manager";
	public static final String LABEL_COL_LOCATION_RICHIESTA = "Location Richiesta";
	public static final String LABEL_COL_LOCATION_RISPOSTA = "Location Risposta";
	public static final String LABEL_COL_NOME_PORTA = "Nome Porta";
	public static final String LABEL_COL_CREDENZIALI = "Credenziali";
	public static final String LABEL_COL_LOCATION_CONNETTORE = "Location Connettore";
	public static final String LABEL_COL_URL_INVOCAZIONE = "Url Invocazione";
	public static final String LABEL_COL_DUPLICATI_RICHIESTA = "Duplicati Richiesta";
	public static final String LABEL_COL_DUPLICATI_RISPOSTA = "Duplicati Risposta";
	public static final String LABEL_COL_CLUSTER_ID = "ID Cluster";
	public static final String LABEL_COL_EVENTI_GESTIONE = "Eventi";
	public static final String LABEL_COL_LATENZA_PORTA = "Latenza Gateway";
	public static final String LABEL_COL_LATENZA_SERVIZIO = "Latenza Servizio";
	public static final String LABEL_COL_LATENZA_TOTALE = "Latenza Totale";
	public static final String LABEL_COL_INDIRIZZO_CLIENT = "Indirizzo Client";
	public static final String LABEL_COL_X_FORWARDED_FOR = "X-Forwarded-For";
	public static final String LABEL_COL_TIPO_RICHIESTA = "Metodo HTTP";
	public static final String LABEL_COL_CODICE_RISPOSTA_INGRESSO = "Codice Risposta Ingresso";
	public static final String LABEL_COL_CODICE_RISPOSTA_USCITA = "Codice Risposta Uscita";
}
