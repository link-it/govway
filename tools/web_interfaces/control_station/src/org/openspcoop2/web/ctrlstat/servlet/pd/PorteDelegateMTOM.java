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
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
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

/***
 * 
 * PorteDelegateMTOM
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteDelegateMTOM extends Action {

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
			String mtomRichiesta = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MTOM_RICHIESTA);
			String mtomRisposta = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MTOM_RISPOSTA);
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

			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();

			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = pde.getNome();

			boolean visualizzazioneCompletaMTOM = porteDelegateCore.isShowMTOMVisualizzazioneCompleta();

			String modeMtomListRichiesta[] = null;
			String modeMtomListRisposta[] = null;

			if(visualizzazioneCompletaMTOM){
				modeMtomListRichiesta = new String [4];
				modeMtomListRichiesta[0] = MTOMProcessorType.DISABLE.getValue();
				modeMtomListRichiesta[1] =	MTOMProcessorType.PACKAGING.getValue();
				modeMtomListRichiesta[2] = 	MTOMProcessorType.UNPACKAGING.getValue();
				modeMtomListRichiesta[3] =	MTOMProcessorType.VERIFY.getValue();

				modeMtomListRisposta = new String [4];
				modeMtomListRisposta[0] = MTOMProcessorType.DISABLE.getValue();
				modeMtomListRisposta[1] =	MTOMProcessorType.PACKAGING.getValue();
				modeMtomListRisposta[2] = 	MTOMProcessorType.UNPACKAGING.getValue();
				modeMtomListRisposta[3] =	MTOMProcessorType.VERIFY.getValue();
			}else {
				modeMtomListRichiesta = new String [3];
				modeMtomListRichiesta[0] = MTOMProcessorType.DISABLE.getValue();
				modeMtomListRichiesta[1] =	MTOMProcessorType.PACKAGING.getValue();
				modeMtomListRichiesta[2] =	MTOMProcessorType.VERIFY.getValue();

				modeMtomListRisposta = new String [3];
				modeMtomListRisposta[0] = MTOMProcessorType.DISABLE.getValue();
				modeMtomListRisposta[1] = 	MTOMProcessorType.UNPACKAGING.getValue();
				modeMtomListRisposta[2] =	MTOMProcessorType.VERIFY.getValue();
			}


			// prelevo lo stato di MTOM
			int numMTOMreq = 0;
			int numMTOMres = 0;
			boolean isMTOMAbilitatoReq = false;
			boolean isMTOMAbilitatoRes= false;

			MTOMProcessorType mtomReqTmp = null;
			MTOMProcessorType mtomResTmp = null;

			if(pde.getMtomProcessor()!= null){
				if(pde.getMtomProcessor().getRequestFlow() != null){
					numMTOMreq = pde.getMtomProcessor().getRequestFlow().sizeParameterList();
					mtomReqTmp = pde.getMtomProcessor().getRequestFlow().getMode();
				}

				if(pde.getMtomProcessor().getResponseFlow() != null){
					numMTOMres = pde.getMtomProcessor().getResponseFlow().sizeParameterList();
					mtomResTmp = pde.getMtomProcessor().getResponseFlow().getMode();
				}
			}

			if(mtomRichiesta == null){
				if(mtomReqTmp == null)
					mtomRichiesta = MTOMProcessorType.DISABLE.getValue();
				else
					mtomRichiesta = mtomReqTmp.getValue();
			}

			if(mtomRisposta == null){
				if(mtomResTmp == null)
					mtomRisposta = MTOMProcessorType.DISABLE.getValue();
				else
					mtomRisposta = mtomResTmp.getValue();
			}

			// calcolo lo stato di richiesta e risposta
			if(!mtomRichiesta.equals(MTOMProcessorType.DISABLE.getValue()) && !mtomRichiesta.equals(MTOMProcessorType.UNPACKAGING.getValue()))
				isMTOMAbilitatoReq = true;

			if(!mtomRisposta.equals(MTOMProcessorType.DISABLE.getValue()) && !mtomRisposta.equals(MTOMProcessorType.UNPACKAGING.getValue()))
				isMTOMAbilitatoRes = true;
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);

			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			lstParam.add(new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DI + idporta,  null));
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);

			Parameter[] urlParms = { pId,pIdSoggetto,pIdAsps,pIdFrizione };
			
			Parameter url1 = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_REQUEST_LIST , urlParms);
			Parameter url2 = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_RESPONSE_LIST , urlParms);

			if(	ServletUtils.isEditModeInProgress(request) && !applicaModifica){


				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// Parametri per la form di abilitazione
				dati = porteDelegateHelper.addMTOMToDati(dati, modeMtomListRichiesta,modeMtomListRisposta, mtomRichiesta, mtomRisposta,
						isMTOMAbilitatoReq ? url1.getValue() : null,
								isMTOMAbilitatoRes ? url2.getValue() : null,
										contaListe, numMTOMreq, numMTOMres);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, idFruizione, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.MTOMCheckData(TipoOperazione.OTHER);
			if (!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// Parametri per la form di abilitazione
				dati = porteDelegateHelper.addMTOMToDati(dati, modeMtomListRichiesta,modeMtomListRisposta ,mtomRichiesta, mtomRisposta,
						isMTOMAbilitatoReq ? url1.getValue() : null,
								isMTOMAbilitatoRes ? url2.getValue() : null,
										contaListe, numMTOMreq, numMTOMres);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, idFruizione, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM,
						ForwardParams.OTHER(""));
			}


			// Modifico i dati della porta delegata nel db
			MTOMProcessorType nuovoValoreRichiesta = MTOMProcessorType.toEnumConstant(mtomRichiesta);
			MTOMProcessorType nuovoValoreRisposta = MTOMProcessorType.toEnumConstant(mtomRisposta);

			// se il processor e' null lo creo
			if(pde.getMtomProcessor() == null){
				MtomProcessor mtomProcessor = new MtomProcessor();
				pde.setMtomProcessor(mtomProcessor);
			}

			if(pde.getMtomProcessor().getRequestFlow() == null){
				MtomProcessorFlow requestFlow = new MtomProcessorFlow();
				pde.getMtomProcessor().setRequestFlow(requestFlow);
			}

			if(pde.getMtomProcessor().getResponseFlow() == null){
				MtomProcessorFlow responseFlow = new MtomProcessorFlow();
				pde.getMtomProcessor().setResponseFlow(responseFlow);
			}

			pde.getMtomProcessor().getRequestFlow().setMode(nuovoValoreRichiesta);
			pde.getMtomProcessor().getResponseFlow().setMode(nuovoValoreRisposta);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			// Aggiorno valori MTOM request e response
			pde = porteDelegateCore.getPortaDelegata(idInt);

			numMTOMreq = 0;
			numMTOMres = 0;
			isMTOMAbilitatoReq = false;
			isMTOMAbilitatoRes= false;

			if(pde.getMtomProcessor()!= null){
				if(pde.getMtomProcessor().getRequestFlow() != null){
					numMTOMreq = pde.getMtomProcessor().getRequestFlow().sizeParameterList();
					mtomReqTmp = pde.getMtomProcessor().getRequestFlow().getMode();
				}

				if(pde.getMtomProcessor().getResponseFlow() != null){
					numMTOMres = pde.getMtomProcessor().getResponseFlow().sizeParameterList();
					mtomResTmp = pde.getMtomProcessor().getResponseFlow().getMode();
				}
			}

			if(mtomReqTmp == null)
				mtomRichiesta = MTOMProcessorType.DISABLE.getValue();
			else
				mtomRichiesta = mtomReqTmp.getValue();

			if(mtomResTmp == null)
				mtomRisposta = MTOMProcessorType.DISABLE.getValue();
			else
				mtomRisposta = mtomResTmp.getValue();

			// calcolo lo stato di richiesta e risposta
			if(!mtomRichiesta.equals(MTOMProcessorType.DISABLE.getValue()) && !mtomRichiesta.equals(MTOMProcessorType.UNPACKAGING.getValue()))
				isMTOMAbilitatoReq = true;

			if(!mtomRisposta.equals(MTOMProcessorType.DISABLE.getValue()) && !mtomRisposta.equals(MTOMProcessorType.UNPACKAGING.getValue()))
				isMTOMAbilitatoRes = true;

			//  Parametri per la form di abilitazione
			dati = porteDelegateHelper.addMTOMToDati(dati, modeMtomListRichiesta,modeMtomListRisposta, mtomRichiesta, mtomRisposta, 
					isMTOMAbilitatoReq ? url1.getValue() : null,
							isMTOMAbilitatoRes ? url2.getValue() : null,
									contaListe, numMTOMreq, numMTOMres);

			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, idFruizione, dati);

			pd.setDati(dati);
			
			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM,
					ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MTOM , 
					ForwardParams.OTHER(""));
		} 
	}

}
