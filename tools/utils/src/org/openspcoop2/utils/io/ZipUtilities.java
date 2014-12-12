/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2014 Link.it srl (http://link.it). All rights reserved. 
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


package org.openspcoop2.utils.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

	public static String getRootDir(String entry) throws UtilsException{
		try{
			String rootDir = null;
			String dir = entry;
			int indexOf=dir.indexOf(File.separatorChar);
			if(indexOf<=0){
				throw new UtilsException("Errore durante la comprensione della directory radice presente all'interno dello zip ("+indexOf+")");
			}
			dir = dir.substring(0,indexOf);
			if(dir==null || "".equals(dir)){
				throw new UtilsException("Errore durante la comprensione della directory radice presente all'interno dello zip ("+dir+")");
			}
			rootDir=dir+File.separatorChar;
			return rootDir;
		}catch(UtilsException ex){
			throw ex;
		}catch(Exception e){
			throw new UtilsException("Errore durante la comprensione della directory radice presente all'interno dello zip: "+e.getMessage(),e);
		}
	}
	
	public static String getBaseName(String file) throws UtilsException{
		try{
			return (new File(file)).getName();
		}catch(Exception e){
			throw new UtilsException("Errore durante la comprensione del base name per il file ["+file+"]: "+e.getMessage(),e);
		}
	}
	
	public static String operativeSystemConversion(String entryName){

		char separatorCharWindows = '\\';
		char separatorCharLinux = '/';
		boolean isWindows =  (File.separatorChar == separatorCharWindows);
		
		StringBuffer fixed = new StringBuffer(entryName);
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
			ZipFile zf = new ZipFile(zipFile);
			try{
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
							throw new Exception("Creazione directory ["+fDir.getAbsolutePath()+"] per entry ["+ze.getName()+"] non riuscita");
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
				if(out!=null){
					out.close();
				}
				if(iZip!=null){
					iZip.close();
				}
			}

		}catch(Exception e){
			throw new UtilsException("Unzip non riuscito: "+e.getMessage(),e);
		}
	}
	
}
