/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;

import org.apache.log4j.Logger;
import org.openspcoop2.core.eccezione.errore_applicativo.Eccezione;
import org.openspcoop2.core.eccezione.errore_applicativo.ErroreApplicativo;
import org.openspcoop2.core.eccezione.errore_applicativo.utils.XMLUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.Esito;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
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
	
	public EsitoBuilder(IProtocolFactory protocolFactory){
		this.log = protocolFactory.getLogger();
		this.factory = protocolFactory;
	}
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.factory;
	}

	@Override
	public Esito getEsito(OpenSPCoop2Message message) throws ProtocolException {
		return getEsito(message,null);
	}

	@Override
	public Esito getEsito(OpenSPCoop2Message message,
			ProprietaErroreApplicativo erroreApplicativo)
			throws ProtocolException {
		try{
			SOAPBody body = message.getSOAPBody();
			
			ITraduttore trasl = this.factory.createTraduttore();
			
			if(body==null){
				return Esito.OK; // oneway
			}
			else{
				if(body.hasFault()){
					SOAPFault fault = body.getFault();
					String actor = fault.getFaultActor();
					String reason = fault.getFaultString();
					String codice = fault.getFaultCodeAsQName().getLocalPart();	
					//System.out.println("ACTOR["+actor+"] REASON["+reason+"] CODICE["+codice+"]");	

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
							return Esito.ERRORE_GENERICO;
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
										return Esito.ERRORE_PROCESSAMENTO_PDD_4XX;
									}else if(valueInt>=500 && valueInt<=599){
										return Esito.ERRORE_PROCESSAMENTO_PDD_5XX;
									}else{
										return Esito.ERRORE_GENERICO;
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
									return Esito.ERRORE_GENERICO;
								}
							}else{
								// EccezioneBusta
								return Esito.ERRORE_PROTOCOLLO;
							}
						}

					}
					else{

						if("Client".equals(codice) && trasl.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE).equals(reason) ){
							return Esito.ERRORE_PROTOCOLLO;
						}
						else if("Server".equals(codice) && trasl.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO).equals(reason) ){
							return Esito.ERRORE_PROTOCOLLO;
						}
						else{
							return Esito.ERRORE_APPLICATIVO;
						}

					}

				}
				else{
					return getEsitoMessaggioApplicativo(erroreApplicativo, body);
				}
			}
		}catch(Exception e){
			throw new ProtocolException("Comprensione stato non riuscita: "+e.getMessage(),e);
		}
	}

	
	public Esito getEsitoMessaggioApplicativo(ProprietaErroreApplicativo erroreApplicativo,SOAPBody body){
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
								return Esito.ERRORE_PROTOCOLLO;
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
										return Esito.ERRORE_PROCESSAMENTO_PDD_4XX;
									}else if(valueInt>=500 && valueInt<=599){
										return Esito.ERRORE_PROCESSAMENTO_PDD_5XX;
									}else{
										return Esito.ERRORE_GENERICO;
									}
								}else{
									return Esito.ERRORE_GENERICO; // ???
								}
							}
							
						}catch(Exception e){
							this.log.error("Errore durante la comprensione dell'esito: "+e.getMessage(),e);
						}
						
					}
					
//					if("errore-applicativo".equals(childNode.getLocalName()) &&
//							org.openspcoop2.core.eccezione.errore_applicativo.constants.Costanti.ROOT_LOCAL_NAME_ERRORE_APPLICATIVO.equals(childNode.getNamespaceURI())){
//						NodeList elements = childNode.getChildNodes();
//						if(elements!=null){
//							for(int i=0;i<elements.getLength();i++){
//								Node elem = elements.item(i);
//								if("eccezione".equals(elem.getLocalName())){
//									NodeList elementsEccezione = elem.getChildNodes();
//									if(elementsEccezione!=null){
//										for(int j=0;j<elementsEccezione.getLength();j++){
//											Node tipoEccezione = elementsEccezione.item(j);
//											if(org.openspcoop2.protocol.basic.Costanti.ECCEZIONE_INTEGRAZIONE.equals(tipoEccezione.getLocalName())){
//												NamedNodeMap attr = tipoEccezione.getAttributes();
//												if(attr!=null){
//													Node at = attr.getNamedItem("codiceEccezione");
//													if(at!=null){
//														String value = at.getNodeValue();
//														String prefixFaultCode = erroreApplicativo.getFaultPrefixCode();
//														if(prefixFaultCode==null){
//															prefixFaultCode="OPENSPCOOP2_ORG_";
//														}
//														if(value.startsWith(prefixFaultCode)){
//															value = value.substring(prefixFaultCode.length());
//															int valueInt = Integer.parseInt(value);
//															if(valueInt>=400 && valueInt<=499){
//																return Esito.ERRORE_PROCESSAMENTO_PDD_4XX;
//															}else if(valueInt>=500 && valueInt<=599){
//																return Esito.ERRORE_PROCESSAMENTO_PDD_5XX;
//															}else{
//																return Esito.ERRORE_GENERICO;
//															}
//														}else{
//															return Esito.ERRORE_GENERICO; // ???
//														}
//													}
//												}
//											}
//											else if(org.openspcoop2.protocol.basic.Costanti.ECCEZIONE_PROTOCOLLO.equals(tipoEccezione.getLocalName())){
//												return Esito.ERRORE_PROTOCOLLO;
//											}
//										}
//									}
//								}
//							}
//						}
//					}
				}
			}
		}

		return Esito.OK;
	}
	
}
