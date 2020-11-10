/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.costanti;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ModalitaIdentificazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TrasformazioneIdentificazioneRisorsaFallita;
import org.openspcoop2.core.config.constants.TrasformazioneRegolaParametroTipoAzione;
import org.openspcoop2.core.config.constants.VersioneSOAP;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.pdd.core.autorizzazione.CostantiAutorizzazione;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.web.lib.mvc.Costanti;

/**
 * CostantiControlStation
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class CostantiControlStation {
	
	public final static String DEFAULT_OPENSPCOOP2_PREFIX_LOCAL_PATH = "console";
	public final static String DEFAULT_OPENSPCOOP2_PROPERTIES_NAME = "CONSOLE";
	
	public final static String OPENSPCOOP2_PREFIX_LOCAL_PATH = "PREFIXNAMETEMPLATE";
	public final static String OPENSPCOOP2_PROPERTIES_NAME = "NAMETEMPLATE";
	
    public final static String _OPENSPCOOP2_CONSOLE_LOCAL_PATH = OPENSPCOOP2_PREFIX_LOCAL_PATH+"_local.properties";
    public final static String _OPENSPCOOP2_CONSOLE_PROPERTIES = "OPENSPCOOP2_"+OPENSPCOOP2_PROPERTIES_NAME+"_PROPERTIES";
    
    public final static String _OPENSPCOOP2_DATASOURCE_LOCAL_PATH = OPENSPCOOP2_PREFIX_LOCAL_PATH+"_local.datasource.properties";
    public final static String _OPENSPCOOP2_DATASOURCE_PROPERTIES = "OPENSPCOOP2_"+OPENSPCOOP2_PROPERTIES_NAME+"_DATASOURCE_PROPERTIES";
    
    public final static String _OPENSPCOOP2_REGISTRO_SERVIZI_REMOTO_LOCAL_PATH = OPENSPCOOP2_PREFIX_LOCAL_PATH+"_local.registroServiziRemoto.properties";
    public final static String _OPENSPCOOP2_REGISTRO_SERVIZI_REMOTO_PROPERTIES = "OPENSPCOOP2_"+OPENSPCOOP2_PROPERTIES_NAME+"_REGISTRO_SERVIZI_REMOTO_PROPERTIES";
	
    public final static String _OPENSPCOOP2_LOGGER_LOCAL_PATH = OPENSPCOOP2_PREFIX_LOCAL_PATH+"_local.log4j2.properties";
    public final static String _OPENSPCOOP2_LOGGER_PROPERTIES = "OPENSPCOOP2_"+OPENSPCOOP2_PROPERTIES_NAME+"_LOG_PROPERTIES";
    
	/** Logger */
	public static final String DRIVER_DB_PDD_CONSOLE_LOGGER = "DRIVER_DB_PDD_CONSOLE";

	/** Debug COSTANT STRING */
	public static final String DEBUG_STRING = "PDD_CONSOLE_DEBUG";
	
	/** Tooltip Utils */
	public static final String TOOLTIP_BREAK_LINE = "&#10;";
	
	/** PLACEHOLDER PROTOCOLLO in creazione automatica endpoint del soggetto */
	public static final String PLACEHOLDER_SOGGETTO_ENDPOINT_CREAZIONE_AUTOMATICA = "@protocol@";
	
	/** PLACEHOLDER PDD  */
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_IP_GESTIONE = "@IP_GESTIONE@";
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_PORTA_GESTIONE = "@PORTA_GESTIONE@";
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_PROTOCOLLO_GESTIONE = "@PROTOCOLLO_GESTIONE@";
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_IP_PUBBLICO = "@IP@";
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_PORTA_PUBBLICA = "@PORTA@";
	public static final String PLACEHOLDER_INFORMAZIONI_PDD_PROTOCOLLO_PUBBLICO = "@PROTOCOLLO@";
	
	/** Intervallo in millisecondi per Filtro JMS effettuato dai Gestori */
	public static final int JMS_FILTER = 100;

	/** Sleep per simulare Algoritmo transazione XA */
	public static final int INTERVALLO_TRANSAZIONE_XA = 2000;

	/** Sleep per receive */
	public static final int INTERVALLO_RECEIVE = 10000;
	
	/** PERFORM OPERATION */
	public static final int PERFORM_OPERATION_CREATE = 0;
	public static final int PERFORM_OPERATION_UPDATE = 1;
	public static final int PERFORM_OPERATION_DELETE = 2;
	
	/** SCRIPT OPERATION */
	public static final String SCRIPT_PERFORM_OPERATION_CREATE = "add";
	public static final String SCRIPT_PERFORM_OPERATION_DELETE = "delete";
	
	/** SESSION ATTRIBUTE */
	public final static String SESSION_PARAMETRO_GESTIONE_INFO_PROTOCOLLO = "GestioneInfoProtocollo";
	public final static String SESSION_PARAMETRO_VISUALIZZA_ACCORDI_AZIONI = "ShowAccordiAzioni";
	public final static String SESSION_PARAMETRO_VISUALIZZA_ACCORDI_COOPERAZIONE = "ShowAccordiCooperazione";
	public final static String SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI = "SoggettoVirtuale";
	public final static String SESSION_PARAMETRO_MODALITA_INTERFACCIA = "ModalitaInterfaccia";
	public final static String SESSION_PARAMETRO_SINGLE_PDD = "singlePdD";
	public final static String SESSION_PARAMETRO_GESTIONE_CONFIGURAZIONI_PERSONALIZZATE = "ConfigurazioniPersonalizzate";
	public final static String SESSION_PARAMETRO_SAME_DB_WEBUI = "sameDBWebUI";
	public final static String SESSION_PARAMETRO_TIPO_DB = "tipoDB";
	public final static String SESSION_PARAMETRO_OLD_CONFIGURAZIONE_PROPERTIES_PREFIX = Costanti.SESSION_PARAMETRO_OLD_CONFIGURAZIONE_PROPERTIES_PREFIX;
	
	
	/** LABEL GENERALI */
	
	public final static String LABEL_PARAMETRO_PROTOCOLLO_DI = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI;
	public final static String LABEL_PARAMETRO_PROTOCOLLO = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO;
	public final static String LABEL_PARAMETRO_PROTOCOLLI = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLI;
	public final static String LABEL_PARAMETRO_PROTOCOLLO_COMPACT = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	public final static String LABEL_PARAMETRO_PROTOCOLLI_COMPACT = org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLI_COMPACT;
	
	public final static String LABEL_EMPTY = "&nbsp;";
	
	public final static String LABEL_BOTTONE_INDIVIDUA_GRUPPO = "Individua Gruppo";
	public final static String LABEL_BOTTONE_INDIVIDUA_CONNETTORE = "Individua Connettore";
	
	public final static String LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE_COLUMN = "Non standard";
	public final static String LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_SHORT_MESSAGE = "Configurazione non visualizzabile";
	public final static String LABEL_CONFIGURAZIONE_IMPOSTATA_MODALITA_AVANZATA_LONG_MESSAGE = "Attenzione: Configurazione non standard (Utilizzare l'interfaccia avanzata)";
	public final static String LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO = "Aggiornamento effettuato con successo";
	public final static String LABEL_AGGIORNAMENTO_CONFIGURAZIONE_PROPERTIES_EFFETTUATO_CON_SUCCESSO = "Aggiornamento effettuato con successo";
	public final static String LABEL_STRUMENTI = "Strumenti";
	public final static String LABEL_LINKIT_WEB = "https://link.it";
	public final static String LABEL_OPENSPCOOP2_WEB = "https://govway.org";
	public final static String LABEL_PARAMETRO_ID = "Id";
	public final static String LABEL_PARAMETRO_ID_SOGGETTO = "IdSogg";
	public final static String LABEL_PARAMETRO_ID_PORTA = "IdPorta";
	public final static String LABEL_PARAMETRO_ID_ASPS = "IdAsps";
	public final static String LABEL_PARAMETRO_ID_FRUIZIONE = "IdFruizione";
	public final static String LABEL_PARAMETRO_NOME = "Nome";
	public final static String LABEL_PARAMETRO_VALORE = "Valore";
	public final static String LABEL_PARAMETRO_SERVIZIO_APPLICATIVO = "Servizio Applicativo";
	public final static String LABEL_PARAMETRO_APPLICATIVO = "Applicativo";
	public final static String LABEL_PARAMETRO_STATO = "Stato";
	public final static String LABEL_PARAMETRO_MESSAGE_SECURITY = "Message-Security";
	public final static String LABEL_PARAMETRO_RICHIESTA = "Richiesta";
	public final static String LABEL_PARAMETRO_RISPOSTA = "Risposta";
	public final static String LABEL_PARAMETRO_OBBLIGATORIO = "Elemento Obbligatorio";
	public final static String LABEL_PARAMETRO_PATTERN = "Pattern";
	public final static String LABEL_PARAMETRO_CONTENT_TYPE = "Content Type";
	public final static String LABEL_PARAMETRO_PARAMETRI = "Parametri";
	public final static String LABEL_PARAMETRO_APPLICA_MTOM = "Applica MTOM";
	public final static String LABEL_PARAMETRO_API_CONTESTO = "Riferito in";
	public final static String LABEL_PARAMETRO_API_IMPLEMENTAZIONE = "Implementazione API";
	public final static String LABEL_PARAMETRO_RUOLO = "Nome";
	public final static String LABEL_PARAMETRO_RUOLO_TIPOLOGIA = "Fonte";
	public final static String LABEL_PARAMETRO_RUOLO_TIPOLOGIA_XACML_POLICY = "Fonte Ruoli";
	public final static String LABEL_PARAMETRO_RUOLO_MATCH = "Ruoli Richiesti";
	public final static String LABEL_PARAMETRO_RUOLO_MATCH_ALL = "tutti";
	public final static String LABEL_PARAMETRO_RUOLO_MATCH_ANY = "almeno uno";
	public final static String LABEL_PARAMETRO_RUOLO_CONTESTO = "Contesto";
	public final static String LABEL_PARAMETRO_SCOPE = "Nome";
	public final static String LABEL_PARAMETRO_SCOPE_TIPOLOGIA = "Fonte";
	public final static String LABEL_PARAMETRO_SCOPE_TIPOLOGIA_XACML_POLICY = "Fonte Scope";
	public final static String LABEL_PARAMETRO_SCOPE_MATCH = "Scope Richiesti";
	public final static String LABEL_PARAMETRO_SCOPE_MATCH_ALL = "tutti";
	public final static String LABEL_PARAMETRO_SCOPE_MATCH_ANY = "almeno uno";
	public final static String LABEL_PARAMETRO_SCOPE_CONTESTO = "Contesto";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI = "Controllo degli Accessi";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO = "Accesso API";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE = "Autenticazione";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_CUSTOM = "Autenticazione - controlli custom";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_TRASPORTO = "Trasporto";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_CANALE = "Canale";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTENTICAZIONE_TOKEN = "Required Claims"; //"Token";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE = "Autorizzazione";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CANALE = "Autorizzazione Canale";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_MESSAGGIO = "Autorizzazione Messaggio";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CUSTOM = "Autorizzazione - controlli custom";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_PERSONALIZZATA = "Personalizzata";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI = "Autorizzazione Contenuti";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CONTROLLI_AUTORIZZAZIONE = "Autorizzazione Contenuti - controlli richiesti";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CONTROLLI_AUTORIZZAZIONE_CUSTOM = "Autorizzazione Contenuti - controlli custom";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_GESTIONE_TOKEN = "Autenticazione Token"; //"Gestione Token";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE = "Stato";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_BASIC_FORWARD = "Forward Authorization";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_HEADER = ModalitaIdentificazione.HEADER_BASED.getLabelParametro();
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_FORM = ModalitaIdentificazione.FORM_BASED.getLabelParametro();
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TOKEN_CLAIM = ModalitaIdentificazione.TOKEN.getLabelParametro();
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TOKEN_CLAIM_PERSONALIZZATO = "seleziona altro claim";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TOKEN_CLAIM_PERSONALIZZATO_ESTESO = "Nome del Claim";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TOKEN_CLAIM_PERSONALIZZATO_NOME = "Nome";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_ESPRESSIONE = ModalitaIdentificazione.URL_BASED.getLabelParametro();
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_FORWARD_HEADER = "Forward Header";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_FORWARD_FORM = "Forward Parametro Url";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE = "Opzionale";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_PERSONALIZZATA = "Personalizzata";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER = "Issuer";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID = "ClientId";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT = "Subject";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME = "Username";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL = "eMail";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_POSIZIONE = "Posizione";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_NOMI_STANDARD_OAS3 = "Nomi Standard OAS3";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_FORWARD = "Forward";
	public final static String LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_APIKEY_FORWARD_PREFIX = "Forward ";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE = "Stato";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SERVIZI_APPLICATIVI_SUFFIX = "Richiedente";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SERVIZI_APPLICATIVI = "Autorizzazione per Richiedente";//"Applicativi Autenticati";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_SOGGETTI = "Autorizzazione per Richiedente";//"Soggetti Autenticati";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI_SUFFIX = "Ruoli";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI = "Autorizzazione per Ruoli";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE_SUFFIX = "Scope";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE = "Autorizzazione per Scope";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_SUBTITLE_SUFFIX = "Token Claims";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_SUBTITLE = "Autorizzazione per Token Claims";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN = "Claims";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_XACML_SUFFIX = "XACML Policy";
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_XACML = "Autorizzazione XACML";
	public final static String LABEL_PARAMETRO_AUTORIZZAZIONE_CONTENUTI = "";
	public final static String LABEL_PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO = "Stato";
	public final static String LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA = "Correlazione Applicativa";
	public final static String LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA_RICHIESTA = "Regole";
	public final static String LABEL_PARAMETRO_CORRELAZIONE_APPLICATIVA_RISPOSTA = "Regole";
	public final static String LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_LABEL = "Scadenza (minuti)";
	public final static String LABEL_PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA_NOTE = "Definisce una scadenza per il riuso del solito ID Protocollo";
	public final static String LABEL_PARAMETRO_SERVICE_BINDING_API = "Tipo API";
	public final static String LABEL_PARAMETRO_STATO_PACKAGE = "Stato";
	public final static String LABEL_PARAMETRO_SERVICE_BINDING = "Tipo";
	public final static String LABEL_PARAMETRO_SERVICE_BINDING_SOAP = "Soap";
	public final static String LABEL_PARAMETRO_SERVICE_BINDING_REST = "Rest";
	public final static String LABEL_PARAMETRO_PROTOCOLLO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public final static String LABEL_PARAMETRO_SERVICE_BINDING_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public final static String LABEL_PARAMETRO_HTTP_METHOD_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public final static String LABEL_PARAMETRO_HTTP_METHOD_COMPACT = "Method";
	public final static String LABEL_PARAMETRO_HTTP_METHOD = "HTTP Method";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE = "Tipo Messaggio";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_DEFAULT = "Default";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_SOAP_11 = "Soap 1.1";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_SOAP_12 = "Soap 1.2";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_XML = "Xml";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_JSON = "Json";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_BINARY = "Binary";
	public final static String LABEL_PARAMETRO_MESSAGE_TYPE_MIME_MULTIPART = "MIME-Multipart";
	public final static String LABEL_PARAMETRO_INTERFACE_TYPE = "Formato Specifica";
	public final static String LABEL_PARAMETRO_INTERFACE_TYPE_WSDL_11 = "Wsdl 1.1";
	public final static String LABEL_PARAMETRO_INTERFACE_TYPE_WADL = "Wadl";
	public final static String LABEL_PARAMETRO_INTERFACE_TYPE_SWAGGER_2 = "Swagger 2";
	public final static String LABEL_PARAMETRO_INTERFACE_TYPE_OPEN_API_3 = "Open API 3";
	public final static String LABEL_PARAMETRO_SCHEMI_XSD = "Schemi XSD";
	public final static String LABEL_PARAMETRO_REGISTRO_OPENSPCOOP = "Registro API";
	public static final String LABEL_PATTERN = "Pattern";
	public static final String LABEL_PORTA_APPLICATIVA_CON_PARAMETRI = "Porta Applicativa {0}";
	public static final String LABEL_PORTA_DELEGATA_CON_PARAMETRI = "Porta Delegata {0}";
	public static final String LABEL_NON_DEFINITO = "Non definito";
	public static final String LABEL_PARAMETRO_AZIONE = "Azione";
	public final static String LABEL_PARAMETRO_AZIONI = "Azioni";
	public final static String LABEL_PARAMETRO_AZIONI_CONFIG_DI = "Azioni di ";
	public static final String LABEL_PARAMETRO_RISORSA = "Risorsa";
	public final static String LABEL_PARAMETRO_RISORSE = "Risorse";
	public final static String LABEL_PARAMETRO_RISORSE_CONFIG_DI = "Risorse di ";
	public final static String LABEL_PARAMETRO_PORTE_NOME_GRUPPO = "Nome Gruppo";
	public final static String LABEL_DEL_GRUPPO = " del gruppo ";
	public final static String LABEL_DEL_CONNETTORE = " del connettore ";
	public final static String LABEL_PARAMETRO_PORTA_AZIONE_MODALITA = "Modalità Identificazione Azione";
	public final static String LABEL_PARAMETRO_PORTA_RISORSA_MODALITA = "Modalità Identificazione Risorsa";
	public final static String LABEL_PARAMETRO_PORTA_QUALSIASI_AZIONE = "Tutte le azioni del servizio";
	public final static String LABEL_PARAMETRO_PORTA_QUALSIASI_RISORSA = "Tutte le risorse del servizio";
	public final static String LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP = "Configurazione abilitata (Clicca per disabilitare)";
	public final static String LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP = "Configurazione disabilitata (Clicca per abilitare)";
	public final static String LABEL_PARAMETRO_PORTA_ABILITATO_TOOLTIP_NO_ACTION = "Configurazione abilitata";
	public final static String LABEL_PARAMETRO_PORTA_DISABILITATO_TOOLTIP_NO_ACTION = "Configurazione disabilitata";
	public final static String LABEL_PARAMETRO_PORTA_CONFERMA_ABILITAZIONE_CONFIG_DI = "Conferma abilitazione di ";
	public final static String LABEL_PARAMETRO_PORTA_CONFERMA_DISABILITAZIONE_CONFIG_DI = "Conferma disabilitazione di ";
	public final static String LABEL_PARAMETRO_DEFAULT_ALL_AZIONI_RIDEFINITE_TOOLTIP = "Tutte le azioni sono state riassegnate";
	public final static String LABEL_PARAMETRO_DEFAULT_ALL_RISORSE_RIDEFINITE_TOOLTIP = "Tutte le risorse sono state riassegnate";
	public final static String LABEL_AGGIUNTA_AZIONI_COMPLETATA = "Tutti le azioni disponibili sono già state riassegnate in un gruppo";
	public final static String LABEL_AGGIUNTA_RISORSE_COMPLETATA = "Tutti le risorse disponibili sono già state riassegnate in un gruppo";
	public final static String LABEL_TUTTE_AZIONI_DEFAULT = "Tutte le azioni dell'API";
	public final static String LABEL_TUTTE_RISORSE_DEFAULT = "Tutte le risorse dell'API";
	public final static String LABEL_PARAMETRO_SOGGETTO = "Soggetto";
	public final static String LABEL_PARAMETRO_SOGGETTI = "Soggetti";
	public final static String LABEL_SOGGETTI = "Soggetti";
	public final static String LABEL_APPLICATIVI = "Applicativi";
	public final static String LABEL_INTEGRAZIONE = "Integrazione";
	public final static String LABEL_METADATI = "Metadati";
	public final static String LABEL_METADATI_INFO = "Per consentire lo scambio di informazioni, funzionali all’integrazione tra applicativi e gateway, sono previste differenti strutture dati, indicate con il termine Header di Integrazione, che possono essere attivate tramite i tipi descritti di seguito<BR/>"+
			"Il Gateway dispone di una sua configurazione di default per la generazione degli header. Tramite il campo '"+CostantiControlStation.LABEL_METADATI+"' è possibile ridefinire tale comportamento.<BR/>"+
			"<BR/>"+
			"<b>trasporto</b>: le informazioni sono veicolate all'interno di header HTTP<BR/>"+
			"<b>urlBased</b>: le informazioni sono veicolate come parametri della url<BR/>"+
			"<b>soap</b>: le informazioni sono incluse in uno specifico header SOAP proprietario di GovWay<BR/>"+
			"<b>wsa</b>: le informazioni sono incluse in un header SOAP secondo il formato standard WS-Addressing<BR/>"+
			"<b>none</b>: non viene utilizzata alcuna modalità; questo tipo serve a sovrascrivere le impostazioni di default del Gateway<BR/>"+
			"<b>trasportoExt, urlBasedExt, soapExt, wsaExt</b>: rispetto alla descrizione fornita precedentemente, le informazioni vengono veicolate anche fuori dal dominio di gestione<BR/>"+
			"<BR/>"+
			"Sono anche disponibili modalità che consentono di definire tramite un template freemaker o velocity (definito a livello globale) come le informazioni siano inserite nel messaggio.<BR/>"+
			"Il tipo di template (freemarker/velocity) e il path del file template possono essere specifici per API indicandoli nelle proprietà 'integrazione.template.richiesta/risposta.tipo' e 'integrazione.template.richiesta/risposta.file'<BR/>"+
			"<b>template</b>: il template viene applicato sia alla richiesta che alla risposta<BR/>"+
			"<b>template-request</b>: il template viene applicato solamente alla richiesta<BR/>"+
			"<b>template-response</b>: il template viene applicato solamente alla risposta<BR/>"+
			"<BR/>"+
			"Sono infine disponibili modalità che generano gli header di integrazione compatibili con le versioni di OpenSPCoop 2.x e 1.x:<BR/>"+
			"<b>openspcoop2-trasporto</b> o <b>openspcoop1-trasporto</b>: le informazioni sono veicolate all'interno di header HTTP senza prefisso 'X-'<BR/>"+
			"<b>openspcoop2-x-trasporto</b> o <b>openspcoop1-x-trasporto</b>: le informazioni sono veicolate all'interno di header HTTP con prefisso 'X-'<BR/>"+
			"<b>openspcoop2-urlBased</b> o <b>openspcoop1-urlBased</b>: le informazioni sono veicolate come parametri della url<BR/>"+
			"<b>openspcoop2-soap</b> o <b>openspcoop1-soap</b>: le informazioni sono incluse in uno specifico header SOAP proprietario di OpenSPCoop 2.x o 1.x<BR/>"+
			"<b>openspcoop2-*Ext</b>: rispetto alla descrizione fornita precedentemente, le informazioni vengono veicolate anche fuori dal dominio di gestione<BR/>";
	
	public final static String LABEL_METADATI_INTEGRAZIONE = "Metadati Integrazione";
	public final static String LABEL_STATELESS = "Stateless";
	public final static String LABEL_GESTIONE_STATELESS = "Gestione Stateless";
	public final static String LABEL_GESTIONE_STATEFUL = "Gestione Stateful";
	public final static String LABEL_LOCAL_FORWARD = "Local Forward";
	public final static String LABEL_LOCAL_FORWARD_PA = "Porta Inbound";
	public final static String LABEL_BEHAVIOUR = "Behaviour";
	public final static String LABEL_GESTIONE_MANIFEST = "Gestione Manifest";
	public final static String LABEL_RICEVUTA_ASINCRONA_SIMMETRICA = "Ricevuta Simmetrica";
	public final static String LABEL_RICEVUTA_ASINCRONA_ASIMMETRICA = "Ricevuta Asimmetrica";
	public final static String LABEL_PARAMETRO_PORTE_STATO = "Stato";
	public final static String LABEL_PARAMETRO_PORTE_ACCETTA_MTOM = "Accetta MTOM";
	public final static String LABEL_PARAMETRO_PORTE_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_PORTE_TIPO_VALIDAZIONE = "Tipo Validazione";
	public final static String LABEL_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI = "Validazione";
	public final static String LABEL_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_CONFIG_DI = "Validazione di ";
	public final static String LABEL_PORTE_CORRELAZIONE_APPLICATIVA_ATTENZIONE = "Attenzione";
	private final static String LABEL_PORTE_CORRELAZIONE_APPLICATIVA_ATTENZIONE_MESSAGGIO_PARAM = "LUNGHEZZA_CARATTERI";
	private final static String LABEL_PORTE_CORRELAZIONE_APPLICATIVA_ATTENZIONE_MESSAGGIO = "L'identificativo applicativo estratto deve possedere una lunghezza non superiore ai "+LABEL_PORTE_CORRELAZIONE_APPLICATIVA_ATTENZIONE_MESSAGGIO_PARAM+" caratteri";
	public final static String getLABEL_PORTE_CORRELAZIONE_APPLICATIVA_ATTENZIONE_MESSAGGIO(int lenght) {
		return LABEL_PORTE_CORRELAZIONE_APPLICATIVA_ATTENZIONE_MESSAGGIO.replace(LABEL_PORTE_CORRELAZIONE_APPLICATIVA_ATTENZIONE_MESSAGGIO_PARAM, lenght+"");
	}
	public final static String LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI = "Qualsiasi";
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA_LABEL = "Attenzione";
	public final static String LABEL_CONFIGURAZIONE_INCOMPLETA = "Configurazione della sicurezza incompleta";
	public final static String LABEL_CONFIGURAZIONE_PROPERTIES_PROCEDI = "Procedi con la configurazione";
	public final static String LABEL_CONFIGURAZIONE_PROPERTIES_COMPLETA = "Completa la configurazione";
	public final static String LABEL_CONFIGURAZIONE_PROPERTIES = "Configurazione";
	public final static String LABEL_CONFIGURAZIONE_PROPERTIES_CONFIGURAZIONE_MANUALE = "Configurazione Manuale";
	public final static String LABEL_CONFIGURAZIONE_MTOM_INCOMPLETA = "Configurazione incompleta";
	public final static String LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN = "Stato";
	public final static String LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY = "Policy";
	public final static String LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE = "Token Opzionale";
	public final static String LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT = "Validazione JWT";
	public final static String LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION = "Introspection";
	public final static String LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO = "User Info";
	public final static String LABEL_PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD = "Token Forward";
	public final static String LABEL_PARAMETRO_PORTE_ELEMENTO_XML = "Elemento";
	
	public final static String LABEL_GESTIONE_MESSAGE_ENGINE = "Tipo";
	public final static String GESTIONE_MESSAGE_ENGINE_DEFAULT = "Default";
	
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA_STATO = "Stato";
	
	public final static String LABEL_STATO_ABILITATO = "Abilitato";
	public final static String LABEL_STATO_DISABILITATO = "Disabilitato";
	public final static String LABEL_STATO_WARNING_ONLY = "WarningOnly";
	
	public final static String LABEL_QUALSIASI = "Qualsiasi";
	public final static String LABEL_DEFAULT = "Default";
	public final static String LABEL_NESSUNO = "Nessuno";
	public final static String LABEL_ABILITATO = "Abilitato";
	public final static String LABEL_SI = "Si";
	public final static String LABEL_NO = "No";
	public final static String LABEL_SOAP_11 = "SOAP 1.1";
	public final static String LABEL_SOAP_12 = "SOAP 1.2";
		
	public final static String LABEL_DUMP = "Dump";
	public final static String LABEL_REGISTRAZIONE_MESSAGGI = "Registrazione Messaggi";
	public final static String LABEL_REGISTRAZIONE_MESSAGGI_CONFIG_DI = "Registrazione Messaggi di ";
	public final static String LABEL_DUMP_CONFIGURAZIONE = "Configurazione";
	
	public final static String LABEL_PARAMETRO_DUMP_STATO = "Stato";
	public final static String LABEL_PARAMETRO_DUMP_STATO_DEFAULT = "default";
	public final static String LABEL_PARAMETRO_DUMP_STATO_RIDEFINITO = "ridefinito";
	public final static String LABEL_PARAMETRO_DUMP_REALTIME = "Realtime";
	public final static String LABEL_PARAMETRO_DUMP_RICHIESTA_STATO = "Stato";
	public final static String LABEL_PARAMETRO_DUMP_RISPOSTA_STATO = "Stato";
	public final static String LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY = "Body";
	public final static String LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS = "Attachments";
	public final static String LABEL_PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS = "Headers";
	public final static String LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_BODY = "Body";
	public final static String LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS = "Attachments";
	public final static String LABEL_PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS = "Headers";
	public final static String LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY = "Body";
	public final static String LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS = "Attachments";
	public final static String LABEL_PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS = "Headers";
	public final static String LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_BODY = "Body";
	public final static String LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS = "Attachments";
	public final static String LABEL_PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS = "Headers";

	public final static String LABEL_PARAMETRO_DUMP_SEZIONE_GENERALE = "Generale";
	public final static String LABEL_PARAMETRO_DUMP_SEZIONE_RICHIESTA = "Richiesta";
	public final static String LABEL_PARAMETRO_DUMP_SEZIONE_RISPOSTA = "Risposta";
	public final static String LABEL_PARAMETRO_DUMP_SEZIONE_INGRESSO = "Ingresso";
	public final static String LABEL_PARAMETRO_DUMP_SEZIONE_USCITA = "Uscita";
	public static final String LABEL_REGISTRAZIONE_MESSAGGI_MODIFICATA_CON_SUCCESSO = "Configurazione Registrazione Messaggi modificata con successo";
	
	public final static String LABEL_PARAMETRO_REQUEST_FLOW_PROPERTIES_CONFIG_NAME = "Schema Sicurezza";
	public final static String LABEL_PARAMETRO_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME = "Schema Sicurezza";
	public final static String LABEL_PARAMETRO_PROPERTIES_CONFIG_NAME = "Nome";
	public final static String LABEL_PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY = "Policy";
	public final static String LABEL_PARAMETRO_DOCUMENTO_SICUREZZA_XACML_NUOVA_POLICY = "Nuova";
	public final static String LABEL_AGGIORNAMENTO_DOCUMENTO_SICUREZZA_XACML_POLICY = "Modifica Policy";
	public final static String LABEL_DOWNLOAD_DOCUMENTO_SICUREZZA_XACML_POLICY = "Download Policy Attuale";

	public static final String LABEL_SICUREZZA_MESSAGGIO_STATO = "Sicurezza Messaggio (Stato)";
	public static final String LABEL_SICUREZZA_MESSAGGIO_SCHEMA_RICHIESTA = "Schema Sicurezza (Richiesta)";
	public static final String LABEL_SICUREZZA_MESSAGGIO_SCHEMA_RISPOSTA = "Schema Sicurezza (Risposta)";
	public static final String LABEL_SICUREZZA_MESSAGGIO_SCHEMA_NESSUNO = "Nessuno";
	public static final String LABEL_SICUREZZA_MESSAGGIO_SCHEMA_CONFIGURAZIONE_MANUALE = "Configurazione Manuale";
	public static final String VALUE_SICUREZZA_MESSAGGIO_SCHEMA_DEFAULT = "default";
	
	public final static String LABEL_CONFIGURAZIONE_CORS = "Gestione CORS";
	public final static String LABEL_CONFIGURAZIONE_CORS_ACCESS_CONTROL = "Access Control";
	public final static String LABEL_CONFIGURAZIONE_CORS_DI = "Gestione CORS di ";
	
	public final static String LABEL_CONFIGURAZIONE_CANALE = "Canale";
	public final static String LABEL_CONFIGURAZIONE_CANALE_DI = "Canale di ";
	
	
	public final static String LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_BOTTONE = "Verifica";
	public final static String LABEL_CONFIGURAZIONE_CONNETTIVITA = "Connettività";
	public final static String LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_TITLE = "Verifica Connettività";
	public final static String LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE = "Verifica Connettività Connettore";
	public final static String LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_DI = LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE+" di ";
	public final static String LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_EFFETTUATO_CON_SUCCESSO = "Test di connettività effettuato con successo";
	public final static String LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_FALLITA = "Test di connettività fallito: ";
	public final static String LABEL_VERIFICA_CONNETTORE_VALORE_LINK = "verifica";
	public final static String LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI = "Verifica su tutti i nodi";
	public final static String LABEL_CONFIGURAZIONE_CONNETTORI_MULTIPLI = "Configurazione Connettori Multipli";
	public final static String LABEL_ELENCO_CONNETTORI_MULTIPLI = "Elenco Connettori";
	
	public final static String LABEL_CONFIGURAZIONE_RESPONSE_CACHING = "Caching Risposta";
	public final static String LABEL_CONFIGURAZIONE_RESPONSE_CACHING_DI = "Caching Risposta di ";
	
	public final static String LABEL_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLE = "Regole di Caching Risposta";
	public final static String LABEL_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLE_DI = "Regole di Caching Risposta di";
	
	public final static String LABEL_CONFIGURAZIONE_PROXY_PASS_REGOLE = "Regole di Proxy Pass";
	public final static String LABEL_CONFIGURAZIONE_PROXY_PASS_REGOLE_DI = "Regole di Proxy Pass di";
	
	public final static String LABEL_CONFIGURAZIONE_DEFAULT = "Configurazione di default del Gateway";
	public final static String LABEL_CONFIGURAZIONE_RIDEFINITA = "Configurazione ridefinita per l'API";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_STATO = "Stato";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO = "Tipo";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO_GESTITO_GATEWAY ="Gestito dal Gateway";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO_GESTITO_APPLICATIVO = "Gestito dall'Applicativo";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_TIPO_GESTITO_APPLICATIVO_DEMANDATO = "Gestione demandata all'implementazione dell'API";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS = "All Allow Origins";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS = "Allow Origins";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_HEADERS = "All Allow Request Headers";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS = "Allow Request Headers";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_METHODS = "All Allow Methods";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS = "Allow Methods";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_CREDENTIALS = "Allow Credentials";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS = "Expose Response Headers";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE = "Max Age";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE_SECONDS = "Max Age Seconds";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE_SECONDS_NOTE = "Utilizza il valore -1 per disabilitare il caching";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE = "Canale";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO = "Canale";
	
	public final static String LABEL_PARAMETRO_CORS_STATO_PORTA = "Stato";
	public final static String LABEL_PARAMETRO_CORS_STATO_PORTA_DEFAULT = "default";
	public final static String LABEL_PARAMETRO_CORS_STATO_PORTA_RIDEFINITO = "ridefinito";
	public static final String LABEL_GESTIONE_CORS_MODIFICATA_CON_SUCCESSO = "Configurazione CORS modificata con successo";
	public static final String LABEL_CONFIGURAZIONE_RESPONSE_CACHING_GENERAZIONE_HASH = "Generazione Hash";
	public static final String LABEL_GESTIONE_CANALE_MODIFICATA_CON_SUCCESSO = "Configurazione Canale modificata con successo";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO = "Stato";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_TIMEOUT = "Cache Timeout (secondi)";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE = "Dimensione Max Risposta";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE_BYTES = "Dimensione Max (kb)";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_URI_INVOCAZIONE = "URL di Richiesta";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS = "Headers";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS_NOMI_HEADERS = "Headers";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS = "URL Parameters";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS_NOMI = "URL Parameters";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_PAYLOAD = "Payload";
	public static final String LABEL_GESTIONE_RESPONSE_CACHING_MODIFICATA_CON_SUCCESSO = "Configurazione Response Caching modificata con successo";
	public final static String LABEL_PARAMETRO_RESPONSE_CACHING_STATO_PORTA = "Stato";
	public final static String LABEL_PARAMETRO_RESPONSE_CACHING_STATO_PORTA_DEFAULT = "default";
	public final static String LABEL_PARAMETRO_RESPONSE_CACHING_STATO_PORTA_RIDEFINITO = "ridefinito";
	public final static String LABEL_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL = "Cache Control";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_CACHE = "No Cache";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_MAX_AGE = "Max Age";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_STORE = "No Store";
	public final static String NOTE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS_NOMI_HEADERS = "Indicare gli Headers da utilizzare per il calcolo dell'Hash";
	public final static String NOTE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS_NOMI = "Indicare i parametri della URL da utilizzare per il calcolo dell'Hash";
	
	public final static String LABEL_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIOME_AVANZATA = "Configurazione Avanzata";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA = "Regola";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLE = "Regole";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MIN = "Min";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MAX = "Max";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE = "Codice Risposta";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_FAULT = "Fault";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_CACHE_TIMEOUT_SECONDS = "Cache Timeout (Secondi)";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA = "Regola";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLE = "Regole";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_STATO = "Stato";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_NOME = "Nome";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_DESCRIZIONE = "Descrizione";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_POSIZIONE = "Ordine";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REG_EXPR = "Espressione Regolare";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT = "Regola";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO = "Contesto";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL = "Base URL";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_PROFILO = "Profilo";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SOGGETTO = "Soggetto";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_RUOLO = "Ruolo";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SERVICE_BINDING = "Tipo API";
	
	public final static String LABEL_PROXY_PASS_REGOLA_CRITERI_APPLICABILITA = "Criteri di Applicabilità";
	public final static String LABEL_PROXY_PASS_REGOLA_NUOVA_URL = "Nuova URL di Invocazione";
	
	public final static String MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT_STRINGA_LIBERA = "Stringa utilizzata per individuare l'applicabilità della regola.<BR/>Si ha un'applicabilità se il contesto dell'API (url di invocazione senza la Base URL) inizia con la stringa fornita"; 
	public final static String MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT_REGEXP = "Espressione Regolare utilizzata per individuare l'applicabilità della regola.<BR/>L'espressione viene verificata sull contesto dell'API (url di invocazione senza la Base URL)"; 
	public final static String MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO = "Indica il contesto da utilizzare dopo la Base URL";
	public final static String MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL = "Permette di ridefinire la Base URL utilizzata rispetto a quanto definito nella configurazione generale";
	public final static String MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_EXPR_DATI_DINAMICI_REGXP = "<BR/><BR/>È possibile utilizzare la keyword '${posizione}' per impostare un valore dinamico individuato tramite l'espressione regolare fornita.<BR/>Il primo match, all'interno dell'espressione regolare, è rappresentata da '${0}'.<BR/>Ad esempio: http://server:8080/${0}/altro/${1}/";
	public final static String MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_EXPR_DATI_DINAMICI_CANALE = "<BR/><BR/>È possibile utilizzare la keyword '${canale}' per utilizzare l'identificativo del canale associato all'API.<BR/>Ad esempio: http://server:8080/${canale}/";
	public final static String MESSAGGIO_INFO_PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_EXPR_DATI_DINAMICI_TAG = "<BR/><BR/>È possibile utilizzare la keyword '${tag}' per utilizzare l'identificativo del tag associato all'API. Poichè ad un'API è possibile associare più tag, nel caso precedente verrà utilizzato quello alla prima posizione, altrimenti è selezionabile il tag desiderato tramite l'espressione ${tag[posizione]}.<BR/>Il primo tag, all'interno della lista, è rappresentata da '${tag[0]}'.<BR/>Ad esempio: http://server:8080/${tag[0]}/";

	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI = "Trasformazioni";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_DI = "Trasformazioni di ";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_REGOLE_TRASFORMAZIONE = "Regole di Trasformazione";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA = "Applicabilit&agrave;";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_TRASFORMAZIONE = "Trasformazione";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA = "Richiesta";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTE = "Risposte";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTE_DI = "Risposte di ";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA = "Risposta";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADERS = "HTTP Headers";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADERS_DI = "HTTP Headers di ";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER = "HTTP Header";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER = "HTTP Header";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADERS = "HTTP Headers";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADERS_DI = "HTTP Headers di ";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRI = "URL Parameters";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRI_DI = "URL Parameters di ";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO = "URL Parameter";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_TRASPORTO = "Trasporto";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_CONTENUTO = "Contenuto";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP = "Trasformazione SOAP";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_REST = "Trasformazione Rest";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_TRASPORTO = "Trasporto";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENUTO = "Contenuto";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP = "Trasformazione SOAP";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_REST = "Trasformazione Rest";
	
	
	public final static String LABEL_PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE = "Id";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_POSIZIONE = "Ordine";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_NOME = "Nome";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOME = "Nome";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_STATO = "Stato";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_RISORSE = "Risorse";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI = "Azioni";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_TRUE = "Qualsiasi";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_FALSE = "Azioni selezionate";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_RISORSE_ALL_VALUE_FALSE = "Risorse selezionate";
	public final static String [] LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUES = new String [] {
			LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_TRUE,
			LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_FALSE
	};
	public final static String [] LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_RISORSE_ALL_VALUES = new String [] {
			LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_TRUE,
			LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_RISORSE_ALL_VALUE_FALSE
	};
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_CT = "Content Type";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_PATTERN = "Pattern";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_ENABLED = "Abilitato";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO = "Tipo Conversione";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE = "Template";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_NUOVO_TEMPLATE = "Nuovo";
	public final static String LABEL_AGGIORNAMENTO_DOCUMENTO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE = "Modifica Template";
	public final static String LABEL_DOWNLOAD_DOCUMENTO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE = "Download Template Attuale";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE = "Content Type";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE_ATTACHMENT = "Content Type Attachment";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_TRANSFORMATION = "Abilitato";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_METHOD = "HTTP Method";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH = "Path";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH_NOTE = "Ridefinire il Method e/o il Path solamente per modificarli rispetto alla richiesta originale";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_TRANSFORMATION = "Abilitato";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_VERSION = "Versione";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ACTION = "SOAP Action";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE = "Imbustamento SOAP";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACH = "Attachment";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TITLE_BODY = "SOAP Body";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO = "Tipo Conversione";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE = "Template";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_NUOVO_TEMPLATE = "Nuovo";
	public final static String LABEL_AGGIORNAMENTO_DOCUMENTO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE = "Modifica Template";
	public final static String LABEL_DOWNLOAD_DOCUMENTO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE = "Download Template Attuale";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS = "Codice Risposta";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MIN = "Min";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MAX = "Max";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_CT = "Content Type";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_PATTERN = "Pattern";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_ENABLED = "Abilitato";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO = "Tipo Conversione";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE = "Template";
	public final static String LABEL_DOWNLOAD_DOCUMENTO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE = "Download Template Attuale";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE = "Content Type";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE_ATTACHMENT = "Content Type Attachment";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_RETURN_CODE = "Codice Risposta";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_TRANSFORMATION = "Abilitato";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE = "Imbustamento SOAP";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_AS_ATTACH = "Attachment";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TITLE_BODY = "SOAP Body";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO = "Tipo Conversione";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE = "Template";
	public final static String LABEL_DOWNLOAD_DOCUMENTO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE = "Download Template Attuale";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_VALORE = "Valore";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_NOME = "Nome";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_TIPO = "Operazione";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_IDENTIFICAZIONE = "Identificazione Fallita";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE = "Valore";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_NOME = "Nome";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_TIPO = "Operazione";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_IDENTIFICAZIONE = "Identificazione Fallita";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_VALORE = "Valore";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_NOME = "Nome";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_TIPO = "Operazione";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_IDENTIFICAZIONE = "Identificazione Fallita";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_BLOCCA = "Termina con errore";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_IGNORA_HEADER = "Continua senza header";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_IGNORA_PARAMETRO = "Continua senza parametro";
		
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE = "Permette di definire il comportamento del Gateway quando non riesce a risolvere parti dinamiche contenute nel valore indicato.<br/>Le configurazioni utilizzabili sono:";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORE_BLOCCA = "<b>"+LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_BLOCCA+"</b>: la transazione termina con un errore che riporta la fallita risoluzione della parte dinamica indicata per il valore;";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORE_IGNORA_HEADER = "<b>"+LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_IGNORA_HEADER+"</b>: la transazione continua senza completare la gestione dell'header.";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORE_IGNORA_PARAMETRO = "<b>"+LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_IGNORA_PARAMETRO+"</b>: la transazione continua senza completare la gestione del parametro della url.";
	public final static List<String> LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORI_HEADER = new ArrayList<>();
	static {
		LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORI_HEADER.add(LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORE_BLOCCA);
		LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORI_HEADER.add(LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORE_IGNORA_HEADER);
	}	
	public final static List<String> LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORI_PARAMETRO = new ArrayList<>();
	static {
		LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORI_PARAMETRO.add(LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORE_BLOCCA);
		LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORI_PARAMETRO.add(LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOTE_VALORE_IGNORA_PARAMETRO);
	}	
	public final static String LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_ALL = "Registra qualsiasi esito";
	public final static String LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK = "Completate con successo";
	public final static String LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT = "Fault Applicativo";
	public final static String LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE = "Fallite";
	public final static String LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE_FAULT = "Fallite - Fault Applicativo";
	public final static String LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_SCARTATE = "Scartate";
	
	public final static String LABEL_CONFIGURAZIONE_RISORSA_NUMERO_RICHIESTE = "Numero Richieste";
	public final static String LABEL_CONFIGURAZIONE_RISORSA_NUMERO_RICHIESTE_SIMULTANEE = "Numero Richieste Simultanee";
	public final static String LABEL_CONFIGURAZIONE_RISORSA_OCCUPAZIONE_BANDA = "Occupazione Banda";
	public final static String LABEL_CONFIGURAZIONE_RISORSA_TEMPO_MEDIO_RISPOSTA = "Tempo Medio Risposta";
	public final static String LABEL_CONFIGURAZIONE_RISORSA_COMPLESSIVO_RISPOSTA = "Tempo Complessivo Risposta";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SPOSTA_SU = "Sposta su";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SPOSTA_GIU = "Sposta gi&ugrave;";
		
	// POLICY TIPO
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO = "Tipo";
	
	public final static String DEFAULT_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO = "qualsiasi";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public final static String PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_BUILT_IN = "built-in";
	public final static String PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_UTENTE = "utente";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_BUILT_IN = "Built-in";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_UTENTE = "Utente";
	public final static String[] LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_VALORI = {
			DEFAULT_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO,
			PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_BUILT_IN,
			PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_UTENTE
	};
	public final static String[] LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_LABELS = {
			LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_QUALSIASI,
			LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_BUILT_IN,
			LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_UTENTE
	};
	
	public final static String LABEL_PARAMETRO_AUTENTICAZIONE_CUSTOM_PROPERTIES = "Propriet&agrave;";
	public final static String LABEL_PARAMETRO_AUTORIZZAZIONE_CUSTOM_PROPERTIES = "Propriet&agrave;";
	public final static String LABEL_PARAMETRO_AUTORIZZAZIONE_CONTENUTI_CUSTOM_PROPERTIES = "Propriet&agrave;";
	
	
	public final static String LABEL_PARAMETRO_ABILITA_USO_APPLICATIVO_SERVER = "Utilizza Applicativo Server";
	public final static String LABEL_PARAMETRO_ID_APPLICATIVO_SERVER = "Applicativo";
	
	public final static String LABEL_IN_USO_COLONNA_HEADER = "Uso";
	public final static String LABEL_IN_USO_TOOLTIP = "Riferimenti";
	public final static String LABEL_IN_USO_BODY_HEADER_RISULTATI = "Risultati della ricerca";
	
	public final static String LABEL_CONFIGURAZIONE_CANALE_DEFAULT = "Configurazione di default del Gateway";
	public final static String LABEL_CONFIGURAZIONE_CANALE_DEFAULT_API = "Configurazione di default dell'API";
	public final static String LABEL_CONFIGURAZIONE_CANALE_RIDEFINITO_API = "Configurazione ridefinita per l'API";
	public final static String LABEL_CONFIGURAZIONE_CANALE_RIDEFINITO_EROGAZIONE = "Configurazione ridefinita per l'erogazione";
	public final static String LABEL_CONFIGURAZIONE_CANALE_RIDEFINITO_FRUIZIONE = "Configurazione ridefinita per la fruizione";
	
	
	// POLICY RISORSA TIPO
	
	public final static boolean USE_SELECT_LIST_SEPARATE_METRICHE = false;
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA_TIPO = "Metrica";
	
	public final static TipoRisorsaPolicyAttiva DEFAULT_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_RISORSA_TIPO_VALUE = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE;
	
	public final static String[] LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_RISORSE_VALORI = {
			TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE.getValue(),
			TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE.getValue(),
			TipoRisorsaPolicyAttiva.OCCUPAZIONE_BANDA.getValue(),
			TipoRisorsaPolicyAttiva.TEMPO_MEDIO_RISPOSTA.getValue(),
			TipoRisorsaPolicyAttiva.TEMPO_COMPLESSIVO_RISPOSTA.getValue(),
			TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO.getValue(),
			TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE.getValue(),
			TipoRisorsaPolicyAttiva.NUMERO_FAULT_APPLICATIVI.getValue(),
			TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI.getValue()
	};
	public final static String[] LABEL_PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPI_RISORSE_LABELS = {
			LABEL_CONFIGURAZIONE_RISORSA_NUMERO_RICHIESTE,
			LABEL_CONFIGURAZIONE_RISORSA_NUMERO_RICHIESTE_SIMULTANEE,
			LABEL_CONFIGURAZIONE_RISORSA_OCCUPAZIONE_BANDA,
			LABEL_CONFIGURAZIONE_RISORSA_TEMPO_MEDIO_RISPOSTA,
			LABEL_CONFIGURAZIONE_RISORSA_COMPLESSIVO_RISPOSTA,
			LABEL_CONFIGURAZIONE_RISORSA_NUMERO_RICHIESTE+ " "+LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK,	
			LABEL_CONFIGURAZIONE_RISORSA_NUMERO_RICHIESTE+ " "+LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE,	
			"Numero Fault Applicativi",	
			LABEL_CONFIGURAZIONE_RISORSA_NUMERO_RICHIESTE+ " "+LABEL_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE+" o Fault Applicativi",	
	};
	
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO = "Stato";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_DISABILITATO = "disabilitato";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_ABILITATO = "abilitato";
	public final static String LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM = "custom";

	public final static String[] PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_LABELS = {
			LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_DISABILITATO,
			LABEL_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_ABILITATO
	};
	
	/** PARAMETERS **/
	
	public final static String PARAMETRO_ID = "id";
	public final static String PARAMETRO_ID_SOGGETTO = "idsogg";
	public final static String PARAMETRO_ID_PORTA = "idPorta";
	public final static String PARAMETRO_ID_ASPS = "idAsps";
	public final static String PARAMETRO_ID_FRUIZIONE = "myId";
	public final static String PARAMETRO_NOME = "nome";
	public final static String PARAMETRO_NOME_PORTA = "nomePorta";
	public final static String PARAMETRO_SERVIZIO_APPLICATIVO = "servizioApplicativo";
	public final static String PARAMETRO_VALORE = "valore";
	public final static String PARAMETRO_PROTOCOLLO = "protocollo";
	public final static String PARAMETRO_ACCESSO_DA_CHANGE = "accessoDaChange";
	public final static String PARAMETRO_MESSAGE_SECURITY = "messageSecurity";
	public final static String PARAMETRO_MTOM_RICHIESTA = "mtomReq";
	public final static String PARAMETRO_MTOM_RISPOSTA = "mtomRes";
	public final static String PARAMETRO_OBBLIGATORIO = "obbl";
	public final static String PARAMETRO_CONTENT_TYPE = "contentT";
	public final static String PARAMETRO_PATTERN = "pattern";
	public final static String PARAMETRO_APPLICA_MTOM_RICHIESTA = "applicaMTOMReq";
	public final static String PARAMETRO_APPLICA_MTOM_RISPOSTA = "applicaMTOMRes";
	public final static String PARAMETRO_USAIDSOGG = "usaidsogg";
	public final static String PARAMETRO_EXTENDED_FORM_ID = "extendedFormUniqueId";
	public final static String PARAMETRO_CONTENT_DISPOSITION = "Content-Disposition";
	public final static String PREFIX_CONTENT_DISPOSITION = "form-data; name=\"";
	public final static String SUFFIX_CONTENT_DISPOSITION = "\"";
	public final static String PREFIX_FILENAME = "filename=\"";
	public final static String SUFFIX_FILENAME = "\"";
	public final static String PARAMETRO_RUOLO = "ruolo";
	public final static String PARAMETRO_RUOLO_TIPOLOGIA = "ruoloTipologia";
	public final static String PARAMETRO_RUOLO_MATCH = "ruoloMatch";
	public final static String PARAMETRO_SCOPE = "scope";
	public final static String PARAMETRO_SCOPE_MATCH = "scopeMatch";
	public final static String PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO = "controlloAccessiStato";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE = "autenticazione";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO = "autenticazionePrincipalTipo";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE_PARAMETRO_LIST = "autenticazioneParametro";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM = "autenticazioneCustom";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE = "autenticazioneOpzionale";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER = "autenticazioneIssuer";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID = "autenticazioneClientId";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT = "autenticazioneSubject";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME = "autenticazioneUsername";
	public final static String PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL = "autenticazioneEMail";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE = "autorizzazione";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM = "autorizzazioneCustom";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE = "autorizzazioneAutenticazione";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI = "autorizzazioneRuoli";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE = "autorizzazioneScope";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN = "autorizzazioneToken";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS = "autorizzazioneTokenOptions";
	public final static String PARAMETRO_PORTE_TRACCIAMENTO_ESITO = "portaEsiti";
	public final static String PARAMETRO_AUTORIZZAZIONE_CONTENUTI = "autorizzazioneContenuti";
	public final static String PARAMETRO_AUTORIZZAZIONE_CONTENUTI_STATO = "authContenutiStato";
	public final static String PARAMETRO_AUTORIZZAZIONE_CONTENUTI_PROPERTIES = "authContenutiProp";
	public final static String PARAMETRO_SCADENZA_CORRELAZIONE_APPLICATIVA = "scadcorr";
	public final static String PARAMETRO_APPLICA_MODIFICA = "applicaMod";
	public final static String PARAMETRO_ABILITA = "abilita";
	public final static String PARAMETRO_SOGGETTO = "soggt";
	public final static String PARAMETRO_SERVIZIO_APPLICATIVO_AUTORIZZATO = "saAuthz";
	public final static String PARAMETRO_PORTE_TIPO_VALIDAZIONE = "tipo_validazione";
	public final static String PARAMETRO_PORTE_XSD = "xsd";
	public final static String PARAMETRO_PORTE_APPLICA_MTOM = PARAMETRO_APPLICA_MTOM_RICHIESTA;
	public final static String PARAMETRO_PROVIDER = "provider";
	public final static String PARAMETRO_CONTROLLO_FIRST_TIME = "paramFirstTime";
	public final static String PARAMETRO_PORTE_GESTIONE_TOKEN = "gestioneToken";
	public final static String PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY = "gtPolicy";
	public final static String PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE = "gtOpzionale";
	public final static String PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT = "gtValidazione";
	public final static String PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION = "gtIntrospection";
	public final static String PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO = "gtUserInfo";
	public final static String PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD = "gtTokenForward";
	public final static String PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY = "docSicXacmlPol";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA = "autorizzazioneModIPA";
	public final static String PARAMETRO_PORTE_AUTORIZZAZIONE_MODIPA_STATO = "autorizzazioneModIPAStato";
	public final static String PARAMETRO_ABILITA_USO_APPLICATIVO_SERVER = "saServerEnabled";
	public final static String PARAMETRO_ID_APPLICATIVO_SERVER = "saServer";
	
	public final static String PARAMETRO_RESET_SEARCH = "resetSearch";
	
	public final static String PARAMETRO_SERVICE_BINDING = org.openspcoop2.protocol.engine.constants.Costanti.CONSOLE_PARAMETRO_SERVICE_BINDING;
	public final static String PARAMETRO_SERVICE_BINDING_SEARCH = "serviceBindingSearch";
	public final static String PARAMETRO_MESSAGE_TYPE = "messageType";
	public final static String PARAMETRO_INTERFACE_TYPE = "interfaceType";
		
	public final static String PARAMETRO_ELEMENTO_XML = "elemxml";
	public final static String PARAMETRO_MODE_CORRELAZIONE_APPLICATIVA = "mode";
	public final static String PARAMETRO_ID_CORRELAZIONE= "idcorr";
		
	public final static String ATTRIBUTO_CONFIGURAZIONE_PARENT = "portaPar";
	public final static int ATTRIBUTO_CONFIGURAZIONE_PARENT_NONE = 0;
	public final static int ATTRIBUTO_CONFIGURAZIONE_PARENT_SOGGETTO = 1;
	public final static int ATTRIBUTO_CONFIGURAZIONE_PARENT_CONFIGURAZIONE = 2;
	
	public final static String PARAMETRO_AZIONE = "azione";
	public final static String PARAMETRO_AZIONI = "azioni";
	public final static int RIGHE_MULTISELECT_AZIONI = 10;
	public final static String PARAMETRO_NOME_GRUPPO = "nomeGruppo";
	
	public final static String PARAMETRO_DUMP_STATO = "dumpStato";
	public final static String PARAMETRO_DUMP_REALTIME = "dumpRealTime";
	public final static String PARAMETRO_DUMP_RICHIESTA_STATO = "dumpStatoReq";
	public final static String PARAMETRO_DUMP_RISPOSTA_STATO = "dumpStatoRes";
	public final static String PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY = "dumpReqInBody";
	public final static String PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS = "dumpReqInAtt";
	public final static String PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS = "dumpReqInHead";
	public final static String PARAMETRO_DUMP_RICHIESTA_USCITA_BODY = "dumpReqOutBody";
	public final static String PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS = "dumpReqOutAtt";
	public final static String PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS = "dumpReqOutHead";
	public final static String PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY = "dumpResInBody";
	public final static String PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS = "dumpResInAtt";
	public final static String PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS = "dumpResInHead";
	public final static String PARAMETRO_DUMP_RISPOSTA_USCITA_BODY = "dumpResOutBody";
	public final static String PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS = "dumpResOutAtt";
	public final static String PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS = "dumpResOutHead";
	
	public final static String PARAMETRO_REQUEST_FLOW_PROPERTIES_CONFIG_NAME = "propertiesConfigNameReq";
	public final static String PARAMETRO_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME = "propertiesConfigNameRes";
	public final static String PARAMETRO_PROPERTIES_CONFIG_NAME = "propertiesConfigName";
	
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_STATO = "corsStato";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_TIPO = "corsTipo";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS= "corsAAllOrig";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS = "corsAllOrig";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_HEADERS= "corsAAllHdr";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS = "corsAllHead";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_METHODS= "corsAAllMeth";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS = "corsAllMeth";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_CREDENTIALS = "corsAllCred";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS = "corsExpHead";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE = "corsMaxAge";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE_SECONDS = "corsMaxAgeSec";
	public final static String PARAMETRO_CONFIGURAZIONE_CORS_STATO_PORTA = "corsStatoPorta";
	
	public final static String PARAMETRO_CONFIGURAZIONE_CANALI_CANALE = "canale";
	public final static String PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO = "canaleStato";
	
	public final static String PARAMETRO_VERIFICA_CONNETTORE_ID = "connettoreId";
	public final static String PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI = "connettoreAccessoDaGruppi";
	public final static String PARAMETRO_VERIFICA_CONNETTORE_REGISTRO = "connettoreRegistro";
	public final static String PARAMETRO_VERIFICA_CONNETTORE_NODO = "connettoreNodo";
	public final static String PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI = "connettoreAccessoDaCM";
	
	public final static String PARAMETRO_ID_TAB = "idTab";
	public final static String PARAMETRO_RESET_ID_TAB = "rIdTab";
	public final static String PARAMETRO_ID_CONN_TAB = "idConnTab";
	public final static String PARAMETRO_RESET_ID_CONN_TAB = "rIdConnTab";
	
	public final static String PARAMETRO_API_PAGE_INFO = "fromApiPageInfo";
	
	public final static String PARAMETRO_CONFIGURAZIONE_DATI_INVOCAZIONE = "configurazioneDatiInvocazione";
	public final static String PARAMETRO_CONFIGURAZIONE_ALTRO_PORTA = "configurazioneAltroPorta";
	public final static String PARAMETRO_CONFIGURAZIONE_ALTRO_API = "configurazioneAltroApi";
	public final static String PARAMETRO_CONNETTORE_DA_LISTA_APS = "connettoreDaListaAps";
	
	
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO = "resCacheStato";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_TIMEOUT = "resCacheTimeout";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE = "resCacheMaxResSize";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE_BYTES = "resCacheMaxResSizeB";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_URI_INVOCAZIONE = "resCacheDUri";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS = "resCacheDQueryPar";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS_NOMI = "resCacheDQueryParNomi";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS = "resCacheDHead";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS_NOMI_HEADERS = "resCacheDHeadNomiH";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_PAYLOAD = "resCacheDpay";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO_PORTA = "resCacheStatoPorta";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_CACHE = "resCacheCCNoC";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_MAX_AGE = "resCacheCCMA";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_STORE = "resCacheCCNoS";
	
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MIN = "resCacheCCRegMinCode";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MAX = "resCacheCCRegMaxCode";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE = "resCacheCCRegCode";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_FAULT = "resCacheCCRegFault";
	public final static String PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_CACHE_TIMEOUT_SECONDS = "resCacheCCRegTimeout";
	
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_ID_REGOLA = "ppId";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_STATO = "ppStato";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_NOME = "ppNome";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_DESCRIZIONE = "ppDescr";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_POSIZIONE = "ppPos";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REG_EXPR = "ppRegExpr";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_REGOLA_TEXT = "ppRegText";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_CONTESTO_ESTERNO = "ppCE";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_BASE_URL = "ppBaseUrl";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_PROFILO = "ppProfilo";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SOGGETTO = "ppSogg";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_RUOLO = "ppRuolo";
	public final static String PARAMETRO_CONFIGURAZIONE_PROXY_PASS_REGOLA_SERVICE_BINDING = "ppServB";
	
	
	public final static String PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE = "idTrasf";
	public final static String PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RISPOSTA = "idTrasfRes";
	public final static String PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RICHIESTA_HEADER = "idTrasfReqHead";
	public final static String PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RICHIESTA_PARAMETRO = "idTrasfReqParam";
	public final static String PARAMETRO_ID_CONFIGURAZIONE_TRASFORMAZIONE_RISPOSTA_HEADER = "idTrasfResHead";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_FIRST = "trFirst";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_NOME = "trNome";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_STATO = "trStato";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL = "trAppAzioniAll";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_TRUE = Costanti.CHECK_BOX_ENABLED;
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_FALSE = Costanti.CHECK_BOX_DISABLED;
	public final static String [] PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUES = new String [] {
			PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_TRUE,
			PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI_ALL_VALUE_FALSE
	};
	
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_POSIZIONE = "trPos";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_AZIONI = "trAppAzioni";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_CT = "trAppCT";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_PATTERN = "trAppPat";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_LIST = "trAppList";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_ENABLED = "trReqConvEn";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO = "trReqConvTp";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK = "trReqConvTpChk";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TEMPLATE = "trReqConvTem";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONTENT_TYPE = "trReqCT";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_TRANSFORMATION = "trRestTr";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_METHOD = "trRestMet";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REST_PATH = "trRestPath";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_TRANSFORMATION = "trSoapTr";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_VERSION = "trSoapVers";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ACTION = "trSoapAc";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE = "trSoapEnv";
//	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACH = "trSoapEnvAsAt";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO = "trSoapTp";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TIPO_CHECK = "trSoapTpChk";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_TEMPLATE = "trSoapEnvTemp";
	
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_NOME = "trResNome";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS = "trResAppStat";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MIN = "trResAppStatMin";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MAX = "trResAppStatMax";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_CT = "trResAppCT";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_PATTERN = "trResAppPat";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_ENABLED = "trResConvEn";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO = "trResConvTp";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO_CHECK = "trResConvTpChk";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE = "trResConvTem";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE = "trResCT";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_RETURN_CODE = "trResRetCode";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_TRANSFORMATION = "trResSoapTr";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE = "trResSoapEnv";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_AS_ATTACH = "trResSoapEnvAsAt";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO = "trResSoapTp";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO_CHECK = "trResSoapTpChk";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE = "trResSoapEnvTemp";
	
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_VALORE = "trResHeadVal";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_NOME = "trResHeadNome";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_TIPO = "trResHeadTipo";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_HEADER_IDENTIFICAZIONE = "trResHeadIden";
	
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_VALORE = "trReqHeadVal";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_NOME = "trReqHeadNome";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_TIPO = "trReqHeadTipo";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_HEADER_IDENTIFICAZIONE = "trReqHeadIden";
	
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_VALORE = "trReqParVal";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_NOME = "trReqParNome";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_TIPO = "trReqParTipo";
	public final static String PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_PARAMETRO_IDENTIFICAZIONE = "trReqParIden";
	
	/** PARAMETRI MESSAGE PAGE **/
	
	public static final String PARAMETER_MESSAGE_TEXT = Costanti.PARAMETER_MESSAGE_TEXT;
	public static final String PARAMETER_MESSAGE_TITLE = Costanti.PARAMETER_MESSAGE_TITLE;
	public static final String PARAMETER_MESSAGE_TYPE = Costanti.PARAMETER_MESSAGE_TYPE;
	public static final String PARAMETER_MESSAGE_BREADCRUMB = Costanti.PARAMETER_MESSAGE_BREADCRUMB;
	
	/** VALUES **/
	
	public final static int MAX_LENGTH_VALORE_STATO_RATE_LIMITING = 80;
	
	public final static String DEFAULT_VALUE_ABILITATO = "abilitato";
	public final static String DEFAULT_VALUE_DISABILITATO = "disabilitato";
	public final static String DEFAULT_VALUE_WARNING_ONLY = "warningOnly";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_ABILITATO = "abilitato";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_DISABILITATO = "disabilitato";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_REQUEST_FLOW = "Request Flow";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_RESPONSE_FLOW = "Response Flow";
	public final static String DEFAULT_VALUE_PARAMETRO_MTOM_DISABLE = "disable";
	public final static String DEFAULT_VALUE_PARAMETRO_MTOM_PACKAGING = "packaging";
	public final static String DEFAULT_VALUE_PARAMETRO_MTOM_UNPACKAGING = "unpackaging";
	public final static String DEFAULT_VALUE_PARAMETRO_MTOM_VERIFY = "verify";
	public final static String DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM =  "custom";
	public final static String DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM = "custom";
	
	public final static String DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_DISABILITATO = CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.toString();
	public final static String DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_ABILITATO = CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.toString();
	public final static String DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_WARNING_ONLY = CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.toString();
	public final static String DEFAULT_VALUE_PARAMETRO_PORTE_TIPO_VALIDAZIONE_XSD =  CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.toString();
	public final static String DEFAULT_VALUE_PARAMETRO_PORTE_TIPO_VALIDAZIONE_INTERFACE = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.toString();
	public final static String DEFAULT_VALUE_PARAMETRO_PORTE_TIPO_VALIDAZIONE_OPENSPCOOP = CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.toString();
	
	public final static String DEFAULT_VALUE_PARAMETRO_PROTOCOLLO_QUALSIASI = "";
	
	public final static String DEFAULT_VALUE_PARAMETRO_SOGGETTO_QUALSIASI = "";
	public final static String LABEL_PARAMETRO_SOGGETTO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP = "SOAP";
	public final static String DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST = "REST";
	public final static String DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI = "";
	
	public final static String DEFAULT_VALUE_PARAMETRO_HTTP_METHOD_QUALSIASI = "";
	
	public final static String DEFAULT_VALUE_PARAMETRO_RUOLO_TIPOLOGIA_QUALSIASI = "";
	public final static String RUOLI_TIPOLOGIA_LABEL_INTERNO = "Registro";
	public final static String RUOLI_TIPOLOGIA_LABEL_ESTERNO = "Esterna";
	public final static String LABEL_PARAMETRO_RUOLO_TIPOLOGIA_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String DEFAULT_VALUE_PARAMETRO_RUOLO_CONTESTO_QUALSIASI = "";
	public final static String RUOLI_CONTESTO_UTILIZZO_LABEL_EROGAZIONE = "Erogazione";
	public final static String RUOLI_CONTESTO_UTILIZZO_LABEL_FRUIZIONE = "Fruizione";
	public final static String LABEL_PARAMETRO_RUOLO_CONTESTO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String DEFAULT_VALUE_PARAMETRO_SCOPE_TIPOLOGIA_QUALSIASI = "";
	public final static String SCOPE_TIPOLOGIA_LABEL_INTERNO = "Registro";
	public final static String SCOPE_TIPOLOGIA_LABEL_ESTERNO = "Esterna";
	public final static String LABEL_PARAMETRO_SCOPE_TIPOLOGIA_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String DEFAULT_VALUE_PARAMETRO_SCOPE_CONTESTO_QUALSIASI = "";
	public final static String SCOPE_CONTESTO_UTILIZZO_LABEL_EROGAZIONE = "Erogazione";
	public final static String SCOPE_CONTESTO_UTILIZZO_LABEL_FRUIZIONE = "Fruizione";
	public final static String LABEL_PARAMETRO_SCOPE_CONTESTO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String DEFAULT_VALUE_PARAMETRO_API_CONTESTO_QUALSIASI = "";
	public final static String API_CONTESTO_UTILIZZO_LABEL_EROGAZIONE = "Erogazione";
	public final static String API_CONTESTO_UTILIZZO_LABEL_FRUIZIONE = "Fruizione";
	public final static String API_CONTESTO_UTILIZZO_LABEL_EROGAZIONE_FRUIZIONE = "Erogazione/Fruizione";
	public final static String API_CONTESTO_UTILIZZO_LABEL_SOGGETTI = "Soggetti";
	public final static String API_CONTESTO_UTILIZZO_LABEL_APPLICATIVI = "Applicativi";
	public final static String LABEL_PARAMETRO_API_CONTESTO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String DEFAULT_VALUE_PARAMETRO_API_IMPLEMENTAZIONE_QUALSIASI = "";
	public final static String LABEL_PARAMETRO_API_IMPLEMENTAZIONE_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;

	public final static String DEFAULT_VALUE_PARAMETRO_APPLICATIVO_QUALSIASI = "";
	public final static String LABEL_PARAMETRO_APPLICATIVO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String DEFAULT_VALUE_PARAMETRO_RUOLO_QUALSIASI = "";
	public final static String LABEL_PARAMETRO_RUOLO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String DEFAULT_VALUE_PARAMETRO_GRUPPO_QUALSIASI = "";
	public final static String LABEL_PARAMETRO_GRUPPO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String DEFAULT_VALUE_PARAMETRO_CANALE_QUALSIASI = "";
	public final static String LABEL_PARAMETRO_CANALE_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	public final static String PREFIX_VALUE_PARAMETRO_CANALE_DEFAULT = Filtri.PREFIX_VALUE_CANALE_DEFAULT;
	
	public final static String DEFAULT_VALUE_PARAMETRO_TIPO_CREDENZIALI_QUALSIASI = "";
	public final static String LABEL_PARAMETRO_TIPO_CREDENZIALI_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String DEFAULT_VALUE_PARAMETRO_TIPO_TOKEN_POLICY_QUALSIASI = "";
	public final static String LABEL_PARAMETRO_TIPO_TOKEN_POLICY_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_DEFAULT = "D";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_SOAP_11 = "SOAP_11";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_SOAP_12 = "SOAP_12";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_XML = "XML";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_JSON = "JSON";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_BINARY = "BINARY";
	public final static String DEFAULT_VALUE_PARAMETRO_MESSAGE_TYPE_MIME_MULTIPART = "MIME_MULTIPART";
	
	public final static String VALUE_PARAMETRO_INTERFACE_TYPE_WSDL_11 = FormatoSpecifica.WSDL_11.getValue();
	public final static String VALUE_PARAMETRO_INTERFACE_TYPE_WADL = FormatoSpecifica.WADL.getValue();
	public final static String VALUE_PARAMETRO_INTERFACE_TYPE_SWAGGER_2 = FormatoSpecifica.SWAGGER_2.getValue();
	public final static String VALUE_PARAMETRO_INTERFACE_TYPE_OPEN_API_3 = FormatoSpecifica.OPEN_API_3.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_REST = CostantiRegistroServizi.DEFAULT_VALUE_INTERFACE_TYPE_REST.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_INTERFACE_TYPE_SOAP = CostantiRegistroServizi.DEFAULT_VALUE_INTERFACE_TYPE_SOAP.getValue();
	
	public final static String VALUE_PARAMETRO_MODE_CORRELAZIONE_INPUT_BASED = CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_INPUT_BASED.toString();
	public final static String VALUE_PARAMETRO_MODE_CORRELAZIONE_URL_BASED = CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_URL_BASED.toString();
	public final static String VALUE_PARAMETRO_MODE_CORRELAZIONE_HEADER_BASED = CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_HEADER_BASED.toString();
	public final static String VALUE_PARAMETRO_MODE_CORRELAZIONE_CONTENT_BASED = CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED.toString();
	public final static String VALUE_PARAMETRO_MODE_CORRELAZIONE_DISABILITATO = CostantiConfigurazione.CORRELAZIONE_APPLICATIVA_RICHIESTA_DISABILITATO.toString();
	public final static String LABEL_PARAMETRO_MODE_CORRELAZIONE_DISABILITATO = "Disabilitata";
	
	public final static String VALUE_PARAMETRO_DUMP_STATO_DEFAULT = "default";
	public final static String VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO = "ridefinito";
	public final static String VALUE_PARAMETRO_DUMP_SEZIONE_RICHIESTA = "richiesta";
	public final static String VALUE_PARAMETRO_DUMP_SEZIONE_RISPOSTA = "risposta";
	
	public final static String LABEL_LIST_VALORE_NON_PRESENTE = "--";
	public static final String DEFAULT_VALUE_NON_SELEZIONATO = "-";
	public static final String DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA = ""; // lasciare vuota, se si usa il trattino rimane aperto l'area di ricerca con filtro Qualsiasi
		
	public final static String VALUE_PARAMETRO_PROPERTIES_MODE_DEFAULT = "default";
	
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN = org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA;
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_RETRIEVE_POLICY_TOKEN = org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA_RETRIEVE;
	
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE = StatoFunzionalita.DISABILITATO.getValue();
	
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT = StatoFunzionalitaConWarning.ABILITATO.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION = StatoFunzionalitaConWarning.ABILITATO.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO = StatoFunzionalitaConWarning.ABILITATO.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD = StatoFunzionalita.ABILITATO.getValue();
	
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER = StatoFunzionalita.DISABILITATO.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID = StatoFunzionalita.DISABILITATO.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT = StatoFunzionalita.DISABILITATO.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME = StatoFunzionalita.DISABILITATO.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL = StatoFunzionalita.DISABILITATO.getValue();
	
	
	public final static int DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PATTERN_LIST_MAX_VALUE = 100;
	
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_11 = VersioneSOAP._1_1.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RICHIESTA_SOAP_VERSION_12 = VersioneSOAP._1_2.getValue();
	
	public final static String[] SELECT_VALUES_STATO_FUNZIONALITA_CON_WARNING = {StatoFunzionalitaConWarning.ABILITATO.getValue(), StatoFunzionalitaConWarning.DISABILITATO.getValue(), StatoFunzionalitaConWarning.WARNING_ONLY.getValue()};
	public final static String[] SELECT_VALUES_STATO_FUNZIONALITA= {StatoFunzionalita.ABILITATO.getValue(), StatoFunzionalita.DISABILITATO.getValue()}; 
	
	public final static String[] SELECT_VALUES_STATO_FUNZIONALITA_RESPONSE_CACHING_DIGEST_QUERY_PARAMETERS= 
		{StatoFunzionalitaCacheDigestQueryParameter.ABILITATO.getValue(), StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE.getValue(), StatoFunzionalitaCacheDigestQueryParameter.DISABILITATO.getValue()}; 

	public static final String NOME_FILE_EROGAZIONE_XACML_POLICY_XML_SUFFIX = "xacmlPolicy.xml";
	public static final String NOME_FILE_FRUIZIONE_XACML_POLICY_XML_SUFFIX = "fruizioneXacmlPolicy.xml";
	
	public final static String VALUE_PARAMETRO_CORS_STATO_DEFAULT = "default";
	public final static String VALUE_PARAMETRO_CORS_STATO_RIDEFINITO = "ridefinito";
	
	public final static String VALUE_PARAMETRO_RESPONSE_CACHING_STATO_DEFAULT = "default";
	public final static String VALUE_PARAMETRO_RESPONSE_CACHING_STATO_RIDEFINITO = "ridefinito";
	
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI = "Qualsiasi";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO = "Singolo";
	public final static String LABEL_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO = "Intervallo";
	
	public final static String[] SELECT_LABELS_CONFIGURAZIONE_RETURN_CODE = {
			LABEL_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI,LABEL_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO,LABEL_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO
	};
	
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI = "qualsiasi";
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO = "esatto";
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO = "intervallo";
	
	public final static String[] SELECT_VALUES_CONFIGURAZIONE_RETURN_CODE = {
			 VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI,
			 VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO,
			 VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO
	};
	
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU = "su";
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_GIU = "giu";
	
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_ADD = TrasformazioneRegolaParametroTipoAzione.ADD.getValue();
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_DELETE = TrasformazioneRegolaParametroTipoAzione.DELETE.getValue();
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_UPDATE = TrasformazioneRegolaParametroTipoAzione.UPDATE.getValue();
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_UPDATE_OR_ADD = TrasformazioneRegolaParametroTipoAzione.UPDATE_OR_ADD.getValue();
	
	public final static String[] SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO = {
			VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_ADD,
			VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_DELETE,
			VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_UPDATE,
			VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_UPDATE_OR_ADD
	};
	
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_BLOCCA = TrasformazioneIdentificazioneRisorsaFallita.BLOCCA.getValue();
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_IGNORA = TrasformazioneIdentificazioneRisorsaFallita.IGNORA.getValue();
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA = VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_BLOCCA;
	
	public final static String[] SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA = {
			VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_BLOCCA,
			VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_IGNORA
	};
	
	public final static String[] SELECT_LABELS_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_HEADER = {
			LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_BLOCCA,
			LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_IGNORA_HEADER
	};
	public final static String[] SELECT_LABELS_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_PARAMETRO = {
			LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_BLOCCA,
			LABEL_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA_IGNORA_PARAMETRO
	};
	
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO = "updateTipo";
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE = "updateFile";
	
	public final static String LABEL_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO = "Disabilitato";
	public final static String LABEL_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_BODY = "Utilizza contenuto come SOAP Body";
	public final static String LABEL_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT = "Utilizza contenuto come Attachment";
	
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO = "0";
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_BODY = "1";
	public final static String VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT = "2";
	
	public final static String[] SELECT_VALUES_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE = {
			VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO,
			VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_BODY,
			VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT
	};
	
	public final static String[] SELECT_LABELS_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE = {
			LABEL_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO,
			LABEL_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_BODY,
			LABEL_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT
	};
	
	public final static String VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO = "pubblico";
	public final static String VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_AUTENTICATO = "autenticato";
	public final static String[] SELECT_VALUES_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO = {
			VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_AUTENTICATO,
			VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO
	};
	
	public final static String VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_DISABILITATO = StatoFunzionalita.DISABILITATO.getValue();
	public final static String VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_ABILITATO = StatoFunzionalita.ABILITATO.getValue();
	public final static String VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM = "custom";

	public final static String[] PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_VALUES = {
			VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_DISABILITATO,
			VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_ABILITATO
	};
	
	public final static boolean VALUE_TRASFORMAZIONI_CHECK_UNIQUE_NOME_TIPO_HEADER_URL = false;
	public final static String MESSAGGIO_TRASFORMAZIONI_CHECK_UNIQUE_NOME_TIPO_URL = "Una regola con la propriet&agrave indicata risulta gi&agrave; registrata";
	public final static String MESSAGGIO_TRASFORMAZIONI_CHECK_UNIQUE_NOME_TIPO_HEADER = "Una regola con l'header indicato risulta gi&agrave; registrata";

	public final static String DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_QUALSIASI = "";
	public final static String DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_EROGAZIONE = "erogazione";
	public final static String DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_FRUIZIONE = "fruizione";
	
	public final static String[] SELECT_VALUES_PARAMETRO_PROXY_PASS_REGOLA_RUOLO = { DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_QUALSIASI, DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_EROGAZIONE, DEFAULT_VALUE_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_FRUIZIONE};
	
		
	public final static String LABEL_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_EROGAZIONE = "Erogazione";
	public final static String LABEL_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_FRUIZIONE = "Fruizione";
	public final static String LABEL_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_QUALSIASI = CostantiControlStation.LABEL_QUALSIASI;
	
	public final static String[] SELECT_LABELS_PARAMETRO_PROXY_PASS_REGOLA_RUOLO = { LABEL_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_QUALSIASI, LABEL_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_EROGAZIONE, LABEL_PARAMETRO_PROXY_PASS_REGOLA_RUOLO_FRUIZIONE };
	
	public final static String[] SELECT_VALUES_PARAMETRO_PROXY_PASS_REGOLA_SERVICE_BINDING = { DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_QUALSIASI, DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_SOAP, DEFAULT_VALUE_PARAMETRO_SERVICE_BINDING_REST };
	public final static String[] SELECT_LABELS_PARAMETRO_PROXY_PASS_REGOLA_SERVICE_BINDING = { LABEL_PARAMETRO_SERVICE_BINDING_QUALSIASI, LABEL_PARAMETRO_SERVICE_BINDING_SOAP, LABEL_PARAMETRO_SERVICE_BINDING_REST };
	
	public final static String LABEL_DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO_RIDEFINITO = "ridefinito";
	public final static String LABEL_DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO_DEFAULT = "default ({0})";
	public final static String LABEL_DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO_DEFAULT_API = "default API ({0})";
	
	public final static String DEFAULT_VALUE_PARAMETRO_CANALE_STATO_RIDEFINITO = "ridefinito";
	public final static String DEFAULT_VALUE_PARAMETRO_CANALE_STATO_DEFAULT = "default";
	
	public final static String[] VALUES_PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO = { DEFAULT_VALUE_PARAMETRO_CANALE_STATO_DEFAULT, DEFAULT_VALUE_PARAMETRO_CANALE_STATO_RIDEFINITO	 };
	
	/** OTHER */
	public final static String IMAGES_DIR = "images";
	public final static String CSS_DIR = "css";
	public final static String JS_DIR = "js";
	public final static String FONTS_DIR = "fonts";
	public final static String OPERATIONS_DELIMITER = "\n--------------------------------------------\n\n";
	public final static String RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_JMX = "jmx";
	public final static String RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_OPENSPCOOP = "openspcoop";
	
	/** ICONE */
	public final static String ICONA_FRECCIA_SU = "&#xE316;";
	public final static String ICONA_FRECCIA_GIU = "&#xE313;";
	public final static String ICONA_PLACEHOLDER = "&#160;&#160;&#160;&#160;&#160;";
	
	public final static String ICONA_CONTINUE = "&#xe5db;";
	public final static String ICONA_BREAK = "&#xe5cd;";
	
	/** COSTANTI FILE TEMPORANEI */
	public final static String TEMP_FILE_PREFIX = "__pddconsole__";
	public final static String TEMP_FILE_SUFFIX = ".tmp"; 
	
	/** COSTANTI VISUALIZZAZIONE MESSAGGI MODIFICA POSIZIONE TRASFORMAZIONI */
	public static final boolean VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_REGOLA_TRASFORMAZIONE = false;
	public static final boolean VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_RISPOSTA_REGOLA_TRASFORMAZIONE = false;
	public static final boolean VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_POLICY = false;
	
	/** COSTANTI VISUALIZZAZIONE MESSAGGI MODIFICA POSIZIONE REGOLA PROXY PASS */
	public static final boolean VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_REGOLA_PROXY_PASS = false;
	
	/** COSTANTE DIMENSIONE TEXT_AREAD */
	public final static int LABEL_PARAMETRO_TEXT_AREA_SIZE = 3;
	
	public final static int LABEL_PARAMETRO_TEXT_AREA_AZIONI_SIZE = 5;
	
	public final static int LABEL_PARAMETRO_TEXT_AREA_API_SIZE = 30;
	public final static int LABEL_PARAMETRO_TEXT_AREA_API_COLUMNS = 110;
	
	/** INFO */
	
	public final static String LABEL_PARAMETRO_FORCE_INTERFACE_BASED_LEFT = "Identificazione tramite API";
	public final static String LABEL_PARAMETRO_FORCE_INTERFACE_BASED_RIGHT = "Modalità alternativa utilizzata se l'identificazione indicata fallisce";
	
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA = "Espressione applicata sul messaggio; se si ha un match la regola di correlazione verrà utilizzata.<br/>I tipi di espressione utilizzabili sono:";
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_SOAP = "<b>Path</b>: espressione XPath";
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_XPATH = "<b>XPath</b>: espressione XPath utilizzabile con messaggi XML";
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_JSONPATH = "<b>JSONPath</b>: espressione JsonPath utilizzabile con messaggi JSON";
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_ACTION_SOAP = "<b>Azione</b>: identificativo di un azione dell'API";
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_ACTION_REST = "<b>Risorsa</b>: identificativo di una risorsa dell'API";
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_LOCAL_NAME_REST = "<b>LocalName</b>: localName (senza prefisso e namespace) dell'elemento radice di un messaggio XML";
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_LOCAL_NAME_SOAP = "<b>LocalName</b>: localName (senza prefisso e namespace) del primo elemento interno al SOAPBody ";
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_EMPTY = "<b>Campo vuoto</b>: indica qualsiasi elemento; la regola verrà utilizzata se non ne esiste una più specifica";
	public final static List<String> LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_REST = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_REST.add(LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_ACTION_REST);
		LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_REST.add(LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_XPATH);
		LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_REST.add(LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_JSONPATH);
		LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_REST.add(LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_LOCAL_NAME_REST);
		LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_REST.add(LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_EMPTY);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_SOAP = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_SOAP.add(LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_ACTION_SOAP);
		LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_SOAP.add(LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_SOAP);
		LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_SOAP.add(LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_LOCAL_NAME_SOAP);
		LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_VALORI_SOAP.add(LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_APPLICABILITA_EMPTY);
	}
	
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_REST = "Espressione utilizzata sul messaggio per estrarre un identificativo applicativo.<br/>I tipi di espressione utilizzabili sono:";
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_SOAP = "Espressione utilizzata sul messaggio per estrarre un identificativo applicativo.";
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_XPATH = "<b>XPath</b>: utilizzabile con messaggi XML";
	public final static String LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_JSONPATH = "<b>JSONPath</b>: utilizzabile con messaggi JSON";
	public final static List<String> LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_VALORI_REST = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_VALORI_REST.add(LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_XPATH);
		LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_VALORI_REST.add(LABEL_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_INFO_PATTERN_JSONPATH);
	}

	public final static String LABEL_CONFIGURAZIONE_MTOM_INFO_NOME_SOAP_PACKAGE = "Identificativo della regola di processamento";
	public final static String LABEL_CONFIGURAZIONE_MTOM_INFO_PATTERN_SOAP_PACKAGE = "Espressione XPath che identifica sul messaggio un elemento da convertire in MTOM-XOP attachment";
	public final static String LABEL_CONFIGURAZIONE_MTOM_INFO_PATTERN_SOAP_VERIFY = "Espressione XPath che identifica sul messaggio un elemento; il gateway verifica che l'elemento individuato sia un MTOM-XOP attachment";
	public final static String LABEL_CONFIGURAZIONE_MTOM_INFO_CONTENT_TYPE_SOAP_PACKAGE = "L'elemento convertito in MTOM-XOP attachment possiederà il Content-Type indicato.<BR/>Nel caso non ne sia definito uno verrà utilizzato il Content-Type '"+HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM+"'";
	public final static String LABEL_CONFIGURAZIONE_MTOM_INFO_CONTENT_TYPE_SOAP_VERIFY = "Indica il Content-Type associato all'attachment MTOM-XOP per l'elemento identificato; il gateway verifica la corrispondenza.<BR/>Nel caso non ne sia definito uno verrà utilizzato il Content-Type '"+HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM+"'";
		
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE = "Lista di Content-Type per i quali la regola di trasformazione verrà utilizzata; di seguito i formati utilizzabili:";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_EMPTY = "<b>empty</b>: valore speciale che rappresenta una richiesta senza Content-Type"; 
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_1 = "<b>type/subtype</b>: indicazione puntuale di un Content-Type"; 
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_2 = "<b>type/*</b>: hanno un match tutti i Content-Type appartenenti al tipo indicato"; 
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_3 = "<b>*/*+xml</b>: hanno un match tutti i Content-Type che terminano con '+xml'";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_4 = "<b>regexpType/regexpSubType</b>: hanno un match tutti i Content-Type che soddisfano le espressioni regolari indicate"; 
	public final static List<String> LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_1);
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_2);
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_3);
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_4);
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORI.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_CONTENT_TYPE_VALORE_EMPTY);
	}
	
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_REST_RICHIESTA = "Espressione da applicare sul messaggio di richiesta; se si ha un match la regola di trasformazione verrà utilizzata.<br/>I tipi di espressione utilizzabili sono:";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_REST_RISPOSTA = "Espressione da applicare sul messaggio di risposta; se si ha un match la regola di trasformazione verrà utilizzata.<br/>I tipi di espressione utilizzabili sono:";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_SOAP_RICHIESTA = "Espressione XPath da applicare sul messaggio di richiesta; se si ha un match la regola di trasformazione verrà utilizzata.";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_SOAP_RISPOSTA = "Espressione XPath da applicare sul messaggio di risposta; se si ha un match la regola di trasformazione verrà utilizzata.";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_XPATH = "<b>XPath</b>: utilizzabile con messaggi XML";
	public final static String LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_JSONPATH = "<b>JSONPath</b>: utilizzabile con messaggi JSON";
	public final static List<String> LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_VALORI_REST = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_VALORI_REST.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_XPATH);
		LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_VALORI_REST.add(LABEL_CONFIGURAZIONE_TRASFORMAZIONI_APPLICABILITA_INFO_PATTERN_JSONPATH);
	}
	
	// NOTA: usato anche in ModIConsoleCostanti
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO = "Il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway.<br/>Le espressioni utilizzabili sono:";
	
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ID_TRANSAZIONE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TRANSACTION_ID_VALUE+"}</b>: identificativo UUID della transazione";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_DATA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+":FORMAT}</b>: data di elaborazione del messaggio; il formato fornito deve essere conforme a quanto richiesto dalla classe java 'java.text.SimpleDateFormat' (es. ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+":yyyyMMdd_HHmmssSSS})";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+":NAME}</b>: valore presente nell'header http che possiede il nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+":NAME}</b>: valore associato al parametro della url con nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_BUSTA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+":FIELD}</b>: permette di utilizzare informazioni generiche del profilo; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_BUSTA_OBJECT+"' (es. per il mittente usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+":mittente})";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PROPERTY_BUSTA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_PROPERTY+":NAME}</b>: permette di riferire informazioni specifiche del profilo presenti nella traccia (es. identificativo SDI). Il valore 'NAME' indica il nome della proprietà da utilizzare";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata su un messaggio XML"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+":EXPR}</b>: espressione JSONPath applicata su un messaggio JSON"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_URL = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+":EXPR}</b>: espressione regolare applicata sulla url"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_TOKEN_INFO = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":FIELD}</b>: permette di accedere ai claim di un token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TOKEN_INFO+"' (es. per ottenere il valore del claim 'sub' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":sub})";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_CONTEXT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":FIELD}</b>: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+"' (es. per il principal usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":credential.principal})";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PROPERTY_CONFIG = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CONFIG_PROPERTY+":NAME}</b>: permette di accedere alle proprietà configurate per l'API. Il valore 'NAME' indica il nome della proprietà da utilizzare";
	
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_DATA = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":FORMAT}</b>: data di elaborazione del messaggio di risposta; il formato fornito deve essere conforme a quanto richiesto dalla classe java 'java.text.SimpleDateFormat' (es. ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":yyyyMMdd_HHmmssSSS})";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":NAME}</b>: valore presente nell'header http della risposta che possiede il nome 'NAME'";
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":EXPR}</b>: espressione XPath applicata sulla risposta"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":EXPR}</b>: espressione XPath applicata su una risposta XML"; 
	public final static String LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+":EXPR}</b>: espressione JSONPath applicata su una risposta JSON"; 
	
	
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_ALL_VALORI = new ArrayList<>();
	static {
		// NOTA: non modificare le posizioni, poiche' utilizzate sotto per inizializzare le varie liste
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_URL);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ID_TRANSAZIONE);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_DATA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PROPERTY_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_TOKEN_INFO);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PROPERTY_CONFIG);
	}
	// NOTA: usato anche in ModIConsoleCostanti
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI.remove(2); // elimino xpath su soap
	}
	// NOTA: usato anche in ModIConsoleCostanti
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI.remove(3); // elimino xpath su xml
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI.remove(3); // elimino json path
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI);
		LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI.remove(2); // elimino xpath su soap
		LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI.remove(2); // elimino xpath su xml
		LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI.remove(2); // elimino json path
		LABEL_CONFIGURAZIONE_INFO_CONNETTORE_VALORI.remove(2); // elimino espressione regolare
	}

	public final static List<String> LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		// NOTA: non modificare le posizioni, poiche' utilizzate sotto per inizializzare le varie liste
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_HEADER);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_HEADER);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_QUERY);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_XPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_XPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_URL);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_ID_TRANSAZIONE);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_DATA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_RESPONSE_DATA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PROPERTY_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_TOKEN_INFO);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_TRASPORTO_PROPERTY_CONFIG);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE.remove(3); // elimino xpath su soap
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE.remove(3); // elimino xpath su soap per la risposta
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_ALL_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE.remove(5); // elimino xpath su xml
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE.remove(5); // elimino xpath su xml per la risposta
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE.remove(5); // elimino json path
		LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE.remove(5); // elimino json path per la risposta
	}
	
	public final static String LABEL_CONFIGURAZIONE_INFO_TEMPLATE = "Il template fornito può contenere parti dinamiche risolte a runtime dal Gateway.<br/>Le espressioni utilizzabili sono:";
	
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI = "È possiile accedere ai seguenti oggetti resi disponibili a runtime dal Gateway:";
	
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER = "Il file fornito deve rispettare la sintassi del template engine 'Freemarker'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_FREEMARKER_ZIP = "Il file fornito deve essere un archivio zip contenenti dei files che rispettano la sintassi del template engine 'Freemarker'.<br/>"+
			"GovWay richiede la presenza, all'interno dell'archivio zip, di un file indice che deve possedere il nome '"+org.openspcoop2.pdd.core.dynamic.Costanti.ZIP_INDEX_ENTRY_FREEMARKER+"'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
	
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY = "Il file fornito deve rispettare la sintassi del template engine 'Velocity'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_VELOCITY_ZIP = "Il file fornito deve essere un archivio zip contenenti dei files che rispettano la sintassi del template engine 'Velocity'.<br/>"+
			"GovWay richiede la presenza, all'interno dell'archivio zip, di un file indice che deve possedere il nome '"+org.openspcoop2.pdd.core.dynamic.Costanti.ZIP_INDEX_ENTRY_VELOCITY+"'.<br/>"+
			LABEL_CONFIGURAZIONE_INFO_OBJECT_TEMPLATE_OGGETTI_DISPONIBILI;
		
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_ID_TRANSAZIONE = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TRANSACTION_ID_OBJECT+"</b>: identificativo UUID della transazione ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TRANSACTION_ID+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_DATA = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+"</b>: data di elaborazione del messaggio ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_DATE_OBJECT+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+"</b>: headers http ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_HEADER_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+"</b>: parametri della url ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_QUERY_PARAMETER_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_BUSTA = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_OBJECT+"</b>: informazioni generiche del profilo ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_BUSTA_OBJECT+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_PROPERTY_BUSTA = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_BUSTA_PROPERTY+"</b>: permette di riferire informazioni specifiche del profilo presenti nella traccia ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_BUSTA_PROPERTY_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni da messaggi XML tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_JSONPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni da messaggi JSON tramite espressioni JSONPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_JSON_PATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_URL = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dalla url tramite espressioni regolari ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_URL_REGEXP+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_REQUEST = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_REQUEST+"</b>: permette di accedere al contenuto della richiesta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_MESSAGE+")";
	//public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_CONTEXT = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CTX_OBJECT+"</b>: permette di accedere al contesto della richiesta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_CTX_OBJECT_HTML_ESCAPED+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_CONTEXT = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CTX_OBJECT+"</b>: permette di accedere al contesto della richiesta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_CTX_OBJECT_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TOKEN_INFO = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+"</b>: permette di accedere ai claims di un token ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TOKEN_INFO+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_TRANSPORT_CONTEXT = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+"</b>: permette di accedere ai dati della richiesta http ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+")");
	public static final String LABEL_CONFIGURAZIONE_INFO_OBJECT_PROPERTY_CONFIG = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CONFIG_PROPERTY+"</b>: permette di accedere alle proprietà configurate per l'API ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_CONFIG_PROPERTY_HTML_ESCAPED+")";
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_ERROR_HANDLER = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ERROR_HANDLER_OBJECT+"</b>: permette di generare risposte personalizzate che segnalano l'impossibilità di proseguire la trasformazione ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ERROR_HANDLER_OBJECT+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_LOAD_STATIC+"</b>: permette di definire classi (es. class[\"org.apache.commons.lang.StringUtils\"] ).");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_NEW_INSTANCE+"</b>: permette di istanziare una classe (es. new(\"java.lang.StringBuilder\",\"Commento Iniziale\") ).");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_LOAD_STATIC+"</b>: permette di definire classi (es. class.forName(\"org.apache.commons.lang.StringUtils\") ).");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_CLASS_NEW_INSTANCE+"</b>: permette di istanziare una classe (es. new.instance(\"java.lang.StringBuilder\",\"Commento Iniziale\") ).");
	
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_DATA = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_DATE_OBJECT+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: data di elaborazione del messaggio di risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_DATE_OBJECT+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_HEADER = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: headers http della risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_HEADER+")");
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH_SOAP = StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio di risposta tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio XML di risposta tramite espressioni XPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_XML_XPATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_JSONPATH= StringEscapeUtils.escapeHtml("<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_SUFFIX_RESPONSE+"</b>: fornisce metodi per verificare la presenza o estrarre informazioni dal messaggio JSON di risposta tramite espressioni JSONPath ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_ELEMENT_JSON_PATH+")"); 
	public final static String LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE = "<b>"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_RESPONSE+"</b>: permette di accedere al contenuto della risposta ("+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_MESSAGE+")";
	
	
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI = new ArrayList<>();
	static {
		// NOTA: non modificare le posizioni, poiche' utilizzate sotto per inizializzare le varie liste
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_URL);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ID_TRANSAZIONE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_DATA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_PROPERTY_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TOKEN_INFO);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TRANSPORT_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_REQUEST);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ERROR_HANDLER);
	}
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI.remove(2); // elimino xpath su soap
	}
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI.remove(3); // elimino xpath su xml
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI.remove(3); // elimino json path
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}
	
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		// NOTA: non modificare le posizioni, poiche' utilizzate sotto per inizializzare le varie liste
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_HEADER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_HEADER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_QUERY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH_SOAP);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_XPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_XPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_JSONPATH);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_URL);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ID_TRANSAZIONE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_DATA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE_DATA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_PROPERTY_BUSTA);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TOKEN_INFO);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_TRANSPORT_CONTEXT);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_PROPERTY_CONFIG);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_RESPONSE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_ERROR_HANDLER);
	}
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE.remove(3); // elimino xpath su soap
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE.remove(3); // elimino xpath su soap per la risposta
	}
	private final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_ALL_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE.remove(5); // elimino xpath su xml
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE.remove(5); // elimino xpath su xml per la risposta
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE.remove(5); // elimino json path
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE.remove(5); // elimino json path per la risposta
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_FREEMARKER);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_FREEMARKER.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_FREEMARKER);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_REST_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY.addAll(LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_CLASS_VELOCITY);
		LABEL_CONFIGURAZIONE_INFO_OBJECT_SOAP_VALORI_CON_RISPOSTE_VELOCITY.add(LABEL_CONFIGURAZIONE_INFO_OBJECT_NEW_INSTANCE_VELOCITY);
	}
	
	public final static String LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS = "Il file fornito deve contenere proprietà indicate come nome=valore in ogni linea.<BR/>"+
		"Il nome della proprietà corrisponde all'entry name all'interno dell'archivio (es. dir/subDir/entryName1).<BR/>"+
		"Il valore della proprietà corrisponde al contenuto dell'entry.<BR/>"+
		"<BR/>"+
		"È possibile selezionare parti del messaggio, per associarle come contenuto dell'entry, utilizzando le seguenti espressioni dinamiche risolte a runtime dal Gateway:";
		
	
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_CONTENT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_CONTENT+"}</b>: payload della richiesta";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_CONTENT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_CONTENT+"}</b>: payload della risposta";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_PREFIX+"index"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una richiesta multipart alla posizione indicata dall'intero 'index'";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_PREFIX+"index"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una risposta multipart alla posizione indicata dall'intero 'index'";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH_BY_ID = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_BY_ID_PREFIX+"id"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una richiesta multipart che possiede il "+HttpConstants.CONTENT_ID+" indicato";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH_BY_ID = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ATTACH_BY_ID_PREFIX+"id"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_SUFFIX+"}</b>: attachment presente in una risposta multipart che possiede il "+HttpConstants.CONTENT_ID+" indicato";
	
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_ENVELOPE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ENVELOPE+"}</b>: soap envelope della richiesta";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_ENVELOPE = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_ENVELOPE+"}</b>: soap envelope della risposta";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_BODY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_BODY+"}</b>: contenuto del soap body della richiesta";
	public final static String LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_BODY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.COMPRESS_BODY+"}</b>: contenuto del soap body della risposta";
	
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI);
	}
	
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_REST_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_REST_VALORI_CON_RISPOSTE);
	}
	
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_ENVELOPE);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_SOAP_BODY);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_REQUEST_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI);
	}
	
	public final static List<String> LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_CONTENT);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_ENVELOPE);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_SOAP_BODY);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.add(LABEL_CONFIGURAZIONE_TEMPLATE_COMPRESS_RESPONSE_ATTACH_BY_ID);
		LABEL_CONFIGURAZIONE_INFO_TEMPLATE_COMPRESS_SOAP_VALORI_CON_RISPOSTE.addAll(LABEL_CONFIGURAZIONE_INFO_TRASFORMAZIONI_TRASPORTO_SOAP_VALORI_CON_RISPOSTE);
	}
	
	
	
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_NOTE = "Indicare per riga i claims richiesti (nome=valore); visualizzare 'info' per maggiori dettagli";
		
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS = "Indicare per riga i claims richiesti nel token nella forma (nome=valore).<br/>"+
			"Ogni valore può essere fornito in una delle seguenti modalità:<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_ANY_VALUE+"</b> : indica qualsiasi valore non nullo<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : la regola è soddisfatta se l'intero valore del claim ha un match rispetto all'espressione regolare EXPR indicata<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : simile alla precedente regola, il match dell'espressione regolare può avvenire anche su una sottostringa del valore del claim<br/>"+
			"- <b>valore</b> : indica esattamente il valore (case sensitive) che deve possedere il claim; il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway descritte di seguito<br/>"+
			"- <b>valore1,..,valoreN</b> : è possibile elencare differenti valori ammissibili; come per la precedente opzione il valore può contenere parti dinamiche<br/>"+
			"<br/>Le espressioni utilizzabili come parti dinamiche, risolte a runtime dal gateway, sono:";
	
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+":NAME}</b>: valore presente nell'header http che possiede il nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+":NAME}</b>: valore associato al parametro della url con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata sul messaggio"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata su un messaggio XML"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+":EXPR}</b>: espressione JSONPath applicata su un messaggio JSON"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_URL = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+":EXPR}</b>: espressione regolare applicata sulla url di invocazione"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TRASPORTO_CONTEXT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":FIELD}</b>: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+"' (es. per il principal usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":credential.principal})";
	
	public final static List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_JSONPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TRASPORTO_CONTEXT);
	}
	
	public final static List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_XPATH_SOAP);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_TOKEN_CLAIMS_TRASPORTO_CONTEXT);
	}
	
	
	
	
	
	public final static String LABEL_PARAMETRO_PORTE_AUTORIZZAZIONE_CONTENUTI_NOTE = "Indicare per riga i controlli richiesti (risorsa=valore); visualizzare 'info' per maggiori dettagli";
	
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI= "Indicare per riga i controlli di autorizzazione richiesti nella forma (risorsa=valore).<br/><br/>"+
			"Una risorsa identifica un header, una parte dell'url o del messaggio, un claim del token o un principal etc.<br/>"+
			"Per identificare una risorsa sono utilizzabili le espressioni dinamiche descritte nell'ultima parte di questo box informazioni.<br/><br/>"+
			"Ogni valore può essere fornito in una delle seguenti modalità:<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_ANY_VALUE+"</b> : indica qualsiasi valore non nullo<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_MATCH_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : la regola è soddisfatta se il valore della risorsa ha un match completo rispetto all'espressione regolare EXPR indicata<br/>"+
			"- <b>"+CostantiAutorizzazione.AUTHZ_REGEXP_FIND_PREFIX+"EXPR"+CostantiAutorizzazione.AUTHZ_REGEXP_SUFFIX+"</b> : simile alla precedente regola, il match dell'espressione regolare può avvenire anche su una sottostringa del valore della risorsa<br/>"+
			"- <b>valore</b> : indica esattamente il valore (case sensitive) che deve possedere la risorsa; il valore può essere definito come costante o contenere parti dinamiche risolte a runtime dal Gateway descritte di seguito<br/>"+
			"- <b>valore1,..,valoreN</b> : è possibile elencare differenti valori ammissibili; come per la precedente opzione il valore può contenere parti dinamiche<br/>"+
			"<br/><b>Identificazione delle risorse tramite espressioni dinamiche</b><br/><br/>Le espressioni utilizzabili, risolte a runtime dal gateway, sono:";
	
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_HEADER+":NAME}</b>: valore presente nell'header http che possiede il nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_QUERY_PARAMETER+":NAME}</b>: valore associato al parametro della url con nome 'NAME'";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH_SOAP = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata sul messaggio"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_XML_XPATH+":EXPR}</b>: espressione XPath applicata su un messaggio XML"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_JSONPATH = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_JSON_PATH+":EXPR}</b>: espressione JSONPath applicata su un messaggio JSON"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_URL = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_ELEMENT_URL_REGEXP+":EXPR}</b>: espressione regolare applicata sulla url di invocazione"; 
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_INFO = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":FIELD}</b>: permette di accedere ai claim di un token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_TOKEN_INFO+"' (es. per ottenere il valore del claim 'sub' usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_TOKEN_INFO+":sub})";
	public final static String LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TRASPORTO_CONTEXT = "<b>${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":FIELD}</b>: permette di accedere ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe '"+org.openspcoop2.pdd.core.dynamic.Costanti.TYPE_MAP_URL_PROTOCOL_CONTEXT_OBJECT+"' (es. per il principal usare ${"+org.openspcoop2.pdd.core.dynamic.Costanti.MAP_URL_PROTOCOL_CONTEXT_OBJECT+":credential.principal})";
	
	public final static List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_REST_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_JSONPATH);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_INFO);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_REST_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TRASPORTO_CONTEXT);
	}
	
	public final static List<String> LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SOAP_VALORI = new ArrayList<>();
	static {
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_HEADER);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_QUERY);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_URL);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_XPATH_SOAP);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TOKEN_INFO);
		LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_CLAIMS_SOAP_VALORI.add(LABEL_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_TRASPORTO_CONTEXT);
	}
	
	
	
	
	
	public final static String LABEL_CONFIGURAZIONE_POLICY_STATO_ABILITATO = "<b>"+CostantiControlStation.LABEL_STATO_ABILITATO+"</b>: la policy viene applicata alle richieste che soddisfano i criteri di applicabilità";
	public final static String LABEL_CONFIGURAZIONE_POLICY_STATO_WARNING_ONLY = "<b>"+CostantiControlStation.LABEL_STATO_WARNING_ONLY+"</b>: la policy viene applicata in modalità warning only, limitandosi a segnalare nella diagnostica le violazioni senza bloccare le richieste";
	public final static String LABEL_CONFIGURAZIONE_POLICY_STATO_DISABILITATO = "<b>"+CostantiControlStation.LABEL_STATO_DISABILITATO+"</b>: la policy è disabilitata e non verrà applicata a nessun richiesta";
	public final static List<String> LABEL_CONFIGURAZIONE_POLICY_STATO_VALORI = new ArrayList<>();
	static {
		LABEL_CONFIGURAZIONE_POLICY_STATO_VALORI.add(LABEL_CONFIGURAZIONE_POLICY_STATO_ABILITATO);
		LABEL_CONFIGURAZIONE_POLICY_STATO_VALORI.add(LABEL_CONFIGURAZIONE_POLICY_STATO_WARNING_ONLY);
		LABEL_CONFIGURAZIONE_POLICY_STATO_VALORI.add(LABEL_CONFIGURAZIONE_POLICY_STATO_DISABILITATO);
	}
	
	
	public final static List<String> LABEL_TOKEN_VALUES = new ArrayList<>();
	static {
		LABEL_TOKEN_VALUES.add(LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
		LABEL_TOKEN_VALUES.add(LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
		LABEL_TOKEN_VALUES.add(LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
		LABEL_TOKEN_VALUES.add(LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
		LABEL_TOKEN_VALUES.add(LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
	}
	
	public final static List<String> LABEL_TOKEN_VALUES_WITHOUT_ISSUER = new ArrayList<>();
	static {
		LABEL_TOKEN_VALUES_WITHOUT_ISSUER.add(LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
		LABEL_TOKEN_VALUES_WITHOUT_ISSUER.add(LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
		LABEL_TOKEN_VALUES_WITHOUT_ISSUER.add(LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
		LABEL_TOKEN_VALUES_WITHOUT_ISSUER.add(LABEL_PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
	}
	
	public final static List<String> TOKEN_VALUES = new ArrayList<>();
	static {
		TOKEN_VALUES.add(TipoCredenzialeMittente.token_subject.name());
		TOKEN_VALUES.add(TipoCredenzialeMittente.token_issuer.name());
		TOKEN_VALUES.add(TipoCredenzialeMittente.token_clientId.name());
		TOKEN_VALUES.add(TipoCredenzialeMittente.token_username.name());
		TOKEN_VALUES.add(TipoCredenzialeMittente.token_eMail.name());
	}
	
	public final static List<String> TOKEN_VALUES_WITHOUT_ISSUER = new ArrayList<>();
	static {
		TOKEN_VALUES_WITHOUT_ISSUER.add(TipoCredenzialeMittente.token_subject.name());
		TOKEN_VALUES_WITHOUT_ISSUER.add(TipoCredenzialeMittente.token_clientId.name());
		TOKEN_VALUES_WITHOUT_ISSUER.add(TipoCredenzialeMittente.token_username.name());
		TOKEN_VALUES_WITHOUT_ISSUER.add(TipoCredenzialeMittente.token_eMail.name());
	}
			
	/** MESSAGGI */
	public static final String MESSAGGIO_CONFERMA_REGOLA_POLICY_SPOSTATA_CORRETTAMENTE = "Posizione della policy modificata correttamente.";
	public static final String MESSAGGIO_CONFERMA_REGOLA_TRASFORMAZIONE_SPOSTATA_CORRETTAMENTE = "Posizione della regola modificata correttamente.";
	public static final String MESSAGGIO_CONFERMA_REGOLA_TRASFORMAZIONE_RISPOSTA_SPOSTATA_CORRETTAMENTE ="Posizione della regola di risposta modificata correttamente.";
	public static final String MESSAGGIO_CONFERMA_REGOLA_PROXY_PASS_SPOSTATA_CORRETTAMENTE = "Posizione della regola modificata correttamente.";
	
	public static final String MESSAGGIO_CONFERMA_ABILITAZIONE_GRUPPO ="Procedere con l''abilitazione del gruppo ''{0}''?"; 
	public static final String MESSAGGIO_CONFERMA_ABILITAZIONE_FROM_API ="Procedere con l'abilitazione dell'API ?"; 
	
	public static final String MESSAGGIO_CONFERMA_DISABILITAZIONE_GRUPPO ="Procedere con la disabilitazione del gruppo ''{0}''?"; 
	public static final String MESSAGGIO_CONFERMA_DISABILITAZIONE_FROM_API ="Procedere con la disabilitazione dell'API ?"; 
	
	public static final String MESSAGGIO_CONFERMA_REGISTRAZIONE_MESSAGGI_DOPPIO_SPAZIO = "L''attuale configurazione, prevedendo di registrare i dati del messaggio di {0} sia in Ingresso che in Uscita, raddoppia l''ammontare di spazio occupato. Procedere con la configurazione effettuata?";
	
	/** MESSAGGI ERRORE */
	public static final String MESSAGGIO_ERRORE_CONTROLLO_ACCESSO_DISABILITAZIONE_GESTIONE_TOKEN_AUTENTICAZIONE_PRINCIPAL = "Non è possibile disabilitare la gestione del token in presenza di autenticazione principal che accede a claim del token";
	public static final String MESSAGGIO_ERRORE_CORRELAZIONE_APPLICATIVA_CON_ELEMENTO_XML_DEFINITA_GIA_ESISTENTE = "Esiste gi&agrave; una correlazione applicativa con elemento [{0}] definita nella {1}";
	public static final String MESSAGGIO_ERRORE_MODALITA_IDENTIFICAZIONE_CON_TIPI_POSSIBILI = "Modalit&agrave; identificazione dev'essere disabilitato, headerBased, urlBased, contentBased o inputBased ";
	public static final String MESSAGGIO_ERRRORE_DATI_INCOMPLETI = "Dati incompleti.";
	public static final String MESSAGGIO_ERRRORE_CAMPI_DIFFERENTI = "&Egrave; necessario indicare dei valori differenti in {0} e {1}";
	public static final String MESSAGGIO_ERRRORE_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_XX = "Dati incompleti. &Egrave; necessario indicare: {0}";
	public static final String MESSAGGIO_ERRRORE_DATI_INCOMPLETI_VALORE_MINORE_DEL_MINIMO = "Il valore indicato per {0} è minore del minimo consentito: ${1}";
	public static final String MESSAGGIO_ERRRORE_DATI_INCOMPLETI_VALORE_MINORE_DEL_MASSIMO = "Il valore indicato per {0} è maggiore del massimo consentito: ${1}";
	public static final String MESSAGGIO_ERRORE_SCADENZA_CORRELAZIONE_APPLICATIVA_NON_VALIDA_INSERIRE_UN_NUMERO_INTERO_MAGGIORE_DI_ZERO = "Scadenza Correlazione Applicativa non valida, inserire un numero intero maggiore di zero";
	public static final String MESSAGGIO_ERRORE_CON_LA_SOLA_MODALITA_DI_AUTORIZZAZIONE_XX_DEVE_ESSERE_INDICATA_ANCHE_UNA_MODALITA_DI_AUTENTICAZIONE_YY = "Con la sola modalit&agrave; di autorizzazione ''{0}'' deve essere indicata anche una modalit&agrave; di autenticazione";
	public static final String MESSAGGIO_ERRORE_CON_LA_SOLA_MODALITA_DI_AUTORIZZAZIONE_XX_NON_E_POSSIBILE_ASSOCIATA_UNA_MODALITÀ_DI_AUTENTICAZIONE_OPZIONALE = "Con la sola modalit&agrave; di autorizzazione ''{0}'' non &egrave; possibile associata una modalit&agrave; di autenticazione ''opzionale''";
	public static final String MESSAGGIO_ERRORE_CON_UNA_MODALITA_DI_AUTENTICAZIONE_BASIC_OBBLIGATORIA_NON_E_POSSIBILE_SELEZIONARE_ENTRAMBE_LE_MODALITA_DI_AUTORIZZAZIONE = "Con una modalit&agrave; di autenticazione ''"+TipoAutenticazione.BASIC.getLabel()+"'' obbligatoria non &egrave; possibile selezionare entrambe le modalit&agrave; di autorizzazione ''{0}'' e ''{1}''.<BR/>Per usare entrambe le autorizzazioni rendere opzionale l''autenticazione";
	public static final String MESSAGGIO_ERRORE_LA_PORTA_CONTIENE_GIA_DEI_RUOLI_CHE_NON_SONO_COMPATIBILI_CON_LA_NUOVA_AUTORIZZAZIONE = "La porta contiene gi&agrave; dei ruoli che non sono compatibili con la nuova autorizzazione ''{0}'' scelta.<BR/>Eliminare i ruoli prima di procedere con la modifica del tipo di autorizzazione.";
	public static final String MESSAGGIO_ERRORE_CON_UNA_FONTE_PER_I_RUOLI_DI_TIPO_XX_DEVE_ESSERE_ASSOCIATA_UNA_MODALITÀ_DI_AUTENTICAZIONE = "Con una {0} per i ruoli di tipo ''{1}'' deve essere associata una modalit&agrave; di autenticazione";
	public static final String MESSAGGIO_ERRORE_CON_UNA_FONTE_PER_I_RUOLI_DI_TIPO_XX_NON_E_POSSIBILE_ASSOCIATA_UNA_MODALITÀ_DI_AUTENTICAZIONE_OPZIONALE = "Con una {0} per i ruoli di tipo ''{1}'' non &egrave; possibile associata una modalit&agrave; di autenticazione ''opzionale''";
	public static final String MESSAGGIO_ERRORE_LA_PORTA_CONTIENE_DEI_RUOLI_XX_CHE_NON_SONO_COMPATIBILI_CON_LA_NUOVA_FONTE_SCELTA = "La porta contiene gi&agrave; dei ruoli ({0}) che non sono compatibili con la nuova {1} ''{2}'' scelta.";
	public static final String MESSAGGIO_ERRORE_SELEZIONARE_ALMENO_UNA_MODALITÀ_DI_AUTORIZZAZIONE = "Selezionare almeno una modalit&agrave; di autorizzazione";
	public static final String MESSAGGIO_ERRORE_IL_RUOLO_XX_E_GIA_STATO_ASSOCIATA_AL_SOGGETTO = "Il ruolo ''{0}'' &egrave; gi&agrave; stato associata al soggetto";
	public static final String MESSAGGIO_ERRORE_LO_SCOPE_XX_E_GIA_STATO_ASSOCIATA_AL_SOGGETTO = "Lo scope ''{0}'' &egrave; gi&agrave; stato associata al soggetto";
	public static final String MESSAGGIO_ERRORE_NON_ESISTONO_RUOLI_ASSOCIABILI = "Non esistono ruoli associabili";
	public static final String MESSAGGIO_ERRORE_NON_ESISTONO_SCOPE_ASSOCIABILI = "Non esistono scope associabili";
	public static final String MESSAGGIO_ERRORE_NON_ESISTONO_ULTERIORI_RUOLI_ASSOCIABILI = "Non esistono ulteriori ruoli associabili";
	public static final String MESSAGGIO_ERRORE_NON_ESISTONO_ULTERIORI_SCOPE_ASSOCIABILI = "Non esistono ulteriori scope associabili";
	public static final String MESSAGGIO_ERRORE_IL_CAMPO_XX_DEVE_RISPETTARE_IL_PATTERN_YY = "Il campo {0} deve rispettare il seguente pattern: {1}";
	public static final String MESSAGGIO_ERRORE_PROPRIETA_DI_MTOM_GIA_ASSOCIATA_ALLA_PORTA_APPLICATIVA_XX = "La propriet&agrave; di MTOM {0} &egrave; gi&agrave; stato associata alla porta applicativa {1}";
	public static final String MESSAGGIO_ERRORE_PROPRIETA_DI_MTOM_GIA_ASSOCIATA_ALLA_PORTA_DELEGATA_XX = "La propriet&agrave; di MTOM {0} &egrave; gi&agrave; stato associata alla porta delegata {1}";
	public static final String MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEL_CAMPO_CONTENT_TYPE = "Non inserire spazi nel campo Content Type";
	public static final String MESSAGGIO_ERRROE_NON_INSERIRE_SPAZI_NEL_CAMPO_PATTERN = "Non inserire spazi nel campo Pattern";
	public static final String MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEL_CAMPO_NOME = "Non inserire spazi nel campo Nome";
	public static final String MESSAGGIO_ERRORE_STATO_DELLA_RISPOSTA_DEVE_ESSERE_DISABLED_PACKAGING_UNPACKAGING_O_VERIFY = "Stato della Risposta dev'essere disabled, packaging, unpackaging o verify.";
	public static final String MESSAGGIO_ERRORE_STATO_DELLA_RICHIESTA_DEVE_ESSERE_DISABLED_PACKAGING_UNPACKAGING_O_VERIFY = "Stato della Richiesta dev'essere disabled, packaging, unpackaging o verify.";
	public static final String MESSAGGIO_ERRORE_CORRELAZIONE_APPLICATIVA_PER_LA_RISPOSTA_CON_ELEMENTO_DEFINITA_GIA_ESISTENTE = "Esiste gi&agrave; una correlazione applicativa per la risposta con elemento [{0}] definita nella {1}";
	public static final String MESSAGGIO_ERRORE_NON_INSERIRE_SPAZI_NEI_CAMPI_DI_TESTO = "Non inserire spazi nei campi di testo";
	public static final String MESSAGGIO_ERRORE_AZIONE_PORTA_NON_PUO_ESSERE_VUOTA = "Deve essere selezionata una Azione";
	public static final String MESSAGGIO_ERRORE_NOME_GRUPPO_GIA_ESISTENTE = "Il nome indicato è già stato assegnato ad un altro gruppo";
	public static final String MESSAGGIO_ERRORE_NOME_GRUPPO_NON_PUO_ESSERE_VUOTA = "Deve essere indicato un nome per il gruppo";
	public static final String MESSAGGIO_ERRORE_AZIONE_PORTA_GIA_PRESENTE = "L'azione scelta &egrave; gi&agrave; presente";
	public static final String MESSAGGIO_ERRORE_RUOLI_PRESENTI_AUTORIZZAZIONE_DISABILITATA = "Non &egrave; possibile disabilitare l'autorizzazione senza prima eliminare i ruoli associati";
	public static final String MESSAGGIO_ERRORE_SCOPE_PRESENTI_AUTORIZZAZIONE_DISABILITATA = "Non &egrave; possibile disabilitare l'autorizzazione senza prima eliminare gli scope associati";
	public static final String MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_AUTORIZZAZIONE_DISABILITATA = "Non &egrave; possibile disabilitare l'autorizzazione senza prima eliminare gli applicativi associati";
	public static final String MESSAGGIO_ERRORE_SOGGETTI_PRESENTI_AUTORIZZAZIONE_DISABILITATA = "Non &egrave; possibile disabilitare l'autorizzazione senza prima eliminare i soggetti associati";
	public static final String MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_AUTENTICAZIONE_MODIFICATA = "Non &egrave; possibile modificare l'autenticazione senza prima eliminare gli applicativi associati";
	public static final String MESSAGGIO_ERRORE_SOGGETTI_PRESENTI_AUTENTICAZIONE_MODIFICATA = "Non &egrave; possibile modificare l'autenticazione senza prima eliminare i soggetti associati";
//	public static final String MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_TRASFORMAZIONI_AUTORIZZAZIONE_DISABILITATA = "Non &egrave; possibile disabilitare l'autorizzazione senza prima eliminare gli applicativi associati alle trasformazioni:";
//	public static final String MESSAGGIO_ERRORE_SOGGETTI_PRESENTI_TRASFORMAZIONI_AUTORIZZAZIONE_DISABILITATA = "Non &egrave; possibile disabilitare l'autorizzazione senza prima eliminare i soggetti associati alle trasformazioni:";
	public static final String MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_TRASFORMAZIONI_AUTENTICAZIONE_MODIFICATA = "Non &egrave; possibile modificare l'autenticazione senza prima eliminare gli applicativi associati alle trasformazioni:";
	public static final String MESSAGGIO_ERRORE_APPLICATIVI_PRESENTI_RATE_LIMITING_AUTENTICAZIONE_MODIFICATA = "Non &egrave; possibile modificare l'autenticazione senza prima eliminare gli applicativi associati alle politiche di Rate Limiting:";
	public static final String MESSAGGIO_ERRORE_SOGGETTI_PRESENTI_TRASFORMAZIONI_AUTENTICAZIONE_MODIFICATA = "Non &egrave; possibile modificare l'autenticazione senza prima eliminare i soggetti associati alle trasformazioni:";
	public static final String MESSAGGIO_ERRORE_VALIDAZIONE_XSD_DEV_ESSERE_ABILITATO_DISABILITATO_O_WARNING_ONLY = "Validazione XSD dev'essere abilitato, disabilitato o warningOnly";
	public static final String MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_NON_VALIDO = "Valore del campo {0} non valido.";
	public static final String MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_DELLA_YY_NON_VALIDO = "Valore del campo {0} della {1} non valido.";
	public static final String MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMPO_VALORE_DEL_CAMPO_XX_YY_DELLA_ZZ_NON_VALIDO = "Valore del campo {0} {1} della {2} non valido.";
	public static final String MESSAGGIO_ERRORE_CONFIGURAZIONE_POLICY_TOKEN_DATI_INCOMPLETI_E_NECESSARIO_INDICARE_UNA_MODALITA = "Dati incompleti. &Egrave; necessario abilitare almeno una voce tra {0}";
	public static final String MESSAGGIO_ERRORE_CONFIGURAZIONE_DUMP_DATI_INCOMPLETI_E_NECESSARIO_ABILITARE_UNA_VOCE = "Impossibile abilitare la Registrazione dei messaggi di {0} senza specificare almeno una voce di Ingresso o Uscita";
	public final static String MESSAGGIO_ERRORE_AUTORIZZAZIONE_TOKEN = "I claims in ogni riga devono essere indicati come coppia (nome=valore); non è stato riscontrato il carattere separatore '='";
	public static final String MESSAGGIO_ERRORE_POLICY_OBBLIGATORIA_CON_LA_NUOVA_AUTORIZZAZIONE = "La policy &egrave; obbligatoria quando si seleziona l''autorizzazione ''{0}''";
	public static final String MESSAGGIO_ERRORE_NOME_GRUPPO_GIA_PRESENTE = "Il nome gruppo indicato &egrave; gi&agrave; utilizzato in un'altra configurazione";
	public static final String MESSAGGIO_ERRORE_TOKEN_OPTIONS_NON_INDICATI = "Dati incompleti. Definire almeno un claim utilizzato per autorizzare la richiesta";
	public static final String MESSAGGIO_ERRORE_CORS_SPAZI_BIANCHI_NON_AMMESSI = "Non inserire spazi bianchi nei valori del campo {0}";
	public static final String MESSAGGIO_ERRORE_CORS_CAMPO_OBBLIGATORIO = "Inserire almeno un valore nel campo {0}";
	public static final String MESSAGGIO_ERRORE_CORS_DIPENDENZA = "Non è consentito abilitare entrambe le modalità {0} e {1}";
	public static final String MESSAGGIO_ERRORE_CORS_ALLOW_METHOD_NON_VALIDO = "Il valore {0} indicato per il campo {1} non rappresenta un HTTP-Method valido";
	public static final String MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_DUPLICATA = "&Egrave; gi&agrave; presente una regola di trasformazione con i parametri di applicabilit&agrave; indicati.";
	public static final String MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_NOME = "&Egrave; gi&agrave; presente una regola di trasformazione con il nome indicato.";
	public static final String MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_DUPLICATA_APPLICATIVO = "&Egrave; gi&agrave; presente una regola di trasformazione, con gli stessi parametri di applicabilit&agrave; e l'applicativo selezionato.";
	public static final String MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_DUPLICATA_SOGGETTO = "&Egrave; gi&agrave; presente una regola di trasformazione, con gli stessi parametri di applicabilit&agrave; e il soggetto selezionato.";
	public static final String MESSAGGIO_ERRORE_AZIONE_NON_ASSEGNABILE = "Azione {0} non assegnabile poich&egrave; utilizzata come filtro della policy di rate limiting ''{1}''";
	public static final String MESSAGGIO_ERRORE_AZIONE_NON_ASSEGNABILE_GRUPPO = "Azione {0} non assegnabile poich&egrave; utilizzata come filtro della policy di rate limiting ''{1}'' (gruppo: {2})";

	public static final String MESSAGGIO_ERRORE_AUTENTICAZIONE_CUSTOM_NON_INDICATA = "Dati incompleti. Definire almeno un valore per il controllo di autenticazione di tipo custom";
	public static final String MESSAGGIO_ERRORE_AUTORIZZAZIONE_CUSTOM_NON_INDICATA = "Dati incompleti. Definire almeno un valore per il controllo di autorizzazione di tipo custom";
	public static final String MESSAGGIO_ERRORE_AUTORIZZAZIONE_CONTENUTO_NON_INDICATA = "Dati incompleti. Definire almeno un controllo di autorizzazione per il contenuto della richiesta";
	public static final String MESSAGGIO_ERRORE_AUTORIZZAZIONE_CONTENUTO_CUSTOM_NON_INDICATA = "Dati incompleti. Definire almeno un valore per il controllo di autorizzazione contenuti di tipo custom";
	public final static String MESSAGGIO_ERRORE_AUTORIZZAZIONE_CONTENUTO_TOKEN_NON_VALIDI = "I controlli di autorizzazione del contenuto in ogni riga devono essere indicati come coppia (risorsa=valore); non è stato riscontrato il carattere separatore '='";
	public final static String MESSAGGIO_ERRORE_AUTORIZZAZIONE_CONTENUTO_TOKEN_NON_VALIDI_RISORSA_NON_DEFINITA_PREFIX = "I controlli di autorizzazione del contenuto in ogni riga devono essere indicati come coppia (risorsa=valore); non è stato riscontrata l'indicazione di una risorsa, tramite espressioni dinamiche, nella linea ";

	/** ICONE E TOOLTIP */
	public final static String ICONA_MODIFICA_CONFIGURAZIONE = "&#xE3C9;";
	public final static String ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP = "Modifica";
	public final static String ICONA_MODIFICA_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO = "Modifica {0}";
	
	public final static String ICONA_VISUALIZZA = "&#xE89E;";
	public final static String ICONA_VISUALIZZA_TOOLTIP = "Visualizza";
	public final static String ICONA_VISUALIZZA_TOOLTIP_CON_PARAMETRO = "Visualizza {0}";
	
	public final static String ICONA_VERIFICA = "&#xE8BE;";
	public final static String ICONA_VERIFICA_TOOLTIP = "Verifica";
	public final static String ICONA_VERIFICA_TOOLTIP_CON_PARAMETRO = "Verifica {0}";
	
	public final static String ICONA_MODIFICA_CONFIGURAZIONE_CONNETTORI_MULTIPLI = "&#xE8B8;";
	public final static String ICONA_MODIFICA_CONFIGURAZIONE_CONNETTORI_MULTIPLI_TOOLTIP = "Configurazione Connettori Multipli";
	public final static String ICONA_MODIFICA_CONFIGURAZIONE_CONNETTORI_MULTIPLI_TOOLTIP_CON_PARAMETRO = "Configurazione Connettori Multipli {0}";
	
	public final static String ICONA_ELENCO_CONNETTORI_MULTIPLI = "list_alt";
	public final static String ICONA_ELENCO_CONNETTORI_MULTIPLI_TOOLTIP = "Elenco Connettori";
	public final static String ICONA_ELENCO_CONNETTORI_MULTIPLI_TOOLTIP_CON_PARAMETRO = "Elenco Connettori {0}";
	
	public final static String ICONA_UPGRADE_CONFIGURAZIONE = "&#xE3CA;";
	public final static String ICONA_UPGRADE_CONFIGURAZIONE_TOOLTIP = "Aggiorna";
	public final static String ICONA_UPGRADE_CONFIGURAZIONE_TOOLTIP_CON_PARAMETRO = "Aggiorna {0}";
	
	public final static String ICONA_MODIFICA_TOGGLE_ON = "toggle_on";
	public final static String ICONA_MODIFICA_TOGGLE_OFF = "toggle_off";
	
	// Indica il numero delle possibili classi CSS per i tag dei gruppi, modificare questo valore se si vuole modificare il numero delle classi disponibili
	public final static Integer NUMERO_GRUPPI_CSS = Costanti.NUMERO_GRUPPI_CSS;
}
