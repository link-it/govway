package org.openspcoop2.core.diagnostica.ws.server.config;

import java.util.Properties;

import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.core.diagnostica.ws.server.config.ServerProperties;

/**     
 * AuthorizationManager
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AuthorizationManager extends org.openspcoop2.generic_project.utils.AuthorizationManager {

	// ------- Static -------------
	
	private static AuthorizationManager authorizationManager = null;
	private static synchronized void initAuthorizationManager() throws ServiceException{
		if(AuthorizationManager.authorizationManager==null){
			AuthorizationManager.authorizationManager = new AuthorizationManager();
		}
	}
	public static AuthorizationManager getAuthorizationManager() throws ServiceException{
		if(AuthorizationManager.authorizationManager==null){
			AuthorizationManager.initAuthorizationManager();
		}
		return AuthorizationManager.authorizationManager;
	}
	
	
	
	// ------- Instance -------------
	
	
	public AuthorizationManager() throws ServiceException{
		super(AuthorizationManager.getProperties());
	}
	
	private static Properties getProperties() throws ServiceException{
		try{
			return ServerProperties.getInstance().getProperties();
		}catch(Exception e){
			throw new ServiceException(e.getMessage(),e);
		}
	}
	
}