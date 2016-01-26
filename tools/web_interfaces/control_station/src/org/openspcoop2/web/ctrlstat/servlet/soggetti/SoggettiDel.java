/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.web.ctrlstat.servlet.soggetti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.DBOggettiInUsoUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
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
			// ctrlstatHelper ch = new ctrlstatHelper(request, pd, con, session);
			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);

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
			boolean isInUso = false;
			
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);
	
			soggettiHelper.makeMenu();
	
			String userLogin = ServletUtils.getUserLoginFromSession(session);
	
			SoggettiCore soggettiCore = new SoggettiCore();

			String msg = "";

			for (int i = 0; i < idsToRemove.size(); i++) {

				Soggetto soggettoRegistro = null;
				org.openspcoop2.core.config.Soggetto soggettoConfig = null;
				IDSoggetto idSoggetto = null;
				if(soggettiCore.isRegistroServiziLocale()){
					soggettoRegistro = soggettiCore.getSoggettoRegistro(Long.parseLong(idsToRemove.get(i)));
					idSoggetto = new IDSoggetto(soggettoRegistro.getTipo(), soggettoRegistro.getNome());
					soggettoConfig = soggettiCore.getSoggetto(idSoggetto);
				}
				else{
					soggettoConfig = soggettiCore.getSoggetto(Long.parseLong(idsToRemove.get(i)));
					idSoggetto = new IDSoggetto(soggettoConfig.getTipo(), soggettoConfig.getNome());
				}
				boolean soggettoInUso = false;
				HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
				if(soggettiCore.isRegistroServiziLocale()){
					soggettoInUso = soggettiCore.isSoggettoInUso(soggettoRegistro, whereIsInUso);
				}else{
					soggettoInUso = soggettiCore.isSoggettoInUso(soggettoConfig, whereIsInUso);
				}


				if (soggettoInUso) {
					isInUso = true;
					msg += DBOggettiInUsoUtils.toString(idSoggetto, whereIsInUso, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
					msg += org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;

				} else {
					SoggettoCtrlStat sog = new SoggettoCtrlStat(soggettoRegistro, soggettoConfig);
					soggettiCore.performDeleteOperation(userLogin, soggettiHelper.smista(), sog);
				}
			}// chiudo for

			if (isInUso) {
				pd.setMessage(msg);
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

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					SoggettiCostanti.OBJECT_NAME_SOGGETTI, ForwardParams.DEL());
		} 
	}
}
