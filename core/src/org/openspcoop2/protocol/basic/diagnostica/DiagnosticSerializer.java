/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.basic.diagnostica;

import java.io.ByteArrayOutputStream;

import org.openspcoop2.core.diagnostica.utils.XMLUtils;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.XMLRootElement;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.w3c.dom.Element;

/**
 * XMLDiagnosticoBuilder
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DiagnosticSerializer extends BasicComponentFactory implements org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticSerializer {

	protected org.openspcoop2.message.xml.MessageXMLUtils xmlUtils;

	private boolean prettyDocument = false;
	private boolean omitXmlDeclaration = false;

	public DiagnosticSerializer(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		super(protocolFactory);
		this.xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.DEFAULT;
	}
	
	
	@Override
	public void setPrettyDocument(boolean v) {
		this.prettyDocument = v;
	}
	@Override
	public boolean isPrettyDocument() {
		return this.prettyDocument;
	}
	@Override
	public void setOmitXmlDeclaration(boolean v) {
		this.omitXmlDeclaration = v;
	}
	@Override
	public boolean isOmitXmlDeclaration() {
		return this.omitXmlDeclaration;
	}
	
	

	/* --------------------- MESSAGGI DIAGNOSTICI -----------------------*/

	@Override
	public Element toElement(MsgDiagnostico msgDiag) throws ProtocolException{
		Element el = null;
		String tmpId = null;
		try{
						
			if(msgDiag.sizeProperties()>0){
				tmpId = msgDiag.removeProperty(DiagnosticDriver.IDDIAGNOSTICI); // non deve essere serializzato
			}
			
			// serializzazione
			byte[] xmlDiagnostico = XMLUtils.generateMessaggioDiagnostico(msgDiag.getMessaggioDiagnostico());
			el = this.xmlUtils.newElement(xmlDiagnostico);

		} catch(Exception e) {
			logAndThrowError(e, "DiagnosticSerializer.toElement error");
		}
		finally{
			if(tmpId!=null && msgDiag!=null){
				msgDiag.addProperty(DiagnosticDriver.IDDIAGNOSTICI, tmpId);
			}
		}
		return el;
	}
	
	private void logAndThrowError(Exception e, String msg) throws ProtocolException {
		String er = msg+": "+e.getMessage();
		this.log.error(er,e);
		throw new ProtocolException(er,e);
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
		
		ByteArrayOutputStream ret = null;
		String tmpId = null;
		try{
		
			if(msgDiag.sizeProperties()>0){
				tmpId = msgDiag.removeProperty(DiagnosticDriver.IDDIAGNOSTICI); // non deve essere serializzato
			}
			
			switch (tipoSerializzazione) {
				case XML:
				case DEFAULT:
					
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					XMLUtils.generateMessaggioDiagnostico(msgDiag.getMessaggioDiagnostico(),bout,this.prettyDocument,this.omitXmlDeclaration);
					bout.flush();
					bout.close();
					ret = bout;
					break;
	
				case JSON:
					
					bout = new ByteArrayOutputStream();
					String s = XMLUtils.generateMessaggioDiagnosticoAsJson(msgDiag.getMessaggioDiagnostico(),this.prettyDocument);
					bout.write(s.getBytes());
					bout.flush();
					bout.close();
					ret = bout;
					break;
			}
			
			if(ret==null) {
				throw new ProtocolException("Tipo ["+tipoSerializzazione+"] Non gestito");
			}
			
		} catch(Exception e) {
			logAndThrowError(e, "DiagnosticSerializer.toString error");
		}
		finally{
			if(tmpId!=null && msgDiag!=null){
				msgDiag.addProperty(DiagnosticDriver.IDDIAGNOSTICI, tmpId);
			}
		}
		
		return ret;
	}

	@Override
	public XMLRootElement getXMLRootElement() throws ProtocolException {
		return new DiagnosticXMLRootElement();
	}
}