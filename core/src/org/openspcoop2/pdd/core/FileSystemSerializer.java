package org.openspcoop2.pdd.core;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.monitor.engine.constants.Costanti;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.utils.id.IDUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;

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
	
	private void registra(byte[] oggettSerializzato, String prefix, Date date, File dir) throws Exception{
		
		if(dir.exists()==false){
			this.mkdir(dir);
		}
		
		SimpleDateFormat dateformat = new SimpleDateFormat (formatNew); // SimpleDateFormat non e' thread-safe
		
		String nomeFile = prefix+"_"+dateformat.format(date)+"_"+IDUtilities.getUniqueSerialNumber()+".xml";
		File f = new File(dir, nomeFile);
		FileSystemUtilities.writeFile(f, oggettSerializzato);
		
	}
	
	private synchronized void mkdir(File dir) throws Exception{
		if(dir.exists()==false){
			if(dir.mkdir()==false){
				throw new Exception("Directory ["+this.directory.getAbsolutePath()+"] non esistente e creazione non riuscita");
			}
		}
	}
}
