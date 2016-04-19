/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.protocol.spcoop.builder;

import javax.xml.soap.SOAPBody;

import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.basic.builder.EsitoBuilder;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IEsitoBuilder}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopEsitoBuilder extends EsitoBuilder {

	public SPCoopEsitoBuilder(IProtocolFactory protocolFactory) throws ProtocolException {
		super(protocolFactory);
	}
	
	@Override
	public EsitoTransazione getEsitoMessaggioApplicativo(ProprietaErroreApplicativo erroreApplicativo,SOAPBody body,String tipoContext, EsitoTransazioneName esitoOK) throws ProtocolException{
		Node childNode = body.getFirstChild();
		if(childNode!=null){
			if(childNode.getNextSibling()==null){

				if("MessaggioDiErroreApplicativo".equals(childNode.getLocalName()) &&
						SPCoopCostanti.NAMESPACE_ECCEZIONE_APPLICATIVA_EGOV.equals(childNode.getNamespaceURI())){
					NodeList elements = childNode.getChildNodes();
					if(elements!=null){
						for(int i=0;i<elements.getLength();i++){
							Node elem = elements.item(i);
							if("Eccezione".equals(elem.getLocalName())){
								NodeList elementsEccezione = elem.getChildNodes();
								if(elementsEccezione!=null){
									for(int j=0;j<elementsEccezione.getLength();j++){
										Node tipoEccezione = elementsEccezione.item(j);
										if(SPCoopCostanti.ECCEZIONE_PROCESSAMENTO_SPCOOP.equals(tipoEccezione.getLocalName())){
											NamedNodeMap attr = tipoEccezione.getAttributes();
											if(attr!=null){
												Node at = attr.getNamedItem("codiceEccezione");
												if(at!=null){
													String value = at.getNodeValue();
													String prefixFaultCode = null;
													if(erroreApplicativo!=null)
														prefixFaultCode = erroreApplicativo.getFaultPrefixCode();
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
											}
										}
										else if(SPCoopCostanti.ECCEZIONE_VALIDAZIONE_BUSTA_SPCOOP.equals(tipoEccezione.getLocalName())){
											return this.esitiProperties.convertToEsitoTransazione(EsitoTransazioneName.ERRORE_PROTOCOLLO, tipoContext);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return this.esitiProperties.convertToEsitoTransazione(esitoOK, tipoContext);
	}

}
