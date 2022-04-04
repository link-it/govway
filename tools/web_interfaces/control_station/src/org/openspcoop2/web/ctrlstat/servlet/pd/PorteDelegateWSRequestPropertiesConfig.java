/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
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
import org.openspcoop2.web.lib.mvc.properties.utils.ReadPropertiesUtilities;

/**     
 * PorteDelegateWSRequestPropertiesConfig
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateWSRequestPropertiesConfig  extends Action {

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
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

			String configName = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_PROPERTIES_CONFIG_NAME);
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";

			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			// Stato [si usa per capire se sono entrato per la prima volta nella schermata]		
			boolean first = porteDelegateHelper.isFirstTimeFromHttpParameters(CostantiControlStation.PARAMETRO_CONTROLLO_FIRST_TIME); 

			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();

			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = pde.getNome();
			
			PropertiesSourceConfiguration propertiesSourceConfiguration = porteDelegateCore.getMessageSecurityPropertiesSourceConfiguration();
			ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
			Config configurazione = configManager.getConfigurazione(propertiesSourceConfiguration, configName);
			
			Map<String, Properties> mappaDB = porteDelegateCore.readMessageSecurityRequestPropertiesConfiguration(pde.getId()); 
			
			ConfigBean configurazioneBean = ReadPropertiesUtilities.leggiConfigurazione(configurazione, mappaDB);
			
			if(!first) { // la prima volta non sovrascrivo la configurazione con i valori letti dai parametri
				porteDelegateHelper.aggiornaConfigurazioneProperties(configurazioneBean);
			} else {
				// reset di eventuali configurazioni salvate in sessione
				ServletUtils.removeConfigurazioneBeanFromSession(session, configurazioneBean.getId());
			}
			
			configurazioneBean.updateConfigurazione(configurazione);
			ServletUtils.saveConfigurazioneBeanIntoSession(session, configurazioneBean, configurazioneBean.getId());

			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			Parameter pIdPropertiesConfigReq= new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_PROPERTIES_CONFIG_NAME, configName);
			Parameter[] urlParms = { pId,pIdSoggetto,pIdAsps,pIdFrizione,pIdPropertiesConfigReq };

			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);

			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY,
						pde);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta, 
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, urlParms));

			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG + " " + configurazione.getLabel() ,null));

			
			if(porteDelegateHelper.isEditModeInProgress()){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
//				configurazioneBean.updateConfigurazione(configurazione);

				dati = porteDelegateHelper.addPropertiesConfigToDati(tipoOperazione,dati, configName, configurazioneBean);

				dati = porteDelegateHelper.addHiddenFieldsToDati(tipoOperazione, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);
				
				// Set First Time == false
				porteDelegateHelper.addToDatiFirstTimeDisabled(dati,CostantiControlStation.PARAMETRO_CONTROLLO_FIRST_TIME);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_PROPERTIES_CONFIG, ForwardParams.OTHER(""));
			}
			
			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.checkPropertiesConfigurationData(tipoOperazione, configurazioneBean, null, null, configurazione);
			if(!isOk){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
//				configurazioneBean.updateConfigurazione(configurazione);

				dati = porteDelegateHelper.addPropertiesConfigToDati(tipoOperazione,dati, configName, configurazioneBean);

				dati = porteDelegateHelper.addHiddenFieldsToDati(tipoOperazione, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);
				
				// Set First Time == false
				porteDelegateHelper.addToDatiFirstTimeDisabled(dati,CostantiControlStation.PARAMETRO_CONTROLLO_FIRST_TIME);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,	PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_PROPERTIES_CONFIG, ForwardParams.OTHER(""));
			}
			
			Map<String, Properties> mappaDestinazione = configurazioneBean.getPropertiesMap();	
			Map<String, String> map = DBPropertiesUtils.toMap(mappaDestinazione);
			
			if(pde.getMessageSecurity().getRequestFlow().getParameterList() == null)
				pde.getMessageSecurity().getRequestFlow().setParameterList(new ArrayList<MessageSecurityFlowParameter>());
			
			pde.getMessageSecurity().getRequestFlow().getParameterList().clear();
			
			for (String propKey : map.keySet()) {
				MessageSecurityFlowParameter parameter = new MessageSecurityFlowParameter();
				parameter.setNome(propKey);
				parameter.setValore(map.get(propKey));
				
				pde.getMessageSecurity().getRequestFlow().addParameter(parameter);
			}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);
			
			mappaDB = porteDelegateCore.readMessageSecurityRequestPropertiesConfiguration(pde.getId()); 
			
			configurazioneBean = ReadPropertiesUtilities.leggiConfigurazione(configurazione, mappaDB);
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			configurazioneBean.updateConfigurazione(configurazione);
			ServletUtils.saveConfigurazioneBeanIntoSession(session, configurazioneBean, configurazioneBean.getId());

			dati = porteDelegateHelper.addPropertiesConfigToDati(tipoOperazione,dati, configName, configurazioneBean);

			dati = porteDelegateHelper.addHiddenFieldsToDati(tipoOperazione, id, idsogg, null, idAsps, 
					idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);
			
			// Set First Time == false
			porteDelegateHelper.addToDatiFirstTimeDisabled(dati,CostantiControlStation.PARAMETRO_CONTROLLO_FIRST_TIME);
			
			pd.setDati(dati);
			
			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_CONFIGURAZIONE_PROPERTIES_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_PROPERTIES_CONFIG, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_PROPERTIES_CONFIG, 
					ForwardParams.OTHER(""));
		}  
	}
}
