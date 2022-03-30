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
package org.openspcoop2.monitor.engine.fs_recovery;

import java.io.File;
import java.sql.Connection;

import org.slf4j.Logger;

/**
 * AbstractFSRecovery
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractFSRecovery {

	protected Logger log;
	protected boolean debug;
	protected File directory;
	protected File directoryDLQ;
	protected int tentativi;
	protected int minutiAttesaProcessingFile;
	
	public AbstractFSRecovery( 
			Logger log,
			boolean debug,
			File directory, File directoryDLQ,
			int tentativi,
			int minutiAttesaProcessingFile) {
		this.log = log;
		this.debug = debug;
		this.directory = directory;
		this.directoryDLQ = directoryDLQ;
		this.tentativi = tentativi;
		this.minutiAttesaProcessingFile = minutiAttesaProcessingFile;
	}
	
	public abstract void insertObject(File file, Connection connection) throws Exception;
	
	public void process(Connection connection){

		// process file presenti nella directory.
		// Per ogni file fare il marshall dell'oggetto 'it.link.pdd.core.plugins.eventi.utils.serializer.JaxbDeserializer'
		try {
			// NOTA: prima di processare un file verificare che la sua data di ultima modifica sia piu' vecchia almeno di X minuti (mettere X minuti in una costante)
			// 		 in modo da evitare di processare i file che sono creati nello stesso momento in cui gira la procedura
			File[] filesToProcess = this.directory.listFiles(new LastModifiedFileFilter(this.minutiAttesaProcessingFile));
			
			if(filesToProcess.length > 0) {
				this.log.info("Processamento di ["+filesToProcess.length+"] file");
				
				int countProcessatiCorrettamente = 0;
				int countProcessatiConErrore = 0;

				for(File file: filesToProcess) {
					try {
						if(this.debug)
							this.log.info("Inserimento del file ["+file.getName()+"] ...");

						// Implementare il salvataggio nella classe concreta
						// Se il salvataggio su database va a buon fine si puo eliminare il file.
						this.insertObject(file, connection);
						file.delete();
						if(this.debug)
							this.log.info("Inserimento del file ["+file.getName()+"] completato con successo");
						countProcessatiCorrettamente++;
					} catch(Exception e) {
	
						if(this.debug)
							this.log.warn("Errore durante l'inserimento del file ["+file.getName()+"] rinomino il file:"+e.getMessage(), e);
						
						int tentativiPerFile = FSRecoveryFileUtils.getTentativiFromFilename(file.getName());
						if(tentativiPerFile < this.tentativi) {
							// Se avviene un errore sul marshal o se avviene un errore durante l'inserimento effettuare un rename del file file.renameTo(file2)
							// aggiungendo un suffisso sul numero del tentativo: es. 
							// File originale: Evento_2015-04-27_09\:37\:34.323_1.xml
							// Dopo primo tentativo: Evento_2015-04-27_09\:37\:34.323_1.xml_1.error
							// Dopo secondo tentativo: Evento_2015-04-27_09\:37\:34.323_1.xml_2.error
							// ...
							// In modo da riconoscere gli originali *.xml da quelli che sono andati in errore almeno una volta *.error
		
							File newFile = new File(this.directory, FSRecoveryFileUtils.getNewFileNameFromFilename(file.getName()));
							file.renameTo(newFile);
							if(this.debug)
								this.log.warn("File ["+file.getName()+"] rinominato in ["+newFile.getName()+"]");
						} else {
							// Se per un file si raggiunge il massimo numero di tentativi, effettuare il rename del file file.renameTo(file2)
							// Spostandolo nella directory DLQ.
	
							// NOTA: in DLQ estrapolare la data (solo anno mese e giorno) dal nome del file e usarla come nome della directory all'interno di DLQ. Inserire 
							String newFileName = FSRecoveryFileUtils.renameToDLQ(this.directoryDLQ, file, e, this.log);
							if(this.debug)
								this.log.warn("File ["+file.getName()+"] rinominato in ["+newFileName+"]");
						}
						
						countProcessatiConErrore++;
	
					}
				}
				this.log.info("Processamento di ["+filesToProcess.length+"] file completato. ["+countProcessatiCorrettamente+"] file processati correttamente, ["+countProcessatiConErrore+"] con errore");
			} else {
				this.log.info("Nessun file da processare");
			}
			
		}catch(Exception e) {
			this.log.error("Errore durante Il Recovery: "+e.getMessage(), e);
		}
	}

	
}
