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

package org.openspcoop2.protocol.spcoop.builder;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IBustaBuilder} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopBustaBuilder implements org.openspcoop2.protocol.sdk.builder.IBustaBuilder {

	private SPCoopImbustamento spcoopImbustamento = null;
	private SPCoopSbustamento spcoopSbustamento = null;
	private Logger log = null;
	
	public SPCoopBustaBuilder(IProtocolFactory factory) throws ProtocolException{
		this.spcoopImbustamento = new SPCoopImbustamento(factory);
		this.spcoopSbustamento = new SPCoopSbustamento(factory);
		this.log = factory.getLogger();
	}
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.spcoopImbustamento.getProtocolFactory();
	}

	@Override
	public String newID(IState state, IDSoggetto idSoggetto, String idTransazione,
			Boolean isRichiesta)
			throws ProtocolException {
		return this.spcoopImbustamento.buildID(state, idSoggetto, idTransazione, isRichiesta);
	}

	@Override
	public Date extractDateFromID(String id) throws ProtocolException {
		try{
			
			// es. MinisteroErogatore_MinisteroErogatoreSPCoopIT_0000215_2011-08-04_09:43
			
			if(id==null){
				return null;
			}
			String [] split = id.split("_");
			if(split.length!=5){
				return null;
			}
			
			SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm"); // SimpleDateFormat non e' thread-safe
			String tmp = split[3]+"_"+split[4];
			Date d = dateformat.parse(tmp);
			return d;
			
		}catch(Exception e){
			throw new ProtocolException("Errore durante l'estrazione della data dall'identificativo ["+id+"]: "+ e.getMessage(),e);
		}
	}
	
	@Override
	public SOAPElement toElement(Busta busta,boolean isRichiesta) throws ProtocolException{
		try{
			OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getMessageFactory().createEmptySOAPMessage(SOAPVersion.SOAP11);
			return this.spcoopImbustamento.build_eGovHeader(msg, busta, false);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	@Override
	public String toString(Busta busta,boolean isRichiesta) throws ProtocolException{
		return Utilities.toString(this.log, this.toElement(busta, isRichiesta));
	}
	
	@Override
	public byte[] toByteArray(Busta busta,boolean isRichiesta) throws ProtocolException{
		return Utilities.toByteArray(this.log, this.toElement(busta, isRichiesta));
	}
	
	@Override
	public SOAPElement imbustamento(IState state, OpenSPCoop2Message msg, Busta busta,
			boolean isRichiesta,
			ProprietaManifestAttachments proprietaManifestAttachments)
			throws ProtocolException {
		return this.spcoopImbustamento.imbustamento(msg, busta, 
				isRichiesta, 
				proprietaManifestAttachments);
	}

	@Override
	public SOAPElement addTrasmissione(OpenSPCoop2Message message,
			Trasmissione trasmissione) throws ProtocolException {
		return this.spcoopImbustamento.addTrasmissione(message, trasmissione);
	}

	@Override
	public SOAPElement sbustamento(IState state, OpenSPCoop2Message msg, Busta busta,
			boolean isRichiesta, ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException {
		return this.spcoopSbustamento.sbustamento(msg, proprietaManifestAttachments);
	}

}
