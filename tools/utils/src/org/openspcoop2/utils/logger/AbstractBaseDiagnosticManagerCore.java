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
import java.io.InputStream;
import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.SystemDate;

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
	
	public AbstractBaseDiagnosticManagerCore(String diagnosticPropertiesResourceURI, Boolean throwExceptionPlaceholderFailedResolution) throws UtilsException{
		this(getProperties(diagnosticPropertiesResourceURI), throwExceptionPlaceholderFailedResolution);
	}
	public AbstractBaseDiagnosticManagerCore(File diagnosticPropertiesResource, Boolean throwExceptionPlaceholderFailedResolution) throws UtilsException{
		this(getProperties(diagnosticPropertiesResource), throwExceptionPlaceholderFailedResolution);
	}
	public AbstractBaseDiagnosticManagerCore(Properties diagnosticProperties, Boolean throwExceptionPlaceholderFailedResolution) throws UtilsException{
		try{
			this.diagnosticProperties = diagnosticProperties;
						
			this.throwExceptionPlaceholderFailedResolution = throwExceptionPlaceholderFailedResolution;
			
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
	
	public static Properties getProperties(String diagnosticPropertiesResourceURI) throws UtilsException{
		InputStream is = null;
		try{
			is = AbstractBaseDiagnosticManagerCore.class.getResourceAsStream(diagnosticPropertiesResourceURI);
			if(is==null && diagnosticPropertiesResourceURI.startsWith("/")==false){
				is = AbstractBaseDiagnosticManagerCore.class.getResourceAsStream("/"+diagnosticPropertiesResourceURI);
			}
			if(is==null){
				throw new UtilsException("Resource ["+diagnosticPropertiesResourceURI+"] not found");
			}
			Properties p = new Properties();
			p.load(is);
			return p;
		}
		catch(Exception e){
			if(e instanceof UtilsException){
				throw (UtilsException) e;
			}
			else{
				throw new UtilsException(e.getMessage(),e);
			}
		}
		finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public static Properties getProperties(File diagnosticPropertiesResource) throws UtilsException{
		InputStream is = null;
		try{
			if(diagnosticPropertiesResource.exists()==false){
				throw new Exception("Not exists");
			}
			if(diagnosticPropertiesResource.canRead()==false){
				throw new Exception("Cannot read");
			}
			is = new FileInputStream(diagnosticPropertiesResource);
			Properties p = new Properties();
			p.load(is);
			return p;
		}
		catch(Exception e){
			if(e instanceof UtilsException){
				throw (UtilsException) e;
			}
			else{
				throw new UtilsException(e.getMessage(),e);
			}
		}
		finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
	}
}
