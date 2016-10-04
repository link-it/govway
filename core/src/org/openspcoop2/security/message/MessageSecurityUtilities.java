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

package org.openspcoop2.security.message;

import java.util.Hashtable;

import org.openspcoop2.security.message.constants.SecurityConstants;

/**
 * MessageSecurityUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageSecurityUtilities {

	public static boolean processSOAPFault(Hashtable<String,Object> messageSecurityProperties){
		
		// Default disabilitati
		boolean processEncryptSOAPFault = false;
		boolean processSignatureSOAPFault = false;
		
		String propertyEncrypt = (String) messageSecurityProperties.get(SecurityConstants.ENCRYPTION_SOAP_FAULT);
		if(propertyEncrypt!=null){
			if(SecurityConstants.TRUE.equalsIgnoreCase(propertyEncrypt)){
				processEncryptSOAPFault = true;
			}
		}
		
		String propertySignature = (String) messageSecurityProperties.get(SecurityConstants.SIGNATURE_SOAP_FAULT);
		if(propertySignature!=null){
			if(SecurityConstants.TRUE.equalsIgnoreCase(propertySignature)){
				processSignatureSOAPFault = true;
			}
		}
		
		String action = (String) messageSecurityProperties.remove(SecurityConstants.ACTION);
		
		String [] splitActions = action.split(" ");
		StringBuffer bfNewActions = new StringBuffer();
		for (int i = 0; i < splitActions.length; i++) {
			
			String a = splitActions[i].trim();
			
			if(SecurityConstants.ENCRYPT_ACTION.equals(a)){
				if(processEncryptSOAPFault){
					if(bfNewActions.length()>0){
						bfNewActions.append(" ");
					}
					bfNewActions.append(a);
				}
			}
			else if(SecurityConstants.SIGNATURE_ACTION.equals(a)){
				if(processSignatureSOAPFault){
					if(bfNewActions.length()>0){
						bfNewActions.append(" ");
					}
					bfNewActions.append(a);
				}
			}
			else{
				// altra azione la aggiungo
				if(bfNewActions.length()>0){
					bfNewActions.append(" ");
				}
				bfNewActions.append(a);
			}
		}
		
		
		if(bfNewActions.length()>0){
			String newActions = bfNewActions.toString();
			if(SecurityConstants.TIMESTAMP_ACTION.equals(newActions)){
				// se rimane solo TIMESTAMP disabilitiamo
				return false;
			}else{
				messageSecurityProperties.put(SecurityConstants.ACTION,newActions);
				return true;
			}
		}
		else{
			return false;
		}
	}
	
	
}
