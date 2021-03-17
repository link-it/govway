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

package org.openspcoop2.core.constants;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.datasource.DataSourceParams;
import org.openspcoop2.utils.jmx.CostantiJMX;

/**
 * Costanti 
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {
	
	 /** Contesto della Porta di Dominio */ 
	public final static String ID_TRANSAZIONE = "ID";
	public final static String OPENSPCOOP_STATE = "OPENSPCOOP_STATE";
	public final static String PROTOCOL_NAME = "PROTOCOL_NAME";
	public final static String PROTOCOL_WEB_CONTEXT = "PROTOCOL_WEB_CONTEXT";
	public final static String INTEGRATION_MANAGER_ENGINE_AUTHORIZED = "INTEGRATION_MANAGER_ENGINE_DIRECT_INVOCATION";
	public final static String ID_PORTA = "ID_PORTA";
	public final static String ID_FUNZIONE = "ID_FUNZIONE";
	public final static String ID_MESSAGGIO = "ID_MESSAGGIO";
	public final static String ID_FRUITORE = "FRUITORE";
	public final static String ID_SERVIZIO = "SERVIZIO";
	public final static String HEADER_TRASPORTO = "HEADER_TRASPORTO";
	public final static String TIPO_OPERAZIONE_IM = "TIPO_OPERAZIONE_IM";
	public final static String PORTA_DELEGATA = "PORTA_DELEGATA";
	public final static String STATELESS = "STATELESS";
	public final static String DATA_PRESA_IN_CARICO = "DATA_PRESA_IN_CARICO";
	public final static String ERRORE_UTILIZZO_CONNETTORE = "ERRORE_UTILIZZO_CONNETTORE";
	public final static String ERRORE_SOAP_FAULT_SERVER = "ERRORE_SOAP_FAULT_SERVER";
	public final static String CONTENUTO_RICHIESTA_NON_RICONOSCIUTO = "CONTENUTO_RICHIESTA_NON_RICONOSCIUTO";
	public final static String CONTENUTO_RISPOSTA_NON_RICONOSCIUTO = "CONTENUTO_RISPOSTA_NON_RICONOSCIUTO";
	public final static String ERRORE_AUTENTICAZIONE = "ERRORE_AUTENTICAZIONE";
	public final static String ERRORE_TOKEN = "ERRORE_TOKEN";
	public final static String ERRORE_AUTORIZZAZIONE = "ERRORE_AUTORIZZAZIONE";
	public final static String ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA = "ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA";
	public final static String ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA = "ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA";
	public final static String ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA = "ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA";
	public final static String ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA = "ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA";
	public final static String ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA = "ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA";
	public final static String ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA = "ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA";
	public final static String ERRORE_VALIDAZIONE_RICHIESTA = "ERRORE_VALIDAZIONE_RICHIESTA";
	public final static String ERRORE_VALIDAZIONE_RISPOSTA = "ERRORE_VALIDAZIONE_RISPOSTA";
	public final static String ERRORE_TRASFORMAZIONE_RICHIESTA = "ERRORE_TRASFORMAZIONE_RICHIESTA";
	public final static String ERRORE_TRASFORMAZIONE_RISPOSTA = "ERRORE_TRASFORMAZIONE_RISPOSTA";
	public final static String ERRORE_SOSPENSIONE = "ERRORE_SOSPENSIONE";
	public final static String ERRORE_GENERICO = "ERRORE_GENERICO";
	public final static String TOKEN_NON_PRESENTE = "TOKEN_NON_PRESENTE";
	public final static String ERRORE_AUTENTICAZIONE_TOKEN = "ERRORE_AUTENTICAZIONE_TOKEN";
	public final static String API_NON_INDIVIDUATA = "API_NON_INDIVIDUATA";
	public final static String OPERAZIONE_NON_INDIVIDUATA = "OPERAZIONE_NON_INDIVIDUATA";
	public final static String URL_INVOCAZIONE = "URL_INVOCAZIONE";
	public final static String CREDENZIALI_INVOCAZIONE = "CREDENZIALI_INVOCAZIONE";
	public final static String CLIENT_IP_REMOTE_ADDRESS = "CLIENT_IP_REMOTE_ADDRESS";
	public final static String CLIENT_IP_TRANSPORT_ADDRESS = "CLIENT_IP_TRANSPORT_ADDRESS";	
	public final static String IDENTIFICATIVO_AUTENTICATO = "IDENTIFICATIVO_AUTENTICATO";
	public static final String PROPRIETA_CONFIGURAZIONE = "PROPRIETA_CONFIGURAZIONE";
	public final static String RICHIESTA_DUPLICATA = "RICHIESTA_DUPLICATA";
	public final static String RISPOSTA_DUPLICATA = "RISPOSTA_DUPLICATA";
	public final static String MESSAGE_BOX = "MESSAGE_BOX";
		
	public final static String EMESSI_DIAGNOSTICI_ERRORE = "EMESSI_DIAGNOSTICI_ERRORE";
	public final static String REQUEST_INFO = "REQUEST_INFO";
	public final static String EXTENDED_INFO_TRANSAZIONE = "EXTENDED_INFO_TRANSAZIONE";
	public final static String CORS_PREFLIGHT_REQUEST_VIA_GATEWAY = "CORS_PREFLIGHT_REQUEST_VIA_GATEWAY";
	public final static String CORS_PREFLIGHT_REQUEST_TRASPARENTE = "CORS_PREFLIGHT_REQUEST_TRASPARENTE";
	
	public final static String CONSEGNA_MULTIPLA_SINCRONA_CONFIGURAZIONE = "CONSEGNA_MULTIPLA_SINCRONA_CONFIGURAZIONE";
	public final static String CONSEGNA_MULTIPLA_SINCRONA = "CONSEGNA_MULTIPLA_SINCRONA";
	public final static String CONSEGNA_MULTIPLA_CONNETTORI = "CONSEGNA_MULTIPLA_CONNETTORI";
	
	public final static String ERRORE_VALIDAZIONE_PROTOCOLLO = "ERRORE_PROTOCOLLO";
	
	public final static String ERRORE_TRUE = "true";
	
	public final static String RICHIESTA_INOLTRATA_BACKEND = "RICHIESTA_INOLTRATA_BACKEND";
	public final static String RICHIESTA_INOLTRATA_BACKEND_VALORE = "true";
	
	public final static String [] CONTEXT_OBJECT = 
		new String [] {Costanti.ID_TRANSAZIONE,Costanti.PROTOCOL_NAME,
			Costanti.ID_PORTA,Costanti.ID_FUNZIONE,
			Costanti.ID_MESSAGGIO,Costanti.ID_FRUITORE,Costanti.ID_SERVIZIO,
			Costanti.HEADER_TRASPORTO,Costanti.TIPO_OPERAZIONE_IM,
			Costanti.STATELESS,Costanti.DATA_PRESA_IN_CARICO,
			Costanti.ERRORE_UTILIZZO_CONNETTORE,
			Costanti.ERRORE_SOAP_FAULT_SERVER,
			Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO,
			Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO,
			Costanti.ERRORE_AUTENTICAZIONE,
			Costanti.ERRORE_TOKEN,
			Costanti.ERRORE_AUTORIZZAZIONE,
			Costanti.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA,
			Costanti.ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA,
			Costanti.ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA,
			Costanti.ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA,
			Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA,
			Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA,
			Costanti.URL_INVOCAZIONE,
			Costanti.CREDENZIALI_INVOCAZIONE,
			Costanti.CLIENT_IP_REMOTE_ADDRESS,
			Costanti.CLIENT_IP_TRANSPORT_ADDRESS,
			Costanti.IDENTIFICATIVO_AUTENTICATO};
    
	public final static String CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION = "CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION";
	public final static String CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION = "CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION";
	
	public final static String SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED = "undefined";
	public final static String SESSION_ATTRIBUTE_VALUE_FILTER_UNDEFINED = "undefined";
	
	private final static String JMX_NAME_DATASOURCE_PDD = "DatasourceGW";
	public static DataSourceParams getDataSourceParamsPdD(boolean bindJMX,String tipoDB){
		DataSourceParams dsParams = new DataSourceParams();
		dsParams.setBindJmx(bindJMX);
		dsParams.setWrapOriginalMethods(true); // per poter usare anche getConnection e getConnection(String,String)
		dsParams.setDatabaseType(TipiDatabase.toEnumConstant(tipoDB));
		dsParams.setJmxDomain(CostantiJMX.JMX_DOMINIO);
		dsParams.setJmxName(Costanti.JMX_NAME_DATASOURCE_PDD);
		return dsParams;
	}
	
	public final static String WEB_NEW_LINE = "<br/>";
	
	public static final String MAPPING_EROGAZIONE_PA_NOME_DEFAULT = "__qualsiasi__";
	public static final String MAPPING_FRUIZIONE_PD_NOME_DEFAULT = "__qualsiasi__";
	public static final String MAPPING_DESCRIZIONE_DEFAULT = "Predefinito";
	public static final String MAPPING_EROGAZIONE_PA_DESCRIZIONE_DEFAULT = MAPPING_DESCRIZIONE_DEFAULT;
	public static final String MAPPING_FRUIZIONE_PD_DESCRIZIONE_DEFAULT = MAPPING_DESCRIZIONE_DEFAULT;
	
	
    /** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO = "OneWay_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO = "Sincrono_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO = "AsincronoSimmetrico_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA = "AsincronoSimmetrico_ConsegnaRisposta";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO = "AsincronoAsimmetrico_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_ASINCRONO_ASIMMETRICO_POLLING = "AsincronoAsimmetrico_Polling";
	/** Variabile che indica un tipo di scenario gestito */
	public final static String SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI = "ConsegnaContenutiApplicativi";
	
	
	public final static String LABEL_PARAMETRO_PROTOCOLLO_DI = "Profilo di Interoperabilità";
	public final static String LABEL_PARAMETRO_PROTOCOLLI_DI = "Profili di Interoperabilità";
	public final static String LABEL_PARAMETRO_PROTOCOLLO = "Profilo Interoperabilità";
	public final static String LABEL_PARAMETRO_PROTOCOLLI = "Profili Interoperabilità";
	public final static String LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE = StringEscapeUtils.escapeHtml(LABEL_PARAMETRO_PROTOCOLLO_DI);
	public final static String LABEL_PARAMETRO_PROTOCOLLI_DI_HTML_ESCAPE = StringEscapeUtils.escapeHtml(LABEL_PARAMETRO_PROTOCOLLI_DI);
	public final static String LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE = StringEscapeUtils.escapeHtml(LABEL_PARAMETRO_PROTOCOLLO);
	public final static String LABEL_PARAMETRO_PROTOCOLLI_HTML_ESCAPE = StringEscapeUtils.escapeHtml(LABEL_PARAMETRO_PROTOCOLLI);
	
	public final static String LABEL_PARAMETRO_PROTOCOLLO_COMPACT = "Profilo";
	public final static String LABEL_PARAMETRO_PROTOCOLLI_COMPACT = "Profili";
	
	public final static String LABEL_PARAMETRO_CUSTOM_IN_SELECT = "plugin";
}
