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

package org.openspcoop2.pdd.core.integrazione;


import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;


/**
 * Classe utilizzata per la spedizione di informazioni di integrazione 
 * dalla porta di dominio verso i servizi applicativi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePASoapWithResponseOut extends GestoreIntegrazionePASoap{

	
	@Override
	public void setOutResponseHeader(HeaderIntegrazione integrazione,
			OutResponsePAMessage outResponsePAMessage) throws HeaderIntegrazioneException{
		
		try{
			
			SOAPHeaderElement header = this.utilities.buildHeader(integrazione,
					this.openspcoopProperties.getHeaderSoapNameIntegrazione(), // header name 
					this.openspcoopProperties.getHeaderSoapPrefixIntegrazione(), // prefix
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(), // namespace
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(), // actor
					outResponsePAMessage.getMessage(),
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione(), // nomeElemento ExtInfoProtocol
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione(), // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outResponsePAMessage.getBustaRichiesta(), false, TipoIntegrazione.SOAP)
				);
			
			if(outResponsePAMessage.getMessage().getSOAPHeader() == null){
				outResponsePAMessage.getMessage().getSOAPPart().getEnvelope().addHeader();
			}
			outResponsePAMessage.getMessage().addHeaderElement(outResponsePAMessage.getMessage().getSOAPHeader(), header);
			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePASoap, "+e.getMessage(),e);
		}
		
	}
}