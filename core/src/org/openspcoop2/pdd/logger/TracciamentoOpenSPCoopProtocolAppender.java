/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaProducer;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.resources.MapReader;

/**
 * Contiene l'implementazione di un appender personalizzato,
 * per la registrazione dei tracciamenti su database.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TracciamentoOpenSPCoopProtocolAppender implements ITracciaProducer{

	private static Hashtable<String, ITracciaProducer> mappingProtocolToAppenders = new Hashtable<String, ITracciaProducer>();
	
	private static synchronized void initProtocolAppender(String protocol,OpenspcoopAppender appenderProperties) throws ProtocolException{
		if(TracciamentoOpenSPCoopProtocolAppender.mappingProtocolToAppenders.containsKey(protocol)==false){
			IProtocolFactory<?> p = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocol);
			ITracciaProducer tracciamento = p.createTracciaProducer();
			if(tracciamento==null){
				throw new ProtocolException("ITracciaProducer not defined for protocol ["+protocol+"]");
			}
			try{
				tracciamento.initializeAppender(appenderProperties);
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(), e);
			}
			TracciamentoOpenSPCoopProtocolAppender.mappingProtocolToAppenders.put(protocol, tracciamento);
		}
	}
	
	private static ITracciaProducer getProtocolAppender(String protocol) throws ProtocolException{
		if(TracciamentoOpenSPCoopProtocolAppender.mappingProtocolToAppenders.containsKey(protocol)==false){
			throw new ProtocolException("ProtocolAppender per protocollo["+protocol+"] non inizializzato");
		}
		return  TracciamentoOpenSPCoopProtocolAppender.mappingProtocolToAppenders.get(protocol);
	}
	
		
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
			MapReader<String, IProtocolFactory<?>> table = ProtocolFactoryManager.getInstance().getProtocolFactories();
			Enumeration<String> keys = table.keys();
			while (keys.hasMoreElements()) {
				String protocol = keys.nextElement();
				TracciamentoOpenSPCoopProtocolAppender.initProtocolAppender(protocol,appenderProperties);
			}
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
		try{
			if(traccia.getProtocollo()!=null){
				TracciamentoOpenSPCoopProtocolAppender.getProtocolAppender(traccia.getProtocollo()).log(conOpenSPCoopPdD,traccia);
			}
		}catch(Exception e){
			throw new TracciamentoException(e.getMessage(),e);
		}
	}



	@Override
	public void isAlive() throws CoreException {
		try{
			Enumeration<String> protocols = ProtocolFactoryManager.getInstance().getProtocolNames();
			while(protocols.hasMoreElements()){
				String protocol = protocols.nextElement();
				TracciamentoOpenSPCoopProtocolAppender.getProtocolAppender(protocol).isAlive();
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
