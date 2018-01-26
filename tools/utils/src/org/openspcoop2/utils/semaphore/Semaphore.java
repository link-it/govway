/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.sql.Connection;

import org.slf4j.Logger;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.id.serial.InfoStatistics;
import org.openspcoop2.utils.jdbc.JDBCUtilities;

/**
 * IDSerialGenerator
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Semaphore {

	private InfoStatistics infoStatistics;
	private SemaphoreEngine engine;
	private TipiDatabase databaseType;
	private Logger log;
	private boolean serializableLevel;
	public Semaphore(InfoStatistics infoStatistics, SemaphoreMapping mapping, SemaphoreConfiguration config, TipiDatabase databaseType, Logger log) throws UtilsException{
		this.infoStatistics = infoStatistics;
		this.engine = new SemaphoreEngine(mapping, config, databaseType, log);
		this.databaseType = databaseType;
		this.log = log;
		this.serializableLevel = config.isSerializableLevel();
	}
	
	
	public boolean newLock(Connection con,String details) throws UtilsException {
		return this._engine(con, details, SemaphoreOperationType.NEW);
	}
	public boolean updateLock(Connection con,String details) throws UtilsException {
		return this._engine(con, details, SemaphoreOperationType.UPDATE);
	}
	public boolean releaseLock(Connection con,String details) throws UtilsException {
		return this._engine(con, details, SemaphoreOperationType.RELEASE);
	}
	
	private boolean _engine(Connection con,String details,SemaphoreOperationType tipo) throws UtilsException {

		// Check connessione
		if(con == null){
			throw new UtilsException("Connessione non fornita");
		}
		try{
			if(con.isClosed()){
				throw new UtilsException("Connessione risulta già chiusa");
			}
		}catch(Exception e){
			throw new UtilsException("Test Connessione non riuscito: "+e.getMessage(),e);
		}

		boolean originalConnectionAutocommit = false;
		boolean autoCommitModificato = false;
		try{
			originalConnectionAutocommit = con.getAutoCommit();
		}catch(Exception e){
			throw new UtilsException("Verifica AutoCommit Connessione non riuscito: "+e.getMessage(),e); 
		}
		if(originalConnectionAutocommit==false){
			throw new UtilsException("Lock ["+tipo+"] failed (Non e' possibile fornire una connessione con autocommit disabilitato poiche' l'utility ha necessita' di effettuare operazioni di commit/rollback)");		
		}
		
		int originalConnectionTransactionIsolation = -1;
		boolean transactionIsolationModificato = false;
		try{
			originalConnectionTransactionIsolation = con.getTransactionIsolation();
		}catch(Exception e){
			throw new UtilsException("Lettura livello di isolamento transazione della Connessione non riuscito: "+e.getMessage(),e); 
		}
		
		try{		
			try{				

				//System.out.println("SET TRANSACTION SERIALIZABLE ("+conDB.getTransactionIsolation()+","+conDB.getAutoCommit()+")");
				// Il rollback, non servirebbe, pero le WrappedConnection di JBoss hanno un bug, per cui alcune risorse non vengono rilasciate.
				// Con il rollback tali risorse vengono rilasciate, e poi effettivamente la ConnectionSottostante emette una eccezione.
				try{
					con.rollback();
				}catch(Exception e){
					//System.out.println("ROLLBACK ERROR: "+e.getMessage());
				}
				
				if(this.serializableLevel) {
					JDBCUtilities.setTransactionIsolationSerializable(this.databaseType, con);
					transactionIsolationModificato = true;
				}
				
				if(originalConnectionAutocommit){
					con.setAutoCommit(false);
					autoCommitModificato = true;
				}
				
			} catch(Exception er) {
				this.log.error("Lock ["+tipo+"] failed (impostazione transazione): "+er.getMessage(),er);
				throw new UtilsException("Lock ["+tipo+"] failed (impostazione transazione): "+er.getMessage(),er);		
			}

			return this.engine.lock(con, details, this.infoStatistics, tipo);	

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
				this.log.error("Lock ["+tipo+"] failed (ripristino transazione): "+er.getMessage());
				throw new UtilsException("Lock ["+tipo+"] failed (ripristino transazione): "+er.getMessage());
			}
		}

	}



}
