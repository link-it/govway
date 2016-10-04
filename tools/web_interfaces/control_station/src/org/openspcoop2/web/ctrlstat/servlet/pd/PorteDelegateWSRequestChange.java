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


package org.openspcoop2.web.ctrlstat.servlet.pd;

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
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
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
 * porteDelegateWSRequestChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateWSRequestChange extends Action {

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
			String nome = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String valore = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VALORE);

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo il nome della porta applicativa
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteDelegateCore);

			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = pde.getNome();

			// Prendo nome, tipo e pdd del soggetto
			String tmpTitle = null;
			if(porteDelegateCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}
			else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}
			// String pdd = soggetto.getServer();
			Parameter[] urlParms = { 
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id)	,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg) };
			// Se valore = null, devo visualizzare la pagina per la
			// modifica dati
			//			if (valore == null) {
			if(	ServletUtils.isEditModeInProgress(request)){
				// setto la barra del titolo
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle, 
									PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST, new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)),
									new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_DI + idporta, 
											PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, urlParms),
											new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_DI + idporta, 
													PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_LIST,urlParms  
													),
													new Parameter(nome, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST),
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_DI + idporta, 
									PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, urlParms),
									new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_DI + idporta, 
											PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_LIST,urlParms  
											),
											new Parameter(nome, null)
							);
				}


				MessageSecurity ws = pde.getMessageSecurity();
				if(ws.getRequestFlow()!=null){
					List<MessageSecurityFlowParameter> wsrfpArray = ws.getRequestFlow().getParameterList();
					for (int i = 0; i < wsrfpArray.size(); i++) {
						MessageSecurityFlowParameter wsrfp = wsrfpArray.get(i);
						if (nome.equals(wsrfp.getNome())) {
							valore = wsrfp.getValore();
							break;
						}
					}
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addNomeValoreToDati(TipoOperazione.CHANGE,dati, nome, valore,false);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE,id, idsogg, null, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.porteDelegateMessageSecurityRequestCheckData(TipoOperazione.CHANGE);
			if (!isOk) {
				// setto la barra del titolo
				if(useIdSogg){
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle, 
									PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST, new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)),
									new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_DI + idporta, 
											PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, urlParms),
											new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_DI + idporta, 
													PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_LIST,urlParms  
													),
													new Parameter(nome, null)
							);
				}else {
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null),
							new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST),
							new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_DI + idporta, 
									PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, urlParms),
									new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_DI + idporta, 
											PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_LIST,urlParms  
											),
											new Parameter(nome, null)
							);
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addNomeValoreToDati(TipoOperazione.CHANGE,dati, nome, valore,false);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE,id, idsogg, null, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST,
						ForwardParams.CHANGE());
			}

			// Modifico i dati del message-security della porta delegata nel db
			MessageSecurity ws = pde.getMessageSecurity();
			if(ws.getRequestFlow()!=null){
				List<MessageSecurityFlowParameter> wsrfpArray = ws.getRequestFlow().getParameterList();
				for (int i = 0; i < wsrfpArray.size(); i++) {
					MessageSecurityFlowParameter wsrfp = wsrfpArray.get(i);
					if (nome.equals(wsrfp.getNome())) {
						ws.getRequestFlow().removeParameter(i);
						break;
					}
				}
			}

			MessageSecurityFlowParameter wsrfp = new MessageSecurityFlowParameter();
			wsrfp.setNome(nome);
			wsrfp.setValore(valore);
			if(ws.getRequestFlow()==null){
				ws.setRequestFlow(new MessageSecurityFlow());
			}
			ws.getRequestFlow().addParameter(wsrfp);
			pde.setMessageSecurity(ws);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST;

			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);

			List<MessageSecurityFlowParameter> lista = porteDelegateCore.porteDelegateMessageSecurityRequestList(Integer.parseInt(id), ricerca);

			porteDelegateHelper.preparePorteDelegateMessageSecurityRequestList(idporta, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST, 
					ForwardParams.CHANGE());
		}
	}
}
