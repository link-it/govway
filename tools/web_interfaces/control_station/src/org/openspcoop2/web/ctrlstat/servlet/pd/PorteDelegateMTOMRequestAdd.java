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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/***
 * 
 * PorteDelegateMTOMRequestAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateMTOMRequestAdd extends Action {

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
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			String id = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String nome = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String contentType = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_CONTENT_TYPE);
			String obbligatorio = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_OBBLIGATORIO);
			String pattern = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_PATTERN);
			String idAsps = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParametroLong(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";

			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();

			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = pde.getNome();

			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			
			Parameter[] urlParms = { pId,pIdSoggetto,pIdAsps,pIdFrizione };
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_CONFIG,
						pde);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta, 
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM, urlParms));
			
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_REQUEST_FLOW_DI, // + idporta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_REQUEST_LIST, urlParms  
			));
			
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			
			if(	porteDelegateHelper.isEditModeInProgress()){
					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addMTOMParameterToDati(TipoOperazione.ADD, dati, true, nome, pattern, contentType, obbligatorio,
						pde.getMtomProcessor().getRequestFlow().getMode());
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM_REQUEST, ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.MTOMParameterCheckData(TipoOperazione.ADD,false,true);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addMTOMParameterToDati(TipoOperazione.ADD, dati, true, nome, pattern, contentType, obbligatorio,
						pde.getMtomProcessor().getRequestFlow().getMode());

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.ADD, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM_REQUEST,
						ForwardParams.ADD());
			}

			// Inserisco il parametro della porta delegata nel db
			MtomProcessorFlowParameter nuovoParametro = new MtomProcessorFlowParameter();
			nuovoParametro.setNome(nome);
			nuovoParametro.setPattern(pattern);
			nuovoParametro.setContentType(contentType);
			if( ServletUtils.isCheckBoxEnabled(obbligatorio) || CostantiRegistroServizi.ABILITATO.equals(obbligatorio) ){
				nuovoParametro.setRequired(true);
			} else 
				nuovoParametro.setRequired(false);
			
			MtomProcessor mtomProcessor = pde.getMtomProcessor();
			if (mtomProcessor == null) {
				mtomProcessor = new MtomProcessor();
			}
			if(mtomProcessor.getRequestFlow()==null){
				mtomProcessor.setRequestFlow(new MtomProcessorFlow());
			}
			mtomProcessor.getRequestFlow().addParameter(nuovoParametro);
			pde.setMtomProcessor(mtomProcessor);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.PORTE_DELEGATE_MTOM_REQUEST;

			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);

			List<MtomProcessorFlowParameter> lista = porteDelegateCore.porteDelegateMTOMRequestList(Integer.parseInt(id), ricerca);

			porteDelegateHelper.preparePorteDelegateMTOMRequestList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM_REQUEST,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM_REQUEST, 
					ForwardParams.ADD());
		}  
	}

}
