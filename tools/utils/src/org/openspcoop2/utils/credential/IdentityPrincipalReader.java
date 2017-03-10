package org.openspcoop2.utils.credential;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.utils.Identity;
import org.slf4j.Logger;

/****
 * 
 * Implementazione dell'interfaccia {@link IPrincipalReader} che utilizza la classe {@link Identity} come strumento per leggere il principal dalla request.
 * 
 * @author pintori
 *
 */
public class IdentityPrincipalReader implements IPrincipalReader{


	private Logger log = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IdentityPrincipalReader(Logger log){
		this.log = log;
		this.log.debug("IdentityPrincipalReader inizializzato.");
	}

	@Override
	public void init(Object... parametri) throws PrincipalReaderException {
	}

	@Override
	public String getPrincipal(HttpServletRequest request) throws PrincipalReaderException {
		this.log.debug("Estrazione principal in corso...");

		try{
			Identity identity = new Identity(request);
			String username = identity.getPrincipal();

			this.log.debug("Username trovato nel principal [identity.getPrincipal()]: ["+username+"]");
			this.log.debug("Username trovato nel principal [identity.getUsername()]: ["+identity.getUsername()+"]");

			return username;
		}catch (Exception e) {
			this.log.error("Errore durante la lettura del principal: "+ e.getMessage(),e);
			throw new PrincipalReaderException(e);
		}
	}
}
