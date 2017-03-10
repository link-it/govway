package org.openspcoop2.utils.credential;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/***
 * 
 * Interfaccia che definisce le funzionalita del PrincipalReader.
 * 
 * @author pintori
 *
 */
public interface IPrincipalReader extends Serializable{

	public String getPrincipal(HttpServletRequest request) throws PrincipalReaderException;
	
	public void init(Object ... parametri) throws PrincipalReaderException;
}
