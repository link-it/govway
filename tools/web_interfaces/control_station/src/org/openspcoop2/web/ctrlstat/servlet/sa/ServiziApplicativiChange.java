/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ProtocolProperty;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.FaultIntegrazioneTipo;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.autenticazione.ApiKey;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.KeystoreType;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.OggettoDialogEnum;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoreStatusParams;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * servizioApplicativoChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ServiziApplicativiChange extends Action {
		
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		// Protocol Properties
		IConsoleDynamicConfiguration consoleDynamicConfiguration = null;
		ConsoleConfiguration consoleConfiguration =null;
		ProtocolProperties protocolProperties = null;
		IProtocolFactory<?> protocolFactory= null;
		IRegistryReader registryReader = null; 
		IConfigIntegrationReader configRegistryReader = null; 
		ConsoleOperationType consoleOperationType = null;
		String protocolPropertiesSet = null;
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		consoleOperationType = ConsoleOperationType.CHANGE;
		
		// Parametri relativi al tipo operazione
		List<ProtocolProperty> oldProtocolPropertyList = null;
		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			
			// Preparo il menu
			saHelper.makeMenu();
			
			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			SoggettiCore soggettiCore = new SoggettiCore(saCore);
			
			boolean isApplicativiServerEnabled = saCore.isApplicativiServerEnabled(saHelper);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, session, request);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			boolean useIdSogg = (parentSA!=null && parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO);
			
			boolean interfacciaAvanzata = saHelper.isModalitaAvanzata();
			boolean interfacciaStandard = saHelper.isModalitaStandard();
			
			String id = saHelper.getParametroLong(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			long idServizioApplicativoLong = Long.parseLong(id);
			
			String ruoloFruitore = null; 
			String ruoloErogatore = null; 
			String ruoloSA = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RUOLO_SA);
			String tipoSA = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_TIPO_SA);
			
			boolean useAsClient = false;
			
			if(isApplicativiServerEnabled) {
				if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT.equals(tipoSA)) {
					ruoloSA = ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_FRUITORE;
				}
				if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(tipoSA)) {
					ruoloSA = ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_EROGATORE;
					
					String tmp = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_UTILIZZABILE_COME_CLIENT);
					useAsClient = ServletUtils.isCheckBoxEnabled(tmp);
				}
			}
			
			if(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_FRUITORE.equals(ruoloSA)){
				ruoloFruitore = TipologiaFruizione.NORMALE.getValue();
				ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
			}
			else if(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_RUOLO_EROGATORE.equals(ruoloSA)){
				ruoloErogatore = TipologiaErogazione.TRASPARENTE.getValue();
				ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
			}
	
			
			
			protocolPropertiesSet = saHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
			
			String nomeParameter = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_NOME);
			String descrizione = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_DESCRIZIONE);
			String provider = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			String dominio = saHelper.getParameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO);			
			String fault = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT);
			
			String tipoauthSA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE);
			String utenteSA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_USERNAME);
			String passwordSA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			String subjectSA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_SUBJECT);
			String principalSA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_PRINCIPAL);
			
			String changepwd = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD);
					
			String tipoCredenzialiSSLSorgente = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL);
			if(tipoCredenzialiSSLSorgente == null) {
				tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
			}
			String tipoCredenzialiSSLTipoArchivioS = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO);
			org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio= null;
			if(tipoCredenzialiSSLTipoArchivioS == null) {
				tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.CER; 
			} else {
				tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.valueOf(tipoCredenzialiSSLTipoArchivioS);
			}
			BinaryParameter tipoCredenzialiSSLFileCertificato = saHelper.getBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
			String tipoCredenzialiSSLFileCertificatoPassword = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_PASSWORD);
			List<String> listaAliasEstrattiCertificato = new ArrayList<>();
			String tipoCredenzialiSSLAliasCertificato = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO);
			if (tipoCredenzialiSSLAliasCertificato == null) {
				tipoCredenzialiSSLAliasCertificato = "";
			}
			String tipoCredenzialiSSLAliasCertificatoSubject= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SUBJECT);
			String tipoCredenzialiSSLAliasCertificatoIssuer= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_ISSUER);
			String tipoCredenzialiSSLAliasCertificatoType= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_TYPE);
			String tipoCredenzialiSSLAliasCertificatoVersion= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_VERSION);
			String tipoCredenzialiSSLAliasCertificatoSerialNumber= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SERIAL_NUMBER);
			String tipoCredenzialiSSLAliasCertificatoSelfSigned= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_SELF_SIGNED);
			String tipoCredenzialiSSLAliasCertificatoNotBefore= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_BEFORE);
			String tipoCredenzialiSSLAliasCertificatoNotAfter = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO_NOT_AFTER); 
			String tipoCredenzialiSSLVerificaTuttiICampi = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI);
			if (
					(tipoCredenzialiSSLVerificaTuttiICampi == null || StringUtils.isEmpty(tipoCredenzialiSSLVerificaTuttiICampi)) 
					&&
					(saHelper.isEditModeInProgress() && saHelper.getPostBackElementName()==null) 
				){ // prima volta
				tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
			}
			String tipoCredenzialiSSLConfigurazioneManualeSelfSigned= saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_MANUALE_SELF_SIGNED);
			if (tipoCredenzialiSSLConfigurazioneManualeSelfSigned == null) {
				tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_DISABLED;
			}
			String issuerSA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_ISSUER);

			String tipoCredenzialiSSLWizardStep = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP);
			if (tipoCredenzialiSSLWizardStep == null) {
				tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
			}
			String oldTipoCredenzialiSSLWizardStep = tipoCredenzialiSSLWizardStep;
			
			String multipleApiKey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS);
			String appId = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID);
			String apiKey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_API_KEY);
				
			String tokenPolicySA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_TOKEN_POLICY);
			String tokenClientIdSA = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			
			boolean tokenWithHttpsEnabledByConfigSA = false;
			boolean tokenByPDND = false;
			boolean tokenModiPDNDOauth = false;
			if(tipoauthSA!=null && !StringUtils.isEmpty(tipoauthSA)) {
				tokenByPDND = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_PDND.equals(tipoauthSA) || ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_PDND.equals(tipoauthSA);
				tokenWithHttpsEnabledByConfigSA = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_PDND.equals(tipoauthSA) || ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL_E_TOKEN_OAUTH.equals(tipoauthSA);
				if(tokenWithHttpsEnabledByConfigSA) {
					tipoauthSA = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL;
				}
				tokenModiPDNDOauth = ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_PDND.equals(tipoauthSA) || ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN_OAUTH.equals(tipoauthSA);
				if(tokenModiPDNDOauth) {
					tipoauthSA = ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN;
				}
				if(tokenPolicySA==null || StringUtils.isEmpty(tokenPolicySA) || CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicySA)) {
					if(tokenByPDND) {
						tokenPolicySA = saCore.getDefaultPolicyGestioneTokenPDND();
					}
				}
				else {
					if(!tokenByPDND && (tokenWithHttpsEnabledByConfigSA || tokenModiPDNDOauth) && saCore.isPolicyGestioneTokenPDND(tokenPolicySA)) {
						tokenPolicySA=null;
					}
					else if(tokenByPDND && !saCore.isPolicyGestioneTokenPDND(tokenPolicySA)) {
						tokenPolicySA = saCore.getDefaultPolicyGestioneTokenPDND();
					}
				}
			}
						
			String sbustamentoInformazioniProtocolloRisposta = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RISPOSTA);
			
			String faultactor = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT_ACTOR);
			String genericfault = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT_GENERIC_CODE);
			String prefixfault = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_FAULT_PREFIX);
			
			String invrifRisposta = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RISPOSTA);

			
			String sbustamento = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_SOAP);
			String sbustamentoInformazioniProtocolloRichiesta = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_SBUSTAMENTO_INFO_PROTOCOLLO_RICHIESTA);
			String getmsg = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_MESSAGE_BOX);
			
			String invrifRichiesta = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_INVIO_PER_RIFERIMENTO_RICHIESTA);
			
			String risprif = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_RISPOSTA_PER_RIFERIMENTO);
			
			String tipoauthRichiesta = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_TIPO_AUTENTICAZIONE);
			
			String endpointtype = saHelper.readEndPointType();
			String tipoconn = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			// token policy
			String autenticazioneTokenS = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String tokenPolicy = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			
			// proxy
			String proxyEnabled = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxyHostname = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxyPort = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxyUsername = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxyPassword = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);
			
			// tempi risposta
			String tempiRispostaEnabled = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRispostaConnectionTimeout = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRispostaReadTimeout = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRispostaTempoMedioRisposta = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			String transferMode = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transferModeChunkSize = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirectMode = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirectMaxHop = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String httpImpl = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_HTTP_IMPL);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(saHelper, transferMode, redirectMode, httpImpl);
			
			// http
			String url = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// api key
			String autenticazioneApiKey = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_API_KEY);
			String apiKeyHeader = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_HEADER);
			if(apiKeyHeader==null || StringUtils.isEmpty(apiKeyHeader)) {
				apiKeyHeader = CostantiConnettori.DEFAULT_HEADER_API_KEY;
			}
			String apiKeyValue = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_VALUE);
			String appIdHeader = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_HEADER);
			if(appIdHeader==null || StringUtils.isEmpty(appIdHeader)) {
				appIdHeader = CostantiConnettori.DEFAULT_HEADER_APP_ID;
			}
			String appIdValue = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_VALUE);
			String useOAS3NamesTmp = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_NOMI_OAS);
			boolean useOAS3Names=true;
			if(useOAS3NamesTmp!=null && StringUtils.isNotEmpty(useOAS3NamesTmp)) {
				useOAS3Names = ServletUtils.isCheckBoxEnabled(useOAS3NamesTmp);
			}
			else {
				useOAS3Names = saHelper.isAutenticazioneApiKeyUseOAS3Names(apiKeyHeader, appIdHeader);
			}
			String useAppIdTmp = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_USE_APP_ID);
			boolean useAppId=false;
			if(useAppIdTmp!=null && StringUtils.isNotEmpty(useAppIdTmp)) {
				useAppId = ServletUtils.isCheckBoxEnabled(useAppIdTmp);
			}
			else {
				useAppId = saHelper.isAutenticazioneApiKeyUseAppId(appIdValue);
			}
			
			// jms
			String nomeCodaJMS = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipoCodaJMS = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}
			
			// https
			String httpsurl = url;
			String httpstipologia = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
			String httpsTrustVerifyCertS = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			String httpspath = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = saHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = ServletUtils.isCheckBoxEnabled(httpsstatoS);
			String httpskeystore = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			String httpsKeyAlias = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			String httpsTrustStoreOCSPPolicy = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
			String httpsKeyStoreBYOKPolicy = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_BYOK_POLICY);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = saHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			String requestOutputFileName = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNamePermissions = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
			String requestOutputFileNameHeaders = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputFileNameHeadersPermissions = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
			String requestOutputParentDirCreateIfNotExists = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);
			
			//status
			ConnettoreStatusParams connettoreStatusParams = ConnettoreStatusParams.fillFrom(saHelper);
			
			String resetElementoCacheS = saHelper.getParametroBoolean(CostantiControlStation.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE);
			boolean resetElementoCache = ServletUtils.isCheckBoxEnabled(resetElementoCacheS);
			
			boolean integrationManagerEnabled = !saHelper.isModalitaStandard() && saCore.isIntegrationManagerEnabled();
						
			boolean visualizzaModificaCertificato = false;
			boolean visualizzaAddCertificato = false;
			String servletCredenzialiList = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST;
			List<Parameter> parametersServletCredenzialiList = null;
			Integer numeroCertificati = 0;
			String servletCredenzialiAdd = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_ADD;
			
			Long soggLong = 0L;
			if(provider != null){
				soggLong = Long.parseLong(provider);
			}

			// Prendo il nome e il provider del servizioApplicativo
			ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativoLong);
			String oldNome = sa.getNome();
			IDSoggetto oldIdSoggetto = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
			IDServizioApplicativo oldIdServizioApplicativo = new IDServizioApplicativo();
			oldIdServizioApplicativo.setIdSoggettoProprietario(oldIdSoggetto);
			oldIdServizioApplicativo.setNome(oldNome);
			int idProv = sa.getIdSoggetto().intValue();

			InvocazionePorta ip = sa.getInvocazionePorta();
			InvocazionePortaGestioneErrore ipge = null;
			Credenziali credenziali = null;
			Credenziali oldCredenziali = null;
			if (ip != null) {
				ipge = ip.getGestioneErrore();
				numeroCertificati = ip.sizeCredenzialiList();
				if(ip.sizeCredenzialiList()>0) {
					credenziali = ip.getCredenziali(0);
					oldCredenziali = ip.getCredenziali(0);
					
					visualizzaAddCertificato = true;
					if(ip.sizeCredenzialiList() == 1) {  // se ho definito solo un certificato c'e' il link diretto alla modifica
						visualizzaModificaCertificato = true;
					}
				}
			}
			
			Parameter pIdSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+"");
			Parameter pIdSoggettoSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+"");
			
			parametersServletCredenzialiList = new ArrayList<>();
			parametersServletCredenzialiList.add(pIdSA);
			parametersServletCredenzialiList.add(pIdSoggettoSA);
			
			if(dominio != null) {
				Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
				parametersServletCredenzialiList.add(pDominio);
			}
			
			boolean encryptOldPlainPwd = false;
			if(oldCredenziali!=null && org.openspcoop2.core.config.constants.CredenzialeTipo.BASIC.equals(oldCredenziali.getTipo())) {
				encryptOldPlainPwd = !oldCredenziali.isCertificateStrictVerification() && saCore.isApplicativiPasswordEncryptEnabled(); 
			}
			
			InvocazioneServizio is = sa.getInvocazioneServizio();
			if(is==null) {
				throw new CoreException("ServizioApplicativo con id '"+idServizioApplicativoLong+"' senza InvocazioneServizio");
			}
			InvocazioneCredenziali cis = is.getCredenziali();
			Connettore connis = is.getConnettore();
			if(connis==null) {
				throw new CoreException("ServizioApplicativo con id '"+idServizioApplicativoLong+"' senza connettore in InvocazioneServizio");
			}
			List<Property> cp = connis.getPropertyList();
			
			Boolean isConnettoreCustomUltimaImmagineSalvata = connis.getCustom();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(connis, ConnettoreServletType.SERVIZIO_APPLICATIVO_CHANGE, saHelper, 
							(endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			if (endpointtype == null &&
				connis!=null){
				if ((connis.getCustom()!=null && connis.getCustom()) && 
						!TipiConnettore.HTTPS.toString().equals(connis.getTipo()) && 
						!TipiConnettore.FILE.toString().equals(connis.getTipo()) &&
						!TipiConnettore.STATUS.toString().equals(connis.getTipo())) {
					endpointtype = TipiConnettore.CUSTOM.toString();
					tipoconn = connis.getTipo();
				} else
					endpointtype = connis.getTipo();
			}
			if(endpointtype==null){
				endpointtype=TipiConnettore.DISABILITATO.toString();
			}
			
			String nomeProtocollo = null;
			String tipoENomeSoggetto = null;
			String nomePdd = null;
			IDSoggetto idSoggetto = null;
			if(saCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(idProv);
				nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
				tipoENomeSoggetto = saHelper.getLabelNomeSoggetto(nomeProtocollo, soggetto.getTipo() , soggetto.getNome());
				nomePdd = soggetto.getPortaDominio();
				idSoggetto = new IDSoggetto(soggetto.getTipo() , soggetto.getNome());
			}
			else{
				Soggetto soggetto = soggettiCore.getSoggetto(idProv);
				nomeProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
				tipoENomeSoggetto = saHelper.getLabelNomeSoggetto(nomeProtocollo, soggetto.getTipo() , soggetto.getNome());
				idSoggetto = new IDSoggetto(soggetto.getTipo() , soggetto.getNome());
			}

			if(dominio==null) {
				boolean isSupportatoAutenticazioneApplicativiEsterni = saCore.isSupportatoAutenticazioneApplicativiEsterniErogazione(nomeProtocollo);
				if(isSupportatoAutenticazioneApplicativiEsterni) {
					PddCore pddCore = new PddCore(saCore);
					dominio = pddCore.isPddEsterna(nomePdd) ? SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE : SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
				}
			}
			
			boolean tokenWithHttsSupportato = false;
			if(nomeProtocollo!=null) {
				ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
				tokenWithHttsSupportato = protocolFactoryManager.getProtocolFactoryByName(nomeProtocollo).createProtocolConfiguration().isSupportatoAutenticazioneApplicativiHttpsConToken();
			}
			
			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
			if(!saHelper.isModalitaCompleta()) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
				labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
			}
			
			// Protocol Properties	
			protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(nomeProtocollo);
			consoleDynamicConfiguration =  protocolFactory.createDynamicConfigurationConsole();
			registryReader = saCore.getRegistryReader(protocolFactory); 
			configRegistryReader = saCore.getConfigIntegrationReader(protocolFactory);
			consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigServizioApplicativo(consoleOperationType, saHelper, 
					registryReader, configRegistryReader, oldIdServizioApplicativo);
			protocolProperties = saHelper.estraiProtocolPropertiesDaRequest(consoleConfiguration, consoleOperationType);
			oldProtocolPropertyList = sa.getProtocolPropertyList(); 
			
			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, idServizioApplicativoLong+"");
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_SERVIZIO_APPLICATIVO);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, oldIdServizioApplicativo.toString());
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, URLEncoder.encode( ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE + "?" + request.getQueryString(), "UTF-8"));
			if(nomeProtocollo!=null) {
				propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, nomeProtocollo);
			}
			
			String postBackElementName = saHelper.getPostBackElementName();
			String labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;


			// Controllo se ho modificato il protocollo, ricalcolo il default della versione del protocollo
			@SuppressWarnings("unused")
			boolean postBackTipoAuthSA = false;
			if(postBackElementName != null ){
				// tipo autenticazione
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_TIPO_AUTENTICAZIONE)){
					
					postBackTipoAuthSA = true;
					
					if(tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
						
						if(oldCredenziali!=null && oldCredenziali.getTipo()!=null && oldCredenziali.getTipo().getValue().equals(tipoauthSA)){
							
							tipoCredenzialiSSLWizardStep  = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
							
							// il certificato non e' cambiato 
							if(tipoCredenzialiSSLAliasCertificatoSubject==null || StringUtils.isEmpty(tipoCredenzialiSSLAliasCertificatoSubject)) {
								if(oldCredenziali.getCertificate()!=null) {
									try {
										Certificate cSelezionato = ArchiveLoader.load(oldCredenziali.getCertificate());
										tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
										tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
										tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
										tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
										tipoCredenzialiSSLAliasCertificatoNotBefore = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotBefore());
										tipoCredenzialiSSLAliasCertificatoNotAfter = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotAfter());
									}catch(UtilsException e) {
										pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
										tipoCredenzialiSSLAliasCertificato = "";
										tipoCredenzialiSSLAliasCertificatoSubject= "";
										tipoCredenzialiSSLAliasCertificatoIssuer= "";
										tipoCredenzialiSSLAliasCertificatoType= "";
										tipoCredenzialiSSLAliasCertificatoVersion= "";
										tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
										tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
										tipoCredenzialiSSLAliasCertificatoNotBefore= "";
										tipoCredenzialiSSLAliasCertificatoNotAfter = "";
									}
								}
								tipoCredenzialiSSLVerificaTuttiICampi = oldCredenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
							}
							
							if(tokenWithHttpsEnabledByConfigSA) {
								tokenClientIdSA = oldCredenziali.getUser();
								tokenPolicySA = oldCredenziali.getTokenPolicy();
								
								if(!tokenByPDND && saCore.isPolicyGestioneTokenPDND(tokenPolicySA)) {
									tokenPolicySA=null;
								}
								else if(tokenByPDND && !saCore.isPolicyGestioneTokenPDND(tokenPolicySA)) {
									tokenPolicySA = saCore.getDefaultPolicyGestioneTokenPDND();
								}
								
							}
							
						}
						else {
							
							// reset impostazioni sezione ssl
							tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
							tipoCredenzialiSSLTipoArchivio = ArchiveType.CER;
							tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
							tipoCredenzialiSSLAliasCertificato = "";
							tipoCredenzialiSSLAliasCertificatoSubject= "";
							tipoCredenzialiSSLAliasCertificatoIssuer= "";
							tipoCredenzialiSSLAliasCertificatoType= "";
							tipoCredenzialiSSLAliasCertificatoVersion= "";
							tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
							tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
							tipoCredenzialiSSLAliasCertificatoNotBefore= "";
							tipoCredenzialiSSLAliasCertificatoNotAfter = "";
							listaAliasEstrattiCertificato = new ArrayList<>();
							tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;
							
						}
							
					} else {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
						
						if(oldCredenziali!=null && oldCredenziali.getTipo()!=null) {
							if(oldCredenziali.getTipo().getValue().equals(tipoauthSA)) {
								if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(tipoauthSA)) {
									utenteSA = oldCredenziali.getUser();
									passwordSA = oldCredenziali.getPassword();
									tipoCredenzialiSSLVerificaTuttiICampi = oldCredenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
								}
								if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoauthSA)) {
									utenteSA = oldCredenziali.getUser();
									passwordSA = oldCredenziali.getPassword();
									tipoCredenzialiSSLVerificaTuttiICampi = oldCredenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
									multipleApiKey = oldCredenziali.isAppId() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
									appId = oldCredenziali.getUser();
									apiKey = oldCredenziali.getPassword();
								}
								if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL.equals(tipoauthSA)) {
									principalSA = oldCredenziali.getUser();
								}
								
								if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN.equals(tipoauthSA)) {
									tokenClientIdSA = oldCredenziali.getUser();
									tokenPolicySA = oldCredenziali.getTokenPolicy();
								}
								else if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoauthSA) && tokenWithHttsSupportato) {
									tokenClientIdSA = oldCredenziali.getUser();
									tokenPolicySA = oldCredenziali.getTokenPolicy();
									tokenWithHttpsEnabledByConfigSA = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoauthSA) && StringUtils.isNotEmpty(tokenClientIdSA);
								}
								
								if((tokenWithHttpsEnabledByConfigSA || tokenModiPDNDOauth)) {
									if(!tokenByPDND && saCore.isPolicyGestioneTokenPDND(tokenPolicySA)) {
										tokenPolicySA=null;
									}
									else if(tokenByPDND && !saCore.isPolicyGestioneTokenPDND(tokenPolicySA)) {
										tokenPolicySA = saCore.getDefaultPolicyGestioneTokenPDND();
									}
								}
							}
							else {
								if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(tipoauthSA) ||
										ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoauthSA)) {
									tipoCredenzialiSSLVerificaTuttiICampi = Costanti.CHECK_BOX_DISABLED; // dovrà essere re-effettuata la cifratura
								}
							}
						}
					}
				}

				// tipo di configurazione SSL
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL) || 
						postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO_LINK_MODIFICA)) {
					listaAliasEstrattiCertificato = new ArrayList<>();
					tipoCredenzialiSSLTipoArchivio = ArchiveType.CER;
					tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
					tipoCredenzialiSSLAliasCertificato = "";
					tipoCredenzialiSSLAliasCertificatoSubject= "";
					tipoCredenzialiSSLAliasCertificatoIssuer= "";
					tipoCredenzialiSSLAliasCertificatoType= "";
					tipoCredenzialiSSLAliasCertificatoVersion= "";
					tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
					tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
					tipoCredenzialiSSLAliasCertificatoNotBefore= "";
					tipoCredenzialiSSLAliasCertificatoNotAfter = "";
					saHelper.deleteBinaryParameters(tipoCredenzialiSSLFileCertificato); 
					tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;

					if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE)) {
						subjectSA = "";
						issuerSA = "";
						tipoCredenzialiSSLConfigurazioneManualeSelfSigned = Costanti.CHECK_BOX_DISABLED;
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
					}
				}

				// cambio il tipo archivio butto via il vecchio certificato				
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_TIPO_ARCHIVIO)) {
					saHelper.deleteBinaryParameters(tipoCredenzialiSSLFileCertificato); 
					tipoCredenzialiSSLAliasCertificato = "";
					tipoCredenzialiSSLAliasCertificatoSubject= "";
					tipoCredenzialiSSLAliasCertificatoIssuer= "";
					tipoCredenzialiSSLAliasCertificatoType= "";
					tipoCredenzialiSSLAliasCertificatoVersion= "";
					tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
					tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
					tipoCredenzialiSSLAliasCertificatoNotBefore= "";
					tipoCredenzialiSSLAliasCertificatoNotAfter = "";
					listaAliasEstrattiCertificato = new ArrayList<>();
					tipoCredenzialiSSLVerificaTuttiICampi = ConnettoriCostanti.DEFAULT_VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI;
					tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;
				}

				// selezione alias
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO)) {
					if(StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificato)) {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK;
					} else {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ALIAS_NON_SCELTO;
					}
					tipoCredenzialiSSLAliasCertificatoSubject= "";
					tipoCredenzialiSSLAliasCertificatoIssuer= "";
					tipoCredenzialiSSLAliasCertificatoType= "";
					tipoCredenzialiSSLAliasCertificatoVersion= "";
					tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
					tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
					tipoCredenzialiSSLAliasCertificatoNotBefore= "";
					tipoCredenzialiSSLAliasCertificatoNotAfter = "";
				}
				
				// Change Password basic/api
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CHANGE_PASSWORD)) {
					if(!ServletUtils.isCheckBoxEnabled(changepwd)) {
						if (oldCredenziali != null){
							if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(tipoauthSA)) {
								passwordSA = oldCredenziali.getPassword();
								tipoCredenzialiSSLVerificaTuttiICampi = oldCredenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
							}
							if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoauthSA)) {
								passwordSA = oldCredenziali.getPassword();
								tipoCredenzialiSSLVerificaTuttiICampi = oldCredenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
								multipleApiKey = oldCredenziali.isAppId() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
								appId = oldCredenziali.getUser();
								apiKey = oldCredenziali.getPassword();
							}
						}
					}
					else {
						appId = null;
						apiKey = null;
					}
				}
				
				// apikey
				if(postBackElementName.equalsIgnoreCase(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_MULTIPLE_API_KEYS)) {
					appId = null;
					apiKey = null;
				}
				
			}
			
			// reset elemento dalla cache
			if(resetElementoCache) {
				
				// Uso lo stessoAlias
				List<String> aliases = soggettiCore.getJmxPdDAliases();
				String alias = null;
				if(aliases!=null && !aliases.isEmpty()) {
					alias = aliases.get(0);
				}
				String labelApplicativo = (saHelper.isSoggettoMultitenantSelezionato() ? sa.getNome() : saHelper.getLabelServizioApplicativoConDominioSoggetto(oldIdServizioApplicativo));
				saCore.invokeJmxMethodAllNodesAndSetResult(pd, soggettiCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias), 
						soggettiCore.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApplicativo(alias),
						MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_SUCCESSO,labelApplicativo),
						MessageFormat.format(CostantiControlStation.LABEL_ELIMINATO_CACHE_FALLITO_PREFIX,labelApplicativo),
						idServizioApplicativoLong);				
				
				String resetFromLista = saHelper.getParameter(CostantiControlStation.PARAMETRO_RESET_CACHE_FROM_LISTA);
				boolean arrivoDaLista = "true".equalsIgnoreCase(resetFromLista);
				
				if(arrivoDaLista) {
					
					parentSA = useIdSogg ? ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO : ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
					
					// salvo il punto di ingresso
					ServletUtils.setObjectIntoSession(request, session, parentSA, ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT);
					
					// preparo lista
					ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
					
					int idLista = -1;
					if(!useIdSogg){
						idLista = Liste.SERVIZIO_APPLICATIVO;
					}
					else {
						idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;
					}
	
					List<ServizioApplicativo> lista = null;
										
					// poiche' esistono filtri che hanno necessita di postback salvo in sessione
					if(!ServletUtils.isSearchDone(saHelper)) {
						lista = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista,  ServizioApplicativo.class);
					}
					
					ricerca = saHelper.checkSearchParameters(idLista, ricerca);
					
					saHelper.clearFiltroSoggettoByPostBackProtocollo(0, ricerca, idLista);
					
					if(!useIdSogg){
						boolean filtroSoggetto = false;
						if(saHelper.isSoggettoMultitenantSelezionato()) {
							List<String> protocolli = saCore.getProtocolli(request, session,false);
							if(protocolli!=null && protocolli.size()==1) { // dovrebbe essere l'unico caso in cui un soggetto multitenant è selezionato
								String protocollo = protocolli.get(0);
								filtroSoggetto = !saCore.isSupportatoAutenticazioneApplicativiEsterniErogazione(protocollo);  // devono essere fatti vedere anche quelli
							}
						}
						if(filtroSoggetto) {
							ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, saHelper.getSoggettoMultitenantSelezionato());
						}

						if(lista==null) {
							if(saCore.isVisioneOggettiGlobale(userLogin)){
								lista = saCore.soggettiServizioApplicativoList(null, ricerca);
							}else{
								lista = saCore.soggettiServizioApplicativoList(userLogin, ricerca);
							}
						}
					}else {
						ricerca = saHelper.checkSearchParameters(idLista, ricerca);
						
						lista = saCore.soggettiServizioApplicativoList(ricerca,soggLong);
					}
					
					if(!saHelper.isPostBackFilterElement()) {
						ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
					}
	
					saHelper.prepareServizioApplicativoList(ricerca, lista, useIdSogg, false);
					
					ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
	
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.CHANGE());
					
				}
			}
			
			boolean checkWizard = false;
			if(
					//!postBackTipoAuthSA && // Fix: non veniva attivato il wizard quando da una credenziale diversa da https (anche nessuna) si impostava https   
					tipoauthSA != null && tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
				if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO) &&
						!ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_VERIFICA_TUTTI_CAMPI.equals(postBackElementName)) {
					if(tipoCredenzialiSSLFileCertificato.getValue() != null && tipoCredenzialiSSLFileCertificato.getValue().length > 0) {
						tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ALIAS_NON_SCELTO;
						if(!tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.CER)) {
							if(StringUtils.isNotEmpty(tipoCredenzialiSSLFileCertificatoPassword) || 
									(KeystoreType.JKS.isType(tipoCredenzialiSSLTipoArchivio.name()) && !saCore.isLoadCertificateWizardJksPasswordRequiredRequired()) ||
									(KeystoreType.PKCS12.isType(tipoCredenzialiSSLTipoArchivio.name()) && !saCore.isLoadCertificateWizardPkcs12PasswordRequiredRequired())
									) {
								try {
									listaAliasEstrattiCertificato = ArchiveLoader.readAliases(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLFileCertificatoPassword);
									Collections.sort(listaAliasEstrattiCertificato);
	
									//se ho un solo alias lo imposto
									if(!listaAliasEstrattiCertificato.isEmpty() && listaAliasEstrattiCertificato.size() == 1) {
										tipoCredenzialiSSLAliasCertificato = listaAliasEstrattiCertificato.get(0);
									}
									
									// ho appena caricato il file devo solo far vedere la form senza segnalare il messaggio di errore
									if(oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO)
											||oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE) 
											|| oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO)) {
										checkWizard = true;
										if(listaAliasEstrattiCertificato.size() > 1) {
											pd.setMessage("Il file caricato contiene pi&ugrave; certificati, selezionare un'"+ConnettoriCostanti.LABEL_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_ALIAS_CERTIFICATO, MessageType.INFO);
										}  
									}
								}catch(UtilsException e) {
									pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
									tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO;
									tipoCredenzialiSSLAliasCertificato = "";
									tipoCredenzialiSSLAliasCertificatoSubject= "";
									tipoCredenzialiSSLAliasCertificatoIssuer= "";
									tipoCredenzialiSSLAliasCertificatoType= "";
									tipoCredenzialiSSLAliasCertificatoVersion= "";
									tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
									tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
									tipoCredenzialiSSLAliasCertificatoNotBefore= "";
									tipoCredenzialiSSLAliasCertificatoNotAfter = "";
								}
	
								if(!listaAliasEstrattiCertificato.isEmpty() && StringUtils.isNotEmpty(tipoCredenzialiSSLAliasCertificato)) {
									try {
										Certificate cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
										tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
										tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
										tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
										tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
										tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
										tipoCredenzialiSSLAliasCertificatoNotBefore = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotBefore());
										tipoCredenzialiSSLAliasCertificatoNotAfter = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotAfter());
										tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK;
									}catch(UtilsException e) {
										pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
										tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO;
										tipoCredenzialiSSLAliasCertificato = "";
										tipoCredenzialiSSLAliasCertificatoSubject= "";
										tipoCredenzialiSSLAliasCertificatoIssuer= "";
										tipoCredenzialiSSLAliasCertificatoType= "";
										tipoCredenzialiSSLAliasCertificatoVersion= "";
										tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
										tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
										tipoCredenzialiSSLAliasCertificatoNotBefore= "";
										tipoCredenzialiSSLAliasCertificatoNotAfter = "";
									}
								} 
							} else {
								tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE;
							}
						} else {
							try {
								Certificate cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
								tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
								tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
								tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
								tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
								tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
								tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
								tipoCredenzialiSSLAliasCertificatoNotBefore = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotBefore());
								tipoCredenzialiSSLAliasCertificatoNotAfter = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotAfter());
								
								// dalla seconda volta che passo, posso salvare, la prima mostro il recap del certificato estratto
								
								if(oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK_TIPO_CER)||
										oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK)) {
									tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK;
								} else {
									tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CERTIFICATO_OK_TIPO_CER;
									checkWizard = true;
								}
							}catch(UtilsException e) {
								pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
								tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO;
								tipoCredenzialiSSLAliasCertificato = "";
								tipoCredenzialiSSLAliasCertificatoSubject= "";
								tipoCredenzialiSSLAliasCertificatoIssuer= "";
								tipoCredenzialiSSLAliasCertificatoType= "";
								tipoCredenzialiSSLAliasCertificatoVersion= "";
								tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
								tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
								tipoCredenzialiSSLAliasCertificatoNotBefore= "";
								tipoCredenzialiSSLAliasCertificatoNotAfter = "";
							}
						}
					} else {
						if(tipoCredenzialiSSLAliasCertificatoSubject==null || StringUtils.isEmpty(tipoCredenzialiSSLAliasCertificatoSubject)) {
							tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO;
						}
					}
				}
				
				if(StringUtils.isNotEmpty(tipoCredenzialiSSLWizardStep) && ( tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_CARICA_CERTIFICATO)
						||tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_PASSWORD_NON_PRESENTE) 
						|| tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_WIZARD_STEP_ERRORE_LETTURA_CERTIFICATO)
						)) {
					if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
						labelButtonSalva = ConnettoriCostanti.LABEL_BUTTON_INVIA_CARICA_CERTIFICATO;
					}
				}else { 
					labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;
				}
			} else {
				labelButtonSalva = Costanti.LABEL_MONITOR_BUTTON_INVIA;
			}
			
			if(postBackElementName == null && oldTipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD))
				tipoCredenzialiSSLWizardStep  = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
			
			if(labelButtonSalva!= null) {
				pd.setLabelBottoneInvia(labelButtonSalva);
			}
			
			boolean multipleApiKeysEnabled = false;
			boolean appIdModificabile = ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_APP_ID_MODIFICABILE;
			if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoauthSA) ) {
				multipleApiKeysEnabled = ServletUtils.isCheckBoxEnabled(multipleApiKey);
				if(appIdModificabile && multipleApiKeysEnabled) {
					boolean saDefined = nomeParameter!=null && !"".equals(nomeParameter) && 
							idSoggetto!=null && 
							idSoggetto.getTipo()!=null && !"".equals(idSoggetto.getTipo()) && 
							idSoggetto.getNome()!=null && !"".equals(idSoggetto.getNome());
					if(
							(appId==null || "".equals(appId)) 
							&&
							( saDefined ) 
						){
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setNome(nomeParameter);
						idSA.setIdSoggettoProprietario(idSoggetto);
						appId = saCore.toAppId(nomeProtocollo, idSA, multipleApiKeysEnabled);
					}
				}
			}
			
			boolean modificataTipoAutenticazione = false;
			if(oldCredenziali!=null && oldCredenziali.getTipo()!=null &&
				!oldCredenziali.getTipo().getValue().equals(tipoauthSA)) {
				modificataTipoAutenticazione = true;
			}
			
			// Se nomehid = null, devo visualizzare la pagina per la modifica
			// dati
			if(saHelper.isEditModeInProgress() || checkWizard){
				
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(labelApplicativiDi + tipoENomeSoggetto,
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),								
									new Parameter(oldNome, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(oldNome, null)
							);
				}

				if(nomeParameter==null) {
					nomeParameter = oldNome;
				}
				
				if(descrizione==null) {
					descrizione = sa.getDescrizione();
				}
				
				if (fault == null &&
					ipge != null &&
					ipge.getFault()!=null) {
					fault = ipge.getFault().toString();
				}
				
				if (faultactor == null &&
					ipge != null) {
					faultactor = ipge.getFaultActor();
				}
				if (genericfault == null) {
					if (ipge != null &&
						ipge.getGenericFaultCode()!=null) {
						genericfault = ipge.getGenericFaultCode().toString();
					}
					if ((genericfault == null) || "".equals(genericfault)) {
						genericfault = CostantiConfigurazione.DISABILITATO.toString();
					}
				}
				if (prefixfault == null &&
					ipge != null) {
					prefixfault = ipge.getPrefixFaultCode();
				}
				if (invrifRisposta == null) {
					if (ip != null &&
						ip.getInvioPerRiferimento()!=null) {
						invrifRisposta = ip.getInvioPerRiferimento().toString();
					}
					if ((invrifRisposta == null) || "".equals(invrifRisposta)) {
						invrifRisposta = CostantiConfigurazione.DISABILITATO.toString();
					}
				}
				if (sbustamentoInformazioniProtocolloRisposta == null) {
					if (ip != null &&
						ip.getSbustamentoInformazioniProtocollo()!=null) {
						sbustamentoInformazioniProtocolloRisposta = ip.getSbustamentoInformazioniProtocollo().toString();
					}
					if ((sbustamentoInformazioniProtocolloRisposta == null) || "".equals(sbustamentoInformazioniProtocolloRisposta)) {
						sbustamentoInformazioniProtocolloRisposta = CostantiConfigurazione.ABILITATO.toString();
					}
				}
				
				
				if (tipoauthSA == null &&
					credenziali != null){
					if(credenziali.getTipo()!=null)
						tipoauthSA = credenziali.getTipo().toString();
					utenteSA = credenziali.getUser();
					passwordSA = credenziali.getPassword();
					if(tipoauthSA!=null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(tipoauthSA)){
						tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
					}
					else if(tipoauthSA!=null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoauthSA)){
						tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
						multipleApiKey = credenziali.isAppId() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
						appId = credenziali.getUser();
						apiKey = credenziali.getPassword();
					}
					principalSA = credenziali.getUser();
					tokenClientIdSA = credenziali.getUser();
					tokenPolicySA = credenziali.getTokenPolicy();
					
					tokenWithHttpsEnabledByConfigSA = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoauthSA) && StringUtils.isNotEmpty(tokenClientIdSA);
					if(tokenPolicySA!=null && !StringUtils.isEmpty(tokenPolicySA) && !CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO.equals(tokenPolicySA) &&
						dominio!=null && SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE.equals(dominio) && 
								nomeProtocollo!=null && saCore.isProfiloModIPA(nomeProtocollo)) {
						tokenByPDND=saCore.isPolicyGestioneTokenPDND(tokenPolicySA);
						tokenModiPDNDOauth=!tokenWithHttpsEnabledByConfigSA;
					}
					
					if((tokenWithHttpsEnabledByConfigSA || tokenModiPDNDOauth)) {
						if(!tokenByPDND && saCore.isPolicyGestioneTokenPDND(tokenPolicySA)) {
							tokenPolicySA=null;
						}
						else if(tokenByPDND && !saCore.isPolicyGestioneTokenPDND(tokenPolicySA)) {
							tokenPolicySA = saCore.getDefaultPolicyGestioneTokenPDND();
						}
					}
					
					if(credenziali.getCertificate() != null) {
						tipoCredenzialiSSLFileCertificato.setValue(credenziali.getCertificate());
						tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
						tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
						
						try {
							Certificate cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
							tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
							tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
							tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
							tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
							tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
							tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
							tipoCredenzialiSSLAliasCertificatoNotBefore = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotBefore());
							tipoCredenzialiSSLAliasCertificatoNotAfter = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotAfter());
						}catch(UtilsException e) {
							pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
							tipoCredenzialiSSLAliasCertificato = "";
							tipoCredenzialiSSLAliasCertificatoSubject= "";
							tipoCredenzialiSSLAliasCertificatoIssuer= "";
							tipoCredenzialiSSLAliasCertificatoType= "";
							tipoCredenzialiSSLAliasCertificatoVersion= "";
							tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
							tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
							tipoCredenzialiSSLAliasCertificatoNotBefore= "";
							tipoCredenzialiSSLAliasCertificatoNotAfter = "";
						}
					} else {
						subjectSA = credenziali.getSubject();
						issuerSA = credenziali.getIssuer();
						tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE;
						tipoCredenzialiSSLConfigurazioneManualeSelfSigned = ( subjectSA != null && subjectSA.equals(issuerSA)) ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
					}
				}
				if (tipoauthSA == null) {
					tipoauthSA = ConnettoriCostanti.DEFAULT_AUTENTICAZIONE_TIPO;
				}

				if(ruoloFruitore==null){
					ruoloFruitore=sa.getTipologiaFruizione();
				}
				if(ruoloErogatore==null){
					ruoloErogatore=sa.getTipologiaErogazione();
				}
				
				if(tipoSA == null) {
					tipoSA = sa.getTipo();
					useAsClient = sa.isUseAsClient();
				}
				
				if(tipoSA == null)
					tipoSA = "";
				
				
				if (sbustamento == null &&
					is.getSbustamentoSoap()!=null) {
					sbustamento = is.getSbustamentoSoap().toString();
				}
				if (sbustamentoInformazioniProtocolloRichiesta == null &&
					is.getSbustamentoInformazioniProtocollo()!=null) {
					sbustamentoInformazioniProtocolloRichiesta = is.getSbustamentoInformazioniProtocollo().toString();
				}
				if (getmsg == null){
					if(is.getGetMessage()!=null) {
						getmsg = is.getGetMessage().toString();
					}
					if(!integrationManagerEnabled && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
						// faccio vedere I.M. anche con interfaccia standard
						integrationManagerEnabled = true;
					}
				}
				
				if (invrifRichiesta == null) {
					if(is.getInvioPerRiferimento()!=null)
						invrifRichiesta = is.getInvioPerRiferimento().toString();
					if ((invrifRichiesta == null) || "".equals(invrifRichiesta)) {
						invrifRichiesta = CostantiConfigurazione.DISABILITATO.toString();
					}
				}
				if (risprif == null) {
					if(is.getRispostaPerRiferimento()!=null)
						risprif = is.getRispostaPerRiferimento().toString();
					if ((risprif == null) || "".equals(risprif)) {
						risprif = CostantiConfigurazione.DISABILITATO.toString();
					}
				}
				
				if ((tipoauthRichiesta == null) && (is != null) && is.getAutenticazione()!=null) {
					tipoauthRichiesta = is.getAutenticazione().getValue();
				}
				if ((user == null) && (cis != null)) {
					user = cis.getUser();
					password = cis.getPassword();
				}
				if (endpointtype == null) {
					if ((connis.getCustom()!=null && connis.getCustom()) && !connis.getTipo().equals(TipiConnettore.HTTPS.toString())) {
						endpointtype = TipiConnettore.CUSTOM.toString();
						tipoconn = connis.getTipo();
					} else
						endpointtype = connis.getTipo();
				}
				if(endpointtype==null){
					endpointtype=TipiConnettore.DISABILITATO.toString();
				}

				Map<String, String> props = null;
				if(connis!=null)
					props = connis.getProperties();
				
				if(connettoreDebug==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_DEBUG);
					if(v!=null){
						if("true".equals(v)){
							connettoreDebug = Costanti.CHECK_BOX_ENABLED;
						}
						else{
							connettoreDebug = Costanti.CHECK_BOX_DISABLED;
						}
					}
				}
				
				if(proxyEnabled==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_PROXY_TYPE);
					if(v!=null && !"".equals(v)){
						proxyEnabled = Costanti.CHECK_BOX_ENABLED_TRUE;
						
						// raccolgo anche altre proprietà
						v = props.get(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
						if(v!=null && !"".equals(v)){
							proxyHostname = v.trim();
						}
						v = props.get(CostantiDB.CONNETTORE_PROXY_PORT);
						if(v!=null && !"".equals(v)){
							proxyPort = v.trim();
						}
						v = props.get(CostantiDB.CONNETTORE_PROXY_USERNAME);
						if(v!=null && !"".equals(v)){
							proxyUsername = v.trim();
						}
						v = props.get(CostantiDB.CONNETTORE_PROXY_PASSWORD);
						if(v!=null && !"".equals(v)){
							proxyPassword = v.trim();
						}
					}
				}
				
				if(tempiRispostaEnabled == null ||
						tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) 
						|| 
						tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) 
						|| 
						tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ){
					
					ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
					ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
					
					if( props!=null ) {
						if(tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) ) {
							String v = props.get(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
							if(v!=null && !"".equals(v)){
								tempiRispostaConnectionTimeout = v.trim();
								tempiRispostaEnabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaErogazione().getConnectionTimeout().intValue()+"";
							}
						}
							
						if(tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) ) {
							String v = props.get(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
							if(v!=null && !"".equals(v)){
								tempiRispostaReadTimeout = v.trim();
								tempiRispostaEnabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRispostaReadTimeout = configGenerale.getTempiRispostaErogazione().getReadTimeout().intValue()+"";
							}
						}
						
						if(tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ) {
							String v = props.get(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
							if(v!=null && !"".equals(v)){
								tempiRispostaTempoMedioRisposta = v.trim();
								tempiRispostaEnabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaErogazione().getTempoMedioRisposta().intValue()+"";
							}
						}
					}
					else {
						if(tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) ) {
							tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaErogazione().getConnectionTimeout().intValue()+"";
						}
						if(tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) ) {
							tempiRispostaReadTimeout = configGenerale.getTempiRispostaErogazione().getReadTimeout().intValue()+"";
						}
						if(tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ) {
							tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaErogazione().getTempoMedioRisposta().intValue()+"";
						}
					}
				}
				
				if(transferMode==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
					if(v!=null && !"".equals(v)){
						
						transferMode = v.trim();
						
						if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transferMode)){
							// raccolgo anche altra proprietà correlata
							v = props.get(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
							if(v!=null && !"".equals(v)){
								transferModeChunkSize = v.trim();
							}
						}
						
					}
				}
				
				if(redirectMode==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
					if(v!=null && !"".equals(v)){
						
						if("true".equalsIgnoreCase(v.trim()) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v.trim())){
							redirectMode = CostantiConfigurazione.ABILITATO.getValue();
						}
						else{
							redirectMode = CostantiConfigurazione.DISABILITATO.getValue();
						}					
						
						if(CostantiConfigurazione.ABILITATO.getValue().equals(redirectMode)){
							// raccolgo anche altra proprietà correlata
							v = props.get(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
							if(v!=null && !"".equals(v)){
								redirectMaxHop = v.trim();
							}
						}
						
					}
				}
				
				if(httpImpl==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_HTTP_IMPL);
					if(v!=null && !"".equals(v)){
						
						httpImpl = v.trim();
						
					}
				}
				
				opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(saHelper, transferMode, redirectMode, httpImpl);
				
				if(tokenPolicy==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_TOKEN_POLICY);
					if(v!=null && !"".equals(v)){
						tokenPolicy = v;
						autenticazioneToken = true;
					}
				}
				
				autenticazioneHttp = saHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
				
				if(autenticazioneApiKey==null || StringUtils.isEmpty(autenticazioneApiKey)) {
					for (int i = 0; i < connis.sizePropertyList(); i++) {
						Property singlecp = cp.get(i);
						if (singlecp.getNome().equals(CostantiDB.CONNETTORE_APIKEY_HEADER)) {
							apiKeyHeader = singlecp.getValore();
						}
						else if (singlecp.getNome().equals(CostantiDB.CONNETTORE_APIKEY)) {
							apiKeyValue = singlecp.getValore();
						}
						else if (singlecp.getNome().equals(CostantiDB.CONNETTORE_APIKEY_APPID_HEADER)) {
							appIdHeader = singlecp.getValore();
						}
						else if (singlecp.getNome().equals(CostantiDB.CONNETTORE_APIKEY_APPID)) {
							appIdValue = singlecp.getValore();
						}
					}
					
					autenticazioneApiKey = saHelper.getAutenticazioneApiKey(autenticazioneApiKey, endpointtype, apiKeyValue);
					if(ServletUtils.isCheckBoxEnabled(autenticazioneApiKey)) {
						useOAS3Names = saHelper.isAutenticazioneApiKeyUseOAS3Names(apiKeyHeader, appIdHeader);
						useAppId = saHelper.isAutenticazioneApiKeyUseAppId(appIdValue);
					}
					else {
						apiKeyValue=null;
						apiKeyHeader=null;
						appIdHeader=null;
						appIdValue=null;
					}
				}
				
				for (int i = 0; i < connis.sizePropertyList(); i++) {
					Property singlecp = cp.get(i);
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_HTTP_LOCATION) &&
						url == null) {
						url = singlecp.getValore();
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_NOME) &&
						nomeCodaJMS == null) {
						nomeCodaJMS = singlecp.getValore();
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_TIPO) &&
						tipoCodaJMS == null) {
						tipoCodaJMS = singlecp.getValore();
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY) &&
						connfact == null) {
						connfact = singlecp.getValore();
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_SEND_AS) &&
						sendas == null) {
						sendas = singlecp.getValore();
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL) &&
						initcont == null) {
						initcont = singlecp.getValore();
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG) &&
						urlpgk == null) {
						urlpgk = singlecp.getValore();
					}
					if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL) &&
						provurl == null) {
						provurl = singlecp.getValore();
					}
				}

				if (httpstipologia == null) {
					httpsurl = props.get(CostantiDB.CONNETTORE_HTTPS_LOCATION);
					httpstipologia = props.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE);
					httpshostverifyS = props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER);
					if(httpshostverifyS!=null){
						httpshostverify = Boolean.valueOf(httpshostverifyS);
					}
					httpsTrustVerifyCertS = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_ALL_CERTS);
					if(httpsTrustVerifyCertS!=null){
						httpsTrustVerifyCert = !Boolean.valueOf(httpsTrustVerifyCertS);
					}
					else {
						httpsTrustVerifyCert = true; // backward compatibility
					}
					httpspath = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
					httpstipo = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE);
					httpspwd = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
					httpsalgoritmo = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
					httpspwdprivatekeytrust = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
					httpspathkey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
					httpstipokey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
					httpspwdkey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
					httpspwdprivatekey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
					httpsalgoritmokey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
					httpsKeyAlias = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_ALIAS);
					httpsTrustStoreCRLs = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_CRLS);
					httpsTrustStoreOCSPPolicy = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
					httpsKeyStoreBYOKPolicy = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_BYOK_POLICY);
					if (httpspathkey == null) {
						httpsstato = false;
						httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
					} else {
						httpsstato = true;
						if (httpspathkey.equals(httpspath) &&
								httpstipokey.equals(httpstipo) &&
								(
									(httpspwdkey!=null && httpspwdkey.equals(httpspwd))
									||
									(httpspwdkey==null && httpspwd==null)
								)
							)
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						else
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;
					}
				}
				
				// default
				if(httpsalgoritmo==null || "".equals(httpsalgoritmo)){
					httpsalgoritmo = TrustManagerFactory.getDefaultAlgorithm();
				}
				if(httpsalgoritmokey==null || "".equals(httpsalgoritmokey)){
					httpsalgoritmokey = KeyManagerFactory.getDefaultAlgorithm();
				}
				if(httpstipologia==null || "".equals(httpstipologia)){
					httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
				}
				if(httpshostverifyS==null || "".equals(httpshostverifyS)){
					httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
					httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
				}
				if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
					httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;
					httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
				}
				
				// file
				if(responseInputMode==null && props!=null){
					
					requestOutputFileName = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);	
					requestOutputFileNamePermissions = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_PERMISSIONS);	
					requestOutputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);
					requestOutputFileNameHeadersPermissions = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS_PERMISSIONS);	
					String v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
					if(v!=null && !"".equals(v) &&
						("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) )
						){
						requestOutputParentDirCreateIfNotExists = Costanti.CHECK_BOX_ENABLED_TRUE;
					}					
					v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE);
					if(v!=null && !"".equals(v) &&
						("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) )
						){
						requestOutputOverwriteIfExists = Costanti.CHECK_BOX_ENABLED_TRUE;
					}	
					
					v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_MODE);
					if(v!=null && !"".equals(v) &&
						("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) )
						){
						responseInputMode = CostantiConfigurazione.ABILITATO.getValue();
					}
					if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){						
						responseInputFileName = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE);
						responseInputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS);
						v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ);
						if(v!=null && !"".equals(v) &&
							("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) )
							){
							responseInputDeleteAfterRead = Costanti.CHECK_BOX_ENABLED_TRUE;
						}						
						responseInputWaitTime = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);						
					}
					
				}
				
				// Inizializzazione delle properties da db al primo accesso alla pagina
				if(protocolPropertiesSet == null){
					ProtocolPropertiesUtils.mergeProtocolPropertiesConfig(protocolProperties, oldProtocolPropertyList, consoleOperationType);
				}
				
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				consoleDynamicConfiguration.updateDynamicConfigServizioApplicativo(consoleConfiguration, consoleOperationType, saHelper, protocolProperties, 
						registryReader, configRegistryReader, oldIdServizioApplicativo); 
				
				dati = saHelper.addServizioApplicativoToDati(dati, oldNome, nomeParameter, descrizione, tipoENomeSoggetto, fault, 
						TipoOperazione.CHANGE, idServizioApplicativoLong, contaListe,null,null,provider,dominio,
						utenteSA,passwordSA,subjectSA,principalSA,tipoauthSA,faultactor,genericfault,prefixfault,invrifRisposta,
						sbustamentoInformazioniProtocolloRisposta,
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,id,nomeProtocollo,
						ruoloFruitore,ruoloErogatore,
						sbustamento, sbustamentoInformazioniProtocolloRichiesta, getmsg,
						invrifRichiesta, risprif,
						endpointtype, autenticazioneHttp, url, nomeCodaJMS, tipoCodaJMS,
						user, password, initcont, urlpgk,
						provurl, connfact, sendas, 
						httpsurl, httpstipologia, httpshostverify,
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey,
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						tipoconn, connettoreDebug,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						nomeProtocollo, null, listExtendedConnettore,tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSA,tipoCredenzialiSSLWizardStep,
						changepwd,
						multipleApiKey, appId, apiKey,
						autenticazioneToken,tokenPolicy,tipoSA, useAsClient,
						integrationManagerEnabled, 
						visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd,
						tokenPolicySA, tokenClientIdSA, tokenWithHttpsEnabledByConfigSA,
						autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
						connettoreStatusParams);

				// aggiunta campi custom
				dati = saHelper.addProtocolPropertiesToDatiConfig(dati, consoleConfiguration,consoleOperationType, protocolProperties,oldProtocolPropertyList,propertiesProprietario);
			
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			StringBuilder sbWarningCheck = new StringBuilder();
			boolean isOk = saHelper.servizioApplicativoCheckData(TipoOperazione.CHANGE, null, idProv, ruoloFruitore, ruoloErogatore,
					listExtendedConnettore, sa, sbWarningCheck);
			
			// updateDynamic
			if(isOk) {
				consoleDynamicConfiguration.updateDynamicConfigServizioApplicativo(consoleConfiguration, consoleOperationType, saHelper, protocolProperties, 
						registryReader, configRegistryReader, oldIdServizioApplicativo); 
			}
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					saHelper.validaProtocolProperties(consoleConfiguration, consoleOperationType, protocolProperties);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}

			// Valido i parametri custom se ho gia' passato tutta la validazione prevista
			if(isOk){
				try{
					//validazione campi dinamici
					consoleDynamicConfiguration.validateDynamicConfigServizioApplicativo(consoleConfiguration, consoleOperationType, saHelper, protocolProperties, 
							registryReader, configRegistryReader, oldIdServizioApplicativo);   
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}
			
			if (!isOk) {
				
				// setto la barra del titolo
				
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(labelApplicativiDi + tipoENomeSoggetto,
									ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST,
									new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)),								
									new Parameter(oldNome, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST),
							new Parameter(oldNome, null)
							);
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				consoleDynamicConfiguration.updateDynamicConfigServizioApplicativo(consoleConfiguration, consoleOperationType, saHelper, protocolProperties, 
						registryReader, configRegistryReader, oldIdServizioApplicativo); 
				
				dati = saHelper.addServizioApplicativoToDati(dati, oldNome, nomeParameter, descrizione, tipoENomeSoggetto, fault, 
						TipoOperazione.CHANGE, idServizioApplicativoLong, contaListe,null,null,provider,dominio,
						utenteSA,passwordSA,subjectSA,principalSA,tipoauthSA,faultactor,genericfault,prefixfault,invrifRisposta,
						sbustamentoInformazioniProtocolloRisposta,
						ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,id,nomeProtocollo,
						ruoloFruitore,ruoloErogatore,
						sbustamento, sbustamentoInformazioniProtocolloRichiesta, getmsg,
						invrifRichiesta, risprif,
						endpointtype, autenticazioneHttp, url, nomeCodaJMS, tipoCodaJMS,
						user, password, initcont, urlpgk,
						provurl, connfact, sendas, 
						httpsurl, httpstipologia, httpshostverify,
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey,
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						tipoconn, connettoreDebug,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						nomeProtocollo, null, listExtendedConnettore,tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
						tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
						tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
						tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
						tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSA,tipoCredenzialiSSLWizardStep,
						changepwd,
						multipleApiKey, appId, apiKey,
						autenticazioneToken,tokenPolicy,tipoSA, useAsClient,
						integrationManagerEnabled, 
						visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd,
						tokenPolicySA, tokenClientIdSA, tokenWithHttpsEnabledByConfigSA,
						autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
						connettoreStatusParams);

				// aggiunta campi custom
				dati = saHelper.addProtocolPropertiesToDatiConfig(dati, consoleConfiguration,consoleOperationType, protocolProperties,oldProtocolPropertyList,propertiesProprietario);
							
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.CHANGE());
			}

			
			if(!oldNome.equals(nomeParameter)) {
				sa.setOldIDServizioApplicativoForUpdate(oldIdServizioApplicativo);
				sa.setNome(nomeParameter);
			}
			
			sa.setDescrizione(descrizione);
			
			if(isApplicativiServerEnabled && tipoSA.equals(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER)) {
				tipoauthSA = ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC;
				modificataTipoAutenticazione = false;
			}
			
			if(ruoloFruitore==null){
				ruoloFruitore = TipologiaFruizione.DISABILITATO.getValue();
			}
			if(ruoloErogatore==null){				
				if(sa.getTipologiaErogazione()!=null){
					ruoloErogatore = sa.getTipologiaErogazione();
				}else{
					ruoloErogatore = TipologiaErogazione.DISABILITATO.getValue();
				}
			}
			sa.setTipologiaFruizione(ruoloFruitore);
			sa.setTipologiaErogazione(ruoloErogatore);
			sa.setTipo(tipoSA);
			sa.setUseAsClient(useAsClient);
			
			
			// *** Invocazione Porta ***
			
			boolean secret = false;
			String secretPassword  = null;
			String secretUser = null;
			boolean secretAppId = false;
			
			boolean showInvocazionePortaStep1 = ( (interfacciaAvanzata && !isApplicativiServerEnabled) || !TipologiaFruizione.DISABILITATO.getValue().equals(ruoloFruitore));
			boolean showInvocazionePortaStep2 = !showInvocazionePortaStep1 && (!saHelper.isModalitaCompleta() && TipologiaFruizione.DISABILITATO.getValue().equals(ruoloFruitore));
			
