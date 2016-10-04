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
import org.openspcoop2.core.config.PortaApplicativa;
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
 * PorteApplicativeMTOM
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeMTOM extends Action {

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
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);

			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String id = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String mtomRichiesta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MTOM_RICHIESTA);
			String mtomRisposta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MTOM_RISPOSTA);

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();

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

			boolean visualizzazioneCompletaMTOM = porteApplicativeCore.isShowMTOMVisualizzazioneCompleta();

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
				modeMtomListRichiesta[1] =	MTOMProcessorType.UNPACKAGING.getValue();
				modeMtomListRichiesta[2] =	MTOMProcessorType.VERIFY.getValue();

				modeMtomListRisposta = new String [3];
				modeMtomListRisposta[0] = MTOMProcessorType.DISABLE.getValue();
				modeMtomListRisposta[1] = 	MTOMProcessorType.PACKAGING.getValue();
				modeMtomListRisposta[2] =	MTOMProcessorType.VERIFY.getValue();
			}

			// prelevo lo stato di MTOM
			int numMTOMreq = 0;
			int numMTOMres = 0;
			boolean isMTOMAbilitatoReq = false;
			boolean isMTOMAbilitatoRes= false;

			MTOMProcessorType mtomReqTmp = null;
			MTOMProcessorType mtomResTmp = null;

			if(pa.getMtomProcessor()!= null){
				if(pa.getMtomProcessor().getRequestFlow() != null){
					numMTOMreq = pa.getMtomProcessor().getRequestFlow().sizeParameterList();
					mtomReqTmp = pa.getMtomProcessor().getRequestFlow().getMode();
				}

				if(pa.getMtomProcessor().getResponseFlow() != null){
					numMTOMres = pa.getMtomProcessor().getResponseFlow().sizeParameterList();
					mtomResTmp = pa.getMtomProcessor().getResponseFlow().getMode();
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
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, 
								PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST,
								new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg)),
								new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DI + idporta,  null)
						);
			}else {
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MTOM_DI + idporta,  null)
						);
			}

			Parameter[] urlParms = { 
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID,id)	,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO,idsogg) };
			Parameter url1 = new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_REQUEST_LIST , urlParms);
			Parameter url2 = new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MTOM_RESPONSE_LIST , urlParms);

			if(	ServletUtils.isEditModeInProgress(request)){


				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// Parametri per la form di abilitazione
				dati = porteApplicativeHelper.addMTOMToDati(dati, modeMtomListRichiesta,modeMtomListRisposta, mtomRichiesta, mtomRisposta,
						isMTOMAbilitatoReq ? url1.getValue() : null,
								isMTOMAbilitatoRes ? url2.getValue() : null,
										contaListe, numMTOMreq, numMTOMres);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.MTOMCheckData(TipoOperazione.OTHER);
			if (!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				// Parametri per la form di abilitazione
				dati = porteApplicativeHelper.addMTOMToDati(dati, modeMtomListRichiesta, modeMtomListRisposta,mtomRichiesta, mtomRisposta,
						isMTOMAbilitatoReq ? url1.getValue() : null,
								isMTOMAbilitatoRes ? url2.getValue() : null,
										contaListe, numMTOMreq, numMTOMres);

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null,dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping,
						PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM,
						ForwardParams.OTHER(""));
			}


			// Modifico i dati della porta delegata nel db
			MTOMProcessorType nuovoValoreRichiesta = MTOMProcessorType.toEnumConstant(mtomRichiesta);
			MTOMProcessorType nuovoValoreRisposta = MTOMProcessorType.toEnumConstant(mtomRisposta);

			// se il processor e' null lo creo
			if(pa.getMtomProcessor() == null){
				MtomProcessor mtomProcessor = new MtomProcessor();
				pa.setMtomProcessor(mtomProcessor);
			}

			if(pa.getMtomProcessor().getRequestFlow() == null){
				MtomProcessorFlow requestFlow = new MtomProcessorFlow();
				pa.getMtomProcessor().setRequestFlow(requestFlow);
			}

			if(pa.getMtomProcessor().getResponseFlow() == null){
				MtomProcessorFlow responseFlow = new MtomProcessorFlow();
				pa.getMtomProcessor().setResponseFlow(responseFlow);
			}

			pa.getMtomProcessor().getRequestFlow().setMode(nuovoValoreRichiesta);
			pa.getMtomProcessor().getResponseFlow().setMode(nuovoValoreRisposta);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			// Aggiorno valori MTOM request e response
			pa = porteApplicativeCore.getPortaApplicativa(idInt);

			numMTOMreq = 0;
			numMTOMres = 0;
			isMTOMAbilitatoReq = false;
			isMTOMAbilitatoRes= false;

			if(pa.getMtomProcessor()!= null){
				if(pa.getMtomProcessor().getRequestFlow() != null){
					numMTOMreq = pa.getMtomProcessor().getRequestFlow().sizeParameterList();
					mtomReqTmp = pa.getMtomProcessor().getRequestFlow().getMode();
				}

				if(pa.getMtomProcessor().getResponseFlow() != null){
					numMTOMres = pa.getMtomProcessor().getResponseFlow().sizeParameterList();
					mtomResTmp = pa.getMtomProcessor().getResponseFlow().getMode();
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
			dati = porteApplicativeHelper.addMTOMToDati(dati, modeMtomListRichiesta,modeMtomListRisposta, mtomRichiesta, mtomRisposta, 
					isMTOMAbilitatoReq ? url1.getValue() : null,
							isMTOMAbilitatoRes ? url2.getValue() : null,
									contaListe, numMTOMreq, numMTOMres);

			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping,
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM,
					ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MTOM , 
					ForwardParams.OTHER(""));
		} 
	}

}
