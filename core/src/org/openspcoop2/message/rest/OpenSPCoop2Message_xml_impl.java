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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.xml.XMLUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * Implementazione dell'OpenSPCoop2Message utilizzabile per messaggi xml
 *
 * @author Andrea Poli <poli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2Message_xml_impl extends AbstractBaseOpenSPCoop2RestMessage<Element> implements OpenSPCoop2RestXmlMessage {

	public OpenSPCoop2Message_xml_impl() {
		super();
	}
	public OpenSPCoop2Message_xml_impl(InputStream is,String contentType) throws MessageException {
		super(is, contentType);
	}
	
	@Override
	protected Element buildContent() throws MessageException{
		InputStreamReader isr = null;
		InputSource isSax = null;
		try{
			isr = new InputStreamReader(this.is,this.contentTypeCharsetName);
			isSax = new InputSource(isr);
			return XMLUtils.getInstance().newElement(isSax);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}finally{
			try{
				if(isr!=null){
					isr.close();
				}
			}catch(Exception eClose){}
			try{
				if(this.is!=null){
					this.is.close();
				}
			}catch(Exception eClose){}
		}
	}

	@Override
	protected String buildContentAsString() throws MessageException{
		try{
			return this.getAsString(this.content, false);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	protected void serializeContent(OutputStream os, boolean consume) throws MessageException {
		try{
			XMLUtils.getInstance().writeTo(this.content, os, true);
			os.flush();
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}

	
}
