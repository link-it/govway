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



package org.openspcoop2.pdd.logger;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.BasicProtocolFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiException;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDriverMsgDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnosticoCorrelazione;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * Interfaccia di ricerca dei messaggi diagnostici
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverMsgDiagnostici implements IDriverMsgDiagnostici {
	

	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.basicProtocolFactory;
	}


	public void setPropertiesMsgDiagnostici(Vector<String> properties) {
		this.driverBase.setPropertiesMsgDiagnostici(properties);
	}
	public void setPropertiesMsgDiagCorrelazione(Vector<String> properties) {
		this.driverBase.setPropertiesMsgDiagCorrelazione(properties);
	}	
	
	
	/** Driver di base: valido per tutti i protocolli */
	org.openspcoop2.protocol.basic.diagnostica.DriverMsgDiagnostici driverBase = null;
	
	/** Factory di base */
	private BasicProtocolFactory basicProtocolFactory;
	
	public DriverMsgDiagnostici(String nomeDataSource, String tipoDatabase, Properties prop) throws DriverMsgDiagnosticiException {
		this(nomeDataSource,tipoDatabase,prop,null);
	}
	
	public DriverMsgDiagnostici(String nomeDataSource, String tipoDatabase, Properties prop, Logger log) throws DriverMsgDiagnosticiException {
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.diagnostica.DriverMsgDiagnostici) this.basicProtocolFactory.createDriverMSGDiagnostici();
			this.driverBase.init(nomeDataSource, tipoDatabase,prop,log);
		}catch(Exception e){
			throw new DriverMsgDiagnosticiException(e.getMessage(),e);
		}
	}
	
	public DriverMsgDiagnostici(DataSource dataSourceObject, String tipoDatabase) throws DriverMsgDiagnosticiException {
		this(dataSourceObject,tipoDatabase,null);
	}
		
	public DriverMsgDiagnostici(DataSource dataSourceObject, String tipoDatabase, Logger log) throws DriverMsgDiagnosticiException {
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.diagnostica.DriverMsgDiagnostici) this.basicProtocolFactory.createDriverMSGDiagnostici();
			this.driverBase.init(dataSourceObject, tipoDatabase,log);
		}catch(Exception e){
			throw new DriverMsgDiagnosticiException(e.getMessage(),e);
		}	
	}
	
	public DriverMsgDiagnostici(Connection connection, String tipoDatabase, Logger log) throws DriverMsgDiagnosticiException {
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.diagnostica.DriverMsgDiagnostici) this.basicProtocolFactory.createDriverMSGDiagnostici();
			this.driverBase.init(connection, tipoDatabase,log);
		}catch(Exception e){
			throw new DriverMsgDiagnosticiException(e.getMessage(),e);
		}	
	}
	
	public DriverMsgDiagnostici(String urlJDBC,String driverJDBC,
			String username,String password, String tipoDatabase, Logger log) throws DriverMsgDiagnosticiException {
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.diagnostica.DriverMsgDiagnostici) this.basicProtocolFactory.createDriverMSGDiagnostici();
			this.driverBase.init(urlJDBC,driverJDBC,username,password,tipoDatabase,log);
		}catch(Exception e){
			throw new DriverMsgDiagnosticiException(e.getMessage(),e);
		}	
	}


	/* *********** ACCESSI TRAMITE RICERCHE (DIAGNOSTICI) ******* */
	
	/**
	 * Si occupa di ritornare il numero di diagnostici che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return numero di diagnostici che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public int countMessaggiDiagnostici(FiltroRicercaDiagnostici filtro) throws DriverMsgDiagnosticiException{
		return this.driverBase.countMessaggiDiagnostici(filtro);
	}
	
	/**
	 * Si occupa di ritornare i diagnostici che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return diagnostici che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public List<MsgDiagnostico> getMessaggiDiagnostici(FiltroRicercaDiagnosticiConPaginazione filtro)  
		throws DriverMsgDiagnosticiException, DriverMsgDiagnosticiNotFoundException{
		return this.driverBase.getMessaggiDiagnostici(filtro);
	}
	
	
	/**
	 * Si occupa di eliminare i diagnostici che rispettano il filtro di ricerca
	 * 
	 * @param filter Filtro di ricerca
	 * @return numero di diagnostici eliminati
	 * @throws DriverTracciamentoException
	 */
	@Override
	public int deleteMessaggiDiagnostici(FiltroRicercaDiagnostici filter) throws DriverMsgDiagnosticiException{
		return this.driverBase.deleteMessaggiDiagnostici(filter);
	}
	
	
	
	
	
	/* *********** ACCESSI TRAMITE RICERCHE (CORRELAZIONI DIAGNOSTICI con il PROTOCOLLO) ******* */
	
	/**
	 * Si occupa di ritornare il numero di informazioni di correlazione dei diagnostici che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return numero di informazioni di correlazione dei diagnostici che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public int countInfoCorrelazioniMessaggiDiagnostici(FiltroRicercaDiagnostici filtro) throws DriverMsgDiagnosticiException{
		return this.driverBase.countInfoCorrelazioniMessaggiDiagnostici(filtro);
	}
	
	/**
	 * Si occupa di ritornare informazioni di correlazione dei diagnostici che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return informazioni di correlazione dei diagnostici che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public List<MsgDiagnosticoCorrelazione> getInfoCorrelazioniMessaggiDiagnostici(FiltroRicercaDiagnosticiConPaginazione filtro)  
		throws DriverMsgDiagnosticiException, DriverMsgDiagnosticiNotFoundException{
		return this.driverBase.getInfoCorrelazioniMessaggiDiagnostici(filtro);
	}
	
	
	/**
	 * Si occupa di eliminare informazioni di correlazione dei diagnostici che rispettano il filtro di ricerca
	 * 
	 * @param filter Filtro di ricerca
	 * @return numero di diagnostici eliminati
	 * @throws DriverTracciamentoException
	 */
	@Override
	public int deleteInfoCorrelazioniMessaggiDiagnostici(FiltroRicercaDiagnostici filter) throws DriverMsgDiagnosticiException{
		return this.driverBase.deleteInfoCorrelazioniMessaggiDiagnostici(filter);
	}
	
	
	
	
	
	/* ******* RISORSE INTERNE ********** */
	
	@Override
	public void close() throws DriverMsgDiagnosticiException {
		this.driverBase.close();
	}	
	
	
	
	
	
	
	
	

	
	
	/* ********** UTILITY DEL DRIVER (non inserita nell'Interfaccia) ************ */
	
	
	public List<MsgDiagnosticoCorrelazione> getInfoEntryCorrelazione(FiltroRicercaDiagnosticiConPaginazione filter,boolean all) throws DriverMsgDiagnosticiException, SQLQueryObjectException{
		return this.driverBase.getInfoEntryCorrelazione(filter, all);
	}
	
	public long getTotUnionEntry(FiltroRicercaDiagnosticiConPaginazione filter) throws DriverMsgDiagnosticiException, SQLQueryObjectException{
		return this.driverBase.getTotUnionEntry(filter);
	}
}


