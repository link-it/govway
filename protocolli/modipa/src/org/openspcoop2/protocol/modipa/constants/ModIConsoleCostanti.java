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



package org.openspcoop2.protocol.modipa.constants;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.pdd.core.dynamic.DynamicHelperCostanti;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * Classe dove sono fornite le stringhe costanti, definite dalla specifica del protocollo ModI, 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ModIConsoleCostanti {
	
	private ModIConsoleCostanti() {}
	
   
	public static final String MODIPA_TITLE_LABEL = Costanti.MODIPA_PROTOCOL_LABEL;
	@SuppressWarnings("unused")
	private static final String MODIPA_PREFIX_TITLE_LABEL = Costanti.MODIPA_PROTOCOL_LABEL+" - ";
	
	public static final String MODIPA_VALUE_UNDEFINED = ModICostanti.MODIPA_VALUE_UNDEFINED;
    public static final String MODIPA_LABEL_UNDEFINED = "-";
	
    private static final String SUFFIX_LABEL = "__LABEL";
    
	
    // Traccia Ext Info
    
    public static final String MODIPA_API_TRACCIA_EXT_INFO_PROFILO_INTERAZIONE_NON_BLOCCANTE_LABEL = "Interazione Non Bloccante";
    public static final String MODIPA_API_TRACCIA_EXT_INFO_PROFILO_SICUREZZA_MESSAGGIO_LABEL = "Sicurezza Messaggio";
	public static final String MODIPA_API_TRACCIA_EXT_INFO_PROFILO_SICUREZZA_MESSAGGIO_SIGNED_HEADERS_LABEL = "Headers HTTP Firmati";
	public static final String MODIPA_API_TRACCIA_EXT_INFO_PROFILO_SICUREZZA_MESSAGGIO_SIGNED_SOAP_LABEL = "Elementi SOAP Firmati";
	public static final String MODIPA_API_TRACCIA_EXT_INFO_PROFILO_SICUREZZA_MESSAGGIO_AUDIT_LABEL = "Informazioni Audit";
	
    
    
	// Condivise
		
	public static final String MODIPA_SICUREZZA_CHOICE_LABEL = CostantiLabel.MODIPA_SICUREZZA_CHOICE_LABEL;
	public static final String MODIPA_SICUREZZA_CHOICE_MESSAGE_LABEL = CostantiLabel.MODIPA_SICUREZZA_CHOICE_MESSAGE_LABEL;
	public static final String MODIPA_SICUREZZA_CHOICE_TOKEN_PDND_LABEL = CostantiLabel.MODIPA_SICUREZZA_CHOICE_TOKEN_PDND_LABEL;
	public static final String MODIPA_SICUREZZA_CHOICE_TOKEN_OAUTH_LABEL = CostantiLabel.MODIPA_SICUREZZA_CHOICE_TOKEN_OAUTH_LABEL;
	public static final String MODIPA_SICUREZZA_CHOICE_MESSAGE_TOKEN_PDND_LABEL = CostantiLabel.MODIPA_SICUREZZA_CHOICE_MESSAGE_TOKEN_PDND_LABEL;
	public static final String MODIPA_SICUREZZA_CHOICE_MESSAGE_TOKEN_OAUTH_LABEL = CostantiLabel.MODIPA_SICUREZZA_CHOICE_MESSAGE_TOKEN_OAUTH_LABEL;
	
	public static final String MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL = CostantiLabel.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL;
	public static final String MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL;
	public static final String MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_LABEL_MSG_ERROR = ModIConsoleCostanti.MODIPA_APPLICATIVI_LABEL + " - " + MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_LABEL;
	public static final String MODIPA_SICUREZZA_MESSAGGIO_CERTIFICATO_SUBTITLE_ID = "modipaSicurezzaSubTitleId";
	
	public static final String MODIPA_SICUREZZA_MESSAGGIO_MODI_AUTH_SUBTITLE_LABEL = CostantiLabel.MODIPA_SICUREZZA_CHOICE_MESSAGE_LABEL;
	public static final String MODIPA_SICUREZZA_MESSAGGIO_MODI_AUTH_SUBTITLE_ID = "modipaSicurezzaModIAuthSubTitleId";
	
	public static final String MODIPA_SICUREZZA_MESSAGGIO_ID = ModICostanti.MODIPA_SICUREZZA_MESSAGGIO;
	public static final String MODIPA_SICUREZZA_MESSAGGIO_LABEL = "Abilitato";
	
	public static final String MODIPA_SICUREZZA_TOKEN_SUBTITLE_LABEL = CostantiLabel.MODIPA_SICUREZZA_TOKEN_SUBTITLE_LABEL;
	public static final String MODIPA_SICUREZZA_TOKEN_SUBTITLE_ID = "modipaTokenSubTitleId";
		
	public static final String MODIPA_SICUREZZA_TOKEN_ID = ModICostanti.MODIPA_SICUREZZA_TOKEN;
	public static final String MODIPA_SICUREZZA_TOKEN_LABEL = "Abilitato";
	
	public static final String MODIPA_SICUREZZA_TOKEN_POLICY_ID = ModICostanti.MODIPA_SICUREZZA_TOKEN_POLICY;
	public static final String MODIPA_SICUREZZA_TOKEN_POLICY_LABEL = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_POLICY_VALIDAZIONE;
	public static final String MODIPASICUREZZA_TOKEN_POLICY_NOTE = "<b>!!Attenzione!!</b> Per consentire un'identificazione dell'applicativo su API erogate da altri soggetti di dominio interno selezionare una token policy.";
	
	public static final String MODIPA_SICUREZZA_TOKEN_CLIENT_ID = ModICostanti.MODIPA_SICUREZZA_TOKEN_CLIENT_ID;
	public static final String MODIPA_SICUREZZA_TOKEN_CLIENT_LABEL = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID;
	
	public static final String MODIPA_SICUREZZA_TOKEN_KID_ID = ModICostanti.MODIPA_SICUREZZA_TOKEN_KID_ID;
	public static final String MODIPA_SICUREZZA_TOKEN_KID_LABEL = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_KID;
	
	public static final String MODIPA_KEYSTORE_MODE_LABEL = "Modalità";
	public static final String MODIPA_KEYSTORE_MODE_ID = ModICostanti.MODIPA_KEYSTORE_MODE;
	public static final String MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE = ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_ARCHIVE;
	public static final String MODIPA_KEYSTORE_MODE_LABEL_ARCHIVE = "Archivio";
    public static final String MODIPA_KEYSTORE_MODE_VALUE_PATH = ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH;
    public static final String MODIPA_KEYSTORE_MODE_LABEL_PATH = "File System";
    public static final String MODIPA_KEYSTORE_MODE_VALUE_HSM = ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_HSM;
    public static final String MODIPA_KEYSTORE_MODE_LABEL_HSM = "HSM";
    public static final String MODIPA_KEYSTORE_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_KEYSTORE_MODE_VALUE_PATH; 
    public static final String MODIPA_KEYSTORE_MODE_NOTE_PATH = "<b>!!Attenzione!!</b> La modalità selezionata non consente un'identificazione dell'applicativo su API erogate da altri soggetti di dominio interno.<br/>Per consentire l'identificazione caricare il certificato associato alla chiave privata.";
	
    public static final String MODIPA_KEYSTORE_ARCHIVE_LABEL = "Archivio";
	public static final String MODIPA_KEYSTORE_ARCHIVE_ID = ModICostanti.MODIPA_KEYSTORE_ARCHIVE;
	public static final String MODIPA_KEYSTORE_ARCHIVE_NOTE_UPDATE = "<b>Attenzione:</b> Se i parametri di accesso al nuovo archivio differiscono rispetto a quello precedentemente caricato, <BR/> devono essere aggiornati, ritornando alla pagina precente, prima di procedere con il caricamento del nuovo archivio.";
	
	public static final String MODIPA_KEYSTORE_CERTIFICATO_LABEL = "Certificato";
	public static final String MODIPA_KEYSTORE_CERTIFICATO_ID = ModICostanti.MODIPA_KEYSTORE_CERTIFICATE;
		
	public static final String MODIPA_KEYSTORE_PATH_LABEL = CostantiLabel.MODIPA_STORE_PATH_LABEL;
	public static final String MODIPA_KEYSTORE_PATH_ID = ModICostanti.MODIPA_KEYSTORE_PATH;
	
	public static final String MODIPA_KEYSTORE_TYPE_LABEL = CostantiLabel.MODIPA_STORE_TYPE_LABEL;
	public static final String MODIPA_KEYSTORE_TYPE_ID = ModICostanti.MODIPA_KEYSTORE_TYPE;
	public static final String MODIPA_KEYSTORE_TYPE_VALUE_KEY_PAIR = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_KEY_PAIR;
	public static final String MODIPA_KEYSTORE_TYPE_LABEL_KEY_PAIR = CostantiLabel.KEYSTORE_TYPE_KEY_PAIR;
    public static final String MODIPA_KEYSTORE_TYPE_VALUE_PUBLIC_KEY = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_PUBLIC_KEY;
    public static final String MODIPA_KEYSTORE_TYPE_LABEL_PUBLIC_KEY = CostantiLabel.KEYSTORE_TYPE_PUBLIC_KEY;
    public static final String MODIPA_KEYSTORE_TYPE_VALUE_JWK = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JWK;
    public static final String MODIPA_KEYSTORE_TYPE_LABEL_JWK = CostantiLabel.KEYSTORE_TYPE_JWK;
	public static final String MODIPA_KEYSTORE_TYPE_VALUE_JKS = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_JKS;
	public static final String MODIPA_KEYSTORE_TYPE_LABEL_JKS = CostantiLabel.KEYSTORE_TYPE_JKS;
    public static final String MODIPA_KEYSTORE_TYPE_VALUE_PKCS12 = ModICostanti.MODIPA_KEYSTORE_TYPE_VALUE_PKCS12;
    public static final String MODIPA_KEYSTORE_TYPE_LABEL_PKCS12 = CostantiLabel.KEYSTORE_TYPE_PKCS12;
    public static final String MODIPA_KEYSTORE_TYPE_DEFAULT_VALUE = MODIPA_KEYSTORE_TYPE_VALUE_JKS;
    
    public static final String MODIPA_KEYSTORE_PATH_PRIVATE_KEY_LABEL = CostantiLabel.MODIPA_STORE_PATH_PRIVATE_KEY_LABEL;
	public static final String MODIPA_KEYSTORE_PATH_PUBLIC_KEY_LABEL = CostantiLabel.MODIPA_STORE_PATH_PUBLIC_KEY_LABEL;
	public static final String MODIPA_KEYSTORE_PATH_PUBLIC_KEY_ID = ModICostanti.MODIPA_KEYSTORE_PATH_PUBLIC_KEY;
    
	public static final String MODIPA_KEYSTORE_KEY_ALGORITHM_LABEL = CostantiLabel.MODIPA_STORE_ALGORITHM_ID_LABEL;
	public static final String MODIPA_KEYSTORE_KEY_ALGORITHM_ID = ModICostanti.MODIPA_KEYSTORE_ALGORITHM;
	public static final String MODIPA_KEYSTORE_KEY_ALGORITHM_DEFAULT_VALUE = KeyUtils.ALGO_RSA;
	
    public static final String MODIPA_KEYSTORE_PASSWORD_LABEL = "Password";
	public static final String MODIPA_KEYSTORE_PASSWORD_ID = ModICostanti.MODIPA_KEYSTORE_PASSWORD;
	
	public static final String MODIPA_KEY_ALIAS_LABEL = CostantiLabel.MODIPA_KEY_ALIAS_LABEL;
	public static final String MODIPA_KEY_ALIAS_ID = ModICostanti.MODIPA_KEY_ALIAS;
	
	public static final String MODIPA_KEY_PASSWORD_LABEL = "Password Chiave Privata";
	public static final String MODIPA_KEY_PASSWORD_ID = ModICostanti.MODIPA_KEY_PASSWORD;
	
	public static final String MODIPA_KEY_CN_SUBJECT_ID = ModICostanti.MODIPA_KEY_CN_SUBJECT;
	
	public static final String MODIPA_KEY_CN_ISSUER_ID = ModICostanti.MODIPA_KEY_CN_ISSUER;
	
	public static final String MODIPA_PROFILO_MODE_VALUE_UNDEFINED = ModICostanti.MODIPA_PROFILO_UNDEFINED;
	public static final String MODIPA_PROFILO_MODE_VALUE_DEFAULT = ModICostanti.MODIPA_PROFILO_DEFAULT;
	public static final String MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI = ModICostanti.MODIPA_PROFILO_RIDEFINISCI;
	
    public static final String MODIPA_KEYSTORE_FRUIZIONE_APPLICATIVO = ModICostanti.MODIPA_KEYSTORE_FRUIZIONE_APPLICATIVO;
	public static final String MODIPA_KEYSTORE_FRUIZIONE = ModICostanti.MODIPA_KEYSTORE_FRUIZIONE;
   
	
	
	
	// Applicativi
	
	public static final String MODIPA_APPLICATIVI_LABEL = MODIPA_TITLE_LABEL + " - "+ CostantiLabel.MODIPA_SICUREZZA_MESSAGGIO_SUBTITLE_LABEL;
	public static final String MODIPA_APPLICATIVI_ID = "modipaApplicativiTitleId";
	
	/** public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_LABEL = "Reply Audience/WSA-To"; */
	public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO_LABEL = CostantiLabel.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO_LABEL;
	public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO_LABEL = CostantiLabel.MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO_LABEL;
	public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE;
	public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO_NOTE = "Identificativo dell'Applicativo scambiato nei token di sicurezza";
	public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO_NOTE = "Identificativo dell'Applicativo indicato nel token di sicurezza della risposta";
	public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_INTERNO = "Identificativo dell’applicativo utilizzato per valorizzare gli elementi indicati di seguito, generati all'interno del token di sicurezza:<BR/>"+
			"- in una fruzione viene utilizzato per valorizzare il claim 'client_id' per API REST e l'header 'wsa:From' per API SOAP del token associato alla richiesta (<b>Nota:</b> se non definito viene utilizzato il nome dell'applicativo);<BR/>"+
			"- in un'erogazione viene utilizzato per valorizzare il claim 'aud' per API REST e l'header 'wsa:To' per API SOAP del token associato alla risposta.<BR/><BR/>"+
			"In una fruizione, inoltre, se è abilitata la funzionalità 'Verifica Audience / WSAddressing To' nella configurazione ModI di sicurezza della risposta, viene verificato che nel token associato alla risposta ricevuta vi sia un claim 'aud' per API REST o un header 'wsa:To' per API SOAP che possiede un valore identico all'identificato fornito.";
	public static final String MODIPA_APPLICATIVI_AUDIENCE_RISPOSTA_INFO_DOMINIO_ESTERNO = "Identificativo dell’applicativo utilizzato per valorizzare il claim 'aud' per API REST e l'header 'wsa:To' per API SOAP nel token di sicurezza associato alla risposta. Se non definito viene utilizzato il nome dell'erogazione";
	
	public static final String MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_LABEL = "URL (x5u)";
	public static final String MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_SA_RICHIESTA_X509_VALUE_X5URL;
	public static final String MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_NOTE = "URL che riferisce un certificato (o certificate chain) X.509 corrispondente alla chiave firmataria del security token";
	public static final String MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_INFO = "URL che riferisce un certificato (o certificate chain) X.509 corrispondente alla chiave firmataria del security token.<BR/>"+
			"Deve essere obbligatoriamente definito se l'applicativo fruisce di API REST configurate per generare un token di sicurezza tramite il claim 'x5u'";
	
	
	

	
	
	
	// API
	
	public static final String MODIPA_API_LABEL = MODIPA_TITLE_LABEL;
	public static final String MODIPA_API_ID = "modipaAPITitleId";
		
	public static final String MODIPA_API_PROFILO_CANALE_LABEL = CostantiLabel.MODIPA_API_PROFILO_CANALE_LABEL;
	public static final String MODIPA_API_PROFILO_CANALE_ID = "modipaAPIProfiloSicurezzaSubTitleId";
        
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL = "Pattern";
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE;
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01 = ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01;
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_NEW = CostantiLabel.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01;
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_OLD = "IDAC01";
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_NOTE = "Direct Trust Transport-Level Security";
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02 = ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02;
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_NEW = CostantiLabel.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02;
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_OLD = "IDAC02";
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_NOTE = "Direct Trust mutual Transport-Level Security"; 
    public static final String MODIPA_PROFILO_SICUREZZA_CANALE_DEFAULT_VALUE = MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01;
	
	
    
    // OPERAZIONI - RISORSE
	
 	public static final String MODIPA_AZIONE_LABEL = MODIPA_TITLE_LABEL;
 	public static final String MODIPA_AZIONE_ID = "modipaAzioneTitleId";
    
     
	public static final String MODIPA_API_PROFILO_INTERAZIONE_LABEL = "Interazione";
	public static final String MODIPA_API_PROFILO_INTERAZIONE_ID = "modipaAzioneProfiloInterazioneSubTitleId";
	
	public static final String MODIPA_PROFILO_INTERAZIONE_LABEL = "Pattern";
	public static final String MODIPA_PROFILO_INTERAZIONE_ID = ModICostanti.MODIPA_PROFILO_INTERAZIONE;
	public static final String MODIPA_PROFILO_INTERAZIONE_ID_INUSE_READONLY = ModICostanti.MODIPA_PROFILO_INTERAZIONE+SUFFIX_LABEL;
	public static final String MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD = ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD;
	public static final String MODIPA_PROFILO_INTERAZIONE_LABEL_CRUD = "Accesso CRUD";
	public static final String MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE = ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE;
	public static final String MODIPA_PROFILO_INTERAZIONE_LABEL_BLOCCANTE = "Bloccante";
	public static final String MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE = ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE;
	public static final String MODIPA_PROFILO_INTERAZIONE_LABEL_NON_BLOCCANTE = "Non Bloccante";
    public static final String MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE = ModICostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_REST_VALUE;
    public static final String MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE = ModICostanti.MODIPA_PROFILO_INTERAZIONE_DEFAULT_SOAP_VALUE;
	
	public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_LABEL = "Tipo";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_ID_INUSE_READONLY = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA+SUFFIX_LABEL;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_LABEL_PUSH = "PUSH";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PULL;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_LABEL_PULL = "PULL";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_DEFAULT_VALUE = MODIPA_PROFILO_INTERAZIONE_ASINCRONA_VALUE_PUSH;

    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL = "Funzione";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_ID_INUSE_READONLY = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO+SUFFIX_LABEL;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RICHIESTA = "Richiesta";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA_STATO;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RICHIESTA_STATO = "Richiesta Stato";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RISPOSTA;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_LABEL_RISPOSTA = "Risposta";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_DEFAULT_VALUE = MODIPA_PROFILO_INTERAZIONE_ASINCRONA_RUOLO_VALUE_RICHIESTA;
    
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA_ID = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_RICHIESTA_CORRELATA;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA_ID = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_SERVIZIO_RICHIESTA_CORRELATA;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA_ID = ModICostanti.MODIPA_PROFILO_INTERAZIONE_ASINCRONA_AZIONE_RICHIESTA_CORRELATA;
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_LABEL = "Richiesta Correlata"; 
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_API_LABEL = "API Richiesta Correlata";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_SERVIZIO_LABEL = "Servizio";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_AZIONE_LABEL = "Azione";
    public static final String MODIPA_PROFILO_INTERAZIONE_ASINCRONA_API_CORRELATA_A_RISORSA_LABEL = "Risorsa";
    
	public static final String MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_LABEL = CostantiLabel.MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_LABEL;
	public static final String MODIPA_API_PROFILO_SICUREZZA_MESSAGGIO_ID = "modipaProfiloSicurezzaSubTitleId";
    	
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL = "Pattern";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_UNDEFINED = MODIPA_LABEL_UNDEFINED;
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_OLD = "IDAR01";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_NEW = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_NOTE = "Direct Trust con certificato X.509";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_OLD = "IDAS01";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_NEW = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_NOTE = "Direct Trust con certificato X.509";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_OLD = "IDAR02";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_NEW = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_NOTE = "Direct Trust con certificato X.509 con unicità del token";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_OLD = "IDAS02";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_NEW = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_NOTE = "Direct Trust con certificato X.509 con unicità del messaggio";

    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_OLD = "IDAR03 con IDAR01";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_NEW = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_NOTE = "Integrità payload del messaggio";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_OLD = "IDAS03 con IDAS01";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_NEW = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_NOTE = "Integrità payload del messaggio";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_OLD = "IDAR03 con IDAR02";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_NEW = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_NOTE = "Integrità payload del messaggio + unicità del token";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_OLD = "IDAS03 con IDAS02";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_NEW = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_NOTE = "Integrità payload del messaggio + unicità del messaggio";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0401;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0401_REST = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0401_REST;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0401_REST_NOTE = "Integrità payload del messaggio";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0402;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0402_REST = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0402_REST;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0402_REST_NOTE = "Integrità payload del messaggio + unicità del token";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_DEFAULT_VALUE = MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED;
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LABEL = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LABEL_LOCALE = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LOCALE;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_PDND;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LABEL_PDND = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_PDND;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_OAUTH;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_LABEL_OAUTH = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_OAUTH;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_DEFAULT_VALUE = MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_VALUE_LOCALE;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_NOTE_LOCALE = "Token ID_AUTH generato dal mittente secondo le Linee Guida 'ModI'";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_NOTE_PDND = "Token ID_AUTH negoziato con la PDND";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SORGENTE_TOKEN_IDAUTH_NOTE_OAUTH = "Token ID_AUTH negoziato con un Authorization Server OAuth";
    
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_MODIPA;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_MODIPA;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_MODIPA_AUTH_IN_RESPONSE;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_CUSTOM = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_CUSTOM;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_CUSTOM = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_CUSTOM;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_VALUE_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_LABEL_AUTHORIZATION_CUSTOM_AUTH_IN_RESPONSE;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_IDAM03_DEFAULT_VALUE = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_IDAM03_DEFAULT_VALUE;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_NOT_IDAM03_DEFAULT_VALUE = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_NOT_IDAM03_DEFAULT_VALUE;
    
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_LABEL = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_LABEL;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HEADER_CUSTOM;
    
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL = CostantiLabel.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL; // Ho levato Includi come prefisso perche' nel caso di fruizione, l'header deve essere ripulito, quindi sarebbe piu' gestisci
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_LABEL_RIGHT = "Non ripudiabilità della trasmissione";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REQUEST_DIGEST;
	public static final boolean MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_DEFAULT = false;
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_REST_INFO_HEADER = "Questa funzionalità consente di estendere il pattern aggiungendo all’interno del token di sicurezza della risposta il digest della richiesta.<BR/>"+
			"La funzionalità consente di implementare la soluzione per la non ripudiabilità della trasmissione come suggerito nelle linee guida di interoperabilità<BR/>"+
			"Il digest della richiesta viene aggiunto nel token della risposta all'interno del claim 'request_digest'.";
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REQUEST_DIGEST_SOAP_INFO_HEADER = "Questa funzionalità consente di estendere il pattern aggiungendo all’interno dell'header di sicurezza tutti i digest della richiesta.<BR/>"+
			"La funzionalità consente di implementare la soluzione per la non ripudiabilità della trasmissione come suggerito nelle linee guida di interoperabilità<BR/>"+
			"I digest della richiesta sono inseriti in un header 'X-RequestDigest' firmato.";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL_RIGHT = "Dati del dominio del fruitore";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA;
    public static final boolean MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_DEFAULT_VALUE = false;
    
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SUBTITLE_LABEL = MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL;
	public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SUBTITLE_ID = "modipaSecurityMessageCorniceSicurezzaSubTitleId";
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_LABEL = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_LABEL;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_AUDIT_REST_01 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_AUDIT_REST_01;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_LABEL_AUDIT_REST_01 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_LABEL_AUDIT_REST_01;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_AUDIT_REST_02 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_AUDIT_REST_02;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_LABEL_AUDIT_REST_02 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_LABEL_AUDIT_REST_02;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_OLD;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_LABEL_OLD = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_LABEL_OLD;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_DEFAULT_VALUE = MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_PATTERN_VALUE_AUDIT_REST_01;
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_LABEL = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_LABEL;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_INFO_HEADER = "Il pattern selezionato consente di aggiungere all’interno del token di sicurezza di Audit le informazioni sul fruitore che ha effettuato la richiesta. Le informazioni sono veicolate nei seguenti claims:";   
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_OPZIONALE_LABEL = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_OPZIONALE_LABEL;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_OPZIONALE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_OPZIONALE;
    public static final boolean MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_OPZIONALE_DEFAULT_VALUE = false;
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_REST_INFO_HEADER = "Questa funzionalità consente di estendere il pattern aggiungendo all’interno del token di sicurezza le informazioni sull’utente che ha effettuato la richiesta. Le informazioni sono veicolate nei seguenti claims:";
    private static final List<String> MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_REST_INFO_VALORI = new ArrayList<>();
	public static List<String> getModipaProfiloSicurezzaMessaggioCorniceSicurezzaRestInfoValori() {
		return MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_REST_INFO_VALORI;
	}
	static {
		MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_REST_INFO_VALORI.add("<b>iss</b>: dominio di appartenenza dell’utente");
		MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_REST_INFO_VALORI.add("<b>sub</b>: identificativo univoco dell’utente all’interno del dominio indicato nel claim 'iss'");
		MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_REST_INFO_VALORI.add("<b>user_ip</b>: postazione da cui l’utente ha effettuato la richiesta");
	}
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SOAP_INFO_HEADER = "Questa funzionalità consente di estendere il pattern aggiungendo all’interno dell'header di sicurezza un'asserzione SAML firmata contenente le informazioni sull’utente che ha effettuato la richiesta. Le informazioni sono presenti, all'interno dell'asserzione, nelle seguenti posizioni:";
    private static final List<String> MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SOAP_INFO_VALORI = new ArrayList<>();
	public static List<String> getModipaProfiloSicurezzaMessaggioCorniceSicurezzaSoapInfoValori() {
		return MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SOAP_INFO_VALORI;
	}
	static {
		MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SOAP_INFO_VALORI.add("<b>saml2:Subject/NameID</b>: dominio di appartenenza dell’utente");
		MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SOAP_INFO_VALORI.add("<b>saml2:Attribute/User</b>: identificativo univoco dell’utente all’interno del dominio indicato nel claim 'iss'");
		MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SOAP_INFO_VALORI.add("<b>saml2:Attribute/IP-User</b>: postazione da cui l’utente ha effettuato la richiesta");
	}
    
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_DEFAULT_VALUE = ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT;
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_LABEL_DEFAULT = "Usa pattern API";
    public static final String MODIPA_PROFILO_SICUREZZA_MESSAGGIO_ACTION_MODE_LABEL_RIDEFINISCI = "Ridefinito";
    
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL = CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_ID = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_DEFAULT;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_ENTRAMBI_CON_ATTACHMENTS;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RICHIESTA_CON_ATTACHMENTS;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_RISPOSTA_CON_ATTACHMENTS;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_VALUE_PERSONALIZZATO;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI = CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA = CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA = CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI_CON_ATTACHMENTS = CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_ENTRAMBI_CON_ATTACHMENTS;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA_CON_ATTACHMENTS = CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RICHIESTA_CON_ATTACHMENTS;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA_CON_ATTACHMENTS = CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_RISPOSTA_CON_ATTACHMENTS;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_PERSONALIZZATO = CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_PERSONALIZZATO;
    
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_PARAMETRI_RISPOSTA = CostantiLabel.MODIPA_API_CONFIGURAZIONE_SICUREZZA_MESSAGGIO_MODE_LABEL_PARAMETRI_RISPOSTA;
    
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_LABEL = "Sicurezza Messaggio nella Richiesta";
	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_ID = "modipaSicurezzaRichiestaSubTitleId";
	
	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_LABEL = "Stato";
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_ID = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DEFAULT;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_ABILITATO;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_DISABILITATO;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_VALUE_PERSONALIZZATO;
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_LABEL_ABILITATO = "Abilitato";
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_LABEL_DISABILITATO = "Disabilitato";
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_MODE_LABEL_PERSONALIZZATO = "Personalizza Criteri";
    
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_LABEL = "Content-Type";
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RICHIESTA_CONTENT_TYPE_MODE_ID;
    
    public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_LABEL = "Sicurezza Messaggio nella Risposta";
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_ID = "modipaSicurezzaRispostaSubTitleId";
   	
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_LABEL = "Stato";
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_ID = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE;
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DEFAULT;
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_ABILITATO;
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_DISABILITATO;
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_VALUE_PERSONALIZZATO;
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_LABEL_ABILITATO = "Abilitato";
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_LABEL_DISABILITATO = "Disabilitato";
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_MODE_LABEL_PERSONALIZZATO = "Personalizza Criteri";
   
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_LABEL = "Content-Type";
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_CONTENT_TYPE_MODE_ID;
   	
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_LABEL = "Codice Risposta";
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID = ModICostanti.MODIPA_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID;
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RISPOSTA_RETURN_CODE_MODE_ID_INFO = "Lista di codici di risposta HTTP per i quali la sicurezza messaggio verrà utilizzata; è possibile indicare un codice http puntuale (es. 200) o un intervallo fornendo due codici separati dal trattino (es. 200-299).";
   		
   	public static final String MODIPA_API_CONFIGURAZIONE_SICUREZZA_RICHIESTA_O_RISPOSTA_CONTENT_TYPE_MODE_ID_INFO_CONTENT_TYPE = "Lista di Content-Type per i quali la sicurezza messaggio verrà utilizzata; di seguito i formati utilizzabili:";
   	
	// EROGAZIONI / FRUIZIONI
	
	public static final String MODIPA_API_IMPL_RICHIESTA_LABEL = MODIPA_TITLE_LABEL+" - Richiesta";
	public static final String MODIPA_API_IMPL_RICHIESTA_ID = "modipaAPIImplRequestTitleId";
	
	public static final String MODIPA_API_IMPL_RISPOSTA_LABEL = MODIPA_TITLE_LABEL+" - Risposta";
	public static final String MODIPA_API_IMPL_RISPOSTA_ID = "modipaAPIImplResponseTitleId";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_LABEL = "Sicurezza Messaggio";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_ID = "modipaAPIImplProfiloSicurezzaRequestSubTitleId";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_RICHIESTA_LABEL = MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_RICHIESTA_ID = "modipaAPIImplProfiloCorniceSicurezzaRequestSubTitleId";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_LABEL = "Sicurezza Messaggio";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_ID = "modipaAPIImplProfiloSicurezzaResponseSubTitleId";
	
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_ALG;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_ALG;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_RS256 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS256;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_RS384 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS384;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_RS512 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_RS512;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_ES256 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES256;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_ES384 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES384;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_ES512 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_ES512;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_DEFAULT_VALUE = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_ALG_VALUE_RS256;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_DIGEST_ENCODING;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_DIGEST_ENCODING;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_VALUE_BASE64 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_BASE64;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL_BASE64 = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL_BASE64; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_VALUE_HEX = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_HEX;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL_HEX = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DIGEST_ENCODING_LABEL_HEX; 
		
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_LABEL = "Riferimento X.509";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_LABEL_TRUE = "Utilizza impostazioni della Richiesta";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_VALUE_FALSE = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_AS_REQUEST_VALUE_FALSE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_LABEL_FALSE = "Ridefinisci";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_DEFAULT_VALUE = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_AS_REQUEST_VALUE_TRUE;
		
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_LABEL_X5C = "x5c (Certificate)";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5C = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5C;
	public static final boolean MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5C_DEFAULT_VALUE = true;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_LABEL_X5T = "x5t#256 (Certificate SHA-256 Thumbprint)";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5T = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5T;
	public static final boolean MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5T_DEFAULT_VALUE = true;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_LABEL_X5U = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5U = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_VALUE_X5U;
	public static final boolean MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5U_DEFAULT_VALUE = false;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_LABEL;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN;
    public static final boolean MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RIFERIMENTO_X509_X5C_USE_CERTIFICATE_CHAIN_DEFAULT_VALUE = false;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_LABEL = "URL (x5u)";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_X5U_URL_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RICHIESTA_X509_VALUE_X5URL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X5U_URL_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_RISPOSTA_X509_VALUE_X5URL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_NOTE = MODIPA_APPLICATIVI_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_NOTE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_X5U_URL_INFO = "URL che riferisce un certificato (o certificate chain) X.509 corrispondente alla chiave firmataria del security token.";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_ALG;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_ALG;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_DSA_SHA_256 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_DSA_SHA_256;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_DSA_SHA_256 = "DSA-SHA-256"; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_224 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_224;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_224 = "RSA-SHA-224"; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_256 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_256;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_256 = "RSA-SHA-256"; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_384 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_384;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_384 = "RSA-SHA-384"; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_512 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_RSA_SHA_512;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_RSA_SHA_512 = "RSA-SHA-512"; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_ECDSA_SHA_224 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_224;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_ECDSA_SHA_224 = "ECDSA-SHA-224"; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_ECDSA_SHA_256 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_256;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_ECDSA_SHA_256 = "ECDSA-SHA-256"; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_ECDSA_SHA_384 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_384;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_ECDSA_SHA_384 = "ECDSA-SHA-384"; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_ECDSA_SHA_512 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_ECDSA_SHA_512;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_LABEL_ECDSA_SHA_512 = "ECDSA-SHA-512"; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_DEFAULT_VALUE = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_ALG_VALUE_RSA_SHA_256;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_CANONICALIZATION_ALG;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_CANONICALIZATION_ALG;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_VALUE_INCLUSIVE_C14N_10 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_10;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_10 = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_10;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_VALUE_INCLUSIVE_C14N_11 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_INCLUSIVE_C14N_11;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_11 = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_INCLUSIVE_C14N_11;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_VALUE_EXCLUSIVE_C14N_10 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_EXCLUSIVE_C14N_10;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_EXCLUSIVE_C14N_10 = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_LABEL_EXCLUSIVE_C14N_10;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_DEFAULT_VALUE = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_CANONICALIZATION_ALG_VALUE_EXCLUSIVE_C14N_10;
	
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_BINARY_SECURITY_TOKEN = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_BINARY_SECURITY_TOKEN;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_SECURITY_TOKEN_REFERENCE = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_SECURITY_TOKEN_REFERENCE;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_SECURITY_TOKEN_REFERENCE = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_SECURITY_TOKEN_REFERENCE;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_X509 = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_X509;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_X509 = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_X509;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_THUMBPRINT = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_THUMBPRINT;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_THUMBPRINT = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_THUMBPRINT;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_SKI = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_KEY_IDENTIFIER_SKI;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_SKI = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_LABEL_KEY_IDENTIFIER_SKI;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_DEFAULT_VALUE = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_VALUE_BINARY_SECURITY_TOKEN;

    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_LABEL;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN;
    public static final boolean MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_USE_CERTIFICATE_CHAIN_DEFAULT_VALUE = false;
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_LABEL;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RICHIESTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RISPOSTA_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN;
    public static final boolean MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_RIFERIMENTO_X509_BINARY_SECURITY_TOKEN_INCLUDE_SIGNATURE_TOKEN_DEFAULT_VALUE = false;
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_VALORE_DEFAULT = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_TTL_LABEL;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_VALORE_RIDEFINITO = "Time to Live (secondi)";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_DEFAULT = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_VALUE_DEFAULT = ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_DEFAULT;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_LABEL_RIDEFINISCI = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_RIDEFINISCI;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_VALUE_RIDEFINISCI = ModIConsoleCostanti.MODIPA_PROFILO_MODE_VALUE_RIDEFINISCI;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_DEFAULT_VALUE = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_VALUE_DEFAULT;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_LABEL = "";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_IAT_SECONDS;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_IAT_SECONDS;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_NOTE = "I token creati precedentemente all'intervallo temporale indicato, in secondi, verranno rifiutati";
	public static final long MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_IAT_SECONDS_DEFAULT_VALUE = 300l;
    
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_LABEL = "Time to Live (secondi)";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_EXPIRED;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_EXPIRED;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_NOTE = "Indica la validità temporale, in secondi, a partire dalla data di creazione del security token";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_NOTE_RESPONSE = "Indica la validità temporale, in secondi, a partire dalla data di creazione del security token della risposta";
	public static final long MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_EXPIRED_DEFAULT_VALUE = 300l;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_SOAP_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_FRUIZIONE_NOTE = "Indica a chi è riferito il security token; se non viene fornito un valore verrà utilizzata la url del connettore";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_EROGAZIONE_NOTE = "Se non viene fornito un valore, il valore atteso all'interno del security token corrisponderà all'url di invocazione";

	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_JWT_CLAIMS;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_JWT_CLAIMS;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_LABEL; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_NOTE = DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_NOTE;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_TEMPLATE_HEADER_AGID;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_LABEL = "Coesistenza Token Authorization e "+MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_SOAP_LABEL = "Coesistenza Token Authorization e Integrity";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_GENERAZIONE_ID = "modipaSecurityMessageDuplicateHdrSubImbId";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_VALIDAZIONE_ID = "modipaSecurityMessageDuplicateHdrSubValId";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_LABEL = "Identificativo 'jti'";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_SAME = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_VALUE_SAME;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_LABEL_SAME = "Stesso identificativo";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_DIFFERENT = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_VALUE_DIFFERENT;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_LABEL_DIFFERENT = "Differente identificativo";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_DEFAULT = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_VALUE_SAME;

	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_LABEL = "Usa come ID Messaggio";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_AUTHORIZATION = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_AUTHORIZATION;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_LABEL_AUTHORIZATION = HttpConstants.AUTHORIZATION;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_MODI = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_MODI;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_LABEL_MODI = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_DEFAULT = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JTI_AS_ID_MESSAGGIO_VALUE_MODI;

	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_SAME = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_SAME;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL_SAME = "Stesso identificativo";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_AUDIENCE_VALUE_DIFFERENT;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_LABEL_DIFFERENT = "Differente identificativo";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_DEFAULT = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_VALUE_SAME;

	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_LABEL = ""; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_AUDIENCE_INTEGRITY_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_AUDIENCE_INTEGRITY;
		
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_LABEL; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_AUTHORIZATION_NOTE = 
			/**DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_NOTE;*/
			"";// viene messo sulla nota dell'authorization
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_JWT_CLAIMS_MODI;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_JWT_CLAIMS_MODI_LABEL; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_JWT_CLAIMS_MODI_NOTE = DynamicHelperCostanti.LABEL_PARAMETRO_MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_REST_JWT_CLAIMS_NOTE;
		
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_LABEL = "Id 'jti' per Filtro Duplicati";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_REST_DOPPI_HEADER_FILTRO_DUPLICATI; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_AUTHORIZATION;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_LABEL_AUTHORIZATION = HttpConstants.AUTHORIZATION;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_MODI = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_REST_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_MODI;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_LABEL_MODI = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_SUBSECTION_TEMPLATE_HEADER_AGID;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_DEFAULT = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_DOPPI_HEADER_FILTRO_DUPLICATI_VALUE_MODI;
	
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_REST_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_REST_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_SOAP_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_SOAP_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_FRUIZIONE_KEYSTORE_FRUIZIONE_NOTE = null; // non servono spiegazioni, il campo diventa required
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_FRUIZIONE_KEYSTORE_SA_NOTE = "La verifica utilizza, se configurato, il valore indicato di seguito altrimenti quello configurato nell'applicativo mittente";
	public static final boolean MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_DEFAULT = true;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_VALORE_LABEL = ""; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RISPOSTA_VALORE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RISPOSTA_AUDIENCE_VALORE;
	
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_REST;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_NOTE = "Elenco di Header http da firmare";
	/**public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_HTTP_HEADERS_DEFAULT_VALUE = HttpConstants.DIGEST+","+HttpConstants.CONTENT_TYPE+","+HttpConstants.CONTENT_ENCODING;*/
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_SOAP;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_NOTE = "Indicare per riga gli header da firmare; visualizzare 'info' per maggiori dettagli";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SOAP_HEADERS_INFO = "Tramite questa configurazione è possibile firmare ulteriori header soap rispetto a quelli previsti dalla specifica ModI (Timestamp, WSAddressing, Body).<BR/>"+
			"Gli ulteriori header devono essere indicati su ogni riga tramite la sintassi: {namespace}localName<BR/>Ad esempio:<BR/>- {http://example.govway.org}NomeHeader1<BR/>- {http://example.govway.org}NomeHeader2";
		
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_DEFAULT = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_MODE_LABEL_RIDEFINISCI = ModIConsoleCostanti.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_RIDEFINISCI;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_AUDIENCE_RICHIESTA_REST_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIT_AUDIENCE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_VALUE_SAME = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTE_AUDIT_AUDIENCE_VALUE_SAME;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_LABEL_SAME = "Stesso identificativo";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_VALUE_DIFFERENT = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIT_AUDIENCE_VALUE_DIFFERENT;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_LABEL_DIFFERENT = "Differente identificativo";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_VALUE_DEFAULT = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_VALUE_SAME;

	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_AUDIT_CUSTOM_LABEL = ""; 
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_AUDIENCE_AUDIT_CUSTOM_RICHIESTA_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_RICHIESTA_AUDIENCE_CUSTOM_AUDIT;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_ID_PREFIX = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_PREFIX;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_PROFILO_DEFAULT;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_DEFAULT_INFO_INTESTAZIONE_CLAIM = "CLAIM";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_DEFAULT_INFO_INTESTAZIONE = "Il valore del claim '"+MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_MODE_DEFAULT_INFO_INTESTAZIONE_CLAIM+"' deve essere indicato nella richiesta di fruizione tramite una delle seguenti modalità:";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_ID_PREFIX = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_PREFIX;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_SCHEMA_LABEL = "";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_LABEL = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_PROFILO_DEFAULT;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_MODE_DEFAULT_INFO_INTESTAZIONE = "Per default questa informazione assume il valore del soggetto registrato su GovWay, di dominio interno, per il quale si sta effettuando la richiesta di fruizione dell'API.";
		
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_LABEL = "";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_CODICE_ENTE;
		
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_LABEL = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_PROFILO_DEFAULT;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_DEFAULT_INFO_INTESTAZIONE = "L'identificativo dell'utente deve essere indicato nella richiesta di fruizione tramite una delle seguenti modalità:";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_DEFAULT_INFO_HTTP = "Header http 'GovWay-CS-User'";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_MODE_DEFAULT_INFO_URL = "Parametro della url 'govway_cs_user'";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_LABEL = "";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_USER;
		
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_LABEL = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_PROFILO_DEFAULT;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_DEFAULT_INFO_INTESTAZIONE = "La postazione dell’utente deve essere indicata nella richiesta di fruizione tramite una delle seguenti modalità:";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_DEFAULT_INFO_HTTP = "Header http 'GovWay-CS-IPUser'";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_MODE_DEFAULT_INFO_URL = "Parametro della url 'govway_cs_ipuser'";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_LABEL = "";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CORNICE_SICUREZZA_IP_USER;
		
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_PROFILO_DEFAULT;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_OAUTH_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_PROFILO_UNDEFINED;
	
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_CERTIFICATI_TRUSTSTORE_LABEL = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_MODE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_CERTIFICATI_TRUSTSTORE_ID = "modipaAPIImplTrustStoreCertsSubTitleId";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH_LABEL = CostantiLabel.MODIPA_STORE_PATH_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PATH;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_LABEL = CostantiLabel.MODIPA_STORE_TYPE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JKS;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_LABEL_JKS = CostantiLabel.KEYSTORE_TYPE_JKS;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JWK = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_VALUE_JWK;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_TYPE_LABEL_JWK = CostantiLabel.KEYSTORE_TYPE_JWK;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD_LABEL = "Password";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_LABEL = CostantiLabel.MODIPA_TRUSTSTORE_CRLS_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_CRLS_NOTE = "Elencare più file separandoli con la ','";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_LABEL = CostantiLabel.MODIPA_TRUSTSTORE_OCSP_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_OCSP_POLICY;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_PROFILO_DEFAULT;
	
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_SSL_TRUSTSTORE_LABEL = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_MODE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_SSL_TRUSTSTORE_ID = "modipaAPIImplTrustStoreSslSubTitleId";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH_LABEL = CostantiLabel.MODIPA_STORE_PATH_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PATH;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_LABEL = CostantiLabel.MODIPA_STORE_TYPE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_VALUE_JKS;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_TYPE_LABEL_JKS = CostantiLabel.KEYSTORE_TYPE_JKS;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD_LABEL = "Password";
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_PASSWORD;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_LABEL = CostantiLabel.MODIPA_TRUSTSTORE_CRLS_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_CRLS_NOTE = "Elencare più file separandoli con la ','";
			
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_LABEL = CostantiLabel.MODIPA_TRUSTSTORE_OCSP_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_SSL_TRUSTSTORE_OCSP_POLICY;

	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_KEYSTORE_FRUIZIONE_APPLICATIVO;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_APPLICATIVO = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_APPLICATIVO;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_FRUIZIONE = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL_FRUIZIONE;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_DEFAULT_VALUE = ModICostanti.MODIPA_PROFILO_DEFAULT;

	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_LABEL = MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_ID = "modipaAPIImplKeyStoreSubTitleId";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_UNDEFINED = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_UNDEFINED;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_DEFAULT;
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_RIDEFINISCI = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_STORE_MODE_LABEL_RIDEFINISCI;
    
    
	public static final String MODIPA_API_IMPL_SICUREZZA_OAUTH_LABEL_OAUTH = MODIPA_TITLE_LABEL+" - "+CostantiLabel.MODIPA_SICUREZZA_CHOICE_TOKEN_OAUTH_LABEL;
	public static final String MODIPA_API_IMPL_SICUREZZA_OAUTH_LABEL_PDND = MODIPA_TITLE_LABEL+" - "+CostantiLabel.MODIPA_SICUREZZA_CHOICE_TOKEN_PDND_LABEL;
	public static final String MODIPA_API_IMPL_SICUREZZA_OAUTH_ID = "modipaAPIImplOauthTitleId";
    
    public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_SUBSECTION_LABEL_OAUTH = CostantiLabel.MODIPA_SICUREZZA_CHOICE_TOKEN_OAUTH_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_SUBSECTION_LABEL_PDND = CostantiLabel.MODIPA_SICUREZZA_CHOICE_TOKEN_PDND_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_SUBSECTION_ID = "modipaSecurityMessageOauthSubId";
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO_LABEL = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_OAUTH_IDENTIFICATIVO;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KID_LABEL = CostantiLabel.LABEL_CREDENZIALI_AUTENTICAZIONE_TOKEN_KID;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KID_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_OAUTH_KID;
	
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KEYSTORE_MODE_LABEL = CostantiLabel.MODIPA_API_IMPL_PROFILO_SICUREZZA_MESSAGGIO_FRUIZIONE_KEYSTORE_MODE_LABEL;
	public static final String MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KEYSTORE_MODE_ID = ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_KEYSTORE_MODE;
	public static final boolean MODIPA_API_IMPL_PROFILO_SICUREZZA_OAUTH_KEYSTORE_MODE_DEFAULT_VALUE = false;
	
	
}





