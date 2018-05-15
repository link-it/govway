package org.openspcoop2.monitor.engine.fs_recovery;

import java.io.File;
import java.sql.Connection;

import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.slf4j.Logger;


/**
 * FSRecoveryTransazioni
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryTransazioni {

	private FSRecoveryTransazioniImpl transazioniImpl;
	private FSRecoveryTracceImpl tracceImpl;
	private FSRecoveryDiagnosticiImpl diagnosticiImpl;
	private static final int MINUTI_ATTESA_PROCESSING_FILE = 1;
	
	public FSRecoveryTransazioni( 
			Logger log,
			boolean debug,
			org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM,
			ITracciaProducer tracciamentoAppender,
			IDiagnosticProducer diagnosticoAppender,
			File directoryDiagnostici, File directoryDiagnosticiDLQ,
			File directoryTracce, File directoryTracceDLQ,
			File directoryTransazioni, File directoryTransazioniDLQ,
			int tentativi) {
	
		this.transazioniImpl = new FSRecoveryTransazioniImpl(log, debug, transazioniSM, directoryTransazioni, directoryTransazioniDLQ, tentativi, MINUTI_ATTESA_PROCESSING_FILE);
		this.tracceImpl = new FSRecoveryTracceImpl(log, debug, tracciamentoAppender, directoryTracce, directoryTracceDLQ, tentativi, MINUTI_ATTESA_PROCESSING_FILE);
		this.diagnosticiImpl = new FSRecoveryDiagnosticiImpl(log, debug, diagnosticoAppender, directoryDiagnostici, directoryDiagnosticiDLQ, tentativi, MINUTI_ATTESA_PROCESSING_FILE);
	}
	
	public void process(Connection connection){
		
		// Marshal delle transazioni:
		// 'org.openspcoop2.core.transazioni.utils.serializer.JaxbDeserializer'
		// DAO delle transazioni:
		// this.transazioniSM.getTransazioneService().create(obj);
	
		// Marshal dei diagnostici:
		// org.openspcoop2.core.diagnostica.utils.serializer.JaxbDeserializer
		// DAO dei diagnostici
		//org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico msgDiagOp2 = new MsgDiagnostico(oggettoDeserializzato);
		//this.diagnosticoAppender.log(connection, msgDiagOp2);
		
		// Marshal delle tracce:
		// org.openspcoop2.core.tracciamento.utils.serializer.JaxbDeserializer
		// DAO delle tracce
		//org.openspcoop2.protocol.sdk.tracciamento.Tracciamento tracciaOp2 = new Tracciamento(oggettoDeserializzato);
		//this.tracciamentoAppender.log(connection, tracciaOp2);
				
		
		// process file presenti nella directory.
		// Per ogni file fare il marshall dell'oggetto 
		// Usare poi il service per salvare su database
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
		
		
		// NOTA: usare this.debug per emettere o meno informazioni in this.log
		
		// NOTA: prima di processare un file verificare che la sua data di ultima modifica sia piu' vecchia almeno di X minuti (mettere X minuti in una costante)
		// 		 in modo da evitare di processare i file che sono creati nello stesso momento in cui gira la procedura
		
		// NOTA: in DLQ estrapolare la data (solo anno mese e giorno) dal nome del file e usarla come nome della directory all'interno di DLQ.
		this.transazioniImpl.process(connection);
		this.tracceImpl.process(connection);
		this.diagnosticiImpl.process(connection);
	}
	
}
