/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.integrazione.backward_compatibility;

import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazioneException;
import org.openspcoop2.pdd.core.integrazione.OutRequestPDMessage;
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
public class AbstractGestoreIntegrazionePDSoapWithRequestOutBC extends AbstractGestoreIntegrazionePDSoapBC{

	
	
	public AbstractGestoreIntegrazionePDSoapWithRequestOutBC(boolean openspcoop2) {
		super(openspcoop2);
	}

	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPDMessage outRequestPDMessage) throws HeaderIntegrazioneException{
	
		try{
			OpenSPCoop2Message msg = outRequestPDMessage.getMessage();
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())==false){
				throw new Exception("Non utilizzabile con un Service Binding Rest");
			}
			OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
			
			SOAPHeaderElement header = this.utilitiesResponseBC.buildHeader(integrazione, 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapNameIntegrazione_backwardCompatibility_openspcoop1(), // header name 
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapPrefixIntegrazione_backwardCompatibility_openspcoop1(), // prefix
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(), // namespace
					this.openspcoop2 ? 
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapActorIntegrazione_backwardCompatibility_openspcoop1(), // actor
					soapMsg,
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeElementoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeElemento ExtInfoProtocol
					this.openspcoop2 ?
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop2() :
							this.openspcoopProperties.getHeaderSoapExtProtocolInfoNomeAttributoIntegrazione_backwardCompatibility_openspcoop1()	, // nomeAttributo ExtInfoProtocol
					this.getProtocolFactory().createProtocolManager().buildIntegrationProperties(outRequestPDMessage.getBustaRichiesta(), true, TipoIntegrazione.SOAP)	
				);
			//System.out.println((new org.openspcoop.dao.message.OpenSPCoopMessageFactory().createMessage().getAsString(header)));
			if(soapMsg.getSOAPHeader() == null){
				soapMsg.getSOAPPart().getEnvelope().addHeader();
			}
			soapMsg.addHeaderElement(soapMsg.getSOAPHeader(), header);
			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDSoap, "+e.getMessage(),e);
		}
		
	}
	

}
