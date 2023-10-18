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
	public static final MapKey<String> ID_TRANSAZIONE = Map.newMapKey("ID");
	public static final MapKey<String> OPENSPCOOP_STATE = Map.newMapKey("OPENSPCOOP_STATE");
	public static final MapKey<String> PROTOCOL_NAME = Map.newMapKey("PROTOCOL_NAME");
	public static final MapKey<String> PROTOCOL_WEB_CONTEXT = Map.newMapKey("PROTOCOL_WEB_CONTEXT");
	public static final MapKey<String> INTEGRATION_MANAGER_ENGINE_AUTHORIZED = Map.newMapKey("INTEGRATION_MANAGER_ENGINE_DIRECT_INVOCATION");
	public static final MapKey<String> ID_PORTA = Map.newMapKey("ID_PORTA");
	public static final MapKey<String> ID_FUNZIONE = Map.newMapKey("ID_FUNZIONE");
	public static final MapKey<String> ID_MESSAGGIO = Map.newMapKey("ID_MESSAGGIO");
	public static final MapKey<String> ID_FRUITORE = Map.newMapKey("FRUITORE");
	public static final MapKey<String> ID_SERVIZIO = Map.newMapKey("SERVIZIO");
	public static final MapKey<String> HEADER_TRASPORTO = Map.newMapKey("HEADER_TRASPORTO");
	public static final MapKey<String> TIPO_OPERAZIONE_IM = Map.newMapKey("TIPO_OPERAZIONE_IM");
	public static final MapKey<String> PORTA_DELEGATA = Map.newMapKey("PORTA_DELEGATA");
	public static final MapKey<String> STATELESS = Map.newMapKey("STATELESS");
	public static final MapKey<String> DATA_PRESA_IN_CARICO = Map.newMapKey("DATA_PRESA_IN_CARICO");
	public static final MapKey<String> ERRORE_UTILIZZO_CONNETTORE = Map.newMapKey("ERRORE_UTILIZZO_CONNETTORE");
	public static final MapKey<String> ERRORE_SOAP_FAULT_SERVER = Map.newMapKey("ERRORE_SOAP_FAULT_SERVER");
	public static final MapKey<String> CONTENUTO_RICHIESTA_NON_RICONOSCIUTO = Map.newMapKey("CONTENUTO_RICHIESTA_NON_RICONOSCIUTO");
	public static final MapKey<String> CONTENUTO_RISPOSTA_NON_RICONOSCIUTO = Map.newMapKey("CONTENUTO_RISPOSTA_NON_RICONOSCIUTO");
	public static final MapKey<String> ERRORE_AUTENTICAZIONE = Map.newMapKey("ERRORE_AUTENTICAZIONE");
	public static final MapKey<String> ERRORE_TOKEN = Map.newMapKey("ERRORE_TOKEN");
	public static final MapKey<String> ERRORE_NEGOZIAZIONE_TOKEN = Map.newMapKey("ERRORE_NEGOZIAZIONE_TOKEN");
	public static final MapKey<String> ERRORE_AUTORIZZAZIONE = Map.newMapKey("ERRORE_AUTORIZZAZIONE");
	public static final MapKey<String> ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA = Map.newMapKey("ERRORE_SICUREZZA_MESSAGGIO_RICHIESTA");
	public static final MapKey<String> ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA = Map.newMapKey("ERRORE_SICUREZZA_MESSAGGIO_RISPOSTA");
	public static final MapKey<String> ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA = Map.newMapKey("ERRORE_ALLEGATI_MESSAGGIO_RICHIESTA");
	public static final MapKey<String> ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA = Map.newMapKey("ERRORE_ALLEGATI_MESSAGGIO_RISPOSTA");
	public static final MapKey<String> ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA = Map.newMapKey("ERRORE_CORRELAZIONE_APPLICATIVA_RICHIESTA");
	public static final MapKey<String> ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA = Map.newMapKey("ERRORE_CORRELAZIONE_APPLICATIVA_RISPOSTA");
	public static final MapKey<String> ERRORE_VALIDAZIONE_RICHIESTA = Map.newMapKey("ERRORE_VALIDAZIONE_RICHIESTA");
	public static final MapKey<String> ERRORE_VALIDAZIONE_RISPOSTA = Map.newMapKey("ERRORE_VALIDAZIONE_RISPOSTA");
	public static final MapKey<String> ERRORE_TRASFORMAZIONE_RICHIESTA = Map.newMapKey("ERRORE_TRASFORMAZIONE_RICHIESTA");
	public static final MapKey<String> ERRORE_TRASFORMAZIONE_RISPOSTA = Map.newMapKey("ERRORE_TRASFORMAZIONE_RISPOSTA");
	public static final MapKey<String> ERRORE_SOSPENSIONE = Map.newMapKey("ERRORE_SOSPENSIONE");
	public static final MapKey<String> ERRORE_GENERICO = Map.newMapKey("ERRORE_GENERICO");
	public static final MapKey<String> TOKEN_NON_PRESENTE = Map.newMapKey("TOKEN_NON_PRESENTE");
	public static final MapKey<String> ERRORE_AUTENTICAZIONE_TOKEN = Map.newMapKey("ERRORE_AUTENTICAZIONE_TOKEN");
	public static final MapKey<String> API_NON_INDIVIDUATA = Map.newMapKey("API_NON_INDIVIDUATA");
	public static final MapKey<String> OPERAZIONE_NON_INDIVIDUATA = Map.newMapKey("OPERAZIONE_NON_INDIVIDUATA");
	public static final MapKey<String> URL_INVOCAZIONE = Map.newMapKey("URL_INVOCAZIONE");
	public static final MapKey<String> CREDENZIALI_INVOCAZIONE = Map.newMapKey("CREDENZIALI_INVOCAZIONE");
	public static final MapKey<String> CLIENT_IP_REMOTE_ADDRESS = Map.newMapKey("CLIENT_IP_REMOTE_ADDRESS");
	public static final MapKey<String> CLIENT_IP_TRANSPORT_ADDRESS = Map.newMapKey("CLIENT_IP_TRANSPORT_ADDRESS");	
	public static final MapKey<String> IDENTIFICATIVO_AUTENTICATO = Map.newMapKey("IDENTIFICATIVO_AUTENTICATO");
	public static final MapKey<String> PROPRIETA_CONFIGURAZIONE = Map.newMapKey("PROPRIETA_CONFIGURAZIONE");
	public static final MapKey<String> PROPRIETA_SOGGETTO_EROGATORE = Map.newMapKey("PROPRIETA_SOGGETTO_EROGATORE");
	public static final MapKey<String> PROPRIETA_SOGGETTO_FRUITORE = Map.newMapKey("PROPRIETA_SOGGETTO_FRUITORE");
	public static final MapKey<String> PROPRIETA_APPLICATIVO = Map.newMapKey("PROPRIETA_APPLICATIVO");
	public static final MapKey<String> RICHIESTA_DUPLICATA = Map.newMapKey("RICHIESTA_DUPLICATA");
	public static final MapKey<String> RISPOSTA_DUPLICATA = Map.newMapKey("RISPOSTA_DUPLICATA");
	public static final MapKey<String> MESSAGE_BOX = Map.newMapKey("MESSAGE_BOX");
	public static final MapKey<String> SECURITY_TOKEN = Map.newMapKey("SECURITY_TOKEN");
	public static final MapKey<String> ID_APPLICATIVO_TOKEN = Map.newMapKey("ID_APPLICATIVO_TOKEN");
	public static final MapKey<String> PROPRIETA_APPLICATIVO_TOKEN = Map.newMapKey("PROPRIETA_APPLICATIVO_TOKEN");
	public static final MapKey<String> PROPRIETA_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN = Map.newMapKey("PROPRIETA_SOGGETTO_PROPRIETARIO_APPLICATIVO_TOKEN");
    public static final MapKey<String> IDENTITA_GESTORE_CREDENZIALI = Map.newMapKey("IDENTITA_GESTORE_CREDENZIALI");
    public static final MapKey<String> INFORMAZIONI_INTEGRAZIONE = Map.newMapKey("INFORMAZIONI_INTEGRAZIONE");
    public static final MapKey<String> INFORMAZIONI_INTEGRAZIONE_RISPOSTA = Map.newMapKey("INFORMAZIONI_INTEGRAZIONE_RISPOSTA");
		
	public static final MapKey<String> EMESSI_DIAGNOSTICI_ERRORE = Map.newMapKey("EMESSI_DIAGNOSTICI_ERRORE");
	public static final MapKey<String> REQUEST_INFO = Map.newMapKey("REQUEST_INFO");
	public static final MapKey<String> REQUEST_INFO_IN_MEMORY = Map.newMapKey("REQUEST_INFO_IN_MEMORY");
	public static final MapKey<String> EXTENDED_INFO_TRANSAZIONE = Map.newMapKey("EXTENDED_INFO_TRANSAZIONE");
	public static final MapKey<String> CORS_PREFLIGHT_REQUEST_VIA_GATEWAY = Map.newMapKey("CORS_PREFLIGHT_REQUEST_VIA_GATEWAY");
	public static final MapKey<String> CORS_PREFLIGHT_REQUEST_TRASPARENTE = Map.newMapKey("CORS_PREFLIGHT_REQUEST_TRASPARENTE");
	
	public static final MapKey<String> CONSEGNA_MULTIPLA_SINCRONA_CONFIGURAZIONE = Map.newMapKey("CONSEGNA_MULTIPLA_SINCRONA_CONFIGURAZIONE");
	public static final MapKey<String> CONSEGNA_MULTIPLA_SINCRONA = Map.newMapKey("CONSEGNA_MULTIPLA_SINCRONA");
	public static final MapKey<String> CONSEGNA_MULTIPLA_CONNETTORI = Map.newMapKey("CONSEGNA_MULTIPLA_CONNETTORI");
	public static final MapKey<String> CONSEGNA_MULTIPLA_CONNETTORI_BY_ID = Map.newMapKey("CONSEGNA_MULTIPLA_CONNETTORI_BY_ID");
	public static final MapKey<String> CONSEGNA_MULTIPLA_CONNETTORI_BY_SA = Map.newMapKey("CONSEGNA_MULTIPLA_CONNETTORI_BY_SA");
	
	public static final MapKey<String> CONSEGNA_MULTIPLA_NOME_CONNETTORE = Map.newMapKey("CONSEGNA_MULTIPLA_NOME_CONNETTORE");
	public static final MapKey<String> CONSEGNA_MULTIPLA_NOME_CONNETTORE_API = Map.newMapKey("CONSEGNA_MULTIPLA_NOME_CONNETTORE_API");
	
	public static final MapKey<String> LIMITED_STREAM = Map.newMapKey("LIMITED_STREAM");
	
	public static final MapKey<String> ERRORE_VALIDAZIONE_PROTOCOLLO = Map.newMapKey("ERRORE_PROTOCOLLO");
	
	public static final String ERRORE_TRUE = "true";
	
	public static final MapKey<String> RICHIESTA_INOLTRATA_BACKEND = Map.newMapKey("RICHIESTA_INOLTRATA_BACKEND");
	public static final String RICHIESTA_INOLTRATA_BACKEND_VALORE = "true";
	
	public static final List<MapKey<String>> CONTEXT_OBJECT = new ArrayList<MapKey<String>>();
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
		CONTEXT_OBJECT.add(Costanti.ERRORE_NEGOZIAZIONE_TOKEN);
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
    
	public static final MapKey<String> CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION = Map.newMapKey("CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION");
	public static final MapKey<String> CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION = Map.newMapKey("CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION");
	
	public static final String SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED = "undefined";
	public static final String SESSION_ATTRIBUTE_VALUE_FILTER_UNDEFINED = "undefined";
	
    public static final String PREFIX_TIMEOUT_REQUEST = "Request ";
    public static final String PREFIX_TIMEOUT_RESPONSE = "Response ";
    
    public static final String PREFIX_LIMITED_REQUEST = "Request ";
    public static final String PREFIX_LIMITED_RESPONSE = "Response ";
	
	public static final String JMX_NAME_DATASOURCE_PDD = "DatasourceGW";
	public static DataSourceParams getDataSourceParamsPdD(boolean bindJMX,String tipoDB){
		DataSourceParams dsParams = new DataSourceParams();
		dsParams.setBindJmx(bindJMX);
		dsParams.setWrapOriginalMethods(true); // per poter usare anche getConnection e getConnection(String,String)
		dsParams.setDatabaseType(TipiDatabase.toEnumConstant(tipoDB));
		dsParams.setJmxDomain(CostantiJMX.JMX_DOMINIO);
		dsParams.setJmxName(Costanti.JMX_NAME_DATASOURCE_PDD);
		return dsParams;
	}
	
	public static final String WEB_NEW_LINE = "<br/>";
	
	public static final String MAPPING_EROGAZIONE_PA_NOME_DEFAULT = "__qualsiasi__";
	public static final String MAPPING_FRUIZIONE_PD_NOME_DEFAULT = "__qualsiasi__";
	public static final String MAPPING_DESCRIZIONE_DEFAULT = "Predefinito";
	public static final String MAPPING_EROGAZIONE_PA_DESCRIZIONE_DEFAULT = MAPPING_DESCRIZIONE_DEFAULT;
	public static final String MAPPING_FRUIZIONE_PD_DESCRIZIONE_DEFAULT = MAPPING_DESCRIZIONE_DEFAULT;
	
	
    /** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO = "OneWay_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO = "Sincrono_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_ASINCRONO_SIMMETRICO_INVOCAZIONE_SERVIZIO = "AsincronoSimmetrico_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA = "AsincronoSimmetrico_ConsegnaRisposta";
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_ASINCRONO_ASIMMETRICO_INVOCAZIONE_SERVIZIO = "AsincronoAsimmetrico_InvocazioneServizio";
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_ASINCRONO_ASIMMETRICO_POLLING = "AsincronoAsimmetrico_Polling";
	/** Variabile che indica un tipo di scenario gestito */
	public static final String SCENARIO_CONSEGNA_CONTENUTI_APPLICATIVI = "ConsegnaContenutiApplicativi";
	
	
	public static final String LABEL_PARAMETRO_PROTOCOLLO_DI = "Profilo di Interoperabilità";
	public static final String LABEL_PARAMETRO_PROTOCOLLI_DI = "Profili di Interoperabilità";
	public static final String LABEL_PARAMETRO_PROTOCOLLO = "Profilo Interoperabilità";
	public static final String LABEL_PARAMETRO_PROTOCOLLI = "Profili Interoperabilità";
	public static final String LABEL_PARAMETRO_PROTOCOLLO_DI_HTML_ESCAPE = StringEscapeUtils.escapeHtml(LABEL_PARAMETRO_PROTOCOLLO_DI);
	public static final String LABEL_PARAMETRO_PROTOCOLLI_DI_HTML_ESCAPE = StringEscapeUtils.escapeHtml(LABEL_PARAMETRO_PROTOCOLLI_DI);
	public static final String LABEL_PARAMETRO_PROTOCOLLO_HTML_ESCAPE = StringEscapeUtils.escapeHtml(LABEL_PARAMETRO_PROTOCOLLO);
	public static final String LABEL_PARAMETRO_PROTOCOLLI_HTML_ESCAPE = StringEscapeUtils.escapeHtml(LABEL_PARAMETRO_PROTOCOLLI);
	
	public static final String LABEL_PARAMETRO_PROTOCOLLO_COMPACT = "Profilo";
	public static final String LABEL_PARAMETRO_PROTOCOLLI_COMPACT = "Profili";
	
	public static final String VALUE_PARAMETRO_CUSTOM_IN_SELECT =  "custom";
	
	public static final String LABEL_PARAMETRO_CUSTOM_IN_SELECT = "plugin";
}
