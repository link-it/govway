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



package org.openspcoop2.pdd.logger;

import java.sql.Connection;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.protocol.engine.BasicProtocolFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;

/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione dei messaggi su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DumpOpenSPCoopAppenderDB implements IDumpProducer{

	/** Driver di base: valido per tutti i protocolli */
	org.openspcoop2.protocol.basic.dump.DumpProducer dumpBase = null;
	
	/** Factory di base */
	private BasicProtocolFactory basicProtocolFactory;
	
		
	/**
	 * Inizializza l'engine di un appender per la registrazione
	 * di un dump applicativo emesso da una porta di dominio.
	 * 
	 * @param appenderProperties Proprieta' dell'appender
	 * @throws DumpException
	 */
	@Override
	public void initializeAppender(OpenspcoopAppender appenderProperties) throws DumpException{
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			this.dumpBase = (org.openspcoop2.protocol.basic.dump.DumpProducer) this.basicProtocolFactory.createDumpProducer();
			this.dumpBase.initializeAppender(appenderProperties);
		}catch(Exception e){
			throw new DumpException(e.getMessage(),e);
		}
	}

	
	
	/**
	 * Dump di un messaggio
	 * 
	 * @param conOpenSPCoopPdD Connessione verso il database
	 * @param messaggio
	 * @throws DumpException
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public void dump(Connection conOpenSPCoopPdD,Messaggio messaggio) throws DumpException{
		this.dumpBase.dump(conOpenSPCoopPdD, messaggio);
	}
	@Override
	public void dump(Connection conOpenSPCoopPdD,Messaggio messaggio,boolean headersCompact) throws DumpException{
		this.dumpBase.dump(conOpenSPCoopPdD,messaggio,headersCompact);
	}



	@Override
	public void isAlive() throws CoreException {
		this.dumpBase.isAlive();
	}



	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return this.basicProtocolFactory;
	}


}
