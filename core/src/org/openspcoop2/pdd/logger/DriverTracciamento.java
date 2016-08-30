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
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.basic.ProtocolliRegistrati;
import org.openspcoop2.protocol.engine.BasicProtocolFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracce;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracceConPaginazione;
import org.openspcoop2.protocol.sdk.tracciamento.IDriverTracciamento;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;



/**
 * Interfaccia Tracciamento
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverTracciamento implements IDriverTracciamento {

	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.basicProtocolFactory;
	}
	
	/**
	 * Properties
	 */
	public void setProperties(Vector<String> properties) {
		this.driverBase.setProperties(properties);
	}
	
	/** Driver di base: valido per tutti i protocolli */
	org.openspcoop2.protocol.basic.tracciamento.DriverTracciamento driverBase = null;
	
	/** Factory di base */
	private BasicProtocolFactory basicProtocolFactory;
	
	public DriverTracciamento(String nomeDataSource, String tipoDatabase, Properties prop) throws DriverTracciamentoException {
		this(nomeDataSource,tipoDatabase,prop,null);
	}
	
	public DriverTracciamento(String nomeDataSource, String tipoDatabase, Properties prop, Logger log) throws DriverTracciamentoException {
		
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.tracciamento.DriverTracciamento) this.basicProtocolFactory.createDriverTracciamento();
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
			this.driverBase = (org.openspcoop2.protocol.basic.tracciamento.DriverTracciamento) this.basicProtocolFactory.createDriverTracciamento();
			ProtocolliRegistrati pRegistrati = new ProtocolliRegistrati(ProtocolFactoryManager.getInstance().getProtocolFactories());
			this.driverBase.init(pRegistrati,dataSourceObject, tipoDatabase,log);
		}catch(Exception e){
			throw new DriverTracciamentoException(e.getMessage(),e);
		}
	}
	
	public DriverTracciamento(Connection connection, String tipoDatabase, Logger log) throws DriverTracciamentoException {
		try{
			this.basicProtocolFactory = new BasicProtocolFactory(log);
			this.driverBase = (org.openspcoop2.protocol.basic.tracciamento.DriverTracciamento) this.basicProtocolFactory.createDriverTracciamento();
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
			this.driverBase = (org.openspcoop2.protocol.basic.tracciamento.DriverTracciamento) this.basicProtocolFactory.createDriverTracciamento();
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
	 * Recupera la traccia in base ad una serie di properties
	 * 
	 * @return Traccia
	 * @throws DriverMsgDiagnosticiException
	 */
	@Override
	public Traccia getTraccia(TipoTraccia tipoTraccia,Hashtable<String, String> propertiesRicerca) throws DriverTracciamentoException, DriverTracciamentoNotFoundException{
		return this.driverBase.getTraccia(tipoTraccia, propertiesRicerca);
	} 
	

	
	
	
	
	
	
	/* ******* RISORSE INTERNE ********** */
	
	@Override
	public void close() throws DriverTracciamentoException {
		this.driverBase.close();
	}
}


