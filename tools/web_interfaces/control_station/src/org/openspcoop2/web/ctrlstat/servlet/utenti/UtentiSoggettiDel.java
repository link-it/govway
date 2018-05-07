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
package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.User;

/***
 * 
 * UtentiSoggettiDel
 * 
 * @author pintori
 *
 */
public class UtentiSoggettiDel extends Action {

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
			UtentiHelper utentiHelper = new UtentiHelper(request, pd, session);
			String nomesu = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_USERNAME);
			String objToRemove = utentiHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			
			// Preparo il menu
			utentiHelper.makeMenu();
			
			UtentiCore utentiCore = new UtentiCore();
			StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			List<IDSoggetto> soggettiDaRimuovere = new ArrayList<IDSoggetto>();
			
			while (objTok.hasMoreElements()) {
				String tipoNomeSoggetto = objTok.nextToken();
				soggettiDaRimuovere.add(new IDSoggetto(tipoNomeSoggetto.split("/")[0],tipoNomeSoggetto.split("/")[1]));
			}
			
			User user = utentiCore.getUser(nomesu);
			
			for (IDSoggetto idSoggetto : soggettiDaRimuovere) {
				if(user.getSoggetti() != null && user.getSoggetti().contains(idSoggetto)) {
					user.getSoggetti().remove(idSoggetto);
				}
			}
			
			// salvataggio sul db
			utentiCore.performUpdateOperation(userLogin, utentiHelper.smista(), user); 
	
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session,Search.class);
	
			int idLista = Liste.UTENTI_SOGGETTI;
	
			ricerca = utentiHelper.checkSearchParameters(idLista, ricerca);
	
			List<IDSoggetto> lista = utentiCore.utentiSoggettiList(nomesu, ricerca);
	
			utentiHelper.prepareUtentiSoggettiList(ricerca, lista, user);
	
			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, UtentiCostanti.OBJECT_NAME_UTENTI_SOGGETTI, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					UtentiCostanti.OBJECT_NAME_UTENTI_SOGGETTI, ForwardParams.DEL());
		} 
	}

}
