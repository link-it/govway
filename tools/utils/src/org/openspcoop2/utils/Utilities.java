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



package org.openspcoop2.utils;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;


/**
 * Libreria contenente metodi utili generale.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Utilities {

	
	
	// ** Thread Sleep **
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		}catch(Throwable t) {
			// ignore
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T execute(int secondsTimeout, Callable<?> callable) throws TimeoutException, UtilsException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			Future<?> future = executor.submit(callable);
			return (T) future.get(secondsTimeout, TimeUnit.SECONDS); //timeout is in 2 seconds
		} catch (TimeoutException e) {
		    //System.err.println("Timeout");
		    throw e;
		} catch (InterruptedException e) {
			//System.err.println("Interrupted");
			throw new UtilsException(e.getMessage(),e);
		} catch (ExecutionException e) {
			//System.err.println("ExecutionException");
			throw new UtilsException(e.getMessage(),e);
		}finally {
			executor.shutdownNow();
		}
	}
	
	
	
	
	// Class.newInstance() is deprecated in Java 1++
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(String className) throws UtilsException {
		if(className!=null && !StringUtils.isEmpty(className)) {
			try {
				return  (T) newInstance(Class.forName(className));
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(), e);
			}
		}
		return null;
	}
	public static <T> T newInstance(Class<T> classType) throws UtilsException {
		if(classType!=null) {
			try {
				return  (T) classType.getConstructor().newInstance();
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(), e);
			}
		}
		return null;
	}
	
	public static <T> T newInstance_throwInstantiationException(String className) throws InstantiationException {
		try {
			return newInstance(className);
		}catch(Exception e) {
			throw new InstantiationException(e.getMessage());
		}
	}
	public static <T> T newInstance_throwInstantiationException(Class<T> classType) throws InstantiationException {
		try {
			return newInstance(classType);
		}catch(Exception e) {
			throw new InstantiationException(e.getMessage());
		}
	}
	


	/** ArrayBuffer utilizzato per la lettura */
	public static final int DIMENSIONE_BUFFER = 65536;
	
	
	/** String with charset Processing */
	public static String getAsString(URL url,String charsetName) throws UtilsException{
		return getAsString(url, charsetName, true);
	}
	public static String getAsString(URL url,String charsetName,boolean throwExceptionInputStreamEmpty) throws UtilsException{
		try{
			InputStream openStream = null;
			String content = null;
			try{
				openStream = url.openStream();
				content = Utilities.getAsString(openStream,charsetName,throwExceptionInputStreamEmpty);
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
	public static String getAsString(InputStream is,String charsetName) throws UtilsException{
		return getAsString(is,charsetName,true);
	}
	public static String getAsString(InputStream is,String charsetName,boolean throwExceptionInputStreamEmpty) throws UtilsException{
		try{
			return Utilities.getAsInputStreamReader(is,charsetName,throwExceptionInputStreamEmpty).toString();			
		}
		catch (UtilsException e) {
			throw e;
		}
		catch (java.lang.Exception e) {
			throw new UtilsException("Utilities.getAsString error "+e.getMessage(),e);
		}
	}
	public static StringBuilder getAsInputStreamReader(InputStream isParam,String charsetName) throws UtilsException{
		return getAsInputStreamReader(isParam, charsetName, true);
	}
	public static StringBuilder getAsInputStreamReader(InputStream isParam,String charsetName,boolean throwExceptionInputStreamEmpty) throws UtilsException{
		InputStreamReader isr = null;
		BufferedReader bufferedReader = null;
		try{
			if(isParam==null){
				if(throwExceptionInputStreamEmpty){
					throw new UtilsException("InputStream is null");
				}
				else{
					return null;
				}
			}
			
			InputStream is = normalizeStream(isParam, throwExceptionInputStreamEmpty);
			if(is==null) {
				// rifaccio il controllo nonostante l'eccezione venga lanciata nel normalizeStream
				if(throwExceptionInputStreamEmpty){
					throw new UtilsException("InputStream is empty (class:"+isParam.getClass().getName()+")");
				}
				else{
					return null;
				}
			}
			
			isr = new InputStreamReader(is, charsetName);
			StringWriter writer = new StringWriter();
//			bufferedReader = new BufferedReader(isr);
//	        char[] buf = new char[Utilities.DIMENSIONE_BUFFER];
//	        int numRead=0;
//	        while((numRead=bufferedReader.read(buf)) != -1){
//	            String readData = String.valueOf(buf, 0, numRead);
//	            buffer.append(readData);
//	        }
			CopyCharStream.copy(isr, writer);
	        return new StringBuilder(writer.toString());
			
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
				throw new UtilsException("Utilities.getAsString error",e);
			}
		
		} finally{
			try{
				if(bufferedReader!=null){
					bufferedReader.close();
				}
				if(isr!=null){
					isr.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	
	/** RawBinary Processing */
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
			ByteArrayOutputStream bout = Utilities.getAsByteArrayOuputStream(is,throwExceptionInputStreamEmpty);
			if(bout!=null) {
				return bout.toByteArray();
			}
			else {
				return null; // puo' succedere solo se throwExceptionInputStreamEmpty e' false
			}
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
	public static void writeAsByteArrayOuputStream(ByteArrayOutputStream bout, InputStream isParam) throws UtilsException{
		writeAsByteArrayOuputStream(bout, isParam, true);
	}
	public static ByteArrayOutputStream getAsByteArrayOuputStream(InputStream isParam,boolean throwExceptionInputStreamEmpty) throws UtilsException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		writeAsByteArrayOuputStream(bout, isParam, throwExceptionInputStreamEmpty);
		if(bout.size()>0) {
			return bout;
		}else {
			return null;
		}
	}
	public static void writeAsByteArrayOuputStream(OutputStream bout, InputStream isParam,boolean throwExceptionInputStreamEmpty) throws UtilsException{
		try{
			if(isParam==null){
				if(throwExceptionInputStreamEmpty){
					throw new UtilsException("InputStream is null");
				}
				else{
					return;
				}
			}
			
			InputStream is = normalizeStream(isParam, throwExceptionInputStreamEmpty);
			if(is==null) {
				// rifaccio il controllo nonostante l'eccezione venga lanciata nel normalizeStream
				if(throwExceptionInputStreamEmpty){
					throw new UtilsException("InputStream is empty (class:"+isParam.getClass().getName()+")");
				}
				else{
					return;
				}
			}			
			
//			byte [] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
//			int letti = 0;
//			while( (letti=is.read(buffer)) != -1 ){
//				bout.write(buffer, 0, letti);
//			}
//			bout.flush();
//			bout.close();
			CopyStream.copy(CopyStreamMethod.AUTO, is, bout);
			
			return;
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


	public static InputStream normalizeStream(InputStream is, boolean throwExceptionInputStreamEmpty) throws UtilsException {
		try {
			if(is!=null){
				byte[] buffer = new byte[2]; // UTF-16
				int letti = is.read(buffer);
				if( letti <=0 ) {
					if(throwExceptionInputStreamEmpty){
						throw new UtilsException("InputStream is empty (class:"+is.getClass().getName()+")");
					}
					else{
						return null;
					}
				} else {
					// Metodo alternativo: java.io.PushbackInputStream
					//System.out.println("NORMALIZED BY FROM ["+this.isResponse.getClass().getName()+"] ...");
					byte [] b = null;
					if(letti==2) {
						b = buffer;
					}
					else {
						b = new byte[1]; // e' per forza 1
						b[0] = buffer[0];
					}
					return new SequenceInputStream(new ByteArrayInputStream(b),is);
				}
			}
			return is; // null
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	
	/** Copy Stream */
	
	public static void copy(InputStream is,OutputStream os) throws UtilsException{
		try{
			CopyStream.copy(CopyStreamMethod.AUTO, is, os);
//			byte [] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
//			int letti = 0;
//			while( (letti=is.read(buffer)) != -1 ){
//				os.write(buffer, 0, letti);
//			}
//			os.flush();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	


	/** Properties */
	
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
			throw new UtilsException("Utilities.readProperties Riscontrato errore durante la lettura delle proprieta' con prefisso ["+prefix+"]: "+e.getMessage(),e);
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
		return convertSystemTimeIntoString_millisecondi(time, millisecondiCheck, true, ":",".","");
	}
	public static String convertSystemTimeIntoString_millisecondi(long time,boolean millisecondiCheck, boolean printZeroValues, 
			String separatorUnit,String separatorMs, String separatorValue){
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
		StringBuilder bf = new StringBuilder();

		long giorni = ore/24;
		long oreRimaste = ore%24;

		if(giorni>0){
			bf.append(giorni);
			bf.append(separatorValue);
			bf.append("d");
		}
		else{
			// Nothing
		}

		if(giorni>0){
			if(oreRimaste>0){
				if(bf.length()>0){
					bf.append(separatorUnit);
				}
				bf.append(oreRimaste);
				bf.append(separatorValue);
				bf.append("h");
			}else{
				if(printZeroValues && bf.length()>0){
					bf.append(separatorUnit);
					bf.append("0");
					bf.append(separatorValue);
					bf.append("h");
				}
			}
		}
		else{
			if(ore>0){
				if(bf.length()>0){
					bf.append(separatorUnit);
				}
				bf.append(ore);
				bf.append(separatorValue);
				bf.append("h");
			}else{
				if(printZeroValues && bf.length()>0){
					bf.append(separatorUnit);
					bf.append("0");
					bf.append(separatorValue);
					bf.append("h");
				}
			}
		}


		if(minuti>0){
			if(bf.length()>0){
				bf.append(separatorUnit);
			}
			bf.append(minuti);
			bf.append(separatorValue);
			bf.append("m");
		}else{
			if(printZeroValues && bf.length()>0){
				bf.append(separatorUnit);
				bf.append("0");
				bf.append(separatorValue);
				bf.append("m");
			}
		}

		if(secondi>0){
			if(bf.length()>0){
				bf.append(separatorUnit);
			}
			bf.append(secondi);
			bf.append(separatorValue);
			bf.append("s");
		}
		else{
			if(printZeroValues && bf.length()>0){
				bf.append(separatorUnit);
				bf.append("0");
				bf.append(separatorValue);
				bf.append("s");
			}
		}

		if(millisecondiCheck){
			if(millisecondi>0 || (millisecondi==0 && printZeroValues)){
				if(bf.length()>0){
					bf.append(separatorMs);
				}
				bf.append(millisecondi);
				bf.append(separatorValue);
				bf.append("ms");
			}
			else{
				if(printZeroValues && bf.length()>0){
					bf.append(separatorMs);
					bf.append("0");
					bf.append(separatorValue);
					bf.append("ms");
				}
			}
		}


		if(bf.length()==0){
			bf.append("conversione non riuscita");
		}

		return bf.toString();
	}
	
	public static long convertSystemTimeInNumeroOre(long time){
		if(time < 0)
			time = 0;
				
		// tempo in millisecondi diviso mille -> tempo in secondi
		long diff = (time)/1000;
		// tempo in ore = tempo in secondi / 60 * 60
		long ore = diff/3600;
		
		// controllo che non ci siano parti di ora residue
		if((diff%3600) != 0)
			ore ++;
			
		return ore;
	}
	
	public static long convertSystemTimeInNumeroGiorni(long time){
		if(time < 0)
			time = 0;
				
		// tempo in millisecondi diviso mille -> tempo in secondi
		long diff = (time)/1000;
		// tempo in ore = tempo in secondi / 60 * 60
		long ore = diff/3600;
		
		// controllo che non ci siano parti di ora residue
		if((diff%3600) != 0)
			ore ++;
		
		long giorni = ore/24;
		long oreRimaste = ore%24;
		
		if(oreRimaste != 0)
			giorni ++;
		
		return giorni;
	}
	


	private final static double KB = 1024;
	private final static double MB = 1048576;
	private final static double GB = 1073741824;
	public static String convertBytesToFormatString(long value) {
		return convertBytesToFormatString(value, false, "");
	}
	public static String convertBytesToFormatString(long value, boolean upperCase, String separator) {

		MessageFormat mf = new MessageFormat("{0,number,#.##}");
		Double len = null;
		String res = "";
		//il valore e' in byte
		len = Long.valueOf(value).doubleValue();
		long d = Math.round(len/Utilities.KB); 
		if(d<=1){
			//byte
			Object[] objs = {len};
			res = mf.format(objs);
			res +=separator;
			res += "b";
		}else if(d>1 && d<1000){
			//kilo byte
			Object[] objs = {len/Utilities.KB};
			res = mf.format(objs);
			res +=separator;
			res += "kb";
		}else  if (d >= 1000 && d < 1000000){
			//mega byte
			Object[] objs = {len/Utilities.MB};
			res = mf.format(objs);
			res +=separator;
			res += "mb";
		}
		else{
			// giga byte
			Object[] objs = { len/Utilities.GB };
			res = mf.format(objs);
			res +=separator;
			res +="gb";
		}

		if(upperCase){
			res = res.toUpperCase();
		}
		
		return res;
	}




	/* STRING UTILS NORMALIZE NAME */
	
	public static String convertNameToSistemaOperativoCompatible(String nome,boolean convertCharNotPermitted,Character charJollyCharNotPermitted,
			List<Character> permit, boolean addUniqueSuffixIfFoundCharNotPermitted){
		StringBuilder bf = new StringBuilder();
		boolean charNotPermittedFound = false;
		for (int i = 0; i < nome.length(); i++) {
			if(Character.isLetterOrDigit(nome.charAt(i))){
				bf.append(nome.charAt(i));
			}
			else {
				if(permit!=null){
					// check che sia nella lista dei caratteri permessi
					boolean found = false;
					for (char charPermit : permit) {
						if(charPermit == nome.charAt(i)){
							found = true;
							break;
						}
					}
					if(found){
						bf.append(nome.charAt(i));
						continue;
					}
				}
				
				// Se non e' nella lista dei caratteri permessi, se e' abilitata la conversione, converto il carattere non permesso nel carattere jolly
				// altrimenti lo "brucio"
				if(convertCharNotPermitted){
					// sostituisco tutto con il carattere jolly
					bf.append(charJollyCharNotPermitted);
				}
				charNotPermittedFound = true;
			}
		}
		if(charNotPermittedFound && addUniqueSuffixIfFoundCharNotPermitted){
			bf.append("_");
			bf.append(getNextCounterFile());
		}
		return bf.toString();
	}
	private static int counterFileNameWithCharNotPermitted = 0;
	private static synchronized int getNextCounterFile(){
		if(counterFileNameWithCharNotPermitted==Integer.MAX_VALUE){
			counterFileNameWithCharNotPermitted = 0;
		}
		counterFileNameWithCharNotPermitted++;
		return counterFileNameWithCharNotPermitted;
	}
	
	
	public static String camelCase(String value) throws UtilsException{
		char [] delimiters = new char[] {'.' , '_' , '-' , ':' , ';' , ',' , ' ' };
		return camelCase(delimiters, value);
	}
	public static String camelCase(char [] delimiters, String value) throws UtilsException{
				
		String s = WordUtils.capitalizeFully(value, delimiters);
		for (int i = 0; i < delimiters.length; i++) {
			String c = delimiters[i]+"";
			while(s.contains(c)) {
				s = s.replace(c, "");
			}
		}
		
		return s;
	}
	
	
	
	
	
	/* STRING UTILS CON ESCAPE */
	
	public static String[] split(String value, char separator) throws UtilsException{
		
		//System.out.println("SUBJECT SPLIT ["+separator+"]");
		
		StringBuilder bf = new StringBuilder();
		List<String> splitResults = new ArrayList<String>();
		if(value==null || value.length()<=0){
			throw new UtilsException("Valore non fornito");
		}
		for (int i = 0; i < value.length(); i++) {
			if(value.charAt(i) == separator){
				
//				if(i>0){
//					boolean v = (value.charAt(i-1) != '\\');
//					System.out.println("SUBJECT SERPARATOR FOUND i["+i+"] value["+value.charAt(i)+"] value-1["+value.charAt(i-1)+"] ("+v+")");
//				}
//				else{
//					System.out.println("SUBJECT SERPARATOR FOUND i["+i+"] value["+value.charAt(i)+"]");
//				}
				
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
		
		//System.out.println("SUBJECT SPLIT FINE ["+splitResults+"]");
		
		splitResults.add(bf.toString());
		return splitResults.toArray(new String[1]);
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
	
	public static String readFirstErrorValidMessageFromException(Throwable e) {
		if(e instanceof NullPointerException) {
			 return "NullPointerException";
		}else {
			Throwable inner = Utilities.getInnerNotEmptyMessageException(e);
			if(inner!=null) {
				return inner.getMessage();
			}
			else {
				if(Utilities.isEmpytMessageException(e)) {
					return e.toString();
				}
				else {
					return e.getMessage();
				}
			}
		}
	}
	
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
	
	public static boolean existsInnerInstanceException(Throwable e,Class<?> found){
		//System.out.println("ANALIZZO ["+e.getClass().getName()+"] ("+found+")");
		if(found.isInstance(e) ){
			//System.out.println("OK ["+e.getClass().getName()+"]");
			return true;
		}else{
			if(e.getCause()!=null){
				//System.out.println("INNER ["+e.getClass().getName()+"]...");
				return Utilities.existsInnerInstanceException(e.getCause(), found);
			}
			else{
				//System.out.println("ESCO ["+e.getClass().getName()+"]");
				return false;
			}
		}
	}
	public static boolean existsInnerInstanceException(Throwable e,String found) throws ClassNotFoundException{
		return Utilities.existsInnerInstanceException(e,Class.forName(found));
	}
	
	public static Throwable getInnerInstanceException(Throwable e,Class<?> found, boolean last){
		//System.out.println("ANALIZZO ["+e.getClass().getName()+"] ("+found+")");
		if(found.isInstance(e) ){
			
			if(last) {
				if(e.getCause()!=null && existsInnerInstanceException(e.getCause(), found)) {
					//System.out.println("INNER ["+e.getClass().getName()+"]...");
					return Utilities.getInnerInstanceException(e.getCause(), found, last);
				}
				else {
					//System.out.println("OK ["+e.getClass().getName()+"]");
					return e;
				}
			}
			else {
				//System.out.println("OK ["+e.getClass().getName()+"]");
				return e;
			}
		}else{
			if(e.getCause()!=null){
				//System.out.println("INNER ["+e.getClass().getName()+"]...");
				return Utilities.getInnerInstanceException(e.getCause(), found, last);
			}
			else{
				//System.out.println("ESCO ["+e.getClass().getName()+"]");
				return null;
			}
		}
	}
	public static Throwable getInnerInstanceException(Throwable e,String found, boolean last) throws ClassNotFoundException{
		return Utilities.getInnerInstanceException(e,Class.forName(found), last);
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

			StringBuilder bf = new StringBuilder();
			int index = indexOfValueWrong-1;
			while(tmp.charAt(index) != ' '){
				bf.append(tmp.charAt(index));
				index--;
			}

			StringBuilder replaceString = new StringBuilder();
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
	public static String getErrorMessagePrintableTextMaxLength(int length, int maxLength) {
		return "Visualizzazione non riuscita: la dimensione del pacchetto fornito ("+Utilities.convertBytesToFormatString(length)+") supera il limite consentito ("+Utilities.convertBytesToFormatString(maxLength)+")";
	}
	public static String convertToPrintableText(byte [] b,int maxBytes) throws UtilsException{
		ByteArrayOutputStream bout = null;
		try{

			bout = new ByteArrayOutputStream();
			
			if(b.length>maxBytes){
				throw new UtilsException(getErrorMessagePrintableTextMaxLength(b.length,maxBytes));
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
			
		}
		catch(UtilsException e){
			throw e;
		}
		catch(Exception e){
			throw new UtilsException("Visualizzazione non riuscita: Documento binario?",e);
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
	
	
	
	
	
	
	
	
	
	
	// ** Locale **
	
	public static String toString(java.util.Locale locale){
		return toString(locale,"\n");
	}
	public static String toString(java.util.Locale locale, String separator){
		StringBuilder bf = new StringBuilder();
		toString(locale,bf,separator);
		return bf.toString();
	}
	public static void toString(java.util.Locale locale, StringBuilder bf, String separator){
		
		bf.append(locale.getDisplayName());
		bf.append(separator);
		
		bf.append("Language: ");
		if(locale.getLanguage()!=null && !"".equals(locale.getLanguage().trim())){
			bf.append(locale.getLanguage());
			bf.append(" (");
			bf.append(locale.getDisplayLanguage());
			bf.append(") [ISO3:");
			try{
				if(locale.getISO3Language()!=null){
					bf.append(locale.getISO3Language());
				}
				else{
					bf.append("-");
				}
			}catch(Exception e){
				bf.append(e.getMessage());
			}
			bf.append("]");	
		}
		else{
			bf.append("-");
		}
		bf.append(separator);
		
		bf.append("Country: ");
		if(locale.getCountry()!=null && !"".equals(locale.getCountry().trim())){
			bf.append(locale.getCountry());
			bf.append(" (");
			bf.append(locale.getDisplayCountry());
			bf.append(") [ISO3:");
			try{
				if(locale.getISO3Language()!=null){
					bf.append(locale.getISO3Country());
				}
				else{
					bf.append("-");
				}
			}catch(Exception e){
				bf.append(e.getMessage());
			}
			bf.append("]");	
		}
		else{
			bf.append("-");
		}
		bf.append(separator);
				
		bf.append("Script: ");
		if(locale.getScript()!=null && !"".equals(locale.getScript().trim())){
			bf.append(locale.getScript());
			bf.append(" (");
			bf.append(locale.getDisplayScript());
			bf.append(")");
		}
		else{
			bf.append("-");
		}
		bf.append(separator);
		
		bf.append("Variant: ");
		if(locale.getVariant()!=null && !"".equals(locale.getVariant().trim())){
			bf.append(locale.getVariant());
			bf.append(" (");
			bf.append(locale.getDisplayVariant());
			bf.append(")");
		}
		else{
			bf.append("-");
		}
		bf.append(separator);
		
//		Iterator<String> it = locale.getUnicodeLocaleAttributes().iterator();
//		while (it.hasNext()) {
//			String attribute = (String) it.next();
//			bf.append("Attribute["+attribute+"]");
//			bf.append(separator);
//		}
//		
//		Iterator<Character> itC = locale.getExtensionKeys().iterator();
//		while (itC.hasNext()) {
//			Character character = (Character) itC.next();
//			bf.append("Extension["+character+"]=["+locale.getExtension(character)+"]");
//			bf.append(separator);
//		}
//		
//		it = locale.getUnicodeLocaleKeys().iterator();
//		while (it.hasNext()) {
//			String key = (String) it.next();
//			bf.append("Key["+key+"]=["+locale.getUnicodeLocaleType(key)+"]");
//			bf.append(separator);
//		}
	
	}
	
	
	
	// ** TimeZone **
	
	public static String toString(java.util.TimeZone timeZone){
		return toString(timeZone,false);
	}
	public static String toString(java.util.TimeZone timeZone, boolean allInfo){
		StringBuilder bf = new StringBuilder();
		toString(timeZone,bf,allInfo);
		return bf.toString();
	}
	public static void toString(java.util.TimeZone timeZone, StringBuilder bf, boolean allInfo){
		bf.append(timeZone.getID());
		bf.append(" (");
		bf.append(timeZone.getDisplayName());
		bf.append(")");
		if(allInfo){
			bf.append(" DSTSaving:");
			bf.append(timeZone.getDSTSavings());
			bf.append(" RawOffset:");
			bf.append(timeZone.getRawOffset());
		}
	}
	
	
	// ** URL **
	
	public static String buildUrl(String prefix, String contesto) {
		String url = prefix;
		if(contesto!=null && !"".equals(contesto)) {
			if(!url.endsWith("/")) {
				if(!contesto.startsWith("/")) {
					url = url +"/";
				}
			}
			else {
				if(contesto.startsWith("/") && contesto.length()>1) {
					contesto = contesto.substring(1);
				}
			}
			url = url + contesto;
		}
		return url;
	}
	
	
	// ** ConcurrentHashMap **
	
	public static ConcurrentHashMap<String, String> convertToConcurrentHashMap(Properties map) {
		if(map==null || map.isEmpty()) {
			return null;
		}
		ConcurrentHashMap<String, String> newMap = new ConcurrentHashMap<String, String>();
		Iterator<Object> it = map.keySet().iterator();
		while (it.hasNext()) {
			Object k = it.next();
			if(k instanceof String) {
				String key = (String)k;
				newMap.put(key, map.getProperty(key));
			}
		}
		return newMap;
	}
	public static <K,V> ConcurrentHashMap<K, V> convertToConcurrentHashMap(Map<K, V> map) {
		if(map==null || map.isEmpty()) {
			return null;
		}
		ConcurrentHashMap<K, V> newMap = new ConcurrentHashMap<K, V>();
		Iterator<K> it = map.keySet().iterator();
		while (it.hasNext()) {
			K k = (K) it.next();
			newMap.put(k, map.get(k));
		}
		return newMap;
	}
}
