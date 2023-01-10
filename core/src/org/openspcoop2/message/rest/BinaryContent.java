/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * BinaryContent
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BinaryContent extends AbstractLazyContent<byte[]> {

	public BinaryContent(InputStream is, String contentType) throws MessageException {
		try{
			this.init(is, contentType);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	public BinaryContent(DumpByteArrayOutputStream contentBuffer, String contentType) throws MessageException {
		try{
			this.init(contentBuffer, contentType);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	@Override
	public byte[] buildContent(InputStream is) throws MessageException {
		return new byte[1]; // serve solo a non farlo avere null
	}
	@Override
	public byte[] buildContent(byte[] c) throws MessageException {
		return new byte[1]; // serve solo a non farlo avere null
	}
	@Override
	public void writeContentTo(OutputStream os, boolean consume) throws MessageException {
		super.writeTo(false, os, consume);
	}
	
	@Override
	public byte[] getContent() throws MessageException {
		if(this.contentBuffer!=null) {
			try {
				if(this.contentBuffer.isSerializedOnFileSystem()) {
					return FileSystemUtilities.readBytesFromFile(this.contentBuffer.getSerializedFile());
				}
				else {
					return this.contentBuffer.toByteArray();
				}
			}catch(Exception e){
				throw new MessageException(e.getMessage(),e);
			}
		}
		else {
			return this.contentByteArray;
		}
	}
	
}
