/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.transport.http.credential;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;

/**
 * Factory per i PrincipalReader
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PrincipalReaderFactory {
	
	public static IPrincipalReader getReader(Logger log, String nomePrincipalReader) throws PrincipalReaderException{
		
		log.debug("Caricamento Principal Reader ["+nomePrincipalReader+"] in corso...");
		
		PrincipalReaderType principalReaderType = PrincipalReaderType.toEnumConstant(nomePrincipalReader);
		
		String className = null;
		if(principalReaderType != null){
			switch(principalReaderType){
			case PRINCIPAL:
				return new IdentityPrincipalReader(log);
			case HEADER:
				return new IdentityHttpReader(log);
			}
		} else {
			// se non ho trovato una classe corrispondente al paramentro allora carico la classe indicata 
			className = nomePrincipalReader;
		}
		
		log.debug("Caricamento classe ["+className+"] in corso...");
		try{
			Class<?> c = Class.forName(className);
			Constructor<?> constructor = c.getConstructor(Logger.class);
			IPrincipalReader p = (IPrincipalReader) constructor.newInstance(log);
			return  p;
		} catch (Exception e) {
			throw new PrincipalReaderException("Impossibile caricare la classe indicata ["+className+"] " + e.getMessage(), e);
		}
	}
}
