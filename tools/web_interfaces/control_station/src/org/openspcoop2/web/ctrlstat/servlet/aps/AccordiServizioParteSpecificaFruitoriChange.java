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


package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
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
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.utils.UpdateProprietaOggetto;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ControlStationCoreException;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoreStatusParams;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateHelper;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**
 * serviziFruitoriChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		AccordiServizioParteSpecificaFruitoriChangeStrutsBean strutsBean = new AccordiServizioParteSpecificaFruitoriChangeStrutsBean();
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		strutsBean.consoleOperationType = ConsoleOperationType.CHANGE;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		List<ProtocolProperty> oldProtocolPropertyList = null;

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			
			// Preparo il menu
			apsHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) 
				parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session, request).getValue();
			
			// prendo i dati hidden del pdold e li metto nel pd attuale
			PageData pdOld = ServletUtils.getPageDataFromSession(request, session);
			pd.setHidden(pdOld.getHidden());

			boolean isModalitaCompleta = apsHelper.isModalitaCompleta();
			
			strutsBean.editMode = apsHelper.getParametroEditMode(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			strutsBean.protocolPropertiesSet = apsHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);

			String idServizio = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			long idServizioLong = Long.valueOf(idServizio);
			
			String idServizioFruitore = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID);// id della fruizione
			long idServizioFruitoreInt = Long.parseLong(idServizioFruitore);

			// NOTA PARAMETRO_APS_MY_ID e' l'id della fruizoione, mentre PARAMETRO_APS_PROVIDER_FRUITORE e' l'id del soggetto fruitore 
			String idSoggettoFruitore = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE); 
			String correlato = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
			
			String myTipo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_TIPO);
			if(myTipo == null) myTipo = "";
			String myNome = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_NOME);
			if(myNome == null) myNome = "";

			String endpointtype = apsHelper.readEndPointType();
			String tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;

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

			// http
			String url = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
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
			String nome = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_NOME_CODA);
			String tipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_CODA);
			String initcont = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_INIT_CTX);
			String urlpgk = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_URL_PKG);
			String provurl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PROVIDER_URL);
			String connfact = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_CONNECTION_FACTORY);
			String sendas = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_TIPO_OGGETTO_JMS);
			if(TipiConnettore.JMS.toString().equals(endpointtype)){
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_USERNAME);
				password = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_JMS_PASSWORD);
			}

			// https
			String httpsurl = url;
			String httpstipologia = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE );
			String httpshostverifyS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			boolean httpshostverify = false;
			if (httpshostverifyS != null && httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED))
				httpshostverify = true;
			String httpsTrustVerifyCertS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS );
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			String httpspath = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
			String httpstipo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			String httpspwd = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
			String httpsalgoritmo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
			String httpsstatoS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO);
			boolean httpsstato = false;
			if (httpsstatoS != null && httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED))
				httpsstato = true;
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

			//status
			ConnettoreStatusParams connettoreStatusParams = ConnettoreStatusParams.fillFrom(apsHelper);
			
			String statoPackage = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);

			String backToStato = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO);
			String actionConfirm = apsHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);
			
			String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
				else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
			}
			String azioneConnettore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE);
			String azioneConnettoreIdPorta = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA);
			boolean forceEnableConnettore =  gestioneFruitori;
			if(endpointtype!=null && !"".equals(endpointtype) &&
				forceEnableConnettore &&
				TipiConnettore.DISABILITATO.toString().equals(endpointtype)) {
				forceEnableConnettore = false;
			}
			
			String tmpModificaProfilo = apsHelper.getParametroBoolean(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
			boolean modificaProfilo = false;
			if(tmpModificaProfilo!=null) {
				modificaProfilo = "true".equals(tmpModificaProfilo);
			}
			
			boolean addPropertiesHidden = false;
			if(!apsHelper.isModalitaCompleta() && !modificaProfilo) {
				addPropertiesHidden = true;
			}
			
			boolean viewOnlyConnettore = gestioneFruitori || (PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE==parentPD);
			if(modificaProfilo) {
				viewOnlyConnettore = false;
			}
			if(viewOnlyConnettore) {
				addPropertiesHidden = true;
			}
			
			boolean accessoDaListaAPS = false;
			String accessoDaAPSParametro = null;
			if(gestioneFruitori) {
				accessoDaAPSParametro = apsHelper.getParametroBoolean(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONNETTORE_DA_LISTA_APS);
				if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro) || modificaProfilo) {
					accessoDaListaAPS = true;
				}
			}
						
			boolean validazioneDocumenti = true;
			String tmpValidazioneDocumenti = apsHelper.getParametroBoolean(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			if(apsHelper.isEditModeInProgress()){
				// primo accesso alla servlet
				if(tmpValidazioneDocumenti!=null){
					if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						validazioneDocumenti = true;
					}
					else if("false".equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_DISABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
						validazioneDocumenti = false;
					}
				}else{
					validazioneDocumenti = true;
				}
			}else{
				if(Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(tmpValidazioneDocumenti) || Costanti.CHECK_BOX_ENABLED.equalsIgnoreCase(tmpValidazioneDocumenti)){
					validazioneDocumenti = true;
				}
				else{
					validazioneDocumenti = false;
				}
			}

			String idTab = apsHelper.getParametroInteger(CostantiControlStation.PARAMETRO_ID_TAB);
			if(azioneConnettoreIdPorta!=null && !"".equals(azioneConnettoreIdPorta) && !apsHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// Prendo il servizio
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();

			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizioLong);
			String nomeservizio = asps.getNome();
			String tiposervizio = asps.getTipo();
			Integer versioneservizio = asps.getVersione();

			IDServizio idServizioObject = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			idServizioObject.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
			idServizioObject.setPortType(asps.getPortType());
			
			// se ho impostato nome e tipo del fruitore li utilizzo per ricostruire l'id del fruitore
			if(StringUtils.isNotBlank(myTipo) && StringUtils.isNotBlank(myNome)){
				long idSoggetto = soggettiCore.getIdSoggetto(myNome,myTipo);
				idServizioFruitoreInt = apsCore.getServizioFruitore(idServizioObject, idSoggetto); 
			}
			
			// Prendo nome e tipo del fruitore dal db
			Fruitore servFru = apsCore.getServizioFruitore(idServizioFruitoreInt);
			myTipo = servFru.getTipo();
			myNome = servFru.getNome();
			// Prendo pero poi immagine del fruitore dall'asps
			for (Fruitore check : asps.getFruitoreList()) {
				if(check.getTipo().equals(myTipo) && check.getNome().equals(myNome)) {
					servFru = check;
					break;
				}
			}

			Boolean isConnettoreCustomUltimaImmagineSalvata = servFru.getConnettore().getCustom();

			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(servFru.getConnettore(), ConnettoreServletType.FRUIZIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA_CHANGE, apsHelper, 
							(endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro

			// Prendo il soggetto erogatore del servizio
			String tipoSoggettoErogatore = asps.getTipoSoggettoErogatore();
			String nomeSoggettoErogatore = asps.getNomeSoggettoErogatore();
			String idSoggettoErogatoreDelServizio = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			if ((idSoggettoErogatoreDelServizio == null) || idSoggettoErogatoreDelServizio.equals("")) {
				PageData oldPD = ServletUtils.getPageDataFromSession(request, session);

				idSoggettoErogatoreDelServizio = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);

				if (idSoggettoErogatoreDelServizio == null || idSoggettoErogatoreDelServizio.equals("")) {
					IDSoggetto idSE = new IDSoggetto(tipoSoggettoErogatore, nomeSoggettoErogatore);
					Soggetto se = soggettiCore.getSoggettoRegistro(idSE);
					idSoggettoErogatoreDelServizio = "" + se.getId();
				}
			}

			if( correlato == null){
				correlato = TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio()) ?
						AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_CORRELATO :
							AccordiServizioParteSpecificaCostanti.DEFAULT_VALUE_NORMALE;
			}
			
			boolean postBackViaPost = true;
			
			// setto i dati come campi hidden nel pd per portarmeli dietro

			Soggetto soggettoFruitore = null;
			if ((idSoggettoFruitore != null) && !idSoggettoFruitore.equals("")) {
				long idSoggettoFruitoreAsInt = Long.parseLong(idSoggettoFruitore);
				soggettoFruitore = soggettiCore.getSoggettoRegistro(idSoggettoFruitoreAsInt);
			} 
			/**else {
				//				Soggetto soggetto = soggettiCore.getSoggettoRegistro(new IDSoggetto(servFru.getTipo(),servFru.getNome()));
				//				profiloSoggettoFruitore = soggetto.getVersioneProtocollo();
			}*/
			if(soggettoFruitore==null) {
				throw new ControlStationCoreException("Fruitore con id '"+idSoggettoFruitore+"' non trovato");
			}

			String protocollo = apsCore.getProtocolloAssociatoTipoServizio(tiposervizio);
			List<String> versioniProtocollo = apsCore.getVersioniProtocollo(protocollo);
			/**List<String> tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo);

			// Prendo la lista di soggetti (tranne quello del servizio)
			// e la metto in un array
//			List<Soggetto> soggList = soggettiCore.soggettiRegistroList("", new Search(true));
//			String[] soggettiList = null;
//			String[] soggettiListLabel = null;
//			List<String> soggettiListList = new ArrayList<>();
//			List<String> soggettiListLabelList = new ArrayList<>();
//			for (int i = 0; i < soggList.size(); i++) {
//				Soggetto fru = soggList.get(i);
//				if(tipiSoggettiCompatibiliAccordo.contains(fru.getTipo())){
//					soggettiListList.add("" + fru.getId());
//					soggettiListLabelList.add(fru.getTipo() + "/" + fru.getNome());
//				}
//			}
//			soggettiList = soggettiListList.toArray(new String[1]);
//			soggettiListLabel = soggettiListLabelList.toArray(new String[1]);*/
			// Non serve una vera lista. Basta avere una lista di un elemento con la fruizione in corso
			String[] soggettiList = new String [] { (idSoggettoFruitore+"") };
			String[] soggettiListLabel = new String [] { (soggettoFruitore.getTipo() + "/" + soggettoFruitore.getNome()) };

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

			//se passo dal link diretto di ripristino stato imposto il nuovo stato
			if(backToStato != null  && (actionConfirm == null || actionConfirm.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK)))
				statoPackage = backToStato;


			String nomefru = servFru.getNome();
			String tipofru = servFru.getTipo();
			IDSoggetto idSF = new IDSoggetto(tipofru, nomefru);
			Soggetto soggFru = soggettiCore.getSoggettoRegistro(idSF);
			idSoggettoFruitore = "" + soggFru.getId();
			if(statoPackage==null)
				statoPackage = servFru.getStatoPackage();
			String oldStatoPackage = servFru.getStatoPackage();	

			
			IDFruizione idFruizione = new IDFruizione();
			idFruizione.setIdServizio(idServizioObject);
			idFruizione.setIdFruitore(idSF);

			Connettore connettore = servFru.getConnettore();
			ConfigurazioneServizioAzione configurazioneServizioAzione = null;
			if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
				for (ConfigurazioneServizioAzione check : servFru.getConfigurazioneAzioneList()) {
					if(check.getAzioneList().contains(azioneConnettore)) {
						configurazioneServizioAzione = check;
						connettore = configurazioneServizioAzione.getConnettore();
						break;
					}
				}
			}

			strutsBean.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			strutsBean.consoleDynamicConfiguration =  strutsBean.protocolFactory.createDynamicConfigurationConsole();
			strutsBean.registryReader = soggettiCore.getRegistryReader(strutsBean.protocolFactory); 
			strutsBean.configRegistryReader = soggettiCore.getConfigIntegrationReader(strutsBean.protocolFactory);
			
			strutsBean.consoleConfiguration = strutsBean.consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleOperationType, apsHelper, 
					strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione  );
			strutsBean.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(strutsBean.consoleConfiguration, strutsBean.consoleOperationType);

			oldProtocolPropertyList = servFru.getProtocolPropertyList(); 

			if(strutsBean.protocolPropertiesSet == null){
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(strutsBean.protocolProperties, oldProtocolPropertyList, strutsBean.consoleOperationType);
			}

			Parameter pIdSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_FRUITORE,idSoggettoFruitore); 
			Parameter pMyId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_ID, idServizioFruitoreInt+"");
			Parameter pMyTipo = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_TIPO, myTipo);
			Parameter pMyNome = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MY_NOME, myNome);
			Parameter pIdSoggettoErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, idSoggettoErogatoreDelServizio);
			Parameter pId = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, idServizio);
			Parameter pModificaProfilo = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO, modificaProfilo+"");
			Parameter urlChange = new Parameter("", AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, pMyId, pId, pIdSoggettoErogatore,pMyTipo,pMyNome,pIdSoggettoFruitore,pModificaProfilo);
			
			String fruitoreLabel = apsHelper.getLabelNomeSoggetto(protocollo, tipofru , nomefru);
			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, idServizioFruitoreInt+"");
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_FRUITORE);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, fruitoreLabel);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, 
					URLEncoder.encode( urlChange.getValue() , "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, protocollo);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, "");
			
			String postBackElementName = apsHelper.getPostBackElementName();
			
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
						configurazioneServizioAzione!=null?configurazioneServizioAzione.getAzioneList() : null);
				
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
			
			// setto la barra del titolo
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pdOld, session);
			// Per il titolo utilizzo sempre e comunque il tipo PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE
			List<Parameter> lstParm = porteDelegateHelper.getTitoloPD(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE, idSoggettoFruitore,idServizio, idServizioFruitore);

			if(viewOnlyConnettore) {
								
				String labelPerPorta = null;
				if(accessoDaListaAPS) {
					if(!isModalitaCompleta) {
						if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
							labelPerPorta = ErogazioniCostanti.LABEL_ASPS_PORTE_DELEGATE_MODIFICA_CONNETTORE;
						} else {
							labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_DI+	porteDelegateHelper.getLabelIdServizio(asps);
						}
					}
					else {
						labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE;
					}
				}
				else {
					PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
					PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(Long.parseLong(azioneConnettoreIdPorta)); 
					labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
							PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE_DI,
							PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CONNETTORE,
							portaDelegata);
				}
				
				Parameter pConnettore = new Parameter(labelPerPorta, null);
				if(accessoDaListaAPS) {
					lstParm.set(lstParm.size()-1, pConnettore);
				}
				else {
					lstParm.add(pConnettore);
				}
			}
			else if(modificaProfilo) {
				lstParm.set(lstParm.size()-1, new Parameter(AccordiServizioParteComuneCostanti.LABEL_PARAMETRO_APC_PROTOCOLLO, null));
			}
			else {
				lstParm.set(lstParm.size()-1, new Parameter(fruitoreLabel, null));
			}
			
			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati

			if (ServletUtils.isEditModeInProgress(strutsBean.editMode)) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );


				Map<String, String> props = connettore.getProperties();

				if (endpointtype == null) {
					if ((connettore.getCustom()!=null && connettore.getCustom()) && 
							!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS) && 
							!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_FILE) &&
							!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_STATUS)) {
						endpointtype = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
						tipoconn = connettore.getTipo();
					} else
						endpointtype = connettore.getTipo();
					
					if(forceEnableConnettore &&
						TipiConnettore.DISABILITATO.toString().equals(endpointtype)) {
						forceEnableConnettore = false;
					}
				}

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
								tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaFruizione().getConnectionTimeout().intValue()+"";
							}
						}
							
						if(tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) ) {
							String v = props.get(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
							if(v!=null && !"".equals(v)){
								tempiRispostaReadTimeout = v.trim();
								tempiRispostaEnabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRispostaReadTimeout = configGenerale.getTempiRispostaFruizione().getReadTimeout().intValue()+"";
							}
						}
						
						if(tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ) {
							String v = props.get(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
							if(v!=null && !"".equals(v)){
								tempiRispostaTempoMedioRisposta = v.trim();
								tempiRispostaEnabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
							}
							else {
								tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaFruizione().getTempoMedioRisposta().intValue()+"";
							}
						}
					}
					else {
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
				
				opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, transferMode, redirectMode, httpImpl);
				
				if(tokenPolicy==null && props!=null){
					String v = props.get(CostantiDB.CONNETTORE_TOKEN_POLICY);
					if(v!=null && !"".equals(v)){
						tokenPolicy = v;
						autenticazioneToken = true;
					}
				}
				
				if (url == null) {
					url = props.get(CostantiDB.CONNETTORE_HTTP_LOCATION);
				}
				if (nome == null) {
					nome = props.get(CostantiDB.CONNETTORE_JMS_NOME);
					tipo = props.get(CostantiDB.CONNETTORE_JMS_TIPO);
					String userTmp = props.get(CostantiDB.CONNETTORE_USER);
					if(userTmp!=null && !"".equals(userTmp)){
						user = userTmp;
					}
					String passwordTmp = props.get(CostantiDB.CONNETTORE_PWD);
					if(passwordTmp!=null && !"".equals(passwordTmp)){
						password = passwordTmp;
					}
					initcont = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
					urlpgk = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
					provurl = props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
					connfact = props.get(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
					sendas = props.get(CostantiDB.CONNETTORE_JMS_SEND_AS);
				}

				autenticazioneHttp = apsHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);

				if(autenticazioneApiKey==null || StringUtils.isEmpty(autenticazioneApiKey)) {
					apiKeyHeader = props.get(CostantiDB.CONNETTORE_APIKEY_HEADER);
					apiKeyValue = props.get(CostantiDB.CONNETTORE_APIKEY);
					appIdHeader = props.get(CostantiDB.CONNETTORE_APIKEY_APPID_HEADER);
					appIdValue = props.get(CostantiDB.CONNETTORE_APIKEY_APPID);
					
					autenticazioneApiKey = apsHelper.getAutenticazioneApiKey(autenticazioneApiKey, endpointtype, apiKeyValue);
					if(ServletUtils.isCheckBoxEnabled(autenticazioneApiKey)) {
						useOAS3Names = apsHelper.isAutenticazioneApiKeyUseOAS3Names(apiKeyHeader, appIdHeader);
						useAppId = apsHelper.isAutenticazioneApiKeyUseAppId(appIdValue);
					}
					else {
						apiKeyValue=null;
						apiKeyHeader=null;
						appIdHeader=null;
						appIdValue=null;
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
				if(postBackElementName == null || "".equals(postBackElementName) ){
					// altrimenti sono checkbox
					if(httpshostverifyS==null || "".equals(httpshostverifyS)){
						httpshostverifyS = Costanti.CHECK_BOX_ENABLED_TRUE;
						httpshostverify = ServletUtils.isCheckBoxEnabled(httpshostverifyS);
					}
					if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
						httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;
						httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
					}
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

				if (props != null && connettoreStatusParams != null) {
					connettoreStatusParams.updateFromDB(props);
				}
				if(strutsBean.wsdlimpler == null){
					strutsBean.wsdlimpler = new BinaryParameter();
					strutsBean.wsdlimpler.setValue(servFru.getByteWsdlImplementativoErogatore());
				}

				if(strutsBean.wsdlimplfru == null){
					strutsBean.wsdlimplfru = new BinaryParameter();
					strutsBean.wsdlimplfru.setValue(servFru.getByteWsdlImplementativoFruitore());
				}

				if(backToStato == null){
					// preparo i campi
					List<DataElement> dati = new ArrayList<>();
					dati.add(ServletUtils.getDataElementForEditModeFinished());

					// update della configurazione 
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);

					dati = apsHelper.addHiddenFieldsToDati(tipoOp, idServizio, null, null, dati);

					dati = apsHelper.addServiziFruitoriToDati(dati, idSoggettoFruitore, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, soggettiList, soggettiListLabel, idServizio,
							idServizioFruitore,tipoOp, idSoggettoErogatoreDelServizio, "", "", nomeservizio, tiposervizio, versioneservizio, correlato,
							statoPackage,oldStatoPackage,asps.getStatoPackage(),null,validazioneDocumenti,
							null,
							null,null,null,null,null,null,null,null,null,null,null,null,
							apcCore.toMessageServiceBinding(as.getServiceBinding()), apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica()),
							azioneConnettore, azioneConnettoreIdPorta, accessoDaAPSParametro, parentPD,null,null,null,null,null,null,null,null,null,
							null,null,null,null,null,
							null,null,
							null,null,null,null,
							null,null,null,null,null,
							null,
							null,null,null);

					dati = apsHelper.addFruitoreToDati(tipoOp, versioniLabel, versioniValues, dati, 
							oldStatoPackage, idServizio, idServizioFruitore, idSoggettoErogatoreDelServizio,
							nomeservizio, tiposervizio, versioneservizio, idSoggettoFruitore,
							asps, servFru);

					if(modificaProfilo) {
						dati = apsHelper.addEndPointToDatiAsHidden(dati, apcCore.toMessageServiceBinding(as.getServiceBinding()), connettoreDebug,
								endpointtype, autenticazioneHttp,
								url, nome,
								tipo, user, password, initcont, urlpgk, provurl,
								connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, 
								httpsurl, httpstipologia, httpshostverify, 
								httpsTrustVerifyCert, httpspath, httpstipo,
								httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey, httpstipokey,
								httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
								idSoggettoErogatoreDelServizio, null, null, null, null,
								oldStatoPackage,
								proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
								tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
								opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
								requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
								requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								autenticazioneToken, tokenPolicy,
								autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
								connettoreStatusParams);
					}
					else {
						dati = apsHelper.addEndPointToDati(dati, apcCore.toMessageServiceBinding(as.getServiceBinding()), connettoreDebug, endpointtype, autenticazioneHttp,
								null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
								url, nome,
								tipo, user, password, initcont, urlpgk, provurl,
								connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, 
								httpsurl, httpstipologia, httpshostverify, 
								httpsTrustVerifyCert, httpspath, httpstipo,
								httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey, httpstipokey,
								httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
								idSoggettoErogatoreDelServizio, azioneConnettoreIdPorta, null, null, null,
								oldStatoPackage, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
								tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
								opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
								requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
								requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								autenticazioneToken, tokenPolicy, forcePDND, forceOAuth,
								listExtendedConnettore, forceEnableConnettore,
								protocollo, forceHttps, forceHttpsClient, false, false, null, null,
								autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
								connettoreStatusParams,
								postBackViaPost);
					}

					// aggiunta campi custom
					if(addPropertiesHidden) {
						dati = apsHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					}else {
						dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					}

					pd.setDati(dati);

					if(apsHelper.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(servFru.getStatoPackage())){
						pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
					}

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
							ForwardParams.CHANGE());
				}
			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziFruitoriCheckData(tipoOp,
					soggettiList, idServizio, "", "", null, "", "", idSoggettoFruitore,
					endpointtype, url, nome, tipo, user, password, initcont,
					urlpgk, provurl, connfact, sendas, strutsBean.wsdlimpler, strutsBean.wsdlimplfru,
					idServizioFruitore, httpsurl,
					httpstipologia, httpshostverify,
					httpsTrustVerifyCert, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
					tipoconn,validazioneDocumenti,backToStato,autenticazioneHttp,
					proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
					tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
					opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
					requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
					requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					null,null,null,null,null,null,null,
					null, null, null,null,null,
					autenticazioneToken, tokenPolicy,
					autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
					listExtendedConnettore);

			// updateDynamic
			if(isOk){
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
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);

				dati = apsHelper.addHiddenFieldsToDati(tipoOp, idServizio, null, null, dati);

				dati = apsHelper.addServiziFruitoriToDati(dati, idSoggettoFruitore, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, soggettiList, soggettiListLabel, idServizio,
						idServizioFruitore, tipoOp, idSoggettoErogatoreDelServizio, "", "", nomeservizio, tiposervizio, versioneservizio,  correlato,
						statoPackage,oldStatoPackage,asps.getStatoPackage(),null,validazioneDocumenti,
						null,
						null,null,null,null,null,null,null,null,null,null,null,null,
						apcCore.toMessageServiceBinding(as.getServiceBinding()), apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica()),
						azioneConnettore, azioneConnettoreIdPorta, accessoDaAPSParametro, parentPD,null,null,null,null,null,null,null,null,null,
						null,null,null,null,null,
						null,null,
						null,null,null,null,
						null,null,null,null,null,
						null,
						null,null,null);

				dati = apsHelper.addFruitoreToDati(tipoOp, versioniLabel, versioniValues, dati, 
						oldStatoPackage, idServizio, idServizioFruitore, idSoggettoErogatoreDelServizio, nomeservizio, tiposervizio, versioneservizio, idSoggettoFruitore,
						asps, servFru);

				if(modificaProfilo) {
					dati = apsHelper.addEndPointToDatiAsHidden(dati, apcCore.toMessageServiceBinding(as.getServiceBinding()), connettoreDebug,
							endpointtype, autenticazioneHttp,
							url, nome,
							tipo, user, password, initcont, urlpgk, provurl,
							connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, 
							httpsurl, httpstipologia, httpshostverify, 
							httpsTrustVerifyCert, httpspath, httpstipo,
							httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey, httpstipokey,
							httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
							tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
							idSoggettoErogatoreDelServizio, null, null, null, null,
							oldStatoPackage,
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken, tokenPolicy,
							autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
							connettoreStatusParams
							);
				}
				else {
					dati = apsHelper.addEndPointToDati(dati, apcCore.toMessageServiceBinding(as.getServiceBinding()), connettoreDebug, endpointtype, autenticazioneHttp, 
							null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
							url, nome,
							tipo, user, password, initcont, urlpgk, provurl,
							connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, 
							httpsurl, httpstipologia, httpshostverify, 
							httpsTrustVerifyCert, httpspath, httpstipo,
							httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey, httpstipokey,
							httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
							tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
							idSoggettoErogatoreDelServizio, azioneConnettoreIdPorta, null, null, null,
							oldStatoPackage, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken, tokenPolicy,  forcePDND, forceOAuth,
							listExtendedConnettore, forceEnableConnettore,
							protocollo, forceHttps, forceHttpsClient, false, false, null, null,
							autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
							connettoreStatusParams,
							postBackViaPost);
				}

				// aggiunta campi custom
				if(addPropertiesHidden) {
					dati = apsHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}else {
					dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
						ForwardParams.CHANGE());
			}

			if(actionConfirm == null &&
				backToStato != null){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				// update della configurazione 
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
						strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);

				dati = apsHelper.addHiddenFieldsToDati(tipoOp, idServizio, null, null, dati);
				
				dati = apsHelper.addServiziFruitoriToDati(dati, idSoggettoFruitore, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, soggettiList, soggettiListLabel, idServizio,
						idServizioFruitore, tipoOp, idSoggettoErogatoreDelServizio, "", "", nomeservizio, tiposervizio, versioneservizio,  correlato,
						statoPackage,oldStatoPackage,asps.getStatoPackage(),null,validazioneDocumenti,
						null,
						null,null,null,null,null,null,null,null,null,null,null,null,
						apcCore.toMessageServiceBinding(as.getServiceBinding()), apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica()),
						azioneConnettore, azioneConnettoreIdPorta, accessoDaAPSParametro, parentPD,null,null,null,null,null,null,null,null,null,
						null,null,null,null,null,
						null,null,
						null,null,null,null,
						null,null,null,null,null,
						null,
						null,null,null);

				dati = apsHelper.addFruitoreToDati(tipoOp, versioniLabel, versioniValues, dati, 
						oldStatoPackage, idServizio, idServizioFruitore, idSoggettoErogatoreDelServizio, nomeservizio, tiposervizio, versioneservizio, idSoggettoFruitore,
						asps, servFru);

				dati = apsHelper.addEndPointToDati(dati, apcCore.toMessageServiceBinding(as.getServiceBinding()), connettoreDebug, endpointtype, autenticazioneHttp, 
						null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, 
						httpsurl, httpstipologia, httpshostverify, 
						httpsTrustVerifyCert, httpspath, httpstipo,
						httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey, httpstipokey,
						httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
						idSoggettoErogatoreDelServizio, azioneConnettoreIdPorta, null, null, null,
						oldStatoPackage, true,
						isConnettoreCustomUltimaImmagineSalvata, 
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken, tokenPolicy, forcePDND, forceOAuth,
						listExtendedConnettore, forceEnableConnettore,
						protocollo, forceHttps, forceHttpsClient, false, false, null, null,
						autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
						connettoreStatusParams,
						postBackViaPost);

				dati = apsHelper.addServiziFruitoriToDatiAsHidden(dati, idSoggettoFruitore, "", "", soggettiList, soggettiListLabel, idServizio,
						idServizioFruitore, tipoOp, idSoggettoErogatoreDelServizio, "", "", nomeservizio, tiposervizio,  correlato,statoPackage,oldStatoPackage,asps.getStatoPackage(),null,validazioneDocumenti,
						azioneConnettore);

				dati = apsHelper.addFruitoreToDatiAsHidden(tipoOp, versioniLabel, versioniValues, dati, 
						oldStatoPackage, idServizio, idServizioFruitore, idSoggettoErogatoreDelServizio, nomeservizio, tiposervizio, idSoggettoFruitore);

				dati = apsHelper.addEndPointToDatiAsHidden(dati, apcCore.toMessageServiceBinding(as.getServiceBinding()), connettoreDebug,
						endpointtype, autenticazioneHttp,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, 
						httpsurl, httpstipologia, httpshostverify, 
						httpsTrustVerifyCert, httpspath, httpstipo,
						httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey, httpstipokey,
						httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
						idSoggettoErogatoreDelServizio, null, null, null, null,
						oldStatoPackage,
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken, tokenPolicy,
						autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
						connettoreStatusParams
						);
				
				if(backToStato != null) {
					// backtostato per chiudere la modifica dopo la conferma
					DataElement de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
					de.setValue(backToStato);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO);
					dati.add(de);
				}

				// aggiunta campi custom
				if(addPropertiesHidden) {
					dati = apsHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}else {
					dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					
					// aggiunta campi custom come hidden, quelli sopra vengono bruciati dal no-edit
					dati = apsHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}

				String msg = "&Egrave; stato richiesto di ripristinare lo stato dell soggetto fruitore [{0}] in operativo. Tale operazione permetter&agrave; successive modifiche all''accordo. Vuoi procedere?";
			
				String pre = Costanti.HTML_MODAL_SPAN_PREFIX;
				String post = Costanti.HTML_MODAL_SPAN_SUFFIX;
				pd.setMessage(pre + MessageFormat.format(msg, fruitoreLabel) + post, Costanti.MESSAGE_TYPE_CONFIRM);
				
				pd.setDati(dati);

				String[][] bottoni = { 
						{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX

						},
						{ Costanti.LABEL_MONITOR_BUTTON_CONFERMA,
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
							Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};

				pd.setBottoni(bottoni);
				
				// disabilito la form
				pd.disableEditMode();

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
						ForwardParams.CHANGE());

			}

			// Modifico i dati del fruitore nel db
			Connettore connettoreNew = null;
			connettoreNew = new Connettore();
			connettoreNew.setNome(connettore.getNome());
			connettoreNew.setId(connettore.getId());

			String oldConnT = connettore.getTipo();
			if ((connettore.getCustom()!=null && connettore.getCustom()) && 
					!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS) && 
					!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_FILE) &&
					!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_STATUS)){
				// mantengo vecchie proprieta connettore custom
				for(int i=0; i<connettore.sizePropertyList(); i++){
					connettoreNew.addProperty(connettore.getProperty(i));
				}
				oldConnT = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
			}
			apsHelper.fillConnettore(connettoreNew, connettoreDebug, endpointtype, oldConnT, tipoconn, url,
					nome, tipo, user, password,
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

			Fruitore fruitore = new Fruitore();
			
			fruitore.setConfigurazioneAzioneList(servFru.getConfigurazioneAzioneList());
			
			fruitore.setId(Long.valueOf(idSoggettoFruitore));
			if(configurazioneServizioAzione!=null) {
				configurazioneServizioAzione.setConnettore(connettoreNew);
				fruitore.setConnettore(servFru.getConnettore());
			}
			else {
				fruitore.setConnettore(connettoreNew);
			}
			fruitore.setTipo(tipofru);
			fruitore.setNome(nomefru);
			fruitore.setByteWsdlImplementativoErogatore(servFru.getByteWsdlImplementativoErogatore());
			fruitore.setByteWsdlImplementativoFruitore(servFru.getByteWsdlImplementativoFruitore());
			
			// Prendo i dati del soggetto erogatore del servizio
			Soggetto soggettoErogatore = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggettoErogatoreDelServizio));
			tipoSoggettoErogatore = soggettoErogatore.getTipo();
			nomeSoggettoErogatore = soggettoErogatore.getNome();

			AccordoServizioParteSpecifica serviziosp = apsCore.getAccordoServizioParteSpecifica(idServizioLong);

			// Elimino il vecchio fruitore ed aggiungo il nuovo
			
			for (int i = 0; i < serviziosp.sizeFruitoreList(); i++) {
				Fruitore tmpFru = serviziosp.getFruitore(i);
				if (tmpFru.getId().longValue() == servFru.getId().longValue()) {
					serviziosp.removeFruitore(i);
					break;
				}
			}


			// stato
			fruitore.setStatoPackage(statoPackage);

			// Check stato
			if(apsHelper.isShowGestioneWorkflowStatoDocumenti()){

				try{
					apsCore.validaStatoFruitoreAccordoServizioParteSpecifica(fruitore, serviziosp);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );

					// preparo i campi
					List<DataElement> dati = new ArrayList<>();

					dati.add(ServletUtils.getDataElementForEditModeFinished());

					// update della configurazione 
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigFruizioneAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, idFruizione);

					dati = apsHelper.addHiddenFieldsToDati(tipoOp, idServizio, null, null, dati);

					dati = apsHelper.addServiziFruitoriToDati(dati, idSoggettoFruitore, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, soggettiList, soggettiListLabel, idServizio, 
							idServizioFruitore, tipoOp, idSoggettoErogatoreDelServizio, "", "", nomeservizio, tiposervizio, versioneservizio,  
							correlato,statoPackage,oldStatoPackage,asps.getStatoPackage(),null,validazioneDocumenti,
							null,
							null,null,null,null,null,null,null,null,null,null,null,null,
							apcCore.toMessageServiceBinding(as.getServiceBinding()), apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica()),
							azioneConnettore, azioneConnettoreIdPorta, accessoDaAPSParametro, parentPD,null,null,null,null,null,null,null,null,null,
							null,null,null,null,null,
							null,null,
							null,null,null,null,
							null,null,null,null,null,
							null,
							null,null,null);

					dati = apsHelper.addFruitoreToDati(tipoOp, versioniLabel, versioniValues, dati, 
							oldStatoPackage, idServizio, idServizioFruitore, idSoggettoErogatoreDelServizio, nomeservizio, tiposervizio, versioneservizio, idSoggettoFruitore,
							asps, servFru);

					if(modificaProfilo) {
						dati = apsHelper.addEndPointToDatiAsHidden(dati, apcCore.toMessageServiceBinding(as.getServiceBinding()), connettoreDebug,
								endpointtype, autenticazioneHttp,
								url, nome,
								tipo, user, password, initcont, urlpgk, provurl,
								connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, 
								httpsurl, httpstipologia, httpshostverify, 
								httpsTrustVerifyCert, httpspath, httpstipo,
								httpspwd, httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey, httpstipokey,
								httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
								idSoggettoErogatoreDelServizio, null, null, null, null,
								oldStatoPackage,
								proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
								tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
								opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
								requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
								requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								autenticazioneToken, tokenPolicy,
								autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
								connettoreStatusParams
								);
					}
					else {
						dati = apsHelper.addEndPointToDati(dati, apcCore.toMessageServiceBinding(as.getServiceBinding()), connettoreDebug, endpointtype, autenticazioneHttp, 
								null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
								url,
								nome, tipo, user, password, initcont, urlpgk,
								provurl, connfact, sendas,
								AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,tipoOp, 
								httpsurl, httpstipologia, httpshostverify, 
								httpsTrustVerifyCert, httpspath,
								httpstipo, httpspwd, httpsalgoritmo, httpsstato,
								httpskeystore, httpspwdprivatekeytrust,
								httpspathkey, httpstipokey, httpspwdkey,
								httpspwdprivatekey, httpsalgoritmokey,
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE, idServizio, idServizioFruitore,
								idSoggettoErogatoreDelServizio, azioneConnettoreIdPorta, null, null, null,
								oldStatoPackage, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
								tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
								opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
								requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
								requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								autenticazioneToken, tokenPolicy, forcePDND, forceOAuth,
								listExtendedConnettore, forceEnableConnettore,
								protocollo, forceHttps, forceHttpsClient, false, false, null, null,
								autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
								connettoreStatusParams,
								postBackViaPost);
					}

					// aggiunta campi custom
					if(addPropertiesHidden) {
						dati = apsHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					}else {
						dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					}

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
							ForwardParams.CHANGE());
				}
			}

			//imposto properties custom
			fruitore.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(strutsBean.protocolProperties, strutsBean.consoleOperationType, oldProtocolPropertyList));
					
			serviziosp.addFruitore(fruitore);
			apsCore.setDataAggiornamentoFruitore(fruitore);
			
			String superUser = ServletUtils.getUserLoginFromSession(session);
			
			UpdateProprietaOggetto updateProprietaOggetto = null;
			
			if(azioneConnettoreIdPorta!=null && !"".equals(azioneConnettoreIdPorta)){
				updateProprietaOggetto = getUpdateProprietaOggettoSafe(apsCore, azioneConnettoreIdPorta, superUser);
			}
			
			if(updateProprietaOggetto!=null) {
				apsCore.performUpdateOperation(superUser, apsHelper.smista(), serviziosp, updateProprietaOggetto);
			}
			else {
				apsCore.performUpdateOperation(superUser, apsHelper.smista(), serviziosp);
			}

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			if(accessoDaListaAPS) {
				if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
					ErogazioniHelper erogazioniHelper = new ErogazioniHelper(request, pd, session);
					asps = apsCore.getAccordoServizioParteSpecifica(idServizioLong);
					erogazioniHelper.prepareErogazioneChange(TipoOperazione.CHANGE, asps, new IDSoggetto(fruitore.getTipo(), fruitore.getNome()));
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
				}
				
				int idLista = Liste.SERVIZI;
				
				ricerca = apsHelper.checkSearchParameters(idLista, ricerca);
				
				ricerca.addFilter(idLista, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
				
				PermessiUtente pu = ServletUtils.getUserFromSession(request, session).getPermessi();
				boolean [] permessi = new boolean[2];
				permessi[0] = pu.isServizi();
				permessi[1] = pu.isAccordiCooperazione();
				List<AccordoServizioParteSpecifica> lista2 = null;
				if(apsCore.isVisioneOggettiGlobale(superUser)){
					lista2 = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, gestioneErogatori);
				}else{
					lista2 = apsCore.soggettiServizioList(superUser, ricerca, permessi, gestioneFruitori, gestioneErogatori);
				}

				apsHelper.prepareServiziList(ricerca, lista2);
				
			}
			else if(gestioneFruitori || (PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE==parentPD)) {
				int idLista = Liste.CONFIGURAZIONE_FRUIZIONE;
				ricerca = apsHelper.checkSearchParameters(idLista, ricerca);
				IDServizio idServizioFromAccordo = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
				IDSoggetto idSoggettoFruitoreObj = new IDSoggetto();
				String tipoSoggettoFruitore = null;
				String nomeSoggettoFruitore = null;
				if(apsCore.isRegistroServiziLocale()){
					org.openspcoop2.core.registry.Soggetto soggettoFruitoreObj = soggettiCore.getSoggettoRegistro(Integer.parseInt(idSoggettoFruitore));
					tipoSoggettoFruitore = soggettoFruitoreObj.getTipo();
					nomeSoggettoFruitore = soggettoFruitoreObj.getNome();
				}else{
					org.openspcoop2.core.config.Soggetto soggettoFruitoreObj = soggettiCore.getSoggetto(Integer.parseInt(idSoggettoFruitore));
					tipoSoggettoFruitore = soggettoFruitoreObj.getTipo();
					nomeSoggettoFruitore = soggettoFruitoreObj.getNome();
				}
				idSoggettoFruitoreObj.setTipo(tipoSoggettoFruitore);
				idSoggettoFruitoreObj.setNome(nomeSoggettoFruitore);
				List<MappingFruizionePortaDelegata> lista = apsCore.serviziFruitoriMappingList(idServizioFruitoreInt, idSoggettoFruitoreObj , idServizioFromAccordo, ricerca);
				apsHelper.serviziFruitoriMappingList(lista, idServizio, idSoggettoFruitore, idSoggettoFruitoreObj, idServizioFruitore, ricerca);
			} 
			else{
				int idLista = Liste.SERVIZI_FRUITORI;
				ricerca = apsHelper.checkSearchParameters(idLista, ricerca);
				List<Fruitore> lista = apsCore.serviziFruitoriList(idServizioLong, ricerca);
				apsHelper.prepareServiziFruitoriList(lista, idServizio, ricerca);
			}
			
			ForwardParams fwP = ForwardParams.CHANGE();
			if( azioneConnettoreIdPorta!=null && !"".equals(azioneConnettoreIdPorta) && !apsHelper.isModalitaCompleta()) {
				fwP = PorteDelegateCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE;
			}
	
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			return ServletUtils.getStrutsForwardEditModeFinished( mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,fwP);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					ForwardParams.CHANGE());
		} 
	}
	
	private UpdateProprietaOggetto getUpdateProprietaOggettoSafe(AccordiServizioParteSpecificaCore apsCore, String azioneConnettoreIdPorta, String superUser) {
		try {
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(Long.parseLong(azioneConnettoreIdPorta));
			if(portaDelegata!=null) {
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(portaDelegata.getNome());
				return new UpdateProprietaOggetto(idPD, superUser);
			}
		}catch(Exception e) {
			ControlStationCore.logError(e.getMessage(),e);
		}
		return null;
	}
}
