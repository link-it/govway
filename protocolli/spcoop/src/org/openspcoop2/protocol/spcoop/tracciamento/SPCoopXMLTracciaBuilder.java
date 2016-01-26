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



package org.openspcoop2.protocol.spcoop.tracciamento;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.protocol.basic.tracciamento.XMLTracciaBuilder;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.tracciamento.IXMLTracciaBuilder;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.spcoop.builder.SPCoopImbustamento;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.tracciamento.IXMLTracciaBuilder} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopXMLTracciaBuilder extends XMLTracciaBuilder implements IXMLTracciaBuilder {
	
	private SPCoopImbustamento imbustamento = null;
	
	public SPCoopXMLTracciaBuilder(IProtocolFactory factory) throws ProtocolException{
		super(factory);
		this.imbustamento = new SPCoopImbustamento(factory);
	}
	
	@Override
	public SOAPElement toElement(Traccia tracciaObject)
			throws ProtocolException {
		try{
			OpenSPCoop2MessageFactory fac = OpenSPCoop2MessageFactory.getMessageFactory();
			
			SOAPFactory sf = SoapUtils.getSoapFactory(SOAPVersion.SOAP11);
			SOAPElement traccia = sf.createElement("traccia","eGov_IT_Trac","http://www.cnipa.it/schemas/2003/eGovIT/Tracciamento1_0/");
			
			SOAPElement GDO =  traccia.addChildElement("GDO","eGov_IT_Trac","http://www.cnipa.it/schemas/2003/eGovIT/Tracciamento1_0/");
			if(tracciaObject.getGdo()==null){
				GDO.setValue(this.factory.createTraduttore().getDate_protocolFormat());
			}else{
				GDO.setValue(this.factory.createTraduttore().getDate_protocolFormat(tracciaObject.getGdo()));
			}


			SOAPElement IdentificativoPorta = traccia.addChildElement("IdentificativoPorta","eGov_IT_Trac","http://www.cnipa.it/schemas/2003/eGovIT/Tracciamento1_0/");
			IdentificativoPorta.setValue(tracciaObject.getIdSoggetto().getCodicePorta());

			SOAPElement TipoMessaggio = traccia.addChildElement("TipoMessaggio","eGov_IT_Trac","http://www.cnipa.it/schemas/2003/eGovIT/Tracciamento1_0/");
			TipoMessaggio.setValue(tracciaObject.getTipoMessaggio().toString());


			SOAPElement hdrEGov = null;
			Busta busta = tracciaObject.getBusta();
			SOAPElement bustaInDom = tracciaObject.getBustaAsElement();
			String bustaAsString = tracciaObject.getBustaAsString();
			byte[] bustaInByte = tracciaObject.getBustaAsByteArray();
			if(bustaInDom!=null){
				// Tracciamento dall'oggetto dom
				hdrEGov = fac.createEmptySOAPMessage(SOAPVersion.SOAP11).cleanXSITypes(bustaInDom);
			}else if(bustaAsString != null) {
				// Tracciamento dai byte di una Busta
				hdrEGov = fac.createMessage(SOAPVersion.SOAP11).createSOAPElement(bustaAsString.getBytes());
			}else if(bustaInByte != null) {
				// Tracciamento dai byte di una Busta
				hdrEGov = fac.createMessage(SOAPVersion.SOAP11).createSOAPElement(bustaInByte);
			}else if(busta!=null){
				// Tracciamento dall'oggetto Busta
				hdrEGov = this.imbustamento.build_eGovHeader(null, busta, false, true); 
			}
			else{
				throw new NullPointerException("Busta non fornita in alcun modo");
			}
			if(hdrEGov == null){
				throw new ProtocolException("XMLBuilder.buildElement_Tracciamento fallito");
			}
			traccia.addChildElement(hdrEGov);

			return  traccia;

		} catch(Exception e) {
			this.log.error("XMLTracciaBuilder error: "+e.getMessage(),e);
			throw new ProtocolException("XMLTracciaBuilder error: "+e.getMessage(),e);
		}
	}
	
}