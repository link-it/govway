/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
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
			HashMap<String, ArrayList<String>> whereIsInUso = new HashMap<String, ArrayList<String>>();
	
			SoggettiHelper soggettiHelper = new SoggettiHelper(request, pd, session);
	
			soggettiHelper.makeMenu();
	
			String userLogin = ServletUtils.getUserLoginFromSession(session);
	
			SoggettiCore soggettiCore = new SoggettiCore();

			String msg = "";

			for (int i = 0; i < idsToRemove.size(); i++) {

				Soggetto soggettoRegistro = null;
				org.openspcoop2.core.config.Soggetto soggettoConfig = null;
				String tipoNome = null;
				if(soggettiCore.isRegistroServiziLocale()){
					soggettoRegistro = soggettiCore.getSoggettoRegistro(Long.parseLong(idsToRemove.get(i)));
					soggettoConfig = soggettiCore.getSoggetto(new IDSoggetto(soggettoRegistro.getTipo(), soggettoRegistro.getNome()));
				}
				else{
					soggettoConfig = soggettiCore.getSoggetto(Long.parseLong(idsToRemove.get(i)));
				}
				boolean soggettoInUso = false;
				if(soggettiCore.isRegistroServiziLocale()){
					soggettoInUso = soggettiCore.isSoggettoInUso(soggettoRegistro, whereIsInUso);
					tipoNome = soggettoRegistro.getTipo() + "/" + soggettoRegistro.getNome();
				}else{
					soggettoInUso = soggettiCore.isSoggettoInUso(soggettoConfig, whereIsInUso);
					tipoNome = soggettoConfig.getTipo() + "/" + soggettoConfig.getNome();
				}


				if (soggettoInUso) {
					Set<String> keys = whereIsInUso.keySet();
					isInUso = true;
					msg += tipoNome + " non rimosso perch&egrave; :<br>";
					for (String key : keys) {
						ArrayList<String> messages = whereIsInUso.get(key);

						if (key.equals("Fruitore") && (messages.size() > 0)) {
							msg += "- fruitore di Servizi: " + messages.toString() + "<BR>";
						}
						if (key.equals("Erogatore") && (messages.size() > 0)) {
							msg += "- erogatore di Servizi: " + messages.toString() + "<BR>";
						}
						if (key.equals("Porte Delegate") && (messages.size() > 0)) {
							msg += "- in uso in Porte Delegate: " + messages.toString() + "<BR>";
						}
						if (key.equals("Porte Applicative") && (messages.size() > 0)) {
							msg += "- in uso in Porte Applicative: " + messages.toString() + "<BR>";
						}
						if (key.equals("Servizi Applicativi") && (messages.size() > 0)) {
							msg += "- in uso in Servizi Applicativi: " + messages.toString() + "<BR>";
						}
						if (key.equals("Accordi") && (messages.size() > 0)) {
							msg += "- referente in Accordi: " + messages.toString() + "<BR>";
						}
						if (key.equals("Accordi Cooperazione") && (messages.size() > 0)) {
							msg += "- referente in Accordi Cooperazione: " + messages.toString() + "<BR>";
						}
						if (key.equals("Soggetti Partecipanti") && (messages.size() > 0)) {
							msg += "- partecipante in Accordi Cooperazione: " + messages.toString() + "<BR>";
						}

					}// chiudo for

					msg += "<br>";

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
