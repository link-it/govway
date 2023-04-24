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
package org.openspcoop2.protocol.registry;

import java.io.File;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.StatoCheck;
import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.CRLCertstore;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.ocsp.IOCSPResourceReader;
import org.openspcoop2.utils.certificate.ocsp.OCSPResourceReader;
import org.openspcoop2.utils.certificate.ocsp.OCSPValidatorImpl;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.IOCSPValidator;
import org.slf4j.Logger;

/**
 *  CertificateUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CertificateUtils {

	private static String FORMAT_DATE_CERTIFICATE = "dd/MM/yyyy HH:mm";
	
	public static String toString(Certificate certificate, String separator, String newLine) throws Exception {
		StringBuilder sb = new StringBuilder();
		
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_CERTIFICATE);
		
		String certificatoSubject = certificate.getCertificate().getSubject().getNameNormalized();
		boolean certificatoSelfSigned = certificate.getCertificate().isSelfSigned();
		String certificatoIssuer = null;
		if(!certificatoSelfSigned) {
			certificatoIssuer = certificate.getCertificate().getIssuer().getNameNormalized();
		}
		sb.append(CostantiLabel.CERTIFICATE_SUBJECT).append(separator).append(certificatoSubject);
		if(certificatoSelfSigned) {
			sb.append(newLine).append(CostantiLabel.CERTIFICATE_SELF_SIGNED);
		}
		else {
			sb.append(newLine).append(CostantiLabel.CERTIFICATE_ISSUER).append(separator).append(certificatoIssuer);
		}
		
		String notBefore = null;
		if(certificate.getCertificate().getNotBefore()!=null) {
			notBefore = sdf.format(certificate.getCertificate().getNotBefore());
		}
		String notAfter = null;
		if(certificate.getCertificate().getNotAfter()!=null) {
			notAfter = sdf.format(certificate.getCertificate().getNotAfter());
		}
		if(notBefore!=null) {
			sb.append(newLine).append(CostantiLabel.CERTIFICATE_NOT_BEFORE).append(separator).append(notBefore);
		}
		if(notAfter!=null) {
			sb.append(newLine).append(CostantiLabel.CERTIFICATE_NOT_AFTER).append(separator).append(notAfter);
		}
		
		//String serialNumber = certificate.getCertificate().getSerialNumber() + "";
		String serialNumberHex = certificate.getCertificate().getSerialNumberHex() + "";
		//sb.append(newLine).append(CostantiLabel.CERTIFICATE_SERIAL_NUMBER).append(separator).append(serialNumber);
		//sb.append(newLine).append(CostantiLabel.CERTIFICATE_SERIAL_NUMBER_HEX).append(separator).append(serialNumberHex);
		sb.append(newLine).append(CostantiLabel.CERTIFICATE_SERIAL_NUMBER).append(separator).append(serialNumberHex);
		
		//String certificatoType = certificate.getCertificate().getType();
		//String certificatoVersion = certificate.getCertificate().getVersion() + "";
		
		return sb.toString();
	}
	
	public static String toStringKeyStore(KeystoreParams params,
			String separator, String newLine) {
		return _toString(true, params.getKeyAlias(), null, null,
				params.getPath(), params.getType(), 
				separator, newLine);
	}
	public static String toStringKeyStore(String storePath, String storeType,
			String keyAlias,
			String separator, String newLine) {
		return _toString(true, keyAlias, null, null,
				storePath, storeType, 
				separator, newLine);
	}
	public static String toStringTrustStore(KeystoreParams params,
			String separator, String newLine) {
		return _toString(false, params.getKeyAlias(), params.getCrls(), params.getOcspPolicy(),
				params.getPath(), params.getType(), 
				separator, newLine);
	}
	public static String toStringTrustStore(String storePath, String storeType,
			String trustCRL, String ocspPolicy,
			String separator, String newLine) {
		return toStringTrustStore(storePath, storeType,
				trustCRL, ocspPolicy,
				null,
				separator, newLine);
	}
	public static String toStringTrustStore(String storePath, String storeType,
			String trustCRL, String ocspPolicy,
			String certAlias,
			String separator, String newLine) {
		return _toString(false, certAlias, trustCRL, ocspPolicy,
				storePath, storeType, 
				separator, newLine);
	}
	public static String _toString(boolean keystore, String keyAlias, String trustCRL, String ocspPolicy,
			String storePath, String storeType, 
			String separator, String newLine) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(keystore? CostantiLabel.KEYSTORE : CostantiLabel.TRUSTSTORE);
		sb.append(separator);
		boolean hsm = HSMUtils.isKeystoreHSM(separator);
		String location = storePath;
		if(hsm) {
			location = CostantiLabel.STORE_HSM;
		}
		sb.append("(").append(storeType).append(") ").append(location);
		
		if(!keystore && trustCRL!=null) {
			sb.append(newLine);
			sb.append(CostantiLabel.CRLs);
			sb.append(separator);
			sb.append(trustCRL);
		}
		
		if(!keystore && ocspPolicy!=null) {
			sb.append(newLine);
			sb.append(CostantiLabel.OCSP_POLICY);
			sb.append(separator);
			sb.append(ocspPolicy);
		}
		
		if(keyAlias!=null) {
			sb.append(newLine);
			if(keystore) {
				sb.append(CostantiLabel.KEY_ALIAS);
			}
			else {
				sb.append(CostantiLabel.CERTIFICATE_ALIAS);
			}
			sb.append(separator);
			sb.append(keyAlias);
		}
		
		return sb.toString();
	}
	
	
	public static KeystoreParams readKeyStoreParamsJVM() {
		
		KeystoreParams params = null;
		
		String keyStoreLocation = System.getProperty("javax.net.ssl.keyStore");
		String keyStoreType = System.getProperty("javax.net.ssl.keyStoreType");
		String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
		
		if(keyStoreLocation!=null || keyStoreType!=null) {
			if(keyStoreLocation==null) {
				keyStoreLocation = "NONE";
			}
			if(keyStoreType==null) {
				keyStoreType = "JKS";
			}
			params = new KeystoreParams();
			params.setPath(keyStoreLocation);
			params.setType(keyStoreType);
			params.setPassword(keyStorePassword);
		}
	
		return params;
	}
	
	public static KeystoreParams readTrustStoreParamsJVM() {
		
		KeystoreParams params = null;
		
		String trustStoreLocation = System.getProperty("javax.net.ssl.trustStore");
		String trustStoreType = System.getProperty("javax.net.ssl.trustStoreType");
		String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
		
		if(trustStoreLocation!=null) {
			if(trustStoreType==null) {
				trustStoreType = "JKS";
			}
			params = new KeystoreParams();
			params.setPath(trustStoreLocation);
			params.setType(trustStoreType);
			params.setPassword(trustStorePassword);
		}
	
		return params;
	}
	
	
	public static CertificateCheck checkCertificateClient(List<byte[]>certs, List<Boolean> strictValidation, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws Exception{
		
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_CERTIFICATE);
		
		if(certs==null || certs.size()<=0) {
			throw new Exception("Nessun certificato individuato");
		}
		if(strictValidation==null || strictValidation.size()<=0) {
			throw new Exception("Nessuna informazione sul tipo di validazione fornita");
		}
		if(strictValidation.size()!=certs.size()) {
			throw new Exception("Rilevata inconsistenza tra le informazioni fornite sul tipo di validazione e i certificati");
		}
		
		CertificateCheck esito = new CertificateCheck();
		
		boolean error = false;
		boolean warning = false;
		boolean almostOneValid = false;
		for (int i = 0; i < certs.size(); i++) {
			boolean principale = (i==0);
			Certificate certificate = null;
			String identitaCertificato = "Certificato "+ (principale ? "principale" : "n."+(i+1)); 
			String credenziale = "Certificato";
			if(principale && certs.size()>1) {
				credenziale = credenziale + " principale";
			}
			try {
				byte [] bytesCert = certs.get(i);
				boolean strictValidationCert = strictValidation.get(i);
				certificate = ArchiveLoader.load(bytesCert);
				
				String hex = certificate.getCertificate().getSerialNumberHex();
				String certificateDetails = null;
				if(addCertificateDetails) {
					certificateDetails = CertificateUtils.toString(certificate, separator, newLine);
				}
				else {
					credenziale = credenziale+ " (CN:"+certificate.getCertificate().getSubject().getCN()+" serialNumber:"+hex+")";
				}
				boolean check = true;
				try {
					certificate.getCertificate().checkValid();
				}catch(Throwable t) {
					check = false;
					if(strictValidationCert) {
						String msgErrore = credenziale+" non valido: "+t.getMessage();
						error = true;
						esito.addError(identitaCertificato, msgErrore, certificateDetails);
						continue;
					}
					else {
						String msgErrore = credenziale+" utilizzato per attivare la validazione tramite Subject e Issuer non risulta valido: "+t.getMessage();
						warning = true;
						esito.addWarning(identitaCertificato, msgErrore, certificateDetails);
					}
				}
									
				if(check && certificate.getCertificateChain()!=null && !certificate.getCertificateChain().isEmpty()) {
					for (CertificateInfo caChain : certificate.getCertificateChain()) {
						String hex_caChain = caChain.getSerialNumberHex();
						String credenziale_caChain = "(CN:"+caChain.getSubject().getCN()+" serialNumber:"+hex_caChain+")";
						try {
							caChain.checkValid();
						}catch(Throwable t) {
							check = false;
							String msgErrore = (credenziale+"; un certificato della catena "+credenziale_caChain+" non risulta valido: "+t.getMessage());
							if(strictValidationCert) {
								error = true;
								esito.addError(identitaCertificato, msgErrore, certificateDetails);
								continue;
							}
							else {
								warning = true;
								esito.addWarning(identitaCertificato, msgErrore, certificateDetails);
							}
						}
					}
				}
				
				if(check && sogliaWarningGiorni>0) {
					if(certificate.getCertificate().getNotAfter()!=null) {
						long expire = certificate.getCertificate().getNotAfter().getTime();
						long now = DateManager.getTimeMillis();
						long diff = expire - now;
						long soglia = (1000l*60l*60l*24l) * ((long)sogliaWarningGiorni);
						//System.out.println("=======================");
						//System.out.println("SUBJECT ["+certificate.getCertificate().getCertificate().getSubjectDN().getName()+"]");
						//System.out.println("NOT AFTER ["+certificate.getCertificate().getNotAfter()+"]");
						//System.out.println("SOGLIA ["+sogliaWarningGiorni+"]");
						//System.out.println("DEXP ["+expire+"]");
						//System.out.println("DNOW ["+now+"]");
						//System.out.println("DIFF ["+diff+"]");
						//System.out.println("SOGLIATR ["+soglia+"]");
						if(diff<soglia) {
							String msgErrore = credenziale+" prossima alla scadenza ("+sogliaWarningGiorni+" giorni): "+sdf.format(certificate.getCertificate().getNotAfter());
							warning = true;
							esito.addWarning(identitaCertificato, msgErrore, certificateDetails);
						}
					}
				}
				
			}catch(Throwable t) {
				// non dovrebbe succedere
				String msgError = "L'analisi del certificato ha prodotto un errore non atteso: "+t.getMessage();
				esito.addError(identitaCertificato, msgError, null);
				log.error(msgError, t);	
				error = true;
				continue;
			}
				
			almostOneValid = true;
		}
		
		
		if(almostOneValid) {
			if(error || warning) {
				esito.setStatoCheck(StatoCheck.WARN);
			}
			else {
				esito.setStatoCheck(StatoCheck.OK);
			}
		}
		else {
			esito.setStatoCheck(StatoCheck.ERROR);
		}
		return esito;

	}
	
	public static CertificateCheck checkTrustStore(String trustStore, boolean classpathSupported, String type, String password, String trustStoreCrls, String ocspPolicy,
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws Exception{
		return checkTrustStore(trustStore, classpathSupported, type, password, trustStoreCrls, ocspPolicy, null,
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
	}
	public static CertificateCheck checkTrustStore(String trustStore, boolean classpathSupported, String type, String password, String trustStoreCrls,String ocspPolicy,String certAlias,
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws Exception{
		boolean hsm = HSMUtils.isKeystoreHSM(type);
		byte [] keystoreBytes = null;
		if(!hsm) {
			File f = new File(trustStore);
			boolean exists = f.exists();
			boolean inClasspath = false;
			if(exists==false && classpathSupported) {
				String uri = trustStore;
				if(!trustStore.startsWith("/")) {
					uri = "/" + uri;	
				}
				try( InputStream is = CertificateUtils.class.getResourceAsStream(uri); ){
					exists = (is!=null);
					if(exists) {
						inClasspath = true;
						keystoreBytes = Utilities.getAsByteArray(is);
					}
				}
			}
			if(exists==false || (!inClasspath && f.canRead()==false)) {
				
				String storeDetails = null;
				if(addCertificateDetails) {
					storeDetails = toStringTrustStore(trustStore, type, trustStoreCrls, ocspPolicy, certAlias, separator, newLine);
				}
				String errorDetails = CostantiLabel.TRUSTSTORE;
				if(!addCertificateDetails) {
					errorDetails = errorDetails + " '"+
							//f.getAbsolutePath()+  // nel caso di path relativo viene visualizzato un PATH basato sul bin di wildfly ed e' forviante
							trustStore+
							"'";
				}
				errorDetails = errorDetails + (!exists ? " not exists" : " cannot read"); 
				
				CertificateCheck esito = new CertificateCheck();
				esito.setStatoCheck(StatoCheck.ERROR);
				esito.addError(trustStore, errorDetails, storeDetails);
				return esito;
			}
			if(!inClasspath) {
				keystoreBytes = FileSystemUtilities.readBytesFromFile(f);
			}
		}
		return checkStore(false, certAlias, trustStoreCrls, ocspPolicy,
				trustStore, keystoreBytes, type, password, 
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
	}
	public static CertificateCheck checkTrustStore(byte[] trustStore, String type, String password, String trustStoreCrls, String ocspPolicy,
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws Exception{
		return checkStore(false, null, trustStoreCrls, ocspPolicy,
				CostantiLabel.STORE_CARICATO_BASEDATI, trustStore, type, password, 
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
	}
	public static CertificateCheck checkKeyStore(String keyStore, boolean classpathSupported, String type, String password, String aliasKey,
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws Exception{
		boolean hsm = HSMUtils.isKeystoreHSM(type);
		byte [] keystoreBytes = null;
		if(!hsm) {
			File f = new File(keyStore);
			boolean exists = f.exists();
			boolean inClasspath = false;
			if(exists==false && classpathSupported) {
				String uri = keyStore;
				if(!keyStore.startsWith("/")) {
					uri = "/" + uri;	
				}
				try( InputStream is = CertificateUtils.class.getResourceAsStream(uri); ){
					exists = (is!=null);
					if(exists) {
						inClasspath = true;
						keystoreBytes = Utilities.getAsByteArray(is);
					}
				}
			}
			if(exists==false || (!inClasspath && f.canRead()==false)) {
				
				String storeDetails = null;
				if(addCertificateDetails) {
					storeDetails = toStringKeyStore(keyStore, type, aliasKey, separator, newLine);
				}
				String errorDetails = CostantiLabel.KEYSTORE;
				if(!addCertificateDetails) {
					errorDetails = errorDetails + " '"+
							//f.getAbsolutePath()+  // nel caso di path relativo viene visualizzato un PATH basato sul bin di wildfly ed e' forviante
							keyStore+
							"'";
				}
				errorDetails = errorDetails + (!exists ? " not exists" : " cannot read"); 
				
				CertificateCheck esito = new CertificateCheck();
				esito.setStatoCheck(StatoCheck.ERROR);
				esito.addError(keyStore, errorDetails, storeDetails);
				return esito;
			}
			if(!inClasspath) {
				keystoreBytes = FileSystemUtilities.readBytesFromFile(f);
			}
		}
		return checkStore(true, aliasKey, null, null,
				keyStore, keystoreBytes, type, password, 
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
	}
	public static CertificateCheck checkKeyStore(byte[] keyStore, String type, String password, String aliasKey,
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws Exception{
		return checkStore(true, aliasKey, null, null,
				CostantiLabel.STORE_CARICATO_BASEDATI, keyStore, type, password, 
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
	}
	private static CertificateCheck checkStore(boolean keystore, String aliasKey, String trustStoreCrls, String ocspPolicy,
			String storePath, byte[] storeBytes, String type, String password,
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws Exception{
		
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_CERTIFICATE);
		
		String storeDetails = "";
		if(addCertificateDetails) {
			if(keystore) {
				storeDetails = toStringKeyStore(storePath, type, aliasKey, separator, newLine);
			}
			else {
				storeDetails = toStringTrustStore(storePath, type, trustStoreCrls, ocspPolicy, aliasKey, separator, newLine);
			}
		}
				
		org.openspcoop2.utils.certificate.KeyStore store = null;
		try {
			if(HSMUtils.isKeystoreHSM(type)) {
				store = HSMManager.getInstance().getKeystore(type);
			}
			else {
				store = new org.openspcoop2.utils.certificate.KeyStore(storeBytes, type, password);
			}
		}catch(Throwable t) {
			CertificateCheck esito = new CertificateCheck();
			esito.setStatoCheck(StatoCheck.ERROR);
			esito.addError(storePath, "Non è possibile accedere al "+(keystore ? CostantiLabel.KEYSTORE : CostantiLabel.TRUSTSTORE)+": "+t.getMessage(), storeDetails);
			return esito;
		}
		
		IOCSPValidator ocspValidator = null;
		CRLCertstore crls = null;		
		if(!keystore && ( 
				(trustStoreCrls!=null && StringUtils.isNotEmpty(trustStoreCrls)) 
				|| 
				(ocspPolicy!=null && StringUtils.isNotEmpty(ocspPolicy))
				)
			) {
			
			boolean crlByOcsp = false;
			if(ocspPolicy!=null && StringUtils.isNotEmpty(ocspPolicy)) {
				LoggerBuffer lb = new LoggerBuffer();
				lb.setLogDebug(log);
				lb.setLogError(log);
				IOCSPResourceReader ocspResourceReader = new OCSPResourceReader();
				ocspValidator = new OCSPValidatorImpl(lb, store, trustStoreCrls, ocspPolicy, ocspResourceReader);
				if(ocspValidator!=null) {
					OCSPValidatorImpl gOcspValidator = (OCSPValidatorImpl) ocspValidator;
					if(gOcspValidator.getOcspConfig()!=null) {
						crlByOcsp = gOcspValidator.getOcspConfig().isCrl();
					}
				}
			}
			
			List<String> crlList = null;
			if(trustStoreCrls!=null && StringUtils.isNotEmpty(trustStoreCrls)) { 
				crlList = CRLCertstore.readCrlPaths(trustStoreCrls);
			}
			if(crlList!=null && !crlList.isEmpty()) {
				for (String path : crlList) {
					File f = new File(path);
					if(f.exists()==false || f.canRead()==false) {
						
						//String identita = CostantiLabel.CRL+" '"+path+"'";
						String identita = CostantiLabel.CRL;
						if(addCertificateDetails) {
							identita = CostantiLabel.CRL+" '"+path+"'";
						}
						String errorDetails = identita;
						if(!addCertificateDetails) {
							errorDetails = errorDetails + " '"+
									//f.getAbsolutePath()+  // nel caso di path relativo viene visualizzato un PATH basato sul bin di wildfly ed e' forviante
									path+
									"'";
						}
						errorDetails = errorDetails + (!f.exists() ? " not exists" : " cannot read"); 
						
						CertificateCheck esito = new CertificateCheck();
						esito.setStatoCheck(StatoCheck.ERROR);
						esito.addError(identita, errorDetails, storeDetails);
						return esito;
					}					
				}
				
				if(!crlByOcsp) {
					crls = new CRLCertstore(trustStoreCrls);
				}
			}
		}
		
		CertificateCheck esito = new CertificateCheck();
		boolean error = false;
		boolean warning = false;
		
		List<String> aliasesForCheck = null;
		
		if(keystore) {
			
			List<String> alias = new ArrayList<>();
			if(aliasKey!=null) {
				alias.add(aliasKey);
			}
			else {
				Enumeration<String> aliases = store.aliases();
				while (aliases.hasMoreElements()) {
					String aliasCheck = (String) aliases.nextElement();
					if(store.getKeystore().isKeyEntry(aliasCheck)) {
						alias.add(aliasCheck);
					}
				}
			}
			if(alias.isEmpty()) {
				esito = new CertificateCheck();
				esito.setStatoCheck(StatoCheck.ERROR);
				esito.addError(storePath, "Nel "+CostantiLabel.KEYSTORE+" non sono presenti chiavi private", storeDetails);
				return esito;
			}
			
			aliasesForCheck = alias;
			
		}
		else {
			
			List<String> alias = new ArrayList<>();
			if(aliasKey!=null) {
				alias.add(aliasKey);
			}
			else {
				Enumeration<String> aliases = store.aliases();
				while (aliases.hasMoreElements()) {
					String aliasCheck = (String) aliases.nextElement();
					alias.add(aliasCheck);
				}
			}
			
			if(alias.isEmpty()) {
				esito = new CertificateCheck();
				esito.setStatoCheck(StatoCheck.ERROR);
				esito.addError(storePath, "Nel "+CostantiLabel.TRUSTSTORE+" non sono presenti certificati", storeDetails);
				return esito;
			}
			
			aliasesForCheck = alias;
		}
		
		List<Certificate> listCertificate = new ArrayList<Certificate>();
		if(aliasesForCheck!=null && !aliasesForCheck.isEmpty()) {
			for (String aliasVerify : aliasesForCheck) {
				
				java.security.cert.X509Certificate cert = (java.security.cert.X509Certificate) store.getCertificate(aliasVerify);
				if(cert==null) {
					esito = new CertificateCheck();
					esito.setStatoCheck(StatoCheck.ERROR);
					esito.addError(storePath, "Nel "+
							(keystore ? CostantiLabel.KEYSTORE : CostantiLabel.TRUSTSTORE)+
							" non è presente "+
							(keystore ? "una coppia di chiavi" : "un certificato")+
							" con alias '"+aliasVerify+"'", storeDetails);
					return esito;
				}
				
				java.security.cert.Certificate[] baseCertChain = store.getCertificateChain(aliasVerify);
				List<java.security.cert.X509Certificate> certChain = null;
				if(baseCertChain!=null && baseCertChain.length>0) {
					for (int i = 0; i < baseCertChain.length; i++) {
						java.security.cert.Certificate check = baseCertChain[i];
						if(check instanceof X509Certificate) {
							if(certChain==null) {
								certChain = new ArrayList<>();
							}
							certChain.add((X509Certificate) check);
						}
					}
				}
								
				Certificate certificate = new Certificate(aliasVerify, 
						cert, 
						certChain );
				listCertificate.add(certificate);
			}
		}
				
		if(listCertificate!=null && !listCertificate.isEmpty()) {
			for (Certificate certificate : listCertificate) {	
				
				String aliasVerify = certificate.getCertificate().getName();
				String identita = "Certificato '"+aliasVerify+"'";
				
				String aliasDetails = "";
				if(!storeDetails.contains(CostantiLabel.KEY_ALIAS+separator+aliasVerify)) {
					aliasDetails = (addCertificateDetails ? newLine : "") + 
							CostantiLabel.ALIAS+separator+aliasVerify;
				}
				String certificateDetails = CertificateUtils.toString(certificate, separator, newLine);
				certificateDetails = storeDetails +
						aliasDetails +
						newLine +
						certificateDetails;
				boolean check = true;
				try {
					if(keystore || crls==null) {
						certificate.getCertificate().checkValid();
					}
					else {
						certificate.getCertificate().checkValid(crls.getCertStore(), store);
					}
					if(ocspValidator!=null) {
						ocspValidator.valid(certificate.getCertificate().getCertificate());
					}
				}catch(Throwable t) {
					check = false;
					String msgErrore = "Certificato non valido: "+t.getMessage();
					error = true;
					esito.addError(identita, msgErrore, certificateDetails);
					continue;
				}
									
				if(check && certificate.getCertificateChain()!=null && !certificate.getCertificateChain().isEmpty()) {
					for (CertificateInfo caChain : certificate.getCertificateChain()) {
						String hex_caChain = caChain.getSerialNumberHex();
						String credenziale_caChain = "(CN:"+caChain.getSubject().getCN()+" serialNumber:"+hex_caChain+")";
						try {
							if(keystore || crls==null) {
								caChain.checkValid();
							}
							else {
								caChain.checkValid(crls.getCertStore(), store);
							}
							// La validazione della catena viene effettuata direttamente dal ocsp validator se previsto dalla policy
//							if(ocspValidator!=null) {
//								ocspValidator.valid(certificate.getCertificate().getCertificate());
//							}
						}catch(Throwable t) {
							check = false;
							String msgErrore = ("Un certificato della catena "+credenziale_caChain+" non risulta valido: "+t.getMessage());
							error = true;
							esito.addError(identita, msgErrore, certificateDetails);
						}
					}
				}
				
				if(check && sogliaWarningGiorni>0) {
					if(certificate.getCertificate().getNotAfter()!=null) {
						long expire = certificate.getCertificate().getNotAfter().getTime();
						long now = DateManager.getTimeMillis();
						long diff = expire - now;
						long soglia = (1000l*60l*60l*24l) * ((long)sogliaWarningGiorni);
						//System.out.println("=======================");
						//System.out.println("SUBJECT ["+certificate.getCertificate().getCertificate().getSubjectDN().getName()+"]");
						//System.out.println("NOT AFTER ["+certificate.getCertificate().getNotAfter()+"]");
						//System.out.println("SOGLIA ["+sogliaWarningGiorni+"]");
						//System.out.println("DEXP ["+expire+"]");
						//System.out.println("DNOW ["+now+"]");
						//System.out.println("DIFF ["+diff+"]");
						//System.out.println("SOGLIATR ["+soglia+"]");
						if(diff<soglia) {
							String msgErrore = "Certificato prossima alla scadenza ("+sogliaWarningGiorni+" giorni): "+sdf.format(certificate.getCertificate().getNotAfter());
							warning = true;
							esito.addWarning(identita, msgErrore, certificateDetails);
						}
					}
				}
				
			}
		}
				
		if(error) {
			esito.setStatoCheck(StatoCheck.ERROR);
		}
		else if(warning) {
			esito.setStatoCheck(StatoCheck.WARN);
		}
		else {
			esito.setStatoCheck(StatoCheck.OK);
		}
		return esito;

	}
	
	
	public static CertificateCheck checkSingleCertificate(String storeDetails, byte[] bytesCert, 
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws Exception{
		
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_CERTIFICATE);
		
		
		CertificateCheck esito = new CertificateCheck();
		boolean error = false;
		boolean warning = false;
		
		Certificate certificate = ArchiveLoader.load(bytesCert);		
		
		String identita = "Certificato";
		
		String certificateDetails = storeDetails +
				newLine +
				CertificateUtils.toString(certificate, separator, newLine);
		boolean check = true;
		try {
			certificate.getCertificate().checkValid();
		}catch(Throwable t) {
			check = false;
			String msgErrore = "Certificato non valido: "+t.getMessage();
			error = true;
			esito.addError(identita, msgErrore, certificateDetails);
		}
							
		if(check && certificate.getCertificateChain()!=null && !certificate.getCertificateChain().isEmpty()) {
			for (CertificateInfo caChain : certificate.getCertificateChain()) {
				String hex_caChain = caChain.getSerialNumberHex();
				String credenziale_caChain = "(CN:"+caChain.getSubject().getCN()+" serialNumber:"+hex_caChain+")";
				try {
					caChain.checkValid();
				}catch(Throwable t) {
					check = false;
					String msgErrore = ("Un certificato della catena "+credenziale_caChain+" non risulta valido: "+t.getMessage());
					error = true;
					esito.addError(identita, msgErrore, certificateDetails);
				}
			}
		}
		
		if(check && sogliaWarningGiorni>0) {
			if(certificate.getCertificate().getNotAfter()!=null) {
				long expire = certificate.getCertificate().getNotAfter().getTime();
				long now = DateManager.getTimeMillis();
				long diff = expire - now;
				long soglia = (1000l*60l*60l*24l) * ((long)sogliaWarningGiorni);
				//System.out.println("=======================");
				//System.out.println("SUBJECT ["+certificate.getCertificate().getCertificate().getSubjectDN().getName()+"]");
				//System.out.println("NOT AFTER ["+certificate.getCertificate().getNotAfter()+"]");
				//System.out.println("SOGLIA ["+sogliaWarningGiorni+"]");
				//System.out.println("DEXP ["+expire+"]");
				//System.out.println("DNOW ["+now+"]");
				//System.out.println("DIFF ["+diff+"]");
				//System.out.println("SOGLIATR ["+soglia+"]");
				if(diff<soglia) {
					String msgErrore = "Certificato prossima alla scadenza ("+sogliaWarningGiorni+" giorni): "+sdf.format(certificate.getCertificate().getNotAfter());
					warning = true;
					esito.addWarning(identita, msgErrore, certificateDetails);
				}
			}
		}
			
		if(error) {
			esito.setStatoCheck(StatoCheck.ERROR);
		}
		else if(warning) {
			esito.setStatoCheck(StatoCheck.WARN);
		}
		else {
			esito.setStatoCheck(StatoCheck.OK);
		}
		return esito;

	}
}
