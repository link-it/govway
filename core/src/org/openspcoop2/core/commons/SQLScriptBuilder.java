/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * SQLScriptBuilder
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SQLScriptBuilder {

	public static void main(String[] args) throws Exception {
		// Metodo utilizzato dal setup antinstaller
		
		String sqlSourceDir = (String) args[0];
		
		String sqlDestDir = (String) args[1];
		
		String modalitaInstallazione = (String) args[2];
		
		boolean splitDDL_DML = true;
		
		String versionePrecedente = null;
		String versioneAttuale = null;
		String tipoDatabase = null;
		if(args.length>3){
			versionePrecedente = (String) args[3];
			versioneAttuale = (String) args[4];
			tipoDatabase = (String) args[5];
		}
		
		// NOTA: Non far stampare niente, viene usato come meccanismo di check per vedere se l'esecuzione e' andata a buon fine
		//System.out.println("Modalita ["+modalitaInstallazione+"]");
		
		if("nuova".equals(modalitaInstallazione)){
			buildSql_NuovaInstallazione(new File(sqlSourceDir),new File(sqlDestDir),splitDDL_DML);
		}
		else if("aggiornamento".equals(modalitaInstallazione)){
			
			//System.out.println("versionePrecedente ["+versionePrecedente+"]");
			//System.out.println("versioneAttuale ["+versioneAttuale+"]");
			//System.out.println("tipoDatabase ["+tipoDatabase+"]");
			
			buildSql_Aggiornamento(new File(sqlSourceDir),new File(sqlDestDir),versionePrecedente,
					versioneAttuale, tipoDatabase);
		}
		else{
			throw new Exception("Modalità installazione ["+modalitaInstallazione+"] sconosciuta");
		}
		
	}
	
	private static void buildSql_NuovaInstallazione(File sqlSourceDir, File sqlDestDir,
			boolean splitDDL_DML) throws Exception {
		
		if(sqlSourceDir.exists()==false){
			throw new Exception("Source dir ["+sqlSourceDir.getAbsolutePath()+"] not exists");
		}
		if(sqlSourceDir.canRead()==false){
			throw new Exception("Source dir ["+sqlSourceDir.getAbsolutePath()+"] cannot read");
		}
		
		if(sqlDestDir.exists()==false){
			throw new Exception("Dest dir ["+sqlDestDir.getAbsolutePath()+"] not exists");
		}
		if(sqlDestDir.canWrite()==false){
			throw new Exception("Dest dir ["+sqlDestDir.getAbsolutePath()+"] cannot write");
		}
		
		int prefix = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		// BASE
		File [] f = sqlSourceDir.listFiles();
		for (int i = 0; i < f.length; i++) {
			if(f[i].isFile()) {
				_createSql(sqlSourceDir, f[i].getName(), 
						sqlDestDir, "GovWay.sql", prefix, bout,
						splitDDL_DML);
			}
		}
		
		File dest = new File(sqlDestDir, "GovWay.sql");
		
		bout.flush();
		bout.close();
		FileSystemUtilities.writeFile(dest, bout.toByteArray());
		
		if(splitDDL_DML){
			
			//System.out.println("Split init ...");
			
			File destInit = new File(sqlDestDir, dest.getName().replace(".sql", "_init.sql"));
			splitFileForDDLtoDML(dest, dest, destInit);
		}
	
	}
	
	private static void buildSql_Aggiornamento(File sqlSourceDir, File sqlDestDir, 
			String precedenteVersione, String versioneAttuale, String tipoDatabase) throws Exception {

		
		if(sqlSourceDir.exists()==false){
			throw new Exception("Source dir ["+sqlSourceDir.getAbsolutePath()+"] not exists");
		}
		if(sqlSourceDir.canRead()==false){
			throw new Exception("Source dir ["+sqlSourceDir.getAbsolutePath()+"] cannot read");
		}

		if(sqlDestDir.exists()==false){
			throw new Exception("Dest dir ["+sqlDestDir.getAbsolutePath()+"] not exists");
		}
		if(sqlDestDir.canWrite()==false){
			throw new Exception("Dest dir ["+sqlDestDir.getAbsolutePath()+"] cannot write");
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
		
		if(tipoDatabase==null){
			throw new Exception("TipoDatabase non fornito");
		}
		TipiDatabase tipiDatabase = TipiDatabase.toEnumConstant(tipoDatabase);
		if(TipiDatabase.DEFAULT.equals(tipiDatabase)) {
			throw new Exception("TipoDatabase fornito ["+tipoDatabase+"] non valido");
		}
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		File infoVersion = null;
		
		while(true){
			
			String actualVersion = productVersion+"."+tmpMajorVersion+"."+tmpMinorVersion;
			String nextVersion = productVersion+"."+tmpMajorVersion+"."+(tmpMinorVersion+1);
			File version = new File(sqlSourceDir,"upgrade_"+actualVersion+"_to_"+nextVersion);
			//System.out.println("CHECK ["+version.getAbsolutePath()+"] ["+version.exists()+"]");
			if(version.exists()){
				if(version.canRead()==false){
					throw new Exception("Source version dir ["+version.getAbsolutePath()+"] cannot read");
				}
				
				File tmp = _createSql_Aggiornamento(version, sqlDestDir, bout, nextVersion, tipoDatabase);
				if(tmp!=null) {
					infoVersion = tmp;
				}
				
				tmpMinorVersion++;
			}
			else{
				// check upgrade to major version +1
				actualVersion = productVersion+"."+tmpMajorVersion+".x";
				nextVersion = productVersion+"."+(tmpMajorVersion+1)+".0";
				version = new File(sqlSourceDir,"upgrade_"+actualVersion+"_to_"+nextVersion);
				//System.out.println("CHECK UPGRADE ["+version.getAbsolutePath()+"] ["+version.exists()+"]");
				if(version.exists()){
					if(version.canRead()==false){
						throw new Exception("Source version dir ["+version.getAbsolutePath()+"] cannot read");
					}
					
					File tmp = _createSql_Aggiornamento(version, sqlDestDir, bout, nextVersion, tipoDatabase);
					if(tmp!=null) {
						infoVersion = tmp;
					}
					
					tmpMajorVersion++;
					tmpMinorVersion=0;
				}
				else{
					break;
				}
			}
			
		}
		
		if(infoVersion!=null) {
			byte[] content = FileSystemUtilities.readBytesFromFile(infoVersion);
			bout.write("\n\n".getBytes());
			bout.write(content);
		}
		
		bout.flush();
		bout.close();
		FileSystemUtilities.writeFile(new File(sqlDestDir, "GovWay_upgrade_"+versioneAttuale+".sql"), bout.toByteArray());
		
	}
	
	private static File _createSql_Aggiornamento(File sqlVersionSourceDir, File sqlDestDir, ByteArrayOutputStream bout , String nextVersion, String tipoDatabase) throws Exception {
						
		File sqlVersionSourceDirDatabase = new File(sqlVersionSourceDir, tipoDatabase);
		
		File[] files = sqlVersionSourceDirDatabase.listFiles();
		if(files!=null && files.length>0) {
			
			if(bout!=null){
				if(bout.size()>0){
					bout.write("\n\n".getBytes());
				}
				bout.write(("-- Upgrade to "+nextVersion).getBytes());
			}
			
			Arrays.sort(files); // sono ordinati per data
			for(File upgradeFile : files) {
				_createSqlAggiornamento(upgradeFile, bout);	
			}
			
		}
		
		File sqlVersionSourceDirInfoVersioneUpgrade = new File(sqlVersionSourceDir, "info-patch.sql");
		if(sqlVersionSourceDirInfoVersioneUpgrade.exists()) {
			return sqlVersionSourceDirInfoVersioneUpgrade;
		}
		
		return null;
	}
	
	
	private static void _createSql(File sqlSourceDir,String sourceFile,File sqlDestDir, String destFile,
			int prefix,ByteArrayOutputStream bout,
			boolean splitDDL_DML) throws Exception{
	
		File src = new File(sqlSourceDir, sourceFile);
		if(bout!=null){
			byte[] b = FileSystemUtilities.readBytesFromFile(src);
			if(bout.size()>0){
				bout.write("\n\n".getBytes());
			}
			bout.write(b);
		}
		else{
			File dest = new File(sqlDestDir, parsePrefix(prefix)+destFile);
			FileSystemUtilities.copy(src, dest);
			if(splitDDL_DML){
				File destInit = new File(sqlDestDir, dest.getName().replace(".sql", "_init.sql"));
				splitFileForDDLtoDML(dest, dest, destInit);
			}
		}
	}
	
	private static void _createSqlAggiornamento(File upgradeFile,ByteArrayOutputStream bout) throws Exception{
		byte[] b = FileSystemUtilities.readBytesFromFile(upgradeFile);
		if(bout.size()>0){
			bout.write("\n\n".getBytes());
		}
		bout.write(b);
	}
	
	private static String parsePrefix(int prefix){
		if(prefix<10){
			return "0"+prefix+"_";
		}
		else{
			return prefix+"_";
		}
	}
	
	private static void splitFileForDDLtoDML(File sqlFile,File sqlFileDest,File sqlFileInitDest) throws Exception{
		
		boolean dmlOpen = false;
		
		ByteArrayOutputStream boutDDL = new ByteArrayOutputStream();
		ByteArrayOutputStream boutDML = new ByteArrayOutputStream();
		
		BufferedReader br = new BufferedReader(new FileReader(sqlFile));
	    String line;
	    while ((line = br.readLine()) != null) {
	       // process the line.
	    	
	    	if(dmlOpen){
	    		boutDML.write(line.getBytes());
	    		boutDML.write("\n".getBytes());
	    		if(line.contains(";")){
	    			dmlOpen = false; // finish
	    		}
	    	}
	    	else{
		    	if(isDML(line)){
		    		dmlOpen = true; // start
		    		boutDML.write(line.getBytes());
		    		boutDML.write("\n".getBytes());
		    		if(line.contains(";")){
		    			dmlOpen = false; // finish (in una unica riga)
		    		}
		    	}
		    	else{
		    		boutDDL.write(line.getBytes());
		    		boutDDL.write("\n".getBytes());
		    	}
	    	}
	    	
	    }
	    br.close();
	    
	    //System.out.println("DDL["+boutDDL.size()+"] DML["+boutDML.size()+"]");
	    
	    if(boutDDL.size()>0){
	    	FileSystemUtilities.writeFile(sqlFileDest, boutDDL.toByteArray());
	    }
	    if(boutDML.size()>0){
	    	FileSystemUtilities.writeFile(sqlFileInitDest, boutDML.toByteArray());
	    }
		
	}
	private static boolean isDML(String line){
		
		String tmp = new String(line);
		tmp = tmp.trim().toLowerCase();
		
		if(tmp.startsWith("insert ")){
			
			// check hsql, es: INSERT INTO db_info_init_seq VALUES (NEXT VALUE FOR seq_db_info);
			if(tmp.contains("_init_seq ") && tmp.contains("next value ") ){
				return false;

			}
			
			// insert on tracce_ext_protocol_info
			if(tmp.contains("insert on ")){
				return false;
			}
			
			return true;
		} 
		else if(tmp.startsWith("update ")){
			
			return true;
			
		}
		else if(tmp.startsWith("delete ")){
			
			// ON DELETE CASCADE
			if(tmp.contains("on delete cascade")){
				return false;
			}
			
			return true;
			
		}
		return false;
		
	}
	
}
