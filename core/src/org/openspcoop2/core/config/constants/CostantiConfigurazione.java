/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.constants.TipoPdD;
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
    public static final String NONE = "none";
	
    /** tipo di porta delegata: static */
    public static final PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_STATIC = PortaDelegataAzioneIdentificazione.STATIC;
    /** tipo di porta delegata: header-based */
    public static final PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_HEADER_BASED = PortaDelegataAzioneIdentificazione.HEADER_BASED;
    /** tipo di porta delegata: url-based */
    public static final PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_URL_BASED = PortaDelegataAzioneIdentificazione.URL_BASED;
    /** tipo di porta delegata: content-based */
    public static final PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_CONTENT_BASED = PortaDelegataAzioneIdentificazione.CONTENT_BASED;
    /** tipo di porta delegata: integration-based */
    public static final PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_INPUT_BASED = PortaDelegataAzioneIdentificazione.INPUT_BASED;
    /** tipo di porta delegata: soapAction-based (solo per azione) */
    public static final PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_SOAP_ACTION_BASED = PortaDelegataAzioneIdentificazione.SOAP_ACTION_BASED;
    /** tipo di porta delegata: wsdlBased (solo per azione) */
    public static final PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_WSDL_BASED = PortaDelegataAzioneIdentificazione.INTERFACE_BASED;
    /** tipo di porta delegata: delegatedBy */
    public static final PortaDelegataAzioneIdentificazione PORTA_DELEGATA_AZIONE_DELEGATED_BY = PortaDelegataAzioneIdentificazione.DELEGATED_BY;
    
    /** tipo di porta applicativa: static */
    public static final PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_STATIC = PortaApplicativaAzioneIdentificazione.STATIC;
    /** tipo di porta applicativa: plugin-based */
    public static final PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_PROTOCOL_BASED = PortaApplicativaAzioneIdentificazione.PROTOCOL_BASED;
    /** tipo di porta applicativa: header-based */
    public static final PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_HEADER_BASED = PortaApplicativaAzioneIdentificazione.HEADER_BASED;
    /** tipo di porta applicativa: url-based */
    public static final PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_URL_BASED = PortaApplicativaAzioneIdentificazione.URL_BASED;
    /** tipo di porta applicativa: content-based */
    public static final PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_CONTENT_BASED = PortaApplicativaAzioneIdentificazione.CONTENT_BASED;
    /** tipo di porta applicativa: integration-based */
    public static final PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_INPUT_BASED = PortaApplicativaAzioneIdentificazione.INPUT_BASED;
    /** tipo di porta applicativa: soapAction-based (solo per azione) */
    public static final PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_SOAP_ACTION_BASED = PortaApplicativaAzioneIdentificazione.SOAP_ACTION_BASED;
    /** tipo di porta applicativa: wsdlBased (solo per azione) */
    public static final PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_WSDL_BASED = PortaApplicativaAzioneIdentificazione.INTERFACE_BASED;
    /** tipo di porta applicativa: delegatedBy */
    public static final PortaApplicativaAzioneIdentificazione PORTA_APPLICATIVA_AZIONE_DELEGATED_BY = PortaApplicativaAzioneIdentificazione.DELEGATED_BY;
    
    /** tipo di porta delegata: static */
    public static final CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_DISABILITATO = CorrelazioneApplicativaRichiestaIdentificazione.DISABILITATO;
    /** tipo di porta delegata: url-based */
    public static final CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_CONTENT_BASED = CorrelazioneApplicativaRichiestaIdentificazione.CONTENT_BASED;
    /** tipo di porta delegata: content-based */
    public static final CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_INPUT_BASED = CorrelazioneApplicativaRichiestaIdentificazione.INPUT_BASED;
    /** tipo di porta delegata: integration-based */
    public static final CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_URL_BASED = CorrelazioneApplicativaRichiestaIdentificazione.URL_BASED;
    /** tipo di porta delegata: integration-based */
    public static final CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_HEADER_BASED = CorrelazioneApplicativaRichiestaIdentificazione.HEADER_BASED;
    /** tipo di porta delegata: template */
    public static final CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_TEMPLATE = CorrelazioneApplicativaRichiestaIdentificazione.TEMPLATE;
    /** tipo di porta delegata: freemarker-template */
    public static final CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_FREEMARKER_TEMPLATE = CorrelazioneApplicativaRichiestaIdentificazione.FREEMARKER_TEMPLATE;
    /** tipo di porta delegata: velocity-template */
    public static final CorrelazioneApplicativaRichiestaIdentificazione CORRELAZIONE_APPLICATIVA_RICHIESTA_VELOCITY_TEMPLATE = CorrelazioneApplicativaRichiestaIdentificazione.VELOCITY_TEMPLATE;

    /** tipo di porta delegata: static */
    public static final CorrelazioneApplicativaRispostaIdentificazione CORRELAZIONE_APPLICATIVA_RISPOSTA_DISABILITATO = CorrelazioneApplicativaRispostaIdentificazione.DISABILITATO;
    /** tipo di porta delegata: url-based */
    public static final CorrelazioneApplicativaRispostaIdentificazione CORRELAZIONE_APPLICATIVA_RISPOSTA_CONTENT_BASED = CorrelazioneApplicativaRispostaIdentificazione.CONTENT_BASED;
    /** tipo di porta delegata: content-based */
    public static final CorrelazioneApplicativaRispostaIdentificazione CORRELAZIONE_APPLICATIVA_RISPOSTA_INPUT_BASED = CorrelazioneApplicativaRispostaIdentificazione.INPUT_BASED;
    /** tipo di porta delegata: content-based */
    public static final CorrelazioneApplicativaRispostaIdentificazione CORRELAZIONE_APPLICATIVA_RISPOSTA_HEADER_BASED = CorrelazioneApplicativaRispostaIdentificazione.HEADER_BASED;
    /** tipo di porta delegata: template */
    public static final CorrelazioneApplicativaRispostaIdentificazione CORRELAZIONE_APPLICATIVA_RISPOSTA_TEMPLATE = CorrelazioneApplicativaRispostaIdentificazione.TEMPLATE;
    /** tipo di porta delegata: freemarker-template */
    public static final CorrelazioneApplicativaRispostaIdentificazione CORRELAZIONE_APPLICATIVA_RISPOSTA_FREEMARKER_TEMPLATE = CorrelazioneApplicativaRispostaIdentificazione.FREEMARKER_TEMPLATE;
    /** tipo di porta delegata: velocity-template */
    public static final CorrelazioneApplicativaRispostaIdentificazione CORRELAZIONE_APPLICATIVA_RISPOSTA_VELOCITY_TEMPLATE = CorrelazioneApplicativaRispostaIdentificazione.VELOCITY_TEMPLATE;

    /** tipo di porta autenticazione: none */
    public static final InvocazioneServizioTipoAutenticazione INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE = InvocazioneServizioTipoAutenticazione.NONE;
    /** tipo di porta autenticazione: basic */
    public static final InvocazioneServizioTipoAutenticazione INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC = InvocazioneServizioTipoAutenticazione.BASIC;
    
    /** tipo di porta autenticazione: basic */
    public static final CredenzialeTipo CREDENZIALE_BASIC = CredenzialeTipo.BASIC;
    /** tipo di porta autenticazione: apikey */
    public static final CredenzialeTipo CREDENZIALE_APIKEY = CredenzialeTipo.APIKEY;
    /** tipo di porta autenticazione: ssl */
    public static final CredenzialeTipo CREDENZIALE_SSL = CredenzialeTipo.SSL;
    /** tipo di porta autenticazione: principal */
    public static final CredenzialeTipo CREDENZIALE_PRINCIPAL = CredenzialeTipo.PRINCIPAL;
    /** tipo di porta autenticazione: token */
    public static final CredenzialeTipo CREDENZIALE_TOKEN = CredenzialeTipo.TOKEN;
    
	public static final String LABEL_CREDENZIALE_BASIC = "http-basic";
	public static final String LABEL_CREDENZIALE_APIKEY = "api-key";
	public static final String LABEL_CREDENZIALE_SSL = "https";
	public static final String LABEL_CREDENZIALE_PRINCIPAL = "principal";
	public static final String LABEL_CREDENZIALE_TOKEN = "token";
	public static final String LABEL_CREDENZIALE_DISABILITATO = "disabilitato";
	
	public static final String LABEL_AUTENTICAZIONE_PRINCIPAL_CONTAINER = ModalitaIdentificazione.CONTAINER_BASED.getLabel();
    public static final String LABEL_AUTENTICAZIONE_PRINCIPAL_HEADER = ModalitaIdentificazione.HEADER_BASED.getLabel();
    public static final String LABEL_AUTENTICAZIONE_PRINCIPAL_FORM = ModalitaIdentificazione.FORM_BASED.getLabel();
    public static final String LABEL_AUTENTICAZIONE_PRINCIPAL_URL = ModalitaIdentificazione.URL_BASED.getLabel();
    public static final String LABEL_AUTENTICAZIONE_PRINCIPAL_CONTENT = ModalitaIdentificazione.CONTENT_BASED.getLabel();
    public static final String LABEL_AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP = ModalitaIdentificazione.INDIRIZZO_IP_BASED.getLabel();
    public static final String LABEL_AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP_X_FORWARDED_FOR = ModalitaIdentificazione.X_FORWARD_FOR_BASED.getLabel();
    public static final String LABEL_AUTENTICAZIONE_PRINCIPAL_TOKEN = ModalitaIdentificazione.TOKEN.getLabel();
    
    /** tipo di porta autenticazione */
    public static final String AUTENTICAZIONE_NONE = "none";
    public static final String AUTENTICAZIONE_BASIC = "basic";
    public static final String AUTENTICAZIONE_APIKEY = "apikey";
    public static final String AUTENTICAZIONE_SSL = "ssl";
    public static final String AUTENTICAZIONE_PRINCIPAL = "principal";
    public static final String AUTENTICAZIONE_TOKEN = "token";
