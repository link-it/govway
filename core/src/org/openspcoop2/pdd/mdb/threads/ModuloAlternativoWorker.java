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


import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.EsitoLib;
import org.openspcoop2.pdd.mdb.GenericLibException;
import org.openspcoop2.pdd.mdb.GenericMessage;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.utils.date.DateManager;

/**
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class ModuloAlternativoWorker implements IWorker {

		 /**
		 * serialVersionUID
		 */
		protected static final long serialVersionUID = 1L;


		/* ********  F I E L D S  P R I V A T I S T A T I C I  ******** */

		/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
		protected String ID_MODULO="ModuloAlternativo";
			
		/** ConfigurazionePdDReader */
		protected ConfigurazionePdDReader configurazionePdDReader;
		/** Properties Reader */
		protected ThreadsImplProperties propertiesReader;
		/** RegistroServiziReader */
		protected OpenSPCoop2Properties oSPCpropertiesReader;
		/** RegistroServiziReader */
		protected RegistroServiziManager registroServiziReader;
		/** DBManager */
		protected DBManager dbManager;
		/** Logger */
		protected Logger log;

		MessageIde ide = null;
		
		public ModuloAlternativoWorker(String idModulo, MessageIde ide){
			this.ide = ide;
			this.ID_MODULO = idModulo;
		}
		
		protected void init() throws Exception{
			try {
				this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
				initReaders();  
				initDBManager();
			}catch(Exception e){
				throw new Exception("[ModuloAlternativoWorker.init] " + e.getMessage() );
			}
		}
			
		/** Crea un Messaggio a partire da un MessageIde e dai dati nel db */
		protected MessaggioSerializzato ricostruisciMessaggio() throws Exception{
		MessaggioSerializzato messaggioSerializzato;
		if (this.ide.getMsg_bytes()==null) System.out.println("Buffer di byte nullo");
			ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream ( this.ide.getMsg_bytes()) );
			messaggioSerializzato = (MessaggioSerializzato) in.readObject();
			in.close();
			return messaggioSerializzato;
		}	
		
		@Override
		public void run() {
			try {
				init();
			} catch (Exception e) {
				this.log.error(this.ID_MODULO+"Worker init abortito a causa di: "+e);
				e.printStackTrace();
				return;
			}
			
			MessaggioSerializzato msgSerial = null;
			
			try {
				msgSerial = ricostruisciMessaggio();
			}catch (Exception e) {
				this.log.error(this.ID_MODULO+"Worker ricostruisci messaggio abortito a causa di: "+e);
				return;
			}

			try {
				EsitoLib esito = onMessage(msgSerial.getMsg(), msgSerial.getIdMessaggio());

				if (esito.isEsitoInvocazione())		
					gestisciOK();
				else 
					gestisciNOK();
			
			} catch (Exception e1) {
				this.log.error(this.ID_MODULO+ e1.getMessage());
			}
		}

		
		protected void gestisciOK() throws Exception{

			Resource resourceDB= null;
			Connection connectionDB = null;
			
			try {
				resourceDB= initResource();
				connectionDB = initConnection(resourceDB,false);
				this.log.info(this.ID_MODULO+": connessione ottenuta");
			}catch (Exception e) {
				this.log.error(this.ID_MODULO+": impossibile collegarsi al db: +");
				throw new Exception(this.ID_MODULO+": impossibile collegarsi al db: " + e.getMessage());
			}
			
			String queryProprietario = "SELECT proprietario, scheduling, scheduling_time, redelivery_delay, redelivery_count, id_messaggio, tipo FROM MESSAGGI WHERE id_messaggio=? AND tipo= ? FOR UPDATE";
			PreparedStatement ps = null;
			ResultSet rs = null;
			
			try{
				ps = connectionDB.prepareStatement(queryProprietario, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				ps.setString(1, this.ide.getIdMessaggio());
				ps.setString(2, this.ide.getTipo());
				rs = ps.executeQuery();
				
				/* Viene letto il proprietario al termine della chiamata di libreria. Se il proprietario
				 * non e' stato modificato significa che il messaggio non dovra' piu' essere pescato da 
				 * nessuno dei thread in seguito ad un errore nel connettore. Il messaggio verra' preso da
				 * un timer.
				 */
	
				while (rs.next()){
					String proprietario = rs.getString(1);
					
					if (proprietario.equalsIgnoreCase( this.ID_MODULO ))
						rs.updateInt("scheduling", 2);
					
					else {
						rs.updateInt("scheduling", 0);
						rs.updateTimestamp("scheduling_time", DateManager.getTimestamp());
						rs.updateInt("redelivery_count", 0);
					}
					
					rs.updateRow();
				}
				
				connectionDB.commit();
						
			/**
			String query = "UPDATE MESSAGGI set " +
					"scheduling=0, scheduling_time=?, redelivery_count=0 where " +
					"id_messaggio=? AND tipo= ?";
			
			PreparedStatement ps = null;
			try {
				ps = connectionDB.prepareStatement(query);
				ps.setTimestamp(1, DateManager.getTimestamp());
				ps.setString(2, this.ide.getIdMessaggio());
				ps.setString(3, this.ide.getTipo());
				ps.executeUpdate();
				connectionDB.commit();
				System.out.println(this.ID_MODULO + "Lib eseguita correttamente, rimetto scheduling a 0 per il messaggio: " + this.ide.getIdMessaggio()  + " | "+ this.ide.getTipo());
			 */
				
				
			}catch (Exception e) {
				System.out.println(e);
				connectionDB.rollback();
			}finally{
				if (rs!=null) rs.close();
				if (ps!=null) ps.close();
				releaseResource(resourceDB);
				if(!connectionDB.isClosed())
					this.log.error(this.ID_MODULO+": gestisciOK: db non ha rilasciato il db");
			}
		}
		
		protected void gestisciNOK() throws Exception{
			
			Resource resourceDB= null;
			Connection connectionDB = null;
			try {
				resourceDB= initResource();
				connectionDB = initConnection(resourceDB, false);
		
			}catch (Exception e) {
				this.log.error(this.ID_MODULO+": impossibile collegarsi al db: +");
				throw new Exception(this.ID_MODULO+".gestisciNOK: impossibile collegarsi al db: " + e.getMessage());
			}
			
			long redelivery_delay = this.propertiesReader.getRedeliveryDelay();
			//Timestamp ts = ide.getRedelivery_delay();
			int redelivery_count = this.ide.getRedelivery_count();
			
			String query = "UPDATE MESSAGGI set scheduling=0, redelivery_count=?, redelivery_delay= ?, scheduling_time=? where " +
					"id_messaggio=? AND tipo= ?";

			PreparedStatement ps = null;

			redelivery_count ++;
			System.out.println("La proprieta' redelivery_delay vale: " + redelivery_delay);
			Timestamp ts = new Timestamp ( DateManager.getTimestamp().getTime() + redelivery_delay );
			
			try {
				ps = connectionDB.prepareStatement(query);
				ps.setInt(1,redelivery_count);
				ps.setTimestamp(2, ts);
				ps.setTimestamp(3, DateManager.getTimestamp());
				ps.setString(4, this.ide.getIdMessaggio());
				ps.setString(5, this.ide.getTipo());
				ps.executeUpdate();
				connectionDB.commit();
				System.out.println(this.ID_MODULO + ".gestisciNOK: chiamata alla libreria dell'MDB fallita " +
						"rimetto scheduling a 0 per il messaggio: " + this.ide.getIdMessaggio()  + " | "+ this.ide.getTipo() +		
						 " settato redelivery_delay a: " + ts);
			
			}catch (Exception e) {
				this.log.error(e.getMessage());
				connectionDB.rollback();
			}finally{
				if(ps!=null) ps.close();
				
				try {
					if (!connectionDB.isClosed()){
						releaseResource(resourceDB);
					}
					if (!connectionDB.isClosed())
						System.out.println(this.ID_MODULO+": gestisciNOK: non riesco a chiudere il db");
				}catch	 (Exception e2) {
					System.out.println(e2);
				}
				
			}
		}
		

		protected void initReaders() throws GenericLibException{
			try{
				this.propertiesReader = ThreadsImplProperties.getInstance();
				this.oSPCpropertiesReader = OpenSPCoop2Properties.getInstance();
				this.configurazionePdDReader = ConfigurazionePdDReader.getInstance();
				this.registroServiziReader = RegistroServiziManager.getInstance();
			}catch (Exception e) {
				throw new GenericLibException("Riscontrato Errore durante l'inizializzazione del Reader della Configurazione");
			}
		}
		
		protected void initDBManager() throws GenericLibException {
			try {
				this.dbManager = DBManager.getInstance();	
			} catch (Exception e) {
				if(this.log!=null)
					this.log.error("Riscontrato errore durante l'inizializzazione del Thread ModuloAlternativoWorker: "
							+ e.getMessage());
				throw new GenericLibException("Riscontrato errore durante l'inizializzazione del DBmanager");
			}
		}
		
		/** Inizializza la risorsa che rappresenta la connessione al db */
		protected Resource initResource() throws Exception{
			Resource resourceDB = null;
			try{
				resourceDB = this.dbManager.getResource(this.oSPCpropertiesReader.getIdentitaPortaDefault(null), this.ID_MODULO, null);
			}catch(Exception e){
				throw new Exception("Impossibile ottenere una Risorsa dal DBManager",e);
			}
			if(resourceDB==null)
				throw new Exception("Impossibile ottenere una Resource dal DBManager"); 
			return resourceDB;
		}


		protected Connection initConnection(Resource resourceDB, boolean autocommit) throws Exception {
			Connection connectionDB = null;	
			try{
				connectionDB = (Connection) resourceDB.getResource();
				connectionDB.setAutoCommit(autocommit);
			}catch(Exception e){
				this.log.error("Riscontrato errore durante la richiesta di una connessione al DB: "+e.getMessage());
				throw new Exception("Impossibile ottenere una Connessione dal DBManager");	
			}
			return connectionDB;
		}

		/** rilascia la risorsa che rappresenta la connessione al db */
		protected void releaseResource(Resource resourceDB) {
			if(resourceDB != null)
				this.dbManager.releaseResource(this.oSPCpropertiesReader.getIdentitaPortaDefault(null), this.ID_MODULO, resourceDB);
		}
		
		
		
		protected abstract EsitoLib onMessage(GenericMessage message, String idMessaggio) throws OpenSPCoopStateException;
		
	}
