/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;

/**
 * PorteApplicativeTrasformazioniRispostaList
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeTrasformazioniRispostaList extends Action {

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
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String nomePorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
	
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(id);
			
			String cambiaPosizione = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_POSIZIONE);
			String idTrasformazioneRispostaS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RISPOSTA);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			if(StringUtils.isNotEmpty(cambiaPosizione)) {
				long idTrasformazioneRisposta = Long.parseLong(idTrasformazioneRispostaS);
				PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));
				
				Trasformazioni trasformazioni = portaApplicativa.getTrasformazioni();
				if(trasformazioni != null) {
					TrasformazioneRegola oldRegola = null;
					for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
						if(reg.getId().longValue() == idTrasformazione) {
							oldRegola = reg;
							break;
						}
					}
					
					TrasformazioneRegolaRisposta rispostaToMove = null;
					
					for (int j = 0; j < oldRegola.sizeRispostaList(); j++) {
						TrasformazioneRegolaRisposta risposta = oldRegola.getRisposta(j);
						if (risposta.getId().longValue() == idTrasformazioneRisposta) {
							rispostaToMove = risposta;
							break;
						}
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
					
					rispostaToMove.setPosizione(posizioneNuova);
					rispostaToSwitch.setPosizione(posizioneAttuale);
					
					porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), portaApplicativa);
					if(CostantiControlStation.VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_RISPOSTA_REGOLA_TRASFORMAZIONE)
						pd.setMessage( ServletUtils.getMessageFromResourceBundle( session, "ControlStation.ConfermaRegolaTrasformazioneRispostaSpostataCorrettamente" ) , MessageType.INFO);	//CostantiControlStation.MESSAGGIO_CONFERMA_REGOLA_TRASFORMAZIONE_RISPOSTA_SPOSTATA_CORRETTAMENTE
					
					// ricaricare id trasformazione
					portaApplicativa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));
					
					TrasformazioneRegola trasformazioneAggiornata = porteApplicativeCore.getTrasformazione(portaApplicativa.getId(), oldRegola.getNome());
					
					idTrasformazione = trasformazioneAggiornata.getId();
				}
			}
	
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE; 
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			
			
			List<TrasformazioneRegolaRisposta> lista = porteApplicativeCore.porteAppTrasformazioniRispostaList(Long.parseLong(idPorta), idTrasformazione, ricerca);
			
			porteApplicativeHelper.preparePorteAppTrasformazioniRispostaList(nomePorta, idTrasformazione, ricerca, lista); 
	
			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA, ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA, ForwardParams.LIST());
		}  
	}
			
}
