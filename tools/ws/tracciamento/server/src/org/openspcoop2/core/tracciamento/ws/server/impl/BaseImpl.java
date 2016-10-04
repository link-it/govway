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

package org.openspcoop2.core.tracciamento.ws.server.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import org.openspcoop2.core.tracciamento.ws.server.config.AuthorizationManager;
import org.openspcoop2.core.tracciamento.ws.server.config.DriverTracciamento;
import org.openspcoop2.core.tracciamento.ws.server.config.LoggerProperties;
import org.openspcoop2.core.tracciamento.ws.server.config.ServerProperties;
import org.openspcoop2.core.tracciamento.ws.server.config.WSStartup;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoMultipleResultException;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoMultipleResultException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotAuthorizedException;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotAuthorizedException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotFoundException;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotFoundException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotImplementedException;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoNotImplementedException_Exception;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoServiceException;
import org.openspcoop2.core.tracciamento.ws.server.exception.TracciamentoServiceException_Exception;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotAuthorizedException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;


/**     
 * BaseImpl
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class BaseImpl {

	protected ServerProperties serverProperties = null;
	protected boolean setStackTrace = false;
	protected boolean isLogParameters = false;
	protected boolean isLogResponse = false;
	protected boolean isLogAsXml = false;


	protected BaseImpl() {
		try {
			if(WSStartup.initializedResources==false){
				WSStartup.initResources();
			}
			this.serverProperties = ServerProperties.getInstance();
			this.setStackTrace = Boolean.parseBoolean(this.serverProperties.readProperty(true, "exception.stackTrace"));
			this.isLogParameters = Boolean.parseBoolean(this.serverProperties.readProperty(true, "methods.parameters.log"));
			this.isLogResponse = Boolean.parseBoolean(this.serverProperties.readProperty(true, "methods.response.log"));
			this.isLogAsXml = Boolean.parseBoolean(this.serverProperties.readProperty(true,"methods.logDebug.dump.xml"));
		} catch (Exception e) {
			LoggerProperties.getLoggerWS().error("Errore durante l'inizializzazione del Messaggio Service",  e);
		}
	}
			
	protected TracciamentoNotFoundException_Exception throwNotFoundException(String methodName,NotFoundException e, String errorCode, Object objectId) throws TracciamentoNotFoundException_Exception{
		TracciamentoNotFoundException ex = new TracciamentoNotFoundException();
		ex.setMethodName(methodName);
		ex.setErrorMessage(e.getMessage());
		ex.setErrorCode(errorCode);
		if(objectId!=null){
			if(objectId.getClass().getPackage().getName().equals("org.openspcoop2.core.tracciamento") 
				||
				objectId.getClass().getPackage().getName().startsWith("org.openspcoop2.core.tracciamento.ws.server.filter")){
				ex.setObjectId(objectId);
			}
			else{
				ex.setObjectId(objectId.toString());
			}
		}
		if(this.setStackTrace){
			ex.setErrorStackTrace(throwableStackTraceToString(e));
		}
		String id = "";
		if(ex.getObjectId()!=null){
			id = " id["+ex.getObjectId()+"]";
		}
		LoggerProperties.getLoggerWS().error("["+methodName+"] (cod:"+errorCode+")"+id+": "+e.getMessage(),e);
		throw new TracciamentoNotFoundException_Exception(e.getMessage(),ex,e);
	}

	protected TracciamentoNotAuthorizedException_Exception throwNotAuthorizedException(String methodName,NotAuthorizedException e, String errorCode, Object objectId) throws TracciamentoNotAuthorizedException_Exception{
		TracciamentoNotAuthorizedException ex = new TracciamentoNotAuthorizedException();
		ex.setMethodName(methodName);
		ex.setErrorMessage(e.getMessage());
		ex.setErrorCode(errorCode);
		if(objectId!=null){
			if(objectId.getClass().getPackage().getName().equals("org.openspcoop2.core.tracciamento") 
				||
				objectId.getClass().getPackage().getName().startsWith("org.openspcoop2.core.tracciamento.ws.server.filter")){
				ex.setObjectId(objectId);
			}
			else{
				ex.setObjectId(objectId.toString());
			}
		}
		if(this.setStackTrace){
			ex.setErrorStackTrace(throwableStackTraceToString(e));
		}
		String id = "";
		if(ex.getObjectId()!=null){
			id = " id["+ex.getObjectId()+"]";
		}
		LoggerProperties.getLoggerWS().error("["+methodName+"] (cod:"+errorCode+")"+id+": "+e.getMessage(),e);
		throw new TracciamentoNotAuthorizedException_Exception(e.getMessage(),ex,e);
	}
		
	protected TracciamentoNotImplementedException_Exception throwNotImplementedException(String methodName,NotImplementedException e, String errorCode, Object objectId) throws TracciamentoNotImplementedException_Exception{
		TracciamentoNotImplementedException ex = new TracciamentoNotImplementedException();
		ex.setMethodName(methodName);
		ex.setErrorMessage(e.getMessage());
		ex.setErrorCode(errorCode);
		if(objectId!=null){
			if(objectId.getClass().getPackage().getName().equals("org.openspcoop2.core.tracciamento") 
				||
				objectId.getClass().getPackage().getName().startsWith("org.openspcoop2.core.tracciamento.ws.server.filter")){
				ex.setObjectId(objectId);
			}
			else{
				ex.setObjectId(objectId.toString());
			}
		}
		if(this.setStackTrace){
			ex.setErrorStackTrace(throwableStackTraceToString(e));
		}
		String id = "";
		if(ex.getObjectId()!=null){
			id = " id["+ex.getObjectId()+"]";
		}
		LoggerProperties.getLoggerWS().error("["+methodName+"] (cod:"+errorCode+")"+id+": "+e.getMessage(),e);
		throw new TracciamentoNotImplementedException_Exception(e.getMessage(),ex,e);
	}
		
	protected TracciamentoMultipleResultException_Exception throwMultipleResultException(String methodName,MultipleResultException e, String errorCode, Object objectId) throws TracciamentoMultipleResultException_Exception{
		TracciamentoMultipleResultException ex = new TracciamentoMultipleResultException();
		ex.setMethodName(methodName);
		ex.setErrorMessage(e.getMessage());
		ex.setErrorCode(errorCode);
		if(objectId!=null){
			if(objectId.getClass().getPackage().getName().equals("org.openspcoop2.core.tracciamento") 
				||
				objectId.getClass().getPackage().getName().startsWith("org.openspcoop2.core.tracciamento.ws.server.filter")){
				ex.setObjectId(objectId);
			}
			else{
				ex.setObjectId(objectId.toString());
			}
		}
		if(this.setStackTrace){
			ex.setErrorStackTrace(throwableStackTraceToString(e));
		}
		String id = "";
		if(ex.getObjectId()!=null){
			id = " id["+ex.getObjectId()+"]";
		}
		LoggerProperties.getLoggerWS().error("["+methodName+"] (cod:"+errorCode+")"+id+": "+e.getMessage(),e);
		throw new TracciamentoMultipleResultException_Exception(e.getMessage(),ex,e);
	}
	
	protected TracciamentoServiceException_Exception throwServiceException(String methodName,Exception e, String errorCode, Object objectId) throws TracciamentoServiceException_Exception{
		TracciamentoServiceException ex = new TracciamentoServiceException();
		ex.setMethodName(methodName);
		ex.setErrorMessage(e.getMessage());
		ex.setErrorCode(errorCode);
		if(objectId!=null){
			if(objectId.getClass().getPackage().getName().equals("org.openspcoop2.core.tracciamento") 
				||
				objectId.getClass().getPackage().getName().startsWith("org.openspcoop2.core.tracciamento.ws.server.filter")){
				ex.setObjectId(objectId);
			}
			else{
				ex.setObjectId(objectId.toString());
			}
		}
		if(this.setStackTrace){
			ex.setErrorStackTrace(throwableStackTraceToString(e));
		}
		String id = "";
		if(ex.getObjectId()!=null){
			id = " id["+ex.getObjectId()+"]";
		}
		LoggerProperties.getLoggerWS().error("["+methodName+"] (cod:"+errorCode+")"+id+": "+e.getMessage(),e);
		throw new TracciamentoServiceException_Exception(e.getMessage(),ex,e);
	}
	
	protected String throwableStackTraceToString(Throwable e){
		try{
			ByteArrayOutputStream boutErrorStackTrace = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(boutErrorStackTrace);
			e.printStackTrace(ps);
			ps.flush();
			boutErrorStackTrace.flush();
			ps.close();
			boutErrorStackTrace.close();
			return boutErrorStackTrace.toString();
		}catch(Exception eStackTrace){
			return null;
		}
	}	
	
	protected void authorize(boolean searchMethod) throws TracciamentoServiceException_Exception,ServiceException,NotAuthorizedException{
		AuthorizationManager.getAuthorizationManager().authorize(this.getHttpServletRequest(), LoggerProperties.getLoggerWS(), searchMethod);
	}

	protected void checkInitDriverTracciamento(DriverTracciamento driver) throws NotImplementedException, ServiceException{
		if( driver.getDriver() == null ){
			throw new ServiceException("Driver non inizializzato");
		}
		if( ! (driver.getDriver() instanceof org.openspcoop2.pdd.logger.DriverTracciamento) ){
			throw new NotImplementedException("Driver ["+driver.getDriver().getClass().getName()+"] non compatibile con la tipologia ("+org.openspcoop2.pdd.logger.DriverTracciamento.class.getName()+") necessaria per soddisfare la richiesta");
		}
	}
	
	protected void logStartMethod(String methodName, Object ... parameter){
		int size = 0;
		if(parameter!=null){
			size = parameter.length;
		}
		LoggerProperties.getLoggerWS().info("@@@ ["+methodName+"] (parameters:"+size+") ...");
		if(this.isLogParameters && size>0){
			for (int i = 0; i < parameter.length; i++) {
				if(parameter[i] instanceof org.openspcoop2.utils.beans.BaseBean){
					org.openspcoop2.utils.beans.BaseBean bb = (org.openspcoop2.utils.beans.BaseBean) parameter[i];
					try{
						if(this.isLogAsXml){
							LoggerProperties.getLoggerWS().info("["+methodName+"][param"+i+"] = "+bb.toXml_Jaxb());
						}
						else{
							LoggerProperties.getLoggerWS().info("["+methodName+"][param"+i+"] = "+bb.toJson());
						}
					}catch(Throwable e){
						LoggerProperties.getLoggerWS().error("["+methodName+"][param"+i+"] = ERROR, dump non riuscito: "+e.getMessage(),e);
					}
				}
				else{
					LoggerProperties.getLoggerWS().info("["+methodName+"][param"+i+"] = "+parameter[i]);
				}
			}
		}
	}
	
	protected void logEndMethod(String methodName){
		LoggerProperties.getLoggerWS().info("@@@ ["+methodName+"] finished");
	}
	protected void logEndMethod(String methodName, Object response){
		if(response instanceof List<?>){
			logEndMethod_list(methodName, (List<?>) response);
		}
		else if(response instanceof org.openspcoop2.utils.beans.BaseBean){
			org.openspcoop2.utils.beans.BaseBean bb = (org.openspcoop2.utils.beans.BaseBean) response;
			if(this.isLogResponse){
				try{
					if(this.isLogAsXml){
						LoggerProperties.getLoggerWS().info("["+methodName+"][response] = "+bb.toXml_Jaxb());
					}
					else{
						LoggerProperties.getLoggerWS().info("["+methodName+"][response] = "+bb.toJson());
					}
				}catch(Throwable e){
					LoggerProperties.getLoggerWS().error("["+methodName+"][response] = ERROR, dump non riuscito: "+e.getMessage(),e);
				}
			}
			LoggerProperties.getLoggerWS().info("@@@ ["+methodName+"] finished");
		}
		else{
			LoggerProperties.getLoggerWS().info("@@@ ["+methodName+"] finished (return: "+response+")");
		}
	}
	private void logEndMethod_list(String methodName, List<?> response){
		int size = 0;
		if(response!=null){
			size = response.size();
		}
		if(this.isLogResponse && size>0){
			LoggerProperties.getLoggerWS().info("["+methodName+"] response (size:"+size+")");
			for (int i = 0; i < response.size(); i++) {
				if(response.get(i) instanceof org.openspcoop2.utils.beans.BaseBean){
					org.openspcoop2.utils.beans.BaseBean bb = (org.openspcoop2.utils.beans.BaseBean) response.get(i);
					try{
						if(this.isLogAsXml){
							LoggerProperties.getLoggerWS().info("["+methodName+"][response"+i+"] = "+bb.toXml_Jaxb());
						}
						else{
							LoggerProperties.getLoggerWS().info("["+methodName+"][response"+i+"] = "+bb.toJson());
						}
					}catch(Throwable e){
						LoggerProperties.getLoggerWS().error("["+methodName+"][response"+i+"] = ERROR, dump non riuscito: "+e.getMessage(),e);
					}
				}
				else{
					LoggerProperties.getLoggerWS().info("["+methodName+"][response"+i+"] = "+response.get(i));
				}
			}
		}
		LoggerProperties.getLoggerWS().info("@@@ ["+methodName+"] finished (size:"+size+")");
	}
	
	protected abstract javax.servlet.http.HttpServletRequest getHttpServletRequest() throws TracciamentoServiceException_Exception;
	protected abstract javax.servlet.http.HttpServletResponse getHttpServletResponse() throws TracciamentoServiceException_Exception;

}