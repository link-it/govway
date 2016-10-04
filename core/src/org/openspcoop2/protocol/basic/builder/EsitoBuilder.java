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

package org.openspcoop2.protocol.basic.builder;

import java.util.Hashtable;
import java.util.List;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;

import org.slf4j.Logger;
import org.openspcoop2.core.eccezione.errore_applicativo.Eccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo;
import org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
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
import org.openspcoop2.utils.resources.TransportRequestContext;
import org.w3c.dom.Node;

/**	
 * EsitoBuilder
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoBuilder implements org.openspcoop2.protocol.sdk.builder.IEsitoBuilder {
	
	protected Logger log;
	protected IProtocolFactory factory;
	protected EsitiProperties esitiProperties;
	
	public EsitoBuilder(IProtocolFactory protocolFactory) throws ProtocolException{
		this.log = protocolFactory.getLogger();
		this.factory = protocolFactory;
		this.esitiProperties = EsitiProperties.getInstance(this.log);
	}
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.factory;
	}

	protected String getTipoContext(TransportRequestContext transportRequestContext) throws ProtocolException{
		String tipoContext = CostantiProtocollo.ESITO_TRANSACTION_CONTEXT_STANDARD;
		
		if(transportRequestContext!=null){
		
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
	
	private EsitoTransazione getEsitoGenerale(InformazioniErroriInfrastrutturali informazioniErroriInfrastrutturali, String tipoContext) throws ProtocolException{
		if(informazioniErroriInfrastrutturali.isRicevutoSoapFaultServerPortaDelegata()){
			return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_SERVER, tipoContext);
		}
		else{
			return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_GENERICO, tipoContext);
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
	public EsitoTransazione getEsito(TransportRequestContext transportRequestContext, OpenSPCoop2Message message,
			InformazioniErroriInfrastrutturali informazioniErroriInfrastrutturali, Hashtable<String, Object> context) throws ProtocolException {
		return getEsito(transportRequestContext,message,null,informazioniErroriInfrastrutturali,context);
	}

	@Override
	public EsitoTransazione getEsito(TransportRequestContext transportRequestContext, OpenSPCoop2Message message,
			ProprietaErroreApplicativo erroreApplicativo,
			InformazioniErroriInfrastrutturali informazioniErroriInfrastrutturali, Hashtable<String, Object> context)
			throws ProtocolException {
		try{
			
					
			SOAPBody body = null;
			if(message!=null){
				body = message.getSOAPBody();
			}
			
			if(informazioniErroriInfrastrutturali==null){
				// inizializzo con valori di default
				informazioniErroriInfrastrutturali = new InformazioniErroriInfrastrutturali();
			}
			
			ITraduttore trasl = this.factory.createTraduttore();
			
			String tipoContext = this.getTipoContext(transportRequestContext);
			
			// Emissione diagnostici di livello error
			boolean emissioneDiagnosticiError = (context!=null && context.containsKey(org.openspcoop2.core.constants.Costanti.EMESSI_DIAGNOSTICI_ERRORE));
			
			// Tipo di esito OK
			EsitoTransazioneName esitoOK = EsitoTransazioneName.OK;
			if(emissioneDiagnosticiError){
				esitoOK = EsitoTransazioneName.OK_PRESENZA_ANOMALIE;
			}
			
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
											return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.CUSTOM, customCode, tipoContext);
										}
									}
								}catch(Throwable t){
									this.log.error("Errore durante la comprensione dell'esito: "+t.getMessage(),t);
								}
							}
						}
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
			else if(body==null){
				return this.esitiProperties.convertToEsitoTransazione(esitoOK, tipoContext); // oneway
			}
			else{
				if(body.hasFault()){
					SOAPFault fault = body.getFault();
					String actor = fault.getFaultActor();
					String reason = fault.getFaultString();
					String codice = fault.getFaultCodeAsQName().getLocalPart();	
					String namespaceCodice = fault.getFaultCodeAsQName().getNamespaceURI();
					//System.out.println("ACTOR["+actor+"] REASON["+reason+"] CODICE["+codice+"] namespaceCodice["+namespaceCodice+"]");	

					Object backwardCompatibilityActorObject = message.getContextProperty(CostantiProtocollo.BACKWARD_COMPATIBILITY_ACTOR);
					String backwardCompatibilityActor = null;
					if(backwardCompatibilityActorObject!=null){
						backwardCompatibilityActor = (String) backwardCompatibilityActorObject;
					}
					
					boolean faultActorOpenSPCoopV2 = (erroreApplicativo!=null &&
							erroreApplicativo.getFaultActor()!=null &&
							erroreApplicativo.getFaultActor().equals(actor));
					
					boolean faultActorBackwardCompatibility = (backwardCompatibilityActor!=null &&
							backwardCompatibilityActor.equals(actor));
					
					if(faultActorOpenSPCoopV2 || faultActorBackwardCompatibility){

						// L'errore puo' essere generato dalla Porta di Dominio, l'errore puo' essere un :
						// msg di errore 4XX
						// msg di errore 5xx
						// msg dovuto alla ricezione di una busta.

						if(codice==null){
							// CASO NON PREVISTO ???
							return getEsitoGenerale(informazioniErroriInfrastrutturali, tipoContext);
						}
						else{
							String prefixFaultCode = erroreApplicativo.getFaultPrefixCode();
							if(prefixFaultCode==null){
								prefixFaultCode="OPENSPCOOP2_ORG_";
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
										return getEsitoGenerale(informazioniErroriInfrastrutturali, tipoContext);
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
									return getEsitoGenerale(informazioniErroriInfrastrutturali, tipoContext);
								}
							}else{
								// EccezioneBusta
								return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROTOCOLLO, tipoContext);
							}
						}

					}
					else{

						if("Client".equals(codice) && trasl.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE).equals(reason) ){
							return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROTOCOLLO, tipoContext);
						}
						else if("Server".equals(codice) && trasl.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO).equals(reason) ){
							return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROTOCOLLO, tipoContext);
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
								return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_SERVER, tipoContext);
							}
							else{
								return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_APPLICATIVO, tipoContext);
							}
						}

					}

				}
				else{
					return getEsitoMessaggioApplicativo(erroreApplicativo, body, tipoContext, esitoOK);
				}
			}
		}catch(Exception e){
			throw new ProtocolException("Comprensione stato non riuscita: "+e.getMessage(),e);
		}
	}

	
	protected EsitoTransazione getEsitoMessaggioApplicativo(ProprietaErroreApplicativo erroreApplicativo,SOAPBody body,String tipoContext, EsitoTransazioneName esitoOK) throws ProtocolException{
		if(erroreApplicativo!=null){
			Node childNode = body.getFirstChild();
			if(childNode!=null){
				if(childNode.getNextSibling()==null){
					
					if(XMLUtils.isErroreApplicativo(childNode)){
						
						try{
							byte[] xml = org.openspcoop2.message.XMLUtils.getInstance().toByteArray(childNode,true);
							ErroreApplicativo erroreApplicativoObject = XMLUtils.getErroreApplicativo(this.log, xml);
							Eccezione ecc = erroreApplicativoObject.getEccezione();
							if(org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.TIPO_ECCEZIONE_PROTOCOLLO.equals(ecc.getTipo())){
								return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROTOCOLLO, tipoContext);
							}
							else{
								String value = ecc.getCodice().getBase();
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
										return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_GENERICO, tipoContext);
									}
								}else{
									return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_GENERICO, tipoContext); // ???
								}
							}
							
						}catch(Exception e){
							this.log.error("Errore durante la comprensione dell'esito: "+e.getMessage(),e);
						}
						
					}
					
				}
			}
		}

		return this.esitiProperties.convertToEsitoTransazione(esitoOK, tipoContext);

	}
	
}
