/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.protocol.basic.tracciamento;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.basic.ProtocolliRegistrati;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracce;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracceConPaginazione;
import org.openspcoop2.protocol.sdk.tracciamento.IDriverTracciamento;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.StringWrapper;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;



/**
 * Interfaccia Tracciamento
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverTracciamento implements IDriverTracciamento {

	/** 
	 * DataSource
	 */
	DataSource datasource = null;
	/** 
	 * Connessione
	 */
	Connection connection = null;
	boolean connectionOpenViaJDBCInCostructor = false;
	/**
	 * SQLQueryObject
	 */
	String tipoDatabase = null;

	/** Logger utilizzato per info. */
	private Logger log = null;

	public final static String IDTRACCIA = "@@@@@-----@@@@-----IDTRACCIA-DB----@@@@@-----@@@@";
	
	/**
	 * Properties
	 */
	private Vector<String> properties;
	public void setProperties(Vector<String> properties) {
		this.properties = properties;
	}
	
	protected IProtocolFactory protocolFactory;
	private ProtocolliRegistrati protocolliRegistrati;
	public DriverTracciamento(IProtocolFactory factory){
		this.log = factory.getLogger();
		this.protocolFactory = factory;
	}
	
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}
	
	public void init(ProtocolliRegistrati protocolliRegistrati,String nomeDataSource, String tipoDatabase, Properties jndiContext) throws DriverTracciamentoException {
		init(protocolliRegistrati,nomeDataSource,tipoDatabase,jndiContext,null);
	}
	
	public void init(ProtocolliRegistrati protocolliRegistrati,String nomeDataSource, String tipoDatabase, Properties jndiContext, Logger log) throws DriverTracciamentoException {
	
		this.protocolliRegistrati = protocolliRegistrati;
		
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverTracciamentoException("Errore durante l'inizializzazione del logger...",e);
		}

		// Datasource
		try {
			this.log.info("Inizializzo DriverLogAnalyzer...");
			GestoreJNDI gestoreJNDI = new GestoreJNDI(jndiContext);
			this.datasource = (DataSource) gestoreJNDI.lookup(nomeDataSource);
			if (this.datasource == null)
				throw new Exception ("datasource is null");

			this.log.info("Inizializzo DriverLogAnalyzer terminata.");
		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del datasource...",e);
			throw new DriverTracciamentoException("Errore durante la ricerca del datasource...",e);
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
			throw new DriverTracciamentoException("Errore durante la ricerca del SQLQueryObject...",e);
		}
		
	}
	
	public void init(ProtocolliRegistrati protocolliRegistrati,DataSource dataSourceObject, String tipoDatabase) throws DriverTracciamentoException {
		init(protocolliRegistrati,dataSourceObject,tipoDatabase,null);
	}
	
	public void init(ProtocolliRegistrati protocolliRegistrati,DataSource dataSourceObject, String tipoDatabase, Logger log) throws DriverTracciamentoException {
		
		this.protocolliRegistrati = protocolliRegistrati;
		
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverTracciamentoException("Errore durante l'inizializzazione del logger...",e);
		}

		// Datasource
		try {
			this.datasource = dataSourceObject;
			if (this.datasource == null)
				throw new Exception ("datasource is null");
		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del datasource...",e);
			throw new DriverTracciamentoException("Errore durante la ricerca del datasource...",e);
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
			throw new DriverTracciamentoException("Errore durante la ricerca del SQLQueryObject...",e);
		}
	}
	
	public void init(ProtocolliRegistrati protocolliRegistrati,Connection connection, String tipoDatabase, Logger log) throws DriverTracciamentoException {
		
		this.protocolliRegistrati = protocolliRegistrati;
		
		// Logger
		try {
			this.log = log;
		} catch (Exception e) {
			throw new DriverTracciamentoException("Errore durante l'inizializzazione del logger...",e);
		}

		// connection
		this.connection = connection;
		
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
			throw new DriverTracciamentoException("Errore durante la ricerca del SQLQueryObject...",e);
		}

	}
	
	public void init(ProtocolliRegistrati protocolliRegistrati,String urlJDBC,String driverJDBC,
			String username,String password, 
			String tipoDatabase, Logger log) throws DriverTracciamentoException {
		
		this.protocolliRegistrati = protocolliRegistrati;
		
		// Logger
		try {
			this.log = log;
		} catch (Exception e) {
			throw new DriverTracciamentoException("Errore durante l'inizializzazione del logger...",e);
		}

		// connection
		try {
			Class.forName(driverJDBC);
			
			if(username!=null){
				this.connection = DriverManager.getConnection(urlJDBC,username,password);
			}else{
				this.connection = DriverManager.getConnection(urlJDBC);
			}
			this.connectionOpenViaJDBCInCostructor = true;
			
		} catch (Exception e) {
			this.log.error("Errore durante l'inizializzazione della connessione...",e);
			throw new DriverTracciamentoException("Errore durante l'inizializzazione della connessione...",e);
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
			throw new DriverTracciamentoException("Errore durante la ricerca del SQLQueryObject...",e);
		}

	}
	

	
	
	
	
	
	
	
	/* *********** ACCESSI TRAMITE RICERCHE ******* */
	
	/**
	 * Si occupa di ritornare il numero di tracce che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return numero di tracce che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public int countTracce(FiltroRicercaTracce filtro) throws DriverTracciamentoException{
		Connection connectionDB = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int contatore = 0;
		
		try{
			
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
		
			ISQLQueryObject sqlQueryObject = 
				DriverTracciamentoUtilities.createSQLQueryObj_countTracce(filtro, this.tipoDatabase);
			String sql = sqlQueryObject.createSQLQuery();
			
			StringWrapper sqlDebug = new StringWrapper(sqlQueryObject.createSQLQuery());
			DriverTracciamentoUtilities.setValuesSearch(sqlDebug, filtro, 1);
			this.log.debug("Query: "+sqlDebug);
			
			pstmt = connectionDB.prepareStatement(sql);
			DriverTracciamentoUtilities.setValuesSearch(pstmt, filtro, 1);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				contatore = rs.getInt("countTracce");	
			}
			rs.close();
			pstmt.close();
			
			this.log.debug("Query found "+contatore+" rows");
			
			return contatore;
			
		}catch(Exception e){
			throw new DriverTracciamentoException("Tracciamento exception: "+e.getMessage(),e);
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				pstmt.close();
			}catch(Exception eClose){}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){}
		}
	}
	
	
	/**
	 * Si occupa di ritornare le tracce che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return tracce che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public List<Traccia> getTracce(FiltroRicercaTracceConPaginazione filtro)  throws DriverTracciamentoException, DriverTracciamentoNotFoundException{
		Connection connectionDB = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Traccia> tracce = new ArrayList<Traccia>();
		
		try{
			
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObject = 
				DriverTracciamentoUtilities.createSQLQueryObj_searchTracce(filtro, this.tipoDatabase);
			String sql = sqlQueryObject.createSQLQuery();
			
			StringWrapper sqlDebug = new StringWrapper(sqlQueryObject.createSQLQuery());
			DriverTracciamentoUtilities.setValuesSearch(sqlDebug, filtro, 1);
			this.log.debug("Query: "+sqlDebug);
			
			pstmt = connectionDB.prepareStatement(sql);
			DriverTracciamentoUtilities.setValuesSearch(pstmt, filtro, 1);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				
				Long idTraccia = rs.getLong("idTraccia");
				Traccia traccia = 
					DriverTracciamentoUtilities.getTraccia(connectionDB, this.tipoDatabase, 
							this.log, idTraccia, this.properties,
							this.protocolliRegistrati);
				tracce.add(traccia);
			}
			rs.close();
			pstmt.close();
			
			this.log.debug("Query found "+tracce.size()+" rows");
			
			if(tracce.size()>0)
				return tracce;
			else
				throw new DriverTracciamentoNotFoundException("Non sono state trovate tracce che rispettano i criteri di ricerca impostati");
			
		}catch(DriverTracciamentoNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new DriverTracciamentoException("Tracciamento exception: "+e.getMessage(),e);
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				pstmt.close();
			}catch(Exception eClose){}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){}
		}
	}
	
	/**
	 * Si occupa di eliminare le tracce che rispettano il filtro di ricerca
	 * 
	 * @param filtro Filtro di ricerca
	 * @return numero di tracce eliminate
	 * @throws DriverTracciamentoException
	 */
	@Override
	public int deleteTracce(FiltroRicercaTracce filtro) throws DriverTracciamentoException{
		int deleted = 0; 
		Connection con = getConnection();
		PreparedStatement stmt = null;
		
		try {
			
			ISQLQueryObject sqlQueryObject = 
				DriverTracciamentoUtilities.createSQLQueryObj_deleteTracce(filtro, this.tipoDatabase);
			String sql = sqlQueryObject.createSQLDelete();
			
			StringWrapper sqlDebug = new StringWrapper(sqlQueryObject.createSQLDelete());
			DriverTracciamentoUtilities.setValuesSearch(sqlDebug, filtro, 1);
			this.log.debug("Delete: "+sqlDebug);
			
			stmt = con.prepareStatement(sql);
			DriverTracciamentoUtilities.setValuesSearch(stmt, filtro, 1);
			deleted = stmt.executeUpdate();	
			this.log.debug("Deleted "+deleted+" rows");
			
			return deleted;
			
		} catch (Exception e) {
			throw new DriverTracciamentoException(e);
		} finally {
			if(stmt!=null) 
				try{ stmt.close(); } catch (SQLException e) {}
			releaseConnection(con);
			this.log.debug("Tracce Eliminate: " + deleted );
		}
	}
	
	
	
	
	
	
	

	
	/* ******* ACCESSI PUNTUALI ********** */
	
	/**
	 * Recupera la traccia
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	@Override
	public Traccia getTraccia(String idBusta,IDSoggetto codicePorta) throws DriverTracciamentoException, DriverTracciamentoNotFoundException{
		return getTraccia_engine(idBusta,codicePorta,false);
	} 
	
	/**
	 * Recupera la traccia
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	@Override
	public Traccia getTraccia(String idBusta,IDSoggetto codicePorta,boolean ricercaIdBustaComeRiferimentoMessaggio) throws DriverTracciamentoException, DriverTracciamentoNotFoundException{
		return getTraccia_engine(idBusta,codicePorta,ricercaIdBustaComeRiferimentoMessaggio);
	} 
	
	private Traccia getTraccia_engine(String idBusta,IDSoggetto codicePorta,boolean rifMsg) throws DriverTracciamentoException, DriverTracciamentoNotFoundException{
		
		Connection connectionDB = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Traccia tr = null;
		
		try{
			
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObject = DriverTracciamentoUtilities.getSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.TRACCE);
			sqlQueryObject.addSelectField("id");
			if(rifMsg){
				sqlQueryObject.addWhereCondition("rif_messaggio=?");
			}else{
				sqlQueryObject.addWhereCondition("id_messaggio=?");
			}
			if(DriverTracciamentoUtilities.isDefined(codicePorta)){
				if(DriverTracciamentoUtilities.isDefined(codicePorta.getCodicePorta())){
					sqlQueryObject.addWhereCondition("pdd_codice=?");
				}
				if(DriverTracciamentoUtilities.isDefined(codicePorta.getTipo())){
					sqlQueryObject.addWhereCondition("pdd_tipo_soggetto=?");
				}
				if(DriverTracciamentoUtilities.isDefined(codicePorta.getNome())){
					sqlQueryObject.addWhereCondition("pdd_nome_soggetto=?");
				}
			}
			sqlQueryObject.setANDLogicOperator(true);
			
			pstmt = connectionDB.prepareStatement(sqlQueryObject.toString());
			int index = 1;
			pstmt.setString(index++, idBusta);
			if(DriverTracciamentoUtilities.isDefined(codicePorta)){
				if(DriverTracciamentoUtilities.isDefined(codicePorta.getCodicePorta())){
					pstmt.setString(index++, codicePorta.getCodicePorta());
				}
				if(DriverTracciamentoUtilities.isDefined(codicePorta.getTipo())){
					pstmt.setString(index++, codicePorta.getTipo());
				}
				if(DriverTracciamentoUtilities.isDefined(codicePorta.getNome())){
					pstmt.setString(index++, codicePorta.getNome());
				}
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()){
				
				Long idTraccia = rs.getLong("id");
				tr =  DriverTracciamentoUtilities.getTraccia(connectionDB, this.tipoDatabase, 
						this.log, idTraccia, this.properties, this.protocolliRegistrati);
				
			}
			rs.close();
			pstmt.close();
			
			if(tr==null){
				throw new DriverTracciamentoNotFoundException("Traccia non trovata (idBusta:"+idBusta+") (dominio-porta:"+codicePorta+") (rifmsg:"+rifMsg+")");
			}
			return tr;
			
		}catch(DriverTracciamentoNotFoundException d){
			throw d;
		}catch(Exception e){
			throw new DriverTracciamentoException("Tracciamento exception: "+e.getMessage(),e);
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				pstmt.close();
			}catch(Exception eClose){}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){}
		}
	} 
	
	
	
	/**
	 * Recupera la traccia in base ad una serie di properties
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	@Override
	public Traccia getTraccia(TipoTraccia tipoTraccia,Hashtable<String, String> propertiesRicerca) throws DriverTracciamentoException, DriverTracciamentoNotFoundException{
		
		Connection connectionDB = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Traccia tr = null;
		
		try{
			
			// Get Connection
			if(this.connection!=null)
				connectionDB = this.connection;
			else{
				connectionDB = this.datasource.getConnection();
				if(connectionDB==null)
					throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");
			}
			
			ISQLQueryObject sqlQueryObject = DriverTracciamentoUtilities.getSQLQueryObject(this.tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.TRACCE);
			sqlQueryObject.addSelectField("id");
			if(propertiesRicerca!=null && propertiesRicerca.size()>0){
				Enumeration<String> keys = propertiesRicerca.keys();
				while(keys.hasMoreElements()){
					String key = keys.nextElement();
					if(DriverTracciamento.IDTRACCIA.equals(key)){
						// Caso particolare dell'id long della traccia
						sqlQueryObject.addWhereCondition("id=?");
					}else{
						sqlQueryObject.addWhereCondition(key+"=?");
					}
				}
			}else{
				throw new Exception("Properties di ricerca non fornite");
			}
			sqlQueryObject.addWhereCondition("tipo_messaggio=?");
			sqlQueryObject.setANDLogicOperator(true);
			
			pstmt = connectionDB.prepareStatement(sqlQueryObject.toString());
			int index = 1;
			Enumeration<String> keys = propertiesRicerca.keys();
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				String value = propertiesRicerca.get(key);
				if(DriverTracciamento.IDTRACCIA.equals(key)){
					// Caso particolare dell'id long della traccia
					pstmt.setLong(index, Long.parseLong(value));
				}else{
					pstmt.setString(index, value);
				}
				index++;
			}
			pstmt.setString(index, tipoTraccia.toString());
			rs = pstmt.executeQuery();
			if(rs.next()){
				
				Long idTraccia = rs.getLong("id");
				tr =  DriverTracciamentoUtilities.getTraccia(connectionDB, this.tipoDatabase, 
						this.log, idTraccia, this.properties, this.protocolliRegistrati);

			}
			rs.close();
			pstmt.close();
			
			if(tr==null){
				throw new DriverTracciamentoNotFoundException("Traccia non trovata (sizeProperties:"+propertiesRicerca.size()+") (tipo:"+tipoTraccia+")");
			}
			return tr;
			
		}catch(DriverTracciamentoNotFoundException d){
			throw d;
		}catch(Exception e){
			throw new DriverTracciamentoException("Tracciamento exception: "+e.getMessage(),e);
		}finally{
			try{
				rs.close();
			}catch(Exception eClose){}
			try{
				pstmt.close();
			}catch(Exception eClose){}
			try{
				if(this.connection==null)
					connectionDB.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	/* ******* RISORSE INTERNE ********** */
	
	@Override
	public void close() throws DriverTracciamentoException {
		try{
			if(this.connectionOpenViaJDBCInCostructor){
				if(this.connection!=null && this.connection.isClosed()==false){
					this.connection.close();
				}
			}
		}catch(Exception e){
			throw new DriverTracciamentoException(e.getMessage(),e);
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


