/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
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
 * PorteApplicativeMTOMResponseAdd
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeMTOMResponseAdd  extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String nome = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String contentType = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONTENT_TYPE);
			String obbligatorio = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_OBBLIGATORIO);
			String pattern = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PATTERN);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();

			Parameter[] urlParms = { 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id)	,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg) ,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS,idAsps) };
			
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
			lstParam.add(new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_RESPONSE_FLOW_DI, // + idporta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE_LIST, urlParms  
			));
			lstParam.add(ServletUtils.getParameterAggiungi());
			
			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
//			if (nome == null) {
				if(	porteApplicativeHelper.isEditModeInProgress()){
					
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addMTOMParameterToDati(TipoOperazione.ADD, dati, true, nome, pattern, contentType, obbligatorio);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD,id, idsogg,null,idAsps,  dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE, ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.MTOMParameterCheckData(TipoOperazione.ADD,true,false);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addMTOMParameterToDati(TipoOperazione.ADD, dati, true, nome, pattern, contentType, obbligatorio);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD,id, idsogg, null,idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE,
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
			
			MtomProcessor mtomProcessor = pa.getMtomProcessor();
			if (mtomProcessor == null) {
				mtomProcessor = new MtomProcessor();
			}
			if(mtomProcessor.getResponseFlow()==null){
				mtomProcessor.setResponseFlow(new MtomProcessorFlow());
			}
			mtomProcessor.getResponseFlow().addParameter(nuovoParametro);
			pa.setMtomProcessor(mtomProcessor);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_APPLICATIVE_MTOM_RESPONSE;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<MtomProcessorFlowParameter> lista = porteApplicativeCore.porteApplicativeMTOMResponseList(Integer.parseInt(id), ricerca);

			porteApplicativeHelper.preparePorteApplicativeMTOMResponseList(idporta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE, 
					ForwardParams.ADD());
		}  
	}

}
