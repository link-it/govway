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


package org.openspcoop2.web.lib.audit.appender;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.web.lib.audit.AuditException;
import org.openspcoop2.web.lib.audit.DriverAuditDBAppender;
import org.openspcoop2.web.lib.audit.log.Operation;
import org.openspcoop2.web.lib.audit.log.constants.Stato;

/**
 * Appender per registrare operazione di audit
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class AuditDBAppender implements IAuditAppender {

	/** DataSource dove attingere connessioni */
    private DataSource ds = null;
    private String dsName = null;
    private String tipoDatabase = null; //tipoDatabase
    
    private String nomeAppender = null;
	
	@Override
	public void initAppender(String nomeAppender,Properties properties) throws AuditException{
		
		try{
			this.nomeAppender = nomeAppender;
		
			// Datasource
			this.dsName = properties.getProperty("datasource");
			if(this.dsName==null){
				throw new AuditException("Appender["+this.nomeAppender+"] Proprieta' 'datasource' non definita");
			}else{
				this.dsName = this.dsName.trim();
			}
			
			// TipoDatabase
			this.tipoDatabase = properties.getProperty("tipoDatabase");
			if(this.tipoDatabase==null){
				throw new AuditException("Appender["+this.nomeAppender+"] Proprieta' 'tipoDatabase' non definita");
			}else{
				this.tipoDatabase = this.tipoDatabase.trim();
			}
			
			// Contesto
			java.util.Properties ctx = Utilities.readProperties("context-", properties);
			
			// lookup ds
			GestoreJNDI jndi = new GestoreJNDI(ctx);
			this.ds = (DataSource) jndi.lookup(this.dsName);
			
		}catch(AuditException e){
			throw e;
		}catch(Exception e){
			throw new AuditException("Inizializzazione appender["+this.nomeAppender+"] non riuscita: "+e.getMessage(),e);
		}
		
	}
	
	@Override
	public Object registraOperazioneInFaseDiElaborazione(Operation operation) throws AuditException{
		
		Connection con = null;
		try{
		
			con = this.ds.getConnection();
			checkConnection(con);
			
			DriverAuditDBAppender driverDBAppender = new DriverAuditDBAppender(con,this.tipoDatabase);
			driverDBAppender.createOperation(operation);
			return operation.getId();
			
		}catch(Exception e){
			throw new AuditException("Appender["+this.nomeAppender+"] Errore durante la registrazione dell'operazione: "+e.getMessage(),e);
		}finally{
			try{
				if(con!=null) {
					con.close();
				}
			}catch(Exception e){
				// close
			}
		}
	}
	
	private void checkConnection(Connection con) throws Exception {
		if(con == null)
			throw new Exception("Connessione non fornita dal datasource ["+this.dsName+"]");
	}
	
	@Override
	public void registraOperazioneCompletataConSuccesso(Object idOperation) throws AuditException{
		
		Connection con = null;
		try{
		
			con = this.ds.getConnection();
			checkConnection(con);
			
			DriverAuditDBAppender driverDBAppender = new DriverAuditDBAppender(con,this.tipoDatabase);
			Operation operation = driverDBAppender.getOperation((Long)idOperation);
			
			// Aggiorno stato
			operation.setStato(Stato.COMPLETED);
			
			// Aggiorno tempo di esecuzione
			operation.setTimeExecute(DateManager.getDate());
			
			driverDBAppender.updateOperation(operation, false);
						
		}catch(Exception e){
			throw new AuditException("Appender["+this.nomeAppender+"] Errore durante la registrazione dell'operazione: "+e.getMessage(),e);
		}finally{
			try{
				if(con!=null) {
					con.close();
				}
			}catch(Exception e){
				// close
			}
		}
	}
	
	@Override
	public void registraOperazioneTerminataConErrore(Object idOperation,String motivoErrore) throws AuditException{
		
		Connection con = null;
		try{
		
			con = this.ds.getConnection();
			checkConnection(con);
			
			DriverAuditDBAppender driverDBAppender = new DriverAuditDBAppender(con,this.tipoDatabase);
			Operation operation = driverDBAppender.getOperation((Long)idOperation);
			
			// Aggiorno stato
			operation.setStato(Stato.ERROR);
			operation.setError(motivoErrore);
			
			// Aggiorno tempo di esecuzione
			operation.setTimeExecute(DateManager.getDate());
			
			driverDBAppender.updateOperation(operation, false);
						
		}catch(Exception e){
			throw new AuditException("Appender["+this.nomeAppender+"] Errore durante la registrazione dell'operazione: "+e.getMessage(),e);
		}finally{
			try{
				if(con!=null) {
					con.close();
				}
			}catch(Exception e){
				// close
			}
		}
	}
	
}
