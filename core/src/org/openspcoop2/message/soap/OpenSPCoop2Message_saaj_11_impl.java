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

package org.openspcoop2.message.soap;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.commons.io.input.CountingInputStream;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;

/**
 * Implementazione dell'OpenSPCoop2Message
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class OpenSPCoop2Message_saaj_11_impl extends AbstractOpenSPCoop2Message_saaj_impl
{	
	
	/* Costruttori */
				
	public OpenSPCoop2Message_saaj_11_impl(OpenSPCoop2MessageFactory messageFactory) {	
		super(messageFactory, new Message1_1_FIX_Impl());
	}
	
	public OpenSPCoop2Message_saaj_11_impl(OpenSPCoop2MessageFactory messageFactory, MimeHeaders mhs, InputStream is) throws SOAPException, IOException{
		super(messageFactory, new Message1_1_FIX_Impl(mhs, new CountingInputStream(is)));
	}
	
	public OpenSPCoop2Message_saaj_11_impl(OpenSPCoop2MessageFactory messageFactory, SOAPMessage msg) {	
		//TODO questo costruttore non funziona con messaggi con attachment. 
		//C'e' un bug nell'implementazione della sun che non copia gli attachment
		//In particolare il parametro super.mimePart (protetto non accessibile).
		// Per questo motivo essite la classe 1_1 FIX che utilizza direttamente il messaggio fornito 
		super(messageFactory, new Message1_1_FIX_Impl(msg));
	}
	
	
	/* Initialize ed internal Message Impl */
	
	public void initialize(long overhead){
		getMessage1_1_FIX_Impl().setLazyAttachments(false);
		this.incomingsize = getMessage1_1_FIX_Impl().getCountingInputStream().getByteCount() - overhead;
	}

	private Message1_1_FIX_Impl getMessage1_1_FIX_Impl(){
		return ((Message1_1_FIX_Impl)this._getSoapMessage());
	}
	
	
	
	/* ContentType */
	
	@Override
	protected String _super_getContentType() {
		try {
			return _getContentType(false);
		} catch (Throwable e) {
			// Non dovrebbe avvenire errori
		}	return getMessage1_1_FIX_Impl().getContentType();
	}
	
	@Override
	public void setContentType(String type) throws MessageException{
		getMessage1_1_FIX_Impl().setContentType(type);
	}
	
	@Override
	public String getContentType() throws MessageException{
		return _getContentType(true);
	}
	private String _getContentType(boolean includeContentTypeParameters) throws MessageException{
		
		try {
			String ct = getMessage1_1_FIX_Impl().getContentType();
			ContentType cType = new ContentType(ct);
			if(cType.getBaseType().equalsIgnoreCase(HttpConstants.CONTENT_TYPE_MULTIPART_RELATED)) {
				if(getMessage1_1_FIX_Impl().getMimeMultipart() != null)
					cType.setParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_BOUNDARY, 
							getMessage1_1_FIX_Impl().getMimeMultipart().getContentType().getParameter(HttpConstants.CONTENT_TYPE_MULTIPART_PARAMETER_BOUNDARY));
			}
			if(includeContentTypeParameters) {
				return ContentTypeUtilities.buildContentType(cType.toString(),this.contentTypeParamaters);	
			}
			else {
				return cType.toString();
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			try{
				String ct = getMessage1_1_FIX_Impl().getContentType();
				if(includeContentTypeParameters) {
					return ContentTypeUtilities.buildContentType(ct,this.contentTypeParamaters);
				}
				else {
					return ct;
				}
			}catch(Exception eInternal){
				throw new RuntimeException(eInternal.getMessage(),eInternal);
			}
		}
	}


}
