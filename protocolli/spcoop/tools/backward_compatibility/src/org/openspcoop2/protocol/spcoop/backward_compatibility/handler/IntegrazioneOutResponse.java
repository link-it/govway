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
package org.openspcoop2.protocol.spcoop.backward_compatibility.handler;


import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.OutResponseContext;
import org.openspcoop2.pdd.core.handlers.OutResponseHandler;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.BackwardCompatibilityProperties;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.CodeMapping;
import org.openspcoop2.protocol.spcoop.backward_compatibility.config.Costanti;
import org.openspcoop2.protocol.spcoop.backward_compatibility.services.BackwardCompatibilityStartup;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * IntegrazioneOutResponse
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrazioneOutResponse implements OutResponseHandler {

	@Override
	public void invoke(OutResponseContext context) throws HandlerException {

		

		try {

			if(BackwardCompatibilityStartup.initialized==false){
				return;
			}
			
			BackwardCompatibilityProperties backwardCompatibilityProperties = BackwardCompatibilityProperties.getInstance(true);
			
			if(!backwardCompatibilityProperties.getProtocolName().equals(context.getProtocolFactory().getProtocol())){
				return;
			}
			
			if( 
					(!backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata())
					||
					(backwardCompatibilityProperties.isSwitchOpenSPCoopV2PortaDelegata() && context.getPddContext().containsKey(Costanti.OPENSPCOOP2_BACKWARD_COMPATIBILITY))
				){
			
				OpenSPCoop2Message msg = context.getMessaggio();
				
				if(msg!=null && msg.getSOAPBody()!=null && msg.getSOAPBody().hasFault()){
					OpenSPCoop2Properties openspcoop2Properties = OpenSPCoop2Properties.getInstance();
					ProprietaErroreApplicativo erroreApplicativo = openspcoop2Properties.getProprietaGestioneErrorePD(context.getProtocolFactory().createProtocolManager());
					String pddFaultActor = erroreApplicativo.getFaultActor();
					
					if(pddFaultActor.equals(msg.getSOAPBody().getFault().getFaultActor())) {
				
						SOAPFault fault = msg.getSOAPBody().getFault();
						
						CodeMapping codeMapping = CodeMapping.getInstance();
						String codiceOriginale = fault.getFaultCodeAsQName().getLocalPart();
						String codiceErrore = codeMapping.toOpenSPCoopV1Code(codiceOriginale);
						
						if(fault.getDetail()!=null){
							
							NodeList childsDetail = fault.getDetail().getChildNodes();
							if(childsDetail!=null){
								for (int i = 0; i < childsDetail.getLength(); i++) {
									Node n = childsDetail.item(i);
									if("MessaggioDiErroreApplicativo".equals(n.getLocalName()) && "http://www.cnipa.it/schemas/2003/eGovIT/Exception1_0/".equals(n.getNamespaceURI()) ){
										NodeList nChilds = n.getChildNodes();
										if(nChilds!=null){
											for (int j = 0; j < nChilds.getLength(); j++) {
												Node nChild = nChilds.item(j);
												
												if("IdentificativoPorta".equals(nChild.getLocalName())){
													String idPorta = nChild.getTextContent();
													if("OpenSPCoop2SPCoopIT".equals(idPorta)){
														nChild.setTextContent(backwardCompatibilityProperties.getIdentificativoPortaDefault());
													}
													// Devo ritornare il vecchio valore dell'identificativo porta "OpenSPCoop" solo se l'utente non l'aveva ridefinito!
	//												if(openspcoop2Properties.getIdentificativoPortaDefault("spcoop").equals(idPorta)){
	//													nChild.setTextContent(backwardCompatibilityProperties.getIdentificativoPortaDefault());
	//												}
												}
												
												else if("Eccezione".equals(nChild.getLocalName())){
													NodeList nChildsEccezione = nChild.getChildNodes();
													if(nChildsEccezione!=null){
														for (int k = 0; k < nChildsEccezione.getLength(); k++) {
															Node nChildEccezione = nChildsEccezione.item(k);
															if("EccezioneProcessamento".equals(nChildEccezione.getLocalName())){
																NamedNodeMap attributi = nChildEccezione.getAttributes();
																attributi.getNamedItem("codiceEccezione").setTextContent(codiceErrore);
															}
														}
													}
												}
											}
										}
									}
								}
							}
	
						}
						
						fault.setFaultActor(backwardCompatibilityProperties.getFaultActor());
						msg.addContextProperty(CostantiProtocollo.BACKWARD_COMPATIBILITY_ACTOR, backwardCompatibilityProperties.getFaultActor());
						
						fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", codiceErrore, "soapenv"));
						msg.addContextProperty(CostantiProtocollo.BACKWARD_COMPATIBILITY_PREFIX_FAULT_CODE, backwardCompatibilityProperties.getPrefixFaultCode());
			
					}
				}
			}

		}catch(Exception e){

			throw new HandlerException(e.getMessage(),e);
		}

	}

}