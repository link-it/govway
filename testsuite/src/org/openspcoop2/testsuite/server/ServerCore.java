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



package org.openspcoop2.testsuite.server;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.testsuite.core.CostantiTestSuite;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;

/**
 * Componente DB dei Server che si occupa di tracciare il campo is Arrived nel database tracciamento
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */


@SuppressWarnings("serial")
public class ServerCore extends HttpServlet{
	
	/** DatabaseProperties */
	protected DatabaseProperties databaseProperties;
	/** ServerProperties */
	protected TestSuiteProperties testsuiteProperties;
	
	public TestSuiteProperties getTestsuiteProperties() {
		return this.testsuiteProperties;
	}




	/** Logger */
	protected Logger log;
	
	/**
	 * 
	 */
	public ServerCore() {
		super();
		// Inizializzazione TestSuiteProperties
		if(TestSuiteProperties.isInitialized()==false){
			// Server Properties
			TestSuiteProperties.initialize();
			// Logger
			try{
				LoggerWrapperFactory.setLogConfiguration(ServerCore.class.getResource("/"+CostantiTestSuite.LOGGER_PROPERTIES));
			}catch(Exception e){
				throw new TestSuiteException("InitLogger",e);
			}
		}
		
		// Inizializzazione DatabaseProperties
		if(DatabaseProperties.isInitialized()==false){
			DatabaseProperties.initialize();
		}
		
		// Istanza di Server Properties
		this.testsuiteProperties = TestSuiteProperties.getInstance();
		
		// Istanza di logger
		this.log=LoggerWrapperFactory.getLogger("openspcoop2.testsuite");
		
		// Istanza di DatabaseProperties
		this.databaseProperties = DatabaseProperties.getInstance();
		
		// Istanzio ProtocolFactoryManager
		ConfigurazionePdD config = new ConfigurazionePdD();
		config.setLoader(new Loader());
		config.setLog(this.log);
		try{
			ProtocolFactoryManager.initialize(this.log, config, this.testsuiteProperties.getProtocolloDefault());
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		
	}
	

	
	
	/** 
	 * Metodo cheva a scrivere il numero di volte 
	 * che viene ricevuto un messaggio
	 * 
	 * @param id Identi
	 * */
	protected void tracciaIsArrivedIntoDatabase(final String id,final String destinatario,final String protocollo) {

		Thread t = new Thread() {
			@Override
			public void run() {
				
				DatabaseComponent db = null;
				try{
					db = DatabaseProperties.getDatabaseComponentErogatore(protocollo);
					db.getVerificatoreTracciaRichiesta().tracciaIsArrivedIntoDatabase(id, destinatario);
				}catch (Exception e) {
					ServerCore.this.log.error("Impostazione isArrived non riuscita: "+e.getMessage(),e);
				}
				finally{
					db.close();
				}
			}
		};
		
		t.start();

	}


	
}
