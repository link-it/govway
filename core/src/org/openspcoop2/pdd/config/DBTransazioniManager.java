package org.openspcoop2.pdd.config;

import java.sql.Connection;

import javax.sql.DataSource;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.UtilsAlreadyExistsException;
import org.openspcoop2.utils.datasource.DataSourceFactory;
import org.openspcoop2.utils.datasource.DataSourceParams;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.slf4j.Logger;

public class DBTransazioniManager {

	private static DBTransazioniManager staticInstanceDBTransazioniManager;
	public static synchronized void init(DBManager dbManagerRuntimePdD,Logger alog,String tipoDatabase) throws Exception {
		if(staticInstanceDBTransazioniManager==null) {
			staticInstanceDBTransazioniManager = new DBTransazioniManager(dbManagerRuntimePdD, alog, tipoDatabase);
		}
	}
	public static synchronized void init(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX) throws Exception {
		if(staticInstanceDBTransazioniManager==null) {
			staticInstanceDBTransazioniManager = new DBTransazioniManager(nomeDataSource, context, alog, tipoDatabase, useOp2UtilsDatasource, bindJMX);
		}
	}
	public static DBTransazioniManager getInstance() {
		return staticInstanceDBTransazioniManager;
	}
	
	
	
	
	private String tipoDatabase;
	public String getTipoDatabase() {
		return this.tipoDatabase;
	}

	private Logger log;
	
	private DBManager dbManagerRuntimePdD;
	private DataSource datasourceTransazioni;
	
	public DBTransazioniManager(DBManager dbManagerRuntimePdD,Logger alog,String tipoDatabase) throws Exception {
		this.dbManagerRuntimePdD = dbManagerRuntimePdD;
		this.log = alog;
		this.tipoDatabase = tipoDatabase;
	}
	public DBTransazioniManager(String nomeDataSource, java.util.Properties context,Logger alog,String tipoDatabase, 
			boolean useOp2UtilsDatasource, boolean bindJMX) throws Exception {
		try {
			this.log = alog;
			this.tipoDatabase = tipoDatabase;
			
			if(useOp2UtilsDatasource){
				DataSourceParams dsParams = Costanti.getDataSourceParamsPdD(bindJMX, tipoDatabase);
				try{
					this.datasourceTransazioni = DataSourceFactory.newInstance(nomeDataSource, context, dsParams);
				}catch(UtilsAlreadyExistsException exists){
					this.datasourceTransazioni = DataSourceFactory.getInstance(nomeDataSource); 
					if(this.datasourceTransazioni==null){
						throw new Exception("Lookup datasource non riuscita ("+exists.getMessage()+")",exists);
					}
				}
			}
			else{
				GestoreJNDI gestoreJNDI = new GestoreJNDI(context);
				this.datasourceTransazioni = (DataSource) gestoreJNDI.lookup(nomeDataSource);
			}
		} catch (Exception ne) {
			this.log.error("Impossibile instanziare il manager: " + ne.getMessage(),ne);
			throw ne;
		}
	}
	
	
	public Resource getResource(IDSoggetto idPDD,String modulo,String idTransazione) throws Exception {
		if(this.dbManagerRuntimePdD!=null) {
			return this.dbManagerRuntimePdD.getResource(idPDD, modulo, idTransazione);
		}
		else {
			try {
				StringBuffer bf = new StringBuffer();
				if(idPDD!=null) {
					bf.append(idPDD.toString());
					if(modulo!=null) {
						bf.append(".");
					}
				}
				if(modulo!=null) {
					bf.append(modulo);
				}
				if(bf.length()<=0) {
					bf.append("DBTransazioniManager");
				}
				return DBManager.buildResource(this.getConnectionFromDatasource(bf.toString(), idTransazione),
						idPDD, modulo, idTransazione);								
			}
			catch(Exception e) {
				this.log.error("Errore durante l'ottenimento di una connessione: "+e.getMessage(),e);
				throw e;
			}
		}
	}
	
	private Connection getConnectionFromDatasource(String methodName, String idTransazione) throws Exception{
		if(this.datasourceTransazioni instanceof org.openspcoop2.utils.datasource.DataSource){
			return ((org.openspcoop2.utils.datasource.DataSource)this.datasourceTransazioni).getWrappedConnection(idTransazione, "DBTransazioniManager."+methodName);
		}
		else{
			return this.datasourceTransazioni.getConnection();
		}
	}
	
	public void releaseResource(IDSoggetto idPDD,String modulo,Resource resource){
		try {
			if(resource!=null){
				
				if(resource.getResource()!=null){
					Connection connectionDB = (Connection) resource.getResource();
					if(connectionDB != null && (connectionDB.isClosed()==false)){
						connectionDB.close();
					}
				}				
			}

		}
		catch(Exception e) {
			this.log.error("Errore durante il rilascio di una risorsa: "+e.getMessage(),e);
		}
	}
}
