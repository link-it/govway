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



package org.openspcoop2.pdd.logger;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.Hashtable;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.OpenspcoopAppender;
import org.openspcoop2.protocol.basic.dump.DumpProducer;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.dump.DumpException;
import org.openspcoop2.protocol.sdk.dump.IDumpProducer;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.utils.resources.MapReader;

/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione dei tracciamenti su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DumpOpenSPCoopProtocolAppender implements IDumpProducer{

	private static Hashtable<String, IDumpProducer> mappingProtocolToAppenders = new Hashtable<String, IDumpProducer>();
	
	private static synchronized void initProtocolAppender(String protocol,OpenspcoopAppender appenderProperties) throws ProtocolException{
		if(DumpOpenSPCoopProtocolAppender.mappingProtocolToAppenders.containsKey(protocol)==false){
			IProtocolFactory<?> p = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocol);
			IDumpProducer dump = p.createDumpProducer();
			if(dump==null){
				throw new ProtocolException("IDumpProducer not defined for protocol ["+protocol+"]");
			}
			try{
				dump.initializeAppender(appenderProperties);
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(), e);
			}
			DumpOpenSPCoopProtocolAppender.mappingProtocolToAppenders.put(protocol, dump);
		}
	}
	
	private static IDumpProducer getProtocolAppender(String protocol) throws ProtocolException{
		if(DumpOpenSPCoopProtocolAppender.mappingProtocolToAppenders.containsKey(protocol)==false){
			throw new ProtocolException("ProtocolAppender per protocollo["+protocol+"] non inizializzato");
		}
		return  DumpOpenSPCoopProtocolAppender.mappingProtocolToAppenders.get(protocol);
	}
	
		
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
			MapReader<String, IProtocolFactory<?>> table = ProtocolFactoryManager.getInstance().getProtocolFactories();
			Enumeration<String> keys = table.keys();
			while (keys.hasMoreElements()) {
				String protocol = keys.nextElement();
				DumpOpenSPCoopProtocolAppender.initProtocolAppender(protocol,appenderProperties);
			}
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
	@Override
	public void dump(Connection conOpenSPCoopPdD,Messaggio messaggio) throws DumpException{
		try{
			if(messaggio.getProtocollo()!=null){
				DumpOpenSPCoopProtocolAppender.getProtocolAppender(messaggio.getProtocollo()).dump(conOpenSPCoopPdD,messaggio);
			}
		}catch(Exception e){
			throw new DumpException(e.getMessage(),e);
		}
	}
	public void dump(Connection conOpenSPCoopPdD,Messaggio messaggio,boolean headersCompact) throws DumpException{
		try{
			if(messaggio.getProtocollo()!=null){
				IDumpProducer dumpAppender = DumpOpenSPCoopProtocolAppender.getProtocolAppender(messaggio.getProtocollo());
				if(dumpAppender instanceof DumpProducer) {
					((DumpProducer)dumpAppender).dump(conOpenSPCoopPdD,messaggio,headersCompact);
				}
				else {
					dumpAppender.dump(conOpenSPCoopPdD,messaggio);
				}
			}
		}catch(Exception e){
			throw new DumpException(e.getMessage(),e);
		}
	}



	@Override
	public void isAlive() throws CoreException {
		try{
			Enumeration<String> protocols = ProtocolFactoryManager.getInstance().getProtocolNames();
			while(protocols.hasMoreElements()){
				String protocol = protocols.nextElement();
				DumpOpenSPCoopProtocolAppender.getProtocolAppender(protocol).isAlive();
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
