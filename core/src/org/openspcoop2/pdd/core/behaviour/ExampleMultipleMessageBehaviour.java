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
package org.openspcoop2.pdd.core.behaviour;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * ExampleMultipleMessageBehaviour
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExampleMultipleMessageBehaviour implements IBehaviour {

	@Override
	public Behaviour behaviour(GestoreMessaggi gestoreMessaggioRichiesta, Busta busta) throws CoreException {
		try{
			Behaviour behaviour = new Behaviour();
			
			for (int i = 0; i < 3; i++) {
				
				BehaviourForwardTo forwardTo = new BehaviourForwardTo();
				
				OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getMessageFactory().createEmptySOAPMessage(SOAPVersion.SOAP11);
				if(msg.getSOAPHeader()==null){
					msg.getSOAPPart().getEnvelope().addHeader();
				}
				QName qName = new QName("http://www.openspcoop2.org/example", "example");
				SOAPHeaderElement header = msg.getSOAPHeader().addHeaderElement(qName);
				msg.addHeaderElement(msg.getSOAPHeader(), header);
				forwardTo.setMessage(msg);
				
				behaviour.getForwardTo().add(forwardTo);	
				
			}
			
			return behaviour;
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
		
	}

}
