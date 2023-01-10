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

package org.openspcoop2.core.commons;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * PropertiesScriptBuilder
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PropertiesScriptBuilder {

	public static void main(String[] args) throws Exception {
		// Metodo utilizzato dal setup antinstaller
		
		String sourceDir = (String) args[0];
		
		String destDir = (String) args[1];
		
		String distDir = (String) args[2];
		
		String versionePrecedente = null;
		String versioneAttuale = null;
		if(args.length>3){
			versionePrecedente = (String) args[3];
			versioneAttuale = (String) args[4];
		}

		// NOTA: Non far stampare niente, viene usato come meccanismo di check per vedere se l'esecuzione e' andata a buon fine

		//System.out.println("versionePrecedente ["+versionePrecedente+"]");
		//System.out.println("versioneAttuale ["+versioneAttuale+"]");
		//System.out.println("tipoDatabase ["+tipoDatabase+"]");

		
		
		build_Aggiornamento(new File(sourceDir),new File(destDir), new File(distDir),
				versionePrecedente, versioneAttuale);

	}
		
	private static void build_Aggiornamento(File sourceDir, File destDir,  File distDir,
			String precedenteVersione, String versioneAttuale) throws Exception {

		
		if(sourceDir.exists()==false){
			throw new Exception("Source dir ["+sourceDir.getAbsolutePath()+"] not exists");
		}
		if(sourceDir.canRead()==false){
			throw new Exception("Source dir ["+sourceDir.getAbsolutePath()+"] cannot read");
		}

		if(destDir.exists()==false){
			throw new Exception("Dest dir ["+destDir.getAbsolutePath()+"] not exists");
		}
		if(destDir.canWrite()==false){
			throw new Exception("Dest dir ["+destDir.getAbsolutePath()+"] cannot write");
		}
		
		if(distDir.exists()==false){
			throw new Exception("Dest dir ["+destDir.getAbsolutePath()+"] not exists");
		}
		if(distDir.canWrite()==false){
			throw new Exception("Dest dir ["+destDir.getAbsolutePath()+"] cannot write");
		}
		
		if(precedenteVersione==null){
			throw new Exception("Precedente versione non fornita");
		}
		if(precedenteVersione.contains(".")==false){
			throw new Exception("Precedente versione in un formato non corretto ["+precedenteVersione+"] ('.' not found)");
		}
		int indexOfFirstPoint = precedenteVersione.indexOf(".");
		if(indexOfFirstPoint<=0){
			throw new Exception("Precedente versione in un formato non corretto ["+precedenteVersione+"] ('.' not found with index)");
		}
		String productVersionString = precedenteVersione.substring(0, indexOfFirstPoint);
		int productVersion = -1;
		try{
			productVersion = Integer.parseInt(productVersionString);
		}catch(Exception e){
			throw new Exception("Precedente versione in un formato non corretto ["+precedenteVersione+"] (productVersion:"+productVersionString+"): "+e.getMessage(),e);
		}
		int indexOfSecondPoint = precedenteVersione.indexOf(".",indexOfFirstPoint+1);
		if(indexOfSecondPoint<=0 || indexOfSecondPoint<=indexOfFirstPoint){
			throw new Exception("Precedente versione in un formato non corretto ["+precedenteVersione+"] (second '.' not found)");
		}
		String majorVersionString = precedenteVersione.substring(indexOfFirstPoint+1, indexOfSecondPoint);
		int majorVersion = -1;
		try{
			majorVersion = Integer.parseInt(majorVersionString);
		}catch(Exception e){
			throw new Exception("Precedente versione in un formato non corretto ["+precedenteVersione+"] (majorVersion:"+majorVersionString+"): "+e.getMessage(),e);
		}
		if(precedenteVersione.length()<=indexOfSecondPoint){
			throw new Exception("Precedente versione in un formato non corretto ["+precedenteVersione+"] (length)");
		}
		String minorVersionString = precedenteVersione.substring(indexOfSecondPoint+1,precedenteVersione.length());
		int minorVersion = -1;
		try{
			minorVersion = Integer.parseInt(minorVersionString);
		}catch(Exception e){
			//throw new Exception("Precedente versione in un formato non corretto ["+precedenteVersione+"] (minorVersion:"+minorVersionString+"): "+e.getMessage(),e);
			// Potrebbe essere una BUILD VERSION
			if(minorVersionString.contains("_")){
				String newMinor = minorVersionString.split("_")[0];
				try{
					minorVersion = Integer.parseInt(newMinor);
				}catch(Exception eInternal){
					throw new Exception("Precedente versione in un formato non corretto ["+precedenteVersione+"] (minorVersion:"+minorVersionString+" minorVersionBuildNumber:"+newMinor+"): "+eInternal.getMessage(),eInternal);
				}
			}
			else{
				throw new Exception("Precedente versione in un formato non corretto ["+precedenteVersione+"] (minorVersion:"+minorVersionString+"): "+e.getMessage(),e);
			}
			
		}
		//System.out.println(productVersion+"."+majorVersion+"."+minorVersion);
		
		
		int tmpMajorVersion = majorVersion;
		int tmpMinorVersion = minorVersion;
		
		Map<String, Boolean> configCreateFileFromDist = new HashMap<String, Boolean>();
		Map<String, ByteArrayOutputStream> configCreateFileDiff = new HashMap<>();
		
		
		while(true){
			
			String actualVersion = productVersion+"."+tmpMajorVersion+"."+tmpMinorVersion;
			String nextVersion = productVersion+"."+tmpMajorVersion+"."+(tmpMinorVersion+1);
			File version = new File(sourceDir,"upgrade_"+actualVersion+"_to_"+nextVersion);
			//System.out.println("CHECK ["+version.getAbsolutePath()+"] ["+version.exists()+"]");
			if(version.exists()){
				if(version.canRead()==false){
					throw new Exception("Source version dir ["+version.getAbsolutePath()+"] cannot read");
				}
				
				_create_Aggiornamento(version, destDir,
						configCreateFileFromDist, configCreateFileDiff,
						nextVersion);
				
				tmpMinorVersion++;
			}
			else{
				// check upgrade to major version +1
				actualVersion = productVersion+"."+tmpMajorVersion+".x";
				nextVersion = productVersion+"."+(tmpMajorVersion+1)+".0";
				version = new File(sourceDir,"upgrade_"+actualVersion+"_to_"+nextVersion);
				//System.out.println("CHECK UPGRADE ["+version.getAbsolutePath()+"] ["+version.exists()+"]");
				if(version.exists()){
					if(version.canRead()==false){
						throw new Exception("Source version dir ["+version.getAbsolutePath()+"] cannot read");
					}
					
					_create_Aggiornamento(version, destDir, 
							configCreateFileFromDist, configCreateFileDiff,
							nextVersion);
					
					tmpMajorVersion++;
					tmpMinorVersion=0;
				}
				else{
					break;
				}
			}
			
		}
		
		if(configCreateFileFromDist.size()>0) {
			Iterator<String> it = configCreateFileFromDist.keySet().iterator();
			while (it.hasNext()) {
				String id = (String) it.next();
				String fileName = id + ".properties";
				
				File[] files = distDir.listFiles();
				if(files!=null && files.length>0) {
					// Prima esamino i file properties interi poi i diff
					for(File distFile : files) {
						if(distFile.getName().equals(fileName)) {
							File fOut = new File(destDir, fileName);
							FileSystemUtilities.copy(distFile, fOut);
							break;
						}
					}
				}
			}
		}
		
		if(configCreateFileDiff.size()>0) {
			Iterator<String> it = configCreateFileDiff.keySet().iterator();
			while (it.hasNext()) {
				String id = (String) it.next();
				String fileName = id + ".diff";
				
				File fOut = new File(destDir, fileName);
				ByteArrayOutputStream bout = configCreateFileDiff.get(id);
				bout.flush();
				bout.close();
				FileSystemUtilities.writeFile(fOut, bout.toByteArray());

			}
		}
		
	}
	
	private static void _create_Aggiornamento(File sourceDir, File destDir, 
			Map<String, Boolean> configCreateFileFromDist , Map<String, ByteArrayOutputStream> configCreateFileDiff ,
			String nextVersion) throws Exception {
						
		//System.out.println("CHECK.... source ["+sourceDir.getAbsolutePath()+"]");
		
		File[] files = sourceDir.listFiles();
		if(files!=null && files.length>0) {
			
			// Prima esamino i file properties interi poi i diff
			for(File upgradeDir : files) {
				if(upgradeDir.isDirectory() && (
						upgradeDir.getName().equals("core") || upgradeDir.getName().equals("protocolli")
						)) {
					
					File[] fileChilds = upgradeDir.listFiles();
					if(fileChilds!=null && fileChilds.length>0) {
						
						Arrays.sort(fileChilds); // sono ordinati per data
						
						for(File upgradeFile : fileChilds) {
							if(upgradeFile.getName().endsWith(".properties")) {
								String key = upgradeFile.getName().substring(0, upgradeFile.getName().indexOf(".properties"));
								if(configCreateFileFromDist.containsKey(key)==false) {
									//System.out.println("ADDD PROPERTIES ["+key+"]");
									configCreateFileFromDist.put(key, true);
								}
							}
						}
					}
					
				}
				
			}
			
			Arrays.sort(files); // sono ordinati per data
			for(File upgradeDir : files) {
				
				if(upgradeDir.isDirectory() && (
						upgradeDir.getName().equals("core") || upgradeDir.getName().equals("protocolli")
						)) {
					
					File[] fileChilds = upgradeDir.listFiles();
					if(fileChilds!=null && fileChilds.length>0) {

						Arrays.sort(fileChilds); // sono ordinati per data
						
						for(File diffFile : fileChilds) {
				
							if(diffFile.getName().endsWith(".diff")) {
								
								String data= ".YYYY-MM-DD.diff";
								//System.out.println("DIFF NAME ["+diffFile.getName()+"]");
								String key = diffFile.getName().substring(0, (diffFile.getName().length()-data.length()));
								//System.out.println("DIFF KEY ["+key+"]");
								if(configCreateFileFromDist.containsKey(key)==false) {
									
									//System.out.println("ADDD DIFF");
									
									ByteArrayOutputStream bout = null;
									if(configCreateFileDiff.containsKey(key)) {
										bout = configCreateFileDiff.get(key);
									}
									else {
										bout = new ByteArrayOutputStream();
										configCreateFileDiff.put(key, bout);
									}
									
									_createAggiornamento(diffFile, bout);	
								}
							}
							
						}
					}
				}
			}
			
		}

	}
	
	private static void _createAggiornamento(File diffFile, ByteArrayOutputStream bout) throws Exception{
		
		byte[] b = FileSystemUtilities.readBytesFromFile(diffFile);
		if(bout.size()>0){
			bout.write("\n\n".getBytes());
		}
		bout.write(b);
	}
	
}
