/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.message.constants.ServiceBinding;
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
			
			String elemxml = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ELEMENTO_XML);
			if(elemxml!=null)
				elemxml = StringEscapeUtils.escapeHtml4(elemxml);

			String mode = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE);

			String pattern = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PATTERN);
			if(pattern!=null)
				pattern = StringEscapeUtils.escapeHtml4(pattern);

			String idcorrString = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_CORRELAZIONE_APPLICATIVA);
			int idcorr = Integer.parseInt(idcorrString);
			String gif = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			String riusoIdMessaggio = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RIUSO_ID_MESSAGGIO);
			
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);

			// Prendo il nome della porta applicativa
			PortaApplicativa pde = porteApplicativeCore.getPortaApplicativa(idInt);
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(pde.getTipoSoggettoProprietario());
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(apc.getServiceBinding());
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
			if (elemxmlOrig == null || "".equals(elemxmlOrig))
				elemxmlOrig = CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI;
			// Quando esco dal ciclo, cae è il mio elemento
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRACCIAMENTO,
						pde);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_CONFIG_DI+nomePorta;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CORRELAZIONI_APPLICATIVE_RICHIESTA_DI, // + nomePorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)));
			lstParam.add(new Parameter(elemxmlOrig, null));

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				if (elemxml == null) {
					elemxml = StringEscapeUtils.escapeHtml4(cae.getNome());
				}
				if (mode == null &&
					cae.getIdentificazione()!=null) {
					mode = cae.getIdentificazione().toString();
				}
				if (pattern == null) {
					pattern = StringEscapeUtils.escapeHtml4(cae.getPattern());
				}
				if (gif == null &&
					cae.getIdentificazioneFallita()!=null) {
					gif = cae.getIdentificazioneFallita().toString();
				}
				if (riusoIdMessaggio == null &&
					cae.getRiusoIdentificativo()!=null) {
					riusoIdMessaggio = cae.getRiusoIdentificativo().toString();
				}


				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldCorrelazioneApplicativaToDati(TipoOperazione.CHANGE, idcorrString, dati);

				dati = porteApplicativeHelper.addPorteApplicativeCorrelazioneApplicativeRichiestaToDati(TipoOperazione.CHANGE, elemxml, mode,
						pattern, gif, riusoIdMessaggio, dati, apc.getServiceBinding(),
						protocollo);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST,
						ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.correlazioneApplicativaRichiestaCheckData(TipoOperazione.CHANGE,false,
					serviceBinding);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldCorrelazioneApplicativaToDati(TipoOperazione.CHANGE, idcorrString, dati);

				dati = porteApplicativeHelper.addPorteApplicativeCorrelazioneApplicativeRichiestaToDati(TipoOperazione.CHANGE, elemxml, mode,
						pattern, gif, riusoIdMessaggio, dati, apc.getServiceBinding(),
						protocollo);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

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
					caeNew = cae;
					break;
				}
			}
			if(caeNew==null) {
				throw new Exception("CorrelazioneApplicativaElemento con id '"+idcorr+"' non trovato");
			}
			
			if(CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI.equals(elemxml)) {
				caeNew.setNome(null);
			}
			else {
				caeNew.setNome(StringEscapeUtils.unescapeHtml4(elemxml));
			}
			caeNew.setIdentificazione(CorrelazioneApplicativaRichiestaIdentificazione.toEnumConstant(mode));
			if (mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_URL_BASED) ||
					mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_HEADER_BASED) ||
					mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED) ||
					mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_TEMPLATE) ||
					mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_FREEMARKER_TEMPLATE) ||
					mode.equals(PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_VELOCITY_TEMPLATE)) {
				caeNew.setPattern(StringEscapeUtils.unescapeHtml4(pattern));
			}
			if(!PorteApplicativeCostanti.VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_MODE_CORRELAZIONE_DISABILITATO.equals(mode)){
				caeNew.setIdentificazioneFallita(CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstant(gif));
			}
			//ca.addElemento(caeNew); l'elemento e' stato modificato per riferimento
			pde.setCorrelazioneApplicativa(ca);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pde);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<CorrelazioneApplicativaElemento> lista = porteApplicativeCore.porteApplicativeCorrelazioneApplicativaList(idInt, ricerca);

			porteApplicativeHelper.preparePorteApplicativeCorrAppList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST, 
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_CORRELAZIONE_APPLICATIVA_REQUEST,
					ForwardParams.CHANGE());
		}  
	}


}
