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

package org.openspcoop2.utils.certificate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * FixTrustAnchorsNotEmpty
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FixTrustAnchorsNotEmpty {
	
	private FixTrustAnchorsNotEmpty() {}
	
	private static Logger log = LoggerWrapperFactory.getLogger(FixTrustAnchorsNotEmpty.class);
	
	private static CertificateFactory cf = null;
	private static java.security.cert.Certificate certFix = null;
	private static final String PEM = "-----BEGIN CERTIFICATE-----\n"+
			"MIIC9TCCAd0CBE/Ior8wDQYJKoZIhvcNAQEFBQAwPjELMAkGA1UEBhMCSVQxLzAt\n"+
			"BgNVBAMMJkZpeFRydXN0QW5jaG9yc1BhcmFtZXRlck11c3RCZU5vbkVtcHR5MCAX\n"+
			"DTEyMDYwMTExMDg0N1oYDzIyODYwMzE2MTEwODQ3WjA+MQswCQYDVQQGEwJJVDEv\n"+
			"MC0GA1UEAwwmRml4VHJ1c3RBbmNob3JzUGFyYW1ldGVyTXVzdEJlTm9uRW1wdHkw\n"+
			"ggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDDoLP0pdU4WM5DalQHPBhC\n"+
			"ewg2vuS/S6gnG9gLr3N1FzH0efpBuUwX8R7tjX4bEAHnkJdfy+SVAOargNBJnIje\n"+
			"HFmfIwWVjqnHSahZR2IbR8v9LnPgWFljmyu6rtqlSKpL2qhU0pyVaJCtzBUqsTia\n"+
			"BLyQVj+aJJZIjT/BrbNogHds3Ez8Jsh24XbJWA/GcrqFOPW8VP4hg8RS+v7/rr5o\n"+
			"epNQ1l5kc7lg/G4ABpBi3krqCRP2ZVlLdYdv3UbOWyUSOzn6PP17BAgaUAdHJNxg\n"+
			"yxN3F39xMElgO6q9ZykgCDwlAjQtP/ejo5vvRNy7ZEnkY5st7FuUOErOReDQRnFZ\n"+
			"AgMBAAEwDQYJKoZIhvcNAQEFBQADggEBAIq3c5tpfU+L3843mT1iVC2I7D85l2aG\n"+
			"UgxMtBm+yYf428SR/ziL/9Z0DVA8G/6dOLpT2MdKQWEeAR8D3V0VnFZ7quysCug8\n"+
			"XdmSW1kL5Z/rtXHfvQ6FOVYCaOV2f5Th9Hl0Z2dKVLNYBHzTL65OoB+yYMNGXv5Q\n"+
			"ITke2pz4vP3LYv4RDFl80g1Qofpeqa4AFuCkmxlntL7SBCJMtlQutRtJ99ZsxgYG\n"+
			"Mp0Afz8RQ2nSpTW6gUEcYx80oWRSipR7+dmMyeESYnv9l5K6raw3GIrNtzqkfRbx\n"+
			"mZaYPs2bYuX52F4fNc3jqQ8Zd2XnH2nMtHShjFVVfvAY9/LueO2LTuk=\n"+
			"-----END CERTIFICATE-----";
	private static synchronized void initCertificateFactory(){
		if(FixTrustAnchorsNotEmpty.cf==null){
			try (InputStream is = new ByteArrayInputStream(PEM.getBytes());){
				FixTrustAnchorsNotEmpty.cf = org.openspcoop2.utils.certificate.CertificateFactory.getCertificateFactory();
				FixTrustAnchorsNotEmpty.certFix = FixTrustAnchorsNotEmpty.cf.generateCertificate(is);
			}catch(Exception e){
				log.error(e.getMessage(),e);
			}
		}
	}
	
	public static void addCertificate(KeyStore keystore) throws UtilsException{

		try{
			
			// Se esiste un certificato non devo fare altro
			Enumeration<String> aliases = keystore.aliases();
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				/** System.out.println("ALIAS["+alias+"] ..."); */
				if(keystore.isCertificateEntry(alias)){
					/** System.out.println("TROVATO!!!!"); */
					return;
				}
			}
			/** System.out.println("NON TROVATO!"); */
			
			if(FixTrustAnchorsNotEmpty.cf==null){
				FixTrustAnchorsNotEmpty.initCertificateFactory();
			}
			keystore.setCertificateEntry("FixTrustAnchorsParameterMustBeNonEmpty", FixTrustAnchorsNotEmpty.certFix);
			/** System.out.println("AGGIUNTO"); */

		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}

	}

}
