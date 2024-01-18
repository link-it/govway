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

package org.openspcoop2.utils.certificate;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.cert.CertificateParsingException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.SystemDate;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;

/**
 * ClientTest
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Reader {

	public static void main(String[] args) throws UtilsException, FileNotFoundException, CertificateParsingException {
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ALL);
		Logger log = LoggerWrapperFactory.getLogger(Reader.class);
		
		DateManager.initializeDataManager(SystemDate.class.getName(), new Properties(), log);
		
		String usage = "\n\nUsage: java "+Reader.class.getName()+" TYPE PATH [PASSWORD]\n\tTypes: "+ArrayUtils.toString(ArchiveType.values());
		if(args==null || args.length<2) {
			throw new UtilsException("Errore: parametri non forniti"+usage);
		}
		
		String typeArg = args[0];
		ArchiveType type = null;
		try {
			type = ArchiveType.valueOf(typeArg.toUpperCase());
		}catch(Exception e) {
			throw new UtilsException("Errore: tipo archivio '"+typeArg+"' non supportato"+usage);
		}
		
		String pathArg = args[1];
		File f = new File(pathArg);
		if(!f.exists()) {
			throw new UtilsException("Errore: file '"+f.getAbsolutePath()+"' non esistente"+usage);
		}
		if(!f.canRead()) {
			throw new UtilsException("Errore: file '"+f.getAbsolutePath()+"' non accessibile in lettura"+usage);
		}
		byte[] content = FileSystemUtilities.readBytesFromFile(f);
		
		String password = null;
		if(ArchiveType.JKS.equals(type) || ArchiveType.PKCS12.equals(type)) {
			if(args.length<3) {
				throw new UtilsException("Errore: password non fornita, obbligatoria per il tipo di archivio '"+type.name()+"' indicato"+usage);
			}
			password = args[2];
		}
		
		boolean printOnlySubjectNormalizzato = false;
		if(args.length>100) {
			printOnlySubjectNormalizzato = Boolean.valueOf(args[99]);
		}
		
		StringBuilder sb = new StringBuilder();
		read(type, content, password, printOnlySubjectNormalizzato, sb);
		String debug = sb.toString();
		log.info(debug);
	}
	
	public static void read(ArchiveType type, byte[] content, String password, boolean printOnlySubjectNormalizzato, StringBuilder sb) throws UtilsException, CertificateParsingException {
		
		Certificate c = ArchiveLoader.load(type, content, 0, password);
		
		if(printOnlySubjectNormalizzato) {
			
			sb.append(c.getCertificate().getSubject().getNameNormalized());
			
		}
		else {
		
			sb.append("CERT Class: "+c.getCertificate().getCertificate().getClass().getName()).append("\n");
			sb.append("CERT S.N.: "+c.getCertificate().getSerialNumber()).append("\n");
			sb.append("CERT SigAlgName: "+c.getCertificate().getSigAlgName()).append("\n");
			sb.append("CERT Type: "+c.getCertificate().getType()).append("\n");
			sb.append("CERT Version: "+c.getCertificate().getVersion()).append("\n");
			sb.append("CERT Valid: "+c.getCertificate().isValid()).append("\n");
			sb.append("CERT SelfIssued: "+c.getCertificate().isSelfIssued()).append("\n");
			sb.append("CERT SelfSigned: "+c.getCertificate().isSelfSigned()).append("\n");
			sb.append("\n").append("\n");
			sb.append("CERT Subject.CN: "+c.getCertificate().getSubject().getCN()).append("\n");
			sb.append("CERT Subject.toString: "+c.getCertificate().getSubject().toString()).append("\n");
			sb.append("CERT Subject.name: "+c.getCertificate().getSubject().getName()).append("\n");
			sb.append("CERT Subject.canonicalName: "+c.getCertificate().getSubject().getCanonicalName()).append("\n");
			sb.append("CERT Subject.RFC1779Name: "+c.getCertificate().getSubject().getRFC1779Name()).append("\n");
			sb.append("CERT Subject.RFC2253Name: "+c.getCertificate().getSubject().getRFC2253Name()).append("\n");
			sb.append("CERT Subject.nameNormalized: "+c.getCertificate().getSubject().getNameNormalized()).append("\n");
			sb.append("\n").append("\n");
			sb.append("CERT Issuer.CN: "+c.getCertificate().getIssuer().getCN()).append("\n");
			sb.append("CERT Issuer.toString: "+c.getCertificate().getIssuer().toString()).append("\n");
			sb.append("CERT Issuer.name: "+c.getCertificate().getIssuer().getName()).append("\n");
			sb.append("CERT Issuer.canonicalName: "+c.getCertificate().getIssuer().getCanonicalName()).append("\n");
			sb.append("CERT Issuer.RFC1779Name: "+c.getCertificate().getIssuer().getRFC1779Name()).append("\n");
			sb.append("CERT Issuer.RFC2253Name: "+c.getCertificate().getIssuer().getRFC2253Name()).append("\n");
			sb.append("CERT Issuer.nameNormalized: "+c.getCertificate().getIssuer().getNameNormalized()).append("\n");
			sb.append("\n").append("\n");
			sb.append("CERT NotBefore: "+c.getCertificate().getNotBefore()).append("\n");
			sb.append("CERT NotAfter: "+c.getCertificate().getNotAfter()).append("\n");
			sb.append("\n").append("\n");
			List<ExtendedKeyUsage> extendedKeyUsage = c.getCertificate().getExtendedKeyUsage();
			if(extendedKeyUsage!=null) {
				for (ExtendedKeyUsage usageEx : extendedKeyUsage) {
					sb.append("CERT ExtendedKeyUsage: "+usageEx).append("\n");
					sb.append("\n").append("\n");
				}
			}
			List<KeyUsage> keyUsage = c.getCertificate().getKeyUsage();
			if(keyUsage!=null) {
				for (KeyUsage usageEx : keyUsage) {
					sb.append("CERT KeyUsage: "+usageEx).append("\n");
					sb.append("\n").append("\n");
					
				}
			}
			
		}

	}

}
