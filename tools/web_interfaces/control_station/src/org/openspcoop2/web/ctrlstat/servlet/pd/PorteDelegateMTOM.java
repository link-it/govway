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
		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_USA_ID_SOGGETTO, session);


		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);

			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String mtomRichiesta = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MTOM_RICHIESTA);
			String mtomRisposta = request.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MTOM_RISPOSTA);

			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo il nome della porta
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

			if(useIdSogg){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_PORTE_DELEGATE_DI + tmpTitle, 
								PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST,
								new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg)),
								new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DI + idporta,  null)
						);
			}else {
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(PorteDelegateCostanti.LABEL_PORTE_DELEGATE, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_LIST),
						new Parameter(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MTOM_DI + idporta,  null)
						);
			}

			Parameter[] urlParms = { 
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID,id)	,
					new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO,idsogg) };
			Parameter url1 = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_REQUEST_LIST , urlParms);
			Parameter url2 = new Parameter("", PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MTOM_RESPONSE_LIST , urlParms);

			if(	ServletUtils.isEditModeInProgress(request)){


				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// Parametri per la form di abilitazione
				dati = porteDelegateHelper.addMTOMToDati(dati, modeMtomListRichiesta,modeMtomListRisposta, mtomRichiesta, mtomRisposta,
						isMTOMAbilitatoReq ? url1.getValue() : null,
								isMTOMAbilitatoRes ? url2.getValue() : null,
										contaListe, numMTOMreq, numMTOMres);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,dati);

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

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,dati);

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

			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, dati);

			pd.setDati(dati);

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
