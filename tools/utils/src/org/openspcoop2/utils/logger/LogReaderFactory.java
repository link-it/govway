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
package org.openspcoop2.utils.logger;

import java.lang.reflect.Constructor;

import org.openspcoop2.utils.UtilsException;

/**
 * LogReaderFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LogReaderFactory {

	private static Class<ILogReader> loggerImpl = null;
	private static Object [] parameters = null;
	private static Class<?> [] parameterTypes = null;
	
	@SuppressWarnings("unchecked")
	public static void initialize(String implementationClassName, Object ... pars) throws UtilsException, ClassNotFoundException{
		Class<ILogReader> c = (Class<ILogReader>) Class.forName(implementationClassName);
		initialize(c, pars);
	}
	public static void initialize(Class<ILogReader> implementationClass, Object ... pars) throws UtilsException {
		try{
			loggerImpl = (Class<ILogReader>)implementationClass;
			parameters = pars;
			if(parameters!=null){
				parameterTypes = new Class<?>[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					parameterTypes[i] = parameters[i].getClass();
				}
			}
		}catch(Exception e){
			throw new UtilsException("Expected class assignable from "+ILogReader.class.getName()+". Found: "+implementationClass.getName()+" . Error: "+e.getMessage(),e);
		}
	}
	
	public static ILogReader newReader() throws UtilsException{
		
		if(loggerImpl==null){
			throw new UtilsException("LogReaderFactory not Initialized");
		}
		
		try{
			Constructor<ILogReader> c = loggerImpl.getConstructor(parameterTypes);
			ILogReader reader = (ILogReader) c.newInstance(parameters);
			reader.initReader();
			return reader;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
}
