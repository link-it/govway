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
package org.openspcoop2.protocol.spcoop.backward_compatibility.services.skeleton;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.SoapOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * SPCoopExceptionSOAPFaultInterceptor
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopExceptionSOAPFaultInterceptor extends AbstractSoapInterceptor {

	public SPCoopExceptionSOAPFaultInterceptor() {
		 super(Phase.WRITE);
         this.addAfter(SoapOutInterceptor.class.getName());
	}

	@Override
	public void handleMessage(SoapMessage soapMessage) throws Fault {
		
		Object o = soapMessage.getContent(Exception.class);
		if( (o!=null) && (o instanceof Fault)){
			Fault fault = (Fault) o;
			
			if(fault.getOrCreateDetail()!=null){
			
				String codiceErrore = null;
				
				NodeList childsDetail = fault.getDetail().getChildNodes();
				if(childsDetail!=null){
					for (int i = 0; i < childsDetail.getLength(); i++) {
						Node n = childsDetail.item(i);
						if("org.openspcoop.pdd.services.SPCoopException".equals(n.getLocalName())){
							NodeList nChilds = n.getChildNodes();
							if(nChilds!=null){
								for (int j = 0; j < nChilds.getLength(); j++) {
									Node nChild = nChilds.item(j);
									if("codiceEccezione".equals(nChild.getLocalName())){
										codiceErrore = nChild.getTextContent();
									}
								}
							}
						}
					}
				}
				
				if(codiceErrore!=null){
					fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", codiceErrore, "soapenv"));
				}
				
			}
		}
	}


}
