/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.openspcoop2.utils.serialization.ISerializer;
import org.openspcoop2.utils.serialization.SerializationConfig;
import org.openspcoop2.utils.serialization.SerializationFactory;
import org.openspcoop2.utils.serialization.SerializationFactory.SERIALIZATION_TYPE;

/**
 * JsonSerializer
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractSerializerWithFactory extends AbstractSerializerBase {

	protected abstract SERIALIZATION_TYPE getSERIALIZATION_TYPE();
	
	@Override
	protected <T> void _objToXml(String fileName, Class<T> c, T object,
			boolean pretty) throws Exception {
		ISerializer serializer = SerializationFactory.getSerializer(this.getSERIALIZATION_TYPE(), new SerializationConfig());
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(new File(fileName));
			serializer.writeObject(object, fout);
		}finally{
			try{
				fout.flush();
			}catch(Exception e){}
			try{
				fout.close();
			}catch(Exception e){
				// close
			}
		}
	}

	@Override
	protected <T> void _objToXml(OutputStream out, Class<T> c, T object,
			boolean pretty) throws Exception {
		ISerializer serializer = SerializationFactory.getSerializer(this.getSERIALIZATION_TYPE(), new SerializationConfig());
		try{
			serializer.writeObject(object, out);
		}finally{
			try{
				out.flush();
			}catch(Exception e){}
		}
	}


}