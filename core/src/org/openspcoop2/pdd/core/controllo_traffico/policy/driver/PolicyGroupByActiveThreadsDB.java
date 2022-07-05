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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupBy;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.driver.CostantiControlloTraffico;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDateUtils;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.jdbc.IJDBCAdapter;
import org.openspcoop2.utils.jdbc.JDBCAdapterFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.serialization.JavaDeserializer;
import org.openspcoop2.utils.serialization.JavaSerializer;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**     
 * PolicyGroupByActiveThreads
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyGroupByActiveThreadsDB implements Serializable,IPolicyGroupByActiveThreadsInMemory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CT_MAP_TABLE = "ct_map";
	private static final String CT_MAP_COLUMN_KEY = "map_key";
	private static final String CT_MAP_COLUMN_UPDATE_TIME = "map_update_time";
	private static final String CT_MAP_COLUMN_VALUE = "map_value";
	
	private boolean mapExists = false;
	
	private ActivePolicy activePolicy;
	private PolicyGroupByActiveThreadsType tipoGestore;
	private String uniqueIdMap_idActivePolicy;
	private Date uniqueIdMap_updateTime;
	private IState state;
	private IDSoggetto dominio;
	private String idTransazione;
	private OpenSPCoop2Properties op2Properties; 
	private String tipoDatabase;
	private IJDBCAdapter jdbcAdapter;
	private JavaSerializer javaSerializer = null;
	private JavaDeserializer javaDeserializer = null;
	private Logger log;
	private Logger logSql;
	private boolean debug;
	private boolean transactionMode = true;
	
	private long attesaAttivaJDBC;
	private int checkIntervalloJDBC;

	public PolicyGroupByActiveThreadsDB(ActivePolicy activePolicy, PolicyGroupByActiveThreadsType tipoGestore, String uniqueIdMap, IState state,IDSoggetto dominio, String idTransazione) throws PolicyException {
		this.activePolicy = activePolicy;
		this.tipoGestore = tipoGestore;
		this.uniqueIdMap_idActivePolicy = UniqueIdentifierUtilities.extractIdActivePolicy(uniqueIdMap);
		try {
			this.uniqueIdMap_updateTime = UniqueIdentifierUtilities.extractUpdateTimeActivePolicy(uniqueIdMap);
		}catch(Exception e) {
			throw new PolicyException(e.getMessage(),e);
		}
		this.state = state;
		this.dominio = dominio;
		this.idTransazione = idTransazione;
		this.op2Properties = OpenSPCoop2Properties.getInstance();
		this.tipoDatabase = this.op2Properties.getDatabaseType();
		try {
			this.jdbcAdapter = JDBCAdapterFactory.createJDBCAdapter(this.tipoDatabase);
		}catch(Exception e){
			throw new PolicyException("[createJDBCAdapter] "+e.getMessage(),e);
		}
		this.javaSerializer = new JavaSerializer();
		this.javaDeserializer = new JavaDeserializer();
		this.debug = this.op2Properties.isControlloTrafficoDebug();
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(this.debug);
		this.logSql = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTrafficoSql(this.debug);
		
		this.transactionMode = this.op2Properties.isControlloTrafficoGestorePolicyInMemoryDatabase_useTransaction();
		this.attesaAttivaJDBC = this.op2Properties.getControlloTrafficoGestorePolicyInMemoryDatabase_serializableDB_AttesaAttiva();
		this.checkIntervalloJDBC = this.op2Properties.getControlloTrafficoGestorePolicyInMemoryDatabase_serializableDB_CheckInterval();
				
	}
	
	
	@Override
	public ActivePolicy getActivePolicy() {
		return this.activePolicy;
	}
	
	@Override
	public Map<IDUnivocoGroupByPolicy, DatiCollezionati> getMapActiveThreads(){
		
		PolicyConnessioneRuntime resource = null;
		try {
			resource = getConnessione(this.state, this.dominio, "getMapActiveThreads", this.idTransazione);
			
			//checkMap(resource.con);
			
			return _getMapActiveThreads(resource.con);
			
		}
		catch(Throwable e) {
			this.log.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
		finally {
			if(resource!=null) {
				releaseConnessione(resource, this.dominio, "getMapActiveThreads");
			}
		}
	}
	
	@Override
	public void initMap(Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		
		// Non serve essendo già persistente, si rischia di salvare una precedente immagine.
		
		/*
		if(map!=null && map.size()>0){
			PolicyConnessioneRuntime resource = null;
			try {
				resource = getConnessione(this.state, this.dominio, "initMap", this.idTransazione);
				
				checkMap(resource.con);
				
				_initMap(resource.con, map);
				
			}
			catch(Throwable e) {
				this.log.error(e.getMessage(),e);
				throw new RuntimeException(e.getMessage(),e);
			}
			finally {
				if(resource!=null) {
					releaseConnessione(resource, this.dominio, "initMap");
				}
			}
		}
		*/
		
	}
	
	@Override
	public void resetCounters(){
		
		PolicyConnessioneRuntime resource = null;
		try {
			resource = getConnessione(this.state, this.dominio, "resetCounters", this.idTransazione);
			
			//checkMap(resource.con);
			
			_resetCounters(resource.con);
			
		}
		catch(Throwable e) {
			this.log.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
		finally {
			if(resource!=null) {
				releaseConnessione(resource, this.dominio, "resetCounters");
			}
		}
		
	}
	
	@Override
	public void remove() throws UtilsException{
		
		PolicyConnessioneRuntime resource = null;
		try {
			resource = getConnessione(this.state, this.dominio, "remove", this.idTransazione);
			
			//checkMap(resource.con);
			
			_remove(resource.con);
			
		}
		catch(Throwable e) {
			this.log.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
		finally {
			if(resource!=null) {
				releaseConnessione(resource, this.dominio, "remove");
			}
		}
		
	}
	
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<String, Object> ctx) throws PolicyException{
		
		PolicyConnessioneRuntime resource = null;
		DatiCollezionati datiCollezionatiReaded = null;
		try {
			resource = getConnessione(this.state, this.dominio, "registerStartRequest", idTransazione);
			
			checkMap(resource.con);
			
			// mi salvo fuori dal synchronized l'attuale stato
			datiCollezionatiReaded = _registerStartRequest(resource.con, log, idTransazione, datiGroupBy, ctx); 
			
		}finally {
			if(resource!=null) {
				releaseConnessione(resource, this.dominio, "registerStartRequest");
			}
		}
		
		// Tutti i restanti controlli sono effettuati usando il valore di datiCollezionatiReaded, che e' gia' stato modificato
		// Inoltre e' stato re-inserito nella map come oggetto nuovo, quindi il valore dentro il metodo non subira' trasformazioni (essendo stato fatto il deserialize da db)
		// E' possibile procedere con l'analisi rispetto al valore che possiedono il counter dentro questo scope.
		
		return datiCollezionatiReaded;

	}
	
	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<String, Object> ctx) throws PolicyException,PolicyNotFoundException{
		
		PolicyConnessioneRuntime resource = null;
		DatiCollezionati datiCollezionatiReaded = null;
		try {
			resource = getConnessione(this.state, this.dominio, "updateDatiStartRequestApplicabile", idTransazione);
			
			//checkMap(resource.con);
			
			// mi salvo fuori dal synchronized l'attuale stato
			datiCollezionatiReaded = _updateDatiStartRequestApplicabile(resource.con, log, idTransazione, datiGroupBy, ctx);
			
		}finally {
			if(resource!=null) {
				releaseConnessione(resource, this.dominio, "updateDatiStartRequestApplicabile");
			}
		}
		
		// Tutti i restanti controlli sono effettuati usando il valore di datiCollezionatiReaded, che e' gia' stato modificato
		// Inoltre e' stato re-inserito nella map come oggetto nuovo, quindi il valore dentro il metodo non subira' trasformazioni (essendo stato fatto il deserialize da db)
		// E' possibile procedere con l'analisi rispetto al valore che possiedono il counter dentro questo scope.
		
		return datiCollezionatiReaded;

	}
	
	@Override
	public void registerStopRequest(Logger log, String idTransazione,IDUnivocoGroupByPolicy datiGroupBy, Map<String, Object> ctx, 
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata) throws PolicyException,PolicyNotFoundException{
		
		PolicyConnessioneRuntime resource = null;
		try {
			resource = getConnessione(this.state, this.dominio, "registerStopRequest", idTransazione);
			
			//checkMap(resource.con);
			
			_registerStopRequest(resource.con, log, idTransazione,datiGroupBy, ctx,
					dati, isApplicabile, isViolata);
			
		}finally {
			if(resource!=null) {
				releaseConnessione(resource, this.dominio, "registerStopRequest");
			}
		}
		
	}

	
	@Override
	public long getActiveThreads(){
		return this.getActiveThreads(null);
	}
	@Override
	public long getActiveThreads(IDUnivocoGroupByPolicy filtro){
		
		PolicyConnessioneRuntime resource = null;
		try {
			resource = getConnessione(this.state, this.dominio, "getActiveThreads", this.idTransazione);
			
			//checkMap(resource.con);
			
			return _getActiveThreads(resource.con, filtro);
			
		}
		catch(Throwable e) {
			this.log.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
		finally {
			if(resource!=null) {
				releaseConnessione(resource, this.dominio, "getActiveThreads");
			}
		}
		
	}
	
	@Override
	public String printInfos(Logger log, String separatorGroups) throws UtilsException{
		
		PolicyConnessioneRuntime resource = null;
		try {
			resource = getConnessione(this.state, this.dominio, "printInfos", this.idTransazione);
			
			//checkMap(resource.con);
			
			return _printInfos(resource.con, log, separatorGroups);
			
		}
		catch(Throwable e) {
			this.log.error(e.getMessage(),e);
			throw new RuntimeException(e.getMessage(),e);
		}
		finally {
			if(resource!=null) {
				releaseConnessione(resource, this.dominio, "printInfos");
			}
		}
		
	}
	
	private PolicyConnessioneRuntime getConnessione(IState state,IDSoggetto dominio, String funzione, String idTransazione) throws PolicyException {
		
		DBManager dbManager = null;
		Resource r = null;
		String modulo = null;
		try {
			Connection con = null;
				
			if(state!=null) {
				if(state instanceof StateMessage) {
					StateMessage s = (StateMessage) state;
					if(s.getConnectionDB()!=null && !s.getConnectionDB().isClosed()) {
						con = s.getConnectionDB();
						if(con!=null) {
							PolicyConnessioneRuntime cr = new PolicyConnessioneRuntime();
							cr.con = con;
							return cr;
						}
					}
				}
			}
			
			if(dominio==null) {
				dominio = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(null);
			}
			
			modulo = "RateLimitingActiveThreadsDB"+"."+funzione;
			dbManager = DBManager.getInstance();
			r = dbManager.getResource(dominio, modulo, idTransazione);
			if(r==null){
				throw new Exception("Risorsa al database non disponibile");
			}
			con = (Connection) r.getResource();
			if(con == null)
				throw new Exception("Connessione non disponibile");	
			
			PolicyConnessioneRuntime cr = new PolicyConnessioneRuntime();
			cr.con = con;
			cr.r = r;
			return cr;
		}catch(Throwable e) {
			if(r!=null) {
				try {
					dbManager.releaseResource(dominio, modulo, r);
				}catch(Throwable eClose) {}
			}
			throw new PolicyException(e.getMessage(),e);
		}
		
	}
	
	private void releaseConnessione(PolicyConnessioneRuntime p, IDSoggetto dominio, String funzione) {
		if(p.r!=null) {
			if(dominio==null) {
				dominio = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(null);
			}
			DBManager.getInstance().releaseResource(dominio, "RateLimitingActiveThreadsDB"+"."+funzione, p.r);
		}
	}
	
	private final org.openspcoop2.utils.Semaphore lock_checkMap = new org.openspcoop2.utils.Semaphore("PolicyGroupByActiveThreadsDB_checkMap");
	private void checkMap(Connection con) throws PolicyException {
		if(!this.mapExists) {
			_checkMap(con);
		}
	}
	private void _checkMap(Connection con) throws PolicyException {
		
		this.lock_checkMap.acquireThrowRuntime("checkMap");
		try {

			if(!this.mapExists) {
			
				PreparedStatement pstmt = null;
				ResultSet rs = null;
				try {
				
					ISQLQueryObject sqlGet = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
					sqlGet.addSelectField(CT_MAP_COLUMN_KEY);
					sqlGet.addFromTable(CT_MAP_TABLE);
					sqlGet.setANDLogicOperator(true);
					sqlGet.addWhereCondition(CT_MAP_COLUMN_KEY+"=?");
					String query = sqlGet.createSQLQuery();
					if(this.debug) {
						this.logSql.debug("[checkMap"+""+this.idTransazione+"] execute "+DBUtils.formatSQLString(query, this.uniqueIdMap_idActivePolicy));
					}
					pstmt = con.prepareStatement(query);
					pstmt.setString(1, this.uniqueIdMap_idActivePolicy);
					rs = pstmt.executeQuery();
					if(rs == null) {
						pstmt.close();
						throw new UtilsException("CheckMap failed: ResultSet is null?");		
					}
					boolean exist = rs.next();
					if(this.debug) {
						this.logSql.debug("[checkMap"+""+this.idTransazione+"] executed (result:"+exist+") "+DBUtils.formatSQLString(query, this.uniqueIdMap_idActivePolicy));
					}
					rs.close();
					pstmt.close();
					
					if(!exist) {
						
						try{
							if(con.isClosed()){
								throw new UtilsException("Connessione risulta già chiusa");
							}
						}catch(Exception e){
							throw new UtilsException("CheckMap failed: connection closed; "+e.getMessage(),e);
						}

						boolean originalConnectionAutocommit = false;
						boolean autoCommitModificato = false;
						try{
							originalConnectionAutocommit = con.getAutoCommit();
						}catch(Exception e){
							throw new UtilsException("CheckMap failed: autocommit mode disabled; "+e.getMessage(),e); 
						}
						if(originalConnectionAutocommit==false){
							throw new UtilsException("CheckMap failed: autocommit mode disabled; non e' possibile fornire una connessione con autocommit disabilitato poiche' l'utility ha necessita' di effettuare operazioni di commit/rollback)");		
						}
						
						int originalConnectionTransactionIsolation = -1;
						boolean transactionIsolationModificato = false;
						try{
							originalConnectionTransactionIsolation = con.getTransactionIsolation();
						}catch(Exception e){
							throw new UtilsException("Lettura livello di isolamento transazione della Connessione non riuscito: "+e.getMessage(),e); 
						}
						
						try {
							
							try{				

								//System.out.println("SET TRANSACTION SERIALIZABLE ("+conDB.getTransactionIsolation()+","+conDB.getAutoCommit()+")");
								// Il rollback, non servirebbe, pero le WrappedConnection di JBoss hanno un bug, per cui alcune risorse non vengono rilasciate.
								// Con il rollback tali risorse vengono rilasciate, e poi effettivamente la ConnectionSottostante emette una eccezione.
								try{
									con.rollback();
								}catch(Exception e){
									//System.out.println("ROLLBACK ERROR: "+e.getMessage());
								}
								
								JDBCUtilities.setTransactionIsolationSerializable(this.tipoDatabase, con);
								transactionIsolationModificato = true;
																
								if(originalConnectionAutocommit){
									con.setAutoCommit(false);
									autoCommitModificato = true;
								}
								
							} catch(Exception er) {
								throw new UtilsException("CheckMap failed: setting connection error; "+er.getMessage(),er);		
							}
							
							long scadenzaWhile = DateManager.getTimeMillis()
									+ this.attesaAttivaJDBC;
							boolean transactionInsertFinished = false;
							int iteration = 0;
							
							while(transactionInsertFinished==false && DateManager.getTimeMillis() < scadenzaWhile){
								
								try {
									sqlGet = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
									sqlGet.addSelectField(CT_MAP_COLUMN_KEY);
									sqlGet.addFromTable(CT_MAP_TABLE);
									sqlGet.setANDLogicOperator(true);
									sqlGet.addWhereCondition(CT_MAP_COLUMN_KEY+"=?");
									sqlGet.setSelectForUpdate(true);
									
									query = sqlGet.createSQLQuery();
									if(this.debug) {
										this.logSql.debug("[checkMap"+""+this.idTransazione+"] (forUpdate) execute "+DBUtils.formatSQLString(query, this.uniqueIdMap_idActivePolicy));
									}
									pstmt = con.prepareStatement(query);
									pstmt.setString(1, this.uniqueIdMap_idActivePolicy);
									rs = pstmt.executeQuery();
									if(rs == null) {
										pstmt.close();
										throw new UtilsException("CheckMap failed: ResultSet is null?");		
									}
									exist = rs.next();
									if(this.debug) {
										this.logSql.debug("[checkMap"+""+this.idTransazione+"] (forUpdate) executed (result:"+exist+") "+DBUtils.formatSQLString(query, this.uniqueIdMap_idActivePolicy));
									}
									//System.out.println("@check SELECT result:"+exist);
									rs.close();
									pstmt.close();
									
									if(!exist) {
										
										Map<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = new HashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();
										ByteArrayOutputStream bout = new ByteArrayOutputStream();
										this.javaSerializer.writeObject(mapActiveThreads, bout);
										bout.flush();
										bout.close();
										
										StringBuilder queryInsert = new StringBuilder();
										ISQLQueryObject sqlInsert = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
										sqlInsert.addInsertTable(CT_MAP_TABLE);
										sqlInsert.addInsertField(CT_MAP_COLUMN_KEY, "?");
										sqlInsert.addInsertField(CT_MAP_COLUMN_UPDATE_TIME, "?");
										sqlInsert.addInsertField(CT_MAP_COLUMN_VALUE, "?");
										queryInsert.append(sqlInsert.createSQLInsert());
										//System.out.println("INSERT ["+queryInsert.toString()+"]");
										
										if(this.debug) {
											this.logSql.debug("[checkMap"+""+this.idTransazione+"] execute "+DBUtils.formatSQLString(queryInsert.toString(), 
													this.uniqueIdMap_idActivePolicy, 
													DateUtils.getSimpleDateFormatMs().format(this.uniqueIdMap_updateTime), 
													("blob-size:"+bout.size())));
										}
										
										pstmt = con.prepareStatement(queryInsert.toString());
										pstmt.setString(1, this.uniqueIdMap_idActivePolicy);
										Timestamp t = new Timestamp(this.uniqueIdMap_updateTime.getTime());
										pstmt.setTimestamp(2, t);
										this.jdbcAdapter.setBinaryData(pstmt, 3, bout.toByteArray());
										int rows = pstmt.executeUpdate();
										pstmt.close();
										
										if(this.debug) {
											this.logSql.debug("[checkMap"+""+this.idTransazione+"] executed (rows:"+rows+") "+DBUtils.formatSQLString(queryInsert.toString(), 
													this.uniqueIdMap_idActivePolicy, 
													DateUtils.getSimpleDateFormatMs().format(this.uniqueIdMap_updateTime), 
													("blob-size:"+bout.size())));
										}
										
										//System.out.println("@check INSERT rows:"+rows);
										
									}
									
									// Chiusura Transazione
									con.commit();
	
									// ID Costruito
									transactionInsertFinished = true;
									
								} catch(Throwable e) {
									if(this.debug) {
										this.logSql.debug("Transaction error: "+e.getMessage(),e);
									}
									//System.out.println("ERRORE INSERT ("+iteration+"): "+e.getMessage());
									//log.info("ERROR GET SERIAL SQL ["+e.getMessage()+"]");
									try{
										if( rs != null )
											rs.close();
									} catch(Exception er) {}
									try{
										if( pstmt != null )
											pstmt.close();
									} catch(Exception er) {}
									try{
										con.rollback();
									} catch(Exception er) {}
								}

								if(transactionInsertFinished == false){
									// Per aiutare ad evitare conflitti
									try{
										int sleep = (new java.util.Random()).nextInt(this.checkIntervalloJDBC);
										//System.out.println("Sleep: "+sleep);
										Utilities.sleep(sleep); // random
									}catch(Exception eRandom){}
								}
								
								iteration++;
								
							}
							
							if(!transactionInsertFinished) {
								throw new Exception("Check non riuscito dopo '"+iteration+"' tentativi");
							}
							
						}
						finally{

							// Ripristino Transazione
							try{
								if(transactionIsolationModificato){
									con.setTransactionIsolation(originalConnectionTransactionIsolation);
								}
								if(autoCommitModificato){
									con.setAutoCommit(originalConnectionAutocommit);
								}
							} catch(Exception er) {
								//System.out.println("ERROR UNSET:"+er.getMessage());
								throw new UtilsException("CheckMap failed: unsetting connection error; "+er.getMessage());
							}
						}
						
					}
					else {
						
						this.mapExists = true;
						
					}
					
				}catch(Throwable e) {
					throw new PolicyException(e.getMessage(),e);
				}
				
				
				
			}
			
		}finally {
			this.lock_checkMap.release("checkMap");
		}
	}
	
	private Map<IDUnivocoGroupByPolicy, DatiCollezionati> _getMapActiveThreads(Connection con){
		try {
			return _updateMap(con, OperationType.getMapActiveThreads,
					this.log, this.idTransazione, null, null,
					null, false, false,
					null,
					null).map;
		}catch(Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	@SuppressWarnings("unused")
	private void _initMap(Connection con, Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) {
		try {
			if(map!=null && map.size()>0){
				_updateMap(con, OperationType.initMap,
						this.log, this.idTransazione, null, null,
						null, false, false,
						null,
						map);
			}
		}catch(Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	private DatiCollezionati _registerStartRequest(Connection con,Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<String, Object> ctx) throws PolicyException {
		try {
			return _updateMap(con, OperationType.registerStartRequest,
					log, idTransazione, datiGroupBy, ctx,
					null, false, false,
					null,
					null).datiCollezionatiReaded;
		}catch(PolicyNotFoundException e) {
			throw new PolicyException(e.getMessage(),e);
		}
	}
	private DatiCollezionati _updateDatiStartRequestApplicabile(Connection con,Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<String, Object> ctx) throws PolicyException,PolicyNotFoundException{
		return _updateMap(con, OperationType.updateDatiStartRequestApplicabile,
				log, idTransazione, datiGroupBy, ctx,
				null, false, false,
				null,
				null).datiCollezionatiReaded;
	}
	private void _registerStopRequest(Connection con,Logger log, String idTransazione,IDUnivocoGroupByPolicy datiGroupBy, Map<String, Object> ctx, 
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata) throws PolicyException,PolicyNotFoundException{
		_updateMap(con, OperationType.registerStopRequest,
				log, idTransazione, datiGroupBy, ctx,
				dati, isApplicabile, isViolata,
				null,
				null);
	}
	private void _resetCounters(Connection con){
		try {
			_updateMap(con, OperationType.resetCounters,
					this.log, this.idTransazione, null, null,
					null, false, false,
					null,
					null);
		}catch(Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	private void _remove(Connection con){
		try {
			_updateMap(con, OperationType.remove,
					this.log, this.idTransazione, null, null,
					null, false, false,
					null,
					null);
		}catch(Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	private long _getActiveThreads(Connection con,IDUnivocoGroupByPolicy filtro){
		try {
			return _updateMap(con, OperationType.getActiveThreads,
					this.log, this.idTransazione, filtro, null,
					null, false, false,
					null,
					null).counter;
		}catch(Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	private String _printInfos(Connection con,Logger log, String separatorGroups) throws UtilsException{
		try {
			return _updateMap(con, OperationType.printInfos,
					log, this.idTransazione, null, null,
					null, false, false,
					separatorGroups,
					null).info;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	@SuppressWarnings({ "unchecked"})
	private UpdateResult _updateMap(Connection con, OperationType opType,
			Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, Map<String, Object> ctx,
			MisurazioniTransazione dati, boolean isApplicabile, boolean isViolata,
			String separatorGroups,
			Map<IDUnivocoGroupByPolicy, DatiCollezionati> map) throws PolicyException, PolicyNotFoundException {
			
		UpdateResult updateResult = new UpdateResult();
		
		try{
			if(con.isClosed()){
				throw new UtilsException("Connessione risulta già chiusa");
			}
		}catch(Exception e){
			throw new PolicyException("CheckMap failed: connection closed; "+e.getMessage(),e);
		}

		boolean originalConnectionAutocommit = false;
		boolean autoCommitModificato = false;
		try{
			originalConnectionAutocommit = con.getAutoCommit();
		}catch(Exception e){
			throw new PolicyException("CheckMap failed: autocommit mode disabled; "+e.getMessage(),e); 
		}
		if(originalConnectionAutocommit==false){
			throw new PolicyException("CheckMap failed: autocommit mode disabled; non e' possibile fornire una connessione con autocommit disabilitato poiche' l'utility ha necessita' di effettuare operazioni di commit/rollback)");		
		}
				
		try {
			
			if(this.transactionMode) {
				try{				
	
					//System.out.println("SET TRANSACTION SERIALIZABLE ("+conDB.getTransactionIsolation()+","+conDB.getAutoCommit()+")");
					// Il rollback, non servirebbe, pero le WrappedConnection di JBoss hanno un bug, per cui alcune risorse non vengono rilasciate.
					// Con il rollback tali risorse vengono rilasciate, e poi effettivamente la ConnectionSottostante emette una eccezione.
					try{
						con.rollback();
					}catch(Exception e){
						//System.out.println("ROLLBACK ERROR: "+e.getMessage());
					}
								
					if(originalConnectionAutocommit){
						con.setAutoCommit(false);
						autoCommitModificato = true;
					}
					
				} catch(Exception er) {
					throw new PolicyException("CheckMap failed: setting connection error; "+er.getMessage(),er);		
				}
			}
			
			long scadenzaWhile = DateManager.getTimeMillis()
					+ this.attesaAttivaJDBC;
			boolean transactionUpdateFinished = false;
			
			int iteration = 0;
			
			PolicyNotFoundException policyNotFoundException = null;
			PolicyException policyException = null;
			
			while(transactionUpdateFinished==false && DateManager.getTimeMillis() < scadenzaWhile){
				
				PreparedStatement pstmt = null;
				ResultSet rs = null;
				try {			
					ISQLQueryObject sqlGet = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
					sqlGet.addSelectField(CT_MAP_COLUMN_VALUE);
					sqlGet.addSelectField(CT_MAP_COLUMN_UPDATE_TIME);
					sqlGet.addFromTable(CT_MAP_TABLE);
					sqlGet.setANDLogicOperator(true);
					sqlGet.addWhereCondition(CT_MAP_COLUMN_KEY+"=?");
					if(this.transactionMode) {
						sqlGet.setSelectForUpdate(true);
					}
					
					String query = sqlGet.createSQLQuery();
					if(this.debug) {
						this.logSql.debug("[updateMap"+""+this.idTransazione+"] (forUpdate) execute "+DBUtils.formatSQLString(query, this.uniqueIdMap_idActivePolicy));
					}
					pstmt = con.prepareStatement(query);
					pstmt.setString(1, this.uniqueIdMap_idActivePolicy);
					rs = pstmt.executeQuery();
					if(rs == null) {
						pstmt.close();
						throw new UtilsException("CheckMap failed: ResultSet is null?");		
					}
					boolean exist = rs.next();
					if(this.debug) {
						this.logSql.debug("[updateMap"+""+this.idTransazione+"] (forUpdate) executed (result:"+exist+") "+DBUtils.formatSQLString(query, this.uniqueIdMap_idActivePolicy));
					}
					//System.out.println("@update SELECT ["+opType+"] result:"+exist);
					
					Map<IDUnivocoGroupByPolicy, DatiCollezionati> mapActiveThreads = null;
					boolean updateDate = false;
					if(!exist) {
						if(!OperationType.getMapActiveThreads.equals(opType) &&
								!OperationType.resetCounters.equals(opType)  &&
								!OperationType.remove.equals(opType) &&
								!OperationType.getActiveThreads.equals(opType)  &&
								!OperationType.printInfos.equals(opType)) {
							throw new Exception("Map with id '"+this.uniqueIdMap_idActivePolicy+"' not found ?");
						}
					}
					else {
						Timestamp tCheck = rs.getTimestamp(CT_MAP_COLUMN_UPDATE_TIME);
						if(this.uniqueIdMap_updateTime.equals(tCheck)) {
							try(InputStream is = this.jdbcAdapter.getBinaryStream(rs, CT_MAP_COLUMN_VALUE);){
								mapActiveThreads = (Map<IDUnivocoGroupByPolicy, DatiCollezionati>) this.javaDeserializer.readObject(is, Map.class);
							}
						}
						else {
							// data aggiornata
							mapActiveThreads = new HashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();
							updateDate = true;
						}
						if(mapActiveThreads == null) {
							if(!OperationType.getMapActiveThreads.equals(opType) &&
									!OperationType.resetCounters.equals(opType)  &&
									!OperationType.getActiveThreads.equals(opType)  &&
									!OperationType.printInfos.equals(opType)) {
								throw new Exception("Map with id '"+this.uniqueIdMap_idActivePolicy+"' null ?");
							}
						}
					}
					
					rs.close();
					pstmt.close();
					
					
					DatiCollezionati datiCollezionati = null;
					boolean deleteMap = false;
					boolean updateMap = false;
					switch (opType) {
					
					case getMapActiveThreads:
						
						updateResult.map = mapActiveThreads;
						break;
					
					case initMap:
						
						if(map!=null && map.size()>0){
							mapActiveThreads.putAll(map);
							updateMap = true;
						}
						
						break;
						
					case registerStartRequest:
						
						try {
							if(mapActiveThreads.containsKey(datiGroupBy)){
								//System.out.println("<"+idTransazione+">registerStartRequest CHECK CONTAINS ["+datiGroupBy+"]=true");
								datiCollezionati = mapActiveThreads.get(datiGroupBy);	
							}
							else{
								//System.out.println("<"+idTransazione+">registerStartRequest CHECK CONTAINS ["+datiGroupBy+"]=false");
								Date gestorePolicyConfigDate = PolicyDateUtils.readGestorePolicyConfigDateIntoContext(ctx);
								datiCollezionati = new DatiCollezionati(this.activePolicy.getInstanceConfiguration().getUpdateTime(), gestorePolicyConfigDate);
								//System.out.println("<"+idTransazione+">registerStartRequest PUT");
								mapActiveThreads.put(datiGroupBy, datiCollezionati); // registro nuova immagine
							}
							
							// incremento il numero di thread
							//System.out.println("<"+idTransazione+">registerStartRequest in datiCollezionati ...");
							datiCollezionati.registerStartRequest(log, this.activePolicy, ctx);
							//System.out.println("<"+idTransazione+">registerStartRequest in datiCollezionati ok: "+datiCollezionati.getActiveRequestCounter());
							
							updateMap = true;
							
							// mi salvo fuori dal synchronized l'attuale stato
							updateResult.datiCollezionatiReaded = (DatiCollezionati) datiCollezionati.clone(); 
							
						}catch(Exception e) {
							policyException = new PolicyException(e.getMessage(),e);
							transactionUpdateFinished = true;
						}
												
						break;

					case updateDatiStartRequestApplicabile:
						
						try {
							if(mapActiveThreads.containsKey(datiGroupBy)==false){
								//System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
								policyNotFoundException = new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
								transactionUpdateFinished = true;
							}
							else{
								datiCollezionati = mapActiveThreads.get(datiGroupBy);	
							
								// incremento il numero dei contatori
								//System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile updateDatiStartRequestApplicabile ...");
								boolean updated = datiCollezionati.updateDatiStartRequestApplicabile(log, this.activePolicy, ctx);
								//System.out.println("<"+idTransazione+">updateDatiStartRequestApplicabile updateDatiStartRequestApplicabile ok");
														
								if(updated) {
									
									updateMap = true;
									
									// mi salvo fuori dal synchronized l'attuale stato
									updateResult.datiCollezionatiReaded = (DatiCollezionati) datiCollezionati.clone();
								}
							}
						}
						catch(Exception e) {
							policyException = new PolicyException(e.getMessage(),e);
							transactionUpdateFinished = true;
						}
						
						break;
						
					case registerStopRequest:
						try {
							if(mapActiveThreads.containsKey(datiGroupBy)==false){
								//System.out.println("<"+idTransazione+">registerStopRequest Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
								policyNotFoundException = new PolicyNotFoundException("Non sono presenti alcun threads registrati per la richiesta con dati identificativi ["+datiGroupBy.toString()+"]");
								transactionUpdateFinished = true;
							}
							else{
								//System.out.println("<"+idTransazione+">registerStopRequest get ...");
								datiCollezionati = mapActiveThreads.get(datiGroupBy);	
								//System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ...");
								datiCollezionati.registerEndRequest(log, this.activePolicy, ctx, dati);
								//System.out.println("<"+idTransazione+">registerStopRequest registerEndRequest ok");
								if(isApplicabile){
									//System.out.println("<"+idTransazione+">registerStopRequest updateDatiEndRequestApplicabile ...");
									List<Integer> esitiCodeOk = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeOk_senzaFaultApplicativo();
									List<Integer> esitiCodeKo_senzaFaultApplicativo = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeKo_senzaFaultApplicativo();
									List<Integer> esitiCodeFaultApplicativo = EsitiProperties.getInstance(log,dati.getProtocollo()).getEsitiCodeFaultApplicativo();
									datiCollezionati.updateDatiEndRequestApplicabile(log, this.activePolicy, ctx, dati,
											esitiCodeOk,esitiCodeKo_senzaFaultApplicativo, esitiCodeFaultApplicativo, isViolata);
									//System.out.println("<"+idTransazione+">registerStopRequest updateDatiEndRequestApplicabile ok");
								}
								
								updateMap = true;
							}
						}catch(Exception e) {
							policyException = new PolicyException(e.getMessage(),e);
							transactionUpdateFinished = true;
						}
						
						break;
						
					case resetCounters:
						
						if(mapActiveThreads!=null && mapActiveThreads.size()>0){
							Iterator<DatiCollezionati> datiCollezionatiIter = mapActiveThreads.values().iterator();
							while (datiCollezionatiIter.hasNext()) {
								DatiCollezionati item = (DatiCollezionati) datiCollezionatiIter.next();
								item.resetCounters();
							}
							
							updateMap = true;
						}
						
						break;
						
						
					case remove:
						
						if(mapActiveThreads!=null) {
							deleteMap = true;
						}
						
						break;
						
					case getActiveThreads:
						
						try {
							long counter = 0l;
							
							if(mapActiveThreads!=null && !mapActiveThreads.isEmpty()) {
								for (IDUnivocoGroupByPolicy check : mapActiveThreads.keySet()) {
									
									if(datiGroupBy!=null){
										IDUnivocoGroupBy<IDUnivocoGroupByPolicy> idAstype = (IDUnivocoGroupBy<IDUnivocoGroupByPolicy>) check;
										if(!idAstype.match(datiGroupBy)){
											continue;
										}
									}
									
									counter = counter + mapActiveThreads.get(check).getActiveRequestCounter();
								}
							}
							
							// mi appoggio a questa struttura
							updateResult.counter = counter;
							
						}catch(Exception e) {
							policyException = new PolicyException(e.getMessage(),e);
							transactionUpdateFinished = true;
						}
						
						break;
						
					case printInfos:
						
						StringBuilder bf = new StringBuilder();
						if(mapActiveThreads!=null && !mapActiveThreads.isEmpty()) {
							for (IDUnivocoGroupByPolicy check : mapActiveThreads.keySet()) {
								bf.append(separatorGroups);
								bf.append("\n");
								bf.append(CostantiControlloTraffico.LABEL_MODALITA_SINCRONIZZAZIONE).append(" ").append(this.tipoGestore.toLabel());
								bf.append("\n");
								bf.append("Criterio di Collezionamento dei Dati\n");
								bf.append(check.toString(true));
								bf.append("\n");
								mapActiveThreads.get(check).checkDate(log, this.activePolicy); // imposta correttamente gli intervalli
								bf.append(mapActiveThreads.get(check).toString());
								bf.append("\n");
							}
						}
						if(bf.length()<=0){
							bf.append("Nessuna informazione disponibile");
							updateResult.info = bf.toString();
						}
						else{
							updateResult.info = bf.toString()+separatorGroups;
						}
						
						break;
					}
					
					if(updateMap) {
					
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						this.javaSerializer.writeObject(mapActiveThreads, bout);
						bout.flush();
						bout.close();
						
						ISQLQueryObject sqlUpdate = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
						sqlUpdate.addUpdateTable(CT_MAP_TABLE);
						//sqlUpdate.addUpdateField(CT_MAP_COLUMN_KEY, "?");
						if(updateDate) {
							sqlUpdate.addUpdateField(CT_MAP_COLUMN_UPDATE_TIME, "?");
						}
						sqlUpdate.addUpdateField(CT_MAP_COLUMN_VALUE, "?");
						sqlUpdate.setANDLogicOperator(true);
						sqlUpdate.addWhereCondition(CT_MAP_COLUMN_KEY+"=?");
						String queryUpdate = sqlUpdate.createSQLUpdate();
						
						if(this.debug) {
							if(updateDate) {
								this.logSql.debug("[updateMap"+""+this.idTransazione+"] execute "+DBUtils.formatSQLString(queryUpdate,
										DateUtils.getSimpleDateFormatMs().format(this.uniqueIdMap_updateTime),
										("blob-size:"+bout.size()), 
										this.uniqueIdMap_idActivePolicy));
							}
							else {
								this.logSql.debug("[updateMap"+""+this.idTransazione+"] execute "+DBUtils.formatSQLString(queryUpdate,
										("blob-size:"+bout.size()), 
										this.uniqueIdMap_idActivePolicy));
							}
						}
						
						//System.out.println("INSERT ["+queryInsert.toString()+"]");
						pstmt = con.prepareStatement(queryUpdate);
						int index = 1;
						if(updateDate) {
							Timestamp t = new Timestamp(this.uniqueIdMap_updateTime.getTime());
							pstmt.setTimestamp(index++, t);
						}
						this.jdbcAdapter.setBinaryData(pstmt, index++, bout.toByteArray());
						pstmt.setString(index++, this.uniqueIdMap_idActivePolicy);
						int rows = pstmt.executeUpdate();
						pstmt.close();
						
						if(this.debug) {
							if(updateDate) {
								this.logSql.debug("[updateMap"+""+this.idTransazione+"] executed (rows:"+rows+") "+DBUtils.formatSQLString(queryUpdate,
										DateUtils.getSimpleDateFormatMs().format(this.uniqueIdMap_updateTime),
										("blob-size:"+bout.size()), 
										this.uniqueIdMap_idActivePolicy));
							}
							else {
								this.logSql.debug("[updateMap"+""+this.idTransazione+"] executed (rows:"+rows+") "+DBUtils.formatSQLString(queryUpdate,
										("blob-size:"+bout.size()), 
										this.uniqueIdMap_idActivePolicy));
							}
						}
						
						//System.out.println("@update UPDATE ["+opType+"]: "+rows);
					}
					
					if(deleteMap) {
						
						ISQLQueryObject sqlDelete = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
						sqlDelete.addDeleteTable(CT_MAP_TABLE);
						sqlDelete.setANDLogicOperator(true);
						sqlDelete.addWhereCondition(CT_MAP_COLUMN_KEY+"=?");
						String queryDelete = sqlDelete.createSQLDelete();
						
						if(this.debug) {
							this.logSql.debug("[deleteMap"+""+this.idTransazione+"] execute "+DBUtils.formatSQLString(queryDelete,
										this.uniqueIdMap_idActivePolicy));
						}
						
						//System.out.println("INSERT ["+queryInsert.toString()+"]");
						pstmt = con.prepareStatement(queryDelete);
						int index = 1;
						pstmt.setString(index++, this.uniqueIdMap_idActivePolicy);
						int rows = pstmt.executeUpdate();
						pstmt.close();
						
						if(this.debug) {
							this.logSql.debug("[updateMap"+""+this.idTransazione+"] executed (rows:"+rows+") "+DBUtils.formatSQLString(queryDelete,
										this.uniqueIdMap_idActivePolicy));
						}
						
					}
					
					if(this.transactionMode) {
						// Chiusura Transazione
						con.commit();
					}
					
					// ID Costruito
					transactionUpdateFinished = true;
				} catch(Throwable e) {
					//System.out.println("ERRORE UPDATE ("+iteration+"): "+e.getMessage());
					//log.info("ERROR GET SERIAL SQL ["+e.getMessage()+"]");
					try{
						if( rs != null )
							rs.close();
					} catch(Exception er) {}
					try{
						if( pstmt != null )
							pstmt.close();
					} catch(Exception er) {}
					if(this.transactionMode) {
						try{
							con.rollback();
						} catch(Exception er) {}
					}
					else {
						throw new PolicyException("Operazione non riuscita: "+e.getMessage(),e);
					}
				}

				if(transactionUpdateFinished == false){
					if(this.transactionMode) {
						// Per aiutare ad evitare conflitti
						try{
							int sleep = (new java.util.Random()).nextInt(this.checkIntervalloJDBC);
							//System.out.println("Sleep: "+sleep);
							Utilities.sleep(sleep); // random
						}catch(Exception eRandom){}
					}
					else {
						throw new PolicyException("Operazione non riuscita");
					}
				}
				
				iteration++;
				
				if(policyNotFoundException!=null) {
					throw policyNotFoundException;
				}
				if(policyException!=null) {
					throw policyException;
				}
				
			}
			
			if(!transactionUpdateFinished) {
				throw new PolicyException("Operazione non riuscita dopo '"+iteration+"' tentativi");
			}
			
		}
		finally{

			// Ripristino Transazione
			if(this.transactionMode) {
				try{
					if(autoCommitModificato){
						con.setAutoCommit(originalConnectionAutocommit);
					}
				} catch(Exception er) {
					//System.out.println("ERROR UNSET:"+er.getMessage());
					throw new PolicyException("CheckMap failed: unsetting connection error; "+er.getMessage());
				}
			}
		}

		return updateResult;
		
	}
}
	
class PolicyConnessioneRuntime{
	
	Resource r = null;
	Connection con = null;
	
}

class UpdateResult{
	
	DatiCollezionati datiCollezionatiReaded;
	long counter;
	String info;
	Map<IDUnivocoGroupByPolicy, DatiCollezionati> map;
	
}

enum OperationType {
	
	registerStartRequest,
	updateDatiStartRequestApplicabile,
	registerStopRequest,
	getActiveThreads,
	printInfos,
	resetCounters,
	remove,
	getMapActiveThreads,
	initMap
	
}
