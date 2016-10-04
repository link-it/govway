/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import org.openspcoop2.protocol.sdk.diagnostica.IMsgDiagnosticoOpenSPCoopAppender;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazioneApplicativa;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazioneServizioApplicativo;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoException;


/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione dei msg diagnostici su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MsgDiagnosticoOpenSPCoopAppenderDB implements IMsgDiagnosticoOpenSPCoopAppender{

	/** Driver di base: valido per tutti i protocolli */
	org.openspcoop2.protocol.basic.diagnostica.MsgDiagnosticoOpenSPCoopAppenderDB diagnosticaBase = null;
	
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
			this.diagnosticaBase = (org.openspcoop2.protocol.basic.diagnostica.MsgDiagnosticoOpenSPCoopAppenderDB) this.basicProtocolFactory.createMsgDiagnosticoOpenSPCoopAppender();
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
	 * Creazione di un entry che permette di effettuare una correlazione con i msg diagnostici
	 * 
	 * @param msgDiagCorrelazione Informazioni di correlazione
	 * @throws MsgDiagnosticoException
	 */
	@Override
	public void logCorrelazione(Connection conOpenSPCoopPdD,MsgDiagnosticoCorrelazione msgDiagCorrelazione) throws MsgDiagnosticoException{
		this.diagnosticaBase.logCorrelazione(conOpenSPCoopPdD,msgDiagCorrelazione);
	}
	
	
	/**
	 * Creazione di una correlazione applicativa tra messaggi diagnostici e servizi applicativi.
	 * 
	 * @param msgDiagCorrelazioneSA Informazioni necessarie alla registrazione del servizio applicativo
	 */
	@Override
	public void logCorrelazioneServizioApplicativo(Connection conOpenSPCoopPdD,MsgDiagnosticoCorrelazioneServizioApplicativo msgDiagCorrelazioneSA)throws MsgDiagnosticoException{
		this.diagnosticaBase.logCorrelazioneServizioApplicativo(conOpenSPCoopPdD,msgDiagCorrelazioneSA);
	}
	
	
	/**
	 * Registrazione dell'identificativo di correlazione applicativa della risposta
	 * 
	 * @param msgDiagCorrelazioneApplicativa Informazioni necessarie alla registrazione della correlazione
	 * @throws MsgDiagnosticoException
	 */
	@Override
	public void logCorrelazioneApplicativaRisposta(Connection conOpenSPCoopPdD,MsgDiagnosticoCorrelazioneApplicativa msgDiagCorrelazioneApplicativa) throws MsgDiagnosticoException{
		this.diagnosticaBase.logCorrelazioneApplicativaRisposta(conOpenSPCoopPdD,msgDiagCorrelazioneApplicativa);
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
	public IProtocolFactory getProtocolFactory() {
		return this.basicProtocolFactory;
	}
}
