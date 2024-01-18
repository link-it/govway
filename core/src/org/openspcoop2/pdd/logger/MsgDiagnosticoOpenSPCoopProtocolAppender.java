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
import java.util.Enumeration;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticProducer;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoException;
import org.openspcoop2.utils.resources.MapReader;


/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione dei msg diagnostici su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MsgDiagnosticoOpenSPCoopProtocolAppender implements IDiagnosticProducer{

	private static java.util.HashMap<String, IDiagnosticProducer> mappingProtocolToAppenders = new java.util.HashMap<String, IDiagnosticProducer>();
	
	private static synchronized void initProtocolAppender(String protocol,OpenspcoopAppender appenderProperties) throws ProtocolException{
		if(MsgDiagnosticoOpenSPCoopProtocolAppender.mappingProtocolToAppenders.containsKey(protocol)==false){
			IProtocolFactory<?> p = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocol);
			IDiagnosticProducer msgDiag = p.createDiagnosticProducer();
			if(msgDiag==null){
				throw new ProtocolException("IDiagnosticProducer not defined for protocol ["+protocol+"]");
			}
			try{
				msgDiag.initializeAppender(appenderProperties);
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(), e);
			}
			MsgDiagnosticoOpenSPCoopProtocolAppender.mappingProtocolToAppenders.put(protocol, msgDiag);
		}
	}
	
	private static IDiagnosticProducer getProtocolAppender(String protocol) throws ProtocolException{
		if(MsgDiagnosticoOpenSPCoopProtocolAppender.mappingProtocolToAppenders.containsKey(protocol)==false){
			throw new ProtocolException("ProtocolAppender per protocollo["+protocol+"] non inizializzato");
		}
		return  MsgDiagnosticoOpenSPCoopProtocolAppender.mappingProtocolToAppenders.get(protocol);
	}
    
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
			MapReader<String, IProtocolFactory<?>> table = ProtocolFactoryManager.getInstance().getProtocolFactories();
			Enumeration<String> keys = table.keys();
			while (keys.hasMoreElements()) {
				String protocol = keys.nextElement();
				MsgDiagnosticoOpenSPCoopProtocolAppender.initProtocolAppender(protocol,appenderProperties);
			}
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
		try{
			if(msgDiagnostico.getProtocollo()!=null){
				MsgDiagnosticoOpenSPCoopProtocolAppender.getProtocolAppender(msgDiagnostico.getProtocollo()).log(conOpenSPCoopPdD,msgDiagnostico);
			}
		}catch(Exception e){
			throw new MsgDiagnosticoException(e.getMessage(),e);
		}
	}
	
	
	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws DriverException eccezione che contiene il motivo della mancata connessione
	 */
	@Override
	public void isAlive() throws CoreException{
		try{
			Enumeration<String> protocols = ProtocolFactoryManager.getInstance().getProtocolNames();
			while(protocols.hasMoreElements()){
				String protocol = protocols.nextElement();
				MsgDiagnosticoOpenSPCoopProtocolAppender.getProtocolAppender(protocol).isAlive();
			}
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
		
	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return null; // non e' possibile localizzarla
	}

}
