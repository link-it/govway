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
package org.openspcoop2.utils.logger;

import java.lang.reflect.Constructor;

import org.openspcoop2.utils.UtilsException;

/**
 * LoggerFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoggerFactory {

	private static Class<ILogger> loggerImpl = null;
	private static Object [] parameters = null;
	private static Class<?> [] parameterTypes = null;
	
	@SuppressWarnings("unchecked")
	public static void initialize(String implementationClassName, Object ... pars) throws UtilsException, ClassNotFoundException{
		Class<ILogger> c = (Class<ILogger>) Class.forName(implementationClassName);
		initialize(c, pars);
	}
	public static void initialize(Class<ILogger> implementationClass, Object ... pars) throws UtilsException {
		try{
			loggerImpl = (Class<ILogger>)implementationClass;
			parameters = pars;
			if(parameters!=null){
				parameterTypes = new Class<?>[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					parameterTypes[i] = parameters[i].getClass();
				}
			}
		}catch(Exception e){
			throw new UtilsException("Expected class assignable from "+ILogger.class.getName()+". Found: "+implementationClass.getName()+" . Error: "+e.getMessage(),e);
		}
	}
	
	public static ILogger newLogger() throws UtilsException{
		return _newLogger(null);
	}
	public static ILogger newLogger(IContext context) throws UtilsException{
		return _newLogger(context);
	}
	private static ILogger _newLogger(IContext context) throws UtilsException{
		
		if(loggerImpl==null){
			throw new UtilsException("LoggerFactory not Initialized");
		}
		
		try{
			Constructor<ILogger> c = loggerImpl.getConstructor(parameterTypes);
			ILogger logger = (ILogger) c.newInstance(parameters);
			if(context!=null){
				logger.initLogger(context);
			}
			else{
				logger.initLogger();
			}
			return logger;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static ILogger newLogger(String implementationClassName) throws UtilsException{
		return _newLogger(implementationClassName, null);
	}
	public static ILogger newLogger(String implementationClassName, IContext context) throws UtilsException{
		return _newLogger(implementationClassName, context);
	}
	@SuppressWarnings("unchecked")
	private static ILogger _newLogger(String implementationClassName, IContext context) throws UtilsException{
		Class<ILogger> c = null;
		try {
			c = (Class<ILogger>) Class.forName(implementationClassName);
		}catch(Exception e){
			throw new UtilsException("Expected class assignable from "+ILogger.class.getName()+". Found: "+implementationClassName+" . Error: "+e.getMessage(),e);
		}
		return _newLogger(c, context);
	}
	
	public static ILogger newLogger(Class<? extends ILogger> implementationClass) throws UtilsException{
		return _newLogger(implementationClass, null);
	}
	public static ILogger newLogger(Class<? extends ILogger> implementationClass, IContext context) throws UtilsException{
		return _newLogger(implementationClass, context);
	}
	private static ILogger _newLogger(Class<? extends ILogger> implementationClass, IContext context) throws UtilsException{
		
		try{
			Constructor<? extends ILogger> c = implementationClass.getConstructor(parameterTypes);
			ILogger logger = (ILogger) c.newInstance(parameters);
			if(context!=null){
				logger.initLogger(context);
			}
			else{
				logger.initLogger();
			}
			return logger;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
}
