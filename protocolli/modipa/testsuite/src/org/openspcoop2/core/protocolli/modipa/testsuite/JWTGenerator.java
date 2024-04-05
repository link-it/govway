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

package org.openspcoop2.core.protocolli.modipa.testsuite;

import java.io.File;
import java.util.Properties;

import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWSOptions;
import org.openspcoop2.utils.security.JsonSignature;
import org.openspcoop2.utils.security.JwtHeaders;

/**
* JWTGenerator
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class JWTGenerator {

	public static void main(String[] args) throws Exception {
		
		if(args==null || args.length<5) {
			throw new Exception("Error Usage: JWTGenerator jsonPayload keystore password alias algo");
		}
		
		String jsonInput = FileSystemUtilities.readFile(args[0]);
		String keystore = args[1];
		String password = args[2];
		String alias = args[3];
		String algo = args[4];
		
		File fKeystore = new File(keystore); 
		
		Properties signatureProps = new Properties();
		signatureProps.put("rs.security.keystore.file", fKeystore.getPath());
		signatureProps.put("rs.security.keystore.type", fKeystore.getName().endsWith(".p12") ? "pkcs12" : "jks");
		signatureProps.put("rs.security.keystore.alias",alias);
		signatureProps.put("rs.security.keystore.password",password);
		signatureProps.put("rs.security.key.password",password);
		signatureProps.put("rs.security.signature.algorithm",algo);
//		signatureProps.put("rs.security.signature.include.cert","false");
//		signatureProps.put("rs.security.signature.include.public.key","false");
//		signatureProps.put("rs.security.signature.include.key.id","false");
//		signatureProps.put("rs.security.signature.include.cert.sha1","false");
//		signatureProps.put("rs.security.signature.include.cert.sha256","false");
		
		JWSOptions options = new JWSOptions(JOSESerialization.COMPACT);
		JwtHeaders jwtHeader = new JwtHeaders();
		jwtHeader.setType("JWT");
		jwtHeader.setKid(alias);
		Certificate c = ArchiveLoader.loadFromKeystorePKCS12(FileSystemUtilities.readBytesFromFile(fKeystore), alias, password);
		jwtHeader.addX509cert(c.getCertificate().getCertificate());
		jwtHeader.setAddX5C(true);
		JsonSignature jsonSignature = new JsonSignature(signatureProps, jwtHeader, options);

		String token = jsonSignature.sign(jsonInput);
		System.out.println(token);
	}

}
