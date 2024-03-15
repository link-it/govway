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
package org.openspcoop2.monitor.engine.fs_recovery;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.utils.serializer.JaxbDeserializer;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.engine.constants.Costanti;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;

/**
 * FSRecoveryTransazioniImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryTransazioniImpl extends AbstractFSRecovery {
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM;

	public FSRecoveryTransazioniImpl( 
			Logger log,
			boolean debug,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			File directory, File directoryDLQ,
			int tentativi,
			long msAttesaProcessingFile) {
		super(log, debug, directory, directoryDLQ, tentativi, msAttesaProcessingFile);
		this.transazioniSM = transazioniSM;
	}

	@Override
	public void process(Connection connection) {
		this.log.info("Recovery Transazioni ...");
		super.process(connection);
		this.log.info("Recovery Transazioni completato");
	}
	
	private static boolean enrichEventi = false;	
	public static boolean isEnrichEventi() {
		return enrichEventi;
	}
	public static void setEnrichEventi(boolean enrichEventi) {
		FSRecoveryTransazioniImpl.enrichEventi = enrichEventi;
	}

	@Override
	public void insertObject(File file, Connection connection) throws UtilsException, UtilsMultiException {
		JaxbDeserializer deserializer = new JaxbDeserializer();
		Transazione transazione = null;
		try {
			transazione = deserializer.readTransazione(file);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
		
		// aggiungo evento transazione
		// Non e' più possibile aggiungerlo da quando abbiamo trasformato la colonna in 'eventi'.
		if(enrichEventi) {
			if(transazione.getEventiGestione()==null || "".equals(transazione.getEventiGestione())){
				transazione.setEventiGestione(Costanti.EVENTO_FILE_SYSTEM_RECOVERY);
			}
			else{
				transazione.setEventiGestione(transazione.getEventiGestione()+","+Costanti.EVENTO_FILE_SYSTEM_RECOVERY);
			}
		}
		
		try {
			String id = this.transazioniSM.getTransazioneService().convertToId(transazione);
	
			//Se esiste gia' una transazione con questo id, sposto il file direttamente nella DLQ indicando nel file README associato questo motivo
			if(this.transazioniSM.getTransazioneService().exists(id)) {
				
				// Devo verificare che non abbia uno stato parziale
				IPaginatedExpression expr = this.transazioniSM.getTransazioneServiceSearch().newPaginatedExpression();
				expr.limit(1);
				expr.equals(Transazione.model().ID_TRANSAZIONE, id);
				List<Object> list = this.transazioniSM.getTransazioneServiceSearch().select(expr, Transazione.model().ESITO_CONTESTO);
				boolean update = false;
				String contesto = null;
				if(list!=null && !list.isEmpty()) {
					Object o = list.get(0);
					if(o instanceof String) {
						contesto = (String) o;
						if(EsitoUtils.isFaseIntermedia(contesto)) {
							update = true;
						}
					}
				}
				/**System.out.println("***************************** File ["+file.getName()+"] gestito tramite un aggiornamento:"+update+", precedente stato: "+contesto);*/
				
				if(update) {
					this.transazioniSM.getTransazioneService().update(id,transazione);
					if(this.debug)
						this.logDebug("File ["+file.getName()+"] gestito tramite un aggiornamento, precedente stato: "+contesto);
				}
				else {
					String newFileName = FSRecoveryFileUtils.renameToDLQ(this.directoryDLQ, file, "Transazione con id ["+id+"] gia' presente nel database.", this.log);
					if(this.debug)
						this.logWarn("File ["+file.getName()+"] rinominato in ["+newFileName+"]");
				}
			}
			else{
				this.transazioniSM.getTransazioneService().create(transazione);
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}


}
