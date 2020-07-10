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


package org.openspcoop2.core.config.constants;

import org.openspcoop2.core.commons.ModalitaIdentificazione;
import org.openspcoop2.utils.TipiDatabase;

/**
 * Costanti per gli oggetti dao del package org.openspcoop.dao.config
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiConfigurazione {

	/** tipo di scelta none */
    public final static String NONE = "none";
	
    /** tipo di porta delegata: static */
    public final static PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_STATIC = PortaDelegataAzioneIdentificazione.STATIC;
    /** tipo di porta delegata: header-based */
    public final static PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_HEADER_BASED = PortaDelegataAzioneIdentificazione.HEADER_BASED;
    /** tipo di porta delegata: url-based */
    public final static PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_URL_BASED = PortaDelegataAzioneIdentificazione.URL_BASED;
    /** tipo di porta delegata: content-based */
    public final static PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_CONTENT_BASED = PortaDelegataAzioneIdentificazione.CONTENT_BASED;
    /** tipo di porta delegata: integration-based */
    public final static PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_INPUT_BASED = PortaDelegataAzioneIdentificazione.INPUT_BASED;
    /** tipo di porta delegata: soapAction-based (solo per azione) */
    public final static PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_SOAP_ACTION_BASED = PortaDelegataAzioneIdentificazione.SOAP_ACTION_BASED;
    /** tipo di porta delegata: wsdlBased (solo per azione) */
    public final static PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_WSDL_BASED = PortaDelegataAzioneIdentificazione.INTERFACE_BASED;
    /** tipo di porta delegata: delegatedBy */
    public final static PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_DELEGATED_BY = PortaDelegataAzioneIdentificazione.DELEGATED_BY;
    
    /** tipo di porta applicativa: static */
    public final static PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_STATIC = PortaApplicativaAzioneIdentificazione.STATIC;
    /** tipo di porta applicativa: plugin-based */
    public final static PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_PROTOCOL_BASED = PortaApplicativaAzioneIdentificazione.PROTOCOL_BASED;
    /** tipo di porta applicativa: header-based */
    public final static PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_HEADER_BASED = PortaApplicativaAzioneIdentificazione.HEADER_BASED;
    /** tipo di porta applicativa: url-based */
    public final static PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_URL_BASED = PortaApplicativaAzioneIdentificazione.URL_BASED;
    /** tipo di porta applicativa: content-based */
    public final static PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_CONTENT_BASED = PortaApplicativaAzioneIdentificazione.CONTENT_BASED;
    /** tipo di porta applicativa: integration-based */
    public final static PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_INPUT_BASED = PortaApplicativaAzioneIdentificazione.INPUT_BASED;
    /** tipo di porta applicativa: soapAction-based (solo per azione) */
    public final static PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_SOAP_ACTION_BASED = PortaApplicativaAzioneIdentificazione.SOAP_ACTION_BASED;
    /** tipo di porta applicativa: wsdlBased (solo per azione) */
    public final static PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_WSDL_BASED = PortaApplicativaAzioneIdentificazione.INTERFACE_BASED;
    /** tipo di porta applicativa: delegatedBy */
    public final static PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_DELEGATED_BY = PortaApplicativaAzioneIdentificazione.DELEGATED_BY;
    
    /** tipo di porta delegata: static */
    public final static CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_DISABILITATO = CorrelazioneApplicativaRichiestaIdentificazione.DISABILITATO;
    /** tipo di porta delegata: url-based */
    public final static CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED = CorrelazioneApplicativaRichiestaIdentificazione.CONTENT_BASED;
    /** tipo di porta delegata: content-based */
    public final static CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_INPUT_BASED = CorrelazioneApplicativaRichiestaIdentificazione.INPUT_BASED;
    /** tipo di porta delegata: integration-based */
    public final static CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_URL_BASED = CorrelazioneApplicativaRichiestaIdentificazione.URL_BASED;
    /** tipo di porta delegata: integration-based */
    public final static CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_HEADER_BASED = CorrelazioneApplicativaRichiestaIdentificazione.HEADER_BASED;

    /** tipo di porta delegata: static */
    public final static CorrelazioneApplicativaRispostaIdentificazione CORRELAZIONE_APPLICATIVA_RISPOSTA_DISABILITATO = CorrelazioneApplicativaRispostaIdentificazione.DISABILITATO;
    /** tipo di porta delegata: url-based */
    public final static CorrelazioneApplicativaRispostaIdentificazione CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED = CorrelazioneApplicativaRispostaIdentificazione.CONTENT_BASED;
    /** tipo di porta delegata: content-based */
    public final static CorrelazioneApplicativaRispostaIdentificazione CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED = CorrelazioneApplicativaRispostaIdentificazione.INPUT_BASED;
    /** tipo di porta delegata: content-based */
    public final static CorrelazioneApplicativaRispostaIdentificazione CORRELAZIONE_APPLICATIVA_RISPOSTA_HEADER_BASED = CorrelazioneApplicativaRispostaIdentificazione.HEADER_BASED;
   
    /** tipo di porta autenticazione: none */
    public final static InvocazioneServizioTipoAutenticazione INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE = InvocazioneServizioTipoAutenticazione.NONE;
    /** tipo di porta autenticazione: basic */
    public final static InvocazioneServizioTipoAutenticazione INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC = InvocazioneServizioTipoAutenticazione.BASIC;
    
    /** tipo di porta autenticazione: basic */
    public final static CredenzialeTipo CREDENZIALE_BASIC = CredenzialeTipo.BASIC;
    /** tipo di porta autenticazione: apikey */
    public final static CredenzialeTipo CREDENZIALE_APIKEY = CredenzialeTipo.APIKEY;
    /** tipo di porta autenticazione: ssl */
    public final static CredenzialeTipo CREDENZIALE_SSL = CredenzialeTipo.SSL;
    /** tipo di porta autenticazione: principal */
    public final static CredenzialeTipo CREDENZIALE_PRINCIPAL = CredenzialeTipo.PRINCIPAL;
    
	public final static String LABEL_CREDENZIALE_BASIC = "http-basic";
	public final static String LABEL_CREDENZIALE_APIKEY = "api-key";
	public final static String LABEL_CREDENZIALE_SSL = "https";
	public final static String LABEL_CREDENZIALE_PRINCIPAL = "principal";
	public final static String LABEL_CREDENZIALE_DISABILITATO = "disabilitato";
	
	public final static String LABEL_AUTENTICAZIONE_PRINCIPAL_CONTAINER = ModalitaIdentificazione.CONTAINER_BASED.getLabel();
    public final static String LABEL_AUTENTICAZIONE_PRINCIPAL_HEADER = ModalitaIdentificazione.HEADER_BASED.getLabel();
    public final static String LABEL_AUTENTICAZIONE_PRINCIPAL_FORM = ModalitaIdentificazione.FORM_BASED.getLabel();
    public final static String LABEL_AUTENTICAZIONE_PRINCIPAL_URL = ModalitaIdentificazione.URL_BASED.getLabel();
    public final static String LABEL_AUTENTICAZIONE_PRINCIPAL_CONTENT = ModalitaIdentificazione.CONTENT_BASED.getLabel();
    public final static String LABEL_AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP = ModalitaIdentificazione.INDIRIZZO_IP_BASED.getLabel();
    public final static String LABEL_AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP_X_FORWARDED_FOR = ModalitaIdentificazione.X_FORWARD_FOR_BASED.getLabel();
    public final static String LABEL_AUTENTICAZIONE_PRINCIPAL_TOKEN = ModalitaIdentificazione.TOKEN.getLabel();
    
    /** tipo di porta autenticazione */
    public final static String AUTENTICAZIONE_NONE = "none";
    public final static String AUTENTICAZIONE_BASIC = "basic";
    public final static String AUTENTICAZIONE_APIKEY = "apikey";
    public final static String AUTENTICAZIONE_SSL = "ssl";
    public final static String AUTENTICAZIONE_PRINCIPAL = "principal";
