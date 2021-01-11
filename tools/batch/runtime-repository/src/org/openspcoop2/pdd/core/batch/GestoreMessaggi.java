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

package org.openspcoop2.pdd.core.batch;

import java.util.Date;

import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.slf4j.Logger;

/**
* GestoreMessaggi
*
* @author Poli Andrea (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class GestoreMessaggi extends AbstractGestore {
	
	private Integer scadenzaMessaggi;
	
	public GestoreMessaggi(boolean debug, boolean logQuery,
			Logger logCore, Logger logSql,
			int finestraSecondi, int refreshConnection,
			Integer scadenzaMessaggi, String tipoRepositoryBuste) throws Exception {
		super(TipoRuntimeRepository.MESSAGGI, debug, logQuery, logCore, logSql, finestraSecondi, refreshConnection, tipoRepositoryBuste);
		this.scadenzaMessaggi = scadenzaMessaggi;
	}
	
	@Override
	public void process() throws Exception {
		
		_process(false);
		
		if(this.scadenzaMessaggi!=null && this.scadenzaMessaggi>0) {
			_process(true);
		}
		
	}
		
	private void _process(boolean scaduti) throws Exception {
		
		try{

			// Calcolo data minima
			Date minDate = org.openspcoop2.pdd.core.GestoreMessaggi.getOraRegistrazioneMinima(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql);
			
			if(minDate==null) {
				if(this.debug) {
					this.logCore.debug("Non esistono messaggi da eliminare");
					return;
				}
			}
			
			Date now = DateManager.getDate();			
			
			Date leftInterval = minDate;
			Date rightInterval = null;
			if(minDate!=null) {
				rightInterval = new Date(minDate.getTime()+(this.finestraSecondi*1000));
				if(rightInterval.after(now)) {
					rightInterval = now;
				}
			}
			else {
				rightInterval = now;
			}
			
			while(rightInterval.before(now)) {
				
				if(scaduti) {
					cleanMessaggiScaduti(leftInterval, rightInterval);
				}
				else {
					cleanMessaggiInutili(leftInterval, rightInterval);
				}
				
				leftInterval = rightInterval;
				rightInterval = new Date(leftInterval.getTime()+(this.finestraSecondi*1000));
				if(rightInterval.after(now)) {
					rightInterval = now;
				}
				
			}
			
			// ultimo intervallo
			if(scaduti) {
				cleanMessaggiScaduti(leftInterval, rightInterval);
			}
			else {
				cleanMessaggiInutili(leftInterval, rightInterval);
			}
			
			
		}catch(Throwable e){
			String msgErrore = "Errore durante la gestione del repository '"+this.tipoRuntimeRepository.getValue()+"': "+e.getMessage();
			this.logCore.error(msgErrore,e );
			throw new Exception(msgErrore,e);
		}finally {
			this.closeConnection();
		}
		
	}
	

	private void cleanMessaggiInutili(Date leftInterval, Date rightInterval) throws Exception {
		String periodo = null;
		if(this.debug) {
			if(leftInterval!=null) {
				periodo = "["+DateUtils.getSimpleDateFormatMs().format(leftInterval)+" - "+DateUtils.getSimpleDateFormatMs().format(rightInterval)+"] ";
			}
			else {
				periodo = "[* - "+DateUtils.getSimpleDateFormatMs().format(rightInterval)+"] ";
			}
		}
		
		// INBOX
		
		if(this.debug) {
			this.logCore.debug(periodo+"Ricerca messaggi da eliminare in 'INBOX' ...");
		}
		int count = org.openspcoop2.pdd.core.GestoreMessaggi.countMessaggiInutiliIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, leftInterval, rightInterval);
		if(this.debug) {
			this.logCore.debug(periodo+"Trovati "+count+" messaggi da eliminare in 'INBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" messaggi in 'INBOX' ...");
			}
			SortedMap<Integer> eliminati = org.openspcoop2.pdd.core.GestoreMessaggi.deleteMessaggiInutiliIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, 
					leftInterval, rightInterval, this.repository);
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" messaggi in 'INBOX' completata ("+format(eliminati)+")");
			}
		}

		// OUTBOX
		
		if(this.debug) {
			this.logCore.debug(periodo+"Ricerca messaggi da eliminare in 'OUTBOX' ...");
		}
		count = org.openspcoop2.pdd.core.GestoreMessaggi.countMessaggiInutiliIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, leftInterval, rightInterval);
		if(this.debug) {
			this.logCore.debug(periodo+"Trovati "+count+" messaggi da eliminare in 'OUTBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" messaggi in 'OUTBOX' ...");
			}
			SortedMap<Integer> eliminati = org.openspcoop2.pdd.core.GestoreMessaggi.deleteMessaggiInutiliIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, 
					leftInterval, rightInterval, this.repository);
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" messaggi in 'OUTBOX' completata ("+format(eliminati)+")");
			}
		}
		
	}
	
	
	private void cleanMessaggiScaduti(Date leftInterval, Date rightInterval) throws Exception {
		String periodo = null;
		if(this.debug) {
			if(leftInterval!=null) {
				periodo = "["+DateUtils.getSimpleDateFormatMs().format(leftInterval)+" - "+DateUtils.getSimpleDateFormatMs().format(rightInterval)+"] ";
			}
			else {
				periodo = "[* - "+DateUtils.getSimpleDateFormatMs().format(rightInterval)+"] ";
			}
		}
		
		// INBOX
		
		if(this.debug) {
			this.logCore.debug(periodo+"Ricerca messaggi scaduti in 'INBOX' ...");
		}
		int count = org.openspcoop2.pdd.core.GestoreMessaggi.countMessaggiScadutiIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, leftInterval, rightInterval, this.scadenzaMessaggi);
		if(this.debug) {
			this.logCore.debug(periodo+"Trovati "+count+" messaggi scaduti in 'INBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" messaggi scaduti in 'INBOX' ...");
			}
			SortedMap<Integer> eliminati = org.openspcoop2.pdd.core.GestoreMessaggi.deleteMessaggiScadutiIntoInBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, 
					leftInterval, rightInterval, this.scadenzaMessaggi, this.repository);
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" messaggi scaduti in 'INBOX' completata ("+format(eliminati)+")");
			}
		}

		// OUTBOX
		
		if(this.debug) {
			this.logCore.debug(periodo+"Ricerca messaggi scaduti in 'OUTBOX' ...");
		}
		count = org.openspcoop2.pdd.core.GestoreMessaggi.countMessaggiScadutiIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, leftInterval, rightInterval, this.scadenzaMessaggi);
		if(this.debug) {
			this.logCore.debug(periodo+"Trovati "+count+" messaggi scaduti in 'OUTBOX'");
		}
		if(count>0) {
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" messaggi scaduti in 'OUTBOX' ...");
			}
			SortedMap<Integer> eliminati = org.openspcoop2.pdd.core.GestoreMessaggi.deleteMessaggiScadutiIntoOutBox(this.refreshConnection(), this.tipoDatabase, this.logQuery, this.logSql, 
					leftInterval, rightInterval, this.scadenzaMessaggi, this.repository);
			if(this.debug) {
				this.logCore.debug(periodo+"Eliminazione "+count+" messaggi scaduti in 'OUTBOX' completata ("+format(eliminati)+")");
			}
		}
		
	}
}
