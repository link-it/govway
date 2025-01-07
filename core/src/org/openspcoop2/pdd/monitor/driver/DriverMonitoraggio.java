/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.monitor.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.mdb.InoltroBuste;
import org.openspcoop2.pdd.mdb.InoltroRisposte;
import org.openspcoop2.pdd.monitor.Busta;
import org.openspcoop2.pdd.monitor.BustaServizio;
import org.openspcoop2.pdd.monitor.BustaSoggetto;
import org.openspcoop2.pdd.monitor.Dettaglio;
import org.openspcoop2.pdd.monitor.Messaggio;
import org.openspcoop2.pdd.monitor.Proprieta;
import org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna;
import org.openspcoop2.pdd.monitor.StatoPdd;
import org.openspcoop2.pdd.monitor.constants.CostantiMonitoraggio;
import org.openspcoop2.pdd.monitor.constants.StatoMessaggio;
import org.openspcoop2.pdd.timers.TimerConsegnaContenutiApplicativiThread;
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.slf4j.Logger;

/**
 * Driver per il Monitoraggio di GovWay
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class DriverMonitoraggio implements IDriverMonitoraggio{

	/** 
	 * DataSource
	 */
	private DataSource datasource = null;

	// Connection passata al momento della creazione dell'oggetto
	private Connection globalConnection = null;
	/**
	 * SQLQueryObject
	 */
	private String tipoDatabase = null;


	/** Logger utilizzato per info. */
	private Logger log = null;
	private void logError(String msg, Exception e) {
		if(this.log!=null) {
			this.log.error(msg, e);
		}
	}
	private void logDebug(String msg) {
		if(this.log!=null) {
			this.log.debug(msg);
		}
	}

	/**
	 * Properties
	 */
	private List<String> properties;
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public DriverMonitoraggio(String nomeDataSource, String tipoDatabase, Properties prop) throws DriverMonitoraggioException {
		this(nomeDataSource,tipoDatabase,prop,null);
	}

	public DriverMonitoraggio(String nomeDataSource, String tipoDatabase, Properties prop,Logger log) throws DriverMonitoraggioException {
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(CostantiMonitoraggio.MONITORAGGIO_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverMonitoraggioException("Errore durante l'inizializzazione del logger...",e);
		}

		// Datasource
		try {
			this.log.info("Inizializzo DriverMonitoraggioDB...");
			InitialContext initCtx = new InitialContext(prop);
			this.datasource = (DataSource) initCtx.lookup(nomeDataSource);
			if (this.datasource == null)
				throw new DriverMonitoraggioException ("datasource is null");

			initCtx.close();
			this.log.info("Inizializzo DriverMonitoraggioDB terminata.");
		} catch (Exception e) {
			this.logError("Errore durante la ricerca del datasource...",e);
			throw new DriverMonitoraggioException("Errore durante la ricerca del datasource...",e);
		}

		// ISQLQueryObject SQLObjectFactory
		try {
			this.log.info("Inizializzo ISQLQueryObject ...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new DriverMonitoraggioException("Tipo database non gestito");
			}

			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.logError("Errore durante la ricerca del SQLQueryObject...",e);
			throw new DriverMonitoraggioException("Errore durante la ricerca del SQLQueryObject("+tipoDatabase+")...",e);
		}
	}



	public DriverMonitoraggio(String connectionUrl, String driverJDBC,
			String username, String password, String tipoDatabase)
					throws DriverMonitoraggioException {
		this(connectionUrl, driverJDBC, username, password, tipoDatabase, null);
	}

	public DriverMonitoraggio(String connectionUrl, String driverJDBC,
			String username, String password, String tipoDatabase,Logger log)
					throws DriverMonitoraggioException {
		// Logger
		try {
			if(log==null)
				this.log = LoggerWrapperFactory.getLogger(CostantiMonitoraggio.MONITORAGGIO_DRIVER_DB_LOGGER);
			else
				this.log = log;
		} catch (Exception e) {
			throw new DriverMonitoraggioException("Errore durante l'inizializzazione del logger...",e);
		}

		// Connection
		try{
			this.log.info("Inizializzo DriverMonitoraggioDB...");
			ClassLoaderUtilities.newInstance(driverJDBC);
			this.globalConnection = initConnection(connectionUrl, username, password);
			checkConnection(this.globalConnection);
			this.log.info("Inizializzo DriverMonitoraggioDB terminata.");
		} catch (Exception e) {
			this.logError("Errore durante l'inizializzazione della connessione...",e);
			throw new DriverMonitoraggioException("Errore durante l'inizializzazione della connessione...",e);
		}

		// ISQLQueryObject SQLObjectFactory
		try {
			this.log.info("Inizializzo ISQLQueryObject ...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new DriverMonitoraggioException("Tipo database non gestito");
			}

			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.logError("Errore durante la ricerca del SQLQueryObject...",e);
			throw new DriverMonitoraggioException("Errore durante la ricerca del SQLQueryObject("+tipoDatabase+")...",e);
		}
	}


	private static Connection initConnection(String connectionUrl, String username, String password) throws SQLException {
		if(username!=null){
			return DriverManager.getConnection(connectionUrl,username,password);
		}else{
			return DriverManager.getConnection(connectionUrl);
		}
	}
	private static void checkConnection(Connection con) throws DriverMonitoraggioException {
		if(con == null){
			throw new DriverMonitoraggioException("Connection is null");
		}
	}
	
	private Connection getConnection() throws SQLException {
		if (this.datasource!=null)
			return this.datasource.getConnection();
		else
			return this.globalConnection;
	}
	private void releaseConnection(Connection con) {
		try{
			if(this.datasource!=null && con!=null) {
				JDBCUtilities.closeConnection(BasicComponentFactory.getCheckLogger(), con, BasicComponentFactory.isCheckAutocommit(), BasicComponentFactory.isCheckIsClosed());
			}
		}catch(Exception e){
			// close
		}
	}

	/**
	 * Ritorna lo stato delle richieste pendenti che matchano il criterio di filtro.
	 * 
	 * @param search criterio di filtro
	 * @return stato delle richieste pendenti
	 * @throws DriverMonitoraggioException
	 */
	@Override
	public StatoPdd getStatoRichiestePendenti(FilterSearch search) throws DriverMonitoraggioException{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ISQLQueryObject sqlQueryObject = null;
		try{

			// Ottengo connessione
			con = getConnection();
			if(con==null)
				throw new DriverMonitoraggioException("Connection non ottenuta dal datasource["+this.datasource+"]");

			StatoPdd statoPdD = new StatoPdd();
			java.sql.Timestamp now = DateManager.getTimestamp();
			java.sql.Timestamp dataRegistrazioneLimite = null;
			//	FILTRO d) Soglia (ora_registrazione piu' vecchia di NOW-search.getSoglia()) Soglia in Minuti
			if(search.getSoglia()!=-1){
				dataRegistrazioneLimite = new java.sql.Timestamp(DateManager.getTimeMillis()-(search.getSoglia()*1000*60));
			}


			// TotaleMessaggi
			long totaleMessaggi = 0;
			long totaleMessaggiDuplicati = -1;
			sqlQueryObject = this.newSQLQueryObjectStatoRichiestePendenti(search);
			pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			int paramIndex = 0;
			if(search.getSoglia()!=-1)
				pstmt.setTimestamp(++paramIndex, dataRegistrazioneLimite);
			List<BustaSoggetto> filtroSoggetti = search.getSoggettoList();
			if(filtroSoggetti!=null){			
				for(int k=0; k<filtroSoggetti.size(); k++){

					// fruitore
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());

					// OR

					// erogatore
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());
				}
			}
			rs = pstmt.executeQuery();
			if(rs.next()){
				totaleMessaggi = rs.getLong("totMessaggi");
				if(totaleMessaggi>0){
					statoPdD.setTotMessaggi(totaleMessaggi);
					statoPdD.setTempoMedioAttesa((now.getTime()-rs.getLong("ora_registrazione_media"))/1000);
					statoPdD.setTempoMaxAttesa((now.getTime()-rs.getLong("ora_registrazione_old"))/1000);
				}
			}
			rs.close();
			pstmt.close();
			this.logDebug("query["+sqlQueryObject+"] totaleMessaggi:"+totaleMessaggi);
			this.logDebug("Soglia ["+search.getSoglia()+"] ["+dataRegistrazioneLimite+"] totaleMessaggi");

			if(totaleMessaggi>0){

				// Messaggi in Consegna
				if( (search.getStato()==null) || ("".equals(search.getStato().getValue())) || (StatoMessaggio.CONSEGNA.equals(search.getStato())) ){
					sqlQueryObject = this.newSQLQueryObjectStatoRichiestePendenti(search);
					if((search.getStato()==null) || ("".equals(search.getStato().getValue()))){
						sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".PROPRIETARIO='"+ConsegnaContenutiApplicativi.ID_MODULO+"'");
					}
					pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
					paramIndex = 0;
					if(search.getSoglia()!=-1)
						pstmt.setTimestamp(++paramIndex, dataRegistrazioneLimite);
					if(filtroSoggetti!=null){
						for(int k=0; k<filtroSoggetti.size(); k++){

							// fruitore
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());

							// OR

							// erogatore
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());
						}
					}
					rs = pstmt.executeQuery();
					if(rs.next()){
						long tot = rs.getLong("totMessaggi");
						if(tot>0){
							statoPdD.setNumMsgInConsegna(tot);
							statoPdD.setTempoMedioAttesaInConsegna((now.getTime()-rs.getLong("ora_registrazione_media"))/1000);
							statoPdD.setTempoMaxAttesaInConsegna((now.getTime()-rs.getLong("ora_registrazione_old"))/1000);
						}
					}
					rs.close();
					pstmt.close();
					this.logDebug("query["+sqlQueryObject+"] totaleMessaggiInConsegna:"+statoPdD.getNumMsgInConsegna());
					this.logDebug("Soglia ["+search.getSoglia()+"] ["+dataRegistrazioneLimite+"] totaleMessaggiInConsegna");
				}

				// Messaggi in Spedizione
				if( (search.getStato()==null) || ("".equals(search.getStato().getValue())) || (StatoMessaggio.SPEDIZIONE.equals(search.getStato())) ){
					sqlQueryObject = this.newSQLQueryObjectStatoRichiestePendenti(search);
					if( (search.getStato()==null) || ("".equals(search.getStato().getValue())) ){
						sqlQueryObject.addWhereCondition(false,GestoreMessaggi.MESSAGGI+".PROPRIETARIO='InoltroBuste'",
								GestoreMessaggi.MESSAGGI+".PROPRIETARIO='InoltroRisposte'");
					}
					pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
					paramIndex = 0;
					if(search.getSoglia()!=-1)
						pstmt.setTimestamp(++paramIndex, dataRegistrazioneLimite);
					if(filtroSoggetti!=null){
						for(int k=0; k<filtroSoggetti.size(); k++){

							// fruitore
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());

							// OR

							// erogatore
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());
						}
					}
					rs = pstmt.executeQuery();
					if(rs.next()){
						long tot = rs.getLong("totMessaggi");
						if(tot>0){
							statoPdD.setNumMsgInSpedizione(tot);
							statoPdD.setTempoMedioAttesaInSpedizione((now.getTime()-rs.getLong("ora_registrazione_media"))/1000);
							statoPdD.setTempoMaxAttesaInSpedizione((now.getTime()-rs.getLong("ora_registrazione_old"))/1000);
						}
					}
					rs.close();
					pstmt.close();
					this.logDebug("query["+sqlQueryObject+"] totaleMessaggiInSpedizione:"+statoPdD.getNumMsgInSpedizione());
					this.logDebug("Soglia ["+search.getSoglia()+"] ["+dataRegistrazioneLimite+"] totaleMessaggiInSpedizione");
				}

				// Messaggi ne in Spedizione, ne in Consegna (PROCESSAMENTO) 
				if( (search.getStato()==null) || ("".equals(search.getStato().getValue())) || (StatoMessaggio.PROCESSAMENTO.equals(search.getStato())) ){
					sqlQueryObject = this.newSQLQueryObjectStatoRichiestePendenti(search);
					if( (search.getStato()==null) || ("".equals(search.getStato().getValue())) ){
						sqlQueryObject.addWhereCondition(true, GestoreMessaggi.MESSAGGI+".PROPRIETARIO<>'InoltroBuste'",
								GestoreMessaggi.MESSAGGI+".PROPRIETARIO<>'InoltroRisposte'",
								GestoreMessaggi.MESSAGGI+".PROPRIETARIO<>'"+ConsegnaContenutiApplicativi.ID_MODULO+"'");
					}
					pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
					paramIndex = 0;
					if(search.getSoglia()!=-1)
						pstmt.setTimestamp(++paramIndex, dataRegistrazioneLimite);
					if(filtroSoggetti!=null){
						for(int k=0; k<filtroSoggetti.size(); k++){

							// fruitore
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());

							// OR

							// erogatore
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());
						}
					}
					rs = pstmt.executeQuery();
					if(rs.next()){
						long tot = rs.getLong("totMessaggi");
						if(tot>0){
							statoPdD.setNumMsgInProcessamento(tot);
							statoPdD.setTempoMedioAttesaInProcessamento((now.getTime()-rs.getLong("ora_registrazione_media"))/1000);
							statoPdD.setTempoMaxAttesaInProcessamento((now.getTime()-rs.getLong("ora_registrazione_old"))/1000);
						}
					}
					rs.close();
					pstmt.close();
					this.logDebug("query["+sqlQueryObject+"] totaleMessaggiInProcessamento:"+statoPdD.getNumMsgInProcessamento());
					this.logDebug("Soglia ["+search.getSoglia()+"] ["+dataRegistrazioneLimite+"] totaleMessaggiInProcessamento");
				}

			}

			// MessaggiDuplicati
			this.logDebug("Calcolo numero pacchetti duplicati...");
			sqlQueryObject = this.newSQLQueryPacchettiDuplicati(search);
			pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			if(search.getSoglia()!=-1)
				pstmt.setTimestamp(1, dataRegistrazioneLimite);
			rs = pstmt.executeQuery();
			if(rs.next()){
				totaleMessaggiDuplicati = rs.getLong("numduplicati");
				statoPdD.setTotMessaggiDuplicati(totaleMessaggiDuplicati);
			}
			rs.close();
			pstmt.close();
			this.logDebug("query["+sqlQueryObject.toString()+"] totaleMessaggiDuplicati:"+totaleMessaggiDuplicati);
			this.logDebug("Soglia ["+search.getSoglia()+"] ["+dataRegistrazioneLimite+"] totaleMessaggiDuplicati");


			return statoPdD;

		}catch(Exception e){
			if(sqlQueryObject!=null){
				this.logError("getStatoRichiestePendenti error SQL["+sqlQueryObject.toString()+"]",e);
				throw new DriverMonitoraggioException("getStatoRichiestePendenti error SQL["+sqlQueryObject.toString()+"]: "+e.getMessage(),e);
			}else{
				this.logError("getStatoRichiestePendenti error",e);
				throw new DriverMonitoraggioException("getStatoRichiestePendenti error: "+e.getMessage(),e);
			}

		}finally{
			JDBCUtilities.closeResources(rs, pstmt);
			releaseConnection(con);
		}
	}

	/**
	 * Ritorna semplicemente il numero di messaggi in gestione
	 * 
	 * @return il numero di messaggi in gestione
	 * @throws DriverMonitoraggioException
	 */
	public long getTotaleMessaggiInGestione() throws DriverMonitoraggioException{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{

			// Ottengo connessione
			con = getConnection();
			if(con==null)
				throw new DriverMonitoraggioException("Connection non ottenuta dal datasource["+this.datasource+"]");

			// TotaleMessaggi
			long totaleMessaggi = 0;
			String query = "SELECT count(*) as totMessaggi FROM "+GestoreMessaggi.MESSAGGI+" WHERE "+GestoreMessaggi.MESSAGGI+".PROPRIETARIO<>'"+TimerGestoreMessaggi.ID_MODULO+"'";
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();
			if(rs.next()){
				totaleMessaggi = rs.getLong("totMessaggi");
			}
			rs.close();
			pstmt.close();
			this.logDebug("query["+query+"] totaleMessaggi:"+totaleMessaggi);

			return totaleMessaggi;

		}catch(Exception e){
			this.logError("getTotaleMessaggiInGestione error",e);
			throw new DriverMonitoraggioException("getStatoRichiestePendenti error: "+e.getMessage(),e);
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				// close
			}
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception e){
				// close
			}
			releaseConnection(con);
		}
	}

	/**
	 * Ritorna il numero delle richieste pendenti che matchano il criterio di filtro.
	 * 
	 * @param search criterio di filtro
	 * @return numero di richieste pendenti
	 * @throws DriverMonitoraggioException
	 */
	@Override
	public long countListaRichiestePendenti(FilterSearch search) throws DriverMonitoraggioException{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ISQLQueryObject sqlQueryObject = null;
		// Congelamento di offset e limit (per la count non deve essere utilizzato)
		long oldLimit = search.getLimit();
		long oldOffset = search.getOffset();
		try{
			// Ottengo connessione
			con = getConnection();
			if(con==null)
				throw new DriverMonitoraggioException("Connection non ottenuta dal datasource["+this.datasource+"]");

			java.sql.Timestamp dataRegistrazioneLimite = null;
			//	FILTRO d) Soglia (ora_registrazione piu' vecchia di NOW-search.getSoglia()) Soglia in Minuti
			if(search.getSoglia()!=-1){
				dataRegistrazioneLimite = new java.sql.Timestamp(DateManager.getTimeMillis()-(search.getSoglia()*1000*60));
			}

			// TotaleMessaggi: Il totale dei messaggi non deve subire influenze di offset/limit
			long totaleMessaggi = 0;
			search.setLimit(-1);
			search.setOffset(-1);
			sqlQueryObject = this.newSQLQueryObjectStatoRichiestePendenti(search);
			pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			int paramIndex = 0;
			if(search.getSoglia()!=-1)
				pstmt.setTimestamp(++paramIndex, dataRegistrazioneLimite);
			List<BustaSoggetto> filtroSoggetti = search.getSoggettoList();
			if(filtroSoggetti!=null){
				for(int k=0; k<filtroSoggetti.size(); k++){

					// fruitore
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());

					// OR

					// erogatore
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());
				}
			}
			rs = pstmt.executeQuery();
			if(rs.next()){
				totaleMessaggi = rs.getLong("totMessaggi");
			}
			rs.close();
			pstmt.close();
			this.logDebug("query["+sqlQueryObject+"] totaleMessaggi:"+totaleMessaggi);
			this.logDebug("Soglia ["+search.getSoglia()+"] ["+dataRegistrazioneLimite+"] totaleMessaggi");

			return totaleMessaggi;

		}catch(Exception e){
			if(sqlQueryObject!=null){
				this.logError("getListaRichiestePendenti error SQL["+sqlQueryObject.toString()+"]",e);
				throw new DriverMonitoraggioException("getListaRichiestePendenti error SQL["+sqlQueryObject.toString()+"]: "+e.getMessage(),e);
			}else{
				this.logError("getListaRichiestePendenti error",e);
				throw new DriverMonitoraggioException("getListaRichiestePendenti error: "+e.getMessage(),e);
			}
		}finally{
			// ripristino valori offset/limit
			search.setLimit(oldLimit);
			search.setOffset(oldOffset);
			JDBCUtilities.closeResources(rs, pstmt);
			releaseConnection(con);
		}
	}

	/**
	 * Ritorna I dettagli dei messaggi delle richieste pendenti che matchano il criterio di filtro.
	 * 
	 * @param search criterio di filtro
	 * @return dettagli dei messaggi delle richieste pendenti
	 * @throws DriverMonitoraggioException
	 */
	@Override
	public List<Messaggio> getListaRichiestePendenti(FilterSearch search) throws DriverMonitoraggioException{
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ISQLQueryObject sqlQueryObject = null;
		try{
			// Ottengo connessione
			con = getConnection();
			if(con==null)
				throw new DriverMonitoraggioException("Connection non ottenuta dal datasource["+this.datasource+"]");

			java.sql.Timestamp dataRegistrazioneLimite = null;
			//	FILTRO d) Soglia (ora_registrazione piu' vecchia di NOW-search.getSoglia()) Soglia in Minuti
			if(search.getSoglia()!=-1){
				dataRegistrazioneLimite = new java.sql.Timestamp(DateManager.getTimeMillis()-(search.getSoglia()*1000*60));
			}

			List<BustaSoggetto> filtroSoggetti = search.getSoggettoList();

			// Query
			sqlQueryObject = this.newSQLQueryObjectListaRichiestePendenti(search);
			pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			int paramIndex = 0;
			if(search.getSoglia()!=-1)
				pstmt.setTimestamp(++paramIndex, dataRegistrazioneLimite);
			if(filtroSoggetti!=null){
				for(int k=0; k<filtroSoggetti.size(); k++){

					// fruitore
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());

					// OR

					// erogatore
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());
				}
			}
			rs = pstmt.executeQuery();
			List<Messaggio> msgs = new ArrayList<>();

			this.logDebug("query["+sqlQueryObject+"] listaMessaggi");
			this.logDebug("Soglia ["+search.getSoglia()+"] ["+dataRegistrazioneLimite+"] listaMessaggi");
			
			while(rs.next()){
				Messaggio m = new Messaggio();
				// id
				m.setIdMessaggio(rs.getString("id_messaggio"));
				// Dettaglio
				Dettaglio dettaglio = new Dettaglio();
				dettaglio.setIdModulo(rs.getString("PROPRIETARIO"));
				dettaglio.setTipo(rs.getString("tipo"));
				dettaglio.setErroreProcessamento(rs.getString("ERRORE_PROCESSAMENTO"));
				dettaglio.setIdCorrelazioneApplicativa(rs.getString("CORRELAZIONE_APPLICATIVA"));
				if(this.properties!=null){
					for (int i = 0; i < this.properties.size(); i++) {
						String key = this.properties.get(i);
						Proprieta proprieta = new Proprieta();
						proprieta.setNome(key);
						proprieta.setValore(rs.getString(key));
						dettaglio.addProprieta(proprieta);
					}
				}
				m.setDettaglio(dettaglio);
				// Ora Registrazione
				if(rs.getTimestamp("ORA_REGISTRAZIONE")!=null){
					m.setOraRegistrazione(rs.getTimestamp("ORA_REGISTRAZIONE"));
				}
				// Ora Attuale
				m.setOraAttuale(DateManager.getDate());
				// Stato
				if(ConsegnaContenutiApplicativi.ID_MODULO.equals(rs.getString("PROPRIETARIO"))){
					m.setStato(StatoMessaggio.CONSEGNA);
				}else if(InoltroBuste.ID_MODULO.equals(rs.getString("PROPRIETARIO"))){
					m.setStato(StatoMessaggio.SPEDIZIONE);
				}else if(InoltroRisposte.ID_MODULO.equals(rs.getString("PROPRIETARIO"))){
					m.setStato(StatoMessaggio.SPEDIZIONE);
				}else {
					m.setStato(StatoMessaggio.PROCESSAMENTO);
				}

				// Info
				if(search.getBusta()!=null){
					Busta bustaInfo = new Busta();
					// Mittente
					BustaSoggetto mittente = new BustaSoggetto();
					mittente.setNome(rs.getString("MITTENTE"));
					mittente.setTipo(rs.getString("TIPO_MITTENTE"));
					bustaInfo.setMittente(mittente);
					// Destinatario
					BustaSoggetto destinatario = new BustaSoggetto();
					destinatario.setNome(rs.getString("DESTINATARIO"));
					destinatario.setTipo(rs.getString("TIPO_DESTINATARIO"));
					bustaInfo.setDestinatario(destinatario);
					// Servizio
					BustaServizio servizio = new BustaServizio();
					servizio.setTipo(rs.getString("TIPO_SERVIZIO"));
					servizio.setNome(rs.getString("SERVIZIO"));
					try {
						servizio.setVersione(Integer.valueOf(rs.getString("VERSIONE_SERVIZIO")));
					}catch(Exception e) {
						// ignore
					}
					bustaInfo.setServizio(servizio);
					// Azione
					bustaInfo.setAzione(rs.getString("AZIONE"));
					// Profilo di Collaborazione
					bustaInfo.setProfiloCollaborazione(rs.getString("PROFILO_DI_COLLABORAZIONE"));
					// RiferimentoMessaggio
					bustaInfo.setRiferimentoMessaggio(rs.getString("RIFERIMENTO_MESSAGGIO"));
					//	Collaborazione
					bustaInfo.setCollaborazione(rs.getString("COLLABORAZIONE"));
					m.setBustaInfo(bustaInfo);
				}
				msgs.add(m);
			}
			rs.close();
			pstmt.close();

			for(int i=0; i<msgs.size(); i++){

				//	Prendo Dettaglio servizio applicativo per messaggi in consegna
				boolean checkSA = false;
				if(msgs.get(i)!=null && 
						StatoMessaggio.CONSEGNA.equals(msgs.get(i).getStato()) &&
						msgs.get(i).getDettaglio()!=null){
					checkSA = true;
				}else if(msgs.get(i)!=null &&  msgs.get(i).getDettaglio()!=null && 
						msgs.get(i).getDettaglio().getIdModulo()!=null &&
						msgs.get(i).getDettaglio().getIdModulo().startsWith("RicezioneContenutiApplicativi")){
					checkSA = true;
				}					
				if(checkSA){
					this.logDebug("Raccolgo informazioni dei servizi applicativi...");
					String sqlQuerySA = "SELECT * FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI +
							" WHERE ID_MESSAGGIO=?";
					pstmt = con.prepareStatement(sqlQuerySA);
					pstmt.setString(1, msgs.get(i).getIdMessaggio());
					rs = pstmt.executeQuery();
					List<ServizioApplicativoConsegna> sconsegna = new ArrayList<>();
					while(rs.next()){
						ServizioApplicativoConsegna datiConsegna = new ServizioApplicativoConsegna();
						if(rs.getInt("SBUSTAMENTO_SOAP")==1)
							datiConsegna.setSbustamentoSoap(true);
						else
							datiConsegna.setSbustamentoSoap(false);
						if(rs.getInt("SBUSTAMENTO_INFO_PROTOCOL")==1)
							datiConsegna.setSbustamentoInformazioniProtocollo(true);
						else
							datiConsegna.setSbustamentoInformazioniProtocollo(false);
						if(rs.getInt("INTEGRATION_MANAGER")==1)
							datiConsegna.setAutorizzazioneIntegrationManager(true);
						else
							datiConsegna.setAutorizzazioneIntegrationManager(false);
						datiConsegna.setNome(rs.getString("SERVIZIO_APPLICATIVO"));
						datiConsegna.setTipoConsegna(rs.getString("TIPO_CONSEGNA"));
						datiConsegna.setErroreProcessamento(rs.getString("ERRORE_PROCESSAMENTO"));
						if(rs.getTimestamp("RISPEDIZIONE")!=null){
							datiConsegna.setDataRispedizione(rs.getTimestamp("RISPEDIZIONE"));
						}
						datiConsegna.setNomePorta(rs.getString("NOME_PORTA"));
						datiConsegna.setCoda(rs.getString("CODA"));
						datiConsegna.setPriorita(rs.getString("PRIORITA"));
						if(rs.getInt("ATTESA_ESITO")==1)
							datiConsegna.setAttesaEsito(true);
						else
							datiConsegna.setAttesaEsito(false);
						sconsegna.add(datiConsegna);
					}
					rs.close();
					pstmt.close();

					if(!sconsegna.isEmpty()){
						msgs.get(i).getDettaglio().setServizioApplicativoConsegnaList(sconsegna);
					}

				}

				if(msgs.get(i)!=null &&  msgs.get(i).getBustaInfo()!=null &&
						StatoMessaggio.SPEDIZIONE.equals(msgs.get(i).getStato())){
					// Prendo dettaglio AttesaRiscontro
					if(search.getBusta()!=null && search.getBusta().isAttesaRiscontro()){
						msgs.get(i).getBustaInfo().setAttesaRiscontro(true);
					}else{
						this.logDebug("Raccolgo informazioni per attesa riscontro...");
						String sqlQueryRiscontro = "SELECT * FROM "+Costanti.RISCONTRI_DA_RICEVERE +
								" WHERE ID_MESSAGGIO=?";
						pstmt = con.prepareStatement(sqlQueryRiscontro);
						pstmt.setString(1, msgs.get(i).getIdMessaggio());
						rs = pstmt.executeQuery();
						msgs.get(i).getBustaInfo().setAttesaRiscontro(rs.next());
						rs.close();
						pstmt.close();
					}
				}
			}


			return msgs;

		}catch(Exception e){
			if(sqlQueryObject!=null){
				this.logError("getListaRichiestePendenti error SQL["+sqlQueryObject.toString()+"]",e);
				throw new DriverMonitoraggioException("getListaRichiestePendenti error SQL["+sqlQueryObject.toString()+"]: "+e.getMessage(),e);
			}else{
				this.logError("getListaRichiestePendenti error",e);
				throw new DriverMonitoraggioException("getListaRichiestePendenti error: "+e.getMessage(),e);
			}
		}finally{
			JDBCUtilities.closeResources(rs, pstmt);
			releaseConnection(con);
		}
	}


	/**
	 * Elimina puntualmente il messaggio indicato dall'id.
	 * 
	 * @param idMessaggio messaggio da eliminare
	 * @throws DriverMonitoraggioException
	 */
	private boolean deleteMessaggio(String idMessaggio,String tipo) throws DriverMonitoraggioException{
		Connection con = null;
		PreparedStatement pstmt = null;
		try{

			// Ottengo connessione
			con = getConnection();
			if(con==null)
				throw new DriverMonitoraggioException("Connection non ottenuta dal datasource["+this.datasource+"]");

			// Raccolta valori chiave
			// TIPO
			String tipoMessaggio = tipo;
			// Ricerco tipoMessaggio se non presenti 
			if(tipoMessaggio==null){
				this.logDebug("Ricerco tipo messaggio...");
				FilterSearch filtro = new FilterSearch();
				filtro.setIdMessaggio(idMessaggio);
				List<Messaggio> lista = this.getListaRichiestePendenti(filtro);
				if(lista==null || lista.isEmpty()){
					return false; // messaggio non presente
				}
				if(lista.get(0).getDettaglio()==null ||
						lista.get(0).getDettaglio().getTipo()==null){
					throw new DriverMonitoraggioException("Tipo messaggio non identificato per l'id: "+idMessaggio);
				}
				tipoMessaggio = lista.get(0).getDettaglio().getTipo();
			}
			this.logDebug("Messaggio con id("+idMessaggio+") da eliminare possiede tipo: "+tipoMessaggio);

			if(Costanti.OUTBOX.equals(tipoMessaggio)){
				// Elimino eventuali riscontri
				String sqlQuery = "DELETE FROM "+Costanti.RISCONTRI_DA_RICEVERE + " WHERE ID_MESSAGGIO=?";
				pstmt = con.prepareStatement(sqlQuery);
				pstmt.setString(1, idMessaggio);
				int operation = pstmt.executeUpdate();
				if(operation>0){
					this.logDebug("Attesa riscontro per "+idMessaggio+" eliminato");
				}else{
					this.logDebug("Attesa riscontro per "+idMessaggio+" non esistente");
				}
				pstmt.close();

				// Elimino eventuali asincroni
				sqlQuery = "DELETE FROM "+Costanti.PROFILO_ASINCRONO + " WHERE ID_MESSAGGIO=? AND TIPO=?";
				pstmt = con.prepareStatement(sqlQuery);
				pstmt.setString(1, idMessaggio);
				pstmt.setString(2, tipoMessaggio);
				operation = pstmt.executeUpdate();
				if(operation>0){
					this.logDebug("ProfiloAsincrono per "+idMessaggio+" eliminato");
				}else{
					this.logDebug("ProfiloAsincrono per "+idMessaggio+" non esistente");
				}
				pstmt.close();
			}


			// Elimino messaggio
			String sqlQuery = "UPDATE "+GestoreMessaggi.MESSAGGI + " SET PROPRIETARIO='"+TimerGestoreMessaggi.ID_MODULO+"' WHERE PROPRIETARIO<>'"+TimerGestoreMessaggi.ID_MODULO+"' AND ID_MESSAGGIO=?  AND tipo=?";
			pstmt = con.prepareStatement(sqlQuery);
			pstmt.setString(1, idMessaggio);
			pstmt.setString(2, tipoMessaggio);
			int operation = pstmt.executeUpdate();
			boolean result = false;
			if(operation>0){
				result = true;
				this.logDebug("Messaggio "+idMessaggio+" eliminato");
			}else{
				this.logDebug("Messaggio "+idMessaggio+" non esistente");
			}
			pstmt.close();
			return result;

		}catch(Exception e){
			this.logError("deleteMessaggio error",e);
			throw new DriverMonitoraggioException("deleteMessaggio error: "+e.getMessage());
		}finally{
			JDBCUtilities.closeResources(pstmt);
			releaseConnection(con);
		}
	}
	
	private boolean aggiornaDataRispedizioneMessaggio(String idMessaggio,String tipo,Timestamp data) throws DriverMonitoraggioException{
		Connection con = null;
		PreparedStatement pstmt = null;
		try{

			// Ottengo connessione
			con = getConnection();
			if(con==null)
				throw new DriverMonitoraggioException("Connection non ottenuta dal datasource["+this.datasource+"]");

			// Raccolta valori chiave
			// TIPO
			String tipoMessaggio = tipo;
			// Ricerco tipoMessaggio se non presenti 
			if(tipoMessaggio==null){
				this.logDebug("Ricerco tipo messaggio...");
				FilterSearch filtro = new FilterSearch();
				filtro.setIdMessaggio(idMessaggio);
				List<Messaggio> lista = this.getListaRichiestePendenti(filtro);
				if(lista==null || lista.isEmpty()){
					return false; // messaggio non presente
				}
				if(lista.get(0).getDettaglio()==null ||
						lista.get(0).getDettaglio().getTipo()==null){
					throw new DriverMonitoraggioException("Tipo messaggio non identificato per l'id: "+idMessaggio);
				}
				tipoMessaggio = lista.get(0).getDettaglio().getTipo();
			}
			this.logDebug("Messaggio con id("+idMessaggio+") da eliminare possiede tipo: "+tipoMessaggio);

			if(Costanti.OUTBOX.equals(tipoMessaggio)){
				// Aggiorno messaggio
				String sqlQuery = "UPDATE "+GestoreMessaggi.MESSAGGI + " SET RISPEDIZIONE=? WHERE ID_MESSAGGIO=?  AND TIPO=?";
				pstmt = con.prepareStatement(sqlQuery);
				pstmt.setTimestamp(1, data);
				pstmt.setString(2, idMessaggio);
				pstmt.setString(3, tipoMessaggio);
				int operation = pstmt.executeUpdate();
				boolean result = false;
				if(operation>0){
					result = true;
					this.logDebug("Messaggio "+idMessaggio+" aggiornato");
				}else{
					this.logDebug("Messaggio "+idMessaggio+" non aggiornato");
				}
				pstmt.close();
				return result;	
			}
			else {
				// Aggiorno messaggio
				String sqlQuery = "UPDATE "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI + " SET RISPEDIZIONE=? WHERE ID_MESSAGGIO=?  AND TIPO=?";
				pstmt = con.prepareStatement(sqlQuery);
				pstmt.setTimestamp(1, data);
				pstmt.setString(2, idMessaggio);
				pstmt.setString(3, tipoMessaggio);
				int operation = pstmt.executeUpdate();
				boolean result = false;
				if(operation>0){
					result = true;
					this.logDebug("Messaggio "+idMessaggio+" aggiornato");
				}else{
					this.logDebug("Messaggio "+idMessaggio+" non aggiornato");
				}
				pstmt.close();
				return result;	
			}

		}catch(Exception e){
			this.logError("deleteMessaggio error",e);
			throw new DriverMonitoraggioException("deleteMessaggio error: "+e.getMessage());
		}finally{
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception e){
				// close
			}
			releaseConnection(con);
		}
	}



	/**
	 * Ritorna e cancella i dettagli dei messaggi delle richieste pendenti che matchano il criterio di filtro.
	 * 
	 * @param search criterio di filtro
	 * @return dettagli dei messaggi delle richieste pendenti
	 * @throws DriverMonitoraggioException
	 */
	@Override
	public long deleteRichiestePendenti(FilterSearch search) throws DriverMonitoraggioException{
		return richiestePendentiEngine(search, true);
	}
	
	/**
	 * Aggiorna la data di rispedizione delle richieste pendenti che matchano il criterio di filtro.
	 * 
	 * @param search criterio di filtro
	 * @return numero dei messaggi delle richieste pendenti
	 * @throws DriverMonitoraggioException
	 */
	@Override
	public long aggiornaDataRispedizioneRichiestePendenti(FilterSearch search) throws DriverMonitoraggioException{
		return richiestePendentiEngine(search, false);
	}
	
	private long richiestePendentiEngine(FilterSearch search, boolean delete) throws DriverMonitoraggioException{
		
		String nomeMetodo = delete? "deleteRichiestePendenti":"aggiornaDataRispedizioneRichiestePendenti";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ISQLQueryObject sqlQueryObject = null;
		try{
			// Ottengo connessione
			con = getConnection();
			if(con==null)
				throw new DriverMonitoraggioException("Connection non ottenuta dal datasource["+this.datasource+"]");

			java.sql.Timestamp dataRegistrazioneLimite = null;
			//	FILTRO d) Soglia (ora_registrazione piu' vecchia di NOW-search.getSoglia()) Soglia in Minuti
			if(search.getSoglia()!=-1){
				dataRegistrazioneLimite = new java.sql.Timestamp(DateManager.getTimeMillis()-(search.getSoglia()*1000*60));
			}
			// Query
			sqlQueryObject = this.newSQLQueryObjectListaRichiestePendenti(search);
			pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			int paramIndex = 0;
			if(search.getSoglia()!=-1)
				pstmt.setTimestamp(++paramIndex, dataRegistrazioneLimite);
			List<BustaSoggetto> filtroSoggetti = search.getSoggettoList();
			if(filtroSoggetti!=null){
				for(int k=0; k<filtroSoggetti.size(); k++){

					// fruitore
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());

					// OR

					// erogatore
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++paramIndex, filtroSoggetti.get(k).getNome());
				}
			}
			rs = pstmt.executeQuery();
			List<Messaggio> msgs = new ArrayList<>();

			this.logDebug("query["+sqlQueryObject+"] listaMessaggi");
			this.logDebug("Soglia ["+search.getSoglia()+"] ["+dataRegistrazioneLimite+"] listaMessaggi");

			while(rs.next()){
				Messaggio m = new Messaggio();
				// id
				m.setIdMessaggio(rs.getString("ID_MESSAGGIO"));
				// Dettaglio
				Dettaglio dettaglio = new Dettaglio();
				dettaglio.setTipo(rs.getString("tipo"));
				m.setDettaglio(dettaglio);
				msgs.add(m);
			}
			rs.close();
			pstmt.close();

			Timestamp now = DateManager.getTimestamp();
			
			long numeroMsg = 0;
			while(!msgs.isEmpty()){
				Messaggio msgForDelete = msgs.remove(0);
				if(delete) {
					if(this.deleteMessaggio(msgForDelete.getIdMessaggio(), msgForDelete.getDettaglio().getTipo())) {
						numeroMsg++;
					}
				}
				else {
					if(this.aggiornaDataRispedizioneMessaggio(msgForDelete.getIdMessaggio(), msgForDelete.getDettaglio().getTipo(), now)) {
						numeroMsg++;
					}
				}
			}
			if(delete) {
				this.logDebug("eliminati "+numeroMsg+" messaggi");
			}
			else {
				this.logDebug("aggiornati "+numeroMsg+" messaggi");
			}

			return numeroMsg;

		}catch(Exception e){
			if(sqlQueryObject!=null){
				this.logError(nomeMetodo+" error SQL["+sqlQueryObject.toString()+"]",e);
				throw new DriverMonitoraggioException(nomeMetodo+" error SQL["+sqlQueryObject.toString()+"]: "+e.getMessage(),e);
			}else{
				this.logError(nomeMetodo+" error",e);
				throw new DriverMonitoraggioException(nomeMetodo+" error: "+e.getMessage(),e);
			}
		}finally{
			JDBCUtilities.closeResources(rs, pstmt);
			releaseConnection(con);
		}
	}


	@Override
	public StatoConsegneAsincrone getStatoConsegneAsincrone(FiltroStatoConsegnaAsincrona filtro) throws DriverMonitoraggioException{
		
		String nomeMetodo = "getStatoConsegneAsincrone";
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try{
			// Ottengo connessione
			con = getConnection();
			if(con==null)
				throw new DriverMonitoraggioException("Connection non ottenuta dal datasource["+this.datasource+"]");

			// Query
			sqlQuery = this.newSQLQueryStatoConsegneAsincrone(filtro);
			/**System.out.println("QUERY: "+sqlQuery);*/
			pstmt = con.prepareStatement(sqlQuery);
			rs = pstmt.executeQuery();
			StatoConsegneAsincrone stati = new StatoConsegneAsincrone();

			this.logDebug("query["+sqlQuery+"] getStatoConsegneAsincrone");
			
			while(rs.next()){
				StatoConsegnaAsincrona stato = new StatoConsegnaAsincrona();
				
				// dati generali
				stato.setNow(rs.getTimestamp(StatoConsegnaAsincrona.ALIAS_NOW));
				stato.setServizioApplicativo(rs.getString(StatoConsegnaAsincrona.ALIAS_SERVIZIO_APPLICATIVO));
				
				// InCoda
				stato.setInCoda(rs.getLong(StatoConsegnaAsincrona.ALIAS_IN_CODA));
				stato.setVecchioInCoda(rs.getTimestamp(StatoConsegnaAsincrona.ALIAS_IN_CODA_VECCHIO));
				stato.setRecenteInCoda(rs.getTimestamp(StatoConsegnaAsincrona.ALIAS_IN_CODA_RECENTE));
				
				// InRiconsegna
				stato.setInRiconsegna(rs.getLong(StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA));
				stato.setVecchioInRiconsegna(rs.getTimestamp(StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_VECCHIO));
				stato.setRecenteInRiconsegna(rs.getTimestamp(StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_RECENTE));
				
				// InMessageBox
				stato.setInMessageBox(rs.getLong(StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX));
				stato.setVecchioInMessageBox(rs.getTimestamp(StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_VECCHIO));
				stato.setRecenteInMessageBox(rs.getTimestamp(StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_RECENTE));
				
				stati.addStato(stato);
			}
			rs.close();
			pstmt.close();

			this.logDebug("trovati "+stati.size()+" stati");
	
			return stati;

		}catch(Exception e){
			if(sqlQuery!=null){
				this.logError(nomeMetodo+" error SQL["+sqlQuery+"]",e);
				throw new DriverMonitoraggioException(nomeMetodo+" error SQL["+sqlQuery+"]: "+e.getMessage(),e);
			}else{
				this.logError(nomeMetodo+" error",e);
				throw new DriverMonitoraggioException(nomeMetodo+" error: "+e.getMessage(),e);
			}
		}finally{
			JDBCUtilities.closeResources(rs, pstmt);
			releaseConnection(con);
		}
		
	}
	
	




	private ISQLQueryObject newSQLQueryObjectStatoRichiestePendenti(FilterSearch search) throws SQLQueryObjectException{
		return newSQLQueryObjectRichiestePendenti(true,search);
	}
	private ISQLQueryObject newSQLQueryObjectListaRichiestePendenti(FilterSearch search) throws SQLQueryObjectException{
		return newSQLQueryObjectRichiestePendenti(false,search);
	}

	private ISQLQueryObject newSQLQueryObjectRichiestePendenti(boolean statoRichieste,FilterSearch search) throws SQLQueryObjectException{

		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);

		// FROM TABLE
		sqlQueryObject.addFromTable(GestoreMessaggi.MESSAGGI);
		if( (search.getMessagePattern()!=null) && (!"".equals(search.getMessagePattern())) ){
			sqlQueryObject.addFromTable(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
		}
		if( (search.getBusta()!=null) ){
			sqlQueryObject.addFromTable(Costanti.REPOSITORY);
			if(search.getBusta().isAttesaRiscontro()){
				sqlQueryObject.addFromTable(Costanti.RISCONTRI_DA_RICEVERE);
			}
		}else if(search.getSoggettoList()!=null && !search.getSoggettoList().isEmpty()){
			sqlQueryObject.addFromTable(Costanti.REPOSITORY);
		}

		// Condizione per legare le tabelle
		String legameTabelleSQLMsgId = GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+GestoreMessaggi.DEFINIZIONE_MESSAGGI+".ID_MESSAGGIO";
		String legameTabelleSQLMsgTipo = GestoreMessaggi.MESSAGGI+".TIPO="+GestoreMessaggi.DEFINIZIONE_MESSAGGI+".TIPO";
		String legameTabelleSQLBustaId = GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+Costanti.REPOSITORY+".ID_MESSAGGIO";
		String legameTabelleSQLBustaTipo = GestoreMessaggi.MESSAGGI+".TIPO="+Costanti.REPOSITORY+".TIPO";
		String legameTabelleSQLRiscontro = GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+Costanti.RISCONTRI_DA_RICEVERE+".ID_MESSAGGIO";
		if( (search.getMessagePattern()!=null) && (!"".equals(search.getMessagePattern())) ){
			sqlQueryObject.addWhereCondition(true,legameTabelleSQLMsgId,legameTabelleSQLMsgTipo);
		}
		if(search.getBusta()!=null){
			sqlQueryObject.addWhereCondition(true,legameTabelleSQLBustaId,legameTabelleSQLBustaTipo);
		}else if(search.getSoggettoList()!=null && !search.getSoggettoList().isEmpty()){
			sqlQueryObject.addWhereCondition(true,legameTabelleSQLBustaId,legameTabelleSQLBustaTipo);
		}
		if(search.getBusta()!=null && search.getBusta().isAttesaRiscontro()){
			sqlQueryObject.addWhereCondition(legameTabelleSQLRiscontro);
		}



		if(statoRichieste){
			// SELECT PER STATO

			// Count
			sqlQueryObject.addSelectCountField(GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO", "totMessaggi", true);

			// Tempo Medio
			sqlQueryObject.addSelectAvgTimestampField(GestoreMessaggi.MESSAGGI+".ora_registrazione", "ora_registrazione_media");

			// Tempo Massimo
			sqlQueryObject.addSelectMinTimestampField(GestoreMessaggi.MESSAGGI+".ora_registrazione", "ora_registrazione_old");

		}else {

			// SELECT FIELD PER LISTA

			// Tabella OpenSPCoop.sql.Messaggi
			sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"ID_MESSAGGIO");
			sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"TIPO");
			sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"PROPRIETARIO");
			sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"ORA_REGISTRAZIONE");
			sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"ERRORE_PROCESSAMENTO");		
			sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"CORRELAZIONE_APPLICATIVA");
			if(this.properties!=null){
				for (int i = 0; i < this.properties.size(); i++) {
					String key = this.properties.get(i);
					sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,key);
				}
			}
			if( (search.getMessagePattern()!=null) && (!"".equals(search.getMessagePattern())) ){
				// Tabella OpenSPCoop.sql.DefinizioneMessaggi
// Queste due colonne non servono: altrimenti si ottiene un errore column ambiguously defined.
// se in futuro serviranno, utilizzare degli alias
				/**sqlQueryObject.addSelectField(GestoreMessaggi.DEFINIZIONE_MESSAGGI,"ID_MESSAGGIO");
				sqlQueryObject.addSelectField(GestoreMessaggi.DEFINIZIONE_MESSAGGI,"TIPO");*/
				sqlQueryObject.addSelectField(GestoreMessaggi.DEFINIZIONE_MESSAGGI,"MSG_BYTES");
			}	
			if(search.getBusta()!=null){
				// Tabella Libreria.sql.Repository
// Queste due colonne non servono: altrimenti si ottiene un errore column ambiguously defined.
// se in futuro serviranno, utilizzare degli alias
				/**sqlQueryObject.addSelectField(Costanti.REPOSITORY,"ID_MESSAGGIO");
				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"TIPO");*/
				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"TIPO_MITTENTE");
				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"MITTENTE");
				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"TIPO_DESTINATARIO");
				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"DESTINATARIO");
				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"TIPO_SERVIZIO");
				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"SERVIZIO");
				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"AZIONE");
				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"PROFILO_DI_COLLABORAZIONE");
				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"RIFERIMENTO_MESSAGGIO");
				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"COLLABORAZIONE");
			}	
			if(search.getBusta()!=null && search.getBusta().isAttesaRiscontro()){
				// Tabella Libreria.sql.RiscontriDaRicevere
// Queste due colonne non servono: altrimenti si ottiene un errore column ambiguously defined.
// se in futuro serviranno, utilizzare degli alias
				/**sqlQueryObject.addSelectField(Costanti.RISCONTRI_DA_RICEVERE,"ID_MESSAGGIO");*/
			}

		}

		// Where Logico Operator
		sqlQueryObject.setANDLogicOperator(true);

		// Order By OraRegistrazione
		if(!statoRichieste){
			sqlQueryObject.addOrderBy(GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE");
			sqlQueryObject.setSortType(false); // DESC
		}

		// Proprietario non deve essere GestoreMessaggi, a meno che il filtro non richieda proprio questo
		if( (search.getStato()==null) || ("".equals(search.getStato().getValue())) || (!StatoMessaggio.CANCELLATO.equals(search.getStato())) )
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".PROPRIETARIO<>'"+TimerGestoreMessaggi.ID_MODULO+"'");

		// Filtri:

		// a) StatoMessaggio
		if( (search.getStato()!=null) && (!"".equals(search.getStato().getValue())) ){
			if(StatoMessaggio.CONSEGNA.equals(search.getStato())){
				sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".PROPRIETARIO='"+ConsegnaContenutiApplicativi.ID_MODULO+"'");
			}else if(StatoMessaggio.SPEDIZIONE.equals(search.getStato())){
				sqlQueryObject.addWhereCondition(false,
						GestoreMessaggi.MESSAGGI+".PROPRIETARIO='"+InoltroBuste.ID_MODULO+"'",
						GestoreMessaggi.MESSAGGI+".PROPRIETARIO='"+InoltroRisposte.ID_MODULO+"'");
			}else if(StatoMessaggio.CANCELLATO.equals(search.getStato())){
				sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".PROPRIETARIO='"+TimerGestoreMessaggi.ID_MODULO+"'");
			}else if(StatoMessaggio.PROCESSAMENTO.equals(search.getStato())){
				sqlQueryObject.addWhereCondition(true, GestoreMessaggi.MESSAGGI+".PROPRIETARIO<>'"+InoltroBuste.ID_MODULO+"'",
						GestoreMessaggi.MESSAGGI+".PROPRIETARIO<>'"+InoltroRisposte.ID_MODULO+"'",
						GestoreMessaggi.MESSAGGI+".PROPRIETARIO<>'"+ConsegnaContenutiApplicativi.ID_MODULO+"'");
			}else{
				throw new SQLQueryObjectException("Stato per filtro non conosciuto: "+search.getStato());
			}
		}

		// b) idMessaggio e propertiesRicerca
		if( (search.getIdMessaggio()!=null) && (!"".equals(search.getIdMessaggio())) ){
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO='"+search.getIdMessaggio()+"'");
		}
		if( (search.getProprietaList()!=null) && (!search.getProprietaList().isEmpty()) ){
			List<Proprieta> proprietaList = search.getProprietaList();
			for (Proprieta proprieta : proprietaList) {
				sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+"."+proprieta.getNome()+"='"+proprieta.getValore()+"'");	
			}
		}

		// c) MessagePattern e CorrelazioneApplicativa
		if( (search.getMessagePattern()!=null) && (!"".equals(search.getMessagePattern())) ){
			sqlQueryObject.addWhereLikeCondition(GestoreMessaggi.DEFINIZIONE_MESSAGGI+".MSG_BYTES",search.getMessagePattern(),true,true);
		}
		if( (search.getCorrelazioneApplicativa()!=null) && (!"".equals(search.getCorrelazioneApplicativa())) ){
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".CORRELAZIONE_APPLICATIVA='"+search.getCorrelazioneApplicativa()+"'");
		}

		// d) Soglia (ora_registrazione piu' vecchia di NOW-search.getSoglia())
		if(search.getSoglia()!=-1){
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".ora_registrazione<=?");
		}

		if(!statoRichieste){
			// e) limit
			if(search.getLimit()!=-1){
				sqlQueryObject.setLimit((int)search.getLimit());
			}
			// f) offset
			if(search.getOffset()!=-1){
				sqlQueryObject.setOffset((int)search.getOffset());
			}
		}

		if(search.getBusta()!=null){

			// Busta filter

			// mittente
			if(search.getBusta().getMittente()!=null){
				if( (search.getBusta().getMittente().getTipo()!=null) && (!"".equals(search.getBusta().getMittente().getTipo())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_MITTENTE='"+search.getBusta().getMittente().getTipo()+"'");
				}
				if( (search.getBusta().getMittente().getNome()!=null) && (!"".equals(search.getBusta().getMittente().getNome())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".MITTENTE='"+search.getBusta().getMittente().getNome()+"'");
				}
			}

			// destinatario
			if(search.getBusta().getDestinatario()!=null){
				if( (search.getBusta().getDestinatario().getTipo()!=null) && (!"".equals(search.getBusta().getDestinatario().getTipo())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_DESTINATARIO='"+search.getBusta().getDestinatario().getTipo()+"'");
				}
				if( (search.getBusta().getDestinatario().getNome()!=null) && (!"".equals(search.getBusta().getDestinatario().getNome())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".DESTINATARIO='"+search.getBusta().getDestinatario().getNome()+"'");
				}
			}

			// servizio
			if(search.getBusta().getServizio()!=null){
				if( (search.getBusta().getServizio().getTipo()!=null) && (!"".equals(search.getBusta().getServizio().getTipo())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_SERVIZIO='"+search.getBusta().getServizio().getTipo()+"'");
				}
				if( (search.getBusta().getServizio().getNome()!=null) && (!"".equals(search.getBusta().getServizio().getNome())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".SERVIZIO='"+search.getBusta().getServizio().getNome()+"'");
				}
				if( (search.getBusta().getServizio().getVersione()!=null) && (search.getBusta().getServizio().getVersione().intValue()>0) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".VERSIONE_SERVIZIO='"+search.getBusta().getServizio().getVersione().intValue()+"'");
				}
			}

			// azione
			if( (search.getBusta().getAzione()!=null) && (!"".equals(search.getBusta().getAzione())) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".AZIONE='"+search.getBusta().getAzione()+"'");
			}

			// profiloCollaborazione
			if( (search.getBusta().getProfiloCollaborazione()!=null) && (!"".equals(search.getBusta().getProfiloCollaborazione())) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".PROFILO_DI_COLLABORAZIONE='"+search.getBusta().getProfiloCollaborazione()+"'");
			}

			// riferimentoMessaggio
			if( (search.getBusta().getRiferimentoMessaggio()!=null) && (!"".equals(search.getBusta().getRiferimentoMessaggio())) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".RIFERIMENTO_MESSAGGIO='"+search.getBusta().getRiferimentoMessaggio()+"'");
			}

			// Collaborazione
			if( (search.getBusta().getCollaborazione()!=null) && (!"".equals(search.getBusta().getCollaborazione())) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".COLLABORAZIONE='"+search.getBusta().getCollaborazione()+"'");
			}

		}

		if(search.getSoggettoList()!=null && !search.getSoggettoList().isEmpty()){

			List<BustaSoggetto> filtroSoggetti = search.getSoggettoList();
			StringBuilder query = new StringBuilder();
			for(int k=0; k<filtroSoggetti.size(); k++){
				if(k>0)
					query.append(" OR ");
				query.append("( ");
				query.append("("+Costanti.REPOSITORY+".TIPO_MITTENTE = ? AND "+Costanti.REPOSITORY+".MITTENTE = ?)");
				query.append(" OR ");
				query.append("("+Costanti.REPOSITORY+".TIPO_DESTINATARIO = ? AND "+Costanti.REPOSITORY+".DESTINATARIO = ?)");
				query.append(" )");
			}
			sqlQueryObject.addWhereCondition(query.toString());
		}

		return sqlQueryObject;
	}










	private ISQLQueryObject newSQLQueryPacchettiDuplicati(FilterSearch search) throws SQLQueryObjectException{

		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);

		// FROM TABLE
		sqlQueryObject.addFromTable(Costanti.REPOSITORY);

		// SELECT FIELD
		sqlQueryObject.addSelectField("sum(DUPLICATI) as numduplicati");

		// Where Logico Operator
		sqlQueryObject.setANDLogicOperator(true);


		// Filtri:

		// a) idMessaggio
		if( (search.getIdMessaggio()!=null) && (!"".equals(search.getIdMessaggio())) ){
			sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".ID_MESSAGGIO='"+search.getIdMessaggio()+"'");
		}

		// b) Soglia (ora_registrazione piu' vecchia di NOW-search.getSoglia())
		if(search.getSoglia()!=-1){
			sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".ora_registrazione<=?");
		}

		if(search.getBusta()!=null){

			// Busta filter

			// mittente
			if(search.getBusta().getMittente()!=null){
				if( (search.getBusta().getMittente().getTipo()!=null) && (!"".equals(search.getBusta().getMittente().getTipo())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_MITTENTE='"+search.getBusta().getMittente().getTipo()+"'");
				}
				if( (search.getBusta().getMittente().getNome()!=null) && (!"".equals(search.getBusta().getMittente().getNome())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".MITTENTE='"+search.getBusta().getMittente().getNome()+"'");
				}
			}

			// destinatario
			if(search.getBusta().getDestinatario()!=null){
				if( (search.getBusta().getDestinatario().getTipo()!=null) && (!"".equals(search.getBusta().getDestinatario().getTipo())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_DESTINATARIO='"+search.getBusta().getDestinatario().getTipo()+"'");
				}
				if( (search.getBusta().getDestinatario().getNome()!=null) && (!"".equals(search.getBusta().getDestinatario().getNome())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".DESTINATARIO='"+search.getBusta().getDestinatario().getNome()+"'");
				}
			}

			// servizio
			if(search.getBusta().getServizio()!=null){
				if( (search.getBusta().getServizio().getTipo()!=null) && (!"".equals(search.getBusta().getServizio().getTipo())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_SERVIZIO='"+search.getBusta().getServizio().getTipo()+"'");
				}
				if( (search.getBusta().getServizio().getNome()!=null) && (!"".equals(search.getBusta().getServizio().getNome())) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".SERVIZIO='"+search.getBusta().getServizio().getNome()+"'");
				}
				if( (search.getBusta().getServizio().getVersione()!=null) && (search.getBusta().getServizio().getVersione().intValue()>0) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".VERSIONE_SERVIZIO='"+search.getBusta().getServizio().getVersione().intValue()+"'");
				}
			}

			// azione
			if( (search.getBusta().getAzione()!=null) && (!"".equals(search.getBusta().getAzione())) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".AZIONE='"+search.getBusta().getAzione()+"'");
			}

			// profiloCollaborazione
			if( (search.getBusta().getProfiloCollaborazione()!=null) && (!"".equals(search.getBusta().getProfiloCollaborazione())) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".PROFILO_DI_COLLABORAZIONE='"+search.getBusta().getProfiloCollaborazione()+"'");
			}

			// riferimentoMessaggio
			if( (search.getBusta().getRiferimentoMessaggio()!=null) && (!"".equals(search.getBusta().getRiferimentoMessaggio())) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".RIFERIMENTO_MESSAGGIO='"+search.getBusta().getRiferimentoMessaggio()+"'");
			}

			// Collaborazione
			if( (search.getBusta().getCollaborazione()!=null) && (!"".equals(search.getBusta().getCollaborazione())) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".COLLABORAZIONE='"+search.getBusta().getCollaborazione()+"'");
			}

		}

		return sqlQueryObject;
	}
	
	
	private String newSQLQueryStatoConsegneAsincrone(FiltroStatoConsegnaAsincrona filtro) throws SQLQueryObjectException{

		String aliasTableServiziApplicativi = "sa";
		String aliasTableMessaggi = "m";
		
		// ** InCoda **
		
		/*
		 *	select 0 as inRiconsegna, count(*) as inCoda, 
		 *  null::timestamp as vecchioInRiconsegna, null::timestamp as recenteInRiconsegna, 
		 *  min(sa.ora_registrazione) as vecchioInCoda, max(sa.ora_registrazione) as recenteInCoda, 
		 *  0 as inCaricoIM, null::timestamp as vecchioInCaricoIM, null::timestamp as recenteInCaricoIM, 
		 *  sa.SERVIZIO_APPLICATIVO  
		 *  from msg_servizi_applicativi sa,messaggi m 
		 *  where 
		 *  sa.attesa_esito=0 AND 
		 *  (sa.errore_processamento_compact='TimerConsegnaContenutiApplicativi') AND 
		 *  sa.tipo_consegna='Connettore' AND 
		 *  m.id_messaggio=sa.id_messaggio AND 
		 *  m.proprietario<>'GestoreMessaggi' 
		 *  group by sa.servizio_applicativo
		 **/
		
		ISQLQueryObject sqlQueryObjectCoda = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
		
		sqlQueryObjectCoda.addFromTable(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI,aliasTableServiziApplicativi);
		sqlQueryObjectCoda.addFromTable(GestoreMessaggi.MESSAGGI,aliasTableMessaggi);
		
		sqlQueryObjectCoda.addSelectAliasField(getIntZeroValue(),StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA);
		sqlQueryObjectCoda.addSelectCountField(StatoConsegnaAsincrona.ALIAS_IN_CODA);
		sqlQueryObjectCoda.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_VECCHIO);
		sqlQueryObjectCoda.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_RECENTE);
		sqlQueryObjectCoda.addSelectMinField(aliasTableServiziApplicativi, GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ORA_REGISTRAZIONE, StatoConsegnaAsincrona.ALIAS_IN_CODA_VECCHIO);
		sqlQueryObjectCoda.addSelectMaxField(aliasTableServiziApplicativi, GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ORA_REGISTRAZIONE, StatoConsegnaAsincrona.ALIAS_IN_CODA_RECENTE);
		sqlQueryObjectCoda.addSelectAliasField(getIntZeroValue(),StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX);
		sqlQueryObjectCoda.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_VECCHIO);
		sqlQueryObjectCoda.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_RECENTE);
		sqlQueryObjectCoda.addSelectAliasField(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_SERVIZIO_APPLICATIVO,StatoConsegnaAsincrona.ALIAS_SERVIZIO_APPLICATIVO);
		
		sqlQueryObjectCoda.setANDLogicOperator(true);
		sqlQueryObjectCoda.addWhereCondition(aliasTableServiziApplicativi+"."+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ATTESA_ESITO+"=0");
		sqlQueryObjectCoda.addWhereCondition(aliasTableServiziApplicativi+"."+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ERRORE_PROCESSAMENTO_COMPACT+"='"+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"'");
		sqlQueryObjectCoda.addWhereCondition(aliasTableServiziApplicativi+"."+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_TIPO_CONSEGNA+"='"+GestoreMessaggi.CONSEGNA_TRAMITE_CONNETTORE+"'");
		sqlQueryObjectCoda.addWhereCondition(aliasTableMessaggi+"."+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+"="+aliasTableServiziApplicativi+"."+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ID_MESSAGGIO);
		sqlQueryObjectCoda.addWhereCondition(aliasTableMessaggi+"."+GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"<>'"+TimerGestoreMessaggi.ID_MODULO+"'");
		
		sqlQueryObjectCoda.addGroupBy(StatoConsegnaAsincrona.ALIAS_SERVIZIO_APPLICATIVO);
		
		
		// ** InRiconsegna **
		
		/*
		 *	select count(*) as inRiconsegna, 0 as inCoda, 
		 *  min(sa.ora_registrazione) as vecchioInRiconsegna, max(sa.ora_registrazione) as recenteInRiconsegna,  
		 *  null::timestamp as vecchioInCoda, null::timestamp as recenteInCoda, 
		 *  0 as inCaricoIM, null::timestamp as vecchioInCaricoIM, null::timestamp as recenteInCaricoIM, 
		 *  sa.SERVIZIO_APPLICATIVO 
		 *  from msg_servizi_applicativi sa,messaggi m 
		 *  where 
		 *  sa.attesa_esito=0 AND 
		 *  (sa.errore_processamento_compact<>'TimerConsegnaContenutiApplicativi') AND 
		 *  sa.tipo_consegna='Connettore' AND 
		 *  m.id_messaggio=sa.id_messaggio AND 
		 *  m.proprietario<>'GestoreMessaggi' 
		 *  group by sa.servizio_applicativo
		 **/
		
		ISQLQueryObject sqlQueryObjectRiconsegna = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
		
		sqlQueryObjectRiconsegna.addFromTable(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI,aliasTableServiziApplicativi);
		sqlQueryObjectRiconsegna.addFromTable(GestoreMessaggi.MESSAGGI,aliasTableMessaggi);
		
		sqlQueryObjectRiconsegna.addSelectCountField(StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA);
		sqlQueryObjectRiconsegna.addSelectAliasField(getIntZeroValue(),StatoConsegnaAsincrona.ALIAS_IN_CODA);
		sqlQueryObjectRiconsegna.addSelectMinField(aliasTableServiziApplicativi, GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ORA_REGISTRAZIONE, StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_VECCHIO);
		sqlQueryObjectRiconsegna.addSelectMaxField(aliasTableServiziApplicativi, GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ORA_REGISTRAZIONE, StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_RECENTE);
		sqlQueryObjectRiconsegna.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_CODA_VECCHIO);
		sqlQueryObjectRiconsegna.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_CODA_RECENTE);
		sqlQueryObjectRiconsegna.addSelectAliasField(getIntZeroValue(),StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX);
		sqlQueryObjectRiconsegna.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_VECCHIO);
		sqlQueryObjectRiconsegna.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_RECENTE);
		sqlQueryObjectRiconsegna.addSelectAliasField(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_SERVIZIO_APPLICATIVO,StatoConsegnaAsincrona.ALIAS_SERVIZIO_APPLICATIVO);
		
		sqlQueryObjectRiconsegna.setANDLogicOperator(true);
		sqlQueryObjectRiconsegna.addWhereCondition(aliasTableServiziApplicativi+"."+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ATTESA_ESITO+"=0");
		sqlQueryObjectRiconsegna.addWhereCondition(aliasTableServiziApplicativi+"."+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ERRORE_PROCESSAMENTO_COMPACT+"<>'"+TimerConsegnaContenutiApplicativiThread.ID_MODULO+"'");
		sqlQueryObjectRiconsegna.addWhereCondition(aliasTableServiziApplicativi+"."+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_TIPO_CONSEGNA+"='"+GestoreMessaggi.CONSEGNA_TRAMITE_CONNETTORE+"'");
		sqlQueryObjectRiconsegna.addWhereCondition(aliasTableMessaggi+"."+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+"="+aliasTableServiziApplicativi+"."+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ID_MESSAGGIO);
		sqlQueryObjectRiconsegna.addWhereCondition(aliasTableMessaggi+"."+GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"<>'"+TimerGestoreMessaggi.ID_MODULO+"'");
		
		sqlQueryObjectRiconsegna.addGroupBy(StatoConsegnaAsincrona.ALIAS_SERVIZIO_APPLICATIVO);
		
		
		// ** InMessageBox **
		
		/*
		 *	select 0 as inRiconsegna, 0 as inCoda, 
		 *  null::timestamp as vecchioInRiconsegna, null::timestamp as recenteInRiconsegna,  
		 *  null::timestamp as vecchioInCoda, null::timestamp as recenteInCoda, 
		 *  count(*) as inCaricoIM, min(sa.ora_registrazione) as vecchioInCaricoIM, max(sa.ora_registrazione) as recenteInCaricoIM, 
		 *  sa.SERVIZIO_APPLICATIVO 
		 *  from msg_servizi_applicativi sa,messaggi m 
		 *  where 
		 *  sa.tipo_consegna='IntegrationManager' AND 
		 *  m.id_messaggio=sa.id_messaggio AND 
		 *  m.proprietario<>'GestoreMessaggi' 
		 *  group by sa.servizio_applicativo
		 **/
		
		ISQLQueryObject sqlQueryObjectMessageBox = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
		
		sqlQueryObjectMessageBox.addFromTable(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI,aliasTableServiziApplicativi);
		sqlQueryObjectMessageBox.addFromTable(GestoreMessaggi.MESSAGGI,aliasTableMessaggi);
		
		sqlQueryObjectMessageBox.addSelectAliasField(getIntZeroValue(),StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA);
		sqlQueryObjectMessageBox.addSelectAliasField(getIntZeroValue(),StatoConsegnaAsincrona.ALIAS_IN_CODA);
		sqlQueryObjectMessageBox.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_VECCHIO);
		sqlQueryObjectMessageBox.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_RECENTE);
		sqlQueryObjectMessageBox.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_CODA_VECCHIO);
		sqlQueryObjectMessageBox.addSelectAliasField(getNullTimestampValue(),StatoConsegnaAsincrona.ALIAS_IN_CODA_RECENTE);
		sqlQueryObjectMessageBox.addSelectCountField(StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX);
		sqlQueryObjectMessageBox.addSelectMinField(aliasTableServiziApplicativi, GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ORA_REGISTRAZIONE, StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_VECCHIO);
		sqlQueryObjectMessageBox.addSelectMaxField(aliasTableServiziApplicativi, GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ORA_REGISTRAZIONE, StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_RECENTE);
		sqlQueryObjectMessageBox.addSelectAliasField(GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_SERVIZIO_APPLICATIVO,StatoConsegnaAsincrona.ALIAS_SERVIZIO_APPLICATIVO);
		
		sqlQueryObjectMessageBox.setANDLogicOperator(true);
		sqlQueryObjectMessageBox.addWhereCondition(aliasTableServiziApplicativi+"."+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_TIPO_CONSEGNA+"='"+GestoreMessaggi.CONSEGNA_TRAMITE_INTEGRATION_MANAGER+"'");
		sqlQueryObjectMessageBox.addWhereCondition(aliasTableMessaggi+"."+GestoreMessaggi.MESSAGGI_COLUMN_ID_MESSAGGIO+"="+aliasTableServiziApplicativi+"."+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI_COLUMN_ID_MESSAGGIO);
		sqlQueryObjectMessageBox.addWhereCondition(aliasTableMessaggi+"."+GestoreMessaggi.MESSAGGI_COLUMN_PROPRIETARIO+"<>'"+TimerGestoreMessaggi.ID_MODULO+"'");
		
		sqlQueryObjectMessageBox.addGroupBy(StatoConsegnaAsincrona.ALIAS_SERVIZIO_APPLICATIVO);

		
		// ** UNION **
		
		/**
		 * select CURRENT_TIMESTAMP as now, 
		 * max(inRiconsegna) as inRiconsegna, max(inCoda) as inCoda, 
		 * min(vecchioInRiconsegna) as vecchioInRiconsegna, max(recenteInRiconsegna) as recenteInRiconsegna, 
		 * min(vecchioInCoda) as vecchioInCoda, max(recenteInCoda) as recenteInCoda, 
		 * max(inCaricoIM) as inCaricoIM, min(vecchioInCaricoIM) as vecchioInCaricoIM, max(recenteInCaricoIM) as recenteInCaricoIM,  
		 * servizio_applicativo from (
				QUERY_IN_CODA
			UNION ALL
				QUERY_IN_RICONSEGNA
			UNION ALL
				QUERY_MESSAGE_BOX
		   ) as query group by servizio_applicativo;
		 */
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabase);
		
		sqlQueryObject.addSelectAliasField("CURRENT_TIMESTAMP", StatoConsegnaAsincrona.ALIAS_NOW);
		sqlQueryObject.addSelectMaxField(StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA, StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA);
		sqlQueryObject.addSelectMaxField(StatoConsegnaAsincrona.ALIAS_IN_CODA, StatoConsegnaAsincrona.ALIAS_IN_CODA);
		sqlQueryObject.addSelectMinField(StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_VECCHIO, StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_VECCHIO);
		sqlQueryObject.addSelectMaxField(StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_RECENTE, StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA_RECENTE);
		sqlQueryObject.addSelectMinField(StatoConsegnaAsincrona.ALIAS_IN_CODA_VECCHIO, StatoConsegnaAsincrona.ALIAS_IN_CODA_VECCHIO);
		sqlQueryObject.addSelectMaxField(StatoConsegnaAsincrona.ALIAS_IN_CODA_RECENTE, StatoConsegnaAsincrona.ALIAS_IN_CODA_RECENTE);
		sqlQueryObject.addSelectMaxField(StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX, StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX);
		sqlQueryObject.addSelectMinField(StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_VECCHIO, StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_VECCHIO);
		sqlQueryObject.addSelectMaxField(StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_RECENTE, StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX_RECENTE);		
		sqlQueryObject.addSelectAliasField(StatoConsegnaAsincrona.ALIAS_SERVIZIO_APPLICATIVO, StatoConsegnaAsincrona.ALIAS_SERVIZIO_APPLICATIVO);
		
		sqlQueryObject.addGroupBy(StatoConsegnaAsincrona.ALIAS_SERVIZIO_APPLICATIVO);
		
		if(filtro!=null) {
			if(filtro.isOrderByInCoda()) {
				sqlQueryObject.addOrderBy(StatoConsegnaAsincrona.ALIAS_IN_CODA, false);
			}
			if(filtro.isOrderByInRiconsegna()) {
				sqlQueryObject.addOrderBy(StatoConsegnaAsincrona.ALIAS_IN_RICONSEGNA, false);
			}
			if(filtro.isOrderByInMessageBox()) {
				sqlQueryObject.addOrderBy(StatoConsegnaAsincrona.ALIAS_IN_MESSAGE_BOX, false);
			}
		}
		sqlQueryObject.addOrderBy(StatoConsegnaAsincrona.ALIAS_SERVIZIO_APPLICATIVO, true);
			
		return sqlQueryObject.createSQLUnion(true, sqlQueryObjectRiconsegna, sqlQueryObjectCoda, sqlQueryObjectMessageBox);
	}
	
	private String getNullTimestampValue() {
		if(TipiDatabase.POSTGRESQL.equals(this.tipoDatabase)) {
			return "null::timestamp";
		}
		else {
			return "null";
		}
	}
	private String getIntZeroValue() {
		return "0";
	}
}
