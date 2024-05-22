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

package org.openspcoop2.pdd.core.batch;

import java.sql.SQLException;
import java.util.Date;

import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateUtils;
import org.slf4j.Logger;

/**
* GestoreMessaggi
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class GestoreBuste extends AbstractGestore {
	
	private boolean useDataRegistrazione;
	
	public GestoreBuste(boolean debug, boolean logQuery,
			Logger logCore, Logger logSql,
			int finestraSecondi, int refreshConnection,
			String tipoRepositoryBuste, boolean useDataRegistrazione) throws UtilsException {
		super(TipoRuntimeRepository.BUSTE, debug, logQuery, logCore, logSql, finestraSecondi, refreshConnection, tipoRepositoryBuste);
		
		this.useDataRegistrazione = useDataRegistrazione;
	}
	
	@Override
	public void process() throws UtilsException {
		
		// NOTA: prima le scadute per favorire un minDate più basso e quindi effettuare meno ricerche iterando per le varie finestre
		
		try {
			processEngine(true);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
		
		try {
			processEngine(false);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(), e);
		}
		
	}
		
	private void processEngine(boolean scaduti) throws UtilsException, SQLException {
		
		try{
			
			if(scaduti) {
				cleanBusteScadute();
			}
			else {
			
				// Calcolo data minima e massima
				Date minDate = null;
				Date maxDate = null;
				if(this.useDataRegistrazione) {
					minDate = RepositoryBuste.getDataRegistrazioneBustaMinima(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql);	
					maxDate = RepositoryBuste.getDataRegistrazioneBustaMassima(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql);
				}
				else {
					minDate = RepositoryBuste.getDataScadenzaBustaMinima(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql);	
					maxDate = RepositoryBuste.getDataScadenzaBustaMassima(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql);
				}
				
				if(minDate==null || maxDate==null) {
					if(this.debug) {
						this.logCoreDebug("Non esistono buste da eliminare");
						return;
					}
				}
				
				Date leftInterval = minDate;
				Date rightInterval = null;
				if(minDate!=null) {
					rightInterval = new Date(minDate.getTime()+(this.finestraSecondi*1000));
					if(rightInterval.after(maxDate)) {
						rightInterval = maxDate;
					}
				}
				else {
					rightInterval = maxDate;
				}
				
				while(rightInterval!=null && rightInterval.before(maxDate)) {
					
					cleanBusteInutili(leftInterval, rightInterval);
					
					leftInterval = rightInterval;
					rightInterval = new Date(leftInterval.getTime()+(this.finestraSecondi*1000));
					if(rightInterval.after(maxDate)) {
						rightInterval = maxDate;
					}
					
				}
				
				// ultimo intervallo
				cleanBusteInutili(leftInterval, rightInterval);
				
			}
			
		}catch(Throwable e){
			String msgErrore = "Errore durante la gestione del repository '"+this.tipoRuntimeRepository.getValue()+"': "+e.getMessage();
			this.logCoreError(msgErrore,e );
			throw new UtilsException(msgErrore,e);
		}finally {
			this.closeConnection();
		}
		
	}
	

	private void cleanBusteInutili(Date leftInterval, Date rightInterval) throws ProtocolException, SQLException {

		String periodo = null;
		if(this.debug) {
			if(rightInterval!=null) {
				if(leftInterval!=null) {
					periodo = "["+DateUtils.getSimpleDateFormatMs().format(leftInterval)+" - "+DateUtils.getSimpleDateFormatMs().format(rightInterval)+"] ";
				}
				else {
					periodo = "[* - "+DateUtils.getSimpleDateFormatMs().format(rightInterval)+"] ";
				}
			}
			else {
				if(leftInterval!=null) {
					periodo = "["+DateUtils.getSimpleDateFormatMs().format(leftInterval)+" - *"+"] ";
				}
				else {
					periodo = "[* - *] ";
				}
			}
		}
		
		// INBOX
		
		if(this.debug) {
			this.logCoreDebug(periodo+"Ricerca buste da eliminare in 'INBOX' ...");
		}
		int count = RepositoryBuste.countBusteInutiliIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository, leftInterval, rightInterval, this.useDataRegistrazione);
		if(this.debug) {
			this.logCoreDebug(periodo+"Trovate "+count+" buste da eliminare in 'INBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCoreDebug(periodo+"Eliminazione "+count+" buste in 'INBOX' ...");
			}
			SortedMap<Integer> eliminati = RepositoryBuste.deleteBusteInutiliIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository, leftInterval, rightInterval, this.useDataRegistrazione);
			if(this.debug) {
				this.logCoreDebug(periodo+"Eliminazione "+count+" buste in 'INBOX' completata ("+format(eliminati)+")");
			}
		}

		// OUTBOX
		
		if(this.debug) {
			this.logCoreDebug(periodo+"Ricerca buste da eliminare in 'OUTBOX' ...");
		}
		count = RepositoryBuste.countBusteInutiliIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository, leftInterval, rightInterval, this.useDataRegistrazione);
		if(this.debug) {
			this.logCoreDebug(periodo+"Trovate "+count+" buste da eliminare in 'OUTBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCoreDebug(periodo+"Eliminazione "+count+" buste in 'OUTBOX' ...");
			}
			SortedMap<Integer> eliminati = RepositoryBuste.deleteBusteInutiliIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository, leftInterval, rightInterval, this.useDataRegistrazione);
			if(this.debug) {
				this.logCoreDebug(periodo+"Eliminazione "+count+" buste in 'OUTBOX' completata ("+format(eliminati)+")");
			}
		}
		
	}
	
	
	private void cleanBusteScadute() throws ProtocolException, SQLException {
		
		// INBOX
		
		if(this.debug) {
			this.logCoreDebug("Ricerca buste scadute in 'INBOX' ...");
		}
		int count = RepositoryBuste.countBusteScaduteIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository);
		if(this.debug) {
			this.logCoreDebug("Trovate "+count+" buste scadute in 'INBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCoreDebug("Eliminazione "+count+" buste scadute in 'INBOX' ...");
			}
			SortedMap<Integer> eliminati = RepositoryBuste.deleteBusteScaduteIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository);
			if(this.debug) {
				this.logCoreDebug("Eliminazione "+count+" buste scadute in 'INBOX' completata ("+format(eliminati)+")");
			}
		}

		// OUTBOX
		
		if(this.debug) {
			this.logCoreDebug("Ricerca buste scadute in 'OUTBOX' ...");
		}
		count = RepositoryBuste.countBusteScaduteIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository);
		if(this.debug) {
			this.logCoreDebug("Trovate "+count+" buste scadute in 'OUTBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCoreDebug("Eliminazione "+count+" buste scadute in 'OUTBOX' ...");
			}
			SortedMap<Integer> eliminati = RepositoryBuste.deleteBusteScaduteIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository);
			if(this.debug) {
				this.logCoreDebug("Eliminazione "+count+" buste scadute in 'OUTBOX' completata ("+format(eliminati)+")");
			}
		}
		
	}
}
