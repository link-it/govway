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
package org.openspcoop2.message;

import java.util.Iterator;
import java.util.Map;

import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ParameterList;

/**
 * ContentTypeUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentTypeUtilities {

	public static String buildContentType(Map<String, String> parameters,String contentType) throws RuntimeException{
		try{
			ContentType cType = new ContentType(contentType);
			if(parameters.size()>0){
				Iterator<String> itP = parameters.keySet().iterator();
				while (itP.hasNext()) {
					String parameterName = (String) itP.next();
					String parameterValue = parameters.get(parameterName);
					if(cType.getParameterList()==null){
						cType.setParameterList(new ParameterList());
					}
					cType.getParameterList().remove(parameterName);
					cType.getParameterList().set(parameterName, parameterValue);
				}
			}
			return cType.toString();
		}catch(Exception e){
			throw new RuntimeException("Error during buildContentType: "+e.getMessage(), e);
		}
	}
	
}
