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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.ArrayList;
import java.util.List;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceParameter;
import org.openspcoop2.core.registry.ResourceRequest;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.constants.ParameterType;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * AccordiServizioParteComuneResourcesParametersDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneResourcesParametersDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		// Inizializzo GeneralData
		GeneralHelper generalHelper = new GeneralHelper(session);

		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = (String) ServletUtils.getUserLoginFromSession(session);

		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			
			// Preparo il menu
			apcHelper.makeMenu();
			
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();

			String id = apcHelper.getParametroLong(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			long idAccordoLong = Long.valueOf(id);
			String objToRemove = apcHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			
			String nomeRisorsa = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_NOME);
			if (nomeRisorsa == null) {
				nomeRisorsa = "";
			}
			String statusS = apcHelper.getParametroInteger(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCES_RESPONSE_STATUS);
			Integer status = null;
			try {
				if(statusS!=null)
					status = Integer.parseInt(statusS);
			} catch(Exception e) {
				// ignore
			}
			String isReq = apcHelper.getParametroBoolean(AccordiServizioParteComuneCostanti.PARAMETRO_APC_RESOURCE_REQUEST);
			boolean isRequest = ServletUtils.isCheckBoxEnabled(isReq);

			ArrayList<String> resourcesToRemove = Utilities.parseIdsToRemove(objToRemove);
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idAccordoLong);

			Resource risorsa = null;
			for (int j = 0; j < as.sizeResourceList(); j++) {
				risorsa = as.getResource(j);
				if (nomeRisorsa.equals(risorsa.getNome())) {
					break;
				}
			}
			
			if(risorsa==null) {
				throw new Exception("Risorsa con nome '"+nomeRisorsa+"' non trovata nell'accordo con id '"+idAccordoLong+"'");
			}
			
			Long idResponse = null;
			Long idResource = null;
			ResourceRequest resourceRequest = null;
			ResourceResponse resourceResponse = null;
			List<ResourceParameter> parameterList = null;
			if(isRequest) {
				resourceRequest = risorsa.getRequest();
				idResource = risorsa.getId();
				if(resourceRequest != null)
					parameterList = resourceRequest.getParameterList();
			} else {
				if(risorsa.getResponseList() != null) {
					for (int i = 0; i < risorsa.getResponseList().size(); i++) {
						resourceResponse = risorsa.getResponse(i);
						if (resourceResponse.getStatus() == status) {
							idResponse = resourceResponse.getId();
							break;
						}
					}
					if(resourceResponse != null)
						parameterList = resourceResponse.getParameterList();
				}
			}
			
			String tipoNomeParameter = "";
			boolean modificaAS_effettuata = false;
			StringBuilder errori = new StringBuilder();
			for (int i = 0; i < resourcesToRemove.size(); i++) {
				tipoNomeParameter = resourcesToRemove.get(i);
				String[] split = tipoNomeParameter.split("/");
				String tipoParamaterS = (split != null && split.length > 0) ? split[0] : null;
				String nomeParameter = (split != null && split.length > 1) ? split[1] : "";
				ParameterType tipoParametro = tipoParamaterS!= null ? ParameterType.toEnumConstant(tipoParamaterS) : ParameterType.QUERY;
				
				// Effettuo eliminazione
				int idx = -1;			
				if(parameterList != null && parameterList.size() > 0) {
					for (int j  = 0; j  < parameterList.size(); j ++) {
						ResourceParameter resourceParameter = parameterList.get(j );
						if(resourceParameter.getNome().equals(nomeParameter) && resourceParameter.getParameterType().equals(tipoParametro)) {
							idx = j ;
							break;
						}
					}
					if(idx > -1) {
						modificaAS_effettuata = true;
						parameterList.remove(idx);
					}
				}
			}
			
			// imposto msg di errore se presente
			if(errori.length()>0)
				pd.setMessage(errori.toString());

			// effettuo le operazioni
			if(modificaAS_effettuata)
				apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			// Devo rileggere l'accordo dal db, perche' altrimenti
			// manca l'id dei port-type
			as = apcCore.getAccordoServizioFull(idAccordoLong);
			
			risorsa = null;
			for (int j = 0; j < as.sizeResourceList(); j++) {
				risorsa = as.getResource(j);
				if (nomeRisorsa.equals(risorsa.getNome())) {
					break;
				}
			}
			
			if(risorsa==null) {
				throw new Exception("Risorsa con nome '"+nomeRisorsa+"' non trovata nell'accordo con id '"+idAccordoLong+"'");
			}
			
			idResponse = null;
			idResource = null;
			resourceRequest = null;
			resourceResponse = null;
			if(isRequest) {
				resourceRequest = risorsa.getRequest();
				idResource = risorsa.getId();
			} else {
				if(risorsa.getResponseList() != null) {
					for (int i = 0; i < risorsa.getResponseList().size(); i++) {
						resourceResponse = risorsa.getResponse(i);
						if (resourceResponse.getStatus() == status) {
							idResponse = resourceResponse.getId();
							break;
						}
					}
				}
			}

			List<ResourceParameter> lista = apcCore.accordiResourceParametersList(idResource, isRequest, idResponse, ricerca);

			apcHelper.prepareAccordiResourcesParametersList(id, as, lista, ricerca, tipoAccordo, isRequest, risorsa, resourceRequest, resourceResponse);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_PARAMETERS, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_RESOURCES_PARAMETERS, ForwardParams.DEL());
		} 
	}
}
