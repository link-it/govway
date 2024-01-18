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


package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.util.List;
import java.util.ArrayList;

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

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);

			String elemxml = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ELEMENTO_XML);
			if(elemxml!=null)
				elemxml = StringEscapeUtils.escapeHtml(elemxml);

			String mode = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE);

			String pattern = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PATTERN);
			if(pattern!=null)
				pattern = StringEscapeUtils.escapeHtml(pattern);

			String idcorrString = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_CORRELAZIONE);
			int idcorr = Integer.parseInt(idcorrString);
			String gif = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo il nome della porta delegata
			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(apc.getServiceBinding());
			String nomePorta = pde.getNome();

			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(pde.getTipoSoggettoProprietario());
			
			// Prendo il nome originario della correlazione applicativa
			String elemxmlOrig = "";
			CorrelazioneApplicativaRisposta ca = pde.getCorrelazioneApplicativaRisposta();
			CorrelazioneApplicativaRispostaElemento cae = null;
			// Quando esco dal ciclo, cae è il mio elemento
			for (int i = 0; i < ca.sizeElementoList(); i++) {
				cae = ca.getElemento(i);
				if (idcorr == cae.getId().intValue()) {
					elemxmlOrig = cae.getNome();
					break;
				}
			}
			if (elemxmlOrig == null || "".equals(elemxmlOrig))
				elemxmlOrig = CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI;

			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			Parameter pNomePorta = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, nomePorta);
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRACCIAMENTO,
						pde);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_CONFIG_DI+nomePorta;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, pId,pIdSoggetto,pIdAsps,pIdFrizione, pNomePorta));
			
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RISPOSTA_DI, // + nome,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE_LIST,pId,pIdSoggetto,pIdAsps,pIdFrizione, pNomePorta));
			
			lstParam.add(new Parameter(elemxmlOrig , null));
			
			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(	porteDelegateHelper.isEditModeInProgress()){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				if (elemxml == null) {
					elemxml = StringEscapeUtils.escapeHtml(cae.getNome());
				}
				if (mode == null &&
					cae.getIdentificazione()!=null) {
					mode = cae.getIdentificazione().toString();
				}
				if (pattern == null) {
					pattern = StringEscapeUtils.escapeHtml(cae.getPattern());
				}
				if (gif == null &&
					cae.getIdentificazioneFallita()!=null) {
					gif = cae.getIdentificazioneFallita().toString();
				}


				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				dati = porteDelegateHelper.addPorteDelegateCorrelazioneApplicativaResponseToDati(TipoOperazione.CHANGE, 
						pd,  elemxml, mode, pattern, gif, dati, idcorrString, apc.getServiceBinding(),
						protocollo);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE, ForwardParams.CHANGE());

			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.correlazioneApplicativaRispostaCheckData(TipoOperazione.CHANGE,true,
					serviceBinding);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);
				
				dati = porteDelegateHelper.addPorteDelegateCorrelazioneApplicativaResponseToDati(TipoOperazione.CHANGE, 
						pd,   elemxml, mode, pattern, gif, dati, idcorrString, apc.getServiceBinding(),
						protocollo);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

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
					caeNew = cae;
					break;
				}
			}
			if(caeNew==null) {
				throw new Exception("CorrelazioneApplicativaRispostaElemento con id '"+idcorr+"' non trovata");
			}

			if(CostantiControlStation.LABEL_PORTE_CORRELAZIONE_APPLICATIVA_QUALSIASI.equals(elemxml)) {
				caeNew.setNome(null);
			}
			else {
				caeNew.setNome(StringEscapeUtils.unescapeHtml(elemxml));
			}
			caeNew.setIdentificazione(CorrelazioneApplicativaRispostaIdentificazione.toEnumConstant(mode));
			if (mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED) || 
					mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_HEADER_BASED) || 
					mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED) ||
					mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_TEMPLATE) ||
					mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_FREEMARKER_TEMPLATE) ||
					mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_VELOCITY_TEMPLATE)) {
				caeNew.setPattern(StringEscapeUtils.unescapeHtml(pattern));
			}
			if(!PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_DISABILITATO.equals(mode)){
				caeNew.setIdentificazioneFallita(CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstant(gif));
			}
			//ca.addElemento(caeNew); l'elemento e' stato modificato per riferimento
			pde.setCorrelazioneApplicativaRisposta(ca);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<CorrelazioneApplicativaRispostaElemento> lista = porteDelegateCore.porteDelegateCorrelazioneApplicativaRispostaList(idInt, ricerca);

			porteDelegateHelper.preparePorteDelegateCorrAppRispostaList(nomePorta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_RESPONSE, 
					ForwardParams.CHANGE());
		}  
	}
}
