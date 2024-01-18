/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.security.message.soapbox;

import org.adroitlogic.soapbox.MessageSecurityContext;
import org.adroitlogic.soapbox.SBConstants;
import org.adroitlogic.soapbox.SecurityConfig;
import org.adroitlogic.soapbox.SecurityRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * SymmetricCryptoUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SymmetricCryptoUtils {

	 public static Element createKeyInfoElement(
		        Document doc, SecurityRequest secReq, MessageSecurityContext msgSecCtx, SecurityConfig secConfig) {
		 
		// create KeyInfo and SecurityTokenReference elements
		// String alias = secReq.getCertAlias();
		 Element keyInfoElem = doc.createElementNS(SBConstants.DS, "ds:KeyInfo");
		 Element securityTokenReferenceElem = doc.createElementNS(SBConstants.WSSE, "wsse:SecurityTokenReference");
		 
		 
		 keyInfoElem.appendChild(securityTokenReferenceElem);
		 return keyInfoElem;
		 
		 
	 }
	
}
