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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.MtomProcessorFlowParameter;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
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

/***
 * 
 * PorteApplicativeMTOMResponseChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeMTOMResponseChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO, session);


		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String id = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String nome = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String contentType = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONTENT_TYPE);
			String obbligatorio = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_OBBLIGATORIO);
			String pattern = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_PATTERN);

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo il nome della porta applicativa
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);

			PortaApplicativa pde = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pde.getNome();

			// Prendo nome, tipo e pdd del soggetto
			String tmpTitle = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}
			// String pdd = soggetto.getServer();
			Parameter[] urlParms = { 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id)	,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg) };

			MtomProcessorFlowParameter flowParameterOld = null;
			MtomProcessor mtomProcessor = pde.getMtomProcessor();
			if(mtomProcessor.getResponseFlow()!=null){
				List<MtomProcessorFlowParameter> wsrfpArray = mtomProcessor.getResponseFlow().getParameterList();
				for (int i = 0; i < wsrfpArray.size(); i++) {
					MtomProcessorFlowParameter wsrfp = wsrfpArray.get(i);
					if (nome.equals(wsrfp.getNome())) {
						flowParameterOld = mtomProcessor.getResponseFlow().getParameter(i);
						break;
					}
				}
			}
						
			if(	ServletUtils.isEditModeInProgress(request)){
				// setto la barra del titolo
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, 
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST, new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg)),
									new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DI + idporta, 
											PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM, urlParms),
											new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_RESPONSE_FLOW_DI + idporta, 
													PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE_LIST,urlParms  
													),
													new Parameter(nome, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DI + idporta, 
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM, urlParms),
									new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_RESPONSE_FLOW_DI + idporta, 
											PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE_LIST,urlParms  
											),
											new Parameter(nome, null)
							);
				}

				// primo accesso 
				if(nome == null)
					if(flowParameterOld != null)
						nome  = flowParameterOld.getNome();
				
				if(pattern == null)
					if(flowParameterOld != null)
						pattern  = flowParameterOld.getPattern();
				
				if(contentType == null)
					if(flowParameterOld != null)
						contentType  = flowParameterOld.getContentType();
				
				if(obbligatorio == null)
					if(flowParameterOld != null){
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
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addMTOMParameterToDati(TipoOperazione.CHANGE, dati, false, nome, pattern, contentType, obbligatorio);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE,id, idsogg, null, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.MTOMParameterCheckData(TipoOperazione.CHANGE, true, false);
			if (!isOk) {
				// setto la barra del titolo
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, 
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST, new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg)),
									new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DI + idporta, 
											PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM, urlParms),
											new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_RESPONSE_FLOW_DI + idporta, 
													PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE_LIST,urlParms  
													),
													new Parameter(nome, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST),
							new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DI + idporta, 
									PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM, urlParms),
									new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_RESPONSE_FLOW_DI + idporta, 
											PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE_LIST,urlParms  
											),
											new Parameter(nome, null)
							);
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addMTOMParameterToDati(TipoOperazione.CHANGE, dati, false, nome, pattern, contentType, obbligatorio);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE,id, idsogg, null, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE,
						ForwardParams.CHANGE());
			}

			// Modifico i dati del message-security della porta delegata nel db
			// Rimozione vecchio parametro
			mtomProcessor = pde.getMtomProcessor();
			if(mtomProcessor.getResponseFlow()!=null){
				List<MtomProcessorFlowParameter> wsrfpArray = mtomProcessor.getResponseFlow().getParameterList();
				for (int i = 0; i < wsrfpArray.size(); i++) {
					MtomProcessorFlowParameter wsrfp = wsrfpArray.get(i);
					if (nome.equals(wsrfp.getNome())) {
						mtomProcessor.getResponseFlow().removeParameter(i);
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

			mtomProcessor = pde.getMtomProcessor();
			if (mtomProcessor == null) {
				mtomProcessor = new MtomProcessor();
			}
			if(mtomProcessor.getResponseFlow()==null){
				mtomProcessor.setResponseFlow(new MtomProcessorFlow());
			}
			mtomProcessor.getResponseFlow().addParameter(nuovoParametro);
			pde.setMtomProcessor(mtomProcessor);



			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pde);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_APPLICATIVE_MTOM_RESPONSE;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<MtomProcessorFlowParameter> lista = porteApplicativeCore.porteApplicativeMTOMResponseList(Integer.parseInt(id), ricerca);

			porteApplicativeHelper.preparePorteApplicativeMTOMResponseList(idporta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE, 
					ForwardParams.CHANGE());
		}
	}

}
