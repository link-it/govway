/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.semaphore;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.jdbc.JDBCParameterUtilities;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**
 * Semaphore
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SemaphoreEngine {

	protected static SemaphoreEngine getSemaphore(SemaphoreMapping mapping, SemaphoreConfiguration config, TipiDatabase databaseType, Logger log) throws UtilsException {
		return new SemaphoreEngine(mapping, config, databaseType, log);
	}
	
	private SemaphoreMapping mapping;
	private SemaphoreConfiguration config;
	private TipiDatabase databaseType;
	private JDBCParameterUtilities jdbcParameterUtils;
	private Logger log;
	
	public SemaphoreEngine(SemaphoreMapping mapping, SemaphoreConfiguration config, TipiDatabase databaseType, Logger log) throws UtilsException {
		this.mapping = mapping;
		this.config = config;
		this.databaseType = databaseType;
		this.log = log;
		
		// Check
		if(this.mapping.getTable()==null) {
			throw new UtilsException("Table name not defined in SemaphoreMapping");
		}
		if(this.mapping.getIdNode()==null) {
			throw new UtilsException("IdNode column name not defined in SemaphoreMapping");
		}
		if(this.mapping.getLockDate()==null) {
			throw new UtilsException("Lock Date column name not defined in SemaphoreMapping");
		}
		if(this.mapping.getUpdateDate()==null) {
			throw new UtilsException("Update Date column name not defined in SemaphoreMapping");
		}
		if(this.databaseType==null) {
			throw new UtilsException("Database Type not defined");
		}
		try {
			this.jdbcParameterUtils = new JDBCParameterUtilities(this.databaseType);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	protected boolean createEmptyLock(Connection conDB, boolean throwExceptionIfExists) throws UtilsException{
		
		String table = this.mapping.getTable();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		boolean exist = false;
		try{
			// Lettura attuale valore
			ISQLQueryObject sqlGet = SQLObjectFactory.createSQLQueryObject(this.databaseType);
			sqlGet.addFromTable(table);
			sqlGet.setANDLogicOperator(true);
			if(this.mapping.sizeUniqueConditionValues()>0) {
				for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
					Object o = this.mapping.getUniqueConditionValue(i);
					if(o!=null) {
						sqlGet.addWhereCondition(this.mapping.getUniqueConditionColumnName(i)+"=?");
					}
					else {
						sqlGet.addWhereIsNullCondition(this.mapping.getUniqueConditionColumnName(i));
					}
				}
			}
			
			//System.out.println("SELECT ["+sqlGet.createSQLQuery()+"]");
			pstmt = conDB.prepareStatement(sqlGet.createSQLQuery());
			int index = 1;
			if(this.mapping.sizeUniqueConditionValues()>0) {
				for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
					Object o = this.mapping.getUniqueConditionValue(i);
					if(o!=null) {
						this.jdbcParameterUtils.setParameter(pstmt, index++, 
								this.mapping.getUniqueConditionValue(i), 
								this.mapping.getUniqueConditionType(i));
					}
				}
			}
			rs = pstmt.executeQuery();
			if(rs == null) {
				pstmt.close();
				this.log.error("Creazione empty lock non riuscita: ResultSet is null?");
				throw new UtilsException("Creazione empty lock non riuscita: ResultSet is null?");		
			}
			exist = rs.next();
		} catch(Throwable e) {
			this.log.error("Creazione empty lock non riuscita: "+e.getMessage(),e);
			throw new UtilsException("Creazione empty lock non riuscita: "+e.getMessage(),e);		
		} finally {
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {}
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {}
		}
			
		if(exist) {
			if(throwExceptionIfExists) {
				throw new UtilsException("Entry already exists");
			}
			return false;
		}
		else {
			try{
				// INSERT
				ISQLQueryObject sqlGet = SQLObjectFactory.createSQLQueryObject(this.databaseType);
				sqlGet.addInsertTable(table);
				if(this.mapping.sizeUniqueConditionValues()>0) {
					for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
						sqlGet.addInsertField(this.mapping.getUniqueConditionColumnName(i),"?");
					}
				}
				
				//System.out.println("INSERT ["+sqlGet.createSQLInsert()+"]");
				pstmt = conDB.prepareStatement(sqlGet.createSQLInsert());
				int index = 1;
				if(this.mapping.sizeUniqueConditionValues()>0) {
					for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
						Object o = this.mapping.getUniqueConditionValue(i);
						if(o!=null) {
							this.jdbcParameterUtils.setParameter(pstmt, index++, 
									this.mapping.getUniqueConditionValue(i), 
									this.mapping.getUniqueConditionType(i));
						}
					}
				}
				int n = pstmt.executeUpdate();
				return n>0;
			} catch(Throwable e) {
				this.log.error("Creazione empty lock non riuscita: "+e.getMessage(),e);
				throw new UtilsException("Creazione empty lock non riuscita: "+e.getMessage(),e);		
			} finally {
				try{
					if( pstmt != null )
						pstmt.close();
				} catch(Exception er) {}
			}
		}
		
	}
	
	protected boolean lock(Connection conDB, String details, InfoStatistics infoStatistics,SemaphoreOperationType operationType) throws UtilsException{
				
		boolean operazioneConclusaConSuccesso = false; 
		// nel caso di newLock, termina con successo se si ottiene il lock.
		// nel caso di updateLock, termina con successo se cmq il lock è sempre in possesso del nodo cluster
		// nel caso di releaseLock termina sempre con successo.
		
		long attesaAttivaJDBC = this.config.getSerializableTimeWaitMs();
		int checkIntervalloJDBC = this.config.getSerializableNextIntervalTimeMs();
		
		boolean processOK = false;

		long scadenzaWhile = DateManager.getTimeMillis()
				+ attesaAttivaJDBC;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		
		int iteration = 0;
		
		List<String> messageException = new ArrayList<String>();
		
		String table = this.mapping.getTable();
		String columnIdNode = this.mapping.getIdNode();
		String columnLockDate = this.mapping.getLockDate();
		String columnUpdateDate = this.mapping.getUpdateDate();
		String columnDetails = this.mapping.getDetails();

		boolean rowNotExistsAndSerializableLevelNotFound = false;
		
		while(processOK==false && rowNotExistsAndSerializableLevelNotFound==false && 
				DateManager.getTimeMillis() < scadenzaWhile){

			iteration++;

			//log.info("process interval["+checkInterval+"]   secondi "+(scadenzaWhile-DateManager.getTimeMillis())/1000);

			operazioneConclusaConSuccesso = false;
			PreparedStatement pstmt = null;
			PreparedStatement pstmtInsert = null;
			ResultSet rs = null;
			try{
				// Lettura attuale valore
				ISQLQueryObject sqlGet = SQLObjectFactory.createSQLQueryObject(this.databaseType);
				sqlGet.addSelectField(columnIdNode);
				sqlGet.addSelectField(columnLockDate);
				sqlGet.addSelectField(columnUpdateDate);
				sqlGet.addFromTable(table);
				sqlGet.setANDLogicOperator(true);
				if(this.mapping.sizeUniqueConditionValues()>0) {
					for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
						Object o = this.mapping.getUniqueConditionValue(i);
						if(o!=null) {
							sqlGet.addWhereCondition(this.mapping.getUniqueConditionColumnName(i)+"=?");
						}
						else {
							sqlGet.addWhereIsNullCondition(this.mapping.getUniqueConditionColumnName(i));
						}
					}
				}
				sqlGet.setSelectForUpdate(true);
				
				//System.out.println("SELECT ["+sqlGet.createSQLQuery()+"]");
				pstmt = conDB.prepareStatement(sqlGet.createSQLQuery());
				int index = 1;
				if(this.mapping.sizeUniqueConditionValues()>0) {
					for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
						Object o = this.mapping.getUniqueConditionValue(i);
						if(o!=null) {
							this.jdbcParameterUtils.setParameter(pstmt, index++, 
									this.mapping.getUniqueConditionValue(i), 
									this.mapping.getUniqueConditionType(i));
						}
					}
				}
				rs = pstmt.executeQuery();
				if(rs == null) {
					pstmt.close();
					this.log.error("Creazione serial non riuscita: ResultSet is null?");
					throw new UtilsException("Creazione serial non riuscita: ResultSet is null?");		
				}
				boolean exist = rs.next();
				String idNode = null;
				Timestamp lockDate = null;
				Timestamp updateDate = null;
				if(exist){
					idNode = rs.getString(columnIdNode);
					lockDate = rs.getTimestamp(columnLockDate);
					updateDate = rs.getTimestamp(columnUpdateDate);
				}		
				rs.close();
				pstmt.close();

				if(!exist) {
					if(JDBCUtilities.isTransactionIsolationSerializable(conDB.getTransactionIsolation(), this.databaseType)==false) {
						rowNotExistsAndSerializableLevelNotFound = true;
						continue;
					}
				}
				
				Timestamp now = DateManager.getTimestamp();
				
				SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
				StringBuilder statoLock = new StringBuilder("Lock per tabella ["+table+"]");
				if(this.mapping.sizeUniqueConditionValues()>0) {
					for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
						statoLock.append(" ["+this.mapping.getUniqueConditionColumnName(i)+"="+this.mapping.getUniqueConditionValue(i)+"]");
					}
				}
				statoLock.append("\n");
				if(exist && idNode!=null){
					statoLock.append("OldIdNode["+idNode+"] OldcreateTime["+dateformat.format(lockDate)+"] OldupdateTime["+dateformat.format(updateDate)+"]");
					statoLock.append("\n");
				}
				statoLock.append("IdNode["+this.config.getIdNode()+"] ");
				statoLock.append("Now["+dateformat.format(now)+"]");
				if(details!=null) {
					statoLock.append(" Details["+details+"]");
				}
				
				SemaphoreEvent semaphoreEvent = new SemaphoreEvent();
				semaphoreEvent.setDate(now);
				semaphoreEvent.setOperationType(operationType);
				semaphoreEvent.setIdNode(this.config.getIdNode());
				boolean emitSemaphoreEvent = false;
				
				if(SemaphoreOperationType.NEW.equals(operationType)) {
				
					if(!exist){
	
						// CREO ENTRY
						ISQLQueryObject sqlInsert = SQLObjectFactory.createSQLQueryObject(this.databaseType);
						sqlInsert.addInsertTable(table);
						sqlInsert.addInsertField(columnIdNode, "?");
						sqlInsert.addInsertField(columnLockDate, "?");
						sqlInsert.addInsertField(columnUpdateDate, "?");
						sqlInsert.addInsertField(columnDetails, "?");
						if(this.mapping.sizeUniqueConditionValues()>0) {
							for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
								sqlInsert.addInsertField(this.mapping.getUniqueConditionColumnName(i),"?");
							}
						}
						//System.out.println("INSERT ["+sqlInsert.createSQLInsert()+"]");
						pstmtInsert = conDB.prepareStatement(sqlInsert.createSQLInsert());
						index = 1;
						pstmtInsert.setString(index++, this.config.getIdNode());
						pstmtInsert.setTimestamp(index++, now);
						pstmtInsert.setTimestamp(index++, now);
						this.jdbcParameterUtils.setParameter(pstmtInsert, index++, details, String.class);
						if(this.mapping.sizeUniqueConditionValues()>0) {
							for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
								this.jdbcParameterUtils.setParameter(pstmtInsert, index++, 
										this.mapping.getUniqueConditionValue(i), 
										this.mapping.getUniqueConditionType(i));
							}
						}
						pstmtInsert.execute();
						pstmtInsert.close();
						
						operazioneConclusaConSuccesso = true;
						
						if(this.config.isEmitEvent()) {
							
							semaphoreEvent.setDetails(statoLock.toString());
							semaphoreEvent.setSeverity(SemaphoreEventSeverity.INFO);
							semaphoreEvent.setLock(operazioneConclusaConSuccesso);
							emitSemaphoreEvent = true;
						}
																	
					}else{
							
						String errore = null;
						
						if(idNode==null) {
							
							// non esiste un altro thread che possiede il lock, lo prendo
							operazioneConclusaConSuccesso = true;
							
						}
						else {
														
							// Controllo idleTime
							if(this.config.getMaxIdleTime()>0) {
								
								long diff = now.getTime() - updateDate.getTime();
								if(diff>this.config.getMaxIdleTime()) {
									errore = "Idle Time ("+this.config.getMaxIdleTime()+"ms) exceeded (actual: "+diff+"ms). Lock obtained for idNode '"+this.config.getIdNode()+"'";
									operazioneConclusaConSuccesso = true;
								}
								
							}
							if(errore==null) {
								// Controllo MaxLife
								if(this.config.getMaxLife()>0) {
									
									long diff = now.getTime() - lockDate.getTime();
									if(diff>this.config.getMaxLife()) {
										errore = "Max Life Time ("+this.config.getMaxLife()+"ms) exceeded (actual: "+diff+"ms). Lock obtained for idNode '"+this.config.getIdNode()+"'";
										operazioneConclusaConSuccesso = true;
									}
									
								}
							}
							
							if(errore!=null) {
								String dettaglioErrore = statoLock.toString()+"\n"+errore;
								this.log.warn(dettaglioErrore);
							}
							
						}
						
						if(operazioneConclusaConSuccesso) {
							
							ISQLQueryObject sqlUpdate = SQLObjectFactory.createSQLQueryObject(this.databaseType);
							sqlUpdate.addUpdateTable(table);
							sqlUpdate.addUpdateField(columnIdNode, "?");
							sqlUpdate.addUpdateField(columnLockDate, "?");
							sqlUpdate.addUpdateField(columnUpdateDate, "?");
							sqlUpdate.addUpdateField(columnDetails, "?");
							sqlUpdate.setANDLogicOperator(true);
							if(this.mapping.sizeUniqueConditionValues()>0) {
								for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
									Object o = this.mapping.getUniqueConditionValue(i);
									if(o!=null) {
										sqlUpdate.addWhereCondition(this.mapping.getUniqueConditionColumnName(i)+"=?");
									}
									else {
										sqlUpdate.addWhereIsNullCondition(this.mapping.getUniqueConditionColumnName(i));
									}
								}
							}
							//System.out.println("UPDATE ["+sqlUpdate.createSQLUpdate()+"]");
							pstmtInsert = conDB.prepareStatement(sqlUpdate.createSQLUpdate());
							index = 1;
							pstmtInsert.setString(index++, this.config.getIdNode());
							pstmtInsert.setTimestamp(index++, now);
							pstmtInsert.setTimestamp(index++, now);
							this.jdbcParameterUtils.setParameter(pstmtInsert, index++, details, String.class);
							if(this.mapping.sizeUniqueConditionValues()>0) {
								for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
									Object o = this.mapping.getUniqueConditionValue(i);
									if(o!=null) {
										this.jdbcParameterUtils.setParameter(pstmtInsert, index++, 
												this.mapping.getUniqueConditionValue(i), 
												this.mapping.getUniqueConditionType(i));
									}
								}
							}
							pstmtInsert.executeUpdate();
							pstmtInsert.close();
							
							if(this.config.isEmitEvent()) {
								if(errore!=null) {
									semaphoreEvent.setDetails(errore+"\n"+statoLock.toString()); // non e' un errore vero, ma la spiegazione del perche' ho levato il lock ad un altro
								}
								else {
									semaphoreEvent.setDetails(statoLock.toString());
								}
								semaphoreEvent.setSeverity(SemaphoreEventSeverity.INFO);
								semaphoreEvent.setLock(operazioneConclusaConSuccesso);
								emitSemaphoreEvent = true;
							}
						}
					}

				}
				else if(SemaphoreOperationType.UPDATE.equals(operationType)
						||
						SemaphoreOperationType.RELEASE.equals(operationType)) {
					
					if(idNode!=null && idNode.equals(this.config.getIdNode())) {
						
						// update valore della colonna di update 
						ISQLQueryObject sqlUpdate = SQLObjectFactory.createSQLQueryObject(this.databaseType);
						sqlUpdate.addUpdateTable(table);
						if(SemaphoreOperationType.UPDATE.equals(operationType)){
							sqlUpdate.addUpdateField(columnUpdateDate, "?");
							sqlUpdate.addUpdateField(columnDetails, "?");
						}
						else {
							sqlUpdate.addUpdateField(columnIdNode, "?");
							sqlUpdate.addUpdateField(columnLockDate, "?");
							sqlUpdate.addUpdateField(columnUpdateDate, "?");
							sqlUpdate.addUpdateField(columnDetails, "?");
						}
						sqlUpdate.setANDLogicOperator(true);
						if(this.mapping.sizeUniqueConditionValues()>0) {
							for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
								Object o = this.mapping.getUniqueConditionValue(i);
								if(o!=null) {
									sqlUpdate.addWhereCondition(this.mapping.getUniqueConditionColumnName(i)+"=?");
								}
								else {
									sqlUpdate.addWhereIsNullCondition(this.mapping.getUniqueConditionColumnName(i));
								}
							}
						}
						//System.out.println("UPDATE ["+sqlUpdate.createSQLUpdate()+"]");
						pstmtInsert = conDB.prepareStatement(sqlUpdate.createSQLUpdate());
						index = 1;
						if(SemaphoreOperationType.UPDATE.equals(operationType)){
							pstmtInsert.setTimestamp(index++, now);
							this.jdbcParameterUtils.setParameter(pstmtInsert, index++, details, String.class);
						}
						else {
							this.jdbcParameterUtils.setParameter(pstmtInsert, index++, null, String.class);
							this.jdbcParameterUtils.setParameter(pstmtInsert, index++, null, Timestamp.class);
							this.jdbcParameterUtils.setParameter(pstmtInsert, index++, null, Timestamp.class);
							this.jdbcParameterUtils.setParameter(pstmtInsert, index++, null, String.class);
						}
						if(this.mapping.sizeUniqueConditionValues()>0) {
							for (int i = 0; i < this.mapping.sizeUniqueConditionValues(); i++) {
								Object o = this.mapping.getUniqueConditionValue(i);
								if(o!=null) {
									this.jdbcParameterUtils.setParameter(pstmtInsert, index++, 
											this.mapping.getUniqueConditionValue(i), 
											this.mapping.getUniqueConditionType(i));
								}
							}
						}
						pstmtInsert.executeUpdate();
						pstmtInsert.close();
						
						operazioneConclusaConSuccesso = true;
						
						if(this.config.isEmitEvent()) {
							semaphoreEvent.setDetails(statoLock.toString());
							semaphoreEvent.setSeverity(SemaphoreEventSeverity.DEBUG);
							semaphoreEvent.setLock(operazioneConclusaConSuccesso);
							emitSemaphoreEvent = true;
						}

					}
					else {
						
						// if idCluster e' null o diverso dal mio 
						// Puo' darsi che qualche altro batch si e' preso il lock perchè era scaduto il tempo, e poi addirittura l'ha anche rilascato
						String msgErrore = "IdNode is null, lock without owner";
						if(idNode!=null) {
							msgErrore = "IdNode owner ["+idNode+"] different";
						}
						
						operazioneConclusaConSuccesso = false;
						
						if(this.config.isEmitEvent()) {
							semaphoreEvent.setDetails(statoLock.toString()+"\n"+msgErrore);
							//semaphoreEvent.setSeverity(SemaphoreEventSeverity.ERROR);
							semaphoreEvent.setSeverity(SemaphoreEventSeverity.WARN);
							semaphoreEvent.setLock(operazioneConclusaConSuccesso);
							emitSemaphoreEvent = true;
						}
												
					}
				
				}
				

				// Chiusura Transazione
				conDB.commit();

				// Emetto evento dopo il commit
				if(emitSemaphoreEvent) {
					this.config.getEventGenerator().emitEvent(conDB, semaphoreEvent);
				}
				
				// Analisi Lock effettuata
				processOK = true;

			} catch(Throwable e) {
				ps.append("********* Exception Iteration ["+iteration+"] **********\n");
				String msg = e.getMessage(); // per evitare out of memory
				if(msg==null){
					msg = "NULL-MESSAGE";
				}
				if(messageException.contains(msg)){
					ps.append("Message already occurs: "+msg);
				}
				else{
					e.printStackTrace(ps);
					messageException.add(msg);
				}
				ps.append("\n\n");
				
				if(infoStatistics!=null){
					infoStatistics.addErrorSerializableAccess(e);
				}
				
				//System.out.println("ERRORE: "+e.getMessage());
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
					if( pstmtInsert != null )
						pstmtInsert.close();
				} catch(Exception er) {}
				try{
					conDB.rollback();
				} catch(Exception er) {}
			}

			if(processOK == false){
				// Per aiutare ad evitare conflitti
				try{
					int intervalloDestro = checkIntervalloJDBC;
					if(this.config.isSerializableNextIntervalTimeMsIncrementMode()){
						intervalloDestro = intervalloDestro + (iteration*this.config.getSerializableNextIntervalTimeMsIncrement());
						if(intervalloDestro>this.config.getMaxSerializableNextIntervalTimeMs()){
							intervalloDestro = this.config.getMaxSerializableNextIntervalTimeMs();
						}
					}
					
					int sleep = (new java.util.Random()).nextInt(intervalloDestro);
					//System.out.println("Sleep: "+sleep);
					Utilities.sleep(sleep); // random
				}catch(Exception eRandom){}
			}
		}

		try{
			if( ps != null ){
				ps.flush();
			}
		} catch(Exception er) {}
		try{
			if( out != null ){
				out.flush();
			}
		} catch(Exception er) {}
		try{
			if( ps != null ){
				ps.close();
			}
		} catch(Exception er) {}
		try{
			if( out != null ){
				out.close();
			}
		} catch(Exception er) {}
		
		if(rowNotExistsAndSerializableLevelNotFound){
			String msgError = "Raw not exists and serializable level is disabled";
			this.log.error(msgError); // in out è presente l'intero stackTrace
			throw new UtilsException(msgError);	
		}
		
		if(processOK==false){
			String msgError = "Lock process failed: l'accesso serializable non ha permesso il recupero del lock";
			this.log.error(msgError+": "+out.toString()); // in out è presente l'intero stackTrace
			
			if(this.config.isEmitEvent()) {
				SemaphoreEvent event = new SemaphoreEvent();
				event.setDate(DateManager.getDate());
				event.setOperationType(operationType);
				event.setDetails(msgError);
				event.setSeverity(SemaphoreEventSeverity.ERROR);
				event.setLock(false);
				this.config.getEventGenerator().emitEvent(conDB, event);
			}
			
			throw new UtilsException(msgError);	
		}

		return operazioneConclusaConSuccesso;
	}
	
}
