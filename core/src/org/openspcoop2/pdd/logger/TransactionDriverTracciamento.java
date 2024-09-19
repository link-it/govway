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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.dao.IDBCredenzialeMittenteServiceSearch;
import org.openspcoop2.core.transazioni.dao.ITransazioneServiceSearch;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.core.transazioni.utils.ProjectInfo;
import org.openspcoop2.core.transazioni.utils.PropertiesSerializator;
import org.openspcoop2.core.transazioni.utils.credenziali.CredenzialeTokenClient;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.NonNegativeNumber;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.expression.SortOrder;
import org.openspcoop2.pdd.core.transazioni.DateUtility;
import org.openspcoop2.pdd.logger.record.AbstractDatoRicostruzione;
import org.openspcoop2.pdd.logger.traccia.CostantiMappingTracciamento;
import org.openspcoop2.pdd.logger.traccia.InformazioniRecordTraccia;
import org.openspcoop2.pdd.logger.traccia.MappingRicostruzioneTraccia;
import org.openspcoop2.pdd.logger.traccia.PopolamentoTracciaUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.constants.EsitoElaborazioneMessaggioTracciatura;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoException;
import org.openspcoop2.protocol.sdk.tracciamento.DriverTracciamentoNotFoundException;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracce;
import org.openspcoop2.protocol.sdk.tracciamento.FiltroRicercaTracceConPaginazione;
import org.openspcoop2.protocol.sdk.tracciamento.ITracciaDriver;
import org.openspcoop2.protocol.sdk.tracciamento.InformazioniProtocollo;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.slf4j.Logger;

