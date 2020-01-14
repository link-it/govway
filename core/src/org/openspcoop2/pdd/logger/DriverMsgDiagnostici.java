/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.protocol.engine.BasicProtocolFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiException;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.slf4j.Logger;

/**
 * Interfaccia di ricerca dei messaggi diagnostici
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverMsgDiagnostici implements IDiagnosticDriver {
	

	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return this.basicProtocolFactory;
	}



	public void setPropertiesMsgDiagnostici(List<String> properties) {
		this.driverBase.setPropertiesMsgDiagnostici(properties);
	}
	
	
	/** Driver di base: valido per tutti i protocolli */
	org.openspcoop2.protocol.basic.diagnostica.DiagnosticDriver driverBase = null;
	
	/** Factory di base */
	private BasicProtocolFactory basicProtocolFactory;
	
	public DriverMsgDiagnostici(String nomeDataSource, String tipoDatabase, Properties prop) throws DriverMsgDiagnosticiException {
		this(nomeDataSource,tipoDatabase,prop,null);
	}
	
	public DriverMsgDiagnostici(String nomeDataSource, String tipoDatabase, Properties prop, Logger log) throws DriverMsgDiagnosticiException {
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.diagnostica.DiagnosticDriver) this.basicProtocolFactory.createDiagnosticDriver();
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
			this.driverBase = (org.openspcoop2.protocol.basic.diagnostica.DiagnosticDriver) this.basicProtocolFactory.createDiagnosticDriver();
			this.driverBase.init(dataSourceObject, tipoDatabase,log);
		}catch(Exception e){
			throw new DriverMsgDiagnosticiException(e.getMessage(),e);
		}	
	}
	
	public DriverMsgDiagnostici(Connection connection, String tipoDatabase, Logger log) throws DriverMsgDiagnosticiException {
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.diagnostica.DiagnosticDriver) this.basicProtocolFactory.createDiagnosticDriver();
			this.driverBase.init(connection, tipoDatabase,log);
		}catch(Exception e){
			throw new DriverMsgDiagnosticiException(e.getMessage(),e);
		}	
	}
	
	public DriverMsgDiagnostici(String urlJDBC,String driverJDBC,
			String username,String password, String tipoDatabase, Logger log) throws DriverMsgDiagnosticiException {
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.diagnostica.DiagnosticDriver) this.basicProtocolFactory.createDiagnosticDriver();
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
	
	
	/* ******* RISORSE INTERNE ********** */
	
	@Override
	public void close() throws DriverMsgDiagnosticiException {
		this.driverBase.close();
	}	
	
}


