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


package org.openspcoop2.web.ctrlstat.servlet.config;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ConfigurazioneResponseCachingConfigurazioneRegolaAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConfigurazioneResponseCachingConfigurazioneRegolaAdd extends Action {

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
			
			// Preparo il menu
			confHelper.makeMenu();
			
			String returnCode = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE);
			if(returnCode == null)
				returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI;
			
			String statusMin = confHelper.getParametroInteger(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MIN);
			if(statusMin == null)
				statusMin = "";
			String statusMax = confHelper.getParametroInteger(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_MAX);
			if(statusMax == null)
				statusMax = "";
			String fault = confHelper.getParametroBoolean(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_FAULT);
			String cacheSeconds = confHelper.getParametroInteger(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_CACHE_TIMEOUT_SECONDS);
			if(cacheSeconds == null)
				cacheSeconds = "";

			ConfigurazioneCore confCore = new ConfigurazioneCore();

			List<Parameter> lstParam = new ArrayList<>();
			
			String postBackElementName = confHelper.getPostBackElementName();
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null &&
				postBackElementName.equals(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE)) {
				statusMin = "";
				statusMax = "";
			}

			// setto la barra del titolo
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_GENERALE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_GENERALE));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLE, ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_LIST));
			lstParam.add(ServletUtils.getParameterAggiungi());
			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (confHelper.isEditModeInProgress()) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = confHelper.addResponseCachingConfigurazioneRegola(TipoOperazione.ADD, returnCode, statusMin, statusMax, fault, cacheSeconds, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA, 
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.responseCachingConfigurazioneRegolaCheckData(TipoOperazione.ADD);
			if (!isOk) {

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = confHelper.addResponseCachingConfigurazioneRegola(TipoOperazione.ADD, returnCode, statusMin, statusMax, fault, cacheSeconds, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA, 
						ForwardParams.ADD());
			}

			// salvataggio regola
			ResponseCachingConfigurazioneRegola regola = new ResponseCachingConfigurazioneRegola();
			
			if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI)) {
				regola.setReturnCodeMin(null);
				regola.setReturnCodeMax(null);
			} else if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO)) {
				regola.setReturnCodeMin(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
				regola.setReturnCodeMax(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
			} else { // intervallo
				regola.setReturnCodeMin(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
				regola.setReturnCodeMax(StringUtils.isNotEmpty(statusMax) ? Integer.parseInt(statusMax) : null);
			}

			regola.setFault(ServletUtils.isCheckBoxEnabled(fault)); 
			regola.setCacheTimeoutSeconds(StringUtils.isNotEmpty(cacheSeconds) ? Integer.parseInt(cacheSeconds) : null);
			
			Configurazione configurazioneGenerale = confCore.getConfigurazioneGenerale();
			ResponseCachingConfigurazioneGenerale responseCaching = configurazioneGenerale.getResponseCaching();
			ResponseCachingConfigurazione configurazione = responseCaching.getConfigurazione();
			configurazione.addRegola(regola);
			
			confCore.performUpdateOperation(userLogin, confHelper.smista(), configurazioneGenerale);
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA;

			ricerca = confHelper.checkSearchParameters(idLista, ricerca);

			List<ResponseCachingConfigurazioneRegola> lista = confCore.responseCachingConfigurazioneRegolaList(ricerca); 

			confHelper.prepareResponseCachingConfigurazioneRegolaList(ricerca, lista, configurazione.getCacheTimeoutSeconds());
						
			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_MODIFICATA_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA, ForwardParams.ADD());
		}
	}


}
