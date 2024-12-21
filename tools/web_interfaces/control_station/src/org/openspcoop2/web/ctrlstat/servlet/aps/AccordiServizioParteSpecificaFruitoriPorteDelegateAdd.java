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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoreStatusParams;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * AccordiServizioParteSpecificaPorteDelegateAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriPorteDelegateAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
						
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			
			// Preparo il menu
			apsHelper.makeMenu();
			
			String idAsps = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			String idFruizione = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);
			Long idFru = Long.parseLong(idFruizione);
			
			String idSoggFruitoreDelServizio = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO);
			@SuppressWarnings("unused")
			Long idSoggFru = Long.parseLong(idSoggFruitoreDelServizio);
			
			String [] azioni = apsHelper.getParameterValues(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONI);
			
			String nome = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String nomeGruppo = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_GRUPPO);
		
			String modeCreazione = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CREAZIONE);
			String modeCreazioneConnettore = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CREAZIONE_CONNETTORE);
			String identificazione = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_IDENTIFICAZIONE);
			String mappingPD = apsHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MAPPING);

			String controlloAccessiStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO);
			
			String fruizioneServizioApplicativo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUIZIONE_NOME_SA);
			String fruizioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			String fruizioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			String fruizioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			String fruizioneAutenticazionePrincipalTipo = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(fruizioneAutenticazionePrincipalTipo, false);
			List<String> fruizioneAutenticazioneParametroList = apsHelper.convertFromDataElementValue_parametroAutenticazioneList(fruizioneAutenticazione, fruizioneAutenticazionePrincipal);
			
			String fruizioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			String fruizioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			String fruizioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			String fruizioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			String fruizioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);

			String nomeSA = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);
			
			String gestioneToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			String gestioneTokenPolicy = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
			String gestioneTokenOpzionale = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE);
			String gestioneTokenValidazioneInput = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
			String gestioneTokenIntrospection = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
			String gestioneTokenUserInfo = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
			String gestioneTokenTokenForward = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
			
			String autenticazioneTokenIssuer = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
			String autenticazioneTokenClientId = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			String autenticazioneTokenSubject = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
			String autenticazioneTokenUsername = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
			String autenticazioneTokenEMail = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
			
			String autorizzazioneAutenticatiToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE_TOKEN);
			String autorizzazioneRuoliToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI_TOKEN);
			String autorizzazioneRuoliTipologiaToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA_TOKEN);
			String autorizzazioneRuoliMatchToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH_TOKEN);
			
			String autorizzazioneToken = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
			String autorizzazioneTokenOptions = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
			String autorizzazioneScope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			String scope = apsHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE);
			
			BinaryParameter allegatoXacmlPolicy = apsHelper.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			
			String identificazioneAttributiStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_STATO);
			String [] attributeAuthoritySelezionate = apsHelper.getParameterValues(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY);
			String attributeAuthorityAttributi = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_ATTRIBUTI_AUTHORITY_ATTRIBUTI);
			
			Properties parametersPOST = null;
			
			String endpointtype = apsHelper.readEndPointType();
			String tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			String autenticazioneHttp = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			String connettoreDebug = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			// token policy
			String autenticazioneTokenS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String tokenPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			
			// proxy
			String proxyEnabled = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxyHostname = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxyPort = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxyUsername = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxyPassword = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			String tempiRispostaEnabled = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRispostaConnectionTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRispostaReadTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRispostaTempoMedioRisposta = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			String transferMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transferModeChunkSize = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirectMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirectMaxHop = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String httpImpl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_HTTP_IMPL);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, transferMode, redirectMode, httpImpl);

			String user= null;
			String password =null;
			
			// http
			String url = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(endpointtype)){
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			// api key
			String autenticazioneApiKey = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_API_KEY);
			String apiKeyHeader = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_HEADER);
			if(apiKeyHeader==null || StringUtils.isEmpty(apiKeyHeader)) {
				apiKeyHeader = CostantiConnettori.DEFAULT_HEADER_API_KEY;
			}
			String apiKeyValue = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_VALUE);
			String appIdHeader = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_HEADER);
			if(appIdHeader==null || StringUtils.isEmpty(appIdHeader)) {
				appIdHeader = CostantiConnettori.DEFAULT_HEADER_APP_ID;
			}
			String appIdValue = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_VALUE);
			String useOAS3NamesTmp = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_NOMI_OAS);
			boolean useOAS3Names=true;
			if(useOAS3NamesTmp!=null && StringUtils.isNotEmpty(useOAS3NamesTmp)) {
				useOAS3Names = ServletUtils.isCheckBoxEnabled(useOAS3NamesTmp);
			}
			else {
				useOAS3Names = apsHelper.isAutenticazioneApiKeyUseOAS3Names(apiKeyHeader, appIdHeader);
			}
			String useAppIdTmp = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_USE_APP_ID);
			boolean useAppId=false;
			if(useAppIdTmp!=null && StringUtils.isNotEmpty(useAppIdTmp)) {
				useAppId = ServletUtils.isCheckBoxEnabled(useAppIdTmp);
			}
			else {
				useAppId = apsHelper.isAutenticazioneApiKeyUseAppId(appIdValue);
			}
			
			// jms
			String nomeCodaJms = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipoJms = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String tipoSendas = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			String httpsurl = url;
			String httpstipologia = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			String httpshostverifyS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			String httpsTrustVerifyCertS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			String httpspath = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			String httpstipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			String httpskeystore = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			String httpspwdprivatekeytrust = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			String httpspathkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			String httpstipokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			String httpspwdkey = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			String httpspwdprivatekey = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			String httpsalgoritmokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			String httpsKeyAlias = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			String httpsTrustStoreCRLs = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			String httpsTrustStoreOCSPPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
			String httpsKeyStoreBYOKPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_BYOK_POLICY);
			if(TipiConnettore.HTTPS.toString().equals(endpointtype)){
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			String requestOutputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			String requestOutputFileNamePermissions = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
			String requestOutputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			String requestOutputFileNameHeadersPermissions = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
			String requestOutputParentDirCreateIfNotExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			String requestOutputOverwriteIfExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			String responseInputMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			String responseInputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			String responseInputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			String responseInputDeleteAfterRead = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			String responseInputWaitTime = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);

			// stauts
			ConnettoreStatusParams connettoreStatusParams = ConnettoreStatusParams.fillFrom(apsHelper);
			
			boolean servizioApplicativoServerEnabled = false;
			String servizioApplicativoServer = null;
			
			boolean httpshostverify = false;
			if (httpshostverifyS != null && httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				httpshostverify = true;
			boolean httpsstato = false;
			if (httpsstatoS != null && httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				httpsstato = true;

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;
			
			boolean forceEnableConnettore = false;
			if( (!apsHelper.isModalitaCompleta())) {
				forceEnableConnettore = true;
			}

			boolean postBackViaPost = true;
			
			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_PORTA_APPLICATIVA_ADD, apsHelper, 
							parametersPOST, (endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro

			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteDelegateCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteDelegateCore);
			int idServizio = Integer.parseInt(idAsps);
			AccordoServizioParteSpecifica asps  =apsCore.getAccordoServizioParteSpecifica(idServizio);
			IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			IDSoggetto idSoggettoFruitore = new IDSoggetto();
			String tipoSoggettoFruitore = null;
			String nomeSoggettoFruitore = null;
			if(apsCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggettoFruitore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggFruitoreDelServizio));
				tipoSoggettoFruitore = soggettoFruitore.getTipo();
				nomeSoggettoFruitore = soggettoFruitore.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggettoFruitore = soggettiCore.getSoggetto(Integer.parseInt(idSoggFruitoreDelServizio));
				tipoSoggettoFruitore = soggettoFruitore.getTipo();
				nomeSoggettoFruitore = soggettoFruitore.getNome();
			}
			idSoggettoFruitore.setTipo(tipoSoggettoFruitore);
			idSoggettoFruitore.setNome(nomeSoggettoFruitore);
		
			AccordiServizioParteSpecificaFruitoriPorteDelegateMappingInfo mappingInfo = AccordiServizioParteSpecificaUtilities.getMappingInfo(mappingPD, idSoggettoFruitore, asps, apsCore);
			MappingFruizionePortaDelegata mappingSelezionato = mappingInfo.getMappingSelezionato();
			MappingFruizionePortaDelegata mappingDefault = mappingInfo.getMappingDefault();
			String mappingLabel = mappingInfo.getMappingLabel();
			String[] listaMappingLabels = mappingInfo.getListaMappingLabels();
			String[] listaMappingValues = mappingInfo.getListaMappingValues();
			List<String> azioniOccupate = mappingInfo.getAzioniOccupate();
			String nomeNuovaConfigurazione = mappingInfo.getNomeNuovaConfigurazione();
						
			AccordoServizioParteComuneSintetico as = null;
			ServiceBinding serviceBinding = null;
			IDAccordo idAccordo = null;
			String protocollo = null;
			String portType = null;
			if (asps != null) {
				idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				as = apcCore.getAccordoServizioSintetico(idAccordo);
				serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
				protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
				portType = asps.getPortType();
			}

			boolean forceHttps = false;
			boolean forceHttpsClient = false;
			boolean forcePDND = false;
			boolean forceOAuth = false;
			if(idAccordo!=null && apsHelper.isProfiloModIPA(protocollo)) {
				forceHttps = apsHelper.forceHttpsProfiloModiPA();
				
				BooleanNullable forceHttpsClientWrapper = BooleanNullable.NULL(); 
				BooleanNullable forcePDNDWrapper = BooleanNullable.NULL(); 
				BooleanNullable forceOAuthWrapper = BooleanNullable.NULL(); 
				
				List<String> azioniList = null;
				if(azioni!=null && azioni.length>0) {
					azioniList = Arrays.asList(azioni);
				}
				apsHelper.readModIConfiguration(forceHttpsClientWrapper, forcePDNDWrapper, forceOAuthWrapper, 
						idAccordo,portType, 
						azioniList);
				
				if(forceHttpsClientWrapper.getValue()!=null) {
					forceHttpsClient = forceHttpsClientWrapper.getValue().booleanValue();
				}
				if(forcePDNDWrapper.getValue()!=null) {
					forcePDND = forcePDNDWrapper.getValue().booleanValue();
				}
				if(forceOAuthWrapper.getValue()!=null) {
					forceOAuth = forceOAuthWrapper.getValue().booleanValue();
				}
				
			}
			
			// Prendo le azioni  disponibili
			boolean addTrattinoSelezioneNonEffettuata = false;
			int sogliaAzioni = addTrattinoSelezioneNonEffettuata ? 1 : 0;
			Map<String,String> azioniS = porteDelegateCore.getAzioniConLabel(asps, as, addTrattinoSelezioneNonEffettuata, true, azioniOccupate);
			String[] azioniDisponibiliList = null;
			String[] azioniDisponibiliLabelList = null;
			if(azioniS!=null && azioniS.size()>0) {
				azioniDisponibiliList = new String[azioniS.size()];
				azioniDisponibiliLabelList = new String[azioniS.size()];
				int i = 0;
				for (String string : azioniS.keySet()) {
					azioniDisponibiliList[i] = string;
					azioniDisponibiliLabelList[i] = azioniS.get(string);
					i++;
				}
			}
			
			String postBackElementName = apsHelper.getPostBackElementName();
			boolean initConnettore = false;
			// Controllo se ho modificato l'azione allora ricalcolo il nome
			if(postBackElementName != null &&
				postBackElementName.equalsIgnoreCase(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_CREAZIONE_CONNETTORE) &&
				// devo resettare il connettore
				ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
				initConnettore = true;
			}
			
			// ServiziApplicativi
			boolean escludiSAServer = saCore.isApplicativiServerEnabled(apsHelper);
			String filtroTipoSA = escludiSAServer ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT : null;
			
			List<String> saList = new ArrayList<>();
			saList.add("-");
			if(idSoggettoFruitore!=null){
				String auth = fruizioneAutenticazione;
				if(auth==null || "".equals(auth)){
					auth = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
				}
				CredenzialeTipo credenziale = CredenzialeTipo.toEnumConstant(auth);
				Boolean appId = null;
				if(CredenzialeTipo.APIKEY.equals(credenziale)) {
					ApiKeyState apiKeyState =  new ApiKeyState(null);
					appId = apiKeyState.appIdSelected;
				}
				boolean bothSslAndToken = false;
				String tokenPolicyNull = null;
				boolean tokenPolicyOR = false;
				
				List<IDServizioApplicativoDB> oldSilList = null;
				if(apsCore.isVisioneOggettiGlobale(userLogin)){
					oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoFruitore,null,
							credenziale, appId, filtroTipoSA, 
							bothSslAndToken, tokenPolicyNull, tokenPolicyOR);
				}
				else {
					oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoFruitore,userLogin,
							credenziale, appId, filtroTipoSA, 
							bothSslAndToken, tokenPolicyNull, tokenPolicyOR);
				}
				if(oldSilList!=null && !oldSilList.isEmpty()){
					for (int i = 0; i < oldSilList.size(); i++) {
						saList.add(oldSilList.get(i).getNome());		
					}
				}
			}

			List<Parameter> lstParm = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitoreDelServizio, idAsps, idFruizione);

			lstParm.add(ServletUtils.getParameterAggiungi());
			
			// Token Policy
			List<GenericProperties> gestorePolicyTokenList = confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN, null);
			String [] policyLabels = new String[gestorePolicyTokenList.size() + 1];
			String [] policyValues = new String[gestorePolicyTokenList.size() + 1];
			
			policyLabels[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			policyValues[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			
			for (int i = 0; i < gestorePolicyTokenList.size(); i++) {
			GenericProperties genericProperties = gestorePolicyTokenList.get(i);
				policyLabels[(i+1)] = genericProperties.getNome();
				policyValues[(i+1)] = genericProperties.getNome();
			}

			// AttributeAuthority
			List<GenericProperties> attributeAuthorityList = confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_ATTRIBUTE_AUTHORITY, null);
			String [] attributeAuthorityLabels = new String[attributeAuthorityList.size()];
			String [] attributeAuthorityValues = new String[attributeAuthorityList.size()];
			for (int i = 0; i < attributeAuthorityList.size(); i++) {
				GenericProperties genericProperties = attributeAuthorityList.get(i);
				attributeAuthorityLabels[i] = genericProperties.getNome();
				attributeAuthorityValues[i] = genericProperties.getNome();
			}

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (apsHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm); 

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				if(azioniDisponibiliList==null || azioniDisponibiliList.length <= sogliaAzioni) {
					pd.setMessage(porteDelegateHelper.getLabelAllAzioniConfigurate(serviceBinding), Costanti.MESSAGE_TYPE_INFO);
					pd.disableEditMode();
				}
				else {
				
					if(azioni == null) {
						azioni = new String[0];
					}
	
					if(nome == null) {
						// nome mapping calcolato in base al numero id configurazioni non di default presenti
						nome = nomeNuovaConfigurazione;
	
						if(identificazione == null)
							identificazione = PortaApplicativaAzioneIdentificazione.DELEGATED_BY.toString();
					}
					
					if(modeCreazione == null)
						modeCreazione = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA;
										
					if(modeCreazioneConnettore == null) {
						modeCreazioneConnettore = Costanti.CHECK_BOX_DISABLED;
					}
						
					if(mappingPD==null) {
						mappingPD = listaMappingValues[listaMappingValues.length-1]; // sono ordinati all'incontrario
					}
						
					if(fruizioneServizioApplicativo==null || "".equals(fruizioneServizioApplicativo))
						fruizioneServizioApplicativo = "-";
					if(fruizioneRuolo==null || "".equals(fruizioneRuolo))
						fruizioneRuolo = "-";
					if(fruizioneAutenticazione==null || "".equals(fruizioneAutenticazione))
						fruizioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					if(fruizioneAutorizzazione==null || "".equals(fruizioneAutorizzazione)){
						String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteDelegate();
						fruizioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
							fruizioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
							fruizioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						fruizioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
					}
					if(gestioneToken == null) {
						gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
						gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
						gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
						gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
						gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
						gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
						gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
						autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
						autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
						autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
						autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
						autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
					}
					if(scope ==null || "".equals(scope))
						scope = "-";
					if(autorizzazioneScope ==null)
						autorizzazioneScope = "";
					if(identificazioneAttributiStato==null) {
						identificazioneAttributiStato = StatoFunzionalita.DISABILITATO.getValue();
					}
					// solo in modalita' nuova
					if(initConnettore) {
						tipoconn = "";
						url = "";
						nomeCodaJms = "";
						user = "";
						password = "";
						initcont = "";
						urlpgk = "";
						provurl = "";
						connfact = "";
						httpsurl = "";
						httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
						httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
						httpshostverify = true;
						httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
						httpspath = "";
						httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
						httpspwd = "";
						httpsstato = false;
						httpskeystore = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DEFAULT;
						httpspwdprivatekeytrust = "";
						httpspathkey = "";
						httpstipokey =ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
						httpspwdkey = "";
						httpspwdprivatekey = "";
						
						if(endpointtype==null) {
							if(!apsHelper.isModalitaCompleta()) {
								endpointtype = TipiConnettore.HTTP.getNome();
							}
							else {
								endpointtype = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO;
							}
						}
												
						tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
						tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];

						autenticazioneHttp = apsHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);
						
						apiKeyHeader = null;
						apiKeyValue = null;
						appIdHeader = null;
						appIdValue = null;
												
						tempiRispostaEnabled=null;
						ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
						ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
						tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
						tempiRispostaReadTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
						tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";
							
					}
					
					// default (da inizializzare cmq)
					if (TipiConnettore.HTTPS.toString().equals(endpointtype)) {
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
					}
					
					// Devo cmq rileggere i valori se non definiti
					if(tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) 
							|| 
							tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) 
							|| 
							tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ){
						ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
						ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
						if(tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) ) {
							tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
						}
						if(tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) ) {
							tempiRispostaReadTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
						}
						if(tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ) {
							tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";
						}
					}
					
	
					dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, idSoggFruitoreDelServizio, null, null, 
							idFruizione, tipoSoggettoFruitore, nomeSoggettoFruitore, dati);
					dati = apsHelper.addConfigurazioneFruizioneToDati(TipoOperazione.ADD, dati, nome, nomeGruppo, azioni, azioniDisponibiliList, azioniDisponibiliLabelList, idAsps, idSoggettoFruitore,
							identificazione, asps, as, serviceBinding, modeCreazione, modeCreazioneConnettore, listaMappingLabels, listaMappingValues,
							mappingPD, mappingLabel, saList, nomeSA, 
							controlloAccessiStato,
							fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, fruizioneAutenticazionePrincipal, fruizioneAutenticazioneParametroList,
							true, fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, 
							fruizioneAutorizzazioneRuoli, fruizioneRuolo, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, fruizioneServizioApplicativo,
							gestioneToken, policyLabels, policyValues, 
							gestioneTokenPolicy, gestioneTokenOpzionale,
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
							autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
							autorizzazioneToken,autorizzazioneTokenOptions,
							autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
							identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
							autorizzazioneAutenticatiToken, 
							autorizzazioneRuoliToken, autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);
					
					if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
						dati = apsHelper.addEndPointToDati(dati, serviceBinding, connettoreDebug, endpointtype, autenticazioneHttp, 
								null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
								url, nomeCodaJms,
								tipoJms, user,
								password, initcont, urlpgk,
								provurl, connfact, tipoSendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.ADD, 
								httpsurl, httpstipologia, httpshostverify, 
								httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
								httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey,
								httpstipokey, httpspwdkey, 
								httpspwdprivatekey, httpsalgoritmokey,
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_ADD, null, null,
								null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
								tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
								opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
								requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
								requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								autenticazioneToken, tokenPolicy, forcePDND, forceOAuth,
								listExtendedConnettore, forceEnableConnettore,
								protocollo, forceHttps, forceHttpsClient, false, servizioApplicativoServerEnabled,servizioApplicativoServer, null,
								autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
								connettoreStatusParams,
								postBackViaPost);
					}
				}
					
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
						ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.configurazioneFruizioneCheckData(TipoOperazione.ADD, nome, nomeGruppo, azioni, asps, azioniOccupate,modeCreazione,null,true, mappingInfo);
			
			// controllo endpoint
			if(isOk && ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
				isOk = apsHelper.endPointCheckData(serviceBinding, protocollo, false,
						endpointtype, url, nome, tipoJms,
						user, password, initcont, urlpgk, provurl, connfact,
						tipoSendas, httpsurl, httpstipologia, httpshostverify,
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
						httpskeystore, httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						tipoconn,autenticazioneHttp,
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken, tokenPolicy,
						autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
						listExtendedConnettore,servizioApplicativoServerEnabled,servizioApplicativoServer);
			}
			
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm); 

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, idSoggFruitoreDelServizio, null, null, 
						idFruizione, tipoSoggettoFruitore, nomeSoggettoFruitore, dati);

				dati = apsHelper.addConfigurazioneFruizioneToDati(TipoOperazione.ADD, dati, nome, nomeGruppo, azioni, azioniDisponibiliList, azioniDisponibiliLabelList, idAsps, idSoggettoFruitore,
						identificazione, asps, as, serviceBinding, modeCreazione, modeCreazioneConnettore, listaMappingLabels, listaMappingValues,
						mappingPD, mappingLabel, saList, nomeSA, 
						controlloAccessiStato,
						fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, fruizioneAutenticazionePrincipal, fruizioneAutenticazioneParametroList,
						true, fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, 
						fruizioneAutorizzazioneRuoli, fruizioneRuolo, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch, fruizioneServizioApplicativo,
						gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazioneToken,autorizzazioneTokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
						identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
						autorizzazioneAutenticatiToken, 
						autorizzazioneRuoliToken, autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);
				
				if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
					dati = apsHelper.addEndPointToDati(dati, serviceBinding, connettoreDebug, endpointtype, autenticazioneHttp, 
							null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_INTERNO_PREFIX , 
							url, nomeCodaJms,
							tipoJms, user,
							password, initcont, urlpgk,
							provurl, connfact, tipoSendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,TipoOperazione.ADD, 
							httpsurl, httpstipologia, httpshostverify, 
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, 
							httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
							tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_PORTE_APPLICATIVE_ADD, null, null,
							null, null, null, null, null, null, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken, tokenPolicy, forcePDND, forceOAuth,
							listExtendedConnettore, forceEnableConnettore,
							protocollo, forceHttps, forceHttpsClient, false, servizioApplicativoServerEnabled,servizioApplicativoServer, null,
							autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
							connettoreStatusParams,
							postBackViaPost);
				}

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE, 
						ForwardParams.ADD());
			}

			if(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_NUOVA.equals(modeCreazione)
					&& CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO.equals(controlloAccessiStato)) {
				
				fruizioneAutenticazione = TipoAutenticazione.DISABILITATO.getValue();
				fruizioneAutenticazioneOpzionale = null;
				fruizioneAutenticazionePrincipal = null;
				fruizioneAutenticazioneParametroList = null;
				
				fruizioneAutorizzazione = TipoAutorizzazione.DISABILITATO.getValue();
				fruizioneAutorizzazioneAutenticati = null;
				fruizioneAutorizzazioneRuoli = null;
				fruizioneAutorizzazioneRuoliTipologia = null;
				fruizioneAutorizzazioneRuoliMatch = null;
				
			}

			
			AccordiServizioParteSpecificaUtilities.addAccordoServizioParteSpecificaPorteDelegate(
					mappingDefault,
					mappingSelezionato,
					nome, nomeGruppo, azioni, modeCreazione, modeCreazioneConnettore,
					endpointtype, tipoconn, autenticazioneHttp,
					connettoreDebug,
					url,
					nomeCodaJms, tipoJms, 
					initcont, urlpgk, provurl, connfact, tipoSendas, 
					user, password,
					httpsurl, httpstipologia, httpshostverify,
					httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
					httpsalgoritmo, httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey,
					httpspwdprivatekey, httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
					proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
					tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
					opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
					requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
					requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					autenticazioneToken, tokenPolicy,
					listExtendedConnettore,
					fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, fruizioneAutenticazionePrincipal, fruizioneAutenticazioneParametroList,
					fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch,
					fruizioneServizioApplicativo, fruizioneRuolo, 
					autorizzazioneAutenticatiToken, 
					autorizzazioneRuoliToken, autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken,
					autorizzazioneTokenOptions,
					autorizzazioneScope, scope, autorizzazioneScopeMatch,allegatoXacmlPolicy,
					gestioneToken, 
					gestioneTokenPolicy,  gestioneTokenOpzionale,  
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
					autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					idSoggettoFruitore, asps, 
					userLogin,
					apsCore, apsHelper,
					identificazioneAttributiStato, attributeAuthoritySelezionate, attributeAuthorityAttributi,
					apiKeyHeader, apiKeyValue, appIdHeader, appIdValue, connettoreStatusParams);
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_FRUIZIONE;

			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);

			List<MappingFruizionePortaDelegata> lista = apsCore.serviziFruitoriMappingList(idFru, idSoggettoFruitore , idServizio2, ricerca);
			
			apsHelper.serviziFruitoriMappingList(lista, idAsps, idSoggFruitoreDelServizio, idSoggettoFruitore, idFruizione, ricerca);
			
			// reset posizione tab
			if(!apsHelper.isModalitaCompleta())
				ServletUtils.setObjectIntoSession(request, session, "0", CostantiControlStation.PARAMETRO_ID_TAB);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			ForwardParams fwP = apsHelper.isModalitaCompleta() ? ForwardParams.ADD() : AccordiServizioParteSpecificaCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE, fwP);

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI_PORTE_DELEGATE,
					ForwardParams.ADD());
		}  
	}
}
