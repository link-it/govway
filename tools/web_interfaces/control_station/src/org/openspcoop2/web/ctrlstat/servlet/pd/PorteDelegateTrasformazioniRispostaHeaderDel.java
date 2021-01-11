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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.ArrayList;
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
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * PorteDelegateTrasformazioniRispostaHeaderDel
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateTrasformazioniRispostaHeaderDel extends Action {

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
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			
			String idPorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			// String idsogg = porteDelegateHelper.getParameter("idsogg");
			// int soggInt = Integer.parseInt(idsogg);
			String nomePorta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String idTrasformazioneS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);
			
			String idTrasformazioneRispostaS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE_RISPOSTA);
			long idTrasformazioneRisposta = Long.parseLong(idTrasformazioneRispostaS);

			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			String objToRemove = porteDelegateHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);

			Long idParametro = null;

			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(Long.parseLong(idPorta));
			Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
			TrasformazioneRegola regola = null;
			TrasformazioneRegolaRisposta risposta = null;
			for (int i = 0; i < idsToRemove.size(); i++) {

				idParametro = Long.parseLong(idsToRemove.get(i));

				
				for (int j = 0; j < trasformazioni.sizeRegolaList(); j++) {
					TrasformazioneRegola regolaTmp = trasformazioni.getRegola(j);
					if (regolaTmp.getId().longValue() == idTrasformazione) {
						regola = regolaTmp;
						break;
					}
				}
				
				for (int j = 0; j < regola.sizeRispostaList(); j++) {
					TrasformazioneRegolaRisposta rispostaTmp = regola.getRisposta(j);
					if (rispostaTmp.getId().longValue() == idTrasformazioneRisposta) {
						risposta = rispostaTmp;
						break;
					}
				}
				
				for (int j = 0; j < risposta.sizeHeaderList(); j++) {
					TrasformazioneRegolaParametro paramteroTmp = risposta.getHeader(j);
					if (paramteroTmp.getId().longValue() == idParametro) {
						risposta.removeHeader(j);
						break;
					}
				}
			}

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
			
			// ricaricare id trasformazione
			portaDelegata = porteDelegateCore.getPortaDelegata(Long.parseLong(idPorta));

			TrasformazioneRegola trasformazioneAggiornata = porteDelegateCore.getTrasformazione(Long.parseLong(idPorta), regola.getNome());

			// ricarico la risposta
			String patternRispostaDBCheck = (risposta.getApplicabilita() != null && StringUtils.isNotEmpty(risposta.getApplicabilita().getPattern())) ? risposta.getApplicabilita().getPattern() : null;
			String contentTypeRispostaAsString = (risposta.getApplicabilita() != null &&risposta.getApplicabilita().getContentTypeList() != null) ? StringUtils.join(risposta.getApplicabilita().getContentTypeList(), ",") : "";
			String contentTypeRispostaDBCheck = StringUtils.isNotEmpty(contentTypeRispostaAsString) ? contentTypeRispostaAsString : null;
			Integer statusMinRisposta= (risposta.getApplicabilita() != null && risposta.getApplicabilita().getReturnCodeMin() != null) ? risposta.getApplicabilita().getReturnCodeMin() : null;
			Integer statusMaxRisposta= (risposta.getApplicabilita() != null && risposta.getApplicabilita().getReturnCodeMax() != null) ? risposta.getApplicabilita().getReturnCodeMax() : null;
			TrasformazioneRegolaRisposta rispostaAggiornata = porteDelegateCore.getTrasformazioneRisposta(Long.parseLong(idPorta), trasformazioneAggiornata.getId(), statusMinRisposta, statusMaxRisposta, patternRispostaDBCheck, contentTypeRispostaDBCheck);
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE_HEADER; 
			
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);
			
			List<TrasformazioneRegolaParametro> lista = porteDelegateCore.porteDelegateTrasformazioniRispostaHeaderList(Long.parseLong(idPorta), trasformazioneAggiornata.getId(), rispostaAggiornata.getId(), ricerca);
			
			porteDelegateHelper.preparePorteDelegateTrasformazioniRispostaHeaderList(nomePorta, trasformazioneAggiornata.getId(), rispostaAggiornata.getId(), ricerca, lista); 
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForward (mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_HEADER,
					ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_HEADER, ForwardParams.DEL());
		} 
	}

}
