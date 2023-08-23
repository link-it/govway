/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
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
 * porteDelegateCorrAppAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateCorrelazioneApplicativaRequestAdd extends Action {

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
			String mode = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE);
			if (mode == null) {
				mode = PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED;
			}
			String pattern = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PATTERN);
			String gif = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_GESTIONE_IDENTIFICAZIONE_FALLITA);
			String riusoIdentificativoMessaggio = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_RIUSO_ID_MESSAGGIO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore( );
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo il nome della porta delegata
			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(apc.getServiceBinding());
			String nome = pde.getNome();
			
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(pde.getTipoSoggettoProprietario());

			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			Parameter pNome = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME, nome);
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRACCIAMENTO,
						pde);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_CONFIG_DI+nome;
			}
			lstParam.add(new Parameter(labelPerPorta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA, pId,pIdSoggetto,pIdAsps,pIdFrizione, pNome));
			
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_CORRELAZIONI_APPLICATIVE_RICHIESTA_DI, // + nome,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST_LIST,pId,pIdSoggetto,pIdAsps,pIdFrizione, pNome));
			
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				dati = porteDelegateHelper. addPorteDelegateCorrelazioneApplicativaRequestToDati(TipoOperazione.ADD , pd,  
						elemxml, mode, pattern, gif, riusoIdentificativoMessaggio, dati, null, protocollo,
						apc.getServiceBinding());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST, ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.correlazioneApplicativaRichiestaCheckData(TipoOperazione.ADD,true,
					serviceBinding);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				dati = porteDelegateHelper.	addPorteDelegateCorrelazioneApplicativaRequestToDati(TipoOperazione.ADD, pd, 
						elemxml, mode, pattern, gif, riusoIdentificativoMessaggio, dati, null, protocollo,
						apc.getServiceBinding());

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST,
						ForwardParams.ADD());
			}

			// Inserisco la correlazione applicativa nel db
			CorrelazioneApplicativaElemento cae = new CorrelazioneApplicativaElemento();
			cae.setNome(elemxml);
			cae.setIdentificazione(CorrelazioneApplicativaRichiestaIdentificazione.toEnumConstant(mode));
			if (mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_URL_BASED) || 
					mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_HEADER_BASED) || 
					mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_CONTENT_BASED) ||
					mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_TEMPLATE) ||
					mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_FREEMARKER_TEMPLATE) ||
					mode.equals(PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_VELOCITY_TEMPLATE)) {
				cae.setPattern(pattern);
			}

			if(!PorteDelegateCostanti.VALUE_PARAMETRO_PORTE_DELEGATE_TIPO_MODE_CORRELAZIONE_DISABILITATO.equals(mode)){
				cae.setIdentificazioneFallita(CorrelazioneApplicativaGestioneIdentificazioneFallita.toEnumConstant(gif));
				cae.setRiusoIdentificativo(StatoFunzionalita.toEnumConstant(riusoIdentificativoMessaggio));
			}

			CorrelazioneApplicativa ca = pde.getCorrelazioneApplicativa();
			if (ca == null) {
				ca = new CorrelazioneApplicativa();
			}
			ca.addElemento(cae);
			pde.setCorrelazioneApplicativa(ca);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			List<CorrelazioneApplicativaElemento> lista = porteDelegateCore.porteDelegateCorrelazioneApplicativaList(idInt, ricerca);

			porteDelegateHelper.preparePorteDelegateCorrAppList(nome, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_CORRELAZIONE_APPLICATIVA_REQUEST, 
					ForwardParams.ADD());
		}  
	}


}
