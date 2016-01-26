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


package org.openspcoop2.utils.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.StringReader;

import org.openspcoop2.utils.Utilities;


/**	
 * Contiene utility per effettuare la serializzazione di un oggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JavaDeserializer implements IDeserializer {

	@Override
	public Object getObject(String s, Class<?> classType) throws IOException {
		StringReader reader = new StringReader(s);
		return this.readObject(reader, classType);
	}

	@Override
	public Object readObject(InputStream is, Class<?> classType)
			throws IOException {
		ObjectInputStream ois = null;
		try{
			ois = new ObjectInputStream(is);
			Object o = ois.readObject();
			if(classType.isInstance(o)){
				return o;
			}else{
				throw new Exception("Oggetto deserializzato non e' di tipo ["+classType.getName()+"] bensi' di tipo ["+o.getClass().getName()+"]");
			}
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}finally{
			try{
				if(ois!=null){
					ois.close();
				}
			}catch(Exception eClose){}
		}
	}

	@Override
	public Object readObject(Reader reader, Class<?> classType)
			throws IOException {
		ObjectInputStream ois = null;
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int letti = 0;
			char[]buffer = new char[Utilities.DIMENSIONE_BUFFER];
			while( (letti = reader.read(buffer)) != -1 ){
				for(int i=0; i<letti; i++){
					bout.write(buffer[i]);
				}
			}
			bout.flush();
			bout.close();
			
			ois = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
			Object o = ois.readObject();
			if(classType.isInstance(o)){
				return o;
			}else{
				throw new Exception("Oggetto deserializzato non e' di tipo ["+classType.getName()+"] bensi' di tipo ["+o.getClass().getName()+"]");
			}
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}finally{
			try{
				if(ois!=null){
					ois.close();
				}
			}catch(Exception eClose){}
		}
	}

	

}
