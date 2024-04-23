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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CredenzialeTipo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.ProtocolSubscription;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ApiKeyState;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
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
 * serviziFruitoriAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String superUser =  ServletUtils.getUserLoginFromSession(session);

		AccordiServizioParteSpecificaFruitoriAddStrutsBean strutsBean = new AccordiServizioParteSpecificaFruitoriAddStrutsBean();
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		strutsBean.consoleOperationType = ConsoleOperationType.ADD;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.ADD;

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			
			strutsBean.editMode = apsHelper.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);

			strutsBean.id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			strutsBean.idSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE);
			strutsBean.correlato = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);

			strutsBean.controlloAccessiStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO);
			
			strutsBean.fruizioneServizioApplicativo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUIZIONE_NOME_SA);
			strutsBean.fruizioneRuolo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			strutsBean.fruizioneAutenticazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			strutsBean.fruizioneAutenticazioneOpzionale = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			String fruizioneAutenticazionePrincipalTipo = apsHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			strutsBean.fruizioneAutenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(fruizioneAutenticazionePrincipalTipo, false);
			strutsBean.fruizioneAutenticazioneParametroList = apsHelper.convertFromDataElementValue_parametroAutenticazioneList(strutsBean.fruizioneAutenticazione, strutsBean.fruizioneAutenticazionePrincipal);
			strutsBean.fruizioneAutorizzazione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			strutsBean.fruizioneAutorizzazioneAutenticati = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			strutsBean.fruizioneAutorizzazioneRuoli = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			strutsBean.fruizioneAutorizzazioneRuoliTipologia = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			strutsBean.fruizioneAutorizzazioneRuoliMatch = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);
			
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
			
			strutsBean.endpointtype = apsHelper.readEndPointType();
			strutsBean.tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			strutsBean.autenticazioneHttp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);

			strutsBean.connettoreDebug = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);
			
			// token policy
			String autenticazioneTokenS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			strutsBean.autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			strutsBean.tokenPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			
			// proxy
			strutsBean.proxyEnabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			strutsBean.proxyHostname = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			strutsBean.proxyPort = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			strutsBean.proxyUsername = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			strutsBean.proxyPassword = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// tempi risposta
			strutsBean.tempiRispostaEnabled = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			strutsBean.tempiRispostaConnectionTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			strutsBean.tempiRispostaReadTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			strutsBean.tempiRispostaTempoMedioRisposta = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);
			
			// opzioni avanzate
			strutsBean.transferMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			strutsBean.transferModeChunkSize = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			strutsBean.redirectMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			strutsBean.redirectMaxHop = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			strutsBean.opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, strutsBean.transferMode, strutsBean.redirectMode);

			// http
			strutsBean.url = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL  );
			if(TipiConnettore.HTTP.toString().equals(strutsBean.endpointtype)){
				strutsBean.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				strutsBean.password = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			// api key
			strutsBean.autenticazioneApiKey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_API_KEY);
			strutsBean.apiKeyHeader = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_HEADER);
			if(strutsBean.apiKeyHeader==null || StringUtils.isEmpty(strutsBean.apiKeyHeader)) {
				strutsBean.apiKeyHeader = CostantiConnettori.DEFAULT_HEADER_API_KEY;
			}
			strutsBean.apiKeyValue = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_VALUE);
			strutsBean.appIdHeader = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_HEADER);
			if(strutsBean.appIdHeader==null || StringUtils.isEmpty(strutsBean.appIdHeader)) {
				strutsBean.appIdHeader = CostantiConnettori.DEFAULT_HEADER_APP_ID;
			}
			strutsBean.appIdValue = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_VALUE);
			String useOAS3NamesTmp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_NOMI_OAS);
			strutsBean.useOAS3Names=true;
			if(useOAS3NamesTmp!=null && StringUtils.isNotEmpty(useOAS3NamesTmp)) {
				strutsBean.useOAS3Names = ServletUtils.isCheckBoxEnabled(useOAS3NamesTmp);
			}
			else {
				strutsBean.useOAS3Names = apsHelper.isAutenticazioneApiKeyUseOAS3Names(strutsBean.apiKeyHeader, strutsBean.appIdHeader);
			}
			String useAppIdTmp = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_USE_APP_ID);
			strutsBean.useAppId=false;
			if(useAppIdTmp!=null && StringUtils.isNotEmpty(useAppIdTmp)) {
				strutsBean.useAppId = ServletUtils.isCheckBoxEnabled(useAppIdTmp);
			}
			else {
				strutsBean.useAppId = apsHelper.isAutenticazioneApiKeyUseAppId(strutsBean.appIdValue);
			}
			
			// jms
			strutsBean.nome = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			strutsBean.tipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			strutsBean.initcont = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			strutsBean.urlpgk = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			strutsBean.provurl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			strutsBean.connfact = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			strutsBean.sendas = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(strutsBean.endpointtype)){
				strutsBean.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				strutsBean.password = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			strutsBean.httpsurl = strutsBean.url;
			strutsBean.httpstipologia = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			strutsBean.httpshostverifyS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			String httpsTrustVerifyCertS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			strutsBean.httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			strutsBean.httpspath = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
			strutsBean.httpstipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			strutsBean.httpspwd = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			strutsBean.httpsalgoritmo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			strutsBean.httpsstatoS = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			strutsBean.httpskeystore = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE);
			strutsBean.httpspwdprivatekeytrust = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_STORE);
			strutsBean.httpspathkey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_LOCATION);
			strutsBean.httpstipokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_TYPE);
			strutsBean.httpspwdkey = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
			strutsBean.httpspwdprivatekey = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_PASSWORD_PRIVATE_KEY_KEYSTORE);
			strutsBean.httpsalgoritmokey = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
			strutsBean.httpsKeyAlias = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_ALIAS_PRIVATE_KEY_KEYSTORE);
			strutsBean.httpsTrustStoreCRLs = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_CRL);
			strutsBean.httpsTrustStoreOCSPPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
			if(TipiConnettore.HTTPS.toString().equals(strutsBean.endpointtype)){
				strutsBean.user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				strutsBean.password = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			
			// file
			strutsBean.requestOutputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME);
			strutsBean.requestOutputFileNamePermissions = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
			strutsBean.requestOutputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS);
			strutsBean.requestOutputFileNameHeadersPermissions = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
			strutsBean.requestOutputParentDirCreateIfNotExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
			strutsBean.requestOutputOverwriteIfExists = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE_NAME);
			strutsBean.responseInputMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_MODE);
			strutsBean.responseInputFileName = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME);
			strutsBean.responseInputFileNameHeaders = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_HEADERS);
			strutsBean.responseInputDeleteAfterRead = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_FILE_NAME_DELETE_AFTER_READ);
			strutsBean.responseInputWaitTime = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);


			strutsBean.statoPackage = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);

			strutsBean.wsdlimpler = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_EROGATORE);
			strutsBean.wsdlimplfru = apsHelper.getBinaryParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_WSDL_FRUITORE);

			if(apsHelper.isMultipart()){
				strutsBean.decodeRequestValidazioneDocumenti = true;
			}

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PddCore pddCore = new PddCore(apsCore);
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);

			if(ServletUtils.isEditModeInProgress(strutsBean.editMode)){
				// primo accesso alla servlet
				strutsBean.validazioneDocumenti = true;
				if (apsHelper.isModalitaAvanzata()) {
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(tmpValidazioneDocumenti!=null){
						if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
							strutsBean.validazioneDocumenti = true;
						}else{
							strutsBean.validazioneDocumenti = false;
						}
					}
				}
			}else{
				if(!strutsBean.decodeRequestValidazioneDocumenti){
					String tmpValidazioneDocumenti = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						strutsBean.validazioneDocumenti = true;
					}else{
						strutsBean.validazioneDocumenti = false;
					}
				}
			}

			strutsBean.httpshostverify = false;
			if (strutsBean.httpshostverifyS != null && strutsBean.httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				strutsBean.httpshostverify = true;
			strutsBean.httpsstato = false;
			if (strutsBean.httpsstatoS != null && strutsBean.httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				strutsBean.httpsstato = true;

			long idServizioLong = Long.parseLong(strutsBean.id);

			Boolean isConnettoreCustomUltimaImmagineSalvata = null;

			Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.FRUIZIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, apsHelper, 
							null, //this.parametersPOST, 
							(strutsBean.endpointtype==null), strutsBean.endpointtype); // uso endpointtype per capire se è la prima volta che entro

			// prendo l'id del soggetto erogatore lo propago
			// lo metto nel pd come campo hidden
			PageData oldPD = ServletUtils.getPageDataFromSession(request, session);
			pd.setHidden(oldPD.getHidden());

			String idSoggErogatore = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);

			boolean postBackViaPost = true;
			
			// Preparo il menu
			apsHelper.makeMenu();

			// Prendo nome e tipo dal db

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizioLong);
			String nomeservizio = asps.getNome();
			String tiposervizio = asps.getTipo();
			Integer versioneservizio = asps.getVersione();

			IDServizio idServizioObject = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			idServizioObject.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
			idServizioObject.setPortType(asps.getPortType());
			
			IDFruizione idFruizione = new IDFruizione();
			idFruizione.setIdServizio(idServizioObject);
			
			
			if(strutsBean.correlato == null){
				strutsBean.correlato = (TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio()) ?
						AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO :
							AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_NORMALE);
			}

			/**String profiloSoggettoFruitore = null;
			//if ((this.provider != null) && !this.provider.equals("")) {
			//	long idFruitore = Long.parseLong(this.provider);
			//	Soggetto soggetto = soggettiCore.getSoggettoRegistro(idFruitore);
			//	profiloSoggettoFruitore = soggetto.getVersioneProtocollo();
			//}
			//String profiloValue = profiloSoggettoFruitore;
			//if(this.profilo!=null && !"".equals(this.profilo) && !"-".equals(this.profilo)){
			//	profiloValue = this.profilo;
			//}*/

			String protocollo = apsCore.getProtocolloAssociatoTipoServizio(tiposervizio);
			List<String> versioniProtocollo = apsCore.getVersioniProtocollo(protocollo);
			List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);

			String tmpTitle = apsHelper.getLabelIdServizio(asps);

			// Soggetti fruitori
			// tutti i soggetti anche il soggetto attuale
			// tranne quelli già registrati come fruitori
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			List<Fruitore> fruList1 = apsCore.getSoggettiWithServizioNotFruitori(idServizioLong,true,null);
			Map<String, Fruitore> mapFruitori = new HashMap<>();
			List<String> keyFruitori = new ArrayList<>();
			if(fruList1!=null && !fruList1.isEmpty()){
				for (Fruitore fr : fruList1) {
					String key = fr.getTipo()+""+fr.getNome();
					if(!keyFruitori.contains(key)){
						keyFruitori.add(key);
						mapFruitori.put(key, fr);
					}
				}
			}
			if(!keyFruitori.isEmpty()){
				Collections.sort(keyFruitori);
			}
			
			List<String> soggettiListList = new ArrayList<>();
			List<String> soggettiListLabelList = new ArrayList<>();
			IDSoggetto idSoggettoSelected = null;
			IDSoggetto idSoggettoFirst = null;
			for (int i = 0; i < keyFruitori.size(); i++) {
				String tipoNome = keyFruitori.get(i);
				Fruitore fru = mapFruitori.get(tipoNome);
				if(tipiSoggettiCompatibiliAccordo.contains(fru.getTipo())){
					soggettiListList.add("" + fru.getId());
					soggettiListLabelList.add(apsHelper.getLabelNomeSoggetto(protocollo, fru.getTipo() , fru.getNome()));
					if(idSoggettoFirst==null) {
						idSoggettoFirst = new IDSoggetto(fru.getTipo(), fru.getNome());
					}
					if(strutsBean.idSoggettoFruitore!=null && !"".equals(strutsBean.idSoggettoFruitore)){
						long idProvider = Long.parseLong(strutsBean.idSoggettoFruitore);
						if(fru.getId()==idProvider){
							idSoggettoSelected = new IDSoggetto(fru.getTipo(), fru.getNome());
						}
					}
				}
			}
			if(!soggettiListList.isEmpty()){
				soggettiList = soggettiListList.toArray(new String[1]);
				soggettiListLabel = soggettiListLabelList.toArray(new String[1]);

				if(idSoggettoSelected==null &&
					idSoggettoFirst!=null){
					idSoggettoSelected = idSoggettoFirst; // prendo il primo soggetto se esiste
				}
			}

			idFruizione.setIdFruitore(idSoggettoSelected);

			// Versioni
			String[] versioniValues = new String[versioniProtocollo.size()+1];
			String[] versioniLabel = new String[versioniProtocollo.size()+1];
			versioniLabel[0] = "usa versione fruitore";
			versioniValues[0] = "-";
			for (int i = 0; i < versioniProtocollo.size(); i++) {
				String tmp = versioniProtocollo.get(i);
				versioniLabel[i+1] = tmp;
				versioniValues[i+1] = tmp;
			}


			// ServiziApplicativi
			boolean escludiSAServer = saCore.isApplicativiServerEnabled(apsHelper);
			String filtroTipoSA = escludiSAServer ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_CLIENT : null;
			
			List<String> saList = new ArrayList<>();
			saList.add("-");
			if(idSoggettoSelected!=null){
				String auth = strutsBean.fruizioneAutenticazione;
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
				String tokenPolicy = null;
				boolean tokenPolicyOR = false;
				
				List<IDServizioApplicativoDB> oldSilList = null;
				if(apsCore.isVisioneOggettiGlobale(superUser)){
					oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoSelected,null,
							credenziale, appId, filtroTipoSA, 
							bothSslAndToken, tokenPolicy, tokenPolicyOR);
				}
				else {
					oldSilList = saCore.soggettiServizioApplicativoList(idSoggettoSelected,superUser,
							credenziale, appId, filtroTipoSA, 
							bothSslAndToken, tokenPolicy, tokenPolicyOR);
				}
				if(oldSilList!=null && !oldSilList.isEmpty()){
					for (int i = 0; i < oldSilList.size(); i++) {
						saList.add(oldSilList.get(i).getNome());		
					}
				}

			}

			strutsBean.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			strutsBean.consoleDynamicConfiguration =  strutsBean.protocolFactory.createDynamicConfigurationConsole();
			strutsBean.registryReader = soggettiCore.getRegistryReader(strutsBean.protocolFactory); 
			strutsBean.configRegistryReader = soggettiCore.getConfigIntegrationReader(strutsBean.protocolFactory);
			
			// ID Accordo Null per default
			strutsBean.consoleConfiguration = strutsBean.consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleOperationType, apsHelper, 
					strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione  );
			strutsBean.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(strutsBean.consoleConfiguration, strutsBean.consoleOperationType);

			AccordoServizioParteComuneSintetico as = apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			
			boolean forceHttps = false;
			boolean forceHttpsClient = false;
			boolean forcePDND = false;
			boolean forceOAuth = false;
			if(apsHelper.isProfiloModIPA(protocollo)) {
				forceHttps = apsHelper.forceHttpsProfiloModiPA();
				
				BooleanNullable forceHttpsClientWrapper = BooleanNullable.NULL(); 
				BooleanNullable forcePDNDWrapper = BooleanNullable.NULL(); 
				BooleanNullable forceOAuthWrapper = BooleanNullable.NULL(); 
				
				apsHelper.readModIConfiguration(forceHttpsClientWrapper, forcePDNDWrapper, forceOAuthWrapper, 
						IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as), asps.getPortType(), 
						null);
				
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
			if(ServletUtils.isEditModeInProgress(strutsBean.editMode)){
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ strutsBean.id),
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggErogatore)
						));
				lstParm.add(ServletUtils.getParameterAggiungi());

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				if(soggettiListList.isEmpty()){

					pd.setMessage(AccordiServizioParteSpecificaCostanti.LABEL_AGGIUNTA_FRUITORI_COMPLETATA, Costanti.MESSAGE_TYPE_INFO);

					pd.disableEditMode();

				}

				else{
					dati = apsHelper.addHiddenFieldsToDati(tipoOp, strutsBean.id, null, null, dati);

					if (strutsBean.idSoggettoFruitore == null) {
						strutsBean.idSoggettoFruitore = "";
/**						if(strutsBean.wsdlimpler.getValue() == null)
//							strutsBean.wsdlimpler.setValue(new byte[1]);
//						if(strutsBean.wsdlimplfru.getValue() == null)
//							strutsBean.wsdlimplfru.setValue(new byte[1]); */ 
						strutsBean.endpointtype = AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_DISABILITATO;
						strutsBean.tipoconn = "";
						strutsBean.url = "";
						strutsBean.nome = "";
						strutsBean.tipo = ConnettoriCostanti.TIPI_CODE_JMS[0];
						strutsBean.user = "";
						strutsBean.password = "";
						strutsBean.initcont = "";
						strutsBean.urlpgk = "";
						strutsBean.provurl = "";
						strutsBean.connfact = "";
						strutsBean.sendas = ConnettoriCostanti.TIPO_SEND_AS[0];
						strutsBean.httpsurl = "";
						strutsBean.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
						strutsBean.httpshostverify = true;
						strutsBean.httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
						strutsBean.httpspath = "";
						strutsBean.httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
						strutsBean.httpspwd = "";
						strutsBean.httpsalgoritmo = "";
						strutsBean.httpsstato = false;
						strutsBean.httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						strutsBean.httpspwdprivatekeytrust = "";
						strutsBean.httpspathkey = "";
						strutsBean.httpstipokey = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
						strutsBean.httpspwdkey = "";
						strutsBean.httpspwdprivatekey = "";
						strutsBean.httpsalgoritmokey = "";

						if(apsHelper.isShowGestioneWorkflowStatoDocumenti()){
							if(strutsBean.statoPackage==null || "".equals(strutsBean.statoPackage)){
								strutsBean.statoPackage=StatiAccordo.bozza.toString();
							}

							//Se l'ASPS riferito e' in stato operativo o finale allora setto la fruizione come operativa.
							if(asps.getStatoPackage().equals(StatiAccordo.operativo.toString()) || asps.getStatoPackage().equals(StatiAccordo.finale.toString())){
								strutsBean.statoPackage=StatiAccordo.operativo.toString();
							}

						}else{
							strutsBean.statoPackage=StatiAccordo.finale.toString();
						}
					}
					
					if(strutsBean.fruizioneServizioApplicativo==null || "".equals(strutsBean.fruizioneServizioApplicativo))
						strutsBean.fruizioneServizioApplicativo = "-";
					if(strutsBean.fruizioneRuolo==null || "".equals(strutsBean.fruizioneRuolo))
						strutsBean.fruizioneRuolo = "-";
					if(strutsBean.fruizioneAutenticazione==null || "".equals(strutsBean.fruizioneAutenticazione))
						strutsBean.fruizioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteDelegate();
					if(strutsBean.fruizioneAutorizzazione==null || "".equals(strutsBean.fruizioneAutorizzazione)){
						String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteDelegate();
						strutsBean.fruizioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
							strutsBean.fruizioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
							strutsBean.fruizioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						strutsBean.fruizioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
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
					
					// default
					if(strutsBean.httpsalgoritmo==null || "".equals(strutsBean.httpsalgoritmo)){
						strutsBean.httpsalgoritmo = TrustManagerFactory.getDefaultAlgorithm();
					}
					if(strutsBean.httpsalgoritmokey==null || "".equals(strutsBean.httpsalgoritmokey)){
						strutsBean.httpsalgoritmokey = KeyManagerFactory.getDefaultAlgorithm();
					}
					if(strutsBean.httpstipologia==null || "".equals(strutsBean.httpstipologia)){
						strutsBean.httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
					}
					if(strutsBean.httpshostverifyS==null || "".equals(strutsBean.httpshostverifyS)){
						strutsBean.httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
						strutsBean.httpshostverify = true;
					}
					if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
						httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;
						strutsBean.httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
					}

					strutsBean.autenticazioneHttp = apsHelper.getAutenticazioneHttp(strutsBean.autenticazioneHttp, strutsBean.endpointtype, strutsBean.user);

					if(strutsBean.tempiRispostaConnectionTimeout==null || "".equals(strutsBean.tempiRispostaConnectionTimeout) 
							|| 
							strutsBean.tempiRispostaReadTimeout==null || "".equals(strutsBean.tempiRispostaReadTimeout) 
							|| 
							strutsBean.tempiRispostaTempoMedioRisposta==null || "".equals(strutsBean.tempiRispostaTempoMedioRisposta) ){
						
						ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
						ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
						
						if(strutsBean.tempiRispostaConnectionTimeout==null || "".equals(strutsBean.tempiRispostaConnectionTimeout) ) {
							strutsBean.tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
						}
						if(strutsBean.tempiRispostaReadTimeout==null || "".equals(strutsBean.tempiRispostaReadTimeout) ) {
							strutsBean.tempiRispostaReadTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
						}
						if(strutsBean.tempiRispostaTempoMedioRisposta==null || "".equals(strutsBean.tempiRispostaTempoMedioRisposta) ) {
							strutsBean.tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";
						}
						
					}
					
					// update della configurazione 
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);


					dati = apsHelper.addServiziFruitoriToDati(dati, strutsBean.idSoggettoFruitore, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, soggettiList,
							soggettiListLabel, "0", strutsBean.id, tipoOp, "", "", "", nomeservizio, tiposervizio, versioneservizio, strutsBean.correlato, strutsBean.statoPackage, strutsBean.statoPackage,asps.getStatoPackage(), null,strutsBean.validazioneDocumenti,
							strutsBean.controlloAccessiStato,
							strutsBean.fruizioneServizioApplicativo,strutsBean.fruizioneRuolo,strutsBean.fruizioneAutenticazione,strutsBean.fruizioneAutenticazioneOpzionale,strutsBean.fruizioneAutenticazionePrincipal, strutsBean.fruizioneAutenticazioneParametroList, strutsBean.fruizioneAutorizzazione,
							strutsBean.fruizioneAutorizzazioneAutenticati, strutsBean.fruizioneAutorizzazioneRuoli, strutsBean.fruizioneAutorizzazioneRuoliTipologia, strutsBean.fruizioneAutorizzazioneRuoliMatch,
							saList,apcCore.toMessageServiceBinding(as.getServiceBinding()), apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica()),
							null, null, null, null,
							gestioneToken, policyLabels, policyValues, 
							gestioneTokenPolicy, gestioneTokenOpzionale,
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
							autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
							autorizzazioneToken, autorizzazioneTokenOptions,
							autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
							identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
							autorizzazioneAutenticatiToken, 
							autorizzazioneRuoliToken,  autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);
	
					dati = apsHelper.addFruitoreToDati(TipoOperazione.ADD, versioniLabel, versioniValues, dati,null
							,null,null,null,null,null,null,null,null,null);

					String tipoSendas = ConnettoriCostanti.TIPO_SEND_AS[0];
					String tipoJms = ConnettoriCostanti.TIPI_CODE_JMS[0];
					if (apsHelper.isModalitaAvanzata()) {
						dati = apsHelper.addEndPointToDati(dati, strutsBean.connettoreDebug, strutsBean.endpointtype, strutsBean.autenticazioneHttp, null, 
								strutsBean.url, strutsBean.nome,
								tipoJms, strutsBean.user,
								strutsBean.password, strutsBean.initcont, strutsBean.urlpgk,
								strutsBean.provurl, strutsBean.connfact, tipoSendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, 
								strutsBean.httpsurl, strutsBean.httpstipologia,	strutsBean.httpshostverify, 
								strutsBean.httpsTrustVerifyCert, strutsBean.httpspath, strutsBean.httpstipo, strutsBean.httpspwd,
								strutsBean.httpsalgoritmo, strutsBean.httpsstato, strutsBean.httpskeystore,
								strutsBean.httpspwdprivatekeytrust, strutsBean.httpspathkey,
								strutsBean.httpstipokey, strutsBean.httpspwdkey, 
								strutsBean.httpspwdprivatekey, strutsBean.httpsalgoritmokey,
								strutsBean.httpsKeyAlias, strutsBean.httpsTrustStoreCRLs, strutsBean.httpsTrustStoreOCSPPolicy,
								strutsBean.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
								null, null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								strutsBean.proxyEnabled, strutsBean.proxyHostname, strutsBean.proxyPort, strutsBean.proxyUsername, strutsBean.proxyPassword,
								strutsBean.tempiRispostaEnabled, strutsBean.tempiRispostaConnectionTimeout, strutsBean.tempiRispostaReadTimeout, strutsBean.tempiRispostaTempoMedioRisposta,
								strutsBean.opzioniAvanzate, strutsBean.transferMode, strutsBean.transferModeChunkSize, strutsBean.redirectMode, strutsBean.redirectMaxHop,
								strutsBean.requestOutputFileName, strutsBean.requestOutputFileNamePermissions, strutsBean.requestOutputFileNameHeaders, strutsBean.requestOutputFileNameHeadersPermissions,
								strutsBean.requestOutputParentDirCreateIfNotExists,strutsBean.requestOutputOverwriteIfExists,
								strutsBean.responseInputMode, strutsBean.responseInputFileName, strutsBean.responseInputFileNameHeaders, strutsBean.responseInputDeleteAfterRead, strutsBean.responseInputWaitTime,
								strutsBean.autenticazioneToken,strutsBean.tokenPolicy, forcePDND, forceOAuth,
								listExtendedConnettore, false,
								protocollo, forceHttps, forceHttpsClient, false, false, null, null,
								strutsBean.autenticazioneApiKey, strutsBean.useOAS3Names, strutsBean.useAppId, strutsBean.apiKeyHeader, strutsBean.apiKeyValue, strutsBean.appIdHeader, strutsBean.appIdValue,
								postBackViaPost);
					}else{
						//spostato dentro l'helper
					}
				}

				// aggiunta campi custom
				dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziFruitoriCheckData(TipoOperazione.ADD, soggettiList,
					strutsBean.id, "", "", null, "", "", strutsBean.idSoggettoFruitore,
					strutsBean.endpointtype, strutsBean.url, strutsBean.nome, strutsBean.tipo,
					strutsBean.user, strutsBean.password, strutsBean.initcont, strutsBean.urlpgk,
					strutsBean.provurl, strutsBean.connfact, strutsBean.sendas,
					strutsBean.wsdlimpler, strutsBean.wsdlimplfru, "0",
					strutsBean.httpsurl, strutsBean.httpstipologia, strutsBean.httpshostverify, 
					strutsBean.httpsTrustVerifyCert, strutsBean.httpspath, strutsBean.httpstipo,
					strutsBean.httpspwd, strutsBean.httpsalgoritmo, strutsBean.httpsstato,
					strutsBean.httpskeystore, strutsBean.httpspwdprivatekeytrust,
					strutsBean.httpspathkey, strutsBean.httpstipokey,
					strutsBean.httpspwdkey, strutsBean.httpspwdprivatekey,
					strutsBean.httpsalgoritmokey, 
					strutsBean.httpsKeyAlias, strutsBean.httpsTrustStoreCRLs, strutsBean.httpsTrustStoreOCSPPolicy,
					strutsBean.tipoconn,strutsBean.validazioneDocumenti,null,strutsBean.autenticazioneHttp,
					strutsBean.proxyEnabled, strutsBean.proxyHostname, strutsBean.proxyPort, strutsBean.proxyUsername, strutsBean.proxyPassword,
					strutsBean.tempiRispostaEnabled, strutsBean.tempiRispostaConnectionTimeout, strutsBean.tempiRispostaReadTimeout, strutsBean.tempiRispostaTempoMedioRisposta,
					strutsBean.opzioniAvanzate, strutsBean.transferMode, strutsBean.transferModeChunkSize, strutsBean.redirectMode, strutsBean.redirectMaxHop,
					strutsBean.requestOutputFileName, strutsBean.requestOutputFileNamePermissions, strutsBean.requestOutputFileNameHeaders, strutsBean.requestOutputFileNameHeadersPermissions,
					strutsBean.requestOutputParentDirCreateIfNotExists,strutsBean.requestOutputOverwriteIfExists,
					strutsBean.responseInputMode, strutsBean.responseInputFileName, strutsBean.responseInputFileNameHeaders, strutsBean.responseInputDeleteAfterRead, strutsBean.responseInputWaitTime,
					strutsBean.fruizioneServizioApplicativo,strutsBean.fruizioneRuolo,strutsBean.fruizioneAutenticazione,strutsBean.fruizioneAutenticazioneOpzionale,strutsBean.fruizioneAutenticazionePrincipal, strutsBean.fruizioneAutenticazioneParametroList, strutsBean.fruizioneAutorizzazione,
					strutsBean.fruizioneAutorizzazioneAutenticati, strutsBean.fruizioneAutorizzazioneRuoli, strutsBean.fruizioneAutorizzazioneRuoliTipologia, strutsBean.fruizioneAutorizzazioneRuoliMatch,
					allegatoXacmlPolicy,
					strutsBean.autenticazioneToken,strutsBean.tokenPolicy,
					strutsBean.autenticazioneApiKey, strutsBean.useOAS3Names, strutsBean.useAppId, strutsBean.apiKeyHeader, strutsBean.apiKeyValue, strutsBean.appIdHeader, strutsBean.appIdValue,
					listExtendedConnettore);

			// updateDynamic
			if(isOk) {
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);		
			}
			
			// Validazione base dei parametri custom 
			if(isOk){
				try{
					apsHelper.validaProtocolProperties(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, strutsBean.protocolProperties);
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
					strutsBean.consoleDynamicConfiguration.validateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}


			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParm = new ArrayList<>();

				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
				lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
						AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ strutsBean.id)
						//				,
						//						new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggErogatore)
						));
				lstParm.add(ServletUtils.getParameterAggiungi());

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);

				dati = apsHelper.addHiddenFieldsToDati(tipoOp, strutsBean.id, null, null, dati);

				dati = apsHelper.addServiziFruitoriToDati(dati, strutsBean.idSoggettoFruitore, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, soggettiList, soggettiListLabel, "0", strutsBean.id, tipoOp,
						"", "", "", nomeservizio, tiposervizio, versioneservizio, strutsBean.correlato, strutsBean.statoPackage, strutsBean.statoPackage,asps.getStatoPackage(),null,strutsBean.validazioneDocumenti,
						strutsBean.controlloAccessiStato,
						strutsBean.fruizioneServizioApplicativo,strutsBean.fruizioneRuolo,strutsBean.fruizioneAutenticazione,strutsBean.fruizioneAutenticazioneOpzionale,strutsBean.fruizioneAutenticazionePrincipal, strutsBean.fruizioneAutenticazioneParametroList, strutsBean.fruizioneAutorizzazione,
						strutsBean.fruizioneAutorizzazioneAutenticati, strutsBean.fruizioneAutorizzazioneRuoli, strutsBean.fruizioneAutorizzazioneRuoliTipologia, strutsBean.fruizioneAutorizzazioneRuoliMatch,
						saList,apcCore.toMessageServiceBinding(as.getServiceBinding()), apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica()),
						null, null, null, null,gestioneToken, policyLabels, policyValues, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazioneToken, autorizzazioneTokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
						identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
						autorizzazioneAutenticatiToken, 
						autorizzazioneRuoliToken,  autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);

				dati = apsHelper.addFruitoreToDati(tipoOp, versioniLabel, versioniValues, 
						dati,null
						,null,null,null,null,null,null,null,null,null);

				if (apsHelper.isModalitaAvanzata()) {
					dati = apsHelper.addEndPointToDati(dati, strutsBean.connettoreDebug, strutsBean.endpointtype, strutsBean.autenticazioneHttp, null,
							strutsBean.url, strutsBean.nome, strutsBean.tipo, strutsBean.user,
							strutsBean.password, strutsBean.initcont, strutsBean.urlpgk,
							strutsBean.provurl, strutsBean.connfact, strutsBean.sendas,
							AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, 
							strutsBean.httpsurl, strutsBean.httpstipologia,	strutsBean.httpshostverify, 
							strutsBean.httpsTrustVerifyCert, strutsBean.httpspath, strutsBean.httpstipo,
							strutsBean.httpspwd, strutsBean.httpsalgoritmo, strutsBean.httpsstato,
							strutsBean.httpskeystore, strutsBean.httpspwdprivatekeytrust,
							strutsBean.httpspathkey, strutsBean.httpstipokey,
							strutsBean.httpspwdkey, strutsBean.httpspwdprivatekey,
							strutsBean.httpsalgoritmokey, 
							strutsBean.httpsKeyAlias, strutsBean.httpsTrustStoreCRLs, strutsBean.httpsTrustStoreOCSPPolicy,
							strutsBean.tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
							null, null, null, null, null, null, null, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							strutsBean.proxyEnabled, strutsBean.proxyHostname, strutsBean.proxyPort, strutsBean.proxyUsername, strutsBean.proxyPassword,
							strutsBean.tempiRispostaEnabled, strutsBean.tempiRispostaConnectionTimeout, strutsBean.tempiRispostaReadTimeout, strutsBean.tempiRispostaTempoMedioRisposta,
							strutsBean.opzioniAvanzate, strutsBean.transferMode, strutsBean.transferModeChunkSize, strutsBean.redirectMode, strutsBean.redirectMaxHop,
							strutsBean.requestOutputFileName, strutsBean.requestOutputFileNamePermissions, strutsBean.requestOutputFileNameHeaders, strutsBean.requestOutputFileNameHeadersPermissions,
							strutsBean.requestOutputParentDirCreateIfNotExists,strutsBean.requestOutputOverwriteIfExists,
							strutsBean.responseInputMode, strutsBean.responseInputFileName, strutsBean.responseInputFileNameHeaders, strutsBean.responseInputDeleteAfterRead, strutsBean.responseInputWaitTime,
							strutsBean.autenticazioneToken,strutsBean.tokenPolicy, forcePDND, forceOAuth,
							listExtendedConnettore, false,
							protocollo, forceHttps, forceHttpsClient, false, false, null, null,
							strutsBean.autenticazioneApiKey, strutsBean.useOAS3Names, strutsBean.useAppId, strutsBean.apiKeyHeader, strutsBean.apiKeyValue, strutsBean.appIdHeader, strutsBean.appIdValue,
							postBackViaPost);
				}else{
					//spostato dentro l'helper
				}

				// aggiunta campi custom
				dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
						ForwardParams.ADD());
			}

			// Inserisco il fruitore nel db
			int idProv = Integer.parseInt(strutsBean.idSoggettoFruitore);

			// prendo il nome e il tipo del soggetto proprietario della
			// porta delegata
			// che sarebbe il soggetto fruitore di questo servizio
			// Soggetto Fruitore
			Soggetto sogFru = soggettiCore.getSoggettoRegistro(idProv);
			String nomeFruitore = sogFru.getNome();
			String tipoFruitore = sogFru.getTipo();
			String pdd = sogFru.getPortaDominio();


			Connettore connettore = null;
			if (apsHelper.isModalitaAvanzata()) {
				connettore = new Connettore();
				
				apsHelper.fillConnettore(connettore, strutsBean.connettoreDebug, strutsBean.endpointtype, strutsBean.endpointtype, strutsBean.tipoconn, strutsBean.url,
						strutsBean.nome, strutsBean.tipo, strutsBean.user, strutsBean.password,
						strutsBean.initcont, strutsBean.urlpgk, strutsBean.provurl, strutsBean.connfact,
						strutsBean.sendas, strutsBean.httpsurl, strutsBean.httpstipologia, strutsBean.httpshostverify, 
						strutsBean.httpsTrustVerifyCert, strutsBean.httpspath, strutsBean.httpstipo,
						strutsBean.httpspwd, strutsBean.httpsalgoritmo, strutsBean.httpsstato,
						strutsBean.httpskeystore, strutsBean.httpspwdprivatekeytrust,
						strutsBean.httpspathkey, strutsBean.httpstipokey,
						strutsBean.httpspwdkey, strutsBean.httpspwdprivatekey,
						strutsBean.httpsalgoritmokey,
						strutsBean.httpsKeyAlias, strutsBean.httpsTrustStoreCRLs, strutsBean.httpsTrustStoreOCSPPolicy,
						strutsBean.proxyEnabled, strutsBean.proxyHostname, strutsBean.proxyPort, strutsBean.proxyUsername, strutsBean.proxyPassword,
						strutsBean.tempiRispostaEnabled, strutsBean.tempiRispostaConnectionTimeout, strutsBean.tempiRispostaReadTimeout, strutsBean.tempiRispostaTempoMedioRisposta,
						strutsBean.opzioniAvanzate, strutsBean.transferMode, strutsBean.transferModeChunkSize, strutsBean.redirectMode, strutsBean.redirectMaxHop,
						strutsBean.requestOutputFileName, strutsBean.requestOutputFileNamePermissions, strutsBean.requestOutputFileNameHeaders, strutsBean.requestOutputFileNameHeadersPermissions,
						strutsBean.requestOutputParentDirCreateIfNotExists,strutsBean.requestOutputOverwriteIfExists,
						strutsBean.responseInputMode, strutsBean.responseInputFileName, strutsBean.responseInputFileNameHeaders, strutsBean.responseInputDeleteAfterRead, strutsBean.responseInputWaitTime,
						strutsBean.tokenPolicy,
						strutsBean.apiKeyHeader, strutsBean.apiKeyValue, strutsBean.appIdHeader, strutsBean.appIdValue,
						listExtendedConnettore);
			}

			Fruitore fruitore = new Fruitore();
			fruitore.setId(Long.valueOf(idProv));
			fruitore.setTipo(tipoFruitore);
			fruitore.setNome(nomeFruitore);
			fruitore.setConnettore(connettore);

			FormatoSpecifica formato = null;
			if(as!=null) {
				formato = as.getFormatoSpecifica();
			}
			String wsdlimplerS = strutsBean.wsdlimpler.getValue() != null ? new String(strutsBean.wsdlimpler.getValue()) : null; 
			fruitore.setByteWsdlImplementativoErogatore(apsCore.getInterfaceAsByteArray(formato, wsdlimplerS));
			String wsdlimplfruS = strutsBean.wsdlimplfru.getValue() != null ? new String(strutsBean.wsdlimplfru.getValue()) : null; 
			fruitore.setByteWsdlImplementativoFruitore(apsCore.getInterfaceAsByteArray(formato, wsdlimplfruS));

			AccordoServizioParteSpecifica servsp = apsCore.getAccordoServizioParteSpecifica(idServizioLong);

			// stato
			fruitore.setStatoPackage(strutsBean.statoPackage);

			/**			Spostato sopra a livello di edit in progress			
			//			//Se l'ASPS riferito e' in stato operativo o finale allora setto la fruizione come operativa.
			//			if(asps.getStatoPackage().equals(StatiAccordo.operativo.toString()) || asps.getStatoPackage().equals(StatiAccordo.finale.toString())){
			//				fruitore.setStatoPackage(StatiAccordo.operativo.toString());
			//			}*/

			// Check stato
			if(apsHelper.isShowGestioneWorkflowStatoDocumenti()){

				try{
					apsCore.validaStatoFruitoreAccordoServizioParteSpecifica(fruitore, servsp);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					List<Parameter> lstParm = new ArrayList<>();

					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_LIST));
					lstParm.add(new Parameter(AccordiServizioParteSpecificaCostanti.LABEL_APS_FUITORI_DI  + tmpTitle, 
							AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_LIST ,
							new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, ""+ strutsBean.id)
							//					,
							//							new Parameter( AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+ idSoggErogatore)
							));
					lstParm.add(ServletUtils.getParameterAggiungi());

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );

					// preparo i campi
					List<DataElement> dati = new ArrayList<>();

					dati.add(ServletUtils.getDataElementForEditModeFinished());

					// update della configurazione 
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);

					dati = apsHelper.addHiddenFieldsToDati(tipoOp, strutsBean.id, null, null, dati);

					dati = apsHelper.addServiziFruitoriToDati(dati, strutsBean.idSoggettoFruitore, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, 
							soggettiList, soggettiListLabel, "0", strutsBean.id, tipoOp, "", "", "", nomeservizio, tiposervizio, versioneservizio, strutsBean.correlato, 
							strutsBean.statoPackage, strutsBean.statoPackage,asps.getStatoPackage(),null,strutsBean.validazioneDocumenti,
							strutsBean.controlloAccessiStato,
							strutsBean.fruizioneServizioApplicativo,strutsBean.fruizioneRuolo,strutsBean.fruizioneAutenticazione,strutsBean.fruizioneAutenticazioneOpzionale,strutsBean.fruizioneAutenticazionePrincipal, strutsBean.fruizioneAutenticazioneParametroList, strutsBean.fruizioneAutorizzazione,
							strutsBean.fruizioneAutorizzazioneAutenticati, strutsBean.fruizioneAutorizzazioneRuoli, strutsBean.fruizioneAutorizzazioneRuoliTipologia, strutsBean.fruizioneAutorizzazioneRuoliMatch,
							saList,apcCore.toMessageServiceBinding(as.getServiceBinding()), apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica()),
							null, null, null, null,gestioneToken, policyLabels, policyValues, 
							gestioneTokenPolicy, gestioneTokenOpzionale,
							gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
							autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
							autorizzazioneToken, autorizzazioneTokenOptions,
							autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
							identificazioneAttributiStato, attributeAuthorityLabels, attributeAuthorityValues, attributeAuthoritySelezionate, attributeAuthorityAttributi,
							autorizzazioneAutenticatiToken, 
							autorizzazioneRuoliToken,  autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken);

					dati = apsHelper.addFruitoreToDati(TipoOperazione.ADD, versioniLabel, versioniValues, dati,null
							,null,null,null,null,null,null,null,null,null);

					if (apsHelper.isModalitaAvanzata()) {
						dati = apsHelper.addEndPointToDati(dati, strutsBean.connettoreDebug, strutsBean.endpointtype, strutsBean.autenticazioneHttp, null,
								strutsBean.url, strutsBean.nome, strutsBean.tipo, strutsBean.user,
								strutsBean.password, strutsBean.initcont, strutsBean.urlpgk,
								strutsBean.provurl, strutsBean.connfact, strutsBean.sendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, 
								strutsBean.httpsurl, strutsBean.httpstipologia, strutsBean.httpshostverify, 
								strutsBean.httpsTrustVerifyCert, strutsBean.httpspath, strutsBean.httpstipo,
								strutsBean.httpspwd, strutsBean.httpsalgoritmo, strutsBean.httpsstato,
								strutsBean.httpskeystore, strutsBean.httpspwdprivatekeytrust,
								strutsBean.httpspathkey, strutsBean.httpstipokey,
								strutsBean.httpspwdkey, strutsBean.httpspwdprivatekey,
								strutsBean.httpsalgoritmokey, 
								strutsBean.httpsKeyAlias, strutsBean.httpsTrustStoreCRLs, strutsBean.httpsTrustStoreOCSPPolicy,
								strutsBean.tipoconn, 
								AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_ADD, null,
								null, null, null, null, null, null, null, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								strutsBean.proxyEnabled, strutsBean.proxyHostname, strutsBean.proxyPort, strutsBean.proxyUsername, strutsBean.proxyPassword,
								strutsBean.tempiRispostaEnabled, strutsBean.tempiRispostaConnectionTimeout, strutsBean.tempiRispostaReadTimeout, strutsBean.tempiRispostaTempoMedioRisposta,
								strutsBean.opzioniAvanzate, strutsBean.transferMode, strutsBean.transferModeChunkSize, strutsBean.redirectMode, strutsBean.redirectMaxHop,
								strutsBean.requestOutputFileName, strutsBean.requestOutputFileNamePermissions, strutsBean.requestOutputFileNameHeaders, strutsBean.requestOutputFileNameHeadersPermissions,
								strutsBean.requestOutputParentDirCreateIfNotExists,strutsBean.requestOutputOverwriteIfExists,
								strutsBean.responseInputMode, strutsBean.responseInputFileName, strutsBean.responseInputFileNameHeaders, strutsBean.responseInputDeleteAfterRead, strutsBean.responseInputWaitTime,
								strutsBean.autenticazioneToken,strutsBean.tokenPolicy, forcePDND, forceOAuth,
								listExtendedConnettore, false,
								protocollo, forceHttps, forceHttpsClient, false, false, null, null,
								strutsBean.autenticazioneApiKey, strutsBean.useOAS3Names, strutsBean.useAppId, strutsBean.apiKeyHeader, strutsBean.apiKeyValue, strutsBean.appIdHeader, strutsBean.appIdValue,
								postBackViaPost);
					}else{
						//spostato dentro l'helper
					}

					// aggiunta campi custom
					dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
							ForwardParams.ADD());
				}
			}

			//imposto properties custom
			fruitore.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(strutsBean.protocolProperties, strutsBean.consoleOperationType,null));

			servsp.addFruitore(fruitore);
			apsCore.setDataCreazioneFruitore(fruitore);
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), servsp);

			// Prendo i dati del soggetto erogatore del servizio
			String mynomeprov = asps.getNomeSoggettoErogatore();
			String mytipoprov = asps.getTipoSoggettoErogatore();

			// creo la porta delegata in automatico 
			boolean generazionePortaDelegata = true;
			/*
			 * bug-fix #61 Se il soggetto (fruitore) afferisce a una porta di
			 * dominio di tipo 'esterno', la porta delegata non deve essere
			 * creata.
			 */
			try{
				if(pddCore.isPddEsterna(pdd)){
					generazionePortaDelegata = false;
				}
			}catch(DriverControlStationNotFound dNT){
				// In singlePdD la porta di dominio e' opzionale.
				if(!apsCore.isSinglePdD()){
					throw dNT;
				}
			}
			if (generazionePortaDelegata) {
				
				List<Object> listaOggettiDaCreare = new ArrayList<>();
				
				IDSoggetto idFruitore = new IDSoggetto(tipoFruitore, nomeFruitore);
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tiposervizio, nomeservizio, mytipoprov, mynomeprov, versioneservizio);
				ServiceBinding serviceBinding = org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(as.getServiceBinding()) ?
						ServiceBinding.REST : ServiceBinding.SOAP;
				ProtocolSubscription subscriptionDefault = strutsBean.protocolFactory.createProtocolIntegrationConfiguration().
						createDefaultSubscription(serviceBinding, idFruitore, idServizio);
				
				PortaDelegata portaDelegata = subscriptionDefault.getPortaDelegata();
				MappingFruizionePortaDelegata mappingFruizione = subscriptionDefault.getMapping();
				portaDelegata.setIdSoggetto((long) idProv);

				if(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO.equals(strutsBean.controlloAccessiStato)) {
					strutsBean.fruizioneAutenticazione = TipoAutenticazione.DISABILITATO.getValue();
					strutsBean.fruizioneAutenticazioneOpzionale = null;
					strutsBean.fruizioneAutenticazionePrincipal = null;
					strutsBean.fruizioneAutenticazioneParametroList = null;
				}
				
				if(CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_STATO_PUBBLICO.equals(strutsBean.controlloAccessiStato)) {
					strutsBean.fruizioneAutorizzazione = TipoAutorizzazione.DISABILITATO.getValue();
					strutsBean.fruizioneAutorizzazioneAutenticati = null;
					strutsBean.fruizioneAutorizzazioneRuoli = null;
					strutsBean.fruizioneAutorizzazioneRuoliTipologia = null;
					strutsBean.fruizioneAutorizzazioneRuoliMatch = null;
				}
				
				porteDelegateCore.configureControlloAccessiPortaDelegata(portaDelegata, 
						strutsBean.fruizioneAutenticazione, strutsBean.fruizioneAutenticazioneOpzionale,strutsBean.fruizioneAutenticazionePrincipal, strutsBean.fruizioneAutenticazioneParametroList,
						strutsBean.fruizioneAutorizzazione, strutsBean.fruizioneAutorizzazioneAutenticati, strutsBean.fruizioneAutorizzazioneRuoli, strutsBean.fruizioneAutorizzazioneRuoliTipologia, strutsBean.fruizioneAutorizzazioneRuoliMatch,
						strutsBean.fruizioneServizioApplicativo, strutsBean.fruizioneRuolo,
						autorizzazioneAutenticatiToken, 
						autorizzazioneRuoliToken,  autorizzazioneRuoliTipologiaToken, autorizzazioneRuoliMatchToken,
						autorizzazioneTokenOptions,
						autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
						identificazioneAttributiStato, attributeAuthoritySelezionate, attributeAuthorityAttributi);
				
				porteDelegateCore.configureControlloAccessiGestioneToken(portaDelegata, gestioneToken, 
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazioneTokenOptions
						);
							
				// Verifico prima che la porta delegata non esista già
				if (!porteDelegateCore.existsPortaDelegata(mappingFruizione.getIdPortaDelegata())){
					listaOggettiDaCreare.add(portaDelegata);
				}
				listaOggettiDaCreare.add(mappingFruizione);
				
				porteDelegateCore.performCreateOperation(superUser, apsHelper.smista(), listaOggettiDaCreare.toArray());
			}


			// cancello i file temporanei
			apsHelper.deleteBinaryParameters(strutsBean.wsdlimpler,strutsBean.wsdlimplfru);
			apsHelper.deleteBinaryProtocolPropertiesTmpFiles(strutsBean.protocolProperties);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.SERVIZI_FRUITORI;

			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);

			List<Fruitore> lista = apsCore.serviziFruitoriList(Integer.parseInt(strutsBean.id), ricerca);

			apsHelper.prepareServiziFruitoriList(lista, strutsBean.id, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished( mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					ForwardParams.ADD());
		}  
	}

	
}
