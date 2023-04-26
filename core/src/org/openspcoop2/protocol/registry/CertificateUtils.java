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
import java.security.PrivateKey;
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
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.CRLCertstore;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.JWKSet;
import org.openspcoop2.utils.certificate.KeyUtils;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.certificate.hsm.HSMManager;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.certificate.ocsp.IOCSPResourceReader;
import org.openspcoop2.utils.certificate.ocsp.OCSPResourceReader;
import org.openspcoop2.utils.certificate.ocsp.OCSPValidatorImpl;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Charset;
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
	
	private CertificateUtils() {}

	private static final String FORMAT_DATE_CERTIFICATE = "dd/MM/yyyy HH:mm";
	
	public static String toString(Certificate certificate, String separator, String newLine) throws UtilsException {
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
		
		/** String serialNumber = certificate.getCertificate().getSerialNumber() + ""; */
		String serialNumberHex = certificate.getCertificate().getSerialNumberHex() + "";
		/** sb.append(newLine).append(CostantiLabel.CERTIFICATE_SERIAL_NUMBER).append(separator).append(serialNumber);
		//sb.append(newLine).append(CostantiLabel.CERTIFICATE_SERIAL_NUMBER_HEX).append(separator).append(serialNumberHex); */
		sb.append(newLine).append(CostantiLabel.CERTIFICATE_SERIAL_NUMBER).append(separator).append(serialNumberHex);
		
		/** String certificatoType = certificate.getCertificate().getType();
		//String certificatoVersion = certificate.getCertificate().getVersion() + ""; */
		
		return sb.toString();
	}
	
	public static String toStringKeyStore(KeystoreParams params,
			String separator, String newLine) {
		return toStringEngine(true, params.getKeyAlias(), null, null,
				params.getPath(), params.getType(), 
				separator, newLine);
	}
	public static String toStringKeyStore(String storePath, String storeType,
			String keyAlias,
			String separator, String newLine) {
		return toStringEngine(true, keyAlias, null, null,
				storePath, storeType, 
				separator, newLine);
	}
	public static String toStringTrustStore(KeystoreParams params,
			String separator, String newLine) {
		return toStringEngine(false, params.getKeyAlias(), params.getCrls(), params.getOcspPolicy(),
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
		return toStringEngine(false, certAlias, trustCRL, ocspPolicy,
				storePath, storeType, 
				separator, newLine);
	}
	private static String toStringEngine(boolean keystore, String keyAlias, String trustCRL, String ocspPolicy,
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
			sb.append(CostantiLabel.CRLS);
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
	
	private static final String NOT_EXISTS_STRING = " not exists";
	private static final String CANNOT_READ_STRING = " cannot read";
	private static final String READING_PREFIX_STRING = "Reading ";
	private static final String SERIAL_NUMBER_STRING = " serialNumber:";
	private static String getSuffixCertificateNotValid(Exception t) {
		return " non risulta valido: "+t.getMessage();
	}
	private static String getSuffixFailed(Exception t) {
		return " failed: "+t.getMessage();
	}
	
	public static CertificateCheck checkCertificateClient(List<byte[]>certs, List<Boolean> strictValidation, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws UtilsException{
		
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_CERTIFICATE);
		
		if(certs==null || certs.isEmpty()) {
			throw new UtilsException("Nessun certificato individuato");
		}
		if(strictValidation==null || strictValidation.isEmpty()) {
			throw new UtilsException("Nessuna informazione sul tipo di validazione fornita");
		}
		if(strictValidation.size()!=certs.size()) {
			throw new UtilsException("Rilevata inconsistenza tra le informazioni fornite sul tipo di validazione e i certificati");
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
					credenziale = credenziale+ " (CN:"+certificate.getCertificate().getSubject().getCN()+SERIAL_NUMBER_STRING+hex+")";
				}
				boolean check = true;
				try {
					certificate.getCertificate().checkValid();
				}catch(Exception t) {
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
						String hexCaChain = caChain.getSerialNumberHex();
						String credenzialeCaChain = "(CN:"+caChain.getSubject().getCN()+SERIAL_NUMBER_STRING+hexCaChain+")";
						try {
							caChain.checkValid();
						}catch(Exception t) {
							check = false;
							String msgErrore = (credenziale+"; un certificato della catena "+credenzialeCaChain+getSuffixCertificateNotValid(t));
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
				
				if(check && sogliaWarningGiorni>0 &&
					certificate.getCertificate().getNotAfter()!=null) {
					long expire = certificate.getCertificate().getNotAfter().getTime();
					long now = DateManager.getTimeMillis();
					long diff = expire - now;
					long soglia = (1000l*60l*60l*24l) * ((long)sogliaWarningGiorni);
					/**System.out.println("=======================");
					//System.out.println("SUBJECT ["+certificate.getCertificate().getCertificate().getSubjectDN().getName()+"]");
					//System.out.println("NOT AFTER ["+certificate.getCertificate().getNotAfter()+"]");
					//System.out.println("SOGLIA ["+sogliaWarningGiorni+"]");
					//System.out.println("DEXP ["+expire+"]");
					//System.out.println("DNOW ["+now+"]");
					//System.out.println("DIFF ["+diff+"]");
					//System.out.println("SOGLIATR ["+soglia+"]");*/
					if(diff<soglia) {
						String msgErrore = credenziale+" prossima alla scadenza ("+sogliaWarningGiorni+" giorni): "+sdf.format(certificate.getCertificate().getNotAfter());
						warning = true;
						esito.addWarning(identitaCertificato, msgErrore, certificateDetails);
					}
				}
				
			}catch(Exception t) {
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
			Logger log) throws UtilsException{
		return checkTrustStore(trustStore, classpathSupported, type, password, trustStoreCrls, ocspPolicy, null,
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
	}
	public static CertificateCheck checkTrustStore(String trustStore, boolean classpathSupported, String type, String password, String trustStoreCrls,String ocspPolicy,String certAlias,
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws UtilsException{
		boolean hsm = HSMUtils.isKeystoreHSM(type);
		byte [] keystoreBytes = null;
		if(!hsm) {
			File f = new File(trustStore);
			boolean exists = f.exists();
			boolean inClasspath = false;
			if(!exists && classpathSupported) {
				String uri = trustStore;
				if(!trustStore.startsWith("/")) {
					uri = "/" + uri;	
				}
				try {
					try( InputStream is = CertificateUtils.class.getResourceAsStream(uri); ){
						exists = (is!=null);
						if(exists) {
							inClasspath = true;
							keystoreBytes = Utilities.getAsByteArray(is);
						}
					}
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
			if(!exists || (!inClasspath && !f.canRead())) {
				
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
				errorDetails = errorDetails + (!exists ? NOT_EXISTS_STRING : CANNOT_READ_STRING); 
				
				CertificateCheck esito = new CertificateCheck();
				esito.setStatoCheck(StatoCheck.ERROR);
				esito.addError(trustStore, errorDetails, storeDetails);
				return esito;
			}
			if(!inClasspath) {
				try {
					keystoreBytes = FileSystemUtilities.readBytesFromFile(f);
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
		}
		return checkStore(false, certAlias, null, trustStoreCrls, ocspPolicy,
				trustStore, keystoreBytes, type, password, 
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
	}
	public static CertificateCheck checkTrustStore(byte[] trustStore, String type, String password, String trustStoreCrls, String ocspPolicy,
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws UtilsException{
		return checkStore(false, null, null, trustStoreCrls, ocspPolicy,
				CostantiLabel.STORE_CARICATO_BASEDATI, trustStore, type, password, 
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
	}
	public static CertificateCheck checkKeyStore(String keyStore, boolean classpathSupported, String type, String password, String aliasKey, String passwordKey,
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws UtilsException{
		boolean hsm = HSMUtils.isKeystoreHSM(type);
		byte [] keystoreBytes = null;
		if(!hsm) {
			File f = new File(keyStore);
			boolean exists = f.exists();
			boolean inClasspath = false;
			if(!exists && classpathSupported) {
				String uri = keyStore;
				if(!keyStore.startsWith("/")) {
					uri = "/" + uri;	
				}
				try {
					try( InputStream is = CertificateUtils.class.getResourceAsStream(uri); ){
						exists = (is!=null);
						if(exists) {
							inClasspath = true;
							keystoreBytes = Utilities.getAsByteArray(is);
						}
					}
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
			if(!exists || (!inClasspath && !f.canRead())) {
				
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
				errorDetails = errorDetails + (!exists ? NOT_EXISTS_STRING : CANNOT_READ_STRING); 
				
				CertificateCheck esito = new CertificateCheck();
				esito.setStatoCheck(StatoCheck.ERROR);
				esito.addError(keyStore, errorDetails, storeDetails);
				return esito;
			}
			if(!inClasspath) {
				try {
					keystoreBytes = FileSystemUtilities.readBytesFromFile(f);
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
		}
		return checkStore(true, aliasKey, passwordKey, null, null,
				keyStore, keystoreBytes, type, password, 
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
	}
	public static CertificateCheck checkKeyStore(byte[] keyStore, String type, String password, String aliasKey, String passwordKey,
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws UtilsException{
		return checkStore(true, aliasKey, passwordKey, null, null,
				CostantiLabel.STORE_CARICATO_BASEDATI, keyStore, type, password, 
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
	}
	private static CertificateCheck checkStore(boolean keystore, String aliasKey, String passwordKey, String trustStoreCrls, String ocspPolicy,
			String storePath, byte[] storeBytes, String type, String password,
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws UtilsException{
		
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
		}catch(Exception t) {
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
					if(!f.exists() || !f.canRead()) {
						
						/** String identita = CostantiLabel.CRL+" '"+path+"'"; */
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
						errorDetails = errorDetails + (!f.exists() ? NOT_EXISTS_STRING : CANNOT_READ_STRING); 
						
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
				try {
					Enumeration<String> aliases = store.aliases();
					while (aliases.hasMoreElements()) {
						String aliasCheck = aliases.nextElement();
						if(store.getKeystore().isKeyEntry(aliasCheck)) {
							alias.add(aliasCheck);
						}
					}
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
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
					String aliasCheck = aliases.nextElement();
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
		
		List<Certificate> listCertificate = new ArrayList<>();
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
				
				if(keystore) {
					PrivateKey privateKey = null;
					String errorKey = null;
					try {
						privateKey = store.getPrivateKey(aliasVerify, passwordKey);
					}catch(Exception e) {
						errorKey = e.getMessage();
					}
					if(privateKey==null) {
						esito = new CertificateCheck();
						esito.setStatoCheck(StatoCheck.ERROR);
						esito.addError(storePath, "Nel "+
								CostantiLabel.KEYSTORE+
								" la chiave "+
								" con alias '"+aliasVerify+"' non è accessibile"+
								(errorKey!=null ? ": "+errorKey : ""), storeDetails);
						return esito;
					}
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
				}catch(Exception t) {
					String msgErrore = "Certificato non valido: "+t.getMessage();
					error = true;
					esito.addError(identita, msgErrore, certificateDetails);
					/** check = false; */
					continue;
				}
									
				if(check && certificate.getCertificateChain()!=null && !certificate.getCertificateChain().isEmpty()) {
					for (CertificateInfo caChain : certificate.getCertificateChain()) {
						String hexCaChain = caChain.getSerialNumberHex();
						String credenzialeCaChain = "(CN:"+caChain.getSubject().getCN()+SERIAL_NUMBER_STRING+hexCaChain+")";
						try {
							if(keystore || crls==null) {
								caChain.checkValid();
							}
							else {
								caChain.checkValid(crls.getCertStore(), store);
							}
							// La validazione della catena viene effettuata direttamente dal ocsp validator se previsto dalla policy
/**							if(ocspValidator!=null) {
//								ocspValidator.valid(certificate.getCertificate().getCertificate());
//							}*/
						}catch(Exception t) {
							check = false;
							String msgErrore = ("Un certificato della catena "+credenzialeCaChain+getSuffixCertificateNotValid(t));
							error = true;
							esito.addError(identita, msgErrore, certificateDetails);
						}
					}
				}
				
				if(check && sogliaWarningGiorni>0 &&
					certificate.getCertificate().getNotAfter()!=null) {
					long expire = certificate.getCertificate().getNotAfter().getTime();
					long now = DateManager.getTimeMillis();
					long diff = expire - now;
					long soglia = (1000l*60l*60l*24l) * (sogliaWarningGiorni);
					/**System.out.println("=======================");
					//System.out.println("SUBJECT ["+certificate.getCertificate().getCertificate().getSubjectDN().getName()+"]");
					//System.out.println("NOT AFTER ["+certificate.getCertificate().getNotAfter()+"]");
					//System.out.println("SOGLIA ["+sogliaWarningGiorni+"]");
					//System.out.println("DEXP ["+expire+"]");
					//System.out.println("DNOW ["+now+"]");
					//System.out.println("DIFF ["+diff+"]");
					//System.out.println("SOGLIATR ["+soglia+"]");*/
					if(diff<soglia) {
						String msgErrore = "Certificato prossima alla scadenza ("+sogliaWarningGiorni+" giorni): "+sdf.format(certificate.getCertificate().getNotAfter());
						warning = true;
						esito.addWarning(identita, msgErrore, certificateDetails);
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
			String separator, String newLine) throws UtilsException{
		
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
		}catch(Exception t) {
			check = false;
			String msgErrore = "Certificato non valido: "+t.getMessage();
			error = true;
			esito.addError(identita, msgErrore, certificateDetails);
		}
							
		if(check && certificate.getCertificateChain()!=null && !certificate.getCertificateChain().isEmpty()) {
			for (CertificateInfo caChain : certificate.getCertificateChain()) {
				String hexCaChain = caChain.getSerialNumberHex();
				String credenzialeCaChain = "(CN:"+caChain.getSubject().getCN()+SERIAL_NUMBER_STRING+hexCaChain+")";
				try {
					caChain.checkValid();
				}catch(Exception t) {
					check = false;
					String msgErrore = ("Un certificato della catena "+credenzialeCaChain+getSuffixCertificateNotValid(t));
					error = true;
					esito.addError(identita, msgErrore, certificateDetails);
				}
			}
		}
		
		if(check && sogliaWarningGiorni>0 &&
			certificate.getCertificate().getNotAfter()!=null) {
			long expire = certificate.getCertificate().getNotAfter().getTime();
			long now = DateManager.getTimeMillis();
			long diff = expire - now;
			long soglia = (1000l*60l*60l*24l) * (sogliaWarningGiorni);
			/**System.out.println("=======================");
			//System.out.println("SUBJECT ["+certificate.getCertificate().getCertificate().getSubjectDN().getName()+"]");
			//System.out.println("NOT AFTER ["+certificate.getCertificate().getNotAfter()+"]");
			//System.out.println("SOGLIA ["+sogliaWarningGiorni+"]");
			//System.out.println("DEXP ["+expire+"]");
			//System.out.println("DNOW ["+now+"]");
			//System.out.println("DIFF ["+diff+"]");
			//System.out.println("SOGLIATR ["+soglia+"]");*/
			if(diff<soglia) {
				String msgErrore = "Certificato prossima alla scadenza ("+sogliaWarningGiorni+" giorni): "+sdf.format(certificate.getCertificate().getNotAfter());
				warning = true;
				esito.addWarning(identita, msgErrore, certificateDetails);
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
	
	
	
	public static CertificateCheck checkKeyPair(boolean classpathSupported, String privateKey, String publicKey, String passwordKey, String algorithm,
			boolean addCertificateDetails, String separator) throws UtilsException{
		return checkKeyPairEngine(true,
				classpathSupported, privateKey, publicKey, passwordKey, algorithm,
				addCertificateDetails, separator);
	}
	public static String toStringKeyPair(KeystoreParams params,
			String separator) {
		return toStringKeyPair(params.getPath(), params.getKeyPairPublicKeyPath(),
				separator);
	}
	private static String toStringKeyPair(String privateKey, String publicKey,
			String separator) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(CostantiLabel.KEY_PAIR);
		sb.append(separator);
		sb.append("private:");
		sb.append(privateKey);
		sb.append(" ");
		sb.append("public:");
		sb.append(publicKey);
		
		return sb.toString();
	}
	
	
	public static CertificateCheck checkPublicKey(boolean classpathSupported, String publicKey, String algorithm,
			boolean addCertificateDetails, String separator) throws UtilsException{
		return checkKeyPairEngine(false,
				classpathSupported, null, publicKey, null, algorithm,
				addCertificateDetails, separator);
	}	
	public static String toStringPublicKey(KeystoreParams params,
			String separator) {
		return toStringPublicKey(params.getPath(), 
				separator);
	}
	private static String toStringPublicKey(String publicKey,
			String separator) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(CostantiLabel.PUBLIC_KEY);
		sb.append(separator);
		sb.append(publicKey);
		
		return sb.toString();
	}
	
	
	
	
	private static CertificateCheck checkKeyPairEngine(boolean isKeyPair,
			boolean classpathSupported, String privateKey, String publicKey, String passwordKey, String algorithm,
			boolean addCertificateDetails, String separator) throws UtilsException{
		
		String storeDetails = null;
		if(addCertificateDetails) {
			if(isKeyPair) {
				storeDetails = toStringKeyPair(privateKey, publicKey, separator);
			}
			else {
				storeDetails = toStringPublicKey(publicKey, separator);
			}
		}
		
		
		// PRIVATE KEY
		
		byte[] privateKeyBytes = null;
		if(isKeyPair) {
			File f = new File(privateKey);
			boolean exists = f.exists();
			boolean inClasspath = false;
			if(!exists && classpathSupported) {
				String uri = privateKey;
				if(!privateKey.startsWith("/")) {
					uri = "/" + uri;	
				}
				try {
					try( InputStream is = CertificateUtils.class.getResourceAsStream(uri); ){
						exists = (is!=null);
						if(exists) {
							inClasspath = true;
							privateKeyBytes = Utilities.getAsByteArray(is);
						}
					}
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
			if(!exists || (!inClasspath && !f.canRead())) {
				
				String errorDetails = CostantiLabel.PRIVATE_KEY;
				if(!addCertificateDetails) {
					errorDetails = errorDetails + " '"+
							//f.getAbsolutePath()+  // nel caso di path relativo viene visualizzato un PATH basato sul bin di wildfly ed e' forviante
							privateKey+
							"'";
				}
				errorDetails = errorDetails + (!exists ? NOT_EXISTS_STRING : CANNOT_READ_STRING); 
				
				CertificateCheck esito = new CertificateCheck();
				esito.setStatoCheck(StatoCheck.ERROR);
				esito.addError(privateKey, errorDetails, storeDetails);
				return esito;
			}
			if(!inClasspath) {
				try {
					privateKeyBytes = FileSystemUtilities.readBytesFromFile(f);
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
		}
		
		// PUBLIC KEY
		
		byte[] publicKeyBytes = null;
		File f = new File(publicKey);
		boolean exists = f.exists();
		boolean inClasspath = false;
		if(!exists && classpathSupported) {
			String uri = publicKey;
			if(!publicKey.startsWith("/")) {
				uri = "/" + uri;	
			}
			try {
				try( InputStream is = CertificateUtils.class.getResourceAsStream(uri); ){
					exists = (is!=null);
					if(exists) {
						inClasspath = true;
						publicKeyBytes = Utilities.getAsByteArray(is);
					}
				}
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
				
		if(!exists || (!inClasspath && !f.canRead())) {
			
			String errorDetails = CostantiLabel.PUBLIC_KEY;
			if(!addCertificateDetails) {
				errorDetails = errorDetails + " '"+
						//f.getAbsolutePath()+  // nel caso di path relativo viene visualizzato un PATH basato sul bin di wildfly ed e' forviante
						publicKey+
						"'";
			}
			errorDetails = errorDetails + (!exists ? NOT_EXISTS_STRING : CANNOT_READ_STRING); 
			
			CertificateCheck esito = new CertificateCheck();
			esito.setStatoCheck(StatoCheck.ERROR);
			esito.addError(publicKey, errorDetails, storeDetails);
			return esito;
		}
		if(!inClasspath) {
			try {
				publicKeyBytes = FileSystemUtilities.readBytesFromFile(f);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		
		return checkKeyPairEngine(isKeyPair,
				storeDetails, privateKeyBytes, publicKeyBytes, passwordKey, algorithm,
				addCertificateDetails);
	}
	private static CertificateCheck checkKeyPairEngine(boolean isKeyPair,
			String storeDetails, byte[] privateKey, byte[] publicKey, String passwordKey, String algorithm,
			boolean addCertificateDetails) throws UtilsException{
				
		KeyUtils keyUtils = null;
		try {
			keyUtils = KeyUtils.getInstance(algorithm);
		}catch(Exception t) {
			String errorDetails = "KeyAlgorithm";
			if(!addCertificateDetails) {
				errorDetails = errorDetails + " '"+
						//f.getAbsolutePath()+  // nel caso di path relativo viene visualizzato un PATH basato sul bin di wildfly ed e' forviante
						algorithm+
						"'";
			}
			errorDetails = errorDetails + " unknown: "+t.getMessage(); 
			
			CertificateCheck esito = new CertificateCheck();
			esito.setStatoCheck(StatoCheck.ERROR);
			esito.addError(algorithm, errorDetails, storeDetails);
			return esito;
		}
		
		if(isKeyPair) {
			try {
				if(passwordKey!=null) {
					keyUtils.getPrivateKey(privateKey, passwordKey);
				}
				else {
					keyUtils.getPrivateKey(privateKey);
				}
			}catch(Exception t) {
				String errorDetails = READING_PREFIX_STRING+ CostantiLabel.PRIVATE_KEY+getSuffixFailed(t);
				CertificateCheck esito = new CertificateCheck();
				esito.setStatoCheck(StatoCheck.ERROR);
				esito.addError(CostantiLabel.PRIVATE_KEY, errorDetails, storeDetails);
				return esito;
			}
		}
		
		try {
			keyUtils.getPublicKey(publicKey);
		}catch(Exception t) {
			String errorDetails = READING_PREFIX_STRING+ CostantiLabel.PUBLIC_KEY+getSuffixFailed(t);
			CertificateCheck esito = new CertificateCheck();
			esito.setStatoCheck(StatoCheck.ERROR);
			esito.addError(CostantiLabel.PUBLIC_KEY, errorDetails, storeDetails);
			return esito;
		}
		
		CertificateCheck esito = new CertificateCheck();
		esito.setStatoCheck(StatoCheck.OK);
		return esito;
	}
	
	
	
	
	
	public static CertificateCheck checkKeystoreJWKs(boolean classpathSupported, String jwksPath, String keyAlias, 
			boolean addCertificateDetails, String separator, String newLine) throws UtilsException{
		return checkJWKsEngine(true,
				classpathSupported, jwksPath, keyAlias,
				addCertificateDetails, separator, newLine);
	}
	public static CertificateCheck checkTruststoreJWKs(boolean classpathSupported, String jwksPath, String keyAlias, 
			boolean addCertificateDetails, String separator, String newLine) throws UtilsException{
		return checkJWKsEngine(false,
				classpathSupported, jwksPath, keyAlias,
				addCertificateDetails, separator, newLine);
	}
	private static CertificateCheck checkJWKsEngine(boolean keystore,
			boolean classpathSupported, String jwksPath, String keyAlias, 
			boolean addCertificateDetails, String separator, String newLine) throws UtilsException{
		
		String storeDetails = null;
		if(addCertificateDetails) {
			storeDetails = toStringJWKs(keystore, jwksPath, keyAlias,
					separator, newLine);
		}
				
		String jwks = null;
		File f = new File(jwksPath);
		boolean exists = f.exists();
		boolean inClasspath = false;
		if(!exists && classpathSupported) {
			String uri = jwksPath;
			if(!jwksPath.startsWith("/")) {
				uri = "/" + uri;	
			}
			try {
				try( InputStream is = CertificateUtils.class.getResourceAsStream(uri); ){
					exists = (is!=null);
					if(exists) {
						inClasspath = true;
						jwks = Utilities.getAsString(is, Charset.UTF_8.getValue());
					}
				}
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
				
		if(!exists || (!inClasspath && !f.canRead())) {
			
			String errorDetails = keystore ? CostantiLabel.KEYSTORE : CostantiLabel.TRUSTSTORE;
			if(!addCertificateDetails) {
				errorDetails = errorDetails + " '"+
						//f.getAbsolutePath()+  // nel caso di path relativo viene visualizzato un PATH basato sul bin di wildfly ed e' forviante
						jwksPath+
						"'";
			}
			errorDetails = errorDetails + (!exists ? NOT_EXISTS_STRING : CANNOT_READ_STRING); 
			
			CertificateCheck esito = new CertificateCheck();
			esito.setStatoCheck(StatoCheck.ERROR);
			esito.addError(jwksPath, errorDetails, storeDetails);
			return esito;
		}
		if(!inClasspath) {
			try {
				jwks = FileSystemUtilities.readFile(f);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		
		return checkJWKsEngine(keystore, 
				storeDetails, jwks, keyAlias);
	}
	private static CertificateCheck checkJWKsEngine(boolean keystore, 
			String storeDetails,
			String jwks, String keyAlias) throws UtilsException{
				
		com.nimbusds.jose.jwk.JWKSet set = null;
		try {
			JWKSet jwkset = new JWKSet(jwks);
			set = jwkset.getJWKSet();
		}catch(Exception t) {
			String errorDetails = READING_PREFIX_STRING+ (keystore ? CostantiLabel.KEYSTORE : CostantiLabel.TRUSTSTORE) +getSuffixFailed(t);
			CertificateCheck esito = new CertificateCheck();
			esito.setStatoCheck(StatoCheck.ERROR);
			esito.addError(keystore ? CostantiLabel.KEYSTORE : CostantiLabel.TRUSTSTORE, errorDetails, storeDetails);
			return esito;
		}
		
		CertificateCheck checkAlias = checkJWKsEngineKeyAlias(keyAlias, set, keystore, storeDetails);
		if(checkAlias!=null) {
			return checkAlias;
		}
		
		CertificateCheck esito = new CertificateCheck();
		esito.setStatoCheck(StatoCheck.OK);
		return esito;
	}
	private static CertificateCheck checkJWKsEngineKeyAlias(String keyAlias, com.nimbusds.jose.jwk.JWKSet set, boolean keystore, String storeDetails) {
		if(keyAlias!=null && StringUtils.isNotEmpty(keyAlias)) {
			try {
				com.nimbusds.jose.jwk.JWK jwk = set.getKeyByKeyId(keyAlias);
				if(jwk==null) {
					throw new UtilsException("kid not exists");
				}
			}catch(Exception t) {
				String errorDetails = (keystore ? CostantiLabel.KEY_ALIAS : CostantiLabel.CERTIFICATE_ALIAS) +" unknown: "+t.getMessage();
				CertificateCheck esito = new CertificateCheck();
				esito.setStatoCheck(StatoCheck.ERROR);
				esito.addError(keystore ? CostantiLabel.KEYSTORE : CostantiLabel.TRUSTSTORE, errorDetails, storeDetails);
				return esito;
			}
		}
		return null;
	}
	public static String toStringKeystoreJWKs(KeystoreParams params,
			String separator, String newLine) {
		return toStringJWKs(true, params.getPath(), params.getKeyAlias(),
				separator, newLine);
	}
	public static String toStringTruststoreJWKs(KeystoreParams params,
			String separator, String newLine) {
		return toStringJWKs(false, params.getPath(), params.getKeyAlias(),
				separator, newLine);
	}
	private static String toStringJWKs(boolean keystore, String jwksPath, String keyAlias,
			String separator, String newLine) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(keystore? CostantiLabel.KEYSTORE : CostantiLabel.TRUSTSTORE);
		sb.append(separator);
		sb.append(CostantiLabel.JWKS);
		sb.append(" ");
		sb.append(jwksPath);
		
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
}
