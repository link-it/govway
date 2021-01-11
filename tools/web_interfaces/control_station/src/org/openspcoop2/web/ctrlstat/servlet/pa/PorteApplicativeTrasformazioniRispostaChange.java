/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.TrasformazioneSoapRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
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
 * PorteApplicativeTrasformazioniRispostaChange
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteApplicativeTrasformazioniRispostaChange extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);
		
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			long idInt = Long.parseLong(idPorta);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			
			String first = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_FIRST);
			String nomeRisposta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_NOME);
			
			String returnCode = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS);
			
			String statusMin = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MIN);
			if(statusMin == null)
				statusMin = "";
			String statusMax = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS_MAX);
			if(statusMax == null)
				statusMax = "";
			String pattern = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_PATTERN);
			String contentType = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_CT);
			
			String idTrasformazioneS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE);
			long idTrasformazione = Long.parseLong(idTrasformazioneS);
			
			String idTrasformazioneRispostaS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RISPOSTA);
			long idTrasformazioneRisposta = Long.parseLong(idTrasformazioneRispostaS);
			
			String trasformazioneContenutoRispostaAbilitatoS  = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_ENABLED);
			boolean trasformazioneContenutoRispostaAbilitato = trasformazioneContenutoRispostaAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneContenutoRispostaAbilitatoS) : false;
			String trasformazioneContenutoRispostaTipoS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO);
			org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneContenutoRispostaTipo = 
					trasformazioneContenutoRispostaTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneContenutoRispostaTipoS) : 
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
			BinaryParameter trasformazioneContenutoRispostaTemplate = porteApplicativeHelper.getBinaryParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TEMPLATE);
			String trasformazioneContenutoRispostaTipoCheck = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO_CHECK); 
			String trasformazioneContenutoRispostaContentType = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_CONTENT_TYPE);
			String trasformazioneContenutoRispostaReturnCode = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_RETURN_CODE);
			
			String trasformazioneRispostaSoapAbilitatoS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_SOAP_TRANSFORMATION); 
			boolean trasformazioneRispostaSoapAbilitato = trasformazioneRispostaSoapAbilitatoS != null ? ServletUtils.isCheckBoxEnabled(trasformazioneRispostaSoapAbilitatoS) : false;
			String trasformazioneRispostaSoapEnvelope = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE);
			String trasformazioneRispostaSoapEnvelopeTipoS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO);
			org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione trasformazioneRispostaSoapEnvelopeTipo =
					trasformazioneRispostaSoapEnvelopeTipoS != null ? org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.toEnumConstant(trasformazioneRispostaSoapEnvelopeTipoS) : 
						org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
			BinaryParameter trasformazioneRispostaSoapEnvelopeTemplate = porteApplicativeHelper.getBinaryParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TEMPLATE);
			String trasformazioneRispostaSoapEnvelopeTipoCheck = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO_CHECK);

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			// Prendo nome della porta applicativa
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String nomePorta = pa.getNome();
			
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
			AccordoServizioParteComuneSintetico apc = apcCore.getAccordoServizioSintetico(asps.getIdAccordo()); 
			ServiceBinding serviceBindingMessage = apcCore.toMessageServiceBinding(apc.getServiceBinding());
			
			Trasformazioni trasformazioni = pa.getTrasformazioni();
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
			boolean trasformazioneRichiestaSoapAbilitato = false;
			if(oldRegola.getRichiesta() != null) {
				trasformazioneContenutoRichiestaAbilitato = oldRegola.getRichiesta().getConversione();
				trasformazioneRichiestaRestAbilitato = oldRegola.getRichiesta().getTrasformazioneRest() != null;
				trasformazioneRichiestaSoapAbilitato = oldRegola.getRichiesta().getTrasformazioneSoap() != null;
			}
			
			String nomeRispostaTitle = oldRisposta.getNome();
			String nomeTrasformazione = oldRegola.getNome();
			Parameter pIdTrasformazione = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazione+"");
			
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			
			if (postBackElementName != null) {
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_APPLICABILITA_STATUS)) {
					statusMin = "";
					statusMax = "";
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_ENABLED)) {
					trasformazioneContenutoRispostaTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					trasformazioneContenutoRispostaContentType = "";
					//trasformazioneContenutoRispostaReturnCode = "";
					trasformazioneRispostaSoapAbilitato = false;
					trasformazioneRispostaSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
					trasformazioneRispostaSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					
					porteApplicativeHelper.deleteBinaryParameters(trasformazioneContenutoRispostaTemplate, trasformazioneRispostaSoapEnvelopeTemplate);
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_CONVERSIONE_TIPO)) {
					trasformazioneContenutoRispostaTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO;
					porteApplicativeHelper.deleteBinaryParameters(trasformazioneContenutoRispostaTemplate);
				}
				
				if(postBackElementName.equals(trasformazioneContenutoRispostaTemplate.getName())) {
					if(StringUtils.isEmpty(trasformazioneContenutoRispostaTipoCheck))
						trasformazioneContenutoRispostaTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_SOAP_TRANSFORMATION)) {
					porteApplicativeHelper.deleteBinaryParameters(trasformazioneRispostaSoapEnvelopeTemplate);
					if(trasformazioneRispostaSoapAbilitato) {
						trasformazioneRispostaSoapEnvelope = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_DISABILITATO;
						trasformazioneRispostaSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY;
					} 
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE)) {
					porteApplicativeHelper.deleteBinaryParameters(trasformazioneRispostaSoapEnvelopeTemplate);
					if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT.equals(trasformazioneRispostaSoapEnvelope)) {
						trasformazioneRispostaSoapEnvelopeTipo = org.openspcoop2.pdd.core.trasformazioni.TipoTrasformazione.EMPTY; 
					}
				}
				
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_SOAP_ENVELOPE_TIPO)) {
					trasformazioneRispostaSoapEnvelopeTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_TIPO;
					porteApplicativeHelper.deleteBinaryParameters(trasformazioneRispostaSoapEnvelopeTemplate);
				}
				
				if(postBackElementName.equals(trasformazioneRispostaSoapEnvelopeTemplate.getName())) {
					if(StringUtils.isEmpty(trasformazioneRispostaSoapEnvelopeTipoCheck))
						trasformazioneRispostaSoapEnvelopeTipoCheck = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_REQ_CONVERSIONE_TIPO_CHECK_UPDATE_FILE;
				}
			}
			
			// parametri visualizzazione link
			String servletTrasformazioniRispostaHeadersList = PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_HEADER_LIST;
			int numeroTrasformazioniRispostaHeaders = oldRisposta.sizeHeaderList();
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRispostaHeaders = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE, idTrasformazioneS));
			parametriInvocazioneServletTrasformazioniRispostaHeaders.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_TRASFORMAZIONE_RISPOSTA, idTrasformazioneRispostaS));
			

			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_DI+nomePorta;
			}

			lstParam.add(new Parameter(labelPerPorta,
					PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_LIST,
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps)
					));
			
			lstParam.add(new Parameter(nomeTrasformazione, PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_CHANGE, 
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta),
					new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg),
					new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps), pIdTrasformazione));
			
			String labelPag = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE;
			
			List<Parameter> parametriInvocazioneServletTrasformazioniRisposta = new ArrayList<Parameter>();
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, idPorta));
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg));
			parametriInvocazioneServletTrasformazioniRisposta.add(new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps));
			parametriInvocazioneServletTrasformazioniRisposta.add(pIdTrasformazione);
			
			lstParam.add(new Parameter(labelPag,PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA_LIST,parametriInvocazioneServletTrasformazioniRisposta));
			
			lstParam.add(new Parameter(nomeRispostaTitle, null));

			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// Se nomehid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
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
							returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO;
						else 
							returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO;
					} else if(statusMinInteger != null && statusMaxInteger == null) { // definito solo l'estremo inferiore
						returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO;
					} else if(statusMinInteger == null && statusMaxInteger != null) { // definito solo l'estremo superiore
						returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_INTERVALLO;
					} else { //entrambi null 
						returnCode = CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI;
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

				dati = porteApplicativeHelper.addTrasformazioneRispostaToDati(TipoOperazione.CHANGE, dati, idInt, oldRisposta, false, idTrasformazioneS, idTrasformazioneRispostaS, 
						apc.getServiceBinding(),
						nomeRisposta,
						returnCode, statusMin, statusMax, pattern, contentType, servletTrasformazioniRispostaHeadersList, parametriInvocazioneServletTrasformazioniRispostaHeaders, numeroTrasformazioniRispostaHeaders, 
						trasformazioneContenutoRichiestaAbilitato, trasformazioneRichiestaRestAbilitato, trasformazioneRichiestaSoapAbilitato,
						trasformazioneContenutoRispostaAbilitato, trasformazioneContenutoRispostaTipo, trasformazioneContenutoRispostaTemplate, trasformazioneContenutoRispostaTipoCheck, trasformazioneContenutoRispostaContentType, trasformazioneContenutoRispostaReturnCode, 
						serviceBindingMessage, trasformazioneRispostaSoapAbilitato, trasformazioneRispostaSoapEnvelope, trasformazioneRispostaSoapEnvelopeTipo, trasformazioneRispostaSoapEnvelopeTemplate, trasformazioneRispostaSoapEnvelopeTipoCheck);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA,	ForwardParams.CHANGE());
			}
			
				
			boolean isOk = porteApplicativeHelper.trasformazioniRispostaCheckData(TipoOperazione.CHANGE, oldRegola, oldRisposta);
			
			if(isOk) {
				// quando un parametro viene inviato come vuoto, sul db viene messo null, gestisco il caso
				Integer statusMinDBCheck = StringUtils.isNotEmpty(statusMin) ? Integer.parseInt(statusMin) : null;
				Integer statusMaxDBCheck = StringUtils.isNotEmpty(statusMax) ? Integer.parseInt(statusMax) : null;
				if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO))
					statusMaxDBCheck = statusMinDBCheck;
				String patternDBCheck = StringUtils.isNotEmpty(pattern) ? pattern : null;
				String contentTypeDBCheck = StringUtils.isNotEmpty(contentType) ? contentType : null;
				TrasformazioneRegolaRisposta regolaRispostaDBCheck_applicabilita = porteApplicativeCore.getTrasformazioneRisposta(Long.parseLong(idPorta), idTrasformazione, statusMinDBCheck, statusMaxDBCheck, patternDBCheck, contentTypeDBCheck);
				TrasformazioneRegolaRisposta regolaRispostaDBCheck_nome = porteApplicativeCore.getTrasformazioneRisposta(Long.parseLong(idPorta), idTrasformazione, nomeRisposta);
				
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
				
				dati = porteApplicativeHelper.addTrasformazioneRispostaToDati(TipoOperazione.CHANGE, dati, idInt, oldRisposta, false, idTrasformazioneS, idTrasformazioneRispostaS, 
						apc.getServiceBinding(),
						nomeRisposta,
						returnCode, statusMin, statusMax, pattern, contentType, servletTrasformazioniRispostaHeadersList, parametriInvocazioneServletTrasformazioniRispostaHeaders, numeroTrasformazioniRispostaHeaders, 
						trasformazioneContenutoRichiestaAbilitato, trasformazioneRichiestaRestAbilitato, trasformazioneRichiestaSoapAbilitato,
						trasformazioneContenutoRispostaAbilitato, trasformazioneContenutoRispostaTipo, trasformazioneContenutoRispostaTemplate, trasformazioneContenutoRispostaTipoCheck, trasformazioneContenutoRispostaContentType, trasformazioneContenutoRispostaReturnCode,
						serviceBindingMessage, trasformazioneRispostaSoapAbilitato, trasformazioneRispostaSoapEnvelope, trasformazioneRispostaSoapEnvelopeTipo, trasformazioneRispostaSoapEnvelopeTemplate, trasformazioneRispostaSoapEnvelopeTipoCheck);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.CHANGE, idPorta, idsogg, idPorta,idAsps,  dati);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA,	ForwardParams.CHANGE());
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

			if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_QUALSIASI)) {
				rispostaDaAggiornare.getApplicabilita().setReturnCodeMin(null);
				rispostaDaAggiornare.getApplicabilita().setReturnCodeMax(null);
			} else if(returnCode.equals(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_RETURN_CODE_ESATTO)) {
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
			
			rispostaDaAggiornare.setReturnCode(StringUtils.isNotEmpty(trasformazioneContenutoRispostaReturnCode) ? Integer.parseInt(trasformazioneContenutoRispostaReturnCode) : null);
						
			rispostaDaAggiornare.setConversione(trasformazioneContenutoRispostaAbilitato);
			
			if(trasformazioneContenutoRispostaAbilitato) {
				if(trasformazioneContenutoRispostaTipo.isTemplateRequired()) {
					// 	se e' stato aggiornato il template lo sovrascrivo
					if(trasformazioneContenutoRispostaTemplate.getValue() != null && trasformazioneContenutoRispostaTemplate.getValue().length  > 0)
						rispostaDaAggiornare.setConversioneTemplate(trasformazioneContenutoRispostaTemplate.getValue());
				}else {
					rispostaDaAggiornare.setConversioneTemplate(null);
				}
				
				if(trasformazioneContenutoRispostaContentType!=null && !"".equals(trasformazioneContenutoRispostaContentType)) {
					rispostaDaAggiornare.setContentType(trasformazioneContenutoRispostaContentType);
				}
				else {
					rispostaDaAggiornare.setContentType(null);
				}
				rispostaDaAggiornare.setConversioneTipo(trasformazioneContenutoRispostaTipo.getValue());
				
				if(trasformazioneRispostaSoapAbilitato) {
					if(rispostaDaAggiornare.getTrasformazioneSoap() == null)
						rispostaDaAggiornare.setTrasformazioneSoap(new TrasformazioneSoapRisposta());
					
					if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_ATTACHMENT.equals(trasformazioneRispostaSoapEnvelope)) { // attachment
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
					} else if(CostantiControlStation.VALUE_PARAMETRO_CONFIGURAZIONE_TRASFORMAZIONI_SOAP_ENVELOPE_AS_BODY.equals(trasformazioneRispostaSoapEnvelope)) { // body
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
				rispostaDaAggiornare.setConversioneTipo(null);
				rispostaDaAggiornare.setTrasformazioneSoap(null);
			}
			
			porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			// ricaricare id trasformazione
			pa = porteApplicativeCore.getPortaApplicativa(Long.parseLong(idPorta));

			TrasformazioneRegola trasformazioneAggiornata = porteApplicativeCore.getTrasformazione(pa.getId(), regola.getNome());
			
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTE; 
			
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<TrasformazioneRegolaRisposta> lista = porteApplicativeCore.porteAppTrasformazioniRispostaList(Long.parseLong(idPorta), trasformazioneAggiornata.getId(), ricerca);
			
			porteApplicativeHelper.preparePorteAppTrasformazioniRispostaList(nomePorta, trasformazioneAggiornata.getId(), ricerca, lista); 
						
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA,	ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_TRASFORMAZIONI_RISPOSTA, 
					ForwardParams.CHANGE());
		}
	}
}