//    public final static String AUTENTICAZIONE_PRINCIPAL_SSL_BASIC = "principalOrSslOrBasic";
//    public final static String AUTENTICAZIONE_PRINCIPAL_BASIC = "principalOrBasic";
//    public final static String AUTENTICAZIONE_PRINCIPAL_SSL = "principalOrSsl";
//    public final static String AUTENTICAZIONE_SSL_BASIC = "sslOrBasic";
    
    public final static String AUTENTICAZIONE_PRINCIPAL_CONTAINER = "container";
    public final static String AUTENTICAZIONE_PRINCIPAL_HEADER = "header-based";
    public final static String AUTENTICAZIONE_PRINCIPAL_FORM = "form-based";
    public final static String AUTENTICAZIONE_PRINCIPAL_URL = "url-based";
    public final static String AUTENTICAZIONE_PRINCIPAL_CONTENT = "content-based";
    public final static String AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP = "indirizzo-ip";
    public final static String AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP_X_FORWARDED_FOR = "x-forwarded-for";
    public final static String AUTENTICAZIONE_PRINCIPAL_TOKEN = "token";
    
    /** tipo di porta autorizzazione */
    public final static String AUTORIZZAZIONE_NONE = "none";
    
    public final static String AUTORIZZAZIONE_AUTHENTICATED = "authenticated";
    
    public final static String AUTORIZZAZIONE_ROLES = "roles";
    public final static String AUTORIZZAZIONE_INTERNAL_ROLES = "internalRoles";
    public final static String AUTORIZZAZIONE_EXTERNAL_ROLES = "externalRoles";
    public final static String AUTORIZZAZIONE_AUTHENTICATED_OR_ROLES = "authenticatedOrRoles"; 
    public final static String AUTORIZZAZIONE_AUTHENTICATED_OR_INTERNAL_ROLES = "authenticatedOrInternalRoles";
    public final static String AUTORIZZAZIONE_AUTHENTICATED_OR_EXTERNAL_ROLES = "authenticatedOrExternalRoles";
    
    public final static String AUTORIZZAZIONE_XACML_POLICY = "xacmlPolicy";
    public final static String AUTORIZZAZIONE_INTERNAL_XACML_POLICY = "internalXacmlPolicy";
    public final static String AUTORIZZAZIONE_EXTERNAL_XACML_POLICY = "externalXacmlPolicy";

    public final static String AUTORIZZAZIONE_TOKEN = "token";
    
    /** tipo di configurazione: xml */
    public final static String CONFIGURAZIONE_XML = "xml";
    /** tipo di configurazione: db */
    public final static String CONFIGURAZIONE_DB = "db";
    /** tipo di configurazione: gui */
    public final static String CONFIGURAZIONE_GUI = "gui";
    
    /** tipo di ora utilizzata dalla porta di dominio: LOCALE */
    public final static String TEMPO_TIPO_LOCALE = "locale";
    /** tipo di ora utilizzata dalla porta di dominio: Sincronizzato */
    public final static String TEMPO_TIPO_SINCRONIZZATO = "sincronizzato";
    
    /** tipo di repository: fs */
    public final static String REPOSITORY_FILE_SYSTEM = "fs";
    /** tipo di repository: db */
    public final static String REPOSITORY_DB = "db";
    
    /** tipo di comunicazione infrastrutturale: jms */
    public final static String COMUNICAZIONE_INFRASTRUTTURALE_JMS = "jms";
    /** tipo di comunicazione infrastrutturale: db */
    public final static String COMUNICAZIONE_INFRASTRUTTURALE_DB = "db";

    /** tipo di header di integrazione: trasporto */
    public final static String HEADER_INTEGRAZIONE_TRASPORTO = "trasporto";
        /** tipo di header di integrazione: urlBased */
    public final static String HEADER_INTEGRAZIONE_URL_BASED = "urlBased";
    /** tipo di header di integrazione: soap */
    public final static String HEADER_INTEGRAZIONE_SOAP = "soap";
      
    /** livello di validazione buste: normale */
    public final static ValidazioneBusteTipoControllo VALIDAZIONE_PROTOCOL_LIVELLO_NORMALE = ValidazioneBusteTipoControllo.NORMALE;
    /** livello di validazione buste: rigido */
    public final static ValidazioneBusteTipoControllo VALIDAZIONE_PROTOCOL_LIVELLO_RIGIDO = ValidazioneBusteTipoControllo.RIGIDO;
    
    /** Costante che indica una funzionalita' abilitata */
    public final static StatoFunzionalita ABILITATO = StatoFunzionalita.ABILITATO;
    /** Costante che indica una funzionalita' disabilitata */
    public final static StatoFunzionalita DISABILITATO = StatoFunzionalita.DISABILITATO;
    
    /** Costante che indica una funzionalita' abilitata */
    public final static StatoFunzionalitaConWarning STATO_CON_WARNING_ABILITATO = StatoFunzionalitaConWarning.ABILITATO;
    /** Costante che indica una funzionalita' disabilitata */
    public final static StatoFunzionalitaConWarning STATO_CON_WARNING_DISABILITATO = StatoFunzionalitaConWarning.DISABILITATO;
    /** Costante che indica una funzionalita' warningOnly */
    public final static StatoFunzionalitaConWarning STATO_CON_WARNING_WARNING_ONLY = StatoFunzionalitaConWarning.WARNING_ONLY;
    
    /** Costante che indica una funzionalita' abilitata */
    public final static String TRUE = "true";
    /** Costante che indica una funzionalita' disabilitata */
    public final static String FALSE = "false";

     /** Costante che indica una risposta sulla connessione */
    public final static TipoConnessioneRisposte CONNECTION_REPLY = TipoConnessioneRisposte.REPLY;
    /** Costante che indica il servizio gop disabilitato */
    public final static TipoConnessioneRisposte NEW_CONNECTION = TipoConnessioneRisposte.NEW;
  
    /** tipo di errore applicativo: SOAP Fault */
    public final static FaultIntegrazioneTipo ERRORE_APPLICATIVO_SOAP = FaultIntegrazioneTipo.SOAP;
    /** tipo di errore applicativo: XML, errore applicativo */
    public final static FaultIntegrazioneTipo ERRORE_APPLICATIVO_XML = FaultIntegrazioneTipo.XML;

	/** Costante che indica un comportamento: ACCETTA */
	public static final GestioneErroreComportamento GESTIONE_ERRORE_ACCETTA_MSG = GestioneErroreComportamento.ACCETTA;
	/** Costante che indica un comportamento: RISPEDISCI */
	public static final GestioneErroreComportamento GESTIONE_ERRORE_RISPEDISCI_MSG = GestioneErroreComportamento.RISPEDISCI;
	
	/** Costante che indica un comportamento: ACCETTA */
	public static final CorrelazioneApplicativaGestioneIdentificazioneFallita ACCETTA = CorrelazioneApplicativaGestioneIdentificazioneFallita.ACCETTA;
	/** Costante che indica un comportamento: BLOCCA */
	public static final CorrelazioneApplicativaGestioneIdentificazioneFallita BLOCCA = CorrelazioneApplicativaGestioneIdentificazioneFallita.BLOCCA;

	/** Costante che indica una funzionalita' di gestione del oneway stateful */
    public final static String ONEWAY_STATEFUL_1_0 = "1.0";
    /** Costante che indica una funzionalita' di gestione del oneway stateful */
    public final static String ONEWAY_STATEFUL_1_1 = "1.1";
	
	 /** Tipi di registro. */
    public static final RegistroTipo REGISTRO_UDDI = RegistroTipo.UDDI;
    public static final RegistroTipo REGISTRO_XML = RegistroTipo.XML;
    public static final RegistroTipo REGISTRO_WEB = RegistroTipo.WEB;
    public static final RegistroTipo REGISTRO_DB = RegistroTipo.DB;
    public static final RegistroTipo REGISTRO_WS = RegistroTipo.WS;
    
    /** Algoritmo utilizzato nella cache del registro dei servizi e della configurazione */
    public final static AlgoritmoCache CACHE_LRU = AlgoritmoCache.LRU;
    public final static AlgoritmoCache CACHE_MRU = AlgoritmoCache.MRU;
	
    /** Nome di una eventuale cache per la configurazione */
    public final static String CACHE_CONFIGURAZIONE_PDD = "configurazionePdD";
    
    /** Tipo di AcknowledgeMode: AUTO_ACKNOWLEDGE */
    public final static String AUTO_ACKNOWLEDGE = "AUTO_ACKNOWLEDGE";
    /** Tipo di AcknowledgeMode: CLIENT_ACKNOWLEDGE */
    public final static String CLIENT_ACKNOWLEDGE = "CLIENT_ACKNOWLEDGE";
    /** Tipo di AcknowledgeMode: DUPS_OK_ACKNOWLEDGE */
    public final static String DUPS_OK_ACKNOWLEDGE = "DUPS_OK_ACKNOWLEDGE";
   
    /** Validazione WSDL: XSD */
    public final static ValidazioneContenutiApplicativiTipo VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD = ValidazioneContenutiApplicativiTipo.XSD;
    /** Validazione WSDL: WSDL */
    public final static ValidazioneContenutiApplicativiTipo VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE = ValidazioneContenutiApplicativiTipo.INTERFACE;
    /** Validazione WSDL: Accordo */
    public final static ValidazioneContenutiApplicativiTipo VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP = ValidazioneContenutiApplicativiTipo.OPENSPCOOP;
    
    public final static String VALIDAZIONE_CONTENUTI_APPLICATIVI_PRINT_SEPARATOR = "-";
    public final static String VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_CON_MTOM = "mtom/xop";
    public final static String VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_IN_WARNING_MODE = "warningOnly";
    public final static String VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_BODY_NON_PRESENTE = " (contenuto non presente nella risposta)";
    public final static String VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_FAULT_PRESENTE = " (la risposta contiene un Fault)";
	
    /** tipo di repository */
    public final static String REPOSITORY_BUSTE_DEFAULT = "default";
    /** tipo di repository ottimizzato che utilizza operazioni su byte */
    public final static String REPOSITORY_BUSTE_AUTO_BYTEWISE = "auto-bytewise";
    /** tipo di repository ottimizzato che utilizza operazioni su byte */
    public final static String REPOSITORY_BUSTE_BYTEWISE = "bytewise";
    /** tipo di repository ottimizzato che utilizza operazioni su byte */
    public final static String REPOSITORY_BUSTE_BIT_OR_AND_FUNCTION = "bitOrAndFunction";
    /** tipo di repository ottimizzato che utilizza operazioni su byte per Oracle */
    public final static String REPOSITORY_BUSTE_BYTEWISE_ORACLE = TipiDatabase.ORACLE.toString();

    
	/** Logger */
	public static final String DRIVER_DB_LOGGER = "DRIVER_DB_CONFIGURAZIONE";
	
	/** tipo di server: j2ee */
    public final static String SERVER_J2EE = "j2ee";
    /** tipo di server: web */
    public final static String SERVER_WEB = "web";
    
    /** Token */
	public final static String GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION = "gestionePolicyToken";
	public final static String GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE = "retrievePolicyToken";
	public final static String GENERIC_PROPERTIES_TIPOLOGIA_INSTALLER = "installer";
    
    /** Routing */
    public final static String ROUTE_REGISTRO = "registro";
    public final static String ROUTE_GATEWAY = "gateway";
    
    /** Filtro duplicati OpenSPCoop */
    public final static String FILTRO_DUPLICATI_OPENSPCOOP = "openspcoop";
    
	public final static String ROOT_LOCAL_NAME_CONFIG = "openspcoop2";
	public final static String LOCAL_NAME_SOGGETTO = "soggetto";
	public final static String LOCAL_NAME_PORTA_DELEGATA = "porta-delegata";
	public final static String LOCAL_NAME_PORTA_APPLICATIVA = "porta-applicativa";
	public final static String LOCAL_NAME_SERVIZIO_APPLICATIVO = "servizio-applicativo";
	public final static String TARGET_NAMESPACE = "http://www.openspcoop2.org/core/config";
	
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_PREFIX = "http://localhost:8080/govway/";
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_DEFAULT_RULE_NAME = "-default-";
	public final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_DEFAULT_RULE_DESCRIPTION = "Default rule";
	
	private final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_INVOCAZIONE_PROTOCOL_TEMPLATE = "PROTOCOLLO";
	private final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_PD = DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_INVOCAZIONE_PROTOCOL_TEMPLATE+"out/";
	public static String getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePd(String context) {
		String c = "";
		if(!"".equals(context)) {
			c = context +"/";
		}
		return DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_PD.replace(DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_INVOCAZIONE_PROTOCOL_TEMPLATE, c);
	}
	private final static String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA = DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_INVOCAZIONE_PROTOCOL_TEMPLATE+"in/";
	public static String getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePa(String context) {
		String c = "";
		if(!"".equals(context)) {
			c = context +"/";
		}
		return DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA.replace(DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_INVOCAZIONE_PROTOCOL_TEMPLATE, c);
	}
	
	 /** Servizio Applicativo Tipo */
    public final static String CLIENT = "client";
    public final static String SERVER = "server";
    public final static String CLIENT_OR_SERVER = "clientORserver";
    
}
