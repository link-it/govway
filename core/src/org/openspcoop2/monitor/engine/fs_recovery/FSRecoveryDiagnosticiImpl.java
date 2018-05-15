package org.openspcoop2.monitor.engine.fs_recovery;

import java.io.File;
import java.sql.Connection;

import org.slf4j.Logger;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.diagnostica.utils.serializer.JaxbDeserializer;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;

/**
 * FSRecoveryDiagnosticiImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryDiagnosticiImpl extends AbstractFSRecovery {

	private IDiagnosticProducer diagnosticoAppender;

	public FSRecoveryDiagnosticiImpl( 
			Logger log,
			boolean debug,
			IDiagnosticProducer diagnosticoAppender,
			File directory, File directoryDLQ,
			int tentativi,
			int minutiAttesaProcessingFile) {
		super(log, debug, directory, directoryDLQ, tentativi, minutiAttesaProcessingFile);
		this.diagnosticoAppender = diagnosticoAppender;
	}

	@Override
	public void process(Connection connection) {
		this.log.info("Recovery Diagnostici ...");
		super.process(connection);
		this.log.info("Recovery Diagnostici completato");
	}

	@Override
	public void insertObject(File file, Connection connection) throws Exception {
		JaxbDeserializer deserializer = new JaxbDeserializer();
		MessaggioDiagnostico diagnostico = deserializer.readMessaggioDiagnostico(file);
		MsgDiagnostico msgDiagOp2 = new MsgDiagnostico(diagnostico);
		this.diagnosticoAppender.log(connection, msgDiagOp2);
	}


}
