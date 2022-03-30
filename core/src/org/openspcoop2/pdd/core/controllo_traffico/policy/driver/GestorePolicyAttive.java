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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import org.openspcoop2.core.controllo_traffico.driver.IGestorePolicyAttive;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.slf4j.Logger;

/**     
 * GestorePolicyAttive
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
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

