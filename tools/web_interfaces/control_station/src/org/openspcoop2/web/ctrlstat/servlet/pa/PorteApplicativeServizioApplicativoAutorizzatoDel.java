/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * porteAppServizioApplicativoDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeServizioApplicativoAutorizzatoDel extends Action {

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
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			String idPorta = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			
			String tokenList = porteApplicativeHelper.getParametroBoolean(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TOKEN_AUTHORIZATION);
			boolean isToken = tokenList!=null && !"".equals(tokenList) && Boolean.valueOf(tokenList);
			
			String objToRemove = porteApplicativeHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			String sa = "";

			// Prendo la porta applicativa
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(pa.getTipoSoggettoProprietario());
			boolean modi = porteApplicativeCore.isProfiloModIPA(protocollo);
			
			for (int i = 0; i < idsToRemove.size(); i++) {

				sa = idsToRemove.get(i);
				if(isToken && !modi) {	
					if(pa.getAutorizzazioneToken()!=null && pa.getAutorizzazioneToken().getServiziApplicativi()!=null) {
						for (int j = 0; j < pa.getAutorizzazioneToken().getServiziApplicativi().sizeServizioApplicativoList(); j++) {
							PortaApplicativaAutorizzazioneServizioApplicativo saAutorizzato = pa.getAutorizzazioneToken().getServiziApplicativi().getServizioApplicativo(j);
							String idSaAutorizzato = saAutorizzato.getNome()+"@"+saAutorizzato.getTipoSoggettoProprietario() + "/" + saAutorizzato.getNomeSoggettoProprietario();
							if (sa.equals(idSaAutorizzato)) {
								pa.getAutorizzazioneToken().getServiziApplicativi().removeServizioApplicativo(j);
								break;
							}
						}
					}
				}
				else {
					for (int j = 0; j < pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList(); j++) {
						PortaApplicativaAutorizzazioneServizioApplicativo saAutorizzato = pa.getServiziApplicativiAutorizzati().getServizioApplicativo(j);
						String idSaAutorizzato = saAutorizzato.getNome()+"@"+saAutorizzato.getTipoSoggettoProprietario() + "/" + saAutorizzato.getNomeSoggettoProprietario();
						if (sa.equals(idSaAutorizzato)) {
							pa.getServiziApplicativiAutorizzati().removeServizioApplicativo(j);
							break;
						}
					}
				}
			}

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO;
			if(isToken && !modi) {
				idLista = Liste.PORTE_APPLICATIVE_TOKEN_SERVIZIO_APPLICATIVO;
			}

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			
			List<PortaApplicativaAutorizzazioneServizioApplicativo> lista = (isToken && !modi) ? 
					porteApplicativeCore.porteAppServiziApplicativiAutorizzatiTokenList(Integer.parseInt(idPorta), ricerca)
					:
					porteApplicativeCore.porteAppServiziApplicativiAutorizzatiList(Integer.parseInt(idPorta), ricerca);

			porteApplicativeHelper.preparePorteAppServizioApplicativoAutorizzatoList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO, 
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_SERVIZIO_APPLICATIVO_AUTORIZZATO,
					ForwardParams.DEL());
		}  
	}
}
