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
package org.openspcoop2.pdd.core.behaviour;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * ExampleMultipleMessageWithResponseBustaToBehaviour
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ExampleMultipleMessageWithResponseBustaToBehaviour implements IBehaviour {

	@Override
	public Behaviour behaviour(GestoreMessaggi gestoreMessaggioRichiesta, Busta busta) throws CoreException {
		try{
			Behaviour behaviour = new Behaviour();
			
			BehaviourResponseTo responseTo = new BehaviourResponseTo();
			responseTo.setResponseTo(true);
			
			String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
					"<soapenv:Body><prova>CIAO</prova></soapenv:Body></soapenv:Envelope>";
			OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(SOAPVersion.SOAP11, xml);
			OpenSPCoop2Message msgReplyTo = pr.getMessage_throwParseException();
			responseTo.setMessage(msgReplyTo);
			
			Busta bustaRisposta = busta.invertiBusta(busta.getTipoOraRegistrazione(), busta.getTipoOraRegistrazioneValue());
			bustaRisposta.setTipoServizio(busta.getTipoServizio());
			bustaRisposta.setServizio(busta.getServizio());
			bustaRisposta.setAzione(busta.getAzione());
			bustaRisposta.setProfiloDiCollaborazione(busta.getProfiloDiCollaborazione(), busta.getProfiloDiCollaborazioneValue());
			bustaRisposta.setRiferimentoMessaggio(busta.getID());
			IDSoggetto mittente = new IDSoggetto(bustaRisposta.getTipoMittente(), bustaRisposta.getMittente());
			mittente.setCodicePorta(gestoreMessaggioRichiesta.getProtocolFactory().createTraduttore().getIdentificativoPortaDefault(mittente));
			bustaRisposta.setID(gestoreMessaggioRichiesta.getProtocolFactory().createBustaBuilder().newID(gestoreMessaggioRichiesta.getOpenspcoopstate().getStatoRichiesta(), 
					mittente, 
					(String) gestoreMessaggioRichiesta.getPddContext().getObject(Costanti.CLUSTER_ID), 
					false));
			responseTo.setBusta(bustaRisposta);
			
			behaviour.setResponseTo(responseTo);
			
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
