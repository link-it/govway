/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.web.ctrlstat.servlet.archivi;

import java.util.Vector;

import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.Imbustamento;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.Sbustamento;
import org.openspcoop2.pdd.services.RicezioneBuste;
import org.openspcoop2.pdd.services.RicezioneContenutiApplicativi;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.lib.mvc.ForwardParams;

/**
 * ArchiviCostanti
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiviCostanti {

	/* OBJECT NAME */
	
	public final static String OBJECT_NAME_ARCHIVI_IMPORT = "import";
	public final static ForwardParams TIPO_OPERAZIONE_IMPORT = ForwardParams.OTHER("");
	
	public final static String OBJECT_NAME_ARCHIVI_EXPORT = "export";
	public final static ForwardParams TIPO_OPERAZIONE_EXPORT = ForwardParams.OTHER("");
	
	public final static String OBJECT_NAME_ARCHIVI_DIAGNOSTICA = "diagnostica";
	public final static ForwardParams TIPO_OPERAZIONE_DIAGNOSTICA = ForwardParams.OTHER("");
	
	public final static String OBJECT_NAME_ARCHIVI_DIAGNOSTICA_TESTO = "diagnosticaTesto";
	public final static ForwardParams TIPO_OPERAZIONE_DIAGNOSTICA_TESTO = ForwardParams.OTHER("");
	
	public final static String OBJECT_NAME_ARCHIVI_TRACCIAMENTO = "tracciamento";
	public final static ForwardParams TIPO_OPERAZIONE_TRACCIAMENTO = ForwardParams.OTHER("");
	
	public final static String OBJECT_NAME_ARCHIVI_TRACCIAMENTO_TESTO = "tracciamentoTesto";
	public final static ForwardParams TIPO_OPERAZIONE_TRACCIAMENTO_TESTO = ForwardParams.OTHER("");
	
	
	/* SERVLET NAME EXPORT */
	
	public final static String SERVLET_NAME_DOCUMENTI_EXPORT = "downloadDocumento";
	public final static String SERVLET_NAME_TRACCE_EXPORT = "exporterTracce";
	public final static String SERVLET_NAME_MESSAGGI_DIAGNOSTICI_EXPORT = "exporterDiagnostica";
	public final static String SERVLET_NAME_PACKAGE_EXPORT = "exporterArchivi";
	public final static String SERVLET_NAME_RESOCONTO_EXPORT = "downloadResoconto";
	
	
	
	/* SERVLET NAME */
	
	public final static String SERVLET_NAME_ARCHIVI_IMPORT = OBJECT_NAME_ARCHIVI_IMPORT+".do";
	public final static Vector<String> SERVLET_ARCHIVI_IMPORT = new Vector<String>();
	static{
		SERVLET_ARCHIVI_IMPORT.add(SERVLET_NAME_ARCHIVI_IMPORT);
	}
	
	public final static String SERVLET_NAME_ARCHIVI_EXPORT = OBJECT_NAME_ARCHIVI_EXPORT+".do";
	public final static Vector<String> SERVLET_ARCHIVI_EXPORT = new Vector<String>();
	static{
		SERVLET_ARCHIVI_EXPORT.add(SERVLET_NAME_ARCHIVI_EXPORT);
		SERVLET_ARCHIVI_EXPORT.add(SERVLET_NAME_PACKAGE_EXPORT);
	}
	
	public final static String SERVLET_NAME_ARCHIVI_DIAGNOSTICA = OBJECT_NAME_ARCHIVI_DIAGNOSTICA+".do";
	public final static String SERVLET_NAME_ARCHIVI_DIAGNOSTICA_LIST = OBJECT_NAME_ARCHIVI_DIAGNOSTICA+"List.do";
	public final static String SERVLET_NAME_ARCHIVI_DIAGNOSTICA_TESTO = OBJECT_NAME_ARCHIVI_DIAGNOSTICA_TESTO+".do";
	public final static Vector<String> SERVLET_ARCHIVI_DIAGNOSTICA = new Vector<String>();
	static{
		SERVLET_ARCHIVI_DIAGNOSTICA.add(SERVLET_NAME_ARCHIVI_DIAGNOSTICA);
		SERVLET_ARCHIVI_DIAGNOSTICA.add(SERVLET_NAME_ARCHIVI_DIAGNOSTICA_LIST);
		SERVLET_ARCHIVI_DIAGNOSTICA.add(SERVLET_NAME_ARCHIVI_DIAGNOSTICA_TESTO);
	}
	
	public final static String SERVLET_NAME_ARCHIVI_TRACCIAMENTO = OBJECT_NAME_ARCHIVI_TRACCIAMENTO+".do";
	public final static String SERVLET_NAME_ARCHIVI_TRACCIAMENTO_LIST = OBJECT_NAME_ARCHIVI_TRACCIAMENTO+"List.do";
	public final static String SERVLET_NAME_ARCHIVI_TRACCIAMENTO_TESTO = OBJECT_NAME_ARCHIVI_TRACCIAMENTO_TESTO+".do";
	public final static Vector<String> SERVLET_ARCHIVI_TRACCIAMENTO = new Vector<String>();
	static{
		SERVLET_ARCHIVI_TRACCIAMENTO.add(SERVLET_NAME_ARCHIVI_TRACCIAMENTO);
		SERVLET_ARCHIVI_TRACCIAMENTO.add(SERVLET_NAME_ARCHIVI_TRACCIAMENTO_LIST);
		SERVLET_ARCHIVI_TRACCIAMENTO.add(SERVLET_NAME_ARCHIVI_TRACCIAMENTO_TESTO);
	}
	
		
		
	/* LABEL GENERALI */
	
	public final static String LABEL_ARCHIVI_IMPORT = "Importa";
	public final static String LABEL_ARCHIVI_EXPORT = "Esporta";
	public final static String LABEL_ARCHIVI_ELIMINA = "Elimina";
	public final static String LABEL_DOWNLOAD = "Download";
	public final static String LABEL_REPORTISTICA = CostantiControlStation.LABEL_STRUMENTI;
	public final static String LABEL_DIAGNOSTICA = "Diagnostica";
	public final static String LABEL_TRACCIAMENTO = "Tracciamento";
	public final static String LABEL_MESSAGGI = "Messaggi";
	public final static String LABEL_TRACCE = "Tracce";
	public final static String LABEL_SORGENTE_DATI = "Sorgente Dati";
	public final static String LABEL_FILTRO_RICERCA = "Filtro Ricerca";
	public final static String LABEL_SOGGETTO_MITTENTE = "Soggetto Mittente";
	public final static String LABEL_SOGGETTO_DESTINATARIO = "Soggetto Destinatario";
	public final static String LABEL_MITTENTE = "Mittente";
	public final static String LABEL_DESTINATARIO = "Destinatario";
	public final static String LABEL_SERVIZIO = "Servizio";
	public final static String LABEL_DATA = "Data";
	public final static String LABEL_DETTAGLIO = "Dettaglio";
	public final static String LABEL_ESPORTA_XML = "Esporta XML";
	public final static String LABEL_TESTO_DIAGNOSTICO = "Testo diagnostico";
	public final static String LABEL_DETTAGLIO_DIAGNOSTICO = "Dettaglio messaggio diagnostico";
	public final static String LABEL_INFORMAZIONI_PROTOCOLLO = "Informazioni Protocollo";
	public final static String LABEL_SERVIZIO_AZIONE = "Servizio[@Azione]";
	public final static String LABEL_TIPO_MESSAGGIO = "Tipo";
	public final static String LABEL_TIPO_MESSAGGIO_2 = "Tipo Messaggio";
	public final static String LABEL_TRACCIA = "Traccia";
	public final static String LABEL_RICHIESTA = "Richiesta";
	public final static String LABEL_RISPOSTA = "Risposta";
	public final static String LABEL_TIPO_SOGGETTO = "Tipo";
	public final static String LABEL_NOME_SOGGETTO = "Nome";
	public final static String LABEL_ID_PORTA_SOGGETTO = "IdentificativoPorta";
	public final static String LABEL_INDIRIZZO_SOGGETTO = "Indirizzo";
	public final static String LABEL_SOGGETTO_PORTA = "Soggetto Porta";
	public final static String LABEL_SERVIZIO_CORRELATO = "Servizio Correlato";
	public final static String LABEL_DATI_PER_PROTOCOLLO = "Dati relativi al protocollo ";
	public final static String LABEL_COLLABORAZIONE = "Collaborazione";
	public final static String LABEL_SORGENTE_TEMPORALE = "Sorgente temporale";
	public final static String LABEL_ORA_REGISTRAZIONE = "Ora Registrazione";
	public final static String LABEL_SCADENZA = "Scadenza";
	public final static String LABEL_PROFILO_TRASMISSIONE = "Profilo Trasmissione";
	public final static String LABEL_PROFILO_TRASMISSIONE_INOLTRO = "Inoltro";
	public final static String LABEL_PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE = "Conferma Ricezione";
	public final static String LABEL_SEQUENZA = "Sequenza";
	public final static String LABEL_DIGEST = "Digest";
	public final static String LABEL_LISTA_RISCONTRI = "Lista Riscontri";
	public final static String LABEL_LISTA_TRASMISSIONI = "Lista Trasmissioni";
	public final static String LABEL_LISTA_ECCEZIONI = "Lista Eccezioni";
	public final static String LABEL_REGISTRAZIONE = "Registrazione";
	public final static String LABEL_ECCEZIONE_RILEVANZA = "Rilevanza";
	public final static String LABEL_ECCEZIONE_CODICE = "Codice";
	public final static String LABEL_ECCEZIONE_CONTESTO = "Contesto";
	public final static String LABEL_ECCEZIONE_POSIZIONE = "Posizione";
	public final static String LABEL_LISTA_INFO_PROTOCOLLO = "Informazioni aggiuntive sul protocollo";
	public final static String LABEL_LISTA_INFO_AGGIUNTIVE = "Informazioni aggiuntive";
	public final static String LABEL_RUOLO_PDD = "Ruolo della Porta";
	public final static String LABEL_ESITO_ELABORAZIONE = "Esito Elaborazione";
	public final static String LABEL_DETTAGLIO_ESITO_ELABORAZIONE = "Dettaglio Esito Elaborazione";
	public final static String LABEL_SERVIZIO_APPLICATIVO_FRUITORE = "Servizio Applicativo Fruitore";
	public final static String LABEL_SERVIZIO_APPLICATIVO_EROGATORE = "Servizio Applicativo Erogatore";
	public final static String LABEL_CORRELAZIONE_APPLICATIVA = "Correlazione Applicativa";
	public final static String LABEL_TRACCIA_LOCATION_IN = "Indirizzo di provenienza della busta";
	public final static String LABEL_TRACCIA_LOCATION_OUT = "Indirizzo a cui è stata spedita la busta";
	public final static String LABEL_TRACCIA_LOCATION = "Indirizzo provenienza/spedizione della busta";
	public final static String LABEL_LISTA_INFO_ALLEGATI = "Informazioni aggiuntive Allegati";
	public final static String LABEL_ALLEGATO_CONTENT_ID = "Content-ID";
	public final static String LABEL_ALLEGATO_CONTENT_LOCATION = "Content-Location";
	public final static String LABEL_ALLEGATO_CONTENT_TYPE = "Content-Type";
	public final static String LABEL_ARCHIVIO = "Archivio";
	public final static String LABEL_IMPORT_ERRORE = "<BR><BR><B>Errore</B>: ";
	public final static String LABEL_IMPORT_ERROR_HEADER = "Rilevato un errore durante l'import dell'archivio: <BR>";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_MANCANTI = "Attenzione: L'archivio non possiede tutte le informazioni necessarie per completare l'operazione di import";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_IDENTIFICAZIONE_MANCANTI = "Informazioni d'identificazione mancanti";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI = "Informazioni di protocollo non presenti (es. profilo di collaborazione, correlazione per gli asincroni ...)";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_WSDL_MODE="Automatica dall'accordo (WSDL,SpecificaConversazione,...)";
	public static final String LABEL_IMPORT_ERROR_INFORMAZIONI_PROTOCOLLO_MANCANTI_RICONOSCIMENTO_USER_INPUT = "Configurazione manuale dei servizi e delle operazioni presenti nel WSDL";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO = "Servizio (PortType) implementato non esistente";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO_MESSAGGIO_KEY = "@ID_PORT_TYPE@";
	public final static String LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO_MESSAGGIO = "Il servizio implementato ("+
			LABEL_IMPORT_ERROR_INFORMAZIONI_PORT_TYPE_RIFERITO_MESSAGGIO_KEY+") non risulta registrato nell'accordo di servizio parte comune (o composto) riferito<br>Selezionare uno dei servizi esistenti";
	
	
	/* PARAMETRI */
	
	public final static String PARAMETRI_ARCHIVI = "params";
	
	public final static String PARAMETRO_ARCHIVI_ID = "id";
	public final static String PARAMETRO_ARCHIVI_TIPO_ACCORDO = "what";
	
	public final static String PARAMETRO_ARCHIVI_NOME_DATASOURCE = "nomeDs";
	public final static String PARAMETRO_ARCHIVI_ID_FUNZIONE = "idfunzione";
	public final static String PARAMETRO_ARCHIVI_DATA_INIZIO = "datainizio";
	public final static String PARAMETRO_ARCHIVI_DATA_FINE = "datafine";
	public final static String PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE = "profcoll";
	public final static String PARAMETRO_ARCHIVI_TIPO_MITTENTE = "tipo_mittente";
	public final static String PARAMETRO_ARCHIVI_NOME_MITTENTE = "nome_mittente";
	public final static String PARAMETRO_ARCHIVI_TIPO_DESTINATARIO = "tipo_destinatario";
	public final static String PARAMETRO_ARCHIVI_NOME_DESTINATARIO = "nome_destinatario";
	public final static String PARAMETRO_ARCHIVI_TIPO_SERVIZIO = "tipo_servizio";
	public final static String PARAMETRO_ARCHIVI_NOME_SERVIZIO = "servizio";
	public final static String PARAMETRO_ARCHIVI_AZIONE = "nome_azione";
	public final static String PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA = "correlazioneApplicativa";
	public final static String PARAMETRO_ARCHIVI_PROTOCOLLO = "protocollo";
	public final static String PARAMETRO_ARCHIVI_SEARCH = "search";
	public final static String PARAMETRO_ARCHIVI_INDEX = "index";
	public final static String PARAMETRO_ARCHIVI_PAGE_SIZE = "pageSize";
	public final static String PARAMETRO_ARCHIVI_ID_MESSAGGIO = "idMessaggio";
	public final static String PARAMETRO_ARCHIVI_ID_MESSAGGIO_SEARCH = "idMessaggioS";
	public final static String PARAMETRO_ARCHIVI_ID_MESSAGGIO_RISPOSTA = "idMessaggioRisposta";
	public final static String PARAMETRO_ARCHIVI_ID_PORTA = "idporta";
	public final static String PARAMETRO_ARCHIVI_MESSAGGIO = "messaggio";
	public final static String PARAMETRO_ARCHIVI_CODICE_PORTA = "codicePorta";
	public final static String PARAMETRO_ARCHIVI_NOME_SOGGETTO_PORTA = "nomeSoggettoPorta";
	public final static String PARAMETRO_ARCHIVI_TIPO_SOGGETTO_PORTA = "tipoSoggettoPorta";
	
	public final static String PARAMETRO_ARCHIVI_ALLEGATO_ID_ALLEGATO = "idAllegato";
	public final static String PARAMETRO_ARCHIVI_ALLEGATO_ID_ACCORDO = "idAccordo";
	public final static String PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO = "tipoDocumento";
	public final static String PARAMETRO_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO = "tipoDocumentoDaScaricare";
		
	public final static String PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA = "severita";
	
	public final static String PARAMETRO_ARCHIVI_IMPORTER_MODALITA = "modalita";
	public final static String PARAMETRO_ARCHIVI_PACKAGE_FILE = "theFile";
	public final static String PARAMETRO_ARCHIVI_PACKAGE_FILE_PATH = "filePath";
	public final static String PARAMETRO_ARCHIVI_PACKAGE_FILE_SIMPLE_NAME = "fileName";
	public final static String PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI = "validazioneDocumenti";
	public final static String PARAMETRO_ARCHIVI_UPDATE_ENABLED = "updateEnabled";
	public final static String PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO = "tipologiaArchivio";
	public final static String PARAMETRO_ARCHIVI_TIPO = "tipoArchivio";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID ="importInformationMissingObjectId";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID_DESCRIPTION ="importInformationMissingObjectIdDescription";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_COLLECTION_FILE_PATH ="importInformationMissingCollectionFilePath";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_FILE_PATH ="importInformationMissingObjectFilePath";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_CLASS ="importInformationMissingClassObject";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT = "soggetto";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_VERSIONE_INPUT = "versione"; // lasciare versione per avere il widget grafico spinbanner
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT = "modalitaAcquisizione";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_IS_DEFINED ="servizioInputIsDefined";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE ="servizioInput";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_IS_DEFINED = "_azioneInputIsDefined";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION = "_azioneInput";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO = "_profiloCollaborazioneInput";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO = "_servizioAzioneCorrelataInput";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_CORRELATA = "_azioneCorrelataInput";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_PORT_TYPE_IMPLEMENTED_INPUT = "portTypeImplemented";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_SERVIZIO_PARTE_COMUNE_INPUT = "accordoServizioParteComune";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_COOPERAZIONE_INPUT = "accordoCooperazione";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_HIDDEN = "ProprietaInputHidden_";
	public final static String PARAMETRO_ARCHIVI_IMPORT_INPUT_PROPRIETA_PREFIX_VALUE = "ProprietaInputValue_";
	public final static String PARAMETRO_ARCHIVI_EXPORT_TIPO = "tipoExport";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE = "cascade";
	public final static String PARAMETRO_ARCHIVI_EXPORT_TIPO_DUMP = "tipoDump";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD = "cascadePdd";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_SOGGETTI = "cascadeSoggetti";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_SERVIZI_APPLICATIVI = "cascadeServiziApplicativi";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_DELEGATE = "cascadePorteDelegate";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_APPLICATIVE = "cascadePorteApplicative";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_COOPERAZIONE = "cascadeAc";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_COMUNE = "cascadeAspc";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_COMPOSTO = "cascadeAsc";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_SPECIFICA = "cascadeAsps";
	public final static String PARAMETRO_ARCHIVI_EXPORT_CASCADE_FRUIZIONI = "cascadeFruizioni";
	
	/* LABEL PARAMETRI */
	
	public final static String LABEL_PARAMETRO_ARCHIVI_NOME_DATASOURCE = "Nome";
	public final static String LABEL_PARAMETRO_ARCHIVI_DATA_INIZIO = "Inizio intervallo (aaaa-mm-gg)";
	public final static String LABEL_PARAMETRO_ARCHIVI_DATA_FINE = "Fine intervallo (aaaa-mm-gg)";
	public final static String LABEL_PARAMETRO_ARCHIVI_PROFILO_COLLABORAZIONE = "Profilo di collaborazione";
	public final static String LABEL_PARAMETRO_ARCHIVI_ID_FUNZIONE = "Identificativo funzione";
	public final static String LABEL_PARAMETRO_ARCHIVI_TIPO_MITTENTE = "Tipo Mittente";
	public final static String LABEL_PARAMETRO_ARCHIVI_NOME_MITTENTE = "Nome Mittente";
	public final static String LABEL_PARAMETRO_ARCHIVI_TIPO_DESTINATARIO = "Tipo Destinatario";
	public final static String LABEL_PARAMETRO_ARCHIVI_NOME_DESTINATARIO = "Nome Destinatario";
	public final static String LABEL_PARAMETRO_ARCHIVI_TIPO_SERVIZIO = "Tipo Servizio";
	public final static String LABEL_PARAMETRO_ARCHIVI_NOME_SERVIZIO = "Nome Servizio";
	public final static String LABEL_PARAMETRO_ARCHIVI_AZIONE = "Azione";
	public final static String LABEL_PARAMETRO_ARCHIVI_CORRELAZIONE_APPLICATIVA = "ID Applicativo";
	public final static String LABEL_PARAMETRO_ARCHIVI_PROTOCOLLO = "Protocollo";
	public final static String LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO = "ID Messaggio";
	public final static String LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO_RISPOSTA = "Riferimento Messaggio";
	public final static String LABEL_PARAMETRO_ARCHIVI_ID_MESSAGGIO_RISPOSTA_BREVE = "Rif. Messaggio";
	public final static String LABEL_PARAMETRO_ARCHIVI_ID_PORTA = "Identificativo Porta di Dominio";
	public final static String LABEL_PARAMETRO_ARCHIVI_MESSAGGIO = "Messaggio";
	
	public final static String LABEL_PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA = "Livello severit&agrave;";
	
	public final static String LABEL_PARAMETRO_ARCHIVI_PACKAGE_FILE = "File";
	public final static String LABEL_PARAMETRO_ARCHIVI_PACKAGE_FILE_SIMPLE_NAME = "File Caricato";
	public final static String LABEL_PARAMETRO_ARCHIVI_VALIDAZIONE_DOCUMENTI = "Validazione Documenti";
	public final static String LABEL_PARAMETRO_ARCHIVI_UPDATE_ENABLED = "Aggiornamento";
	public final static String LABEL_PARAMETRO_ARCHIVI_TIPOLOGIA_ARCHIVIO = "Tipologia archivio";
	public final static String LABEL_PARAMETRO_ARCHIVI_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_OBJECT_ID_DESCRIPTION ="Descrizione";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT = "Soggetto";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_VERSIONE_INPUT = "Versione";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT = "Acquisizione informazioni di protocollo";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_TITLE_KEY = "@PT_NAME@";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_TITLE = 
			"Informazioni per il port type '"+LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_TITLE_KEY+"'";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE ="Nome servizio";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE_KEY = "@OP_NAME@";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE = 
			"Operation '"+LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_OPERATION_TITLE_KEY+"'";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION ="Nome azione";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_PROFILO = "Profilo di collaborazione";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_SERVIZIO_CORRELATO = 
			"Nome del servizio di richiesta correlato";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_MODALITA_ACQUISIZIONE_INPUT_PORT_TYPE_OPERATION_CORRELATA = 
			"Nome dell'azione di richiesta correlata";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_PORT_TYPE_IMPLEMENTED_INPUT = "Servizio";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_SERVIZIO_PARTE_COMUNE_INPUT = "AccordoServizioParteComune/ServizioComposto";
	public final static String LABEL_PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_COOPERAZIONE_INPUT = "AccordoCooperazione";
	
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE = "Includi tutti gli elementi riferiti";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SEZIONE = "Configurazione 'Cascade'";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_TIPO_DUMP = "Tipologia di salvataggio";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PDD = "Porte di Dominio";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SOGGETTI = "Soggetti";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_SERVIZI_APPLICATIVI = "Servizi Applicativi";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_DELEGATE = "Porte Delegate";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_PORTE_APPLICATIVE = "Porte Applicative";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_COOPERAZIONE = "Accordi di Cooperazione";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_COMUNE = "Accordi di Servizio Parte Comune";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_COMPOSTO = "Accordi di Servizio Composto";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_ACCORDI_SERVIZIO_PARTE_SPECIFICA = "Accordi di Servizio Parte Specifica";
	public final static String LABEL_PARAMETRO_ARCHIVI_EXPORT_CASCADE_FRUIZIONI = "Fruizioni";
		
	
	
	/* DEFAULT VALUE PARAMETRI */
	
	public final static String PARAMETRO_ARCHIVI_IMPORTER_MODALITA_IMPORT = "import";
	public final static String PARAMETRO_ARCHIVI_IMPORTER_MODALITA_ELIMINA = "elimina";
	
	public final static String FORMATO_DATA = "yyyy-MM-dd hh:mm:ss.SSS";
	
	public final static String PARAMETRO_DOWNLOAD_RESOCONTO_VALORE = "DownloadResocontoValore";
	
	public final static String PARAMETRO_ARCHIVI_PROTOCOLLO_UNDEFINDED = "-";
	
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_SOGGETTO_INPUT_UNDEFINDED = "-";
	
	public final static String PARAMETRO_ARCHIVI_IMPORT_INFO_MISSING_ACCORDO_INPUT_UNDEFINDED = "-";
	
	public final static String PARAMETRO_TRACCIA_LOCATION_IN = "IN:";
	public final static String PARAMETRO_TRACCIA_LOCATION_OUT = "OUT:";
	
	public final static String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_PARTE_COMUNE = "accordiAPC";
	public final static String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_PARTE_SPECIFICA = "accordiAPS";
	public final static String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_SERVIZIO_COMPOSTO = "accordiASC";
	public final static String PARAMETRO_VALORE_ARCHIVI_TIPO_ACCORDO_COOPERAZIONE = "accordiCooperazione";
	
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_COMUNE = "aspc";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_PARTE_SPECIFICA = "asps";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_COOPERAZIONE = "ac";

	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_DOCUMENTO = "documento";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_DEFINITORIO = "wsdlDefinitorio";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_CONCETTUALE = "wsdlConcettuale";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_EROGATORE = "wsdlLogicoErogatore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_LOGICO_FRUITORE = "wsdlLogicoFruitore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_EROGATORE = "wsdlImplementativoErogatore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_FRUITORE = "wsdlImplementativoFruitore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_CONCETTUALE = "specificaConversazioneConcettuale";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_EROGATORE = "specificaConversazioneLogicoErogatore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_SPECIFICA_CONVERSAZIONE_LOGICO_FRUITORE = "specificaConversazioneLogicoFruitore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_XSD_SCHEMA_COLLECTION = "xsdSchemaCollection";
		
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_TIPO_SOGGETTO_FRUITORE = "wsdlImplementativoTipoSoggettoFruitore";
	public final static String PARAMETRO_VALORE_ARCHIVI_ALLEGATO_TIPO_ACCORDO_TIPO_DOCUMENTO_WSDL_IMPLEMENTATIVO_NOME_SOGGETTO_FRUITORE = "wsdlImplementativoNomeSoggettoFruitore";
	
	public final static String PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO = "asincrono-asimmetrico";
	public final static String PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO = "asincrono-simmetrico";
	public final static String PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_SINCRONO = "sincrono";
	public final static String PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ONEWAY = "oneway";
	public final static String PARAMETRO_VALORE_ARCHIVI_PROFILO_COLLABORAZIONE_ANY ="-";

	public final static String[] PARAMETRO_VALORI_ARCHIVI_ID_FUNZIONE = { "-", ConsegnaContenutiApplicativi.ID_MODULO, 
		Imbustamento.ID_MODULO, InoltroBuste.ID_MODULO, RicezioneBuste.ID_MODULO ,
		RicezioneContenutiApplicativi.ID_MODULO, Sbustamento.ID_MODULO };
	
	public final static String[] PARAMETRO_VALORI_ARCHIVI_DIAGNOSTICA_SEVERITA = { "0", "1", "2", "3", "4", "5", "6", "7" };
	public final static String PARAMETRO_VALORE_ARCHIVI_DIAGNOSTICA_SEVERITA_DEFAULT ="4";
	
	public final static String[] PARAMETRO_VALORI_ARCHIVI_EXPORT_TIPO_DUMP = { ArchiveType.ALL.toString(), ArchiveType.ALL_WITHOUT_CONFIGURAZIONE.toString(), ArchiveType.CONFIGURAZIONE.toString() };
	public final static String[] PARAMETRO_LABEL_ARCHIVI_EXPORT_TIPO_DUMP = { "Esportazione completa", "Dati applicativi", "Parametri di configurazione" };
}
