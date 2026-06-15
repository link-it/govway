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
 * Servlet di aggiunta di un {@code ConnettoreLlmProviderRef} al container LLM corrente.
 * Replica la maschera del Connettore standard tramite
 * {@link ConnettoriHelper#addEndPointToDati} (Tipo, Endpoint, Autenticazione Http/Token/API Key,
 * Proxy, Tempi Risposta, Opzioni Avanzate, Debug), preceduta dalla sezione LLM
 * (Provider + Modelli).
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LlmConnettoriMultipliAdd extends Action {

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
			if (idAspsParam == null || idAspsParam.isEmpty()) {
				throw new IllegalArgumentException("Parametro " + LlmConnettoriMultipliCostanti.PARAMETRO_LLM_ID_ASPS + " mancante");
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

			// === Lettura parametri (sezione Connettore standard) ===
			Properties parametersPOST = null;

			// LLM provider+models
			String llmPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_LLM_PROVIDER);
			String[] llmBindings = apsHelper.getParameterValues(ConnettoriCostanti.PARAMETRO_CONNETTORE_LLM_BINDING);

			// Endpoint type + auth http
			String endpointtype = apsHelper.readEndPointType();
			String tipoconn = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TIPO_PERSONALIZZATO);
			String autenticazioneHttp = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_ENDPOINT_TYPE_ENABLE_HTTP);
			String connettoreDebug = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_DEBUG);

			// Token policy
			String autenticazioneTokenS = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY_STATO);
			boolean autenticazioneToken = ServletUtils.isCheckBoxEnabled(autenticazioneTokenS);
			String tokenPolicy = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TOKEN_POLICY);
			boolean forcePDND = false;
			boolean forceOAuth = false;
			boolean forceDPoP = false;

			// Proxy
			String proxyEnabled = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_ENABLED);
			String proxyHostname = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_HOSTNAME);
			String proxyPort = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PORT);
			String proxyUsername = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_USERNAME);
			String proxyPassword = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_PROXY_PASSWORD);

			// Tempi risposta
			String tempiRispostaEnabled = apsHelper.getParametroBoolean(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_REDEFINE);
			String tempiRispostaConnectionTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_CONNECTION_TIMEOUT);
			String tempiRispostaReadTimeout = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_READ_TIMEOUT);
			String tempiRispostaTempoMedioRisposta = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_TEMPI_RISPOSTA_TEMPO_MEDIO_RISPOSTA);

			// Opzioni avanzate
			String transferMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_MODE);
			String transferModeChunkSize = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_TRANSFER_CHUNK_SIZE);
			String redirectMode = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MODE);
			String redirectMaxHop = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_REDIRECT_MAX_HOP);
			String httpImpl = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_OPZIONI_AVANZATE_HTTP_IMPL);
			String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(apsHelper, transferMode, redirectMode, httpImpl);

			// HTTP/HTTPS url + credenziali
			String user = null;
			String password = null;
			String url = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_URL);
			if (TipiConnettore.HTTP.toString().equals(endpointtype)) {
				user = apsHelper.getParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_USERNAME);
				password = apsHelper.getLockedParameter(ConnettoriCostanti.PARAMETRO_INVOCAZIONE_CREDENZIALI_AUTENTICAZIONE_PASSWORD);
			}

			// API Key
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

			// HTTPS (campi server + client)
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
			// container ha sempre tipo disabilitato; il concreto LLM e' http (default)
			if (endpointtype == null) {
				endpointtype = TipiConnettore.HTTP.getNome();
			}

			ServletUtils.setPageDataTitle(pd, LlmConnettoriMultipliUtils.buildBreadcrumb(apsHelper, asps, fruitoreCtx,
					Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, true, LlmConnettoriMultipliUtils.buildListLinkParams(apsHelper)));

			// === Primo load (parametro editMode null) -> render del form ===
			if (apsHelper.isEditModeInProgress()) {
				// Default per i campi HTTPS al primo render (cfr. PorteApplicativeConnettoriMultipliAdd:528).
				// NB: solo al primo GET. Sul POST le checkbox vuote indicano una scelta esplicita di disabilitare
				// (Verifica server / Verifica hostname), e non vanno re-defaultate altrimenti la validazione di
				// endPointCheckData rifiuterebbe il salvataggio chiedendo il Path del TrustStore.
				if (TipiConnettore.HTTPS.toString().equals(endpointtype)) {
					if (httpsalgoritmo == null || httpsalgoritmo.isEmpty()) httpsalgoritmo = javax.net.ssl.TrustManagerFactory.getDefaultAlgorithm();
					if (httpsalgoritmokey == null || httpsalgoritmokey.isEmpty()) httpsalgoritmokey = javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm();
					if (httpstipologia == null || httpstipologia.isEmpty()) httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
					if (httpstipo == null || httpstipo.isEmpty()) httpstipo = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
					if (httpstipokey == null || httpstipokey.isEmpty()) httpstipokey = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TIPOLOGIA_KEYSTORE_TYPE;
					if (httpshostverifyS == null || httpshostverifyS.isEmpty()) httpshostverify = true;
					if (httpsTrustVerifyCertS == null || httpsTrustVerifyCertS.isEmpty()) httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
				}
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

			// === Validazione ===
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
			if (isOk) {
				isOk = apsHelper.checkLLMPolicyData(true, llmPolicy);
			}
			if (isOk
					&& LlmConnettoriMultipliUtils.existsProviderInContainer(asps,
							idFruitoreParam, idSoggettoFruitoreParam, idPortaParam, idPortaDelegataParam,
							paCore, pdCore, saCore, llmPolicy, null)) {
				pd.setMessage("Esiste già un provider con policy '" + llmPolicy + "' configurato. Modificalo invece di crearne uno nuovo.");
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

			// === Save: costruisci Connettore concreto + wrap in ProviderRef + append al container ===
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			org.openspcoop2.core.config.Connettore concretoConfig = new org.openspcoop2.core.config.Connettore();
			concretoConfig.setTipo(endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM) ? tipoconn : endpointtype);
			// HTTPS: le property specifiche (truststore/keystore) vanno salvate in connettori_custom
			// per essere rilette correttamente dal driver (il branch HTTPS standalone in
			// DriverConfigurazioneDB_connettoriLIB.getConnettore richiede custom=1).
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
				Fruitore fr = LlmConnettoriMultipliUtils.resolveFruitore(asps, idFruitoreParam, idSoggettoFruitoreParam);
				if (fr == null || fr.getConnettore() == null) {
					throw new IllegalStateException("Fruitore o container LLM non trovato");
				}
				org.openspcoop2.core.registry.ConnettoreLlmProviderRef ref = LlmConnettoriProviderRefUtils
						.buildRegistryRefFromConfig(concretoConfig, fr.getConnettore().getNome(), llmPolicy, llmBindings);
				if (fr.getConnettore().getConnettoreLlm() == null) {
					fr.getConnettore().setConnettoreLlm(new org.openspcoop2.core.registry.ConnettoreLlm());
				}
				fr.getConnettore().getConnettoreLlm().addProvider(ref);
				apsCore.performUpdateOperation(userLogin, apsHelper.smista(), asps);
			} else {
				org.openspcoop2.core.config.ServizioApplicativo sa = fruitoreCtx
						? LlmConnettoriMultipliUtils.resolveServizioApplicativoFruizione(idPortaDelegataParam, pdCore, saCore)
						: LlmConnettoriMultipliUtils.resolveServizioApplicativoErogazione(asps, idPortaParam, paCore, saCore);
				if (sa == null || sa.getInvocazioneServizio() == null || sa.getInvocazioneServizio().getConnettore() == null) {
					throw new IllegalStateException("Servizio Applicativo o container LLM non trovato");
				}
				org.openspcoop2.core.config.Connettore container = sa.getInvocazioneServizio().getConnettore();
				org.openspcoop2.core.config.ConnettoreLlmProviderRef ref = LlmConnettoriProviderRefUtils
						.buildConfigRefFromConfig(concretoConfig, container.getNome(), llmPolicy, llmBindings);
				if (container.getConnettoreLlm() == null) {
					container.setConnettoreLlm(new org.openspcoop2.core.config.ConnettoreLlm());
				}
				container.getConnettoreLlm().addProvider(ref);
				saCore.performUpdateOperation(userLogin, apsHelper.smista(), sa);
			}

			pd.setMessage("Provider LLM aggiunto correttamente", Costanti.MESSAGE_TYPE_INFO);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			return LlmConnettoriMultipliUtils.redirectToList(apsHelper);

		} catch (Exception e) {
			ControlStationCore.logError("Errore esecuzione " + LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI + " Add", e);
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping,
					LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI, ForwardParams.ADD());
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
		LlmConnettoriMultipliUtils.addHiddenContextFields(apsHelper, dati, false);

		// Sezione LLM (Provider + Modelli)
		apsHelper.addLLMProvider(dati, llmPolicy, llmBindings, TipoOperazione.ADD, postBackViaPost);

		// Sezione Connettore (identica alla maschera standard, tramite addEndPointToDati)
		apsHelper.addEndPointToDati(dati, serviceBinding, connettoreDebug, endpointtype, autenticazioneHttp,
				null, url, null, null, user, password,
				null, null, null, null, null,
				LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI, TipoOperazione.ADD,
				httpsurl, httpstipologia, httpshostverify,
				httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
				httpsalgoritmo, httpsstato, httpskeystore,
				httpspwdprivatekeytrust, httpspathkey,
				httpstipokey, httpspwdkey,
				httpspwdprivatekey, httpsalgoritmokey,
				httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy, httpsKeyStoreBYOKPolicy,
				tipoconn, LlmConnettoriMultipliCostanti.SERVLET_NAME_LLM_CONNETTORI_MULTIPLI_ADD, null, null,
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
		return ServletUtils.getStrutsForward(mapping, LlmConnettoriMultipliCostanti.OBJECT_NAME_LLM_CONNETTORI_MULTIPLI, ForwardParams.ADD());
	}

}
