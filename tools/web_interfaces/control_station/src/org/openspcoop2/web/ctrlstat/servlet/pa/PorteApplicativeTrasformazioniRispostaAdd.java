/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

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
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteApplicativeTrasformazioniRispostaAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeTrasformazioniRispostaAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	
		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idTrasformazioneS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);
			
			String nomeRisposta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_NOME);
			
			String returnCode = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS);
			if(returnCode == null)
				returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_QUALSIASI;
			
			String statusMin = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MIN);
			if(statusMin == null)
				statusMin = "";
			String statusMax = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MAX);
			if(statusMax == null)
				statusMax = "";
			
			String pattern = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_PATTERN);
			String contentType = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_APPLICABILITA_CT);
			
			

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pa.getNome();
			
			Trasformazioni trasformazioni = pa.getTrasformazioni();
			TrasformazioneRegola regola = null;
			for (int j = 0; j < trasformazioni.sizeRegolaList(); j++) {
				TrasformazioneRegola regolaTmp = trasformazioni.getRegola(j);
				if (regolaTmp.getId().longValue() == idTrasformazione) {
					regola = regolaTmp;
					break;
				}
			}
			
			String nomeTrasformazione = regola.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null) {
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS)) {
					statusMin = "";
					statusMax = "";
				}
			}

			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps), pIdTrasformazione));
			
			String labelPag = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRisposta = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdTrasformazione);
			
			lstParam.add(new Parameter(labelPag,PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_LIST,parametriInvocazioneServletTrasformazioniRisposta));
			
			lstParam.add(ServletUtils.getParameterAggiungi());

			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addTrasformazioneRispostaToDatiOpAdd(dati, idTrasformazioneS, nomeRisposta, returnCode, statusMin, statusMax, pattern, contentType);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA,	ForwardParams.ADD());
			}
			
			
			boolean isOk = porteApplicativeHelper.trasformazioniRispostaCheckData(TipoOperazione.ADD, regola, null);
			
			if(isOk) {
				// quando un parametro viene inviato come vuoto, sul db viene messo null, gestisco il caso
				Integer statusMinDBCheck = StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null;
				Integer statusMaxDBCheck = StringUtils.isNotEmpty(statusMax) ? Integer.parseInt(statusMax) : null;
				if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_ESATTO))
					statusMaxDBCheck = statusMinDBCheck;
				String patternDBCheck = StringUtils.isNotEmpty(pattern) ? pattern : null;
				String contentTypeDBCheck = StringUtils.isNotEmpty(contentType) ? contentType : null;
				boolean giaRegistrato = porteApplicativeCore.existsTrasformazioneRisposta(Long.parseLong(idPorta), idTrasformazione, statusMinDBCheck, statusMaxDBCheck, patternDBCheck, contentTypeDBCheck);
				if (giaRegistrato) {
					pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_DUPLICATA);
					isOk = false;
				}
				if (isOk) {
					giaRegistrato = porteApplicativeCore.existsTrasformazioneRisposta(Long.parseLong(idPorta), idTrasformazione, nomeRisposta);
					if (giaRegistrato) {
						pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_NOME);
						isOk = false;
					}
				}
			}
			
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addTrasformazioneRispostaToDatiOpAdd(dati, idTrasformazioneS, nomeRisposta, returnCode, statusMin, statusMax, pattern, contentType);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA,	ForwardParams.ADD());
			}
			
			// salvataggio nuova regola
			for (int j = 0; j < trasformazioni.sizeRegolaList(); j++) {
				TrasformazioneRegola regolaTmp = trasformazioni.getRegola(j);
				if (regolaTmp.getId().longValue() == idTrasformazione) {
					regola = regolaTmp;
					break;
				}
			}
			
			TrasformazioneRegolaRisposta risposta = new TrasformazioneRegolaRisposta();

			// calcolo prossima posizione
			int posizione = 1;
			for (TrasformazioneRegolaRisposta check : regola.getRispostaList()) {
				if(check.getPosizione()>=posizione) {
					posizione = check.getPosizione()+1;
				}
			}
			
			risposta.setNome(nomeRisposta);
			risposta.setPosizione(posizione);
			
			TrasformazioneRegolaApplicabilitaRisposta applicabilita = new TrasformazioneRegolaApplicabilitaRisposta();
			
			if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_QUALSIASI)) {
				applicabilita.setReturnCodeMin(null);
				applicabilita.setReturnCodeMax(null);
			} else if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_ESATTO)) {
				applicabilita.setReturnCodeMin(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
				applicabilita.setReturnCodeMax(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
			} else { // intervallo
				applicabilita.setReturnCodeMin(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
				applicabilita.setReturnCodeMax(StringUtils.isNotEmpty(statusMax) ? Integer.parseInt(statusMax) : null);
			}
			
			applicabilita.setPattern(pattern);
			if(contentType != null) {
				applicabilita.getContentTypeList().addAll(Arrays.asList(contentType.split(",")));
			}
			
			
			risposta.setApplicabilita(applicabilita);
			regola.addRisposta(risposta );
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			// ricaricare id trasformazione
			pa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));

			String patternDBCheck = (regola.getApplicabilita() != null && StringUtils.isNotEmpty(regola.getApplicabilita().getPattern())) ? regola.getApplicabilita().getPattern() : null;
			String contentTypeAsString = (regola.getApplicabilita() != null &&regola.getApplicabilita().getContentTypeList() != null) ? StringUtils.join(regola.getApplicabilita().getContentTypeList(), ",") : "";
			String contentTypeDBCheck = StringUtils.isNotEmpty(contentTypeAsString) ? contentTypeAsString : null;
			String azioniAsString = (regola.getApplicabilita() != null && regola.getApplicabilita().getAzioneList() != null) ? StringUtils.join(regola.getApplicabilita().getAzioneList(), ",") : "";
			String azioniDBCheck = StringUtils.isNotEmpty(azioniAsString) ? azioniAsString : null;
			TrasformazioneRegola trasformazioneAggiornata = porteApplicativeCore.getTrasformazione(pa.getId(), azioniDBCheck, patternDBCheck, contentTypeDBCheck);
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE; 
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<TrasformazioneRegolaRisposta> lista = porteApplicativeCore.porteAppTrasformazioniRispostaList(Long.parseLong(idPorta), trasformazioneAggiornata.getId(), ricerca);
			
			porteApplicativeHelper.preparePorteAppTrasformazioniRispostaList(nomePorta, trasformazioneAggiornata.getId(), ricerca, lista); 
						
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA, ForwardParams.ADD());


		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA, ForwardParams.ADD());
		} 
	}
}
