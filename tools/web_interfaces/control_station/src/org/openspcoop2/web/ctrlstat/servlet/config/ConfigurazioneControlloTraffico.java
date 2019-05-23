/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.controllo_traffico.Cache;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneRateLimiting;
import org.openspcoop2.core.controllo_traffico.TempiRispostaErogazione;
import org.openspcoop2.core.controllo_traffico.TempiRispostaFruizione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
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
 * ConfigurazioneControlloTraffico
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneControlloTraffico extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		TipoOperazione tipoOperazione = TipoOperazione.OTHER;

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

			ConfigurazioneCore confCore = new ConfigurazioneCore();
			

			org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = confCore.getConfigurazioneControlloTraffico();
			
			// annullo eventuale ricerca precedente
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			if(ricerca!=null) {
				ricerca.addFilter(Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY, Filtri.FILTRO_TIPO_POLICY, CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_UTENTE);
			}
			
			// conto policy
			Search searchPolicyUtente = new Search();
			searchPolicyUtente.addFilter( Liste.CONFIGURAZIONE_CONTROLLO_TRAFFICO_CONFIGURAZIONE_POLICY, Filtri.FILTRO_TIPO_POLICY, 
					CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_TIPO_UTENTE);
			long sizePolicy = confCore.countConfigurazionePolicy(searchPolicyUtente);
			
			// conto policy attivate globalmente
			long sizeGlobalPolicy = confCore.countAttivazionePolicy(null,null,null);
			
			// Stato [si usa per capire se sono entrato per la prima volta nella schermata]		
			boolean first = confHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME); 
			
			if(configurazioneControlloTraffico.getControlloTraffico()==null){
				configurazioneControlloTraffico.setControlloTraffico(new org.openspcoop2.core.controllo_traffico.ConfigurazioneControlloTraffico());
			}
			if(configurazioneControlloTraffico.getRateLimiting()==null){
				configurazioneControlloTraffico.setRateLimiting(new ConfigurazioneRateLimiting());
			}
			if(configurazioneControlloTraffico.getCache() ==null){
				configurazioneControlloTraffico.setCache(new Cache());
			}
			if(configurazioneControlloTraffico.getTempiRispostaErogazione() ==null){
				configurazioneControlloTraffico.setTempiRispostaErogazione(new TempiRispostaErogazione());
			}
			if(configurazioneControlloTraffico.getTempiRispostaFruizione() ==null){
				configurazioneControlloTraffico.setTempiRispostaFruizione(new TempiRispostaFruizione());
			}
			
			StringBuilder sbParsingError = new StringBuilder();
			// Limitazioni
			
			String errorControlloTraffico = confHelper.readConfigurazioneControlloTrafficoFromHttpParameters(configurazioneControlloTraffico.getControlloTraffico(), first);
			if(errorControlloTraffico!=null){
				confHelper.addParsingError(sbParsingError,errorControlloTraffico);
			}
			
			
			// RateLimiting
			
			String errorRateLimiting = confHelper.readConfigurazioneRateLimitingFromHttpParameters(configurazioneControlloTraffico.getRateLimiting(), first);
			if(errorRateLimiting!=null){
				confHelper.addParsingError(sbParsingError,errorRateLimiting);
			}
			

			// Tempi di Risposta Fruizione
			
			String errorTempiRispostaFruizione = confHelper.readTempiRispostaFruizioneFromHttpParameters(configurazioneControlloTraffico.getTempiRispostaFruizione(), first);
			if(errorTempiRispostaFruizione!=null){
				confHelper.addParsingError(sbParsingError,errorTempiRispostaFruizione);
			}
			
			
			// Tempi di Risposta Erogazione
			
			String errorTempiRispostaErogazione = confHelper.readTempiRispostaErogazioneFromHttpParameters(configurazioneControlloTraffico.getTempiRispostaErogazione(),  first);
			if(errorTempiRispostaErogazione!=null){
				confHelper.addParsingError(sbParsingError,errorTempiRispostaErogazione);
			}

			
			// Cache
			
			String errorCache = confHelper.readConfigurazioneCacheFromHttpParameters(configurazioneControlloTraffico.getCache(), first);
			if(errorCache!=null){
				confHelper.addParsingError(sbParsingError,errorCache);
			}
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO, null));

			// edit in progress
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);

				confHelper.addConfigurazionControlloTrafficoToDati(dati, tipoOperazione, configurazioneControlloTraffico, sizePolicy, sizeGlobalPolicy, false); 

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO,	ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.checkDatiConfigurazioneControlloTraffico(tipoOperazione, sbParsingError, configurazioneControlloTraffico);
			if (!isOk) {

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_FIRST_TIME);

				confHelper.addConfigurazionControlloTrafficoToDati(dati, tipoOperazione, configurazioneControlloTraffico, sizePolicy, sizeGlobalPolicy, false); 

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO,	ForwardParams.OTHER(""));
			}

			confCore.performUpdateOperation(userLogin, confHelper.smista(), configurazioneControlloTraffico);

			// Preparo la lista
			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_MODIFICATA_CON_SUCCESSO_RIAVVIO_RICHIESTO , Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			// ricarico la configurazione
			configurazioneControlloTraffico = confCore.getConfigurazioneControlloTraffico();

			// Set First is false
			confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);

			confHelper.addConfigurazionControlloTrafficoToDati(dati, tipoOperazione, configurazioneControlloTraffico, sizePolicy, sizeGlobalPolicy, true); 

			pd.setDati(dati);
			pd.disableEditMode();

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_TRAFFICO, ForwardParams.OTHER(""));
		}
	}
}
