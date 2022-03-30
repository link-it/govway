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
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.protocol.engine.BasicProtocolFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoException;


/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione dei msg diagnostici su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MsgDiagnosticoOpenSPCoopAppenderDB implements IDiagnosticProducer{

	/** Driver di base: valido per tutti i protocolli */
	org.openspcoop2.protocol.basic.diagnostica.DiagnosticProducer diagnosticaBase = null;
	
	/** Factory di base */
	private BasicProtocolFactory basicProtocolFactory;
    
    /**
	 * Inizializza l'engine di un appender per la registrazione
	 * di un msg Diagnostico emesso da una porta di dominio.
	 * 
	 * @param appenderProperties Proprieta' dell'appender
	 * @throws MsgDiagnosticoException
	 */
	@Override
	public void initializeAppender(OpenspcoopAppender appenderProperties) throws MsgDiagnosticoException{
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			this.diagnosticaBase = (org.openspcoop2.protocol.basic.diagnostica.DiagnosticProducer) this.basicProtocolFactory.createDiagnosticProducer();
			this.diagnosticaBase.initializeAppender(appenderProperties);
			this.diagnosticaBase.setForceIndex(OpenSPCoop2Properties.getInstance().isForceIndex());
		}catch(Exception e){
			throw new MsgDiagnosticoException(e.getMessage(),e);
		}	
	}

	
	/**
	 * Registra un msg Diagnostico emesso da una porta di dominio,
	 * utilizzando le informazioni definite dalla specifica SPC.
	 * 
	 * @param msgDiagnostico Messaggio diagnostico
	 * @throws MsgDiagnosticoException
	 */
	@Override
	public void log(Connection conOpenSPCoopPdD,MsgDiagnostico msgDiagnostico) throws MsgDiagnosticoException{
		this.diagnosticaBase.log(conOpenSPCoopPdD,msgDiagnostico);
	}
	
	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws DriverException eccezione che contiene il motivo della mancata connessione
	 */
	@Override
	public void isAlive() throws CoreException{
		this.diagnosticaBase.isAlive();
	}
		
	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return this.basicProtocolFactory;
	}

}
