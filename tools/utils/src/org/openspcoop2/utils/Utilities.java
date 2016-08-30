/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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



package org.openspcoop2.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.apache.xml.security.utils.RFC2253Parser;


/**
 * Libreria contenente metodi utili generale.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Utilities {


	/** ArrayBuffer utilizzato per la lettura */
	public static final int DIMENSIONE_BUFFER = 65536;

	public static byte[] getAsByteArray(URL url) throws UtilsException{
		return getAsByteArray(url, true);
	}
	public static byte[] getAsByteArray(URL url,boolean throwExceptionInputStreamEmpty) throws UtilsException{
		try{
			InputStream openStream = null;
			byte[] content = null;
			try{
				openStream = url.openStream();
				content = Utilities.getAsByteArray(openStream,throwExceptionInputStreamEmpty);
			}finally{
				try{
					openStream.close();
				}catch(Exception e){}
			}
			return content;
		}
		catch (UtilsException e) {
			throw e;
		}
		catch (java.lang.Exception e) {
			throw new UtilsException("Utilities.readBytes error "+e.getMessage(),e);
		}
	}
	public static byte[] getAsByteArray(InputStream is) throws UtilsException{
		return getAsByteArray(is,true);
	}
	public static byte[] getAsByteArray(InputStream is,boolean throwExceptionInputStreamEmpty) throws UtilsException{
		try{
			return Utilities.getAsByteArrayOuputStream(is,throwExceptionInputStreamEmpty).toByteArray();
		}
		catch (UtilsException e) {
			throw e;
		}
		catch (java.lang.Exception e) {
			throw new UtilsException("Utilities.readBytes error "+e.getMessage(),e);
		}
	}
	public static ByteArrayOutputStream getAsByteArrayOuputStream(InputStream isParam) throws UtilsException{
		return getAsByteArrayOuputStream(isParam, true);
	}
	public static ByteArrayOutputStream getAsByteArrayOuputStream(InputStream isParam,boolean throwExceptionInputStreamEmpty) throws UtilsException{
		try{
			if(isParam==null){
				if(throwExceptionInputStreamEmpty){
					throw new UtilsException("InputStream is null");
				}
				else{
					return null;
				}
			}
			
			byte[] b = new byte[1];
			InputStream is = null;
			if(isParam.read(b) == -1) {
				if(throwExceptionInputStreamEmpty){
					throw new UtilsException("InputStream is empty");
				}
				else{
					return null;
				}
			} else {
				// Metodo alternativo: java.io.PushbackInputStream
				is = new SequenceInputStream(new ByteArrayInputStream(b),isParam);
			}
			
			
			byte [] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
			int letti = 0;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			while( (letti=is.read(buffer)) != -1 ){
				bout.write(buffer, 0, letti);
			}
			bout.flush();
			bout.close();
			return bout;
		} catch (java.lang.Exception e) {
			if(e instanceof java.io.IOException){
				if(isEmpytMessageException(e)==false){
					throw new UtilsException(e.getMessage(),e);
				}
			}
			else if(existsInnerException(e, java.io.IOException.class)){
				Throwable tInternal = getInnerException(e, java.io.IOException.class);
				if(isEmpytMessageException(tInternal)==false){
					throw new UtilsException(tInternal.getMessage(),e);
				}
			}

			Throwable tInternal = getInnerNotEmptyMessageException(e);
			if(tInternal!=null){
				throw new UtilsException(tInternal.getMessage(),e);
			}
			else{
				throw new UtilsException("Utilities.readBytes error",e);
			}
		
		}
	}






	public static java.util.Properties getAsProperties(InputStream is) throws UtilsException{
		try{
			if(is==null){
				throw new UtilsException("Utilities.getAsProperties error: InputStream is null");
			}
			Properties p = new Properties();
			p.load(is);
			return p;
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static java.util.Properties getAsProperties(byte[] content) throws UtilsException{
		ByteArrayInputStream bin = null;
		try{
			if(content==null){
				throw new UtilsException("Utilities.getAsProperties error: Content is null");
			}
			bin = new ByteArrayInputStream(content);
			return getAsProperties(bin);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		finally{
			try{
				if(bin!=null){
					bin.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	public static java.util.Properties getAsProperties(URL url) throws UtilsException{
		InputStream is = null;
		try{
			if(url==null){
				throw new UtilsException("Utilities.getAsProperties error: URL is null");
			}
			is = url.openStream();
			return getAsProperties(is);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
	}

	/**
	 * Legge le proprieta' che possiedono un nome che inizia con un determinato prefisso
	 * 
	 * @param prefix
	 * @param sorgente java.util.Properties
	 * @return java.util.Properties
	 * @throws UtilsException
	 */
	public static java.util.Properties readProperties (String prefix,java.util.Properties sorgente)throws UtilsException{
		java.util.Properties prop = new java.util.Properties();
		try{ 
			java.util.Enumeration<?> en = sorgente.propertyNames();
			for (; en.hasMoreElements() ;) {
				String property = (String) en.nextElement();
				if(property.startsWith(prefix)){
					String key = (property.substring(prefix.length()));
					if(key != null)
						key = key.trim();
					String value = sorgente.getProperty(property);
					if(value!=null)
						value = value.trim();
					if(key!=null && value!=null)
						prop.setProperty(key,value);
				}
			}
			return prop;
		}catch(java.lang.Exception e) {
			throw new UtilsException("Utilities.readProperties Riscontrato errore durante la lettura delle propriete con prefisso ["+prefix+"]: "+e.getMessage(),e);
		}  
	}
	/**
	 * Legge le proprieta' che possiedono un nome che inizia con un determinato prefisso
	 * 
	 * @param prefix
	 * @param key Array di chiavi
	 * @param name Array di nomi
	 * @return java.util.Properties
	 * @throws UtilsException
	 */
	public static java.util.Properties readProperties (String prefix,String[]key,String[] name)throws UtilsException{
		java.util.Properties sorgente = new java.util.Properties();
		if(key!=null && name!=null && key.length==name.length){
			for(int i=0; i<key.length; i++){
				sorgente.put(key[i], name[i]);
			}
		}	
		return Utilities.readProperties(prefix,sorgente);
	}




	/**
	 * Converte unix_timestamp in stringa
	 * 
	 * @param time unix_timestamp
	 * @param millisecondiCheck Se true verranno indicati anche i millisecondi
	 * @return String unix_timestamp
	 */
	public static String convertSystemTimeIntoString_millisecondi(long time,boolean millisecondiCheck){
		//System.out.println("VALORE PASSATO: ["+time+"]");
		long millisecondi = time % 1000;
		//System.out.println("Millisecondi (Valore%1000): ["+millisecondi+"]");
		long diff = (time)/1000;
		//System.out.println("Diff... (valore/1000) ["+diff+"]");
		long ore = diff/3600;
		//System.out.println("Ore... (diff/3600) ["+ore+"]");
		long minuti = (diff%3600) / 60;
		//System.out.println("Minuti... (diff%3600) / 60 ["+minuti+"]");
		long secondi = (diff%3600) % 60;
		//System.out.println("Secondi... (diff%3600) % 60 ["+secondi+"]");
		StringBuffer bf = new StringBuffer();

		long giorni = ore/24;
		long oreInUnGiorno = giorni%24;

		if(giorni>0){
			bf.append(giorni+"d");
		}
		else{
			// Nothing
		}

		if(giorni>0){
			if(oreInUnGiorno>0){
				if(bf.length()>0)
					bf.append(":");
				bf.append(oreInUnGiorno+"h");
			}else{
				if(bf.length()>0){
					bf.append(":0h");
				}
			}
		}
		else{
			if(ore>0){
				if(bf.length()>0)
					bf.append(":");
				bf.append(ore+"h");
			}else{
				if(bf.length()>0){
					bf.append(":0h");
				}
			}
		}


		if(minuti>0){
			if(bf.length()>0)
				bf.append(":");
			bf.append(minuti+"m");
		}else{
			if(bf.length()>0){
				bf.append(":0m");
			}
		}

		if(secondi>0){
			if(bf.length()>0)
				bf.append(":");
			bf.append(secondi+"s");
		}
		else{
			if(bf.length()>0){
				bf.append(":0s");
			}
		}

		if(millisecondiCheck){
			if(millisecondi>=0){
				if(bf.length()>0)
					bf.append(".");
				bf.append(millisecondi+"ms");
			}
			else{
				if(bf.length()>0){
					bf.append(".0ms");
				}
			}
		}


		if(bf.length()==0){
			bf.append("conversione non riuscita");
		}

		return bf.toString();
	}


	private final static double KB = 1024;
	private final static double MB = 1048576;
	public static String convertBytesToFormatString(long value) {

		MessageFormat mf = new MessageFormat("{0,number,#.##}");
		Double len = null;
		String res = "";
		//il valore e' in byte
		len = new Long(value).doubleValue();
		long d = Math.round(len/Utilities.KB); 
		if(d<=1){
			//byte
			Object[] objs = {len};
			res = mf.format(objs);
			res += "b";
		}else if(d>1 && d<1000){
			//kilo byte
			Object[] objs = {len/Utilities.KB};
			res = mf.format(objs);
			res += "kb";
		}else{
			//mega byte
			Object[] objs = {len/Utilities.MB};
			res = mf.format(objs);
			res += "mb";
		}

		return res;
	}




	
	
	
	/* STRING UTILS CON ESCAPE */
	
	public static String[] split(String value, char separator) throws UtilsException{
		StringBuffer bf = new StringBuffer();
		List<String> splitResults = new ArrayList<String>();
		if(value==null || value.length()<=0){
			throw new UtilsException("Valore non fornito");
		}
		for (int i = 0; i < value.length(); i++) {
			if(value.charAt(i) == separator){
				if(i>0 && (value.charAt(i-1) != '\\') ){
					splitResults.add(bf.toString());
					bf.delete(0, bf.length());
				}
				else if (i==0){
					splitResults.add(bf.toString());
					bf.delete(0, bf.length());
				}
				else{
					bf.append(value.charAt(i));
				}
			}
			else{
				bf.append(value.charAt(i));
			}
		}
		splitResults.add(bf.toString());
		return splitResults.toArray(new String[1]);
	}
	
	private static boolean contains(String value,String separator){
		int indexOf = value.indexOf(separator);
		boolean found = false;
		if(indexOf==0){
			found = true;
		}
		else{
			while(indexOf>0){
				char precedente = value.charAt(indexOf-1);
				if(precedente == '\\'){
					if(indexOf+1>value.length()){
						break;
					}
					else{
						indexOf = value.indexOf(separator,indexOf+1);
					}
				}
				else{
					found = true;
					break;
				}
			}
		}
		return found;
	}
	


	/* UTILITY SSL */
	public static boolean sslVerify(String subjectPresenteNellaConfigurazione, String subjectArrivatoConnessioneSSL) throws UtilsException{

		//System.out.println("SSL VERIFY CONF["+subjectPresenteNellaConfigurazione+"] SSL["+subjectArrivatoConnessioneSSL+"]");

		// Costruzione key=value
		Hashtable<String, String> hashSubjectArrivatoConnessioneSSL = Utilities.getSubjectIntoHashtable(subjectArrivatoConnessioneSSL);
		Hashtable<String, String> hashSubjectPresenteNellaConfigurazione = Utilities.getSubjectIntoHashtable(subjectPresenteNellaConfigurazione);

		if(hashSubjectArrivatoConnessioneSSL.size() != hashSubjectPresenteNellaConfigurazione.size()){
			//System.out.println("LUNGHEZZA DIVERSA");
			return false;
		}


		Enumeration<String> keys = hashSubjectArrivatoConnessioneSSL.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();

			if(hashSubjectPresenteNellaConfigurazione.containsKey(key)==false){
				//System.out.println("KEY ["+key+"] non presente");
				return false;
			}

			// Prendo valori
			String connessioneSSLValue = hashSubjectArrivatoConnessioneSSL.get(key);
			String configurazioneInternaValue = hashSubjectPresenteNellaConfigurazione.get(key);
			
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
			
			if(connessioneSSLValue.equals(configurazioneInternaValue)==false){
				//System.out.println("VALUE SSL["+connessioneSSLValue+"]=CONF["+configurazioneInternaValue+"] non match");
				return false;
			}
		}

		//System.out.println("SSL RETURN TRUE");
		return true;

	}


	public static String formatSubject(String subject) throws UtilsException{

		// Autenticazione SSL deve essere LIKE
		Hashtable<String, String> hashSubject = Utilities.getSubjectIntoHashtable(subject);
		StringBuffer bf = new StringBuffer();
		bf.append("/");
		Enumeration<String> keys = hashSubject.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			String value = hashSubject.get(key);
			bf.append(Utilities.formatKeySubject(key));
			bf.append("=");
			bf.append(Utilities.formatValueSubject(value));
			bf.append("/");
		}
		return bf.toString();
	}

	public static void validaSubject(String subjectParam) throws UtilsException{
		
		String subject = subjectParam;
		UtilsException normalizedException = null;
		try{
			String tmp = normalizeSubject(subjectParam);
			subject = tmp;
		}catch(UtilsException e){
			// non voglio rilanciare l'eccezione, verra' segnalata l'eccezione puntuale.
			// Se cosi' non fosse solo in fondo viene sollevata l'eccezione.
			normalizedException = e;
		}
		
		boolean commaFound = contains(subject, ",");
		boolean slashFound = contains(subject, "/");
		if(commaFound && slashFound){
			throw new UtilsException("("+subject+") Non possono coesistere i separatore \",\" e \"/\", solo uno dei due tipi deve essere utilizzato come delimitatore (usare eventualmente come carattere di escape '\\')");
		}
		if(commaFound==false && slashFound==false && subject.contains("=")==false){
			throw new UtilsException("("+subject+") Subject non valido, nemmeno una coppia nome=valore trovata");
		}
		String [] valoriSubject = Utilities.getValoriSubject(subject);
		boolean campoObbligatorioCN = false;
		boolean campoObbligatorioOU = false;
		boolean campoObbligatorioO = false;
		boolean campoObbligatorioL = false;
		boolean campoObbligatorioST = false;
		boolean campoObbligatorioC = false;
		boolean campoObbligatorioE = false;
		for(int i=0; i<valoriSubject.length; i++){
			if(valoriSubject[i].contains("=")==false){
				throw new UtilsException("("+subject+") Comprensione subject non riuscita: ["+valoriSubject[i]+"] non separata dal carattere \"=\". Verificare che non esistano coppie che possiedono valori che contengono il carattere separatore (usare eventualmente come carattere di escape '\\')");
			}
			String [] keyValue = valoriSubject[i].trim().split("=");
			if(keyValue.length<2){
				throw new UtilsException("("+subject+") Comprensione subject non riuscita: ["+valoriSubject[i]+"] non contiene un valore? Verificare che non esistano coppie che possiedono valori che contengono il carattere separatore (usare eventualmente come carattere di escape '\\')");
			}
//			if(keyValue.length!=2){
//				throw new UtilsException("("+subject+") Comprensione subject non riuscita: ["+valoriSubject[i]+"] contiene piu' di un carattere \"=\"");
//			}
			if(keyValue[0].trim().contains(" ")){
				throw new UtilsException("("+subject+") Comprensione subject non riuscita: il campo ["+valoriSubject[i]+"] contiene spazi nella chiave identificativa ["+keyValue[0].trim()+"]");
			}
			if(Utilities.formatKeySubject(keyValue[0]).equalsIgnoreCase("CN")){
				campoObbligatorioCN = true;
			}
			else if(Utilities.formatKeySubject(keyValue[0]).equalsIgnoreCase("OU")){
				campoObbligatorioOU = true;
			}
			else if(Utilities.formatKeySubject(keyValue[0]).equalsIgnoreCase("O")){
				campoObbligatorioO = true;
			}
			else if(Utilities.formatKeySubject(keyValue[0]).equalsIgnoreCase("L")){
				campoObbligatorioL = true;
			}
			else if(Utilities.formatKeySubject(keyValue[0]).equalsIgnoreCase("ST")){
				campoObbligatorioST = true;
			}
			else if(Utilities.formatKeySubject(keyValue[0]).equalsIgnoreCase("C")){
				campoObbligatorioC = true;
			}
			else if(Utilities.formatKeySubject(keyValue[0]).equalsIgnoreCase("E")){
				campoObbligatorioE = true;
			}
		}
		if(!campoObbligatorioCN && !campoObbligatorioOU && !campoObbligatorioO && !campoObbligatorioL && !campoObbligatorioST && !campoObbligatorioC && !campoObbligatorioE){
			throw new UtilsException("("+subject+") Almeno un attributo di certificato tra 'CN', 'OU', 'O', 'L', 'ST', 'C' e 'E' deve essere valorizzato.");
		}
		
		if(normalizedException!=null){
			throw normalizedException;
		}
	}

	
	public static String normalizeSubject(String subjectParam) throws UtilsException{
		
		/*
		 *  The subject extract from class represents an X.500 Principal. 
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
			String subject = RFC2253Parser.normalize(subjectParam);
			//System.out.println(" ORIGINALE ["+subjectParams+"]  NORMALIZZATO ["+subject+"]");
			return subject;
		}catch(Exception e){
			throw new UtilsException("("+subjectParam+") Normalizzazione RFC2253 non riuscita: "+e.getMessage(),e);
		}
	}

	public static String [] getValoriSubject(String subjectParam) throws UtilsException{
		try{
			
			String subject = normalizeSubject(subjectParam);
			
			return _getValoriSubject(subject);
			
		}catch(Exception e){
			
			try{
			
				javax.naming.ldap.LdapName prova = new javax.naming.ldap.LdapName(subjectParam);
				Enumeration<String> ens = prova.getAll();
				List<String> values = new ArrayList<String>();
				while (ens.hasMoreElements()) {
					String name = (String) ens.nextElement();
					values.add(name);
				}
				
				if(values.size()>0){
					return values.toArray(new String[1]);
				}
				else{
					throw new Exception("Coppie nome/valore non trovate");
				}
				
			}catch(Exception e2Level){
				e2Level.printStackTrace(System.out);
				throw new UtilsException("("+subjectParam+") javax.naming.ldap.LdapName reader failed: "+e2Level.getMessage()+". \nFirst method error: "+e.getMessage(),e);
			}
				
		}
	}
		
	private static String [] _getValoriSubject(String subject) throws UtilsException{
			
		String [] valori;
		boolean commaFound = contains(subject, ",");
		boolean slashFound = contains(subject, "/");
		if(commaFound){
			if(subject.startsWith(",")){
				subject = subject.substring(1);
			}
			if(subject.endsWith(",")){
				subject = subject.substring(0,subject.length()-1);
			}
			//valori =  subject.split(",");
			valori = split(subject, ',');
		}else{
			if(slashFound==false){
				int indexOf = subject.indexOf("=");
				if(indexOf<=0){
					throw new UtilsException("("+subject+") Separatore validi per il subject interno alla configurazione di OpenSPCoop non trovati:  \",\" o \"/\" e carattere \"=\" non presente");
				}
				if(subject.indexOf("=",indexOf+1)>=0){
					throw new UtilsException("("+subject+") Separatore validi per il subject interno alla configurazione di OpenSPCoop non trovati:  \",\" o \"/\"");
				}
				valori =  new String[1];
				valori[0] = subject;
			}else{
				if(subject.startsWith("/")){
					subject = subject.substring(1);
				}
				if(subject.endsWith("/")){
					subject = subject.substring(0,subject.length()-1);
				}
				//valori =  subject.split("/");
				valori = split(subject, '/');
			}
		}
		if(valori==null || valori.length<1){
			throw new UtilsException("("+subject+") Comprensione subject interno alla configurazione di OpenSPCoop non riuscita: null??");
		}
		
		// validazione
		for(int i=0; i<valori.length; i++){
			if(valori[i].contains("=")==false){
				throw new UtilsException("("+subject+") Comprensione subject non riuscita: ["+valori[i]+"] non separata dal carattere \"=\". Verificare che non esistano coppie che possiedono valori che contengono il carattere separatore (usare eventualmente come carattere di escape '\\')");
			}
			String [] keyValue = valori[i].trim().split("=");
			if(keyValue.length<2){
				throw new UtilsException("("+subject+") Comprensione subject non riuscita: ["+valori[i]+"] non contiene un valore? Verificare che non esistano coppie che possiedono valori che contengono il carattere separatore (usare eventualmente come carattere di escape '\\')");
			}
		}
				
		return valori;
	}
	
	public static Hashtable<String, String> getSubjectIntoHashtable(String subject) throws UtilsException{
		Hashtable<String, String> hashSubject = new Hashtable<String, String>();
		String [] valoriSubject = Utilities.getValoriSubject(subject);
		for(int i=0; i<valoriSubject.length; i++){
			if(valoriSubject[i].contains("=")==false){
				throw new UtilsException("("+subject+") Comprensione subject non riuscita: ["+valoriSubject[i]+"] non separata dal carattere \"=\"");
			}
			String [] keyValue = valoriSubject[i].trim().split("=");
			if(keyValue.length<2){
				throw new UtilsException("("+subject+") Comprensione subject non riuscita: ["+valoriSubject[i]+"] non contiene un valore? Verificare che non esistano coppie che possiedono valori che contengono il carattere separatore (usare eventualmente come carattere di escape '\\')");
			}
//			if(keyValue.length!=2){
//				throw new UtilsException("("+subject+") Comprensione subject non riuscita: ["+valoriSubject[i]+"] contiene piu' di un carattere \"=\"");
//			}
			//System.out.println("CONF INTERNA ["+Utilities.formatKeySubject(keyValue[0])+"] ["+Utilities.formatValueSubject(keyValue[1])+"]");
			String valore = Utilities.formatValueSubject(valoriSubject[i].trim().substring(keyValue[0].length()+"=".length()));
			hashSubject.put(Utilities.formatKeySubject(keyValue[0]), valore);
		}
		return hashSubject;
	}

	public static String formatKeySubject(String keySubject){
		return keySubject.trim().toLowerCase();
	}
	public static String formatValueSubject(String valueSubject){
		// siccome uso il carattere '/' come separatore, un eventuale '/' deve essere escaped.
		StringBuffer bf = new StringBuffer();
		for (int i = 0; i < valueSubject.length(); i++) {
			if(valueSubject.charAt(i)=='/'){
				// escape
				if(i==0){
					bf.append('\\');
				}
				else{
					// verifico se non ho gia' effettuato l'escape
					if(valueSubject.charAt((i-1))!='\\'){
						bf.append('\\');
					}
				}
			}
			bf.append(valueSubject.charAt(i));
		}
		return bf.toString().trim();
	}






	// Metodi per il logging dell'heap space

	public static boolean freeMemoryLog = false;
	public static Logger log;
	public static final Runtime s_runtime = Runtime.getRuntime ();
	public static final long INITIAL_SIZE = Utilities.s_runtime.freeMemory();
	public static void printFreeMemory(String descr){
		if(Utilities.freeMemoryLog){
			long currentSize = Utilities.s_runtime.freeMemory();
			Utilities.log.debug("[Free Memory Space]" + "[CURRENT: " + Utilities.convertBytesToFormatString(currentSize) + "] [DIFF: " + Utilities.convertBytesToFormatString(Utilities.INITIAL_SIZE - Utilities.s_runtime.freeMemory()) + "] " + descr);
		}
	}






	// Gestione eccezioni
	
	public static boolean isEmpytMessageException(Throwable e){
		if(e.getMessage()==null ||
				"".equals(e.getMessage()) || 
				"null".equalsIgnoreCase(e.getMessage()) ){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static boolean existsInnerException(Throwable e,Class<?> found){
		return Utilities.existsInnerException(e,found.getName());
	}
	public static boolean existsInnerException(Throwable e,String found){
		//System.out.println("ANALIZZO ["+e.getClass().getName()+"] ("+found+")");
		if(e.getClass().getName().equals(found) ){
			//System.out.println("OK ["+e.getClass().getName()+"]");
			return true;
		}else{
			if(e.getCause()!=null){
				//System.out.println("INNER ["+e.getClass().getName()+"]...");
				return Utilities.existsInnerException(e.getCause(), found);
			}
			else{
				//System.out.println("ESCO ["+e.getClass().getName()+"]");
				return false;
			}
		}
	}

	public static Throwable getInnerException(Throwable e,Class<?> found){
		return Utilities.getInnerException(e,found.getName());
	}
	public static Throwable getInnerException(Throwable e,String found){
		//System.out.println("ANALIZZO ["+e.getClass().getName()+"] ("+found+")");
		if(e.getClass().getName().equals(found) ){
			//System.out.println("OK ["+e.getClass().getName()+"]");
			return e;
		}else{
			if(e.getCause()!=null){
				//System.out.println("INNER ["+e.getClass().getName()+"]...");
				return Utilities.getInnerException(e.getCause(), found);
			}
			else{
				//System.out.println("ESCO ["+e.getClass().getName()+"]");
				return null;
			}
		}
	}

	public static Throwable getLastInnerException(Throwable e){
		if(e.getCause()==null){
			return e;
		}
		else{
			return Utilities.getLastInnerException(e.getCause());
		}
	}

	public static boolean existsInnerMessageException(Throwable e,String msg,boolean contains){
		//System.out.println("ANALIZZO ["+e.getClass().getName()+"] ("+found+")");
		boolean search = false;
		if(contains){
			search = e.getMessage()!=null && e.getMessage().contains(msg);
		}else{
			search = e.getMessage()!=null && e.getMessage().equals(msg);
		}
		if( search ){
			//System.out.println("OK ["+e.getClass().getName()+"]");
			return true;
		}else{
			if(e.getCause()!=null){
				//System.out.println("INNER ["+e.getClass().getName()+"]...");
				return Utilities.existsInnerMessageException(e.getCause(), msg, contains);
			}
			else{
				//System.out.println("ESCO ["+e.getClass().getName()+"]");
				return false;
			}
		}
	}

	public static Throwable getInnerMessageException(Throwable e,String msg,boolean contains){
		//System.out.println("ANALIZZO ["+e.getClass().getName()+"] ("+found+")");
		boolean search = false;
		if(contains){
			search = e.getMessage()!=null && e.getMessage().contains(msg);
		}else{
			search = e.getMessage()!=null && e.getMessage().equals(msg);
		}
		if( search ){
			//System.out.println("OK ["+e.getClass().getName()+"]");
			return e;
		}else{
			if(e.getCause()!=null){
				//System.out.println("INNER ["+e.getClass().getName()+"]...");
				return Utilities.getInnerMessageException(e.getCause(), msg, contains);
			}
			else{
				//System.out.println("ESCO ["+e.getClass().getName()+"]");
				return null;
			}
		}
	}


	public static Throwable getInnerNotEmptyMessageException(Throwable e){
		//System.out.println("ANALIZZO ["+e.getClass().getName()+"] ("+e.getMessage()+")");
		if(e.getMessage()!=null && !"".equals(e.getMessage()) && !"null".equalsIgnoreCase(e.getMessage())){
			return e;
		}

		if(e.getCause()!=null){
			//System.out.println("INNER ["+e.getClass().getName()+"]...");
			return Utilities.getInnerNotEmptyMessageException(e.getCause());
		}
		else{
			return e; // sono nella foglia, ritorno comunque questa eccezione
		}
	}


	public static boolean isExceptionInstanceOf(Class<?> c,Throwable t){
		return isExceptionInstanceOf(c.getName(), t);
	}
	public static boolean isExceptionInstanceOf(String className,Throwable t){
		if(t.getClass().getName().equals(className)){
			return true;
		}
//		else if(t.getClass().getSuperclass()!=null && t.getClass().getSuperclass().equals(className)){
//			return true;
//		}
		else{
			try{
				Class<?> c = Class.forName(className); 
				return c.isInstance(t);
			}catch(Throwable tException){
				return false;
			}
		}
	}







	// ** Elimina un attributo xml che contiene la keyword indicata nel secondo parametro */

	public static String eraserXmlAttribute(String tmp,String keyword){
		int indexOfValueWrong = tmp.indexOf(keyword);
		while(indexOfValueWrong>0){

			StringBuffer bf = new StringBuffer();
			int index = indexOfValueWrong-1;
			while(tmp.charAt(index) != ' '){
				bf.append(tmp.charAt(index));
				index--;
			}

			StringBuffer replaceString = new StringBuffer();
			for (int i = (bf.toString().length()-1); i >=0; i--) {
				replaceString.append(bf.toString().charAt(i));
			}
			replaceString.append(keyword);

			index = indexOfValueWrong+keyword.length();
			while( (tmp.charAt(index) != ' ') && (tmp.charAt(index) != '>') ){
				replaceString.append(tmp.charAt(index));
				index++;
			}

			//System.out.println("Replace ["+replaceString.toString()+"]");
			tmp = StringUtils.replace(tmp, replaceString.toString(), "");

			indexOfValueWrong = tmp.indexOf(keyword);
		}
		return tmp;
	}










	// ** Traduce in String un byte[] se il testo è visualizzabile **
	public static String convertToPrintableText(byte [] b,int maxBytes) throws UtilsException{
		ByteArrayOutputStream bout = null;
		try{

			bout = new ByteArrayOutputStream();
			
			if(b.length>maxBytes){
				throw new UtilsException("Visualizzazione non riuscita: la dimensione del pacchetto fornito ("+Utilities.convertBytesToFormatString(b.length)+") supera il limite consentito ("+Utilities.convertBytesToFormatString(maxBytes)+")");
			}

			for (int i = 0; i < b.length; i++) {
				if(!Utilities.isPrintableChar((char)b[i])){
					throw new UtilsException("Visualizzazione non riuscita: il documento contiene caratteri non visualizzabili");
				}
			}

			bout.write(b);
			bout.flush();
			bout.close();
			String tmp = bout.toString();
			bout = null;
			return tmp;
			
		}catch(Exception e){
			throw new UtilsException("Visualizzazione non riuscita: Documento binario?");
		}finally{
			try{
				if(bout!=null){
					bout.close();
				}
			}catch(Exception eClose){}
		}

	}

	public static boolean isPrintableChar( char c ) {
		if ( Character.isDefined(c))
		{
			return true;
		}
		else{
			return false;
		}
	}
	
	

}
