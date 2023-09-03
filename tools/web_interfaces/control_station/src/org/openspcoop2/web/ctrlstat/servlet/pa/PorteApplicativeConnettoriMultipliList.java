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

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteApplicativeConnettoriMultipliList
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeConnettoriMultipliList extends Action {

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
			
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			
			String idPorta = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String nomeConnettoreChangeListBreadcump = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_FROM_BREADCUMP_CHANGE_NOME_CONNETTORE);
	
			String idTab = porteApplicativeHelper.getParametroInteger(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(Integer.parseInt(idPorta));
	
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
	
			int idLista = Liste.PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
	
			// poiche' esistono filtri che hanno necessita di postback salvo in sessione
			List<PortaApplicativaServizioApplicativo> listaFiltrata = null;
			if(!ServletUtils.isSearchDone(porteApplicativeHelper)) {
				listaFiltrata = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista,  PortaApplicativaServizioApplicativo.class);
			}
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
	
			if(listaFiltrata==null) {
				IDSoggetto idSoggettoProprietario = new IDSoggetto(portaApplicativa.getTipoSoggettoProprietario(), portaApplicativa.getNomeSoggettoProprietario());
				listaFiltrata = porteApplicativeHelper.applicaFiltriRicercaConnettoriMultipli(ricerca, idLista, portaApplicativa.getServizioApplicativoList(), idSoggettoProprietario);
			}
			
			if(!porteApplicativeHelper.isPostBackFilterElement()) {
				ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, listaFiltrata); // salvo poiche' esistono filtri che hanno necessita di postback
			}
			
			if(nomeConnettoreChangeListBreadcump!=null && StringUtils.isNotEmpty(nomeConnettoreChangeListBreadcump)) {
				porteApplicativeHelper.preparePorteAppConnettoriMultipliList_fromChangeConnettore(ricerca, listaFiltrata, portaApplicativa, nomeConnettoreChangeListBreadcump);
			}
			else {
				porteApplicativeHelper.preparePorteAppConnettoriMultipliList(ricerca, listaFiltrata, portaApplicativa);
			}
	
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI,	ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI,	ForwardParams.LIST());
		} 
	}
}
