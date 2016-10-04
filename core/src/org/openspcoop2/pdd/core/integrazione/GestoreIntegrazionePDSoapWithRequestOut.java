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
 * Classe utilizzata per la ricezione di informazioni di integrazione 
 * dai servizi applicativi verso la porta di dominio.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePDSoapWithRequestOut extends GestoreIntegrazionePDSoap{

	
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPDMessage outRequestPDMessage) throws HeaderIntegrazioneException{
	
		try{
			
			SOAPHeaderElement header = this.utilities.buildHeader(integrazione, 
					this.openspcoopProperties.getHeaderSoapNameIntegrazione(), // header name 
					this.openspcoopProperties.getHeaderSoapPrefixIntegrazione(), // prefix
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(), // namespace
					this.openspcoopProperties.getHeaderSoapActorIntegrazione(), // actor
					outRequestPDMessage.getMessage(),
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione(), // nomeElemento ExtInfoProtocol
					this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione(), // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outRequestPDMessage.getBustaRichiesta(), true, TipoIntegrazione.SOAP)	
				);
			//System.out.println((new org.openspcoop.dao.message.OpenSPCoopMessageFactory().createMessage().getAsString(header)));
			if(outRequestPDMessage.getMessage().getSOAPHeader() == null){
				outRequestPDMessage.getMessage().getSOAPPart().getEnvelope().addHeader();
			}
			//outRequestPDMessage.getMessage().getSOAPHeader().addChildElement(header);
			outRequestPDMessage.getMessage().addHeaderElement(outRequestPDMessage.getMessage().getSOAPHeader(), header);
			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
		
	}
	

}
