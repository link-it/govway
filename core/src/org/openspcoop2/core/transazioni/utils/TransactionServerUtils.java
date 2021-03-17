/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.transazioni.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.IDAOFactory;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.transazioni.IdTransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.TransazioneApplicativoServer;
import org.openspcoop2.core.transazioni.dao.ITransazioneApplicativoServerService;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCTransazioneService;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.TransazioneFieldConverter;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.Case;
import org.openspcoop2.utils.sql.CastColumnType;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;

/**     
 * TransactionServerUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionServerUtils {

	// *** SAVE ***
	
	public static boolean recover(ITransazioneApplicativoServerService transazioneService, TransazioneApplicativoServer serverInfoParam) throws Exception {
		
		String idTransazione = serverInfoParam.getIdTransazione();
		if(idTransazione==null) {
			throw new CoreException("IdTransazione non esistente nel contesto");
		}
		
		String servizioApplicativoErogatore = serverInfoParam.getServizioApplicativoErogatore();
		if(servizioApplicativoErogatore==null) {
			throw new CoreException("Id servizioApplicativoErogatore non esistente nel contesto");
		}
		
		boolean firstEntry = serverInfoParam.getDataUscitaRichiesta()==null && serverInfoParam.getDataPrelievoIm()==null && serverInfoParam.getDataEliminazioneIm()==null;
		
		if(firstEntry) {
			return save(transazioneService, serverInfoParam, false, false, true);		
		}
		else {
			IdTransazioneApplicativoServer idTransazioneApplicativoServer = new IdTransazioneApplicativoServer();
			idTransazioneApplicativoServer.setIdTransazione(idTransazione);
			idTransazioneApplicativoServer.setServizioApplicativoErogatore(servizioApplicativoErogatore);
			if(transazioneService.exists(idTransazioneApplicativoServer)) {
				
				TransazioneApplicativoServer transazioneApplicativoServer = transazioneService.get(idTransazioneApplicativoServer);
				if(!transazioneApplicativoServer.isConsegnaTerminata() && transazioneApplicativoServer.getDataEliminazioneIm()==null && transazioneApplicativoServer.getDataMessaggioScaduto()==null) {
					return save(transazioneService, serverInfoParam, true, false, true);
				}
				// else ho già registrato l'ultima informazione, è inutile fare update delle informazioni parziali.
				else {
					return false;
				}
			}
			else {
				throw new Exception("In attesa della registrazione dell'informazione capostipite");
			}
		}
		
	}
	
	public static boolean save(ITransazioneApplicativoServerService transazioneService, TransazioneApplicativoServer serverInfoParam, boolean update, boolean throwNotFoundIfNotExists, boolean recover) throws Exception {
		
		String idTransazione = serverInfoParam.getIdTransazione();
		if(idTransazione==null) {
			throw new CoreException("IdTransazione non esistente nel contesto");
		}
		
		String servizioApplicativoErogatore = serverInfoParam.getServizioApplicativoErogatore();
		if(servizioApplicativoErogatore==null) {
			throw new CoreException("Id servizioApplicativoErogatore non esistente nel contesto");
		}
		
		//System.out.println("SAVE id["+idTransazione+"] ["+servizioApplicativoErogatore+"] update:"+update+" ....");
		
		String protocol = serverInfoParam.getProtocollo();
		if(protocol==null) {
			throw new CoreException("Protocollo non esistente nel contesto");
		}
		
		IdTransazioneApplicativoServer idTransazioneApplicativoServer = new IdTransazioneApplicativoServer();
		idTransazioneApplicativoServer.setIdTransazione(idTransazione);
		idTransazioneApplicativoServer.setServizioApplicativoErogatore(servizioApplicativoErogatore);
		
		
		
		if(update) {
			
			// inefficente: fa 2 query
			// TransazioneApplicativoServer transazioneApplicativoServer = transazioneService.get(idTransazioneApplicativoServer);
			
			IExpression expression = transazioneService.newExpression();
			expression.equals(TransazioneApplicativoServer.model().ID_TRANSAZIONE, idTransazione);
			expression.and();
			expression.equals(TransazioneApplicativoServer.model().SERVIZIO_APPLICATIVO_EROGATORE, servizioApplicativoErogatore);
			TransazioneApplicativoServer transazioneApplicativoServerReadFromDB = null;
			try {
				transazioneApplicativoServerReadFromDB = transazioneService.find(expression);
			}catch(NotFoundException notfound) {
				if(throwNotFoundIfNotExists) {
					throw notfound;
				}
				else {
					return false;
				}
			}
			
			// aggiorno data registrazione per controllo effettuato durante l'aggiornamento della transazione (metodo sotto: aggiornaInformazioneConsegnaTerminata)
			serverInfoParam.setDataRegistrazione(transazioneApplicativoServerReadFromDB.getDataRegistrazione());
			
						
			if(serverInfoParam.getDataMessaggioScaduto()!=null) {
				
				if( transazioneApplicativoServerReadFromDB.getDataMessaggioScaduto()!=null ) {
					return false; // l'informazione salvata sul database indica già un messaggio scaduto.
				}
				
				// date
				transazioneApplicativoServerReadFromDB.setDataMessaggioScaduto(serverInfoParam.getDataMessaggioScaduto());
				
			}
			else if(serverInfoParam.getDataEliminazioneIm()!=null) {
				
				if( transazioneApplicativoServerReadFromDB.getDataEliminazioneIm()!=null ) {
					return false; // l'informazione salvata sul database indica già un messaggio eliminato via I.M..
				}
				
				// date
				transazioneApplicativoServerReadFromDB.setDataEliminazioneIm(serverInfoParam.getDataEliminazioneIm());
				
				// cluster id
				transazioneApplicativoServerReadFromDB.setClusterIdEliminazioneIm(serverInfoParam.getClusterIdEliminazioneIm());
				
				// segno come completata
				transazioneApplicativoServerReadFromDB.setConsegnaTerminata(true);
				
			}
			else if(serverInfoParam.getDataPrelievoIm()!=null) {
				
				if( transazioneApplicativoServerReadFromDB.getDataEliminazioneIm()!=null ) {
					return false; // l'informazione salvata sul database indica già un messaggio eliminato via I.M..
				}
				
				// date
				transazioneApplicativoServerReadFromDB.setDataPrelievoIm(serverInfoParam.getDataPrelievoIm());
				
				// data primo tentativo
				if(transazioneApplicativoServerReadFromDB.getDataPrimoPrelievoIm()==null) {
					if(serverInfoParam.getDataPrelievoIm()!=null) {
						transazioneApplicativoServerReadFromDB.setDataPrimoPrelievoIm(serverInfoParam.getDataPrelievoIm()); // primo tentativo di prelievo
					}
				}
				
				// numero tentativi
				transazioneApplicativoServerReadFromDB.setNumeroPrelieviIm(transazioneApplicativoServerReadFromDB.getNumeroPrelieviIm()+1);	
	
				// cluster id
				transazioneApplicativoServerReadFromDB.setClusterIdPrelievoIm(serverInfoParam.getClusterIdPrelievoIm());
				
			}
			else {
			
				if( transazioneApplicativoServerReadFromDB.isConsegnaTerminata() ) {
					return false; // l'informazione salvata sul database indica già un messaggio completato.
				}
				
				// ** successivi invii **
				
				// id transazione, servizio applicativo erogatore e data registrazione non sono da aggiornare
											
				// esito
				transazioneApplicativoServerReadFromDB.setConsegnaTerminata(serverInfoParam.isConsegnaTerminata());
				transazioneApplicativoServerReadFromDB.setDettaglioEsito(serverInfoParam.getDettaglioEsito());
				
				// date
				transazioneApplicativoServerReadFromDB.setDataAccettazioneRichiesta(serverInfoParam.getDataAccettazioneRichiesta());
				transazioneApplicativoServerReadFromDB.setDataUscitaRichiesta(serverInfoParam.getDataUscitaRichiesta());
				transazioneApplicativoServerReadFromDB.setDataAccettazioneRisposta(serverInfoParam.getDataAccettazioneRisposta());
				transazioneApplicativoServerReadFromDB.setDataIngressoRisposta(serverInfoParam.getDataIngressoRisposta());
				
				// dimensioni
				transazioneApplicativoServerReadFromDB.setRichiestaUscitaBytes(serverInfoParam.getRichiestaUscitaBytes());
				transazioneApplicativoServerReadFromDB.setRispostaIngressoBytes(serverInfoParam.getRispostaIngressoBytes());
			
				// location e codice risposta
				transazioneApplicativoServerReadFromDB.setLocationConnettore(serverInfoParam.getLocationConnettore());
				transazioneApplicativoServerReadFromDB.setCodiceRisposta(serverInfoParam.getCodiceRisposta());
				
				// fault
				transazioneApplicativoServerReadFromDB.setFault(serverInfoParam.getFault());
				transazioneApplicativoServerReadFromDB.setFormatoFault(serverInfoParam.getFormatoFault());
				
				// data primo tentativo
				if(transazioneApplicativoServerReadFromDB.getDataPrimoTentativo()==null) {
					if(serverInfoParam.getDataUscitaRichiesta()!=null) {
						transazioneApplicativoServerReadFromDB.setDataPrimoTentativo(serverInfoParam.getDataUscitaRichiesta()); // primo tentativo di consegna
					}
				}
				
				// numero tentativi
				transazioneApplicativoServerReadFromDB.setNumeroTentativi(transazioneApplicativoServerReadFromDB.getNumeroTentativi()+1);	
	
				// cluster id
				transazioneApplicativoServerReadFromDB.setClusterIdConsegna(serverInfoParam.getClusterIdConsegna());
				
				
				// aggiorno errore e fault se ho uno stato non ok
				
				if(!serverInfoParam.isConsegnaTerminata()) {
					
					if(serverInfoParam.getDataUscitaRichiesta()!=null) {
						transazioneApplicativoServerReadFromDB.setDataUltimoErrore(serverInfoParam.getDataUscitaRichiesta());
					}
					else {
						transazioneApplicativoServerReadFromDB.setDataUltimoErrore(serverInfoParam.getDataAccettazioneRichiesta());
					}
					
					transazioneApplicativoServerReadFromDB.setDettaglioEsitoUltimoErrore(serverInfoParam.getDettaglioEsito());
					
					transazioneApplicativoServerReadFromDB.setCodiceRispostaUltimoErrore(serverInfoParam.getCodiceRisposta());
					
					transazioneApplicativoServerReadFromDB.setUltimoErrore(serverInfoParam.getUltimoErrore());
					
					transazioneApplicativoServerReadFromDB.setLocationUltimoErrore(serverInfoParam.getLocationConnettore());
					
					transazioneApplicativoServerReadFromDB.setClusterIdUltimoErrore(serverInfoParam.getClusterIdConsegna());
									
					transazioneApplicativoServerReadFromDB.setFaultUltimoErrore(serverInfoParam.getFault());
					transazioneApplicativoServerReadFromDB.setFormatoFaultUltimoErrore(serverInfoParam.getFormatoFault());
				}
				
			}
			
			transazioneService.update(idTransazioneApplicativoServer, transazioneApplicativoServerReadFromDB);
			
			return true;
		}
		else {
			
			// ** primo invio **
			
			TransazioneApplicativoServer transazioneApplicativoServer = serverInfoParam;
			
			// protocollo
			transazioneApplicativoServer.setProtocollo(protocol);
			
			// data registrazione
			if(transazioneApplicativoServer.getDataRegistrazione()==null) {
				transazioneApplicativoServer.setDataRegistrazione(DateManager.getDate());
			}
			
			// data primo tentativo
			if(transazioneApplicativoServer.getDataUscitaRichiesta()!=null) {
				transazioneApplicativoServer.setDataPrimoTentativo(transazioneApplicativoServer.getDataUscitaRichiesta()); // primo tentativo di consegna
			}
			if(transazioneApplicativoServer.getDataPrelievoIm()!=null) {
				transazioneApplicativoServer.setDataPrimoPrelievoIm(transazioneApplicativoServer.getDataPrelievoIm());
			}			
			
			// numero tentativi
			transazioneApplicativoServer.setNumeroTentativi(0);
			transazioneApplicativoServer.setNumeroPrelieviIm(0);
			
			// cluster id
			transazioneApplicativoServer.setClusterIdPresaInCarico(serverInfoParam.getClusterIdPresaInCarico());
							
			// CREO
			transazioneService.create(transazioneApplicativoServer);
			
			return true;
		}

	}
	
	public static void safe_aggiornaInformazioneConsegnaTerminata(TransazioneApplicativoServer transazioneApplicativoServer, Connection con, 
			String tipoDatabase, Logger logCore,
			IDAOFactory daoFactory, Logger logFactory, ServiceManagerProperties smpFactory,
			boolean debug,
			int esitoConsegnaMultipla, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, int ok,
			long gestioneSerializableDB_AttesaAttiva, int gestioneSerializableDB_CheckInterval) {
		
		// aggiorno esito transazione
		try{
			TransactionServerUtils.aggiornaInformazioneConsegnaTerminata(transazioneApplicativoServer, con, 
					tipoDatabase, logCore,
					daoFactory, logFactory, smpFactory,
					debug,
					esitoConsegnaMultipla, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
					gestioneSerializableDB_AttesaAttiva, gestioneSerializableDB_CheckInterval);
		}catch(Throwable e){
			String msg = "Errore durante l'aggiornamento delle transazione relativamente all'informazione del server '"+transazioneApplicativoServer.getServizioApplicativoErogatore()+"': " + e.getLocalizedMessage();
			logCore.error("["+transazioneApplicativoServer.getIdTransazione()+"] "+msg,e);
		}
	}
	
	private static void aggiornaInformazioneConsegnaTerminata(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			String tipoDatabase, Logger logCore,
			IDAOFactory daoFactory, Logger logFactory, ServiceManagerProperties smpFactory,
			boolean debug,
			int esitoConsegnaMultipla, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, int ok,
			long gestioneSerializableDB_AttesaAttiva, int gestioneSerializableDB_CheckInterval) throws CoreException {
		
		boolean useSerializableMode = false;
		if(useSerializableMode) {
			_serializableMode_aggiornaInformazioneConsegnaTerminata(transazioneApplicativoServer, connectionDB,
					tipoDatabase, logCore,
					daoFactory, logFactory, smpFactory,
					debug,
					esitoConsegnaMultipla, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
					gestioneSerializableDB_AttesaAttiva, gestioneSerializableDB_CheckInterval);
		}
		else {
			
			int i = 0;
			if(transazioneApplicativoServer.getDataRegistrazione()==null) {
				i=2;
			}
//			else {
//				System.out.println("DATA REGISTRAZIONE '"+org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(transazioneApplicativoServer.getDataRegistrazione())+"'");
//			}
			for (; i < 3; i++) {
				
				// primo test: finestra di 5 minuti
				// secondo test: finestra di 1 ora
				// terzo test: senza finestra temporale
				
				//System.out.println("INTERVALLO '"+i+"'");
				
				Timestamp leftValue = null;
				Timestamp rightValue = null;
				if(i==0) {
					leftValue = new Timestamp(transazioneApplicativoServer.getDataRegistrazione().getTime() - (1000*60*5));
					rightValue = new Timestamp(transazioneApplicativoServer.getDataRegistrazione().getTime() + (1000*60*5));
				}
				else if(i==1) {
					leftValue = new Timestamp(transazioneApplicativoServer.getDataRegistrazione().getTime() - (1000*60*60));
					rightValue = new Timestamp(transazioneApplicativoServer.getDataRegistrazione().getTime() + (1000*60*60));
				}
			
				boolean USE_SERIALIZABLE = true;
				int row = _aggiornaInformazioneConsegnaTerminata(transazioneApplicativoServer, connectionDB,
						tipoDatabase, logCore,
						daoFactory, logFactory, smpFactory,
						debug,
						esitoConsegnaMultipla, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
						leftValue, rightValue,
						USE_SERIALIZABLE,
						gestioneSerializableDB_AttesaAttiva, gestioneSerializableDB_CheckInterval);
				if(row>0) {
					break;
				}
				
			}
			
		}
		
	}
	
	private static int _aggiornaInformazioneConsegnaTerminata(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			String tipoDatabase, Logger logCore,
			IDAOFactory daoFactory, Logger logFactory, ServiceManagerProperties smpFactory,
			boolean debug,
			int esitoConsegnaMultipla, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, int ok,
			Timestamp leftValue, Timestamp rightValue,
			boolean serializable,
			long gestioneSerializableDB_AttesaAttiva, int gestioneSerializableDB_CheckInterval) throws CoreException {
		
		if(serializable) {
			
			/*
		      Viene realizzato con livello di isolamento SERIALIZABLE, per essere sicuri
		      che esecuzioni parallele non leggano dati inconsistenti.
		      Con il livello SERIALIZABLE, se ritorna una eccezione, deve essere riprovato
		      La sincronizzazione e' necessaria per via del possibile accesso simultaneo del servizio Gop
		      e del servizio che si occupa di eliminare destinatari di messaggi
			 */
			// setAutoCommit e livello Isolamento
			int oldTransactionIsolation = -1;
			try{
				oldTransactionIsolation = connectionDB.getTransactionIsolation();
				// già effettuato fuori dal metodo connectionDB.setAutoCommit(false);
				JDBCUtilities.setTransactionIsolationSerializable(tipoDatabase, connectionDB);
			} catch(Exception er) {
				throw new CoreException("(setIsolation) "+er.getMessage(),er);
			}

			boolean updateEffettuato = false;
			
			long scadenzaWhile = DateManager.getTimeMillis() + gestioneSerializableDB_AttesaAttiva;
			
			int row = -1;
			while(updateEffettuato==false && DateManager.getTimeMillis() < scadenzaWhile){

				try{
					row = _aggiornaInformazioneConsegnaTerminata(transazioneApplicativoServer, connectionDB,
							tipoDatabase, logCore,
							daoFactory, logFactory, smpFactory,
							debug,
							esitoConsegnaMultipla, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
							leftValue, rightValue);

					updateEffettuato = true;

				} catch(Exception e) {
					//System.out.println("Serializable error:"+e.getMessage());
				}

				if(updateEffettuato == false){
					// Per aiutare ad evitare conflitti
					try{
						Utilities.sleep((new java.util.Random()).nextInt(gestioneSerializableDB_CheckInterval)); // random da 0ms a checkIntervalms
					}catch(Exception eRandom){}
				}
			}
			// Ripristino Transazione
			try{
				connectionDB.setTransactionIsolation(oldTransactionIsolation);
				// già effettuato fuori dal metodo connectionDB.setAutoCommit(true);
			} catch(Exception er) {
				throw new CoreException("(ripristinoIsolation) "+er.getMessage(),er);
			}
			
			return row;

		}
		else {
			
			return _aggiornaInformazioneConsegnaTerminata(transazioneApplicativoServer, connectionDB,
					tipoDatabase, logCore,
					daoFactory, logFactory, smpFactory,
					debug,
					esitoConsegnaMultipla, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
					leftValue, rightValue);
			
		}
	}
	
	private static int _aggiornaInformazioneConsegnaTerminata(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			String tipoDatabase, Logger logCore,
			IDAOFactory daoFactory, Logger logFactory, ServiceManagerProperties smpFactory,
			boolean debug,
			int esitoConsegnaMultipla, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, int ok,
			Timestamp leftValue, Timestamp rightValue) throws CoreException {
	
		PreparedStatement pstmt = null;
		try {
		
			TransazioneFieldConverter transazioneFieldConverter = new TransazioneFieldConverter(tipoDatabase);
			String dataIngressoRichiestaColumn = transazioneFieldConverter.toColumn(Transazione.model().DATA_INGRESSO_RICHIESTA, false);
			String idTransazioneColumn = transazioneFieldConverter.toColumn(Transazione.model().ID_TRANSAZIONE, false);
			String esitoColumn = transazioneFieldConverter.toColumn(Transazione.model().ESITO, false);
			String consegneMultipleInCorsoColumn = transazioneFieldConverter.toColumn(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO, false);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			
			sqlQueryObject.addUpdateTable(CostantiDB.TRANSAZIONI);
			
			sqlQueryObject.addUpdateField(consegneMultipleInCorsoColumn, consegneMultipleInCorsoColumn+"-1");
			
			if(ok != transazioneApplicativoServer.getDettaglioEsito() || transazioneApplicativoServer.getDataMessaggioScaduto()!=null) {
				/* UPDATE transazioni 
				 * 		SET consegne_multiple=consegne_multiple-1, 
				 * 		    esito=esitoConsegnaMultiplaFallita  
				 * WHERE (ESITO=esitoConsegnaMultipla OR ESITO=esitoConsegnaMultiplaFallita)
				 */
				sqlQueryObject.addUpdateField(esitoColumn,"?");   
			}
			else {
				 /*	UPDATE transazioni 
				 * 		SET consegne_multiple=consegne_multiple-1, 
				 *			esito = ( CASE
				 *					  WHEN (consegne_multiple=1 AND esito=esitoConsegnaMultipla) THEN esitoConsegnaMultiplaCompletata 
				 *					  ELSE  (ESITO)
				 *					END )
				 * WHERE (ESITO=esitoConsegnaMultipla OR ESITO=esitoConsegnaMultiplaFallita)
				 */
				Case caseValue = new Case(CastColumnType.INT, esitoColumn); 
				caseValue.addCase(consegneMultipleInCorsoColumn+"<=? AND "+esitoColumn+"=?", "?");
				sqlQueryObject.addUpdateField(esitoColumn, caseValue);
			}
			
			sqlQueryObject.setANDLogicOperator(true);
			
			if(leftValue!=null && rightValue!=null) {
				sqlQueryObject.addWhereBetweenCondition(dataIngressoRichiestaColumn, false, "?", "?");
			}
			
			sqlQueryObject.addWhereCondition(idTransazioneColumn+"=?");
			
			sqlQueryObject.addWhereCondition(false, 
					esitoColumn+"=?",
					esitoColumn+"=?");
			
			String updateCommand = sqlQueryObject.createSQLUpdate();
			//System.out.println("QUERY '"+updateCommand+"'");
			
			pstmt = connectionDB.prepareStatement(updateCommand);
			int index = 1;
			List<Object> params = new ArrayList<Object>();
			
			if(ok != transazioneApplicativoServer.getDettaglioEsito() || transazioneApplicativoServer.getDataMessaggioScaduto()!=null) {
				pstmt.setInt(index++, esitoConsegnaMultiplaFallita);
				params.add(esitoConsegnaMultiplaFallita);
			}
			else {
				pstmt.setInt(index++, 1); // il decremento nell'unico comando di update funziona transazionalmente; quindi quando lo porto a 0, il valore nella condizione è 1(lascio il <= per stare tranquillo)
				params.add(1);
				pstmt.setInt(index++, esitoConsegnaMultipla);
				params.add(esitoConsegnaMultipla);
				pstmt.setInt(index++, esitoConsegnaMultiplaCompletata);
				params.add(esitoConsegnaMultiplaCompletata);
			}
			
			if(leftValue!=null && rightValue!=null) {
				pstmt.setTimestamp(index++, leftValue);
				params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(leftValue));
				pstmt.setTimestamp(index++, rightValue);
				params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(rightValue));
			}
			
			pstmt.setString(index++, transazioneApplicativoServer.getIdTransazione());
			params.add(transazioneApplicativoServer.getIdTransazione());
			
			pstmt.setInt(index++, esitoConsegnaMultipla);
			params.add(esitoConsegnaMultipla);
			pstmt.setInt(index++, esitoConsegnaMultiplaFallita);
			params.add(esitoConsegnaMultiplaFallita);
			
			int row = pstmt.executeUpdate();
			if(row!=1) {
				if(logCore!=null) {
					String comandoSql = DBUtils.formatSQLString(updateCommand, params.toArray());
					if(row<1) {
						logCore.debug("Trovata transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"' per cui il comando di aggiornamento della transazione non ha comportato effetti: "+comandoSql);
					}
					else {
						throw new CoreException("Trovata più di una transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"' ??");
					}
				}
			}
			
			//String comandoSql = formatSQLString(updateCommand, params.toArray());
			//System.out.println("ROW: "+row+" (app:"+transazioneApplicativoServer.getConnettoreNome()+" id:"+transazioneApplicativoServer.getIdTransazione()+") sql:"+comandoSql);
			
			pstmt.close();
			pstmt = null;
			
			// Chiusura Transazione
			connectionDB.commit();
			
			return row;
			
		}catch(Throwable e) {
			
			try{
				if(pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch(Exception er) {}
			try{
				connectionDB.rollback();
			} catch(Exception er) {}
			
			throw new CoreException(e.getMessage(),e);
		}finally{
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Throwable t) {}
		}
		
	}
	
	@Deprecated
	private static void _serializableMode_aggiornaInformazioneConsegnaTerminata(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			String tipoDatabase, Logger logCore,
			IDAOFactory daoFactory, Logger logFactory, ServiceManagerProperties smpFactory,
			boolean debug,
			int esitoConsegnaMultipla, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, int ok,
			long gestioneSerializableDB_AttesaAttiva, int gestioneSerializableDB_CheckInterval) throws CoreException {
		
		/*
	      Viene realizzato con livello di isolamento SERIALIZABLE, per essere sicuri
	      che esecuzioni parallele non leggano dati inconsistenti.
	      Con il livello SERIALIZABLE, se ritorna una eccezione, deve essere riprovato
	      La sincronizzazione e' necessaria per via del possibile accesso simultaneo del servizio Gop
	      e del servizio che si occupa di eliminare destinatari di messaggi
		 */
		// setAutoCommit e livello Isolamento
		int oldTransactionIsolation = -1;
		try{
			oldTransactionIsolation = connectionDB.getTransactionIsolation();
			// già effettuato fuori dal metodo connectionDB.setAutoCommit(false);
			JDBCUtilities.setTransactionIsolationSerializable(tipoDatabase, connectionDB);
		} catch(Exception er) {
			throw new CoreException("(setIsolation) "+er.getMessage(),er);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean updateEffettuato = false;
		
		long scadenzaWhile = DateManager.getTimeMillis() + gestioneSerializableDB_AttesaAttiva;
		
		CoreException coreException = null;
		while(updateEffettuato==false && DateManager.getTimeMillis() < scadenzaWhile){

			try{	
				IDAOFactory daoF = daoFactory;
				Logger log = logFactory;
				ServiceManagerProperties smp = smpFactory;
				
				org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager jdbcServiceManager =
						(org.openspcoop2.core.transazioni.dao.jdbc.JDBCServiceManager) daoF.getServiceManager(org.openspcoop2.core.transazioni.utils.ProjectInfo.getInstance(), 
								connectionDB, false,
								smp, log);
				jdbcServiceManager.getJdbcProperties().setShowSql(debug);
				JDBCTransazioneService transazioneService = (JDBCTransazioneService) jdbcServiceManager.getTransazioneService();
				
				transazioneService.enableSelectForUpdate();
				
				IPaginatedExpression pagExpression = transazioneService.newPaginatedExpression();
				pagExpression.equals(Transazione.model().ID_TRANSAZIONE, transazioneApplicativoServer.getIdTransazione());
				List<Map<String, Object>> l = null;
				try {
					l = transazioneService.select(pagExpression, Transazione.model().ESITO, Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO);
				}catch (NotFoundException notfound) {}
				if(l!=null && l.size()>0) {
					if(l.size()>1) {
						coreException = new CoreException("Trovata più di una transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"'");
					}
					else {
												
						Object oEsito = l.get(0).get(Transazione.model().ESITO.getFieldName());
						int esito = -1;
						if(oEsito!=null && oEsito instanceof Integer) {
							esito = (Integer) oEsito;
						}
						
						Object oConsegneMultiple = l.get(0).get(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO.getFieldName());
						int consegneMultiple = -1;
						if(oConsegneMultiple!=null && oConsegneMultiple instanceof Integer) {
							consegneMultiple = (Integer) oConsegneMultiple;
						}
												
						if(esitoConsegnaMultipla == esito || esitoConsegnaMultiplaFallita == esito) {
							int decrement = consegneMultiple-1;
							UpdateField uFieldConsegneMultipleInCorso = new UpdateField(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO, decrement);
							UpdateField uFieldEsito = null;
							if(esitoConsegnaMultipla == esito) {
								// non appena c'è un errore, marca la transazione come fallita
								if(ok != transazioneApplicativoServer.getDettaglioEsito() || transazioneApplicativoServer.getDataMessaggioScaduto()!=null) {
									uFieldEsito = new UpdateField(Transazione.model().ESITO, esitoConsegnaMultiplaFallita);
								}
								else if(decrement<=0) {
									uFieldEsito = new UpdateField(Transazione.model().ESITO, esitoConsegnaMultiplaCompletata);
								}
							}
							if(uFieldEsito!=null) {
								transazioneService.updateFields(transazioneApplicativoServer.getIdTransazione(), uFieldConsegneMultipleInCorso, uFieldEsito);
							}
							else {
								transazioneService.updateFields(transazioneApplicativoServer.getIdTransazione(), uFieldConsegneMultipleInCorso);
							}
						}
						else if(esitoConsegnaMultiplaCompletata == esito) {
							if(logCore!=null) {
								logCore.debug("Trovata transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"', con un esito '"+esito+"' (ConsegnaMultiplaCompletata) già gestita da un altro nodo");
							}
						}
						else {
							coreException = new CoreException("Trovata transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"', con un esito '"+esito+"' non atteso");
						}

						
					}
				}

				transazioneService.disableSelectForUpdate();
				
				// Chiusura Transazione
				connectionDB.commit();

				// ID Costruito
				updateEffettuato = true;

			} catch(Exception e) {
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				try{
					connectionDB.rollback();
				} catch(Exception er) {}
			}

			if(updateEffettuato == false){
				// Per aiutare ad evitare conflitti
				try{
					Utilities.sleep((new java.util.Random()).nextInt(gestioneSerializableDB_CheckInterval)); // random da 0ms a checkIntervalms
				}catch(Exception eRandom){}
			}
		}
		// Ripristino Transazione
		try{
			connectionDB.setTransactionIsolation(oldTransactionIsolation);
			// già effettuato fuori dal metodo connectionDB.setAutoCommit(true);
		} catch(Exception er) {
			throw new CoreException("(ripristinoIsolation) "+er.getMessage(),er);
		}

		if(coreException!=null) {
			throw coreException;
		}
	}
}
