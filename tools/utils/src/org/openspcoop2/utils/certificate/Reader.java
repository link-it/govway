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

package org.openspcoop2.utils.certificate;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.SystemDate;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * ClientTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Reader {

	public static void main(String[] args) throws Exception {
		
		DateManager.initializeDataManager(SystemDate.class.getName(), new Properties(), LoggerWrapperFactory.getLogger(Reader.class));
		
		String usage = "\n\nUsage: java "+Reader.class.getName()+" TYPE PATH [PASSWORD]\n\tTypes: "+ArrayUtils.toString(ArchiveType.values());
		if(args==null || args.length<2) {
			throw new Exception("Errore: parametri non forniti"+usage);
		}
		
		String typeArg = args[0];
		ArchiveType type = null;
		try {
			type = ArchiveType.valueOf(typeArg.toUpperCase());
		}catch(Exception e) {
			throw new Exception("Errore: tipo archivio '"+typeArg+"' non supportato"+usage);
		}
		
		String pathArg = args[1];
		File f = new File(pathArg);
		if(f.exists()==false) {
			throw new Exception("Errore: file '"+f.getAbsolutePath()+"' non esistente"+usage);
		}
		if(f.canRead()==false) {
			throw new Exception("Errore: file '"+f.getAbsolutePath()+"' non accessibile in lettura"+usage);
		}
		byte[] content = FileSystemUtilities.readBytesFromFile(f);
		
		String password = null;
		if(ArchiveType.JKS.equals(type) || ArchiveType.PKCS12.equals(type)) {
			if(args.length<3) {
				throw new Exception("Errore: password non fornita, obbligatoria per il tipo di archivio '"+type.name()+"' indicato"+usage);
			}
			password = args[2];
		}
		
		Certificate c = ArchiveLoader.load(type, content, 0, password);
		
		boolean printOnlySubjectNormalizzato = false;
		
		if(printOnlySubjectNormalizzato) {
			
			System.out.println(c.getCertificate().getSubject().getNameNormalized());
			
		}
		else {
		
			System.out.println("CERT Class: "+c.getCertificate().getCertificate().getClass().getName());
			System.out.println("CERT S.N.: "+c.getCertificate().getSerialNumber());
			System.out.println("CERT SigAlgName: "+c.getCertificate().getSigAlgName());
			System.out.println("CERT Type: "+c.getCertificate().getType());
			System.out.println("CERT Version: "+c.getCertificate().getVersion());
			System.out.println("CERT Valid: "+c.getCertificate().isValid());
			System.out.println("CERT SelfIssued: "+c.getCertificate().isSelfIssued());
			System.out.println("CERT SelfSigned: "+c.getCertificate().isSelfSigned());
			System.out.println("\n");
			System.out.println("CERT Subject.CN: "+c.getCertificate().getSubject().getCN());
			System.out.println("CERT Subject.toString: "+c.getCertificate().getSubject().toString());
			System.out.println("CERT Subject.name: "+c.getCertificate().getSubject().getName());
			System.out.println("CERT Subject.canonicalName: "+c.getCertificate().getSubject().getCanonicalName());
			System.out.println("CERT Subject.RFC1779Name: "+c.getCertificate().getSubject().getRFC1779Name());
			System.out.println("CERT Subject.RFC2253Name: "+c.getCertificate().getSubject().getRFC2253Name());
			System.out.println("CERT Subject.nameNormalized: "+c.getCertificate().getSubject().getNameNormalized());
			System.out.println("\n");
			System.out.println("CERT Issuer.CN: "+c.getCertificate().getIssuer().getCN());
			System.out.println("CERT Issuer.toString: "+c.getCertificate().getIssuer().toString());
			System.out.println("CERT Issuer.name: "+c.getCertificate().getIssuer().getName());
			System.out.println("CERT Issuer.canonicalName: "+c.getCertificate().getIssuer().getCanonicalName());
			System.out.println("CERT Issuer.RFC1779Name: "+c.getCertificate().getIssuer().getRFC1779Name());
			System.out.println("CERT Issuer.RFC2253Name: "+c.getCertificate().getIssuer().getRFC2253Name());
			System.out.println("CERT Issuer.nameNormalized: "+c.getCertificate().getIssuer().getNameNormalized());
			System.out.println("\n");
			System.out.println("CERT NotBefore: "+c.getCertificate().getNotBefore());
			System.out.println("CERT NotAfter: "+c.getCertificate().getNotAfter());
			System.out.println("\n");
			List<ExtendedKeyUsage> extendedKeyUsage = c.getCertificate().getExtendedKeyUsage();
			if(extendedKeyUsage!=null) {
				for (ExtendedKeyUsage usageEx : extendedKeyUsage) {
					System.out.println("CERT ExtendedKeyUsage: "+usageEx);
					System.out.println("\n");
				}
			}
			List<KeyUsage> keyUsage = c.getCertificate().getKeyUsage();
			if(keyUsage!=null) {
				for (KeyUsage usageEx : keyUsage) {
					System.out.println("CERT KeyUsage: "+usageEx);
					System.out.println("\n");
					
				}
			}
			
		}

	}

}
