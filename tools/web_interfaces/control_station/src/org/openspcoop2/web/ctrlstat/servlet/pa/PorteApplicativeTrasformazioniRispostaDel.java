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
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.Trasformazioni;
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
 * PorteApplicativeTrasformazioniRispostaDel
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeTrasformazioniRispostaDel extends Action {

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
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			// String idsogg = porteApplicativeHelper.getParameter("idsogg");
			// int soggInt = Integer.parseInt(idsogg);
			String nomePorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(id);

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			String objToRemove = porteApplicativeHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			Long idRisp = null;

			// Prendo l'accesso registro
			PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));
			Trasformazioni trasformazioni = portaApplicativa.getTrasformazioni();
			TrasformazioneRegola regola = null;
			for (int i = 0; i < idsToRemove.size(); i++) {

				idRisp = Long.parseLong(idsToRemove.get(i));

				
				for (int j = 0; j < trasformazioni.sizeRegolaList(); j++) {
					TrasformazioneRegola regolaTmp = trasformazioni.getRegola(j);
					if (regolaTmp.getId().longValue() == idTrasformazione) {
						regola = regolaTmp;
						break;
					}
				}
				
				for (int j = 0; j < regola.sizeRispostaList(); j++) {
					TrasformazioneRegolaRisposta risposta = regola.getRisposta(j);
					if (risposta.getId().longValue() == idRisp.longValue()) {
						regola.removeRisposta(j);
						break;
					}
				}
			}

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), portaApplicativa);
			
			// ricaricare id trasformazione
			portaApplicativa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));

			TrasformazioneRegola trasformazioneAggiornata = porteApplicativeCore.getTrasformazione(Long.parseLong(idPorta), regola.getNome());

			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE; 
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<TrasformazioneRegolaRisposta> lista = porteApplicativeCore.porteAppTrasformazioniRispostaList(Long.parseLong(idPorta), trasformazioneAggiornata.getId(), ricerca);
			
			porteApplicativeHelper.preparePorteAppTrasformazioniRispostaList(nomePorta, trasformazioneAggiornata.getId(), ricerca, lista); 
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA, ForwardParams.DEL());
		} 
	}

}
