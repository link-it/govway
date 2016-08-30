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
package org.openspcoop2.utils.logger;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.SystemDate;
import org.openspcoop2.utils.logger.config.DiagnosticConfig;

/**
 * AbstractBaseDiagnosticManagerCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractBaseDiagnosticManagerCore  {

	protected DiagnosticManager diagnosticManager;
	private boolean throwExceptionPlaceholderFailedResolution;
	private Properties diagnosticProperties;
	
	public AbstractBaseDiagnosticManagerCore(DiagnosticConfig diagnosticConfig) throws UtilsException{
		
		DiagnosticConfig.validate(diagnosticConfig); // garantisce che getThrowExceptionPlaceholderFailedResolution non torni null. e readProperties contenga una configurazione valida
		try{
			
			this.diagnosticProperties = DiagnosticConfig.readProperties(diagnosticConfig);
						
			this.throwExceptionPlaceholderFailedResolution = diagnosticConfig.getThrowExceptionPlaceholderFailedResolution();
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	public void init(IContext context,ILogger loggerForCallback) throws UtilsException{
		
		try{
			
			DateManager.initializeDataManager(SystemDate.class.getName(), null, null);
			
			this.diagnosticManager = new DiagnosticManager(this.diagnosticProperties, context, this.throwExceptionPlaceholderFailedResolution, loggerForCallback);
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	// ---- STATIC 
	
	public static Properties getProperties(File file) throws UtilsException{
		String filePath = "fs";
		try{
			if(file==null){
				throw new Exception("Resource file undefined");
			}
			filePath = file.getAbsolutePath();
			if(file.exists()){
				Properties p = new Properties();
				FileInputStream fin = null;
				try{
					fin = new FileInputStream(file);
					p.load(fin);
				}finally{
					try{
						fin.close();
					}catch(Exception eClose){}
				}
				return p;
			}
			else{
				throw new UtilsException("Resource not exists");
			}
		}catch(Exception e){
			throw new UtilsException("Reading Properties failed (resource ["+filePath+"]): "+e.getMessage(),e);
		}
	}
	public static Properties getProperties(String name) throws UtilsException{
		try{
			if(name==null){
				throw new Exception("Resource name undefined");
			}
			File f = new File(name);
			if(f.exists()){
				return getProperties(f);
			}
			else{
				String newName = null;
				if(name.trim().startsWith("/")){
					newName = name;
				}
				else{
					newName = "/" + name;
				}
				URL url = AbstractBaseDiagnosticManagerCore.class.getResource(newName);
				if(url!=null){
					return getProperties(url);
				}
				else{
					throw new UtilsException("Resource ["+name+"] not found");
				}
			}
		}catch(Exception e){
			throw new UtilsException("Reading Properties failed (resource ["+name+"]): "+e.getMessage(),e);
		}
	}
	public static Properties getProperties(URL url) throws UtilsException{
		URI uri = null;
		try{
			if(url==null){
				throw new Exception("Resource URL undefined");
			}
			uri = url.toURI();
		}catch(Exception e){
			throw new UtilsException("Reading Properties failed (url ["+url+"]): "+e.getMessage(),e);
		}
		return getProperties(uri);

	}
	public static Properties getProperties(URI uri) throws UtilsException{
		try{
			if(uri==null){
				throw new Exception("Resource URI undefined");
			}
			File f = new File(uri);
			return getProperties(f);
		}catch(Exception e){
			throw new UtilsException("Reading Properties failed (uri ["+uri+"]): "+e.getMessage(),e);
		}
	}

}
