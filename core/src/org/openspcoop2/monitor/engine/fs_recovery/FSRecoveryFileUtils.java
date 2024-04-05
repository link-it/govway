/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * FSRecoveryFileUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryFileUtils {
	
	private FSRecoveryFileUtils() {}

	public static final String ERROR_SUFFIX = ".error";
	
	public static String getDateFromFilename(String fileName) throws UtilsException {
		String[] split = fileName.split("_");
		if(split.length < 2) {
			throw new UtilsException("Impossibile ricavare la data dal nome file ["+fileName+"]");
		}
		return split[1];
	}
	
	public static String getNewFileNameFromFilename(String fileName) throws UtilsException {
		
		int tentativi = getTentativiFromFilename(fileName);
		
		if(tentativi == 0) {
			return fileName + "_1"+ERROR_SUFFIX;
		} else {
			String substring = fileName.substring(0, fileName.indexOf(ERROR_SUFFIX));
			
			String[] split = substring.split("_");
			if(split==null || split.length <= 0) {
				throw new UtilsException("Impossibile ricavare il nuovo nome file dal nome file ["+fileName+"]");
			}
			StringBuilder sb  = new StringBuilder();
			for(int i =0; i < split.length -1; i++) {
				sb.append(split[i]).append("_");
			}
			int newErrorIndex = tentativi+1;
			sb.append(newErrorIndex).append(ERROR_SUFFIX);
			return sb.toString();
		}
	}
	
	public static int getTentativiFromFilename(String fileName) throws UtilsException {
		if(fileName.endsWith(".xml")) {
			return 0;
		} else {
			if(!fileName.endsWith(ERROR_SUFFIX)){
				throw new UtilsException("Impossibile ricavare il numero di tentativi dal nome file ["+fileName+"]");	
			}
			
			String substring = fileName.substring(0, fileName.indexOf(ERROR_SUFFIX));
			
			String[] split = substring.split("_");
			if(split==null || split.length <= 0) {
				throw new UtilsException("Impossibile ricavare il numero di tentativi dal nome file ["+fileName+"]");
			}
			return Integer.parseInt(split[split.length -1].trim());
		}
	}
	
	public static String renameToDLQ(File directoryDLQ, File file, Throwable e, Logger log) throws UtilsException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintWriter pw = new PrintWriter(baos);){
			e.printStackTrace(pw);
			pw.flush();
			baos.flush();
			return renameToDLQ(directoryDLQ, file, baos.toString(), log);
		} catch(Exception er) {
			throw new UtilsException(e.getMessage(),e);
		}
		
	}
	
	public static String renameToDLQ(File directoryDLQ, File file, String error, Logger log) throws UtilsException {
		
		if(log!=null) {
			// nop
		}
		
		// Se per un file si raggiunge il massimo numero di tentativi, effettuare il rename del file file.renameTo(file2)
		// Spostandolo nella directory DLQ.

		// NOTA: in DLQ estrapolare la data (solo anno mese e giorno) dal nome del file e usarla come nome della directory all'interno di DLQ.
		String data = FSRecoveryFileUtils.getDateFromFilename(file.getName());
		
		File dir = new File(directoryDLQ, data + File.separator);
		
		File newFile = new File(dir, file.getName());
		
		dir.mkdirs();
		if(!file.renameTo(newFile)) {
			// ignore
		}
		
		File readme = new File(dir, file.getName() + ".README");
		try (FileOutputStream fos = new FileOutputStream(readme);){
			fos.write(error.getBytes());
			fos.flush();
		} 
		catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
			
		return newFile.getAbsolutePath();

	}
	
	
	
}
