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

package org.openspcoop2.utils.mail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.openspcoop2.utils.UtilsException;

/**
 * MailAttach
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MailBinaryAttach extends MailAttach {

	private byte[] content;
	private InputStream inputStream;
	
	public MailBinaryAttach(String name, byte[] content, String contentType){
		super(name, contentType);
		this.content = content;
	}
	public MailBinaryAttach(String name, InputStream inputStream, String contentType){
		super(name, contentType);
		this.inputStream = inputStream;
	}
	public MailBinaryAttach(String name, byte[] content){
		this(name, content, null);
	}
	public MailBinaryAttach(String name, InputStream inputStream){
		this(name, inputStream, null);
	}
	
	public byte[] getContent() throws UtilsException {
		if(this.content==null){
			return org.openspcoop2.utils.Utilities.getAsByteArray(this.inputStream);
		}
		return this.content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public InputStream getInputStream() {
		if(this.inputStream!=null){
			return this.inputStream;
		}
		else{
			return new ByteArrayInputStream(this.content);
		}
	}
	public void setInputStream(InputStream is) {
		this.inputStream = is;
	}
}
