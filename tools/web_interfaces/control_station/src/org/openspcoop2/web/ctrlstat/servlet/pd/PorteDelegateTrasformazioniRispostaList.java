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
package org.openspcoop2.web.ctrlstat.servlet.pd;

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
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteDelegateTrasformazioniRispostaList
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateTrasformazioniRispostaList extends Action {

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
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			String idPorta = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
	
			String id = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(id);
			
			String cambiaPosizione = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_POSIZIONE);
			String idTrasformazioneRispostaS = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE_RISPOSTA);
			
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			if(StringUtils.isNotEmpty(cambiaPosizione)) {
				long idTrasformazioneRisposta = Long.parseLong(idTrasformazioneRispostaS);
				PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(Long.parseLong(idPorta));
				
				Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
				if(trasformazioni != null) {
					TrasformazioneRegola oldRegola = null;
					for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
						if(reg.getId().longValue() == idTrasformazione) {
							oldRegola = reg;
							break;
						}
					}
					if(oldRegola==null) {
						throw new Exception("TrasformazioneRegola con id '"+idTrasformazione+"' non trovata");
					}
					
					TrasformazioneRegolaRisposta rispostaToMove = null;
					
					for (int j = 0; j < oldRegola.sizeRispostaList(); j++) {
						TrasformazioneRegolaRisposta risposta = oldRegola.getRisposta(j);
						if (risposta.getId().longValue() == idTrasformazioneRisposta) {
							rispostaToMove = risposta;
							break;
						}
					}
					if(rispostaToMove==null) {
						throw new Exception("TrasformazioneRegolaRisposta con id '"+idTrasformazioneRisposta+"' non trovata");
					}
					
					int posizioneAttuale = rispostaToMove.getPosizione();
					int posizioneNuova = cambiaPosizione.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU) ? (posizioneAttuale - 1) : (posizioneAttuale + 1);
					
					TrasformazioneRegolaRisposta rispostaToSwitch = null;
					for (int j = 0; j < oldRegola.sizeRispostaList(); j++) {
						TrasformazioneRegolaRisposta risposta = oldRegola.getRisposta(j);
						if (risposta.getPosizione() == posizioneNuova) {
							rispostaToSwitch = risposta;
							break;
						}
					}
					if(rispostaToSwitch==null) {
						throw new Exception("TrasformazioneRegolaRisposta con id '"+posizioneNuova+"' non trovata");
					}
					
					rispostaToMove.setPosizione(posizioneNuova);
					rispostaToSwitch.setPosizione(posizioneAttuale);
					
					porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
					if(CostantiControlStation.VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_RISPOSTA_REGOLA_TRASFORMAZIONE)
						pd.setMessage(CostantiControlStation.MESSAGGIO_CONFERMA_REGOLA_TRASFORMAZIONE_RISPOSTA_SPOSTATA_CORRETTAMENTE, MessageType.INFO);
					
					// ricaricare id trasformazione
					portaDelegata = porteDelegateCore.getPortaDelegata(Long.parseLong(idPorta));

					TrasformazioneRegola trasformazioneAggiornata = porteDelegateCore.getTrasformazione(portaDelegata.getId(), oldRegola.getNome());
					
					idTrasformazione = trasformazioneAggiornata.getId();
				}
			}
	
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE; 
			
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			List<TrasformazioneRegolaRisposta> lista = porteDelegateCore.porteDelegateTrasformazioniRispostaList(Long.parseLong(idPorta), idTrasformazione, ricerca);
			
			porteDelegateHelper.preparePorteDelegateTrasformazioniRispostaList(idTrasformazione, ricerca, lista); 
	
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA, ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA, ForwardParams.LIST());
		}  
	}
			
}
