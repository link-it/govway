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

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
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
 * PorteApplicativeMTOMRequestChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeMTOMRequestChange extends Action {

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
			
			String id = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParametroLong(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String nome = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String contentType = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONTENT_TYPE);
			String obbligatorio = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_OBBLIGATORIO);
			String pattern = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PATTERN);

			// Prendo il nome della porta applicativa
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();

			Parameter[] urlParms = { 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id)	,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg) ,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS,idAsps) };

			MtomProcessorFlowParameter flowParameterOld = null;
			MtomProcessor mtomProcessor = pa.getMtomProcessor();
			if(mtomProcessor.getRequestFlow()!=null){
				List<MtomProcessorFlowParameter> wsrfpArray = mtomProcessor.getRequestFlow().getParameterList();
				for (int i = 0; i < wsrfpArray.size(); i++) {
					MtomProcessorFlowParameter wsrfp = wsrfpArray.get(i);
					if (nome.equals(wsrfp.getNome())) {
						flowParameterOld = mtomProcessor.getRequestFlow().getParameter(i);
						break;
					}
				}
			}
			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta, 
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM, urlParms));
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_REQUEST_FLOW_DI, // + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_REQUEST_LIST, urlParms  
			));
			lstParam.add(new Parameter(nome, null));
			
			
			if(	porteApplicativeHelper.isEditModeInProgress()){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// primo accesso 
				if(nome == null &&
					flowParameterOld != null) {
					nome  = flowParameterOld.getNome();
				}
				
				if(pattern == null &&
					flowParameterOld != null) {
					pattern  = flowParameterOld.getPattern();
				}
				
				if(contentType == null &&
					flowParameterOld != null) {
					contentType  = flowParameterOld.getContentType();
				}
				
				if(obbligatorio == null &&
					flowParameterOld != null){
					boolean b = flowParameterOld.getRequired();
					obbligatorio = b ? "yes" : ""; 
				}
				
				if(nome == null)
					nome = "";
				
				if(pattern == null)
					pattern = "";
				
				if(contentType == null)
					contentType = "";
				
				if(obbligatorio == null)
					obbligatorio = "";
					
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addMTOMParameterToDati(TipoOperazione.CHANGE, dati, false, nome, pattern, contentType, obbligatorio,
						pa.getMtomProcessor().getRequestFlow().getMode());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_REQUEST, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.MTOMParameterCheckData(TipoOperazione.CHANGE, false, false);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addMTOMParameterToDati(TipoOperazione.CHANGE, dati, false, nome, pattern, contentType, obbligatorio,
						pa.getMtomProcessor().getRequestFlow().getMode());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_REQUEST,
						ForwardParams.CHANGE());
			}

			// Modifico i dati del message-security della porta delegata nel db
			// Rimozione vecchio parametro
			mtomProcessor = pa.getMtomProcessor();
			if(mtomProcessor.getRequestFlow()!=null){
				List<MtomProcessorFlowParameter> wsrfpArray = mtomProcessor.getRequestFlow().getParameterList();
				for (int i = 0; i < wsrfpArray.size(); i++) {
					MtomProcessorFlowParameter wsrfp = wsrfpArray.get(i);
					if (nome.equals(wsrfp.getNome())) {
						mtomProcessor.getRequestFlow().removeParameter(i);
						break;
					}
				}
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

			mtomProcessor = pa.getMtomProcessor();
			if (mtomProcessor == null) {
				mtomProcessor = new MtomProcessor();
			}
			if(mtomProcessor.getRequestFlow()==null){
				mtomProcessor.setRequestFlow(new MtomProcessorFlow());
			}
			mtomProcessor.getRequestFlow().addParameter(nuovoParametro);
			pa.setMtomProcessor(mtomProcessor);



			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			int idLista = Liste.PORTE_APPLICATIVE_MTOM_REQUEST;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<MtomProcessorFlowParameter> lista = porteApplicativeCore.porteApplicativeMTOMRequestList(Integer.parseInt(id), ricerca);

			porteApplicativeHelper.preparePorteApplicativeMTOMRequestList(ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_REQUEST,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI,	ForwardParams.CHANGE());
		}
	}

}
