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
package org.openspcoop2.pdd.core.transazioni;

import java.sql.Connection;
import java.util.ArrayList;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.dao.ITransazioneService;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.pdd.config.DBTransazioniManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

/**     
 * RepositoryGestioneStateful
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RepositoryGestioneStateful {

	private static final String ID_MODULO = "RepositoryGestioneStateful";
	
	private static Boolean gestioneStatefulAbilitata = null;
	
	private static String tipoDatabase = null; //tipoDatabase
	private static DAOFactory daoFactory = null;
    private static ServiceManagerProperties daoFactoryServiceManagerPropertiesTransazioni = null;
    private static Logger log = null;
    private static Logger daoFactoryLoggerTransazioni = null;
	
	private static OpenSPCoop2Properties openspcoopProperties = null;
	private static boolean debug;
	
	private static GestoreTransazioniStateful gestoreTransazioniStateful = null;
	private static synchronized void init() throws TransactionStatefulNotSupportedException{
		if(gestioneStatefulAbilitata==null){
			try{
				openspcoopProperties = OpenSPCoop2Properties.getInstance();
				gestioneStatefulAbilitata = openspcoopProperties.isTransazioniStatefulEnabled();
			
				tipoDatabase = openspcoopProperties.getDatabaseType();
				//System.out.println("DS["+this.datasource+"] TIPODB["+this.tipoDatabase+"]");
	
				if(tipoDatabase==null){
					throw new Exception("Tipo Database non definito");
				}
	
				openspcoopProperties = OpenSPCoop2Properties.getInstance();
	
				debug = openspcoopProperties.isTransazioniStatefulDebug();
				
				DAOFactoryProperties daoFactoryProperties = null;
				log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniStateful(debug);
				daoFactoryLoggerTransazioni = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniStatefulSql(debug);
				daoFactory = DAOFactory.getInstance(daoFactoryLoggerTransazioni);
				daoFactoryProperties = DAOFactoryProperties.getInstance(daoFactoryLoggerTransazioni);
				daoFactoryServiceManagerPropertiesTransazioni = daoFactoryProperties.getServiceManagerProperties(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance());
				daoFactoryServiceManagerPropertiesTransazioni.setShowSql(debug);	
				daoFactoryServiceManagerPropertiesTransazioni.setDatabaseType(DBTransazioniManager.getInstance().getTipoDatabase());

			}catch(Exception e){
				throw new TransactionStatefulNotSupportedException("Inizializzazione risorse database non riuscita: "+e.getMessage(),e);
			}
			try{
				gestoreTransazioniStateful = new GestoreTransazioniStateful(log, daoFactoryLoggerTransazioni,
						tipoDatabase,
						debug);
			}catch(Exception e){
				throw new TransactionStatefulNotSupportedException("Inizializzazione GestoreTransazioniStateful non riuscita: "+e.getMessage(),e);
			}
		}
	}
	private static void checkGestioneAbilitata() throws TransactionStatefulNotSupportedException{
		if(gestioneStatefulAbilitata==null){
			init();
		}
		if(gestioneStatefulAbilitata==false){
			throw new TransactionStatefulNotSupportedException("Non abilitata la gestione delle transazioni stateful");
		}
	}
	
	
	// TODO: Gestione realizzata su database!!!!
	// Questa implementazione puo' causare OUT OF MEMORY
	private static ArrayList<StatefulObject> repository = 
		new ArrayList<StatefulObject>();
			
	private static void invokeGestoreTransazioniStateful(StatefulObject s,TransactionDB transactionDB)throws TransactionStatefulNotSupportedException {
		Resource dbResource = null;
		DBTransazioniManager dbManager = DBTransazioniManager.getInstance();
		IDSoggetto idDominio = openspcoopProperties.getIdentitaPortaDefault(s.getProtocollo());
		Connection con = null;
		try{
			dbResource = dbManager.getResource(idDominio, ID_MODULO, null);
			con = (Connection) dbResource.getResource();	
			
			boolean autoCommit = false;
			con.setAutoCommit(autoCommit);
			
			org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
					(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), 
							con, autoCommit,
							daoFactoryServiceManagerPropertiesTransazioni, daoFactoryLoggerTransazioni);
			jdbcServiceManager.getJdbcProperties().setShowSql(debug);
			ITransazioneService transazioneService = jdbcServiceManager.getTransazioneService();
			
			gestoreTransazioniStateful.gestioneStatefulObject(transazioneService, con, s, transactionDB);
			con.commit();
		}catch(Exception e){
			try{
				con.rollback();
			}catch(Exception eRollback){}
			throw new TransactionStatefulNotSupportedException("Errore durante la gestione della transazione stateful",e);
		}finally{
			try{
				con.setAutoCommit(true);
			}catch(Exception eRollback){}
			try {
				dbManager.releaseResource(idDominio, ID_MODULO, dbResource);
			} catch (Exception e) {}
		}
	}
	
	private static TransactionDB invokeGestoreTransazioniStateful_readTransactionDB(String protocollo, String idTransazione)throws TransactionStatefulNotSupportedException {
		Resource dbResource = null;
		DBTransazioniManager dbManager = DBTransazioniManager.getInstance();
		IDSoggetto idDominio = openspcoopProperties.getIdentitaPortaDefault(protocollo);
		Connection con = null;
		try{
			dbResource = dbManager.getResource(idDominio, ID_MODULO+".read", null);
			con = (Connection) dbResource.getResource();
			
			org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
					(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) daoFactory.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), con,
							daoFactoryServiceManagerPropertiesTransazioni, daoFactoryLoggerTransazioni);
			jdbcServiceManager.getJdbcProperties().setShowSql(debug);
			ITransazioneService transazioneService = jdbcServiceManager.getTransazioneService();
			
			return gestoreTransazioniStateful.readTransazione(transazioneService, idTransazione);
		}catch(Exception e){
			throw new TransactionStatefulNotSupportedException("Errore durante la gestione della transazione stateful",e);
		}finally{
			try {
				dbManager.releaseResource(idDominio, ID_MODULO, dbResource);
			} catch (Exception e) {}
		}
	}
	
	
	public static void addMessaggio(String idTransazione, Messaggio messaggio) throws TransactionStatefulNotSupportedException {
		
		checkGestioneAbilitata();
		
		StatefulObject s = new StatefulObject(messaggio.getProtocollo());
		s.setIdTransazione(idTransazione);
		s.setObject(messaggio);
		s.setType(StatefulObjectType.MESSAGGIO);
		
		// OUT OF MEMORY PROBLEM 
		//repository.add(s);
		invokeGestoreTransazioniStateful(s,null);

	}
	
	public static void addMsgDiagnostico(String idTransazione, MsgDiagnostico msgDiag) throws TransactionStatefulNotSupportedException {
		
		checkGestioneAbilitata();
		
		StatefulObject s = new StatefulObject(msgDiag.getProtocollo());
		s.setIdTransazione(idTransazione);
		s.setObject(msgDiag);
		s.setType(StatefulObjectType.MSGDIAGNOSTICO);
		
		// OUT OF MEMORY PROBLEM 
		//repository.add(s);
		invokeGestoreTransazioniStateful(s,null);

	}
	
	public static void addTraccia(String idTransazione, Traccia traccia) throws TransactionStatefulNotSupportedException {

		checkGestioneAbilitata();
		
		StatefulObject s = new StatefulObject(traccia.getProtocollo());
		s.setIdTransazione(idTransazione);
		s.setObject(traccia);
		s.setType(StatefulObjectType.TRACCIA);
		// OUT OF MEMORY PROBLEM 
		//repository.add(s);
		invokeGestoreTransazioniStateful(s,null);

	}
	
	public static void addOutRequestStatefulObject(String protocollo,String idTransazione, OutRequestStatefulObject object) throws TransactionStatefulNotSupportedException {
		
		checkGestioneAbilitata();
		
		StatefulObject s = new StatefulObject(protocollo);
		s.setIdTransazione(idTransazione);
		s.setObject(object);
		s.setType(StatefulObjectType.OUT_REQUEST_STATEFUL_OBJECT);
			
		TransactionDB tr = invokeGestoreTransazioniStateful_readTransactionDB(protocollo,idTransazione);
		// Se la transazione e' null, attendo 1 secondo e attendo se deve essere terminata la sua scrittura
		if(tr==null){
			Utilities.sleep(500);
			tr = invokeGestoreTransazioniStateful_readTransactionDB(protocollo,idTransazione);
		}
		if(tr==null){
			Utilities.sleep(500);
			tr = invokeGestoreTransazioniStateful_readTransactionDB(protocollo,idTransazione);
		}
		if(tr!=null){
			invokeGestoreTransazioniStateful(s,tr);
		}else{
			// OUT OF MEMORY PROBLEM 
			repository.add(s);
		}
	}
	
	public static void addInResponseStatefulObject(String protocollo,String idTransazione, InResponseStatefulObject object) throws TransactionStatefulNotSupportedException {
		
		checkGestioneAbilitata();
		
		StatefulObject s = new StatefulObject(protocollo);
		s.setIdTransazione(idTransazione);
		s.setObject(object);
		s.setType(StatefulObjectType.IN_RESPONSE_STATEFUL_OBJECT);
		
		TransactionDB tr = invokeGestoreTransazioniStateful_readTransactionDB(protocollo,idTransazione);
		// Se la transazione e' null, attendo 1 secondo e attendo se deve essere terminata la sua scrittura
		if(tr==null){
			Utilities.sleep(500);
			tr = invokeGestoreTransazioniStateful_readTransactionDB(protocollo,idTransazione);
		}
		if(tr==null){
			Utilities.sleep(500);
			tr = invokeGestoreTransazioniStateful_readTransactionDB(protocollo,idTransazione);
		}
		if(tr!=null){
			invokeGestoreTransazioniStateful(s,tr);
		}else{
			// OUT OF MEMORY PROBLEM 
			repository.add(s);
		}
	}
	
	public static void addObject(StatefulObject s) throws TransactionStatefulNotSupportedException {

		checkGestioneAbilitata();
		
		repository.add(s);
	}
	
	// NOTA: se si realizza questa versione su database mantenere il comportamento a LISTA
	// Si inserisce in fondo, si preleva in cima, tramite la order by
	
	public static int size(){
		return repository.size();
	}
	
	public static StatefulObject getObject(){
		return repository.get(0); // oggetto in testa alla coda
	}
	
	public static StatefulObject removeObject(){
		return repository.remove(0); // oggetto in testa alla coda
	}
}
