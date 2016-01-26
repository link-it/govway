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


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * JavaSerializer
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JavaSerializer extends AbstractSerializer {

	
	@Override
	protected <T> void _objToXml(String fileName, Class<T> c, T object,
			boolean pretty) throws Exception {
		org.openspcoop2.utils.serialization.JavaSerializer javaSerializer = new org.openspcoop2.utils.serialization.JavaSerializer();
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(new File(fileName));
			javaSerializer.writeObject(object, fout);
		}finally{
			try{
				fout.flush();
			}catch(Exception e){}
			try{
				fout.close();
			}catch(Exception e){}
		}
	}

	@Override
	protected <T> void _objToXml(OutputStream out, Class<T> c, T object,
			boolean pretty) throws Exception {
		org.openspcoop2.utils.serialization.JavaSerializer javaSerializer = new org.openspcoop2.utils.serialization.JavaSerializer();
		try{
			javaSerializer.writeObject(object, out);
		}finally{
			try{
				out.flush();
			}catch(Exception e){}
		}
	}


}