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



package org.openspcoop2.protocol.engine.driver;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;


/**
 * Sono inclusi i metodi per la gestione dell'History delle buste inviate/ricevuta.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class History  {

	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** Se IState e' un'istanza di StatefulMessage possiede una Connessione SQL in autoCommit mode su cui effettuare query 
	 *  Altrimenti, e' un'istanza di StatelessMessage e nn necessita di connessioni 
	 * */
	private IState state;


	/** GestoreRepository */
	private IGestoreRepository gestoreRepositoryBuste;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	public History(IState state){
		this(state,Configurazione.getLibraryLog());
	}
	/**
	 * Costruttore. 
	 *
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	public History(IState state, Logger alog){
		this.state = state;
		this.gestoreRepositoryBuste = Configurazione.getGestoreRepositoryBuste();
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(History.class.getName());
		}
	}





	/* ********  B U S T E     I N V I A T E  ******** */
	/**
	 * Metodo che si occupa di salvare una busta, precedentemente registrata, 
	 * nell'history delle buste inviate. 
	 * 
	 * @param id identificativo della busta da salvare.
	 * 
	 */
	public void registraBustaInviata(String id) throws ProtocolException{
		registraBusta(id,Costanti.OUTBOX);
	}
	/**
	 * Metodo che si occupa di eliminare una busta, precedentemente registrata, 
	 * nell'history delle buste inviate. 
	 * 
	 * @param id identificativo della busta da eliminare.
	 * 
	 */
	public void eliminaBustaInviata(String id) throws ProtocolException{
		eliminaBusta(id,Costanti.OUTBOX);
	}
	public void eliminaBustaInviataPerRiscontri(String id) throws ProtocolException{
		eliminaBustaPerRiscontri(id,Costanti.OUTBOX);
	}

















	/* ********  B U S T E     R I C E V U T E  ******** */
	/**
	 * Metodo che si occupa di salvare una busta, precedentemente registrata, 
	 * nell'history delle buste ricevute. 
	 * 
	 * @param id identificativo della busta da salvare.
	 * 
	 */
	public void registraBustaRicevuta(String id) throws ProtocolException{
		registraBusta(id,Costanti.INBOX);
	}
	/**
	 * Metodo che si occupa di eliminare una busta, precedentemente registrata, 
	 * nell'history delle buste ricevute. 
	 * 
	 * @param id identificativo della busta da eliminare.
	 * 
	 */
	public void eliminaBustaRicevuta(String id) throws ProtocolException{
		eliminaBusta(id,Costanti.INBOX);
	}
	public void eliminaBustaRicevutaPerRiscontri(String id) throws ProtocolException{
		eliminaBustaPerRiscontri(id,Costanti.INBOX);
	}

	/**
	 * Ritorna true, se una busta con il medesimo identificativo e' gia' stata precedentemente registrata.
	 *
	 * @param id identificativo della busta.
	 * @return true se una busta con il medesimo identificativo e' gia' stata precedentemente registrata. false altrimenti
	 * 
	 */
	public boolean bustaRicevutaPrecedentemente(String id) throws ProtocolException{
		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{	

			StringBuffer query = new StringBuffer();
			query.append("select ID_MESSAGGIO from ");
			query.append(Costanti.REPOSITORY);
			query.append(" WHERE ID_MESSAGGIO = ? AND TIPO=? AND ");
			
			// controllo anche accesso_history=1 perche' puo' darsi che sia in 
			// corso di gestione...questa non la devo considerare gia ricevuta! 
			// per attuare questo controllo guardo che la busta non possiede accesso_history=1 (se e' gia' stata ricevuta possiede accesso_history=1)
			query.append(this.gestoreRepositoryBuste.createSQLCondition_History(true));
			
			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,Costanti.INBOX);

			rs = pstmt.executeQuery();
			if(rs != null){ 
				if(rs.next() == true) {
					rs.close();
					pstmt.close();
					return true;
				}
				else {
					rs.close();
					pstmt.close();
					return false;
				}
			}

			pstmt.close();

			return false;

		} catch(Exception e) {
			String errorMsg = "HISTORY_BUSTE, Errore durante il check bustaRicevutaPrecedentemente "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if(rs!=null)
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmt!=null)
					pstmt.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}
	}	


	// ************ GESTIONE HISTORY ************

	/**
	 * Metodo che si occupa di salvare una busta, precedentemente registrata, 
	 * nell'history delle buste. 
	 * 
	 * @param id identificativo della busta da salvare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * 
	 */
	public void registraBusta(String id,String tipoBusta) throws ProtocolException{

		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmtUpdate = null;
		try{	

			StringBuffer queryUpdate = new StringBuffer();
			queryUpdate.append("UPDATE ");
			queryUpdate.append(Costanti.REPOSITORY);
			queryUpdate.append(" SET ");
			queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_History(true));
			queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
			pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
			pstmtUpdate.setString(1,id);
			pstmtUpdate.setString(2,tipoBusta);

			// Add PreparedStatement
			stateMSG.getPreparedStatement().put("UPDATE saveBustaForHistory"+tipoBusta+"_"+id,pstmtUpdate);

		} catch(Exception e) {
			String errorMsg = "HISTORY_BUSTE, Errore di registrazione "+tipoBusta+"/"+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if( pstmtUpdate != null )
					pstmtUpdate.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}
	}
	
	/**
	 * Metodo che si occupa di eliminare una busta, precedentemente registrata, 
	 * nell'history delle buste. 
	 * 
	 * @param id identificativo della busta da salvare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * 
	 */
	public void eliminaBusta(String id,String tipoBusta) throws ProtocolException{
		eliminaBusta(id,tipoBusta,false);
	}
	public void eliminaBusta(String id,String tipoBusta,boolean forzaEliminazioneDb) throws ProtocolException{

		if(this.state instanceof StatefulMessage || forzaEliminazioneDb) {
			StateMessage state = (StateMessage)this.state;
			Connection connectionDB = state.getConnectionDB();

			PreparedStatement pstmtUpdate = null;
			try{	

				StringBuffer queryUpdate = new StringBuffer();
				queryUpdate.append("UPDATE ");
				queryUpdate.append(Costanti.REPOSITORY);
				queryUpdate.append(" SET ");
				queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_History(false));
				queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
				pstmtUpdate.setString(1,id);
				pstmtUpdate.setString(2,tipoBusta);

				// Add PreparedStatement
				state.getPreparedStatement().put("UPDATE eliminaBustaForHistory"+tipoBusta+"_"+id,pstmtUpdate);

			} catch(Exception e) {
				String errorMsg = "HISTORY_BUSTE, Errore di cancellazione "+tipoBusta+"/"+id+": "+e.getMessage();		
				this.log.error(errorMsg,e);
				try{
					if( pstmtUpdate != null )
						pstmtUpdate.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				throw new ProtocolException(errorMsg,e);
			}
		}
		else {
			throw new ProtocolException ("Metodo non invocabile in modalita' stateless");
		}
	}
	
	/**
	 * Metodo che si occupa di eliminare una busta, precedentemente registrata, 
	 * nell'history delle buste. 
	 * 
	 * @param id identificativo della busta da salvare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * 
	 */
	public void eliminaBustaPerRiscontri(String id,String tipoBusta) throws ProtocolException{

		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmtUpdate = null;
		try{	

			StringBuffer queryUpdate = new StringBuffer();
			queryUpdate.append("UPDATE ");
			queryUpdate.append(Costanti.REPOSITORY);
			queryUpdate.append(" SET ");
			queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_History(false));
			queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
			pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
			pstmtUpdate.setString(1,id);
			pstmtUpdate.setString(2,tipoBusta);

			// Add PreparedStatement
			stateMSG.getPreparedStatement().put("UPDATE eliminaBustaForHistory"+tipoBusta+"_"+id,pstmtUpdate);

		} catch(Exception e) {
			String errorMsg = "HISTORY_BUSTE, Errore di cancellazione "+tipoBusta+"/"+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if( pstmtUpdate != null )
					pstmtUpdate.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}
	}
}
