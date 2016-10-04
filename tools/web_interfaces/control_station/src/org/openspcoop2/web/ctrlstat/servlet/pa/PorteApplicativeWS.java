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
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
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
import org.openspcoop2.web.lib.users.dao.InterfaceType;

/**
 * porteAppWS
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeWS extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		Boolean useIdSogg= ServletUtils.getBooleanAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_USA_ID_SOGGETTO, session);


		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(session); 

			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String idPorta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String statoMessageSecurity = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
			String applicaMTOMRichiesta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MTOM_RICHIESTA);
			String applicaMTOMRisposta = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MTOM_RISPOSTA);

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// modalitaInterfaccia
			boolean isModalitaAvanzata = ServletUtils.getUserFromSession(session).getInterfaceType().equals(InterfaceType.AVANZATA);
			
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
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tmpTitle = soggetto.getTipo() + "/" + soggetto.getNome();
			}
			// String pdd = soggetto.getServer();

			// Calcolo lo stato MTOM
			boolean isMTOMAbilitatoReq = false;
			boolean isMTOMAbilitatoRes= false;

			MTOMProcessorType mtomReqTmp = null;
			MTOMProcessorType mtomResTmp = null;

			if(pa.getMtomProcessor()!= null){
				if(pa.getMtomProcessor().getRequestFlow() != null){
					mtomReqTmp = pa.getMtomProcessor().getRequestFlow().getMode();
				}

				if(pa.getMtomProcessor().getResponseFlow() != null){
					mtomResTmp = pa.getMtomProcessor().getResponseFlow().getMode();
				}
			}

			// calcolo lo stato di richiesta e risposta
			if(isModalitaAvanzata && mtomReqTmp!= null && !mtomReqTmp.equals(MTOMProcessorType.DISABLE) && !mtomReqTmp.equals(MTOMProcessorType.VERIFY))
				isMTOMAbilitatoReq = true;

			if(isModalitaAvanzata && mtomResTmp!= null && !mtomResTmp.equals(MTOMProcessorType.DISABLE ) && !mtomResTmp.equals(MTOMProcessorType.VERIFY))
				isMTOMAbilitatoRes = true;

			// Conto i ws-request ed i ws-response
			MessageSecurity paMessageSecurity = pa.getMessageSecurity();
			int numMessageSecurityReq = 0;
			int numMessageSecurityRes = 0;
			if (paMessageSecurity != null) {
				if(paMessageSecurity.getRequestFlow()!=null){
					numMessageSecurityReq = paMessageSecurity.getRequestFlow().sizeParameterList();
				}
				if(paMessageSecurity.getResponseFlow()!=null){
					numMessageSecurityRes = paMessageSecurity.getResponseFlow().sizeParameterList();
				}
			}

			if(statoMessageSecurity == null)
				statoMessageSecurity = pa.getStatoMessageSecurity();



			if(useIdSogg){
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, 
								PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)),
								new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_DI  + idporta, null)
						);
			}else {
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE, null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_DI  + idporta, null)
						);	
			}

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(request)) {


				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, idPorta, idsogg, idPorta, dati);

				Parameter url1 = new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID	,idPorta),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO	,idsogg)
						);

				Parameter url2 = new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID	,idPorta),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO	,idsogg)
						);

				
				if (paMessageSecurity != null) {
					if(paMessageSecurity.getRequestFlow()!=null){
						StatoFunzionalita applyToMtomRich = paMessageSecurity.getRequestFlow().getApplyToMtom();
						
						if(applicaMTOMRichiesta == null)
							if(applyToMtomRich != null && applyToMtomRich.equals(StatoFunzionalita.ABILITATO) && isMTOMAbilitatoReq)
								applicaMTOMRichiesta = "yes";
							else
								applicaMTOMRichiesta = "";
					}
					if(paMessageSecurity.getResponseFlow()!=null){
						StatoFunzionalita applyToMtomRich = paMessageSecurity.getResponseFlow().getApplyToMtom();
						
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
				
				
				dati = porteApplicativeHelper.addMessageSecurityToDati(dati, statoMessageSecurity, url1.getValue(), url2.getValue(), contaListe, numMessageSecurityReq, numMessageSecurityRes,
						isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,
						ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.WSCheckData(TipoOperazione.OTHER);
			if (!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, idPorta, idsogg, null, dati);

				Parameter url1 = new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID	,idPorta),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO	,idsogg)
						);

				Parameter url2 = new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_LIST,
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID	,idPorta),
						new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO	,idsogg)
						);

				dati = porteApplicativeHelper.addMessageSecurityToDati(dati,   statoMessageSecurity, url1.getValue(), url2.getValue(), contaListe, numMessageSecurityReq, numMessageSecurityRes,
						isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY, 
						ForwardParams.OTHER(""));
			}

			// Modifico i dati della porta applicativa nel db
			pa.setStatoMessageSecurity(statoMessageSecurity);

			if (pa.getMessageSecurity() == null)
				pa.setMessageSecurity(new MessageSecurity());

			if(pa.getMessageSecurity().getRequestFlow()==null){
				pa.getMessageSecurity().setRequestFlow(new MessageSecurityFlow());
			}
			if(pa.getMessageSecurity().getResponseFlow() ==null){
				pa.getMessageSecurity().setResponseFlow(new MessageSecurityFlow());
			}

			if(isMTOMAbilitatoReq){
				if(applicaMTOMRichiesta != null && (ServletUtils.isCheckBoxEnabled(applicaMTOMRichiesta))){
					pa.getMessageSecurity().getRequestFlow().setApplyToMtom(StatoFunzionalita.ABILITATO);
				}else {
					pa.getMessageSecurity().getRequestFlow().setApplyToMtom(StatoFunzionalita.DISABILITATO);
				}
			}else 
				pa.getMessageSecurity().getRequestFlow().setApplyToMtom(null);

			if(isMTOMAbilitatoRes){
				if(applicaMTOMRisposta  != null && (ServletUtils.isCheckBoxEnabled(applicaMTOMRisposta))){
					pa.getMessageSecurity().getResponseFlow().setApplyToMtom(StatoFunzionalita.ABILITATO);
				}else {
					pa.getMessageSecurity().getResponseFlow().setApplyToMtom(StatoFunzionalita.DISABILITATO);
				}
			}else 
				pa.getMessageSecurity().getResponseFlow().setApplyToMtom(null);

			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, idPorta, idsogg, null, dati);

			// Aggiorno valori MessageSecurity request e response
			pa = porteApplicativeCore.getPortaApplicativa(idInt);
			
			paMessageSecurity = pa.getMessageSecurity();

			applicaMTOMRichiesta = null;
			applicaMTOMRisposta = null;

			if (paMessageSecurity != null) {
				if(paMessageSecurity.getRequestFlow()!=null){
					numMessageSecurityReq = paMessageSecurity.getRequestFlow().sizeParameterList();
					StatoFunzionalita applyToMtomRich = paMessageSecurity.getRequestFlow().getApplyToMtom();

					if(applicaMTOMRichiesta == null)
					if(applyToMtomRich != null && applyToMtomRich.equals(StatoFunzionalita.ABILITATO) && isMTOMAbilitatoReq)
						applicaMTOMRichiesta = "yes";
					else
						applicaMTOMRichiesta = "";
				}
				if(paMessageSecurity.getResponseFlow()!=null){
					numMessageSecurityRes = paMessageSecurity.getResponseFlow().sizeParameterList();
					StatoFunzionalita applyToMtomRich = paMessageSecurity.getResponseFlow().getApplyToMtom();

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
		 
			Parameter url1 = new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_LIST,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID	,idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO	,idsogg)
					);

			Parameter url2 = new Parameter("", PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_LIST,
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID	,idPorta),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO	,idsogg)
					);

			dati = porteApplicativeHelper.addMessageSecurityToDati(dati,   statoMessageSecurity, url1.getValue(), url2.getValue(), 
					contaListe, numMessageSecurityReq, numMessageSecurityRes,isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY, 
					ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,
					ForwardParams.OTHER(""));
		}  
	}
}
