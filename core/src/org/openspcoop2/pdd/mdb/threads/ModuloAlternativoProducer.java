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


package org.openspcoop2.pdd.mdb.threads;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.threshold.ThreadsUtils;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.GenericLibException;
import org.openspcoop2.utils.date.DateManager;

/**
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModuloAlternativoProducer implements IProducer {

	protected String ID_MODULO="ModuloAlternativo";
	protected BlockingQueue <IWorker> coda;
	
	protected String cluster_id = null;
	
	/** Properties Reader */
	private ThreadsImplProperties propertiesReader;
	private OpenSPCoop2Properties oSPCpropertiesReader;
	/** Logger */
	private Logger log;
	/** DBManager */
	private DBManager dbManager;
	
	private long attesa_producer=100;
	
	public ModuloAlternativoProducer(String idModulo, BlockingQueue <IWorker> coda){
		try {
			this.ID_MODULO = idModulo;
			initLettoreProperties();
			initLogger();
			initDBManager();
			this.attesa_producer = this.propertiesReader.getAttesaProducer();
			this.cluster_id = this.oSPCpropertiesReader.getClusterId(true);
		}catch (Exception e) {
			this.log.error(this.ID_MODULO+ " Errore in fase di inizializzazione " + e);
		}
		this.coda = coda;
	}
	
	/**
	 * Ottiene una connessione al db dal resource manager, crea un PreparedStatement e, a intervalli di tempo
	 * regolari setta nel prepared statement creato il limite ai risultati della query in relazione ai posti
	 * liberi nella coda di thread, e l'ora attuale per evitare di trattare messaggi che devono essere trattati
	 * successivamente.
	 * Una volta settato il PreparedStatement esegue la query e crea gli workers per la gestione del messaggio.
	 * 
	 */
	@Override
	public void run() {
		Resource resourceDB = null;
		Connection connectionDB = null;
		
		if (this.propertiesReader == null)System.out.println("Lettore ThreadProp null");
		if (this.oSPCpropertiesReader == null)System.out.println("Lettore OpenSPCoopProp null");
		if (this.dbManager == null ) System.out.println("DBManager null");
		if (this.log == null) System.out.println("Logger null");
		
		// creazione di risorsa e connessione bisogna settare AUTOCOMMIT FALSE E SERIALIZABLE
		try {
			resourceDB = initResource();
			connectionDB = initConnection(resourceDB, false);
		}catch(Exception e){
			this.log.error(this.ID_MODULO +".cercaNuoviMessaggi: Impossibile connettersi al db: " + e);
			return;
		}
		
		PreparedStatement ps = prepareStatement(connectionDB);
	
		System.out.println(this.ID_MODULO + " producer attivato ");
		while (!this.stop){
			int postiLiberi = this.coda.remainingCapacity();
			if (postiLiberi > 0){ 
				Vector <MessageIde> messaggiTrovati = EseguiCercaNuoviMessaggi(connectionDB, ps, postiLiberi);
			//	Vector <MessageIde> messaggiTrovati = cercaNuoviMessaggiOld(postiLiberi);
				if (messaggiTrovati != null && messaggiTrovati.size()>0){
					//aggiunge i task per la gestione dei messaggi alla coda
					creaWorkers(messaggiTrovati);
				}
			}
			ThreadsUtils.attesa(this.attesa_producer);
		}
		
		try		{
			if(ps!=null)ps.close();
			releaseResource(resourceDB);
		}catch(Exception e){
			this.log.error(this.ID_MODULO + " " +e);
		}
		
		this.log.info(this.ID_MODULO + "Producer: Fermato");
		System.out.println(this.ID_MODULO + " Producer fermato");
	}

	public void creaWorkers(Vector <MessageIde> messaggiTrovati){
		//implementare nelle sottoclassi
	}
	
	/** 
	 * Prepara lo statement per la ricerca di messaggi da gestire.
	 * @param connectionDB
	 * @return Il preparedStatement per la ricerca
	 */
	private PreparedStatement prepareStatement(Connection connectionDB){
		PreparedStatement ps = null;
		String preparedQuery = 
			"SELECT id_messaggio, tipo, scheduling, redelivery_count, redelivery_delay," +
			" ora_registrazione, scheduling_time, msg_bytes" +
			" FROM "+ GestoreMessaggi.MESSAGGI +
			" WHERE	proprietario= ?" +
			" AND scheduling=0" +
			" AND redelivery_delay < ?" + 
			" AND redelivery_COUNT < ?" +
			" AND cluster_id= ?" +
			" ORDER BY ora_registrazione" +
			" LIMIT ? FOR UPDATE";
			
			try {
				ps = connectionDB.prepareStatement(preparedQuery,
						ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE);
				
				ps.setString(1, this.ID_MODULO);
				//settare solo prima dell'esecuzione: ps.setTimestamp(2, DateManager.getTimestamp());
				ps.setInt(3, this.propertiesReader.getRedeliveryCount());
				ps.setString(4, this.cluster_id);
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			return ps;
	}
	
	/**
	 * Setta il limite della query e l'ora corrente nel campo scheduling time, ed esegue la query.
	 * Marca come schedulati nel db i risultati della query. 
	 * la gestione del messaggio
	 * @param connectionDB
	 * @param ps
	 * @param limiteRisultati
	 * @return MessageIde
	 */
	protected Vector <MessageIde> EseguiCercaNuoviMessaggi(Connection connectionDB, PreparedStatement ps, int limiteRisultati) {
		Vector <MessageIde> result = new Vector <MessageIde> ();
		ResultSet rs = null;
		try {
			ps.setTimestamp(2, DateManager.getTimestamp());
			ps.setInt(5,limiteRisultati);
			rs = ps.executeQuery();
	
		
		while (rs.next()){
			System.out.println(this.ID_MODULO + ".EseguiCercaNuoviMess: trovato: " + 
					rs.getString("id_messaggio") + "/ sched: " + rs.getInt("scheduling")
					+ " redelivery_delay: " + rs.getTimestamp("redelivery_delay") );
			System.out.println("....");
			
			rs.updateInt("scheduling", 1); 
			rs.updateTimestamp("scheduling_time", DateManager.getTimestamp());
			Timestamp temp = new Timestamp(DateManager.getTimestamp().getTime() + 1000);
			System.out.println(temp);
			rs.updateTimestamp("redelivery_delay", temp);
			rs.updateRow();
	
			MessageIde ide = new MessageIde();
			ide.setIdMessaggio(rs.getString(1));
			ide.setTipo(rs.getString(2));
			ide.setRedelivery_count(rs.getInt(4));
			byte [] data = rs.getBytes("msg_bytes"); if ( data!=null) ide.setMsg_bytes(data);
			result.add(ide);
	
			System.out.println(this.ID_MODULO + ".EseguiCercaNuoviMess: modificato: " + 
					rs.getString("id_messaggio") + "/ sched: " + rs.getInt("scheduling")
					+ " redelivery_delay: " + rs.getTimestamp("redelivery_delay") );
			System.out.println("messaggio inserito nei risultati da processare");
			System.out.println("/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/");
			System.out.println("");
		}
		
		connectionDB.commit();
		if (result!=null && result.size()>0)
		System.out.println(this.ID_MODULO + ".EseguiCercaNuoviMess: Commit eseguito su "
				+ result.size() + " messaggi");
		
		return result;
		
		}catch (Exception e) {
			System.out.println(e);
			this.log.error(this.ID_MODULO +":" + e.getMessage());
			try {
				connectionDB.rollback();
				System.out.println(this.ID_MODULO + ".EseguiCercaNuoviMess: Rollback eseguito su "
						+ result.size() + " messaggi");
			}catch(SQLException e1){
				this.log.error(this.ID_MODULO +":Fallito Rollback" + e.getMessage());
			}
			return null;
		}
		
		finally {
			try {
				if (rs!=null) rs.close();
			}catch (Exception e) {
				this.log.error(this.ID_MODULO +": non riesco a rilasciare la connessione "+ e.getMessage());
			}
		}
	}
	
	/**
	 * interroga il Db alla ricerca di nuovi messaggi da trattare 
	 * 
	 * @param limiteRisultati
	 * @param connectionDB
	 * @return un vettore di MessageIde che contiene gli identificatori dei messaggi schedulati
	 */
	protected Vector <MessageIde> cercaNuoviMessaggi(int limiteRisultati, Connection connectionDB) {	
		String preparedQuery = 
			"SELECT id_messaggio, tipo, scheduling, redelivery_count, redelivery_delay," +
			" ora_registrazione, scheduling_time, msg_bytes" +
			" FROM "+ GestoreMessaggi.MESSAGGI +
			" WHERE	proprietario= ?" +
			" AND scheduling='0'" +
			" AND redelivery_delay < ?" + 
			" AND redelivery_COUNT < ?" +
			" AND cluster_id= ?" +
			" ORDER BY ora_registrazione" +
			" LIMIT " + limiteRisultati + " FOR UPDATE";
	
		Vector <MessageIde> result = new Vector <MessageIde> ();
		
		ResultSet rs = null;
		PreparedStatement ps = null;

		// ricerca di nuovi messaggi
		try {
			ps = connectionDB.prepareStatement(preparedQuery,
					ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			ps.setString(1, this.ID_MODULO);
			ps.setTimestamp(2, DateManager.getTimestamp());
			ps.setInt(3, this.propertiesReader.getRedeliveryCount());
			ps.setString(4, this.cluster_id);
		
			rs = ps.executeQuery();
			
			while (rs.next()){
				
//				System.out.println(ID_MODULO + " trovato record: "+"[IdMessaggio:]:"+rs.getString(1)+
//						" [scheduling]: "+rs.getInt(3));
								
				rs.updateInt("scheduling", 1); 
				rs.updateTimestamp("scheduling_time", DateManager.getTimestamp());
				Timestamp ts = new Timestamp (System.currentTimeMillis() + 5000 ) ;
				rs.updateTimestamp("redelivery_delay", ts);
				rs.updateRow();
				
//				System.out.println(ID_MODULO + " modificato record: "+"[IdMessaggio:]:"+rs.getString(1)+
//						" [scheduling]: "+rs.getInt(3));
				
				MessageIde ide = new MessageIde();
				ide.setIdMessaggio(rs.getString(1));
				ide.setTipo(rs.getString(2));
				ide.setRedelivery_count(rs.getInt(4));
				if ( rs.getTimestamp(5)!=null )	ide.setRedelivery_delay(rs.getTimestamp(5));
				if ( rs.getBytes("msg_bytes")!=null) ide.setMsg_bytes(rs.getBytes("msg_bytes"));
				result.add(ide);
			}
			
			connectionDB.commit();
			
			return result;
			
		}catch (Exception e){
			System.out.println(e);
			this.log.error(this.ID_MODULO +":" + e.getMessage());
			try {
				connectionDB.rollback();
			}catch(SQLException e1){
				this.log.error(this.ID_MODULO +":Fallito Rollback" + e.getMessage());
			}
			return null;
		}
		
		finally {
			try {
				if (rs!=null) rs.close();
				if (ps!=null) ps.close();
			}catch (Exception e) {
				this.log.error(this.ID_MODULO +": non riesco a rilasciare la connessione "+ e.getMessage());
			}
		}

	}

	
	
	
	/** cerca nel db nuovi messaggi e li marca per la gestione */
	protected Vector <MessageIde> cercaNuoviMessaggiOld(int limiteRisultati) {

		String preparedQuery = 
			"SELECT id_messaggio, tipo, scheduling, redelivery_count, redelivery_delay," +
			" ora_registrazione, scheduling_time, msg_bytes" +
			" FROM "+ GestoreMessaggi.MESSAGGI +
			" WHERE	proprietario= ?" +
			" AND scheduling=0" +
			" AND redelivery_delay < ?" + 
			" AND redelivery_COUNT < ?" +
			" AND cluster_id= ?" +
			" ORDER BY ora_registrazione" +
			" LIMIT ? FOR UPDATE";
		
		Resource resourceDB = null;
		Connection connectionDB = null;

		Vector <MessageIde> id_messaggi = new Vector <MessageIde> ();

		// creazione di risorsa e connessione bisogna settare AUTOCOMMIT FALSE E SERIALIZABLE
		try {
			resourceDB = initResource();
			connectionDB = initConnection(resourceDB, false);
		}catch(Exception e){
			System.out.println(this.ID_MODULO +": Impossibile connettersi al db: " + e);
			return null;
		}

		// ricerca di nuovi messaggi
		

		PreparedStatement ps = null;
		ResultSet rs = null;

		
		// gestione result set
		try {
			ps=connectionDB.prepareStatement(preparedQuery);
			ps.setString(1, this.ID_MODULO);
			ps.setTimestamp(2, DateManager.getTimestamp());
			ps.setInt(3, this.propertiesReader.getRedeliveryCount());
			ps.setString(4, this.cluster_id);
			ps.setInt(5, limiteRisultati);
			rs = ps.executeQuery();
			while (rs.next()){
				System.out.println(this.ID_MODULO + "trovato: " + rs.getString("id_messaggio") + "/ sched: " + rs.getInt("scheduling"));
				MessageIde ide = new MessageIde();
				ide.setIdMessaggio(rs.getString(1));
				ide.setTipo(rs.getString(2));
				ide.setRedelivery_count(rs.getInt(4));
				//Timestamp ts = rs.getTimestamp(5); if ( ts!=null )	ide.setRedelivery_delay(ts);
				byte [] data = rs.getBytes("msg_bytes"); if ( data!=null) ide.setMsg_bytes(data);
				id_messaggi.add(ide);
			}
			rs.close();
			ps.close();
		}catch (SQLException e){
			this.log.error(this.ID_MODULO +":" + e.getMessage());
			releaseResource(resourceDB);
			return null;
		}

		try {
			//imposta 'scheduling lock' sui messaggi
			if (id_messaggi != null && id_messaggi.size() > 0) {
				String preparedUpdate = "UPDATE "+GestoreMessaggi.MESSAGGI+ 
				" set scheduling=1 where id_messaggio = ? and tipo = ? ";
				PreparedStatement ps3 = connectionDB.prepareStatement(preparedUpdate);
				for (int i=0; i< id_messaggi.size(); i++) {
					try{
						ps3.setString(1, id_messaggi.elementAt(i).getIdMessaggio());
						ps3.setString(2, id_messaggi.elementAt(i).getTipo());
						ps3.executeUpdate();
						System.out.print(this.ID_MODULO + "set sched=1 al msg: " + id_messaggi.elementAt(i).getIdMessaggio() + " | " + id_messaggi.elementAt(i).getTipo() + ".......");
					}catch (SQLException e1) {
						this.log.error(this.ID_MODULO +": SqlExecption, eseguo rollback " + e1);
						connectionDB.rollback();
					}
				}
				ps3.close();			
				connectionDB.commit();
				System.out.println(this.ID_MODULO + ".................................................... COMMIT ESEGUITO");
			}
		}

		catch (Exception e) {
			this.log.error(this.ID_MODULO +": errore " + e);	
			try {
				connectionDB.rollback();
			} catch (SQLException e1) {
				this.log.error(this.ID_MODULO +": rollback fallito");
			}
			return null;
		}

		finally {
			try {
				releaseResource(resourceDB);
				if (!connectionDB.isClosed())
					this.log.error(this.ID_MODULO +": CercaNuoviMessaggi: non riesco a chiudere il db");
			}catch (Exception e) {
				this.log.error(this.ID_MODULO +": non riesco a rilasciare la connessione");
			}
		}
		return id_messaggi;
	}
	
	
	
	private void initLogger() {
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	}

	private void initLettoreProperties() throws GenericLibException{
		try{
			this.propertiesReader = ThreadsImplProperties.getInstance();
			this.oSPCpropertiesReader = OpenSPCoop2Properties.getInstance();
		}catch (Exception e) {
			throw new GenericLibException("Riscontrato Errore durante l'inizializzazione del Reader delle Properties");
		}
	}

	private void initDBManager() throws GenericLibException {
		try {
			this.dbManager = DBManager.getInstance();	
		} catch (Exception e) {
			if(this.log!=null)
				this.log.error("Riscontrato errore durante l'inizializzazione del Thread InoltroBusteProducer: "
						+ e.getMessage());
			throw new GenericLibException("Riscontrato errore durante l'inizializzazione del DBManager di " + this.ID_MODULO);
		}
	}
	
	/** Inizializza la risorsa che rappresenta la connessione al db */
	private Resource initResource() throws Exception{
		Resource resourceDB = null;
		try{
			resourceDB = this.dbManager.getResource(this.oSPCpropertiesReader.getIdentitaPortaDefault(null), this.ID_MODULO, null);
		}catch(Exception e){
			throw new Exception("Impossibile ottenere una Risorsa dal DBManager",e);
		}
		if(resourceDB==null){
			System.out.println(this.ID_MODULO + "producer initResource fallita");
			throw new Exception("Impossibile ottenere una Resource dal DBManager");
		}
			
		return resourceDB;
	}


	private Connection initConnection(Resource resourceDB, boolean autocommit) throws Exception {
		Connection connectionDB = null;	
		try{
			connectionDB = (Connection) resourceDB.getResource();
			connectionDB.setAutoCommit(autocommit);
		}catch(Exception e){
			this.log.error("Riscontrato errore durante la richiesta di una connessione al DB: "+e.getMessage());
			System.out.println(this.ID_MODULO + "producer initResource fallita");
			throw new Exception("Impossibile ottenere una Connessione dal DBManager");	
		}
		return connectionDB;
	}

	/** rilascia la risorsa che rappresenta la connessione al db */
	private void releaseResource(Resource resourceDB) {
		if(resourceDB != null)
			this.dbManager.releaseResource(this.oSPCpropertiesReader.getIdentitaPortaDefault(null), this.ID_MODULO, resourceDB);
	}
	
		
    // VARIABILE PER STOP
	private boolean stop = false;
	@Override
	public boolean isStop() {	return this.stop;	}
	@Override
	public void setStop(boolean stop) { 	this.stop = stop;	}
}
