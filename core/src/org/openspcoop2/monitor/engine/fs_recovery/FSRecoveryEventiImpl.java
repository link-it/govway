/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.eventi.Evento;
import org.openspcoop2.core.eventi.utils.serializer.JaxbDeserializer;

import java.io.File;
import java.sql.Connection;

import org.slf4j.Logger;

/**
 * FSRecoveryEventiImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FSRecoveryEventiImpl extends AbstractFSRecovery {
	private org.openspcoop2.core.eventi.dao.IServiceManager pluginsEventiSM;

	public FSRecoveryEventiImpl( 
			Logger log,
			boolean debug,
			org.openspcoop2.core.eventi.dao.IServiceManager pluginsEventiSM,
			File directory, File directoryDLQ,
			int tentativi,
			int minutiAttesaProcessingFile) {
		super(log, debug, directory, directoryDLQ, tentativi, minutiAttesaProcessingFile);
		this.pluginsEventiSM = pluginsEventiSM;
	}

	@Override
	public void process(Connection connection) {
		this.log.info("Recovery Eventi ...");
		super.process(connection);
		this.log.info("Recovery Eventi completato");
	}

	@Override
	public void insertObject(File file, Connection connection) throws Exception {
		JaxbDeserializer deserializer = new JaxbDeserializer();
		Evento evento = deserializer.readEvento(file);
		this.pluginsEventiSM.getEventoService().create(evento);
	}


}
