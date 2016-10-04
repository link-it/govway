/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.PortaApplicativa;
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
 * porteApplicativeCorrAppRispostaAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeCorrelazioneApplicativaResponseAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO, session);


		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);

			String idPorta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String elemxml = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
			String mode = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE);
			if (mode == null) {
				mode = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED;
			}
			String pattern = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PATTERN);
			String gif = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_IDENTIFICAZIONE_FALLITA);

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome e tipo del soggetto
			String tmpTitle = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}

			// Prendo il nome della porta applicativa
			PortaApplicativa pde = porteApplicativeCore.getPortaApplicativa(idInt);
			String nome = pde.getNome();

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				if(useIdSogg){
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, 
								PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + nome,
								PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nome)),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
						);
				}else{
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + nome,
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nome)),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
							);
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addPorteApplicativeCorrelazioneApplicativeRispostaToDati(TipoOperazione.ADD,
						elemxml, mode, pattern, gif, dati);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.correlazioneApplicativaRispostaCheckData(TipoOperazione.ADD,false);
			if (!isOk) {
				// setto la barra del titolo
				if(useIdSogg){
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, 
								PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + nome,
								PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nome)),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
						);
				}else{
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI + nome,
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nome)),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
							);
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addPorteApplicativeCorrelazioneApplicativeRispostaToDati(TipoOperazione.ADD,
						elemxml, mode, pattern, gif, dati);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE, 
						ForwardParams.ADD());
			}

			// Inserisco la correlazione applicativa nel db
			CorrelazioneApplicativaRispostaElemento cae = new CorrelazioneApplicativaRispostaElemento();
			cae.setNome(elemxml);
			cae.setIdentificazione(CorrelazioneApplicativaRispostaIdentificazione.toEnumConstant(mode));
			if (mode.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
					mode.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
				cae.setPattern(pattern);
			}
			
			if(!PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO.equals(mode)){
				cae.setIdentificazioneFallita(CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstant(gif));
			}
			
			CorrelazioneApplicativaRisposta ca = pde.getCorrelazioneApplicativaRisposta();
			if (ca == null) {
				ca = new CorrelazioneApplicativaRisposta();
			}
			ca.addElemento(cae);
			pde.setCorrelazioneApplicativaRisposta(ca);

			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pde);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<CorrelazioneApplicativaRispostaElemento> lista = porteApplicativeCore.porteApplicativeCorrelazioneApplicativaRispostaList(idInt, ricerca);

			porteApplicativeHelper.preparePorteApplicativeCorrAppRispostaList(nome, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE, 
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_RESPONSE,
					ForwardParams.ADD());
		}  
	}

	
}
