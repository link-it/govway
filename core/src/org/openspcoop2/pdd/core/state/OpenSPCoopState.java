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

package org.openspcoop2.pdd.core.state;

import java.sql.Connection;
import java.util.List;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.DBConsegneMessageBoxManager;
import org.openspcoop2.pdd.config.DBConsegnePreseInCaricoManager;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.GenericMessage;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * Oggetto che rappresenta lo stato di una richiesta/risposta all'interno della PdD
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class OpenSPCoopState implements IOpenSPCoopState {
	
	/* ---------- Logger ---------*/
	protected Logger logger = null;
	
	/* ---------- Connessione al database ---------------- */
	protected OpenSPCoopStateDBManager dbManager = null;
	/** DBManager */
	protected DBManager dbManager_runtime= null;
	/** DBManager */
	protected DBConsegnePreseInCaricoManager dbManager_consegnePreseInCarico = null;
	/** DBManager */
	protected DBConsegneMessageBoxManager dbManager_consegneMessageBox = null;
	/** Resource */
	protected Resource resourceDB = null;
	/** Connessione al database */
	protected Connection connectionDB = null;
	/** Connessione inizializzata */
	protected boolean connessioneInizializzata = false;
	
	/* ----------- Altre informazioni ------------*/
	/** Identita PdD */
	protected IDSoggetto identitaPdD = null;
	/** Identita Modulo */
	protected String idModulo = null;
	/** GenericMessage LIB */
	protected GenericMessage messageLib;
	/** Identificativo della sessione */
	protected String IDMessaggioSessione;
	
	/* ---------- Stato ------------ */
	protected StateMessage richiestaStato = null;
	protected StateMessage rispostaStato = null;
	
	/* ---------- Indicazione se lavorare come stateful (utilizzare la connessione) o come stateless (operazioni NOP) -------------- */
	protected boolean useConnection;
	
	
	
	
	
	
	/* ----------- Convertitori ------------*/
	public static OpenSPCoopStateless toStateless(OpenSPCoopStateful stateful,boolean useConnection){
		OpenSPCoopStateless stateless = new OpenSPCoopStateless();
		stateless.connectionDB = stateful.connectionDB;
		stateless.dbManager = stateful.dbManager;
		stateless.dbManager_runtime = stateful.dbManager_runtime;
		stateless.dbManager_consegnePreseInCarico = stateful.dbManager_consegnePreseInCarico;
		stateless.dbManager_consegneMessageBox = stateful.dbManager_consegneMessageBox;
		stateless.IDMessaggioSessione = stateful.IDMessaggioSessione;
		stateless.identitaPdD = stateful.identitaPdD;
		stateless.idModulo = stateful.idModulo;
		stateless.logger = stateful.logger;
		stateless.messageLib = stateful.messageLib;
		stateless.resourceDB = stateful.resourceDB;
		stateless.richiestaStato = stateful.richiestaStato;
		stateless.rispostaStato = stateful.rispostaStato;
		stateless.useConnection = useConnection;
		stateless.connessioneInizializzata = stateful.connessioneInizializzata;
		
		StatelessMessage tempRichiesta = new StatelessMessage(stateful.getConnectionDB(),stateful.logger);
		if(stateful.getStatoRichiesta()!=null) {
			tempRichiesta.setPreparedStatement(((StateMessage) stateful.getStatoRichiesta()).getPreparedStatement());
		}
		stateless.setStatoRichiesta(new StatelessMessage(tempRichiesta));
		
		StatelessMessage tempRisposta = new StatelessMessage(stateful.getConnectionDB(),stateful.logger);
		if(stateful.getStatoRisposta()!=null) {
			tempRisposta.setPreparedStatement(((StateMessage) stateful.getStatoRisposta()).getPreparedStatement());
		}
		stateless.setStatoRisposta(new StatelessMessage(tempRisposta));
		
		return stateless;
	}
	
	
	


	

	/* ----------- Init resource ------------*/

	public abstract void updateStatoRichiesta() throws UtilsException ;
	public abstract void updateStatoRisposta() throws UtilsException ;
		
	@Override
	public void initResource(IDSoggetto identitaPdD,String idModulo,String idTransazione)throws OpenSPCoopStateException{
		initResource(identitaPdD,idModulo,idTransazione, OpenSPCoopStateDBManager.runtime);
	}
	@Override
	public void initResource(IDSoggetto identitaPdD,String idModulo,String idTransazione, OpenSPCoopStateDBManager dbManager)throws OpenSPCoopStateException{
		
		this.dbManager = dbManager; // per update
		
		// Check parametri
		if(identitaPdD==null){
			throw new OpenSPCoopStateException("IdentitaPdD non presente");
		}
		this.identitaPdD = identitaPdD;
		if(idModulo==null){
			throw new OpenSPCoopStateException("IDModulo non presente");
		}
		this.idModulo = idModulo;
		
		// Logger
		this.logger = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		
		if(this.useConnection){
			
			// Get Connessione
			if(dbManager!=null) {
				switch (dbManager) {
				case runtime:
					this.dbManager_runtime = DBManager.getInstance();
					break;
				case smistatoreMessaggiPresiInCarico:
					this.dbManager_consegnePreseInCarico = DBConsegnePreseInCaricoManager.getInstanceSmistatore();
					break;
				case consegnePreseInCarico:
					this.dbManager_consegnePreseInCarico = DBConsegnePreseInCaricoManager.getInstanceRuntime();
					break;
				case messageBox:
					this.dbManager_consegneMessageBox = DBConsegneMessageBoxManager.getInstanceRuntime();
					break;
				}
			}
			else {
				this.dbManager_runtime = DBManager.getInstance();
			}
			try{
				if(this.dbManager_runtime!=null) {
					this.resourceDB = this.dbManager_runtime.getResource(identitaPdD,this.idModulo,idTransazione);
				}
				else if(this.dbManager_consegnePreseInCarico!=null) {
					this.resourceDB = this.dbManager_consegnePreseInCarico.getResource(identitaPdD,this.idModulo,idTransazione);
				}
				else {
					this.resourceDB = this.dbManager_consegneMessageBox.getResource(identitaPdD,this.idModulo,idTransazione);
				}
			}catch(Exception e){
				throw new OpenSPCoopStateException("Riscontrato errore durante la richiesta di una connessione al DB",e);
			}
			if(this.resourceDB==null){
				throw new OpenSPCoopStateException("Riscontrato errore durante la richiesta di una connessione al DB (Risorsa is null).");
			}
			if(this.resourceDB.getResource() == null){
					throw new OpenSPCoopStateException("Riscontrato errore durante la richiesta di una connessione al DB (Connessione is null)."); 
			}
			this.connectionDB = (Connection) this.resourceDB.getResource();
			
			// Stato
			try{
				this.updateStatoRichiesta();
				this.updateStatoRisposta();
			}catch(Exception e){
				this.logger.error("Update stato richiesta/risposta non riuscito",e);
				if(this.dbManager_runtime!=null) {
					this.dbManager_runtime.releaseResource(this.identitaPdD,this.idModulo,this.resourceDB);
				}
				else if(this.dbManager_consegnePreseInCarico!=null) {
					this.dbManager_consegnePreseInCarico.releaseResource(this.identitaPdD,this.idModulo,this.resourceDB);
				}
				else {
					this.dbManager_consegneMessageBox.releaseResource(this.identitaPdD,this.idModulo,this.resourceDB);
				}
				throw new OpenSPCoopStateException("Update stato richiesta/risposta non riuscito",e);
			}
			
			this.connessioneInizializzata = true;
		}
	} 
	
	@Override
	public void updateResource(String idTransazione) throws OpenSPCoopStateException{
		if( this.useConnection ){
			try{
				if(this.dbManager!=null) {
					switch (this.dbManager) {
					case runtime:
						if(this.dbManager_runtime==null) {
							this.dbManager_runtime = DBManager.getInstance();
						}
						break;
					case smistatoreMessaggiPresiInCarico:
						if(this.dbManager_consegnePreseInCarico==null) {
							this.dbManager_consegnePreseInCarico = DBConsegnePreseInCaricoManager.getInstanceSmistatore();
						}
						break;
					case consegnePreseInCarico:
						if(this.dbManager_consegnePreseInCarico==null) {
							this.dbManager_consegnePreseInCarico = DBConsegnePreseInCaricoManager.getInstanceRuntime();
						}
						break;
					case messageBox:
						if(this.dbManager_consegneMessageBox==null) {
							this.dbManager_consegneMessageBox = DBConsegneMessageBoxManager.getInstanceRuntime();
						}
						break;
					}
				}
				else {
					if(this.dbManager_runtime==null) {
						this.dbManager_runtime = DBManager.getInstance();
					}
				}
				
				if(this.dbManager_runtime!=null) {
					this.resourceDB = this.dbManager_runtime.getResource(this.identitaPdD,this.idModulo,idTransazione);
				}
				else if(this.dbManager_consegnePreseInCarico!=null) {
					this.resourceDB = this.dbManager_consegnePreseInCarico.getResource(this.identitaPdD,this.idModulo,idTransazione);
				}
				else {
					this.resourceDB = this.dbManager_consegneMessageBox.getResource(this.identitaPdD,this.idModulo,idTransazione);
				}
			}catch(Exception e){
				throw new OpenSPCoopStateException("Riscontrato errore durante la richiesta di una connessione al DB",e);
			}
			if(this.resourceDB==null){
				throw new OpenSPCoopStateException("Riscontrato errore durante la richiesta di una connessione al DB (Risorsa is null).");
			}
			if(this.resourceDB.getResource() == null){
				throw new OpenSPCoopStateException("Riscontrato errore durante la richiesta di una connessione al DB (Connessione is null)."); 
			}
			this.connectionDB = (Connection) this.resourceDB.getResource();
			
			this.richiestaStato.updateConnection(this.connectionDB);
			this.rispostaStato.updateConnection(this.connectionDB);
			
			this.connessioneInizializzata = true;
		}
	}
	
	@Override
	public void releaseResource() {
		if(this.useConnection && this.connessioneInizializzata){
			try{
				if(this.richiestaStato!=null && this.richiestaStato.getPreparedStatement()!=null){
					if(this.richiestaStato.getPreparedStatement().size()>0){
						List<String> l = this.richiestaStato.getPreparedStatement().keys();
						if(l!=null && !l.isEmpty()) {
							for (String key : l) {
								this.logger.error("PREPARED STATEMENT NON CHIUSA (RICHIESTA): "+key);
							}
						}
						this.richiestaStato.closePreparedStatement();
					}
				}
			}catch (Exception e) {	
				this.logger.error("Chiusure prepared statement della richiesta non riuscita",e);
			}
			try{
				if(this.rispostaStato!=null && this.rispostaStato.getPreparedStatement()!=null){
					if(this.rispostaStato.getPreparedStatement().size()>0){
						List<String> l = this.rispostaStato.getPreparedStatement().keys();
						if(l!=null && !l.isEmpty()) {
							for (String key : l) {
								this.logger.error("PREPARED STATEMENT NON CHIUSA (RISPOSTA): "+key);
							}
						}
						this.rispostaStato.closePreparedStatement();
					}
				}
			}catch (Exception e) {	
				this.logger.error("Chiusure prepared statement della risposta non riuscita",e);
			}
			try{
				if(this.resourceDB!=null){
					if(this.dbManager_runtime!=null) {
						this.dbManager_runtime.releaseResource(this.identitaPdD,this.idModulo,this.resourceDB);
					}
					else if(this.dbManager_consegnePreseInCarico!=null) {
						this.dbManager_consegnePreseInCarico.releaseResource(this.identitaPdD,this.idModulo,this.resourceDB);
					}
					else {
						this.dbManager_consegneMessageBox.releaseResource(this.identitaPdD,this.idModulo,this.resourceDB);
					}
				}
				if(this.richiestaStato!=null) {
					this.richiestaStato.updateConnection(null);
				}
				if(this.rispostaStato!=null) {
					this.rispostaStato.updateConnection(null);
				}
			}catch (Exception e) {	
				this.logger.error("Rilasciate risorse con errore: "+e.getMessage(),e);
				OpenSPCoop2Logger.getLoggerOpenSPCoopConsole().error("Rilasciate risorse con errore: "+e.getMessage());
			}
			this.connessioneInizializzata = false;
		}
	}
	
	@Override
	public boolean resourceReleased(){
		return !this.connessioneInizializzata;
	}
	
	public void forceFinallyReleaseResource() {
		if(this.connessioneInizializzata){
			this.useConnection = true; // force
			//System.out.println("Force close ...");
			this.releaseResource();
		}
	}
	
	
	
	
	
	
	/* ----------- Commit / Close ------------*/
	
	@Override
	public void commit() throws OpenSPCoopStateException{
		if(this.useConnection){
			if(   (this.richiestaStato!=null && this.richiestaStato.getPreparedStatement()!=null && this.richiestaStato.getPreparedStatement().size()>0)  ||
				  (this.rispostaStato!=null && this.rispostaStato.getPreparedStatement()!=null && this.rispostaStato.getPreparedStatement().size()>0)
			){
				try{
					this.connectionDB.setAutoCommit(false);
					
//					System.out.println("PREPARED STATEMENT (RICHIESTA)");
//					Enumeration<String> en = this.richiestaStato.getPreparedStatement().keys();
//					while(en.hasMoreElements()){
//						String key = en.nextElement();
//						System.out.println("PREPARED STATEMENT (RICHIESTA): "+key);
//					}			
					if(this.richiestaStato!=null) {
						this.richiestaStato.executePreparedStatement();
					}
					
//					System.out.println("PREPARED STATEMENT (RISPOSTA)");
//					en = this.rispostaStato.getPreparedStatement().keys();
//					while(en.hasMoreElements()){
//						String key = en.nextElement();
//						System.out.println("PREPARED STATEMENT (RISPOSTA): "+key);
//					}	
					if(this.rispostaStato!=null) {
						this.rispostaStato.executePreparedStatement();
					}
					
					this.connectionDB.commit();
					this.connectionDB.setAutoCommit(true);
					
				}catch (Exception e) {
					// Chiudo eventuali prepared statement non chiuse
					try{
						if(this.richiestaStato!=null) {
							this.richiestaStato.closePreparedStatement();
						}
					}catch(Exception eClose){}
					try{
						if(this.rispostaStato!=null) {
							this.rispostaStato.closePreparedStatement();
						}
					}catch(Exception eClose){}
					//  Rollback quanto effettuato (se l'errore e' avvenuto sul commit, o prima nell'execute delle PreparedStatement)
					try{
						this.connectionDB.rollback();
					}catch(Exception er){}
					// Ripristino connessione
					try{
						this.connectionDB.setAutoCommit(true);
					}catch(Exception er){}
					// Rilancio l'eccezione: sara' catturata dal catch seguente, che chiudera' le preparedStatement, 
					// chiude la connessione al DB ed effettua il Rollback dell'MDB(a meno del profilo sincrono).
					throw new OpenSPCoopStateException(e.getMessage(),e);
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	/* ----------- GET / SET ------------*/
	@Override
	public GenericMessage getMessageLib() {
		return this.messageLib;
	}

	public void setMessageLib(GenericMessage messageLib) {
		this.messageLib = messageLib;
	}

	@Override
	public String getIDMessaggioSessione() {
		return this.IDMessaggioSessione;
	}

	public void setIDMessaggioSessione(String idSessione) {
		this.IDMessaggioSessione = idSessione;
	}
	
	@Override
	public IState getStatoRichiesta(){
		return this.richiestaStato;
	}
	@Override
	public void setStatoRichiesta(IState statoRichiesta){
		this.richiestaStato = (StateMessage) statoRichiesta;
	}
	@Override
	public IState getStatoRisposta(){
		return this.rispostaStato;
	}
	@Override
	public void setStatoRisposta(IState statoRisposta){
		this.rispostaStato = (StateMessage) statoRisposta;
	}
	public Connection getConnectionDB() {
		return this.connectionDB;
	}
	public void setConnectionDB(Connection connectionDB) {
		this.connectionDB = connectionDB;
	}
	
	public boolean isUseConnection() {
		return this.useConnection;
	}

	public void setUseConnection(boolean useConnection) {
		this.useConnection = useConnection;
	}

}
