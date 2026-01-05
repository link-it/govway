/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.monitor.engine.constants.Costanti;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
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
	private static synchronized void initialize() throws CoreException{
		if(staticInstance==null){
			staticInstance = new FileSystemSerializer();
		}
	}
	public static FileSystemSerializer getInstance() throws CoreException{
		if(staticInstance==null){
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (FileSystemSerializer.class) {
				initialize();
			}
		}
		return staticInstance;
	}
	
	
	private File directory = null; 
	
	private FileSystemSerializer() throws CoreException{
		
		this.directory = OpenSPCoop2Properties.getInstance().getFileSystemRecoveryRepository();
		String prefix = "Directory ["+this.directory.getAbsolutePath()+"] ";
		if(!this.directory.exists() &&
			!this.directory.mkdir()){
			throw new CoreException(prefix+"non esistente e creazione non riuscita");
		}
		if(!this.directory.canRead()){
			throw new CoreException(prefix+"non accessibile in lettura");
		}
		if(!this.directory.canWrite()){
			throw new CoreException(prefix+"non accessibile in scrittura");
		}
	}
	
	private static final String FORMAT_NEW = "yyyyMMdd_HHmmssSSS"; // compatibile con windows S.O.
	@SuppressWarnings("unused")
	private static final String FORMAT_OLD = "yyyy-MM-dd_HH:mm:ss.SSS";
	
	public void registraEvento(byte[] oggettSerializzato, Date date) throws CoreException, UtilsException{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_EVENTI, date, getDirEventi());
	}
	public File getDirEventi(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_EVENTI);
	}
		
	public void registraTransazione(byte[] oggettSerializzato, Date date) throws CoreException, UtilsException{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_TRANSAZIONE, date, getDirTransazioni());
	}
	public File getDirTransazioni(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRANSAZIONE);
	}
	
	public void registraDiagnostico(byte[] oggettSerializzato, Date date) throws CoreException, UtilsException{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_DIAGNOSTICO, date,getDirDiagnostici());
	}
	public File getDirDiagnostici(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DIAGNOSTICO);
	}
	
	public void registraTraccia(byte[] oggettSerializzato, Date date) throws CoreException, UtilsException{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_TRACCIA, date,getDirTracce());
	}
	public File getDirTracce(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRACCIA);
	}
	
	public void registraDump(byte[] oggettSerializzato, Date date) throws CoreException, UtilsException{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_DUMP, date,getDirDump());
	}
	public File getDirDump(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_DUMP);
	}
	
	public void registraTransazioneApplicativoServer(byte[] oggettSerializzato, Date date) throws CoreException, UtilsException{
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_TRANSAZIONE_APPLICATIVO_SERVER, date,getDirTransazioneApplicativoServer());
	}
	public File getDirTransazioneApplicativoServer(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRANSAZIONE_APPLICATIVO_SERVER);
	}
	
	public void registraTransazioneApplicativoServerConsegnaTerminata(byte[] oggettSerializzato, Date date) throws CoreException, UtilsException {
		this.registra(oggettSerializzato, Costanti.PREFIX_FILE_SYSTEM_REPOSITORY_TRANSAZIONE_APPLICATIVO_SERVER_CONSEGNA_TERMINATA, date,getDirTransazioneApplicativoServerConsegnaTerminata());
	}
	public File getDirTransazioneApplicativoServerConsegnaTerminata(){
		return new File(this.directory,Costanti.DIRECTORY_FILE_SYSTEM_REPOSITORY_TRANSAZIONE_APPLICATIVO_SERVER_CONSEGNA_TERMINATA);
	}
	
	private void registra(byte[] oggettSerializzato, String prefix, Date date, File dir) throws CoreException, UtilsException{
		
		if(!dir.exists()){
			this.mkdir(dir);
		}
		
		SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(FORMAT_NEW);
		
		/**long uniqueId = IDUtilities.getUniqueSerialNumber("FileSystemSerializer");*/
		// Fix: per renderlo univoco indipendentemente dalla macchina
		String uniqueId = null;
		try {
			uniqueId = UniqueIdentifierManager.newUniqueIdentifier().getAsString().replace("-", "_");
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		String nomeFile = prefix+"_"+dateformat.format(date)+"__"+uniqueId+".xml";
		File f = new File(dir, nomeFile);
		FileSystemUtilities.writeFile(f, oggettSerializzato);
		
	}
	
	private void mkdir(File dir) throws CoreException{
		if(!dir.exists()){
			mkdirEngine(dir);
		}
	}
	private synchronized void mkdirEngine(File dir) throws CoreException{
		if(!dir.exists() &&
			!dir.mkdir()){
			throw new CoreException("Directory ["+this.directory.getAbsolutePath()+"] non esistente e creazione non riuscita");
		}
	}
}