/**     
 * TransactionDriverTracciamento
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionDriverTracciamento implements ITracciaDriver {

	private static final String TIPO_DB_NON_GESTITO = "Tipo database non gestito";
	private static final String NON_IMPLEMENTATO = "Metodo non implementato";


	/** 
	 * DataSource
	 */
	DataSource datasource = null;
	
	/** 
	 * Connessione
	 */
	Connection connection = null;
	boolean connectionOpenViaJDBCInCostructor = false;

	/**
	 * SQLQueryObject
	 */
	String tipoDatabase = null;

	/** Driver Tracciamento originale */
	DriverTracciamento driverTracciamento = null;

	/** DAO Factory */
	DAOFactory daoFactory = null;
	
	/** Logger utilizzato per info. */
	private Logger log = null;
	private void logDebug(String msg) {
		if(this.log!=null) {
			this.log.debug(msg);
		}
	}

	public TransactionDriverTracciamento(String nomeDataSource, String tipoDatabase, Properties prop) throws DriverTracciamentoException {
		this(nomeDataSource,tipoDatabase,prop,null);
	}

	public TransactionDriverTracciamento(String nomeDataSource, String tipoDatabase, Properties prop, Logger log) throws DriverTracciamentoException {
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(org.openspcoop2.protocol.basic.Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverTracciamentoException("(ds jndiName) Errore durante l'inizializzazione del logger: "+e.getMessage(),e);
		}

		// Datasource
		try {
			InitialContext initCtx = new InitialContext(prop);
			this.datasource = (DataSource) initCtx.lookup(nomeDataSource);
			if (this.datasource == null)
				throw new DriverTracciamentoException ("datasource is null");

			initCtx.close();
		} catch (Exception e) {
			throw new DriverTracciamentoException("(ds jndiName) Errore durante la ricerca del datasource: "+e.getMessage(),e);
		}

		// ISQLQueryObject
		try {
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new DriverTracciamentoException(TIPO_DB_NON_GESTITO);
			}
		} catch (Exception e) {
			throw new DriverTracciamentoException("(ds jndiName) Errore durante la ricerca del SQLQueryObject: "+e.getMessage(),e);
		}

		// Driver tracciamento
		try{
			this.driverTracciamento = new DriverTracciamento(nomeDataSource, tipoDatabase, prop, log);
		} catch (Exception e) {
			throw new DriverTracciamentoException("(ds jndiName) Errore durante l'inizializzazione del driver delle tracce: "+e.getMessage(),e);
		}
		
		// DAO Factory
		try{
			this.daoFactory = DAOFactory.getInstance(log);
		}catch (Exception e) {
			throw new DriverTracciamentoException("(ds jndiName) Errore durante l'inizializzazione del dao factory: "+e.getMessage(),e);
		}
	}
	
	public TransactionDriverTracciamento(DataSource dataSourceObject, String tipoDatabase) throws DriverTracciamentoException {
		this(dataSourceObject,tipoDatabase,null);
	}

	public TransactionDriverTracciamento(DataSource dataSourceObject, String tipoDatabase, Logger log) throws DriverTracciamentoException {
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(org.openspcoop2.protocol.basic.Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverTracciamentoException("(ds) Errore durante l'inizializzazione del logger: "+e.getMessage(),e);
		}

		// Datasource
		try {
			this.datasource = dataSourceObject;
			if (this.datasource == null)
				throw new DriverTracciamentoException ("datasource is null");
		} catch (Exception e) {
			throw new DriverTracciamentoException("(ds) Errore durante la ricerca del datasource: "+e.getMessage(),e);
		}

		// ISQLQueryObject
		try {
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new DriverTracciamentoException(TIPO_DB_NON_GESTITO);
			}
		} catch (Exception e) {
			throw new DriverTracciamentoException("(ds) Errore durante la ricerca del SQLQueryObject: "+e.getMessage(),e);
		}

		// Driver tracciamento
		try{
			this.driverTracciamento = new DriverTracciamento(this.datasource, tipoDatabase, log);
		} catch (Exception e) {
			throw new DriverTracciamentoException("(ds) Errore durante l'inizializzazione del driver delle tracce: "+e.getMessage(),e);
		}	
		
		// DAO Factory
		try{
			this.daoFactory = DAOFactory.getInstance(log);
		}catch (Exception e) {
			throw new DriverTracciamentoException("(ds) Errore durante l'inizializzazione del dao factory: "+e.getMessage(),e);
		}
	}
	
	
	public TransactionDriverTracciamento(Connection connection, String tipoDatabase) throws DriverTracciamentoException {
		this(connection,tipoDatabase,null);
	}

	public TransactionDriverTracciamento(Connection connection, String tipoDatabase, Logger log) throws DriverTracciamentoException {
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(org.openspcoop2.protocol.basic.Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverTracciamentoException("(connection) Errore durante l'inizializzazione del logger: "+e.getMessage(),e);
		}

		// connection
		this.connection = connection;

		// ISQLQueryObject
		try {
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new DriverTracciamentoException(TIPO_DB_NON_GESTITO);
			}
		} catch (Exception e) {
			throw new DriverTracciamentoException("(connection) Errore durante la ricerca del SQLQueryObject: "+e.getMessage(),e);
		}

		// Driver tracciamento
		try{
			this.driverTracciamento = new DriverTracciamento(this.connection, tipoDatabase, log);
		} catch (Exception e) {
			throw new DriverTracciamentoException("(connection) Errore durante l'inizializzazione del driver delle tracce: "+e.getMessage(),e);
		}	
		
		// DAO Factory
		try{
			this.daoFactory = DAOFactory.getInstance(log);
		}catch (Exception e) {
			throw new DriverTracciamentoException("(connection) Errore durante l'inizializzazione del dao factory: "+e.getMessage(),e);
		}
	}
	
	
	public TransactionDriverTracciamento(String urlJDBC,String driverJDBC,
			String username,String password, String tipoDatabase) throws DriverTracciamentoException {
		this(urlJDBC,driverJDBC,username,password,tipoDatabase,null);
	}

	public TransactionDriverTracciamento(String urlJDBC,String driverJDBC,
			String username,String password, String tipoDatabase, Logger log) throws DriverTracciamentoException {
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(org.openspcoop2.protocol.basic.Costanti.LOGANALIZER_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverTracciamentoException("(connection url) Errore durante l'inizializzazione del logger: "+e.getMessage(),e);
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
			throw new DriverTracciamentoException("(connection url) Errore durante l'inizializzazione della connessione: "+e.getMessage(),e);
		}

		// ISQLQueryObject
		try {
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new DriverTracciamentoException(TIPO_DB_NON_GESTITO);
			}
		} catch (Exception e) {
			throw new DriverTracciamentoException("(connection url) Errore durante la ricerca del SQLQueryObject: "+e.getMessage(),e);
		}

		// Driver tracciamento
		try{
			this.driverTracciamento = new DriverTracciamento(this.connection, tipoDatabase, log);
		} catch (Exception e) {
			throw new DriverTracciamentoException("(connection url) Errore durante l'inizializzazione del driver delle tracce: "+e.getMessage(),e);
		}	
		
		// DAO Factory
		try{
			this.daoFactory = DAOFactory.getInstance(log);
		}catch (Exception e) {
			throw new DriverTracciamentoException("(connection url) Errore durante l'inizializzazione del dao factory: "+e.getMessage(),e);
		}
	}
	
	
	
	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return null;
	}

	private Connection getConnection() throws SQLException {
		Connection con = null;
		if(this.connection!=null){
			con = this.connection;
		}else{
			con = this.datasource.getConnection();
		}
		return  con;
	}
	private void checkConnection(Connection con) throws DriverTracciamentoException {
		if(con==null)
			throw new DriverTracciamentoException("Connection non ottenuta");
	}
	private void closeConnection(Connection con) {
		if(this.connection==null){
			try{
				if(con!=null) {
					con.close();
				}
			}catch(Exception e){
				// close
			}
		}
	}
	
	@Override
	public int countTracce(FiltroRicercaTracce filtro)
			throws DriverTracciamentoException {

		Connection con = null;
		JDBCServiceManager transazioniServiceManager = null;
		long transazioniTrovate = 0;
		try{
			con = getConnection();
			checkConnection(con);
						
			transazioniServiceManager = 
					(JDBCServiceManager) this.daoFactory.getServiceManager(new ProjectInfo(), con, true);
			
			ITransazioneServiceSearch transazioneServiceSearch = transazioniServiceManager.getTransazioneServiceSearch();
						
			IExpression expression = this.newExpressionEngine(transazioneServiceSearch, filtro);
			NonNegativeNumber nn = transazioneServiceSearch.count(expression);
			if(nn!=null){
				transazioniTrovate = nn.longValue();
			}			
		}catch(Exception e){
			throw new DriverTracciamentoException("Riscontrato errore durante la lettura (count) dei dati (Filtro:"+filtro.toString()+"): "+e.getMessage(),e);
		}finally{
			try{
				if(transazioniServiceManager!=null) {
					transazioniServiceManager.close();
				}
			}catch(Exception e){
				// close
			}
			closeConnection(con);
		}
			
		int offset = 0;
		int limit = 25;
		int count = 0;
		FiltroRicercaTracceConPaginazione filtroPaginato = new FiltroRicercaTracceConPaginazione(filtro);
		filtroPaginato.setOffset(offset);
		filtroPaginato.setLimit(limit);
		List<Traccia> list = null;
		try{
			list = this.getTracceEngine(filtroPaginato);
			while(offset<transazioniTrovate){
				count = count + list.size();
				offset = offset + limit; // analizzo 25 transazioni alla volta, le quali possono avere all'interno 0/1/2 tracce
				filtroPaginato.setOffset(offset);
				list = this.getTracceEngine(filtroPaginato);
			}
		}catch(DriverTracciamentoNotFoundException notFound){
			// ignore
		}
		return count;
	}

	@Override
	public List<Traccia> getTracce(FiltroRicercaTracceConPaginazione filtro)
			throws DriverTracciamentoException,
			DriverTracciamentoNotFoundException {
		
		if(filtro.getOffset()>0){
			throw new DriverTracciamentoException("Metodo non implementato in questa versione (Offset >0 non è ricostruibile)");
		}
		
		List<Traccia> list = this.getTracceEngine(filtro);
		if(filtro.getLimit()>0 && list.size()>filtro.getLimit()){
			List<Traccia> listWithFilter = new ArrayList<>();
			for (int i = 0; i < 25; i++) {
				listWithFilter.add(list.get(i));
			}
			return listWithFilter;
		}
		else{
			return list;
		}
	}
	private void addTracciaSafe(List<Traccia> tracce, RuoloMessaggio ruolo, Connection con, Map<String, String> propertiesRicerca, FiltroRicercaTracceConPaginazione filtro) throws DriverTracciamentoException {
		try{
			tracce.add(this.getTracciaByPropertiesEngine(con, propertiesRicerca, ruolo,
					filtro.getMinDate(), filtro.getMaxDate()));
		}catch(DriverTracciamentoNotFoundException dNotFound){
			// ignore
		}
	}
	private List<Traccia> getTracceEngine(FiltroRicercaTracceConPaginazione filtro)
			throws DriverTracciamentoException,
			DriverTracciamentoNotFoundException {
		
		Connection con = null;

		JDBCServiceManager transazioniServiceManager = null;
		try{
			con = getConnection();
			checkConnection(con);
						
			transazioniServiceManager = 
					(JDBCServiceManager) this.daoFactory.getServiceManager(new ProjectInfo(), con, true);
			
			ITransazioneServiceSearch transazioneServiceSearch = transazioniServiceManager.getTransazioneServiceSearch();
						
			IPaginatedExpression expression = this.newPaginatedExpressionEngine(transazioneServiceSearch, filtro);
						
			List<Map<String, Object>> lstTransazioni = transazioneServiceSearch.select(expression, Transazione.model().ID_TRANSAZIONE, 
					Transazione.model().ID_MESSAGGIO_RICHIESTA,Transazione.model().ID_MESSAGGIO_RISPOSTA);
			
			List<Traccia> tracce = new ArrayList<>();

			for(Map<String, Object> transazioneMap : lstTransazioni) {
				
				// **** Lettura da database ****
				
				String idTransazione = (String) transazioneMap.get(Transazione.model().ID_TRANSAZIONE.getFieldName());
				
				Object oIdBustaRichiesta = transazioneMap.get(Transazione.model().ID_MESSAGGIO_RICHIESTA.getFieldName());
				String idBustaRichiesta = (oIdBustaRichiesta instanceof String) ? (String) oIdBustaRichiesta : null;
				
				Object oIdBustaRisposta = transazioneMap.get(Transazione.model().ID_MESSAGGIO_RISPOSTA.getFieldName());
				String idBustaRisposta = (oIdBustaRisposta instanceof String)  ? (String) oIdBustaRisposta : null;
				
				Map<String, String> propertiesRicerca = new HashMap<>();
				propertiesRicerca.put(CostantiDB.TRACCE_COLUMN_ID_TRANSAZIONE, idTransazione);
				
				if(filtro.getIdBusta()!=null){
					if(filtro.getTipoTraccia()==null){
						if(filtro.getIdBusta().equals(idBustaRichiesta)){
							addTracciaSafe(tracce, RuoloMessaggio.RICHIESTA, con, propertiesRicerca, filtro);
						}else if(filtro.getIdBusta().equals(idBustaRisposta)){
							addTracciaSafe(tracce, RuoloMessaggio.RISPOSTA, con, propertiesRicerca, filtro);
						}
					}
					else{
						switch (filtro.getTipoTraccia()) {
						case RICHIESTA:
							addTracciaSafe(tracce, RuoloMessaggio.RICHIESTA, con, propertiesRicerca, filtro);
							break;
						case RISPOSTA:
							addTracciaSafe(tracce, RuoloMessaggio.RISPOSTA, con, propertiesRicerca, filtro);
							break;
						default:
							break;
						}
					}
				}
				else if(filtro.getIdBustaRichiesta()!=null || filtro.getIdBustaRisposta()!=null){
					if(filtro.getIdBustaRichiesta()!=null){
						addTracciaSafe(tracce, RuoloMessaggio.RICHIESTA, con, propertiesRicerca, filtro);
					}
					if(filtro.getIdBustaRisposta()!=null){
						addTracciaSafe(tracce, RuoloMessaggio.RISPOSTA, con, propertiesRicerca, filtro);
					}
				}
				else if(filtro.getRiferimentoMessaggio()!=null){
					addTracciaSafe(tracce, RuoloMessaggio.RISPOSTA, con, propertiesRicerca, filtro);
				}
				else{
					// aggiungo sia richiesta che risposta se presenti
					addTracciaSafe(tracce, RuoloMessaggio.RICHIESTA, con, propertiesRicerca, filtro);
					addTracciaSafe(tracce, RuoloMessaggio.RISPOSTA, con, propertiesRicerca, filtro);
				}				
			}
			
			if(tracce.isEmpty()){
				throw new DriverTracciamentoNotFoundException("Tracce non trovate che rispettano il filtro di ricerca (Filtro:"+filtro.toString()+")");
			}
			else{
				return tracce;
			}
				
		}catch(DriverTracciamentoNotFoundException e){
			throw e;
		}catch(NotFoundException e){
			throw new DriverTracciamentoNotFoundException(e.getMessage(),e);
		}catch(Exception e){
			throw new DriverTracciamentoException("Riscontrato errore durante la lettura dei dati (Filtro:"+filtro.toString()+"): "+e.getMessage(),e);
		}finally{
			try{
				if(transazioniServiceManager!=null) {
					transazioniServiceManager.close();
				}
			}catch(Exception e){
				// close
			}
			closeConnection(con);
		}
		
	}
	
	private IExpression newExpressionEngine(ITransazioneServiceSearch transazioneServiceSearch,FiltroRicercaTracce filtro) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, ProtocolException {
		IExpression expression = transazioneServiceSearch.newExpression();
		
		if(filtro.getMaxDate()!=null){
			expression.lessEquals(Transazione.model().DATA_INGRESSO_RICHIESTA, filtro.getMaxDate());
		}
		if(filtro.getMinDate()!=null){
			expression.greaterEquals(Transazione.model().DATA_INGRESSO_RICHIESTA, filtro.getMinDate());
		}
		if(filtro.getTipoPdD()!=null){
			expression.equals(Transazione.model().PDD_RUOLO, filtro.getTipoPdD().getTipo());
		}
		if(filtro.getDominio()!=null){
			if(filtro.getDominio().getCodicePorta()!=null){
				expression.equals(Transazione.model().PDD_CODICE, filtro.getDominio().getCodicePorta());
			}
			if(filtro.getDominio().getTipo()!=null){
				expression.equals(Transazione.model().PDD_TIPO_SOGGETTO, filtro.getDominio().getTipo());
			}
			if(filtro.getDominio().getNome()!=null){
				expression.equals(Transazione.model().PDD_NOME_SOGGETTO, filtro.getDominio().getNome());
			}
		}
		
		boolean isOttimizzazioniTransazioniIdProtocolloDateInCampiSeparati = true; // static info
		
		if(filtro.getIdBusta()!=null){
			IExpression expressionIdBusta = transazioneServiceSearch.newExpression();
			if(isOttimizzazioniTransazioniIdProtocolloDateInCampiSeparati){
				
				if(filtro.getProtocollo()==null){
					throw new ServiceException("Se nel filtro viene impostato l'id della busta, ed è attiva l'opzione della comprensione delle date interne agli id, deve essere specificato nel filtro anche il protocollo");
				}
				IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(filtro.getProtocollo());
				IBustaBuilder<?> protocolBustaBuilder = protocolFactory.createBustaBuilder(null);
				
				Timestamp tIdBusta = DateUtility.getTimestampIntoIdProtocollo(this.log,protocolBustaBuilder,filtro.getIdBusta());
				if(tIdBusta==null){
					if(filtro.getTipoTraccia()==null){
						expressionIdBusta.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getIdBusta());
						expressionIdBusta.or();
						expressionIdBusta.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, filtro.getIdBusta());
					}
					else{
						switch (filtro.getTipoTraccia()) {
						case RICHIESTA:
							expressionIdBusta.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getIdBusta());
							break;
						case RISPOSTA:
							expressionIdBusta.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, filtro.getIdBusta());
							break;
						default:
							break;
						}
					}
				}else{
					IExpression expressionRichiesta = transazioneServiceSearch.newExpression();
					IExpression expressionRisposta = transazioneServiceSearch.newExpression();
					
					if(filtro.getTipoTraccia()==null){
						expressionRichiesta.equals(Transazione.model().DATA_ID_MSG_RICHIESTA, tIdBusta).and().equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getIdBusta());
						expressionRisposta.equals(Transazione.model().DATA_ID_MSG_RISPOSTA, tIdBusta).and().equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, filtro.getIdBusta());
						expressionIdBusta.or(expressionRichiesta, expressionRisposta);
					}
					else{
						switch (filtro.getTipoTraccia()) {
						case RICHIESTA:
							expressionIdBusta.equals(Transazione.model().DATA_ID_MSG_RICHIESTA, tIdBusta).and().equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getIdBusta());
							break;
						case RISPOSTA:
							expressionIdBusta.equals(Transazione.model().DATA_ID_MSG_RISPOSTA, tIdBusta).and().equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, filtro.getIdBusta());
							break;
						default:
							break;
						}
					}
				}
			}else{
				if(filtro.getTipoTraccia()==null){
					expressionIdBusta.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getIdBusta()).
						or().
						equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, filtro.getIdBusta());
				}
				else{
					switch (filtro.getTipoTraccia()) {
					case RICHIESTA:
						expressionIdBusta.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getIdBusta());
						break;
					case RISPOSTA:
						expressionIdBusta.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, filtro.getIdBusta());
						break;
					default:
						break;
					}
				}
			}
			expression.and(expressionIdBusta);
		}
		
		if(filtro.getIdBustaRichiesta()!=null){
			if(isOttimizzazioniTransazioniIdProtocolloDateInCampiSeparati){
				
				if(filtro.getProtocollo()==null){
					throw new ServiceException("Se nel filtro viene impostato l'identificativo di richiesta della busta, ed è attiva l'opzione della comprensione delle date interne agli id, deve essere specificato nel filtro anche il protocollo");
				}
				IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(filtro.getProtocollo());
				IBustaBuilder<?> protocolBustaBuilder = protocolFactory.createBustaBuilder(null);
				
				Timestamp tIdBusta = DateUtility.getTimestampIntoIdProtocollo(this.log,protocolBustaBuilder,filtro.getIdBustaRichiesta());
				if(tIdBusta==null){
					expression.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getIdBustaRichiesta());
				}else{
					IExpression expressionIdBusta = transazioneServiceSearch.newExpression();
					expressionIdBusta.equals(Transazione.model().DATA_ID_MSG_RICHIESTA, tIdBusta).and().
						equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getIdBustaRichiesta());
					expression.and(expressionIdBusta);
				}
			}else{
				expression.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getIdBustaRichiesta());
			}
		}
		
		if(filtro.getIdBustaRisposta()!=null){
			if(isOttimizzazioniTransazioniIdProtocolloDateInCampiSeparati){
				
				if(filtro.getProtocollo()==null){
					throw new ServiceException("Se nel filtro viene impostato l'identificativo di risposta della busta, ed è attiva l'opzione della comprensione delle date interne agli id, deve essere specificato nel filtro anche il protocollo");
				}
				IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(filtro.getProtocollo());
				IBustaBuilder<?> protocolBustaBuilder = protocolFactory.createBustaBuilder(null);
				
				Timestamp tIdBusta = DateUtility.getTimestampIntoIdProtocollo(this.log,protocolBustaBuilder,filtro.getIdBustaRisposta());
				if(tIdBusta==null){
					expression.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, filtro.getIdBustaRisposta());
				}else{
					IExpression expressionIdBusta = transazioneServiceSearch.newExpression();
					expressionIdBusta.equals(Transazione.model().DATA_ID_MSG_RISPOSTA, tIdBusta).and().
						equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, filtro.getIdBustaRisposta());
					expression.and(expressionIdBusta);
				}
			}else{
				expression.equals(Transazione.model().ID_MESSAGGIO_RISPOSTA, filtro.getIdBustaRisposta());
			}
		}
		
		if(filtro.getRiferimentoMessaggio()!=null){
			if(isOttimizzazioniTransazioniIdProtocolloDateInCampiSeparati){
				
				if(filtro.getProtocollo()==null){
					throw new ServiceException("Se nel filtro viene impostato il riferimento messaggio della busta, ed è attiva l'opzione della comprensione delle date interne agli id, deve essere specificato nel filtro anche il protocollo");
				}
				IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(filtro.getProtocollo());
				IBustaBuilder<?> protocolBustaBuilder = protocolFactory.createBustaBuilder(null);
				
				Timestamp tRifMessaggio = DateUtility.getTimestampIntoIdProtocollo(this.log,protocolBustaBuilder,filtro.getRiferimentoMessaggio());
				if(tRifMessaggio==null){
					expression.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getRiferimentoMessaggio());
				}else{
					IExpression expressionRiferimento = transazioneServiceSearch.newExpression();
					expressionRiferimento.equals(Transazione.model().DATA_ID_MSG_RICHIESTA, tRifMessaggio).and().equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getRiferimentoMessaggio());
					expression.and(expressionRiferimento);
				}
			}else{
				expression.equals(Transazione.model().ID_MESSAGGIO_RICHIESTA, filtro.getRiferimentoMessaggio());
			}
		}

		if(filtro.getInformazioniProtocollo()!=null){
			InformazioniProtocollo info = filtro.getInformazioniProtocollo();
			if(info.getMittente()!=null){
				if(info.getMittente().getTipo()!=null){
					expression.equals(Transazione.model().TIPO_SOGGETTO_FRUITORE, info.getMittente().getTipo());
				}
				if(info.getMittente().getNome()!=null){
					expression.equals(Transazione.model().NOME_SOGGETTO_FRUITORE, info.getMittente().getNome());
				}
				if(info.getMittente().getCodicePorta()!=null){
					expression.equals(Transazione.model().IDPORTA_SOGGETTO_FRUITORE, info.getMittente().getCodicePorta());
				}
			}
			if(info.getDestinatario()!=null){
				if(info.getDestinatario().getTipo()!=null){
					expression.equals(Transazione.model().TIPO_SOGGETTO_EROGATORE, info.getDestinatario().getTipo());
				}
				if(info.getDestinatario().getNome()!=null){
					expression.equals(Transazione.model().NOME_SOGGETTO_EROGATORE, info.getDestinatario().getNome());
				}
				if(info.getDestinatario().getCodicePorta()!=null){
					expression.equals(Transazione.model().IDPORTA_SOGGETTO_EROGATORE, info.getDestinatario().getCodicePorta());
				}
			}
			if(info.getTipoServizio()!=null){
				expression.equals(Transazione.model().TIPO_SERVIZIO, info.getTipoServizio());
			}
			if(info.getServizio()!=null){
				expression.equals(Transazione.model().NOME_SERVIZIO, info.getServizio());
			}
			if(info.getAzione()!=null){
				expression.equals(Transazione.model().AZIONE, info.getAzione());
			}
			if(info.getProfiloCollaborazioneEngine()!=null){
				expression.equals(Transazione.model().PROFILO_COLLABORAZIONE_OP_2, info.getProfiloCollaborazioneEngine().getEngineValue());
			}
			if(info.getProfiloCollaborazioneProtocollo()!=null){
				expression.equals(Transazione.model().PROFILO_COLLABORAZIONE_PROT, info.getProfiloCollaborazioneProtocollo());
			}
		}

		// Correlazione Applicativa Richiesta / Risposta: 
		// - se vengono forniti entrambi i campi, li controllo entrambi (in AND o OR a seconda del parametro isIdCorrelazioneApplicativaOrMatch)
		// - se viene fornito uno solo dei campi viene aggiunto in maniera canonica
		
		if(filtro.getIdCorrelazioneApplicativa() != null && filtro.getIdCorrelazioneApplicativaRisposta() != null){
			
			IExpression correlazioneExpression = transazioneServiceSearch.newExpression();
			
			correlazioneExpression.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA, filtro.getIdCorrelazioneApplicativa());
			if(filtro.isIdCorrelazioneApplicativaOrMatch()) {
				correlazioneExpression.or();
			} else {
				correlazioneExpression.and();
			}
			correlazioneExpression.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA, filtro.getIdCorrelazioneApplicativaRisposta());

			expression.and(correlazioneExpression);
			
		}	else if(filtro.getIdCorrelazioneApplicativa() != null){
			expression.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA, filtro.getIdCorrelazioneApplicativa());
		}	else if(filtro.getIdCorrelazioneApplicativaRisposta() != null){
			expression.equals(Transazione.model().ID_CORRELAZIONE_APPLICATIVA_RISPOSTA, filtro.getIdCorrelazioneApplicativaRisposta());
		}
		
		if(filtro.getServizioApplicativoFruitore()!=null){
			expression.equals(Transazione.model().SERVIZIO_APPLICATIVO_FRUITORE, filtro.getServizioApplicativoFruitore());
		}
		if(filtro.getServizioApplicativoErogatore()!=null){
			expression.equals(Transazione.model().SERVIZIO_APPLICATIVO_EROGATORE, filtro.getServizioApplicativoErogatore());
		}

		if(filtro.getProtocollo()!=null){
			expression.equals(Transazione.model().PROTOCOLLO, filtro.getProtocollo());
		}
		
		return expression;
	}
	private IPaginatedExpression newPaginatedExpressionEngine(ITransazioneServiceSearch transazioneServiceSearch,FiltroRicercaTracceConPaginazione filtro) throws ServiceException, NotImplementedException, ExpressionNotImplementedException, ExpressionException, ProtocolException {
		IPaginatedExpression expression = transazioneServiceSearch.toPaginatedExpression(this.newExpressionEngine(transazioneServiceSearch, filtro));
		
		expression.sortOrder(filtro.isAsc() ? SortOrder.ASC : SortOrder.DESC);
		expression.addOrder(Transazione.model().DATA_INGRESSO_RICHIESTA);
		
		expression.offset(filtro.getOffset());
		if(filtro.getLimit()>0)
			expression.limit(filtro.getLimit());
		
		return expression;
	}

	@Override
	public int deleteTracce(FiltroRicercaTracce filter)
			throws DriverTracciamentoException {
		throw new DriverTracciamentoException(NON_IMPLEMENTATO);
	}

	@Override
	public Traccia getTraccia(String idBusta, IDSoggetto codicePorta)
			throws DriverTracciamentoException,
			DriverTracciamentoNotFoundException {
		throw new DriverTracciamentoException(NON_IMPLEMENTATO);
	}

	@Override
	public Traccia getTraccia(String idBusta, IDSoggetto codicePorta,
			boolean ricercaIdBustaComeRiferimentoMessaggio)
			throws DriverTracciamentoException,
			DriverTracciamentoNotFoundException {
		throw new DriverTracciamentoException(NON_IMPLEMENTATO);
	}

	@Override
	public Traccia getTraccia(RuoloMessaggio tipoTraccia,
			Map<String, String> propertiesRicerca)
			throws DriverTracciamentoException,
			DriverTracciamentoNotFoundException {
		try{
			Traccia tr = getTracciaByPropertiesEngine(null, propertiesRicerca, tipoTraccia,
					null, null);			
			if(tr==null){
				throw new DriverTracciamentoNotFoundException("NotFound");
			}
			return tr;
			
		}catch(DriverTracciamentoNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new DriverTracciamentoException(e.getMessage(),e);
		}
	}
	
	@Override
	public Traccia getTraccia(String idTransazione, RuoloMessaggio tipoTraccia)
			throws DriverTracciamentoException, DriverTracciamentoNotFoundException {
		
		Map<String, String> propertiesRicerca = new HashMap<>();
		propertiesRicerca.put(CostantiDB.TRACCE_COLUMN_ID_TRANSAZIONE, idTransazione);
		return this.getTraccia(tipoTraccia, propertiesRicerca);
		
	}
	
	
	
	/* ******* RISORSE INTERNE ********** */
	
	@Override
	public void close() throws DriverTracciamentoException {
		try{
			if(this.driverTracciamento!=null){
				this.driverTracciamento.close();
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
				/**throw new DriverTracciamentoException(e.getMessage(),e);*/
			}
		}
	}
	
	
	
	private static final long ID_TRACCIA_RICOSTRUITA = -2;  // viene usato il valore -2 per indicare che si tratta di una traccia ricostruita (per funzionare dove viene usato org.openspcoop2.protocol.basic.tracciamento.DriverTracciamento.IDTRACCIA)
	private static final String ID_TRACCIA_RICOSTRUITA_AS_STRING = ID_TRACCIA_RICOSTRUITA + "";	
	
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
	
	private Traccia getTracciaByPropertiesEngine(
			Connection conDB,
			Map<String, String> propertiesRicerca,
			RuoloMessaggio tipoTraccia,
			Date minDate,
			Date maxDate)
			throws DriverTracciamentoException,
			DriverTracciamentoNotFoundException {
		
		String idTransazione = propertiesRicerca.get(CostantiDB.TRACCE_COLUMN_ID_TRANSAZIONE);
		if(idTransazione==null){
			throw new DriverTracciamentoException("Identificativo di transazione non fornito");
		}
		String idTraccia = propertiesRicerca.get(org.openspcoop2.protocol.basic.tracciamento.TracciaDriver.IDTRACCIA);
		boolean removeIdTraccia = false;
		if(idTraccia!=null && ID_TRACCIA_RICOSTRUITA_AS_STRING.equals(idTraccia)){
			propertiesRicerca.remove(org.openspcoop2.protocol.basic.tracciamento.TracciaDriver.IDTRACCIA);
			removeIdTraccia = true;
		}
		
		Connection con = null;
		boolean closeConnection = true;
		JDBCServiceManager transazioniServiceManager = null;
		try{
			if(conDB!=null){
				con = conDB;
				closeConnection = false;
			}else{
				if(this.connection!=null){
					con = this.connection;
					closeConnection = false;
				}else{
					con = this.datasource.getConnection();
				}
			}
			if(con==null)
				throw new DriverTracciamentoException("Connection non ottenuta");
			
			transazioniServiceManager = 
					(JDBCServiceManager) this.daoFactory.getServiceManager(new ProjectInfo(), con, true);
			
			
			// Informazioni salvataggio tracce
			CredenzialiMittente credentialsFiller = new CredenzialiMittente();
			InformazioniRecordTraccia informazioniSalvataggioTraccia = getInformazioniSalvataggioTraccia(transazioniServiceManager, idTransazione, tipoTraccia, credentialsFiller,
					minDate,
					maxDate);
			
			// Non vi sono informazioni eventuali sulla simulazione. Provo a recuperarla direttamente
			if(informazioniSalvataggioTraccia==null){
				
				// per evitare deadlock di connessioni
				transazioniServiceManager = close(transazioniServiceManager);
				if(closeConnection){
					con = close(con);
				}
				
				Traccia traccia = this.driverTracciamento.getTraccia(tipoTraccia, propertiesRicerca);
				// set Properties
				setPropertiesEngine(traccia, propertiesRicerca, idTransazione);
				return traccia;
			}
			
			// Non era stata emessa una traccia di richiesta per la transazione
			if(!informazioniSalvataggioTraccia.isPresente()){
				throw new DriverTracciamentoNotFoundException("non presente");
			}
			
			// La traccia era stata interamente salvata nel database delle tracce, poiche' non possedeva tutti i valori ricostruibili automaticamente
			if(!informazioniSalvataggioTraccia.isRicostruibile()){
				
				// per evitare deadlock di connessioni
				transazioniServiceManager = close(transazioniServiceManager);
				if(closeConnection){
					close(con);
				}
				
				Traccia traccia = this.driverTracciamento.getTraccia(tipoTraccia, propertiesRicerca);
				// set Properties
				setPropertiesEngine(traccia, propertiesRicerca, idTransazione);
				return traccia;
			}
			
			// Recupero transazione
			ITransazioneServiceSearch transazioneServiceSearch = transazioniServiceManager.getTransazioneServiceSearch();
			Transazione transazione = null;
			if(maxDate!=null && minDate!=null){
				// ottimizzazione per partizioni
				IPaginatedExpression exprGetIdTransazione = transazioneServiceSearch.newPaginatedExpression();
				if(maxDate!=null){
					exprGetIdTransazione.lessEquals(Transazione.model().DATA_INGRESSO_RICHIESTA, maxDate);
				}
				if(minDate!=null){
					exprGetIdTransazione.greaterEquals(Transazione.model().DATA_INGRESSO_RICHIESTA, minDate);
				}
				exprGetIdTransazione.equals(Transazione.model().ID_TRANSAZIONE,idTransazione);
				/**transazione = transazioneServiceSearch.find(exprGetIdTransazione);*/
				// NOTA: findAll più efficiente
				List<Transazione> transazioni = transazioneServiceSearch.findAll(exprGetIdTransazione);
				if(transazioni==null || transazioni.isEmpty()) {
					throw new DriverTracciamentoNotFoundException("Transazione con id '"+idTransazione+"' non trovata minDate:"+minDate+" maxDate:"+maxDate+"");
				}
				transazione = transazioni.get(0);
				if(transazione == null) {
					throw new DriverTracciamentoNotFoundException("Transazione con id '"+idTransazione+"' minDate:"+minDate+" maxDate:"+maxDate+" ritornata null?");
				}			
			}
			else {
				transazione = transazioneServiceSearch.get(idTransazione);
			}
			
			// Costruzione traccia
			Traccia traccia = this.buildTraccia(tipoTraccia, transazione, credentialsFiller.getTokenClientId(), informazioniSalvataggioTraccia);
			// set Properties
			setPropertiesEngine(traccia, propertiesRicerca, idTransazione);
			return traccia;
								
		}catch(DriverTracciamentoNotFoundException e){
			throw e;
		}catch(Exception e){
			throw new DriverTracciamentoException("Riscontrato errore durante la lettura dei dati (Transazione:"+idTransazione+"): "+e.getMessage(),e);
		}finally{
			close(transazioniServiceManager);
			if(closeConnection){
				close(con);
			}
			if(removeIdTraccia){
				propertiesRicerca.put(org.openspcoop2.protocol.basic.tracciamento.TracciaDriver.IDTRACCIA,idTraccia);
			}
		}
		
	}
	private void setPropertiesEngine(Traccia traccia, Map<String, String> propertiesRicerca, String idTransazione){
		// set Properties
		if(traccia.getProperties()==null){
			traccia.setProperties(new HashMap<>());
		}
		if(propertiesRicerca!=null && propertiesRicerca.size()>0){
			traccia.getProperties().putAll(propertiesRicerca);
		}
		if(!traccia.getProperties().containsKey(CostantiDB.TRACCE_COLUMN_ID_TRANSAZIONE)){
			traccia.getProperties().put(CostantiDB.TRACCE_COLUMN_ID_TRANSAZIONE, idTransazione);
		}
		if(!traccia.getProperties().containsKey(org.openspcoop2.protocol.basic.tracciamento.TracciaDriver.IDTRACCIA)){
			traccia.getProperties().put(org.openspcoop2.protocol.basic.tracciamento.TracciaDriver.IDTRACCIA, ID_TRACCIA_RICOSTRUITA_AS_STRING); 
		}
	}

	private Traccia buildTraccia(RuoloMessaggio tipoTraccia, Transazione transazione, CredenzialeMittente credenzialeClientId, InformazioniRecordTraccia infoSalvataggioTraccia) throws CoreException {
		
		Traccia traccia = new Traccia();
		
		Object oGdo = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_DATA_REGISTRAZIONE).getDato();
		traccia.setGdo(oGdo!=null ? (Date)oGdo : null);
		
		IDSoggetto idSoggetto = null;
		if(transazione.getPddCodice()!=null || transazione.getPddTipoSoggetto()!=null || transazione.getPddNomeSoggetto()!=null){
			idSoggetto = new IDSoggetto(transazione.getPddTipoSoggetto(), transazione.getPddNomeSoggetto(), transazione.getPddCodice());
		}
		traccia.setIdSoggetto(idSoggetto);
		
		switch (transazione.getPddRuolo()) {
		case APPLICATIVA:
			traccia.setTipoPdD(TipoPdD.APPLICATIVA);	
			break;
		case DELEGATA:
			traccia.setTipoPdD(TipoPdD.DELEGATA);	
			break;
		case INTEGRATION_MANAGER:
			traccia.setTipoPdD(TipoPdD.INTEGRATION_MANAGER);	
			break;
		case ROUTER:
			traccia.setTipoPdD(TipoPdD.ROUTER);	
			break;
		default:
			break;
		}
		
		traccia.setTipoMessaggio(tipoTraccia);

		traccia.setBusta(this.buildBusta(tipoTraccia, transazione, credenzialeClientId, infoSalvataggioTraccia ));
		
		if(tipoTraccia.equals(RuoloMessaggio.RICHIESTA)) {
			traccia.setBustaAsString(transazione.getHeaderProtocolloRichiesta());
		} else {
			traccia.setBustaAsString(transazione.getHeaderProtocolloRisposta());
		}
		
		traccia.setCorrelazioneApplicativa(transazione.getIdCorrelazioneApplicativa());
		traccia.setCorrelazioneApplicativaRisposta(transazione.getIdCorrelazioneApplicativaRisposta());
		
		if(tipoTraccia.equals(RuoloMessaggio.RICHIESTA)) {
			traccia.setLocation(transazione.getLocationRichiesta());
		} else {
			traccia.setLocation(transazione.getLocationRisposta());
		}
		
		traccia.setProtocollo(transazione.getProtocollo());
		
		EsitoElaborazioneMessaggioTracciato esito = new EsitoElaborazioneMessaggioTracciato();
		Object oEsito = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_ESITO_TRACCIA).getDato();
		if(oEsito!=null){
			Character esitoChar = (Character) oEsito;
			if(CostantiMappingTracciamento.TRACCIA_BUSTA_ESITO_TRACCIA_INVIATA == esitoChar ){
				esito.setEsito(EsitoElaborazioneMessaggioTracciatura.INVIATO);
			}
			else if(CostantiMappingTracciamento.TRACCIA_BUSTA_ESITO_TRACCIA_RICEVUTA == esitoChar ){
				esito.setEsito(EsitoElaborazioneMessaggioTracciatura.RICEVUTO);
			}
			else if(CostantiMappingTracciamento.TRACCIA_BUSTA_ESITO_TRACCIA_ERRORE == esitoChar ){
				esito.setEsito(EsitoElaborazioneMessaggioTracciatura.ERRORE);
			}
		}
		if(esito.getEsito()!=null && esito.getEsito().equals(EsitoElaborazioneMessaggioTracciatura.ERRORE) && 
				TipoPdD.DELEGATA.equals(traccia.getTipoPdD())) {
			esito.setDettaglio(transazione.getFaultCooperazione());
		}
		
		traccia.setEsitoElaborazioneMessaggioTracciato(esito);
		
		return traccia;
	}
	
	private Busta buildBusta(RuoloMessaggio tipoTraccia, Transazione transazione, CredenzialeMittente credenzialeClientId, 
			InformazioniRecordTraccia infoSalvataggioTraccia) throws CoreException {
		
		boolean richiesta = tipoTraccia.equals(RuoloMessaggio.RICHIESTA);
		
		IDSoggetto mittente = PopolamentoTracciaUtils.getIdSoggettoMittente(transazione, richiesta);
		IDSoggetto destinatario = PopolamentoTracciaUtils.getIdSoggettoDestinatario(transazione, richiesta);
		
		
		Busta busta = new Busta(transazione.getProtocollo());
		
		busta.setMittente(mittente.getNome());
		busta.setTipoMittente(mittente.getTipo());
		busta.setIdentificativoPortaMittente(mittente.getCodicePorta());
		busta.setIndirizzoMittente(richiesta ? transazione.getIndirizzoSoggettoFruitore() :  transazione.getIndirizzoSoggettoErogatore());
		
		busta.setDestinatario(destinatario.getNome());
		busta.setTipoDestinatario(destinatario.getTipo());
		busta.setIdentificativoPortaDestinatario(destinatario.getCodicePorta());
		busta.setIndirizzoDestinatario(richiesta ? transazione.getIndirizzoSoggettoErogatore() :  transazione.getIndirizzoSoggettoFruitore());
		
		boolean applicativoToken = false;
		
		if(credenzialeClientId!=null) {
			AbstractDatoRicostruzione<?> dato = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_SOGGETTO_APPLICATIVO_TOKEN);
			Object tracciaBustaSoggettoApplicativoToken = null;
			if(dato!=null) {
				tracciaBustaSoggettoApplicativoToken = dato.getDato();
			}
			if(tracciaBustaSoggettoApplicativoToken != null) {
				boolean soggettoApplicativoToken = ((Character)tracciaBustaSoggettoApplicativoToken) == CostantiMappingTracciamento.TRACCIA_BUSTA_SOGGETTO_APPLICATIVO_TOKEN_TRUE;
				if(soggettoApplicativoToken) {
					IDServizioApplicativo idApplicativoToken = null;
					if(credenzialeClientId!=null && credenzialeClientId.getCredenziale()!=null) {
						try {
							idApplicativoToken = CredenzialeTokenClient.convertApplicationDBValueToOriginal(credenzialeClientId.getCredenziale());
						}catch(Exception e) {
							// ignore
						}
					}
					if(idApplicativoToken!=null && idApplicativoToken.getIdSoggettoProprietario()!=null) {
						if(RuoloMessaggio.RICHIESTA.equals(tipoTraccia)) {
							busta.setMittente(idApplicativoToken.getIdSoggettoProprietario().getNome());
							busta.setTipoMittente(idApplicativoToken.getIdSoggettoProprietario().getTipo());
							busta.setIdentificativoPortaMittente(null);
							busta.setIndirizzoMittente(null);
							applicativoToken=true;
						}
						else {
							busta.setDestinatario(idApplicativoToken.getIdSoggettoProprietario().getNome());
							busta.setTipoDestinatario(idApplicativoToken.getIdSoggettoProprietario().getTipo());
							busta.setIdentificativoPortaDestinatario(null);
							busta.setIndirizzoDestinatario(null);
							applicativoToken=true;
						}
					}
				}
			}
		}
		
		busta.setProfiloDiCollaborazione(ProfiloDiCollaborazione.toProfiloDiCollaborazione(transazione.getProfiloCollaborazioneOp2()));
		busta.setProfiloDiCollaborazioneValue(transazione.getProfiloCollaborazioneProt());
		
		Object oServizioCorrelatoPresente = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_SERVIZIO_CORRELATO_PRESENTE).getDato();

		if(((Character)oServizioCorrelatoPresente).equals(CostantiMappingTracciamento.TRACCIA_BUSTA_SERVIZIO_CORRELATO_PRESENTE_TRUE)) {
			busta.setServizioCorrelato(transazione.getNomeServizioCorrelato());
			busta.setTipoServizioCorrelato(transazione.getTipoServizioCorrelato());
		}
		
		busta.setCollaborazione(transazione.getIdCollaborazione());
		
		busta.setServizio(transazione.getNomeServizio());
		busta.setTipoServizio(transazione.getTipoServizio());
		busta.setVersioneServizio(transazione.getVersioneServizio());
		
		busta.setAzione(transazione.getAzione());
		
		
		if(RuoloMessaggio.RICHIESTA.equals(tipoTraccia)){
			busta.setID(transazione.getIdMessaggioRichiesta());
		}else{
			busta.setID(transazione.getIdMessaggioRisposta());
		}
		
		Object oRiferimentoMessaggio = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_RIFERIMENTO_MESSAGGIO).getDato();
		if(oRiferimentoMessaggio!=null){
			char riferimento = ((Character)oRiferimentoMessaggio);
			String riferimentoMessaggio;
			if(CostantiMappingTracciamento.TRACCIA_BUSTA_RIFERIMENTO_MESSAGGIO_ID_MESSAGGIO_RICHIESTA == riferimento) {
				riferimentoMessaggio = transazione.getIdMessaggioRichiesta();
			} else if(CostantiMappingTracciamento.TRACCIA_BUSTA_RIFERIMENTO_MESSAGGIO_ID_MESSAGGIO_RISPOSTA == riferimento) {
				riferimentoMessaggio = transazione.getIdMessaggioRisposta();
			} else {
				riferimentoMessaggio = transazione.getIdAsincrono();
			}
			
			busta.setRiferimentoMessaggio(riferimentoMessaggio);
		}
		
		Object oOraRegistrazione = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_ORA_REGISTRAZIONE).getDato();
		busta.setOraRegistrazione(oOraRegistrazione!=null ? (Date)oOraRegistrazione : null);
		
		Object oTipoOraRegistrazione = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP).getDato();
		TipoOraRegistrazione tipoOraRegistrazione=null;
		if(oTipoOraRegistrazione!=null){
			char tipo = ((Character)oTipoOraRegistrazione);
			if(CostantiMappingTracciamento.TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP_SCONOSCIUTO == tipo){
				tipoOraRegistrazione = TipoOraRegistrazione.UNKNOWN;
			}
			else if(CostantiMappingTracciamento.TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP_SINCRONIZZATO == tipo){
				tipoOraRegistrazione = TipoOraRegistrazione.SINCRONIZZATO;
			}
			else if(CostantiMappingTracciamento.TIPO_ORA_REGISTRAZIONE_BY_OPENSPCOOP_LOCALE == tipo){
				tipoOraRegistrazione = TipoOraRegistrazione.LOCALE;
			}
		}
		Object oTipoOraRegistrazioneValue = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_TIPO_ORA_REGISTRAZIONE_BY_PROTOCOLLO).getDato();
		busta.setTipoOraRegistrazione(tipoOraRegistrazione, 
				oTipoOraRegistrazioneValue!=null ? (String)oTipoOraRegistrazioneValue : null);
		

		Object oScadenza = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_SCADENZA).getDato();
		if(oScadenza != null) {
			busta.setScadenza((Date) oScadenza);
		}

		Object oConfermaRicezione = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_CONFERMA_RICHIESTA).getDato();
		
		if(oConfermaRicezione != null) {
			busta.setConfermaRicezione(((Character)oConfermaRicezione) == CostantiMappingTracciamento.TRACCIA_BUSTA_CONFERMA_RICHIESTA_TRUE);
		}

		Object oInoltro = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_FILTRO_DUPLICATI_CODE).getDato();
		Inoltro inoltro = null;
		
		if(oInoltro != null) {
			char value = ((Character)oInoltro);
			if(CostantiMappingTracciamento.TRACCIA_FILTRO_DUPLICATI_CODE_ALPIUUNAVOLTA == value) {
				inoltro = Inoltro.SENZA_DUPLICATI;
			} else if(CostantiMappingTracciamento.TRACCIA_FILTRO_DUPLICATI_CODE_PIUDIUNAVOLTA == value) {
				inoltro = Inoltro.CON_DUPLICATI;
			} else {
				inoltro = Inoltro.UNKNOWN;
			}
			
		}
		
		Object oInoltroValue = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_FILTRO_DUPLICATI).getDato();
		busta.setInoltro(inoltro, oInoltroValue != null ? (String)oInoltroValue : null);

		Object oSequenza = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_SEQUENZA).getDato();
		if(oSequenza != null) {
			busta.setSequenza(Long.valueOf((String)oSequenza));
		}
		
		busta.setServizioApplicativoFruitore(transazione.getServizioApplicativoFruitore());
		busta.setServizioApplicativoErogatore(transazione.getServizioApplicativoErogatore());
		
		busta.setProtocollo(transazione.getProtocollo());

		busta.setDigest(richiesta ? transazione.getDigestRichiesta() : transazione.getDigestRisposta());
		
		String properties = richiesta ? transazione.getProtocolloExtInfoRichiesta() : transazione.getProtocolloExtInfoRisposta();
		Map<String, List<String>> bustaProperties = PropertiesSerializator.convertoFromDBColumnValue(properties);
		if(bustaProperties!=null && bustaProperties.size()>0){
			for (Map.Entry<String,List<String>> entry : bustaProperties.entrySet()) {
				String key = entry.getKey();
				List<String> values = bustaProperties.get(key);
				if(values!=null && !values.isEmpty()) {
					for (String value : values) {
						busta.addProperty(key, value);	
					}
				}
			}
		}

		Object oRiscontroOraRegistrazione = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_RISCONTRO_ORA_REGISTRAZIONE).getDato();
		Object oRiscontroTipoOraRegistrazione = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE).getDato();
		Object oRiscontroTipoOraRegistrazioneCode = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE_CODE).getDato();

		if(oRiscontroOraRegistrazione != null && oRiscontroTipoOraRegistrazione != null && oRiscontroTipoOraRegistrazioneCode != null) {
			
			Riscontro riscontro = new Riscontro();
			riscontro.setID(transazione.getIdMessaggioRichiesta());
			riscontro.setOraRegistrazione((Date)oRiscontroOraRegistrazione);
			
			char value = ((Character)oRiscontroTipoOraRegistrazioneCode);
			if(CostantiMappingTracciamento.TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE_CODE_LOCALE == value) {
				riscontro.setTipoOraRegistrazione(TipoOraRegistrazione.LOCALE);
			} else if(CostantiMappingTracciamento.TRACCIA_BUSTA_RISCONTRO_TIPO_ORA_REGISTRAZIONE_CODE_SINCRONIZZATO == value) {
				riscontro.setTipoOraRegistrazione(TipoOraRegistrazione.SINCRONIZZATO);
			} else {
				riscontro.setTipoOraRegistrazione(TipoOraRegistrazione.UNKNOWN);
			}
			
			riscontro.setTipoOraRegistrazioneValue((String)oRiscontroTipoOraRegistrazione);
			
			busta.addRiscontro(riscontro);
		}
		
		if(!applicativoToken) {
		
			Object oPrimaTrasmissioneOraRegistrazione = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_ORA_REGISTRAZIONE).getDato();
			Object oPrimaTrasmissioneTipoOraRegistrazione = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE).getDato();
			Object oPrimaTrasmissioneTipoOraRegistrazioneCode = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE).getDato();
	
			if(oPrimaTrasmissioneOraRegistrazione != null && oPrimaTrasmissioneTipoOraRegistrazione != null && oPrimaTrasmissioneTipoOraRegistrazioneCode != null) {
				
				Trasmissione trasmissione = new Trasmissione();
				trasmissione.setOraRegistrazione((Date)oPrimaTrasmissioneOraRegistrazione);
				
				boolean trasmissioneNormale = true;
				char value = ((Character)oPrimaTrasmissioneTipoOraRegistrazioneCode);
				if(CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_NORMALE_TIPO_ORA_REGISTRAZIONE_CODE_LOCALE == value) {
					trasmissione.setTempo(TipoOraRegistrazione.LOCALE);
				} else if(CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_NORMALE_TIPO_ORA_REGISTRAZIONE_CODE_SINCRONIZZATO == value) {
					trasmissione.setTempo(TipoOraRegistrazione.SINCRONIZZATO);
				} else if(CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_NORMALE_TIPO_ORA_REGISTRAZIONE_CODE_SCONOSCIUTO == value) {
					trasmissione.setTempo(TipoOraRegistrazione.UNKNOWN);
				} else if(CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_INVERTITA_TIPO_ORA_REGISTRAZIONE_CODE_LOCALE == value) {
					trasmissione.setTempo(TipoOraRegistrazione.LOCALE);
					trasmissioneNormale = false;
				} else if(CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_INVERTITA_TIPO_ORA_REGISTRAZIONE_CODE_SINCRONIZZATO == value) {
					trasmissione.setTempo(TipoOraRegistrazione.SINCRONIZZATO);
					trasmissioneNormale = false;
				} else if(CostantiMappingTracciamento.TRACCIA_BUSTA_PRIMA_TRASMISSIONE_INVERTITA_TIPO_ORA_REGISTRAZIONE_CODE_SCONOSCIUTO == value) {
					trasmissione.setTempo(TipoOraRegistrazione.UNKNOWN);
					trasmissioneNormale = false;
				} else {
					trasmissione.setTempo(TipoOraRegistrazione.UNKNOWN);
				}
				
				trasmissione.setTempoValue((String)oPrimaTrasmissioneTipoOraRegistrazione);
				
				if(trasmissioneNormale){
					trasmissione.setOrigine(mittente.getNome());
					trasmissione.setTipoOrigine(mittente.getTipo());
					trasmissione.setIdentificativoPortaOrigine(mittente.getCodicePorta());
					trasmissione.setIndirizzoOrigine(busta.getIndirizzoMittente());
					
					trasmissione.setDestinazione(destinatario.getNome());
					trasmissione.setTipoDestinazione(destinatario.getTipo());
					trasmissione.setIdentificativoPortaDestinazione(destinatario.getCodicePorta());
					trasmissione.setIndirizzoDestinazione(busta.getIndirizzoDestinatario());
				}
				else{
					trasmissione.setOrigine(destinatario.getNome());
					trasmissione.setTipoOrigine(destinatario.getTipo());
					trasmissione.setIdentificativoPortaOrigine(destinatario.getCodicePorta());
					trasmissione.setIndirizzoOrigine(busta.getIndirizzoDestinatario());
					
					trasmissione.setDestinazione(mittente.getNome());
					trasmissione.setTipoDestinazione(mittente.getTipo());
					trasmissione.setIdentificativoPortaDestinazione(mittente.getCodicePorta());
					trasmissione.setIndirizzoDestinazione(busta.getIndirizzoMittente());
				}
				
				busta.addTrasmissione(trasmissione);
				
			}		
			
			Object oSecondaTrasmissioneOraRegistrazione = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_ORA_REGISTRAZIONE).getDato();
			Object oSecondaTrasmissioneTipoOraRegistrazione = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE).getDato();
			Object oSecondaTrasmissioneTipoOraRegistrazioneCode = infoSalvataggioTraccia.getDato(MappingRicostruzioneTraccia.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE).getDato();
	
			if(oSecondaTrasmissioneOraRegistrazione != null && oSecondaTrasmissioneTipoOraRegistrazione != null && oSecondaTrasmissioneTipoOraRegistrazioneCode != null) {
				
				Trasmissione trasmissione = new Trasmissione();
				trasmissione.setOraRegistrazione((Date)oSecondaTrasmissioneOraRegistrazione);
				
				char value = ((Character)oSecondaTrasmissioneTipoOraRegistrazioneCode);
				if(CostantiMappingTracciamento.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE_LOCALE == value) {
					trasmissione.setTempo(TipoOraRegistrazione.LOCALE);
				} else if(CostantiMappingTracciamento.TRACCIA_BUSTA_SECONDA_TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_CODE_SINCRONIZZATO == value) {
					trasmissione.setTempo(TipoOraRegistrazione.SINCRONIZZATO);
				} else {
					trasmissione.setTempo(TipoOraRegistrazione.UNKNOWN);
				}
				
				trasmissione.setTempoValue((String)oSecondaTrasmissioneTipoOraRegistrazione);
				
				trasmissione.setOrigine(destinatario.getNome());
				trasmissione.setTipoOrigine(destinatario.getTipo());
				trasmissione.setIdentificativoPortaOrigine(destinatario.getCodicePorta());
				trasmissione.setIndirizzoOrigine(busta.getIndirizzoDestinatario());
				
				trasmissione.setDestinazione(mittente.getNome());
				trasmissione.setTipoDestinazione(mittente.getTipo());
				trasmissione.setIdentificativoPortaDestinazione(mittente.getCodicePorta());
				trasmissione.setIndirizzoDestinazione(busta.getIndirizzoMittente());
				
				busta.addTrasmissione(trasmissione);
				
			}		
		}
		
		
		return busta;
		
	}
	
	
	private InformazioniRecordTraccia getInformazioniSalvataggioTraccia(
			JDBCServiceManager transazioniServiceManager,
			String idTransazione, RuoloMessaggio tipoTraccia,
			CredenzialiMittente credentialsFiller,
			Date minDate,
			Date maxDate) throws DriverTracciamentoException{

		try{
			
			ITransazioneServiceSearch transazioneServiceSearch = transazioniServiceManager.getTransazioneServiceSearch();
			
			IPaginatedExpression pagExpr = transazioneServiceSearch.newPaginatedExpression();
			if(maxDate!=null){
				pagExpr.lessEquals(Transazione.model().DATA_INGRESSO_RICHIESTA, maxDate); // ottimizzazione partizioni
			}
			if(minDate!=null){
				pagExpr.greaterEquals(Transazione.model().DATA_INGRESSO_RICHIESTA, minDate); // ottimizzazione partizioni
			}
			pagExpr.equals(Transazione.model().ID_TRANSAZIONE,idTransazione);
			
			List<Map<String,Object>> selectField = null;
			if(RuoloMessaggio.RICHIESTA.equals(tipoTraccia)){
				selectField = transazioneServiceSearch.select(pagExpr, Transazione.model().TRACCIA_RICHIESTA,
						Transazione.model().TOKEN_CLIENT_ID);
			}else{
				selectField = transazioneServiceSearch.select(pagExpr, Transazione.model().TRACCIA_RISPOSTA,
						Transazione.model().TOKEN_CLIENT_ID);
			}
			
			if(selectField==null || selectField.isEmpty()){
				throw new DriverTracciamentoException("Recupero informazioni per ricostruire la traccia [isRichiesta:"+
						tipoTraccia+"] non riuscito. Transazione con ID["+
						idTransazione+"] non presente?");
			}
			if(selectField.size()>1){
				throw new DriverTracciamentoException("Recupero informazioni per ricostruire la traccia [isRichiesta:"+
						tipoTraccia+"] non riuscito. Trovata piu' di una Transazione con ID["+
						idTransazione+"]?");
			}
			
			Map<String,Object> dbValue = selectField.get(0);
			
			String dbValueTraccia =null;
			if(RuoloMessaggio.RICHIESTA.equals(tipoTraccia)){
				dbValueTraccia = this.readValue(dbValue, Transazione.model().TRACCIA_RICHIESTA, idTransazione);
			}
			else {
				dbValueTraccia = this.readValue(dbValue, Transazione.model().TRACCIA_RISPOSTA, idTransazione);
			}
			if(dbValueTraccia==null){
				return null;
			}
			
			InformazioniRecordTraccia info = InformazioniRecordTraccia.convertoFromDBColumnValue(dbValueTraccia);
			this.logDebug("RECUPERO ["+info.toString()+"]");
			
			
			if(credentialsFiller!=null) {
				String dbValueTokenClientId = this.readValue(dbValue, Transazione.model().TOKEN_CLIENT_ID, idTransazione);
				if(dbValueTokenClientId!=null && StringUtils.isNotEmpty(dbValueTokenClientId)) {
					CredenzialeMittente credenzialeClientId = getCredenzialeMittente(transazioniServiceManager, dbValueTokenClientId, idTransazione);
					credentialsFiller.setTokenClientId(credenzialeClientId);
				}
			}
			
			
			return info;
				
		}catch(Exception e){
			throw new DriverTracciamentoException("Riscontrato errore durante la lettura dei dati (Transazione:"+idTransazione+"): "+e.getMessage(),e);
		}
	}
	
	private CredenzialeMittente getCredenzialeMittente(JDBCServiceManager transazioniServiceManager, String dbValueTokenClientId, String idTransazione) throws ServiceException, NotImplementedException, MultipleResultException{
		CredenzialeMittente credenzialeClientId = null;
		long id = -1;
		try {
			IDBCredenzialeMittenteServiceSearch credenzialeMittenteServiceSearch = (IDBCredenzialeMittenteServiceSearch) transazioniServiceManager.getCredenzialeMittenteServiceSearch();
			id = Long.parseLong(dbValueTokenClientId);
			credenzialeClientId = credenzialeMittenteServiceSearch.get(id);
			this.logDebug("RECUPERATO CLIENT ID ["+credenzialeClientId+"] per id ["+idTransazione+"]");
		}catch(NotFoundException notFound) {
			credenzialeClientId = new CredenzialeMittente();
			credenzialeClientId.setId(id);
			credenzialeClientId.setCredenziale("Informazione non disponibile");
		}
		return credenzialeClientId;
	}
	
	private String readValue(Map<String,Object> dbValue, IField field, String idTransazione) throws DriverTracciamentoException{
		Object objectDBValue = dbValue.get(field.getFieldName());
		String dbValueAsString = null;
		if((objectDBValue instanceof org.apache.commons.lang.ObjectUtils.Null)){
			objectDBValue = null;
		}
		if(objectDBValue!=null){
			if( ! (objectDBValue instanceof String)){
				throw new DriverTracciamentoException("Recupero informazioni per ricostruire la traccia non riuscito. Trovato campo '"+field.getFieldName()
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
