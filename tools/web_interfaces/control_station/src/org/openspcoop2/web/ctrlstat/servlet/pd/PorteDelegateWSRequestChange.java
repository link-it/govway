/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessageSecurityFlowParameter;
import org.openspcoop2.core.config.PortaDelegata;
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
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String nome = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME);
			String valore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_VALORE);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo il nome della porta applicativa
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();

			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = pde.getNome();

			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			Parameter[] urlParms = { pId,pIdSoggetto,pIdAsps,pIdFrizione };
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_CONFIG_DI+
						porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(pde);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta, 
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, urlParms));
			
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_DI, // + idporta,
					PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_LIST, urlParms  
			));
			
			lstParam.add(new Parameter(nome, null));
			
			// Se valore = null, devo visualizzare la pagina per la
			// modifica dati
			//			if (valore == null) {
			if(	porteDelegateHelper.isEditModeInProgress()){
				// setto la barra del titolo

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

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, idFruizione, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.porteDelegateMessageSecurityRequestCheckData(TipoOperazione.CHANGE);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addNomeValoreToDati(TipoOperazione.CHANGE,dati, nome, valore,false);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, idFruizione, dati);

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
