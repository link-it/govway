/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.ProtocolliRegistrati;
import org.openspcoop2.protocol.engine.BasicProtocolFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracce;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracceConPaginazione;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.slf4j.Logger;



/**
 * Interfaccia Tracciamento
 *
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverTracciamento implements ITracciaDriver {

	
	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return this.basicProtocolFactory;
	}

	/**
	 * Properties
	 */
	public void setProperties(List<String> properties) {
		this.driverBase.setProperties(properties);
	}
	
	/** Driver di base: valido per tutti i protocolli */
	org.openspcoop2.protocol.basic.tracciamento.TracciaDriver driverBase = null;
	
	/** Factory di base */
	private BasicProtocolFactory basicProtocolFactory;
	
	public DriverTracciamento(String nomeDataSource, String tipoDatabase, Properties prop) throws DriverTracciamentoException {
		this(nomeDataSource,tipoDatabase,prop,null);
	}
	
	public DriverTracciamento(String nomeDataSource, String tipoDatabase, Properties prop, Logger log) throws DriverTracciamentoException {
		
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.tracciamento.TracciaDriver) this.basicProtocolFactory.createTracciaDriver();
			ProtocolliRegistrati pRegistrati = new ProtocolliRegistrati(ProtocolFactoryManager.getInstance().getProtocolFactories());
			this.driverBase.init(pRegistrati,nomeDataSource, tipoDatabase,prop,log);
		}catch(Exception e){
			throw new DriverTracciamentoException(e.getMessage(),e);
		}
		
	}
	
	public DriverTracciamento(DataSource dataSourceObject, String tipoDatabase) throws DriverTracciamentoException {
		this(dataSourceObject,tipoDatabase,null);
	}
	
	public DriverTracciamento(DataSource dataSourceObject, String tipoDatabase, Logger log) throws DriverTracciamentoException {
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.tracciamento.TracciaDriver) this.basicProtocolFactory.createTracciaDriver();
			ProtocolliRegistrati pRegistrati = new ProtocolliRegistrati(ProtocolFactoryManager.getInstance().getProtocolFactories());
			this.driverBase.init(pRegistrati,dataSourceObject, tipoDatabase,log);
		}catch(Exception e){
			throw new DriverTracciamentoException(e.getMessage(),e);
		}
	}
	
	public DriverTracciamento(Connection connection, String tipoDatabase, Logger log) throws DriverTracciamentoException {
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.tracciamento.TracciaDriver) this.basicProtocolFactory.createTracciaDriver();
			ProtocolliRegistrati pRegistrati = new ProtocolliRegistrati(ProtocolFactoryManager.getInstance().getProtocolFactories());
			this.driverBase.init(pRegistrati,connection, tipoDatabase,log);
		}catch(Exception e){
			throw new DriverTracciamentoException(e.getMessage(),e);
		}
	}
	
	public DriverTracciamento(String urlJDBC,String driverJDBC,
			String username,String password, 
			String tipoDatabase, Logger log) throws DriverTracciamentoException {
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.tracciamento.TracciaDriver) this.basicProtocolFactory.createTracciaDriver();
			ProtocolliRegistrati pRegistrati = new ProtocolliRegistrati(ProtocolFactoryManager.getInstance().getProtocolFactories());
			this.driverBase.init(pRegistrati,urlJDBC, driverJDBC,
					username, password,
					tipoDatabase,log);
		}catch(Exception e){
			throw new DriverTracciamentoException(e.getMessage(),e);
		}
	}
	

	
	
	
	
	/* *********** ACCESSI TRAMITE RICERCHE ******* */
	
	/**
	 * Si occupa di ritornare il numero di tracce che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return numero di tracce che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public int countTracce(FiltroRicercaTracce filtro) throws DriverTracciamentoException{
		return this.driverBase.countTracce(filtro);
	}
	
	/**
	 * Si occupa di ritornare le tracce che rispettano il filtro di ricerca
	 *
	 * @param filtro Filtro di ricerca
	 * @return tracce che rispettano il filtro di ricerca
	 * 
	 */
	@Override
	public List<Traccia> getTracce(FiltroRicercaTracceConPaginazione filtro)  throws DriverTracciamentoException, DriverTracciamentoNotFoundException{
		return this.driverBase.getTracce(filtro);
	}
	
	
	/**
	 * Si occupa di eliminare le tracce che rispettano il filtro di ricerca
	 * 
	 * @param filtro Filtro di ricerca
	 * @return numero di tracce eliminate
	 * @throws DriverTracciamentoException
	 */
	@Override
	public int deleteTracce(FiltroRicercaTracce filtro) throws DriverTracciamentoException{
		return this.driverBase.deleteTracce(filtro);
	}
	
	
	
	
	
	
	
	
	/* ******* ACCESSI PUNTUALI ********** */
	
	/**
	 * Recupera la traccia
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	@Override
	public Traccia getTraccia(String idBusta,IDSoggetto codicePorta) throws DriverTracciamentoException, DriverTracciamentoNotFoundException{
		return this.driverBase.getTraccia(idBusta, codicePorta);
	} 
	
	/**
	 * Recupera la traccia
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	@Override
	public Traccia getTraccia(String idBusta,IDSoggetto codicePorta,boolean ricercaIdBustaComeRiferimentoMessaggio) throws DriverTracciamentoException, DriverTracciamentoNotFoundException{
		return this.driverBase.getTraccia(idBusta, codicePorta, ricercaIdBustaComeRiferimentoMessaggio);
	} 
	
	/**
	 * Recupera la traccia in base all'id di transazione
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	@Override
	public Traccia getTraccia(String idTransazione, RuoloMessaggio tipoTraccia) throws DriverTracciamentoException, DriverTracciamentoNotFoundException{
		return this.driverBase.getTraccia(idTransazione, tipoTraccia);
	}
	
	/**
	 * Recupera la traccia in base ad una serie di properties
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	@Override
	public Traccia getTraccia(RuoloMessaggio tipoTraccia,Map<String, String> propertiesRicerca) throws DriverTracciamentoException, DriverTracciamentoNotFoundException{
		return this.driverBase.getTraccia(tipoTraccia, propertiesRicerca);
	} 
	

	
	
	
	
	
	
	/* ******* RISORSE INTERNE ********** */
	
	@Override
	public void close() throws DriverTracciamentoException {
		this.driverBase.close();
	}
}


