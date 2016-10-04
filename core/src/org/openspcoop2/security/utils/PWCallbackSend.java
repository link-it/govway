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



package org.openspcoop2.security.utils;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;

/**
 * Gestore delle password dei certificati scambiati con wssecurity, gestore di esempio.
 * 	
 * @author Montebove Luciano <L.Montebove@finsiel.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class PWCallbackSend implements CallbackHandler {

    @Override
	public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
		//System.out.println("Alias ["+pc.getIdentifer()+"]");
                // setta la password per l'alias della chiave 
//                if ("erogatore".equals(pc.getIdentifer())) {
//                    pc.setPassword("foobar");
//                }
                
               // System.out.println("Alias ["+pc.getIdentifier()+"]");
                if ("pd".equals(pc.getIdentifier())) {
                	pc.setPassword("certpd");
                }else if ("pa".equals(pc.getIdentifier())) {
                	pc.setPassword("certpa");
                }
                else if ("pdP12".equalsIgnoreCase(pc.getIdentifier())) {
                	pc.setPassword("keypd");
                }else if ("paP12".equalsIgnoreCase(pc.getIdentifier())) {
                	pc.setPassword("keypa");
                }
                
                
              //pc.setPassword("foobar");
            } else {
                throw new UnsupportedCallbackException(callbacks[i],
                        "Unrecognized Callback");
            }
        }
    }
}
