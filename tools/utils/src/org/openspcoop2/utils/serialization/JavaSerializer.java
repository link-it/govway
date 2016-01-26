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

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;


/**	
 * Contiene utility per effettuare la serializzazione di un oggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JavaSerializer implements ISerializer {

	@Override
	public String getObject(Object o) throws IOException {
		try{
			StringWriter writer = new StringWriter();
			writeObject(o,writer);
			writer.flush();
			writer.close();
			return writer.toString();
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}
	}

	@Override
	public void writeObject(Object o, OutputStream out) throws IOException {
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(out);
			oos.writeObject(o);
			oos.flush();
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}finally{
			try{
				if(oos!=null){
					oos.close();
				}
			}catch(Exception eClose){}
		}
	}

	@Override
	public void writeObject(Object o, Writer out) throws IOException {
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			writeObject(o,bout);
			bout.flush();
			bout.close();
			byte[]array = bout.toByteArray();
			for(int i=0; i<bout.size();i++){
				out.write(array[i]);
			}
			
		}catch(Exception e){
			throw new IOException(e.getMessage(),e);
		}
	}

}
