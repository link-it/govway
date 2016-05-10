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
import org.openspcoop2.utils.id.UniversallyUniqueIdentifierGenerator;
import org.openspcoop2.utils.logger.beans.Diagnostic;
import org.openspcoop2.utils.logger.constants.LowSeverity;
import org.openspcoop2.utils.logger.constants.Severity;

/**
 * AbstractBasicLogger
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 11425 $, $Date: 2016-01-26 11:12:54 +0100 (Tue, 26 Jan 2016) $
 */
public abstract class AbstractBasicLogger implements ILogger {

	protected String idTransaction;
	private DiagnosticManager diagnosticManager;
	private boolean throwExceptionPlaceholderFailedResolution;
	private Properties diagnosticProperties;
	
	public AbstractBasicLogger(String diagnosticPropertiesResourceURI, Boolean throwExceptionPlaceholderFailedResolution) throws UtilsException{
		this(getProperties(diagnosticPropertiesResourceURI), throwExceptionPlaceholderFailedResolution);
	}
	public AbstractBasicLogger(File diagnosticPropertiesResource, Boolean throwExceptionPlaceholderFailedResolution) throws UtilsException{
		this(getProperties(diagnosticPropertiesResource), throwExceptionPlaceholderFailedResolution);
	}
	public AbstractBasicLogger(Properties diagnosticProperties, Boolean throwExceptionPlaceholderFailedResolution) throws UtilsException{
		try{
			this.diagnosticProperties = diagnosticProperties;
						
			this.throwExceptionPlaceholderFailedResolution = throwExceptionPlaceholderFailedResolution;
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	@Override
	public void initLogger() throws UtilsException{
		
		try{
			
			org.openspcoop2.utils.id.UniversallyUniqueIdentifierGenerator g = new UniversallyUniqueIdentifierGenerator();
			this.idTransaction = g.newID().toString();
			
			DateManager.initializeDataManager(SystemDate.class.getName(), null, null);
			
			this.diagnosticManager = new DiagnosticManager(this.diagnosticProperties, this.getContext(), this.throwExceptionPlaceholderFailedResolution, this);
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	@Override
	public void log(String message, LowSeverity severity) throws UtilsException{
		String functionDefault = this.diagnosticManager.getDefaultFunction();
		this.log(functionDefault, message, severity.toSeverity(), this.diagnosticManager.getDefaultCode(functionDefault, severity));
	}
	
	@Override
	public void log(String message, LowSeverity severity, String function) throws UtilsException{
		this.log(function, message, severity.toSeverity(), this.diagnosticManager.getDefaultCode(function, severity));
	}

	@Override
	public void log(String code) throws UtilsException{
		this.log(this.diagnosticManager.getFunction(code),
				this.diagnosticManager.getMessage(code),
				this.diagnosticManager.getSeverity(code),
				this.diagnosticManager.getCode(code));
	}

	@Override
	public void log(String code, String... params) throws UtilsException{
		this.log(this.diagnosticManager.getFunction(code),
				this.diagnosticManager.getMessage(code, params),
				this.diagnosticManager.getSeverity(code),
				this.diagnosticManager.getCode(code));
	}
	
	// serve per evitare che la chiamata con string ricata erroneamente nella firma Object invece che nella firma String ... params
	@Override
	public void log(String code, String param) throws UtilsException{
		this.log(code,new String [] {param});
	} 
	
	@Override
	public void log(String code, Object o) throws UtilsException{
		this.log(this.diagnosticManager.getFunction(code),
				this.diagnosticManager.getMessage(code,o),
				this.diagnosticManager.getSeverity(code),
				this.diagnosticManager.getCode(code));
	}
	
	private void log(String function,String message, Severity severity, String code) throws UtilsException{
		
		Diagnostic diagnostic = new Diagnostic();
		diagnostic.setDate(DateManager.getDate());
		diagnostic.setCode(code);
		diagnostic.setFunction(function);
		diagnostic.setIdTransaction(this.idTransaction);
		diagnostic.setMessage(message);
		diagnostic.setSeverity(severity);
		this.log(diagnostic, this.getContext());
		
	}
	
	protected abstract void log(Diagnostic diagnostic,IContext context) throws UtilsException;
	
	
	
	
	// ---- STATIC 
	
	private static Properties getProperties(String diagnosticPropertiesResourceURI) throws UtilsException{
		InputStream is = null;
		try{
			is = AbstractBasicLogger.class.getResourceAsStream(diagnosticPropertiesResourceURI);
			if(is==null && diagnosticPropertiesResourceURI.startsWith("/")==false){
				is = AbstractBasicLogger.class.getResourceAsStream("/"+diagnosticPropertiesResourceURI);
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
	
	private static Properties getProperties(File diagnosticPropertiesResource) throws UtilsException{
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
