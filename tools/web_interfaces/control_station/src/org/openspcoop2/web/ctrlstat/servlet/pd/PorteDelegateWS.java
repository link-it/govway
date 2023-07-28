/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.TrasformazioneRegola;
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

		try {
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);

			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
			Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session, request);
			if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String statoMessageSecurity = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY);
			String applicaMTOMRichiesta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM_RICHIESTA);
			String applicaMTOMRisposta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MTOM_RISPOSTA);
			String applicaModificaS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_APPLICA_MODIFICA);
			boolean applicaModifica = ServletUtils.isCheckBoxEnabled(applicaModificaS);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			String idPropertiesConfigReq = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME);
			String idPropertiesConfigRes = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME);
			
			String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// modalitaInterfaccia
			boolean isModalitaAvanzata = porteDelegateHelper.isModalitaAvanzata();

			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore aspsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(aspsCore);

			PortaDelegata pde = porteDelegateCore.getPortaDelegata(idInt);
			if(pde==null) {
				throw new Exception("PortaDelegata con id '"+idInt+"' non trovata");
			}
			String idporta = pde.getNome();
			
			AccordoServizioParteSpecifica asps = aspsCore.getAccordoServizioParteSpecifica(Long.parseLong(idAsps));
			AccordoServizioParteComuneSintetico as = apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			ServiceBinding serviceBindingAPI = apcCore.toMessageServiceBinding(as.getServiceBinding()); 

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
			String oldIdPropertiesConfigReq = null;
			String oldIdPropertiesConfigRes = null;
			if (pdeMessageSecurity != null) {
				if(pdeMessageSecurity.getRequestFlow()!=null){
					numMessageSecurityReq = pdeMessageSecurity.getRequestFlow().sizeParameterList();
					oldIdPropertiesConfigReq = pdeMessageSecurity.getRequestFlow().getMode();
				}
				if(pdeMessageSecurity.getResponseFlow()!=null){
					numMessageSecurityRes = pdeMessageSecurity.getResponseFlow().sizeParameterList();
					oldIdPropertiesConfigRes = pdeMessageSecurity.getResponseFlow().getMode();
				}
			}

			if(statoMessageSecurity == null)
				statoMessageSecurity = pde.getStatoMessageSecurity();
			
			// imposto lo stato iniziale del mode scelto
			if(idPropertiesConfigReq == null)
				idPropertiesConfigReq = oldIdPropertiesConfigReq;
			
			if(idPropertiesConfigRes == null)
				idPropertiesConfigRes = oldIdPropertiesConfigRes;
			
			if(idPropertiesConfigReq == null)
				idPropertiesConfigReq = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO;
			
			if(idPropertiesConfigRes == null)
				idPropertiesConfigRes = PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO;
			
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFrizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			Parameter pIdPropertiesConfigReq= new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_PROPERTIES_CONFIG_NAME, idPropertiesConfigReq);
			Parameter pIdPropertiesConfigRes= new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_PROPERTIES_CONFIG_NAME, idPropertiesConfigRes);
			
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY,
						pde);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_CONFIG_DI+idporta;
			}
			lstParam.add(new Parameter(labelPerPorta,  null));

			Parameter[] urlReqParms = { pId,pIdSoggetto,pIdAsps,pIdFrizione,pIdPropertiesConfigReq };
			Parameter[] urlResParms = { pId,pIdSoggetto,pIdAsps,pIdFrizione,pIdPropertiesConfigRes };
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam);

			String servletNameRequestList =  idPropertiesConfigReq.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME) 
					? PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_LIST : PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_PROPERTIES_CONFIG;
			String servletNameResponseList = idPropertiesConfigRes.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME)
					? PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_LIST  : PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_PROPERTIES_CONFIG;
			
			Parameter urlRequestList = new Parameter("", servletNameRequestList , urlReqParms);
			Parameter urlResponseList = new Parameter("", servletNameResponseList , urlResParms);
			
			PropertiesSourceConfiguration propertiesSourceConfiguration = porteDelegateCore.getMessageSecurityPropertiesSourceConfiguration();
			
			ConfigManager configManager = ConfigManager.getinstance(ControlStationCore.getLog());
			configManager.leggiConfigurazioni(propertiesSourceConfiguration, true);
			
			ServiceBinding serviceBindingSecurityTransformazioni = null;
			// se ci sono delle trasformazioni abilitate posso dover modificare il binding (Solo nelle fruizioni)
			if(pde!=null && pde.getTrasformazioni()!=null && pde.getTrasformazioni().sizeRegolaList()>0) {
				for (int i = 0; i < pde.getTrasformazioni().sizeRegolaList(); i++) {
					TrasformazioneRegola tr = pde.getTrasformazioni().getRegola(i);
					if(StatoFunzionalita.ABILITATO.equals(tr.getStato()) &&
						tr.getRichiesta()!=null) {
						if(ServiceBinding.REST.equals(serviceBindingAPI)) {
							if(tr.getRichiesta().getTrasformazioneSoap()!=null) {
								serviceBindingSecurityTransformazioni = ServiceBinding.SOAP;
								break;
							}
						}
						else {
							if(tr.getRichiesta().getTrasformazioneRest()!=null) {
								serviceBindingSecurityTransformazioni = ServiceBinding.REST;
								break;
							}
						}
					}
				}
			}
			
			List<String> nomiConfigurazioniReq = configManager.getNomiConfigurazioni(propertiesSourceConfiguration,serviceBindingAPI.name(),PorteDelegateCostanti.TAG_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST,PorteDelegateCostanti.TAG_PORTE_DELEGATE_MESSAGE_SECURITY_PD);
			if(serviceBindingSecurityTransformazioni!=null) {
				List<String> nomiConfigurazioniReqTrasformazioni = configManager.getNomiConfigurazioni(propertiesSourceConfiguration,serviceBindingSecurityTransformazioni.name(),PorteDelegateCostanti.TAG_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST,PorteDelegateCostanti.TAG_PORTE_DELEGATE_MESSAGE_SECURITY_PD);
				if(nomiConfigurazioniReqTrasformazioni!=null && !nomiConfigurazioniReqTrasformazioni.isEmpty()) {
					nomiConfigurazioniReq.addAll(nomiConfigurazioniReqTrasformazioni);
				}
			}
			List<String> labelConfigurazioniReq = configManager.convertToLabel(propertiesSourceConfiguration, nomiConfigurazioniReq);
			
			List<String> propConfigReqLabelListTmp = new ArrayList<>(); 
			propConfigReqLabelListTmp.add(PorteDelegateCostanti.LABEL_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO);
			propConfigReqLabelListTmp.addAll(labelConfigurazioniReq);
			if(porteDelegateHelper.isModalitaAvanzata())
				propConfigReqLabelListTmp.add(PorteDelegateCostanti.LABEL_DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME);
			
			
			List<String>  propConfigReqListTmp = new ArrayList<>(); 
			propConfigReqListTmp.add(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO);
			propConfigReqListTmp.addAll(nomiConfigurazioniReq);
			if(porteDelegateHelper.isModalitaAvanzata())
				propConfigReqListTmp.add(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME);
			
			
			List<String> nomiConfigurazioniRes = configManager.getNomiConfigurazioni(propertiesSourceConfiguration,serviceBindingAPI.name(),PorteDelegateCostanti.TAG_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE,PorteDelegateCostanti.TAG_PORTE_DELEGATE_MESSAGE_SECURITY_PD);
			if(serviceBindingSecurityTransformazioni!=null) {
				List<String> nomiConfigurazioniResTrasformazioni = configManager.getNomiConfigurazioni(propertiesSourceConfiguration,serviceBindingSecurityTransformazioni.name(),PorteDelegateCostanti.TAG_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE,PorteDelegateCostanti.TAG_PORTE_DELEGATE_MESSAGE_SECURITY_PD);
				if(nomiConfigurazioniResTrasformazioni!=null && !nomiConfigurazioniResTrasformazioni.isEmpty()) {
					nomiConfigurazioniRes.addAll(nomiConfigurazioniResTrasformazioni);
				}
			}
			List<String> labelConfigurazioniRes = configManager.convertToLabel(propertiesSourceConfiguration, nomiConfigurazioniRes);
			
			List<String>  propConfigResLabelListTmp = new ArrayList<>(); 
			propConfigResLabelListTmp.add(PorteDelegateCostanti.LABEL_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO);
			propConfigResLabelListTmp.addAll(labelConfigurazioniRes);
			if(porteDelegateHelper.isModalitaAvanzata())
				propConfigResLabelListTmp.add(PorteDelegateCostanti.LABEL_DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME);
			
			
			List<String>  propConfigResListTmp = new ArrayList<>();
			propConfigResListTmp.add(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO);
			propConfigResListTmp.addAll(nomiConfigurazioniRes);
			if(porteDelegateHelper.isModalitaAvanzata())
				propConfigResListTmp.add(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME);

			
			String[] propConfigReqLabelList = propConfigReqLabelListTmp.toArray(new String[propConfigReqLabelListTmp.size()]);
			String[] propConfigReqList= propConfigReqListTmp.toArray(new String[propConfigReqListTmp.size()]);
			
			String[] propConfigResLabelList= propConfigResLabelListTmp.toArray(new String[propConfigResLabelListTmp.size()]);
			String[] propConfigResList= propConfigResListTmp.toArray(new String[propConfigResListTmp.size()]);
			
			// controllo postback 
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			if(postBackElementName != null) {
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME)) {
					applicaModifica = false;
				}
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME)) {
					applicaModifica = false;
				}
				if(postBackElementName.equals(CostantiControlStation.PARAMETRO_MESSAGE_SECURITY)) {
					applicaModifica = false;
				}
			}


			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(	porteDelegateHelper.isEditModeInProgress() && !applicaModifica){

				// preparo i campi
				List<DataElement> dati = new ArrayList<>();
				dati.add(ServletUtils.getDataElementForEditModeFinished());

				if (pdeMessageSecurity != null) {
					if(pdeMessageSecurity.getRequestFlow()!=null){
						StatoFunzionalita applyToMtomRich = pdeMessageSecurity.getRequestFlow().getApplyToMtom();
						if(applicaMTOMRichiesta == null) {
							if(applyToMtomRich != null && applyToMtomRich.equals(StatoFunzionalita.ABILITATO) && isMTOMAbilitatoReq)
								applicaMTOMRichiesta = "yes";
							else
								applicaMTOMRichiesta = "";
						}
					}
					if(pdeMessageSecurity.getResponseFlow()!=null){
						StatoFunzionalita applyToMtomRich = pdeMessageSecurity.getResponseFlow().getApplyToMtom();
						if(applicaMTOMRisposta == null) {
							if(applyToMtomRich != null && applyToMtomRich.equals(StatoFunzionalita.ABILITATO) && isMTOMAbilitatoRes)
								applicaMTOMRisposta = "yes";
							else
								applicaMTOMRisposta = "";
						}
					}
				}

				if(applicaMTOMRichiesta == null){
					applicaMTOMRichiesta = "";
				}

				if(applicaMTOMRisposta == null){
					applicaMTOMRisposta = "";
				}
				
				dati = porteDelegateHelper.addMessageSecurityToDati(dati,  true, idInt, statoMessageSecurity, urlRequestList.getValue(), urlResponseList.getValue() , contaListe, numMessageSecurityReq, numMessageSecurityRes,
						isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta,idPropertiesConfigReq,idPropertiesConfigRes,propConfigReqLabelList, propConfigReqList,  propConfigResLabelList, propConfigResList,
						oldIdPropertiesConfigReq,oldIdPropertiesConfigRes);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.WSCheckData(TipoOperazione.OTHER);
			if (!isOk) {
				// preparo i campi
				List<DataElement> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				dati = porteDelegateHelper.addMessageSecurityToDati(dati, true, idInt, statoMessageSecurity, urlRequestList.getValue(), urlResponseList.getValue() , contaListe, numMessageSecurityReq, numMessageSecurityRes,
						isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta,idPropertiesConfigReq,idPropertiesConfigRes,propConfigReqLabelList, propConfigReqList,  propConfigResLabelList, propConfigResList,
						oldIdPropertiesConfigReq,oldIdPropertiesConfigRes);

				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, 
						idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

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
			String oldRequestMode = pde.getMessageSecurity().getRequestFlow().getMode();
			String oldResponseMode = pde.getMessageSecurity().getResponseFlow().getMode();
			
			String newRequestMode = !idPropertiesConfigReq.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_REQUEST_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO) ? idPropertiesConfigReq : null;
			String newResponseMode = !idPropertiesConfigRes.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MESSAGE_SECURITY_RESPONSE_FLOW_PROPERTIES_CONFIG_NAME_NESSUNO) ? idPropertiesConfigRes : null;
			
			// salvataggio della configurazione delle properties
			pde.getMessageSecurity().getRequestFlow().setMode(newRequestMode);
			pde.getMessageSecurity().getResponseFlow().setMode(newResponseMode);
			
			// se ho cambiato la modalita' di configurazione della sicurezza resetto la vecchia configurazione
			if(oldRequestMode!= null && !oldRequestMode.equals(idPropertiesConfigReq)) {
				pde.getMessageSecurity().getRequestFlow().getParameterList().clear();
			}
			if(oldResponseMode!= null && !oldResponseMode.equals(idPropertiesConfigRes)) {
				pde.getMessageSecurity().getResponseFlow().getParameterList().clear();
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

			if(!CostantiControlStation.DEFAULT_VALUE_PARAMETRO_MESSAGE_SECURITY_ABILITATO.equals(statoMessageSecurity)) {
				// Devo annullare altrimenti si possono creare inconsistenze tra link accesso alla configurazione della sicurezza e non salvataggio reale dello stato generale (se si abilita la sicurezza e precedentemente esisteva già una config per la request o response)
				pde.getMessageSecurity().setRequestFlow(null);
				pde.getMessageSecurity().setResponseFlow(null);
			}
						
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), pde);

			// preparo i campi
			List<DataElement> dati = new ArrayList<>();

			// Aggiorno valori MessageSecurity request e response
			pde = porteDelegateCore.getPortaDelegata(idInt);
			pdeMessageSecurity = pde.getMessageSecurity();

			applicaMTOMRichiesta = null;
			applicaMTOMRisposta = null;

			if (pdeMessageSecurity != null) {
				if(pdeMessageSecurity.getRequestFlow()!=null){
					numMessageSecurityReq = pdeMessageSecurity.getRequestFlow().sizeParameterList();
					oldIdPropertiesConfigReq = pdeMessageSecurity.getRequestFlow().getMode();
					StatoFunzionalita applyToMtomRich = pdeMessageSecurity.getRequestFlow().getApplyToMtom();

					if(applicaMTOMRichiesta == null) {
						if(applyToMtomRich != null && applyToMtomRich.equals(StatoFunzionalita.ABILITATO) && isMTOMAbilitatoReq)
							applicaMTOMRichiesta = "yes";
						else
							applicaMTOMRichiesta = "";
					}
				}
				if(pdeMessageSecurity.getResponseFlow()!=null){
					numMessageSecurityRes = pdeMessageSecurity.getResponseFlow().sizeParameterList();
					oldIdPropertiesConfigRes = pdeMessageSecurity.getResponseFlow().getMode();
					StatoFunzionalita applyToMtomRich = pdeMessageSecurity.getResponseFlow().getApplyToMtom();

					if(applicaMTOMRisposta ==null) {
						if(applyToMtomRich != null && applyToMtomRich.equals(StatoFunzionalita.ABILITATO) && isMTOMAbilitatoRes)
							applicaMTOMRisposta = "yes";
						else
							applicaMTOMRisposta = "";
					}
				}
			}
			
			if(applicaMTOMRichiesta == null){
				applicaMTOMRichiesta = "";
			}

			if(applicaMTOMRisposta == null){
				applicaMTOMRisposta = "";
			}

			dati = porteDelegateHelper.addMessageSecurityToDati(dati,  true, idInt, statoMessageSecurity, urlRequestList.getValue(), urlResponseList.getValue() , 
					contaListe, numMessageSecurityReq, numMessageSecurityRes,isMTOMAbilitatoReq, applicaMTOMRichiesta, isMTOMAbilitatoRes, applicaMTOMRisposta,
					idPropertiesConfigReq,idPropertiesConfigRes,propConfigReqLabelList, propConfigReqList,  propConfigResLabelList, propConfigResList,
					oldIdPropertiesConfigReq,oldIdPropertiesConfigRes);

			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, id, idsogg, null, idAsps, 
					idFruizione, pde.getTipoSoggettoProprietario(), pde.getNomeSoggettoProprietario(), dati);

			pd.setDati(dati);
			
			pd.setMessage(CostantiControlStation.LABEL_AGGIORNAMENTO_EFFETTUATO_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY,
					ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_MESSAGE_SECURITY, 
					ForwardParams.OTHER(""));
		}  
	}
}
