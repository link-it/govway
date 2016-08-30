/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiException;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDriverMsgDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.InformazioniProtocollo;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazione;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.StringWrapper;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * Interfaccia di ricerca dei messaggi diagnostici
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverMsgDiagnostici implements IDriverMsgDiagnostici {
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
	
	/** Logger utilizzato per info. */
	private Logger log = null;

	protected IProtocolFactory protocolFactory;
	
	public DriverMsgDiagnostici(IProtocolFactory factory){
		this.log = factory.getLogger();
		this.protocolFactory = factory;
	}
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}
	
	public final static String IDDIAGNOSTICI = "@@@@@-----@@@@-----IDDIAGNOSTICI-DB----@@@@@-----@@@@";
	
	
	/**
	 * Properties per campi aggiuntivi
	 */
	private Vector<String> propertiesMsgDiagnostici;
	public void setPropertiesMsgDiagnostici(Vector<String> properties) {
		this.propertiesMsgDiagnostici = properties;
	}
	private Vector<String> propertiesMsgDiagCorrelazione;
	public void setPropertiesMsgDiagCorrelazione(Vector<String> properties) {
		this.propertiesMsgDiagCorrelazione = properties;
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
				if(con==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}

			sqlObj = DriverMsgDiagnosticiUtilities.createSQLQueryObj_countMessaggiDiagnostici(filtro, this.tipoDatabase);
			
			StringWrapper sqlDebug = new StringWrapper(sqlObj.createSQLQuery());
			DriverMsgDiagnosticiUtilities.setValuesSearchMessaggiDiagnostici(filtro, sqlDebug, 1);
			this.log.debug("Query : "+sqlDebug);
			
			String sql = sqlObj.createSQLQuery();
			stmt=con.prepareStatement(sql);
			DriverMsgDiagnosticiUtilities.setValuesSearchMessaggiDiagnostici(filtro, stmt, 1);

			rs=stmt.executeQuery();
			if(rs.next()){
				
				countDiagnostici = rs.getInt("countMsgDiagnostici");
				
			}
			rs.close();
			stmt.close();
			
			this.log.debug("Query found "+countDiagnostici+" rows");
			
			return countDiagnostici;
			
		}catch (DriverMsgDiagnosticiException e) {
			throw e;
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException(e);
		}finally{
			try{
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				
			}
			
			if(closeConnection){
				try{
					con.close();
				}catch (Exception e) {
					
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
				if(con==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}

			sqlObj = DriverMsgDiagnosticiUtilities.createSQLQueryObj_searchMessaggiDiagnostici(filtro, this.tipoDatabase);
			
			StringWrapper sqlDebug = new StringWrapper(sqlObj.createSQLQuery());
			DriverMsgDiagnosticiUtilities.setValuesSearchMessaggiDiagnostici(filtro, sqlDebug, 1);
			this.log.debug("Query : "+sqlDebug);
			
			String sql = sqlObj.createSQLQuery();
			stmt=con.prepareStatement(sql);
			DriverMsgDiagnosticiUtilities.setValuesSearchMessaggiDiagnostici(filtro, stmt, 1);

			rs=stmt.executeQuery();
			while(rs.next()){
				
				long idMsgDiagnostico = rs.getLong("idMsgDiagnostico");
				MsgDiagnostico msgDiag = 
					DriverMsgDiagnosticiUtilities.getMsgDiagnostico(con, this.tipoDatabase, 
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
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				
			}
			
			if(closeConnection){
				try{
					con.close();
				}catch (Exception e) {
					
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
				if(con==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}

			sqlObj = DriverMsgDiagnosticiUtilities.createSQLQueryObj_deleteMessaggiDiagnostici(filter, this.tipoDatabase);
			
			StringWrapper sqlDebug = new StringWrapper(sqlObj.createSQLDelete());
			DriverMsgDiagnosticiUtilities.setValuesSearchMessaggiDiagnostici(filter, sqlDebug, 1);
			this.log.debug("Query : "+sqlDebug);
			
			String sql = sqlObj.createSQLDelete();
			stmt=con.prepareStatement(sql);
			DriverMsgDiagnosticiUtilities.setValuesSearchMessaggiDiagnostici(filter, stmt, 1);
			
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
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				
			}
			if(closeConnection){
				try{
					con.close();
				}catch (Exception e) {
					
				}
			}
		}
	}
	
	
	
	
	
	/* *********** ACCESSI TRAMITE RICERCHE (CORRELAZIONI DIAGNOSTICI con il PROTOCOLLO) ******* */
	
	/**
	 * Si occupa di ritornare il numero di informazioni di correlazione dei diagnostici che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return numero di informazioni di correlazione dei diagnostici che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public int countInfoCorrelazioniMessaggiDiagnostici(FiltroRicercaDiagnostici filtro) throws DriverMsgDiagnosticiException{
		PreparedStatement stmt = null;
		ResultSet rs=null;
		Connection con = null;
		boolean closeConnection = true;
		ISQLQueryObject sqlObj =  null;
		int countCorrelazioni = 0;
		try{
			if(this.con!=null){
				con=this.con;
				closeConnection=false;
			}else{
				con = this.datasource.getConnection();
				if(con==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}

			sqlObj = DriverMsgDiagnosticiUtilities.createSQLQueryObj_countMsgDiagCorrelazione(filtro, this.tipoDatabase);
		
			StringWrapper sqlDebug = new StringWrapper(sqlObj.createSQLQuery());
			DriverMsgDiagnosticiUtilities.setValuesSearchMsgDiagCorrelazione(filtro, sqlDebug, 1);
			this.log.debug("Query : "+sqlDebug);
			
			String sql = sqlObj.createSQLQuery();
			stmt=con.prepareStatement(sql);
			DriverMsgDiagnosticiUtilities.setValuesSearchMsgDiagCorrelazione(filtro, stmt, 1);

			rs=stmt.executeQuery();
			if(rs.next()){
				
				countCorrelazioni = rs.getInt("countCorrelazioniMsgDiagnostici");
				
			}
			rs.close();
			stmt.close();
			
			this.log.debug("Query found "+countCorrelazioni+" rows");
			
			return countCorrelazioni;
			
		}catch (DriverMsgDiagnosticiException e) {
			throw e;
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException(e);
		}finally{
			try{
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				
			}
			
			if(closeConnection){
				try{
					con.close();
				}catch (Exception e) {
					
				}
			}
		}
	}
	
	/**
	 * Si occupa di ritornare informazioni di correlazione dei diagnostici che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return informazioni di correlazione dei diagnostici che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public List<MsgDiagnosticoCorrelazione> getInfoCorrelazioniMessaggiDiagnostici(FiltroRicercaDiagnosticiConPaginazione filtro)  
		throws DriverMsgDiagnosticiException, DriverMsgDiagnosticiNotFoundException{
		PreparedStatement stmt = null;
		ResultSet rs=null;
		Connection con = null;
		boolean closeConnection = true;
		ISQLQueryObject sqlObj =  null;
		try{
			ArrayList<MsgDiagnosticoCorrelazione> listaCorrelazioni = new ArrayList<MsgDiagnosticoCorrelazione>();
			if(this.con!=null){
				con=this.con;
				closeConnection=false;
			}else{
				con = this.datasource.getConnection();
				if(con==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}

			sqlObj = DriverMsgDiagnosticiUtilities.createSQLQueryObj_searchMsgDiagCorrelazione(filtro, this.tipoDatabase);
			
			StringWrapper sqlDebug = new StringWrapper(sqlObj.createSQLQuery());
			DriverMsgDiagnosticiUtilities.setValuesSearchMsgDiagCorrelazione(filtro, sqlDebug, 1);
			this.log.debug("Query : "+sqlDebug);
			
			String sql = sqlObj.createSQLQuery();
			stmt=con.prepareStatement(sql);
			DriverMsgDiagnosticiUtilities.setValuesSearchMsgDiagCorrelazione(filtro, stmt, 1);

			rs=stmt.executeQuery();
			while(rs.next()){
				
				long idMsgDiagnosticoCorrelazione = rs.getLong("idMsgDiagCorrelazione");
				MsgDiagnosticoCorrelazione msgDiagCorrelazione = 
					DriverMsgDiagnosticiUtilities.getMsgDiagnosticoCorrelazione(con, this.tipoDatabase,
							this.log, idMsgDiagnosticoCorrelazione, this.propertiesMsgDiagCorrelazione);
				listaCorrelazioni.add(msgDiagCorrelazione);
				
			}
			rs.close();
			stmt.close();
			
			this.log.debug("Query found "+listaCorrelazioni.size()+" rows");
			
			if(listaCorrelazioni.size()>0)
				return listaCorrelazioni;
			else
				throw new DriverMsgDiagnosticiNotFoundException("Non sono stati trovate correlazioni ai diagnostici che rispettano il filtro impostato");
			
		}catch (DriverMsgDiagnosticiNotFoundException e) {
			throw e;
		}catch (DriverMsgDiagnosticiException e) {
			throw e;
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException(e);
		}finally{
			try{
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				
			}
			
			if(closeConnection){
				try{
					con.close();
				}catch (Exception e) {
					
				}
			}
		}
	}
	
	
	/**
	 * Si occupa di eliminare informazioni di correlazione dei diagnostici che rispettano il filtro di ricerca
	 * 
	 * @param filter Filtro di ricerca
	 * @return numero di diagnostici eliminati
	 * @throws DriverTracciamentoException
	 */
	@Override
	public int deleteInfoCorrelazioniMessaggiDiagnostici(FiltroRicercaDiagnostici filter) throws DriverMsgDiagnosticiException{
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
				if(con==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}

			sqlObj = DriverMsgDiagnosticiUtilities.createSQLQueryObj_deleteMsgDiagCorrelazione(filter, this.tipoDatabase);
			
			StringWrapper sqlDebug = new StringWrapper(sqlObj.createSQLDelete());
			DriverMsgDiagnosticiUtilities.setValuesSearchMsgDiagCorrelazione(filter, sqlDebug, 1);
			this.log.debug("Query : "+sqlDebug);
			
			String sql = sqlObj.createSQLDelete();
			stmt=con.prepareStatement(sql);
			DriverMsgDiagnosticiUtilities.setValuesSearchMsgDiagCorrelazione(filter, stmt, 1);
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
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				
			}
			if(closeConnection){
				try{
					con.close();
				}catch (Exception e) {
					
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ********** UTILITY DEL DRIVER (non inserita nell'Interfaccia) ************ */
	
	
	/**
	 * Recupera i messaggi diagnostici
	 * @param filter
	 * @param all true se si vogliono tutti i messaggi, false se si vogliono solo quelli correlati
	 * @return i messaggi diagnostici
	 * @throws DriverMsgDiagnosticiException
	 * @throws SQLQueryObjectException
	 */
	public List<MsgDiagnosticoCorrelazione> getInfoEntryCorrelazione(FiltroRicercaDiagnosticiConPaginazione filter,boolean all) throws DriverMsgDiagnosticiException, SQLQueryObjectException{
		
		PreparedStatement stmt = null;
		ResultSet rs=null;
		Connection con = null;
		boolean closeConnection = true;
		
		try{
			if(this.con!=null){
				con=this.con;
				closeConnection=false;
			}else{
				con = this.datasource.getConnection();
				if(con==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			String sqlQuery="";
			if(all){
				sqlQuery= DriverMsgDiagnosticiUnionUtilities.createUnionObj(filter, this.log, sqlQuery, this.propertiesMsgDiagCorrelazione);
			}else{
				ISQLQueryObject sqlQueryObject=
					DriverMsgDiagnosticiUnionUtilities.createSQLQueryObjCorrelazione(filter, false, this.log, sqlQuery, this.propertiesMsgDiagCorrelazione);
					
				//orderby solo su gdo causa bug orderby
				//sqlQueryObject.addOrderBy("gdo");
//				sqlQueryObject.addOrderBy("tipo_fruitore");
//				sqlQueryObject.addOrderBy("fruitore");
//				sqlQueryObject.addOrderBy("tipo_erogatore");
//				sqlQueryObject.addOrderBy("erogatore");
//				sqlQueryObject.addOrderBy("tipo_servizio");
//				sqlQueryObject.addOrderBy("servizio");
				//sqlQueryObject.setSortType(false);
				
				sqlQueryObject.setLimit(filter.getLimit());
				/*l'offset mi serve in quanto la query prodotta
				 *e' dal createSQLQueryObjCorrelazione contient un offset limitato
				 *quindi sovrascrivo con quello reale
				 */ 
				sqlQueryObject.setOffset(filter.getOffset());
				
				
				sqlQuery=sqlQueryObject.createSQLQuery();
			}
			
			this.log.debug("eseguo query  :"+sqlQuery);
			stmt=con.prepareStatement(sqlQuery);
			
			int numRipetizioni = 1;
			if(all){
				//devo ciclare 3 volte per impostare i 3*3 parametri restituiti dalla union
				//nelle 3 select interne
				if(filter.isPartial())numRipetizioni=2;
				else numRipetizioni=3;
			}
			/*
			 * La query ritornata dalla union e quella normale hanno 3 parametri (data_inizio,data_fine,id_messaggio)
			 * se settati questi parametri devono essere impostati nelle 3 select interne alla union oppure nella sola
			 * query prodotta nel caso !all
			 * la i indica i parametri nella riga
			 * la j indica i parametri per le select interne
			 */
			int count = 1;
			for(int j=1;j<=numRipetizioni;j++){
				//data inizio
				if(filter.getDataInizio()!=null){
					stmt.setTimestamp(count, new Timestamp(filter.getDataInizio().getTime()));
					count++;
				}
				//data fine
				if(filter.getDataFine()!=null){
					stmt.setTimestamp(count, new Timestamp(filter.getDataFine().getTime()));
					count++;
				}
				//idmessaggio
				//nella union la seconda select l'idmessaggio nn viene considerato
				if(j!=2){
					if(filter.getIdBustaRichiesta()!=null && !"".equals(filter.getIdBustaRichiesta())){
						stmt.setString(count, filter.getIdBustaRichiesta());
						count++;
					}
				}
			}
			
						
			ArrayList<MsgDiagnosticoCorrelazione> listaMSGCorrelazione = new ArrayList<MsgDiagnosticoCorrelazione>();
			
			/*
			 * offsetmap tengo traccia della mappa
			 * offsetmap[0] = correlazione
			 * offsetmap[1] = diagnostici
			 * offsetmap[2] = notexist 
			 */
			long[] offsetMap= {0,0,0};
			if(filter.getOffsetMap()!=null) offsetMap=filter.getOffsetMap(); 
			
			rs=stmt.executeQuery();
			while(rs.next()){
				MsgDiagnosticoCorrelazione entry = new MsgDiagnosticoCorrelazione();
				
				/*
				 * Informazioni Comuni
				 */
				entry.setId(rs.getLong("id"));
				entry.setIdBusta(rs.getString("idmessaggio"));
				entry.setProtocollo(rs.getString("protocollo"));
				Timestamp gdo=rs.getTimestamp("gdo");
				entry.setGdo(gdo);
				entry.setCorrelazioneApplicativa(rs.getString("id_correlazione_applicativa"));
				if(this.propertiesMsgDiagCorrelazione!=null){
					for (int i = 0; i < this.propertiesMsgDiagCorrelazione.size(); i++) {
						String key = this.propertiesMsgDiagCorrelazione.get(i);
						entry.addProperty(key, rs.getString(key));
					}
				}
				
				/*
				 * Una entry puo essere correlata oppure no
				 * Una entry non correlata non possiede tipo_servizio, servizio ... cioe' le informazioni 
				 * 
				 */
				String tipo_servizio = rs.getString("tipo_servizio");
				if(tipo_servizio!=null){
					//entry correlazione
					offsetMap[0]++;
					
					entry.setNomePorta(rs.getString("porta"));
					
					//informazioni busta
					IDSoggetto fruitore = new IDSoggetto(rs.getString("tipo_fruitore"),rs.getString("fruitore"));
					IDSoggetto erogatore = new IDSoggetto(rs.getString("tipo_erogatore"),rs.getString("erogatore"));
					IDServizio servizio = new IDServizio(erogatore);
					servizio.setServizio(rs.getString("servizio"));
					servizio.setTipoServizio(rs.getString("tipo_servizio"));
					
					InformazioniProtocollo infoBusta = new InformazioniProtocollo();
					infoBusta.setAzione(rs.getString("azione"));
					infoBusta.setErogatore(erogatore);
					infoBusta.setFruitore(fruitore);
					infoBusta.setServizio(servizio.getServizio());
					infoBusta.setTipoServizio(servizio.getTipoServizio());
					
					entry.setInformazioniProtocollo(infoBusta);
				
					entry.setDelegata(rs.getInt("delegata")==1?true:false);
					entry.setServiziApplicativiList(getMSGDiagnosticoCorrelazioneSA(rs.getLong("id")));
					
				}else {
					//entry diagnostici, se idbusta e' null allora fa parte della 2a query
					//altrimenti della 3a
					if(entry.getIdBusta()==null || "".equals(entry)) offsetMap[1]++;
					else offsetMap[2]++;
				}
				
				listaMSGCorrelazione.add(entry);
			}
			rs.close();
			stmt.close();
			
			//setto offsetmap
			this.log.debug("offsetmap[correlati]: "+offsetMap[0]);
			this.log.debug("offsetmap[diagnostici]: "+offsetMap[1]);
			this.log.debug("offsetmap[notexist]: "+offsetMap[2]);
			
			filter.setOffsetMap(offsetMap);
			
			
			return listaMSGCorrelazione;
			
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException(e);
		}finally{
			try{
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				
			}
			
			if(closeConnection){
				try{
					con.close();
				}catch (Exception e) {
					
				}
			}
		}
		
		
	}
	
	/**
	 * Recupera la lista di servizi applicativi associati ad un messaggio diagnostico di correlazione
	 * @param idCorrelazione
	 * @return List
	 * @throws DriverMsgDiagnosticiException
	 * @throws SQLQueryObjectException
	 */
	private List<String> getMSGDiagnosticoCorrelazioneSA(long idCorrelazione) throws DriverMsgDiagnosticiException, SQLQueryObjectException{
		PreparedStatement stmt = null;
		ResultSet rs=null;
		Connection con = null;
		boolean closeConnection = true;
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
		
		sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE_SA);
		sqlQueryObject.addWhereCondition("id_correlazione='"+idCorrelazione+"'");
		try{
			ArrayList<String> listaNomeSA = new ArrayList<String>();
			
			if(this.con!=null){
				con=this.con;
				closeConnection=false;
			}else{
				con = this.datasource.getConnection();
				if(con==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			stmt=con.prepareStatement(sqlQueryObject.createSQLQuery());
			rs=stmt.executeQuery();
			while(rs.next()){
				listaNomeSA.add(rs.getString("servizio_applicativo"));
			}
			rs.close();
			stmt.close();
			
			return listaNomeSA;
			
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException(e);
		}finally{
			try{
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				
			}
			
			if(closeConnection){
				try{
					con.close();
				}catch (Exception e) {
					
				}
			}
			
		}
	}
	
	
	
	
	
	
	

	public long getTotUnionEntry(FiltroRicercaDiagnosticiConPaginazione filter) throws DriverMsgDiagnosticiException, SQLQueryObjectException{
		
		PreparedStatement stmt = null;
		ResultSet rs=null;
		Connection con = null;
		boolean closeConnection = true;
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
		ISQLQueryObject sqlQueryObject2 = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
		ISQLQueryObject sqlQueryObject3 = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
		ISQLQueryObject subquery = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
		
		try{
			if(this.con!=null){
				con=this.con;
				closeConnection=false;
			}else{
				con = this.datasource.getConnection();
				if(con==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			/*
			 * La union count non va bene!!! Performance troppo ridotte.
			 * Usiamo 3 query distinte per prendere i totali e poi sommiamo i totali
			 * 
			 * 1)SELECT count(idmessaggio) as count FROM msgdiag_correlazione where filtro
			 * 2)SELECT count(*) as count FROM msgdiagnostici WHERE ( msgdiagnostici.idmessaggio is NULL ) +filtro
			 * 3)SELECT count(idmessaggio) as count FROM msgdiagnostici WHERE ( NOT EXISTS (SELECT * FROM msgdiag_correlazione WHERE ( msgdiag_correlazione.idmessaggio=msgdiagnostici.idmessaggio ) ) ) +filtro
			 */
			
			//String query=sqlQueryObject.createSQLUnionCount(false, "totale" ,createSQLQueryObjCorrelazione(filter,false),createSqlQueryObjDiagnostici(filter,false),createQueryObjDiagnosticiNotExist(filter,false));
			long totale = 0;
			int totMSG=0;
			//1)
			//Fix 12/03/2008
			//La query viene costruita con l'operatore OR anziche con l'operatore AND
			//imposto l'operatore AND
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addFromTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE);
			sqlQueryObject.addSelectCountField("idmessaggio", "totale");
			
			/* ----- Condizioni di filtro valide per tutte le query  ------*/
			//data inizio
			if(filter.getDataInizio()!=null){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".gdo>=?");
			}
			//data fine
			if(filter.getDataFine()!=null){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".gdo<=?");
			}
			//id busta
			if(filter.getIdBustaRichiesta()!=null && !"".equals(filter.getIdBustaRichiesta())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".idmessaggio=?");
			}
			//nome porta
			if(filter.getNomePorta()!=null && !"".equals(filter.getNomePorta())){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".porta='"+filter.getNomePorta()+"'");
			}
			//is delegata
			if(filter.isDelegata()!=null){
				sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".delegata="+(filter.isDelegata()?1:0));
			}
						
			//Informazioni
			InformazioniProtocollo infoBusta = filter.getInformazioniProtocollo();
			if(infoBusta!=null){
				//soggetto fruitore
				if(infoBusta.getFruitore()!=null){
					if(infoBusta.getFruitore().getTipo()!=null && !"".equals(infoBusta.getFruitore().getTipo())) sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_fruitore='"+infoBusta.getFruitore().getTipo()+"'");
					if(infoBusta.getFruitore().getNome()!=null && !"".equals(infoBusta.getFruitore().getNome()))sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".fruitore='"+infoBusta.getFruitore().getNome()+"'");
				}
				//soggetto erogatore
				if(infoBusta.getErogatore()!=null){
					if(infoBusta.getErogatore().getTipo()!=null && !"".equals(infoBusta.getErogatore().getTipo())) sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_erogatore='"+infoBusta.getErogatore().getTipo()+"'");
					if(infoBusta.getErogatore().getNome()!=null && !"".equals(infoBusta.getErogatore().getNome())) sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".erogatore='"+infoBusta.getErogatore().getNome()+"'");
				}
				//servizio
				if(infoBusta.getServizio()!=null){
					if(infoBusta.getTipoServizio()!=null && !"".equals(infoBusta.getTipoServizio())) sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_servizio='"+infoBusta.getTipoServizio()+"'");
					if(infoBusta.getServizio()!=null && !"".equals(infoBusta.getServizio())) sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".servizio='"+infoBusta.getServizio()+"'");
				}
				//azione
				if(infoBusta.getAzione()!=null && !"".equals(infoBusta.getAzione())){
					sqlQueryObject.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".azione='"+infoBusta.getAzione()+"'");
				}
			}			
			
			this.log.debug("eseguo query:"+ sqlQueryObject.createSQLQuery());
			
			stmt=con.prepareStatement(sqlQueryObject.createSQLQuery());
			
			int count = 1;
			for(int j=1;j<2;j++){
				//data inizio
				if(filter.getDataInizio()!=null){
					stmt.setTimestamp(count, new Timestamp(filter.getDataInizio().getTime()));
					count++;
				}
				//data fine
				if(filter.getDataFine()!=null){
					stmt.setTimestamp(count, new Timestamp(filter.getDataFine().getTime()));
					count++;
				}
				//idbusta
				if(filter.getIdBustaRichiesta()!=null && !"".equals(filter.getIdBustaRichiesta())){
					stmt.setString(count, filter.getIdBustaRichiesta());
					count++;
				}
			}
			totMSG=0;
			rs=stmt.executeQuery();
			if(rs.next()){
				totMSG=rs.getInt("totale");
			}
			rs.close();
			stmt.close();
			
			totale+=totMSG;			
			
			this.log.debug("getTotUnionEntry (Count 1): ["+totMSG+"] entry matchano il filtro :"+filter.toString());
			
			//2)SELECT count(*) as count FROM msgdiagnostici WHERE ( msgdiagnostici.idmessaggio is NULL ) +filtro
			
			//Fix 12/03/2008
			//La query viene costruita con l'operatore OR anziche con l'operatore AND
			//imposto l'operatore AND
			sqlQueryObject2.setANDLogicOperator(true);
			
			sqlQueryObject2.addFromTable(CostantiDB.MSG_DIAGNOSTICI);
			sqlQueryObject2.addSelectCountField("*","totale");
			
			sqlQueryObject2.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".idmessaggio is NULL");
			
			/* ----- Condizioni di filtro valide per tutte le query  ------*/
			//data inizio
			if(filter.getDataInizio()!=null){
				sqlQueryObject2.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".gdo>=?");
			}
			//data fine
			if(filter.getDataFine()!=null){
				sqlQueryObject2.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".gdo<=?");
			}
			//id busta
			if(filter.getIdBustaRichiesta()!=null && !"".equals(filter.getIdBustaRichiesta())){
				sqlQueryObject2.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".idmessaggio=?");
			}
						
			//Informazioni
			infoBusta = filter.getInformazioniProtocollo();
			if(infoBusta!=null){
				//soggetto fruitore
				if(infoBusta.getFruitore()!=null){
					if(infoBusta.getFruitore().getTipo()!=null && !"".equals(infoBusta.getFruitore().getTipo())) sqlQueryObject2.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_fruitore='"+infoBusta.getFruitore().getTipo()+"'");
					if(infoBusta.getFruitore().getNome()!=null && !"".equals(infoBusta.getFruitore().getNome()))sqlQueryObject2.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".fruitore='"+infoBusta.getFruitore().getNome()+"'");
				}
				//soggetto erogatore
				if(infoBusta.getErogatore()!=null){
					if(infoBusta.getErogatore().getTipo()!=null && !"".equals(infoBusta.getErogatore().getTipo())) sqlQueryObject2.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_erogatore='"+infoBusta.getErogatore().getTipo()+"'");
					if(infoBusta.getErogatore().getNome()!=null && !"".equals(infoBusta.getErogatore().getNome())) sqlQueryObject2.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".erogatore='"+infoBusta.getErogatore().getNome()+"'");
				}
				//servizio
				if(infoBusta.getServizio()!=null){
					if(infoBusta.getTipoServizio()!=null && !"".equals(infoBusta.getTipoServizio())) sqlQueryObject2.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_servizio='"+infoBusta.getTipoServizio()+"'");
					if(infoBusta.getServizio()!=null && !"".equals(infoBusta.getServizio())) sqlQueryObject2.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".servizio='"+infoBusta.getServizio()+"'");
				}
				//azione
				if(infoBusta.getAzione()!=null && !"".equals(infoBusta.getAzione())){
					sqlQueryObject2.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".azione='"+infoBusta.getAzione()+"'");
				}
			}			
			
			this.log.debug("eseguo query:"+ sqlQueryObject2.createSQLQuery());
			
			stmt=con.prepareStatement(sqlQueryObject2.createSQLQuery());
			
			count = 1;
			for(int j=1;j<2;j++){
				//data inizio
				if(filter.getDataInizio()!=null){
					stmt.setTimestamp(count, new Timestamp(filter.getDataInizio().getTime()));
					count++;
				}
				//data fine
				if(filter.getDataFine()!=null){
					stmt.setTimestamp(count, new Timestamp(filter.getDataFine().getTime()));
					count++;
				}
				//idbusta
				if(filter.getIdBustaRichiesta()!=null && !"".equals(filter.getIdBustaRichiesta())){
					stmt.setString(count, filter.getIdBustaRichiesta());
					count++;
				}
			}
			totMSG=0;
			rs=stmt.executeQuery();
			if(rs.next()){
				totMSG=rs.getInt("totale");
			}
			rs.close();
			stmt.close();
			
			totale+=totMSG;			
			
			this.log.debug("getTotUnionEntry (Count 2): ["+totMSG+"] entry matchano il filtro :"+filter.toString());
			
			//3SELECT count(idmessaggio) as count FROM msgdiagnostici 
			//WHERE ( NOT EXISTS (SELECT * FROM msgdiag_correlazione WHERE ( msgdiag_correlazione.idmessaggio=msgdiagnostici.idmessaggio ) ) ) +filtro
			if(!filter.isPartial()){
				//Fix 12/03/2008
				//La query viene costruita con l'operatore OR anziche con l'operatore AND
				//imposto l'operatore AND
				sqlQueryObject3.setANDLogicOperator(true);
				
				sqlQueryObject3.addFromTable(CostantiDB.MSG_DIAGNOSTICI);
				sqlQueryObject3.addSelectCountField("idmessaggio","totale");
				
				//subquery
				subquery.addFromTable(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE);
				subquery.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".idmessaggio="+CostantiDB.MSG_DIAGNOSTICI+".idmessaggio");
				//nome porta
				if(filter.getNomePorta()!=null && !"".equals(filter.getNomePorta())){
					subquery.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".porta='"+filter.getNomePorta()+"'");
				}
				//is delegata
				if(filter.isDelegata()!=null){
					subquery.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".delegata="+(filter.isDelegata()?1:0));
				}
				
				sqlQueryObject3.addWhereExistsCondition(true, subquery);
				
				/* ----- Condizioni di filtro valide per tutte le query  ------*/
				//data inizio
				if(filter.getDataInizio()!=null){
					sqlQueryObject3.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".gdo>=?");
				}
				//data fine
				if(filter.getDataFine()!=null){
					sqlQueryObject3.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".gdo<=?");
				}
				//id busta
				if(filter.getIdBustaRichiesta()!=null && !"".equals(filter.getIdBustaRichiesta())){
					sqlQueryObject3.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".idmessaggio=?");
				}
							
				//Informazioni
				infoBusta = filter.getInformazioniProtocollo();
				if(infoBusta!=null){
					//soggetto fruitore
					if(infoBusta.getFruitore()!=null){
						if(infoBusta.getFruitore().getTipo()!=null && !"".equals(infoBusta.getFruitore().getTipo())) sqlQueryObject3.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_fruitore='"+infoBusta.getFruitore().getTipo()+"'");
						if(infoBusta.getFruitore().getNome()!=null && !"".equals(infoBusta.getFruitore().getNome()))sqlQueryObject3.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".fruitore='"+infoBusta.getFruitore().getNome()+"'");
					}
					//soggetto erogatore
					if(infoBusta.getErogatore()!=null){
						if(infoBusta.getErogatore().getTipo()!=null && !"".equals(infoBusta.getErogatore().getTipo())) sqlQueryObject3.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_erogatore='"+infoBusta.getErogatore().getTipo()+"'");
						if(infoBusta.getErogatore().getNome()!=null && !"".equals(infoBusta.getErogatore().getNome())) sqlQueryObject3.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".erogatore='"+infoBusta.getErogatore().getNome()+"'");
					}
					//servizio
					if(infoBusta.getServizio()!=null){
						if(infoBusta.getTipoServizio()!=null && !"".equals(infoBusta.getTipoServizio())) sqlQueryObject3.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".tipo_servizio='"+infoBusta.getTipoServizio()+"'");
						if(infoBusta.getServizio()!=null && !"".equals(infoBusta.getServizio())) sqlQueryObject3.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_CORRELAZIONE+".servizio='"+infoBusta.getServizio()+"'");
					}
					//azione
					if(infoBusta.getAzione()!=null && !"".equals(infoBusta.getAzione())){
						sqlQueryObject3.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI+".azione='"+infoBusta.getAzione()+"'");
					}
				}			
				
				this.log.debug("eseguo query:"+ sqlQueryObject3.createSQLQuery());
				
				stmt=con.prepareStatement(sqlQueryObject3.createSQLQuery());
				
				count = 1;
				for(int j=1;j<2;j++){
					//data inizio
					if(filter.getDataInizio()!=null){
						stmt.setTimestamp(count, new Timestamp(filter.getDataInizio().getTime()));
						count++;
					}
					//data fine
					if(filter.getDataFine()!=null){
						stmt.setTimestamp(count, new Timestamp(filter.getDataFine().getTime()));
						count++;
					}
					//idbusta
					if(filter.getIdBustaRichiesta()!=null && !"".equals(filter.getIdBustaRichiesta())){
						stmt.setString(count, filter.getIdBustaRichiesta());
						count++;
					}
				}
				totMSG=0;
				rs=stmt.executeQuery();
				if(rs.next()){
					totMSG=rs.getInt("totale");
				}
				rs.close();
				stmt.close();
				
				totale+=totMSG;
				this.log.debug("getTotUnionEntry (Count 3): ["+totMSG+"] entry matchano il filtro :"+filter.toString());
			}
			
			this.log.debug("Totale Union Count : "+totale);
			return totale;
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException(e);
		}finally{
			try{
				if(rs!=null) rs.close();
				if(stmt!=null) stmt.close();
			}catch (Exception e) {
				
			}
			
			if(closeConnection){
				try{
					con.close();
				}catch (Exception e) {
					
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


