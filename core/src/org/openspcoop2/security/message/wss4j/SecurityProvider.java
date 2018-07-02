/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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


package org.openspcoop2.security.message.wss4j;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.security.message.constants.EncryptionKeyTransportAlgorithm;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.xml.XMLCostanti;

/**     
 * SecurityProvider
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 14182 $, $Date: 2018-06-23 21:12:23 +0200 (Sat, 23 Jun 2018) $
 */
public class SecurityProvider extends org.openspcoop2.security.message.xml.SecurityProvider  {

	@Override
	public List<String> getValues(String id) throws ProviderException {
		if(SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE.equals(id)) {
			List<String> l = new ArrayList<>();
			l.add(SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_DIGEST);
			l.add(SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_TEXT);
			l.add(SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_NONE);
			return l;
		}
		else {
			return super.getValues(id);
		}
	}

	@Override
	public List<String> getLabels(String id) throws ProviderException {
		if(SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE.equals(id)) {
			List<String> l = new ArrayList<>();
			l.add("Password "+SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_DIGEST.replace("Password", ""));
			l.add("Password "+SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_TEXT.replace("Password", ""));
			l.add("Password "+SecurityConstants.USERNAME_TOKEN_PASSWORD_TYPE_NONE.replace("Password", ""));
			return l;
		}
		else {
			return super.getLabels(id);
		}
	}

	@Override
	public String getDefault(String id) throws ProviderException {
		
		if(XMLCostanti.ID_ENCRYPT_TRANSPORT_KEY_WRAP_ALGORITHM.equals(id)) {
			return EncryptionKeyTransportAlgorithm.RSA_OAEP.getUri();
		}
		else {
			return super.getDefault(id);
		}
		
	}

}
