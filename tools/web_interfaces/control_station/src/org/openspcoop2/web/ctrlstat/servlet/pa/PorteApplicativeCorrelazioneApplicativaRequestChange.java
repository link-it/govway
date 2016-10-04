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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione;
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
 * porteApplicativeCorrAppChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeCorrelazioneApplicativaRequestChange extends Action {

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
			//String nomePorta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
			String idPorta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);

			String elemxml = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
			if(elemxml!=null)
				elemxml = StringEscapeUtils.escapeHtml(elemxml);

			String mode = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE);

			String pattern = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PATTERN);
			if(pattern!=null)
				pattern = StringEscapeUtils.escapeHtml(pattern);

			String idcorrString = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_CORRELAZIONE_APPLICATIVA);
			int idcorr = Integer.parseInt(idcorrString);
			String gif = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			String riusoIdMessaggio = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RIUSO_ID_MESSAGGIO);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome e tipo del soggetto
			org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
			String tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();

			// Prendo il nome della porta applicativa
			PortaApplicativa pde = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pde.getNome();

			// Prendo il nome originario della correlazione applicativa
			String elemxmlOrig = "";
			CorrelazioneApplicativa ca = pde.getCorrelazioneApplicativa();
			CorrelazioneApplicativaElemento cae = null;
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
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, 
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)),
									new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_DI + nomePorta,
											PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,
											new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
											new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
											new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nomePorta)),
											new Parameter(elemxmlOrig, null)
							);
				}else{
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_DI + nomePorta,
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nomePorta)),
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
				if (riusoIdMessaggio == null){
					if(cae.getRiusoIdentificativo()!=null)
						riusoIdMessaggio = cae.getRiusoIdentificativo().toString();
				}


				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldCorrelazioneApplicativaToDati(TipoOperazione.CHANGE, idcorrString, dati);

				dati = porteApplicativeHelper.addPorteApplicativeCorrelazioneApplicativeRichiestaToDati(TipoOperazione.CHANGE, elemxml, mode,
						pattern, gif, riusoIdMessaggio, dati);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST,
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.correlazioneApplicativaCheckData(TipoOperazione.CHANGE,false);
			if (!isOk) {
				// setto la barra del titolo
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, 
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)),
									new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_DI + nomePorta,
											PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,
											new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
											new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
											new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nomePorta)),
											new Parameter(elemxmlOrig, null)
							);
				}else{
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_DI + nomePorta,
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
									new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, nomePorta)),
									new Parameter(elemxmlOrig, null)
							);
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldCorrelazioneApplicativaToDati(TipoOperazione.CHANGE, idcorrString, dati);

				dati = porteApplicativeHelper.addPorteApplicativeCorrelazioneApplicativeRichiestaToDati(TipoOperazione.CHANGE, elemxml, mode,
						pattern, gif, riusoIdMessaggio, dati);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST, 
						ForwardParams.CHANGE());
			}

			// Modifico i dati della correlazione applicativa della porta
			// applicativa
			// nel db
			CorrelazioneApplicativaElemento caeNew=null;
			for (int i = 0; i < ca.sizeElementoList(); i++) {
				cae = ca.getElemento(i);
				if (idcorr == cae.getId().intValue()) {
					//ca.removeElemento(i);
					caeNew = cae;
					break;
				}
			}

			caeNew.setNome(StringEscapeUtils.unescapeHtml(elemxml));
			caeNew.setIdentificazione(CorrelazioneApplicativaRichiestaIdentificazione.toEnumConstant(mode));
			if (mode.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
					mode.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED)) {
				caeNew.setPattern(StringEscapeUtils.unescapeHtml(pattern));
			}
			if(!PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO.equals(mode)){
				caeNew.setIdentificazioneFallita(CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstant(gif));
			}
			//ca.addElemento(caeNew); l'elemento e' stato modificato per riferimento
			pde.setCorrelazioneApplicativa(ca);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pde);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<CorrelazioneApplicativaElemento> lista = porteApplicativeCore.porteApplicativeCorrelazioneApplicativaList(idInt, ricerca);

			porteApplicativeHelper.preparePorteApplicativeCorrAppList(nomePorta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST, 
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST,
					ForwardParams.CHANGE());
		}  
	}


}
