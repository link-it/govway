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


package org.openspcoop2.utils.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * ZipUtilities
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ZipUtilities {

	public static byte[] zip(byte[] content,String name) throws UtilsException{
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ZipOutputStream zipOut = new ZipOutputStream(bout);
			ByteArrayInputStream bin = new ByteArrayInputStream(content);
			ZipEntry zipEntry = new ZipEntry(name);
			zipOut.putNextEntry(zipEntry);
			zipOut.write(content);
			zipOut.flush();
			zipOut.close();
			bin.close();
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new UtilsException("Errore durante la scrittura dello zip: "+e.getMessage(),e);
		}
	}
	
	public static byte[] zip(List<Entry> entries) throws UtilsException{
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ZipOutputStream zipOut = new ZipOutputStream(bout);
			
			for (Entry entry : entries) {
				String name = entry.getName();
				ZipEntry zipEntry = new ZipEntry(name);
				zipOut.putNextEntry(zipEntry);
				zipOut.write(entry.getContent());
			}
			
			zipOut.flush();
			zipOut.close();
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new UtilsException("Errore durante la scrittura dello zip: "+e.getMessage(),e);
		}
	}
	
	public static byte[] unzip(byte[] zipContent) throws UtilsException{
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ByteArrayInputStream bin = new ByteArrayInputStream(zipContent);
			ZipInputStream zis = new ZipInputStream(bin);
			int count = 0;
			ZipEntry zipEntry = zis.getNextEntry();
	        while(zipEntry != null){
	        	count++;
	        	if(count>1) {
	        		throw new UtilsException("Errore, il metodo supporta solamente archivi zip contenente un file");
	        	}
	        	int len;
	        	byte[] buffer = new byte[1024];
	            while ((len = zis.read(buffer)) > 0) {
	            	bout.write(buffer, 0, len);
	            }
	            zipEntry = zis.getNextEntry();
	        }
	        zis.closeEntry();
	        zis.close();
	        bout.flush();
	        bout.close();
	        bin.close();
	        return bout.toByteArray();
		}catch(Exception e){
			throw new UtilsException("Errore durante la lettura dello zip: "+e.getMessage(),e);
		}
	}

	public static List<Entry> read(byte[] zipContent) throws UtilsException{
		try {
			List<Entry> list = new ArrayList<>();
			
			ByteArrayInputStream in = new ByteArrayInputStream(zipContent);
			ZipInputStream zipIn = new ZipInputStream(in);
		    ZipEntry entry;
		    while ((entry = zipIn.getNextEntry()) != null) {
		        String name = entry.getName();
		    	//System.out.println(name);
		        
		        if(name!=null && !( name.endsWith("/") || name.endsWith("\\") ) ){
		        	Entry zentry = new Entry();
	        		zentry.setName(name);
	        		
	        		ByteArrayOutputStream bout = new ByteArrayOutputStream();
	        		byte contents[] = new byte[4096];
	        		int direct;
	        		while ((direct = zipIn.read(contents, 0, contents.length)) >= 0) {
	        			//System.out.println("Read " + direct + "bytes content.");
	        			bout.write(contents, 0, direct);
	        		}
	        		bout.flush();
	        		bout.close();
	        		
	        		zentry.setContent(bout.toByteArray());
	        		list.add(zentry);
		        }
		        
		        zipIn.closeEntry();
		    }
		    zipIn.close();
		    in.close();

	        return list;
	        
		}catch(Exception e){
			throw new UtilsException("Errore durante la lettura dello zip: "+e.getMessage(),e);
		}
	}

	
	public static String getRootDir(String entry) throws UtilsException{
		try{
			String rootDir = null;
			String dir = entry;
			int indexOf=dir.indexOf(File.separatorChar);
			if(indexOf<=0){
				throw new UtilsException("Errore durante l'identificazione della directory radice presente all'interno dello zip ("+indexOf+")");
			}
			dir = dir.substring(0,indexOf);
			if(dir==null || "".equals(dir)){
				throw new UtilsException("Errore durante l'identificazione della directory radice presente all'interno dello zip ("+dir+")");
			}
			rootDir=dir+File.separatorChar;
			return rootDir;
		}catch(UtilsException ex){
			throw ex;
		}catch(Exception e){
			throw new UtilsException("Errore durante l'identificazione della directory radice presente all'interno dello zip: "+e.getMessage(),e);
		}
	}
	
	public static String getBaseName(String file) throws UtilsException{
		try{
			return (new File(file)).getName();
		}catch(Exception e){
			throw new UtilsException("Errore durante l'identificazione del base name per il file ["+file+"]: "+e.getMessage(),e);
		}
	}
	
	public static String operativeSystemConversion(String entryName){

		char separatorCharWindows = '\\';
		char separatorCharLinux = '/';
		boolean isWindows =  (File.separatorChar == separatorCharWindows);
		
		StringBuilder fixed = new StringBuilder(entryName);
		for( int i = 0; i<fixed.length(); i++){
			
			if(isWindows){
				// se siamo su windows converto tutti i path "linux" in windows mode
				if(fixed.charAt(i) == separatorCharLinux){ 
					fixed.setCharAt(i, File.separatorChar);
				}
			}
			else{
				// se siamo su linux converto tutti i path "windows" in linux mode
				if(fixed.charAt(i) == separatorCharWindows){ 
					fixed.setCharAt(i, File.separatorChar);
				}
			}
			
		}
		
		//System.out.println("Convertito per Windows: "+isWindows);
		
		return fixed.toString();
	}
	
	public static void unzipFile(String zipFile,String dest) throws UtilsException {
		try{
			File destFile = new File(dest);
			if(destFile.exists()==false){
				if(destFile.mkdir()==false){
					throw new Exception("Destinazione ["+dest+"] non esistente e creazione non riuscita");
				}
			}else{
				if(destFile.isDirectory()==false){
					throw new Exception("Destinazione ["+dest+"] non e' una directory");
				}
				if(destFile.canWrite()==false){
					throw new Exception("Destinazione ["+dest+"] non accessibile in scrittura ");
				}
			}

			OutputStream out = null;
			InputStream iZip = null;
			ZipFile zf = null;
			try{
				zf = new ZipFile(zipFile);
				for(Enumeration<?> em = zf.entries(); em.hasMoreElements();){
					ZipEntry ze = (ZipEntry) em.nextElement();
					String targetfile = destFile.getAbsolutePath()+File.separatorChar+ze.getName();

					// Parent
					FileSystemUtilities.mkdirParentDirectory(targetfile);
					
					// File
					if(ze.getName()!=null && ( ze.getName().endsWith("/") || ze.getName().endsWith("\\") ) ){
						
						// Directory
						File fDir = new File(targetfile);
						if(fDir.mkdir()==false){
							try{
								throw new Exception("Creazione directory ["+fDir.getAbsolutePath()+"] per entry ["+ze.getName()+"] non riuscita");
							}finally{
								try{
									if(zf!=null){
										zf.close();
									}
								}catch(Exception eClose){
									// close
								}
							}
						}
				
					}
					else{

						// Creo file
						iZip = zf.getInputStream(ze);			
						out = new FileOutputStream(targetfile);
						byte[] buf = new byte[1024];
						int len;
						while ((len = iZip.read(buf)) > 0) {
							out.write(buf, 0, len);
						}
						iZip.close();
						iZip = null;
						out.flush();
						out.close();
						out = null;
						
					}
				}
			}finally{
				try{
					if(out!=null){
						out.close();
					}
				}catch(Exception eClose){}
				try{
					if(iZip!=null){
						iZip.close();
					}
				}catch(Exception eClose){}
				try{
					if(zf!=null){
						zf.close();
					}
				}catch(Exception eClose){
					// close
				}
			}

		}catch(Exception e){
			throw new UtilsException("Unzip non riuscito: "+e.getMessage(),e);
		}
	}
	
	public static Iterator<ZipEntry> entries(ZipFile zip,boolean ascOrder){
		
		// L'Enumeration ritornato dal metodo standard java.util.zip.ZipFile.entries() 
		// attraversa le entries presenti nello zip nello stesso ordine in cui sono state salvate.
		
		List<String> entryNames = new ArrayList<>();
		Map<String, ZipEntry> map = new HashMap<String, ZipEntry>();
		Enumeration<?> e = zip.entries();
		while(e.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry)e.nextElement();
			String entryName = zipEntry.getName();
			entryNames.add(entryName);
			map.put(entryName, zipEntry);
		}
		
		if(ascOrder){
			java.util.Collections.sort(entryNames);
		}
		else{
			java.util.Collections.sort(entryNames,Collections.reverseOrder());
		}
		
		List<ZipEntry> zipEntries = new ArrayList<ZipEntry>();
		for (String entry : entryNames) {
			zipEntries.add(map.remove(entry));
		}
		return zipEntries.iterator();
	}
	
}
