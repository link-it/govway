/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

import java.util.ArrayList;
import java.util.Arrays;
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
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.TrasformazioneSoapRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteDelegateTrasformazioniRispostaChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateTrasformazioniRispostaChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;
		
		try {
			
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			long idInt = Long.parseLong(id);
			String idsogg = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null)
				idAsps = "";
			
			String idFruizione = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null)
				idFruizione = "";
			
			
			String first = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_FIRST);
			String nomeRisposta = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_NOME);
			
			String returnCode = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS);
			
			String statusMin = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MIN);
			if(statusMin == null)
				statusMin = "";
			String statusMax = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MAX);
			if(statusMax == null)
				statusMax = "";
			String pattern = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_PATTERN);
			String contentType = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_CT);
			
			String idTrasformazioneS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);
			
			String idTrasformazioneRispostaS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE_RISPOSTA);
			long idTrasformazioneRisposta = Long.parseLong(idTrasformazioneRispostaS);
			
			String trasformazioneContenutoRispostaAbilitatoS  = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_ENABLED);
			boolean trasformazioneContenutoRispostaAbilitato = trasformazioneContenutoRispostaAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneContenutoRispostaAbilitatoS) : false;
			String trasformazioneContenutoRispostaTipoS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO);
			org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneContenutoRispostaTipo = 
					trasformazioneContenutoRispostaTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneContenutoRispostaTipoS) : 
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
			BinaryParameter trasformazioneContenutoRispostaTemplate = porteDelegateHelper.getBinaryParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE);
			String trasformazioneContenutoRispostaTipoCheck = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO_CHECK);
			String trasformazioneContenutoRispostaContentType = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE);
			String trasformazioneContenutoRispostaReturnCode = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_RETURN_CODE);
			String trasformazioneRispostaSoapAbilitatoS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_SOAP_TRANSFORMATION); 
			boolean trasformazioneRispostaSoapAbilitato = trasformazioneRispostaSoapAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneRispostaSoapAbilitatoS) : false;
			String trasformazioneRispostaSoapEnvelope = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE);
			String trasformazioneRispostaSoapEnvelopeTipoS = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO);
			org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneRispostaSoapEnvelopeTipo =
					trasformazioneRispostaSoapEnvelopeTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneRispostaSoapEnvelopeTipoS) : 
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
			BinaryParameter trasformazioneRispostaSoapEnvelopeTemplate = porteDelegateHelper.getBinaryParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE);
			String trasformazioneRispostaSoapEnvelopeTipoCheck = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO_CHECK);
			
			Parameter pId = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, id);
			Parameter pIdSoggetto = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, idsogg);
			Parameter pIdAsps = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS, idAsps);
			Parameter pIdFruizione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE, idFruizione);
			
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteDelegateCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteDelegateCore);
			// Preparo il menu
			porteDelegateHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String nomePorta = portaDelegata.getNome();
			
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComune apc = apcCore.getAccordoServizio(asps.getIdAccordo()); 
			ServiceBinding serviceBindingMessage = apcCore.toMessageServiceBinding(apc.getServiceBinding());
			
			Trasformazioni trasformazioni = portaDelegata.getTrasformazioni();
			TrasformazioneRegola oldRegola = null;
			TrasformazioneRegolaRisposta oldRisposta = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					oldRegola = reg;
					break;
				}
			}
			
			for (int j = 0; j < oldRegola.sizeRispostaList(); j++) {
				TrasformazioneRegolaRisposta risposta = oldRegola.getRisposta(j);
				if (risposta.getId().longValue() == idTrasformazioneRisposta) {
					oldRisposta = risposta;
					break;
				}
			}
			
			
			boolean trasformazioneContenutoRichiestaAbilitato = false;
			boolean trasformazioneRichiestaRestAbilitato = false;
			if(oldRegola.getRichiesta() != null) {
				trasformazioneContenutoRichiestaAbilitato = oldRegola.getRichiesta().getConversione();
				trasformazioneRichiestaRestAbilitato = oldRegola.getRichiesta().getTrasformazioneRest() != null;
			}
			
			String nomeRispostaTitle = oldRisposta.getNome();
			String nomeTrasformazione = oldRegola.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE, idTrasformazione+"");
			
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			
			if (postBackElementName != null) {
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS)) {
					statusMin = "";
					statusMax = "";
				}
				
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_ENABLED)) {
					trasformazioneContenutoRispostaTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					trasformazioneContenutoRispostaContentType = "";
					trasformazioneContenutoRispostaReturnCode = "";
					trasformazioneRispostaSoapAbilitato = false;
					trasformazioneRispostaSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
					trasformazioneRispostaSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					
					porteDelegateHelper.deleteBinaryParameters(trasformazioneContenutoRispostaTemplate, trasformazioneRispostaSoapEnvelopeTemplate);
				}
				
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO)) {
					trasformazioneContenutoRispostaTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO;
					porteDelegateHelper.deleteBinaryParameters(trasformazioneContenutoRispostaTemplate);
				}
				
				if(postBackElementName.equals(trasformazioneContenutoRispostaTemplate.getName())) {
					if(StringUtils.isEmpty(trasformazioneContenutoRispostaTipoCheck))
						trasformazioneContenutoRispostaTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
				}
				
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_SOAP_TRANSFORMATION)) {
					porteDelegateHelper.deleteBinaryParameters(trasformazioneRispostaSoapEnvelopeTemplate);
					if(trasformazioneRispostaSoapAbilitato) {
						trasformazioneRispostaSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
						trasformazioneRispostaSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					} 
				}
				
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE)) {
					porteDelegateHelper.deleteBinaryParameters(trasformazioneRispostaSoapEnvelopeTemplate);
					if(trasformazioneRispostaSoapEnvelope.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT)) {
						trasformazioneRispostaSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY; 
					}
				}
				
				if(postBackElementName.equals(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO)) {
					trasformazioneRispostaSoapEnvelopeTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO;
					porteDelegateHelper.deleteBinaryParameters(trasformazioneRispostaSoapEnvelopeTemplate);
				}
				
				if(postBackElementName.equals(trasformazioneRispostaSoapEnvelopeTemplate.getName())) {
					if(StringUtils.isEmpty(trasformazioneRispostaSoapEnvelopeTipoCheck))
						trasformazioneRispostaSoapEnvelopeTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
				}
			}
			
			// parametri visualizzazione link
			String servletTrasformazioniRispostaHeadersList = PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_HEADER_LIST;
			int numeroTrasformazioniRispostaHeaders = oldRisposta.sizeHeaderList();
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRispostaHeaders = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(pId);
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(pIdFruizione);
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(pIdTrasformazione);
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(new Parameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_TRASFORMAZIONE_RISPOSTA, idTrasformazioneRispostaS));
			

			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idsogg, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_LIST, pId, pIdSoggetto, pIdAsps, pIdFruizione));
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_CHANGE, 
					pId, pIdSoggetto, pIdAsps, pIdFruizione, pIdTrasformazione));
			
			String labelPag = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE;
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRisposta = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRisposta.add(pId);
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdSoggetto);
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdAsps);
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdFruizione);
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdTrasformazione);
			
			lstParam.add(new Parameter(labelPag,PorteDelegateCostanti.SERVLET_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA_LIST,parametriInvocazioneServletTrasformazioniRisposta));
			
			lstParam.add(new Parameter(nomeRispostaTitle, null));

			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteDelegateHelper.isEditModeInProgress()) {
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				// primo accesso
				if(first == null) {
					
					nomeRisposta = oldRisposta.getNome();
					
					TrasformazioneRegolaApplicabilitaRisposta applicabilita = oldRisposta.getApplicabilita();
					
					Integer statusMinInteger = applicabilita != null ? applicabilita.getReturnCodeMin() : null;
					Integer statusMaxInteger = applicabilita != null ? applicabilita.getReturnCodeMax() : null;
					
					if(statusMinInteger != null) {
						statusMin = statusMinInteger +"";
					}
					
					if(statusMaxInteger != null) {
						statusMax = statusMaxInteger +"";
					}
					
					// se e' stato salvato il valore 0 lo tratto come null
					if(statusMinInteger != null && statusMinInteger.intValue() <= 0) {
						statusMinInteger = null;
					}
					
					if(statusMaxInteger != null && statusMaxInteger.intValue() <= 0) {
						statusMaxInteger = null;
					}
					
					// Intervallo
					if(statusMinInteger != null && statusMaxInteger != null) {
						if(statusMaxInteger.longValue() == statusMinInteger.longValue()) // esatto
							returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_ESATTO;
						else 
							returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_INTERVALLO;
					} else if(statusMinInteger != null && statusMaxInteger == null) { // definito solo l'estremo inferiore
						returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_INTERVALLO;
					} else if(statusMinInteger == null && statusMaxInteger != null) { // definito solo l'estremo superiore
						returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_INTERVALLO;
					} else { //entrambi null 
						returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_QUALSIASI;
					}
					
					if(applicabilita != null) {
						pattern = applicabilita.getPattern();
						contentType = applicabilita.getContentTypeList() != null ? StringUtils.join(applicabilita.getContentTypeList(), ",") : "";  
					}	
					
					// dati contenuto
					trasformazioneContenutoRispostaAbilitato = oldRisposta.getConversione();
					trasformazioneContenutoRispostaTipo = StringUtils.isNotEmpty(oldRisposta.getConversioneTipo()) ? 
							org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(oldRisposta.getConversioneTipo()) : org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					trasformazioneContenutoRispostaTemplate.setValue(oldRisposta.getConversioneTemplate());
					trasformazioneContenutoRispostaContentType = StringUtils.isNotEmpty(oldRisposta.getContentType()) ? oldRisposta.getContentType() : "";
					trasformazioneContenutoRispostaReturnCode = oldRisposta.getReturnCode() != null ? oldRisposta.getReturnCode() + "" : "";
					
					if(oldRisposta.getTrasformazioneSoap() == null) {
						trasformazioneRispostaSoapAbilitato = false;
						trasformazioneRispostaSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
						trasformazioneRispostaSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					} else {
						trasformazioneRispostaSoapAbilitato = true;
						
						if(oldRisposta.getTrasformazioneSoap().isEnvelope()) {
							if(oldRisposta.getTrasformazioneSoap().isEnvelopeAsAttachment()) {
								trasformazioneRispostaSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT;
							} else {
								trasformazioneRispostaSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_BODY;
							}
						} else {
							trasformazioneRispostaSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
						}
						
						trasformazioneRispostaSoapEnvelopeTipo = StringUtils.isNotEmpty(oldRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo()) ? 
								org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(oldRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo())
								: org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
						trasformazioneRispostaSoapEnvelopeTemplate.setValue(oldRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTemplate()); 
					}
				}

				dati = porteDelegateHelper.addTrasformazioneRispostaToDati(TipoOperazione.CHANGE, dati, idInt, oldRisposta, true, idTrasformazioneS, idTrasformazioneRispostaS, nomeRisposta,
						returnCode, statusMin, statusMax, pattern, contentType, servletTrasformazioniRispostaHeadersList, parametriInvocazioneServletTrasformazioniRispostaHeaders, numeroTrasformazioniRispostaHeaders, 
						trasformazioneContenutoRichiestaAbilitato, trasformazioneRichiestaRestAbilitato, 
						trasformazioneContenutoRispostaAbilitato, trasformazioneContenutoRispostaTipo, trasformazioneContenutoRispostaTemplate, trasformazioneContenutoRispostaTipoCheck, trasformazioneContenutoRispostaContentType, trasformazioneContenutoRispostaReturnCode,
						serviceBindingMessage, trasformazioneRispostaSoapAbilitato, trasformazioneRispostaSoapEnvelope, trasformazioneRispostaSoapEnvelopeTipo, trasformazioneRispostaSoapEnvelopeTemplate, trasformazioneRispostaSoapEnvelopeTipoCheck);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA,	ForwardParams.CHANGE());
			}
			
				
			boolean isOk = porteDelegateHelper.trasformazioniRispostaCheckData(TipoOperazione.CHANGE, oldRegola, oldRisposta);
			
			if(isOk) {
				// quando un parametro viene inviato come vuoto, sul db viene messo null, gestisco il caso
				Integer statusMinDBCheck = StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null;
				Integer statusMaxDBCheck = StringUtils.isNotEmpty(statusMax) ? Integer.parseInt(statusMax) : null;
				if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_ESATTO))
					statusMaxDBCheck = statusMinDBCheck;
				String patternDBCheck = StringUtils.isNotEmpty(pattern) ? pattern : null;
				String contentTypeDBCheck = StringUtils.isNotEmpty(contentType) ? contentType : null;
				TrasformazioneRegolaRisposta regolaRispostaDBCheck_applicabilita = porteDelegateCore.getTrasformazioneRisposta(Long.parseLong(id), idTrasformazione, statusMinDBCheck, statusMaxDBCheck, patternDBCheck, contentTypeDBCheck);
				TrasformazioneRegolaRisposta regolaRispostaDBCheck_nome = porteDelegateCore.getTrasformazioneRisposta(Long.parseLong(id), idTrasformazione, nomeRisposta);
				
				// controllo che le modifiche ai parametri non coincidano con altre regole gia' presenti
				if(regolaRispostaDBCheck_applicabilita != null && regolaRispostaDBCheck_applicabilita.getId().longValue() != oldRisposta.getId().longValue()) {
					pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_DUPLICATA);
					isOk = false;
				}
				if(isOk && regolaRispostaDBCheck_nome != null && regolaRispostaDBCheck_nome.getId().longValue() != oldRisposta.getId().longValue()) {
					pd.setMessage(CostantiControlStation.MESSAGGIO_ERRORE_REGOLA_TRASFORMAZIONE_APPLICABILITA_NOME);
					isOk = false;
				}
			}
			
			if (!isOk) {

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteDelegateHelper.addTrasformazioneRispostaToDati(TipoOperazione.CHANGE, dati, idInt, oldRisposta, true, idTrasformazioneS, idTrasformazioneRispostaS, nomeRisposta,
						returnCode, statusMin, statusMax, pattern, contentType, servletTrasformazioniRispostaHeadersList, parametriInvocazioneServletTrasformazioniRispostaHeaders, numeroTrasformazioniRispostaHeaders, 
						trasformazioneContenutoRichiestaAbilitato, trasformazioneRichiestaRestAbilitato, 
						trasformazioneContenutoRispostaAbilitato, trasformazioneContenutoRispostaTipo, trasformazioneContenutoRispostaTemplate, trasformazioneContenutoRispostaTipoCheck, trasformazioneContenutoRispostaContentType, trasformazioneContenutoRispostaReturnCode,
						serviceBindingMessage, trasformazioneRispostaSoapAbilitato, trasformazioneRispostaSoapEnvelope, trasformazioneRispostaSoapEnvelopeTipo, trasformazioneRispostaSoapEnvelopeTemplate, trasformazioneRispostaSoapEnvelopeTipoCheck);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, id, idsogg, null, idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA,	ForwardParams.CHANGE());
			}
			
			// aggiorno la regola
			TrasformazioneRegola regola = null;
			for (TrasformazioneRegola reg : trasformazioni.getRegolaList()) {
				if(reg.getId().longValue() == idTrasformazione) {
					regola = reg;
					break;
				}
			}
			
			TrasformazioneRegolaRisposta rispostaDaAggiornare = null;
			
			for (int j = 0; j < regola.sizeRispostaList(); j++) {
				TrasformazioneRegolaRisposta risposta = regola.getRisposta(j);
				if (risposta.getId().longValue() == idTrasformazioneRisposta) {
					rispostaDaAggiornare = risposta;
					break;
				}
			}
			
			rispostaDaAggiornare.setNome(nomeRisposta);
			
			if(rispostaDaAggiornare.getApplicabilita() == null)
				rispostaDaAggiornare.setApplicabilita(new TrasformazioneRegolaApplicabilitaRisposta());

			if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_QUALSIASI)) {
				rispostaDaAggiornare.getApplicabilita().setReturnCodeMin(null);
				rispostaDaAggiornare.getApplicabilita().setReturnCodeMax(null);
			} else if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RESPONSE_CACHING_CONFIGURAZIONE_REGOLA_RETURN_CODE_ESATTO)) {
				rispostaDaAggiornare.getApplicabilita().setReturnCodeMin(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
				rispostaDaAggiornare.getApplicabilita().setReturnCodeMax(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
			} else { // intervallo
				rispostaDaAggiornare.getApplicabilita().setReturnCodeMin(StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null);
				rispostaDaAggiornare.getApplicabilita().setReturnCodeMax(StringUtils.isNotEmpty(statusMax) ? Integer.parseInt(statusMax) : null);
			}
			
			rispostaDaAggiornare.getApplicabilita().setPattern(pattern);
			rispostaDaAggiornare.getApplicabilita().getContentTypeList().clear();
			if(contentType != null) {
				rispostaDaAggiornare.getApplicabilita().getContentTypeList().addAll(Arrays.asList(contentType.split(",")));
			}
			
			rispostaDaAggiornare.setConversione(trasformazioneContenutoRispostaAbilitato);
			
			if(trasformazioneContenutoRispostaAbilitato) {
				if(trasformazioneContenutoRispostaTipo.isTemplateRequired()) {
					// 	se e' stato aggiornato il template lo sovrascrivo
					if(trasformazioneContenutoRispostaTemplate.getValue() != null && trasformazioneContenutoRispostaTemplate.getValue().length  > 0)
						rispostaDaAggiornare.setConversioneTemplate(trasformazioneContenutoRispostaTemplate.getValue());
				}else {
					rispostaDaAggiornare.setConversioneTemplate(null);
				}
				rispostaDaAggiornare.setContentType(trasformazioneContenutoRispostaContentType);
				rispostaDaAggiornare.setReturnCode(StringUtils.isNotEmpty(trasformazioneContenutoRispostaReturnCode) ? Integer.parseInt(trasformazioneContenutoRispostaReturnCode) : null);
				
				rispostaDaAggiornare.setConversioneTipo(trasformazioneContenutoRispostaTipo.getValue());
				
				if(trasformazioneRispostaSoapAbilitato) {
					if(rispostaDaAggiornare.getTrasformazioneSoap() == null)
						rispostaDaAggiornare.setTrasformazioneSoap(new TrasformazioneSoapRisposta());
					
					// ct null se trasformazione soap abilitata
					rispostaDaAggiornare.setContentType(null);
					
					if(trasformazioneRispostaSoapEnvelope.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT)) { // attachment
						rispostaDaAggiornare.getTrasformazioneSoap().setEnvelope(true);
						rispostaDaAggiornare.getTrasformazioneSoap().setEnvelopeAsAttachment(true);
						
						if(trasformazioneRispostaSoapEnvelopeTipo.isTemplateRequired()) {
							// 	se e' stato aggiornato il template lo sovrascrivo
							if(trasformazioneRispostaSoapEnvelopeTemplate.getValue() != null && trasformazioneRispostaSoapEnvelopeTemplate.getValue().length  > 0)
								rispostaDaAggiornare.getTrasformazioneSoap().setEnvelopeBodyConversioneTemplate(trasformazioneRispostaSoapEnvelopeTemplate.getValue());
						} else {
							rispostaDaAggiornare.getTrasformazioneSoap().setEnvelopeBodyConversioneTemplate(null);
						}
						
						rispostaDaAggiornare.getTrasformazioneSoap().setEnvelopeBodyConversioneTipo(trasformazioneRispostaSoapEnvelopeTipo.getValue());
					} else if(trasformazioneRispostaSoapEnvelope.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_BODY)) { // body
						rispostaDaAggiornare.getTrasformazioneSoap().setEnvelope(true);
						rispostaDaAggiornare.getTrasformazioneSoap().setEnvelopeAsAttachment(false);
						rispostaDaAggiornare.getTrasformazioneSoap().setEnvelopeBodyConversioneTemplate(null);
						rispostaDaAggiornare.getTrasformazioneSoap().setEnvelopeBodyConversioneTipo(null);
					} else  { // disabilitato
						rispostaDaAggiornare.getTrasformazioneSoap().setEnvelope(false);
						rispostaDaAggiornare.getTrasformazioneSoap().setEnvelopeAsAttachment(false);
						rispostaDaAggiornare.getTrasformazioneSoap().setEnvelopeBodyConversioneTemplate(null);
						rispostaDaAggiornare.getTrasformazioneSoap().setEnvelopeBodyConversioneTipo(null);
					}
						
				} else {
					rispostaDaAggiornare.setTrasformazioneSoap(null);
				}
			} else {
				rispostaDaAggiornare.setConversioneTemplate(null);
				rispostaDaAggiornare.setContentType(null);
				rispostaDaAggiornare.setReturnCode(null);
				rispostaDaAggiornare.setConversioneTipo(null);
				rispostaDaAggiornare.setTrasformazioneSoap(null);
			}
			
			porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
			
			// ricaricare id trasformazione
			portaDelegata = porteDelegateCore.getPortaDelegata(Long.parseLong(id));

			String patternDBCheck = (regola.getApplicabilita() != null && StringUtils.isNotEmpty(regola.getApplicabilita().getPattern())) ? regola.getApplicabilita().getPattern() : null;
			String contentTypeAsString = (regola.getApplicabilita() != null &&regola.getApplicabilita().getContentTypeList() != null) ? StringUtils.join(regola.getApplicabilita().getContentTypeList(), ",") : "";
			String contentTypeDBCheck = StringUtils.isNotEmpty(contentTypeAsString) ? contentTypeAsString : null;
			String azioniAsString = (regola.getApplicabilita() != null && regola.getApplicabilita().getAzioneList() != null) ? StringUtils.join(regola.getApplicabilita().getAzioneList(), ",") : "";
			String azioniDBCheck = StringUtils.isNotEmpty(azioniAsString) ? azioniAsString : null;
			TrasformazioneRegola trasformazioneAggiornata = porteDelegateCore.getTrasformazione(portaDelegata.getId(), azioniDBCheck, patternDBCheck, contentTypeDBCheck);
			
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTE; 
			
			ricerca = porteDelegateHelper.checkSearchParameters(idLista, ricerca);

			List<TrasformazioneRegolaRisposta> lista = porteDelegateCore.porteDelegateTrasformazioniRispostaList(Long.parseLong(id), trasformazioneAggiornata.getId(), ricerca);
			
			porteDelegateHelper.preparePorteDelegateTrasformazioniRispostaList(nomePorta, trasformazioneAggiornata.getId(), ricerca, lista); 
						
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA,	ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_TRASFORMAZIONI_RISPOSTA, 
					ForwardParams.CHANGE());
		}
	}
}
