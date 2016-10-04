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
import java.util.Date;
import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * Sono inclusi i metodi per la gestione dei Riscontri.
 * La gestione dei riscontri puo' essere classificati nella seguente maniera :
 * <ul>
 * <li> Riscontri da ricevere, dove un mittente ha inviato una busta e sta' attendendo un ACK!
 * <li> Riscontri da inviare (modalita' NON PIGGYBACKING), quando una porta di dominio riceve una busta
 *      con profilo 'ConfermaRicezione'==true, deve generare un riscontro apposito.
 * <li> Riscontri da inviare (modalita' PIGGYBACKING), quando una porta di dominio riceve una busta
 *      con profilo 'ConfermaRicezione'==true, deve salvarsi le informazioni per generare successivi riscontri. 
 * </ul>
 * Tutti i metodi hanno bisogno di una connessione ad un DB, precedentemente impostata
 * e passata attraverso l'apposito metodo.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Riscontri  {



	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	/** Se IState e' un'istanza di StatefulMessage possiede una Connessione SQL in autoCommit mode su cui effettuare query 
	 *  Altrimenti, e' un'istanza di StatelessMessage e nn necessita di connessioni  */
	private IState state;



	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	public Riscontri(IState state){
		this(state,Configurazione.getLibraryLog());
	}
	/**
	 * Costruttore. 
	 *
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	public Riscontri(IState state ,Logger alog){
		this.state = state;
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(Riscontri.class.getName());
		}
	}
	
	/* ********  R I S C O N T R I    D A    R I C E V E R E  ******** */

	/**
	 * Aggiunge un riscontro da ricevere nella tabella per la gestione della fase di ricezione riscontro.
	 *
	 * @param id identificativo della busta.
	 * @param timestamp data di invio della busta.
	 * 
	 */
	public void registraRiscontroDaRicevere(String id , Date timestamp)throws ProtocolException{

		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();
		
		PreparedStatement pstmt = null;
		
		try{	

			java.sql.Timestamp oraInvio = new java.sql.Timestamp(timestamp.getTime());

			StringBuffer query = new StringBuffer();
			query.append("INSERT INTO  ");
			query.append(Costanti.RISCONTRI_DA_RICEVERE);
			query.append(" VALUES ( ? , ? )");


			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,id);
			pstmt.setTimestamp(2,oraInvio);

			//	Add PreparedStatement (LASCIARE UPDATE per ordine esecuzione)
			stateMSG.getPreparedStatement().put("UPDATE saveRiscontroDaRicevere_"+id,pstmt);


			// Registrazione nella tabella History
			History historyBuste = new History(this.state);
			historyBuste.registraBustaInviata(id);
	
		} catch(Exception e) {
			String errorMsg = "RISCONTRI, Errore di registrazione "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

	}


	/**
	 * In caso esista nella tabella dei riscontri da ricevere, un riscontro scaduto,
	 * si occupa di ritornare un array di riscontro da reinviare, aggiornando le loro data di registrazione.
	 * Il controllo non e' serializzato, quindi possono essere ritornate anche busta gia' riscontrate, in seguito al controllo.
	 *
	 * @param timeout Minuti dopo il quale una data risulta scaduta.
	 * @return un vector di {@link org.openspcoop2.protocol.sdk.Busta} contenente le informazioni necessarie per il re-invio delle buste, 
	 *         se esistono riscontro scaduti.
	 */
	public Vector<BustaNonRiscontrata> getBustePerUlterioreInoltro(long timeout, int limit, int offset, boolean logQuery)throws ProtocolException{
		
		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		java.util.Vector<String> IDBuste = new java.util.Vector<String>();
		String queryString = null;
		try{	

			long nowTime = DateManager.getTimeMillis() - (timeout * 60 * 1000);
			java.sql.Timestamp scadenzaRiscontro = new java.sql.Timestamp(nowTime);


			if(Configurazione.getSqlQueryObjectType()==null){
				StringBuffer query = new StringBuffer();
				query.append("SELECT ID_MESSAGGIO FROM ");
				query.append(Costanti.RISCONTRI_DA_RICEVERE);
				query.append(" WHERE DATA_INVIO < ? ");
				queryString = query.toString();
			}else{

				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());
				sqlQueryObject.addSelectField("ID_MESSAGGIO");
				sqlQueryObject.addSelectField("DATA_INVIO");
				sqlQueryObject.addFromTable(Costanti.RISCONTRI_DA_RICEVERE);
				sqlQueryObject.addWhereCondition("DATA_INVIO < ?"); // order by e' obbligatorio essendoci l'offset
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("DATA_INVIO");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}


			//System.out.println("QUERY RISCONTRI IS: ["+queryString+"] 1["+scadenzaRiscontro+"]");

			pstmt = connectionDB.prepareStatement(queryString);
			pstmt.setTimestamp(1,scadenzaRiscontro);

			// Esecuzione comando SQL
			long startDateSQLCommand = DateManager.getTimeMillis();
			if(logQuery)
				this.log.debug("[QUERY] (Riscontri) ["+queryString+"] 1["+scadenzaRiscontro+"]...");
			rs = pstmt.executeQuery();		
			long endDateSQLCommand = DateManager.getTimeMillis();
			long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
			if(logQuery)
				this.log.debug("[QUERY] (Riscontri) ["+queryString+"] 1["+scadenzaRiscontro+"] effettuata in "+secondSQLCommand+" secondi");

			if(rs == null) {
				pstmt.close();
				throw new ProtocolException("RS NULL?");
			}
			int countLimit = 0;
			int countOffset = 0;
			while(rs.next()){
				if(Configurazione.getSqlQueryObjectType()==null){
					// OFFSET APPLICATIVO
					if( countOffset>=offset ){
						String id = rs.getString("ID_MESSAGGIO");
						IDBuste.add(id);
						// LIMIT Applicativo
						countLimit++;
						if(countLimit==limit)
							break;
					}
					else{
						countOffset++;
					}
				}else{
					String id = rs.getString("ID_MESSAGGIO");
					IDBuste.add(id);
				}
			}
			rs.close();
			pstmt.close();

		} catch(Exception e) {
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {}
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {}
			String errorMsg = "[Riscontri.getBustePerUlterioreInoltro] errore, queryString["+queryString+"]: "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}
		
		
		Vector<BustaNonRiscontrata> listaBustaNonRiscontrata = new Vector<BustaNonRiscontrata>();
		for (int i = 0; i < IDBuste.size(); i++) {
			BustaNonRiscontrata bustaNonRiscontrata = new BustaNonRiscontrata();
			bustaNonRiscontrata.setIdentificativo(IDBuste.get(i));
			bustaNonRiscontrata.setProfiloCollaborazione(ProfiloDiCollaborazione.ONEWAY);
			listaBustaNonRiscontrata.add(bustaNonRiscontrata);
		}
		
		return listaBustaNonRiscontrata;
		
	}
	
	
	
	/**
	 * Valida un riscontro ricevuto, identificato dall'identificativo della busta.
	 * In caso di validazione del riscontro, l'entry nella tabella di gestione dei riscontri da ricevere,
	 * con chiave di accesso uguale al parametro <var>idRiscontro</var> viene cancellata.
	 *
	 * @param idRiscontro identificativo del riscontro da validare.
	 * @deprecated utilizzare la versione non serializable
	 */
	@Deprecated
	public void validazioneRiscontroRicevuto_serializable(String idRiscontro) throws ProtocolException{
		validazioneRiscontroRicevuto_serializable(idRiscontro,60l,100);
	}


	/**
	 * Valida un riscontro ricevuto, identificato dall'identificativo della busta.
	 * In caso di validazione del riscontro, l'entry nella tabella di gestione dei riscontri da ricevere,
	 * con chiave di accesso uguale al parametro <var>idRiscontro</var> viene cancellata.
	 *
	 * @param idRiscontro identificativo del riscontro da validare.
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * @deprecated utilizzare la versione non serializable
	 */
	@Deprecated
	public void validazioneRiscontroRicevuto_serializable(String idRiscontro,long attesaAttiva,int checkInterval) throws ProtocolException{
		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();


		/*
  Viene realizzato con livello di isolamento SERIALIZABLE, per essere sicuri
  che esecuzioni parallele non leggano dati inconsistenti.
  Con il livello SERIALIZABLE, se ritorna una eccezione, deve essere riprovato
		 */
		// setAutoCommit e livello Isolamento
		int oldTransactionIsolation = -1;
		try{
			oldTransactionIsolation =connectionDB.getTransactionIsolation();
			connectionDB.setAutoCommit(false);
			JDBCUtilities.setTransactionIsolationSerializable(Configurazione.getSqlQueryObjectType(), connectionDB);
		} catch(Exception er) {
			String errorMsg = "RISCONTRI, Errore durante la validazioneRiscontroRicevuto(setIsolation): "+er.getMessage();		
			this.log.error(errorMsg,er);
			throw new ProtocolException(errorMsg,er);
		}


		boolean deleteRiscontroOK = false;

		long scadenzaWhile = DateManager.getTimeMillis() + attesaAttiva;

		while(deleteRiscontroOK==false && DateManager.getTimeMillis() < scadenzaWhile){

			PreparedStatement pstmtDelete = null;
			try{

				// Eliminazione dalla tabella Riscontri
				StringBuffer query = new StringBuffer();
				query.delete(0,query.capacity());
				query.append("DELETE FROM ");
				query.append(Costanti.RISCONTRI_DA_RICEVERE);
				query.append(" WHERE ID_MESSAGGIO = ?");
				pstmtDelete = connectionDB.prepareStatement(query.toString());
				pstmtDelete.setString(1,idRiscontro);
				pstmtDelete.execute();
				pstmtDelete.close();

				// Eliminazione dalla tabella History
				History historyBuste = new History(this.state,this.log);
				historyBuste.eliminaBustaInviata(idRiscontro);
				
				stateMSG.executePreparedStatement();

				// Chiusura Transazione
				connectionDB.commit();

				// ID Costruito
				deleteRiscontroOK = true;

			} catch(Exception e) {
				try{
					if( pstmtDelete != null  )
						pstmtDelete.close();
				} catch(Exception er) {}
				try{
					connectionDB.rollback();
				} catch(Exception er) {}
			}

			if(deleteRiscontroOK == false){
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
			String errorMsg = "RISCONTRI, Errore durante la validazioneRiscontroRicevuto(ripristinoIsolation): "+er.getMessage();		
			this.log.error(errorMsg,er);
			throw new ProtocolException(errorMsg,er);
		}
	}	


	/**
	 * Valida un riscontro ricevuto, identificato dall'identificativo della busta.
	 * In caso di validazione del riscontro, l'entry nella tabella di gestione dei riscontri da ricevere,
	 * con chiave di accesso uguale al parametro <var>idRiscontro</var> viene cancellata.
	 *
	 * @param idRiscontro identificativo del riscontro da validare.
	 */
	public void validazioneRiscontroRicevuto(String idRiscontro) throws ProtocolException{
		
		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmtDelete = null;
		try{

			// Eliminazione dalla tabella Riscontri
			StringBuffer query = new StringBuffer();
			query.delete(0,query.capacity());
			query.append("DELETE FROM ");
			query.append(Costanti.RISCONTRI_DA_RICEVERE);
			query.append(" WHERE ID_MESSAGGIO = ?");
			pstmtDelete = connectionDB.prepareStatement(query.toString());
			pstmtDelete.setString(1,idRiscontro);

			//	Add PreparedStatement (LASCIARE UPDATE per ordine esecuzione)
			stateMSG.getPreparedStatement().put("UPDATE validazioneRiscontroRicevuto_"+idRiscontro,pstmtDelete);

			// Eliminazione dalla tabella History
			History historyBuste = new History(stateMSG,this.log);
			historyBuste.eliminaBustaInviataPerRiscontri(idRiscontro);
		} catch(Exception e) {
			try{
				if( pstmtDelete != null  )
					pstmtDelete.close();
			} catch(Exception er) {}
			String errorMsg = "RISCONTRI, Errore durante la validazioneRiscontroRicevuto: "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}
	}
}





