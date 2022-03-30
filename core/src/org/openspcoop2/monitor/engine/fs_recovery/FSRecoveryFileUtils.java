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
package org.openspcoop2.monitor.engine.fs_recovery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;

/**
 * FSRecoveryFileUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryFileUtils {

	public static String getDateFromFilename(String fileName) throws Exception {
		String[] split = fileName.split("_");
		if(split.length < 2) {
			throw new Exception("Impossibile ricavare la data dal nome file ["+fileName+"]");
		}
		return split[1];
	}
	
	public static String getNewFileNameFromFilename(String fileName) throws Exception {
		
		int tentativi = getTentativiFromFilename(fileName);
		
		if(tentativi == 0) {
			return fileName + "_1.error";
		} else {
			String substring = fileName.substring(0, fileName.indexOf(".error"));
			
			String[] split = substring.split("_");
			if(split.length < 0) {
				throw new Exception("Impossibile ricavare il nuovo nome file dal nome file ["+fileName+"]");
			}
			StringBuilder sb  = new StringBuilder();
			for(int i =0; i < split.length -1; i++) {
				sb.append(split[i]).append("_");
			}
			int newErrorIndex = tentativi+1;
			sb.append(newErrorIndex).append(".error");
			return sb.toString();
		}
	}
	
	public static int getTentativiFromFilename(String fileName) throws Exception {
		if(fileName.endsWith(".xml")) {
			return 0;
		} else {
			if(!fileName.endsWith(".error")){
				throw new Exception("Impossibile ricavare il numero di tentativi dal nome file ["+fileName+"]");	
			}
			
			String substring = fileName.substring(0, fileName.indexOf(".error"));
			
			String[] split = substring.split("_");
			if(split.length < 0) {
				throw new Exception("Impossibile ricavare il numero di tentativi dal nome file ["+fileName+"]");
			}
			return Integer.parseInt(split[split.length -1].trim());
		}
	}
	
	public static String renameToDLQ(File directoryDLQ, File file, Exception e, Logger log) throws Exception {
		ByteArrayOutputStream baos = null;
		PrintWriter pw = null;
		try{
			baos = new ByteArrayOutputStream();
			pw = new PrintWriter(baos);
			e.printStackTrace(pw);
			pw.flush();
			pw.close();
			return renameToDLQ(directoryDLQ, file, baos.toString(), log);
		} finally {
			if(pw != null) {
				try {pw.flush();} catch(Exception ex){}
				try {pw.close();} catch(Exception ex){}
			}
			if(baos != null){
				try {baos.flush();} catch(Exception ex){}
				try {baos.close();} catch(Exception ex){}
			}
		}
		
		
	}
	
	public static String renameToDLQ(File directoryDLQ, File file, String error, Logger log) throws Exception {
		// Se per un file si raggiunge il massimo numero di tentativi, effettuare il rename del file file.renameTo(file2)
		// Spostandolo nella directory DLQ.

		// NOTA: in DLQ estrapolare la data (solo anno mese e giorno) dal nome del file e usarla come nome della directory all'interno di DLQ.
		String data = FSRecoveryFileUtils.getDateFromFilename(file.getName());
		
		File dir = new File(directoryDLQ, data + File.separator);
		
		File newFile = new File(dir, file.getName());
		
		dir.mkdirs();
		file.renameTo(newFile);
		
		FileOutputStream fos = null;
		
		try {
			File readme = new File(dir, file.getName() + ".README");
			fos = new FileOutputStream(readme);
			fos.write(error.getBytes());
			fos.flush();
			fos.close();
		} finally {
			if(fos != null) {
				try {fos.flush();} catch(Exception e){}
				try {fos.close();} catch(Exception e){}
			}
		}
		
		
		return newFile.getAbsolutePath();

	}
	
	
	
}
