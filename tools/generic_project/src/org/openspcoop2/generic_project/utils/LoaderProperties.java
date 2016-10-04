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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.openspcoop2.generic_project.exception.ServiceException;

/**
 * LoaderProperties
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoaderProperties {

	protected Properties properties = null;
	protected String filePropertiesName = null;
	
	public LoaderProperties(String filePropertiesName) throws ServiceException{
		InputStream is = null;
		try{
			this.filePropertiesName = filePropertiesName;
			File f = new File(filePropertiesName);
			if(f.exists()){
				is = new FileInputStream(f);
			}else{
				
//				java.net.URL url = LoaderProperties.class.getResource(filePropertiesName);
//				if(url!=null){
//					System.out.println("PATH ["+url.toString()+"]");
//				}
//				else{
//					 url = LoaderProperties.class.getResource("/"+filePropertiesName);
//					 System.out.println("PATH 2 ["+url.toString()+"]");
//				}
				
				is = LoaderProperties.class.getResourceAsStream(filePropertiesName);
				if(is==null){
					is = LoaderProperties.class.getResourceAsStream("/"+filePropertiesName);
				}
			}
			if(is==null){
				throw new Exception("InputStream is null");
			}
			this.properties = new Properties();
			this.properties.load(is);
			
		}catch(Exception e){
			throw new ServiceException("Loading properties file ["+filePropertiesName+"] failed: "+e.getMessage(),e);
		}finally{
			try{
				is.close();
			}catch(Exception e){}
		}
	}
	public LoaderProperties(Properties properties) throws ServiceException{
		this.properties = properties;
	}

	public Properties getProperties() {
		return this.properties;
	}
	
	
	
}
