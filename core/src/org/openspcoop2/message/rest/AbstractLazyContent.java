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

package org.openspcoop2.message.rest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;

/**
 * AbstractLazyContent
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractLazyContent<T> {

	public static boolean BUILD_LAZY = true;
	
	protected T content;
	protected DumpByteArrayOutputStream contentBuffer;
	protected byte[] contentByteArray; // inefficiente, viene memorizzato due volte, se e' attivo anche il contentBuffer, usare anzi quel costruttore
	protected String contentType;
	
	protected AbstractLazyContent() {}
	public void init(InputStream is, String contentType) throws MessageException {
		try{
			this.contentType = contentType;
			this.contentByteArray = Utilities.getAsByteArray(is);
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	public void init(DumpByteArrayOutputStream contentBuffer, String contentType) throws MessageException {
		try{
			this.contentType = contentType;
			this.contentBuffer = contentBuffer;
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	public void init(T content) {
		this.content = content;
	}
	
	public abstract T buildContent(InputStream is) throws MessageException;
	public abstract T buildContent(byte[] c) throws MessageException;
	public abstract void writeContentTo(OutputStream os, boolean consume) throws MessageException;
	
	public void buildContent() throws MessageException {
		if(this.content==null) {
			if(this.contentBuffer!=null) {
				try{
					if(this.contentBuffer.isSerializedOnFileSystem()) {
						try(InputStream is = new FileInputStream(this.contentBuffer.getSerializedFile())){
							this.content = buildContent(is);
						}
					}
					else {
						this.content = buildContent(this.contentBuffer.toByteArray());
					}
					this.contentBuffer = null;
				}catch(Exception e){
					throw new MessageException(e.getMessage(),e);
				}
			}
			else if(this.contentByteArray!=null) {
				try{
					this.content = buildContent(this.contentByteArray);
					this.contentByteArray = null;
				}catch(Exception e){
					throw new MessageException(e.getMessage(),e);
				}
			}
		}
	}
	public T getContent() throws MessageException {
		if(this.content==null) {
			buildContent();
		}
		return this.content;
	}
	
	public void writeTo(OutputStream os, boolean consume) throws MessageException {
		writeTo(true, os, consume);
	}
	protected void writeTo(boolean checkContent, OutputStream os, boolean consume) throws MessageException {
		try{
			if(checkContent && this.content!=null) {
				this.writeContentTo(os, consume);
				if(consume) {
					this.content=null;
				}
			}
			else if(this.contentBuffer!=null) {
				this.contentBuffer.writeTo(os);
				if(consume) {
					this.contentBuffer=null;
				}
			}
			else if(this.contentByteArray!=null) {
				os.write(this.contentByteArray);
				if(consume) {
					this.contentByteArray=null;
				}
			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
}
