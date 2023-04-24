/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.security.keystore;

import java.security.KeyStore;

import org.openspcoop2.security.SecurityException;

/**
 * FixTrustAnchorsNotEmpty
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FixTrustAnchorsNotEmpty {
	
	private FixTrustAnchorsNotEmpty() {}
	
	public static void addCertificate(KeyStore keystore) throws SecurityException{

		try{
			org.openspcoop2.utils.certificate.FixTrustAnchorsNotEmpty.addCertificate(keystore);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}

	}

}
