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



package org.openspcoop2.protocol.engine.driver;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * Sono inclusi i metodi per la gestione della consegna in ordine (sequenza).
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConsegnaInOrdine  {



	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** Se IState e' un'istanza di StatefulMessage possiede una Connessione SQL in autoCommit mode su cui effettuare query 
	 *  Altrimenti, e' un'istanza di StatelessMessage e nn necessita di connessioni */
	private IState state;
	
	private IProtocolFactory protocolFactory;

	

	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	public ConsegnaInOrdine(IState state,IProtocolFactory protocolFactory){
		this(state,Configurazione.getLibraryLog(),protocolFactory);
	}
	/**
	 * Costruttore. 
	 *
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	public ConsegnaInOrdine(IState state, Logger alog,IProtocolFactory protocolFactory){
		this.state = state;
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(ConsegnaInOrdine.class.getName());
		}
		this.protocolFactory = protocolFactory;
	}
	
	/**
	 * Aggiorna lo stato della Busta
	 * Utilizzato per reimpostare la connessione ??
	 * 
	 */
	public void updateState(IState state){
		this.state = state;
	}

	private static final String NOT_USED = ""; // azione null


	/**
	 * Ritorna l'identificativo di una collaborazione identificata da mittente/destinatario/servizio/azione.
	 * In caso la collaborazione non esiste, viene creata.
	 *  
	 * @param busta Busta su cui impostare la sequenza
	 * 
	 */
	public void setNextSequenza_daInviare(Busta busta)throws ProtocolException{

		if(this.state instanceof StateMessage) {
			
			StateMessage stateful = (StateMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			PreparedStatement pstmtInsert = null;
			String idCollaborazione = null;
			long sequenza = -1;
			try{	

				StringBuffer query = new StringBuffer();
				query.append("SELECT ID_COLLABORAZIONE,PROSSIMA_SEQUENZA FROM ");
				query.append(Costanti.SEQUENZA_DA_INVIARE);
				query.append(" WHERE MITTENTE=? AND TIPO_MITTENTE=? AND DESTINATARIO=? AND TIPO_DESTINATARIO=? AND SERVIZIO=? AND TIPO_SERVIZIO=? AND AZIONE=?");
				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,busta.getMittente());
				pstmt.setString(2,busta.getTipoMittente());
				pstmt.setString(3,busta.getDestinatario());
				pstmt.setString(4,busta.getTipoDestinatario());
				pstmt.setString(5,busta.getServizio());
				pstmt.setString(6,busta.getTipoServizio());
				if(busta.getAzione()!=null)
					pstmt.setString(7,busta.getAzione());
				else
					pstmt.setString(7,NOT_USED);

				// Esecuzione comando SQL
				rs = pstmt.executeQuery();		
				if(rs == null) {
					pstmt.close();
					throw new ProtocolException("RS NULL?");
				}		
				if(rs.next()){
					// Collaborazione preesistente
					idCollaborazione = rs.getString("ID_COLLABORAZIONE");
					sequenza=rs.getLong("PROSSIMA_SEQUENZA");

					// devo aggiornare il next sequence

					long next_sequenza = sequenza + 1;
					if(next_sequenza > Costanti.MAX_VALUE_SEQUENZA_COUNTER){
						next_sequenza = 1;
					} 

					rs.close();
					pstmt.close();

					// aggiornamento sequenza
					StringBuffer queryInsert = new StringBuffer();
					queryInsert.append("UPDATE ");
					queryInsert.append(Costanti.SEQUENZA_DA_INVIARE);
					queryInsert.append(" SET PROSSIMA_SEQUENZA = ? WHERE MITTENTE=? AND TIPO_MITTENTE=? AND DESTINATARIO=? AND TIPO_DESTINATARIO=? AND SERVIZIO=? AND TIPO_SERVIZIO=? AND AZIONE=?");
					pstmtInsert = connectionDB.prepareStatement(queryInsert.toString());
					pstmtInsert.setLong(1,next_sequenza);
					pstmtInsert.setString(2,busta.getMittente());
					pstmtInsert.setString(3,busta.getTipoMittente());
					pstmtInsert.setString(4,busta.getDestinatario());
					pstmtInsert.setString(5,busta.getTipoDestinatario());
					pstmtInsert.setString(6,busta.getServizio());
					pstmtInsert.setString(7,busta.getTipoServizio());
					if(busta.getAzione()!=null){
						pstmtInsert.setString(8,busta.getAzione());
					}else{
						pstmtInsert.setString(8,NOT_USED);
					}

					stateful.getPreparedStatement().put("UPDATE setNextSequenza_daInviare"+busta.getCollaborazione(), pstmtInsert);


				}else{

					// Nuova collaborazione
					idCollaborazione = busta.getID();
					sequenza=1;

					rs.close();
					pstmt.close();				

					StringBuffer queryInsert = new StringBuffer();
					queryInsert.append("INSERT INTO  ");
					queryInsert.append(Costanti.SEQUENZA_DA_INVIARE);
					queryInsert.append(" VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? )");
					pstmtInsert = connectionDB.prepareStatement(queryInsert.toString());
					pstmtInsert.setString(1,busta.getMittente());
					pstmtInsert.setString(2,busta.getTipoMittente());
					pstmtInsert.setString(3,busta.getDestinatario());
					pstmtInsert.setString(4,busta.getTipoDestinatario());
					pstmtInsert.setString(5,busta.getServizio());
					pstmtInsert.setString(6,busta.getTipoServizio());
					if(busta.getAzione()!=null){
						pstmtInsert.setString(7,busta.getAzione());
					}else{
						pstmtInsert.setString(7,NOT_USED);
					}
					pstmtInsert.setLong(8,2); // next sequence is 2
					pstmtInsert.setString(9,busta.getID()); // idCollaborazione is idRequest

					stateful.getPreparedStatement().put("INSERT setNextSequenza_daInviare"+busta.getCollaborazione(), pstmtInsert);


				}

				// impostazione busta
				busta.setSequenza(sequenza);
				busta.setCollaborazione(idCollaborazione);


			} catch(Exception e) {
				String errorMsg = "ConsegnaInOrdine, Errore durante la setNextSequenza_daInviare: "+e.getMessage();		
				try{
					if( rs != null )
						rs.close();
				} catch(Exception er) {}
				try{
					if( pstmt != null )
						pstmt.close();
				} catch(Exception er) {}
				try{
					if( pstmtInsert != null )
						pstmtInsert.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}

		}else{
			throw new ProtocolException("Metodo invocato con IState non valido");
		}
	}










	/**
	 * Ritorna true se la busta contiene una sequenza/collaborazione valida
	 *  
	 * @param busta Busta su cui effettuare la validazione
	 * @return una eccezione se la busta contiene una sequenza/collaborazione non valida
	 * 
	 */
	public Eccezione validazioneDatiConsegnaInOrdine(Busta busta, IProtocolFactory protocolFactory)throws ProtocolException{
		if(this.state instanceof StateMessage) {
			
			StateMessage stateful = (StateMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();
			
			// Check stateless
			boolean connessioneValida = false;
			try{
				if(stateful instanceof StatefulMessage)
					connessioneValida = true;
				else
					connessioneValida = ( (connectionDB!=null) && (!connectionDB.isClosed()) );
			}catch(Exception e){}
					
			if(connessioneValida && busta.getSequenza()!=-1 && busta.getCollaborazione()!=null){

				PreparedStatement pstmt = null;
				ResultSet rs = null;
				try{	

					if(busta.getID().equals(busta.getCollaborazione())){
						// busta Capostipite
						if(busta.getSequenza()!=1){
							this.log.debug("Riscontrato numero di sequenza diverso da 1, in una busta capostipite di una sequenza");
							return Eccezione.getEccezioneValidazione(ErroriCooperazione.CONSEGNA_IN_ORDINE_FUORI_SEQUENZA.getErroreCooperazione(), protocolFactory); 
						}
					}
					else{
						// busta non capostipite
						StringBuffer query = new StringBuffer();
						query.append("SELECT * FROM ");
						query.append(Costanti.SEQUENZA_DA_RICEVERE);
						query.append(" WHERE ID_COLLABORAZIONE=?");
						pstmt = connectionDB.prepareStatement(query.toString());
						pstmt.setString(1,busta.getCollaborazione());

						//	Esecuzione comando SQL
						rs = pstmt.executeQuery();		
						if(rs == null) {
							pstmt.close();
							throw new ProtocolException("RS NULL?");
						}	
						if(rs.next()){
							String tipoMittente = rs.getString("TIPO_MITTENTE");
							String mittente = rs.getString("MITTENTE");
							String tipoDestinatario = rs.getString("TIPO_DESTINATARIO");
							String destinatario = rs.getString("DESTINATARIO");
							String tipoServizio = rs.getString("TIPO_SERVIZIO");
							String servizio = rs.getString("SERVIZIO");
							String azione = rs.getString("AZIONE");
							rs.close();
							pstmt.close();

							//	Check di coerenza
							if( tipoMittente.equals(busta.getTipoMittente())==false){
								this.log.debug("Il tipo di mittente non rispetta quello atteso nella gestione della collaborazione con consegna in ordine.");
								return Eccezione.getEccezioneValidazione(ErroriCooperazione.CONSEGNA_IN_ORDINE_TIPO_MITTENTE_NON_VALIDO.getErroreCooperazione(), protocolFactory);
							}
							if( mittente.equals(busta.getMittente())==false){
								this.log.debug("Il mittente non rispetta quello atteso nella gestione della collaborazione con consegna in ordine.");
								return Eccezione.getEccezioneValidazione(ErroriCooperazione.CONSEGNA_IN_ORDINE_MITTENTE_NON_VALIDO.getErroreCooperazione(), protocolFactory);
							}
							if( tipoDestinatario.equals(busta.getTipoDestinatario())==false){
								this.log.debug("Il tipo di destinatario non rispetta quello atteso nella gestione della collaborazione con consegna in ordine.");
								return Eccezione.getEccezioneValidazione(ErroriCooperazione.CONSEGNA_IN_ORDINE_TIPO_DESTINATARIO_NON_VALIDO.getErroreCooperazione(), protocolFactory);
							}
							if( destinatario.equals(busta.getDestinatario())==false){
								this.log.debug("Il destinatario non rispetta quello atteso nella gestione della collaborazione con consegna in ordine.");
								return Eccezione.getEccezioneValidazione(ErroriCooperazione.CONSEGNA_IN_ORDINE_DESTINATARIO_NON_VALIDO.getErroreCooperazione(),protocolFactory);
							}
							if( tipoServizio.equals(busta.getTipoServizio())==false){
								this.log.debug("Il tipo di servizio non rispetta quello atteso nella gestione della collaborazione con consegna in ordine.");
								return Eccezione.getEccezioneValidazione(ErroriCooperazione.CONSEGNA_IN_ORDINE_TIPO_SERVIZIO_NON_VALIDO.getErroreCooperazione(), protocolFactory);
							}
							if( servizio.equals(busta.getServizio())==false){
								this.log.debug("Il servizio non rispetta quello atteso nella gestione della collaborazione con consegna in ordine.");
								return Eccezione.getEccezioneValidazione(ErroriCooperazione.CONSEGNA_IN_ORDINE_SERVIZIO_NON_VALIDO.getErroreCooperazione(), protocolFactory);
							}
							if( (azione==null && busta.getAzione()!=null) ||
									(azione.equals(busta.getAzione())==false) ){
								this.log.debug("L'azione non rispetta quello attesa nella gestione della collaborazione con consegna in ordine.");
								return Eccezione.getEccezioneValidazione(ErroriCooperazione.CONSEGNA_IN_ORDINE_AZIONE_NON_VALIDA.getErroreCooperazione(), protocolFactory);
							}
						}else{
							this.log.debug("Busta non capostipite che richiede funzionalità di consegna in ordine presenta una collaborazione non registrata per le funzioni di consegna in ordine");
							return Eccezione.getEccezioneValidazione(ErroriCooperazione.CONSEGNA_IN_ORDINE_COLLABORAZIONE_IN_BUSTA_NON_CAPOSTIPITE_SCONOSCIUTA.getErroreCooperazione(), protocolFactory);
						}

					}

				} catch(Exception e) {
					this.log.error("ERROR validazioneDatiConsegnaInOrdine ["+e.getMessage()+"]",e);
					try{
						if( rs != null )
							rs.close();
					} catch(Exception er) {}
					try{
						if( pstmt != null )
							pstmt.close();
					} catch(Exception er) {}
					throw new ProtocolException("ERROR validazioneDatiConsegnaInOrdine ["+e.getMessage()+"]",e);
				}

			}

			return null;
		}else{
			throw new ProtocolException("Metodo invocato con IState non valido");
		}
	}
	
	
	
	
	/**
	 * Ritorna true se la consegna deve essere effettuata, false altrimenti
	 *  
	 * @param busta Busta su cui gestire la sequenza
	 * @return true se la consegna deve essere effettuata, false altrimenti
	 * 
	 */
	public boolean isConsegnaInOrdine(Busta busta)throws ProtocolException{
		return isConsegnaInOrdine(busta, 60l, 100);
	}
	/**
	 * Ritorna true se la consegna deve essere effettuata, false altrimenti
	 *  
	 * @param busta Busta su cui gestire la sequenza
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * @return true se la consegna deve essere effettuata, false altrimenti
	 * 
	 */
	private long sequenzaAttesa = -1;
	public long getSequenzaAttesa() {
		return this.sequenzaAttesa;
	}
	public boolean isConsegnaInOrdine(Busta busta, long attesaAttiva, int checkInterval) throws ProtocolException{

		if(this.state instanceof StateMessage) {
			
			StateMessage stateMsg = (StateMessage)this.state;
			Connection connectionDB = stateMsg.getConnectionDB();

			//controllo che non abbia gia' gestito un numero di sequenza per la busta
			RepositoryBuste repository = new RepositoryBuste(stateMsg, this.log, true, this.protocolFactory);
			long sequenzaGestita = repository.getSequenzaFromInBox(busta.getID());
			if(sequenzaGestita == -2){
				return true; // posso consegnare, sicuramente l'ho gia' consegnata prima.
			}


			/*
	  	Viene realizzato con livello di isolamento SERIALIZABLE, per essere sicuri
	  	di creare sequenza crescenti.
			 */
			// setAutoCommit e livello Isolamento
			int oldTransactionIsolation = -1;
			try{
				oldTransactionIsolation = connectionDB.getTransactionIsolation();
				connectionDB.setAutoCommit(false);
				JDBCUtilities.setTransactionIsolationSerializable(Configurazione.getSqlQueryObjectType(), connectionDB);
			} catch(Exception er) {
				String errorMsg = "ConsegnaInOrdine, errore durante isConsegnaInOrdine(setIsolation): "+er.getMessage();		
				this.log.error(errorMsg,er);
				throw new ProtocolException(errorMsg,er);
			}


			boolean nextSequenceOK = false;
			boolean bustaInOrdine = false;

			long scadenzaWhile = DateManager.getTimeMillis() + attesaAttiva;

			while(nextSequenceOK==false && DateManager.getTimeMillis() < scadenzaWhile){

				PreparedStatement pstmt = null;
				ResultSet rs = null;
				try{	

					if(busta.getID().equals(busta.getCollaborazione())){
						// busta Capostipite

						bustaInOrdine = true;

					}else{
						// busta da controllare

						StringBuffer query = new StringBuffer();
						if(Configurazione.getSqlQueryObjectType()!=null){
							ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());
							sqlQueryObject.addSelectField("SEQUENZA_ATTESA");
							sqlQueryObject.addFromTable(Costanti.SEQUENZA_DA_RICEVERE);
							sqlQueryObject.addWhereCondition("ID_COLLABORAZIONE=?");
							sqlQueryObject.setANDLogicOperator(true);
							sqlQueryObject.setSelectForUpdate(true);
							query.append(sqlQueryObject.createSQLQuery());
						}
						else{
							query.append("SELECT SEQUENZA_ATTESA FROM ");
							query.append(Costanti.SEQUENZA_DA_RICEVERE);
							query.append(" WHERE ID_COLLABORAZIONE=? FOR UPDATE");
						}
						pstmt =  connectionDB.prepareStatement(query.toString());
						pstmt.setString(1,busta.getCollaborazione());

						//	Esecuzione comando SQL
						rs = pstmt.executeQuery();		
						if(rs == null) {
							pstmt.close();
							throw new ProtocolException("RS NULL?");
						}	
						if(rs.next()==false){
							throw new Exception("Informazioni su consegna in ordine non trovate, durante il check di una busta non capostipite");
						}

						this.sequenzaAttesa = rs.getLong("SEQUENZA_ATTESA");
						rs.close();
						pstmt.close();

						// se non e' la sequenza attesa, vado in congelamento
						if(this.sequenzaAttesa!=busta.getSequenza()){
							bustaInOrdine = false;
						}else{
							bustaInOrdine = true;
						}
					}

					// Chiusura Transazione
					connectionDB.commit();

					// ID Costruito
					nextSequenceOK = true;

				} catch(Exception e) {
					this.log.error("ERROR isConsegnaInOrdine ["+e.getMessage()+"]");
					try{
						if( rs != null )
							rs.close();
					} catch(Exception er) {}
					try{
						if( pstmt != null )
							pstmt.close();
					} catch(Exception er) {}
					try{
						connectionDB.rollback();
					} catch(Exception er) {}
				}

				if(nextSequenceOK == false){
					// Per aiutare ad evitare conflitti
					try{
						Thread.sleep((new java.util.Random()).nextInt(checkInterval)); // random da 0ms a checkIntervalms
					}catch(Exception eRandom){}
				}
			}

			// Ripristino Transazione
			try{
				connectionDB.setTransactionIsolation(oldTransactionIsolation);
				connectionDB.setAutoCommit(true);
			} catch(Exception er) {
				String errorMsg = "ConsegnaInOrdine, Errore durante la isConsegnaInOrdine(ripristinoIsolation): "+er.getMessage();		
				this.log.error(errorMsg,er);
				throw new ProtocolException(errorMsg,er);
			}


			if(nextSequenceOK==false){
				throw new ProtocolException("Controllo sequenza per gestione funzionalita' di consegna in ordine non riuscita");
			}

			return bustaInOrdine;
		}else{
			throw new ProtocolException("Metodo invocato con IState non valido");
		}
	}
	
	
	
	





	/**
	 * Aggiorna la sequenza
	 * 
	 * @param busta Busta su cui gestire la sequenza
	 * 
	 */
	public void setNextSequenza_daRicevere(Busta busta)throws ProtocolException{
		if(this.state instanceof StateMessage) {
			
			StateMessage stateful = (StateMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();
			// controllo che non abbia gia' gestito il numero di sequenza per la busta
			RepositoryBuste repository = new RepositoryBuste(stateful, this.log, true, this.protocolFactory);
			long sequenzaGestita = repository.getSequenzaFromInBox(busta.getID());
			if(sequenzaGestita == -2){
				return; // gia gestita
			}

			PreparedStatement pstmtInsert = null;
			try{	

				if(busta.getID().equals(busta.getCollaborazione())){
					// busta Capostipite

					StringBuffer queryInsert = new StringBuffer();
					queryInsert.append("INSERT INTO  ");
					queryInsert.append(Costanti.SEQUENZA_DA_RICEVERE);
					queryInsert.append(" VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? )");
					pstmtInsert =  connectionDB.prepareStatement(queryInsert.toString());
					pstmtInsert.setString(1,busta.getCollaborazione());
					pstmtInsert.setLong(2,(busta.getSequenza()+1)); //prossima sequenza attesa
					pstmtInsert.setString(3,busta.getMittente());
					pstmtInsert.setString(4,busta.getTipoMittente());
					pstmtInsert.setString(5,busta.getDestinatario());
					pstmtInsert.setString(6,busta.getTipoDestinatario());
					pstmtInsert.setString(7,busta.getServizio());
					pstmtInsert.setString(8,busta.getTipoServizio());
					pstmtInsert.setString(9,busta.getAzione());

				}else{
					// aggiorno prossima sequenza attesa
					StringBuffer queryInsert = new StringBuffer();
					queryInsert.append("UPDATE ");
					queryInsert.append(Costanti.SEQUENZA_DA_RICEVERE);
					queryInsert.append(" SET SEQUENZA_ATTESA = ? WHERE ID_COLLABORAZIONE=? ");
					pstmtInsert =  connectionDB.prepareStatement(queryInsert.toString());

					long next_sequenza = busta.getSequenza() + 1;
					if(next_sequenza > Costanti.MAX_VALUE_SEQUENZA_COUNTER){
						next_sequenza = 1;
					} 
					pstmtInsert.setLong(1,next_sequenza);
					pstmtInsert.setString(2,busta.getCollaborazione());
				}
				stateful.getPreparedStatement().put("INSERT setNextSequenza_daRicevere"+busta.getCollaborazione(), pstmtInsert);

				// imposto gestione della sequenza per la busta effettuata
				repository.aggiornaSequenzaIntoInBox(busta.getID(),-2);

			} catch(Exception e) {
				String errorMsg = "ConsegnaInOrdine, Errore durante la setNextSequenza_daRicevere: "+e.getMessage();		
				try{
					if( pstmtInsert != null )
						pstmtInsert.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}


		}else{
			throw new ProtocolException("Metodo invocato con IState non valido");
		}
	}

}