//    public static final String AUTENTICAZIONE_PRINCIPAL_SSL_BASIC = "principalOrSslOrBasic";
//    public static final String AUTENTICAZIONE_PRINCIPAL_BASIC = "principalOrBasic";
//    public static final String AUTENTICAZIONE_PRINCIPAL_SSL = "principalOrSsl";
//    public static final String AUTENTICAZIONE_SSL_BASIC = "sslOrBasic";
    
    public static final String AUTENTICAZIONE_PRINCIPAL_CONTAINER = "container";
    public static final String AUTENTICAZIONE_PRINCIPAL_HEADER = "header-based";
    public static final String AUTENTICAZIONE_PRINCIPAL_FORM = "form-based";
    public static final String AUTENTICAZIONE_PRINCIPAL_URL = "url-based";
    public static final String AUTENTICAZIONE_PRINCIPAL_CONTENT = "content-based";
    public static final String AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP = "indirizzo-ip";
    public static final String AUTENTICAZIONE_PRINCIPAL_INDIRIZZO_IP_X_FORWARDED_FOR = "x-forwarded-for";
    public static final String AUTENTICAZIONE_PRINCIPAL_TOKEN = "token";
    
    /** tipo di porta autorizzazione */
    public static final String AUTORIZZAZIONE_NONE = "none";
    
    public static final String AUTORIZZAZIONE_AUTHENTICATED = "authenticated";
    
    public static final String AUTORIZZAZIONE_ROLES = "roles";
    public static final String AUTORIZZAZIONE_INTERNAL_ROLES = "internalRoles";
    public static final String AUTORIZZAZIONE_EXTERNAL_ROLES = "externalRoles";
    public static final String AUTORIZZAZIONE_AUTHENTICATED_OR_ROLES = "authenticatedOrRoles"; 
    public static final String AUTORIZZAZIONE_AUTHENTICATED_OR_INTERNAL_ROLES = "authenticatedOrInternalRoles";
    public static final String AUTORIZZAZIONE_AUTHENTICATED_OR_EXTERNAL_ROLES = "authenticatedOrExternalRoles";
    
    public static final String AUTORIZZAZIONE_XACML_POLICY = "xacmlPolicy";
    public static final String AUTORIZZAZIONE_INTERNAL_XACML_POLICY = "internalXacmlPolicy";
    public static final String AUTORIZZAZIONE_EXTERNAL_XACML_POLICY = "externalXacmlPolicy";

    public static final String AUTORIZZAZIONE_TOKEN = "token";
    
    /** tipo di configurazione: xml */
    public static final String CONFIGURAZIONE_XML = "xml";
    /** tipo di configurazione: db */
    public static final String CONFIGURAZIONE_DB = "db";
    /** tipo di configurazione: gui */
    public static final String CONFIGURAZIONE_GUI = "gui";
    
    /** tipo di ora utilizzata dalla porta di dominio: LOCALE */
    public static final String TEMPO_TIPO_LOCALE = "locale";
    /** tipo di ora utilizzata dalla porta di dominio: Sincronizzato */
    public static final String TEMPO_TIPO_SINCRONIZZATO = "sincronizzato";
    
    /** tipo di repository: fs */
    public static final String REPOSITORY_FILE_SYSTEM = "fs";
    /** tipo di repository: db */
    public static final String REPOSITORY_DB = "db";
    
    /** tipo di comunicazione infrastrutturale: jms */
    public static final String COMUNICAZIONE_INFRASTRUTTURALE_JMS = "jms";
    /** tipo di comunicazione infrastrutturale: db */
    public static final String COMUNICAZIONE_INFRASTRUTTURALE_DB = "db";

    /** tipo di header di integrazione: trasporto */
    public static final String HEADER_INTEGRAZIONE_TRASPORTO = "trasporto";
        /** tipo di header di integrazione: urlBased */
    public static final String HEADER_INTEGRAZIONE_URL_BASED = "urlBased";
    /** tipo di header di integrazione: soap */
    public static final String HEADER_INTEGRAZIONE_SOAP = "soap";
      
    /** livello di validazione buste: normale */
    public static final ValidazioneBusteTipoControllo VALIDAZIONE_PROTOCOL_LIVELLO_NORMALE = ValidazioneBusteTipoControllo.NORMALE;
    /** livello di validazione buste: rigido */
    public static final ValidazioneBusteTipoControllo VALIDAZIONE_PROTOCOL_LIVELLO_RIGIDO = ValidazioneBusteTipoControllo.RIGIDO;
    
    /** Costante che indica una funzionalita' abilitata */
    public static final StatoFunzionalita ABILITATO = StatoFunzionalita.ABILITATO;
    /** Costante che indica una funzionalita' disabilitata */
    public static final StatoFunzionalita DISABILITATO = StatoFunzionalita.DISABILITATO;
    
    /** Costante che indica una funzionalita' abilitata */
    public static final StatoFunzionalitaConWarning STATO_CON_WARNING_ABILITATO = StatoFunzionalitaConWarning.ABILITATO;
    /** Costante che indica una funzionalita' disabilitata */
    public static final StatoFunzionalitaConWarning STATO_CON_WARNING_DISABILITATO = StatoFunzionalitaConWarning.DISABILITATO;
    /** Costante che indica una funzionalita' warningOnly */
    public static final StatoFunzionalitaConWarning STATO_CON_WARNING_WARNING_ONLY = StatoFunzionalitaConWarning.WARNING_ONLY;
    
    /** Costante che indica una funzionalita' abilitata */
    public static final String TRUE = "true";
    /** Costante che indica una funzionalita' disabilitata */
    public static final String FALSE = "false";

     /** Costante che indica una risposta sulla connessione */
    public static final TipoConnessioneRisposte CONNECTION_REPLY = TipoConnessioneRisposte.REPLY;
    /** Costante che indica il servizio gop disabilitato */
    public static final TipoConnessioneRisposte NEW_CONNECTION = TipoConnessioneRisposte.NEW;
  
    /** tipo di errore applicativo: SOAP Fault */
    public static final FaultIntegrazioneTipo ERRORE_APPLICATIVO_SOAP = FaultIntegrazioneTipo.SOAP;
    /** tipo di errore applicativo: XML, errore applicativo */
    public static final FaultIntegrazioneTipo ERRORE_APPLICATIVO_XML = FaultIntegrazioneTipo.XML;

	/** Costante che indica un comportamento: ACCETTA */
	public static final GestioneErroreComportamento GESTIONE_ERRORE_ACCETTA_MSG = GestioneErroreComportamento.ACCETTA;
	/** Costante che indica un comportamento: RISPEDISCI */
	public static final GestioneErroreComportamento GESTIONE_ERRORE_RISPEDISCI_MSG = GestioneErroreComportamento.RISPEDISCI;
	
	/** Costante che indica un comportamento: ACCETTA */
	public static final CorrelazioneApplicativaGestioneIdentificazioneFallita ACCETTA = CorrelazioneApplicativaGestioneIdentificazioneFallita.ACCETTA;
	/** Costante che indica un comportamento: BLOCCA */
	public static final CorrelazioneApplicativaGestioneIdentificazioneFallita BLOCCA = CorrelazioneApplicativaGestioneIdentificazioneFallita.BLOCCA;

	/** Costante che indica una funzionalita' di gestione del oneway stateful */
    public static final String ONEWAY_STATEFUL_1_0 = "1.0";
    /** Costante che indica una funzionalita' di gestione del oneway stateful */
    public static final String ONEWAY_STATEFUL_1_1 = "1.1";
	
	 /** Tipi di registro. */
    public static final RegistroTipo REGISTRO_XML = RegistroTipo.XML;
    public static final RegistroTipo REGISTRO_DB = RegistroTipo.DB;
    
    /** Algoritmo utilizzato nella cache del registro dei servizi e della configurazione */
    public static final AlgoritmoCache CACHE_LRU = AlgoritmoCache.LRU;
    public static final AlgoritmoCache CACHE_MRU = AlgoritmoCache.MRU;
	
    /** Nome di una eventuale cache per la configurazione */
    public static final String CACHE_CONFIGURAZIONE_PDD = "configurazionePdD";
    
    /** Tipo di AcknowledgeMode: AUTO_ACKNOWLEDGE */
    public static final String AUTO_ACKNOWLEDGE = "AUTO_ACKNOWLEDGE";
    /** Tipo di AcknowledgeMode: CLIENT_ACKNOWLEDGE */
    public static final String CLIENT_ACKNOWLEDGE = "CLIENT_ACKNOWLEDGE";
    /** Tipo di AcknowledgeMode: DUPS_OK_ACKNOWLEDGE */
    public static final String DUPS_OK_ACKNOWLEDGE = "DUPS_OK_ACKNOWLEDGE";
   
    /** Validazione WSDL: XSD */
    public static final ValidazioneContenutiApplicativiTipo VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD = ValidazioneContenutiApplicativiTipo.XSD;
    /** Validazione WSDL: WSDL */
    public static final ValidazioneContenutiApplicativiTipo VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE = ValidazioneContenutiApplicativiTipo.INTERFACE;
    /** Validazione WSDL: Accordo */
    public static final ValidazioneContenutiApplicativiTipo VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP = ValidazioneContenutiApplicativiTipo.OPENSPCOOP;
    
    public static final String VALIDAZIONE_CONTENUTI_APPLICATIVI_PRINT_SEPARATOR = "-";
    public static final String VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_CON_MTOM = "mtom/xop";
    public static final String VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_IN_WARNING_MODE = "warningOnly";
    public static final String VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_BODY_NON_PRESENTE = " (contenuto non presente nella risposta)";
    public static final String VALIDAZIONE_CONTENUTI_APPLICATIVI_VALIDAZIONE_SOAP_FAULT_PRESENTE = " (la risposta contiene un Fault)";
	
    /** tipo di repository */
    public static final String REPOSITORY_BUSTE_DEFAULT = "default";
    /** tipo di repository ottimizzato che utilizza operazioni su byte */
    public static final String REPOSITORY_BUSTE_AUTO_BYTEWISE = "auto-bytewise";
    /** tipo di repository ottimizzato che utilizza operazioni su byte */
    public static final String REPOSITORY_BUSTE_BYTEWISE = "bytewise";
    /** tipo di repository ottimizzato che utilizza operazioni su byte */
    public static final String REPOSITORY_BUSTE_BIT_OR_AND_FUNCTION = "bitOrAndFunction";
    /** tipo di repository ottimizzato che utilizza operazioni su byte per Oracle */
    public static final String REPOSITORY_BUSTE_BYTEWISE_ORACLE = TipiDatabase.ORACLE.toString();

    
	/** Logger */
	public static final String DRIVER_DB_LOGGER = "DRIVER_DB_CONFIGURAZIONE";
	
	/** tipo di server: j2ee */
    public static final String SERVER_J2EE = "j2ee";
    /** tipo di server: web */
    public static final String SERVER_WEB = "web";
    
    /** Token */
	public static final String GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_VALIDATION = "gestionePolicyToken";
	public static final String GENERIC_PROPERTIES_TOKEN_TIPOLOGIA_RETRIEVE = "retrievePolicyToken";
	public static final String GENERIC_PROPERTIES_ATTRIBUTE_AUTHORITY = "attributeAuthority";
	public static final String GENERIC_PROPERTIES_TIPOLOGIA_INSTALLER = "installer";
    
    /** Routing */
    public static final String ROUTE_REGISTRO = "registro";
    public static final String ROUTE_GATEWAY = "gateway";
    
    /** Filtro duplicati OpenSPCoop */
    public static final String FILTRO_DUPLICATI_OPENSPCOOP = "openspcoop";
    
	public static final String ROOT_LOCAL_NAME_CONFIG = "openspcoop2";
	public static final String LOCAL_NAME_SOGGETTO = "soggetto";
	public static final String LOCAL_NAME_PORTA_DELEGATA = "porta-delegata";
	public static final String LOCAL_NAME_PORTA_APPLICATIVA = "porta-applicativa";
	public static final String LOCAL_NAME_SERVIZIO_APPLICATIVO = "servizio-applicativo";
	public static final String TARGET_NAMESPACE = "http://www.openspcoop2.org/core/config";
	
	public static final String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_PREFIX = "http://localhost:8080/govway/";
	public static final String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_DEFAULT_RULE_NAME = "-default-";
	public static final String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_DEFAULT_RULE_DESCRIPTION = "Default rule";
	
	private static final String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_INVOCAZIONE_PROTOCOL_TEMPLATE = "PROTOCOLLO";
	private static final String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_PD = DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_INVOCAZIONE_PROTOCOL_TEMPLATE+"out/";
	public static String getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePd(String context) {
		String c = "";
		if(!"".equals(context)) {
			c = context +"/";
		}
		return DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_URL_INVOCAZIONE_PD.replace(DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_INVOCAZIONE_PROTOCOL_TEMPLATE, c);
	}
	private static final String DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA = DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_INVOCAZIONE_PROTOCOL_TEMPLATE+"in/";
	public static String getDefaultValueParametroConfigurazioneProtocolloPrefixUrlInvocazionePa(String context) {
		String c = "";
		if(!"".equals(context)) {
			c = context +"/";
		}
		return DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA.replace(DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_INVOCAZIONE_PROTOCOL_TEMPLATE, c);
	}
	
	 /** Servizio Applicativo Tipo */
    public static final String CLIENT = "client";
    public static final String SERVER = "server";
    public static final String CLIENT_OR_SERVER = "clientORserver";

    public static final String CODA_DEFAULT = "DEFAULT";
    public static final String PRIORITA_DEFAULT = "DEFAULT";

    public static final String NOME_CONNETTORE_DEFAULT = "Default";
    
    public static final String REGOLA_PROXY_PASS_CONTESTO_VUOTO = "_____@@EMPTY@@____";
    
    public static final String AUTORIZZAZIONE_CONTENUTO_BUILT_IN = "builtIn";
    
	public static final String CONSEGNA_MULTIPLA = "__multi";
	
	public static final String CONSEGNA_LOAD_BALANCE = "__loadBalance";
	
	public static final String CONSEGNA_CONDIZIONALE = "__conditional";
	
	public static final String CONSEGNA_CON_NOTIFICHE = "__notify";
	
	public static final String CONSEGNA_CUSTOM = "__custom";
	
	
	public static final String ALLARMI_TIPOLOGIA_CONFIGURAZIONE = "configurazione";
	public static final String ALLARMI_TIPOLOGIA_DELEGATA = TipoPdD.DELEGATA.toString(); 
	public static final String ALLARMI_TIPOLOGIA_APPLICATIVA = TipoPdD.APPLICATIVA.toString(); 
	public static final String ALLARMI_TIPOLOGIA_SOLO_ASSOCIATE = "SOLO_ASSOCIATE";
	
	public static final String PARAM_ID_CLUSTER = "aPluginClusterId";
	public static final String CLUSTER_ID_NON_DEFINITO = "-";
	
	public static final String PARAM_POLICY_ID = "aPluginPolicyId";
	public static final String POLICY_ID_NON_DEFINITA = "-";

	public static final String POLICY_DYNAMIC_DISCOVERY_CLAIMS_PARSER_TYPE = "policy.discovery.claimsParser";
	public static final String POLICY_DYNAMIC_DISCOVERY_CLAIMS_PARSER_TYPE_CUSTOM = "CUSTOM";
	public static final String POLICY_DYNAMIC_DISCOVERY_CLAIMS_PARSER_CLASS_NAME = "policy.discovery.claimsParser.className";
	public static final String POLICY_DYNAMIC_DISCOVERY_CLAIMS_PARSER_PLUGIN_TYPE = "policy.discovery.claimsParser.pluginType";
	
	public static final String POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE = "policy.validazioneJWT.claimsParser";
	public static final String POLICY_VALIDAZIONE_CLAIMS_PARSER_TYPE_CUSTOM = "CUSTOM";
	public static final String POLICY_VALIDAZIONE_CLAIMS_PARSER_CLASS_NAME = "policy.validazioneJWT.claimsParser.className";
	public static final String POLICY_VALIDAZIONE_CLAIMS_PARSER_PLUGIN_TYPE = "policy.validazioneJWT.claimsParser.pluginType";
	
	public static final String POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE = "policy.introspection.claimsParser";
	public static final String POLICY_INTROSPECTION_CLAIMS_PARSER_TYPE_CUSTOM = "CUSTOM";
	public static final String POLICY_INTROSPECTION_CLAIMS_PARSER_CLASS_NAME = "policy.introspection.claimsParser.className";
	public static final String POLICY_INTROSPECTION_CLAIMS_PARSER_PLUGIN_TYPE = "policy.introspection.claimsParser.pluginType";
	
	public static final String POLICY_USER_INFO_CLAIMS_PARSER_TYPE = "policy.userInfo.claimsParser";
	public static final String POLICY_USER_INFO_CLAIMS_PARSER_TYPE_CUSTOM = "CUSTOM";
	public static final String POLICY_USER_INFO_CLAIMS_PARSER_CLASS_NAME = "policy.userInfo.claimsParser.className";
	public static final String POLICY_USER_INFO_CLAIMS_PARSER_PLUGIN_TYPE = "policy.userInfo.claimsParser.pluginType";
	
	public static final String POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM = "policy.retrieveToken.claimsParser.custom";
	public static final String POLICY_RETRIEVE_TOKEN_PARSER_TYPE_CUSTOM_CYSTOM = "CUSTOM";
	public static final String POLICY_RETRIEVE_TOKEN_PARSER_CLASS_NAME = "policy.retrieveToken.claimsParser.className";
	public static final String POLICY_RETRIEVE_TOKEN_PARSER_PLUGIN_TYPE = "policy.retrieveToken.claimsParser.pluginType";
	
	public static final String AA_RESPONSE_TYPE = "policy.attributeAuthority.response.type";
	public static final String AA_RESPONSE_TYPE_VALUE_JSON = "json";
	public static final String AA_RESPONSE_TYPE_VALUE_JWS = "jws";
	public static final String AA_RESPONSE_TYPE_VALUE_CUSTOM = "custom";
	
	public static final String AA_RESPONSE_PARSER_CLASS_NAME = "policy.attributeAuthority.claimsParser.className";
	public static final String AA_RESPONSE_PARSER_PLUGIN_TYPE = "policy.attributeAuthority.claimsParser.pluginType";
}
