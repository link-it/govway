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



package org.openspcoop2.pdd.monitor.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.slf4j.Logger;
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
import org.openspcoop2.pdd.timers.TimerGestoreMessaggi;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * Driver per il Monitoraggio della Porta di Dominio OpenSPCoop
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

	/**
	 * Properties
	 */
	private Vector<String> properties;
	public void setProperties(Vector<String> properties) {
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
				throw new Exception ("datasource is null");

			initCtx.close();
			this.log.info("Inizializzo DriverMonitoraggioDB terminata.");
		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del datasource...",e);
			throw new DriverMonitoraggioException("Errore durante la ricerca del datasource...",e);
		}

		// ISQLQueryObject SQLObjectFactory
		try {
			this.log.info("Inizializzo ISQLQueryObject ...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new Exception("Tipo database non gestito");
			}

			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del SQLQueryObject...",e);
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
			Class.forName(driverJDBC).newInstance();
			if(username!=null){
				this.globalConnection = DriverManager.getConnection(connectionUrl,username,password);
			}else{
				this.globalConnection = DriverManager.getConnection(connectionUrl);
			}
			if(this.globalConnection == null){
				throw new Exception("Connection is null");
			}
			this.log.info("Inizializzo DriverMonitoraggioDB terminata.");
		} catch (Exception e) {
			this.log.error("Errore durante l'inizializzazione della connessione...",e);
			throw new DriverMonitoraggioException("Errore durante l'inizializzazione della connessione...",e);
		}

		// ISQLQueryObject SQLObjectFactory
		try {
			this.log.info("Inizializzo ISQLQueryObject ...");
			if (TipiDatabase.isAMember(tipoDatabase)) {
				this.tipoDatabase = tipoDatabase;				
			} else {
				throw new Exception("Tipo database non gestito");
			}

			this.log.info("Inizializzo ISQLQueryObject terminata.");

		} catch (Exception e) {
			this.log.error("Errore durante la ricerca del SQLQueryObject...",e);
			throw new DriverMonitoraggioException("Errore durante la ricerca del SQLQueryObject("+tipoDatabase+")...",e);
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
			if (this.datasource!=null)
				con = this.datasource.getConnection();
			else
				con = this.globalConnection;
			if(con==null)
				throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");

			StatoPdd statoPdD = new StatoPdd();
			java.sql.Timestamp now = DateManager.getTimestamp();
			java.sql.Timestamp data_registrazione_limite = null;
			//	FILTRO d) Soglia (ora_registrazione piu' vecchia di NOW-search.getSoglia()) Soglia in Minuti
			if(search.getSoglia()!=-1){
				data_registrazione_limite = new java.sql.Timestamp(DateManager.getTimeMillis()-(search.getSoglia()*1000*60));
			}


			// TotaleMessaggi
			long totaleMessaggi = 0;
			long totaleMessaggiDuplicati = -1;
			sqlQueryObject = this.newSQLQueryObjectStatoRichiestePendenti(search);
			pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			int param_index = 0;
			if(search.getSoglia()!=-1)
				pstmt.setTimestamp(++param_index, data_registrazione_limite);
			List<BustaSoggetto> filtroSoggetti = search.getSoggettoList();
			if(filtroSoggetti!=null){			
				for(int k=0; k<filtroSoggetti.size(); k++){

					// fruitore
					pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());

					// OR

					// erogatore
					pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());
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
			this.log.debug("query["+sqlQueryObject+"] totaleMessaggi:"+totaleMessaggi);
			this.log.debug("Soglia ["+search.getSoglia()+"] ["+data_registrazione_limite+"] totaleMessaggi");

			if(totaleMessaggi>0){

				// Messaggi in Consegna
				if( (search.getStato()==null) || ("".equals(search.getStato())) || (StatoMessaggio.CONSEGNA.equals(search.getStato())) ){
					sqlQueryObject = this.newSQLQueryObjectStatoRichiestePendenti(search);
					if((search.getStato()==null) || ("".equals(search.getStato()))){
						sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".proprietario='"+ConsegnaContenutiApplicativi.ID_MODULO+"'");
					}
					pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
					param_index = 0;
					if(search.getSoglia()!=-1)
						pstmt.setTimestamp(++param_index, data_registrazione_limite);
					if(filtroSoggetti!=null){
						for(int k=0; k<filtroSoggetti.size(); k++){

							// fruitore
							pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());

							// OR

							// erogatore
							pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());
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
					this.log.debug("query["+sqlQueryObject+"] totaleMessaggiInConsegna:"+statoPdD.getNumMsgInConsegna());
					this.log.debug("Soglia ["+search.getSoglia()+"] ["+data_registrazione_limite+"] totaleMessaggiInConsegna");
				}

				// Messaggi in Spedizione
				if( (search.getStato()==null) || ("".equals(search.getStato())) || (StatoMessaggio.SPEDIZIONE.equals(search.getStato())) ){
					sqlQueryObject = this.newSQLQueryObjectStatoRichiestePendenti(search);
					if( (search.getStato()==null) || ("".equals(search.getStato())) ){
						sqlQueryObject.addWhereCondition(false,GestoreMessaggi.MESSAGGI+".proprietario='InoltroBuste'",
								GestoreMessaggi.MESSAGGI+".proprietario='InoltroRisposte'");
					}
					pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
					param_index = 0;
					if(search.getSoglia()!=-1)
						pstmt.setTimestamp(++param_index, data_registrazione_limite);
					if(filtroSoggetti!=null){
						for(int k=0; k<filtroSoggetti.size(); k++){

							// fruitore
							pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());

							// OR

							// erogatore
							pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());
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
					this.log.debug("query["+sqlQueryObject+"] totaleMessaggiInSpedizione:"+statoPdD.getNumMsgInSpedizione());
					this.log.debug("Soglia ["+search.getSoglia()+"] ["+data_registrazione_limite+"] totaleMessaggiInSpedizione");
				}

				// Messaggi ne in Spedizione, ne in Consegna (PROCESSAMENTO) 
				if( (search.getStato()==null) || ("".equals(search.getStato())) || (StatoMessaggio.PROCESSAMENTO.equals(search.getStato())) ){
					sqlQueryObject = this.newSQLQueryObjectStatoRichiestePendenti(search);
					if( (search.getStato()==null) || ("".equals(search.getStato())) ){
						sqlQueryObject.addWhereCondition(true, GestoreMessaggi.MESSAGGI+".proprietario<>'InoltroBuste'",
								GestoreMessaggi.MESSAGGI+".proprietario<>'InoltroRisposte'",
								GestoreMessaggi.MESSAGGI+".proprietario<>'"+ConsegnaContenutiApplicativi.ID_MODULO+"'");
					}
					pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
					param_index = 0;
					if(search.getSoglia()!=-1)
						pstmt.setTimestamp(++param_index, data_registrazione_limite);
					if(filtroSoggetti!=null){
						for(int k=0; k<filtroSoggetti.size(); k++){

							// fruitore
							pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());

							// OR

							// erogatore
							pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
							pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());
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
					this.log.debug("query["+sqlQueryObject+"] totaleMessaggiInProcessamento:"+statoPdD.getNumMsgInProcessamento());
					this.log.debug("Soglia ["+search.getSoglia()+"] ["+data_registrazione_limite+"] totaleMessaggiInProcessamento");
				}

			}

			// MessaggiDuplicati
			// TODO: Filtro con num duplicati
			this.log.debug("Calcolo numero pacchetti duplicati...");
			sqlQueryObject = this.newSQLQueryPacchettiDuplicati(search);
			pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			if(search.getSoglia()!=-1)
				pstmt.setTimestamp(1, data_registrazione_limite);
			rs = pstmt.executeQuery();
			if(rs.next()){
				totaleMessaggiDuplicati = rs.getLong("numduplicati");
				statoPdD.setTotMessaggiDuplicati(totaleMessaggiDuplicati);
			}
			rs.close();
			pstmt.close();
			this.log.debug("query["+sqlQueryObject.toString()+"] totaleMessaggiDuplicati:"+totaleMessaggiDuplicati);
			this.log.debug("Soglia ["+search.getSoglia()+"] ["+data_registrazione_limite+"] totaleMessaggiDuplicati");


			return statoPdD;

		}catch(Exception e){
			if(sqlQueryObject!=null){
				this.log.error("getStatoRichiestePendenti error SQL["+sqlQueryObject.toString()+"]",e);
				throw new DriverMonitoraggioException("getStatoRichiestePendenti error SQL["+sqlQueryObject.toString()+"]: "+e.getMessage(),e);
			}else{
				this.log.error("getStatoRichiestePendenti error",e);
				throw new DriverMonitoraggioException("getStatoRichiestePendenti error: "+e.getMessage(),e);
			}

		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){}
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception e){}
			try{
				if(this.datasource!=null && con!=null)
					con.close();
			}catch(Exception e){}
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
			if (this.datasource!=null)
				con = this.datasource.getConnection();
			else
				con = this.globalConnection;
			if(con==null)
				throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");

			// TotaleMessaggi
			long totaleMessaggi = 0;
			String query = "SELECT count(*) as totMessaggi FROM "+GestoreMessaggi.MESSAGGI+" WHERE "+GestoreMessaggi.MESSAGGI+".proprietario<>'"+TimerGestoreMessaggi.ID_MODULO+"'";
			pstmt = con.prepareStatement(query);
			rs = pstmt.executeQuery();
			if(rs.next()){
				totaleMessaggi = rs.getLong("totMessaggi");
			}
			rs.close();
			pstmt.close();
			this.log.debug("query["+query+"] totaleMessaggi:"+totaleMessaggi);

			return totaleMessaggi;

		}catch(Exception e){
			this.log.error("getTotaleMessaggiInGestione error",e);
			throw new DriverMonitoraggioException("getStatoRichiestePendenti error: "+e.getMessage(),e);
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){}
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception e){}
			try{
				if(this.datasource!=null && con!=null)
					con.close();
			}catch(Exception e){}
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
			if (this.datasource!=null)
				con = this.datasource.getConnection();
			else
				con = this.globalConnection;
			if(con==null)
				throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");

			java.sql.Timestamp data_registrazione_limite = null;
			//	FILTRO d) Soglia (ora_registrazione piu' vecchia di NOW-search.getSoglia()) Soglia in Minuti
			if(search.getSoglia()!=-1){
				data_registrazione_limite = new java.sql.Timestamp(DateManager.getTimeMillis()-(search.getSoglia()*1000*60));
			}

			// TotaleMessaggi: Il totale dei messaggi non deve subire influenze di offset/limit
			long totaleMessaggi = 0;
			search.setLimit(-1);
			search.setOffset(-1);
			sqlQueryObject = this.newSQLQueryObjectStatoRichiestePendenti(search);
			pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			int param_index = 0;
			if(search.getSoglia()!=-1)
				pstmt.setTimestamp(++param_index, data_registrazione_limite);
			List<BustaSoggetto> filtroSoggetti = search.getSoggettoList();
			if(filtroSoggetti!=null){
				for(int k=0; k<filtroSoggetti.size(); k++){

					// fruitore
					pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());

					// OR

					// erogatore
					pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());
				}
			}
			rs = pstmt.executeQuery();
			if(rs.next()){
				totaleMessaggi = rs.getLong("totMessaggi");
			}
			rs.close();
			pstmt.close();
			this.log.debug("query["+sqlQueryObject+"] totaleMessaggi:"+totaleMessaggi);
			this.log.debug("Soglia ["+search.getSoglia()+"] ["+data_registrazione_limite+"] totaleMessaggi");

			return totaleMessaggi;

		}catch(Exception e){
			if(sqlQueryObject!=null){
				this.log.error("getListaRichiestePendenti error SQL["+sqlQueryObject.toString()+"]",e);
				throw new DriverMonitoraggioException("getListaRichiestePendenti error SQL["+sqlQueryObject.toString()+"]: "+e.getMessage(),e);
			}else{
				this.log.error("getListaRichiestePendenti error",e);
				throw new DriverMonitoraggioException("getListaRichiestePendenti error: "+e.getMessage(),e);
			}
		}finally{
			// ripristino valori offset/limit
			search.setLimit(oldLimit);
			search.setOffset(oldOffset);
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){}
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception e){}
			try{
				if(this.datasource!=null && con!=null)
					con.close();
			}catch(Exception e){}
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
			if (this.datasource!=null)
				con = this.datasource.getConnection();
			else
				con = this.globalConnection;
			if(con==null)
				throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");

			java.sql.Timestamp data_registrazione_limite = null;
			//	FILTRO d) Soglia (ora_registrazione piu' vecchia di NOW-search.getSoglia()) Soglia in Minuti
			if(search.getSoglia()!=-1){
				data_registrazione_limite = new java.sql.Timestamp(DateManager.getTimeMillis()-(search.getSoglia()*1000*60));
			}

			List<BustaSoggetto> filtroSoggetti = search.getSoggettoList();

			// Query
			sqlQueryObject = this.newSQLQueryObjectListaRichiestePendenti(search);
			pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			int param_index = 0;
			if(search.getSoglia()!=-1)
				pstmt.setTimestamp(++param_index, data_registrazione_limite);
			if(filtroSoggetti!=null){
				for(int k=0; k<filtroSoggetti.size(); k++){

					// fruitore
					pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());

					// OR

					// erogatore
					pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());
				}
			}
			rs = pstmt.executeQuery();
			List<Messaggio> msgs = new ArrayList<Messaggio>();

			this.log.debug("query["+sqlQueryObject+"] listaMessaggi");
			this.log.debug("Soglia ["+search.getSoglia()+"] ["+data_registrazione_limite+"] listaMessaggi");
			
			while(rs.next()){
				Messaggio m = new Messaggio();
				// id
				m.setIdMessaggio(rs.getString("id_messaggio"));
				// Dettaglio
				Dettaglio dettaglio = new Dettaglio();
				dettaglio.setIdModulo(rs.getString("proprietario"));
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
				if(ConsegnaContenutiApplicativi.ID_MODULO.equals(rs.getString("proprietario"))){
					m.setStato(StatoMessaggio.CONSEGNA);
				}else if(InoltroBuste.ID_MODULO.equals(rs.getString("proprietario"))){
					m.setStato(StatoMessaggio.SPEDIZIONE);
				}else if(InoltroRisposte.ID_MODULO.equals(rs.getString("proprietario"))){
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
					this.log.debug("Raccolgo informazioni dei servizi applicativi...");
					String sqlQuerySA = "SELECT * FROM "+GestoreMessaggi.MSG_SERVIZI_APPLICATIVI +
							" WHERE ID_MESSAGGIO=?";
					pstmt = con.prepareStatement(sqlQuerySA);
					pstmt.setString(1, msgs.get(i).getIdMessaggio());
					rs = pstmt.executeQuery();
					Vector<ServizioApplicativoConsegna> sconsegna = new Vector<ServizioApplicativoConsegna>();
					while(rs.next()){
						ServizioApplicativoConsegna datiConsegna = new ServizioApplicativoConsegna();
						if(rs.getInt("SBUSTAMENTO_SOAP")==1)
							datiConsegna.setSbustamentoSoap(true);
						else
							datiConsegna.setSbustamentoSoap(false);
						if(rs.getInt("INTEGRATION_MANAGER")==1)
							datiConsegna.setAutorizzazioneIntegrationManager(true);
						else
							datiConsegna.setAutorizzazioneIntegrationManager(false);
						datiConsegna.setNome(rs.getString("SERVIZIO_APPLICATIVO"));
						datiConsegna.setTipoConsegna(rs.getString("TIPO_CONSEGNA"));
						datiConsegna.setErroreProcessamento(rs.getString("ERRORE_PROCESSAMENTO"));
						sconsegna.add(datiConsegna);
					}
					rs.close();
					pstmt.close();

					if(sconsegna.size()>0){
						msgs.get(i).getDettaglio().setServizioApplicativoConsegnaList(sconsegna);
					}

				}

				if(msgs.get(i)!=null &&  msgs.get(i).getBustaInfo()!=null &&
						StatoMessaggio.SPEDIZIONE.equals(msgs.get(i).getStato())){
					// Prendo dettaglio AttesaRiscontro
					if(search.getBusta()!=null && search.getBusta().isAttesaRiscontro()){
						msgs.get(i).getBustaInfo().setAttesaRiscontro(true);
					}else{
						this.log.debug("Raccolgo informazioni per attesa riscontro...");
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
				this.log.error("getListaRichiestePendenti error SQL["+sqlQueryObject.toString()+"]",e);
				throw new DriverMonitoraggioException("getListaRichiestePendenti error SQL["+sqlQueryObject.toString()+"]: "+e.getMessage(),e);
			}else{
				this.log.error("getListaRichiestePendenti error",e);
				throw new DriverMonitoraggioException("getListaRichiestePendenti error: "+e.getMessage(),e);
			}
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){}
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception e){}
			try{
				if(this.datasource!=null && con!=null)
					con.close();
			}catch(Exception e){}
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
			if (this.datasource!=null)
				con = this.datasource.getConnection();
			else
				con = this.globalConnection;
			if(con==null)
				throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");

			// Raccolta valori chiave
			// TIPO
			String tipoMessaggio = tipo;
			// Ricerco tipoMessaggio se non presenti 
			if(tipoMessaggio==null){
				this.log.debug("Ricerco tipo messaggio...");
				FilterSearch filtro = new FilterSearch();
				filtro.setIdMessaggio(idMessaggio);
				List<Messaggio> lista = this.getListaRichiestePendenti(filtro);
				if(lista==null || lista.size()<=0){
					return false; // messaggio non presente
				}
				if(lista.get(0).getDettaglio()==null ||
						lista.get(0).getDettaglio().getTipo()==null){
					throw new Exception("Tipo messaggio non identificato per l'id: "+idMessaggio);
				}
				tipoMessaggio = lista.get(0).getDettaglio().getTipo();
			}
			this.log.debug("Messaggio con id("+idMessaggio+") da eliminare possiede tipo: "+tipoMessaggio);

			if(Costanti.OUTBOX.equals(tipoMessaggio)){
				// Elimino eventuali riscontri
				String sqlQuery = "DELETE FROM "+Costanti.RISCONTRI_DA_RICEVERE + " WHERE ID_MESSAGGIO=?";
				pstmt = con.prepareStatement(sqlQuery);
				pstmt.setString(1, idMessaggio);
				int operation = pstmt.executeUpdate();
				if(operation>0){
					this.log.debug("Attesa riscontro per "+idMessaggio+" eliminato");
				}else{
					this.log.debug("Attesa riscontro per "+idMessaggio+" non esistente");
				}
				pstmt.close();

				// Elimino eventuali asincroni
				sqlQuery = "DELETE FROM "+Costanti.PROFILO_ASINCRONO + " WHERE ID_MESSAGGIO=? AND TIPO=?";
				pstmt = con.prepareStatement(sqlQuery);
				pstmt.setString(1, idMessaggio);
				pstmt.setString(2, tipoMessaggio);
				operation = pstmt.executeUpdate();
				if(operation>0){
					this.log.debug("ProfiloAsincrono per "+idMessaggio+" eliminato");
				}else{
					this.log.debug("ProfiloAsincrono per "+idMessaggio+" non esistente");
				}
				pstmt.close();
			}


			// Elimino messaggio
			String sqlQuery = "UPDATE "+GestoreMessaggi.MESSAGGI + " SET proprietario='"+TimerGestoreMessaggi.ID_MODULO+"' WHERE proprietario<>'"+TimerGestoreMessaggi.ID_MODULO+"' AND ID_MESSAGGIO=?  AND tipo=?";
			pstmt = con.prepareStatement(sqlQuery);
			pstmt.setString(1, idMessaggio);
			pstmt.setString(2, tipoMessaggio);
			int operation = pstmt.executeUpdate();
			boolean result = false;
			if(operation>0){
				result = true;
				this.log.debug("Messaggio "+idMessaggio+" eliminato");
			}else{
				this.log.debug("Messaggio "+idMessaggio+" non esistente");
			}
			pstmt.close();
			return result;

		}catch(Exception e){
			this.log.error("deleteMessaggio error",e);
			throw new DriverMonitoraggioException("deleteMessaggio error: "+e.getMessage());
		}finally{
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception e){}
			try{
				if(this.datasource!=null && con!=null)
					con.close();
			}catch(Exception e){}
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
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ISQLQueryObject sqlQueryObject = null;
		try{
			// Ottengo connessione
			if (this.datasource!=null)
				con = this.datasource.getConnection();
			else
				con = this.globalConnection;
			if(con==null)
				throw new Exception("Connection non ottenuta dal datasource["+this.datasource+"]");

			java.sql.Timestamp data_registrazione_limite = null;
			//	FILTRO d) Soglia (ora_registrazione piu' vecchia di NOW-search.getSoglia()) Soglia in Minuti
			if(search.getSoglia()!=-1){
				data_registrazione_limite = new java.sql.Timestamp(DateManager.getTimeMillis()-(search.getSoglia()*1000*60));
			}
			// Query
			sqlQueryObject = this.newSQLQueryObjectListaRichiestePendenti(search);
			pstmt = con.prepareStatement(sqlQueryObject.createSQLQuery());
			int param_index = 0;
			if(search.getSoglia()!=-1)
				pstmt.setTimestamp(++param_index, data_registrazione_limite);
			List<BustaSoggetto> filtroSoggetti = search.getSoggettoList();
			if(filtroSoggetti!=null){
				for(int k=0; k<filtroSoggetti.size(); k++){

					// fruitore
					pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());

					// OR

					// erogatore
					pstmt.setString(++param_index, filtroSoggetti.get(k).getTipo());
					pstmt.setString(++param_index, filtroSoggetti.get(k).getNome());
				}
			}
			rs = pstmt.executeQuery();
			Vector<Messaggio> msgs = new Vector<Messaggio>();

			this.log.debug("query["+sqlQueryObject+"] listaMessaggi");
			this.log.debug("Soglia ["+search.getSoglia()+"] ["+data_registrazione_limite+"] listaMessaggi");

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

			long numeroMsgEliminati = 0;
			while(msgs.size()>0){
				Messaggio msgForDelete = msgs.remove(0);
				if(this.deleteMessaggio(msgForDelete.getIdMessaggio(), msgForDelete.getDettaglio().getTipo()))
					numeroMsgEliminati++;
			}
			this.log.debug("eliminati "+numeroMsgEliminati+" messaggi");

			return numeroMsgEliminati;

		}catch(Exception e){
			if(sqlQueryObject!=null){
				this.log.error("deleteRichiestePendenti error SQL["+sqlQueryObject.toString()+"]",e);
				throw new DriverMonitoraggioException("deleteRichiestePendenti error SQL["+sqlQueryObject.toString()+"]: "+e.getMessage(),e);
			}else{
				this.log.error("deleteRichiestePendenti error",e);
				throw new DriverMonitoraggioException("deleteRichiestePendenti error: "+e.getMessage(),e);
			}
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){}
			try{
				if(pstmt!=null)
					pstmt.close();
			}catch(Exception e){}
			try{
				if(this.datasource!=null && con!=null)
					con.close();
			}catch(Exception e){}
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
		if( (search.getMessagePattern()!=null) && ("".equals(search.getMessagePattern())==false) ){
			sqlQueryObject.addFromTable(GestoreMessaggi.DEFINIZIONE_MESSAGGI);
		}
		if( (search.getBusta()!=null) && ("".equals(search.getBusta())==false) ){
			sqlQueryObject.addFromTable(Costanti.REPOSITORY);
			if(search.getBusta().isAttesaRiscontro()){
				sqlQueryObject.addFromTable(Costanti.RISCONTRI_DA_RICEVERE);
			}
		}else if(search.getSoggettoList()!=null && search.getSoggettoList().size()>0){
			sqlQueryObject.addFromTable(Costanti.REPOSITORY);
		}

		// Condizione per legare le tabelle
		String legameTabelleSQL_MSG_ID = GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+GestoreMessaggi.DEFINIZIONE_MESSAGGI+".ID_MESSAGGIO";
		String legameTabelleSQL_MSG_TIPO = GestoreMessaggi.MESSAGGI+".tipo="+GestoreMessaggi.DEFINIZIONE_MESSAGGI+".tipo";
		String legameTabelleSQL_BUSTA_ID = GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+Costanti.REPOSITORY+".ID_MESSAGGIO";
		String legameTabelleSQL_BUSTA_TIPO = GestoreMessaggi.MESSAGGI+".tipo="+Costanti.REPOSITORY+".tipo";
		String legameTabelleSQL_RISCONTRO = GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO="+Costanti.RISCONTRI_DA_RICEVERE+".ID_MESSAGGIO";
		if( (search.getMessagePattern()!=null) && ("".equals(search.getMessagePattern())==false) ){
			sqlQueryObject.addWhereCondition(true,legameTabelleSQL_MSG_ID,legameTabelleSQL_MSG_TIPO);
		}
		if(search.getBusta()!=null){
			sqlQueryObject.addWhereCondition(true,legameTabelleSQL_BUSTA_ID,legameTabelleSQL_BUSTA_TIPO);
		}else if(search.getSoggettoList()!=null && search.getSoggettoList().size()>0){
			sqlQueryObject.addWhereCondition(true,legameTabelleSQL_BUSTA_ID,legameTabelleSQL_BUSTA_TIPO);
		}
		if(search.getBusta()!=null && search.getBusta().isAttesaRiscontro()){
			sqlQueryObject.addWhereCondition(legameTabelleSQL_RISCONTRO);
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
			sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"tipo");
			sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"proprietario");
			sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"ORA_REGISTRAZIONE");
			sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"ERRORE_PROCESSAMENTO");		
			sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,"CORRELAZIONE_APPLICATIVA");
			if(this.properties!=null){
				for (int i = 0; i < this.properties.size(); i++) {
					String key = this.properties.get(i);
					sqlQueryObject.addSelectField(GestoreMessaggi.MESSAGGI,key);
				}
			}
			if( (search.getMessagePattern()!=null) && ("".equals(search.getMessagePattern())==false) ){
				// Tabella OpenSPCoop.sql.DefinizioneMessaggi
// Queste due colonne non servono: altrimenti si ottiene un errore column ambiguously defined.
// se in futuro serviranno, utilizzare degli alias
//				sqlQueryObject.addSelectField(GestoreMessaggi.DEFINIZIONE_MESSAGGI,"ID_MESSAGGIO");
//				sqlQueryObject.addSelectField(GestoreMessaggi.DEFINIZIONE_MESSAGGI,"tipo");
				sqlQueryObject.addSelectField(GestoreMessaggi.DEFINIZIONE_MESSAGGI,"MSG_BYTES");
			}	
			if(search.getBusta()!=null){
				// Tabella Libreria.sql.Repository
// Queste due colonne non servono: altrimenti si ottiene un errore column ambiguously defined.
// se in futuro serviranno, utilizzare degli alias
//				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"ID_MESSAGGIO");
//				sqlQueryObject.addSelectField(Costanti.REPOSITORY,"tipo");
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
//				sqlQueryObject.addSelectField(Costanti.RISCONTRI_DA_RICEVERE,"ID_MESSAGGIO");
			}

		}

		// Where Logico Operator
		sqlQueryObject.setANDLogicOperator(true);

		// Order By OraRegistrazione
		if(statoRichieste==false){
			sqlQueryObject.addOrderBy(GestoreMessaggi.MESSAGGI+".ORA_REGISTRAZIONE");
			sqlQueryObject.setSortType(false); // DESC
		}

		// Proprietario non deve essere GestoreMessaggi, a meno che il filtro non richieda proprio questo
		if( (search.getStato()==null) || ("".equals(search.getStato())) || ((StatoMessaggio.CANCELLATO.equals(search.getStato())==false)) )
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".proprietario<>'"+TimerGestoreMessaggi.ID_MODULO+"'");

		// Filtri:

		// a) StatoMessaggio
		if( (search.getStato()!=null) && ("".equals(search.getStato())==false) ){
			if(StatoMessaggio.CONSEGNA.equals(search.getStato())){
				sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".proprietario='"+ConsegnaContenutiApplicativi.ID_MODULO+"'");
			}else if(StatoMessaggio.SPEDIZIONE.equals(search.getStato())){
				sqlQueryObject.addWhereCondition(false,
						GestoreMessaggi.MESSAGGI+".proprietario='"+InoltroBuste.ID_MODULO+"'",
						GestoreMessaggi.MESSAGGI+".proprietario='"+InoltroRisposte.ID_MODULO+"'");
			}else if(StatoMessaggio.CANCELLATO.equals(search.getStato())){
				sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".proprietario='"+TimerGestoreMessaggi.ID_MODULO+"'");
			}else if(StatoMessaggio.PROCESSAMENTO.equals(search.getStato())){
				sqlQueryObject.addWhereCondition(true, GestoreMessaggi.MESSAGGI+".proprietario<>'"+InoltroBuste.ID_MODULO+"'",
						GestoreMessaggi.MESSAGGI+".proprietario<>'"+InoltroRisposte.ID_MODULO+"'",
						GestoreMessaggi.MESSAGGI+".proprietario<>'"+ConsegnaContenutiApplicativi.ID_MODULO+"'");
			}else{
				throw new SQLQueryObjectException("Stato per filtro non conosciuto: "+search.getStato());
			}
		}

		// b) idMessaggio e propertiesRicerca
		if( (search.getIdMessaggio()!=null) && ("".equals(search.getIdMessaggio())==false) ){
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".ID_MESSAGGIO='"+search.getIdMessaggio()+"'");
		}
		if( (search.getProprietaList()!=null) && (search.getProprietaList().size()>0) ){
			List<Proprieta> proprietaList = search.getProprietaList();
			for (Proprieta proprieta : proprietaList) {
				sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+"."+proprieta.getNome()+"='"+proprieta.getValore()+"'");	
			}
		}

		// c) MessagePattern e CorrelazioneApplicativa
		if( (search.getMessagePattern()!=null) && ("".equals(search.getMessagePattern())==false) ){
			sqlQueryObject.addWhereLikeCondition(GestoreMessaggi.DEFINIZIONE_MESSAGGI+".MSG_BYTES",search.getMessagePattern(),true,true);
		}
		if( (search.getCorrelazioneApplicativa()!=null) && ("".equals(search.getCorrelazioneApplicativa())==false) ){
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".CORRELAZIONE_APPLICATIVA='"+search.getCorrelazioneApplicativa()+"'");
		}

		// d) Soglia (ora_registrazione piu' vecchia di NOW-search.getSoglia())
		if(search.getSoglia()!=-1){
			sqlQueryObject.addWhereCondition(GestoreMessaggi.MESSAGGI+".ora_registrazione<=?");
		}

		if(statoRichieste==false){
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
				if( (search.getBusta().getMittente().getTipo()!=null) && ("".equals(search.getBusta().getMittente().getTipo())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_MITTENTE='"+search.getBusta().getMittente().getTipo()+"'");
				}
				if( (search.getBusta().getMittente().getNome()!=null) && ("".equals(search.getBusta().getMittente().getNome())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".MITTENTE='"+search.getBusta().getMittente().getNome()+"'");
				}
			}

			// destinatario
			if(search.getBusta().getDestinatario()!=null){
				if( (search.getBusta().getDestinatario().getTipo()!=null) && ("".equals(search.getBusta().getDestinatario().getTipo())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_DESTINATARIO='"+search.getBusta().getDestinatario().getTipo()+"'");
				}
				if( (search.getBusta().getDestinatario().getNome()!=null) && ("".equals(search.getBusta().getDestinatario().getNome())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".DESTINATARIO='"+search.getBusta().getDestinatario().getNome()+"'");
				}
			}

			// servizio
			if(search.getBusta().getServizio()!=null){
				if( (search.getBusta().getServizio().getTipo()!=null) && ("".equals(search.getBusta().getServizio().getTipo())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_SERVIZIO='"+search.getBusta().getServizio().getTipo()+"'");
				}
				if( (search.getBusta().getServizio().getNome()!=null) && ("".equals(search.getBusta().getServizio().getNome())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".SERVIZIO='"+search.getBusta().getServizio().getNome()+"'");
				}
			}

			// azione
			if( (search.getBusta().getAzione()!=null) && ("".equals(search.getBusta().getAzione())==false) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".AZIONE='"+search.getBusta().getAzione()+"'");
			}

			// profiloCollaborazione
			if( (search.getBusta().getProfiloCollaborazione()!=null) && ("".equals(search.getBusta().getProfiloCollaborazione())==false) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".PROFILO_DI_COLLABORAZIONE='"+search.getBusta().getProfiloCollaborazione()+"'");
			}

			// riferimentoMessaggio
			if( (search.getBusta().getRiferimentoMessaggio()!=null) && ("".equals(search.getBusta().getRiferimentoMessaggio())==false) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".RIFERIMENTO_MESSAGGIO='"+search.getBusta().getRiferimentoMessaggio()+"'");
			}

			// Collaborazione
			if( (search.getBusta().getCollaborazione()!=null) && ("".equals(search.getBusta().getCollaborazione())==false) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".COLLABORAZIONE='"+search.getBusta().getCollaborazione()+"'");
			}

		}

		if(search.getSoggettoList()!=null && search.getSoggettoList().size()>0){

			List<BustaSoggetto> filtroSoggetti = search.getSoggettoList();
			StringBuffer query = new StringBuffer();
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
		if( (search.getIdMessaggio()!=null) && ("".equals(search.getIdMessaggio())==false) ){
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
				if( (search.getBusta().getMittente().getTipo()!=null) && ("".equals(search.getBusta().getMittente().getTipo())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_MITTENTE='"+search.getBusta().getMittente().getTipo()+"'");
				}
				if( (search.getBusta().getMittente().getNome()!=null) && ("".equals(search.getBusta().getMittente().getNome())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".MITTENTE='"+search.getBusta().getMittente().getNome()+"'");
				}
			}

			// destinatario
			if(search.getBusta().getDestinatario()!=null){
				if( (search.getBusta().getDestinatario().getTipo()!=null) && ("".equals(search.getBusta().getDestinatario().getTipo())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_DESTINATARIO='"+search.getBusta().getDestinatario().getTipo()+"'");
				}
				if( (search.getBusta().getDestinatario().getNome()!=null) && ("".equals(search.getBusta().getDestinatario().getNome())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".DESTINATARIO='"+search.getBusta().getDestinatario().getNome()+"'");
				}
			}

			// servizio
			if(search.getBusta().getServizio()!=null){
				if( (search.getBusta().getServizio().getTipo()!=null) && ("".equals(search.getBusta().getServizio().getTipo())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".TIPO_SERVIZIO='"+search.getBusta().getServizio().getTipo()+"'");
				}
				if( (search.getBusta().getServizio().getNome()!=null) && ("".equals(search.getBusta().getServizio().getNome())==false) ){
					sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".SERVIZIO='"+search.getBusta().getServizio().getNome()+"'");
				}
			}

			// azione
			if( (search.getBusta().getAzione()!=null) && ("".equals(search.getBusta().getAzione())==false) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".AZIONE='"+search.getBusta().getAzione()+"'");
			}

			// profiloCollaborazione
			if( (search.getBusta().getProfiloCollaborazione()!=null) && ("".equals(search.getBusta().getProfiloCollaborazione())==false) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".PROFILO_DI_COLLABORAZIONE='"+search.getBusta().getProfiloCollaborazione()+"'");
			}

			// riferimentoMessaggio
			if( (search.getBusta().getRiferimentoMessaggio()!=null) && ("".equals(search.getBusta().getRiferimentoMessaggio())==false) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".RIFERIMENTO_MESSAGGIO='"+search.getBusta().getRiferimentoMessaggio()+"'");
			}

			// Collaborazione
			if( (search.getBusta().getCollaborazione()!=null) && ("".equals(search.getBusta().getCollaborazione())==false) ){
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY+".COLLABORAZIONE='"+search.getBusta().getCollaborazione()+"'");
			}

		}

		return sqlQueryObject;
	}
}
