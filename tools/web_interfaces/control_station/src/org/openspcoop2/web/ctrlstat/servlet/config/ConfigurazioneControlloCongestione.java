/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import org.openspcoop2.core.controllo_congestione.Cache;
import org.openspcoop2.core.controllo_congestione.ConfigurazioneControlloTraffico;
import org.openspcoop2.core.controllo_congestione.ConfigurazioneRateLimiting;
import org.openspcoop2.core.controllo_congestione.TempiRispostaErogazione;
import org.openspcoop2.core.controllo_congestione.TempiRispostaFruizione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/***
 * 
 * 
 * @author pintori
 *
 */
public class ConfigurazioneControlloCongestione extends Action {

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
			

			org.openspcoop2.core.controllo_congestione.ConfigurazioneGenerale configurazioneControlloCongestione = confCore.getConfigurazioneControlloCongestione();
			long sizePolicy = confCore.countConfigurazionePolicy(null);
			long sizeGlobalPolicy = confCore.countAttivazionePolicy(null);
			
			// Stato [si usa per capire se sono entrato per la prima volta nella schermata]		
			boolean first = confHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_CONGESTIONE_FIRST_TIME); 
			
			if(configurazioneControlloCongestione.getControlloTraffico()==null){
				configurazioneControlloCongestione.setControlloTraffico(new ConfigurazioneControlloTraffico());
			}
			if(configurazioneControlloCongestione.getRateLimiting()==null){
				configurazioneControlloCongestione.setRateLimiting(new ConfigurazioneRateLimiting());
			}
			if(configurazioneControlloCongestione.getCache() ==null){
				configurazioneControlloCongestione.setCache(new Cache());
			}
			if(configurazioneControlloCongestione.getTempiRispostaErogazione() ==null){
				configurazioneControlloCongestione.setTempiRispostaErogazione(new TempiRispostaErogazione());
			}
			if(configurazioneControlloCongestione.getTempiRispostaFruizione() ==null){
				configurazioneControlloCongestione.setTempiRispostaFruizione(new TempiRispostaFruizione());
			}
			
			StringBuilder sbParsingError = new StringBuilder();
			// Limitazioni
			
			String errorControlloTraffico = confHelper.readConfigurazioneControlloTrafficoFromHttpParameters(configurazioneControlloCongestione.getControlloTraffico(), first);
			if(errorControlloTraffico!=null){
				confHelper.addParsingError(sbParsingError,errorControlloTraffico);
			}
			
			
			// RateLimiting
			
			String errorRateLimiting = confHelper.readConfigurazioneRateLimitingFromHttpParameters(configurazioneControlloCongestione.getRateLimiting(), first);
			if(errorRateLimiting!=null){
				confHelper.addParsingError(sbParsingError,errorRateLimiting);
			}
			

			// Tempi di Risposta Fruizione
			
			String errorTempiRispostaFruizione = confHelper.readTempiRispostaFruizioneFromHttpParameters(configurazioneControlloCongestione.getTempiRispostaFruizione(), first);
			if(errorTempiRispostaFruizione!=null){
				confHelper.addParsingError(sbParsingError,errorTempiRispostaFruizione);
			}
			
			
			// Tempi di Risposta Erogazione
			
			String errorTempiRispostaErogazione = confHelper.readTempiRispostaErogazioneFromHttpParameters(configurazioneControlloCongestione.getTempiRispostaErogazione(),  first);
			if(errorTempiRispostaErogazione!=null){
				confHelper.addParsingError(sbParsingError,errorTempiRispostaErogazione);
			}

			
			// Cache
			
			String errorCache = confHelper.readConfigurazioneCacheFromHttpParameters(configurazioneControlloCongestione.getCache(), first);
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
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_CONGESTIONE_FIRST_TIME);

				confHelper.addConfigurazionControlloCongestioneToDati(dati, tipoOperazione, configurazioneControlloCongestione, sizePolicy, sizeGlobalPolicy, false); 

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_CONGESTIONE,	ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.checkDatiConfigurazioneControlloCongestione(tipoOperazione, sbParsingError, configurazioneControlloCongestione);
			if (!isOk) {

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_CONGESTIONE_FIRST_TIME);

				confHelper.addConfigurazionControlloCongestioneToDati(dati, tipoOperazione, configurazioneControlloCongestione, sizePolicy, sizeGlobalPolicy, false); 

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_CONGESTIONE,	ForwardParams.OTHER(""));
			}

			confCore.performUpdateOperation(userLogin, confHelper.smista(), configurazioneControlloCongestione);

			// Preparo la lista
			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_CONTROLLO_TRAFFICO_MODIFICATA_CON_SUCCESSO_RIAVVIO_RICHIESTO , Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			// ricarico la configurazione
			configurazioneControlloCongestione = confCore.getConfigurazioneControlloCongestione();

			// Set First is false
			confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);

			confHelper.addConfigurazionControlloCongestioneToDati(dati, tipoOperazione, configurazioneControlloCongestione, sizePolicy, sizeGlobalPolicy, true); 

			pd.setDati(dati);
			pd.disableEditMode();

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_CONGESTIONE, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_CONTROLLO_CONGESTIONE, ForwardParams.OTHER(""));
		}
	}
}
