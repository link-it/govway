/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteDelegateTrasformazioniList
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateTrasformazioniList extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
 

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String idPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			String nomePorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String cambiaPosizione = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_POSIZIONE);
			String idTrasformazioneS = porteDelegateHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
	
			String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			
			if(StringUtils.isNotEmpty(cambiaPosizione)) {
				long idTrasformazione = Long.parseLong(idTrasformazioneS);
				PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(Long.parseLong(idPorta));
				
				Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
				if(trasformazioni != null) {
					TrasformazioneRegola regolaToMove = null;
					for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
						if(reg.getId().longValue() == idTrasformazione) {
							regolaToMove = reg;
							break;
						}
					}
					
					int posizioneAttuale = regolaToMove.getPosizione();
					int posizioneNuova = cambiaPosizione.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU) ? (posizioneAttuale - 1) : (posizioneAttuale + 1);
					
					TrasformazioneRegola regolaToSwitch = null;
					for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
						if(reg.getPosizione() == posizioneNuova) {
							regolaToSwitch = reg;
							break;
						}
					}
					
					regolaToMove.setPosizione(posizioneNuova);
					regolaToSwitch.setPosizione(posizioneAttuale);
					
					porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
					if(CostantiControlStation.VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_REGOLA_TRASFORMAZIONE)
						pd.setMessage(CostantiControlStation.MESSAGGIO_CONFERMA_REGOLA_TRASFORMAZIONE_SPOSTATA_CORRETTAMENTE, MessageType.INFO);
				}
			}
	
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);
			
			int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI; 
			
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			
			List<TrasformazioneRegola> lista = porteDelegateCore.porteDelegateTrasformazioniList(Long.parseLong(idPorta), ricerca);
			
			porteDelegateHelper.preparePorteDelegateTrasformazioniRegolaList(nomePorta, ricerca, lista);
	
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI, ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI, ForwardParams.LIST());
		}  
	}
			
}
