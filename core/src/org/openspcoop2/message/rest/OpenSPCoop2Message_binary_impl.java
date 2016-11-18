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

package org.openspcoop2.message.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.openspcoop2.message.OpenSPCoop2RestBinaryMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.Utilities;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi binari
 *
 * @author Andrea Poli <poli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_binary_impl extends AbstractBaseOpenSPCoop2RestMessage<byte[]> implements OpenSPCoop2RestBinaryMessage {

	public OpenSPCoop2Message_binary_impl() {
		super();
	}
	public OpenSPCoop2Message_binary_impl(InputStream is,String contentType) throws MessageException {
		super(is, contentType);
	}
	
	@Override
	protected byte[] buildContent() throws MessageException{
		try{
			return Utilities.getAsByteArray(this.is);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	@Override
	protected String buildContentAsString() throws MessageException{
		try{
			return Utilities.getAsString(new ByteArrayInputStream(this.content), this.contentTypeCharsetName);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	protected void serializeContent(OutputStream os, boolean consume) throws MessageException {
		try{
			os.write(this.content);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	
}
