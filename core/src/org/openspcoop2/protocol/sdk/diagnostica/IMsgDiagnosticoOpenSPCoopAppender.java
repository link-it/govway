/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.protocol.sdk.diagnostica;

import java.sql.Connection;

import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.protocol.sdk.IProtocolFactory;

/**
 * Contiene la definizione una interfaccia per la registrazione personalizzata dei msg diagnostici.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public interface IMsgDiagnosticoOpenSPCoopAppender extends IMonitoraggioRisorsa {

	/**
	 * Recupera l'implementazione della factory per il protocollo in uso
	 * @return protocolFactory in uso.
	 */
		
	public IProtocolFactory getProtocolFactory();
	
	/**
	 * Inizializza l'engine di un appender per la registrazione
	 * di un msg Diagnostico emesso da una porta di dominio.
	 * 
	 * @param appenderProperties Proprieta' dell'appender
	 * @throws MsgDiagnosticoException
	 */
	public void initializeAppender(OpenspcoopAppender appenderProperties) throws MsgDiagnosticoException;

	
	/**
	 * Registra un Messaggio Diagnostico emesso da una porta di dominio.
	 * 
	 * @param msgDiagnostico Messaggio diagnostico
	 * @throws MsgDiagnosticoException
	 */
	public void log(Connection conOpenSPCoopPdD,MsgDiagnostico msgDiagnostico) throws MsgDiagnosticoException;
	
	
	/**
	 * Creazione di un entry che permette di effettuare una correlazione con i msg diagnostici
	 * 
	 * @param msgDiagCorrelazione Informazioni di correlazione
	 * @throws MsgDiagnosticoException
	 */
	public void logCorrelazione(Connection conOpenSPCoopPdD,MsgDiagnosticoCorrelazione msgDiagCorrelazione) throws MsgDiagnosticoException; 
	
		
	
	/**
	 * Creazione di una correlazione applicativa tra messaggi diagnostici e servizi applicativi.
	 * 
	 * @param msgDiagCorrelazioneSA Informazioni necessarie alla registrazione del servizio applicativo
	 * @throws MsgDiagnosticoException
	 */
	public void logCorrelazioneServizioApplicativo(Connection conOpenSPCoopPdD,MsgDiagnosticoCorrelazioneServizioApplicativo msgDiagCorrelazioneSA) throws MsgDiagnosticoException;

	/**
	 * Registrazione dell'identificativo di correlazione applicativa della risposta
	 * 
	 * @param msgDiagCorrelazioneApplicativa Informazioni necessarie alla registrazione della correlazione
	 * @throws MsgDiagnosticoException
	 */
	public void logCorrelazioneApplicativaRisposta(Connection conOpenSPCoopPdD,MsgDiagnosticoCorrelazioneApplicativa msgDiagCorrelazioneApplicativa) throws MsgDiagnosticoException;
}
