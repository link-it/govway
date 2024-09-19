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
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.dao.IDBCredenzialeMittenteServiceSearch;
import org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.core.transazioni.utils.ProjectInfo;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.pdd.logger.diagnostica.ConvertitoreCodiceDiagnostici;
import org.openspcoop2.pdd.logger.diagnostica.InformazioniRecordDiagnostici;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiException;
import org.openspcoop2.protocol.sdk.diagnostica.DriverMsgDiagnosticiNotFoundException;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnostici;
import org.openspcoop2.protocol.sdk.diagnostica.FiltroRicercaDiagnosticiConPaginazione;
import org.openspcoop2.protocol.sdk.diagnostica.IDiagnosticDriver;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.slf4j.Logger;

/**     
 * TransactionDriverMsgDiagnostici
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionDriverMsgDiagnostici implements IDiagnosticDriver {


	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return null;
	}

	private static final String TIPO_DB_NON_GESTITO = "Tipo database non gestito";

	/** 
	 * DataSource
	 */
	DataSource datasource = null;

	/** 
	 * Connection
	 */
	Connection connection = null;
	boolean connectionOpenViaJDBCInCostructor = false;

	/**
	 * SQLQueryObject
	 */
	String tipoDatabase = null;

	/** DriverMsgDiagnostici originale */
	DriverMsgDiagnostici driverMsgDiagnostici = null;

	/** DAO Factory */
	DAOFactory daoFactory = null;

	/** Logger utilizzato per info. */
	private Logger log = null;

	public TransactionDriverMsgDiagnostici(String nomeDataSource, String tipoDatabase, Properties prop) throws DriverMsgDiagnosticiException {
		this(nomeDataSource,tipoDatabase,prop,null);
	}

	public TransactionDriverMsgDiagnostici(String nomeDataSource, String tipoDatabase, Properties prop, Logger log) throws DriverMsgDiagnosticiException {
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(org.openspcoop2.protocol.basic.Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(ds jndiName) Errore durante l'inizializzazione del logger: "+e.getMessage(),e);
		}

		// Datasource
		try {
			InitialContext initCtx = new InitialContext(prop);
			this.datasource = (DataSource) initCtx.lookup(nomeDataSource);
			if (this.datasource == null)
				throw new DriverMsgDiagnosticiException ("datasource is null");

			initCtx.close();
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(ds jndiName) Errore durante la ricerca del datasource: "+e.getMessage(),e);
		}

		// ISQLQueryObject
		try {
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new DriverMsgDiagnosticiException(TIPO_DB_NON_GESTITO);
			}
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(ds jndiName) Errore durante la ricerca del SQLQueryObject: "+e.getMessage(),e);
		}

		// Driver msg diagnostici
		try{
			this.driverMsgDiagnostici = new DriverMsgDiagnostici(nomeDataSource, tipoDatabase, prop, log);
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(ds jndiName) Errore durante l'inizializzazione del driver dei messaggi diagnostici: "+e.getMessage(),e);
		}	

		// DAO Factory
		try{
			this.daoFactory = DAOFactory.getInstance(log);
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(ds jndiName) Errore durante l'inizializzazione del dao factory: "+e.getMessage(),e);
		}

	}

	public TransactionDriverMsgDiagnostici(DataSource dataSourceObject, String tipoDatabase) throws DriverMsgDiagnosticiException {
		this(dataSourceObject,tipoDatabase,null);
	}

	public TransactionDriverMsgDiagnostici(DataSource dataSourceObject, String tipoDatabase, Logger log) throws DriverMsgDiagnosticiException {
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(org.openspcoop2.protocol.basic.Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(ds) Errore durante l'inizializzazione del logger: "+e.getMessage(),e);
		}

		// Datasource
		try {
			this.datasource = dataSourceObject;
			if (this.datasource == null)
				throw new DriverMsgDiagnosticiException ("datasource is null");
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(ds) Errore durante la ricerca del datasource: "+e.getMessage(),e);
		}

		// ISQLQueryObject
		try {
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new DriverMsgDiagnosticiException(TIPO_DB_NON_GESTITO);
			}
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(ds) Errore durante la ricerca del SQLQueryObject: "+e.getMessage(),e);
		}

		// Driver msg diagnostici
		try{
			this.driverMsgDiagnostici = new DriverMsgDiagnostici(this.datasource, tipoDatabase, log);
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(ds) Errore durante l'inizializzazione del driver dei messaggi diagnostici: "+e.getMessage(),e);
		}	

		// DAO Factory
		try{
			this.daoFactory = DAOFactory.getInstance(log);
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(ds) Errore durante l'inizializzazione del dao factory: "+e.getMessage(),e);
		}
	}

	public TransactionDriverMsgDiagnostici(Connection connection, String tipoDatabase) throws DriverMsgDiagnosticiException {
		this(connection,tipoDatabase,null);
	}
	public TransactionDriverMsgDiagnostici(Connection connection, String tipoDatabase, Logger log) throws DriverMsgDiagnosticiException {

		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(org.openspcoop2.protocol.basic.Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(connection) Errore durante l'inizializzazione del logger: "+e.getMessage(),e);
		}

		// connection
		this.connection = connection;

		// ISQLQueryObject
		try {
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new DriverMsgDiagnosticiException(TIPO_DB_NON_GESTITO);
			}
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(connection) Errore durante la ricerca del SQLQueryObject: "+e.getMessage(),e);
		}

		// Driver msg diagnostici
		try{
			this.driverMsgDiagnostici = new DriverMsgDiagnostici(this.connection, tipoDatabase, log);
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(connection) Errore durante l'inizializzazione del driver dei messaggi diagnostici: "+e.getMessage(),e);
		}	

		// DAO Factory
		try{
			this.daoFactory = DAOFactory.getInstance(log);
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(connection) Errore durante l'inizializzazione del dao factory: "+e.getMessage(),e);
		}
	}


	public TransactionDriverMsgDiagnostici(String urlJDBC,String driverJDBC,
			String username,String password, String tipoDatabase) throws DriverMsgDiagnosticiException {
		this(urlJDBC,driverJDBC,username,password,tipoDatabase,null);
	}
	public TransactionDriverMsgDiagnostici(String urlJDBC,String driverJDBC,
			String username,String password, String tipoDatabase, Logger log) throws DriverMsgDiagnosticiException {

		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(org.openspcoop2.protocol.basic.Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(connection url) Errore durante l'inizializzazione del logger: "+e.getMessage(),e);
		}

		// connection
		try {
			Class.forName(driverJDBC);

			if(username!=null){
				this.connection = DriverManager.getConnection(urlJDBC,username,password);
			}else{
				this.connection = DriverManager.getConnection(urlJDBC);
			}
			this.connectionOpenViaJDBCInCostructor = true;

		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(connection url) Errore durante l'inizializzazione della connessione...",e);
		}

		// ISQLQueryObject
		try {
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new DriverMsgDiagnosticiException(TIPO_DB_NON_GESTITO);
			}
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(connection url) Errore durante la ricerca del SQLQueryObject: "+e.getMessage(),e);
		}

		// Driver msg diagnostici
		try{
			this.driverMsgDiagnostici = new DriverMsgDiagnostici(this.connection, tipoDatabase, log);
		} catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(connection url) Errore durante l'inizializzazione del driver dei messaggi diagnostici: "+e.getMessage(),e);
		}	

		// DAO Factory
		try{
			this.daoFactory = DAOFactory.getInstance(log);
		}catch (Exception e) {
			throw new DriverMsgDiagnosticiException("(connection url) Errore durante l'inizializzazione del dao factory: "+e.getMessage(),e);
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
		
		String idTransazione = filtro.getIdTransazione();
		if(idTransazione==null) {
			idTransazione = filtro.getProperty(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE);
		}
		if(idTransazione==null){
			throw new DriverMsgDiagnosticiException("Metodo non implementato in questa versione (Identificativo di transazione non fornito)");
		}
		return this.countListaLog(filtro);
		
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

		String idTransazione = filtro.getIdTransazione();
		if(idTransazione==null) {
			idTransazione = filtro.getProperty(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE);
		}
		if(idTransazione==null){
			throw new DriverMsgDiagnosticiException("Metodo non implementato in questa versione (Identificativo di transazione non fornito)");
		}
		List<MsgDiagnostico> list = null;
		try{
			list = this.getListaLog(filtro);
		}catch(DriverMsgDiagnosticiNotFoundException notFound){
			// ignore
		}
		if(list==null || list.isEmpty()){
			throw new DriverMsgDiagnosticiNotFoundException("Diagnostici non trovati che rispettano il filtro di ricerca (Filtro:"+filtro.toString()+")");
		}
		return list;

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
		throw new DriverMsgDiagnosticiException("Metodo non implementato in questa versione");
	}
	
	private void setPropertiesEngine(List<MsgDiagnostico> listDiagnostici, String idTransazione){
		int index = 1;
		for (MsgDiagnostico msgDiagnostico : listDiagnostici) {
			// set Properties
			if(msgDiagnostico.getProperty(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE)==null){
				msgDiagnostico.addProperty(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE, idTransazione);
			}
			
			// viene usato un valore negativo per indicare che si tratta di un diagnostico ricostruito (per funzionare dove viene usato org.openspcoop2.protocol.basic.diagnostico.DriverDiagnostico.IDDIAGNOSTICO)
			int id = index * -1;
			int code = 0;
			try{
				code = Integer.parseInt(msgDiagnostico.getCodice());
				code = code * -1;
			}catch(Exception e){
				// ignore
			}
			if(msgDiagnostico.getProperty(org.openspcoop2.protocol.basic.diagnostica.DiagnosticDriver.IDDIAGNOSTICI)==null){
				if(code<0){
					msgDiagnostico.addProperty(org.openspcoop2.protocol.basic.diagnostica.DiagnosticDriver.IDDIAGNOSTICI, code+"");
				}
				else{
					msgDiagnostico.addProperty(org.openspcoop2.protocol.basic.diagnostica.DiagnosticDriver.IDDIAGNOSTICI, id+"");
				}
			}
			index++;
		}
	}










	/* ******* RISORSE INTERNE ********** */

	@Override
	public void close() throws DriverMsgDiagnosticiException {
		try{
			if(this.driverMsgDiagnostici!=null){
				this.driverMsgDiagnostici.close();
			}
		}finally{
			try{
				if(this.connectionOpenViaJDBCInCostructor &&
					this.connection!=null && !this.connection.isClosed()){
					this.connection.close();
				}
			}catch(Exception e){
				if(this.log!=null) {
					this.log.error("Close connection failure: "+e.getMessage(),e);
				}
			}
		}
	}	





	/* **** INTERNAL **** */

	private Connection close(Connection con) {
		try{
			if(con!=null) {
				con.close();
				con = null;
			}
		}catch(Exception e){
			// close
		}
		return con;
	}
	private JDBCServiceManager close(JDBCServiceManager transazioniServiceManager) {
		try{
			if(transazioniServiceManager!=null) {
				transazioniServiceManager.close();
				transazioniServiceManager = null;
			}
		}catch(Exception e){
			// ignore
		}
		return transazioniServiceManager;
	}
	private List<Map<String,Object>> selectEsitoProtocollo(ITransazioneServiceSearch transazioneServiceSearch, IPaginatedExpression expression) throws ServiceException, NotImplementedException {
		List<Map<String,Object>> l = null;
		try {
			l = transazioneServiceSearch.select(expression, Transazione.model().ESITO, Transazione.model().PROTOCOLLO);
		}catch(NotFoundException notFound) {
			// ignore
		}
		return l;
	}
	
	public int countListaLog(FiltroRicercaDiagnostici filtro) throws DriverMsgDiagnosticiException{

		String idTransazione = filtro.getIdTransazione();
		if(idTransazione==null) {
			idTransazione = filtro.getProperty(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE);
		}
		if(idTransazione==null){
			throw new DriverMsgDiagnosticiException("Identificativo di transazione non fornito");
		}

		Connection con = null;
		boolean closeConnection = true;
		JDBCServiceManager transazioniServiceManager = null;
		try{
			if(this.connection!=null){
				con = this.connection;
				closeConnection = false;
			}else{
				con = this.datasource.getConnection();
			}
			if(con==null)
				throw new DriverMsgDiagnosticiException("Connection non ottenuta");

			transazioniServiceManager = 
					(JDBCServiceManager) this.daoFactory.getServiceManager(new ProjectInfo(), con, true);
			if(transazioniServiceManager==null) {
				throw new DriverMsgDiagnosticiException("transazioniServiceManager is null");
			}

			ITransazioneServiceSearch transazioneServiceSearch = null;
			
			
			
			if(filtro.getApplicativo()!=null && !"".equals(filtro.getApplicativo()) ){
				
				transazioneServiceSearch = transazioniServiceManager.getTransazioneServiceSearch();
				
				IPaginatedExpression expression = transazioneServiceSearch.newPaginatedExpression();
				expression.equals(Transazione.model().ID_TRANSAZIONE, idTransazione);
				expression.limit(1);
				List<Map<String,Object>> l = selectEsitoProtocollo(transazioneServiceSearch, expression);
				if(l!=null && !l.isEmpty()) {
					Map<String,Object> map = l.remove(0);
					if(map!=null && !map.isEmpty()) {
						
						Object protocolloObj = map.get(Transazione.model().PROTOCOLLO.getFieldName());
						if(protocolloObj instanceof String) {
							String protocollo = (String) protocolloObj;
							Object esitoObj = map.get(Transazione.model().ESITO.getFieldName());
							if(esitoObj instanceof Integer) {
								int esito = (Integer) esitoObj;		
								
								EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(this.log, protocollo);
								EsitoTransazioneName esitoName = esitiProperties.getEsitoTransazioneName(esito);
								
								if(EsitoTransazioneName.isConsegnaMultipla(esitoName)) {
								
									// per evitare deadlock di connessioni
									transazioniServiceManager = close(transazioniServiceManager);
									if(closeConnection){
										con = close(con);
									}
									
									// Consegna Multipla: saranno diagnostici salvati sicuramente nella tabella, relativamente a consegne con presa in carico
									return this.driverMsgDiagnostici.countMessaggiDiagnostici(filtro);
								
								}
							}
						}
						
					}
				}
							
			}
			

			// Informazioni salvataggio diagnostici
			InformazioniRecordDiagnostici informazioniSalvataggioDiagnostici = getInformazioniSalvataggioDiagnostici(transazioniServiceManager, idTransazione, null);

			// per evitare deadlock di connessioni
			transazioniServiceManager = close(transazioniServiceManager);
			if(closeConnection){
				con = close(con);
			}
			
			// Non vi sono informazioni eventuali sulla simulazione. Provo a recuperarla direttamente
			if(informazioniSalvataggioDiagnostici==null){
				return this.driverMsgDiagnostici.countMessaggiDiagnostici(filtro);
			}

			// Non erano stata emessi diagnostici per la transazione
			if(!informazioniSalvataggioDiagnostici.isPresenti()){
				return 0;
			}

			// La traccia era stata interamente salvata nel database delle tracce, poiche' non possedeva tutti i valori ricostruibili automaticamente
			if(!informazioniSalvataggioDiagnostici.isRicostruibili()){
				return this.driverMsgDiagnostici.countMessaggiDiagnostici(filtro);
			}

			
			int size = informazioniSalvataggioDiagnostici.getDiagnostici().size();
			if(informazioniSalvataggioDiagnostici.getDiagnosticiExt()!=null){
				size+=informazioniSalvataggioDiagnostici.getDiagnosticiExt().size();
			}
			
			// Vedo se esistono anche dei diagnostici salvati su database (es. gestione stateful).
			// Se esistono, come ordine temporale saranno cmq successivi a quelli simulati.
			int countSalvatiDatabase = this.driverMsgDiagnostici.countMessaggiDiagnostici(filtro);
			if(countSalvatiDatabase>0) {
				return size+countSalvatiDatabase;
			}
			else {
				return size;
			}


		}catch(Exception e){
			throw new DriverMsgDiagnosticiException("Riscontrato errore durante la lettura (count) dei dati (Transazione:"+idTransazione+"): "+e.getMessage(),e);
		}finally{
			close(transazioniServiceManager);
			if(closeConnection){
				close(con);
			}
		}

	}

	private Traccia getTraccia(RuoloMessaggio ruolo, TransactionDriverTracciamento driverTracciamento, Map<String, String> propertiesRicerca) throws DriverTracciamentoException {
		Traccia tr = null;
		try{
			tr = driverTracciamento.getTraccia(ruolo, propertiesRicerca);
		}catch(DriverTracciamentoNotFoundException dNotFound){
			// ignore
		}
		return tr;
	}
	
	private void fillDiagnostici(FiltroRicercaDiagnosticiConPaginazione filter, List<MsgDiagnostico> list) throws DriverMsgDiagnosticiException {
		int offsetOriginal = -1;
		int limitOriginal = -1;
		
		offsetOriginal = filter.getOffset();
		filter.setOffset(0);
			
		limitOriginal = filter.getLimit();
		filter.setLimit(-1);
		try{
			
			List<MsgDiagnostico> listDB = this.driverMsgDiagnostici.getMessaggiDiagnostici(filter);
			if(listDB!=null && !listDB.isEmpty()){
				list.addAll(listDB);
			}
		}catch(DriverMsgDiagnosticiNotFoundException e){
			// ignore
		}
		finally{
			if(offsetOriginal>=0)
				filter.setOffset(offsetOriginal);
			if(limitOriginal>0)
				filter.setLimit(limitOriginal);
		}
	}
	
	private int parseSafe(String v) {
		int code = -1;
		try{
			code = Integer.parseInt(v);
		}catch(Exception e){
			// ignore
		}	
		return code;
	}
	
	private List<MsgDiagnostico> getListaLog(FiltroRicercaDiagnosticiConPaginazione filter)
			throws DriverMsgDiagnosticiNotFoundException,DriverMsgDiagnosticiException {

		String idTransazione = filter.getIdTransazione();
		if(idTransazione==null) {
			idTransazione = filter.getProperty(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE);
		}
		if(idTransazione==null){
			throw new DriverMsgDiagnosticiException("Identificativo di transazione non fornito");
		}
		
		String idDiagnostico = filter.getProperty(org.openspcoop2.protocol.basic.diagnostica.DiagnosticDriver.IDDIAGNOSTICI);
		int positionDiagnosticoRicostruito = -1;
		boolean positionAsCode = false;
		if(idDiagnostico!=null){
			try{
				int value = Integer.parseInt(idDiagnostico);
				if(value<0){
					// diagnostico ricostruito
					positionDiagnosticoRicostruito = value * -1;
					positionAsCode = positionDiagnosticoRicostruito >= 1000; // le prime tre cifre 001 vengono tradotte in 1
				}
			}catch(Exception e){
				// ignore
			}
		}

		Connection con = null;
		boolean closeConnection = true;
		JDBCServiceManager transazioniServiceManager = null;
		try{
			if(this.connection!=null){
				con = this.connection;
				closeConnection = false;
			}else{
				con = this.datasource.getConnection();
			}
			if(con==null)
				throw new DriverMsgDiagnosticiException("Connection non ottenuta");

			transazioniServiceManager = 
					(JDBCServiceManager) this.daoFactory.getServiceManager(new ProjectInfo(), con, true);
			if(transazioniServiceManager==null) {
				throw new DriverMsgDiagnosticiException("transazioniServiceManager is null");
			}

			ITransazioneServiceSearch transazioneServiceSearch = null;
			Transazione transazione = null;
			
			if(filter.getApplicativo()!=null && !"".equals(filter.getApplicativo()) ){
				
				transazioneServiceSearch = transazioniServiceManager.getTransazioneServiceSearch();
				transazione = transazioneServiceSearch.get(idTransazione);
								
				int esito = transazione.getEsito();
				EsitiProperties esitiProperties = EsitiProperties.getInstanceFromProtocolName(this.log, transazione.getProtocollo());
				EsitoTransazioneName esitoName = esitiProperties.getEsitoTransazioneName(esito);
				
				if(EsitoTransazioneName.isConsegnaMultipla(esitoName)) {
					
					// per evitare deadlock di connessioni
					transazioniServiceManager = close(transazioniServiceManager);
					if(closeConnection){
						con = close(con);
					}
					
					// Consegna Multipla: saranno diagnostici salvati sicuramente nella tabella, relativamente a consegne con presa in carico
					List<MsgDiagnostico> list = this.driverMsgDiagnostici.getMessaggiDiagnostici(filter);
					// set Properties
					setPropertiesEngine(list, idTransazione);
					return list;
					
				}
			}
			
			

			// Informazioni salvataggio diagnostici
			CredenzialiMittente credentialsFiller = new CredenzialiMittente();
			InformazioniRecordDiagnostici informazioniSalvataggioDiagnostici = getInformazioniSalvataggioDiagnostici(transazioniServiceManager, idTransazione, credentialsFiller);

			// Non vi sono informazioni eventuali sulla simulazione. Provo a recuperarla direttamente
			if(informazioniSalvataggioDiagnostici==null){
				
				// per evitare deadlock di connessioni
				transazioniServiceManager = close(transazioniServiceManager);
				if(closeConnection){
					con = close(con);
				}
				
				List<MsgDiagnostico> list = this.driverMsgDiagnostici.getMessaggiDiagnostici(filter);
				// set Properties
				setPropertiesEngine(list, idTransazione);
				return list;
			}

			// Non erano stata emessi diagnostici per la transazione
			if(!informazioniSalvataggioDiagnostici.isPresenti()){
				throw new DriverMsgDiagnosticiNotFoundException("non presente");
			}

			// I diagnostici sono stati interamente salvati nel database dei diagnostici, poiche' non possedeva tutti i valori ricostruibili automaticamente
			if(!informazioniSalvataggioDiagnostici.isRicostruibili()){
				
				// per evitare deadlock di connessioni
				transazioniServiceManager = close(transazioniServiceManager);
				if(closeConnection){
					con = close(con);
				}
				
				List<MsgDiagnostico> list = this.driverMsgDiagnostici.getMessaggiDiagnostici(filter);
				// set Properties
				setPropertiesEngine(list, idTransazione);
				return list;
			}

			// Recupero transazione
			if(transazioneServiceSearch==null) {
				transazioneServiceSearch = transazioniServiceManager.getTransazioneServiceSearch();
			}
			if(transazione==null) {
				transazione = transazioneServiceSearch.get(idTransazione);
			}

			// Recupero tracce
			TransactionDriverTracciamento driverTracciamento = new TransactionDriverTracciamento(con, this.tipoDatabase, this.log);
			Traccia tracciaRichiesta = null;
			Traccia tracciaRisposta = null;
			Map<String, String> propertiesRicerca = new HashMap<>();
			propertiesRicerca.put(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE, idTransazione);
			tracciaRichiesta = getTraccia(RuoloMessaggio.RICHIESTA, driverTracciamento, propertiesRicerca);
			tracciaRisposta = getTraccia(RuoloMessaggio.RISPOSTA, driverTracciamento, propertiesRicerca);

			// Costruzione diagnostici
			ConvertitoreCodiceDiagnostici convertitore = new ConvertitoreCodiceDiagnostici(transazione, tracciaRichiesta, tracciaRisposta, informazioniSalvataggioDiagnostici, credentialsFiller.getTokenClientId());
			List<MsgDiagnostico> listRicostruiti = convertitore.build(this.log);
			
			// Effettuo eventuali filtri
			List<MsgDiagnostico> list = new ArrayList<>();
			for (MsgDiagnostico msgDiagnostico : listRicostruiti) {
				boolean add = true;
				if(filter.getIdFunzione()!=null &&
					!filter.getIdFunzione().equals(msgDiagnostico.getIdFunzione())){
					add = false;
				}
				if(add && filter.getSeverita()!=null &&
					msgDiagnostico.getSeverita()>filter.getSeverita()){
					add = false;
				}
				if(add && filter.getCodice()!=null &&
					!filter.getCodice().equals(msgDiagnostico.getCodice())){
					add = false;
				}
				if(add && filter.getMessaggioCercatoInternamenteTestoDiagnostico()!=null &&
					!msgDiagnostico.getMessaggio().contains(filter.getMessaggioCercatoInternamenteTestoDiagnostico())){
					add = false;
				}
				if(add) {
					list.add(msgDiagnostico);
				}
			}			

			// per evitare deadlock di connessioni
			transazioniServiceManager = close(transazioniServiceManager);
			if(closeConnection){
				con = close(con);
			}
			
			// Vedo se esistono anche dei diagnostici salvati su database (es. gestione stateful).
			// Se esistono, come ordine temporale saranno cmq successivi a quelli simulati.
			fillDiagnostici(filter, list);

			List<MsgDiagnostico> listReturn = new ArrayList<>();
			if(list!=null){
				for (int i = 0; i < list.size(); i++) {
					boolean add = true;
					if(filter.getOffset()>=0 && i<filter.getOffset()){
						add = false;
					}
					if(add) {
						listReturn.add(list.get(i));
						if(filter.getLimit()>0 && listReturn.size()>=filter.getLimit()){
							break;
						}
					}
				}
			}

			if(!listReturn.isEmpty()){
				// set Properties
				setPropertiesEngine(listReturn, idTransazione);
				if(positionDiagnosticoRicostruito>0){
					for (int i = 0; i < list.size(); i++) {
						boolean found = false;
						if(positionAsCode){
							int code = parseSafe(list.get(i).getCodice());
							if(positionDiagnosticoRicostruito == code){
								found = true;
							}
						}
						else{
							if((i+1)==positionDiagnosticoRicostruito){
								found = true;
							}
						}
						if(found){
							List<MsgDiagnostico> listDiag = new ArrayList<>();
							listDiag.add(list.get(i));
							return listDiag;
						}
					}
					throw new DriverMsgDiagnosticiException("Diagnostico con posizione ["+positionDiagnosticoRicostruito+"] non trovato");
				}
				else{
					return listReturn;
				}
			}
			
			throw new DriverMsgDiagnosticiNotFoundException("diagnostici non trovati");

		}catch(DriverMsgDiagnosticiNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new DriverMsgDiagnosticiException("Riscontrato errore durante la lettura dei dati (Transazione:"+idTransazione+"): "+e.getMessage(),e);
		}finally{
			close(transazioniServiceManager);
			if(closeConnection){
				close(con);
			}
		}


	}

	private CredenzialeMittente getCredenzialeMittente(JDBCServiceManager transazioniServiceManager, String dbValueTokenClientId, String idTransazione) throws ServiceException, MultipleResultException, NotImplementedException{
		CredenzialeMittente credenzialeClientId = null;
		long id = -1;
		try {
			IDBCredenzialeMittenteServiceSearch credenzialeMittenteServiceSearch = (IDBCredenzialeMittenteServiceSearch) transazioniServiceManager.getCredenzialeMittenteServiceSearch();
			id = Long.parseLong(dbValueTokenClientId);
			credenzialeClientId = credenzialeMittenteServiceSearch.get(id);
			String msgDebug = "RECUPERATO CLIENT ID ["+credenzialeClientId+"] per id ["+idTransazione+"]";
			this.log.debug(msgDebug);
		}catch(NotFoundException notFound) {
			credenzialeClientId = new CredenzialeMittente();
			credenzialeClientId.setId(id);
			credenzialeClientId.setCredenziale("Informazione non disponibile");
		}
		return credenzialeClientId;
	}
	
	private InformazioniRecordDiagnostici getInformazioniSalvataggioDiagnostici(
			JDBCServiceManager transazioniServiceManager,
			String idTransazione,
			CredenzialiMittente credentialsFiller) throws DriverMsgDiagnosticiException{

		try{

			ITransazioneServiceSearch transazioneServiceSearch = transazioniServiceManager.getTransazioneServiceSearch();

			IPaginatedExpression pagExpr = transazioneServiceSearch.newPaginatedExpression();
			pagExpr.equals(Transazione.model().ID_TRANSAZIONE,idTransazione);

			List<Map<String,Object>> selectField = transazioneServiceSearch.select(pagExpr, Transazione.model().DIAGNOSTICI, 
					Transazione.model().DIAGNOSTICI_LIST_1, Transazione.model().DIAGNOSTICI_LIST_2,
					Transazione.model().DIAGNOSTICI_LIST_EXT, Transazione.model().DIAGNOSTICI_EXT,
					Transazione.model().TOKEN_CLIENT_ID);

			if(selectField==null || selectField.isEmpty()){
				throw new DriverMsgDiagnosticiException("Recupero informazioni per ricostruire i diagnostici non riuscito. Transazione con ID["+
						idTransazione+"] non presente?");
			}
			if(selectField.size()>1){
				throw new DriverMsgDiagnosticiException("Recupero informazioni per ricostruire i diagnostici non riuscito] non riuscito. Trovata piu' di una Transazione con ID["+
						idTransazione+"]?");
			}

			Map<String,Object> dbValue = selectField.get(0);

			String dbValueMetaInf = this.readValue(dbValue, Transazione.model().DIAGNOSTICI, idTransazione);
			if(dbValueMetaInf==null){
				// non sono presenti informazioni sulla simulazione
				return null;
			}

			String dbValueList1 = this.readValue(dbValue, Transazione.model().DIAGNOSTICI_LIST_1, idTransazione);
			String dbValueList2 = this.readValue(dbValue, Transazione.model().DIAGNOSTICI_LIST_2, idTransazione);
			String dbValueListExt = this.readValue(dbValue, Transazione.model().DIAGNOSTICI_LIST_EXT, idTransazione);
			String dbValueDatiExt = this.readValue(dbValue, Transazione.model().DIAGNOSTICI_EXT, idTransazione);


			InformazioniRecordDiagnostici info = InformazioniRecordDiagnostici.convertoFromDBColumnValue(dbValueMetaInf, 
					dbValueList1, dbValueList2,
					dbValueListExt, dbValueDatiExt
					);
			String msgDebug = "RECUPERO ["+info.toString()+"]";
			this.log.debug(msgDebug);
			
			
			if(credentialsFiller!=null) {
				String dbValueTokenClientId = this.readValue(dbValue, Transazione.model().TOKEN_CLIENT_ID, idTransazione);
				if(dbValueTokenClientId!=null && StringUtils.isNotEmpty(dbValueTokenClientId)) {
					CredenzialeMittente credenzialeClientId = getCredenzialeMittente(transazioniServiceManager, dbValueTokenClientId, idTransazione);
					credentialsFiller.setTokenClientId(credenzialeClientId);
				}
			}
			
			return info;

		}catch(Exception e){
			throw new DriverMsgDiagnosticiException("Riscontrato errore durante la lettura dei dati (Transazione:"+idTransazione+"): "+e.getMessage(),e);
		}
	}

	private String readValue(Map<String,Object> dbValue, IField field, String idTransazione) throws DriverMsgDiagnosticiException{
		Object objectDBValue = dbValue.get(field.getFieldName());
		String dbValueAsString = null;
		if((objectDBValue instanceof org.apache.commons.lang.ObjectUtils.Null)){
			objectDBValue = null;
		}
		if(objectDBValue!=null){
			if( ! (objectDBValue instanceof String)){
				throw new DriverMsgDiagnosticiException("Recupero informazioni per ricostruire i diagnostici non riuscito. Trovato campo '"+field.getFieldName()
				+"' di tipo ["+objectDBValue.getClass().getName()
				+"] differente da quello atteso ["+String.class.getName()+"] per la Transazione con ID["+
				idTransazione+"]?");
			}
			else{
				dbValueAsString = (String) objectDBValue;
			}
		}
		return dbValueAsString;
	}

}
