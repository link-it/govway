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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiAttributeAuthority;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoDatiRichieste;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Attachments;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaleConfigurazioneNodo;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.CorsConfigurazioneHeaders;
import org.openspcoop2.core.config.CorsConfigurazioneMethods;
import org.openspcoop2.core.config.CorsConfigurazioneOrigin;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.IndirizzoRisposta;
import org.openspcoop2.core.config.InoltroBusteNonRiscontrate;
import org.openspcoop2.core.config.IntegrationManager;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneControl;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.Risposte;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.ValidazioneBuste;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.AlgoritmoCache;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaApplicativaSoggettiFruitori;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettiErogatori;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoConnessioneRisposte;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.constants.ValidazioneBusteTipoControllo;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.controllo_traffico.constants.CacheAlgorithm;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.MultitenantSoggettiErogazioni;
import org.openspcoop2.web.ctrlstat.costanti.MultitenantSoggettiFruizioni;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedFormServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.servlet.AbstractServletNewWindowChangeExtended;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * configurazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneGenerale extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {


			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);
			ConfigurazioneCore confCore = new ConfigurazioneCore();

			List<IExtendedFormServlet> extendedServletList = confCore.getExtendedServletConfigurazione();

			String inoltromin = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_INOLTRO_MIN);
			String stato = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO);
			String controllo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO);
			String severita = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
			String severitaLog4j = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
			String integman = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_INTEGMAN);
			String nomeintegman = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_NOME_INTEGMAN);
			String profcoll = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROFILO_COLLABORAZIONE);
			String connessione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONNESSIONE);
			String utilizzo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_UTILIZZO);
			String validman = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_VALIDMAN);
			String gestman = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_GESTMAN);
			String registrazioneTracce = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
			String dumpPD = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
			String dumpPA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);
			String xsd = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_XSD);
			String tipoValidazione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_TIPO_VALIDAZIONE);
			String applicaMTOM = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_APPLICA_MTOM);

			String configurazioneCachesTmp = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CACHES);
			boolean isAllHiddenConfigurazione = ServletUtils.isCheckBoxEnabled(configurazioneCachesTmp);
			boolean isAllHiddenCache = !isAllHiddenConfigurazione;
			
			String statoCacheRegistry = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_REGISTRY);
			String dimensionecacheRegistry = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_REGISTRY);
			String algoritmocacheRegistry = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_REGISTRY);
			String idlecacheRegistry = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_REGISTRY);
			String lifecacheRegistry = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_REGISTRY);
			
			String statoCacheConfig = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONFIG);
			String dimensionecacheConfig = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONFIG);
			String algoritmocacheConfig = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONFIG);
			String idlecacheConfig = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONFIG);
			String lifecacheConfig = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONFIG);

			String statoCacheAuthz = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHZ);
			String dimensionecacheAuthz = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHZ);
			String algoritmocacheAuthz = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHZ);
			String idlecacheAuthz = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHZ);
			String lifecacheAuthz = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHZ);

			String statoCacheAuthn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHN);
			String dimensionecacheAuthn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHN);
			String algoritmocacheAuthn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHN);
			String idlecacheAuthn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHN);
			String lifecacheAuthn = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHN);

			String statoCacheToken = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_TOKEN);
			String dimensionecacheToken = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_TOKEN);
			String algoritmocacheToken = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_TOKEN);
			String idlecacheToken = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_TOKEN);
			String lifecacheToken = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_TOKEN);
			
			String statoCacheAA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_ATTRIBUTE_AUTHORITY);
			String dimensionecacheAA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_ATTRIBUTE_AUTHORITY);
			String algoritmocacheAA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_ATTRIBUTE_AUTHORITY);
			String idlecacheAA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_ATTRIBUTE_AUTHORITY);
			String lifecacheAA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_ATTRIBUTE_AUTHORITY);
			
			String statoCacheKeystore = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_KEYSTORE);
			String dimensionecacheKeystore = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_KEYSTORE);
			String algoritmocacheKeystore = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_KEYSTORE);
			String idlecacheKeystore = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_KEYSTORE);
			String lifecacheKeystore = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_KEYSTORE);
			String crlLifecacheKeystore = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CRL_LIFE_CACHE_KEYSTORE);

			String statoCacheControlloTraffico = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONTROLLO_TRAFFICO);
			String dimensionecacheControlloTraffico = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONTROLLO_TRAFFICO);
			String algoritmocacheControlloTraffico = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONTROLLO_TRAFFICO);
			String idlecacheControlloTraffico = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONTROLLO_TRAFFICO);
			String lifecacheControlloTraffico = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONTROLLO_TRAFFICO);
			
			String statoCacheRisposte = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_RISPOSTE);
			String dimensionecacheRisposte = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_RISPOSTE);
			String algoritmocacheRisposte = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_RISPOSTE);
			String idlecacheRisposte = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_RISPOSTE);
			String lifecacheRisposte = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_RISPOSTE);

			String statoCacheConsegna = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONSEGNA);
			String dimensionecacheConsegna = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONSEGNA);
			String algoritmocacheConsegna = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONSEGNA);
			String idlecacheConsegna = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONSEGNA);
			String lifecacheConsegna = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONSEGNA);
			
			String statoCachEdatiRichieste = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_DATI_RICHIESTE);
			String dimensionecachEdatiRichieste = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_DATI_RICHIESTE);
			String algoritmocachEdatiRichieste = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_DATI_RICHIESTE);
			String idlecachEdatiRichieste = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_DATI_RICHIESTE);
			String lifecachEdatiRichieste = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_DATI_RICHIESTE);
			
			String multitenantStatoTmp = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_MULTITENANT_STATO);
			boolean multitenantEnabled = CostantiConfigurazione.ABILITATO.getValue().equals(multitenantStatoTmp);
			String multitenantSoggettiFruizioni = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_MULTITENANT_FRUIZIONI_SOGGETTO_EROGATORE);
			String multitenantSoggettiErogazioni = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_MULTITENANT_EROGAZIONI_SOGGETTI_FRUITORI);

			String corsStatoTmp = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CORS_STATO);
			boolean corsStato = ServletUtils.isCheckBoxEnabled(corsStatoTmp); 
			String corsTipoTmp = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CORS_TIPO);
			TipoGestioneCORS corsTipo = corsTipoTmp != null ? TipoGestioneCORS.toEnumConstant(corsTipoTmp) : TipoGestioneCORS.GATEWAY;
			String corsAllAllowOriginsTmp = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS);
			boolean corsAllAllowOrigins = ServletUtils.isCheckBoxEnabled(corsAllAllowOriginsTmp);
			String corsAllAllowHeadersTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_HEADERS);
			boolean corsAllAllowHeaders = ServletUtils.isCheckBoxEnabled(corsAllAllowHeadersTmp);
			String corsAllAllowMethodsTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_METHODS);
			boolean corsAllAllowMethods = ServletUtils.isCheckBoxEnabled(corsAllAllowMethodsTmp);
			String corsAllowHeaders =  confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS);
			String corsAllowOrigins =  confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS);
			String corsAllowMethods =  confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS);
			String corsAllowCredentialTmp = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_CREDENTIALS);
			boolean corsAllowCredential =  ServletUtils.isCheckBoxEnabled(corsAllowCredentialTmp);
			String corsExposeHeaders = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS);
			String corsMaxAgeTmp = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE);
			boolean corsMaxAge =  ServletUtils.isCheckBoxEnabled(corsMaxAgeTmp);
			String corsMaxAgeSecondsTmp = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CORS_MAX_AGE_SECONDS);
			int corsMaxAgeSeconds = -1;
			if(corsMaxAgeSecondsTmp != null) {
				try {
					corsMaxAgeSeconds = Integer.parseInt(corsMaxAgeSecondsTmp);
				}catch(Exception e) {} 
			}
			
			String responseCachingEnabledTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO);
			boolean responseCachingEnabled = ServletUtils.isCheckBoxEnabled(responseCachingEnabledTmp);
			
			String responseCachingSecondsTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_TIMEOUT);
			int responseCachingSeconds = 1;
			if(responseCachingSecondsTmp != null) {
				try {
					responseCachingSeconds = Integer.parseInt(responseCachingSecondsTmp);
				}catch(Exception e) {} 
			}
			
			
			String responseCachingMaxResponseSizeTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE);
			boolean responseCachingMaxResponseSize = ServletUtils.isCheckBoxEnabled(responseCachingMaxResponseSizeTmp);
			
			String responseCachingMaxResponseSizeBytesTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_MAX_RESPONSE_SIZE_BYTES);
			long responseCachingMaxResponseSizeBytes = 1;
			if(responseCachingMaxResponseSizeBytesTmp != null) {
				try {
					responseCachingMaxResponseSizeBytes = Integer.parseInt(responseCachingMaxResponseSizeBytesTmp);
				}catch(Exception e) {} 
			}
			String responseCachingDigestUrlInvocazioneTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_URI_INVOCAZIONE);
			boolean responseCachingDigestUrlInvocazione = ServletUtils.isCheckBoxEnabled(responseCachingDigestUrlInvocazioneTmp);
			String responseCachingDigestHeadersTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS);
			boolean responseCachingDigestHeaders = ServletUtils.isCheckBoxEnabled(responseCachingDigestHeadersTmp);
			String responseCachingDigestPayloadTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_PAYLOAD);
			boolean responseCachingDigestPayload = ServletUtils.isCheckBoxEnabled(responseCachingDigestPayloadTmp);
			String responseCachingDigestHeadersNomiHeaders = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_HEADERS_NOMI_HEADERS);
			StatoFunzionalitaCacheDigestQueryParameter responseCachingDigestQueryParameter = null;
			String responseCachingDigestQueryParameterTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS);
			if(responseCachingDigestQueryParameterTmp!=null && !"".equals(responseCachingDigestQueryParameterTmp)) {
				responseCachingDigestQueryParameter = StatoFunzionalitaCacheDigestQueryParameter.toEnumConstant(responseCachingDigestQueryParameterTmp, true);
			}
			String responseCachingDigestNomiParametriQuery = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_RESPONSE_DIGEST_QUERY_PARAMETERS_NOMI);
			
			String responseCachingCacheControlNoCacheTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_CACHE);
			boolean responseCachingCacheControlNoCache = ServletUtils.isCheckBoxEnabled(responseCachingCacheControlNoCacheTmp);
			String responseCachingCacheControlMaxAgeTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_MAX_AGE);
			boolean responseCachingCacheControlMaxAge = ServletUtils.isCheckBoxEnabled(responseCachingCacheControlMaxAgeTmp);
			String responseCachingCacheControlNoStoreTmp = confHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CACHE_CONTROL_NO_STORE);
			boolean responseCachingCacheControlNoStore = ServletUtils.isCheckBoxEnabled(responseCachingCacheControlNoStoreTmp);

			Boolean confPersB = ServletUtils.getConfigurazioniPersonalizzateFromSession(session); 
			String confPers = confPersB ? "true" : "false";
			Configurazione configurazione = confCore.getConfigurazioneGenerale();
			ResponseCachingConfigurazioneGenerale responseCachingGenerale = configurazione.getResponseCaching();
			ResponseCachingConfigurazione cachingConfigurazione = responseCachingGenerale != null ? responseCachingGenerale.getConfigurazione() : null;
			boolean visualizzaLinkConfigurazioneRegola = confHelper.isResponseCachingAbilitato(cachingConfigurazione) && responseCachingEnabled;
			String servletResponseCachingConfigurazioneRegolaList = ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_LIST;
			List<Parameter> paramsResponseCachingConfigurazioneRegolaList = new ArrayList<>();
			int numeroResponseCachingConfigurazioneRegola = confHelper.numeroRegoleResponseCaching(cachingConfigurazione);
			List<ResponseCachingConfigurazioneRegola> listaRegoleCachingConfigurazione = cachingConfigurazione != null ?  cachingConfigurazione.getRegolaList() : null;
			int numeroRegoleProxyPass = confHelper.numeroRegoleProxyPass(configurazione.getUrlInvocazione());
			int numeroArchiviPlugins = confHelper.numeroPluginsRegistroArchivi();
			int numeroClassiPlugins = confHelper.numeroPluginsRegistroClassi();
			
			String canaliEnabledTmp = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_STATO);
			boolean canaliEnabled = ServletUtils.isCheckBoxEnabled(canaliEnabledTmp);
			CanaliConfigurazione gestioneCanali = configurazione.getGestioneCanali();
			List<CanaleConfigurazione> canaleList = gestioneCanali != null ? gestioneCanali.getCanaleList() : null;
			List<CanaleConfigurazioneNodo> nodoList = gestioneCanali != null ? gestioneCanali.getNodoList() : null;
			int numeroCanali = confHelper.numeroCanali(gestioneCanali);
			int numeroNodi = confHelper.numeroNodi(gestioneCanali);
			String canaliNome = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_NOME);
			String canaliDescrizione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_DESCRIZIONE);
			String canaliDefault = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CANALI_DEFAULT);

			String urlInvocazionePA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PA);
			String urlInvocazionePD = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_PROTOCOLLO_PREFIX_URL_INVOCAZIONE_PD);
			
			List<ExtendedInfo> extendedBeanList = new ArrayList<>();
			if(extendedServletList!=null && !extendedServletList.isEmpty()){
				for (IExtendedFormServlet extendedServlet : extendedServletList) {

					if(extendedServlet==null) {
						throw new Exception("IExtendedFormServlet is null in list");
					}
					
					ExtendedInfo ei = new ExtendedInfo();
					ei.extendedServlet = extendedServlet;
					DBManager dbManager = null;
					Connection con = null;
					try{
						dbManager = DBManager.getInstance();
						con = dbManager.getConnection();
						ei.extendedBean = extendedServlet.getExtendedBean(con, "SingleInstance");
					}finally{
						dbManager.releaseConnection(con);
					}

					ei.extended = false;
					ei.extendedToNewWindow = false;
					if(extendedServlet!=null && extendedServlet.showExtendedInfoUpdate(request, session)){
						ei.extendedBean = extendedServlet.readHttpParameters(configurazione, TipoOperazione.CHANGE,  ei.extendedBean, request);
						ei.extended = true;
						ei.extendedToNewWindow = extendedServlet.extendedUpdateToNewWindow(request, session);
					}

					extendedBeanList.add(ei);

				}
			}



			// Preparo il menu
			confHelper.makeMenu();

			// setto la barra del titolo
			String title = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE;
			if(isAllHiddenConfigurazione) {
				title = ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE;
			}
			ServletUtils.setPageDataTitle_ServletFirst(pd, title, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE);

			// Se idhid != null, modifico i dati della porta di dominio nel
			// db
			if (!confHelper.isEditModeInProgress()) {

				// Controlli sui campi immessi
				boolean isOk = confHelper.configurazioneCheckData();
				if (isOk) {
					if(extendedBeanList!=null && !extendedBeanList.isEmpty()){
						for (ExtendedInfo ei : extendedBeanList) {
							if(ei.extended && !ei.extendedToNewWindow){
								try{
									ei.extendedServlet.checkDati(TipoOperazione.CHANGE, confHelper, confCore, configurazione, ei.extendedBean);
								}catch(Exception e){
									isOk = false;
									pd.setMessage(e.getMessage());
								}
							}
						}
					}
				}
				if (!isOk) {
					// preparo i campi
					List<DataElement> dati = new ArrayList<>();

					dati.add(ServletUtils.getDataElementForEditModeFinished());

					DataElement de = new DataElement();
					de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CACHES);
					de.setValue(configurazioneCachesTmp);
					de.setType(DataElementType.HIDDEN);
					dati.add(de);
					
					dati = confHelper.addConfigurazioneToDati( isAllHiddenConfigurazione, 
							inoltromin, stato, controllo, severita, severitaLog4j, integman, nomeintegman, profcoll, 
							connessione, utilizzo, validman, gestman, registrazioneTracce, dumpPD, dumpPA, 
							xsd, tipoValidazione, confPers, configurazione, dati, applicaMTOM,
							urlInvocazionePA, urlInvocazionePD,
							multitenantEnabled, multitenantSoggettiFruizioni, multitenantSoggettiErogazioni,
							true,
							corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
							corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds,
							responseCachingEnabled,	responseCachingSeconds, responseCachingMaxResponseSize,	responseCachingMaxResponseSizeBytes, 
							responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload, responseCachingDigestHeadersNomiHeaders, responseCachingDigestQueryParameter, responseCachingDigestNomiParametriQuery,
							responseCachingCacheControlNoCache, responseCachingCacheControlMaxAge, responseCachingCacheControlNoStore, visualizzaLinkConfigurazioneRegola,
							servletResponseCachingConfigurazioneRegolaList, paramsResponseCachingConfigurazioneRegolaList, numeroResponseCachingConfigurazioneRegola, numeroRegoleProxyPass,
							canaliEnabled, numeroCanali, numeroNodi, canaliNome, canaliDescrizione, canaleList, canaliDefault,
							numeroArchiviPlugins, numeroClassiPlugins);

					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_DATI_RICHIESTE,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_DATI_RICHIESTE,statoCachEdatiRichieste,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_DATI_RICHIESTE,dimensionecachEdatiRichieste,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_DATI_RICHIESTE,algoritmocachEdatiRichieste,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_DATI_RICHIESTE,idlecachEdatiRichieste,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_DATI_RICHIESTE,lifecachEdatiRichieste,
							isAllHiddenCache);
					
					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_REGISTRY,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_REGISTRY,statoCacheRegistry,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_REGISTRY,dimensionecacheRegistry,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_REGISTRY,algoritmocacheRegistry,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_REGISTRY,idlecacheRegistry,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_REGISTRY,lifecacheRegistry,
							isAllHiddenCache);
					
					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONFIG,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONFIG,statoCacheConfig,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONFIG,dimensionecacheConfig,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONFIG,algoritmocacheConfig,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONFIG,idlecacheConfig,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONFIG,lifecacheConfig,
							isAllHiddenCache);

					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHZ,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHZ,statoCacheAuthz,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHZ,dimensionecacheAuthz,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHZ,algoritmocacheAuthz,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHZ,idlecacheAuthz,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHZ,lifecacheAuthz,
							isAllHiddenCache);

					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHN,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHN,statoCacheAuthn,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHN,dimensionecacheAuthn,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHN,algoritmocacheAuthn,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHN,idlecacheAuthn,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHN,lifecacheAuthn,
							isAllHiddenCache);

					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_TOKEN,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_TOKEN,statoCacheToken,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_TOKEN,dimensionecacheToken,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_TOKEN,algoritmocacheToken,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_TOKEN,idlecacheToken,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_TOKEN,lifecacheToken,
							isAllHiddenCache);
					
					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_ATTRIBUTE_AUTHORITY,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_ATTRIBUTE_AUTHORITY,statoCacheAA,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_ATTRIBUTE_AUTHORITY,dimensionecacheAA,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_ATTRIBUTE_AUTHORITY,algoritmocacheAA,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_ATTRIBUTE_AUTHORITY,idlecacheAA,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_ATTRIBUTE_AUTHORITY,lifecacheAA,
							isAllHiddenCache);
					
					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_KEYSTORE,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_KEYSTORE,statoCacheKeystore,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_KEYSTORE,dimensionecacheKeystore,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_KEYSTORE,algoritmocacheKeystore,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_KEYSTORE,idlecacheKeystore,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_KEYSTORE,lifecacheKeystore,
							isAllHiddenCache);
					confHelper.setDataElementCRLCacheInfo(dati, 
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CRL_LIFE_CACHE_KEYSTORE,crlLifecacheKeystore,
							isAllHiddenCache);

					if(!isAllHiddenCache) {
						confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONTROLLO_TRAFFICO,
								ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONTROLLO_TRAFFICO,statoCacheControlloTraffico,
								ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONTROLLO_TRAFFICO,dimensionecacheControlloTraffico,
								ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONTROLLO_TRAFFICO,algoritmocacheControlloTraffico,
								ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONTROLLO_TRAFFICO,idlecacheControlloTraffico,
								ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONTROLLO_TRAFFICO,lifecacheControlloTraffico,
								isAllHiddenCache);
					}
					
					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_RISPOSTE,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_RISPOSTE,statoCacheRisposte,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_RISPOSTE,dimensionecacheRisposte,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_RISPOSTE,algoritmocacheRisposte,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_RISPOSTE,idlecacheRisposte,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_RISPOSTE,lifecacheRisposte,
							isAllHiddenCache);
					
					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONSEGNA_APPLICATIVI,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONSEGNA,statoCacheConsegna,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONSEGNA,dimensionecacheConsegna,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONSEGNA,algoritmocacheConsegna,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONSEGNA,idlecacheConsegna,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONSEGNA,lifecacheConsegna,
							isAllHiddenCache);

					if(extendedBeanList!=null && !extendedBeanList.isEmpty()){
						for (ExtendedInfo ei : extendedBeanList) {
							if(ei.extended){
								if(ei.extendedToNewWindow){
									AbstractServletNewWindowChangeExtended.addToDatiNewWindow(ei.extendedServlet, 
											dati, confHelper, confCore, configurazione, ei.extendedBean, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE_EXTENDED);
								}else{
									ei.extendedServlet.addToDati(dati, TipoOperazione.CHANGE, confHelper, confCore, configurazione, ei.extendedBean);
								}
							}
						}
					}

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
							ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE,
							ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_GENERALE);
				}

				// Modifico i dati della porta di dominio nel db
				Configurazione newConfigurazione = confCore.getConfigurazioneGenerale();

				if (newConfigurazione.getInoltroBusteNonRiscontrate() != null) {
					newConfigurazione.getInoltroBusteNonRiscontrate().setCadenza(inoltromin);
				} else {
					InoltroBusteNonRiscontrate ibnr = new InoltroBusteNonRiscontrate();
					ibnr.setCadenza(inoltromin);
					newConfigurazione.setInoltroBusteNonRiscontrate(ibnr);
				}

				if (newConfigurazione.getValidazioneBuste() != null) {
					newConfigurazione.getValidazioneBuste().setStato(StatoFunzionalitaConWarning.toEnumConstant(stato));
					newConfigurazione.getValidazioneBuste().setControllo(ValidazioneBusteTipoControllo.toEnumConstant(controllo));
					newConfigurazione.getValidazioneBuste().setProfiloCollaborazione(StatoFunzionalita.toEnumConstant(profcoll));
					newConfigurazione.getValidazioneBuste().setManifestAttachments(StatoFunzionalita.toEnumConstant(validman));
				} else {
					ValidazioneBuste vbe = new ValidazioneBuste();
					vbe.setStato(StatoFunzionalitaConWarning.toEnumConstant(stato));
					vbe.setControllo(ValidazioneBusteTipoControllo.toEnumConstant(controllo));
					vbe.setProfiloCollaborazione(StatoFunzionalita.toEnumConstant(profcoll));
					vbe.setManifestAttachments(StatoFunzionalita.toEnumConstant(validman));
					newConfigurazione.setValidazioneBuste(vbe);
				}

				if (newConfigurazione.getMessaggiDiagnostici() != null) {
					newConfigurazione.getMessaggiDiagnostici().setSeverita(Severita.toEnumConstant(severita));
					newConfigurazione.getMessaggiDiagnostici().setSeveritaLog4j(Severita.toEnumConstant(severitaLog4j));
				} else {
					MessaggiDiagnostici md = new MessaggiDiagnostici();
					md.setSeverita(Severita.toEnumConstant(severita));
					md.setSeveritaLog4j(Severita.toEnumConstant(severitaLog4j));
					newConfigurazione.setMessaggiDiagnostici(md);
				}

				if (newConfigurazione.getIntegrationManager() != null) {
					if (integman == null || !integman.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_IM_CUSTOM))
						newConfigurazione.getIntegrationManager().setAutenticazione(integman);
					else
						newConfigurazione.getIntegrationManager().setAutenticazione(nomeintegman);
				} else {
					IntegrationManager im = new IntegrationManager();
					if (integman == null || !integman.equals(ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_IM_CUSTOM))
						im.setAutenticazione(integman);
					else
						im.setAutenticazione(nomeintegman);
					newConfigurazione.setIntegrationManager(im);
				}

				if (newConfigurazione.getRisposte() != null) {
					newConfigurazione.getRisposte().setConnessione(TipoConnessioneRisposte.toEnumConstant(connessione));
				} else {
					Risposte rs = new Risposte();
					rs.setConnessione(TipoConnessioneRisposte.toEnumConstant(connessione));
					newConfigurazione.setRisposte(rs);
				}

				if (newConfigurazione.getIndirizzoRisposta() != null) {
					newConfigurazione.getIndirizzoRisposta().setUtilizzo(StatoFunzionalita.toEnumConstant(utilizzo));
				} else {
					IndirizzoRisposta it = new IndirizzoRisposta();
					it.setUtilizzo(StatoFunzionalita.toEnumConstant(utilizzo));
					newConfigurazione.setIndirizzoRisposta(it);
				}

				if (newConfigurazione.getAttachments() != null) {
					newConfigurazione.getAttachments().setGestioneManifest(StatoFunzionalita.toEnumConstant(gestman));
				} else {
					Attachments a = new Attachments();
					a.setGestioneManifest(StatoFunzionalita.toEnumConstant(gestman));
					newConfigurazione.setAttachments(a);
				}

				if (newConfigurazione.getTracciamento() != null) {
					newConfigurazione.getTracciamento().setStato(StatoFunzionalita.toEnumConstant(registrazioneTracce));
				} else {
					Tracciamento t = new Tracciamento();
					t.setStato(StatoFunzionalita.toEnumConstant(registrazioneTracce));
					newConfigurazione.setTracciamento(t);
				}

				if (newConfigurazione.getDump() != null) {
					newConfigurazione.getDump().setDumpBinarioPortaDelegata(StatoFunzionalita.toEnumConstant(dumpPD));
					newConfigurazione.getDump().setDumpBinarioPortaApplicativa(StatoFunzionalita.toEnumConstant(dumpPA));
				} else {
					Dump d = new Dump();
					d.setStato(StatoFunzionalita.DISABILITATO);
					d.setDumpBinarioPortaDelegata(StatoFunzionalita.toEnumConstant(dumpPD));
					d.setDumpBinarioPortaApplicativa(StatoFunzionalita.toEnumConstant(dumpPA));
					newConfigurazione.setDump(d);
				}

				if (newConfigurazione.getValidazioneContenutiApplicativi() != null) {
					newConfigurazione.getValidazioneContenutiApplicativi().setStato(StatoFunzionalitaConWarning.toEnumConstant(xsd));
					newConfigurazione.getValidazioneContenutiApplicativi().setTipo(ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione));
					if(applicaMTOM != null){
						if(ServletUtils.isCheckBoxEnabled(applicaMTOM))
							newConfigurazione.getValidazioneContenutiApplicativi().setAcceptMtomMessage(StatoFunzionalita.ABILITATO);
						else 
							newConfigurazione.getValidazioneContenutiApplicativi().setAcceptMtomMessage(StatoFunzionalita.DISABILITATO);
					} else 
						newConfigurazione.getValidazioneContenutiApplicativi().setAcceptMtomMessage(null);
				} else {
					ValidazioneContenutiApplicativi vca = new ValidazioneContenutiApplicativi();
					vca.setStato(StatoFunzionalitaConWarning.toEnumConstant(xsd));
					vca.setTipo(ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione));
					vca.setAcceptMtomMessage(null);
					newConfigurazione.setValidazioneContenutiApplicativi(vca);
				}

				if(newConfigurazione.getAccessoRegistro()==null){
					newConfigurazione.setAccessoRegistro(new AccessoRegistro());
				}
				if(statoCacheRegistry.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoRegistro().setCache(new Cache());
					newConfigurazione.getAccessoRegistro().getCache().setDimensione(dimensionecacheRegistry);
					newConfigurazione.getAccessoRegistro().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocacheRegistry));
					newConfigurazione.getAccessoRegistro().getCache().setItemIdleTime(idlecacheRegistry);
					newConfigurazione.getAccessoRegistro().getCache().setItemLifeSecond(confHelper.convertLifeCacheValue(lifecacheRegistry));
				}
				else{
					newConfigurazione.getAccessoRegistro().setCache(null);
				}
				
				if(newConfigurazione.getAccessoConfigurazione()==null){
					newConfigurazione.setAccessoConfigurazione(new AccessoConfigurazione());
				}
				if(statoCacheConfig.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoConfigurazione().setCache(new Cache());
					newConfigurazione.getAccessoConfigurazione().getCache().setDimensione(dimensionecacheConfig);
					newConfigurazione.getAccessoConfigurazione().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocacheConfig));
					newConfigurazione.getAccessoConfigurazione().getCache().setItemIdleTime(idlecacheConfig);
					newConfigurazione.getAccessoConfigurazione().getCache().setItemLifeSecond(confHelper.convertLifeCacheValue(lifecacheConfig));
				}
				else{
					newConfigurazione.getAccessoConfigurazione().setCache(null);
				}

				if(newConfigurazione.getAccessoDatiAutorizzazione()==null){
					newConfigurazione.setAccessoDatiAutorizzazione(new AccessoDatiAutorizzazione());
				}
				if(statoCacheAuthz.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoDatiAutorizzazione().setCache(new Cache());
					newConfigurazione.getAccessoDatiAutorizzazione().getCache().setDimensione(dimensionecacheAuthz);
					newConfigurazione.getAccessoDatiAutorizzazione().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocacheAuthz));
					newConfigurazione.getAccessoDatiAutorizzazione().getCache().setItemIdleTime(idlecacheAuthz);
					newConfigurazione.getAccessoDatiAutorizzazione().getCache().setItemLifeSecond(confHelper.convertLifeCacheValue(lifecacheAuthz));
				}
				else{
					newConfigurazione.getAccessoDatiAutorizzazione().setCache(null);
				}

				if(newConfigurazione.getAccessoDatiAutenticazione()==null){
					newConfigurazione.setAccessoDatiAutenticazione(new AccessoDatiAutenticazione());
				}
				if(statoCacheAuthn.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoDatiAutenticazione().setCache(new Cache());
					newConfigurazione.getAccessoDatiAutenticazione().getCache().setDimensione(dimensionecacheAuthn);
					newConfigurazione.getAccessoDatiAutenticazione().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocacheAuthn));
					newConfigurazione.getAccessoDatiAutenticazione().getCache().setItemIdleTime(idlecacheAuthn);
					newConfigurazione.getAccessoDatiAutenticazione().getCache().setItemLifeSecond(confHelper.convertLifeCacheValue(lifecacheAuthn));
				}
				else{
					newConfigurazione.getAccessoDatiAutenticazione().setCache(null);
				}

				if(newConfigurazione.getAccessoDatiGestioneToken()==null){
					newConfigurazione.setAccessoDatiGestioneToken(new AccessoDatiGestioneToken());
				}
				if(statoCacheToken.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoDatiGestioneToken().setCache(new Cache());
					newConfigurazione.getAccessoDatiGestioneToken().getCache().setDimensione(dimensionecacheToken);
					newConfigurazione.getAccessoDatiGestioneToken().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocacheToken));
					newConfigurazione.getAccessoDatiGestioneToken().getCache().setItemIdleTime(idlecacheToken);
					newConfigurazione.getAccessoDatiGestioneToken().getCache().setItemLifeSecond(confHelper.convertLifeCacheValue(lifecacheToken));
				}
				else{
					newConfigurazione.getAccessoDatiGestioneToken().setCache(null);
				}
				
				if(newConfigurazione.getAccessoDatiAttributeAuthority()==null){
					newConfigurazione.setAccessoDatiAttributeAuthority(new AccessoDatiAttributeAuthority());
				}
				if(statoCacheAA.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoDatiAttributeAuthority().setCache(new Cache());
					newConfigurazione.getAccessoDatiAttributeAuthority().getCache().setDimensione(dimensionecacheAA);
					newConfigurazione.getAccessoDatiAttributeAuthority().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocacheAA));
					newConfigurazione.getAccessoDatiAttributeAuthority().getCache().setItemIdleTime(idlecacheAA);
					newConfigurazione.getAccessoDatiAttributeAuthority().getCache().setItemLifeSecond(confHelper.convertLifeCacheValue(lifecacheAA));
				}
				else{
					newConfigurazione.getAccessoDatiAttributeAuthority().setCache(null);
				}
				
				if(newConfigurazione.getAccessoDatiKeystore()==null){
					newConfigurazione.setAccessoDatiKeystore(new AccessoDatiKeystore());
				}
				if(statoCacheKeystore.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoDatiKeystore().setCache(new Cache());
					newConfigurazione.getAccessoDatiKeystore().getCache().setDimensione(dimensionecacheKeystore);
					newConfigurazione.getAccessoDatiKeystore().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocacheKeystore));
					newConfigurazione.getAccessoDatiKeystore().getCache().setItemIdleTime(idlecacheKeystore);
					newConfigurazione.getAccessoDatiKeystore().getCache().setItemLifeSecond(confHelper.convertLifeCacheValue(lifecacheKeystore));
					newConfigurazione.getAccessoDatiKeystore().setCrlItemLifeSecond(confHelper.convertLifeCacheValue(crlLifecacheKeystore));
				}
				else{
					newConfigurazione.getAccessoDatiKeystore().setCache(null);
					newConfigurazione.getAccessoDatiKeystore().setCrlItemLifeSecond(null);
				}

				if(newConfigurazione.getResponseCaching()==null){
					newConfigurazione.setResponseCaching(new ResponseCachingConfigurazioneGenerale());
				}
				if(statoCacheRisposte.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getResponseCaching().setCache(new Cache());
					newConfigurazione.getResponseCaching().getCache().setDimensione(dimensionecacheRisposte);
					newConfigurazione.getResponseCaching().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocacheRisposte));
					newConfigurazione.getResponseCaching().getCache().setItemIdleTime(idlecacheRisposte);
					newConfigurazione.getResponseCaching().getCache().setItemLifeSecond(confHelper.convertLifeCacheValue(lifecacheRisposte));
				}
				else{
					newConfigurazione.getResponseCaching().setCache(null);
				}
				
				if(newConfigurazione.getAccessoDatiConsegnaApplicativi()==null){
					newConfigurazione.setAccessoDatiConsegnaApplicativi(new AccessoDatiConsegnaApplicativi());
				}
				if(statoCacheConsegna.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoDatiConsegnaApplicativi().setCache(new Cache());
					newConfigurazione.getAccessoDatiConsegnaApplicativi().getCache().setDimensione(dimensionecacheConsegna);
					newConfigurazione.getAccessoDatiConsegnaApplicativi().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocacheConsegna));
					newConfigurazione.getAccessoDatiConsegnaApplicativi().getCache().setItemIdleTime(idlecacheConsegna);
					newConfigurazione.getAccessoDatiConsegnaApplicativi().getCache().setItemLifeSecond(confHelper.convertLifeCacheValue(lifecacheConsegna));
				}
				else{
					newConfigurazione.getAccessoDatiConsegnaApplicativi().setCache(null);
				}
				
				if(newConfigurazione.getAccessoDatiRichieste()==null){
					newConfigurazione.setAccessoDatiRichieste(new AccessoDatiRichieste());
				}
				if(statoCachEdatiRichieste.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
					newConfigurazione.getAccessoDatiRichieste().setCache(new Cache());
					newConfigurazione.getAccessoDatiRichieste().getCache().setDimensione(dimensionecachEdatiRichieste);
					newConfigurazione.getAccessoDatiRichieste().getCache().setAlgoritmo(AlgoritmoCache.toEnumConstant(algoritmocachEdatiRichieste));
					newConfigurazione.getAccessoDatiRichieste().getCache().setItemIdleTime(idlecachEdatiRichieste);
					newConfigurazione.getAccessoDatiRichieste().getCache().setItemLifeSecond(confHelper.convertLifeCacheValue(lifecachEdatiRichieste));
				}
				else{
					newConfigurazione.getAccessoDatiRichieste().setCache(null);
				}

				newConfigurazione.setMultitenant(new ConfigurazioneMultitenant());
				newConfigurazione.getMultitenant().setStato(multitenantEnabled ? StatoFunzionalita.ABILITATO : StatoFunzionalita.DISABILITATO);

				MultitenantSoggettiFruizioni multitenantSoggettiFruizioniEnum = null;
				if(multitenantSoggettiFruizioni!=null && !"".equals(multitenantSoggettiFruizioni)) {
					multitenantSoggettiFruizioniEnum = MultitenantSoggettiFruizioni.valueOf(multitenantSoggettiFruizioni);
				}
				if(multitenantSoggettiFruizioniEnum!=null) {
					switch (multitenantSoggettiFruizioniEnum) {
					case SOLO_SOGGETTI_ESTERNI:
						newConfigurazione.getMultitenant().setFruizioneSceltaSoggettiErogatori(PortaDelegataSoggettiErogatori.SOGGETTI_ESTERNI);
						break;
					case ESCLUDI_SOGGETTO_FRUITORE:
						newConfigurazione.getMultitenant().setFruizioneSceltaSoggettiErogatori(PortaDelegataSoggettiErogatori.ESCLUDI_SOGGETTO_FRUITORE);
						break;
					case TUTTI:
						newConfigurazione.getMultitenant().setFruizioneSceltaSoggettiErogatori(PortaDelegataSoggettiErogatori.TUTTI);
						break;
					}
				}
				else {
					newConfigurazione.getMultitenant().setFruizioneSceltaSoggettiErogatori(PortaDelegataSoggettiErogatori.SOGGETTI_ESTERNI); // default
				}


				MultitenantSoggettiErogazioni multitenantSoggettiErogazioniEnum = null;
				if(multitenantSoggettiErogazioni!=null && !"".equals(multitenantSoggettiErogazioni)) {
					multitenantSoggettiErogazioniEnum = MultitenantSoggettiErogazioni.valueOf(multitenantSoggettiErogazioni);
				}
				if(multitenantSoggettiErogazioniEnum!=null) {
					switch (multitenantSoggettiErogazioniEnum) {
					case SOLO_SOGGETTI_ESTERNI:
						newConfigurazione.getMultitenant().setErogazioneSceltaSoggettiFruitori(PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI);
						break;
					case ESCLUDI_SOGGETTO_EROGATORE:
						newConfigurazione.getMultitenant().setErogazioneSceltaSoggettiFruitori(PortaApplicativaSoggettiFruitori.ESCLUDI_SOGGETTO_EROGATORE);
						break;
					case TUTTI:
						newConfigurazione.getMultitenant().setErogazioneSceltaSoggettiFruitori(PortaApplicativaSoggettiFruitori.TUTTI);
						break;
					}
				}
				else {
					newConfigurazione.getMultitenant().setErogazioneSceltaSoggettiFruitori(PortaApplicativaSoggettiFruitori.SOGGETTI_ESTERNI); // default
				}

				
				// Url Invocazione
				if(newConfigurazione.getUrlInvocazione()==null) {
					newConfigurazione.setUrlInvocazione(new ConfigurazioneUrlInvocazione());
				}
				newConfigurazione.getUrlInvocazione().setBaseUrl(urlInvocazionePA);
				if(urlInvocazionePD!=null && !"".equals(urlInvocazionePD)) {
					newConfigurazione.getUrlInvocazione().setBaseUrlFruizione(urlInvocazionePD);
				}
				else {
					newConfigurazione.getUrlInvocazione().setBaseUrlFruizione(null);
				}
				

				// cors
				CorsConfigurazione gestioneCors = confHelper.getGestioneCors(corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
						corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge,	corsMaxAgeSeconds);
				newConfigurazione.setGestioneCors(gestioneCors);
				
				// Response Caching
				ResponseCachingConfigurazione responseCaching = confHelper.getResponseCaching(responseCachingEnabled, responseCachingSeconds, responseCachingMaxResponseSize, responseCachingMaxResponseSizeBytes, 
						responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload, responseCachingDigestHeadersNomiHeaders, responseCachingDigestQueryParameter, responseCachingDigestNomiParametriQuery,
						responseCachingCacheControlNoCache, responseCachingCacheControlMaxAge, responseCachingCacheControlNoStore,listaRegoleCachingConfigurazione);
				if(newConfigurazione.getResponseCaching()==null) {
					newConfigurazione.setResponseCaching(new ResponseCachingConfigurazioneGenerale());
				}
				newConfigurazione.getResponseCaching().setConfigurazione(responseCaching);

				// canali
				CanaliConfigurazione newGestioneCanali = confHelper.getGestioneCanali(canaliEnabled, canaliDefault, canaleList, canaliNome, canaliDescrizione, nodoList);
				newConfigurazione.setGestioneCanali(newGestioneCanali);
				
				numeroResponseCachingConfigurazioneRegola = responseCaching.sizeRegolaList();
				visualizzaLinkConfigurazioneRegola = responseCaching.getStato().equals(StatoFunzionalita.ABILITATO);

				confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);

				if(!isAllHiddenCache) {
					org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = confCore.getConfigurazioneControlloTraffico();
					if(statoCacheControlloTraffico.equals(ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO)){
						configurazioneControlloTraffico.setCache(new org.openspcoop2.core.controllo_traffico.Cache());
						configurazioneControlloTraffico.getCache().setCache(true);
						configurazioneControlloTraffico.getCache().setSize(Long.valueOf(dimensionecacheControlloTraffico));
						configurazioneControlloTraffico.getCache().setAlgorithm(CacheAlgorithm.toEnumConstant(algoritmocacheControlloTraffico));
						if(idlecacheControlloTraffico!=null && !"".equals(idlecacheControlloTraffico)) {
							configurazioneControlloTraffico.getCache().setIdleTime(Long.valueOf(idlecacheControlloTraffico));
						}
						configurazioneControlloTraffico.getCache().setLifeTime(Long.valueOf(confHelper.convertLifeCacheValue(lifecacheControlloTraffico)));
					}
					else{
						configurazioneControlloTraffico.setCache(null);
					}
					confCore.performUpdateOperation(userLogin, confHelper.smista(), configurazioneControlloTraffico);
				}
				
				if(extendedBeanList!=null && !extendedBeanList.isEmpty()){
					for (ExtendedInfo ei : extendedBeanList) {
						if(ei.extended && !ei.extendedToNewWindow){
							WrapperExtendedBean wrapperExtendedBean = new WrapperExtendedBean();
							wrapperExtendedBean.setExtendedBean(ei.extendedBean);
							wrapperExtendedBean.setExtendedServlet(ei.extendedServlet);
							wrapperExtendedBean.setOriginalBean(newConfigurazione);
							wrapperExtendedBean.setManageOriginalBean(false);
							confCore.performUpdateOperation(userLogin, confHelper.smista(), wrapperExtendedBean);
						}
					}
				}
				
				List<DataElement> dati = new ArrayList<>();

				DataElement de = new DataElement();
				de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CACHES);
				de.setValue(configurazioneCachesTmp);
				de.setType(DataElementType.HIDDEN);
				dati.add(de);
				
				dati = confHelper.addConfigurazioneToDati(isAllHiddenConfigurazione, 
						inoltromin, stato, controllo, severita, severitaLog4j, integman, nomeintegman, profcoll, 
						connessione, utilizzo, validman, gestman, registrazioneTracce, dumpPD, dumpPA, 
						xsd, tipoValidazione, confPers, configurazione, dati, applicaMTOM,
						urlInvocazionePA, urlInvocazionePD,
						multitenantEnabled, multitenantSoggettiFruizioni, multitenantSoggettiErogazioni,
						false,
						corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
						corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds,
						responseCachingEnabled,	responseCachingSeconds, responseCachingMaxResponseSize,	responseCachingMaxResponseSizeBytes, 
						responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload, responseCachingDigestHeadersNomiHeaders, responseCachingDigestQueryParameter, responseCachingDigestNomiParametriQuery,
						responseCachingCacheControlNoCache, responseCachingCacheControlMaxAge, responseCachingCacheControlNoStore, visualizzaLinkConfigurazioneRegola,
						servletResponseCachingConfigurazioneRegolaList, paramsResponseCachingConfigurazioneRegolaList, numeroResponseCachingConfigurazioneRegola, numeroRegoleProxyPass,
						canaliEnabled, numeroCanali, numeroNodi, canaliNome, canaliDescrizione, canaleList, canaliDefault,
						numeroArchiviPlugins, numeroClassiPlugins);

				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_DATI_RICHIESTE,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_DATI_RICHIESTE,statoCachEdatiRichieste,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_DATI_RICHIESTE,dimensionecachEdatiRichieste,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_DATI_RICHIESTE,algoritmocachEdatiRichieste,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_DATI_RICHIESTE,idlecachEdatiRichieste,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_DATI_RICHIESTE,lifecachEdatiRichieste,
						isAllHiddenCache);
				
				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_REGISTRY,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_REGISTRY,statoCacheRegistry,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_REGISTRY,dimensionecacheRegistry,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_REGISTRY,algoritmocacheRegistry,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_REGISTRY,idlecacheRegistry,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_REGISTRY,lifecacheRegistry,
						isAllHiddenCache);
				
				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONFIG,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONFIG,statoCacheConfig,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONFIG,dimensionecacheConfig,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONFIG,algoritmocacheConfig,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONFIG,idlecacheConfig,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONFIG,lifecacheConfig,
						isAllHiddenCache);

				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHZ,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHZ,statoCacheAuthz,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHZ,dimensionecacheAuthz,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHZ,algoritmocacheAuthz,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHZ,idlecacheAuthz,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHZ,lifecacheAuthz,
						isAllHiddenCache);

				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHN,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHN,statoCacheAuthn,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHN,dimensionecacheAuthn,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHN,algoritmocacheAuthn,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHN,idlecacheAuthn,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHN,lifecacheAuthn,
						isAllHiddenCache);

				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_TOKEN,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_TOKEN,statoCacheToken,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_TOKEN,dimensionecacheToken,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_TOKEN,algoritmocacheToken,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_TOKEN,idlecacheToken,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_TOKEN,lifecacheToken,
						isAllHiddenCache);
				
				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_ATTRIBUTE_AUTHORITY,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_ATTRIBUTE_AUTHORITY,statoCacheAA,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_ATTRIBUTE_AUTHORITY,dimensionecacheAA,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_ATTRIBUTE_AUTHORITY,algoritmocacheAA,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_ATTRIBUTE_AUTHORITY,idlecacheAA,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_ATTRIBUTE_AUTHORITY,lifecacheAA,
						isAllHiddenCache);
				
				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_KEYSTORE,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_KEYSTORE,statoCacheKeystore,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_KEYSTORE,dimensionecacheKeystore,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_KEYSTORE,algoritmocacheKeystore,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_KEYSTORE,idlecacheKeystore,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_KEYSTORE,lifecacheKeystore,
						isAllHiddenCache);
				confHelper.setDataElementCRLCacheInfo(dati, 
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CRL_LIFE_CACHE_KEYSTORE,crlLifecacheKeystore,
						isAllHiddenCache);

				if(!isAllHiddenCache) {
					confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONTROLLO_TRAFFICO,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONTROLLO_TRAFFICO,statoCacheControlloTraffico,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONTROLLO_TRAFFICO,dimensionecacheControlloTraffico,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONTROLLO_TRAFFICO,algoritmocacheControlloTraffico,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONTROLLO_TRAFFICO,idlecacheControlloTraffico,
							ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONTROLLO_TRAFFICO,lifecacheControlloTraffico,
							isAllHiddenCache);
				}
				
				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_RISPOSTE,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_RISPOSTE,statoCacheRisposte,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_RISPOSTE,dimensionecacheRisposte,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_RISPOSTE,algoritmocacheRisposte,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_RISPOSTE,idlecacheRisposte,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_RISPOSTE,lifecacheRisposte,
						isAllHiddenCache);
				
				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONSEGNA_APPLICATIVI,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONSEGNA,statoCacheConsegna,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONSEGNA,dimensionecacheConsegna,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONSEGNA,algoritmocacheConsegna,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONSEGNA,idlecacheConsegna,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONSEGNA,lifecacheConsegna,
						isAllHiddenCache);

				if(extendedBeanList!=null && !extendedBeanList.isEmpty()){
					for (ExtendedInfo ei : extendedBeanList) {
						if(ei.extended){
							if(ei.extendedToNewWindow){
								AbstractServletNewWindowChangeExtended.addToDatiNewWindow(ei.extendedServlet, 
										dati, confHelper, confCore, newConfigurazione, ei.extendedBean, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE_EXTENDED);
							}else{
								ei.extendedServlet.addToDati(dati, TipoOperazione.CHANGE, confHelper, confCore, newConfigurazione, ei.extendedBean);
							}
						}
					}
				}

				pd.setDati(dati);

				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

				pd.disableEditMode();

				generalHelper = new GeneralHelper(session);
				gd = generalHelper.initGeneralData(request); // re-inizializzo per ricalcolare il menu in alto a destra

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeFinished(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE,
						ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_GENERALE);
			}
			
			String postBackElementName = confHelper.getPostBackElementName();
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_STATO)){
					
					if(!confHelper.isResponseCachingAbilitato(cachingConfigurazione) && responseCachingEnabled) {
						ResponseCachingConfigurazione configurazioneTmp = new ResponseCachingConfigurazione();
						
						responseCachingSeconds = configurazioneTmp.getCacheTimeoutSeconds() != null ? configurazioneTmp.getCacheTimeoutSeconds().intValue() : 1;
						
						responseCachingMaxResponseSize = false;
						if(configurazioneTmp.getMaxMessageSize() != null) {
							responseCachingMaxResponseSize = true;
							responseCachingMaxResponseSizeBytes = configurazioneTmp.getMaxMessageSize().longValue();
						}
						
						responseCachingDigestUrlInvocazione = true;
						responseCachingDigestQueryParameter = StatoFunzionalitaCacheDigestQueryParameter.ABILITATO;
						responseCachingDigestHeaders = false;
						responseCachingDigestPayload = true;
						configurazioneTmp.setHashGenerator(new ResponseCachingConfigurazioneHashGenerator());
						if(configurazioneTmp.getHashGenerator() != null) {
							if(configurazioneTmp.getHashGenerator().getQueryParameters() != null)
								responseCachingDigestQueryParameter = configurazioneTmp.getHashGenerator().getQueryParameters();
							
							if(configurazioneTmp.getHashGenerator().getQueryParameterList() != null)  
								responseCachingDigestNomiParametriQuery = StringUtils.join(configurazioneTmp.getHashGenerator().getQueryParameterList(), ",");
							
							if(configurazioneTmp.getHashGenerator().getHeaders() != null)  
								responseCachingDigestHeaders = configurazioneTmp.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO);
							
							if(configurazioneTmp.getHashGenerator().getHeaderList() != null)  
								responseCachingDigestHeadersNomiHeaders = StringUtils.join(configurazioneTmp.getHashGenerator().getHeaderList(), ",");
							
							if(configurazioneTmp.getHashGenerator().getRequestUri() != null) 
								responseCachingDigestUrlInvocazione = configurazioneTmp.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO);
							
							if(configurazioneTmp.getHashGenerator().getPayload() != null) 
								responseCachingDigestPayload = configurazioneTmp.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO);
						}
						
						responseCachingCacheControlNoCache = true;
						responseCachingCacheControlMaxAge = true;
						responseCachingCacheControlNoStore = true;
						configurazioneTmp.setControl(new ResponseCachingConfigurazioneControl());
						if(configurazioneTmp.getControl() != null) {
							responseCachingCacheControlNoCache = configurazioneTmp.getControl().isNoCache();
							responseCachingCacheControlMaxAge = configurazioneTmp.getControl().isMaxAge();
							responseCachingCacheControlNoStore = configurazioneTmp.getControl().isNoStore();
						}
					}
				}
				
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO)){
					CorsConfigurazione oldConfigurazione = configurazione.getGestioneCors(); 
					if(!confHelper.isCorsAbilitato(oldConfigurazione) && corsStato) {
						CorsConfigurazione configurazioneTmp = new CorsConfigurazione();
						
						corsTipo = configurazioneTmp.getTipo();
						if(configurazioneTmp.getTipo() != null && configurazioneTmp.getTipo().equals(TipoGestioneCORS.GATEWAY)) {

							corsAllAllowOrigins = true;
							if(configurazioneTmp.getAccessControlAllAllowOrigins() != null && configurazioneTmp.getAccessControlAllAllowOrigins().equals(StatoFunzionalita.DISABILITATO)) {
								corsAllAllowOrigins = false;

								configurazioneTmp.setAccessControlAllowOrigins(new CorsConfigurazioneOrigin());
								corsAllowOrigins = StringUtils.join(configurazioneTmp.getAccessControlAllowOrigins().getOriginList(), ",");
							}

							corsAllAllowHeaders = false;
							if(configurazioneTmp.getAccessControlAllAllowHeaders() != null && configurazioneTmp.getAccessControlAllAllowHeaders().equals(StatoFunzionalita.ABILITATO)) {
								corsAllAllowHeaders = true;
							}
							if(!corsAllAllowHeaders) {
								configurazioneTmp.setAccessControlAllowHeaders(new CorsConfigurazioneHeaders());
								corsAllowHeaders = StringUtils.join(configurazioneTmp.getAccessControlAllowHeaders().getHeaderList(), ",");
							}

							corsAllAllowMethods = false;
							if(configurazioneTmp.getAccessControlAllAllowMethods() != null && configurazioneTmp.getAccessControlAllAllowMethods().equals(StatoFunzionalita.ABILITATO)) {
								corsAllAllowMethods = true;
							}
							if(!corsAllAllowMethods) {
								configurazioneTmp.setAccessControlAllowMethods(new CorsConfigurazioneMethods());
								corsAllowMethods = StringUtils.join(configurazioneTmp.getAccessControlAllowMethods().getMethodList(), ",");
							}

							configurazioneTmp.setAccessControlExposeHeaders(new CorsConfigurazioneHeaders());
							corsExposeHeaders = StringUtils.join(configurazioneTmp.getAccessControlExposeHeaders().getHeaderList(), ",");

							corsAllowCredential = false;
							if(configurazioneTmp.getAccessControlAllowCredentials() != null && configurazioneTmp.getAccessControlAllowCredentials().equals(StatoFunzionalita.ABILITATO)) {
								corsAllowCredential = true;
							}

							corsMaxAge = false;
							corsMaxAgeSeconds = -1;
							if(configurazioneTmp.getAccessControlMaxAge() != null ) {
								corsMaxAge = true;
								corsMaxAgeSeconds = configurazioneTmp.getAccessControlMaxAge();
							}
						}
					}
				}
			}

			if (inoltromin == null) {
				inoltromin = configurazione.getInoltroBusteNonRiscontrate().getCadenza();
				if(configurazione.getValidazioneBuste().getStato()!=null)
					stato = configurazione.getValidazioneBuste().getStato().toString();
				if(configurazione.getValidazioneBuste().getControllo()!=null)
					controllo = configurazione.getValidazioneBuste().getControllo().toString();
				if(configurazione.getMessaggiDiagnostici().getSeverita()!=null)
					severita = configurazione.getMessaggiDiagnostici().getSeverita().toString();
				if(configurazione.getMessaggiDiagnostici().getSeveritaLog4j()!=null)
					severitaLog4j = configurazione.getMessaggiDiagnostici().getSeveritaLog4j().toString();
				integman = configurazione.getIntegrationManager().getAutenticazione();
				boolean foundIM = false;
				if(integman!=null){
					for (int i = 0; i < ConfigurazioneCostanti.PARAMETRI_CONFIGURAZIONE_IM.length; i++) {
						if(ConfigurazioneCostanti.PARAMETRI_CONFIGURAZIONE_IM[i].equals(integman)){
							foundIM = true;
							break;
						}
					}
				}
				if (!foundIM) {
					nomeintegman = integman;
					integman = ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_IM_CUSTOM  ;
				}
				if(configurazione.getValidazioneBuste().getProfiloCollaborazione()!=null)
					profcoll = configurazione.getValidazioneBuste().getProfiloCollaborazione().toString();
				if(configurazione.getRisposte().getConnessione()!=null)
					connessione = configurazione.getRisposte().getConnessione().toString();
				if(configurazione.getIndirizzoRisposta().getUtilizzo()!=null)
					utilizzo = configurazione.getIndirizzoRisposta().getUtilizzo().toString();
				if(configurazione.getValidazioneBuste().getManifestAttachments()!=null)
					validman = configurazione.getValidazioneBuste().getManifestAttachments().toString();
				if(configurazione.getAttachments().getGestioneManifest()!=null)
					gestman = configurazione.getAttachments().getGestioneManifest().toString();
				if(configurazione.getTracciamento().getStato()!=null)
					registrazioneTracce = configurazione.getTracciamento().getStato().toString();
				if(configurazione.getDump().getDumpBinarioPortaDelegata()!=null)
					dumpPD = configurazione.getDump().getDumpBinarioPortaDelegata().toString();
				if(configurazione.getDump().getDumpBinarioPortaApplicativa()!=null)
					dumpPA = configurazione.getDump().getDumpBinarioPortaApplicativa().toString();
				if (configurazione.getValidazioneContenutiApplicativi() != null) {
					if(configurazione.getValidazioneContenutiApplicativi().getStato()!=null)
						xsd = configurazione.getValidazioneContenutiApplicativi().getStato().toString();
					if(configurazione.getValidazioneContenutiApplicativi().getTipo()!=null)
						tipoValidazione = configurazione.getValidazioneContenutiApplicativi().getTipo().toString();
					if(configurazione.getValidazioneContenutiApplicativi().getAcceptMtomMessage()!=null){
						if(StatoFunzionalita.ABILITATO.equals(configurazione.getValidazioneContenutiApplicativi().getAcceptMtomMessage())){
							applicaMTOM = Costanti.CHECK_BOX_ENABLED_ABILITATO;
						}
						else{
							applicaMTOM = Costanti.CHECK_BOX_DISABLED;
						}
					}
				}
				if(configurazione.getUrlInvocazione()!=null) {
					urlInvocazionePA = configurazione.getUrlInvocazione().getBaseUrl();
					if(configurazione.getUrlInvocazione().getBaseUrlFruizione()!=null) {
						urlInvocazionePD = configurazione.getUrlInvocazione().getBaseUrlFruizione();
					}
					numeroRegoleProxyPass = configurazione.getUrlInvocazione().sizeRegolaList();
				}
				if(configurazione.getAccessoRegistro()!=null && configurazione.getAccessoRegistro().getCache()!=null){
					statoCacheRegistry = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecacheRegistry = configurazione.getAccessoRegistro().getCache().getDimensione();
					if(configurazione.getAccessoRegistro().getCache().getAlgoritmo()!=null){
						algoritmocacheRegistry = configurazione.getAccessoRegistro().getCache().getAlgoritmo().getValue();
					}
					idlecacheRegistry = configurazione.getAccessoRegistro().getCache().getItemIdleTime();
					lifecacheRegistry = configurazione.getAccessoRegistro().getCache().getItemLifeSecond();
				}
				else{
					statoCacheRegistry = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getAccessoConfigurazione()!=null && configurazione.getAccessoConfigurazione().getCache()!=null){
					statoCacheConfig = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecacheConfig = configurazione.getAccessoConfigurazione().getCache().getDimensione();
					if(configurazione.getAccessoConfigurazione().getCache().getAlgoritmo()!=null){
						algoritmocacheConfig = configurazione.getAccessoConfigurazione().getCache().getAlgoritmo().getValue();
					}
					idlecacheConfig = configurazione.getAccessoConfigurazione().getCache().getItemIdleTime();
					lifecacheConfig = configurazione.getAccessoConfigurazione().getCache().getItemLifeSecond();
				}
				else{
					statoCacheConfig = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getAccessoDatiAutorizzazione()!=null && configurazione.getAccessoDatiAutorizzazione().getCache()!=null){
					statoCacheAuthz = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecacheAuthz = configurazione.getAccessoDatiAutorizzazione().getCache().getDimensione();
					if(configurazione.getAccessoDatiAutorizzazione().getCache().getAlgoritmo()!=null){
						algoritmocacheAuthz = configurazione.getAccessoDatiAutorizzazione().getCache().getAlgoritmo().getValue();
					}
					idlecacheAuthz = configurazione.getAccessoDatiAutorizzazione().getCache().getItemIdleTime();
					lifecacheAuthz = configurazione.getAccessoDatiAutorizzazione().getCache().getItemLifeSecond();
				}
				else{
					statoCacheAuthz = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getAccessoDatiAutenticazione()!=null && configurazione.getAccessoDatiAutenticazione().getCache()!=null){
					statoCacheAuthn = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecacheAuthn = configurazione.getAccessoDatiAutenticazione().getCache().getDimensione();
					if(configurazione.getAccessoDatiAutenticazione().getCache().getAlgoritmo()!=null){
						algoritmocacheAuthn = configurazione.getAccessoDatiAutenticazione().getCache().getAlgoritmo().getValue();
					}
					idlecacheAuthn = configurazione.getAccessoDatiAutenticazione().getCache().getItemIdleTime();
					lifecacheAuthn = configurazione.getAccessoDatiAutenticazione().getCache().getItemLifeSecond();
				}
				else{
					statoCacheAuthn = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getAccessoDatiGestioneToken()!=null && configurazione.getAccessoDatiGestioneToken().getCache()!=null){
					statoCacheToken = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecacheToken = configurazione.getAccessoDatiGestioneToken().getCache().getDimensione();
					if(configurazione.getAccessoDatiGestioneToken().getCache().getAlgoritmo()!=null){
						algoritmocacheToken = configurazione.getAccessoDatiGestioneToken().getCache().getAlgoritmo().getValue();
					}
					idlecacheToken = configurazione.getAccessoDatiGestioneToken().getCache().getItemIdleTime();
					lifecacheToken = configurazione.getAccessoDatiGestioneToken().getCache().getItemLifeSecond();
				}
				else{
					statoCacheToken = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getAccessoDatiAttributeAuthority()!=null && configurazione.getAccessoDatiAttributeAuthority().getCache()!=null){
					statoCacheAA = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecacheAA = configurazione.getAccessoDatiAttributeAuthority().getCache().getDimensione();
					if(configurazione.getAccessoDatiAttributeAuthority().getCache().getAlgoritmo()!=null){
						algoritmocacheAA = configurazione.getAccessoDatiAttributeAuthority().getCache().getAlgoritmo().getValue();
					}
					idlecacheAA = configurazione.getAccessoDatiAttributeAuthority().getCache().getItemIdleTime();
					lifecacheAA = configurazione.getAccessoDatiAttributeAuthority().getCache().getItemLifeSecond();
				}
				else{
					statoCacheAA = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getAccessoDatiKeystore()!=null && configurazione.getAccessoDatiKeystore().getCache()!=null){
					statoCacheKeystore = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecacheKeystore = configurazione.getAccessoDatiKeystore().getCache().getDimensione();
					if(configurazione.getAccessoDatiKeystore().getCache().getAlgoritmo()!=null){
						algoritmocacheKeystore = configurazione.getAccessoDatiKeystore().getCache().getAlgoritmo().getValue();
					}
					idlecacheKeystore = configurazione.getAccessoDatiKeystore().getCache().getItemIdleTime();
					lifecacheKeystore = configurazione.getAccessoDatiKeystore().getCache().getItemLifeSecond();
					crlLifecacheKeystore = configurazione.getAccessoDatiKeystore().getCrlItemLifeSecond();
				}
				else{
					statoCacheKeystore = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(!isAllHiddenCache) {
					org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = confCore.getConfigurazioneControlloTraffico();
					if(configurazioneControlloTraffico.getCache()!=null && configurazioneControlloTraffico.getCache().isCache()){
						statoCacheControlloTraffico = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
						dimensionecacheControlloTraffico = configurazioneControlloTraffico.getCache().getSize()+"";
						if(configurazioneControlloTraffico.getCache().getAlgorithm()!=null){
							algoritmocacheControlloTraffico = configurazioneControlloTraffico.getCache().getAlgorithm().getValue();
						}
						if(configurazioneControlloTraffico.getCache().getIdleTime()!=null) {
							idlecacheControlloTraffico = configurazioneControlloTraffico.getCache().getIdleTime()+"";
						}
						lifecacheControlloTraffico = configurazioneControlloTraffico.getCache().getLifeTime()+"";
					}
					else{
						statoCacheControlloTraffico = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
				}
				if(configurazione.getResponseCaching()!=null && configurazione.getResponseCaching().getCache()!=null) {
					statoCacheRisposte = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecacheRisposte = configurazione.getResponseCaching().getCache().getDimensione();
					if(configurazione.getResponseCaching().getCache().getAlgoritmo()!=null){
						algoritmocacheRisposte = configurazione.getResponseCaching().getCache().getAlgoritmo().getValue();
					}
					idlecacheRisposte = configurazione.getResponseCaching().getCache().getItemIdleTime();
					lifecacheRisposte = configurazione.getResponseCaching().getCache().getItemLifeSecond();
				}
				else{
					statoCacheRisposte = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getAccessoDatiConsegnaApplicativi()!=null && configurazione.getAccessoDatiConsegnaApplicativi().getCache()!=null) {
					statoCacheConsegna = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecacheConsegna = configurazione.getAccessoDatiConsegnaApplicativi().getCache().getDimensione();
					if(configurazione.getAccessoDatiConsegnaApplicativi().getCache().getAlgoritmo()!=null){
						algoritmocacheConsegna = configurazione.getAccessoDatiConsegnaApplicativi().getCache().getAlgoritmo().getValue();
					}
					idlecacheConsegna = configurazione.getAccessoDatiConsegnaApplicativi().getCache().getItemIdleTime();
					lifecacheConsegna = configurazione.getAccessoDatiConsegnaApplicativi().getCache().getItemLifeSecond();
				}
				else{
					statoCacheConsegna = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getAccessoDatiRichieste()!=null && configurazione.getAccessoDatiRichieste().getCache()!=null) {
					statoCachEdatiRichieste = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					dimensionecachEdatiRichieste = configurazione.getAccessoDatiRichieste().getCache().getDimensione();
					if(configurazione.getAccessoDatiRichieste().getCache().getAlgoritmo()!=null){
						algoritmocachEdatiRichieste = configurazione.getAccessoDatiRichieste().getCache().getAlgoritmo().getValue();
					}
					idlecachEdatiRichieste = configurazione.getAccessoDatiRichieste().getCache().getItemIdleTime();
					lifecachEdatiRichieste = configurazione.getAccessoDatiRichieste().getCache().getItemLifeSecond();
				}
				else{
					statoCachEdatiRichieste = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
				}
				if(configurazione.getMultitenant()!=null) {
					if(configurazione.getMultitenant().getStato()!=null) {
						multitenantEnabled = StatoFunzionalita.ABILITATO.equals(configurazione.getMultitenant().getStato());
					}
					if(configurazione.getMultitenant().getFruizioneSceltaSoggettiErogatori()!=null) {
						switch (configurazione.getMultitenant().getFruizioneSceltaSoggettiErogatori()) {
						case SOGGETTI_ESTERNI:
							multitenantSoggettiFruizioni = MultitenantSoggettiFruizioni.SOLO_SOGGETTI_ESTERNI.name();
							break;
						case ESCLUDI_SOGGETTO_FRUITORE:
							multitenantSoggettiFruizioni = MultitenantSoggettiFruizioni.ESCLUDI_SOGGETTO_FRUITORE.name();
							break;
						case TUTTI:
							multitenantSoggettiFruizioni = MultitenantSoggettiFruizioni.TUTTI.name();
							break;
						}
					}
					if(configurazione.getMultitenant().getErogazioneSceltaSoggettiFruitori()!=null) {
						switch (configurazione.getMultitenant().getErogazioneSceltaSoggettiFruitori()) {
						case SOGGETTI_ESTERNI:
							multitenantSoggettiErogazioni = MultitenantSoggettiErogazioni.SOLO_SOGGETTI_ESTERNI.name();
							break;
						case ESCLUDI_SOGGETTO_EROGATORE:
							multitenantSoggettiErogazioni = MultitenantSoggettiErogazioni.ESCLUDI_SOGGETTO_EROGATORE.name();
							break;
						case TUTTI:
							multitenantSoggettiErogazioni = MultitenantSoggettiErogazioni.TUTTI.name();
							break;
						}
					}
				}

				corsStato = false;
				CorsConfigurazione gestioneCors = configurazione.getGestioneCors();
				if(gestioneCors != null) {
					if(gestioneCors.getStato() != null && gestioneCors.getStato().equals(StatoFunzionalita.ABILITATO)) {
						corsStato = true;

						corsTipo = gestioneCors.getTipo();
						if(gestioneCors.getTipo() != null && gestioneCors.getTipo().equals(TipoGestioneCORS.GATEWAY)) {

							corsAllAllowOrigins = true;
							if(gestioneCors.getAccessControlAllAllowOrigins() != null && gestioneCors.getAccessControlAllAllowOrigins().equals(StatoFunzionalita.DISABILITATO)) {
								corsAllAllowOrigins = false;

								if(gestioneCors.getAccessControlAllowOrigins() != null) {
									corsAllowOrigins = StringUtils.join(gestioneCors.getAccessControlAllowOrigins().getOriginList(), ",");
								}
							}

							corsAllAllowHeaders = false;
							if(gestioneCors.getAccessControlAllAllowHeaders() != null && gestioneCors.getAccessControlAllAllowHeaders().equals(StatoFunzionalita.ABILITATO)) {
								corsAllAllowHeaders = true;
							}
							if(!corsAllAllowHeaders) {
								if(gestioneCors.getAccessControlAllowHeaders() != null) {
									corsAllowHeaders = StringUtils.join(gestioneCors.getAccessControlAllowHeaders().getHeaderList(), ",");
								}
							}

							corsAllAllowMethods = false;
							if(gestioneCors.getAccessControlAllAllowMethods() != null && gestioneCors.getAccessControlAllAllowMethods().equals(StatoFunzionalita.ABILITATO)) {
								corsAllAllowMethods = true;
							}
							if(!corsAllAllowMethods) {
								if(gestioneCors.getAccessControlAllowMethods() != null) {
									corsAllowMethods = StringUtils.join(gestioneCors.getAccessControlAllowMethods().getMethodList(), ",");
								}
							}

							if(gestioneCors.getAccessControlExposeHeaders() != null) {
								corsExposeHeaders = StringUtils.join(gestioneCors.getAccessControlExposeHeaders().getHeaderList(), ",");
							}

							corsAllowCredential = false;
							if(gestioneCors.getAccessControlAllowCredentials() != null && gestioneCors.getAccessControlAllowCredentials().equals(StatoFunzionalita.ABILITATO)) {
								corsAllowCredential = true;
							}

							corsMaxAge = false;
							corsMaxAgeSeconds = -1;
							if(gestioneCors.getAccessControlMaxAge() != null ) {
								corsMaxAge = true;
								corsMaxAgeSeconds = gestioneCors.getAccessControlMaxAge();
							}
						}

					}
				}
				
				responseCachingGenerale = configurazione.getResponseCaching();
				responseCachingEnabled = false;
				if(responseCachingGenerale != null) {
					ResponseCachingConfigurazione responseCaching = responseCachingGenerale.getConfigurazione();
					if(responseCaching != null && responseCaching.getStato().equals(StatoFunzionalita.ABILITATO)) {
						responseCachingEnabled = true;
						
						responseCachingSeconds = responseCaching.getCacheTimeoutSeconds() != null ? responseCaching.getCacheTimeoutSeconds().intValue() : 1;
						
						responseCachingMaxResponseSize = false;
						if(responseCaching.getMaxMessageSize() != null) {
							responseCachingMaxResponseSize = true;
							responseCachingMaxResponseSizeBytes = responseCaching.getMaxMessageSize().longValue();
						}
						
						responseCachingDigestUrlInvocazione = true;
						responseCachingDigestQueryParameter = StatoFunzionalitaCacheDigestQueryParameter.ABILITATO;
						responseCachingDigestHeaders = false;
						responseCachingDigestPayload = true;
						responseCachingDigestHeadersNomiHeaders = "";
						if(responseCaching.getHashGenerator() != null) {
							
							if(responseCaching.getHashGenerator().getQueryParameters() != null)
								responseCachingDigestQueryParameter = responseCaching.getHashGenerator().getQueryParameters();
							
							if(responseCaching.getHashGenerator().getQueryParameterList() != null)  
								responseCachingDigestNomiParametriQuery = StringUtils.join(responseCaching.getHashGenerator().getQueryParameterList(), ",");
							
							if(responseCaching.getHashGenerator().getHeaders() != null)  
								responseCachingDigestHeaders = responseCaching.getHashGenerator().getHeaders().equals(StatoFunzionalita.ABILITATO);
							
							if(responseCaching.getHashGenerator().getHeaderList() != null)  
								responseCachingDigestHeadersNomiHeaders = StringUtils.join(responseCaching.getHashGenerator().getHeaderList(), ",");
							
							if(responseCaching.getHashGenerator().getRequestUri() != null) 
								responseCachingDigestUrlInvocazione = responseCaching.getHashGenerator().getRequestUri().equals(StatoFunzionalita.ABILITATO);
							
							if(responseCaching.getHashGenerator().getPayload() != null) 
								responseCachingDigestPayload = responseCaching.getHashGenerator().getPayload().equals(StatoFunzionalita.ABILITATO);
						}
						
						responseCachingCacheControlNoCache = true;
						responseCachingCacheControlMaxAge = true;
						responseCachingCacheControlNoStore = true;
						if(responseCaching.getControl() != null) {
							responseCachingCacheControlNoCache = responseCaching.getControl().isNoCache();
							responseCachingCacheControlMaxAge = responseCaching.getControl().isMaxAge();
							responseCachingCacheControlNoStore = responseCaching.getControl().isNoStore();
						}
						
						numeroResponseCachingConfigurazioneRegola = responseCaching.sizeRegolaList();
						visualizzaLinkConfigurazioneRegola = true;
					}
				}
				
				canaliEnabled = false;
				gestioneCanali = configurazione.getGestioneCanali();
				numeroCanali = confHelper.numeroCanali(gestioneCanali);
				numeroNodi = confHelper.numeroNodi(gestioneCanali);
				if(gestioneCanali != null &&
					gestioneCanali.getStato() != null && gestioneCanali.getStato().equals(StatoFunzionalita.ABILITATO)) {
					canaliEnabled = true;
					
					if(numeroCanali > 0) {
						for (CanaleConfigurazione canaleConfigurazione : gestioneCanali.getCanaleList()) {
							if(canaleConfigurazione.isCanaleDefault()) {
								canaliDefault = canaleConfigurazione.getNome();
								break;
							}
						}
					}
				}
			}

			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			dati.add(ServletUtils.getDataElementForEditModeFinished());

			DataElement de = new DataElement();
			de.setName(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CACHES);
			de.setValue(configurazioneCachesTmp);
			de.setType(DataElementType.HIDDEN);
			dati.add(de);
			
			dati = confHelper.addConfigurazioneToDati(isAllHiddenConfigurazione,  
					inoltromin, stato, controllo, severita, severitaLog4j, integman, nomeintegman, profcoll, 
					connessione, utilizzo, validman, gestman, registrazioneTracce, dumpPD, dumpPA, 
					xsd, tipoValidazione, confPers, configurazione, dati, applicaMTOM,
					urlInvocazionePA, urlInvocazionePD,
					multitenantEnabled, multitenantSoggettiFruizioni, multitenantSoggettiErogazioni, true, 
					corsStato, corsTipo, corsAllAllowOrigins, corsAllAllowHeaders, corsAllAllowMethods, 
					corsAllowHeaders, corsAllowOrigins, corsAllowMethods, corsAllowCredential, corsExposeHeaders, corsMaxAge, corsMaxAgeSeconds,
					responseCachingEnabled,	responseCachingSeconds, responseCachingMaxResponseSize,	responseCachingMaxResponseSizeBytes, 
					responseCachingDigestUrlInvocazione, responseCachingDigestHeaders, responseCachingDigestPayload, responseCachingDigestHeadersNomiHeaders, responseCachingDigestQueryParameter, responseCachingDigestNomiParametriQuery,
					responseCachingCacheControlNoCache, responseCachingCacheControlMaxAge, responseCachingCacheControlNoStore, visualizzaLinkConfigurazioneRegola,
					servletResponseCachingConfigurazioneRegolaList, paramsResponseCachingConfigurazioneRegolaList, numeroResponseCachingConfigurazioneRegola, numeroRegoleProxyPass,
					canaliEnabled, numeroCanali, numeroNodi, canaliNome, canaliDescrizione, canaleList, canaliDefault,
					numeroArchiviPlugins, numeroClassiPlugins);

			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_DATI_RICHIESTE,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_DATI_RICHIESTE,statoCachEdatiRichieste,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_DATI_RICHIESTE,dimensionecachEdatiRichieste,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_DATI_RICHIESTE,algoritmocachEdatiRichieste,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_DATI_RICHIESTE,idlecachEdatiRichieste,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_DATI_RICHIESTE,lifecachEdatiRichieste,
					isAllHiddenCache);
			
			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_REGISTRY,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_REGISTRY,statoCacheRegistry,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_REGISTRY,dimensionecacheRegistry,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_REGISTRY,algoritmocacheRegistry,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_REGISTRY,idlecacheRegistry,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_REGISTRY,lifecacheRegistry,
					isAllHiddenCache);
			
			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONFIG,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONFIG,statoCacheConfig,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONFIG,dimensionecacheConfig,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONFIG,algoritmocacheConfig,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONFIG,idlecacheConfig,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONFIG,lifecacheConfig,
					isAllHiddenCache);

			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHZ,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHZ,statoCacheAuthz,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHZ,dimensionecacheAuthz,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHZ,algoritmocacheAuthz,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHZ,idlecacheAuthz,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHZ,lifecacheAuthz,
					isAllHiddenCache);

			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_AUTHN,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_AUTHN,statoCacheAuthn,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_AUTHN,dimensionecacheAuthn,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_AUTHN,algoritmocacheAuthn,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_AUTHN,idlecacheAuthn,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_AUTHN,lifecacheAuthn,
					isAllHiddenCache);

			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_TOKEN,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_TOKEN,statoCacheToken,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_TOKEN,dimensionecacheToken,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_TOKEN,algoritmocacheToken,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_TOKEN,idlecacheToken,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_TOKEN,lifecacheToken,
					isAllHiddenCache);
			
			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_ATTRIBUTE_AUTHORITY,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_ATTRIBUTE_AUTHORITY,statoCacheAA,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_ATTRIBUTE_AUTHORITY,dimensionecacheAA,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_ATTRIBUTE_AUTHORITY,algoritmocacheAA,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_ATTRIBUTE_AUTHORITY,idlecacheAA,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_ATTRIBUTE_AUTHORITY,lifecacheAA,
					isAllHiddenCache);
			
			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_KEYSTORE,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_KEYSTORE,statoCacheKeystore,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_KEYSTORE,dimensionecacheKeystore,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_KEYSTORE,algoritmocacheKeystore,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_KEYSTORE,idlecacheKeystore,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_KEYSTORE,lifecacheKeystore,
					isAllHiddenCache);
			confHelper.setDataElementCRLCacheInfo(dati, 
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CRL_LIFE_CACHE_KEYSTORE,crlLifecacheKeystore,
					isAllHiddenCache);

			if(!isAllHiddenCache) {
				confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONTROLLO_TRAFFICO,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONTROLLO_TRAFFICO,statoCacheControlloTraffico,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONTROLLO_TRAFFICO,dimensionecacheControlloTraffico,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONTROLLO_TRAFFICO,algoritmocacheControlloTraffico,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONTROLLO_TRAFFICO,idlecacheControlloTraffico,
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONTROLLO_TRAFFICO,lifecacheControlloTraffico,
						isAllHiddenCache);
			}
			
			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_RISPOSTE,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_RISPOSTE,statoCacheRisposte,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_RISPOSTE,dimensionecacheRisposte,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_RISPOSTE,algoritmocacheRisposte,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_RISPOSTE,idlecacheRisposte,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_RISPOSTE,lifecacheRisposte,
					isAllHiddenCache);

			confHelper.setDataElementCache(dati,ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CACHE_CONSEGNA_APPLICATIVI,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_STATO_CACHE_CONSEGNA,statoCacheConsegna,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DIMENSIONE_CACHE_CONSEGNA,dimensionecacheConsegna,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_ALGORITMO_CACHE_CONSEGNA,algoritmocacheConsegna,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_IDLE_CACHE_CONSEGNA,idlecacheConsegna,
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIFE_CACHE_CONSEGNA,lifecacheConsegna,
					isAllHiddenCache);
			
			if(extendedBeanList!=null && !extendedBeanList.isEmpty()){
				for (ExtendedInfo ei : extendedBeanList) {
					if(ei.extended){
						if(ei.extendedToNewWindow){
							AbstractServletNewWindowChangeExtended.addToDatiNewWindow(ei.extendedServlet, 
									dati, confHelper, confCore, configurazione, ei.extendedBean, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE_EXTENDED);
						}else{
							ei.extendedServlet.addToDati(dati, TipoOperazione.CHANGE, confHelper, confCore, configurazione, ei.extendedBean);
						}
					}
				}
			}

			pd.setDati(dati);

			String msg = confHelper.getParameter(Costanti.PARAMETER_NAME_MSG_ERROR_EXPORT);
			if(msg!=null && !"".equals(msg)){
				pd.setMessage("Errore durante esportazione: "+msg);
			}

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE,
					ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_GENERALE);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.TIPO_OPERAZIONE_CONFIGURAZIONE_GENERALE);
		}
	}

}


class ExtendedInfo{

	IExtendedBean extendedBean = null;
	boolean extended = false;
	boolean extendedToNewWindow = false;
	IExtendedFormServlet extendedServlet = null;

}
