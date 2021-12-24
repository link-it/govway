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
package org.openspcoop2.pdd.core.handlers.notifier.engine;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCDumpMessaggioService;
import org.openspcoop2.generic_project.expression.impl.sql.ISQLFieldConverter;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**     
 * NotifierDump
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierDump {

	// STATIC

	private static NotifierDump staticInstance = null;
	private static synchronized void initialize() throws Exception{
		if(staticInstance==null){
			try{

				OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
				
				String tipoDatabase = op2Properties.getDatabaseType();
				if(tipoDatabase==null){
					throw new Exception("Tipo Database non definito");
				}

				boolean debug = op2Properties.isTransazioniDebug();
				Logger daoFactoryLogger = null;
				DAOFactory daoFactory = null;
				ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = null;
				try{
					DAOFactoryProperties daoFactoryProperties = null;
					daoFactoryLogger = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSql(debug);
					daoFactory = DAOFactory.getInstance(daoFactoryLogger);
					daoFactoryProperties = DAOFactoryProperties.getInstance(daoFactoryLogger);
					daoFactoryServiceManagerPropertiesTransazioni = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
					daoFactoryServiceManagerPropertiesTransazioni.setShowSql(debug);	
					daoFactoryServiceManagerPropertiesTransazioni.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());
				}catch(Exception e){
					throw new TracciamentoException("Errore durante l'inizializzazione del daoFactory: "+e.getMessage(),e);
				}
				
				// GestoreDump
				staticInstance = new NotifierDump(debug, 
						//ds, 
						tipoDatabase, daoFactory, daoFactoryServiceManagerPropertiesTransazioni, daoFactoryLogger);

			}catch(Exception e){
				throw new Exception("Errore durante l'inizializzazione del NotifierDump: "+e.getMessage(),e);
			}
		}
	}
	public static NotifierDump getInstance() throws Exception{
		if(staticInstance==null){
			initialize();
		}
		return staticInstance;
	}



	// INSTANCE

	private String tipoDatabase;
	
    private DAOFactory daoFactory = null;
    private ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = null;
    private Logger daoFactoryLogger = null;
    
    boolean debug = false;
	
	public NotifierDump( boolean debug, 
			String tipoDatabase,
			DAOFactory daoFactory, ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni, Logger daoFactoryLogger){
		
		this.debug = debug;
		
		this.tipoDatabase = tipoDatabase;
		
		this.daoFactory = daoFactory;
		this.daoFactoryServiceManagerPropertiesTransazioni = daoFactoryServiceManagerPropertiesTransazioni;
		this.daoFactoryLogger = daoFactoryLogger;
	}

	
	private int save(NotifierCallback notifierCallback,
			String idTransazione,TipoMessaggio tipoMessaggio,Map<String, List<String>> headerTrasporto,
			long idDumpConfigurazione,
			String contentType,
			InputStream is,File file,byte[]buffer,
			IDSoggetto dominio) throws Exception{

		PreparedStatement stmt = null;
		DBTransazioniManager dbManager = null;
    	Resource r = null;
    	String idModulo = "NotifierDump.save"+tipoMessaggio.getValue();
    	try{
			notifierCallback.debug("@save getConnection.....");
			
			dbManager = DBTransazioniManager.getInstance();
			r = dbManager.getResource(dominio, idModulo, idTransazione);
			if(r==null){
				throw new Exception("Risorsa al database non disponibile");
			}
			Connection con = (Connection) r.getResource();
			if(con == null)
				throw new Exception("Connessione non disponibile");	

			boolean autoCommit = true;
			
			org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
					(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) this.daoFactory.
						getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), con, autoCommit,
							this.daoFactoryServiceManagerPropertiesTransazioni, this.daoFactoryLogger);
			jdbcServiceManager.getJdbcProperties().setShowSql(this.debug);
			
			JDBCDumpMessaggioService dumpMessaggioService =  (JDBCDumpMessaggioService) jdbcServiceManager.getDumpMessaggioService();
			
			ISQLFieldConverter sqlFielConverter = dumpMessaggioService.getFieldConverter();
			
			// Devo usare per forza la prepared stameent per sfruttare la funzionalità di setStream.
			
			ISQLQueryObject insertSql = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			insertSql.addInsertTable(sqlFielConverter.toTable(DumpMessaggio.model()));
			insertSql.addInsertField(sqlFielConverter.toColumn(DumpMessaggio.model().ID_TRANSAZIONE, false), "?");
			insertSql.addInsertField(sqlFielConverter.toColumn(DumpMessaggio.model().TIPO_MESSAGGIO, false), "?");
			insertSql.addInsertField(sqlFielConverter.toColumn(DumpMessaggio.model().DUMP_TIMESTAMP, false), "?");
			insertSql.addInsertField(sqlFielConverter.toColumn(DumpMessaggio.model().CONTENT_TYPE, false), "?");
			insertSql.addInsertField(sqlFielConverter.toColumn(DumpMessaggio.model().POST_PROCESS_HEADER, false), "?");
			if(file!=null){
				insertSql.addInsertField(sqlFielConverter.toColumn(DumpMessaggio.model().POST_PROCESS_FILENAME, false), "?");
			}else{
				insertSql.addInsertField(sqlFielConverter.toColumn(DumpMessaggio.model().POST_PROCESS_CONTENT, false), "?");
			}
			insertSql.addInsertField("post_process_config_id", "?" );
			insertSql.addInsertField(sqlFielConverter.toColumn(DumpMessaggio.model().POST_PROCESS_TIMESTAMP, false), "?");
			insertSql.addInsertField(sqlFielConverter.toColumn(DumpMessaggio.model().POST_PROCESSED, false), "?");
			
			notifierCallback.debug("@save insertSql ["+insertSql.createSQLInsert()+"].....");
						
			notifierCallback.debug("@save creo PreparedStatement.....");
			
			stmt = con.prepareStatement(insertSql.createSQLInsert());

			int index = 1;
			stmt.setString(index++, idTransazione);
			stmt.setString(index++, tipoMessaggio.toString());
			stmt.setTimestamp(index++, DateManager.getTimestamp());
			stmt.setString(index++, contentType);
			stmt.setString(index++, this.toString(headerTrasporto));
			if(file!=null){
				notifierCallback.debug("set FileName.....");
				stmt.setString(index++, file.getName());
			}else{
				if(is!=null){
					notifierCallback.debug("set BinaryStream.....");
					stmt.setBinaryStream(index++, is);
				}
				else{
					notifierCallback.debug("set JDBCAdapter.....");
					JDBCAdapterFactory.createJDBCAdapter(this.tipoDatabase).setBinaryData(stmt, index++, buffer);
				}
			}
			stmt.setLong(index++, idDumpConfigurazione);
			stmt.setTimestamp(index++, DateManager.getTimestamp());
			stmt.setInt(index++, 0); // dovra' essere processato in posto process.

			notifierCallback.debug("set executeUpdate.....");
			
			int row = stmt.executeUpdate();
			
			notifierCallback.debug("return row ["+row+"].....");
			return row;

		}finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch(Exception eClose){}
			try{
				if(r!=null)
					dbManager.releaseResource(dominio, idModulo, r);
			}catch(Exception eClose){}
		}
	}
	
	private String toString(Map<String, List<String>> headerTrasporto){
		StringBuilder bf = new StringBuilder();
		if(headerTrasporto!=null && headerTrasporto.size()>0){
			Iterator<String> keys = headerTrasporto.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				List<String> values = headerTrasporto.get(key);
				if(values!=null && !values.isEmpty()) {
					for (String value : values) {
						if(bf.length()>0){
							bf.append("\n");
						}
						bf.append(key).append("=").append(value);		
					}
				}
			}
		}
		if(bf.length()>0){
			return bf.toString();
		}
		else{
			return null;
		}
	}
	
	public int saveOnDatabase(NotifierCallback notifierCallback,
			String idTransazione,TipoMessaggio tipoMessaggio, Map<String, List<String>> headerTrasporto, 
			long idDumpConfigurazione,
			String contentType,InputStream is,
			IDSoggetto dominio) throws Exception{
		return this.save(notifierCallback,idTransazione, tipoMessaggio, headerTrasporto, idDumpConfigurazione, contentType, is, null, null, dominio);
	}
	
	public int saveOnFileSystem(NotifierCallback notifierCallback,
			String idTransazione,TipoMessaggio tipoMessaggio, Map<String, List<String>> headerTrasporto, 
			long idDumpConfigurazione,
			String contentType,File file,
			IDSoggetto dominio) throws Exception{
		return this.save(notifierCallback,idTransazione, tipoMessaggio, headerTrasporto, idDumpConfigurazione, contentType, null, file, null, dominio);
	}
	
	public int saveBuffer(NotifierCallback notifierCallback,
			String idTransazione,TipoMessaggio tipoMessaggio, Map<String, List<String>> headerTrasporto, 
			long idDumpConfigurazione,
			String contentType,byte[] content,
			IDSoggetto dominio) throws Exception{
		return this.save(notifierCallback,idTransazione, tipoMessaggio, headerTrasporto, idDumpConfigurazione, contentType, null, null, content, dominio);
	}
	
	
	public int update(NotifierCallback notifierCallback,
			String idTransazione,TipoMessaggio tipoMessaggio,Map<String, List<String>> headerTrasporto,
			IDSoggetto dominio) throws Exception{

		PreparedStatement stmt = null;
		DBTransazioniManager dbManager = null;
    	Resource r = null;
    	String idModulo = "NotifierDump.update"+tipoMessaggio.getValue();
    	try{
			notifierCallback.debug("@save getConnection.....");
			
			dbManager = DBTransazioniManager.getInstance();
			r = dbManager.getResource(dominio, idModulo, idTransazione);
			if(r==null){
				throw new Exception("Risorsa al database non disponibile");
			}
			Connection con = (Connection) r.getResource();
			if(con == null)
				throw new Exception("Connessione non disponibile");	

			boolean autoCommit = true;
			
			org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
					(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) 
						this.daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), con, autoCommit,
							this.daoFactoryServiceManagerPropertiesTransazioni, this.daoFactoryLogger);
			jdbcServiceManager.getJdbcProperties().setShowSql(this.debug);
			
			JDBCDumpMessaggioService dumpMessaggioService =  (JDBCDumpMessaggioService) jdbcServiceManager.getDumpMessaggioService();
			
			ISQLFieldConverter sqlFielConverter = dumpMessaggioService.getFieldConverter();
			
			// Devo usare per forza la prepared stameent per sfruttare la funzionalità di ritornare le righe modificate
			
			ISQLQueryObject updateSql = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
			updateSql.addUpdateTable(sqlFielConverter.toTable(DumpMessaggio.model()));
			updateSql.addUpdateField(sqlFielConverter.toColumn(DumpMessaggio.model().POST_PROCESS_HEADER, false), "?");
			updateSql.setANDLogicOperator(true);
			updateSql.addWhereCondition(sqlFielConverter.toColumn(DumpMessaggio.model().ID_TRANSAZIONE, false)+"=?");
			updateSql.addWhereCondition(sqlFielConverter.toColumn(DumpMessaggio.model().TIPO_MESSAGGIO, false)+"=?");

			notifierCallback.debug("@save updateSql ["+updateSql.createSQLUpdate()+"].....");
						
			notifierCallback.debug("@save creo PreparedStatement.....");
			
			stmt = con.prepareStatement(updateSql.createSQLUpdate());

			stmt.setString(1, this.toString(headerTrasporto));
			stmt.setString(2, idTransazione);
			stmt.setString(3, tipoMessaggio.toString());

			notifierCallback.debug("set executeUpdate.....");
			
			int row = stmt.executeUpdate();
			
			notifierCallback.debug("return row ["+row+"].....");
			return row;

		}finally{
			try{
				if(stmt!=null)
					stmt.close();
			}catch(Exception eClose){}
			try{
				if(r!=null)
					dbManager.releaseResource(dominio, idModulo, r);
			}catch(Exception eClose){}
		}
	}
	
}

