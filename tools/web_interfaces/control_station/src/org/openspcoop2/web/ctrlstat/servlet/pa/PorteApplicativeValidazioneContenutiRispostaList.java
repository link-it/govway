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
import org.openspcoop2.core.config.ValidazioneContenutiApplicativiRisposta;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteApplicativeValidazioneContenutiRispostaList
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeValidazioneContenutiRispostaList extends Action {

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
			
			String idRispostaS = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_ID_RISPOSTA); 
			
			
			String cambiaPosizione = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_VALIDAZIONE_CONTENUTI_RISPOSTA_POSIZIONE);
	
			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			
			if(StringUtils.isNotEmpty(cambiaPosizione)) {
				long idRisposta = Long.parseLong(idRispostaS);
				PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));
				
				ValidazioneContenutiApplicativiRisposta rispostaToMove = null;
				
					for (int j = 0; j < portaApplicativa.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
						ValidazioneContenutiApplicativiRisposta risposta = portaApplicativa.getValidazioneContenutiApplicativi().getRisposta(j);
						if (risposta.getId().longValue() == idRisposta) {
							rispostaToMove = risposta;
							break;
						}
					}
					
					int posizioneAttuale = rispostaToMove.getPosizione();
					int posizioneNuova = cambiaPosizione.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_POSIZIONE_SU) ? (posizioneAttuale - 1) : (posizioneAttuale + 1);
					
					ValidazioneContenutiApplicativiRisposta rispostaToSwitch = null;
					for (int j = 0; j < portaApplicativa.getValidazioneContenutiApplicativi().sizeRispostaList(); j++) {
						ValidazioneContenutiApplicativiRisposta risposta = portaApplicativa.getValidazioneContenutiApplicativi().getRisposta(j);
						if (risposta.getPosizione() == posizioneNuova) {
							rispostaToSwitch = risposta;
							break;
						}
					}
					
					rispostaToMove.setPosizione(posizioneNuova);
					rispostaToSwitch.setPosizione(posizioneAttuale);
					
					porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), portaApplicativa);
					if(CostantiControlStation.VISUALIZZA_MESSAGGIO_CONFERMA_SPOSTAMENTO_VALIDAZIONE_CONTENUTI_RISPOSTA)
						pd.setMessage(CostantiControlStation.MESSAGGIO_CONFERMA_VALIDAZIONE_CONTENUTI_RISPOSTA_SPOSTATA_CORRETTAMENTE, MessageType.INFO);
					
			}
	
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA; 
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			
			List<ValidazioneContenutiApplicativiRisposta> lista = porteApplicativeCore.listaRisposteValidazioneContenuti(Long.parseLong(idPorta), ricerca);
			
			porteApplicativeHelper.preparePorteAppValidazioneContenutiRispostaList(ricerca, lista);  
	
			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA, ForwardParams.LIST());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VALIDAZIONE_CONTENUTI_RISPOSTA, ForwardParams.LIST());
		}  
	}
			
}
