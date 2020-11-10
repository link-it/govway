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
package org.openspcoop2.web.ctrlstat.servlet.pa;

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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiPatternRegola;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRichiesta;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRisposta;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteApplicativeValidazioneContenutiPatternDel
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeValidazioneContenutiPatternDel extends Action {

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

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			// String idsogg = porteApplicativeHelper.getParameter("idsogg");
			// int soggInt = Integer.parseInt(idsogg);
			
			String idParent = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_ID_RISPOSTA);
			String tipoParent = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT);
			
			if(tipoParent == null) {
				tipoParent = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_PORTA;
			}

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			String objToRemove = porteApplicativeHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			Long idRegola = null;

			// Prendo l'accesso registro
			PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));
			
			String parentName = null;
			for (int i = 0; i < idsToRemove.size(); i++) {

				idRegola = Long.parseLong(idsToRemove.get(i));
				
				if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
					for (int j = 0; j < portaApplicativa.getValidazioneContenutiApplicativi().sizeRichiestaList(); j++) {
						ValidazioneContenutiApplicativiRichiesta richiesta = portaApplicativa.getValidazioneContenutiApplicativi().getRichiesta(j);
						if (richiesta.getId().longValue() == Long.parseLong(idParent)) {
							parentName = richiesta.getNome();
							for (int z = 0; z < richiesta.getConfigurazione().getConfigurazionePattern().sizePatternList(); z++) {
								ValidazioneContenutiApplicativiPatternRegola pattern = richiesta.getConfigurazione().getConfigurazionePattern().getPattern(z);
								if (pattern.getId().longValue() == idRegola.longValue()) {
									richiesta.getConfigurazione().getConfigurazionePattern().removePattern(z);
									break;
								}
							}
							break;
						}
					}
				} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
					for (int j = 0; j < portaApplicativa.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
						ValidazioneContenutiApplicativiRisposta risposta = portaApplicativa.getValidazioneContenutiApplicativi().getRisposta(j);
						if (risposta.getId().longValue() == Long.parseLong(idParent)) {
							parentName = risposta.getNome();
							for (int z = 0; z < risposta.getConfigurazione().getConfigurazionePattern().sizePatternList(); z++) {
								ValidazioneContenutiApplicativiPatternRegola pattern = risposta.getConfigurazione().getConfigurazionePattern().getPattern(z);
								if (pattern.getId().longValue() == idRegola.longValue()) {
									risposta.getConfigurazione().getConfigurazionePattern().removePattern(z);
									break;
								}
							}
							break;
						}
					}
				} else {
					for (int j = 0; j < portaApplicativa.getValidazioneContenutiApplicativi().getConfigurazione().getConfigurazionePattern().sizePatternList(); j++) {
						ValidazioneContenutiApplicativiPatternRegola regola = portaApplicativa.getValidazioneContenutiApplicativi().getConfigurazione().getConfigurazionePattern().getPattern(j);
						if (regola.getId().longValue() == idRegola.longValue()) {
							portaApplicativa.getValidazioneContenutiApplicativi().getConfigurazione().getConfigurazionePattern().removePattern(j);
							break;
						}
					}
				}
			}

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), portaApplicativa);
			
			portaApplicativa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));
			
			// ricarico parent
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				for (int j = 0; j < portaApplicativa.getValidazioneContenutiApplicativi().sizeRichiestaList(); j++) {
					ValidazioneContenutiApplicativiRichiesta richiesta = portaApplicativa.getValidazioneContenutiApplicativi().getRichiesta(j);
					if (richiesta.getNome().equals(parentName)) {
						idParent = richiesta.getId()+"";
						break;
					}
				}
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				for (int j = 0; j < portaApplicativa.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
					ValidazioneContenutiApplicativiRisposta risposta = portaApplicativa.getValidazioneContenutiApplicativi().getRisposta(j);
					if (risposta.getNome().equals(parentName)) {
						idParent = risposta.getId()+"";
						break;
					}
				}
			}
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_PATTERN; 
			
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				idLista = Liste.PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RICHIESTA_PATTERN;
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				idLista = Liste.PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA_PATTERN;
			}
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			
			List<ValidazioneContenutiApplicativiPatternRegola> lista = null;
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				lista = porteApplicativeCore.listaPatternValidazioneContenutiRichiesta(Long.parseLong(idParent), ricerca);
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				lista = porteApplicativeCore.listaPatternValidazioneContenutiRisposta(Long.parseLong(idParent), ricerca);
			} else {
				lista = porteApplicativeCore.listaPatternValidazioneContenuti(Long.parseLong(idPorta), ricerca);
			}
			
			porteApplicativeHelper.preparePorteAppValidazioneContenutiPatternList(ricerca, lista, idParent, tipoParent); 
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_PATTERN,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_PATTERN, ForwardParams.DEL());
		} 
	}

}
