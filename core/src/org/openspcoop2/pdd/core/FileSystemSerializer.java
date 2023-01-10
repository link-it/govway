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

package org.openspcoop2.pdd.core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.monitor.engine.constants.Costanti;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**     
 * FileSystemSerializer
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileSystemSerializer {

	private static FileSystemSerializer staticInstance = null;
	private static synchronized void initialize() throws Exception{
		if(staticInstance==null){
			staticInstance = new FileSystemSerializer();
		}
	}
	public static FileSystemSerializer getInstance() throws Exception{
		if(staticInstance==null){
			initialize();
		}
		return staticInstance;
	}
	
	
	private File directory = null; 
	
	public FileSystemSerializer() throws Exception{
		
		this.directory = OpenSPCoop2Properties.getInstance().getFileSystemRecovery_repository();
		if(this.directory.exists()==false){
			if(this.directory.mkdir()==false){
				throw new Exception("Directory ["+this.directory.getAbsolutePath()+"] non esistente e creazione non riuscita");
			}
		}
		if(this.directory.canRead()==false){
			throw new Exception("Directory ["+this.directory.getAbsolutePath()+"] non accessibile in lettura");
		}
		if(this.directory.canWrite()==false){
			throw new Exception("Directory ["+this.directory.getAbsolutePath()+"] non accessibile in scrittura");
		}
	}
	
	private static final String formatNew = "yyyyMMdd_HHmmssSSS"; // compatibile con windows S.O.
	@SuppressWarnings("unused")
	private static final String formatOld = "yyyy-MM-dd_HH:mm:ss.SSS";
	
	public void registraEvento(byte[] oggettSerializzato, Date date) throws Exception{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_EVENTI, date, getDirEventi());
	}
	public File getDirEventi(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_EVENTI);
	}
		
	public void registraTransazione(byte[] oggettSerializzato, Date date) throws Exception{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_TRANSAZIONE, date, getDirTransazioni());
	}
	public File getDirTransazioni(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRANSAZIONE);
	}
	
	public void registraDiagnostico(byte[] oggettSerializzato, Date date) throws Exception{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_DIAGNOSTICO, date,getDirDiagnostici());
	}
	public File getDirDiagnostici(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DIAGNOSTICO);
	}
	
	public void registraTraccia(byte[] oggettSerializzato, Date date) throws Exception{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_TRACCIA, date,getDirTracce());
	}
	public File getDirTracce(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRACCIA);
	}
	
	public void registraDump(byte[] oggettSerializzato, Date date) throws Exception{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_DUMP, date,getDirDump());
	}
	public File getDirDump(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DUMP);
	}
	
	public void registraTransazioneApplicativoServer(byte[] oggettSerializzato, Date date) throws Exception{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_TRANSAZIONE_APPLICATIVO_SERVER, date,getDirTransazioneApplicativoServer());
	}
	public File getDirTransazioneApplicativoServer(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRANSAZIONE_APPLICATIVO_SERVER);
	}
	
	public void registraTransazioneApplicativoServerConsegnaTerminata(byte[] oggettSerializzato, Date date) throws Exception{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_TRANSAZIONE_APPLICATIVO_SERVER_CONSEGNA_TERMINATA, date,getDirTransazioneApplicativoServerConsegnaTerminata());
	}
	public File getDirTransazioneApplicativoServerConsegnaTerminata(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRANSAZIONE_APPLICATIVO_SERVER_CONSEGNA_TERMINATA);
	}
	
	private void registra(byte[] oggettSerializzato, String prefix, Date date, File dir) throws Exception{
		
		if(dir.exists()==false){
			this.mkdir(dir);
		}
		
		SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(formatNew);
		
		String nomeFile = prefix+"_"+dateformat.format(date)+"_"+IDUtilities.getUniqueSerialNumber("FileSystemSerializer")+".xml";
		File f = new File(dir, nomeFile);
		FileSystemUtilities.writeFile(f, oggettSerializzato);
		
	}
	
	private void mkdir(File dir) throws Exception{
		if(dir.exists()==false){
			_mkdir(dir);
		}
	}
	private synchronized void _mkdir(File dir) throws Exception{
		if(dir.exists()==false){
			if(dir.mkdir()==false){
				throw new Exception("Directory ["+this.directory.getAbsolutePath()+"] non esistente e creazione non riuscita");
			}
		}
	}
}
