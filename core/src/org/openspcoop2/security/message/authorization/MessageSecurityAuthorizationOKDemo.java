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



package org.openspcoop2.security.message.authorization;


/**
 * Implementazione esempio dell'interfaccia Authorization
 *
 * @author Montebove Luciano (L.Montebove@finsiel.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class MessageSecurityAuthorizationOKDemo  implements IMessageSecurityAuthorization{


    public MessageSecurityAuthorizationOKDemo() {
    }


    @Override
    public MessageSecurityAuthorizationResult authorize(MessageSecurityAuthorizationRequest request) throws SecurityException{
    	
//    	System.out.println("Autorizzo servizio per: " + request.getWssSecurityPrincipal() +
//    			"|"+ request.getBusta().getMittente() + "|" + request.getBusta().getServizio() + "|"  + request.getBusta().getAzione() + "|");
  	
    	MessageSecurityAuthorizationResult result = new MessageSecurityAuthorizationResult();
    	result.setAuthorized(true);
    	return result;
    }
    
}
