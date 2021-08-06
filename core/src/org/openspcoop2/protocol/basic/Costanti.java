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

package org.openspcoop2.protocol.basic;

import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.archive.ImportMode;

/**	
 * Costanti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Costanti {

	
	/** ERRORE APPLICATIVO */
		
	public static final String ERRORE_INTEGRAZIONE_NAMESPACE = org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TARGET_NAMESPACE; // uso stesso namespace; 
	public static final String ERRORE_INTEGRAZIONE_PREFIX = "integration";
	public final static String ERRORE_INTEGRAZIONE_PREFIX_CODE = "GOVWAY-";
	
	public static final String ERRORE_PROTOCOLLO_NAMESPACE =  "http://govway.org/protocol/fault";
	public static final String ERRORE_PROTOCOLLO_PREFIX = "protocol";
	public final static String ERRORE_PROTOCOLLO_PREFIX_CODE = ERRORE_INTEGRAZIONE_PREFIX_CODE; // e' stato deciso di usare lo stesso dell'integrazione. Viene differenziato per namespace
	
	
	
	private static final Boolean PROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE = true;
	private static Boolean valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE = null;
	public static Boolean isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE() {
		if(valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE==null) {
			initPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE(null);
		}
		return valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE;
	}
	public static synchronized void initPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE(Boolean v) {
		if(valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE==null) {
			if(v!=null){
				valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE = v;
			}
			else {
				valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE = PROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE;
			}
		}
	}
	
	
	
	private static final Boolean PROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE = true;
	private static Boolean valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE = null;
	public static Boolean isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE() {
		if(valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE==null) {
			initPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE(null);
		}
		return valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE;
	}
	public static synchronized void initPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE(Boolean v) {
		if(valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE==null) {
			if(v!=null){
				valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE = v;
			}
			else {
				valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE = PROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CAMEL_CASE_DECODE;
			}
		}
	}
	
	
	
	private static final Boolean PROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM = true;
	private static Boolean valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM = null;
	public static Boolean isPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM() {
		if(valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM==null) {
			initPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM(null);
		}
		return valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM;
	}
	public static synchronized void initPROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM(Boolean v) {
		if(valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM==null) {
			if(v!=null){
				valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM = v;
			}
			else {
				valuePROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM = PROBLEM_RFC7807_ENRICH_TITLE_AS_GOVWAY_TYPE_CUSTOM_CLAIM;
			}
		}
	}
	
	
	
	
	private static final String PROBLEM_RFC7807_GOVWAY_CODE = "govway_status";
	public static String _internal_getPROBLEM_RFC7807_GOVWAY_CODE() {
		return PROBLEM_RFC7807_GOVWAY_CODE;
	}
	private static String valuePROBLEM_RFC7807_GOVWAY_CODE = null;
	public static String getPROBLEM_RFC7807_GOVWAY_CODE() {
		if(valuePROBLEM_RFC7807_GOVWAY_CODE==null) {
			initPROBLEM_RFC7807_GOVWAY_CODE(null);
		}
		return valuePROBLEM_RFC7807_GOVWAY_CODE;
	}
	public static synchronized void initPROBLEM_RFC7807_GOVWAY_CODE(String name) {
		if(valuePROBLEM_RFC7807_GOVWAY_CODE==null) {
			if(name!=null && !"".equals(name)){
				valuePROBLEM_RFC7807_GOVWAY_CODE = name;
			}
			else {
				valuePROBLEM_RFC7807_GOVWAY_CODE = PROBLEM_RFC7807_GOVWAY_CODE;
			}
		}
	}
	
	private static final String PROBLEM_RFC7807_GOVWAY_TYPE = "govway_type";
	public static String _internal_getPROBLEM_RFC7807_GOVWAY_TYPE() {
		return PROBLEM_RFC7807_GOVWAY_TYPE;
	}
	private static String valuePROBLEM_RFC7807_GOVWAY_TYPE = null;
	public static String getPROBLEM_RFC7807_GOVWAY_TYPE() {
		if(valuePROBLEM_RFC7807_GOVWAY_TYPE==null) {
			initPROBLEM_RFC7807_GOVWAY_TYPE(null);
		}
		return valuePROBLEM_RFC7807_GOVWAY_TYPE;
	}
	public static synchronized void initPROBLEM_RFC7807_GOVWAY_TYPE(String name) {
		if(valuePROBLEM_RFC7807_GOVWAY_TYPE==null) {
			if(name!=null && !"".equals(name)){
				valuePROBLEM_RFC7807_GOVWAY_TYPE = name;
			}
			else {
				valuePROBLEM_RFC7807_GOVWAY_TYPE = PROBLEM_RFC7807_GOVWAY_TYPE;
			}
		}
	}
	
	public static final String PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_INTEGRATION = ERRORE_INTEGRAZIONE_PREFIX+":";
	public static final String PROBLEM_RFC7807_GOVWAY_CODE_PREFIX_PROTOCOL = ERRORE_PROTOCOLLO_PREFIX+":";
	
	private static final String PROBLEM_RFC7807_GOVWAY_TRANSACTION_ID = "govway_id";
	public static String _internal_getPROBLEM_RFC7807_GOVWAY_TRANSACTION_ID() {
		return PROBLEM_RFC7807_GOVWAY_TRANSACTION_ID;
	}
	private static String valuePROBLEM_RFC7807_GOVWAY_TRANSACTION_ID = null;
	public static String getPROBLEM_RFC7807_GOVWAY_TRANSACTION_ID() {
		if(valuePROBLEM_RFC7807_GOVWAY_TRANSACTION_ID==null) {
			initPROBLEM_RFC7807_GOVWAY_TRANSACTION_ID(null);
		}
		return valuePROBLEM_RFC7807_GOVWAY_TRANSACTION_ID;
	}
	public static synchronized void initPROBLEM_RFC7807_GOVWAY_TRANSACTION_ID(String name) {
		if(valuePROBLEM_RFC7807_GOVWAY_TRANSACTION_ID==null) {
			if(name!=null && !"".equals(name)){
				valuePROBLEM_RFC7807_GOVWAY_TRANSACTION_ID = name;
			}
			else {
				valuePROBLEM_RFC7807_GOVWAY_TRANSACTION_ID = PROBLEM_RFC7807_GOVWAY_TRANSACTION_ID;
			}
		}
	}
	
	
	
	
	private static final String HTTP_HEADER_GOVWAY_ERROR_STATUS = "GovWay-Transaction-ErrorStatus";
	public static String _internal_getHTTP_HEADER_GOVWAY_ERROR_STATUS() {
		return HTTP_HEADER_GOVWAY_ERROR_STATUS;
	}
	private static String valueHTTP_HEADER_GOVWAY_ERROR_STATUS = null;
	public static String getHTTP_HEADER_GOVWAY_ERROR_STATUS() {
		if(valueHTTP_HEADER_GOVWAY_ERROR_STATUS==null) {
			initHTTP_HEADER_GOVWAY_ERROR_STATUS(null);
		}
		return valueHTTP_HEADER_GOVWAY_ERROR_STATUS;
	}
	public static synchronized void initHTTP_HEADER_GOVWAY_ERROR_STATUS(String name) {
		if(valueHTTP_HEADER_GOVWAY_ERROR_STATUS==null) {
			if(name!=null && !"".equals(name)){
				valueHTTP_HEADER_GOVWAY_ERROR_STATUS = name;
			}
			else {
				valueHTTP_HEADER_GOVWAY_ERROR_STATUS = HTTP_HEADER_GOVWAY_ERROR_STATUS;
			}
		}
	}
	
	
	
	
	private static final String HTTP_HEADER_GOVWAY_ERROR_TYPE = "GovWay-Transaction-ErrorType";
	public static String _internal_getHTTP_HEADER_GOVWAY_ERROR_TYPE() {
		return HTTP_HEADER_GOVWAY_ERROR_TYPE;
	}
	private static String valueHTTP_HEADER_GOVWAY_ERROR_TYPE = null;
	public static String getHTTP_HEADER_GOVWAY_ERROR_TYPE() {
		if(valueHTTP_HEADER_GOVWAY_ERROR_TYPE==null) {
			initHTTP_HEADER_GOVWAY_ERROR_TYPE(null);
		}
		return valueHTTP_HEADER_GOVWAY_ERROR_TYPE;
	}
	public static synchronized void initHTTP_HEADER_GOVWAY_ERROR_TYPE(String name) {
		if(valueHTTP_HEADER_GOVWAY_ERROR_TYPE==null) {
			if(name!=null && !"".equals(name)){
				valueHTTP_HEADER_GOVWAY_ERROR_TYPE = name;
			}
			else {
				valueHTTP_HEADER_GOVWAY_ERROR_TYPE = HTTP_HEADER_GOVWAY_ERROR_TYPE;
			}
		}
	}
	
	
	
	
	private static final String HTTP_HEADER_GOVWAY_ERROR_CODE = "GovWay-Transaction-ErrorCode";
	public static String _internal_getHTTP_HEADER_GOVWAY_ERROR_CODE() {
		return HTTP_HEADER_GOVWAY_ERROR_CODE;
	}
	private static String valueHTTP_HEADER_GOVWAY_ERROR_CODE = null;
	public static String getHTTP_HEADER_GOVWAY_ERROR_CODE() {
		if(valueHTTP_HEADER_GOVWAY_ERROR_CODE==null) {
			initHTTP_HEADER_GOVWAY_ERROR_CODE(null);
		}
		return valueHTTP_HEADER_GOVWAY_ERROR_CODE;
	}
	public static synchronized void initHTTP_HEADER_GOVWAY_ERROR_CODE(String name) {
		if(valueHTTP_HEADER_GOVWAY_ERROR_CODE==null) {
			if(name!=null && !"".equals(name)){
				valueHTTP_HEADER_GOVWAY_ERROR_CODE = name;
			}
			else {
				valueHTTP_HEADER_GOVWAY_ERROR_CODE = HTTP_HEADER_GOVWAY_ERROR_CODE;
			}
		}
	}
	
	
	
	
	public static boolean TRANSACTION_ERROR_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE = false;
	
	public static boolean TRANSACTION_ERROR_SOAP_GENERATE_HTTP_HEADER_GOVWAY_CODE = false;
	
	private static final String TRANSACTION_ERROR_SOAP_HTTP_CODE_SEPARATOR = "-";
	private static final boolean TRANSACTION_ERROR_SOAP_HTTP_CODE_USE = false;
	public static final String getTransactionSoapFaultCode(int govwayReturnCode, String govwayErrorType) {
		if(TRANSACTION_ERROR_SOAP_HTTP_CODE_USE) {
			return govwayReturnCode+Costanti.TRANSACTION_ERROR_SOAP_HTTP_CODE_SEPARATOR+govwayErrorType;
		}
		else {
			return govwayErrorType;
		}
	}
	
	
	public static boolean TRANSACTION_ERROR_STATUS_ABILITATO = false;
	
	public static boolean TRANSACTION_ERROR_INSTANCE_ID_ABILITATO = false;
	
	public static boolean TRANSACTION_FORCE_SPECIFIC_ERROR_DETAILS = false;
	
	public static boolean TRANSACTION_ERROR_SOAP_FAULT_ADD_FAULT_DETAILS_WITH_PROBLEM_RFC7807 = false;
	

	
	
	
	
    /** Ora di creazione di un tracciamento. Il token 'locale' indica il tempo locale
    non sincronizzato da sistema che lo imposta*/
    public static final String TIPO_TEMPO_LOCALE = "Locale";
    /** Ora di creazione di un tracciamento. Il token 'sincronizzato' indica il tempo sincronizzato di rete*/
    public static final String TIPO_TEMPO_SINCRONIZZATO = "Sincronizzato";

    /** Profilo di Trasmissione, attributo inoltro : Al piu' una volta. */
    public static final String PROFILO_TRASMISSIONE_SENZA_DUPLICATI = "ALPIUUNAVOLTA";
    /** Profilo di Trasmissione, attributo inoltro : Piu' una volta. */
    public static final String PROFILO_TRASMISSIONE_CON_DUPLICATI = "PIUDIUNAVOLTA";

	/** Logger */
    public static final String LOGANALIZER_DRIVER_DB_LOGGER = "DRIVER_DB_LOGANALIZER";
    
    /** Definisce un tipo di EccezioneApplicativa */
    public static final String ECCEZIONE_PROTOCOLLO = "EccezioneProtocollo";
    /** Definisce un tipo di EccezioneApplicativa */
    public static final String ECCEZIONE_INTEGRAZIONE = "EccezioneIntegrazione";
       
	/** Archive mode */
	public static final ArchiveMode OPENSPCOOP_ARCHIVE_MODE = new ArchiveMode("govlet");
	public static final ImportMode OPENSPCOOP_IMPORT_ARCHIVE_MODE = new ImportMode(OPENSPCOOP_ARCHIVE_MODE);
	public static final ExportMode OPENSPCOOP_EXPORT_ARCHIVE_MODE = new ExportMode(OPENSPCOOP_ARCHIVE_MODE,true);
	/** Archive mode type */
	public static final ArchiveModeType OPENSPCOOP_ARCHIVE_MODE_TYPE = new ArchiveModeType("govlet");
	/** Archive extension */
	public static final String OPENSPCOOP_ARCHIVE_EXT = "zip";
		
	/**
	 *  Archive extension 
	 *  NOTA: i nomi dei file non sono significativi. L'export li 'normalizzera' (es. viene eliminato lo '/')
	 *  	  il vero nome è dentro la definizione xml.
	 *
	 * openspcoop
	 *      | - archive.info
	 *      |
	 *      | - configurazione
	 *		|		  |
	 *		|		  | - config.xml
	 *      |
	 *      | - porteDominio
	 *		|		  |
	 *		|		  | - pdd1.xml
	 *		|		  |    ...
	 *      |
	 *      | - ruoli
	 *		|		  |
	 *		|		  | - ruolo1.xml
	 *		|		  |    ...
	 *      |    
	 *      | - soggetti
	 *		|		|
	 *		|		| - tipo_nome
	 *		|		|		|
	 *		|		|		| - registroServizi.xml
	 *		|		|		| - config.xml
	 *		|		|		|
	 *		|		|		| - serviziApplicativi
	 *		|		|		|		|
	 *		|		|		|		| - sa1.xml
	 *		|		|		|		|	...
	 *		|		|		|		
	 *		|		|		| - porteDelegate
	 *		|		|		|		|
	 *		|		|		|		| - pd1.xml
	 *		|		|		|		|   ...
	 *		|		|		|		
	 *		|		|		| - porteApplicative
	 *		|		|		|		|
	 *		|		|		|		| - pa1.xml
	 *		|		|		|		|   ...	
	 *		|		|		|		 
	 *		|		|		| - accordiServizioParteComune
	 *		|		|		|		|
	 *		|		|		|		| - nome[_versione]
	 *		|		|		|		|		|
	 *		|		|		|		|		| - aspc1.xml
	 *		|		|		|		|		| - allegati
	 *		|		|		|		|		|       | 
	 *		|		|		|		|		|       | - doc.txt
	 *		|		|		|		|		|   ...
	 *		|		|		|		|
	 *		|		|		|		| - ...		
	 *		|		|		|		  
	 *		|		|		| - accordiServizioParteSpecifica
	 *		|		|		|		|
	 *		|		|		|		| - nome[_versione]
	 *		|		|		|		|		|
	 *		|		|		|		|		| - asps1.xml
	 *		|		|		|		|		| - allegati
	 *		|		|		|		|		|       | 
	 *		|		|		|		|		|       | - doc.txt
	 *		|		|		|		|		| - fruitori
	 *		|		|		|		|		|       | 
	 *		|		|		|		|		|       | - tipo_nome.xml
	 *		|		|		|		|		|   ...
	 *		|		|		|		|
	 *		|		|		|		| - ...			
	 *		|		|		|		  
	 *		|		|		| - accordiCooperazione
	 *		|		|		|		|
	 *		|		|		|		| - nome[_versione]
	 *		|		|		|		|		|
	 *		|		|		|		|		| - ac1.xml
	 *		|		|		|		|		| - allegati
	 *		|		|		|		|		|       | 
	 *		|		|		|		|		|       | - doc.txt
	 *		|		|		|		|		|   ...
	 *		|		|		|		|
	 *		|		|		|		| - ...		
	 *		|		|		|		  
	 *		|		|		|
	 *		|		|		| - accordiServizioComposto
	 *		|		|		|		|
	 *		|		|		|		| - nome[_versione]
	 *		|		|		|		|		|
	 *		|		|		|		|		| - asc1.xml
	 *		|		|		|		|		| - allegati
	 *		|		|		|		|		|       | 
	 *		|		|		|		|		|       | - doc.txt
	 *		|		|		|		|		|   ...
	 *		|		|		|		|
	 *		|		|		|		| - ...		
	 *		|		|		|		  
	 *		|		|		|		  		  
	 **/
	public static final String OPENSPCOOP2_ARCHIVE_ROOT_DIR = "openspcoop";
	
	public static final String OPENSPCOOP2_ARCHIVE_VERSION_FILE_NAME = "archive.info";
	
	public static final String OPENSPCOOP2_ARCHIVE_INFORMATION_MISSING = "informationMissing.xml";
	
	public static final String OPENSPCOOP2_ARCHIVE_EXTENDED_DIR = "extended";
	public static final String OPENSPCOOP2_ARCHIVE_EXTENDED_FILE_NAME = "extended";
	public static final String OPENSPCOOP2_ARCHIVE_EXTENDED_FILE_EXT = ".bin";
	
	public static final String OPENSPCOOP2_ARCHIVE_CONFIGURAZIONE_DIR = "configurazione";
	public static final String OPENSPCOOP2_ARCHIVE_CONFIGURAZIONE_FILE_NAME = "configurazione.xml";
	
	public static final String OPENSPCOOP2_ARCHIVE_URL_INVOCAZIONE_DIR = "configUrlInvocazione";
	public static final String OPENSPCOOP2_ARCHIVE_URL_INVOCAZIONE_CONFIG_FILE_NAME = "configurazione.xml";
	public static final String OPENSPCOOP2_ARCHIVE_URL_INVOCAZIONE_REGOLE_DIR = "regole";
	
	public static final String OPENSPCOOP2_ARCHIVE_CONTROLLO_TRAFFICO_DIR = "controlloTraffico";
	public static final String OPENSPCOOP2_ARCHIVE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_FILE_NAME = "configurazione.xml";
	public static final String OPENSPCOOP2_ARCHIVE_CONTROLLO_TRAFFICO_CONFIG_POLICY_DIR = "configPolicies";
	public static final String OPENSPCOOP2_ARCHIVE_CONTROLLO_TRAFFICO_ACTIVE_POLICY_DIR = "policies"; // devo utilizzare un nome che in ordine lessicografico venga dopo configPolicies
	
	public static final String OPENSPCOOP2_ARCHIVE_ALLARMI_DIR = "allarmi";
	
	public static final String OPENSPCOOP2_ARCHIVE_TOKEN_POLICIES_DIR = "tokenPolicy";
	public static final String OPENSPCOOP2_ARCHIVE_TOKEN_POLICIES_VALIDATION_DIR = "validation";
	public static final String OPENSPCOOP2_ARCHIVE_TOKEN_POLICIES_RETRIEVE_DIR = "retrieve"; 
	
	public static final String OPENSPCOOP2_ARCHIVE_ATTRIBUTE_AUTHORITY_DIR = "attributeAuthority";
	public static final String OPENSPCOOP2_ARCHIVE_ATTRIBUTE_AUTHORITY_RETRIEVE_DIR = "retrieve";
	
	public static final String OPENSPCOOP2_ARCHIVE_PLUGINS_DIR = "plugins";
	public static final String OPENSPCOOP2_ARCHIVE_PLUGINS_CLASSI_DIR = "classi";
	public static final String OPENSPCOOP2_ARCHIVE_PLUGINS_ARCHIVI_DIR = "archivi"; 
	
	public static final String OPENSPCOOP2_ARCHIVE_PORTE_DOMINIO_DIR = "porteDominio";

	public static final String OPENSPCOOP2_ARCHIVE_GRUPPI_DIR = "gruppi";
	
	public static final String OPENSPCOOP2_ARCHIVE_RUOLI_DIR = "ruoli";
	
	public static final String OPENSPCOOP2_ARCHIVE_SCOPE_DIR = "scope";
	
	public static final String OPENSPCOOP2_ARCHIVE_SOGGETTI_DIR = "soggetti";
	public static final String OPENSPCOOP2_ARCHIVE_SOGGETTI_FILE_NAME_REGISTRO = "_soggettoRegistroServizi.xml"; // aggiungo _ inziale per ordine lessicografico con le proprieta di protocollo
	public static final String OPENSPCOOP2_ARCHIVE_SOGGETTI_FILE_NAME_CONFIG = "_soggettoConfigurazione.xml"; // aggiungo _ inziale per ordine lessicografico con le proprieta di protocollo
	
	public static final String OPENSPCOOP2_ARCHIVE_SERVIZI_APPLICATIVI_DIR = "serviziApplicativi";
	public static final String OPENSPCOOP2_ARCHIVE_PORTE_DELEGATE_DIR = "porteDelegate";
	public static final String OPENSPCOOP2_ARCHIVE_PORTE_APPLICATIVE_DIR = "porteApplicative";
	
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME = "accordo.id";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_ID_FILE_NAME_INTERNAL_SEPARATOR = "###";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_DIR = "accordiServizioParteComune";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_COMUNE_FILE_NAME = "accordo.xml";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_DIR = "accordiServizioParteSpecifica";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_FILE_NAME = "accordo.xml";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_PARTE_SPECIFICA_MAPPING_PA_SUFFIX = "_mappingErogazionePA.id";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_DIR = "accordiServizioComposto";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_SERVIZIO_COMPOSTO_FILE_NAME = "accordo.xml";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_DIR = "accordiCooperazione";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_COOPERAZIONE_FILE_NAME = "accordo.xml";
	public static final String OPENSPCOOP2_ARCHIVE_FRUITORE_DIR = "fruitori";
	public static final String OPENSPCOOP2_ARCHIVE_FRUITORE_SERVIZI_APPLICATIVI_AUTORIZZATI = "serviziApplicativiAutorizzati.csv"; // backward compatibility
	public static final String OPENSPCOOP2_ARCHIVE_FRUITORE_MAPPING_PD_SUFFIX = "_mappingFruizionePD.id";
	
	
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_WSDL = "wsdl";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_INTERFACCIA_DEFINITORIA = "InterfacciaDefinitoria.xsd";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_OPENAPI_3_0_JSON = "InterfacciaOpenAPI.json";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_OPENAPI_3_0_YAML = "InterfacciaOpenAPI.yaml";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SWAGGER_2_0_JSON = "InterfacciaSwagger.json";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SWAGGER_2_0_YAML = "InterfacciaSwagger.yaml";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WADL = "Interfaccia.wadl";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_CONCETTUALE_WSDL = "InterfacciaConcettuale.wsdl";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_EROGATORE_WSDL = "InterfacciaLogicaErogatore.wsdl";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_LOGICO_FRUITORE_WSDL = "InterfacciaLogicaFruitore.wsdl";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_EROGATORE_WSDL = "InterfacciaImplementativaErogatore.wsdl";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_IMPLEMENTATIVO_FRUITORE_WSDL = "InterfacciaImplementativaFruitore.wsdl";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_XSD_SCHEMA_COLLECTION = "XSDSchemaCollection.zip";
	public final static String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_XSD_SCHEMA_COLLECTION_ERROR = "XSDSchemaCollection.buildError.txt";
	
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_CONVERSAZIONI = "specificheConversazioni";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_CONCETTUALE = "ConversazioneConcettuale.xml";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_LOGICA_EROGATORE = "ConversazioneLogicaErogatore.xml";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SPECIFICA_CONVERSIONE_LOGICA_FRUITORE = "ConversazioneLogicaFruitore.xml";
	
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_PREFIX = "attachment_";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_ID = ".id";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_ATTACHMENT_SUFFIX_CONTENT = ".raw"; // ".bin"; // ci vuole un nome che in ordine lessicografico venga dopo .id
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_ALLEGATI = "allegati";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SEMIFORMALI = "specificheSemiformali";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_COORDINAMENTO = "specificheCoordinamento";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_LIVELLI_SERVIZIO = "specificheLivelliServizio";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_SPECIFICHE_SICUREZZA = "specificheSicurezza";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_MAPPING = "mapping";
	public static final String OPENSPCOOP2_ARCHIVE_ACCORDI_DIR_PROTOCOL_PROPERTIES = "proprietaProtocollo";
	
	
}
