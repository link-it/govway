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



package org.openspcoop2.protocol.basic.diagnostica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiException;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.StringWrapper;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.slf4j.Logger;

/**
 * Interfaccia di ricerca dei messaggi diagnostici
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DiagnosticDriver extends BasicComponentFactory implements IDiagnosticDriver {
	/** 
	 * DataSource
	 */
	DataSource datasource = null;
	/**
	 * SQLQueryObject
	 */
	String tipoDatabase = null;
	
	Connection con = null;
	boolean connectionOpenViaJDBCInCostructor = false;
		
	public DiagnosticDriver(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
	}

	
	public static final String IDDIAGNOSTICI = "@@@@@-----@@@@-----IDDIAGNOSTICI-DB----@@@@@-----@@@@";
	
	
	/**
	 * Properties per campi aggiuntivi
	 */
	private List<String> propertiesMsgDiagnostici;
	public void setPropertiesMsgDiagnostici(List<String> properties) {
		this.propertiesMsgDiagnostici = properties;
	}	
	
	
	
	
	public void init(String nomeDataSource, String tipoDatabase, Properties jndiProperties) throws DriverMsgDiagnosticiException {
		init(nomeDataSource,tipoDatabase,jndiProperties,null);
	}
	
	public void init(String nomeDataSource, String tipoDatabase, Properties jndiProperties, Logger log) throws DriverMsgDiagnosticiException {
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("Errore durante l'inizializzazione del logger...",e);
		}

		// Datasource
		try {
			this.log.info("Inizializzo DriverLogAnalyzer...");
			GestoreJNDI gestoreJNDI = new GestoreJNDI(jndiProperties);
			this.datasource = (DataSource) gestoreJNDI.lookup(nomeDataSource);
			if (this.datasource == null)
				throw new Exception ("datasource is null");

			this.log.info("Inizializzo DriverLogAnalyzer terminata.");
		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del datasource...",e);
			throw new DriverMsgDiagnosticiException("Errore durante la ricerca del datasource...",e);
		}

		// ISQLQueryObject
		try {
			this.log.info("Inizializzo ISQLQueryObject...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new Exception("Tipo database non gestito");
			}
			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del SQLQueryObject...",e);
			throw new DriverMsgDiagnosticiException("Errore durante la ricerca del SQLQueryObject...",e);
		}
	}
	
	public void init(DataSource dataSourceObject, String tipoDatabase) throws DriverMsgDiagnosticiException {
		init(dataSourceObject,tipoDatabase,null);
	}
		
	public void init(DataSource dataSourceObject, String tipoDatabase, Logger log) throws DriverMsgDiagnosticiException {
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("Errore durante l'inizializzazione del logger...",e);
		}

		// Datasource
		try {
			this.datasource = dataSourceObject;
			if (this.datasource == null)
				throw new Exception ("datasource is null");
		} catch (Exception e) {
			this.log.error("Errore durante l'assegnamento del datasource...",e);
			throw new DriverMsgDiagnosticiException("Errore durante l'assegnamento del datasource...",e);
		}

		// ISQLQueryObject
		try {
			this.log.info("Inizializzo ISQLQueryObject...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new Exception("Tipo database non gestito");
			}
			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del SQLQueryObject...",e);
			throw new DriverMsgDiagnosticiException("Errore durante la ricerca del SQLQueryObject...",e);
		}
	}
	
	public void init(Connection connection, String tipoDatabase, Logger log) throws DriverMsgDiagnosticiException {
		// Logger
		try {
			this.log = log;
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("Errore durante l'inizializzazione del logger...",e);
		}

		// connection
		this.con = connection;
		
		// ISQLQueryObject
		try {
			this.log.info("Inizializzo ISQLQueryObject...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new Exception("Tipo database non gestito");
			}
			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del SQLQueryObject...",e);
			throw new DriverMsgDiagnosticiException("Errore durante la ricerca del SQLQueryObject...",e);
		}
	}
	
	public void init(String urlJDBC,String driverJDBC,
			String username,String password, String tipoDatabase, Logger log) throws DriverMsgDiagnosticiException {
		// Logger
		try {
			this.log = log;
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("Errore durante l'inizializzazione del logger...",e);
		}

		// connection
		try {
			Class.forName(driverJDBC);
			
			if(username!=null){
				this.con = DriverManager.getConnection(urlJDBC,username,password);
			}else{
				this.con = DriverManager.getConnection(urlJDBC);
			}
			this.connectionOpenViaJDBCInCostructor = true;
			
		} catch (Exception e) {
			this.log.error("Errore durante l'inizializzazione della connessione...",e);
			throw new DriverMsgDiagnosticiException("Errore durante l'inizializzazione della connessione...",e);
		}
				
		// ISQLQueryObject
		try {
			this.log.info("Inizializzo ISQLQueryObject...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new Exception("Tipo database non gestito");
			}
			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del SQLQueryObject...",e);
			throw new DriverMsgDiagnosticiException("Errore durante la ricerca del SQLQueryObject...",e);
		}
	}

	
	
	
	
	
	
	
	
	/* *********** ACCESSI TRAMITE RICERCHE (DIAGNOSTICI) ******* */
	
	/**
	 * Si occupa di ritornare il numero di diagnostici che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return numero di diagnostici che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public int countMessaggiDiagnostici(FiltroRicercaDiagnostici filtro) throws DriverMsgDiagnosticiException{
		PreparedStatement stmt = null;
		ResultSet rs=null;
		Connection con = null;
		boolean closeConnection = true;
		ISQLQueryObject sqlObj =  null;
		int countDiagnostici = 0;
		try{
			if(this.con!=null){
				con=this.con;
				closeConnection=false;
			}else{
				con = this.datasource.getConnection();
			}
			checkConnection(con);

			sqlObj = DiagnosticDriverUtilities.createSQLQueryObj_countMessaggiDiagnostici(filtro, this.tipoDatabase);
			
			StringWrapper sqlDebug = new StringWrapper(sqlObj.createSQLQuery());
			DiagnosticDriverUtilities.setValues_countMessaggiDiagnostici(filtro, sqlDebug, 1);
			this.log.debug("Query : "+sqlDebug);
			
			String sql = sqlObj.createSQLQuery();
			stmt=con.prepareStatement(sql);
			DiagnosticDriverUtilities.setValues_countMessaggiDiagnostici(filtro, stmt, 1);

			rs=stmt.executeQuery();
			if(rs.next()){
				
				countDiagnostici = rs.getInt("countMsgDiagnostici");
				
			}
			
			this.log.debug("Query found "+countDiagnostici+" rows");
			
			return countDiagnostici;
			
		}catch (DriverMsgDiagnosticiException e) {
			throw e;
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException(e);
		}finally{
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				// ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				// ignore
			}
			
			if(closeConnection){
				try{
					if(con!=null) {
						con.close();
					}
				}catch (Exception e) {
					// ignore
				}
			}
		}
	}
	
	/**
	 * Si occupa di ritornare i diagnostici che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return diagnostici che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public List<MsgDiagnostico> getMessaggiDiagnostici(FiltroRicercaDiagnosticiConPaginazione filtro)  
		throws DriverMsgDiagnosticiException, DriverMsgDiagnosticiNotFoundException{
		PreparedStatement stmt = null;
		ResultSet rs=null;
		Connection con = null;
		boolean closeConnection = true;
		ISQLQueryObject sqlObj =  null;
		try{
			ArrayList<MsgDiagnostico> listaMSGDiagnostici = new ArrayList<MsgDiagnostico>();
			if(this.con!=null){
				con=this.con;
				closeConnection=false;
			}else{
				con = this.datasource.getConnection();
			}
			checkConnection(con);
			
			sqlObj = DiagnosticDriverUtilities.createSQLQueryObj_searchMessaggiDiagnostici(filtro, this.tipoDatabase);
			
			StringWrapper sqlDebug = new StringWrapper(sqlObj.createSQLQuery());
			DiagnosticDriverUtilities.setValues_searchMessaggiDiagnostici(filtro, sqlDebug, 1);
			this.log.debug("Query : "+sqlDebug);
			
			String sql = sqlObj.createSQLQuery();
			stmt=con.prepareStatement(sql);
			DiagnosticDriverUtilities.setValues_searchMessaggiDiagnostici(filtro, stmt, 1);

			rs=stmt.executeQuery();
			while(rs.next()){
				
				long idMsgDiagnostico = rs.getLong("idMsgDiagnostico");
				MsgDiagnostico msgDiag = 
					DiagnosticDriverUtilities.getMsgDiagnostico(con, this.tipoDatabase, 
							this.log, idMsgDiagnostico, this.propertiesMsgDiagnostici);
				listaMSGDiagnostici.add(msgDiag);
			}
			rs.close();
			stmt.close();
			
			this.log.debug("Query found "+listaMSGDiagnostici.size()+" rows");
			
			if(listaMSGDiagnostici.size()>0)
				return listaMSGDiagnostici;
			else
				throw new DriverMsgDiagnosticiNotFoundException("Non sono stati trovati diagnostici che rispettano il filtro impostato");
			
		}catch (DriverMsgDiagnosticiNotFoundException e) {
			throw e;
		}catch (DriverMsgDiagnosticiException e) {
			throw e;
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException(e);
		}finally{
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				// ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				// ignore
			}
			
			if(closeConnection){
				try{
					if(con!=null) {
						con.close();
					}
				}catch (Exception e) {
					// ignore
				}
			}
		}
	}
	
	
	/**
	 * Si occupa di eliminare i diagnostici che rispettano il filtro di ricerca
	 * 
	 * @param filter Filtro di ricerca
	 * @return numero di diagnostici eliminati
	 * @throws DriverTracciamentoException
	 */
	@Override
	public int deleteMessaggiDiagnostici(FiltroRicercaDiagnostici filter) throws DriverMsgDiagnosticiException{
		PreparedStatement stmt = null;
		int deleted = 0; 
		Connection con = null;
		boolean closeConnection = true;
		ISQLQueryObject sqlObj =  null;
		try{
			if(this.con!=null){
				con=this.con;
				closeConnection=false;
			}else{
				con = this.datasource.getConnection();
			}
			checkConnection(con);
			
			sqlObj = DiagnosticDriverUtilities.createSQLQueryObj_deleteMessaggiDiagnostici(filter, this.tipoDatabase);
			
			StringWrapper sqlDebug = new StringWrapper(sqlObj.createSQLDelete());
			DiagnosticDriverUtilities.setValues_deleteMessaggiDiagnostici(filter, sqlDebug, 1);
			this.log.debug("Query : "+sqlDebug);
			
			String sql = sqlObj.createSQLDelete();
			stmt=con.prepareStatement(sql);
			DiagnosticDriverUtilities.setValues_deleteMessaggiDiagnostici(filter, stmt, 1);
			
			deleted = stmt.executeUpdate();		
			
			stmt.close();
			
			this.log.debug("Deleted "+deleted+" rows");
			
			return deleted;
			
		}catch (DriverMsgDiagnosticiException e) {
			throw e;
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException(e);
		}finally{
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				// ignore
			}
			if(closeConnection){
				try{
					if(con!=null) {
						con.close();
					}
				}catch (Exception e) {
					// ignore
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ******* RISORSE INTERNE ********** */
	
	@Override
	public void close() throws DriverMsgDiagnosticiException {
		try{
			if(this.connectionOpenViaJDBCInCostructor){
				if(this.con!=null && this.con.isClosed()==false){
					this.con.close();
				}
			}
		}catch(Exception e){
			throw new DriverMsgDiagnosticiException(e.getMessage(),e);
		}
	}	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	/* ------------- UTILITY INTERNE -------------------------- */

	private void checkConnection(Connection con) throws Exception {
		if(con==null) {
			if(this.con==null) {
				throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
		}
	}
	
	/**
	 * Viene chiamato in causa per ottenere una connessione al DB
	 */
	public java.sql.Connection getConnection() {
		if (this.datasource == null) {
			return null;
		}

		Connection connectionDB = null;
		try {
			connectionDB = this.datasource.getConnection();
		} catch (Exception e) {
			return null;
		}

		return connectionDB;
	}

	/**
	 * Viene chiamato in causa per rilasciare una connessione al DB, effettuando
	 * precedentemente un commit
	 * 
	 * @param connectionDB
	 *            Connessione da rilasciare.
	 */
	public void releaseConnection(java.sql.Connection connectionDB) {
		try {
			if (connectionDB != null) {
				connectionDB.close();
			}
		} catch (SQLException e) {
		}
	}

	/**
	 * Viene chiamato in causa per ottenere il tipoDatabase
	 */
	public String getTipoDatabase() {
		return this.tipoDatabase;
	}

}


