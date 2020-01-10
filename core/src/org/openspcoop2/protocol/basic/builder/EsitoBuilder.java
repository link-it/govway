/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.basic.builder;

import java.util.Hashtable;
import java.util.List;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;

import org.openspcoop2.core.eccezione.errore_applicativo.Eccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo;
import org.openspcoop2.core.eccezione.errore_applicativo.constants.TipoEccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.builder.InformazioniErroriInfrastrutturali;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.protocol.utils.EsitoIdentificationModeContextProperty;
import org.openspcoop2.protocol.utils.EsitoIdentificationModeSoapFault;
import org.openspcoop2.protocol.utils.EsitoTransportContextIdentification;
import org.openspcoop2.utils.rest.problem.JsonDeserializer;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.XmlDeserializer;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**	
 * EsitoBuilder
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoBuilder extends BasicComponentFactory implements org.openspcoop2.protocol.sdk.builder.IEsitoBuilder {
	
	protected EsitiProperties esitiProperties;
	protected boolean erroreProtocollo = false;
	protected boolean faultEsterno = false;
	
	public EsitoBuilder(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		super(protocolFactory);
		this.esitiProperties = EsitiProperties.getInstance(this.log, protocolFactory.getProtocol());
		this.erroreProtocollo = this.esitiProperties.isErroreProtocollo();
		this.faultEsterno = this.esitiProperties.isFaultEsterno();
	}


	protected String getTipoContext(TransportRequestContext transportRequestContext) throws ProtocolException{
		String tipoContext = CostantiProtocollo.ESITO_TRANSACTION_CONTEXT_STANDARD;
		
		if(transportRequestContext!=null){
		
			if(transportRequestContext.getParametersTrasporto()!=null && transportRequestContext.getParametersTrasporto().size()>0){
				List<EsitoTransportContextIdentification> list = this.esitiProperties.getEsitoTransactionContextHeaderTrasportoDynamicIdentification();
				if(list!=null && list.size()>0){
					for (EsitoTransportContextIdentification esitoTransportContextIdentification : list) {
						if(esitoTransportContextIdentification.match(transportRequestContext.getParametersTrasporto())){
							tipoContext = esitoTransportContextIdentification.getType();
							break;
						}
					}
				}
			}
			
			// urlBased eventualmente sovrascrive l'header
			if(transportRequestContext.getParametersFormBased()!=null && transportRequestContext.getParametersFormBased().size()>0){
				List<EsitoTransportContextIdentification> list = this.esitiProperties.getEsitoTransactionContextHeaderFormBasedDynamicIdentification();
				if(list!=null && list.size()>0){
					for (EsitoTransportContextIdentification esitoTransportContextIdentification : list) {
						if(esitoTransportContextIdentification.match(transportRequestContext.getParametersFormBased())){
							tipoContext = esitoTransportContextIdentification.getType();
							break;
						}
					}
				}
			}
			
			// trasporto con header openspcoop sovrascrive un valore trovato in precedenza
			if(transportRequestContext.getParametersTrasporto()!=null && transportRequestContext.getParametersTrasporto().size()>0){
				String headerName = this.esitiProperties.getEsitoTransactionContextHeaderTrasportoName();
				String value = transportRequestContext.getParameterTrasporto(headerName);
				if(value!=null){
					if(this.esitiProperties.getEsitiTransactionContextCode().contains(value)==false){
						this.log.error("Trovato nell'header http un header con nome ["+headerName+"] il cui valore ["+value+"] non rientra tra i tipi di contesto supportati");
					}
					else{
						tipoContext = value;
					}
				}
			}
	
			// urlBased eventualmente sovrascrive l'header
			if(transportRequestContext.getParametersFormBased()!=null && transportRequestContext.getParametersFormBased().size()>0){
				String propertyName = this.esitiProperties.getEsitoTransactionContextFormBasedPropertyName();
				String value = transportRequestContext.getParameterFormBased(propertyName);
				if(value!=null){
					if(this.esitiProperties.getEsitiTransactionContextCode().contains(value)==false){
						this.log.error("Trovato nella url una proprietà con nome ["+propertyName+"] il cui valore ["+value+"] non rientra tra i tipi di contesto supportati");
					}
					else{
						tipoContext = value;
					}
				}
			}
			
		}
		
		return tipoContext;
	}
	
	private EsitoTransazione getEsitoErroreGenerale(InformazioniErroriInfrastrutturali informazioniErroriInfrastrutturali, String tipoContext) throws ProtocolException{
		if(informazioniErroriInfrastrutturali.isRicevutoSoapFaultServerPortaDelegata()){
			if(this.faultEsterno) {
				return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_SERVER, tipoContext);
			}
			else {
				return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
			}
		}
		else{
			return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
		}
	}
	
	@Override
	public EsitoTransazione getEsito(TransportRequestContext transportRequestContext, EsitoTransazioneName name) {
		String tipoContext = null;
		try{
			tipoContext = this.getTipoContext(transportRequestContext);
			return this.esitiProperties.convertToEsitoTransazione(name, tipoContext);
		}catch(Exception e){
			this.log.error("Errore durante la trasformazione in oggetto EsitoTransazione (utilizzo lo standard): "+e.getMessage(),e);
			Integer code = null;
			EsitoTransazioneName tmp = null;
			try{
				if(tipoContext==null){
					tipoContext = CostantiProtocollo.ESITO_TRANSACTION_CONTEXT_STANDARD;
				}
				code = this.esitiProperties.convertoToCode(name);
				tmp = name;
			}catch(Exception eInternal){
				if(tipoContext!=null){
					// se è uguale a null, l'errore sollevato prima è lo stesso di questo interno
					this.log.error("Errore durante la trasformazione interna per il codice: "+eInternal.getMessage(),eInternal);
				}
				return EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
			}
			try{
				return new EsitoTransazione(tmp,  code, tipoContext);
			}catch(Exception eInternal){
				this.log.error("Errore durante la init EsitoTransazione",eInternal);
				return EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
			}
		}
	}
	
	@Override
	public EsitoTransazione getEsito(TransportRequestContext transportRequestContext, 
			int returnCode, ServiceBinding serviceBinding,	
			OpenSPCoop2Message message,
			InformazioniErroriInfrastrutturali informazioniErroriInfrastrutturali, Hashtable<String, Object> context) throws ProtocolException {
		return getEsito(transportRequestContext,returnCode,serviceBinding,message,null,informazioniErroriInfrastrutturali,context);
	}

	@Override
	public EsitoTransazione getEsito(TransportRequestContext transportRequestContext, 
			int returnCode, ServiceBinding serviceBinding,	
			OpenSPCoop2Message message,
			ProprietaErroreApplicativo erroreApplicativo,
			InformazioniErroriInfrastrutturali informazioniErroriInfrastrutturali, Hashtable<String, Object> context)
			throws ProtocolException {
		try{
			
					
			SOAPBody soapBody = null;
			if(message!=null && ServiceBinding.SOAP.equals(message.getServiceBinding())
					&& !message.isForcedEmptyResponse() && message.getForcedResponse()==null){
				soapBody = message.castAsSoap().getSOAPBody();
			}
						
			if(informazioniErroriInfrastrutturali==null){
				// inizializzo con valori di default
				informazioniErroriInfrastrutturali = new InformazioniErroriInfrastrutturali();
			}
						
			String tipoContext = this.getTipoContext(transportRequestContext);
			
			// Emissione diagnostici di livello error
			boolean emissioneDiagnosticiError = (context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.EMESSI_DIAGNOSTICI_ERRORE));
			boolean corsPreflightRequestViaGateway = (context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY));
			boolean corsPreflightRequestTrasparente = (context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.CORS_PREFLIGHT_REQUEST_TRASPARENTE));
			
			// Tipo di esito OK
			EsitoTransazione returnEsitoOk = null;
			if(emissioneDiagnosticiError){
				returnEsitoOk = this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.OK_PRESENZA_ANOMALIE, tipoContext); 
			}
			else if(corsPreflightRequestViaGateway) {
				returnEsitoOk = this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.CORS_PREFLIGHT_REQUEST_VIA_GATEWAY, tipoContext); 
			}
			else if(corsPreflightRequestTrasparente) {
				returnEsitoOk = this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.CORS_PREFLIGHT_REQUEST_TRASPARENTE, tipoContext); 
			}
			else {
				returnEsitoOk = this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.OK, tipoContext);
			}
			
			// Esito 4xx
			EsitoTransazione esitoErrore4xx = this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_4XX, tipoContext);
			
			// Esito 5xx
			EsitoTransazione esitoErrore5xx = this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
			
			// Devo riconoscere eventuali codifiche custom inserite nel contesto
			if(context!=null){
				List<Integer> customCodeForContextProperty = this.esitiProperties.getEsitiCodeForContextPropertyIdentificationMode();
				if(customCodeForContextProperty!=null && customCodeForContextProperty.size()>0){
					for (Integer customCode : customCodeForContextProperty) {
						List<EsitoIdentificationModeContextProperty> l = this.esitiProperties.getEsitoIdentificationModeContextPropertyList(customCode);
						if(l!=null && l.size()>0){
							for (EsitoIdentificationModeContextProperty esitoIdentificationModeContextProperty : l) {
								try{
									Object p = context.get(esitoIdentificationModeContextProperty.getName());
									if(p!=null && p instanceof String){
										String pS = (String) p;
										if(esitoIdentificationModeContextProperty.getValue()==null ||
												esitoIdentificationModeContextProperty.getValue().equals(pS)){
											// match
											
											EsitoTransazione esito = this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.CUSTOM, customCode, tipoContext);
											
											// ritorno immediatamente se l'esito e' un esito non 'ok'
											// altrimenti verifico se primo ho avuto un errore che altrimenti ha priorita' rispetto al marcare la transazione con questo esito
											List<Integer> esitiOk = this.esitiProperties.getEsitiCodeOk();
											boolean found = false;
											for (Integer intValue : esitiOk) {
												if(intValue.intValue() == customCode.intValue()) {
													found = true;
												}
											}
											if(!found) {
												return esito;
											}
											else {
												returnEsitoOk = esito;
											}
										}
									}
								}catch(Throwable t){
									this.log.error("Errore durante la comprensione dell'esito: "+t.getMessage(),t);
								}
							}
						}
					}
				}
				
				// Eventuale possibilità di definire un errore generico sulle classi custom (es. handler)
				Object p = context.get(org.openspcoop2.core.constants.Costanti.ERRORE_GENERICO);
				if(p!=null && p instanceof String){
					String pS = (String) p;
					if("true".equalsIgnoreCase(pS)) {
						return esitoErrore5xx;
					}
				}
			}
			
			
			if(informazioniErroriInfrastrutturali.isContenutoRichiestaNonRiconosciuto()){
				return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, tipoContext);
			}
			else if(informazioniErroriInfrastrutturali.isContenutoRispostaNonRiconosciuto()){
				return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, tipoContext);
			}
			else if(informazioniErroriInfrastrutturali.isErroreUtilizzoConnettore()){
				return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_INVOCAZIONE, tipoContext);
			}
			else if(soapBody!=null && soapBody.hasFault()){
				return getEsitoSoapFault(message, soapBody, erroreApplicativo, informazioniErroriInfrastrutturali, tipoContext);
			}
			else if(message!=null) {
				
				boolean checkElementSeContieneFaultPdD = true; // fix
				// bisogna controllare il messaggio xml, json o soap fault per vedere se porta al suo interno un errore di protocollo
				// e' necessario farlo pero' solo se siamo in un caso FAULT.
				
				Object erroreGovwayObject = message.getContextProperty(org.openspcoop2.message.constants.Costanti.ERRORE_GOVWAY);
				String erroreGovway = null;
				if(erroreGovwayObject!=null && erroreGovwayObject instanceof String) {
					erroreGovway = (String) erroreGovwayObject;
				}
						
				boolean checkReturnCodeForFault = false;
				switch (message.getMessageType()) {
				case SOAP_11:
				case SOAP_12:
					// body letto precedentemente
					if(MessageRole.FAULT.equals(message.getMessageRole())) {
						if(checkElementSeContieneFaultPdD) {
							if(soapBody!=null) {
								EsitoTransazione esitoErrore = getEsitoMessaggioApplicativo(message.getFactory(),
										erroreApplicativo, soapBody, tipoContext, erroreGovway);
								if(esitoErrore!=null) {
									return esitoErrore;
								}
							}
						}
						checkReturnCodeForFault = true;
					}
					break;
				case XML:	
					if(message.castAsRestXml().isProblemDetailsForHttpApis_RFC7807() && erroreGovway==null) {
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_APPLICATIVO, tipoContext);
					}
					if(MessageRole.FAULT.equals(message.getMessageRole())) {
						if(checkElementSeContieneFaultPdD) {
							if(message.castAsRestXml().hasContent()) {
								EsitoTransazione esitoErrore = getEsitoMessaggioApplicativo(message.getFactory(),
										erroreApplicativo, message.castAsRestXml().getContent(), tipoContext, erroreGovway);
								if(esitoErrore!=null) {
									return esitoErrore;
								}
							}
						}
						checkReturnCodeForFault = true;
					}
					break;
				case JSON:	
					if(message.castAsRestJson().isProblemDetailsForHttpApis_RFC7807() && erroreGovway==null) {
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_APPLICATIVO, tipoContext);
					}
					if(MessageRole.FAULT.equals(message.getMessageRole())) {
						if(checkElementSeContieneFaultPdD) {
							if(message.castAsRestJson().hasContent()) {
								EsitoTransazione esitoErrore = getEsitoMessaggioApplicativo(erroreApplicativo, message.castAsRestJson().getContent(), tipoContext, erroreGovway);
								if(esitoErrore!=null) {
									return esitoErrore;
								}
							}
						}
						checkReturnCodeForFault = true;
					}
					break;
				default:
					if(MessageRole.FAULT.equals(message.getMessageRole())) {
						checkReturnCodeForFault = true;
					}
					break;
				}
				if(checkReturnCodeForFault) {
					if(returnCode>=400 && returnCode<=499) {
						return esitoErrore4xx;
					}
					else {
						return esitoErrore5xx;
					}
				}
				else {
					if(returnCode>=200 && returnCode<=299) {
						return returnEsitoOk;
					}
					else if(returnCode>=300 && returnCode<=399) {
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.HTTP_3xx, tipoContext);
					}
					else if(returnCode>=400 && returnCode<=499) {
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.HTTP_4xx, tipoContext);
					}
					else {
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.HTTP_5xx, tipoContext);
					}
				}
			}
			else {
				if(returnCode>=200 && returnCode<=299) {
					return returnEsitoOk;
				}
				else if(returnCode>=300 && returnCode<=399) {
					return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.HTTP_3xx, tipoContext);
				}
				else if(returnCode>=400 && returnCode<=499) {
					return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.HTTP_4xx, tipoContext);
				}
				else {
					return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.HTTP_5xx, tipoContext);
				}
			}
		}catch(Exception e){
			throw new ProtocolException("Comprensione stato non riuscita: "+e.getMessage(),e);
		}
	}

	private EsitoTransazione getEsitoSoapFault(OpenSPCoop2Message message, SOAPBody soapBody,
			ProprietaErroreApplicativo erroreApplicativo, InformazioniErroriInfrastrutturali informazioniErroriInfrastrutturali,
			String tipoContext) throws ProtocolException {
		SOAPFault fault = soapBody.getFault();
		String actor = fault.getFaultActor();
		String reason = fault.getFaultString();
		String codice = null;
		String namespaceCodice = null;
		if(fault.getFaultCodeAsQName()!=null){
			codice = fault.getFaultCodeAsQName().getLocalPart();	
			namespaceCodice = fault.getFaultCodeAsQName().getNamespaceURI();
		}
		else{
			codice = fault.getFaultCode();
		}
		//System.out.println("ACTOR["+actor+"] REASON["+reason+"] CODICE["+codice+"] namespaceCodice["+namespaceCodice+"]");	

		Object backwardCompatibilityActorObject = message.getContextProperty(CostantiProtocollo.BACKWARD_COMPATIBILITY_ACTOR);
		String backwardCompatibilityActor = null;
		if(backwardCompatibilityActorObject!=null){
			backwardCompatibilityActor = (String) backwardCompatibilityActorObject;
		}
		
		boolean faultInternalError = actor!=null && org.openspcoop2.message.constants.Costanti.DEFAULT_SOAP_FAULT_ACTOR.equals(actor);
		if(faultInternalError) {
			return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
		}
		
		boolean faultActorOpenSPCoopV2 = (erroreApplicativo!=null &&
				erroreApplicativo.getFaultActor()!=null &&
				erroreApplicativo.getFaultActor().equals(actor));
		
		boolean faultActorBackwardCompatibility = (backwardCompatibilityActor!=null &&
				backwardCompatibilityActor.equals(actor));
		
		if(faultActorOpenSPCoopV2 || faultActorBackwardCompatibility){

			// L'errore puo' essere generato da GovWay, l'errore puo' essere un :
			// msg di errore 4XX
			// msg di errore 5xx
			// msg dovuto alla ricezione di una busta.

			if(codice==null){
				// CASO NON PREVISTO ???
				return getEsitoErroreGenerale(informazioniErroriInfrastrutturali, tipoContext);
			}
			else{
				String prefixFaultCode = erroreApplicativo.getFaultPrefixCode();
				if(prefixFaultCode==null){
					prefixFaultCode=org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE;
				}
				boolean prefixOpv2 = codice.startsWith(prefixFaultCode);
				
				Object backwardCompatibilityPrefixObject = message.getContextProperty(CostantiProtocollo.BACKWARD_COMPATIBILITY_PREFIX_FAULT_CODE);
				String backwardCompatibilityPrefix = null;
				if(backwardCompatibilityPrefixObject!=null){
					backwardCompatibilityPrefix = (String) backwardCompatibilityPrefixObject;
				}
				boolean prefixBackwardCompatibility = (backwardCompatibilityPrefix!=null && codice.startsWith(backwardCompatibilityPrefix));
				
				if(prefixOpv2 || prefixBackwardCompatibility){
					// EccezioneProcessamento
					String value = null;
					if(prefixOpv2){
						value = codice.substring(prefixFaultCode.length());
					}
					else{
						value = codice.substring(backwardCompatibilityPrefix.length());
					}
					try{
						int valueInt = Integer.parseInt(value);
						if(valueInt>=400 && valueInt<=499){
							return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_4XX, tipoContext);
						}else if(valueInt>=500 && valueInt<=599){
							return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
						}else{
							return getEsitoErroreGenerale(informazioniErroriInfrastrutturali, tipoContext);
						}
					}catch(Throwable t){
						String error = "Errore calcolato da codice["+codice+"] prefixOpv2["+prefixOpv2+"] prefixFaultCode["+
								prefixFaultCode+"] prefixBackwardCompatibility["+prefixBackwardCompatibility+
								"] prefixBackwardCompatibility["+prefixBackwardCompatibility+"] value["+value+"]";
						if(this.log!=null)
							this.log.error(error+": "+t.getMessage(),t);
						else{
							System.err.print(error);
							t.printStackTrace(System.err);
						}
						return getEsitoErroreGenerale(informazioniErroriInfrastrutturali, tipoContext);
					}
				}else{
					// EccezioneBusta
					if(this.erroreProtocollo) {
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROTOCOLLO, tipoContext);
					}
					else {
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
					}
				}
			}

		}
		else{

			ITraduttore trasl = this.protocolFactory.createTraduttore();
			
			if("Client".equals(codice) && trasl.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE).equals(reason) ){
				if(this.erroreProtocollo) {
					return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROTOCOLLO, tipoContext);
				}
				else {
					return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_APPLICATIVO, tipoContext);
				}
			}
			else if("Server".equals(codice) && trasl.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO).equals(reason) ){
				if(this.erroreProtocollo) {
					return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROTOCOLLO, tipoContext);
				}
				else {
					return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_APPLICATIVO, tipoContext);
				}
			}
			else{
				
				// Devo riconoscere eventuali altre codifiche custom
				List<Integer> customCodeForSoapFault = this.esitiProperties.getEsitiCodeForSoapFaultIdentificationMode();
				if(customCodeForSoapFault!=null && customCodeForSoapFault.size()>0){
					for (Integer customCodeSF : customCodeForSoapFault) {
						List<EsitoIdentificationModeSoapFault> l = this.esitiProperties.getEsitoIdentificationModeSoapFaultList(customCodeSF);
						for (int i = 0; i < l.size(); i++) {
							EsitoIdentificationModeSoapFault e = l.get(i);
							if(e.getFaultCode()!=null){
								if(!e.getFaultCode().equals(codice)){
									continue;
								}
							}
							if(e.getFaultNamespaceCode()!=null){
								if(!e.getFaultNamespaceCode().equals(namespaceCodice)){
									continue;
								}
							}
							if(e.getFaultReason()!=null){
								if(e.getFaultReasonContains()!=null && e.getFaultReasonContains()){
									if(reason==null || !reason.contains(e.getFaultReason())){
										continue;
									}
								}
								else{ 
									if(!e.getFaultReason().equals(reason)){
										continue;
									}
								}
							}
							if(e.getFaultActor()!=null){
								if(!e.getFaultActor().equals(actor)){
									continue;
								}
							}
							if(e.getFaultActorNotDefined()!=null && e.getFaultActorNotDefined()){
								if(actor!=null){
									continue;
								}
							}
							// match
							return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.CUSTOM, customCodeSF, tipoContext);
						}
					}
				}
				
				if(informazioniErroriInfrastrutturali.isRicevutoSoapFaultServerPortaDelegata()){
					if(this.faultEsterno) {
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_SERVER, tipoContext);
					}
					else {
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_APPLICATIVO, tipoContext);
					}
				}
				else{
					return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_APPLICATIVO, tipoContext);
				}
			}

		}
	}
	
	protected EsitoTransazione getEsitoMessaggioApplicativo(OpenSPCoop2MessageFactory messageFactory,
			ProprietaErroreApplicativo erroreApplicativo,SOAPBody body,String tipoContext, String erroreGovway) throws ProtocolException{
		if(erroreApplicativo!=null){
			Node childNode = body.getFirstChild();
			if(childNode!=null){
				return getEsitoMessaggioApplicativo(messageFactory, erroreApplicativo, childNode, tipoContext, erroreGovway);
			}
		}

		return null;

	}
	
	protected EsitoTransazione getEsitoMessaggioApplicativo(OpenSPCoop2MessageFactory messageFactory,
			ProprietaErroreApplicativo erroreApplicativo,Node childNode,String tipoContext, String erroreGovway) throws ProtocolException{
		if(childNode!=null){
			if(childNode.getNextSibling()==null){
				
				if(org.openspcoop2.message.constants.Costanti.TIPO_RFC7807.equals(erroreGovway)) {
					if(childNode instanceof Element) {
						try{
							XmlDeserializer xmlDeserializer = new XmlDeserializer();
							ProblemRFC7807 problem = xmlDeserializer.fromNode((Element) childNode);
							Integer status = problem.getStatus();
							if(status!=null) {
								int valueInt = status.intValue();
								if(valueInt>=400 && valueInt<=499){
									return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_4XX, tipoContext);
								}else if(valueInt>=500 && valueInt<=599){
									return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
								}else{
									return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
								}
							}
							else {
								return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext); // ???
							}
						}catch(Exception e) {
							this.log.error("Errore durante la comprensione dell'esito: "+e.getMessage(),e);
						}
					}
				}
				else {
					if(XMLUtils.isErroreApplicativo(childNode)){
						
						try{
							byte[] xml = org.openspcoop2.message.xml.XMLUtils.getInstance(messageFactory).toByteArray(childNode,true);
							ErroreApplicativo erroreApplicativoObject = XMLUtils.getErroreApplicativo(this.log, xml);
							Eccezione ecc = erroreApplicativoObject.getException();
							if(TipoEccezione.PROTOCOL.equals(ecc.getType())){
								if(this.erroreProtocollo) {
									return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROTOCOLLO, tipoContext);
								}
								else {
									return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
								}
							}
							else{
								String value = ecc.getCode().getBase();
								String prefixFaultCode = erroreApplicativo.getFaultPrefixCode();
								if(prefixFaultCode==null){
									prefixFaultCode=Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE;
								}
								if(value.startsWith(prefixFaultCode)){
									value = value.substring(prefixFaultCode.length());
									int valueInt = Integer.parseInt(value);
									if(valueInt>=400 && valueInt<=499){
										return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_4XX, tipoContext);
									}else if(valueInt>=500 && valueInt<=599){
										return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
									}else{
										return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
									}
								}else{
									return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext); // ???
								}
							}
							
						}catch(Exception e){
							this.log.error("Errore durante la comprensione dell'esito: "+e.getMessage(),e);
						}
						
					}
					
					// se arrivo qua provo a vedere se siamo nel caso di un internal Error
					else if(OpenSPCoop2MessageFactory.isFaultXmlMessage(childNode)) {
						try{
							return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
						}catch(Exception e){
							this.log.error("Errore durante la comprensione dell'esito: "+e.getMessage(),e);
						}
					}
				}
				
			}
		}

		return null;

	}
	
	protected EsitoTransazione getEsitoMessaggioApplicativo(ProprietaErroreApplicativo erroreApplicativo,String jsonBody,String tipoContext, String erroreGovway) throws ProtocolException{
		
		if(org.openspcoop2.message.constants.Costanti.TIPO_RFC7807.equals(erroreGovway)) {
			try{
				JsonDeserializer jsonDeserializer = new JsonDeserializer();
				ProblemRFC7807 problem = jsonDeserializer.fromString(jsonBody);
				Integer status = problem.getStatus();
				if(status!=null) {
					int valueInt = status.intValue();
					if(valueInt>=400 && valueInt<=499){
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_4XX, tipoContext);
					}else if(valueInt>=500 && valueInt<=599){
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
					}else{
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
					}
				}
				else {
					return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext); // ???
				}
			}catch(Exception e) {
				this.log.error("Errore durante la comprensione dell'esito: "+e.getMessage(),e);
			}
		}
		else {
		
			// tipo dell'errore
			// $.exception.type
			
			// codice 
			//$.exception.code.value
			try {
				String tipo = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(jsonBody, "$.exception.type", this.log);
				if(TipoEccezione.PROTOCOL.getValue().equals(tipo)){
					if(this.erroreProtocollo) {
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROTOCOLLO, tipoContext);
					}
					else {
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
					}
				}
				else{
					String value = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(jsonBody, "$.exception.code.value", this.log);
					String prefixFaultCode = erroreApplicativo.getFaultPrefixCode();
					if(prefixFaultCode==null){
						prefixFaultCode=Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE;
					}
					if(value.startsWith(prefixFaultCode)){
						value = value.substring(prefixFaultCode.length());
						int valueInt = Integer.parseInt(value);
						if(valueInt>=400 && valueInt<=499){
							return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_4XX, tipoContext);
						}else if(valueInt>=500 && valueInt<=599){
							return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
						}else{
							return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
						}
					}else{
						return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext); // ???
					}
				}
				
			}catch(Exception e){
				this.log.error("Errore durante la comprensione dell'esito: "+e.getMessage(),e);
			}
			
			// se arrivo qua provo a vedere se siamo nel caso di un internal Error
			try{
				if(OpenSPCoop2MessageFactory.isFaultJsonMessage(jsonBody, this.log)) {
					return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX, tipoContext);
				}
			}catch(Exception e){
				this.log.error("Errore durante la comprensione dell'esito: "+e.getMessage(),e);
			}
			
		}
			
		return null;
	}
	
}
