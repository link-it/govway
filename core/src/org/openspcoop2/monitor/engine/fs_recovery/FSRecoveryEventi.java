/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.monitor.engine.fs_recovery;

import java.io.File;

import org.slf4j.Logger;


/**
 * FSRecoveryEventi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryEventi {

	private FSRecoveryEventiImpl impl;
	private static final int MINUTI_ATTESA_PROCESSING_FILE = 5;
	
	
	public FSRecoveryEventi( 
			Logger log,
			boolean debug,
			org.openspcoop2.core.eventi.dao.IServiceManager pluginsEventiSM,
			File directory, File directoryDLQ,
			int tentativi) {
		this.impl = new FSRecoveryEventiImpl(log, debug, pluginsEventiSM, directory, directoryDLQ, tentativi, MINUTI_ATTESA_PROCESSING_FILE);
	}

	public void process(){
		// process file presenti nella directory.
		// Per ogni file fare il marshall dell'oggetto 'it.link.pdd.core.plugins.eventi.utils.serializer.JaxbDeserializer'
		// Usare poi il service this.pluginsEventiSM.getEventoService().create(obj); per salvare su database
		// Se il salvataggio su database va a buon fine si puo eliminare il file.
		// Se avviene un errore sul marshal o se avviene un errore durante l'inserimento effettuare un rename del file file.renameTo(file2)
		// aggiungendo un suffisso sul numero del tentativo: es. 
		// File originale: Evento_2015-04-27_09\:37\:34.323_1.xml
		// Dopo primo tentativo: Evento_2015-04-27_09\:37\:34.323_1.xml_1.error
		// Dopo secondo tentativo: Evento_2015-04-27_09\:37\:34.323_1.xml_2.error
		// ...
		// In modo da riconoscere gli originali *.xml da quelli che sono andati in errore almeno una volta *.error
		//
		// Se per un file si raggiunge il massimo numero di tentativi, effettuare il rename del file file.renameTo(file2)
		// Spostandolo nella directory DLQ.

		// NOTA: prima di processare un file verificare che la sua data di ultima modifica sia piu' vecchia almeno di X minuti (mettere X minuti in una costante)
		// 		 in modo da evitare di processare i file che sono creati nello stesso momento in cui gira la procedura

		// NOTA: usare this.debug per emettere o meno informazioni in this.log

		// NOTA: in DLQ estrapolare la data (solo anno mese e giorno) dal nome del file e usarla come nome della directory all'interno di DLQ.
		this.impl.process(null);
	}

}
