/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.web.ctrlstat.servlet.aps.llm;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoreStatusParams;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * Servlet di modifica di un {@code ConnettoreLlmProviderRef} del container LLM corrente.
 * Replica la maschera del Connettore standard tramite
 * {@link ConnettoriHelper#addEndPointToDati} pre-popolata con i dati del provider esistente.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LlmConnettoriMultipliChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);
		PageData pd = new PageData();
		GeneralHelper generalHelper = new GeneralHelper(session);
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			apsHelper.makeMenu();

			String idAspsParam = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS);
			String idFruitoreParam = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_FRUITORE);
			String idSoggettoFruitoreParam = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_SOGGETTO_FRUITORE);
			String idPortaParam = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA);
			String idPortaDelegataParam = apsHelper.getParametroLong(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_PORTA_DELEGATA);
			String providerNome = apsHelper.getParameter(LlmConnettoriMultipliCostanti.PARAMETRO_LLM_PROVIDER_NOME);
			if (idAspsParam == null || providerNome == null) {
				throw new IllegalArgumentException("Parametri richiesti mancanti");
			}

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			PorteApplicativeCore paCore = new PorteApplicativeCore(apsCore);
			PorteDelegateCore pdCore = new PorteDelegateCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
			AccordoServizioParteSpecifica asps = LlmConnettoriMultipliUtils.loadAsps(apsCore, idAspsParam);

			boolean fruitoreCtx = LlmConnettoriMultipliUtils.isFruitoreContext(idFruitoreParam, idSoggettoFruitoreParam);
			boolean useRegistry = fruitoreCtx && (idPortaDelegataParam == null || idPortaDelegataParam.isEmpty());

			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(apc.getServiceBinding());
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());

			// Localizza il container e il ref esistente (necessario sia per pre-fill che per save)
			org.openspcoop2.core.config.Connettore containerConfig = null;
			org.openspcoop2.core.registry.Connettore containerRegistry = null;
			org.openspcoop2.core.config.ServizioApplicativo sa = null;
			Fruitore fr = null;
			org.openspcoop2.core.config.ConnettoreLlmProviderRef existingConfig = null;
			org.openspcoop2.core.registry.ConnettoreLlmProviderRef existingRegistry = null;
			if (useRegistry) {
				fr = LlmConnettoriMultipliUtils.resolveFruitore(asps, idFruitoreParam, idSoggettoFruitoreParam);
				if (fr == null || fr.getConnettore() == null || fr.getConnettore().getConnettoreLlm() == null) {
					throw new IllegalStateException("Container LLM non trovato");
				}
				containerRegistry = fr.getConnettore();
				existingRegistry = findRegistryRef(containerRegistry.getConnettoreLlm().getProviderList(), providerNome);
				if (existingRegistry == null) throw new IllegalStateException("Provider '" + providerNome + "' non trovato");
			} else {
				sa = fruitoreCtx
						? LlmConnettoriMultipliUtils.resolveServizioApplicativoFruizione(idPortaDelegataParam, pdCore, saCore)
						: LlmConnettoriMultipliUtils.resolveServizioApplicativoErogazione(asps, idPortaParam, paCore, saCore);
				if (sa == null || sa.getInvocazioneServizio() == null || sa.getInvocazioneServizio().getConnettore() == null
						|| sa.getInvocazioneServizio().getConnettore().getConnettoreLlm() == null) {
					throw new IllegalStateException("Container LLM non trovato");
				}
				containerConfig = sa.getInvocazioneServizio().getConnettore();
				existingConfig = findConfigRef(containerConfig.getConnettoreLlm().getProviderList(), providerNome);
				if (existingConfig == null) throw new IllegalStateException("Provider '" + providerNome + "' non trovato");
			}

			// === Lettura parametri dal POST (gestione postback come maschera standard) ===
			Properties parametersPOST = null;

			String llmPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_LLM_PROVIDER);
			String[] llmBindings = apsHelper.getParameterValues(ConnettoriCostanti.PARAMETRO_CONNETTORE_LLM_BINDING);

			String endpointtype = apsHelper.readEndPointType();
			String tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String connettoreDebug = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			String autenticazioneTokenS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String tokenPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			boolean forcePDND = false;
			boolean forceOAuth = false;
			boolean forceDPoP = false;

			String proxyEnabled = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxyHostname = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxyPort = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxyUsername = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxyPassword = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			String tempiRispostaEnabled = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRispostaConnectionTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRispostaReadTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRispostaTempoMedioRisposta = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);

			String transferMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transferModeChunkSize = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirectMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirectMaxHop = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String httpImpl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_HTTP_IMPL);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, transferMode, redirectMode, httpImpl);

			String user = null;
			String password = null;
			String url = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if (TipiConnettore.HTTP.toString().equals(endpointtype)) {
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			String autenticazioneApiKey = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_API_KEY);
			String apiKeyHeader = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_HEADER);
			if (StringUtils.isEmpty(apiKeyHeader)) apiKeyHeader = CostantiConnettori.DEFAULT_HEADER_API_KEY;
			String apiKeyValue = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_VALUE);
			String appIdHeader = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_HEADER);
			if (StringUtils.isEmpty(appIdHeader)) appIdHeader = CostantiConnettori.DEFAULT_HEADER_APP_ID;
			String appIdValue = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_APP_ID_VALUE);
			String useOAS3NamesTmp = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_NOMI_OAS);
			boolean useOAS3Names = (useOAS3NamesTmp != null && StringUtils.isNotEmpty(useOAS3NamesTmp))
					? ServletUtils.isCheckBoxEnabled(useOAS3NamesTmp)
					: apsHelper.isAutenticazioneApiKeyUseOAS3Names(apiKeyHeader, appIdHeader);
			String useAppIdTmp = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_API_KEY_USE_APP_ID);
			boolean useAppId = (useAppIdTmp != null && StringUtils.isNotEmpty(useAppIdTmp))
					? ServletUtils.isCheckBoxEnabled(useAppIdTmp)
					: apsHelper.isAutenticazioneApiKeyUseAppId(appIdValue);

			String httpsurl = url;
			String httpstipologia = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_SSL_TYPE);
			String httpshostverifyS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_HOST_VERIFY);
			String httpsTrustVerifyCertS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS);
			boolean httpsTrustVerifyCert = ServletUtils.isCheckBoxEnabled(httpsTrustVerifyCertS);
			String httpspath = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
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
			if (TipiConnettore.HTTPS.toString().equals(endpointtype)) {
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}
			boolean httpshostverify = httpshostverifyS != null && httpshostverifyS.equals(Costanti.CHECK_BOX_ENABLED);
			boolean httpsstato = httpsstatoS != null && httpsstatoS.equals(Costanti.CHECK_BOX_ENABLED);

			ConnettoreStatusParams connettoreStatusParams = ConnettoreStatusParams.fillFrom(apsHelper);
			org.openspcoop2.core.config.Connettore conTmp = null;
			List<ExtendedConnettore> listExtendedConnettore = ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp,
					ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_PORTA_APPLICATIVA_ADD, apsHelper,
					parametersPOST, (endpointtype == null), endpointtype);

			boolean postBackViaPost = true;

			// === Pre-fill al primo load (GET, no edit) ===
			if (apsHelper.isEditModeInProgress()) {
				org.openspcoop2.core.config.Connettore concretoPrefill = useRegistry
						? LlmConnettoriProviderRefUtils.extractConfigConnettoreFromRegistry(existingRegistry)
						: LlmConnettoriProviderRefUtils.extractConfigConnettore(existingConfig);
				if (llmPolicy == null) llmPolicy = useRegistry
						? LlmConnettoriProviderRefUtils.findRegistryProperty(existingRegistry, CostantiConnettori.CONNETTORE_LLM_POLICY)
						: LlmConnettoriProviderRefUtils.findConfigProperty(existingConfig, CostantiConnettori.CONNETTORE_LLM_POLICY);
				if (llmBindings == null || llmBindings.length == 0) {
					llmBindings = useRegistry
							? LlmConnettoriProviderRefUtils.extractRegistryBindings(existingRegistry)
							: LlmConnettoriProviderRefUtils.extractConfigBindings(existingConfig);
				}
				if (endpointtype == null) endpointtype = concretoPrefill.getTipo() != null ? concretoPrefill.getTipo() : org.openspcoop2.core.constants.TipiConnettore.HTTP.getNome();
				java.util.Map<String, String> props = concretoPrefill.getProperties();
				if (url == null) url = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTP_LOCATION);
				if (url == null) url = props.get(CostantiConnettori.CONNETTORE_LOCATION);
				httpsurl = url;

				// Debug
				if (connettoreDebug == null) {
					String v = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_DEBUG);
					if (v != null) connettoreDebug = "true".equals(v) ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
				}

				// Auth http (basic user/password)
				if (user == null) {
					String u = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_USER);
					if (u != null && !u.isEmpty()) {
						user = u;
						password = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_PWD);
					}
				}
				autenticazioneHttp = apsHelper.getAutenticazioneHttp(autenticazioneHttp, endpointtype, user);

				// Auth API Key
				if (autenticazioneApiKey == null || autenticazioneApiKey.isEmpty()) {
					String hdrV = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_APIKEY_HEADER);
					if (hdrV != null && !hdrV.isEmpty()) apiKeyHeader = hdrV;
					String valV = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_APIKEY);
					if (valV != null && !valV.isEmpty()) apiKeyValue = valV;
					String appIdHdrV = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_APIKEY_APPID_HEADER);
					if (appIdHdrV != null && !appIdHdrV.isEmpty()) appIdHeader = appIdHdrV;
					String appIdValV = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_APIKEY_APPID);
					if (appIdValV != null && !appIdValV.isEmpty()) appIdValue = appIdValV;
					autenticazioneApiKey = apsHelper.getAutenticazioneApiKey(autenticazioneApiKey, endpointtype, apiKeyValue);
					if (ServletUtils.isCheckBoxEnabled(autenticazioneApiKey)) {
						useOAS3Names = apsHelper.isAutenticazioneApiKeyUseOAS3Names(apiKeyHeader, appIdHeader);
						useAppId = apsHelper.isAutenticazioneApiKeyUseAppId(appIdValue);
					}
				}

				// Auth Token
				if (tokenPolicy == null) {
					String tp = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_TOKEN_POLICY);
					if (tp != null && !tp.isEmpty()) {
						tokenPolicy = tp;
						autenticazioneToken = true;
					}
				}

				// Proxy
				if (proxyEnabled == null) {
					String pType = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_PROXY_TYPE);
					if (pType != null && !pType.isEmpty()) {
						proxyEnabled = Costanti.CHECK_BOX_ENABLED_TRUE;
						proxyHostname = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_PROXY_HOSTNAME);
						proxyPort = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_PROXY_PORT);
						proxyUsername = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_PROXY_USERNAME);
						proxyPassword = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_PROXY_PASSWORD);
					}
				}

				// Tempi risposta (se almeno uno e' impostato)
				if (tempiRispostaEnabled == null) {
					String ct = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
					String rt = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
					String mt = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
					if ((ct != null && !ct.isEmpty()) || (rt != null && !rt.isEmpty()) || (mt != null && !mt.isEmpty())) {
						tempiRispostaEnabled = Costanti.CHECK_BOX_ENABLED_TRUE;
						if (ct != null) tempiRispostaConnectionTimeout = ct;
						if (rt != null) tempiRispostaReadTimeout = rt;
						if (mt != null) tempiRispostaTempoMedioRisposta = mt;
					}
				}

				// HTTPS: TLS + truststore + keystore (cfr. PorteApplicativeConnettoriMultipliChange:1160)
				if (TipiConnettore.HTTPS.toString().equals(endpointtype) && httpstipologia == null) {
					httpsurl = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_LOCATION);
					httpstipologia = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_SSL_TYPE);
					httpshostverifyS = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER);
					if (httpshostverifyS != null) {
						httpshostverify = Boolean.valueOf(httpshostverifyS);
					}
					httpsTrustVerifyCertS = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_TRUST_ALL_CERTS);
					if (httpsTrustVerifyCertS != null) {
						httpsTrustVerifyCert = !Boolean.valueOf(httpsTrustVerifyCertS);
					} else {
						httpsTrustVerifyCert = true;
					}
					httpspath = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
					httpstipo = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE);
					httpspwd = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
					httpsalgoritmo = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
					httpspwdprivatekeytrust = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
					httpspathkey = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
					httpstipokey = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
					httpspwdkey = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
					httpspwdprivatekey = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
					httpsalgoritmokey = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
					httpsKeyAlias = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_KEY_ALIAS);
					httpsTrustStoreCRLs = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_CRLS);
					httpsTrustStoreOCSPPolicy = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
					httpsKeyStoreBYOKPolicy = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTPS_KEY_STORE_BYOK_POLICY);
					if (httpspathkey == null) {
						httpsstato = false;
						httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
					} else {
						httpsstato = true;
						if (httpspathkey.equals(httpspath) && httpstipokey != null && httpstipokey.equals(httpstipo) &&
								((httpspwdkey != null && httpspwdkey.equals(httpspwd)) || (httpspwdkey == null && httpspwd == null))) {
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						} else {
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;
						}
					}
				}
				// Default per i campi HTTPS quando non valorizzati
				if (TipiConnettore.HTTPS.toString().equals(endpointtype)) {
					if (httpsalgoritmo == null || httpsalgoritmo.isEmpty()) httpsalgoritmo = javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm();
					if (httpsalgoritmokey == null || httpsalgoritmokey.isEmpty()) httpsalgoritmokey = javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm();
					if (httpstipologia == null || httpstipologia.isEmpty()) httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
					if (httpshostverifyS == null || httpshostverifyS.isEmpty()) httpshostverify = true;
					if (httpsTrustVerifyCertS == null || httpsTrustVerifyCertS.isEmpty()) httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
				}

				// Opzioni avanzate
				if (transferMode == null) transferMode = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
				if (transferModeChunkSize == null) transferModeChunkSize = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
				if (redirectMode == null) redirectMode = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
				if (redirectMaxHop == null) redirectMaxHop = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
				if (httpImpl == null) httpImpl = props.get(org.openspcoop2.core.constants.CostantiDB.CONNETTORE_HTTP_IMPL);
				opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, transferMode, redirectMode, httpImpl);
			}

			// Pattern breadcrumb GovWay per Change: ultimo segmento = nome dell'oggetto in modifica
			// (qui il nome del LLM Provider, es. "AWS-Bedrock"). Cfr. ConfigurazioneSystemPropertiesChange.
			String labelOggetto = useRegistry
					? LlmConnettoriProviderRefUtils.findRegistryProperty(existingRegistry, CostantiConnettori.CONNETTORE_LLM_POLICY)
					: LlmConnettoriProviderRefUtils.findConfigProperty(existingConfig, CostantiConnettori.CONNETTORE_LLM_POLICY);
			if (labelOggetto == null || labelOggetto.isEmpty()) labelOggetto = providerNome;
			ServletUtils.setPageDataTitle(pd, LlmConnettoriMultipliUtils.buildBreadcrumb(apsHelper, asps, fruitoreCtx,
					labelOggetto, true, LlmConnettoriMultipliUtils.buildListLinkParams(apsHelper)));

			if (apsHelper.isEditModeInProgress()) {
				return renderForm(mapping, request, session, gd, pd, apsHelper,
						llmPolicy, llmBindings, serviceBinding, protocollo,
						endpointtype, autenticazioneHttp, connettoreDebug,
						url, user, password,
						autenticazioneToken, tokenPolicy, forcePDND, forceOAuth, forceDPoP,
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
						httpsurl, httpstipologia, httpshostverify, httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						tipoconn, connettoreStatusParams, listExtendedConnettore,
						postBackViaPost);
			}

			boolean isOk = apsHelper.endPointCheckData(serviceBinding, protocollo, true,
					endpointtype, url, null, null,
					user, password, null, null, null, null,
					null, httpsurl, httpstipologia, httpshostverify,
					httpsTrustVerifyCert, httpspath, httpstipo, httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust, httpspathkey,
					httpstipokey, httpspwdkey,
					httpspwdprivatekey, httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
					tipoconn, autenticazioneHttp,
					proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
					tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
					opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
					null, null, null, null, null, null,
					null, null, null, null, null,
					autenticazioneToken, tokenPolicy,
					autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
					listExtendedConnettore, false, null);
			if (isOk) isOk = apsHelper.checkLLMPolicyData(true, llmPolicy);
			if (isOk
					&& LlmConnettoriMultipliUtils.existsProviderInContainer(asps,
							idFruitoreParam, idSoggettoFruitoreParam, idPortaParam, idPortaDelegataParam,
							paCore, pdCore, saCore, llmPolicy, providerNome)) {
				pd.setMessage("Esiste già un altro provider con policy '" + llmPolicy + "' configurato.");
				isOk = false;
			}
			if (!isOk) {
				return renderForm(mapping, request, session, gd, pd, apsHelper,
						llmPolicy, llmBindings, serviceBinding, protocollo,
						endpointtype, autenticazioneHttp, connettoreDebug,
						url, user, password,
						autenticazioneToken, tokenPolicy, forcePDND, forceOAuth, forceDPoP,
						proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
						tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
						opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
						autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
						httpsurl, httpstipologia, httpshostverify, httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
						httpsalgoritmo, httpsstato, httpskeystore, httpspwdprivatekeytrust, httpspathkey,
						httpstipokey, httpspwdkey, httpspwdprivatekey, httpsalgoritmokey,
						httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
						tipoconn, connettoreStatusParams, listExtendedConnettore,
						postBackViaPost);
			}

			// === Save: rebuild Connettore + replace del ref nel container ===
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			org.openspcoop2.core.config.Connettore concretoConfig = new org.openspcoop2.core.config.Connettore();
			concretoConfig.setTipo(endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM) ? tipoconn : endpointtype);
			// HTTPS: le property specifiche (truststore/keystore) vanno salvate in connettori_custom
			// per essere rilette correttamente dal driver (cfr. nota in LlmConnettoriMultipliAdd).
			if (TipiConnettore.HTTPS.toString().equals(endpointtype)) {
				concretoConfig.setCustom(true);
			}
			apsHelper.fillConnettore(concretoConfig, connettoreDebug, endpointtype, endpointtype, tipoconn, url,
					null, null, user, password,
					null, null, null, null,
					null, httpsurl, httpstipologia, httpshostverify,
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
					null, null, null, null, null, null,
					null, null, null, null, null,
					tokenPolicy,
					apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
					connettoreStatusParams,
					listExtendedConnettore);

			if (useRegistry) {
				containerRegistry.getConnettoreLlm().getProviderList().remove(existingRegistry);
				containerRegistry.getConnettoreLlm().addProvider(
						LlmConnettoriProviderRefUtils.buildRegistryRefFromConfig(concretoConfig, containerRegistry.getNome(), llmPolicy, llmBindings));
				apsCore.performUpdateOperation(userLogin, apsHelper.smista(), asps);
			} else {
				containerConfig.getConnettoreLlm().getProviderList().remove(existingConfig);
				containerConfig.getConnettoreLlm().addProvider(
						LlmConnettoriProviderRefUtils.buildConfigRefFromConfig(concretoConfig, containerConfig.getNome(), llmPolicy, llmBindings));
				saCore.performUpdateOperation(userLogin, apsHelper.smista(), sa);
			}

			pd.setMessage("Provider LLM aggiornato correttamente", Costanti.MESSAGE_TYPE_INFO);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			return LlmConnettoriMultipliUtils.redirectToList(apsHelper);

		} catch (Exception e) {
			ControlStationCore.logError("Errore esecuzione " + LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI + " Change", e);
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping,
					LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI, ForwardParams.CHANGE());
		}
	}

	private ActionForward renderForm(ActionMapping mapping, HttpServletRequest request, HttpSession session, GeneralData gd, PageData pd,
			AccordiServizioParteSpecificaHelper apsHelper,
			String llmPolicy, String[] llmBindings, ServiceBinding serviceBinding, String protocollo,
			String endpointtype, String autenticazioneHttp, String connettoreDebug,
			String url, String user, String password,
			boolean autenticazioneToken, String tokenPolicy, boolean forcePDND, boolean forceOAuth, boolean forceDPoP,
			String proxyEnabled, String proxyHostname, String proxyPort, String proxyUsername, String proxyPassword,
			String tempiRispostaEnabled, String tempiRispostaConnectionTimeout, String tempiRispostaReadTimeout, String tempiRispostaTempoMedioRisposta,
			String opzioniAvanzate, String transferMode, String transferModeChunkSize, String redirectMode, String redirectMaxHop, String httpImpl,
			String autenticazioneApiKey, boolean useOAS3Names, boolean useAppId, String apiKeyHeader, String apiKeyValue, String appIdHeader, String appIdValue,
			String httpsurl, String httpstipologia, boolean httpshostverify, boolean httpsTrustVerifyCert, String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore, String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey, String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs, String httpsTrustStoreOCSPPolicy, String httpsKeyStoreBYOKPolicy,
			String tipoconn, ConnettoreStatusParams connettoreStatusParams, List<ExtendedConnettore> listExtendedConnettore,
			boolean postBackViaPost) throws Exception {

		List<DataElement> dati = new ArrayList<>();
		dati.add(ServletUtils.getDataElementForEditModeFinished());

		// Hidden fields per preservare il contesto durante i postback (Proxy, Auth Http, ecc.)
		LlmConnettoriMultipliUtils.addHiddenContextFields(apsHelper, dati, true);

		apsHelper.addLLMProvider(dati, llmPolicy, llmBindings, TipoOperazione.CHANGE, postBackViaPost);

		apsHelper.addEndPointToDati(dati, serviceBinding, connettoreDebug, endpointtype, autenticazioneHttp,
				null, url, null, null, user, password,
				null, null, null, null, null,
				LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI, TipoOperazione.CHANGE,
				httpsurl, httpstipologia, httpshostverify,
				httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
				httpsalgoritmo, httpsstato, httpskeystore,
				httpspwdprivatekeytrust, httpspathkey,
				httpstipokey, httpspwdkey,
				httpspwdprivatekey, httpsalgoritmokey,
				httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
				tipoconn, LlmConnettoriMultipliCostanti.SERVLET_NAME_LLM_CONNETTORI_MULTIPLI_CHANGE, null, null,
				null, null, null, null, null, null, true,
				null,
				proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
				tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
				opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop, httpImpl,
				null, null, null, null, null, null,
				null, null, null, null, null,
				autenticazioneToken, tokenPolicy, forcePDND, forceOAuth, forceDPoP,
				listExtendedConnettore, true,
				protocollo, false, false,
				false, false, null, null,
				autenticazioneApiKey, useOAS3Names, useAppId, apiKeyHeader, apiKeyValue, appIdHeader, appIdValue,
				connettoreStatusParams,
				postBackViaPost);

		pd.setDati(dati);
		ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
		return ServletUtils.getStrutsForward(mapping, LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI, ForwardParams.CHANGE());
	}

	private static org.openspcoop2.core.config.ConnettoreLlmProviderRef findConfigRef(List<org.openspcoop2.core.config.ConnettoreLlmProviderRef> list, String nome) {
		if (list != null) for (org.openspcoop2.core.config.ConnettoreLlmProviderRef p : list) if (nome.equals(p.getNome())) return p;
		return null;
	}

	private static org.openspcoop2.core.registry.ConnettoreLlmProviderRef findRegistryRef(List<org.openspcoop2.core.registry.ConnettoreLlmProviderRef> list, String nome) {
		if (list != null) for (org.openspcoop2.core.registry.ConnettoreLlmProviderRef p : list) if (nome.equals(p.getNome())) return p;
		return null;
	}

	private static String findProperty(org.openspcoop2.core.config.Connettore c, String name) {
		if (c == null || c.getPropertyList() == null) return null;
		for (org.openspcoop2.core.config.Property p : c.getPropertyList()) {
			if (name.equals(p.getNome())) return p.getValore();
		}
		return null;
	}

}
