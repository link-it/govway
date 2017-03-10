package org.openspcoop2.utils.credential;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;

/****
 * 
 * Factory per i PrincipalReader
 * 
 * @author pintori
 *
 */
public class PrincipalReaderFactory {
	
	public static IPrincipalReader getReader(Logger log, String nomePrincipalReader) throws PrincipalReaderException{
		
		log.debug("Caricamento Principal Reader ["+nomePrincipalReader+"] in corso...");
		
		PrincipalReaderType principalReaderType = PrincipalReaderType.toEnumConstant(nomePrincipalReader);
		
		String className = null;
		if(principalReaderType != null){
			switch(principalReaderType){
			case COOKIE:
				return new CookiePrincipalReader(log);
			case PRINCIPAL:
				return new IdentityPrincipalReader(log);
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
