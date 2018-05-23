package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import org.openspcoop2.core.controllo_congestione.driver.IGestorePolicyAttive;
import org.openspcoop2.core.controllo_congestione.driver.PolicyException;
import org.slf4j.Logger;

public class GestorePolicyAttive {

	
	private static IGestorePolicyAttive staticInstance = null;
	public static synchronized void initialize(Logger log, TipoGestorePolicy tipo, String urlService) throws Exception{
		if(staticInstance==null){
			if(TipoGestorePolicy.IN_MEMORY.equals(tipo)){
				staticInstance = new GestorePolicyAttiveInMemory();
				staticInstance.initialize(log);
			}
			else if(TipoGestorePolicy.WS.equals(tipo)){
				staticInstance = new GestorePolicyAttiveWS();
				staticInstance.initialize(log,urlService);
			}
			else{
				throw new Exception("Tipo GestorePolicyAttive ["+tipo+"] non supportato");
			}
			
		}
	}
	public static IGestorePolicyAttive getInstance() throws PolicyException{
		if(staticInstance==null){
			throw new PolicyException("GestorePolicyAttive non inizializzato");
		}
		return staticInstance;
	}
	
	
}

