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
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteDelegateDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		// PageData pdold = (PageData) session.getAttribute("PageData");

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, session);


		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			int soggInt = -1;
			if(useIdSogg){
				String idsogg = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
				soggInt = Integer.parseInt(idsogg);
			}
			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// Elimino le porte delegate dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			IExtendedListServlet extendedServlet = porteDelegateCore.getExtendedServletPortaDelegata();
			List<Object> listPerformOperations = new ArrayList<Object>();
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				// int idporta = Integer.parseInt(de.getValue());

				// Elimino la porta delegata
				PortaDelegata pde = porteDelegateCore.getPortaDelegata(Long.parseLong(idsToRemove.get(i)));
				
				if(extendedServlet!=null){
					List<IExtendedBean> listExt = null;
					try{
						listExt = extendedServlet.extendedBeanList(TipoOperazione.DEL,porteDelegateHelper,porteDelegateCore,pde);
					}catch(Exception e){
						ControlStationCore.logError(e.getMessage(), e);
					}
					if(listExt!=null && listExt.size()>0){
						for (IExtendedBean iExtendedBean : listExt) {
							WrapperExtendedBean wrapper = new WrapperExtendedBean();
							wrapper.setExtendedBean(iExtendedBean);
							wrapper.setExtendedServlet(extendedServlet);
							wrapper.setOriginalBean(pde);
							wrapper.setManageOriginalBean(false);		
							listPerformOperations.add(wrapper);
						}
					}
				}
				
				listPerformOperations.add(pde);
			}
			
			porteDelegateCore.performDeleteOperation(userLogin, porteDelegateHelper.smista(), listPerformOperations.toArray(new Object[1]));

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<PortaDelegata> lista = null;
			if(useIdSogg){
				int idLista = Liste.PORTE_DELEGATE_BY_SOGGETTO;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(soggInt, ricerca);
			}else{ 
				int idLista = Liste.PORTE_DELEGATE;
				ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
				lista = porteDelegateCore.porteDelegateList(null, ricerca);
			}

			porteDelegateHelper.preparePorteDelegateList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE ,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE, 
					ForwardParams.DEL());
		} 
	}
}
