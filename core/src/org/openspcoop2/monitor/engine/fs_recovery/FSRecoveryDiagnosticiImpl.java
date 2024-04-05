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

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.core.diagnostica.ElencoMessaggiDiagnostici;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.diagnostica.utils.serializer.JaxbDeserializer;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsMultiException;

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
			long msAttesaProcessingFile) {
		super(log, debug, directory, directoryDLQ, tentativi, msAttesaProcessingFile);
		this.diagnosticoAppender = diagnosticoAppender;
	}

	@Override
	public void process(Connection connection) {
		this.log.info("Recovery Diagnostici ...");
		super.process(connection);
		this.log.info("Recovery Diagnostici completato");
	}

	@Override
	public void insertObject(File file, Connection connection) throws UtilsException, UtilsMultiException {
		JaxbDeserializer deserializer = new JaxbDeserializer();
		List<MessaggioDiagnostico> msgDiagnostici = new ArrayList<>();
		try {
			ElencoMessaggiDiagnostici elencoDiagnostici = deserializer.readElencoMessaggiDiagnostici(file);
			if(elencoDiagnostici!=null && elencoDiagnostici.sizeMessaggioDiagnosticoList()>0) {
				for (MessaggioDiagnostico diagnostico : elencoDiagnostici.getMessaggioDiagnosticoList()) {
					msgDiagnostici.add(diagnostico);
				}
			}
		}catch(Exception t) {
			// backward compatibility: si salvavano i singoli messaggi
			try {
				MessaggioDiagnostico diagnostico = deserializer.readMessaggioDiagnostico(file);
				msgDiagnostici.add(diagnostico);
			}catch(Exception tInternal) {
				throw new UtilsMultiException(t,tInternal);
			}
		}
		if(!msgDiagnostici.isEmpty()) {
			for (MessaggioDiagnostico diagnostico : msgDiagnostici) {
				MsgDiagnostico msgDiagOp2 = new MsgDiagnostico(diagnostico);
				try {
					this.diagnosticoAppender.log(connection, msgDiagOp2);
				}catch(Exception e) {
					throw new UtilsException(e.getMessage(),e);
				}
			}
		}
	}


}
