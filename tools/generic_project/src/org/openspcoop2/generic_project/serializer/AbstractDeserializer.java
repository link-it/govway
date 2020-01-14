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
package org.openspcoop2.generic_project.serializer;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.openspcoop2.generic_project.exception.DeserializerException;

/**
 * AbstractDeserializer
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractDeserializer {

	public <T> T xmlToObj(String fileName,Class<T> c) throws DeserializerException {
		return this.xmlToObj(new File(fileName), c);
	}
	public <T> T xmlToObj(File file,Class<T> c) throws DeserializerException {
		FileInputStream fin = null;
		try{
			fin = new FileInputStream(file);
			return this.xmlToObj(fin, c);
		}catch(Exception e){
			throw new DeserializerException(e);
		}
		finally {
			try{
				if(fin!=null) {
					fin.close();
				}
			}catch(Exception eClose){}
		}
	}
	public <T> T xmlToObj(byte[] bytes,Class<T> c) throws DeserializerException {
		ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
		return this.xmlToObj(bin, c);
	}
	public <T> T xmlToObj(InputStream is,Class<T> c) throws DeserializerException {
		try{
			return (T) this._xmlToObj(is, c);
		}catch(Exception e){
			throw new DeserializerException(e);
		}
	}
	protected abstract <T> T _xmlToObj(InputStream is,Class<T> c) throws Exception;
	
	
	public <T> T xmlToObjByByteArray(byte[] xml,Class<T> c) throws DeserializerException {
		ByteArrayInputStream bin = null;
		try{
			bin = new ByteArrayInputStream(xml);
			return (T) this._xmlToObj(bin, c);
		}catch(Exception e){
			throw new DeserializerException(e);
		}finally{
			try{
				bin.close();
			}catch(Exception e){}
		}
	}
	public <T> T xmlToObjByString(String xml,Class<T> c) throws DeserializerException {
		try{
			return this.xmlToObjByByteArray(xml.getBytes(), c);
		}catch(Exception e){
			throw new DeserializerException(e);
		}
	}
}

