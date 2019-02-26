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
package org.openspcoop2.pdd.core.behaviour;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * ExampleMultipleMessageWithResponseAndFilterToBehaviour
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExampleMultipleMessageWithResponseAndFilterToBehaviour implements IBehaviour {

	@Override
	public Behaviour behaviour(GestoreMessaggi gestoreMessaggioRichiesta, Busta busta,
			RequestInfo requestInfo) throws CoreException {
		try{
			Behaviour behaviour = new Behaviour();
			
			BehaviourResponseTo responseTo = new BehaviourResponseTo();
			responseTo.setResponseTo(true);
			behaviour.setResponseTo(responseTo);
			
			for (int i = 0; i < 3; i++) {
				
				BehaviourForwardTo forwardTo = new BehaviourForwardTo();
			
				if(i==1){
					// deny list
					BehaviourForwardToFilter filter = new BehaviourForwardToFilter();
					IDServizioApplicativo id = new IDServizioApplicativo();
					id.setIdSoggettoProprietario(new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario()));
					id.setNome("silGop3");
					filter.getDenyListServiziApplicativi().add(id);
					forwardTo.setFilter(filter);
				}
				else if(i==2){
					// access list
					BehaviourForwardToFilter filter = new BehaviourForwardToFilter();
					IDServizioApplicativo id = new IDServizioApplicativo();
					id.setIdSoggettoProprietario(new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario()));
					id.setNome("silGop3");
					filter.getAccessListServiziApplicativi().add(id);
					forwardTo.setFilter(filter);
				}
				
				OpenSPCoop2SoapMessage msg = OpenSPCoop2MessageFactory.getMessageFactory().createEmptyMessage(MessageType.SOAP_11, MessageRole.REQUEST).castAsSoap();
				if(msg.getSOAPHeader()==null){
					msg.getSOAPPart().getEnvelope().addHeader();
				}
				QName qName = new QName("http://www.openspcoop2.org/example", "example");
				SOAPHeaderElement header = msg.getSOAPHeader().addHeaderElement(qName);
				msg.addHeaderElement(msg.getSOAPHeader(), header);
				forwardTo.setMessage(msg);
				
				BehaviourForwardToConfiguration config = new BehaviourForwardToConfiguration();
				config.setSbustamentoInformazioniProtocollo(StatoFunzionalita.DISABILITATA);
				forwardTo.setConfig(config);
				
				behaviour.getForwardTo().add(forwardTo);	
				
			}
			
			return behaviour;
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
		
	}

}
