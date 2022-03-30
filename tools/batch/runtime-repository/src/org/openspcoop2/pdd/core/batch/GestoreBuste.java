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

package org.openspcoop2.pdd.core.batch;

import java.util.Date;

import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.utils.SortedMap;
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
			String tipoRepositoryBuste, boolean useDataRegistrazione) throws Exception {
		super(TipoRuntimeRepository.BUSTE, debug, logQuery, logCore, logSql, finestraSecondi, refreshConnection, tipoRepositoryBuste);
		
		this.useDataRegistrazione = useDataRegistrazione;
	}
	
	@Override
	public void process() throws Exception {
		
		// NOTA: prima le scadute per favorire un minDate più basso e quindi effettuare meno ricerche iterando per le varie finestre
		
		_process(true);
		
		_process(false);
		
	}
		
	private void _process(boolean scaduti) throws Exception {
		
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
						this.logCore.debug("Non esistono buste da eliminare");
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
				
				if(rightInterval!=null) {
					while(rightInterval.before(maxDate)) {
						
						cleanBusteInutili(leftInterval, rightInterval);
						
						leftInterval = rightInterval;
						rightInterval = new Date(leftInterval.getTime()+(this.finestraSecondi*1000));
						if(rightInterval.after(maxDate)) {
							rightInterval = maxDate;
						}
						
					}
				}
				
				// ultimo intervallo
				cleanBusteInutili(leftInterval, rightInterval);
				
			}
			
		}catch(Throwable e){
			String msgErrore = "Errore durante la gestione del repository '"+this.tipoRuntimeRepository.getValue()+"': "+e.getMessage();
			this.logCore.error(msgErrore,e );
			throw new Exception(msgErrore,e);
		}finally {
			this.closeConnection();
		}
		
	}
	

	private void cleanBusteInutili(Date leftInterval, Date rightInterval) throws Exception {

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
			this.logCore.debug(periodo+"Ricerca buste da eliminare in 'INBOX' ...");
		}
		int count = RepositoryBuste.countBusteInutiliIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository, leftInterval, rightInterval, this.useDataRegistrazione);
		if(this.debug) {
			this.logCore.debug(periodo+"Trovate "+count+" buste da eliminare in 'INBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" buste in 'INBOX' ...");
			}
			SortedMap<Integer> eliminati = RepositoryBuste.deleteBusteInutiliIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository, leftInterval, rightInterval, this.useDataRegistrazione);
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" buste in 'INBOX' completata ("+format(eliminati)+")");
			}
		}

		// OUTBOX
		
		if(this.debug) {
			this.logCore.debug(periodo+"Ricerca buste da eliminare in 'OUTBOX' ...");
		}
		count = RepositoryBuste.countBusteInutiliIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository, leftInterval, rightInterval, this.useDataRegistrazione);
		if(this.debug) {
			this.logCore.debug(periodo+"Trovate "+count+" buste da eliminare in 'OUTBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" buste in 'OUTBOX' ...");
			}
			SortedMap<Integer> eliminati = RepositoryBuste.deleteBusteInutiliIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository, leftInterval, rightInterval, this.useDataRegistrazione);
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" buste in 'OUTBOX' completata ("+format(eliminati)+")");
			}
		}
		
	}
	
	
	private void cleanBusteScadute() throws Exception {
		
		// INBOX
		
		if(this.debug) {
			this.logCore.debug("Ricerca buste scadute in 'INBOX' ...");
		}
		int count = RepositoryBuste.countBusteScaduteIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository);
		if(this.debug) {
			this.logCore.debug("Trovate "+count+" buste scadute in 'INBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCore.debug("Eliminazione "+count+" buste scadute in 'INBOX' ...");
			}
			SortedMap<Integer> eliminati = RepositoryBuste.deleteBusteScaduteIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository);
			if(this.debug) {
				this.logCore.debug("Eliminazione "+count+" buste scadute in 'INBOX' completata ("+format(eliminati)+")");
			}
		}

		// OUTBOX
		
		if(this.debug) {
			this.logCore.debug("Ricerca buste scadute in 'OUTBOX' ...");
		}
		count = RepositoryBuste.countBusteScaduteIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository);
		if(this.debug) {
			this.logCore.debug("Trovate "+count+" buste scadute in 'OUTBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCore.debug("Eliminazione "+count+" buste scadute in 'OUTBOX' ...");
			}
			SortedMap<Integer> eliminati = RepositoryBuste.deleteBusteScaduteIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, this.repository);
			if(this.debug) {
				this.logCore.debug("Eliminazione "+count+" buste scadute in 'OUTBOX' completata ("+format(eliminati)+")");
			}
		}
		
	}
}
