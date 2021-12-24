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



package org.openspcoop2.pdd.logger;

import java.sql.Connection;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.protocol.engine.BasicProtocolFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;

/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione dei tracciamenti su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TracciamentoOpenSPCoopAppenderDB implements ITracciaProducer{

	/** Driver di base: valido per tutti i protocolli */
	org.openspcoop2.protocol.basic.tracciamento.TracciaProducer tracciamentoBase = null;
	
	/** Factory di base */
	private BasicProtocolFactory basicProtocolFactory;
	
		
	/**
	 * Inizializza l'engine di un appender per la registrazione
	 * di un tracciamento emesso da una porta di dominio.
	 * 
	 * @param appenderProperties Proprieta' dell'appender
	 * @throws TracciamentoException
	 */
	@Override
	public void initializeAppender(OpenspcoopAppender appenderProperties) throws TracciamentoException{
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			this.tracciamentoBase = (org.openspcoop2.protocol.basic.tracciamento.TracciaProducer) this.basicProtocolFactory.createTracciaProducer();
			this.tracciamentoBase.initializeAppender(appenderProperties);
		}catch(Exception e){
			throw new TracciamentoException(e.getMessage(),e);
		}
	}

	
	
	/**
	 * Registra una traccia prodotta da una porta di dominio, utilizzando le informazioni definite dalla specifica SPC.
	 * 
	 * @param conOpenSPCoopPdD Connessione verso il database
	 * @param traccia Traccia
	 * @throws TracciamentoException
	 */
	@Override
	public void log(Connection conOpenSPCoopPdD, Traccia traccia) throws TracciamentoException{
		this.tracciamentoBase.log(conOpenSPCoopPdD, traccia);	
	}



	@Override
	public void isAlive() throws CoreException {
		this.tracciamentoBase.isAlive();
	}



	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return this.basicProtocolFactory;
	}


}
