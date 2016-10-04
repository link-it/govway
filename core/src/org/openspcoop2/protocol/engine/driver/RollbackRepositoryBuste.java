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

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;


/**
 * Classe utilizzata per effettuare rollback applicativo
 * di informazioni salvate precedentemente nei DB.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RollbackRepositoryBuste implements java.io.Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** Se IState e' un'istanza di StatefulMessage possiede una Connessione SQL in autoCommit mode su cui effettuare query 
	 *  Altrimenti, e' un'istanza di StatelessMessage e nn necessita di connessioni  */
	private IState state;

	/** Identificativo */
	private String idBusta;

	/** GestoreRepository */
	private IGestoreRepository gestoreRepositoryBuste;

	/** Indicazione se stiamo gestendo il onewat in modalita 11 */
	//private boolean oneWay11 = false;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * @param id ID su cui effettuare il rollback delle precedenti informazioni salvate.
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	public RollbackRepositoryBuste(String id, IState state,boolean oneWay11){
		this(id,state,Configurazione.getLibraryLog(),oneWay11);
	}
	/**
	 * Costruttore. 
	 *
	 * @param id ID su cui effettuare il rollback delle precedenti informazioni salvate.
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	public RollbackRepositoryBuste(String id,IState state,Logger aLog,boolean oneWay11){
		this.idBusta = id;
		this.state = state;
		this.gestoreRepositoryBuste = Configurazione.getGestoreRepositoryBuste();
		if(aLog!=null)
			this.log = aLog;
		else
			this.log = LoggerWrapperFactory.getLogger(RollbackRepositoryBuste.class.getName());
		//this.oneWay11 = oneWay11;
	}




	/**
	 * Metodo che si occupa di eliminare tutti i dati creati durante una gestione di una richiesta
	 * in una fase di Porta di Dominio Delegata (Invocazione di una porta di dominio delegata).
	 *
	 * 
	 */
	public void rollbackBustaIntoOutBox() throws ProtocolException{	
		this.rollback(Costanti.OUTBOX,true); 
	}
	/**
	 * Metodo che si occupa di eliminare tutti i dati creati durante una gestione di una richiesta
	 * in una fase di Porta di Dominio Delegata (Invocazione di una porta di dominio delegata).
	 *
	 * @param rollbackAccessoHistory rollback dell'accesso effettuato dall'History
	 * 
	 */
	public void rollbackBustaIntoOutBox(boolean rollbackAccessoHistory) throws ProtocolException{	
		this.rollback(Costanti.OUTBOX,rollbackAccessoHistory); 
	}

	/**
	 * Metodo che si occupa di eliminare tutti i dati creati durante una gestione di una richiesta
	 * in una fase di Porta di Dominio Applicativa (Invocazione di una porta di dominio applicativa).
	 *
	 * 
	 */
	public void rollbackBustaIntoInBox() throws ProtocolException{
		this.rollback(Costanti.INBOX,true);
	}
	/**
	 * Metodo che si occupa di eliminare tutti i dati creati durante una gestione di una richiesta
	 * in una fase di Porta di Dominio Applicativa (Invocazione di una porta di dominio applicativa).
	 *
	 * @param rollbackAccessoHistory rollback dell'accesso effettuato dall'History
	 * 
	 */
	public void rollbackBustaIntoInBox(boolean rollbackAccessoHistory) throws ProtocolException{
		this.rollback(Costanti.INBOX,rollbackAccessoHistory);
	}









	/* ********  UTILITY DI ROLLBACK APPLICATIVO  ******** */
	/**
	 * Metodo che si occupa di eliminare i dati di una busta presente nel RepositoryBuste.
	 *
	 * @param tipoBusta tipo di busta INBOX/OUTBOX
	 * @param rollbackAccessoHistory rollback dell'accesso effettuato dall'History
	 * 
	 */
	public void rollback(String tipoBusta,boolean rollbackAccessoHistory) throws ProtocolException{

		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		if(connectionDB!=null){
			PreparedStatement pstmtUpdateHistory = null;
			PreparedStatement pstmtUpdateProfilo = null;
			PreparedStatement pstmtUpdatePdd = null;
			try{	
	
				// Le prepared Stamenent devono essere suddivise, per Oracle, ad es, non accetta SET di stessi field.
	
				// rollback AccessoHistory
				if(rollbackAccessoHistory){
					StringBuffer queryUpdateHistory = new StringBuffer();
					queryUpdateHistory.append("UPDATE ");
					queryUpdateHistory.append(Costanti.REPOSITORY);
					queryUpdateHistory.append(" SET ");
					queryUpdateHistory.append(this.gestoreRepositoryBuste.createSQLSet_History(false));
					queryUpdateHistory.append(" WHERE ID_MESSAGGIO = ? AND TIPO=?");
					pstmtUpdateHistory =  connectionDB.prepareStatement(queryUpdateHistory.toString());
					pstmtUpdateHistory.setString(1,this.idBusta);
					pstmtUpdateHistory.setString(2,tipoBusta);
					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("RollbackGeneraleHISTORY_"+tipoBusta+"_"+this.idBusta,pstmtUpdateHistory);
				}
	
				// rollback profilo
				StringBuffer queryUpdateProfilo= new StringBuffer();
				queryUpdateProfilo.append("UPDATE ");
				queryUpdateProfilo.append(Costanti.REPOSITORY);
				queryUpdateProfilo.append(" SET ");
				queryUpdateProfilo.append(this.gestoreRepositoryBuste.createSQLSet_ProfiloCollaborazione(false));
				queryUpdateProfilo.append(" WHERE ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdateProfilo =  connectionDB.prepareStatement(queryUpdateProfilo.toString());
				pstmtUpdateProfilo.setString(1,this.idBusta);
				pstmtUpdateProfilo.setString(2,tipoBusta);
				// Add PreparedStatement
				stateMSG.getPreparedStatement().put("RollbackGeneralePROFILO_"+tipoBusta+"_"+this.idBusta,pstmtUpdateProfilo);
	
				// rollback pdd
				StringBuffer queryUpdatePdd = new StringBuffer();
				queryUpdatePdd.append("UPDATE ");
				queryUpdatePdd.append(Costanti.REPOSITORY);
				queryUpdatePdd.append(" SET ");
				queryUpdatePdd.append(this.gestoreRepositoryBuste.createSQLSet_PdD(false));
				queryUpdatePdd.append(" WHERE ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdatePdd =  connectionDB.prepareStatement(queryUpdatePdd.toString());
				pstmtUpdatePdd.setString(1,this.idBusta);
				pstmtUpdatePdd.setString(2,tipoBusta);
				// Add PreparedStatement
				stateMSG.getPreparedStatement().put("RollbackGeneralePDD_"+tipoBusta+"_"+this.idBusta,pstmtUpdatePdd);
	
			} catch(Exception e) {
				String errorMsg = "ROLLBACK_BUSTE, Errore "+tipoBusta+"/"+this.idBusta+": "+e.getMessage();
				this.log.info(errorMsg,e);
				try{
					if( pstmtUpdateHistory != null )
						pstmtUpdateHistory.close();
				} catch(Exception er) {}
				try{
					if( pstmtUpdateProfilo != null )
						pstmtUpdateProfilo.close();
				} catch(Exception er) {}
				try{
					if( pstmtUpdatePdd != null )
						pstmtUpdatePdd.close();
				} catch(Exception er) {}
				throw new ProtocolException(errorMsg,e);
			}
		}else{
			this.log.debug("Rollback("+tipoBusta+"/"+this.idBusta+") non effettuato, connessione is null");
		}

	}

	/* ********  UTILITY DI SET ACCESSI  ******** */
	/**
	 * Metodo che si occupa di eliminare i dati di una busta presente nel RepositoryBuste.
	 *
	 * @param history accesso effettuato dall'History
	 * @param profilo accesso effettuato dall'History
	 * @param pdd accesso effettuato dall'History
	 * 
	 */
	public void clearAccessiIntoInBox(boolean history,boolean profilo,boolean pdd) throws ProtocolException{
		clearAccessi(Costanti.INBOX,history,profilo,pdd);
	}
	/**
	 * Metodo che si occupa di eliminare i dati di una busta presente nel RepositoryBuste.
	 *
	 * @param history accesso effettuato dall'History
	 * @param profilo accesso effettuato dall'History
	 * @param pdd accesso effettuato dall'History
	 * 
	 */
	public void clearAccessiIntoOutBox(boolean history,boolean profilo,boolean pdd) throws ProtocolException{
		clearAccessi(Costanti.OUTBOX,history,profilo,pdd);
	}
	/**
	 * Metodo che si occupa di eliminare i dati di una busta presente nel RepositoryBuste.
	 *
	 * @param tipoBusta tipo di busta INBOX/OUTBOX
	 * @param history accesso effettuato dall'History
	 * @param profilo accesso effettuato dall'History
	 * @param pdd accesso effettuato dall'History
	 * 
	 */
	private void clearAccessi(String tipoBusta,boolean history,boolean profilo,boolean pdd) throws ProtocolException{
		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		if(connectionDB!=null){
			PreparedStatement pstmtUpdateHistory = null;
			PreparedStatement pstmtUpdateProfilo = null;
			PreparedStatement pstmtUpdatePdd = null;
			try{	
	
				if(history==false && profilo==false && pdd==false)
					return;
	
				// rollback AccessoHistory
				if(history){
					StringBuffer queryUpdateHistory = new StringBuffer();
					queryUpdateHistory.append("UPDATE ");
					queryUpdateHistory.append(Costanti.REPOSITORY);
					queryUpdateHistory.append(" SET ");
					queryUpdateHistory.append(this.gestoreRepositoryBuste.createSQLSet_History(false));
					queryUpdateHistory.append(" WHERE ID_MESSAGGIO = ? AND TIPO=?");
					pstmtUpdateHistory =  connectionDB.prepareStatement(queryUpdateHistory.toString());
					pstmtUpdateHistory.setString(1,this.idBusta);
					pstmtUpdateHistory.setString(2,tipoBusta);
					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("ClearAccessiHISTORY_"+tipoBusta+"_"+this.idBusta,pstmtUpdateHistory);
				}
	
				// rollback profilo
				if(profilo){
					StringBuffer queryUpdateProfilo= new StringBuffer();
					queryUpdateProfilo.append("UPDATE ");
					queryUpdateProfilo.append(Costanti.REPOSITORY);
					queryUpdateProfilo.append(" SET ");
					queryUpdateProfilo.append(this.gestoreRepositoryBuste.createSQLSet_ProfiloCollaborazione(false));
					queryUpdateProfilo.append(" WHERE ID_MESSAGGIO = ? AND TIPO=?");
					pstmtUpdateProfilo =  connectionDB.prepareStatement(queryUpdateProfilo.toString());
					pstmtUpdateProfilo.setString(1,this.idBusta);
					pstmtUpdateProfilo.setString(2,tipoBusta);
					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("ClearAccessiPROFILO_"+tipoBusta+"_"+this.idBusta,pstmtUpdateProfilo);
				}
	
				// rollback pdd
				if(pdd){
					StringBuffer queryUpdatePdd = new StringBuffer();
					queryUpdatePdd.append("UPDATE ");
					queryUpdatePdd.append(Costanti.REPOSITORY);
					queryUpdatePdd.append(" SET ");
					queryUpdatePdd.append(this.gestoreRepositoryBuste.createSQLSet_PdD(false));
					queryUpdatePdd.append(" WHERE ID_MESSAGGIO = ? AND TIPO=?");
					pstmtUpdatePdd = connectionDB.prepareStatement(queryUpdatePdd.toString());
					pstmtUpdatePdd.setString(1,this.idBusta);
					pstmtUpdatePdd.setString(2,tipoBusta);
					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("ClearAccessiPDD_"+tipoBusta+"_"+this.idBusta,pstmtUpdatePdd);
				}
	
	
			} catch(Exception e) {
				String errorMsg = "ROLLBACK_BUSTE, setAccessi Errore "+tipoBusta+"/"+this.idBusta+": "+e.getMessage();
				this.log.info(errorMsg,e);
				try{
					if( pstmtUpdateHistory != null )
						pstmtUpdateHistory.close();
				} catch(Exception er) {}
				try{
					if( pstmtUpdateProfilo != null )
						pstmtUpdateProfilo.close();
				} catch(Exception er) {}
				try{
					if( pstmtUpdatePdd != null )
						pstmtUpdatePdd.close();
				} catch(Exception er) {}
				throw new ProtocolException(errorMsg,e);
			}
		}else{
			this.log.debug("clearAccessi("+tipoBusta+"/"+this.idBusta+") non effettuato, connessione is null");
		}
	}
}
