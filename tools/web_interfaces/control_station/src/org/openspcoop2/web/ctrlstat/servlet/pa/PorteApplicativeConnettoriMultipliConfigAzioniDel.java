/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteApplicativeConnettoriMultipliConfigAzioniDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeConnettoriMultipliConfigAzioniDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

	 

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String objToRemove = porteApplicativeHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			
			// Prendo la porta applicativa
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			org.openspcoop2.pdd.core.behaviour.conditional.ConfigurazioneCondizionale configurazioneCondizionale =
					org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.read(pa, ControlStationCore.getLog());
			Set<String> nomiRegole = configurazioneCondizionale.getRegoleOrdinate();

			for (int i = 0; i < idsToRemove.size(); i++) {
				for (String nomeRegola : nomiRegole) {
					if(nomeRegola.equals(idsToRemove.get(i))){
						configurazioneCondizionale.removeRegola(nomeRegola);
						break;
					}
				}
			}
			
			org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.save(pa, configurazioneCondizionale);

			org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.save(pa, configurazioneCondizionale);
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			// ricarico la configurazione
			pa = porteApplicativeCore.getPortaApplicativa(idInt); 

			configurazioneCondizionale = org.openspcoop2.pdd.core.behaviour.conditional.ConditionalUtils.read(pa, ControlStationCore.getLog());
			
			Set<String> lista = null;
			if(configurazioneCondizionale != null) {
				lista = configurazioneCondizionale.getRegoleOrdinate();
			}

			// Preparo la lista
			ConsoleSearch ricerca = null;
						
			porteApplicativeHelper.preparePorteApplicativeConnettoriMultipliConfigAzioniList(pa, configurazioneCondizionale, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CONFIG_AZIONI,
					ForwardParams.DEL());
		}  
	}
}
