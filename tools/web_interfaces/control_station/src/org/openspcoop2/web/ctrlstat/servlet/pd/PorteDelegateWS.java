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
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteDelegateWS
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteDelegateWS extends Action {

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
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);

			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String statoMessageSecurity = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY);
			String applicaMTOMRichiesta = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM_RICHIESTA);
			String applicaMTOMRisposta = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM_RISPOSTA);
			String applicaModificaS = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MODIFICA);
			boolean applicaModifica = ServletUtils.isCheckBoxEnabled(applicaModificaS);
			String idAsps = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// modalitaInterfaccia
			boolean isModalitaAvanzata = porteDelegateHelper.isModalitaAvanzata();

			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();

			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = pde.getNome();

			// Calcolo lo stato MTOM
			boolean isMTOMAbilitatoReq = false;
			boolean isMTOMAbilitatoRes= false;

			MTOMProcessorType mtomReqTmp = null;
			MTOMProcessorType mtomResTmp = null;

			if(pde.getMtomProcessor()!= null){
				if(pde.getMtomProcessor().getRequestFlow() != null){
					mtomReqTmp = pde.getMtomProcessor().getRequestFlow().getMode();
				}

				if(pde.getMtomProcessor().getResponseFlow() != null){
					mtomResTmp = pde.getMtomProcessor().getResponseFlow().getMode();
				}
			}

			// calcolo lo stato di richiesta e risposta
			if(isModalitaAvanzata && mtomReqTmp!= null && !mtomReqTmp.equals(MTOMProcessorType.DISABLE) && !mtomReqTmp.equals(MTOMProcessorType.VERIFY))
				isMTOMAbilitatoReq = true;

			if(isModalitaAvanzata && mtomResTmp!= null && !mtomResTmp.equals(MTOMProcessorType.DISABLE ) && !mtomResTmp.equals(MTOMProcessorType.VERIFY))
				isMTOMAbilitatoRes = true;

			// Conto i ws-request ed i ws-response
			MessageSecurity pdeMessageSecurity = pde.getMessageSecurity();
			int numMessageSecurityReq = 0;
			int numMessageSecurityRes = 0;
			if (pdeMessageSecurity != null) {
				if(pdeMessageSecurity.getRequestFlow()!=null){
					numMessageSecurityReq = pdeMessageSecurity.getRequestFlow().sizeParameterList();
				}
				if(pdeMessageSecurity.getResponseFlow()!=null){
					numMessageSecurityRes = pdeMessageSecurity.getResponseFlow().sizeParameterList();
				}
			}

			if(statoMessageSecurity == null)
				statoMessageSecurity = pde.getStatoMessageSecurity();
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_DI + idporta,  null));

			Parameter[] urlParms = { pId,pIdSoggetto,pIdAsps,pIdFrizione };
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);

			Parameter url1 = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_LIST , urlParms);
			Parameter url2 = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_LIST , urlParms);

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(	ServletUtils.isEditModeInProgress(request) && !applicaModifica){

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if (pdeMessageSecurity != null) {
					if(pdeMessageSecurity.getRequestFlow()!=null){
						StatoFunzionalita applyToMtomRich = pdeMessageSecurity.getRequestFlow().getApplyToMtom();
						if(applicaMTOMRichiesta == null)
							if(applyToMtomRich != null && applyToMtomRich.equals(StatoFunzionalita.ABILITATO) && isMTOMAbilitatoReq)
								applicaMTOMRichiesta = "yes";
							else
								applicaMTOMRichiesta = "";
					}
					if(pdeMessageSecurity.getResponseFlow()!=null){
						StatoFunzionalita applyToMtomRich = pdeMessageSecurity.getResponseFlow().getApplyToMtom();
						if(applicaMTOMRisposta == null)
							if(applyToMtomRich != null && applyToMtomRich.equals(StatoFunzionalita.ABILITATO) && isMTOMAbilitatoRes)
								applicaMTOMRisposta = "yes";
							else
								applicaMTOMRisposta = "";
					}
				}

				if(applicaMTOMRichiesta == null){
					applicaMTOMRichiesta = "";
				}

				if(applicaMTOMRisposta == null){
					applicaMTOMRisposta = "";
				}
				
				dati = porteDelegateHelper.addMessageSecurityToDati(dati,  statoMessageSecurity, url1.getValue(), url2.getValue() , contaListe, numMessageSecurityReq, numMessageSecurityRes,
						isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, idFruizione, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.WSCheckData(TipoOperazione.OTHER);
			if (!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addMessageSecurityToDati(dati, statoMessageSecurity, url1.getValue(), url2.getValue() , contaListe, numMessageSecurityReq, numMessageSecurityRes,
						isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, idFruizione, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY,
						ForwardParams.OTHER(""));
			}

			// Modifico i dati della porta delegata nel db
			pde.setStatoMessageSecurity(statoMessageSecurity);

			if (pde.getMessageSecurity() == null)
				pde.setMessageSecurity(new MessageSecurity());

			if(pde.getMessageSecurity().getRequestFlow()==null){
				pde.getMessageSecurity().setRequestFlow(new MessageSecurityFlow());
			}
			if(pde.getMessageSecurity().getResponseFlow() ==null){
				pde.getMessageSecurity().setResponseFlow(new MessageSecurityFlow());
			}

			if(isMTOMAbilitatoReq){
				if(applicaMTOMRichiesta != null && (ServletUtils.isCheckBoxEnabled(applicaMTOMRichiesta))){
					pde.getMessageSecurity().getRequestFlow().setApplyToMtom(StatoFunzionalita.ABILITATO);
				}else {
					pde.getMessageSecurity().getRequestFlow().setApplyToMtom(StatoFunzionalita.DISABILITATO);
				}
			}else 
				pde.getMessageSecurity().getRequestFlow().setApplyToMtom(null);

			if(isMTOMAbilitatoRes){
				if(applicaMTOMRisposta  != null && (ServletUtils.isCheckBoxEnabled(applicaMTOMRisposta))){
					pde.getMessageSecurity().getResponseFlow().setApplyToMtom(StatoFunzionalita.ABILITATO);
				}else {
					pde.getMessageSecurity().getResponseFlow().setApplyToMtom(StatoFunzionalita.DISABILITATO);
				}
			}else 
				pde.getMessageSecurity().getResponseFlow().setApplyToMtom(null);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			// Aggiorno valori MessageSecurity request e response
			pde = porteDelegateCore.getPortaDelegata(idInt);
			pdeMessageSecurity = pde.getMessageSecurity();

			applicaMTOMRichiesta = null;
			applicaMTOMRisposta = null;

			if (pdeMessageSecurity != null) {
				if(pdeMessageSecurity.getRequestFlow()!=null){
					numMessageSecurityReq = pdeMessageSecurity.getRequestFlow().sizeParameterList();
					StatoFunzionalita applyToMtomRich = pdeMessageSecurity.getRequestFlow().getApplyToMtom();

					if(applicaMTOMRichiesta == null)
					if(applyToMtomRich != null && applyToMtomRich.equals(StatoFunzionalita.ABILITATO) && isMTOMAbilitatoReq)
						applicaMTOMRichiesta = "yes";
					else
						applicaMTOMRichiesta = "";
				}
				if(pdeMessageSecurity.getResponseFlow()!=null){
					numMessageSecurityRes = pdeMessageSecurity.getResponseFlow().sizeParameterList();
					StatoFunzionalita applyToMtomRich = pdeMessageSecurity.getResponseFlow().getApplyToMtom();

					if(applicaMTOMRisposta ==null)
					if(applyToMtomRich != null && applyToMtomRich.equals(StatoFunzionalita.ABILITATO) && isMTOMAbilitatoRes)
						applicaMTOMRisposta = "yes";
					else
						applicaMTOMRisposta = "";
				}
			}
			
			if(applicaMTOMRichiesta == null){
				applicaMTOMRichiesta = "";
			}

			if(applicaMTOMRisposta == null){
				applicaMTOMRisposta = "";
			}

			dati = porteDelegateHelper.addMessageSecurityToDati(dati,  statoMessageSecurity, url1.getValue(), url2.getValue() , 
					contaListe, numMessageSecurityReq, numMessageSecurityRes,isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta);

			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, idFruizione, dati);

			pd.setDati(dati);
			
			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY,
					ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, 
					ForwardParams.OTHER(""));
		}  
	}
}
