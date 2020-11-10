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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiPatternRegola;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteDelegateValidazioneContenutiPatternList
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateValidazioneContenutiPatternList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
 

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String idPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			
			String idParent = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_ID_RISPOSTA);
			String tipoParent = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT);
			
			if(tipoParent == null) {
				tipoParent = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_PORTA;
			}
			
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
	
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN; 
			
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				idLista = Liste.PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RICHIESTA_PATTERN;
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				idLista = Liste.PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_RISPOSTA_PATTERN;
			}
			
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);

			List<ValidazioneContenutiApplicativiPatternRegola> lista = null;
			if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RICHIESTA)) {
				lista = porteDelegateCore.listaPatternValidazioneContenutiRichiesta(Long.parseLong(idParent), ricerca);
			} else if(tipoParent.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_PATTERN_PARENT_RISPOSTA)) {
				lista = porteDelegateCore.listaPatternValidazioneContenutiRisposta(Long.parseLong(idParent), ricerca);
			} else {
				lista = porteDelegateCore.listaPatternValidazioneContenuti(Long.parseLong(idPorta), ricerca);
			}
			
			porteDelegateHelper.preparePorteDelegateValidazioneContenutiPatternList(ricerca, lista, idParent, tipoParent);
	
			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN, ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_VALIDAZIONE_CONTENUTI_PATTERN, ForwardParams.LIST());
		}  
	}
			
}
