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

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.db.IDServizioApplicativoDB;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.OperationSintetica;
import org.openspcoop2.core.registry.beans.PortTypeSintetico;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.core.registry.driver.db.IDAccordoDB;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiHelper;
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
 * serviziChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaChange extends Action {
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		AccordiServizioParteSpecificaChangeStrutsBean strutsBean = new AccordiServizioParteSpecificaChangeStrutsBean();
		
		// Parametri Protocol Properties relativi al tipo di operazione e al tipo di visualizzazione
		strutsBean.consoleOperationType = ConsoleOperationType.CHANGE;
		
		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.CHANGE;
		List<ProtocolProperty> oldProtocolPropertyList = null;

		try {
			
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			
			// Preparo il menu
			apsHelper.makeMenu();
			
			String tipoProtocollo = apsHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PROTOCOLLO);
			
			strutsBean.editMode = apsHelper.getParametroEditMode(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
			strutsBean.protocolPropertiesSet = apsHelper.getParameter(ProtocolPropertiesCostanti.PARAMETRO_PP_SET);
			
			String id = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			String nomeservizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO);
			String tiposervizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO);
			String accordo = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO);
			String servcorr = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_SERVIZIO_CORRELATO);
			
			String endpointtype = apsHelper.readEndPointType();
			String tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO );
			String autenticazioneHttp = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String user = null;
			String password = null;
			
			String connettoreDebug = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);		
			
			String tipoSoggettoFruitore = apsHelper.getParametroTipoSoggetto(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
			String nomeSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
			IDSoggetto idSoggettoFruitore = null;
			if(tipoSoggettoFruitore!=null && !"".equals(tipoSoggettoFruitore) &&
					nomeSoggettoFruitore!=null && !"".equals(nomeSoggettoFruitore)) {
				idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
			}
			
			// token policy
			String autenticazioneTokenS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String tokenPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			boolean forcePDND = false;
			boolean forceOAuth = false;
			
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
			String httpspath = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION );
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
			
			String profilo = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROFILO);

			
			String priv = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PRIVATO);
			Boolean privato = ServletUtils.isCheckBoxEnabled(priv);
			
			String portType = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PORT_TYPE);
			String descrizione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE);
			String statoPackage = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_STATO_PACKAGE);
			
			String versione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VERSIONE);
			
			String backToStato = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO);
			String backToConfermaModificaDatiServizio = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CONFERMA_MODIFICA_DATI_SERVIZIO);
			String actionConfirm = apsHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);

			String tmpModificaAPI = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_API);
			/**boolean modificaAPI = false;
			if(tmpModificaAPI!=null) {
				modificaAPI = "true".equals(tmpModificaAPI);
			}*/
			
			String tmpModificaProfilo = apsHelper.getParametroBoolean(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
			boolean modificaProfilo = false;
			if(tmpModificaProfilo!=null) {
				modificaProfilo = "true".equals(tmpModificaProfilo);
			}
			
			String tmpCambiaAPI = apsHelper.getParametroBoolean(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CAMBIA_API);
			boolean cambiaAPI = false;
			if(tmpCambiaAPI!=null) {
				cambiaAPI = "true".equals(tmpCambiaAPI);
			}
			
			String tmpModificaDescrizione = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_DESCRIZIONE);
			boolean modificaDescrizione = false;
			if(tmpModificaDescrizione!=null) {
				modificaDescrizione = "true".equals(tmpModificaDescrizione);
			}
			String descrizioneModificata = null;
			if(modificaDescrizione) {
				descrizioneModificata = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_DESCRIZIONE_MODIFICA);
			}
			
			String tmpCambiaSoggettoErogatore = apsHelper.getParametroBoolean(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CAMBIA_SOGGETTO_EROGATORE);
			boolean cambiaErogatore = false;
			if(tmpCambiaSoggettoErogatore!=null) {
				cambiaErogatore = "true".equals(tmpCambiaSoggettoErogatore);
			}
			
			boolean addPropertiesHidden = false;
			if(!apsHelper.isModalitaCompleta() && !modificaProfilo) {
				addPropertiesHidden = true;
			}
			
			boolean validazioneDocumenti = true;
			String tmpValidazioneDocumenti = apsHelper.getParametroBoolean(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_VALIDAZIONE_DOCUMENTI);
			if(ServletUtils.isEditModeInProgress(strutsBean.editMode)){
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
			
			String erogazioneServizioApplicativoServerEnabledS = apsHelper.getParametroBoolean(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ABILITA_USO_APPLICATIVO_SERVER);
			boolean erogazioneServizioApplicativoServerEnabled = ServletUtils.isCheckBoxEnabled(erogazioneServizioApplicativoServerEnabledS);
			String erogazioneServizioApplicativoServer = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_APPLICATIVO_SERVER);
			
			String canale = apsHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE);
			String canaleStato = apsHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CANALI_CANALE_STATO);
			
			boolean postBackViaPost = true;
			
			String[] ptList = null;
			// Prendo nome, tipo e provider dal db
			// Prendo la lista di soggetti e la metto in un array
			// Prendo la lista di accordi e la metto in un array

			String provider = "";
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			String[] accordiList = null;
			String[] accordiListLabel = null;
			AccordiServizioParteSpecificaCore apsCore = null;
			SoggettiCore soggettiCore = null;
			AccordiServizioParteComuneCore apcCore = null;
			PorteApplicativeCore porteApplicativeCore = null;
			PorteDelegateCore porteDelegateCore = null;
			String nomeSoggettoErogatore = "";
			String tipoSoggettoErogatore = "";
			AccordoServizioParteSpecifica asps = null;

			String oldversioneaccordo = null;
			String oldtiposervizio = null;
			String oldnomeservizio = null;
			String oldtiposoggetto = null;
			String oldnomesoggetto = null;
			String oldStatoPackage = null;

			boolean accordoPrivato = false;
			String uriAccordo = null;

			Soggetto soggettoErogatoreID = null;
			List<String> versioniProtocollo = null;
			List<String> tipiSoggettiCompatibiliAccordo = null;
			List<String> tipiServizioCompatibiliAccordo = null;

			apsCore = new AccordiServizioParteSpecificaCore();
			soggettiCore = new SoggettiCore(apsCore);
			apcCore = new AccordiServizioParteComuneCore(apsCore);
			porteApplicativeCore = new PorteApplicativeCore(apsCore);
			porteDelegateCore = new PorteDelegateCore(apsCore);
			PddCore pddCore = new PddCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
			
			// carico i canali
			CanaliConfigurazione gestioneCanali = confCore.getCanaliConfigurazione(false);
			List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : new ArrayList<>();
			boolean gestioneCanaliEnabled = gestioneCanali != null && org.openspcoop2.core.config.constants.StatoFunzionalita.ABILITATO.equals(gestioneCanali.getStato());
			
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
			
			boolean isApplicativiServerEnabled = apsCore.isApplicativiServerEnabled(apsHelper);
			
			// La lista degli SA viene filtrata per tipo se sono abilitati gli applicativiServer.
			String tipoSA = (isApplicativiServerEnabled && gestioneErogatori) ? ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER : null;
			
			PermessiUtente pu = ServletUtils.getUserFromSession(request, session).getPermessi();

			boolean soggettoOperativo = true;
			boolean generaPACheckSoggetto = true; 
			boolean [] permessi = new boolean[2];
			permessi[0] = pu.isServizi();
			permessi[1] = pu.isAccordiCooperazione();
			
			// Servizio
			asps = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));
			if(tipoProtocollo==null) {
				tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
			}
			
			if(cambiaAPI) {
				
				List<IDAccordoDB> listaIdAPI = AccordiServizioParteSpecificaUtilities.getListaIdAPI(tipoProtocollo, userLogin, apsCore, apsHelper);
			
				IDAccordo oldIDAccodo = idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				IDAccordoDB attuale = null;
				if (!listaIdAPI.isEmpty()) {
					int i = 0;
					int remove = -1;
					for (IDAccordo oldIDAccodoCheck : listaIdAPI) {
						if(oldIDAccodoCheck.equals(oldIDAccodo)) {
							remove = i;
							break;
						}
						i++;
					}
					if(remove>=0) {
						attuale = listaIdAPI.remove(remove);
					}
				}
				
				if(attuale!=null) {
					accordiList = new String[listaIdAPI.size()+1];
					accordiListLabel = new String[listaIdAPI.size()+1];
					accordiList[0] = attuale.getId().toString();
					accordiListLabel[0] = apsHelper.getLabelIdAccordo(tipoProtocollo, attuale); // Attuale alla posizione 0
					
					if (!listaIdAPI.isEmpty()) {
						int i = 1;
						for (IDAccordoDB as : listaIdAPI) {
							accordiList[i] = as.getId().toString();
							accordiListLabel[i] = apsHelper.getLabelIdAccordo(tipoProtocollo, as);
							i++;
						}
					}
				}
				
			}
			else {
								
				boolean soloAccordiConsistentiRest = false;
				boolean soloAccordiConsistentiSoap = false;
				if(!apsHelper.isModalitaCompleta()) {
					// filtro accordi senza risorse o senza pt/operation
					soloAccordiConsistentiRest = true;
					soloAccordiConsistentiSoap = true;
				}
				
				List<IDAccordoDB> lista =  
						AccordiServizioParteComuneUtilities.idAccordiListFromPermessiUtente(apcCore, userLogin, new ConsoleSearch(true), permessi, 
								soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
				
				if (!lista.isEmpty()) {
					accordiList = new String[lista.size()];
					accordiListLabel = new String[lista.size()];
					int i = 0;
					for (IDAccordoDB as : lista) {
						accordiList[i] = as.getId().toString();
						IDSoggetto soggettoReferente = null;
						long idReferente = -1;
						if(as.getSoggettoReferenteDB()!=null && as.getSoggettoReferenteDB().getId()!=null)
							idReferente = as.getSoggettoReferenteDB().getId();
	
						if(idReferente>0){
							soggettoReferente = new IDSoggetto();
							soggettoReferente.setTipo(as.getSoggettoReferente().getTipo());
							soggettoReferente.setNome(as.getSoggettoReferente().getNome());
						}
						accordiListLabel[i] = idAccordoFactory.getUriFromValues(as.getNome(),soggettoReferente,as.getVersione());
						i++;
					}
				}
				
			}
			
			String providerSoggettoFruitore = null;
			Fruitore fruitore = null;
			if(gestioneFruitori) {
				// In questa modalità ci deve essere solo un fruitore
				// In questa modalità ci deve essere un fruitore indirizzato
				for (Fruitore check : asps.getFruitoreList()) {
					if(check.getTipo().equals(idSoggettoFruitore.getTipo()) && check.getNome().equals(idSoggettoFruitore.getNome())) {
						fruitore = check;
						break;
					}
				}
				if(fruitore==null) {
					throw new CoreException("Fruitore '"+idSoggettoFruitore.getTipo()+"/"+idSoggettoFruitore.getNome()+"' non trovato");
				}
				providerSoggettoFruitore = fruitore.getId()+"";
			}
			
			String tmpTitle = apsHelper.getLabelIdServizio(asps);
			if(gestioneFruitori) {
				tmpTitle = apsHelper.getLabelServizioFruizione(tipoProtocollo, idSoggettoFruitore, asps);
			}
			else if(gestioneErogatori) {
				tmpTitle = apsHelper.getLabelServizioErogazione(tipoProtocollo, asps);
			}
			
			Boolean isConnettoreCustomUltimaImmagineSalvata = asps.getConfigurazioneServizio()!=null &&
					asps.getConfigurazioneServizio().getConnettore()!=null &&
					asps.getConfigurazioneServizio().getConnettore().getCustom();
			
			List<ExtendedConnettore> listExtendedConnettore = 
					ServletExtendedConnettoreUtils.getExtendedConnettore(asps.getConfigurazioneServizio().getConnettore(), ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_CHANGE, apsHelper, 
							(endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
			
			// Lista port-type associati all'accordo di servizio
			// se l'accordo e' selezionato allora prendo quello selezionato
			// altrimenti il primo
			// della lista
			AccordoServizioParteComuneSintetico as = null;
			if (accordo != null && !"".equals(accordo)) {
				as = apcCore.getAccordoServizioSintetico(Long.parseLong(accordo));
				if(as==null) {
					throw new CoreException("AccordoServizioParteComune con id '"+accordo+"' non trovato");
				}
			} else {
				as = apcCore.getAccordoServizioSintetico(idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
				if(as==null) {
					throw new CoreException("AccordoServizioParteComune con id '"+asps.getAccordoServizioParteComune()+"' non trovato");
				}
				portType = asps.getPortType();
			}
			
			String canaleAPI = as != null ? as.getCanale() : null;  
		
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			org.openspcoop2.protocol.manifest.constants.InterfaceType formatoSpecifica = apcCore.formatoSpecifica2InterfaceType(as.getFormatoSpecifica());

			// Lista di Accordi Compatibili
			List<AccordoServizioParteComune> asParteComuneCompatibili = null;
			try{
				asParteComuneCompatibili = apsCore.findAccordiParteComuneBySoggettoAndNome(as.getNome(), 
						new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome()));
			}catch(Exception e){
				ControlStationCore.logError("Errore durante la ricerca degli accordi parte comune compatibili", e);
			}
			
			// Versione
			versioniProtocollo = apsCore.getVersioniProtocollo(tipoProtocollo);
			tipiSoggettiCompatibiliAccordo = soggettiCore.getTipiSoggettiGestitiProtocollo(tipoProtocollo);
			tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(tipoProtocollo,serviceBinding);

			// verifico implementazioni del servizio utilizzate nelle fruizioni e/o nelle erogazioni
			boolean moreThenOneImplementation = false;
			List<IDPortaDelegata> listMappingPD = new ArrayList<>();
			List<IDPortaApplicativa> listMappingPA = new ArrayList<>();
			if( 
					(gestioneFruitori || gestioneErogatori) 
					&& 
					(
							(tmpModificaAPI!=null && !"".equals(tmpModificaAPI))
							||
							cambiaAPI
							||
							cambiaErogatore
					)
				) {
			
				IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
				
				// Fruizioni
				for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
					IDSoggetto idSoggettoFr = new IDSoggetto(fruitoreCheck.getTipo(), fruitoreCheck.getNome());
					Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoFr);
					if(!pddCore.isPddEsterna(soggetto.getPortaDominio())){
						IDPortaDelegata idPD = porteDelegateCore.getIDPortaDelegataAssociataDefault(idServizio, idSoggettoFr);
						if(idPD!=null) {
							listMappingPD.add(idPD);
						}
					}	
				}
				
				// Erogazioni
				IDPortaApplicativa idPA = porteApplicativeCore.getIDPortaApplicativaAssociataDefault(idServizio);
				if(idPA!=null) {
					listMappingPA.add(idPA);
				}
				
				moreThenOneImplementation = (listMappingPD.size()+listMappingPA.size()) > 1;
			}
			
			// calcolo soggetti compatibili con accordi
			List<Soggetto> list = null;
			String nuovoTipoSoggettoErogatore = null;
			String nuovoNomeSoggettoErogatore = null;
			if(cambiaErogatore) {
				
				provider = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_PROVIDER_CAMBIO_EROGATORE);
				
				ConsoleSearch searchSoggetti = new ConsoleSearch(true);
				searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, tipoProtocollo);
				boolean gestioneFruitoriSoggettiErogatoriEscludiSoggettoFruitore = false;
				if(gestioneFruitori) {
					boolean filtraSoloEsterni = true;
					if(apsCore.isMultitenant() && apsCore.getMultitenantSoggettiFruizioni()!=null) {
						switch (apsCore.getMultitenantSoggettiFruizioni()) {
						case SOLO_SOGGETTI_ESTERNI:
							filtraSoloEsterni = true;
							break;
						case ESCLUDI_SOGGETTO_FRUITORE:
							filtraSoloEsterni = false;
							gestioneFruitoriSoggettiErogatoriEscludiSoggettoFruitore = true;
							break;
						case TUTTI:
							filtraSoloEsterni = false;
							break;
						}
					}
					if(filtraSoloEsterni) {
						searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
					}
				}
				if(gestioneErogatori) {
					searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
				}
				
				if(apsCore.isVisioneOggettiGlobale(userLogin)){
					list = soggettiCore.soggettiRegistroList(null, searchSoggetti);
				}else{
					list = soggettiCore.soggettiRegistroList(userLogin, searchSoggetti);
				}
				
				if(gestioneFruitoriSoggettiErogatoriEscludiSoggettoFruitore && list!=null && !list.isEmpty()) {
					for (int i = 0; i < list.size(); i++) {
						Soggetto soggettoCheck = list.get(i);
						if(soggettoCheck.getTipo().equals(tipoSoggettoFruitore) && soggettoCheck.getNome().equals(nomeSoggettoFruitore)) {
							list.remove(i);
							break;
						}
					}
				}
				if(list!=null && !list.isEmpty()) {
					long idErogatore = asps.getIdSoggetto();
					for (int i = 0; i < list.size(); i++) {
						Soggetto soggettoCheck = list.get(i);
						if(idErogatore == soggettoCheck.getId().longValue()) {
							list.remove(i);
							break;
						}
					}
				}
				if(list == null || list.isEmpty()) {
					if(gestioneFruitori) {
						pd.setMessage("Non risultano registrati altri soggetti che possano erogare API", Costanti.MESSAGE_TYPE_INFO);
					}
					else {
						pd.setMessage("Non risultano registrati altri soggetti", Costanti.MESSAGE_TYPE_INFO);
					}
					
					pd.disableEditMode();

					List<DataElement> dati = new ArrayList<>();

					dati.add(ServletUtils.getDataElementForEditModeFinished());

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
							ForwardParams.CHANGE());
				}
			}
			else {
				if(apsCore.isVisioneOggettiGlobale(userLogin)){
					list = soggettiCore.soggettiRegistroList(null, new ConsoleSearch(true));
				}else{
					list = soggettiCore.soggettiRegistroList(userLogin, new ConsoleSearch(true));
				}
			}
			
			if (list!=null && !list.isEmpty()) {
				List<String> soggettiListTmp = new ArrayList<>();
				List<String> soggettiListLabelTmp = new ArrayList<>();
				if(cambiaErogatore) {
					soggettiListTmp.add("");
					soggettiListLabelTmp.add("-");
				}
				for (Soggetto soggetto : list) {
					if(tipiSoggettiCompatibiliAccordo.contains(soggetto.getTipo())){
						String idS = soggetto.getId().toString(); 
						soggettiListTmp.add(idS);
						soggettiListLabelTmp.add(apsHelper.getLabelNomeSoggetto(tipoProtocollo, soggetto.getTipo() , soggetto.getNome()));
						if(cambiaErogatore && provider!=null && !"".equals(provider) && idS.equals(provider)) {
							nuovoTipoSoggettoErogatore = soggetto.getTipo();
							nuovoNomeSoggettoErogatore = soggetto.getNome();
						}
					}
				}
				soggettiList = soggettiListTmp.toArray(new String[1]);
				soggettiListLabel = soggettiListLabelTmp.toArray(new String[1]);
			}

			nomeSoggettoErogatore = asps.getNomeSoggettoErogatore();
			tipoSoggettoErogatore = asps.getTipoSoggettoErogatore();
			if(cambiaErogatore && nuovoNomeSoggettoErogatore!=null && nuovoTipoSoggettoErogatore!=null) {
				nomeSoggettoErogatore = nuovoNomeSoggettoErogatore;
				tipoSoggettoErogatore = nuovoTipoSoggettoErogatore;
			}

			if(asps.getVersione()!=null)
				oldversioneaccordo = asps.getVersione()+"";
			oldtiposervizio = asps.getTipo();
			oldnomeservizio = asps.getNome();
			oldtiposoggetto = asps.getTipoSoggettoErogatore();
			oldnomesoggetto = asps.getNomeSoggettoErogatore();
			oldStatoPackage = asps.getStatoPackage();		

			// aggiorno tmpTitle
			@SuppressWarnings("unused")
			String tmpVersione = IDServizioFactory.getInstance().getUriFromAccordo(asps);

			soggettoErogatoreID = soggettiCore.getSoggettoRegistro(new IDSoggetto(tipoSoggettoErogatore,nomeSoggettoErogatore));

			if(versione == null)
				versione = oldversioneaccordo;

			if(as!=null){
				accordoPrivato = as.getPrivato()!=null && as.getPrivato();
				uriAccordo = idAccordoFactory.getUriFromAccordo(as);

				if( apsCore.isShowCorrelazioneAsincronaInAccordi() &&
					portType != null && !"".equals(portType) && !"-".equals(portType)){
					PortTypeSintetico pt = null;
					for(int i=0; i<as.getPortType().size(); i++){
						if(portType.equals(as.getPortType().get(i).getNome())){
							pt = as.getPortType().get(i);
							break;
						}
					}
					boolean servizioCorrelato = false;
					if(pt!=null){
						for(int i=0; i<pt.getAzione().size(); i++){
							OperationSintetica op = pt.getAzione().get(i);
							if(op.getCorrelataServizio()!=null && !pt.getNome().equals(op.getCorrelataServizio()) && op.getCorrelata()!=null){
								servizioCorrelato = true;
								break;
							}
						}
					}
					if(servizioCorrelato){
						servcorr=Costanti.CHECK_BOX_ENABLED;
					}
					else{
						servcorr=Costanti.CHECK_BOX_DISABLED;
					}							
				}
			}

			List<PortType> portTypesTmp = apcCore.accordiPorttypeList(as.getId().intValue(), new ConsoleSearch(true));
			List<PortType> portTypes = null;
			
			if(apsHelper.isModalitaCompleta()) {
				portTypes = portTypesTmp;
			}
			else {
				// filtro pt senza op
				portTypes = new ArrayList<>();
				for (PortType portTypeCheck : portTypesTmp) {
					if(portTypeCheck.sizeAzioneList()>0) {
						portTypes.add(portTypeCheck);
					}
				}
			}
			
			
			if (!portTypes.isEmpty()) {
				ptList = new String[portTypes.size() + 1];
				ptList[0] = "-";
				int i = 1;
				for (Iterator<PortType> iterator = portTypes.iterator(); iterator.hasNext();) {
					PortType portType2 = iterator.next();
					ptList[i] = portType2.getNome();
					i++;
				}
			}
			
			// Lista dei servizi applicativi per la creazione automatica
			List<IDServizioApplicativoDB> listaIdSA = null;
			if (gestioneErogatori && (provider != null) && !provider.equals("") && !cambiaErogatore) {
				long idErogatore = Long.parseLong(provider);
				
				// I servizi applicativi da visualizzare sono quelli che hanno
				// -Integration Manager (getMessage abilitato)
				// -connettore != disabilitato
				listaIdSA = saCore.getIdServiziApplicativiWithIdErogatore(idErogatore, tipoSA, true, true);

				List<IDServizioApplicativoDB> newListaIdSA = new ArrayList<>();
				IDServizioApplicativoDB idSA = new IDServizioApplicativoDB();
				idSA.setNome("-"); // elemento nullo di default
				idSA.setIdSoggettoProprietario(new IDSoggetto("-", "-"));
				newListaIdSA.add(idSA);
				if(listaIdSA!=null && !listaIdSA.isEmpty()) {
					newListaIdSA.addAll(listaIdSA);
				}
				listaIdSA = newListaIdSA;
			}
			String [] saSoggetti = ServiziApplicativiHelper.toArray(listaIdSA);
			

			//se passo dal link diretto di ripristino stato (e poi con conferma == ok) imposto il nuovo stato
			if(backToStato != null && (actionConfirm == null || actionConfirm.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK))) 
				statoPackage = backToStato;
			
			
			strutsBean.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
			strutsBean.consoleDynamicConfiguration =  strutsBean.protocolFactory.createDynamicConfigurationConsole();
			strutsBean.registryReader = soggettiCore.getRegistryReader(strutsBean.protocolFactory); 
			strutsBean.configRegistryReader = soggettiCore.getConfigIntegrationReader(strutsBean.protocolFactory);
			IDServizio oldIdAps = apsHelper.getIDServizioFromValues(oldtiposervizio, oldnomeservizio, oldtiposoggetto,oldnomesoggetto, oldversioneaccordo);
			oldIdAps.setUriAccordoServizioParteComune(idAccordoFactory.getUriFromAccordo(as));
			oldIdAps.setPortType((portType != null && !"".equals(portType) ? portType : asps.getPortType()));
			strutsBean.consoleConfiguration = strutsBean.consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleOperationType, apsHelper, 
					strutsBean.registryReader, strutsBean.configRegistryReader, oldIdAps );
			strutsBean.protocolProperties = apsHelper.estraiProtocolPropertiesDaRequest(strutsBean.consoleConfiguration, strutsBean.consoleOperationType);

			oldProtocolPropertyList = asps.getProtocolPropertyList(); 

			if(strutsBean.protocolPropertiesSet == null){
				ProtocolPropertiesUtils.mergeProtocolPropertiesRegistry(strutsBean.protocolProperties, oldProtocolPropertyList, strutsBean.consoleOperationType);
			}

			
			Properties propertiesProprietario = new Properties();
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, id);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_ACCORDO_SERVIZIO_PARTE_SPECIFICA);
			if(uriAccordo!=null) {
				propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, uriAccordo);
			}
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, URLEncoder.encode( AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE + "?" + request.getQueryString(), "UTF-8"));
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, tipoProtocollo);
			propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_ACCORDO, "");
			
			
			
			// verifico versione change
			String postBackElementName = apsHelper.getPostBackElementName();
			boolean portTypeErased = false;
			if(postBackElementName != null ){
			
				if(postBackElementName.equalsIgnoreCase(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ACCORDO)){
					
					if(cambiaAPI) {
						portType = null;
						portTypeErased = true;
					}
					
					if(!cambiaAPI) {
						// ho modificato l'accordo (la versione)
						// verifico se la versione precedente della API era uguale alla versione attuale del servizio, modifico anche la versione del servizio se sono in standard.
						// se non esiste gia' una nuova versione del servizio
						IDAccordo oldIDAccodo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
						if(oldIDAccodo.getVersione().intValue() == asps.getVersione().intValue()) {
							String tmpNewVersion = as.getVersione().intValue()+"";
							IDServizio idApsCheck = apsHelper.getIDServizioFromValues(oldtiposervizio, oldnomeservizio, oldtiposoggetto,oldnomesoggetto, tmpNewVersion);
							if(!apsCore.existsAccordoServizioParteSpecifica(idApsCheck)) {
								versione = tmpNewVersion;
							}
						}
					}
					
				}
				
				backToConfermaModificaDatiServizio = null; // non ho ancora cliccato su salva configurazione
			}
						
			// setto la barra del titolo
			List<Parameter> lstParm = apsHelper.getTitoloAps(TipoOperazione.CHANGE, asps, gestioneFruitori, tmpTitle, null, true, tipoSoggettoFruitore, nomeSoggettoFruitore); 
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session, request).getValue();
			
			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(strutsBean.editMode)) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				if(nomeSoggettoErogatore==null) {
					nomeSoggettoErogatore = asps.getNomeSoggettoErogatore();
				}
				if(tipoSoggettoErogatore==null) {
					tipoSoggettoErogatore = asps.getTipoSoggettoErogatore();
				}
				if (servcorr == null) {
					if(TipologiaServizio.CORRELATO.equals(asps.getTipologiaServizio()))
						servcorr = Costanti.CHECK_BOX_ENABLED;
					else
						servcorr = Costanti.CHECK_BOX_DISABLED;
				}
				if (accordo == null)
					accordo = asps.getIdAccordo().toString();
				if (profilo == null)
					profilo = asps.getVersioneProtocollo();
				if (descrizione == null)
					descrizione = asps.getDescrizione();
				if(statoPackage==null)
					statoPackage = asps.getStatoPackage();

				if(modificaDescrizione &&
					(descrizioneModificata==null) 
					){
					descrizioneModificata = gestioneFruitori ? fruitore.getDescrizione() : asps.getDescrizione();
				}
				
				if(versione==null &&
					asps.getVersione()!=null) {
					versione=asps.getVersione().intValue()+"";
				}

				if(tiposervizio==null){
					tiposervizio = asps.getTipo();
				}
				if(nomeservizio==null){
					nomeservizio = asps.getNome();
				}
				
				// Controllo se il soggetto erogare appartiene ad una pdd di tipo operativo.
				IDSoggetto idSoggettoEr = new IDSoggetto( tipoSoggettoErogatore,  nomeSoggettoErogatore);
				Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoEr );
				if(pddCore.isPddEsterna(soggetto.getPortaDominio())){
					soggettoOperativo = false;
					generaPACheckSoggetto = soggettoOperativo;
				}


				Connettore connettore = asps.getConfigurazioneServizio().getConnettore();

				if ((endpointtype == null) || (url == null) || (nome == null)) {
					Map<String, String> props = connettore.getProperties();

					if (endpointtype == null) {
						if ((connettore.getCustom()!=null && connettore.getCustom()) && 
								!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS) && 
								!connettore.getTipo().equals(CostantiDB.CONNETTORE_TIPO_FILE)) {
							endpointtype = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
							tipoconn = connettore.getTipo();
						} else
							endpointtype = connettore.getTipo();
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
						String userTmp = props.get(CostantiDB.CONNETTORE_USER);
						if(userTmp!=null && !"".equals(userTmp)){
							user = userTmp;
						}
						String passwordTmp = props.get(CostantiDB.CONNETTORE_PWD);
						if(passwordTmp!=null && !"".equals(passwordTmp)){
							password = passwordTmp;
						}
						tipo = props.get(CostantiDB.CONNETTORE_JMS_TIPO);
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
									httpspwdkey.equals(httpspwd))
								httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
							else
								httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;
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

				portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());
				if(portTypeErased) {
					portType = null;
				}
				
				privato = asps.getPrivato()!=null && asps.getPrivato();
				
				if(strutsBean.wsdlimpler == null){
					strutsBean.wsdlimpler = new BinaryParameter();
					strutsBean.wsdlimpler.setValue(asps.getByteWsdlImplementativoErogatore());
				}
				
				if(strutsBean.wsdlimplfru == null){
					strutsBean.wsdlimplfru = new BinaryParameter();
					strutsBean.wsdlimplfru.setValue(asps.getByteWsdlImplementativoFruitore());
				}
				
				if(backToStato == null && backToConfermaModificaDatiServizio==null){
					// preparo i campi
					List<DataElement> dati = new ArrayList<>();
					dati.add(ServletUtils.getDataElementForEditModeFinished());
					
					// update della configurazione 
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties,
							strutsBean.registryReader, strutsBean.configRegistryReader, oldIdAps);
					
					dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

					dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, oldnomeservizio, oldtiposervizio,
							provider, tipoSoggettoErogatore, nomeSoggettoErogatore,
							soggettiList, soggettiListLabel, accordo,serviceBinding, formatoSpecifica, accordiList, accordiListLabel, servcorr, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, tipoOp, 
							id, tipiServizioCompatibiliAccordo, profilo, portType, ptList,  privato,uriAccordo, 
							descrizione, descrizioneModificata, 
							soggettoErogatoreID.getId(),statoPackage,oldStatoPackage
							,versione,versioniProtocollo,validazioneDocumenti,
							null,null,generaPACheckSoggetto,asParteComuneCompatibili,
							null,
							null,null,null,null,null,null,false,
							null,null,null,null,null,null,null,
							tipoProtocollo,null,
							null, null, providerSoggettoFruitore, tipoSoggettoFruitore, nomeSoggettoFruitore,
							null, null, null, null, null, null, null,
							null, null, null, null,
							null,null,null,null,null,null,null,null,null,null,
							null,null,null,null,null,
							null,null,
							null,null,null,null,moreThenOneImplementation, canaleStato, canaleAPI, canale, canaleList, gestioneCanaliEnabled,
							null, null, null, null, null,
							null,
							null,null,null);

					if(apsHelper.isModalitaCompleta() || (!soggettoOperativo && !gestioneFruitori)) {
					
						boolean forceEnableConnettore = false;
						/**if( apsHelper.isModalitaStandard() && !TipiConnettore.DISABILITATO.getNome().equals(endpointtype) ) {
							forceEnableConnettore = true;
						}*/
						
						dati = apsHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp,  
								null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
								url,nome, tipo, user, password, initcont, urlpgk,
								provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp,
								httpsurl, httpstipologia, httpshostverify,
								httpsTrustVerifyCert, httpspath, httpstipo, httpspwd, httpsalgoritmo,
								httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey,
								httpstipokey, httpspwdkey, 
								httpspwdprivatekey, httpsalgoritmokey,
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
								nomeservizio, tiposervizio, null, null, null,
								null, oldStatoPackage, true,
								isConnettoreCustomUltimaImmagineSalvata, 
								proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
								tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
								opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
								requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
								requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								autenticazioneToken, tokenPolicy, forcePDND, forceOAuth,
								listExtendedConnettore, forceEnableConnettore,
								tipoProtocollo, false, false, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
								erogazioneServizioApplicativoServer, saSoggetti,
								autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
								postBackViaPost);
						
					}
					else {
						dati = apsHelper.addEndPointSAServerToDatiAsHidden(dati, erogazioneServizioApplicativoServerEnabled, erogazioneServizioApplicativoServer);
						
						dati = apsHelper.addEndPointToDatiAsHidden(dati, connettoreDebug,
								endpointtype, autenticazioneHttp,
								url, nome, tipo,
								user, password, initcont, urlpgk,
								provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp,
								httpsurl, httpstipologia, httpshostverify,
								httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
								httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey,
								httpstipokey, httpspwdkey,
								httpspwdprivatekey, httpsalgoritmokey,
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
								nomeservizio, tiposervizio, null, null, null,
								null, oldStatoPackage,
								proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
								tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
								opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
								requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
								requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								autenticazioneToken, tokenPolicy,
								autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue);
						
					}
					
					// aggiunta campi custom
					if(addPropertiesHidden) {
						dati = apsHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					}
					else {
						dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					}

					pd.setDati(dati);

					if(apsHelper.isShowGestioneWorkflowStatoDocumenti() && StatiAccordo.finale.toString().equals(asps.getStatoPackage())){
						pd.setMode(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME);
					}

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
					
					return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
							ForwardParams.CHANGE());
				}
			}

			if (apsHelper.isModalitaStandard() && (nomeservizio==null || "".equals(nomeservizio))) {
				switch (serviceBinding) {
				case REST:
					// il nome del servizio e' quello dell'accordo
					nomeservizio = as.getNome();
					break;
				case SOAP:
				default:
					// il nome del servizio e' quello del porttype selezionato
					nomeservizio = portType;
					break;
				}
			}
			
			String descrizioneVerificata = descrizione;
			if(modificaDescrizione) {
				descrizioneVerificata = descrizioneModificata;
			}
			
			// Controlli sui campi immessi
			boolean isOk = apsHelper.serviziCheckData(tipoOp, soggettiList,
					accordiList, asps.getNome(), asps.getTipo(), asps.getVersione(),
					nomeservizio, tiposervizio, provider,
					nomeSoggettoErogatore, tipoSoggettoErogatore, accordo, serviceBinding, 
					servcorr, endpointtype, url, nome, tipo, user, password,
					initcont, urlpgk, provurl, connfact, sendas, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, id,
					profilo, portType,ptList,accordoPrivato,privato,
					httpsurl, httpstipologia, httpshostverify,
					httpsTrustVerifyCert, httpspath, httpstipo, httpspwd, httpsalgoritmo,
					httpsstato, httpskeystore,
					httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey, 
					httpspwdprivatekey, httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
					tipoconn,versione,validazioneDocumenti,backToStato,autenticazioneHttp,
					proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
					tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
					opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
					requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
					requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					null,null,null,null,null,
					null, null, null, null,null,null,false,
					generaPACheckSoggetto, listExtendedConnettore,
					null,null,null,null,null,null,null,
					null, null, null, null,
					tipoProtocollo,null, 
					descrizioneVerificata, tipoSoggettoFruitore, nomeSoggettoFruitore,
					autenticazioneToken, tokenPolicy,
					autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue, 
					erogazioneServizioApplicativoServerEnabled, erogazioneServizioApplicativoServer, 
					canaleStato, canale, gestioneCanaliEnabled);
			
			// updateDynamic
			if(isOk) {
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties,
						strutsBean.registryReader, strutsBean.configRegistryReader, oldIdAps);
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
					strutsBean.consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties, 
							strutsBean.registryReader, strutsBean.configRegistryReader, oldIdAps);
				}catch(ProtocolException e){
					ControlStationCore.getLog().error(e.getMessage(),e);
					pd.setMessage(e.getMessage());
					isOk = false;
				}
			}
			
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());
				if(portTypeErased) {
					portType = null;
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				// update della configurazione 
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties,
						strutsBean.registryReader, strutsBean.configRegistryReader, oldIdAps);

				dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

				dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, oldnomeservizio, oldtiposervizio, 
						provider, tipoSoggettoErogatore, nomeSoggettoErogatore, soggettiList, 
						soggettiListLabel, accordo, serviceBinding,formatoSpecifica, 
						accordiList, accordiListLabel, servcorr, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, tipoOp, 
						id, tipiServizioCompatibiliAccordo, profilo, portType, ptList, privato,uriAccordo, 
						descrizione, descrizioneModificata,  
						soggettoErogatoreID.getId(),
						statoPackage,oldStatoPackage,versione,versioniProtocollo,validazioneDocumenti,
						null,null,generaPACheckSoggetto,asParteComuneCompatibili,
						null,
						null,null,null,null,null,null,false,
						null,null,null,null,null,null,null,
						tipoProtocollo,null,
						null, null, providerSoggettoFruitore, tipoSoggettoFruitore, nomeSoggettoFruitore,
						null, null, null, null, null,null,null,
						null, null, null, null,
						null,null,null,null,null,null,null,null,null,null,
						null,null,null,null,null,
						null,null,
						null,null,null,null,moreThenOneImplementation, canaleStato, canaleAPI, canale, canaleList, gestioneCanaliEnabled,
						null,null,null,null,null,
						null,
						null,null,null);

				if(apsHelper.isModalitaCompleta() || (!soggettoOperativo && !gestioneFruitori)) {
				
					boolean forceEnableConnettore = false;
					/**if( apsHelper.isModalitaStandard() && !TipiConnettore.DISABILITATO.getNome().equals(endpointtype) ) {
						forceEnableConnettore = true;
					}*/
					
					dati = apsHelper.addEndPointToDati(dati, connettoreDebug,  endpointtype, autenticazioneHttp, 
							null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
							url, nome,
							tipo, user, password, initcont, urlpgk, provurl,
							connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, 
							httpsurl, httpstipologia, httpshostverify, 
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, 
							httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
							tipoconn,AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
							nomeservizio, tiposervizio, null, null, null,
							null, oldStatoPackage, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken, tokenPolicy, forcePDND, forceOAuth,
							listExtendedConnettore, forceEnableConnettore,
							tipoProtocollo, false, false, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
							erogazioneServizioApplicativoServer, saSoggetti,
							autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
							postBackViaPost);
					
				}
				else {
					
					dati = apsHelper.addEndPointSAServerToDatiAsHidden(dati, erogazioneServizioApplicativoServerEnabled, erogazioneServizioApplicativoServer);
					
					dati = apsHelper.addEndPointToDatiAsHidden(dati, connettoreDebug,
							endpointtype, autenticazioneHttp,
							url, nome, tipo,
							user, password, initcont, urlpgk,
							provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp,
							httpsurl, httpstipologia, httpshostverify,
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey,
							httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
							tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
							nomeservizio, tiposervizio, null, null, null,
							null, oldStatoPackage,
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken, tokenPolicy,
							autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue);
					
				}
				
				// aggiunta campi custom
				if(addPropertiesHidden) {
					dati = apsHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}
				else {
					dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.CHANGE());

			}

			// I dati dell'utente sono validi, se ha scelto di modificare lo stato da finale ad operativo visualizzo la schermata di conferma
			if( actionConfirm == null &&
				(  backToStato != null || backToConfermaModificaDatiServizio!=null)
				){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParm );

				portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());
				if(portTypeErased) {
					portType = null;
				}

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				// update della configurazione 
				strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties,
						strutsBean.registryReader, strutsBean.configRegistryReader, oldIdAps);
				
				dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

				dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, oldnomeservizio, oldtiposervizio, 
						provider, tipoSoggettoErogatore, nomeSoggettoErogatore, soggettiList, 
						soggettiListLabel, accordo, serviceBinding,formatoSpecifica, 
						accordiList, accordiListLabel, servcorr, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, tipoOp, 
						id, tipiServizioCompatibiliAccordo, profilo, portType, ptList, privato,uriAccordo, 
						descrizione, descrizioneModificata,  
						soggettoErogatoreID.getId(),
						statoPackage,oldStatoPackage,versione,versioniProtocollo,validazioneDocumenti,
						null,null,generaPACheckSoggetto,asParteComuneCompatibili,
						null,
						null,null,null,null,null,null,false,
						null,null,null,null,null,null,null,
						tipoProtocollo,null,
						null, null, providerSoggettoFruitore, tipoSoggettoFruitore, nomeSoggettoFruitore,
						null, null, null, null, null,null,null,
						null, null, null, null,
						null,null,null,null,null,null,null,null,null,null,
						null,null,null,null,null,
						null,null,
						null,null,null,null,moreThenOneImplementation, canaleStato, canaleAPI, canale, canaleList, gestioneCanaliEnabled,
						null,null,null,null,null,
						null,
						null,null,null);

				if(apsHelper.isModalitaCompleta() || (!soggettoOperativo && !gestioneFruitori)) {
					boolean forceEnableConnettore = false;
					if( apsHelper.isModalitaStandard() && !TipiConnettore.DISABILITATO.getNome().equals(endpointtype) ) {
						forceEnableConnettore = true;
					}
					
					dati = apsHelper.addEndPointToDati(dati, connettoreDebug,  endpointtype, autenticazioneHttp, 
							null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
							url, nome,
							tipo, user, password, initcont, urlpgk, provurl,
							connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, 
							httpsurl, httpstipologia,httpshostverify, 
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey, 
							httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
							tipoconn,AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
							nomeservizio, tiposervizio, null, null, null,
							null, oldStatoPackage, true,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							autenticazioneToken, tokenPolicy, forcePDND, forceOAuth,
							listExtendedConnettore, forceEnableConnettore,
							tipoProtocollo, false, false, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
							erogazioneServizioApplicativoServer, saSoggetti,
							autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
							postBackViaPost);
					
				}
				
				dati = apsHelper.addServiziToDatiAsHidden(dati, nomeservizio, tiposervizio, provider, tipoSoggettoErogatore, nomeSoggettoErogatore, soggettiList,
						soggettiListLabel, accordo, serviceBinding,accordiList, accordiListLabel, servcorr, "", "", tipoOp, 
						id, tipiServizioCompatibiliAccordo, profilo, portType, ptList, privato,uriAccordo, 
						descrizione, soggettoErogatoreID.getId(),
						statoPackage,oldStatoPackage,versione,versioniProtocollo,validazioneDocumenti,
						null,null,tipoProtocollo,generaPACheckSoggetto);

				dati = apsHelper.addEndPointSAServerToDatiAsHidden(dati, erogazioneServizioApplicativoServerEnabled, erogazioneServizioApplicativoServer);
				
				dati = apsHelper.addEndPointToDatiAsHidden(dati, connettoreDebug,
						endpointtype, autenticazioneHttp,
						url, nome,
						tipo, user, password, initcont, urlpgk, provurl,
						connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, 
						httpsurl, httpstipologia, httpshostverify, 
						httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore,
						httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, 
						httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						tipoconn,AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
						nomeservizio, tiposervizio, null, null, null,
						null, oldStatoPackage,
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
						requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
						responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
						autenticazioneToken, tokenPolicy,
						autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue);
				
				if(backToStato != null) {
					// backtostato per chiudere la modifica dopo la conferma
					DataElement de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
					de.setValue(backToStato);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_RIPRISTINA_STATO);
					dati.add(de);
				}
				if(backToConfermaModificaDatiServizio != null) {
					// backtostato per chiudere la modifica dopo la conferma
					DataElement de = new DataElement();
					de.setLabel(CostantiControlStation.LABEL_PARAMETRO_NOME);
					de.setValue(backToConfermaModificaDatiServizio);
					de.setType(DataElementType.HIDDEN);
					de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_CONFERMA_MODIFICA_DATI_SERVIZIO);
					dati.add(de);
				}
				
				// aggiunta campi custom
				if(addPropertiesHidden) {
					dati = apsHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}
				else {
					dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					
					// aggiunta campi custom come hidden, quelli sopra vengono bruciati dal no-edit
					dati = apsHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
				}
					
				StringBuilder msgSB = new StringBuilder();
				if(backToStato != null) {
					msgSB.append("&Egrave; stato richiesto di ripristinare lo stato dell''accordo [{0}] in operativo. Tale operazione permetter&agrave; successive modifiche all''accordo. Vuoi procedere?");
				}
				else if(backToConfermaModificaDatiServizio != null) {
					msgSB.append(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_MULTI_API_INFLUENZATE_MODIFICA);
					msgSB.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+"Di seguito vengono elencate tutte le configurazioni coinvolte dalla modifica. Vuoi procedere?");
					msgSB.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
					if(!listMappingPD.isEmpty()) {
						msgSB.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						if(listMappingPD.size()==1) {
							msgSB.append("La fruizione:");
						}
						else if(listMappingPD.size()>1) {
							msgSB.append("Le "+listMappingPD.size()+" fruizioni:");
						}
						msgSB.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						for (IDPortaDelegata idPortaDelegata : listMappingPD) {
							msgSB.append("- ");
							msgSB.append(apsHelper.getLabelServizioFruizione(tipoProtocollo, idPortaDelegata.getIdentificativiFruizione().getSoggettoFruitore(), idPortaDelegata.getIdentificativiFruizione().getIdServizio()));
							msgSB.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						}
					}
					if(!listMappingPA.isEmpty()) {
						msgSB.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						if(listMappingPA.size()==1) {
							msgSB.append("L&#39;erogazione:");
						}
						else if(listMappingPA.size()>1) {
							msgSB.append(listMappingPA.size()+" erogazioni:");
						}
						msgSB.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						for (IDPortaApplicativa idPortaApplicativa : listMappingPA) {
							msgSB.append("- ");
							msgSB.append(apsHelper.getLabelServizioErogazione(tipoProtocollo, idPortaApplicativa.getIdentificativiErogazione().getIdServizio()));
							msgSB.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						}
					}
				}
				String msg = msgSB.length()>0 ? msgSB.toString() : null;
				
				String pre = Costanti.HTML_MODAL_SPAN_PREFIX;
				String post = Costanti.HTML_MODAL_SPAN_SUFFIX;
				if(uriAccordo!=null) {
					pd.setMessage(pre + MessageFormat.format(msg, uriAccordo) + post, Costanti.MESSAGE_TYPE_CONFIRM);
				}
				else {
					pd.setMessage(pre + msg + post, Costanti.MESSAGE_TYPE_CONFIRM);
				}
				
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

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
						ForwardParams.CHANGE());
			}
			
			// Esco immediatamente nel caso di modifica API non confermata
			// Il caso invece del ripristina viene gestito differentemente
			if(moreThenOneImplementation && actionConfirm != null && actionConfirm.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_NO)){
				
				String superUser = ServletUtils.getUserLoginFromSession(session);
				
				ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

				List<AccordoServizioParteSpecifica> listaServizi = null;
				if(apsCore.isVisioneOggettiGlobale(superUser)){
					listaServizi = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, gestioneErogatori);
				}else{
					listaServizi = apsCore.soggettiServizioList(superUser, ricerca,permessi, gestioneFruitori, gestioneErogatori);
				}

				apsHelper.prepareServiziList(ricerca, listaServizi);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
					ErogazioniHelper erogazioniHelper = new ErogazioniHelper(request, pd, session);
					erogazioniHelper.prepareErogazioneChange(TipoOperazione.CHANGE, asps, idSoggettoFruitore);
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
				}
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
						ForwardParams.CHANGE());
			} 
			

			// Modifico i dati del servizio nel db
			
			boolean cambioAccordoAlreadyExists = false;
			if(
					(gestioneFruitori || gestioneErogatori)
					&&
					// Per adesso non gestito, vedi spiegazione nel metodo
					(AccordiServizioParteSpecificaCostanti.isModificaDatiIdentificativiVersoApsEsistente()) 
				){
				IDServizio nuovoIdAps = apsHelper.getIDServizioFromValues(tiposervizio, nomeservizio, tipoSoggettoErogatore, nomeSoggettoErogatore, versione);
				String nuovoURI = IDServizioFactory.getInstance().getUriFromIDServizio(nuovoIdAps);
				String oldURI = IDServizioFactory.getInstance().getUriFromIDServizio(oldIdAps);
				if(!nuovoURI.equals(oldURI)) {
					cambioAccordoAlreadyExists = apsCore.existsAccordoServizioParteSpecifica(nuovoIdAps);
				}
			}			
			
			asps = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));
			fruitore = null;
			if(gestioneFruitori && idSoggettoFruitore!=null) {
				// In questa modalità ci deve essere solo un fruitore
				for (Fruitore check : asps.getFruitoreList()) {
					if(check.getTipo().equals(idSoggettoFruitore.getTipo()) && check.getNome().equals(idSoggettoFruitore.getNome())) {
						fruitore = check;
						break;
					}
				}
			}
			
			
			// nuovi valori
			
			asps.setTipoSoggettoErogatore(tipoSoggettoErogatore);
			asps.setNomeSoggettoErogatore(nomeSoggettoErogatore);
			
			asps.setNome(nomeservizio);
			asps.setTipo(tiposervizio);
			asps.setDescrizione(descrizione);
			if (!"-".equals(profilo))
				asps.setVersioneProtocollo(profilo);
			else
				asps.setVersioneProtocollo(null);

			asps.setPrivato(privato);

			if (portType != null && !"".equals(portType) && !"-".equals(portType))
				asps.setPortType(portType);
			else
				asps.setPortType(null);

			// Connettore
			Connettore newConnettore = new Connettore();
			newConnettore.setId(asps.getConfigurazioneServizio().getConnettore().getId());
			newConnettore.setNome(asps.getConfigurazioneServizio().getConnettore().getNome());
			if (endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
				newConnettore.setTipo(tipoconn);
			else
				newConnettore.setTipo(endpointtype);


			String oldConnT = asps.getConfigurazioneServizio().getConnettore().getTipo();
			if ((asps.getConfigurazioneServizio().getConnettore().getCustom()!=null && asps.getConfigurazioneServizio().getConnettore().getCustom()) && 
					!asps.getConfigurazioneServizio().getConnettore().getTipo().equals(CostantiDB.CONNETTORE_TIPO_HTTPS) && 
					!asps.getConfigurazioneServizio().getConnettore().getTipo().equals(CostantiDB.CONNETTORE_TIPO_FILE)){

				oldConnT = ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM;
				// mantengo vecchie proprieta connettore custom
				for(int i=0; i<asps.getConfigurazioneServizio().getConnettore().sizePropertyList(); i++){
					newConnettore.addProperty(asps.getConfigurazioneServizio().getConnettore().getProperty(i));
				}
			}
			apsHelper.fillConnettore(newConnettore, connettoreDebug, endpointtype, oldConnT,
					tipoconn, url, nome,
					tipo, user, password, initcont, urlpgk, provurl,
					connfact, sendas, 
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
					tokenPolicy,
					apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
					listExtendedConnettore);

			asps.getConfigurazioneServizio().setConnettore(newConnettore);

			// Accordo
			as = apcCore.getAccordoServizioSintetico(Long.parseLong(accordo));
			asps.setAccordoServizioParteComune(idAccordoFactory.getUriFromAccordo(as));
			asps.setIdAccordo(as.getId());

			asps.setTipologiaServizio(((servcorr != null) && servcorr.equals(Costanti.CHECK_BOX_ENABLED)) ? TipologiaServizio.CORRELATO : TipologiaServizio.NORMALE);

			IDServizio oldIDServizioForUpdate = 
					IDServizioFactory.getInstance().getIDServizioFromValues(oldtiposervizio, oldnomeservizio,
							oldtiposoggetto, oldnomesoggetto, 
							(oldversioneaccordo!=null && !"".equals(oldversioneaccordo)) ? Integer.parseInt(oldversioneaccordo) : 1);
			asps.setOldIDServizioForUpdate(oldIDServizioForUpdate);
			
			// Versione
			if(apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(tipoProtocollo)){
				if(versione!=null && !"".equals(versione)){
					asps.setVersione(Integer.parseInt(versione));
				}
				else{
					asps.setVersione(1);
				}
			}else{
				asps.setVersione(1);
			}

			// stato
			asps.setStatoPackage(statoPackage);

			// descrizioneModificata
			if(modificaDescrizione) {
				if(gestioneFruitori) {
					if(fruitore!=null) {
						fruitore.setDescrizione(descrizioneModificata);
					}
				}
				else {
					asps.setDescrizione(descrizioneModificata);
				}
			}

			// Check stato
			if(apsHelper.isShowGestioneWorkflowStatoDocumenti() && !cambioAccordoAlreadyExists){

				try{
					boolean gestioneWsdlImplementativo = apcCore.showPortiAccesso(tipoProtocollo, serviceBinding, formatoSpecifica);
					boolean checkConnettore = !gestioneFruitori && !gestioneErogatori;
					apsCore.validaStatoAccordoServizioParteSpecifica(asps, gestioneWsdlImplementativo, checkConnettore);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParm );

					portType = (portType != null && !"".equals(portType) ? portType : asps.getPortType());

					// preparo i campi
					List<DataElement> dati = new ArrayList<>();

					dati.add(ServletUtils.getDataElementForEditModeFinished());
					
					// update della configurazione 
					strutsBean.consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteSpecifica(strutsBean.consoleConfiguration, strutsBean.consoleOperationType, apsHelper, strutsBean.protocolProperties,
							strutsBean.registryReader, strutsBean.configRegistryReader, oldIdAps);

					dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, null, null, dati);

					dati = apsHelper.addServiziToDati(dati, nomeservizio, tiposervizio, oldnomeservizio, oldtiposervizio, 
							provider, tipoSoggettoErogatore, nomeSoggettoErogatore, soggettiList,
							soggettiListLabel, accordo, serviceBinding,formatoSpecifica, 
							accordiList, accordiListLabel, servcorr, strutsBean.wsdlimpler, strutsBean.wsdlimplfru, tipoOp, 
							id, tipiServizioCompatibiliAccordo, profilo, portType, ptList, privato,uriAccordo, 
							descrizione,  descrizioneModificata,  
							soggettoErogatoreID.getId(),statoPackage,oldStatoPackage,versione,versioniProtocollo,validazioneDocumenti,
							null,null,generaPACheckSoggetto,asParteComuneCompatibili,
							null,
							null,null,null,null,null,null,false,
							null,null,null,null,null,null,null,
							tipoProtocollo,null,
							null, null, providerSoggettoFruitore, tipoSoggettoFruitore, nomeSoggettoFruitore,
							null, null, null, null, null,null,null,
							null, null, null, null,
							null,null,null,null,null,null,null,null,null,null,
							null,null,null,null,null,
							null,null,
							null,null,null,null,moreThenOneImplementation, canaleStato, canaleAPI, canale, canaleList, gestioneCanaliEnabled,
							null,null,null,null,null,
							null,
							null,null,null);

					if(apsHelper.isModalitaCompleta() || (!soggettoOperativo && !gestioneFruitori)) {
					
						boolean forceEnableConnettore = false;
						/**if( apsHelper.isModalitaStandard() && !TipiConnettore.DISABILITATO.getNome().equals(endpointtype) ) {
							forceEnableConnettore = true;
						}*/
						
						dati = apsHelper.addEndPointToDati(dati, connettoreDebug, endpointtype, autenticazioneHttp, 
								null, //(apsHelper.isModalitaCompleta() || !multitenant)?null:AccordiServizioParteSpecificaCostanti.LABEL_APS_APPLICATIVO_ESTERNO_PREFIX,
								url,
								nome, tipo, user, password, initcont, urlpgk,
								provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp, 
								httpsurl, httpstipologia, httpshostverify, 
								httpsTrustVerifyCert, httpspath, httpstipo, httpspwd, 
								httpsalgoritmo, httpsstato,
								httpskeystore, httpspwdprivatekeytrust,
								httpspathkey, httpstipokey, httpspwdkey,
								httpspwdprivatekey, httpsalgoritmokey, 
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
								nomeservizio, tiposervizio, null, null, null,
								null,
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
								tipoProtocollo, false, false, isApplicativiServerEnabled, erogazioneServizioApplicativoServerEnabled,
								erogazioneServizioApplicativoServer, saSoggetti,
								autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
								postBackViaPost);
						
					}
					else {
						dati = apsHelper.addEndPointSAServerToDatiAsHidden(dati, erogazioneServizioApplicativoServerEnabled, erogazioneServizioApplicativoServer);
						
						dati = apsHelper.addEndPointToDatiAsHidden(dati, connettoreDebug,
								endpointtype, autenticazioneHttp,
								url, nome, tipo,
								user, password, initcont, urlpgk,
								provurl, connfact, sendas, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,tipoOp,
								httpsurl, httpstipologia, httpshostverify,
								httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
								httpsalgoritmo, httpsstato, httpskeystore,
								httpspwdprivatekeytrust, httpspathkey,
								httpstipokey, httpspwdkey,
								httpspwdprivatekey, httpsalgoritmokey,
								httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
								tipoconn, AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE, id,
								nomeservizio, tiposervizio, null, null, null,
								null, oldStatoPackage,
								proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
								tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
								opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
								requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
								requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
								responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
								autenticazioneToken, tokenPolicy,
								autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue);
						
					}
					
					// aggiunta campi custom
					if(addPropertiesHidden) {
						dati = apsHelper.addProtocolPropertiesToDatiAsHidden(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					}
					else {
						dati = apsHelper.addProtocolPropertiesToDatiRegistry(dati, strutsBean.consoleConfiguration,strutsBean.consoleOperationType, strutsBean.protocolProperties,oldProtocolPropertyList,propertiesProprietario);
					}

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardGeneralError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS, 
							ForwardParams.CHANGE());
				}
			}


			String superUser = ServletUtils.getUserLoginFromSession(session);

			//imposto properties custom
			asps.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(strutsBean.protocolProperties, strutsBean.consoleOperationType, oldProtocolPropertyList));

			if(gestioneFruitori && fruitore!=null) {
				if(modificaDescrizione || modificaProfilo) {
					apsCore.setDataAggiornamentoFruitore(fruitore);
				}
			}
			else if(gestioneErogatori &&
				(modificaDescrizione || modificaProfilo) 
				){
				apsCore.setDataAggiornamentoServizio(asps); // per evitare l'aggiornamento dei fruitori
			}
			
			List<Object> oggettiDaAggiornare = AccordiServizioParteSpecificaUtilities.getOggettiDaAggiornare(asps, apsCore);
			
			// eseguo l'aggiornamento
			apsCore.performUpdateOperation(superUser, apsHelper.smista(), oggettiDaAggiornare.toArray());

			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			String newUri = IDServizioFactory.getInstance().getUriFromAccordo(asps);
			String oldUri = IDServizioFactory.getInstance().getUriFromIDServizio(asps.getOldIDServizioForUpdate());
			if (!newUri.equals(oldUri) || cambiaAPI) {
				ServletUtils.removeRisultatiRicercaFromSession(request, session, Liste.SERVIZI);
			}
			
			List<AccordoServizioParteSpecifica> listaServizi = null;
			if(apsCore.isVisioneOggettiGlobale(superUser)){
				listaServizi = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, gestioneErogatori);
			}else{
				listaServizi = apsCore.soggettiServizioList(superUser, ricerca,permessi, gestioneFruitori, gestioneErogatori);
			}

			apsHelper.prepareServiziList(ricerca, listaServizi);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
				ErogazioniHelper erogazioniHelper = new ErogazioniHelper(request, pd, session);
				erogazioniHelper.prepareErogazioneChange(TipoOperazione.CHANGE, asps, idSoggettoFruitore);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
			}
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS,
					ForwardParams.CHANGE());
		}  

	}
}
