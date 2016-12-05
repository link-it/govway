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

package org.openspcoop2.protocol.basic.diagnostica;

import java.io.ByteArrayOutputStream;

import org.openspcoop2.core.diagnostica.utils.XMLUtils;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.w3c.dom.Element;

/**
 * XMLDiagnosticoBuilder
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 12359 $, $Date: 2016-11-18 17:24:57 +0100 (Fri, 18 Nov 2016) $
 */
public class DiagnosticSerializer extends BasicComponentFactory implements org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer {

	protected org.openspcoop2.message.xml.XMLUtils xmlUtils;

	public DiagnosticSerializer(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		super(protocolFactory);
		this.xmlUtils = org.openspcoop2.message.xml.XMLUtils.getInstance();
	}

	/* --------------------- MESSAGGI DIAGNOSTICI -----------------------*/

	@Override
	public Element toElement(MsgDiagnostico msgDiag) throws ProtocolException{
		String tmpId = null;
		try{
						
			if(msgDiag.sizeProperties()>0){
				tmpId = msgDiag.removeProperty(DiagnosticDriver.IDDIAGNOSTICI); // non deve essere serializzato
			}
			
			// serializzazione
			byte[] xmlDiagnostico = XMLUtils.generateMessaggioDiagnostico(msgDiag.getMessaggioDiagnostico());
			Element diagnostico = this.xmlUtils.newElement(xmlDiagnostico);

			return  diagnostico;

		} catch(Exception e) {
			this.log.error("DiagnosticSerializer.toElement error: "+e.getMessage(),e);
			throw new ProtocolException("DiagnosticSerializer.toElement error: "+e.getMessage(),e);
		}
		finally{
			if(tmpId!=null && msgDiag!=null){
				msgDiag.addProperty(DiagnosticDriver.IDDIAGNOSTICI, tmpId);
			}
		}
	}

	@Override
	public String toString(MsgDiagnostico msgDiag, TipoSerializzazione tipoSerializzazione) throws ProtocolException {
		return this.toByteArrayOutputStream(msgDiag, tipoSerializzazione).toString();
	}

	@Override
	public byte[] toByteArray(MsgDiagnostico msgDiag, TipoSerializzazione tipoSerializzazione) throws ProtocolException {
		return this.toByteArrayOutputStream(msgDiag, tipoSerializzazione).toByteArray();
	}
	
	protected ByteArrayOutputStream toByteArrayOutputStream(MsgDiagnostico msgDiag, TipoSerializzazione tipoSerializzazione) throws ProtocolException {
		
		String tmpId = null;
		try{
		
			if(msgDiag.sizeProperties()>0){
				tmpId = msgDiag.removeProperty(DiagnosticDriver.IDDIAGNOSTICI); // non deve essere serializzato
			}
			
			switch (tipoSerializzazione) {
				case XML:
				case DEFAULT:
					
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					XMLUtils.generateMessaggioDiagnostico(msgDiag.getMessaggioDiagnostico(),bout);
					bout.flush();
					bout.close();
					return bout;
	
				case JSON:
					
					bout = new ByteArrayOutputStream();
					String s = XMLUtils.generateMessaggioDiagnosticoAsJson(msgDiag.getMessaggioDiagnostico());
					bout.write(s.getBytes());
					bout.flush();
					bout.close();
					return bout;
			}
			
			throw new Exception("Tipo ["+tipoSerializzazione+"] Non gestito");
			
		} catch(Exception e) {
			this.log.error("DiagnosticSerializer.toString error: "+e.getMessage(),e);
			throw new ProtocolException("DiagnosticSerializer.toString error: "+e.getMessage(),e);
		}
		finally{
			if(tmpId!=null && msgDiag!=null){
				msgDiag.addProperty(DiagnosticDriver.IDDIAGNOSTICI, tmpId);
			}
		}
	}
}