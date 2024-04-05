/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaParametro;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.constants.TrasformazioneIdentificazioneRisorsaFallita;
import org.openspcoop2.core.config.constants.TrasformazioneRegolaParametroTipoAzione;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteApplicativeTrasformazioniRispostaHeaderChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeTrasformazioniRispostaHeaderChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		try {
			
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
		
			String idPorta = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idTrasformazioneS = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);
			
			String idTrasformazioneRispostaS = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RISPOSTA);
			long idTrasformazioneRisposta = Long.parseLong(idTrasformazioneRispostaS);
			
			String idTrasformazioneRispostaHeaderS = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RISPOSTA_HEADER);
			long idTrasformazioneRispostaHeader = Long.parseLong(idTrasformazioneRispostaHeaderS);
			
			String tipo = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER_TIPO);
			String nome = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER_NOME);
			String valore = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER_VALORE);
			String identificazione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER_IDENTIFICAZIONE);
			
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			
			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pa.getNome();
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(pa.getTipoSoggettoProprietario());
			
			Trasformazioni trasformazioni = pa.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			TrasformazioneRegolaRisposta oldRisposta = null;
			TrasformazioneRegolaParametro oldParametro = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}
			if(oldRegola==null) {
				throw new Exception("TrasformazioneRegola con id '"+idTrasformazione+"' non trovata");
			}
			
			for (int j = 0; j < oldRegola.sizeRispostaList(); j++) {
				TrasformazioneRegolaRisposta risposta = oldRegola.getRisposta(j);
				if (risposta.getId().longValue() == idTrasformazioneRisposta) {
					oldRisposta = risposta;
					break;
				}
			}
			if(oldRisposta==null) {
				throw new Exception("TrasformazioneRegolaRisposta con id '"+idTrasformazioneRisposta+"' non trovata");
			}
			
			for (int j = 0; j < oldRisposta.sizeHeaderList(); j++) {
				TrasformazioneRegolaParametro parametro = oldRisposta.getHeader(j);
				if (parametro.getId().longValue() == idTrasformazioneRispostaHeader) {
					oldParametro = parametro;
					break;
				}
			}
			if(oldParametro==null) {
				throw new Exception("TrasformazioneRegolaParametro con id '"+idTrasformazioneRispostaHeader+"' non trovata");
			}
			
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			
			String nomeRisposta = oldRisposta.getNome();
			String nomeTrasformazione = oldRegola.getNome();
			String nomeParametro = oldParametro.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			Parameter pIdTrasformazioneRisposta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RISPOSTA, idTrasformazioneRisposta+"");
			
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
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRisposta = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdTrasformazione);
			
			lstParam.add(new Parameter(labelPag,PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_LIST,parametriInvocazioneServletTrasformazioniRisposta));
			
			lstParam.add(new Parameter(nomeRisposta, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_CHANGE, 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps),
					pIdTrasformazione, pIdTrasformazioneRisposta));
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRispostaHeaders = new ArrayList<>();
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(pIdTrasformazione);
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(pIdTrasformazioneRisposta);
			
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADERS,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER_LIST,parametriInvocazioneServletTrasformazioniRispostaHeaders ));
			
			lstParam.add(new Parameter(nomeParametro, null));

			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				// primo accesso
				if(nome == null) {
					nome = oldParametro.getNome();
					tipo = oldParametro.getConversioneTipo().getValue();
					valore = oldParametro.getValore();
					identificazione = oldParametro.getIdentificazioneFallita()!=null ? oldParametro.getIdentificazioneFallita().getValue() : CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_PARAMETRO_IDENTIFICAZIONE_FALLITA;
				}

				dati = porteApplicativeHelper.addTrasformazioneRispostaHeaderToDati(TipoOperazione.CHANGE, protocollo, false, dati, idTrasformazioneS, idTrasformazioneRispostaS, idTrasformazioneRispostaHeaderS, nome, tipo, valore, identificazione, apc.getServiceBinding());
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER,	ForwardParams.CHANGE());
			}
			
			boolean isOk = porteApplicativeHelper.trasformazioniRispostaHeaderCheckData(TipoOperazione.CHANGE);
			if (isOk && (oldParametro!=null && !oldParametro.getNome().equals(nome))) {
				boolean giaRegistrato = porteApplicativeCore.existsTrasformazioneRispostaHeader(Long.parseLong(idPorta), idTrasformazione, idTrasformazioneRisposta, nome, tipo, CostantiControlStation.VALUE_TRASFORMAZIONI_CHECK_UNIQUE_NOME_TIPO_HEADER_URL);

				if (giaRegistrato) {
					pd.setMessage(CostantiControlStation.MESSAGGIO_TRASFORMAZIONI_CHECK_UNIQUE_NOME_TIPO_HEADER);
					isOk = false;
				}
			} 
			if (!isOk) {

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addTrasformazioneRispostaHeaderToDati(TipoOperazione.CHANGE, protocollo, false, dati, idTrasformazioneS, idTrasformazioneRispostaS, idTrasformazioneRispostaHeaderS, nome, tipo, valore, identificazione, apc.getServiceBinding());
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER,	ForwardParams.CHANGE());
			}
			
			// aggiorno la regola
			TrasformazioneRegola regola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					regola = reg;
					break;
				}
			}
			if(regola==null) {
				throw new Exception("TrasformazioneRegola con id '"+idTrasformazione+"' non trovata");
			}
			
			TrasformazioneRegolaRisposta risposta = null;
			for (int j = 0; j < regola.sizeRispostaList(); j++) {
				TrasformazioneRegolaRisposta rispostaTmp = regola.getRisposta(j);
				if (rispostaTmp.getId().longValue() == idTrasformazioneRisposta) {
					risposta = rispostaTmp;
					break;
				}
			}
			if(risposta==null) {
				throw new Exception("TrasformazioneRegolaRisposta con id '"+idTrasformazioneRisposta+"' non trovata");
			}
			
			TrasformazioneRegolaParametro parametroDaAggiornare = null;
			for (int j = 0; j < risposta.sizeHeaderList(); j++) {
				TrasformazioneRegolaParametro parametro = risposta.getHeader(j);
				if (parametro.getId().longValue() == idTrasformazioneRispostaHeader) {
					parametroDaAggiornare = parametro;
					break;
				}
			}
			if(parametroDaAggiornare==null) {
				throw new Exception("TrasformazioneRegolaParametro con id '"+idTrasformazioneRispostaHeader+"' non trovata");
			}
			
			parametroDaAggiornare.setNome(nome);
			parametroDaAggiornare.setValore(valore);
			parametroDaAggiornare.setConversioneTipo(TrasformazioneRegolaParametroTipoAzione.toEnumConstant(tipo));
			if(!TrasformazioneRegolaParametroTipoAzione.DELETE.equals(parametroDaAggiornare.getConversioneTipo())) {
				parametroDaAggiornare.setIdentificazioneFallita(TrasformazioneIdentificazioneRisorsaFallita.toEnumConstant(identificazione));
			}
			else {
				parametroDaAggiornare.setIdentificazioneFallita(null);
			}
			
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			// ricaricare id trasformazione
			pa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));

			TrasformazioneRegola trasformazioneAggiornata = porteApplicativeCore.getTrasformazione(pa.getId(), regola.getNome());
			
			// ricarico la risposta
			String patternRispostaDBCheck = (risposta.getApplicabilita() != null && StringUtils.isNotEmpty(risposta.getApplicabilita().getPattern())) ? risposta.getApplicabilita().getPattern() : null;
			String contentTypeRispostaAsString = (risposta.getApplicabilita() != null &&risposta.getApplicabilita().getContentTypeList() != null) ? StringUtils.join(risposta.getApplicabilita().getContentTypeList(), ",") : "";
			String contentTypeRispostaDBCheck = StringUtils.isNotEmpty(contentTypeRispostaAsString) ? contentTypeRispostaAsString : null;
			Integer statusMinRisposta= (risposta.getApplicabilita() != null && risposta.getApplicabilita().getReturnCodeMin() != null) ? risposta.getApplicabilita().getReturnCodeMin() : null;
			Integer statusMaxRisposta= (risposta.getApplicabilita() != null && risposta.getApplicabilita().getReturnCodeMax() != null) ? risposta.getApplicabilita().getReturnCodeMax() : null;
			TrasformazioneRegolaRisposta rispostaAggiornata = porteApplicativeCore.getTrasformazioneRisposta(Long.parseLong(idPorta), trasformazioneAggiornata.getId(), statusMinRisposta, statusMaxRisposta, patternRispostaDBCheck, contentTypeRispostaDBCheck);
			
			
			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE_HEADER; 
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			
			List<TrasformazioneRegolaParametro> lista = porteApplicativeCore.porteAppTrasformazioniRispostaHeaderList(Long.parseLong(idPorta), trasformazioneAggiornata.getId(), rispostaAggiornata.getId(), ricerca);
			
			porteApplicativeHelper.preparePorteAppTrasformazioniRispostaHeaderList(trasformazioneAggiornata.getId(), rispostaAggiornata.getId(), ricerca, lista); 
						
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER,	ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER, 
					ForwardParams.CHANGE());
		}
	}
}
