/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.soggetti;

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
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * soggettiDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class SoggettiDel extends Action {

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

		try {
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);
			
			// ctrlstatHelper ch = new ctrlstatHelper(request, pd, con, session);
			String objToRemove = soggettiHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);

			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			// // Elimino i soggetti dal db
			// StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			// int[] idToRemove = new int[objTok.countTokens()];
			//
			// int k = 0;
			// while (objTok.hasMoreElements()) {
			// idToRemove[k++] = Integer.parseInt(objTok.nextToken());
			// }
	
			// String soggInUsoServizioApplicativo = "", soggInUsoServ = "",
			// soggInUsoPorteDel = "";
			// String nomeprov = "", tipoprov = "";
			
			soggettiHelper.makeMenu();
	
			String userLogin = ServletUtils.getUserLoginFromSession(session);
	
			SoggettiCore soggettiCore = new SoggettiCore();
			
			StringBuilder inUsoMessage = new StringBuilder();

			boolean deleteOperativo = false;
			boolean deleteAlmostOne = false;
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				Soggetto soggettoRegistro = null;
				org.openspcoop2.core.config.Soggetto soggettoConfig = null;
				if(soggettiCore.isRegistroServiziLocale()){
					soggettoRegistro = soggettiCore.getSoggettoRegistro(Long.parseLong(idsToRemove.get(i)));
				}
				else{
					soggettoConfig = soggettiCore.getSoggetto(Long.parseLong(idsToRemove.get(i)));
				}
				
				SoggettiDelStatus delStatus = SoggettiUtilities.deleteSoggetto(soggettoRegistro, soggettoConfig, userLogin, soggettiCore, soggettiHelper, inUsoMessage, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
				if(delStatus.isDeletedOperativo()) {
					deleteOperativo = true;
				}
				if(delStatus.isDeleted()) {
					deleteAlmostOne = true;
				}
				
			}// chiudo for

			if(deleteAlmostOne) {
				ServletUtils.removeRisultatiRicercaFromSession(session, Liste.SOGGETTI);
			}
			
			if (inUsoMessage.length()>0) {
				pd.setMessage(inUsoMessage.toString());
			}

			// preparo lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			if(soggettiCore.isRegistroServiziLocale()){
				List<Soggetto> lista = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					lista = soggettiCore.soggettiRegistroList(null, ricerca);
				}else{
					lista = soggettiCore.soggettiRegistroList(userLogin, ricerca);
				}
				soggettiHelper.prepareSoggettiList(lista, ricerca);
			}
			else{
				List<org.openspcoop2.core.config.Soggetto> lista = null;
				if(soggettiCore.isVisioneOggettiGlobale(userLogin)){
					lista = soggettiCore.soggettiList(null, ricerca);
				}else{
					lista = soggettiCore.soggettiList(userLogin, ricerca);
				}
				soggettiHelper.prepareSoggettiConfigList(lista, ricerca);
			}

			if(deleteOperativo) {
				generalHelper = new GeneralHelper(session);
				gd = generalHelper.initGeneralData(request); // re-inizializzo per ricalcolare il menu in alto a destra
			}
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.DEL());
		} 
	}
}
