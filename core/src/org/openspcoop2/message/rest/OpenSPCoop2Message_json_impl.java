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

package org.openspcoop2.message.rest;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi json
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_json_impl extends AbstractBaseOpenSPCoop2RestMessage<String> implements OpenSPCoop2RestJsonMessage {

	public OpenSPCoop2Message_json_impl(OpenSPCoop2MessageFactory messageFactory) {
		super(messageFactory);
	}
	public OpenSPCoop2Message_json_impl(OpenSPCoop2MessageFactory messageFactory, InputStream is,String contentType) throws MessageException {
		super(messageFactory, is, contentType);
	}
	
	@Override
	protected String buildContent() throws MessageException{
		try{
			return Utilities.getAsString(this.countingInputStream, this.contentTypeCharsetName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}finally {
			try {
				this.countingInputStream.close();
			}catch(Exception eClose) {}
		}
	}
	
	@Override
	protected String buildContentAsString() throws MessageException{
		return this.content;
	}

	@Override
	protected void serializeContent(OutputStream os, boolean consume) throws MessageException {
		Writer w = null;
		try{
			w = new OutputStreamWriter(os,this.contentTypeCharsetName);
			w.write(this.content);
			w.flush();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}finally{
			try{
				if(w!=null){
					w.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	@Override
	public boolean isProblemDetailsForHttpApis_RFC7807() throws MessageException,MessageNotSupportedException {
		try{
			if(this.contentType==null) {
				return false;
			}
			String baseType = ContentTypeUtilities.readBaseTypeFromContentType(this.contentType);
			return HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807.equalsIgnoreCase(baseType);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	
}
