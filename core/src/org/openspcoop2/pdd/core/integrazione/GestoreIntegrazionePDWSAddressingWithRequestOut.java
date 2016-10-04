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

import org.openspcoop2.message.OpenSPCoop2Message;

/**
 * Classe utilizzata per la ricezione di informazioni di integrazione 
 * dai servizi applicativi verso la porta di dominio.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreIntegrazionePDWSAddressingWithRequestOut extends GestoreIntegrazionePDWSAddressing{

	
	
	@Override
	public void setOutRequestHeader(HeaderIntegrazione integrazione,
			OutRequestPDMessage outRequestPDMessage) throws HeaderIntegrazioneException{
	
		try{
			
			OpenSPCoop2Message message = outRequestPDMessage.getMessage();
			if(message.getSOAPHeader() == null){
				message.getSOAPPart().getEnvelope().addHeader();
			}
			
			if(integrazione.getBusta()!=null){
				
				HeaderIntegrazioneBusta hBusta = integrazione.getBusta();
					
				if(hBusta.getDestinatario()!=null && hBusta.getServizio()!=null){
					
					// To
					SOAPHeaderElement wsaTO = UtilitiesIntegrazioneWSAddressing.buildWSATo(message,this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa", hBusta.getTipoDestinatario(),hBusta.getDestinatario(), hBusta.getTipoServizio(), hBusta.getServizio());
					message.addHeaderElement(message.getSOAPHeader(), wsaTO);
					
					// Action
					if(hBusta.getAzione()!=null){
						SOAPHeaderElement wsaAction = UtilitiesIntegrazioneWSAddressing.buildWSAAction(message,this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa",hBusta.getTipoDestinatario(),hBusta.getDestinatario(), hBusta.getTipoServizio(), hBusta.getServizio(),hBusta.getAzione());
						message.addHeaderElement(message.getSOAPHeader(), wsaAction);
					}
				}
				
				if(hBusta.getMittente()!=null){
					SOAPHeaderElement wsaFROM = UtilitiesIntegrazioneWSAddressing.buildWSAFrom(message,this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa",integrazione.getServizioApplicativo(),hBusta.getTipoMittente(),hBusta.getMittente());
					message.addHeaderElement(message.getSOAPHeader(), wsaFROM);
				}
					
				if(hBusta.getID()!=null){
					SOAPHeaderElement wsaID = UtilitiesIntegrazioneWSAddressing.buildWSAID(message,this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa",hBusta.getID());
					message.addHeaderElement(message.getSOAPHeader(), wsaID);
				}
				
				if(hBusta.getRiferimentoMessaggio()!=null || hBusta.getIdCollaborazione()!=null){
					String rif = hBusta.getRiferimentoMessaggio();
					if(rif==null){
						rif = hBusta.getIdCollaborazione();
					}
					SOAPHeaderElement wsaRelatesTo = UtilitiesIntegrazioneWSAddressing.buildWSARelatesTo(message,this.openspcoopProperties.getHeaderSoapActorIntegrazione()+"/wsa",rif);
					message.addHeaderElement(message.getSOAPHeader(), wsaRelatesTo);
				}
			}
			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("GestoreIntegrazionePDWSAddressing, "+e.getMessage(),e);
		}
		
	}


}