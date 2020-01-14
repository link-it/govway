/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

import org.openspcoop2.security.SecurityException;

/**
 * FixTrustAnchorsNotEmpty
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FixTrustAnchorsNotEmpty {
	
	private static CertificateFactory cf = null;
	private static java.security.cert.Certificate certFix = null;
	private static synchronized void initCertificateFactory(){
		if(FixTrustAnchorsNotEmpty.cf==null){
			InputStream is = null;
			try{
				FixTrustAnchorsNotEmpty.cf = CertificateFactory.getInstance("X.509");
				is = FixTrustAnchorsNotEmpty.class.getResourceAsStream("/org/openspcoop2/security/keystore/FixTrustAnchorsParameterMustBeNonEmpty.cer");
				FixTrustAnchorsNotEmpty.certFix = FixTrustAnchorsNotEmpty.cf.generateCertificate(is);
			}catch(Exception e){
				e.printStackTrace(System.out);
			}finally{
				try{
					if(is!=null){
						is.close();
					}
				}catch(Exception eClose){}
			}
		}
	}
	
	public static void addCertificate(KeyStore keystore) throws SecurityException{

		try{
			
			// Se esiste un certificato non devo fare altro
			Enumeration<String> aliases = keystore.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				//System.out.println("ALIAS["+alias+"] ...");
				if(keystore.isCertificateEntry(alias)){
					//System.out.println("TROVATO!!!!");
					return;
				}
			}
			//System.out.println("NON TROVATO!");
			
			if(FixTrustAnchorsNotEmpty.cf==null){
				FixTrustAnchorsNotEmpty.initCertificateFactory();
			}
			keystore.setCertificateEntry("FixTrustAnchorsParameterMustBeNonEmpty", FixTrustAnchorsNotEmpty.certFix);
			//System.out.println("AGGIUNTO");

		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}

	}

}
