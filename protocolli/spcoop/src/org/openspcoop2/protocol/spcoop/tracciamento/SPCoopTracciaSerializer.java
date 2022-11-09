/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.spcoop.tracciamento;

import java.io.ByteArrayOutputStream;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeaderElement;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.protocol.basic.tracciamento.TracciaSerializer;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.spcoop.SPCoopBustaRawContent;
import org.openspcoop2.protocol.spcoop.builder.SPCoopImbustamento;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.tracciamento.ITracciaSerializer} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopTracciaSerializer extends TracciaSerializer implements ITracciaSerializer {
	
	private SPCoopImbustamento imbustamento = null;
	
	public SPCoopTracciaSerializer(IProtocolFactory<SOAPHeaderElement> factory) throws ProtocolException{
		super(factory);
		this.imbustamento = new SPCoopImbustamento(factory,null);
	}
	
	@Override
	public SOAPElement toElement(Traccia tracciaObject)
			throws ProtocolException {
		try{
			
			SOAPFactory sf = SoapUtils.getSoapFactory(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), MessageType.SOAP_11);
			SOAPElement traccia = sf.createElement("traccia","eGov_IT_Trac","http://www.cnipa.it/schemas/2003/eGovIT/Tracciamento1_0/");
			
			SOAPElement GDO =  traccia.addChildElement("GDO","eGov_IT_Trac","http://www.cnipa.it/schemas/2003/eGovIT/Tracciamento1_0/");
			if(tracciaObject.getGdo()==null){
				GDO.setValue(this.protocolFactory.createTraduttore().getDate_protocolFormat());
			}else{
				GDO.setValue(this.protocolFactory.createTraduttore().getDate_protocolFormat(tracciaObject.getGdo()));
			}


			SOAPElement IdentificativoPorta = traccia.addChildElement("IdentificativoPorta","eGov_IT_Trac","http://www.cnipa.it/schemas/2003/eGovIT/Tracciamento1_0/");
			IdentificativoPorta.setValue(tracciaObject.getIdSoggetto().getCodicePorta());

			SOAPElement TipoMessaggio = traccia.addChildElement("TipoMessaggio","eGov_IT_Trac","http://www.cnipa.it/schemas/2003/eGovIT/Tracciamento1_0/");
			TipoMessaggio.setValue(tracciaObject.getTipoMessaggio().toString());


			SOAPElement hdrEGov = null;
			Busta busta = tracciaObject.getBusta();
			BustaRawContent<?> bustaInDom = tracciaObject.getBustaAsRawContent();
			String bustaAsString = tracciaObject.getBustaAsString();
			byte[] bustaInByte = tracciaObject.getBustaAsByteArray();
			if(bustaInDom!=null){
				// Tracciamento dall'oggetto dom
				hdrEGov = ((SPCoopBustaRawContent) bustaInDom).getElement();
			}else if(bustaAsString != null) {
				// Tracciamento dai byte di una Busta
				hdrEGov = OpenSPCoop2MessageFactory.createSOAPElement(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),MessageType.SOAP_11,bustaAsString.getBytes());
			}else if(bustaInByte != null) {
				// Tracciamento dai byte di una Busta
				hdrEGov = OpenSPCoop2MessageFactory.createSOAPElement(OpenSPCoop2MessageFactory.getDefaultMessageFactory(),MessageType.SOAP_11,bustaInByte);
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
			this.log.error("TracciaSerializer.toElement error: "+e.getMessage(),e);
			throw new ProtocolException("TracciaSerializer.toElement error: "+e.getMessage(),e);
		}
	}
	
	
	@Override
	protected ByteArrayOutputStream toByteArrayOutputStream(Traccia traccia, TipoSerializzazione tipoSerializzazione) throws ProtocolException {
		
		try{
					
			switch (tipoSerializzazione) {
				case XML:
				case DEFAULT:
					
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					bout.write(org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT.toByteArray(this.toElement(traccia)));
					bout.flush();
					bout.close();
					return bout;
	
				default:
					
					throw new Exception("Tipo Serializzazione ["+tipoSerializzazione+"] Non gestito");
			}
			
		} catch(Exception e) {
			this.log.error("DiagnosticSerializer.toString error: "+e.getMessage(),e);
			throw new ProtocolException("DiagnosticSerializer.toString error: "+e.getMessage(),e);
		}

	}
	
}