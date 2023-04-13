/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;
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
	public final static MapKey<String> ID_TRANSAZIONE = Map.newMapKey("ID");
	public final static MapKey<String> OPENSPCOOP_STATE = Map.newMapKey("OPENSPCOOP_STATE");
	public final static MapKey<String> PROTOCOL_NAME = Map.newMapKey("PROTOCOL_NAME");
	public final static MapKey<String> PROTOCOL_WEB_CONTEXT = Map.newMapKey("PROTOCOL_WEB_CONTEXT");
	public final static MapKey<String> INTEGRATION_MANAGER_ENGINE_AUTHORIZED = Map.newMapKey("INTEGRATION_MANAGER_ENGINE_DIRECT_INVOCATION");
	public final static MapKey<String> ID_PORTA = Map.newMapKey("ID_PORTA");
	public final static MapKey<String> ID_FUNZIONE = Map.newMapKey("ID_FUNZIONE");
	public final static MapKey<String> ID_MESSAGGIO = Map.newMapKey("ID_MESSAGGIO");
	public final static MapKey<String> ID_FRUITORE = Map.newMapKey("FRUITORE");
	public final static MapKey<String> ID_SERVIZIO = Map.newMapKey("SERVIZIO");
	public final static MapKey<String> HEADER_TRASPORTO = Map.newMapKey("HEADER_TRASPORTO");
	public final static MapKey<String> TIPO_OPERAZIONE_IM = Map.newMapKey("TIPO_OPERAZIONE_IM");
	public final static MapKey<String> PORTA_DELEGATA = Map.newMapKey("PORTA_DELEGATA");
	public final static MapKey<String> STATELESS = Map.newMapKey("STATELESS");
	public final static MapKey<String> DATA_PRESA_IN_CARICO = Map.newMapKey("DATA_PRESA_IN_CARICO");
	public final static MapKey<String> ERRORE_UTILIZZO_CONNETTORE = Map.newMapKey("ERRORE_UTILIZZO_CONNETTORE");
	public final static MapKey<String> ERRORE_SOAP_FAULT_SERVER = Map.newMapKey("ERRORE_SOAP_FAULT_SERVER");
	public final static MapKey<String> CONTENUTO_RICHIESTA_NON_RICONOSCIUTO = Map.newMapKey("CONTENUTO_RICHIESTA_NON_RICONOSCIUTO");
	public final static MapKey<String> CONTENUTO_RISPOSTA_NON_RICONOSCIUTO = Map.newMapKey("CONTENUTO_RISPOSTA_NON_RICONOSCIUTO");
	public final static MapKey<String> ERRORE_AUTENTICAZIONE = Map.newMapKey("ERRORE_AUTENTICAZIONE");
	public final static MapKey<String> ERRORE_TOKEN = Map.newMapKey("ERRORE_TOKEN");
	public final static MapKey<String> ERRORE_AUTORIZZAZIONE = Map.newMapKey("ERRORE_AUTORIZZAZIONE");
	public final static MapKey<String> ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA = Map.newMapKey("ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA");
	public final static MapKey<String> ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA = Map.newMapKey("ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA");
	public final static MapKey<String> ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA = Map.newMapKey("ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA");
	public final static MapKey<String> ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA = Map.newMapKey("ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA");
	public final static MapKey<String> ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA = Map.newMapKey("ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA");
	public final static MapKey<String> ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA = Map.newMapKey("ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA");
	public final static MapKey<String> ERRORE_VALIDAZIONE_RICHIESTA = Map.newMapKey("ERRORE_VALIDAZIONE_RICHIESTA");
	public final static MapKey<String> ERRORE_VALIDAZIONE_RISPOSTA = Map.newMapKey("ERRORE_VALIDAZIONE_RISPOSTA");
	public final static MapKey<String> ERRORE_TRASFORMAZIONE_RICHIESTA = Map.newMapKey("ERRORE_TRASFORMAZIONE_RICHIESTA");
	public final static MapKey<String> ERRORE_TRASFORMAZIONE_RISPOSTA = Map.newMapKey("ERRORE_TRASFORMAZIONE_RISPOSTA");
	public final static MapKey<String> ERRORE_SOSPENSIONE = Map.newMapKey("ERRORE_SOSPENSIONE");
	public final static MapKey<String> ERRORE_GENERICO = Map.newMapKey("ERRORE_GENERICO");
	public final static MapKey<String> TOKEN_NON_PRESENTE = Map.newMapKey("TOKEN_NON_PRESENTE");
	public final static MapKey<String> ERRORE_AUTENTICAZIONE_TOKEN = Map.newMapKey("ERRORE_AUTENTICAZIONE_TOKEN");
	public final static MapKey<String> API_NON_INDIVIDUATA = Map.newMapKey("API_NON_INDIVIDUATA");
	public final static MapKey<String> OPERAZIONE_NON_INDIVIDUATA = Map.newMapKey("OPERAZIONE_NON_INDIVIDUATA");
	public final static MapKey<String> URL_INVOCAZIONE = Map.newMapKey("URL_INVOCAZIONE");
	public final static MapKey<String> CREDENZIALI_INVOCAZIONE = Map.newMapKey("CREDENZIALI_INVOCAZIONE");
	public final static MapKey<String> CLIENT_IP_REMOTE_ADDRESS = Map.newMapKey("CLIENT_IP_REMOTE_ADDRESS");
	public final static MapKey<String> CLIENT_IP_TRANSPORT_ADDRESS = Map.newMapKey("CLIENT_IP_TRANSPORT_ADDRESS");	
	public final static MapKey<String> IDENTIFICATIVO_AUTENTICATO = Map.newMapKey("IDENTIFICATIVO_AUTENTICATO");
	public static final MapKey<String> PROPRIETA_CONFIGURAZIONE = Map.newMapKey("PROPRIETA_CONFIGURAZIONE");
	public static final MapKey<String> PROPRIETA_SOGGETTO_EROGATORE = Map.newMapKey("PROPRIETA_SOGGETTO_EROGATORE");
	public static final MapKey<String> PROPRIETA_SOGGETTO_FRUITORE = Map.newMapKey("PROPRIETA_SOGGETTO_FRUITORE");
	public static final MapKey<String> PROPRIETA_APPLICATIVO = Map.newMapKey("PROPRIETA_APPLICATIVO");
	public final static MapKey<String> RICHIESTA_DUPLICATA = Map.newMapKey("RICHIESTA_DUPLICATA");
	public final static MapKey<String> RISPOSTA_DUPLICATA = Map.newMapKey("RISPOSTA_DUPLICATA");
	public final static MapKey<String> MESSAGE_BOX = Map.newMapKey("MESSAGE_BOX");
	public final static MapKey<String> SECURITY_TOKEN = Map.newMapKey("SECURITY_TOKEN");
	public final static MapKey<String> ID_APPLICATIVO_TOKEN = Map.newMapKey("ID_APPLICATIVO_TOKEN");
	public static final MapKey<String> PROPRIETA_APPLICATIVO_TOKEN = Map.newMapKey("PROPRIETA_APPLICATIVO_TOKEN");
	public static final MapKey<String> PROPRIETA_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN = Map.newMapKey("PROPRIETA_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN");
    public final static MapKey<String> IDENTITA_GESTORE_CREDENZIALI = Map.newMapKey("IDENTITA_GESTORE_CREDENZIALI");
    public static final MapKey<String> INFORMAZIONI_INTEGRAZIONE = Map.newMapKey("INFORMAZIONI_INTEGRAZIONE");
    public static final MapKey<String> INFORMAZIONI_INTEGRAZIONE_RISPOSTA = Map.newMapKey("INFORMAZIONI_INTEGRAZIONE_RISPOSTA");
		
	public final static MapKey<String> EMESSI_DIAGNOSTICI_ERRORE = Map.newMapKey("EMESSI_DIAGNOSTICI_ERRORE");
	public final static MapKey<String> REQUEST_INFO = Map.newMapKey("REQUEST_INFO");
	public final static MapKey<String> REQUEST_INFO_IN_MEMORY = Map.newMapKey("REQUEST_INFO_IN_MEMORY");
	public final static MapKey<String> EXTENDED_INFO_TRANSAZIONE = Map.newMapKey("EXTENDED_INFO_TRANSAZIONE");
	public final static MapKey<String> CORS_PREFLIGHT_REQUEST_VIA_GATEWAY = Map.newMapKey("CORS_PREFLIGHT_REQUEST_VIA_GATEWAY");
	public final static MapKey<String> CORS_PREFLIGHT_REQUEST_TRASPARENTE = Map.newMapKey("CORS_PREFLIGHT_REQUEST_TRASPARENTE");
	
	public final static MapKey<String> CONSEGNA_MULTIPLA_SINCRONA_CONFIGURAZIONE = Map.newMapKey("CONSEGNA_MULTIPLA_SINCRONA_CONFIGURAZIONE");
	public final static MapKey<String> CONSEGNA_MULTIPLA_SINCRONA = Map.newMapKey("CONSEGNA_MULTIPLA_SINCRONA");
	public final static MapKey<String> CONSEGNA_MULTIPLA_CONNETTORI = Map.newMapKey("CONSEGNA_MULTIPLA_CONNETTORI");
	public final static MapKey<String> CONSEGNA_MULTIPLA_CONNETTORI_BY_ID = Map.newMapKey("CONSEGNA_MULTIPLA_CONNETTORI_BY_ID");
	public final static MapKey<String> CONSEGNA_MULTIPLA_CONNETTORI_BY_SA = Map.newMapKey("CONSEGNA_MULTIPLA_CONNETTORI_BY_SA");
	
	public final static MapKey<String> CONSEGNA_MULTIPLA_NOME_CONNETTORE = Map.newMapKey("CONSEGNA_MULTIPLA_NOME_CONNETTORE");
	public final static MapKey<String> CONSEGNA_MULTIPLA_NOME_CONNETTORE_API = Map.newMapKey("CONSEGNA_MULTIPLA_NOME_CONNETTORE_API");
	
	public final static MapKey<String> LIMITED_STREAM = Map.newMapKey("LIMITED_STREAM");
	
	public final static MapKey<String> ERRORE_VALIDAZIONE_PROTOCOLLO = Map.newMapKey("ERRORE_PROTOCOLLO");
	
	public final static String ERRORE_TRUE = "true";
	
	public final static MapKey<String> RICHIESTA_INOLTRATA_BACKEND = Map.newMapKey("RICHIESTA_INOLTRATA_BACKEND");
	public final static String RICHIESTA_INOLTRATA_BACKEND_VALORE = "true";
	
	public final static List<MapKey<String>> CONTEXT_OBJECT = new ArrayList<MapKey<String>>();
	static {
		CONTEXT_OBJECT.add(Costanti.ID_TRANSAZIONE);
		CONTEXT_OBJECT.add(Costanti.PROTOCOL_NAME);
		CONTEXT_OBJECT.add(Costanti.ID_PORTA);
		CONTEXT_OBJECT.add(Costanti.ID_FUNZIONE);
		CONTEXT_OBJECT.add(Costanti.ID_MESSAGGIO);
		CONTEXT_OBJECT.add(Costanti.ID_FRUITORE);
		CONTEXT_OBJECT.add(Costanti.ID_SERVIZIO);
		CONTEXT_OBJECT.add(Costanti.HEADER_TRASPORTO);
		CONTEXT_OBJECT.add(Costanti.TIPO_OPERAZIONE_IM);
		CONTEXT_OBJECT.add(Costanti.STATELESS);
		CONTEXT_OBJECT.add(Costanti.DATA_PRESA_IN_CARICO);
		CONTEXT_OBJECT.add(Costanti.ERRORE_UTILIZZO_CONNETTORE);
		CONTEXT_OBJECT.add(Costanti.ERRORE_SOAP_FAULT_SERVER);
		CONTEXT_OBJECT.add(Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO);
		CONTEXT_OBJECT.add(Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO);
		CONTEXT_OBJECT.add(Costanti.ERRORE_AUTENTICAZIONE);
		CONTEXT_OBJECT.add(Costanti.ERRORE_TOKEN);
		CONTEXT_OBJECT.add(Costanti.ERRORE_AUTORIZZAZIONE);
		CONTEXT_OBJECT.add(Costanti.ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA);
		CONTEXT_OBJECT.add(Costanti.ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA);
		CONTEXT_OBJECT.add(Costanti.ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA);
		CONTEXT_OBJECT.add(Costanti.ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA);
		CONTEXT_OBJECT.add(Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA);
		CONTEXT_OBJECT.add(Costanti.ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA);
		CONTEXT_OBJECT.add(Costanti.URL_INVOCAZIONE);
		CONTEXT_OBJECT.add(Costanti.CREDENZIALI_INVOCAZIONE);
		CONTEXT_OBJECT.add(Costanti.CLIENT_IP_REMOTE_ADDRESS);
		CONTEXT_OBJECT.add(Costanti.CLIENT_IP_TRANSPORT_ADDRESS);
		CONTEXT_OBJECT.add(Costanti.IDENTIFICATIVO_AUTENTICATO);
	}
    
	public final static MapKey<String> CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION = Map.newMapKey("CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION");
	public final static MapKey<String> CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION = Map.newMapKey("CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION");
	
	public final static String SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED = "undefined";
	public final static String SESSION_ATTRIBUTE_VALUE_FILTER_UNDEFINED = "undefined";
	
    public final static String PREFIX_TIMEOUT_REQUEST = "Request ";
    public final static String PREFIX_TIMEOUT_RESPONSE = "Response ";
    
    public final static String PREFIX_LIMITED_REQUEST = "Request ";
    public final static String PREFIX_LIMITED_RESPONSE = "Response ";
	
	public final static String JMX_NAME_DATASOURCE_PDD = "DatasourceGW";
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
	
	public final static String VALUE_PARAMETRO_CUSTOM_IN_SELECT =  "custom";
	
	public final static String LABEL_PARAMETRO_CUSTOM_IN_SELECT = "plugin";
}