/**			if(isApplicativiServerEnabled) {
//				showInvocazionePortaStep1 = showInvocazionePortaStep1 && ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT.equals(tipoSA);
//				if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(tipoSA)) {
//					showInvocazionePortaStep2 = true;
//				}
//			}*/

			if(showInvocazionePortaStep1){
			
				if (ipge == null)
					ipge = new InvocazionePortaGestioneErrore();
				ipge.setFault(FaultIntegrazioneTipo.toEnumConstant(fault));
				if (fault.equals(ServiziApplicativiCostanti.SERVIZI_APPLICATIVI_FAULT_XML)) {
					ipge.setFaultActor("");
				} else {
					ipge.setFaultActor(faultactor);
				}
				ipge.setGenericFaultCode(StatoFunzionalita.toEnumConstant(genericfault));
				ipge.setPrefixFaultCode(prefixfault);
				if (ip == null)
					ip = new InvocazionePorta();
				ip.setGestioneErrore(ipge);
				if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
					if(ip.sizeCredenzialiList()>0) {
						ip.removeCredenziali(0);
					}
				}
				else {
					if(ip.getCredenzialiList()!=null) {
						ip.getCredenzialiList().clear();
					}
				}
				if(ip.getCredenzialiList()==null) {
					ip.setCredenzialiList(new ArrayList<>());
				}
				credenziali = new Credenziali();
				ip.getCredenzialiList().add(0, credenziali);
				credenziali.setTipo(CredenzialeTipo.toEnumConstant(tipoauthSA));
				

				ApiKey apiKeyGenerated = null;
				
				if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC)) {
					credenziali.setUser(utenteSA);
					credenziali.setPassword(passwordSA);

					if(ServletUtils.isCheckBoxEnabled(changepwd) || modificataTipoAutenticazione) {
						credenziali.setCertificateStrictVerification(false); // se è abilitata la cifratura, verrà impostata a true nel perform update
						if(saCore.isApplicativiPasswordEncryptEnabled()) {
							secret = true;
						}
					}
					else if(encryptOldPlainPwd) {
						secret = true;
					}
					else {
						credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
					}
				} 
				else if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoauthSA)){
					credenziali.setAppId(multipleApiKeysEnabled);
					credenziali.setUser(appId);
					credenziali.setPassword(apiKey);
					if(ServletUtils.isCheckBoxEnabled(changepwd) || modificataTipoAutenticazione) {
						credenziali.setCertificateStrictVerification(false); // se è abilitata la cifratura, verrà impostata a true nel perform update
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setNome(nomeParameter);
						idSA.setIdSoggettoProprietario(idSoggetto);
						if(multipleApiKeysEnabled) {
							apiKeyGenerated = saCore.newMultipleApiKey();
							if(!appIdModificabile) {
								appId = saCore.toAppId(nomeProtocollo, idSA, multipleApiKeysEnabled);
							}
						}
						else {
							appId = saCore.toAppId(nomeProtocollo, idSA, multipleApiKeysEnabled);
							apiKeyGenerated = saCore.newApiKey(nomeProtocollo, idSA);
						}
						credenziali.setUser(appId);
						credenziali.setPassword(apiKeyGenerated.getPassword());
						if(soggettiCore.isApplicativiPasswordEncryptEnabled()) {
							secret = true;
						}
					}
					else {
						credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
					}
				}
				else if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL)) {
					if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
						Certificate cSelezionato = null;
						
						// il certificato non e' cambiato 
						if(tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD)) {
							cSelezionato = ArchiveLoader.load(oldCredenziali.getCertificate());
						} else {
							if(tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.CER)) {
								cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
							}else {
								cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
							}
						}
						
						credenziali.setCnIssuer(cSelezionato.getCertificate().getIssuer().getCN());
						credenziali.setCnSubject(cSelezionato.getCertificate().getSubject().getCN()); 
						credenziali.setCertificate(cSelezionato.getCertificate().getCertificate().getEncoded());
						credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
						
						credenziali.setSubject(null);
						credenziali.setIssuer(null);
						
					} else { // configurazione manuale
						credenziali.setSubject(subjectSA);
						if(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLConfigurazioneManualeSelfSigned)) {
							credenziali.setIssuer(subjectSA);
						} else {
							credenziali.setIssuer(issuerSA);
						}
						
						credenziali.setCnIssuer(null);
						credenziali.setCnSubject(null);
						credenziali.setCertificate(null);
						credenziali.setCertificateStrictVerification(false);
					}
					
					if(tokenWithHttsSupportato && tokenWithHttpsEnabledByConfigSA) {
						credenziali.setTokenPolicy(tokenPolicySA);
						credenziali.setUser(tokenClientIdSA);
					}
				}
				else if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL)) {
					credenziali.setUser(principalSA);
				} 
				else if (tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN)) {
					credenziali.setTokenPolicy(tokenPolicySA);
					credenziali.setUser(tokenClientIdSA);
				}
				if(ip.getCredenzialiList()==null) {
					ip.setCredenzialiList(new ArrayList<>());
				}
				if(ip.getCredenzialiList().isEmpty()) {
					ip.getCredenzialiList().add(credenziali);
				}
				else {
					ip.getCredenzialiList().set(0,credenziali); // Sovrascrivo la credenziale principale
				}
				
				if(secret) {
					secretUser = credenziali.getUser();
					if (apiKeyGenerated!=null) {
						secretPassword = apiKeyGenerated.getApiKey();
					}
					else {
						secretPassword = credenziali.getPassword();
					}
					secretAppId = credenziali.isAppId();
				}
				
				if(interfacciaAvanzata) {
					if(credenziali.getTipo()!=null){
						sa.setTipologiaFruizione(TipologiaFruizione.NORMALE.getValue());
					}
					else{
						sa.setTipologiaFruizione(TipologiaFruizione.DISABILITATO.getValue());
					}
				}
				
				ip.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(sbustamentoInformazioniProtocolloRisposta));
				
				ip.setInvioPerRiferimento(StatoFunzionalita.toEnumConstant(invrifRisposta));
				
				sa.setInvocazionePorta(ip);

			}
			
			if(showInvocazionePortaStep2){
				
				InvocazionePorta invocazionePorta = new InvocazionePorta();
				credenziali = new Credenziali();
				
				if(CostantiConfigurazione.ABILITATO.equals(getmsg)){
					if (tipoauthSA==null || tipoauthSA.equals(ConnettoriCostanti.AUTENTICAZIONE_TIPO_NESSUNA)) {
						credenziali.setTipo(null);
					}else{
						credenziali.setTipo(CredenzialeTipo.toEnumConstant(tipoauthSA));
					}
					credenziali.setUser(utenteSA);
					if(principalSA!=null && !"".equals(principalSA)){
						credenziali.setUser(principalSA); // al posto di user
					}
					credenziali.setPassword(passwordSA);
					
					ApiKey apiKeyGenerated = null;
					
					if(tipoauthSA!=null && ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(tipoauthSA)){
						if(ServletUtils.isCheckBoxEnabled(changepwd) || modificataTipoAutenticazione) {
							credenziali.setCertificateStrictVerification(false); // se è abilitata la cifratura, verrà impostata a true nel perform update
							if(saCore.isApplicativiPasswordEncryptEnabled()) {
								secret = true;
							}
						}
						else if(encryptOldPlainPwd) {
							secret = true;
						}
						else {
							credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
						}
					}
					else if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoauthSA)){
						credenziali.setAppId(multipleApiKeysEnabled);
						credenziali.setUser(appId);
						credenziali.setPassword(apiKey);
						if(ServletUtils.isCheckBoxEnabled(changepwd) || modificataTipoAutenticazione) {
							credenziali.setCertificateStrictVerification(false); // se è abilitata la cifratura, verrà impostata a true nel perform update
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setNome(nomeParameter);
							idSA.setIdSoggettoProprietario(idSoggetto);
							if(multipleApiKeysEnabled) {
								apiKeyGenerated = saCore.newMultipleApiKey();
								if(!appIdModificabile) {
									appId = saCore.toAppId(nomeProtocollo, idSA, multipleApiKeysEnabled);
								}
							}
							else {
								appId = saCore.toAppId(nomeProtocollo, idSA, multipleApiKeysEnabled);
								apiKeyGenerated = saCore.newApiKey(nomeProtocollo, idSA);
							}
							credenziali.setUser(appId);
							credenziali.setPassword(apiKeyGenerated.getPassword());
							if(soggettiCore.isApplicativiPasswordEncryptEnabled()) {
								secret = true;
							}
						}
						else {
							credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
						}
					}
					else if(tipoauthSA !=null  && ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoauthSA)) {
						if(tipoCredenzialiSSLSorgente.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO)) {
							Certificate cSelezionato = null;
							
							// il certificato non e' cambiato 
							if(tipoCredenzialiSSLWizardStep.equals(ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD)) {
								cSelezionato = ArchiveLoader.load(oldCredenziali.getCertificate());
							} else {
								if(tipoCredenzialiSSLTipoArchivio.equals(ArchiveType.CER)) {
									cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
								}else {
									cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato.getValue(), tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLFileCertificatoPassword);
								}
							}
							
							credenziali.setCnIssuer(cSelezionato.getCertificate().getIssuer().getCN());
							credenziali.setCnSubject(cSelezionato.getCertificate().getSubject().getCN()); 
							credenziali.setCertificate(cSelezionato.getCertificate().getCertificate().getEncoded());
							credenziali.setCertificateStrictVerification(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLVerificaTuttiICampi));
						} else { // configurazione manuale
							credenziali.setSubject(subjectSA);
							if(ServletUtils.isCheckBoxEnabled(tipoCredenzialiSSLConfigurazioneManualeSelfSigned)) {
								credenziali.setIssuer(subjectSA);
							} else {
								credenziali.setIssuer(issuerSA);
							}
						}
						
						if(tokenWithHttsSupportato && tokenWithHttpsEnabledByConfigSA) {
							credenziali.setTokenPolicy(tokenPolicySA);
							credenziali.setUser(tokenClientIdSA);
						}
					}
					else if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL.equals(tipoauthSA)) {
						credenziali.setUser(principalSA);
					} 
					else if (ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN.equals(tipoauthSA)) {
						credenziali.setTokenPolicy(tokenPolicySA);
						credenziali.setUser(tokenClientIdSA);
					}
					
					if(secret) {
						secretUser = credenziali.getUser();
						if (apiKeyGenerated!=null) {
							secretPassword = apiKeyGenerated.getApiKey();
						}
						else {
							secretPassword = credenziali.getPassword();
						}
						secretAppId = credenziali.isAppId();
					}
				}
				else{
					credenziali.setTipo(null);
					credenziali.setUser(null);
					credenziali.setPassword(null);
					credenziali.setSubject(null);
				}
				invocazionePorta.addCredenziali(credenziali);
								
				InvocazionePortaGestioneErrore ipgeTmp = new InvocazionePortaGestioneErrore();
				ipgeTmp.setFault(FaultIntegrazioneTipo.SOAP);
				invocazionePorta.setGestioneErrore(ipgeTmp);
				
				invocazionePorta.setSbustamentoInformazioniProtocollo(StatoFunzionalita.ABILITATO);

				sa.setInvocazionePorta(invocazionePorta);				
			}
			
			
			
			
			// *** Invocazione Servizio ***
			
			boolean showInvocazioneServizio = interfacciaStandard && !TipologiaErogazione.DISABILITATO.getValue().equals(ruoloErogatore);
			if(isApplicativiServerEnabled) {
				showInvocazioneServizio = ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(tipoSA);
			}
			
			if(showInvocazioneServizio){
			
				if(sbustamento==null){
					is.setSbustamentoSoap(CostantiConfigurazione.DISABILITATO);
				}else{
					is.setSbustamentoSoap(StatoFunzionalita.toEnumConstant(sbustamento));
				}
				if(sbustamentoInformazioniProtocolloRichiesta==null){
					is.setSbustamentoInformazioniProtocollo(CostantiConfigurazione.ABILITATO);
				}else{
					is.setSbustamentoInformazioniProtocollo(StatoFunzionalita.toEnumConstant(sbustamentoInformazioniProtocolloRichiesta));
				}
				is.setGetMessage(StatoFunzionalita.toEnumConstant(getmsg));
				is.setInvioPerRiferimento(StatoFunzionalita.toEnumConstant(invrifRichiesta));
				is.setRispostaPerRiferimento(StatoFunzionalita.toEnumConstant(risprif));
				if (tipoauthRichiesta!=null && tipoauthRichiesta.equals(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString())) {
					if (cis == null) {
						cis = new InvocazioneCredenziali();
					}
					cis.setUser(user);
					cis.setPassword(password);
					is.setCredenziali(cis);
					is.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
				}
				else if(endpointtype.equals(TipiConnettore.JMS.toString())){
					if(user!=null && password!=null){
						if (cis == null) {
							cis = new InvocazioneCredenziali();
						}
						cis.setUser(user);
						cis.setPassword(password);
					}
					is.setCredenziali(cis);
					is.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
				}
				else {
					is.setCredenziali(null);
					is.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
				}
				String oldConnT = connis.getTipo();
				if ((connis.getCustom()!=null && connis.getCustom()) && 
						!connis.getTipo().equals(TipiConnettore.HTTPS.toString()) && 
						!connis.getTipo().equals(TipiConnettore.FILE.toString()))
					oldConnT = TipiConnettore.CUSTOM.toString();
				saHelper.fillConnettore(connis, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
						nomeCodaJMS, tipoCodaJMS, user, password,
						initcont, urlpgk, provurl, connfact,
						sendas, httpsurl, httpstipologia, httpshostverify, 
						httpsTrustVerifyCert, httpspath, httpstipo,
						httpspwd, httpsalgoritmo, httpsstato,
						httpskeystore, httpspwdprivatekeytrust,
						httpspathkey, httpstipokey,
						httpspwdkey, httpspwdprivatekey,
						httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						tokenPolicy,
						apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
						connettoreStatusParams,
						listExtendedConnettore);
				is.setConnettore(connis);
				
				sa.setInvocazioneServizio(is);
			}
			else if(interfacciaStandard &&
					TipologiaErogazione.DISABILITATO.getValue().equals(ruoloErogatore)){
				is.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
				is.setCredenziali(cis);
				is.setGetMessage(CostantiConfigurazione.DISABILITATO);
				if(is.getConnettore()!=null){
					is.getConnettore().setTipo(TipiConnettore.DISABILITATO.toString());
				}
				sa.setInvocazioneServizio(is);					
			}
			
			//imposto properties custom
			sa.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesConfig(protocolProperties, consoleOperationType, oldProtocolPropertyList)); 
			
			// eseguo l'aggiornamento
			List<Object> listOggettiDaAggiornare = ServiziApplicativiUtilities.getOggettiDaAggiornare(saCore, oldIdServizioApplicativo, sa);
			saCore.performUpdateOperation(userLogin, saHelper.smista(), listOggettiDaAggiornare.toArray());

			saHelper.deleteBinaryParameters(tipoCredenzialiSSLFileCertificato); 
		
			int idLista = -1;
			if(!useIdSogg){
				idLista = Liste.SERVIZIO_APPLICATIVO;
			}
			else {
				idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;
			}
			if(!sa.getNome().equals(oldIdServizioApplicativo.getNome())) {
				ServletUtils.removeRisultatiRicercaFromSession(request, session, idLista);
			}
			
			// Messaggio 'Please Copy'
			if(secret) {
				saHelper.setSecretPleaseCopy(secretPassword, secretUser, secretAppId, tipoauthSA, OggettoDialogEnum.APPLICATIVO, sa.getNome());
			}
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			

			List<ServizioApplicativo> lista = null;

			if(!useIdSogg){
				ricerca = saHelper.checkSearchParameters(idLista, ricerca);

				boolean filtroSoggetto = false;
				if(saHelper.isSoggettoMultitenantSelezionato()) {
					List<String> protocolli = saCore.getProtocolli(request, session,false);
					if(protocolli!=null && protocolli.size()==1) { // dovrebbe essere l'unico caso in cui un soggetto multitenant è selezionato
						String protocollo = protocolli.get(0);
						filtroSoggetto = !saCore.isSupportatoAutenticazioneApplicativiEsterniErogazione(protocollo);  // devono essere fatti vedere anche quelli
					}
				}
				if(filtroSoggetto) {
					ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, saHelper.getSoggettoMultitenantSelezionato());
				}
				
				if(saCore.isVisioneOggettiGlobale(userLogin)){
					lista = saCore.soggettiServizioApplicativoList(null, ricerca);
				}else{
					lista = saCore.soggettiServizioApplicativoList(userLogin, ricerca);
				}
			}else {
				ricerca = saHelper.checkSearchParameters(idLista, ricerca);
				
				lista = saCore.soggettiServizioApplicativoList(ricerca,soggLong);
			}

			saHelper.prepareServizioApplicativoList(ricerca, lista, useIdSogg);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			if(sbWarningCheck!=null && sbWarningCheck.length()>0 &&
				(pd.getMessage()==null || StringUtils.isEmpty(pd.getMessage())) 
				){
				pd.setMessage(sbWarningCheck.toString(), MessageType.WARN);
			}
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.CHANGE());
		}
	}
}
