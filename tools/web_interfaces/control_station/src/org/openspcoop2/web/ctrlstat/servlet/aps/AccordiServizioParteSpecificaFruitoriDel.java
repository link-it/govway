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


package org.openspcoop2.web.ctrlstat.servlet.aps;

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
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Servizio;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * serviziFruitoriDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteSpecificaFruitoriDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		PageData pdold = ServletUtils.getPageDataFromSession(session);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);


		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			/*
			 * Validate the request parameters specified by the user Note: Basic
			 * field validation done in porteDomForm.java Business logic
			 * validation done in porteDomAdd.java
			 */
			String id = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			int idServizio = Integer.parseInt(id);
			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);

			// prendo l'id del soggetto erogatore lo propago
			// lo metto nel pd come campo hidden
			// PageData oldPD = (PageData) session.getAttribute("PageData");
			pd.setHidden(pdold.getHidden());

			// Preparo il menu
			apsHelper.makeMenu();

			// Elimino i fruitori del servizio dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			String fruitore = "";
			Fruitore fru = null;

			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idServizio);
			Servizio ss = asps.getServizio();
			String nomeservizio = ss.getNome();
			String tiposervizio = ss.getTipo();
			// Prendo i dati del soggetto erogatore del servizio
			String mynomeprov = ss.getNomeSoggettoErogatore();
			String mytipoprov = ss.getTipoSoggettoErogatore();

			String superUser =  ServletUtils.getUserLoginFromSession(session);

			IExtendedListServlet extendedServlet = porteDelegateCore.getExtendedServletPortaDelegata();
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				// DataElement de = (DataElement) ((Vector<?>) pdold.getDati()
				// .elementAt(idToRemove[i])).elementAt(0);
				fruitore = idsToRemove.get(i);// de.getValue();

				int idFru = Integer.parseInt(fruitore);

				for (int j = 0; j < asps.sizeFruitoreList(); j++) {
					fru = asps.getFruitore(j);
					if (fru.getId().intValue() == idFru) {
						// rimuovo le politiche di sicurezza del fruitore
						Soggetto sogFruitore = soggettiCore.getSoggetto(new IDSoggetto(fru.getTipo(), fru.getNome()));
						apsCore.deleteAllPoliticheSicurezza(asps.getId(), sogFruitore.getId());
						asps.removeFruitore(j);
						break;
					}
				}

				// cancello la porta delegata associata al fruitore
				// del servizio, se esiste
				String nomePD = fru.getTipo() + fru.getNome() + "/" + mytipoprov + mynomeprov + "/" + tiposervizio + nomeservizio;
				IDPortaDelegata myidpd = new IDPortaDelegata();
				IDSoggetto ids = new IDSoggetto(fru.getTipo(), fru.getNome());
				myidpd.setSoggettoFruitore(ids);
				myidpd.setLocationPD(nomePD);
				if (porteDelegateCore.existsPortaDelegata(myidpd)) {
					PortaDelegata mypd = porteDelegateCore.getPortaDelegata(myidpd);
					
					List<Object> listPerformOperations = new ArrayList<Object>();
					
					if(extendedServlet!=null){
						List<IExtendedBean> listExt = null;
						try{
							listExt = extendedServlet.extendedBeanList(TipoOperazione.DEL,apsHelper,apsCore,mypd);
						}catch(Exception e){
							ControlStationCore.logError(e.getMessage(), e);
						}
						if(listExt!=null && listExt.size()>0){
							for (IExtendedBean iExtendedBean : listExt) {
								WrapperExtendedBean wrapper = new WrapperExtendedBean();
								wrapper.setExtendedBean(iExtendedBean);
								wrapper.setExtendedServlet(extendedServlet);
								wrapper.setOriginalBean(mypd);
								wrapper.setManageOriginalBean(false);		
								listPerformOperations.add(wrapper);
							}
						}
					}
					
					listPerformOperations.add(mypd);
					
					apsCore.performDeleteOperation(superUser, apsHelper.smista(), listPerformOperations.toArray(new Object[1]) );
				}
			}

			apsCore.performUpdateOperation(superUser, apsHelper.smista(), asps);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.SERVIZI_FRUITORI;

			ricerca = apsHelper.checkSearchParameters(idLista, ricerca);

			List<Fruitore> lista = apsCore.serviziFruitoriList(idServizio, ricerca);

			apsHelper.prepareServiziFruitoriList(lista, id, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_FRUITORI,
					ForwardParams.DEL());
		} 
	}
}
