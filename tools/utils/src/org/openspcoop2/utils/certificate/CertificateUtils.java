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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

import org.apache.xml.security.utils.RFC2253Parser;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.slf4j.Logger;
import org.springframework.web.util.UriUtils;

/**
 * CertificateUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CertificateUtils {
	
	private CertificateUtils() {}

	public static void printCertificate(StringBuilder bf,List<java.security.cert.X509Certificate> certs){
		printCertificate(bf, certs, false);
	}
	public static void printCertificate(StringBuilder bf,List<java.security.cert.X509Certificate> certs, boolean addPrefix){
		if(!certs.isEmpty()) {
			java.security.cert.X509Certificate[] certsArray = certs.toArray(new java.security.cert.X509Certificate[1]);
			printCertificate(bf, certsArray, addPrefix);
		}
		else {
			bf.append("X509Certificates: "+0+"\n");
		}
	}
	
	public static void printCertificate(StringBuilder bf,java.security.cert.X509Certificate[] certs){
		printCertificate(bf, certs, false);
	}
	public static void printCertificate(StringBuilder bf,java.security.cert.X509Certificate[] certs, boolean addPrefix){
		bf.append("X509Certificates: "+certs.length+"\n");
		for (int i = 0; i < certs.length; i++) {
			java.security.cert.X509Certificate cert = certs[i];
			printCertificate(bf, cert, i+"", addPrefix);
		}
	}
	
	public static void printCertificate(StringBuilder bf,java.security.cert.X509Certificate cert, String name){
		printCertificate(bf, cert, name, false);
	}
	public static void printCertificate(StringBuilder bf,java.security.cert.X509Certificate cert, String name, boolean addPrefix){
		String prefix = "";
		if(addPrefix) {
			prefix = "Cert["+name+"].";
		}
		bf.append("#### X509Certificate["+name+"]\n");
		bf.append("\t"+prefix+"toString()="+cert.toString()+"\n");
		bf.append("\t"+prefix+"getType()="+cert.getType()+"\n");
		bf.append("\t"+prefix+"getVersion()="+cert.getVersion()+"\n");
		
		if(cert.getIssuerDN()!=null){
			bf.append("\t"+prefix+"cert.getIssuerDN().toString()="+cert.getIssuerDN().toString()+"\n");
			bf.append("\t"+prefix+"cert.getIssuerDN().getName()="+cert.getIssuerDN().getName()+"\n");
		}
		else{
			bf.append("\t"+prefix+"cert.getIssuerDN() is null"+"\n");
		}
		
		if(cert.getIssuerX500Principal()!=null){
			bf.append("\t"+prefix+"getIssuerX500Principal().toString()="+cert.getIssuerX500Principal().toString()+"\n");
			bf.append("\t"+prefix+"getIssuerX500Principal().getName()="+cert.getIssuerX500Principal().getName()+"\n");
			bf.append("\t"+prefix+"getIssuerX500Principal().getName(X500Principal.CANONICAL)="+cert.getIssuerX500Principal().getName(X500Principal.CANONICAL)+"\n");
			bf.append("\t"+prefix+"getIssuerX500Principal().getName(X500Principal.RFC1779)="+cert.getIssuerX500Principal().getName(X500Principal.RFC1779)+"\n");
			bf.append("\t"+prefix+"getIssuerX500Principal().getName(X500Principal.RFC2253)="+cert.getIssuerX500Principal().getName(X500Principal.RFC2253)+"\n");
			/**	Map<String,String> oidMapCanonical = new HashMap<>();
				bf.append("\t"+prefix+"getIssuerX500Principal().getName(X500Principal.CANONICAL,oidMapCanonical)="+
						cert.getIssuerX500Principal().getName(X500Principal.CANONICAL,oidMapCanonical));
				if(oidMapCanonical!=null && oidMapCanonical.size()>0){
					Iterator<String> it = oidMapCanonical.keySet().iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						String value = oidMapCanonical.get(key);
						bf.append("\t"+prefix+"getIssuerX500Principal() ["+key+"]=["+value+"]"+"\n");
					}
				}*/
		}
		else{
			bf.append("\t"+prefix+"cert.getIssuerX500Principal() is null"+"\n");
		}
		
		if(cert.getSubjectDN()!=null){
			bf.append("\t"+prefix+"getSubjectDN().toString()="+cert.getSubjectDN().toString()+"\n");
			bf.append("\t"+prefix+"getSubjectDN().getName()="+cert.getSubjectDN().getName()+"\n");
		}
		else{
			bf.append("\t"+prefix+"cert.getSubjectDN() is null"+"\n");
		}
		
		bf.append("\t"+prefix+"getSerialNumber()="+cert.getSerialNumber()+"\n");
		bf.append("\t"+prefix+"getNotAfter()="+cert.getNotAfter()+"\n");
		bf.append("\t"+prefix+"getNotBefore()="+cert.getNotBefore()+"\n");
		
		if(cert.getSubjectX500Principal()!=null){
			bf.append("\t"+prefix+"getSubjectX500Principal().toString()="+cert.getSubjectX500Principal().toString()+"\n");
			bf.append("\t"+prefix+"getSubjectX500Principal().getName()="+cert.getSubjectX500Principal().getName()+"\n");
			bf.append("\t"+prefix+"getSubjectX500Principal().getName(X500Principal.CANONICAL)="+cert.getSubjectX500Principal().getName(X500Principal.CANONICAL)+"\n");
			bf.append("\t"+prefix+"getSubjectX500Principal().getName(X500Principal.RFC1779)="+cert.getSubjectX500Principal().getName(X500Principal.RFC1779)+"\n");
			bf.append("\t"+prefix+"getSubjectX500Principal().getName(X500Principal.RFC2253)="+cert.getSubjectX500Principal().getName(X500Principal.RFC2253)+"\n");
			/**	Map<String,String> oidMapCanonical = new HashMap<>();
				bf.append("\t"+prefix+"getSubjectX500Principal().getName(X500Principal.CANONICAL,oidMapCanonical)="+
						cert.getSubjectX500Principal().getName(X500Principal.CANONICAL,oidMapCanonical));
				if(oidMapCanonical!=null && oidMapCanonical.size()>0){
					Iterator<String> it = oidMapCanonical.keySet().iterator();
					while (it.hasNext()) {
						String key = (String) it.next();
						String value = oidMapCanonical.get(key);
						bf.append("\t"+prefix+"getSubjectX500Principal() ["+key+"]=["+value+"]"+"\n");
					}
				}*/
		}
		else{
			bf.append("\t"+prefix+"cert.getSubjectX500Principal() is null"+"\n");
		}
	}
	
	
	
	
	
	
	/* UTILITY SSL */
	
	private static final boolean TRIM_VALUE_BEFORE_SAVE_DB = true;
	
	private static void debug(Logger log, String msg) {
		if(log!=null) {
			log.debug(msg);
		}
	}
	private static String getAnalisiTypePrefixString(String principal, PrincipalType type) {
		return "("+principal+") Analisi "+type+" ";
	}
	
	public static boolean sslVerify(String principalPresenteNellaConfigurazione, String principalArrivatoConnessioneSSL, PrincipalType type, Logger log) throws UtilsException{

		if(log!=null) {
			debug(log, "SSL VERIFY CONF["+principalPresenteNellaConfigurazione+"] SSL["+principalArrivatoConnessioneSSL+"]");
		}
		/** System.out.println("SSL VERIFY CONF["+principalPresenteNellaConfigurazione+"] SSL["+principalArrivatoConnessioneSSL+"]"); */

		// Costruzione key=value
		Map<String, List<String>> hashPrincipalArrivatoConnessioneSSL = CertificateUtils.getPrincipalIntoMap(principalArrivatoConnessioneSSL, type);
		Map<String, List<String>> hashPrincipalPresenteNellaConfigurazione = CertificateUtils.getPrincipalIntoMap(principalPresenteNellaConfigurazione, type);

		if(!sslVerifyCheckSize(principalPresenteNellaConfigurazione, principalArrivatoConnessioneSSL, type, 
				hashPrincipalArrivatoConnessioneSSL, hashPrincipalPresenteNellaConfigurazione, log)) {
			return false;
		}

		for (Map.Entry<String,List<String>> entry : hashPrincipalArrivatoConnessioneSSL.entrySet()) {

			String key = entry.getKey();
			
			if(!hashPrincipalPresenteNellaConfigurazione.containsKey(key)){
				/** System.out.println("KEY ["+key+"] non presente"); */
				if(log!=null) {
					List<String> lKeys = new ArrayList<>();
					lKeys.addAll(hashPrincipalPresenteNellaConfigurazione.keySet());
					debug(log, "sslVerify key["+key+"] non trovata in "+type+" "+"Configurazione["+principalPresenteNellaConfigurazione+"]"+", key riscontrate: "+
							lKeys);
				}
				return false;
			}

			// Prendo valori
			List<String> connessioneSSLValueList = hashPrincipalArrivatoConnessioneSSL.get(key);
			List<String> configurazioneInternaValueList = hashPrincipalPresenteNellaConfigurazione.get(key);
			if(connessioneSSLValueList.size() != configurazioneInternaValueList.size()){
				/** System.out.println("LUNGHEZZA DIVERSA KEY ["+key+"]"); */
				if(log!=null) {
					debug(log, "sslVerify "+type+" key["+key+"] trovata in Configurazione["+principalPresenteNellaConfigurazione+"]("+configurazioneInternaValueList.size()+
						") SSL["+principalArrivatoConnessioneSSL+"]("+connessioneSSLValueList.size()+"): lunghezza differente");
				}
				return false;
			}
			
			// Ordino Valori
			Collections.sort(connessioneSSLValueList);
			Collections.sort(configurazioneInternaValueList);
			
			// confronto valori
			if(!sslVerifyCheckValues(key, type, connessioneSSLValueList, configurazioneInternaValueList, log)) {
				return false;
			}
			
		}

		/** System.out.println("SSL RETURN TRUE"); */
		return true;

	}
	private static boolean sslVerifyCheckSize(String principalPresenteNellaConfigurazione, String principalArrivatoConnessioneSSL, PrincipalType type, 
			Map<String, List<String>> hashPrincipalArrivatoConnessioneSSL, Map<String, List<String>> hashPrincipalPresenteNellaConfigurazione, Logger log) {
		if(hashPrincipalArrivatoConnessioneSSL.size() != hashPrincipalPresenteNellaConfigurazione.size()){
			/** System.out.println("LUNGHEZZA DIVERSA"); */
			if(log!=null) {
				debug(log, "sslVerify "+type+" "+"Configurazione["+principalPresenteNellaConfigurazione+"]"+"("+hashPrincipalPresenteNellaConfigurazione.size()+
					") SSL["+principalArrivatoConnessioneSSL+"]("+hashPrincipalArrivatoConnessioneSSL.size()+"): lunghezza differente");
			}
			return false;
		}
		return true;
	}
	private static boolean sslVerifyCheckValues(String key, PrincipalType type, List<String> connessioneSSLValueList, List<String> configurazioneInternaValueList, Logger log) {
		for (int i = 0; i < connessioneSSLValueList.size(); i++) {
			String connessioneSSLValue = connessioneSSLValueList.get(i);
			String configurazioneInternaValue = configurazioneInternaValueList.get(i);
			
			// Normalizzo caratteri escape
			while(connessioneSSLValue.contains("\\/")){
				connessioneSSLValue = connessioneSSLValue.replace("\\/", "/");
			}
			while(connessioneSSLValue.contains("\\,")){
				connessioneSSLValue = connessioneSSLValue.replace("\\,", ",");
			}
			while(configurazioneInternaValue.contains("\\/")){
				configurazioneInternaValue = configurazioneInternaValue.replace("\\/", "/");
			}
			while(configurazioneInternaValue.contains("\\,")){
				configurazioneInternaValue = configurazioneInternaValue.replace("\\,", ",");
			}
			
			if(!connessioneSSLValue.equals(configurazioneInternaValue)){
				if(log!=null) {
					debug(log, "sslVerify key["+key+"] "+type+" Configurazione["+configurazioneInternaValue+"] SSL["+connessioneSSLValue+"] not match");
				}
				/** System.out.println("VALUE SSL["+connessioneSSLValue+"]=CONF["+configurazioneInternaValue+"] non match"); */
				return false;
			}
		}
		return true;
	}


	public static String formatPrincipal(String principal, PrincipalType type) throws UtilsException{

		/** System.out.println("PRIMA ["+principal+"]"); */
		
		// Autenticazione SSL deve essere LIKE
		Map<String, List<String>> hashPrincipal = CertificateUtils.getPrincipalIntoMap(principal, type);
		StringBuilder bf = new StringBuilder();
		bf.append("/");
		for (Map.Entry<String,List<String>> entry : hashPrincipal.entrySet()) {
			
			String key = entry.getKey();
			
			List<String> listValues = hashPrincipal.get(key);
			for (String value : listValues) {
				bf.append(CertificateUtils.formatKeyPrincipal(key));
				bf.append("=");
				bf.append(CertificateUtils.formatValuePrincipal(value));
				bf.append("/");
			}
			
		}
		/** System.out.println("DOPO ["+bf.toString()+"]"); */
		return bf.toString();
	}
	
	public static Map<String, String> formatPrincipalToMap(String principal, PrincipalType type) throws UtilsException{
		Map<String, String> returnMap = new HashMap<>();
		Map<String, List<String>> hashPrincipal = CertificateUtils.getPrincipalIntoMap(principal, type);
		for (Map.Entry<String,List<String>> entry : hashPrincipal.entrySet()) {
			
			String key = entry.getKey();
			
			List<String> listValues = hashPrincipal.get(key);
			for (String value : listValues) {
				String keyFormat = CertificateUtils.formatKeyPrincipal(key);
				String valueFormat = CertificateUtils.formatValuePrincipal(value);
				returnMap.put(keyFormat, valueFormat);
			}
			
		}
		return returnMap;
	}

	public static void validaPrincipal(String principalParam, PrincipalType type) throws UtilsException{
		
		/** System.out.println("PRIMA VALIDAZIONE ["+principalParam+"]"); */
		
		String principal = principalParam;
		UtilsException normalizedException = null;
		try{
			String tmp = normalizePrincipal(principalParam);
			principal = tmp;
		}catch(UtilsException e){
			/** System.out.println("ERRORE: "+e.getMessage());
			// non voglio rilanciare l'eccezione, verra' segnalata l'eccezione puntuale.
			// Se cosi' non fosse solo in fondo viene sollevata l'eccezione. */
			normalizedException = e;
		}
		
		/** System.out.println("DOPOP VALIDAZIONE ["+principal+"]"); */
		
		boolean commaFound = contains(principal, ",");
		boolean slashFound = contains(principal, "/");
		if(commaFound && slashFound){
			throw new UtilsException("("+principal+") Non possono coesistere i separatore \",\" e \"/\", solo uno dei due tipi deve essere utilizzato come delimitatore (usare eventualmente come carattere di escape '\\')");
		}
		if(!commaFound && !slashFound && !principal.contains("=")){
			throw new UtilsException("("+principal+") "+type+" non valido, nemmeno una coppia nome=valore trovata");
		}
		String [] valoriPrincipal = CertificateUtils.getValoriPrincipal(principal, type);
		validaPrincipal(valoriPrincipal, principal, type);
		
		if(normalizedException!=null){
			throw normalizedException;
		}
	}
	private static void validaPrincipal(String [] valoriPrincipal, String principal, PrincipalType type) throws UtilsException {
		boolean campoObbligatorioCN = false;
		boolean campoObbligatorioOU = false;
		boolean campoObbligatorioO = false;
		boolean campoObbligatorioL = false;
		boolean campoObbligatorioST = false;
		boolean campoObbligatorioC = false;
		boolean campoObbligatorioE = false;
		for(int i=0; i<valoriPrincipal.length; i++){
			
			String [] keyValue = getKeyValuePairEngine(valoriPrincipal[i], principal, type);

			if(keyValue[0].trim().contains(" ")){
				throw new UtilsException(getAnalisiTypePrefixString(principal, type)+"fallita: il campo ["+valoriPrincipal[i]+"] contiene spazi nella chiave identificativa ["+keyValue[0].trim()+"]");
			}
			
			if(CertificateUtils.formatKeyPrincipal(keyValue[0]).equalsIgnoreCase("CN")){
				campoObbligatorioCN = true;
			}
			else if(CertificateUtils.formatKeyPrincipal(keyValue[0]).equalsIgnoreCase("OU")){
				campoObbligatorioOU = true;
			}
			else if(CertificateUtils.formatKeyPrincipal(keyValue[0]).equalsIgnoreCase("O")){
				campoObbligatorioO = true;
			}
			else if(CertificateUtils.formatKeyPrincipal(keyValue[0]).equalsIgnoreCase("L")){
				campoObbligatorioL = true;
			}
			else if(CertificateUtils.formatKeyPrincipal(keyValue[0]).equalsIgnoreCase("ST")){
				campoObbligatorioST = true;
			}
			else if(CertificateUtils.formatKeyPrincipal(keyValue[0]).equalsIgnoreCase("C")){
				campoObbligatorioC = true;
			}
			else if(CertificateUtils.formatKeyPrincipal(keyValue[0]).equalsIgnoreCase("E")){
				campoObbligatorioE = true;
			}
		}
		if(!campoObbligatorioCN && !campoObbligatorioOU && !campoObbligatorioO && !campoObbligatorioL && !campoObbligatorioST && !campoObbligatorioC && !campoObbligatorioE){
			throw new UtilsException("("+principal+") Almeno un attributo di certificato tra 'CN', 'OU', 'O', 'L', 'ST', 'C' e 'E' deve essere valorizzato.");
		}
	}

	
	public static String normalizePrincipal(String principalParam) throws UtilsException{
		
		/*
		 *  The principal extract from class represents an X.500 Principal. 
		 *  X500Principals are represented by distinguished names such as "CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US".
		 *  This class can be instantiated by using a string representation of the distinguished name, or by using the ASN.1 DER encoded byte representation 
		 *  of the distinguished name. 
		 *  The current specification for the string representation of a distinguished name is defined in RFC 2253: 
		 *  Lightweight Directory Access Protocol (v3): UTF-8 String Representation of Distinguished Names. 
		 *  This class, however, accepts string formats from both RFC 2253 and RFC 1779: A String Representation of Distinguished Names, 
		 *  and also recognizes attribute type keywords whose OIDs (Object Identifiers) are defined in RFC 3280:
		 *  Internet X.509 Public Key Infrastructure Certificate and CRL Profile. 
		 */
		
		try{
			// Normalizza da un eventuale formato human readable o da un formato RFC 1779 nel formato RFC 2253 gestito poi nel seguito del codice.
			// Alcuni esempi dove il carattere '/' viene usato internamente come valore del CN
			// Esempio di formato "human readable": /C=IT/ST=Italiy/OU=PROVA di Bari, di Como/CN=SPC/SOGGETTO
			// Esempio di formato "RFC 1779": CN=SPC/SOGGETTO, OU="PROVA di Bari, di Como", ST=Italiy, C=IT
			// Esempio di formato "RFC 2253": CN=SPC/SOGGETTO,OU=PROVA di Bari\, di Como,ST=Italiy,C=IT
			return RFC2253Parser.normalize(principalParam);
			/**String principal = RFC2253Parser.normalize(principalParam);
			System.out.println(" ORIGINALE ["+principalParam+"]  NORMALIZZATO ["+principal+"]");
			return principal;*/
		}catch(Exception e){
			throw new UtilsException("("+principalParam+") Normalizzazione RFC2253 non riuscita: "+e.getMessage(),e);
		}
	}

	public static String [] getValoriPrincipal(String principalParam, PrincipalType type) throws UtilsException{
		try{
			/** System.out.println("PRINCIPAL getValoriPrincipal["+principalParam+"]"); */
			String principal = normalizePrincipal(principalParam);
			/** System.out.println("PRINCIPAL dopo normalize getValoriPrincipal["+principal+"]"); */
			
			return getValoriPrincipalEngine(principal, type);
			
		}catch(Exception e){
			
			/** System.out.println("PRINCIPAL getValoriPrincipal["+principalParam+"] errore: "+e.getMessage()); */
			
			try{
			
				javax.naming.ldap.LdapName prova = new javax.naming.ldap.LdapName(principalParam);
				Enumeration<String> ens = prova.getAll();
				List<String> values = new ArrayList<>();
				while (ens.hasMoreElements()) {
					String name = ens.nextElement();
					values.add(name);
				}
				
				if(!values.isEmpty()){
					return values.toArray(new String[1]);
				}
				else{
					throw new UtilsException("Coppie nome/valore non trovate");
				}
				
			}catch(Exception e2Level){
				/**e2Level.printStackTrace(System.out); */
				throw new UtilsException("("+principalParam+") javax.naming.ldap.LdapName reader failed: "+e2Level.getMessage()+". \nFirst method error: "+e.getMessage(),e);
			}
				
		}
	}
		
	private static String [] getValoriPrincipalEngine(String principal, PrincipalType type) throws UtilsException{
			
		String [] valori;
		boolean commaFound = contains(principal, ",");
		boolean slashFound = contains(principal, "/");
		/**System.out.println("PRINCIPAL _getValoriPrincipal commaFound["+commaFound+"] slashFound["+slashFound+"]"); */
		if(commaFound){
			if(principal.startsWith(",")){
				principal = principal.substring(1);
			}
			if(principal.endsWith(",")){
				principal = principal.substring(0,principal.length()-1);
			}
			/**System.out.println("PRINCIPAL _getValoriPrincipal preSplit , ["+principal+"] ..");*/
			valori = Utilities.split(principal, ',');
		}else{
			valori = getValoriPrincipalEngine(principal, type, slashFound);
		}
		if(valori==null || valori.length<1){
			throw new UtilsException(getAnalisiTypePrefixString(principal, type)+"interno alla configurazione di OpenSPCoop non riuscita: null??");
		}
		
		// validazione
		for(int i=0; i<valori.length; i++){
			getKeyValuePairEngine(valori[i], principal, type);
		}
				
		return valori;
	}
	private static String [] getValoriPrincipalEngine(String principal, PrincipalType type, boolean slashFound) throws UtilsException {
		/**System.out.println("PRINCIPAL _getValoriPrincipal comma not found ["+principal+"] .."); */
		String [] valori = null;
		if(!slashFound){
			/** System.out.println("PRINCIPAL _getValoriPrincipal slash not found ["+principal+"] .."); */
			int indexOf = principal.indexOf("=");
			if(indexOf<=0){
				throw new UtilsException("("+principal+") Separatore validi per il "+type+" interno alla configurazione di OpenSPCoop non trovati:  \",\" o \"/\" e carattere \"=\" non presente");
			}
			if(principal.indexOf("=",indexOf+1)>=0){
				throw new UtilsException("("+principal+") Separatore validi per il "+type+" interno alla configurazione di OpenSPCoop non trovati:  \",\" o \"/\"");
			}
			valori =  new String[1];
			valori[0] = principal;
		}else{
			valori = getValoriSlashPrincipalEngine(principal);
		}
		return valori;
	}
	private static String [] getValoriSlashPrincipalEngine(String principal) throws UtilsException {
		if(principal.startsWith("/")){
			principal = principal.substring(1);
		}
		if(principal.endsWith("/")){
			principal = principal.substring(0,principal.length()-1);
		}
		/**System.out.println("PRINCIPAL _getValoriPrincipal preSplit / ["+principal+"] .."); */
		String [] tmpValori = Utilities.split(principal, '/');
		
		// Bug Fix OP-670 certificato formato come:
		// C=IT/ST= /O=Esempio di Agenzia/OU=Servizi Informatici/CN=Ministero dell'Interno/prova/23234234554/DEMO
		List<String> normalize = new ArrayList<>();
		StringBuilder bf = new StringBuilder();
		for (String tmp : tmpValori) {
			if(tmp.contains("=")) {
				if(bf.length()>0) {
					normalize.add(bf.toString());
					bf.delete(0, bf.length());
				}
				bf.append(tmp);
			}
			else {
				bf.append("/").append(tmp);
			}
		}
		if(bf.length()>0) {
			normalize.add(bf.toString());
			bf.delete(0, bf.length());
		}
		return normalize.toArray(new String[1]);
	}
	
	public static Map<String, List<String>> getPrincipalIntoMap(String principal, PrincipalType type) throws UtilsException{
		Map<String, List<String>> hashPrincipal = new HashMap<>();
		String [] valoriPrincipal = CertificateUtils.getValoriPrincipal(principal, type);
		for(int i=0; i<valoriPrincipal.length; i++){
			
			// override eccezione in caso '=' non rpesente
			if(!valoriPrincipal[i].contains("=")){
				String fallita = "fallita"+": ["+valoriPrincipal[i]+"] ";
				throw new UtilsException(getAnalisiTypePrefixString(principal, type)+fallita+"non separata dal carattere \"=\"");
			}
			String [] keyValue = getKeyValuePairEngine(valoriPrincipal[i], principal, type);
			
			/**System.out.println("CONF INTERNA ["+Utilities.formatKeyPrincipal(keyValue[0])+"] ["+Utilities.formatValuePrincipal(keyValue[1])+"]");*/
			String formatKey = CertificateUtils.formatKeyPrincipal(keyValue[0]);
			String formatValue = CertificateUtils.formatValuePrincipal(keyValue[1]);
			List<String> listValue = null;
			if(hashPrincipal.containsKey(formatKey)) {
				listValue = hashPrincipal.get(formatKey);
			}
			else {
				listValue = new ArrayList<>();
				hashPrincipal.put(formatKey, listValue);
			}
			listValue.add(formatValue);
		}
		return hashPrincipal;
	}

	public static String formatKeyPrincipal(String keyPrincipal){
		return keyPrincipal.trim().toLowerCase();
	}
	public static String formatValuePrincipal(String valuePrincipal){
		// siccome uso il carattere '/' come separatore, un eventuale '/' deve essere escaped.
		StringBuilder bf = new StringBuilder();
		for (int i = 0; i < valuePrincipal.length(); i++) {
			if(valuePrincipal.charAt(i)=='/'){
				// escape
				if(i==0){
					bf.append('\\');
				}
				else{
					// verifico se non ho gia' effettuato l'escape
					if(valuePrincipal.charAt((i-1))!='\\'){
						bf.append('\\');
					}
				}
			}
			bf.append(valuePrincipal.charAt(i));
		}
		String value = bf.toString();
		if(TRIM_VALUE_BEFORE_SAVE_DB) {
			value = value.trim();
		}
		return value;
	}

	private static String[] getKeyValuePairEngine(String keyValue, String principal, PrincipalType type) throws UtilsException {
		
		if(!keyValue.contains("=")){
			throw new UtilsException(getAnalisiTypePrefixString(principal, type)+"fallita: ["+keyValue+"] non separata dal carattere \"=\". Verificare che non esistano coppie che possiedono valori che contengono il carattere separatore (usare eventualmente come carattere di escape '\\')");
		}
		
		// Bug Fix OP-664
		// Per non bruciare gli spazi presenti nei valori della chiave che sono ammessi. Ad esempio e' capitato un "C=IT,ST= ,CN=XXESEMPIOXX"
		/**String [] keyValue = keyValue.trim().split("=");*/
		String [] keyValueReturn = keyValue.split("="); // lo spazio è ammesso nel valore.		
		if(keyValueReturn.length<2){
			if(TRIM_VALUE_BEFORE_SAVE_DB && keyValueReturn.length==1) {
				// Serve per i valori inseriti che poi vengono comunque normalizzati con il trim. Se il valore era ' ' viene normalizzato a ''
				// una volta riletto poi si ottiene l'errore.
				// Che ci sia l'uguale e' garantito dai controlli sopra. In pratica keyValue.endsWith("=")
				String key = keyValueReturn[0];
				keyValueReturn = new String[2];
				keyValueReturn[0] = key;
				keyValueReturn[1] = "";
			}
			else {
				throw new UtilsException(getAnalisiTypePrefixString(principal, type)+"fallita: ["+keyValue+"] non contiene un valore? Verificare che non esistano coppie che possiedono valori che contengono il carattere separatore (usare eventualmente come carattere di escape '\\')");
			}
		}
		
		// Lo spazio e' ammesso nel valore, ma non nella chiave
		keyValueReturn[0] = keyValueReturn[0].trim();
		/**System.out.println("KEY["+keyValueReturn[0]+"]=VALUE["+keyValueReturn[1]+"]");*/
		
		// Questo controllo non deve essere fatto poiche' il valore puo' contenere il '='.
/**		if(keyValue.length!=2){
//			throw new UtilsException(getAnalisiTypePrefixString(principal, type)+"fallita: ["+keyValue+"] contiene piu' di un carattere \"=\"");
//		}*/
		if(keyValueReturn.length==2) {
			return keyValueReturn;
		}
		else {
			String[] keyValueReturnNormalized = new String[2];
			keyValueReturnNormalized[0] = keyValueReturn[0];
			keyValueReturnNormalized[1] = extractValueFromKeyPairEngine(keyValue);
			return keyValueReturnNormalized;
		}
		
	}
	private static String extractValueFromKeyPairEngine(String keyValue) throws UtilsException {
		// Questo metodo server poiche' il valore puo' contenere il '=' ed il carattere ' ' anche all'inizio o alla fine.
		// Quindi uno split con il carattere '=' non puo' essere usato.
		// Deve quindi essere estratto dopo il primo uguale
		int indexOf = keyValue.indexOf("=");
		if(indexOf<=0) {
			throw new UtilsException("Carattere '=' non presente in ["+keyValue+"]");
		}
		return keyValue.substring(indexOf+1);
		/**String valoreEstratto = keyValue.substring(indexOf+1);
		System.out.println("VALORE ESTRATTO ["+valoreEstratto+"]");
		return valoreEstratto;*/
	}
	
	
	private static boolean contains(String value,String separator){
		int indexOf = value.indexOf(separator);
		boolean found = false;
		if(indexOf==0){
			found = true;
		}
		else{
			boolean itera = true;
			while(indexOf>0 && itera){
				char precedente = value.charAt(indexOf-1);
				if(precedente == '\\'){
					if(indexOf+1>value.length()){
						itera=false;
					}
					else{
						indexOf = value.indexOf(separator,indexOf+1);
					}
				}
				else{
					found = true;
					itera=false;
				}
			}
		}
		return found;
	}
	
	public static Certificate readCertificate(CertificateDecodeConfig config, String certificateParam) throws UtilsException {
		return readCertificateEngine(config, certificateParam, Charset.UTF_8.getValue());
	}
	public static Certificate readCertificate(CertificateDecodeConfig config, String certificateParam, String charset) throws UtilsException {
		return readCertificateEngine(config, certificateParam, charset);
	}
	
	private static Certificate readCertificateEngine(CertificateDecodeConfig config, String certificateParam, String charset) throws UtilsException {
		if(config.isUrlDecodeOrBase64Decode()) {
			
			Throwable tUrlDecode = null;
			try {
				config.setUrlDecode(true);
				config.setBase64Decode(false);
				return readCertificateEngineEngine(config, certificateParam, charset);
			}catch(Exception t) {
				tUrlDecode = t;
			}
			
			Throwable tBase64Decode = null;
			try {
				config.setUrlDecode(false);
				config.setBase64Decode(true);
				return readCertificateEngineEngine(config, certificateParam, charset);
			}catch(Exception t) {
				tBase64Decode = t;
			}
			
			UtilsMultiException uMulti = new UtilsMultiException("Decodifica non riuscita", tUrlDecode, tBase64Decode);
			throw new UtilsException(uMulti.getMessage(),uMulti);
		}
		else {
			return readCertificateEngineEngine(config, certificateParam, charset);
		}
	}
	private static Certificate readCertificateEngineEngine(CertificateDecodeConfig config, String certificateParam, String charset) throws UtilsException {
		
		if(certificateParam==null || "".equals(certificateParam)){
			throw new UtilsException("Certificate non fornito");
		}
		
		try {
		
			String certificate = certificateParam;
			
			if(config.isUrlDecode()) {
				certificate = UriUtils.decode(certificate, charset);
			}
			
			if(config.isReplace()) {
				int index = 0; // per evitare bug di cicli infiniti
				while(certificate.contains(config.getReplaceSource()) && index<10000) {
					certificate = certificate.replace(config.getReplaceSource(), config.getReplaceDest());
					index++;
				}
			}
			
			if(config.isEnrichPEMBeginEnd()) {
				String bEGIN = "-----BEGIN CERTIFICATE-----";
				String eND = "-----END CERTIFICATE-----";
				if(!certificate.startsWith(bEGIN)) {
					certificate = bEGIN+"\n"+certificate;
				}
				if(!certificate.endsWith(eND)) {
					certificate = certificate+ "\n"+eND;
				}
			}
			
			byte [] certBytes = null;
			if(config.isBase64Decode()) {
				certBytes = Base64Utilities.decode(certificate);
			}
			else {
				certBytes = certificate.getBytes(charset);
			}
			
			// Per adesso l'utility di load gestisce solo il tipo DER. La decodifica in base64 è quindi essenziale, a meno che non sia un DER.
			
			return ArchiveLoader.load(certBytes);
			
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
		
	} 
	
	public static String toPEM(java.security.cert.X509Certificate cert) throws UtilsException {
		try {
			java.io.StringWriter sw = new java.io.StringWriter();
		    try (org.bouncycastle.openssl.jcajce.JcaPEMWriter pw = new org.bouncycastle.openssl.jcajce.JcaPEMWriter(sw)) {
		        pw.writeObject(cert);
		    }
		    sw.flush();
		    sw.close();
		    return sw.toString();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
	}
}

