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
package org.openspcoop2.generic_project.serializer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.openspcoop2.utils.serialization.Filter;

import org.openspcoop2.utils.serialization.JSonSerializer;

/**
 * JsonSerializer
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JsonSerializer extends AbstractSerializer {

	
	@Override
	protected <T> void _objToXml(String fileName, Class<T> c, T object,
			boolean pretty) throws Exception {
		JSonSerializer jsonSerializer = new JSonSerializer(new Filter());
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(new File(fileName));
			jsonSerializer.writeObject(object, fout);
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
		JSonSerializer jsonSerializer = new JSonSerializer(new Filter());
		try{
			jsonSerializer.writeObject(object, out);
		}finally{
			try{
				out.flush();
			}catch(Exception e){}
		}
	}


}