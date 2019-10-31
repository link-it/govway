/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.utils.TransactionServerUtils;
import org.openspcoop2.core.transazioni.utils.serializer.JaxbDeserializer;
import org.slf4j.Logger;

/**
 * FSRecoveryTransazioniImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryTransazioniApplicativoServerImpl extends AbstractFSRecovery {
	private org.openspcoop2.core.transazioni.dao.IServiceManager transazioniSM;

	public FSRecoveryTransazioniApplicativoServerImpl( 
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
		this.log.info("Recovery TransazioneServerApplicativo ...");
		super.process(connection);
		this.log.info("Recovery TransazioneServerApplicativo completato");
	}
	
	@Override
	public void insertObject(File file, Connection connection) throws Exception {
		JaxbDeserializer deserializer = new JaxbDeserializer();
		TransazioneApplicativoServer transazioneApplicativoServer = deserializer.readTransazioneApplicativoServer(file);
		TransactionServerUtils.recover(this.transazioniSM.getTransazioneApplicativoServerService(), transazioneApplicativoServer);
	}


}
