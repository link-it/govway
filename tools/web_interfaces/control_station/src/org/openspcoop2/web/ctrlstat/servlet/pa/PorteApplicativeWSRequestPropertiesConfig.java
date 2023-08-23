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
package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
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
import org.openspcoop2.web.lib.mvc.properties.beans.ConfigBean;

/**     
 * PorteApplicativeWSRequestPropertiesConfig
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeWSRequestPropertiesConfig  extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		TipoOperazione tipoOperazione = TipoOperazione.OTHER;

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

			String configName = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_PROPERTIES_CONFIG_NAME);
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			// Stato [si usa per capire se sono entrato per la prima volta nella schermata]		
			boolean first = porteApplicativeHelper.isFirstTimeFromHttpParameters(CostantiControlStation.PARAMETRO_CONTROLLO_FIRST_TIME); 

			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idPorta = pa.getNome();
			
			PropertiesSourceConfiguration propertiesSourceConfiguration = porteApplicativeCore.getMessageSecurityPropertiesSourceConfiguration();
			ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
			Config configurazione = configManager.getConfigurazione(propertiesSourceConfiguration, configName);
			
			Map<String, Properties> mappaDB = porteApplicativeCore.readMessageSecurityRequestPropertiesConfiguration(pa.getId()); 
			
			ConfigBean configurazioneBean = porteApplicativeCore.leggiConfigurazione(configurazione, mappaDB);
			
			if(!first) { // la prima volta non sovrascrivo la configurazione con i valori letti dai parametri
				porteApplicativeHelper.aggiornaConfigurazioneProperties(configurazioneBean);
			} else {
				// reset di eventuali configurazioni salvate in sessione
				ServletUtils.removeConfigurazioneBeanFromSession(request, session, configurazioneBean.getId());
			}
			
			configurazioneBean.updateConfigurazione(configurazione);
			ServletUtils.saveConfigurazioneBeanIntoSession(request, session, configurazioneBean, configurazioneBean.getId());

			Parameter pId = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
			Parameter pIdPropertiesConfigReq= new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_PROPERTIES_CONFIG_NAME, configName);
			Parameter[] urlParms = { pId,pIdSoggetto,pIdAsps,pIdPropertiesConfigReq };

			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_CONFIG_DI+idPorta;
			}

			lstParam.add(new Parameter(labelPerPorta, 
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY, urlParms));

			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG + " " + configurazione.getLabel() ,null));

			
			if(porteApplicativeHelper.isEditModeInProgress()){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addPropertiesConfigToDati(tipoOperazione,dati, configName, configurazioneBean);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null,idAsps, dati);
				
				// Set First Time == false
				porteApplicativeHelper.addToDatiFirstTimeDisabled(dati,CostantiControlStation.PARAMETRO_CONTROLLO_FIRST_TIME);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_PROPERTIES_CONFIG, ForwardParams.OTHER(""));
			}
			
			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.checkPropertiesConfigurationData(tipoOperazione, configurazioneBean, null, null, configurazione);
			if(!isOk){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addPropertiesConfigToDati(tipoOperazione,dati, configName, configurazioneBean);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, dati);
				
				// Set First Time == false
				porteApplicativeHelper.addToDatiFirstTimeDisabled(dati,CostantiControlStation.PARAMETRO_CONTROLLO_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,	PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_PROPERTIES_CONFIG, ForwardParams.OTHER(""));
			}
			
			Map<String, Properties> mappaDestinazione = configurazioneBean.getPropertiesMap();	
			Map<String, String> map = DBPropertiesUtils.toMap(mappaDestinazione);
			
			if(pa.getMessageSecurity().getRequestFlow().getParameterList() == null)
				pa.getMessageSecurity().getRequestFlow().setParameterList(new ArrayList<>());
			
			pa.getMessageSecurity().getRequestFlow().getParameterList().clear();
			
			for (String propKey : map.keySet()) {
				MessageSecurityFlowParameter parameter = new MessageSecurityFlowParameter();
				parameter.setNome(propKey);
				parameter.setValore(map.get(propKey));
				
				pa.getMessageSecurity().getRequestFlow().addParameter(parameter);
			}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			mappaDB = porteApplicativeCore.readMessageSecurityRequestPropertiesConfiguration(pa.getId()); 
			
			configurazioneBean = porteApplicativeCore.leggiConfigurazione(configurazione, mappaDB);
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			configurazioneBean.updateConfigurazione(configurazione);
			ServletUtils.saveConfigurazioneBeanIntoSession(request, session, configurazioneBean, configurazioneBean.getId());

			dati = porteApplicativeHelper.addPropertiesConfigToDati(tipoOperazione,dati, configName, configurazioneBean);

			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, dati);
			
			// Set First Time == false
			porteApplicativeHelper.addToDatiFirstTimeDisabled(dati,CostantiControlStation.PARAMETRO_CONTROLLO_FIRST_TIME);
			
			pd.setDati(dati);
			
			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_CONFIGURAZIONE_PROPERTIES_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_PROPERTIES_CONFIG, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_PROPERTIES_CONFIG, 
					ForwardParams.OTHER(""));
		}  
	}
}
