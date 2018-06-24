/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.Utilities;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi json
 *
 * @author Andrea Poli <poli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_json_impl extends AbstractBaseOpenSPCoop2RestMessage<String> implements OpenSPCoop2RestJsonMessage {

	public OpenSPCoop2Message_json_impl() {
		super();
	}
	public OpenSPCoop2Message_json_impl(InputStream is,String contentType) throws MessageException {
		super(is, contentType);
	}
	
	@Override
	protected String buildContent() throws MessageException{
		try{
			return Utilities.getAsString(this.is, this.contentTypeCharsetName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
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

	
}
