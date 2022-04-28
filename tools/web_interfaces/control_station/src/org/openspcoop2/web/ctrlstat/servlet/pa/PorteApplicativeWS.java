/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.mvc.properties.utils.ConfigManager;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

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

		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(session); 

			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(idPorta);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String statoMessageSecurity = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY);
			String applicaMTOMRichiesta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MTOM_RICHIESTA);
			String applicaMTOMRisposta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MTOM_RISPOSTA);
			String applicaModificaS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MODIFICA);
			boolean applicaModifica = ServletUtils.isCheckBoxEnabled(applicaModificaS);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idPropertiesConfigReq = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME);
			String idPropertiesConfigRes = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME);
			
			String idTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// modalitaInterfaccia
			boolean isModalitaAvanzata = porteApplicativeHelper.isModalitaAvanzata();
			
			// Prendo il nome della porta
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(aspsCore);

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();
			
			AccordoServizioParteSpecifica asps = aspsCore.getAccordoServizioParteSpecifica(Long.parseLong(idAsps));
			AccordoServizioParteComuneSintetico as = apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			ServiceBinding serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding()); 

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
			String oldIdPropertiesConfigReq = null;
			String oldIdPropertiesConfigRes = null;
			if (paMessageSecurity != null) {
				if(paMessageSecurity.getRequestFlow()!=null){
					numMessageSecurityReq = paMessageSecurity.getRequestFlow().sizeParameterList();
					oldIdPropertiesConfigReq = paMessageSecurity.getRequestFlow().getMode();
				}
				if(paMessageSecurity.getResponseFlow()!=null){
					numMessageSecurityRes = paMessageSecurity.getResponseFlow().sizeParameterList();
					oldIdPropertiesConfigRes = paMessageSecurity.getResponseFlow().getMode();
				}
			}

			if(statoMessageSecurity == null)
				statoMessageSecurity = pa.getStatoMessageSecurity();
			
			// imposto lo stato iniziale del mode scelto
			if(idPropertiesConfigReq == null)
				idPropertiesConfigReq = oldIdPropertiesConfigReq;
			
			if(idPropertiesConfigRes == null)
				idPropertiesConfigRes = oldIdPropertiesConfigRes;
			
			if(idPropertiesConfigReq == null)
				idPropertiesConfigReq = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO;
			
			if(idPropertiesConfigRes == null)
				idPropertiesConfigRes = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO;
			
			Parameter pId = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID	,idPorta);
			Parameter pIdSoggetto = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO	,idsogg);
			Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS,idAsps);
			Parameter pIdPropertiesConfigReq= new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_PROPERTIES_CONFIG_NAME, idPropertiesConfigReq);
			Parameter pIdPropertiesConfigRes= new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_PROPERTIES_CONFIG_NAME, idPropertiesConfigRes);

			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,  null));
			
			Parameter[] urlReqParms = { pId,pIdSoggetto,pIdAsps,pIdPropertiesConfigReq };
			Parameter[] urlResParms = { pId,pIdSoggetto,pIdAsps,pIdPropertiesConfigRes };
			
			String servletNameRequestList =  idPropertiesConfigReq.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME) 
					? PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_LIST : PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_PROPERTIES_CONFIG;
			String servletNameResponseList = idPropertiesConfigRes.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME)
					? PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_LIST  : PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_PROPERTIES_CONFIG;
			
			Parameter urlRequestList = new Parameter("", servletNameRequestList , urlReqParms);
			Parameter urlResponseList = new Parameter("", servletNameResponseList , urlResParms);
			
			PropertiesSourceConfiguration propertiesSourceConfiguration = porteApplicativeCore.getMessageSecurityPropertiesSourceConfiguration();
			
			ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
			configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);
			
			List<String> nomiConfigurazioniReq = configManager.getNomiConfigurazioni(propertiesSourceConfiguration,serviceBinding.name(),PorteApplicativeCostanti.TAG_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST,PorteApplicativeCostanti.TAG_PORTE_APPLICATIVE_MESSAGE_SECURITY_PA);
			List<String> labelConfigurazioniReq = configManager.convertToLabel(propertiesSourceConfiguration, nomiConfigurazioniReq);
			
			List<String> propConfigReqLabelListTmp = new ArrayList<String>(); 
			propConfigReqLabelListTmp.add(PorteApplicativeCostanti.LABEL_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO);
			propConfigReqLabelListTmp.addAll(labelConfigurazioniReq);
			if(porteApplicativeHelper.isModalitaAvanzata())
				propConfigReqLabelListTmp.add(PorteApplicativeCostanti.LABEL_DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME);
			
			
			List<String>  propConfigReqListTmp = new ArrayList<String>(); 
			propConfigReqListTmp.add(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO);
			propConfigReqListTmp.addAll(nomiConfigurazioniReq);
			if(porteApplicativeHelper.isModalitaAvanzata())
				propConfigReqListTmp.add(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME);
			
			List<String> nomiConfigurazioniRes = configManager.getNomiConfigurazioni(propertiesSourceConfiguration,serviceBinding.name(),PorteApplicativeCostanti.TAG_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE,PorteApplicativeCostanti.TAG_PORTE_APPLICATIVE_MESSAGE_SECURITY_PA);
			List<String> labelConfigurazioniRes = configManager.convertToLabel(propertiesSourceConfiguration, nomiConfigurazioniRes);
			
			List<String>  propConfigResLabelListTmp = new ArrayList<String>(); 
			propConfigResLabelListTmp.add(PorteApplicativeCostanti.LABEL_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO);
			propConfigResLabelListTmp.addAll(labelConfigurazioniRes);
			if(porteApplicativeHelper.isModalitaAvanzata())
				propConfigResLabelListTmp.add(PorteApplicativeCostanti.LABEL_DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME);
			
			
			List<String>  propConfigResListTmp = new ArrayList<String>();
			propConfigResListTmp.add(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO);
			propConfigResListTmp.addAll(nomiConfigurazioniRes);
			if(porteApplicativeHelper.isModalitaAvanzata())
				propConfigResListTmp.add(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME);
			
			String[] propConfigReqLabelList = propConfigReqLabelListTmp.toArray(new String[propConfigReqLabelListTmp.size()]);
			String[] propConfigReqList= propConfigReqListTmp.toArray(new String[propConfigReqListTmp.size()]);
			
			String[] propConfigResLabelList= propConfigResLabelListTmp.toArray(new String[propConfigResLabelListTmp.size()]);
			String[] propConfigResList= propConfigResListTmp.toArray(new String[propConfigResListTmp.size()]);
			
			// controllo postback 
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			if(postBackElementName != null) {
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME)) {
					applicaModifica = false;
				}
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME)) {
					applicaModifica = false;
				}
				if(postBackElementName.equals(CostantiControlStation.PARAMETRO_MESSAGE_SECURITY)) {
					applicaModifica = false;
				}
			}
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);

			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (porteApplicativeHelper.isEditModeInProgress() && !applicaModifica) {


				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, idPorta, idsogg, idPorta,  idAsps, dati);

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
				
				
				dati = porteApplicativeHelper.addMessageSecurityToDati(dati, false, idInt, statoMessageSecurity, urlRequestList.getValue(), urlResponseList.getValue(), contaListe, numMessageSecurityReq, numMessageSecurityRes,
						isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta,idPropertiesConfigReq,idPropertiesConfigRes,propConfigReqLabelList, propConfigReqList,  propConfigResLabelList, propConfigResList,
						oldIdPropertiesConfigReq,oldIdPropertiesConfigRes);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,
						ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.WSCheckData(TipoOperazione.OTHER);
			if (!isOk) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, idPorta, idsogg, null, idAsps, dati);

				dati = porteApplicativeHelper.addMessageSecurityToDati(dati,  false, idInt,  statoMessageSecurity, urlRequestList.getValue(), urlResponseList.getValue(), contaListe, numMessageSecurityReq, numMessageSecurityRes,
						isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta,idPropertiesConfigReq,idPropertiesConfigRes,propConfigReqLabelList, propConfigReqList,  propConfigResLabelList, propConfigResList,
						oldIdPropertiesConfigReq,oldIdPropertiesConfigRes);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

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
			
			String oldRequestMode = pa.getMessageSecurity().getRequestFlow().getMode();
			String oldResponseMode = pa.getMessageSecurity().getResponseFlow().getMode();
			
			String newRequestMode = !idPropertiesConfigReq.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO) ? idPropertiesConfigReq : null;
			String newResponseMode = !idPropertiesConfigRes.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO) ? idPropertiesConfigRes : null;
			
			// salvataggio della configurazione delle properties
			pa.getMessageSecurity().getRequestFlow().setMode(newRequestMode);
			pa.getMessageSecurity().getResponseFlow().setMode(newResponseMode);
			
			// se ho cambiato la modalita' di configurazione della sicurezza resetto la vecchia configurazione
			if(oldRequestMode!= null && !oldRequestMode.equals(idPropertiesConfigReq)) {
				pa.getMessageSecurity().getRequestFlow().getParameterList().clear();
			}
			if(oldResponseMode!= null && !oldResponseMode.equals(idPropertiesConfigRes)) {
				pa.getMessageSecurity().getResponseFlow().getParameterList().clear();
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

			if(!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_ABILITATO.equals(statoMessageSecurity)) {
				// Devo annullare altrimenti si possono creare inconsistenze tra link accesso alla configurazione della sicurezza e non salvataggio reale dello stato generale (se si abilita la sicurezza e precedentemente esisteva già una config per la request o response)
				pa.getMessageSecurity().setRequestFlow(null);
				pa.getMessageSecurity().setResponseFlow(null);
			}
			
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
					oldIdPropertiesConfigReq = paMessageSecurity.getRequestFlow().getMode();
					StatoFunzionalita applyToMtomRich = paMessageSecurity.getRequestFlow().getApplyToMtom();

					if(applicaMTOMRichiesta == null)
					if(applyToMtomRich != null && applyToMtomRich.equals(StatoFunzionalita.ABILITATO) && isMTOMAbilitatoReq)
						applicaMTOMRichiesta = "yes";
					else
						applicaMTOMRichiesta = "";
				}
				if(paMessageSecurity.getResponseFlow()!=null){
					numMessageSecurityRes = paMessageSecurity.getResponseFlow().sizeParameterList();
					oldIdPropertiesConfigRes = paMessageSecurity.getResponseFlow().getMode();
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
		 
			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, idPorta, idsogg, null, idAsps, dati);
			
			dati = porteApplicativeHelper.addMessageSecurityToDati(dati, false, idInt,   statoMessageSecurity, urlRequestList.getValue(), urlResponseList.getValue(), 
					contaListe, numMessageSecurityReq, numMessageSecurityRes,isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta,
					idPropertiesConfigReq,idPropertiesConfigRes,propConfigReqLabelList, propConfigReqList,  propConfigResLabelList, propConfigResList,
					oldIdPropertiesConfigReq,oldIdPropertiesConfigRes);

			pd.setDati(dati);

			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			//pd.disableEditMode();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY, 
					ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_MESSAGE_SECURITY,
					ForwardParams.OTHER(""));
		}  
	}
}
