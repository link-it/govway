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
package org.openspcoop2.generic_project.serializer;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import org.openspcoop2.generic_project.exception.SerializerException;
import org.openspcoop2.generic_project.io.IOUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.xml.PrettyPrintXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;

/**
 * AbstractSerializer
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractSerializer {


	private void createFile(String fileName) throws Exception{
		File f = new File(fileName);
		if(f.exists()==false){
			IOUtilities.mkdirParentDirectory(fileName);
			boolean create = f.createNewFile();
			if(create==false){
				throw new FileNotFoundException("File ["+fileName+"] cannot exists and creating failure");
			}
		}
	}
	
	
	
	// FileName+Object
	public <T> void objToXml(String fileName,Class<T> c,T object) throws SerializerException{
		objToXml(fileName, c, object, false);
	}
	public <T> void objToXml(String fileName,Class<T> c,T object,boolean pretty) throws SerializerException{
		try{
			this.createFile(fileName);
			this._objToXml(fileName, c, object, pretty);
		}catch(Exception e){
			throw new SerializerException(e);
		}
	}
	protected abstract <T> void _objToXml(String fileName,Class<T> c,T object,boolean pretty) throws Exception;
	
		
	// OutputStream+Object
	public <T> void objToXml(OutputStream out,Class<T> c,T object) throws SerializerException{
		objToXml(out, c, object, false);
	}
	public <T> void objToXml(OutputStream out,Class<T> c,T object,boolean pretty) throws SerializerException{
		try{
			_objToXml(out,c,object,pretty);
		}catch(Exception e){
			throw new SerializerException(e.getMessage(),e);
		}
	}
	protected abstract <T> void _objToXml(OutputStream out,Class<T> c,T object,boolean pretty) throws Exception;

	
	// File+byte[]
	public void objToXml(String fileName,byte[]contenuto) throws SerializerException{
		objToXml(fileName,contenuto,false);
	}
	public void objToXml(String fileName,byte[]contenuto,boolean pretty) throws SerializerException{
		try{
			this.createFile(fileName);
			if(pretty){
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrettyPrintXMLUtils.prettyPrintWithTrAX(XMLUtils.getInstance().newDocument(contenuto), bout, false);
				bout.flush();
				bout.close();
				FileSystemUtilities.writeFile(fileName, bout.toByteArray());
			}
			else{
				FileSystemUtilities.writeFile(fileName, contenuto);
			}
		}catch(Exception e){
			throw new SerializerException(e);
		}
	}
	
	
	// ToByteArray
	public <T> byte[] objToXmlAsByteArray(Class<T> c,T object) throws SerializerException{
		return objToXmlAsByteArray(c,object,false);
	}
	public <T> byte[] objToXmlAsByteArray(Class<T> c,T object,boolean pretty) throws SerializerException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this._objToXml(bout, c, object, pretty);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new SerializerException(e.getMessage(),e);
		}
	}
	
	
	// ToString
	public <T> String objToXmlAsString(Class<T> c,T object) throws SerializerException{
		return objToXmlAsString(c,object,false);
	}
	public <T> String objToXmlAsString(Class<T> c,T object,boolean pretty) throws SerializerException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this._objToXml(bout, c, object, pretty);
			bout.flush();
			bout.close();
			return bout.toString();
		}catch(Exception e){
			throw new SerializerException(e.getMessage(),e);
		}
	}
	
	
}