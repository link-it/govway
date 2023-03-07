/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
import org.openspcoop2.monitor.engine.constants.Costanti;

import java.io.File;
import java.sql.Connection;

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
			int minutiAttesaProcessingFile) {
		super(log, debug, directory, directoryDLQ, tentativi, minutiAttesaProcessingFile);
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
	public void insertObject(File file, Connection connection) throws Exception {
		JaxbDeserializer deserializer = new JaxbDeserializer();
		Transazione transazione = deserializer.readTransazione(file);
		
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
		
		String id = this.transazioniSM.getTransazioneService().convertToId(transazione);

		//Se esiste gia' una transazione con questo id, sposto il file direttamente nella DLQ indicando nel file README associato questo motivo
		if(this.transazioniSM.getTransazioneService().exists(id)) {
			String newFileName = FSRecoveryFileUtils.renameToDLQ(this.directoryDLQ, file, "Transazione con id ["+id+"] gia' presente nel database.", this.log);
			if(this.debug)
				this.log.warn("File ["+file.getName()+"] rinominato in ["+newFileName+"]");
		}
		else{
			this.transazioniSM.getTransazioneService().create(transazione);
		}
	}


}
