/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/***
 * 
 * PorteDelegateMTOMRequestDel
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateMTOMRequestDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// Elimino i message-security della porta delegata dal db
			String nome = "";
	
			// Prendo la porta delegata
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			MtomProcessor mtomProcessor  = pde.getMtomProcessor();
	
			for (int i = 0; i < idsToRemove.size(); i++) {
	
				nome = idsToRemove.get(i);
	
				if(mtomProcessor.getRequestFlow()!=null){
					List<MtomProcessorFlowParameter> wsrfpArray = mtomProcessor.getRequestFlow().getParameterList();
					for (int j = 0; j < wsrfpArray.size(); j++) {
						MtomProcessorFlowParameter wsrfp = wsrfpArray.get(j);
						if (nome.equals(wsrfp.getNome())) {
							mtomProcessor.getRequestFlow().removeParameter(j);
							break;
						}
					}
				}
			}
	
			pde.setMtomProcessor(mtomProcessor);
	
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);
	
			// Preparo il menu
			porteDelegateHelper.makeMenu();
	
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
	
			int idLista = Liste.PORTE_DELEGATE_MTOM_REQUEST;
	
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
	
			List<MtomProcessorFlowParameter> lista = porteDelegateCore.porteDelegateMTOMRequestList(Integer.parseInt(id), ricerca);
	
			porteDelegateHelper.preparePorteDelegateMTOMRequestList(pde.getNome(), ricerca, lista);
	
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
		 	return ServletUtils.getStrutsForward(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM_REQUEST,
		 			ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM_REQUEST, 
					ForwardParams.DEL());
		}
	}

}
