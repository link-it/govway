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


package org.openspcoop2.web.ctrlstat.servlet.pdd;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.DBOggettiInUsoUtils;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * pddSinglePdDDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PddSinglePdDDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		try {
			PddHelper pddHelper = new PddHelper(request, pd, session);

			String objToRemove = request.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			PddCore pddCore = new PddCore();

			// Preparo il menu
			pddHelper.makeMenu();

			PortaDominio pdd = null;
			StringBuffer pddOperative = new StringBuffer();
			
			boolean isInUso = false;
			String msg = "";
			
			for (int i = 0; i < idsToRemove.size(); i++) {
				
				pdd = pddCore.getPortaDominio(idsToRemove.get(i));
				PdDControlStation pddControlStation = pddCore.getPdDControlStation(pdd.getNome());
				
				if(PddTipologia.OPERATIVO.toString().equals(pddControlStation.getTipo())){
					if(pddOperative.length()>0)
						pddOperative.append(",");
					pddOperative.append(pddControlStation.getNome());
				}
				else{
					ArrayList<String> infos = new ArrayList<String>();
					if (pddCore.isPddInUso(pddControlStation, infos)) {
						isInUso = true;
						msg += DBOggettiInUsoUtils.toString(pdd.getNome(), infos, true, org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						msg += org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;

					} else {
						pddCore.performDeleteOperation(userLogin, pddHelper.smista(), pdd);
					}
				}
				
			}
			
			if(pddOperative.length()>0){
				isInUso = true;
				msg += "La Porta di Dominio ["+pddOperative+"] è non rimuovibile poichè rappresenta la Porta di Dominio locale";
				msg += "<br>";
			}
			
			if (isInUso) {
				pd.setMessage(msg);
			}
			

			// preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session,Search.class); 

			List<PdDControlStation> lista = null;
			if(pddCore.isVisioneOggettiGlobale(userLogin)){
				lista = pddCore.pddList(null, ricerca);
			}else{
				lista = pddCore.pddList(userLogin, ricerca);
			}

			pddHelper.preparePddSinglePddList(lista, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward(mapping, PddCostanti.OBJECT_NAME_PDD_SINGLEPDD, ForwardParams.DEL());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PddCostanti.OBJECT_NAME_PDD_SINGLEPDD, ForwardParams.DEL());
		}
	}
}
