/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteDelegateCorrAppRispostaChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateCorrelazioneApplicativaResponseChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, session);


		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);

			String id = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);

			String elemxml = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
			if(elemxml!=null)
				elemxml = StringEscapeUtils.escapeHtml(elemxml);

			String mode = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE);

			String pattern = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PATTERN);
			if(pattern!=null)
				pattern = StringEscapeUtils.escapeHtml(pattern);

			String idcorrString = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_CORRELAZIONE);
			int idcorr = Integer.parseInt(idcorrString);
			String gif = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_CORRELAZIONE);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome e tipo del soggetto
			String tmpTitle = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}

			// Prendo il nome della porta delegata
			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = pde.getNome();

			// Prendo il nome originario della correlazione applicativa
			String elemxmlOrig = "";
			CorrelazioneApplicativaRisposta ca = pde.getCorrelazioneApplicativaRisposta();
			CorrelazioneApplicativaRispostaElemento cae = null;
			for (int i = 0; i < ca.sizeElementoList(); i++) {
				cae = ca.getElemento(i);
				if (idcorr == cae.getId().intValue()) {
					elemxmlOrig = cae.getNome();
					break;
				}
			}
			if (elemxmlOrig == null)
				elemxmlOrig = "";
			// Quando esco dal ciclo, cae è il mio elemento

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(	ServletUtils.isEditModeInProgress(request)){
				if(useIdSogg){
					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle, 
									PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
									new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
									),
									new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + nomePorta, 
											PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
											new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
											new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
											new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,nomePorta)
											),
											new Parameter(elemxmlOrig, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST),
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + nomePorta, 
									PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
									new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
									new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
									new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,nomePorta)
									),
									new Parameter(elemxmlOrig, null)
							);
				}

				if (elemxml == null) {
					elemxml = StringEscapeUtils.escapeHtml(cae.getNome());
				}
				if (mode == null) {
					if(cae.getIdentificazione()!=null)
						mode = cae.getIdentificazione().toString();
				}
				if (pattern == null) {
					pattern = StringEscapeUtils.escapeHtml(cae.getPattern());
				}
				if (gif == null){
					if(cae.getIdentificazioneFallita()!=null)
						gif = cae.getIdentificazioneFallita().toString();
				}


				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, dati);

				dati = porteDelegateHelper.addPorteDelegateCorrelazioneApplicativaResponseToDati(TipoOperazione.CHANGE, 
						pd,  elemxml, mode, pattern, gif, dati, idcorrString);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE, ForwardParams.CHANGE());

			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.correlazioneApplicativaRispostaCheckData(TipoOperazione.CHANGE,true);
			if (!isOk) {
				if(useIdSogg){
					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle, 
									PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
									new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)
									),
									new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + nomePorta, 
											PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
											new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
											new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
											new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,nomePorta)
											),
											new Parameter(elemxmlOrig, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST),
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + nomePorta, 
									PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
									new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id),
									new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg),
									new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME,nomePorta)
									),
									new Parameter(elemxmlOrig, null)
							);
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, dati);

				dati = porteDelegateHelper.addPorteDelegateCorrelazioneApplicativaResponseToDati(TipoOperazione.CHANGE, 
						pd,   elemxml, mode, pattern, gif, dati, idcorrString);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE,
						ForwardParams.CHANGE());
			}

			// Modifico i dati della correlazione applicativa della porta
			// delegata
			// nel db
			CorrelazioneApplicativaRispostaElemento caeNew=null;
			for (int i = 0; i < ca.sizeElementoList(); i++) {
				cae = ca.getElemento(i);
				if (idcorr == cae.getId().intValue()) {
					//ca.removeElemento(i);
					caeNew = cae;
					break;
				}
			}

			caeNew.setNome(StringEscapeUtils.unescapeHtml(elemxml));
			caeNew.setIdentificazione(CorrelazioneApplicativaRispostaIdentificazione.toEnumConstant(mode));
			if (mode.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED) || 
					mode.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
				caeNew.setPattern(StringEscapeUtils.unescapeHtml(pattern));
			}
			if(!PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_DISABILITATO.equals(mode)){
				caeNew.setIdentificazioneFallita(CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstant(gif));
			}
			//ca.addElemento(caeNew); l'elemento e' stato modificato per riferimento
			pde.setCorrelazioneApplicativaRisposta(ca);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<CorrelazioneApplicativaRispostaElemento> lista = porteDelegateCore.porteDelegateCorrelazioneApplicativaRispostaList(idInt, ricerca);

			porteDelegateHelper.preparePorteDelegateCorrAppRispostaList(nomePorta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE, 
					ForwardParams.CHANGE());
		}  
	}
}
