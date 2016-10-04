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
package org.openspcoop2.generic_project.utils;

import java.util.Properties;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.utils.LoaderProperties;
import org.openspcoop2.utils.Utilities;

/**
 * ServerProperties
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerProperties {


		
	// ------- Instance -------------
	
	private LoaderProperties loader = null;
	
	public ServerProperties(String filePropertiesName) throws ServiceException{
		this.loader = new LoaderProperties(filePropertiesName);
	}
	public ServerProperties(Properties properties) throws ServiceException{
		this.loader = new LoaderProperties(properties);
	}
	
	public String getProperty(String name,boolean required) throws ServiceException{
		String tmp = this.loader.getProperties().getProperty(name);
		if(tmp==null){
			if(required){
				throw new ServiceException("Property ["+name+"] not found");
			}else{
				return null;
			}
		}
		else{
			return tmp.trim();
		}
	}
	
	public Properties readProperties(String prefix) throws ServiceException{
		try{
			return Utilities.readProperties(prefix, this.loader.getProperties());
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
	}
	
	public boolean getBooleanProperty(String name,boolean required) throws ServiceException{
		String p = this.getProperty(name, required);
		if("true".equalsIgnoreCase(p)){
			return  true;
		}
		else{
			return false;
		}
	}
	
}