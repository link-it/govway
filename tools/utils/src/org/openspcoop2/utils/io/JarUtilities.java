/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;


/**
 * JarUtilities
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JarUtilities {

	public static boolean isJar(File file,boolean checkExtension){
		try{	
			JarFile jar = JarUtilities.getJar(file, checkExtension);
			return jar!=null;			
		}catch (Exception e){
			return false;
		}
	}
	
	public static JarFile getJar(File file,boolean checkExtension) throws UtilsException{
		try{
			
			if(checkExtension){
				if(file.getName().endsWith(".jar")==false){
					throw new Exception("Estensione del file diversa da quella attesa");
				}
			}
			JarURLConnection conn = JarUtilities.getConnection(file, null);
			return conn.getJarFile();
						
		}catch (Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static Manifest getManifest(File file) throws UtilsException{
		try{
			JarURLConnection conn = JarUtilities.getConnection(file, null);
			return conn.getManifest();
		}catch (Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] getEntry(File jarFile,String jarEntry) throws UtilsException{
		
		InputStream is = null;
		try{
			
			JarURLConnection conn = JarUtilities.getConnection(jarFile, jarEntry);
			JarFile jar = conn.getJarFile();
			JarEntry entry = conn.getJarEntry();
			if(entry==null){
				return null;
			}
			
			is = jar.getInputStream(entry);
			byte [] buffer = Utilities.getAsByteArray(is);
			return buffer;
			
		}catch (Exception e){
			throw new UtilsException(e.getMessage(),e);
		}finally{
			try{
				is.close();
			}catch(Exception eclose){}
		}
	}
	
	private static JarURLConnection getConnection(File jarFile,String jarEntry) throws MalformedURLException, IOException{
		URL archivio = new URL ("file://" + jarFile.getCanonicalPath ());
		String url = "jar:" + archivio.toExternalForm () + "!/";
		if(jarEntry!=null){
			url = url + jarEntry;
		}
		archivio = new URL (url);
		return (JarURLConnection) archivio.openConnection ();
	}
	
}
