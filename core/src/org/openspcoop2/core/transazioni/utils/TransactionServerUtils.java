/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.security.SecureRandom;
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
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCTransazioneApplicativoServerService;
import org.openspcoop2.core.transazioni.dao.jdbc.JDBCTransazioneService;
import org.openspcoop2.core.transazioni.dao.jdbc.converter.TransazioneFieldConverter;
import org.openspcoop2.generic_project.beans.UpdateField;
import org.openspcoop2.generic_project.exception.MultipleResultException;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IPaginatedExpression;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
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

	private TransactionServerUtils() {}
	
	// *** SAVE ***
	
	public static boolean recover(ITransazioneApplicativoServerService transazioneService, TransazioneApplicativoServer serverInfoParam) throws CoreException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException  {
		
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
			return save(transazioneService, serverInfoParam, false, false, true, false, null);		
		}
		else {
			IdTransazioneApplicativoServer idTransazioneApplicativoServer = new IdTransazioneApplicativoServer();
			idTransazioneApplicativoServer.setIdTransazione(idTransazione);
			idTransazioneApplicativoServer.setServizioApplicativoErogatore(servizioApplicativoErogatore);
			if(transazioneService.exists(idTransazioneApplicativoServer)) {
				
				TransazioneApplicativoServer transazioneApplicativoServer = transazioneService.get(idTransazioneApplicativoServer);
				if(!transazioneApplicativoServer.isConsegnaTerminata() && transazioneApplicativoServer.getDataEliminazioneIm()==null && transazioneApplicativoServer.getDataMessaggioScaduto()==null) {
					boolean useSelectForUpdate = true;
					/*
					 * Grazie alla select for update riesco a mettere il lock solamente sulla riga interessata
					 */
					return save(transazioneService, serverInfoParam, true, false, true, useSelectForUpdate, null);
				}
				// else ho già registrato l'ultima informazione, è inutile fare update delle informazioni parziali.
				else {
					return false;
				}
			}
			else {
				throw new CoreException("In attesa della registrazione dell'informazione capostipite");
			}
		}
		
	}
	
	public static boolean save(ITransazioneApplicativoServerService transazioneService, TransazioneApplicativoServer serverInfoParam, boolean update, boolean throwNotFoundIfNotExists, boolean recover,
			boolean useSelectForUpdate, List<String> timeDetails) throws CoreException, ServiceException, NotImplementedException, NotFoundException, MultipleResultException {
		
		if(recover) {
			// nop
		}
		
		String idTransazione = serverInfoParam.getIdTransazione();
		if(idTransazione==null) {
			throw new CoreException("IdTransazione non esistente nel contesto");
		}
		
		String servizioApplicativoErogatore = serverInfoParam.getServizioApplicativoErogatore();
		if(servizioApplicativoErogatore==null) {
			throw new CoreException("Id servizioApplicativoErogatore non esistente nel contesto");
		}
		
		/**System.out.println("SAVE id["+idTransazione+"] ["+servizioApplicativoErogatore+"] update:"+update+" ....");*/
		
		String protocol = serverInfoParam.getProtocollo();
		if(protocol==null) {
			throw new CoreException("Protocollo non esistente nel contesto");
		}
		
		IdTransazioneApplicativoServer idTransazioneApplicativoServer = new IdTransazioneApplicativoServer();
		idTransazioneApplicativoServer.setIdTransazione(idTransazione);
		idTransazioneApplicativoServer.setServizioApplicativoErogatore(servizioApplicativoErogatore);
		
		
		
		if(update) {
			
			long timeStart = -1;
			if(timeDetails!=null) {
				timeStart = DateManager.getTimeMillis();
			}
			
			// prima era inefficiente: faceva 2 query
			// adesso e' stata trasformata in efficiente e fa una unica query con limit 1
			TransazioneApplicativoServer transazioneApplicativoServerReadFromDB = null;
			IdTransazioneApplicativoServer idTransazioneApplicativoServerReadFromDB = null;
			if(useSelectForUpdate) {
				JDBCTransazioneApplicativoServerService transazioneServiceSearch = (JDBCTransazioneApplicativoServerService) transazioneService;
				try {
					transazioneServiceSearch.enableSelectForUpdate();
					transazioneApplicativoServerReadFromDB = transazioneService.get(idTransazioneApplicativoServer);
				}finally {
					transazioneServiceSearch.disableSelectForUpdate();
				}
				// con la select for update devo poi usare lo stesso comando di WHERE poichè ho messo un lock sulla riga
				// uso quindi idTransazioneApplicativoServer che non possiede l'id long settato
				idTransazioneApplicativoServerReadFromDB = idTransazioneApplicativoServer; 
			}
			else{
				transazioneApplicativoServerReadFromDB = transazioneService.get(idTransazioneApplicativoServer);
				idTransazioneApplicativoServerReadFromDB = new IdTransazioneApplicativoServer();
				idTransazioneApplicativoServerReadFromDB.setId(transazioneApplicativoServerReadFromDB.getId());
			}
			
			if(timeDetails!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				timeDetails.add("readFromDB:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			if(throwNotFoundIfNotExists) {
				// non più usato
			}
			/**
			 * Leggermente inefficiente per via del limit 2 con cui viene implementata la find
			org.openspcoop2.generic_project.expression.IExpression expression = transazioneService.newExpression();
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
			}*/
			
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
				if(transazioneApplicativoServerReadFromDB.getDataPrimoPrelievoIm()==null &&
					serverInfoParam.getDataPrelievoIm()!=null) {
					transazioneApplicativoServerReadFromDB.setDataPrimoPrelievoIm(serverInfoParam.getDataPrelievoIm()); // primo tentativo di prelievo
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
				transazioneApplicativoServerReadFromDB.setDataUscitaRichiestaStream(serverInfoParam.getDataUscitaRichiestaStream());
				transazioneApplicativoServerReadFromDB.setDataAccettazioneRisposta(serverInfoParam.getDataAccettazioneRisposta());
				transazioneApplicativoServerReadFromDB.setDataIngressoRisposta(serverInfoParam.getDataIngressoRisposta());
				transazioneApplicativoServerReadFromDB.setDataIngressoRispostaStream(serverInfoParam.getDataIngressoRispostaStream());
				
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
				if(transazioneApplicativoServerReadFromDB.getDataPrimoTentativo()==null &&
					serverInfoParam.getDataUscitaRichiesta()!=null) {
					transazioneApplicativoServerReadFromDB.setDataPrimoTentativo(serverInfoParam.getDataUscitaRichiesta()); // primo tentativo di consegna
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
			
			if(timeDetails!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				timeDetails.add("prepareUpdate:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			transazioneService.update(idTransazioneApplicativoServerReadFromDB, transazioneApplicativoServerReadFromDB);
			
			if(timeDetails!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				timeDetails.add("update:"+timeProcess);
			}
			
			return true;
		}
		else {
			
			long timeStart = -1;
			if(timeDetails!=null) {
				timeStart = DateManager.getTimeMillis();
			}
			
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
			
			if(timeDetails!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				timeDetails.add("prepareInsert:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			// CREO
			transazioneService.create(transazioneApplicativoServer);

			if(timeDetails!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				timeDetails.add("insert:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			return true;
		}

	}
	
	public static boolean safeAggiornaInformazioneConsegnaTerminata(TransazioneApplicativoServer transazioneApplicativoServer, Connection con, 
			String tipoDatabase, Logger logCore,
			IDAOFactory daoFactory, Logger logFactory, ServiceManagerProperties smpFactory,
			boolean debug,
			int esitoConsegnaMultipla, int esitoConsegnaMultiplaInCorso, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, int ok,
			int esitoIntegrationManagerSingolo, boolean possibileTerminazioneSingleIntegrationManagerMessage, boolean consegnaInErrore,
			long gestioneSerializableDBAttesaAttiva, int gestioneSerializableDBCheckInterval,
			List<String> timeDetails) {
		
		// aggiorno esito transazione
		try{
			return TransactionServerUtils.aggiornaInformazioneConsegnaTerminata(transazioneApplicativoServer, con, 
					tipoDatabase, logCore,
					daoFactory, logFactory, smpFactory,
					debug,
					esitoConsegnaMultipla, esitoConsegnaMultiplaInCorso, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
					esitoIntegrationManagerSingolo, possibileTerminazioneSingleIntegrationManagerMessage, consegnaInErrore,
					gestioneSerializableDBAttesaAttiva, gestioneSerializableDBCheckInterval,
					timeDetails);
		}catch(Throwable e){
			/**System.out.println("["+transazioneApplicativoServer.getConnettoreNome()+"] ["+transazioneApplicativoServer.getIdTransazione()+"] ERRORE AGGIORNAMENTO: "+e.getMessage());
			e.printStackTrace(System.out);*/
			String msg = "Errore durante l'aggiornamento delle transazione relativamente all'informazione del server: " + e.getLocalizedMessage();
			logCore.error("[id:"+transazioneApplicativoServer.getIdTransazione()+"][sa:"+transazioneApplicativoServer.getServizioApplicativoErogatore()+"]["+transazioneApplicativoServer.getConnettoreNome()+"] "+msg,e);
			return false;
		}
	}
	
	private static boolean aggiornaInformazioneConsegnaTerminata(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			String tipoDatabase, Logger logCore,
			IDAOFactory daoFactory, Logger logFactory, ServiceManagerProperties smpFactory,
			boolean debug,
			int esitoConsegnaMultipla, int esitoConsegnaMultiplaInCorso, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, int ok,
			int esitoIntegrationManagerSingolo, boolean possibileTerminazioneSingleIntegrationManagerMessage, boolean consegnaInErrore,
			long gestioneSerializableDBAttesaAttiva, int gestioneSerializableDBCheckInterval,
			List<String> timeDetails) throws CoreException {
		
		boolean consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda = false;
		
		if(possibileTerminazioneSingleIntegrationManagerMessage || consegnaInErrore) {
			// possibileTerminazioneSingleIntegrationManagerMessage
			// Devo comprendere se l'eliminazione del messaggio, effettuata via I.M o tramite scadenza, riguarda la configurazione su connettori multipli o configurazione standard con unico connettore.
			// Se e' su configurazione standard con unico connettore non devo aggiornare le informazioni sulla consegna terminata
			
			// possibilePrimaConsegnaNonAndataABuonFine
			// Devo comprendere se si tratta di una prima consegna, non completata. Serve per aggiornare lo stato della transazione da "In coda" a "In corso"
			
			int i = 0;
			if(transazioneApplicativoServer.getDataRegistrazione()==null) {
				i=2;
			}
/**			else {
				System.out.println("DATA REGISTRAZIONE '"+org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(transazioneApplicativoServer.getDataRegistrazione())+"'");
			}*/
			int esitoTransazione = -1;
			for (; i < 3; i++) {
				
				long timeStart = -1;
				if(timeDetails!=null) {
					timeStart = DateManager.getTimeMillis();
				}
				
				// primo test: finestra di 5 minuti
				// secondo test: finestra di 1 ora
				// terzo test: senza finestra temporale
				
				/**System.out.println("INTERVALLO '"+i+"'");*/
				
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
			
				esitoTransazione = getEsitoTransazione(transazioneApplicativoServer, connectionDB,
						tipoDatabase, logCore,
						leftValue, rightValue);
				
				if(timeDetails!=null) {
					long timeEnd =  DateManager.getTimeMillis();
					long timeProcess = timeEnd-timeStart;
					timeDetails.add("getEsitoIM-"+i+":"+timeProcess);
				}
				
				if(esitoTransazione>=0) {
					// transazione trovata
					if(possibileTerminazioneSingleIntegrationManagerMessage &&
						esitoIntegrationManagerSingolo == esitoTransazione) {
						/**System.out.println("TROVATA IM; termino aggiornamento");*/
						return true; // non devo gestire l'update previsto nei connettori multipli gestito dopo
					}
					if(consegnaInErrore) {
						if(esitoConsegnaMultipla == esitoTransazione) {
							consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda = true;
						}
						else {
							/**System.out.println("TROVATA TRANSAZIONE CON ESITO "+esitoTransazione+" non "in coda"; termino aggiornamento");*/
							return true; // non devo gestire l'update previsto nei connettori multipli gestito dopo
						}
					}
					/**else {
						System.out.println("ESCO e procedo con update normale ["+i+"]");
					}*/
					break;
				}
				/**else {
					System.out.println("NON TROVATA TRANSAZIONE a QUESTO GIRO ["+i+"]");
				}*/
				
			}
			if(esitoTransazione<0) {
				// registro ultimo errore avvenuto durante il ciclo in entrambi i log
				String msgError = "[id:"+transazioneApplicativoServer.getIdTransazione()+"][sa:"+transazioneApplicativoServer.getServizioApplicativoErogatore()+"]["+transazioneApplicativoServer.getConnettoreNome()+"] 'getEsitoTransazione' non riuscta. Tutti gli intervalli non hanno consentito di inviduare la transazione";
				logFactory.error(msgError);
				logCore.error(msgError);
			}
			
		}
		
		return aggiornaInformazioneConsegnaTerminataEngine(transazioneApplicativoServer, connectionDB,
				tipoDatabase, logCore,
				daoFactory, logFactory, smpFactory,
				debug,
				esitoConsegnaMultipla, esitoConsegnaMultiplaInCorso, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
				consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda,
				gestioneSerializableDBAttesaAttiva, gestioneSerializableDBCheckInterval,
				timeDetails);
	}
	private static boolean aggiornaInformazioneConsegnaTerminataEngine(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			String tipoDatabase, Logger logCore,
			IDAOFactory daoFactory, Logger logFactory, ServiceManagerProperties smpFactory,
			boolean debug,
			int esitoConsegnaMultipla, int esitoConsegnaMultiplaInCorso, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, int ok,
			boolean consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda,
			long gestioneSerializableDBAttesaAttiva, int gestioneSerializableDBCheckInterval,
			List<String> timeDetails) throws CoreException {
		
		boolean useSerializableMode = false;
		if(useSerializableMode) {
			serializableModeAggiornaInformazioneConsegnaTerminataEngine(transazioneApplicativoServer, connectionDB,
					tipoDatabase, logCore,
					daoFactory, logFactory, smpFactory,
					debug,
					esitoConsegnaMultipla, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
					consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda,
					gestioneSerializableDBAttesaAttiva, gestioneSerializableDBCheckInterval);
			return true; // non gestisco l'informazione ritornata essendo il metodo deprecato
		}
		else {
			
			boolean transazioneGiaModificata = false;
			
			int i = 0;
			if(transazioneApplicativoServer.getDataRegistrazione()==null) {
				i=2;
			}
/**			else {
				System.out.println("DATA REGISTRAZIONE '"+org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(transazioneApplicativoServer.getDataRegistrazione())+"'");
			}*/
			int row = -1;
			for (; i < 3; i++) {
				
				long timeStart = -1;
				if(timeDetails!=null) {
					timeStart = DateManager.getTimeMillis();
				}
				
				// primo test: finestra di 5 minuti
				// secondo test: finestra di 1 ora
				// terzo test: senza finestra temporale
				
				/**System.out.println("["+transazioneApplicativoServer.getConnettoreNome()+"] ["+transazioneApplicativoServer.getIdTransazione()+"] INTERVALLO '"+i+"'");*/
				
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
			
				boolean useSERIALIZABLE = true;
				StringBuilder sbConflict = new StringBuilder();
				row = aggiornaInformazioneConsegnaTerminataEngine(transazioneApplicativoServer, connectionDB,
						tipoDatabase, logCore,
						logFactory, 
						esitoConsegnaMultipla, esitoConsegnaMultiplaInCorso, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
						consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda,
						leftValue, rightValue,
						useSERIALIZABLE,
						gestioneSerializableDBAttesaAttiva, gestioneSerializableDBCheckInterval,
						sbConflict);
				
				if(timeDetails!=null) {
					long timeEnd =  DateManager.getTimeMillis();
					long timeProcess = timeEnd-timeStart;
					String c = "";
					if(useSERIALIZABLE &&
						sbConflict.length()>0) {
						c="/c"+sbConflict.toString();
					}
					timeDetails.add("updateInfo-"+i+":"+timeProcess+c);
				}
				
				if(row>0) {
					break;
				}
				else if(consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda) {
					/**System.out.println("["+transazioneApplicativoServer.getConnettoreNome()+"] ["+transazioneApplicativoServer.getIdTransazione()+"] existsTransazioneIniziataGestioneConsegnaAsincrona ...");*/
					// in questo caso si sta cercando di aggiornare l'esito:
					//   UPDATE transazioni SET esito = 48 WHERE ( .... ) ( esito=38 )
					// ma se una transazione in parallelo l'ha già modificato si ottiene un row=0 che faceva ritornare false questo metodo, faceva scaturire la registrazione su file e poi l'aggiornamenti di avvenuta consegna che non era corretta.
					transazioneGiaModificata = existsTransazioneIniziataGestioneConsegnaAsincrona(transazioneApplicativoServer, connectionDB,
							tipoDatabase, logCore,
							esitoConsegnaMultiplaInCorso, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, 
							leftValue, rightValue);
					if(transazioneGiaModificata) {
						/**System.out.println("["+transazioneApplicativoServer.getConnettoreNome()+"] ["+transazioneApplicativoServer.getIdTransazione()+"] existsTransazioneIniziataGestioneConsegnaAsincrona FIX");*/
						break;
					}
					/**else {
						System.out.println("["+transazioneApplicativoServer.getConnettoreNome()+"] ["+transazioneApplicativoServer.getIdTransazione()+"] existsTransazioneIniziataGestioneConsegnaAsincrona NO");
					}*/
				}
				
			}
			if(row<=0 && !transazioneGiaModificata) {
				// registro ultimo errore avvenuto durante il ciclo in entrambi i log
				String msgError = "[id:"+transazioneApplicativoServer.getIdTransazione()+"][sa:"+transazioneApplicativoServer.getServizioApplicativoErogatore()+"]["+transazioneApplicativoServer.getConnettoreNome()+"] 'aggiornaInformazioneConsegnaTerminata' non riuscta. Tutti gli intervalli di update non hanno comportato un aggiornamento della transazione";
				logFactory.error(msgError);
				logCore.error(msgError);
				/**System.out.println("["+transazioneApplicativoServer.getConnettoreNome()+"] ["+transazioneApplicativoServer.getIdTransazione()+"] INTERVALLO '"+i+"' FINITO OK FALSE ========================================");*/
				return false;
			}
			else {
				/**System.out.println("["+transazioneApplicativoServer.getConnettoreNome()+"] ["+transazioneApplicativoServer.getIdTransazione()+"] INTERVALLO '"+i+"' FINITO TRUE");*/
				return true;
			}
		}
		
	}
	
	private static java.util.Random rndInstance = null;
	private static synchronized void initRandom() {
		if(rndInstance==null) {
			rndInstance = new SecureRandom();
		}
	}
	protected static java.util.Random getRandom() {
		if(rndInstance==null) {
			initRandom();
		}
		return rndInstance;
	}
	
	private static int aggiornaInformazioneConsegnaTerminataEngine(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			String tipoDatabase, Logger logCore,
			Logger logFactory, 
			int esitoConsegnaMultipla, int esitoConsegnaMultiplaInCorso, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, int ok,
			boolean consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda,
			Timestamp leftValue, Timestamp rightValue,
			boolean serializable,
			long gestioneSerializableDBAttesaAttiva, int gestioneSerializableDBCheckInterval,
			StringBuilder sbConflict) throws CoreException {
		
		/**System.out.println("SERIALIZABLE ("+transazioneApplicativoServer.getConnettoreNome()+"): "+serializable);*/
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
				/** già effettuato fuori dal metodo connectionDB.setAutoCommit(false);*/
				JDBCUtilities.setTransactionIsolationSerializable(tipoDatabase, connectionDB);
			} catch(Exception er) {
				throw new CoreException("(setIsolation) "+er.getMessage(),er);
			}

			boolean updateEffettuato = false;
			
			long scadenzaWhile = DateManager.getTimeMillis() + gestioneSerializableDBAttesaAttiva;
			
			int row = -1;
			Throwable lastT = null;
			int conflitti = 0;
			while(!updateEffettuato && DateManager.getTimeMillis() < scadenzaWhile){

				try{
					row = aggiornaInformazioneConsegnaTerminataEngine(transazioneApplicativoServer, connectionDB,
							tipoDatabase, logCore,
							esitoConsegnaMultipla, esitoConsegnaMultiplaInCorso, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
							consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda,
							leftValue, rightValue);

					updateEffettuato = true;
					/**System.out.println("["+transazioneApplicativoServer.getConnettoreNome()+"] ["+transazioneApplicativoServer.getIdTransazione()+"] OK row:"+row);*/

				} catch(Throwable e) {
					lastT = e;
					/**System.out.println("Serializable error ("+transazioneApplicativoServer.getConnettoreNome()+")  ["+transazioneApplicativoServer.getIdTransazione()+"] (row:"+row+" updateEffettuato:"+updateEffettuato+"):"+e.getMessage());*/
				}

				if(!updateEffettuato){
					// Per aiutare ad evitare conflitti
					try{
						Utilities.sleep(getRandom().nextInt(gestioneSerializableDBCheckInterval)); // random da 0ms a checkIntervalms
					}catch(Exception eRandom){
						// random
					}
					conflitti++;
				}
			}
			sbConflict.append(conflitti+"/updated:"+updateEffettuato);
			// Ripristino Transazione
			try{
				connectionDB.setTransactionIsolation(oldTransactionIsolation);
				/** già effettuato fuori dal metodo connectionDB.setAutoCommit(true);*/
			} catch(Exception er) {
				throw new CoreException("(ripristinoIsolation) "+er.getMessage(),er);
			}
			if(lastT!=null && !updateEffettuato) {
				// registro ultimo errore avvenuto durante il ciclo in entrambi i log
				String date = "";
				if(leftValue!=null || rightValue!=null) {
					StringBuilder sb = new StringBuilder(" [");
					if(leftValue!=null) {
						sb.append(DateUtils.getSimpleDateFormatMs().format(leftValue));
						if(rightValue!=null) {
							sb.append(" - ");
						}
					}
					if(rightValue!=null) {
						sb.append(DateUtils.getSimpleDateFormatMs().format(rightValue));
					}
					sb.append("]");
					date = sb.toString();
				}
				String msgError = "[id:"+transazioneApplicativoServer.getIdTransazione()+"][sa:"+transazioneApplicativoServer.getServizioApplicativoErogatore()+"]["+transazioneApplicativoServer.getConnettoreNome()+"] 'aggiornaInformazioneConsegnaTerminata'"+date+" failed: "+lastT.getMessage();
				logFactory.error(msgError,lastT);
				logCore.error(msgError,lastT);
			}
			
			return row;

		}
		else {
			
			return aggiornaInformazioneConsegnaTerminataEngine(transazioneApplicativoServer, connectionDB,
					tipoDatabase, logCore,
					esitoConsegnaMultipla, esitoConsegnaMultiplaInCorso, esitoConsegnaMultiplaFallita, esitoConsegnaMultiplaCompletata, ok,
					consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda,
					leftValue, rightValue);
			
		}
	}
	
	private static int aggiornaInformazioneConsegnaTerminataEngine(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			String tipoDatabase, Logger logCore,
			int esitoConsegnaMultipla, int esitoConsegnaMultiplaInCorso, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, int ok,
			boolean consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda,
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
			
			if(consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda) {
				/* UPDATE transazioni 
				 * 		SET esito=esitoConsegnaMultiplaInCorso  
				 * WHERE (ESITO=esitoConsegnaMultipla)
				 */
				sqlQueryObject.addUpdateField(esitoColumn,"?");   
			}
			else {
			
				sqlQueryObject.addUpdateField(consegneMultipleInCorsoColumn, consegneMultipleInCorsoColumn+"-1");
				
				if(ok != transazioneApplicativoServer.getDettaglioEsito() || transazioneApplicativoServer.getDataMessaggioScaduto()!=null) {
					/* UPDATE transazioni 
					 * 		SET consegne_multiple=consegne_multiple-1, 
					 * 		    esito=esitoConsegnaMultiplaFallita  
					 * WHERE (ESITO=esitoConsegnaMultipla OR ESITO=esitoConsegnaMultiplaInCorso OR ESITO=esitoConsegnaMultiplaFallita)
					 */
					sqlQueryObject.addUpdateField(esitoColumn,"?");   
				}
				else {
					 /*	UPDATE transazioni 
					 * 		SET consegne_multiple=consegne_multiple-1, 
					 *			esito = ( CASE
					 *					  WHEN (consegne_multiple<=1 AND (esito=esitoConsegnaMultipla OR esito=esitoConsegnaMultiplaInCorso)) THEN esitoConsegnaMultiplaCompletata 
					 *					  WHEN (consegne_multiple>1 AND esito=esitoConsegnaMultipla) THEN esitoConsegnaMultiplaInCorso 
					 *					  ELSE  (ESITO)
					 *					END )
					 * WHERE (ESITO=esitoConsegnaMultipla OR ESITO=esitoConsegnaMultiplaInCorso OR ESITO=esitoConsegnaMultiplaFallita)
					 */
					Case caseValue = new Case(CastColumnType.INT, esitoColumn); 
					caseValue.addCase(consegneMultipleInCorsoColumn+"<=? AND ("+esitoColumn+"=? OR "+esitoColumn+"=?)", "?");
					caseValue.addCase(consegneMultipleInCorsoColumn+">? AND "+esitoColumn+"=?", "?");
					sqlQueryObject.addUpdateField(esitoColumn, caseValue);
				}
				
			}
			
			sqlQueryObject.setANDLogicOperator(true);
			
			if(leftValue!=null && rightValue!=null) {
				sqlQueryObject.addWhereBetweenCondition(dataIngressoRichiestaColumn, false, "?", "?");
			}
			
			sqlQueryObject.addWhereCondition(idTransazioneColumn+"=?");
			
			if(consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda) {
				sqlQueryObject.addWhereCondition(esitoColumn+"=?");
			}
			else {
				sqlQueryObject.addWhereCondition(false, 
						esitoColumn+"=?",
						esitoColumn+"=?",
						esitoColumn+"=?");
			}
			
			String updateCommand = sqlQueryObject.createSQLUpdate();
			/**System.out.println("QUERY '"+updateCommand+"'");*/
			
			pstmt = connectionDB.prepareStatement(updateCommand);
			int index = 1;
			List<Object> params = new ArrayList<>();
			
			if(consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda) {
				pstmt.setInt(index++, esitoConsegnaMultiplaInCorso);
				params.add(esitoConsegnaMultiplaInCorso);
			}
			else {
				if(ok != transazioneApplicativoServer.getDettaglioEsito() || transazioneApplicativoServer.getDataMessaggioScaduto()!=null) {
					pstmt.setInt(index++, esitoConsegnaMultiplaFallita);
					params.add(esitoConsegnaMultiplaFallita);
				}
				else {
					// primo case
					pstmt.setInt(index++, 1); // il decremento nell'unico comando di update funziona transazionalmente; quindi quando lo porto a 0, il valore nella condizione è 1(lascio il <= per stare tranquillo)
					params.add(1);
					pstmt.setInt(index++, esitoConsegnaMultipla);
					params.add(esitoConsegnaMultipla);
					pstmt.setInt(index++, esitoConsegnaMultiplaInCorso);
					params.add(esitoConsegnaMultiplaInCorso);
					pstmt.setInt(index++, esitoConsegnaMultiplaCompletata);
					params.add(esitoConsegnaMultiplaCompletata);
					// secondo case
					pstmt.setInt(index++, 1);
					params.add(1);
					pstmt.setInt(index++, esitoConsegnaMultipla);
					params.add(esitoConsegnaMultipla);
					pstmt.setInt(index++, esitoConsegnaMultiplaInCorso);
					params.add(esitoConsegnaMultiplaInCorso);
				}
			}
			
			if(leftValue!=null && rightValue!=null) {
				pstmt.setTimestamp(index++, leftValue);
				params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(leftValue));
				pstmt.setTimestamp(index++, rightValue);
				params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(rightValue));
			}
			
			pstmt.setString(index++, transazioneApplicativoServer.getIdTransazione());
			params.add(transazioneApplicativoServer.getIdTransazione());
			
			if(consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda) {
				pstmt.setInt(index++, esitoConsegnaMultipla);
				params.add(esitoConsegnaMultipla);
			}
			else {
				pstmt.setInt(index++, esitoConsegnaMultipla);
				params.add(esitoConsegnaMultipla);
				pstmt.setInt(index++, esitoConsegnaMultiplaInCorso);
				params.add(esitoConsegnaMultiplaInCorso);
				pstmt.setInt(index++, esitoConsegnaMultiplaFallita);
				params.add(esitoConsegnaMultiplaFallita);
			}
			
			int row = pstmt.executeUpdate();
			if(row!=1 &&
				logCore!=null) {
				String comandoSql = DBUtils.formatSQLString(updateCommand, params.toArray());
				String prefix = "Trovata transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"' ";
				if(row<1) {
					String msg = prefix+"per cui il comando di aggiornamento della transazione non ha comportato effetti: "+comandoSql;
					logCore.debug(msg);
				}
				else {
					throw new CoreException(prefix+"??");
				}
			}
			
			/**String comandoSql = DBUtils.formatSQLString(updateCommand, params.toArray());
			System.out.println("ROW: "+row+" (app:"+transazioneApplicativoServer.getConnettoreNome()+" id:"+transazioneApplicativoServer.getIdTransazione()+") sql:"+comandoSql);*/
			
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
			} catch(Exception er) {
				// ignore
			}
			try{
				connectionDB.rollback();
			} catch(Exception er) {
				// ignore
			}
			
			throw new CoreException(e.getMessage(),e);
		}finally{
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Throwable t) {
				// ignore
			}
		}
		
	}
	private static boolean existsTransazioneIniziataGestioneConsegnaAsincrona(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			String tipoDatabase, Logger logCore,
			int esitoConsegnaMultiplaInCorso, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, 
			Timestamp leftValue, Timestamp rightValue) throws CoreException {
	
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
		
			TransazioneFieldConverter transazioneFieldConverter = new TransazioneFieldConverter(tipoDatabase);
			String dataIngressoRichiestaColumn = transazioneFieldConverter.toColumn(Transazione.model().DATA_INGRESSO_RICHIESTA, false);
			String idTransazioneColumn = transazioneFieldConverter.toColumn(Transazione.model().ID_TRANSAZIONE, false);
			String esitoColumn = transazioneFieldConverter.toColumn(Transazione.model().ESITO, false);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			
			String countAlias = "conteggio";
			sqlQueryObject.addSelectCountField(idTransazioneColumn, countAlias);
			
			sqlQueryObject.addFromTable(CostantiDB.TRANSAZIONI);
						
			sqlQueryObject.setANDLogicOperator(true);
			
			if(leftValue!=null && rightValue!=null) {
				sqlQueryObject.addWhereBetweenCondition(dataIngressoRichiestaColumn, false, "?", "?");
			}
			
			sqlQueryObject.addWhereCondition(idTransazioneColumn+"=?");
			
			sqlQueryObject.addWhereCondition(false, 
						esitoColumn+"=?",
						esitoColumn+"=?",
						esitoColumn+"=?");
			
			String queryCommand = sqlQueryObject.createSQLQuery();
			/**System.out.println("QUERY '"+queryCommand+"'");*/
			
			pstmt = connectionDB.prepareStatement(queryCommand);
			int index = 1;
			List<Object> params = new ArrayList<>();
						
			if(leftValue!=null && rightValue!=null) {
				pstmt.setTimestamp(index++, leftValue);
				params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(leftValue));
				pstmt.setTimestamp(index++, rightValue);
				params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(rightValue));
			}
			
			pstmt.setString(index++, transazioneApplicativoServer.getIdTransazione());
			params.add(transazioneApplicativoServer.getIdTransazione());
			
			pstmt.setInt(index++, esitoConsegnaMultiplaInCorso);
			params.add(esitoConsegnaMultiplaInCorso);
			pstmt.setInt(index++, esitoConsegnaMultiplaFallita);
			params.add(esitoConsegnaMultiplaFallita);
			pstmt.setInt(index++, esitoConsegnaMultiplaCompletata);
			params.add(esitoConsegnaMultiplaCompletata);
						
			int count = 0;
			rs = pstmt.executeQuery();
			if(rs.next()) {
				count = rs.getInt(countAlias);
			}
			else if(logCore!=null) {
				String comandoSql = DBUtils.formatSQLString(queryCommand, params.toArray());
				if(count<1) {
					String msg = "Non trovata transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"' già in gestione: "+comandoSql;
					logCore.debug(msg);
				}
				else {
					throw new CoreException("Trovata più di una transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"' ??");
				}
			}
			
			/**String comandoSql = DBUtils.formatSQLString(queryCommand, params.toArray());
			System.out.println("existsTransazioneIniziataGestioneConsegnaAsincrona Count: "+count+" (app:"+transazioneApplicativoServer.getConnettoreNome()+" id:"+transazioneApplicativoServer.getIdTransazione()+") sql:"+comandoSql);*/
			
			rs.close();
			rs = null;
			pstmt.close();
			pstmt = null;
			
			return count>0;
			
		}catch(Throwable e) {
			
			try{
				if(rs != null) {
					rs.close();
					rs = null;
				}
			} catch(Exception er) {
				// ignore
			}
			try{
				if(pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch(Exception er) {
				// ignore
			}
			try{
				connectionDB.rollback();
			} catch(Exception er) {
				// ignore
			}
			
			throw new CoreException(e.getMessage(),e);
		}finally{
			try{
				if(rs != null) {
					rs.close();
				}
			} catch(Exception er) {
				// ignore
			}
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Throwable t) {
				// ignore
			}
		}
		
	}
	
	private static int getEsitoTransazione(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			String tipoDatabase, Logger logCore,
			Timestamp leftValue, Timestamp rightValue) throws CoreException {
	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
		
			TransazioneFieldConverter transazioneFieldConverter = new TransazioneFieldConverter(tipoDatabase);
			String dataIngressoRichiestaColumn = transazioneFieldConverter.toColumn(Transazione.model().DATA_INGRESSO_RICHIESTA, false);
			String idTransazioneColumn = transazioneFieldConverter.toColumn(Transazione.model().ID_TRANSAZIONE, false);
			String esitoColumn = transazioneFieldConverter.toColumn(Transazione.model().ESITO, false);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			
			sqlQueryObject.addFromTable(CostantiDB.TRANSAZIONI);
			
			sqlQueryObject.addSelectField(esitoColumn);
			
			sqlQueryObject.setANDLogicOperator(true);
			
			if(leftValue!=null && rightValue!=null) {
				sqlQueryObject.addWhereBetweenCondition(dataIngressoRichiestaColumn, false, "?", "?");
			}
			
			sqlQueryObject.addWhereCondition(idTransazioneColumn+"=?");
						
			String select = sqlQueryObject.createSQLQuery();
			/**System.out.println("QUERY '"+select+"'");*/
			
			pstmt = connectionDB.prepareStatement(select);
			int index = 1;
			List<Object> params = new ArrayList<>();
			
			if(leftValue!=null && rightValue!=null) {
				pstmt.setTimestamp(index++, leftValue);
				params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(leftValue));
				pstmt.setTimestamp(index++, rightValue);
				params.add(org.openspcoop2.utils.date.DateUtils.getSimpleDateFormatMs().format(rightValue));
			}
			
			pstmt.setString(index++, transazioneApplicativoServer.getIdTransazione());
			params.add(transazioneApplicativoServer.getIdTransazione());
						
			rs = pstmt.executeQuery();
			int esitoReturn = -1;
			while(rs.next()) {
				
				int found = rs.getInt(esitoColumn);
				
				if(logCore!=null) {
					String comandoSql = DBUtils.formatSQLString(select, params.toArray());
					if(esitoReturn<0) {
						esitoReturn = found;
					}
					else {
						rs.close();
						pstmt.close();
						throw new CoreException("Trovata più di una transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"' ?? (query: "+comandoSql+")");
					}
				}
			}
			
			/**String comandoSql = formatSQLString(updateCommand, params.toArray());
			System.out.println("ROW: "+row+" (app:"+transazioneApplicativoServer.getConnettoreNome()+" id:"+transazioneApplicativoServer.getIdTransazione()+") sql:"+comandoSql);*/
			
			rs.close();
			pstmt.close();
			pstmt = null;
						
			connectionDB.commit(); // anche se si tratta di una read, serve per non far rimanere aperta la transazione
			
			return esitoReturn;
			
		}catch(Throwable e) {
			
			try{
				if(rs != null) {
					rs.close();
					rs = null;
				}
			} catch(Exception er) {
				// ignore
			}
			try{
				if(pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch(Exception er) {
				// ignore
			}
			try{
				connectionDB.rollback();
			} catch(Exception er) {
				// ignore
			}
			
			throw new CoreException(e.getMessage(),e);
		}finally{
			try{
				if(rs != null) {
					rs.close();
					rs = null;
				}
			} catch(Exception er) {
				// ignore
			}
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Throwable t) {
				// ignore
			}
		}
		
	}
	
	public static boolean existsTransaction(String idTransaction, Connection connectionDB,
			String tipoDatabase, Logger logCore, boolean connectionDBCommit) throws CoreException {
	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
		
			if(logCore!=null) {
				// nop
			}
			
			TransazioneFieldConverter transazioneFieldConverter = new TransazioneFieldConverter(tipoDatabase);
			String idTransazioneColumn = transazioneFieldConverter.toColumn(Transazione.model().ID_TRANSAZIONE, false);
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			
			sqlQueryObject.addFromTable(CostantiDB.TRANSAZIONI);
			
			sqlQueryObject.addSelectField(idTransazioneColumn);
			
			sqlQueryObject.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition(idTransazioneColumn+"=?");
						
			String select = sqlQueryObject.createSQLQuery();
			/**System.out.println("QUERY '"+select+"'");*/
			
			pstmt = connectionDB.prepareStatement(select);
			int index = 1;
			List<Object> params = new ArrayList<>();
			
			pstmt.setString(index++, idTransaction);
			params.add(idTransaction);
						
			rs = pstmt.executeQuery();
			boolean esitoReturn = rs.next();

			/**String comandoSql = formatSQLString(updateCommand, params.toArray());
			System.out.println("Esists: "+esitoReturn+" (app:"+transazioneApplicativoServer.getConnettoreNome()+" id:"+transazioneApplicativoServer.getIdTransazione()+") sql:"+comandoSql);*/
			
			rs.close();
			pstmt.close();
			pstmt = null;
						
			if(connectionDBCommit) {
				connectionDB.commit(); // anche se si tratta di una read, serve per non far rimanere aperta la transazione
			}
			
			return esitoReturn;
			
		}catch(Throwable e) {
			
			try{
				if(rs != null) {
					rs.close();
					rs = null;
				}
			} catch(Exception er) {
				// ignore
			}
			try{
				if(pstmt != null) {
					pstmt.close();
					pstmt = null;
				}
			} catch(Exception er) {
				// ignore
			}
			try{
				connectionDB.rollback();
			} catch(Exception er) {
				// ignore
			}
			
			throw new CoreException(e.getMessage(),e);
		}finally{
			try{
				if(rs != null) {
					rs.close();
					rs = null;
				}
			} catch(Exception er) {
				// ignore
			}
			try {
				if(pstmt!=null) {
					pstmt.close();
				}
			}catch(Throwable t) {
				// ignore
			}
		}
		
	}
	
	@Deprecated
	private static void serializableModeAggiornaInformazioneConsegnaTerminataEngine(TransazioneApplicativoServer transazioneApplicativoServer, Connection connectionDB,
			String tipoDatabase, Logger logCore,
			IDAOFactory daoFactory, Logger logFactory, ServiceManagerProperties smpFactory,
			boolean debug,
			int esitoConsegnaMultipla, int esitoConsegnaMultiplaFallita, int esitoConsegnaMultiplaCompletata, int ok,
			boolean consegnaInErroreRilevatoEsitoTransazioneConsegnaInCoda,
			long gestioneSerializableDBAttesaAttiva, int gestioneSerializableDBCheckInterval) throws CoreException {
		
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
			/** già effettuato fuori dal metodo connectionDB.setAutoCommit(false);*/
			JDBCUtilities.setTransactionIsolationSerializable(tipoDatabase, connectionDB);
		} catch(Exception er) {
			throw new CoreException("(setIsolation) "+er.getMessage(),er);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean updateEffettuato = false;
		
		long scadenzaWhile = DateManager.getTimeMillis() + gestioneSerializableDBAttesaAttiva;
		
		CoreException coreException = null;
		while(!updateEffettuato && DateManager.getTimeMillis() < scadenzaWhile){

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
				if(l!=null && !l.isEmpty()) {
					if(l.size()>1) {
						coreException = new CoreException("Trovata più di una transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"'");
					}
					else {
												
						Object oEsito = l.get(0).get(Transazione.model().ESITO.getFieldName());
						int esito = -1;
						if(oEsito instanceof Integer) {
							esito = (Integer) oEsito;
						}
						
						Object oConsegneMultiple = l.get(0).get(Transazione.model().CONSEGNE_MULTIPLE_IN_CORSO.getFieldName());
						int consegneMultiple = -1;
						if(oConsegneMultiple instanceof Integer) {
							consegneMultiple = (Integer) oConsegneMultiple;
						}
												
						String prefix = "Trovata transazione con id '"+transazioneApplicativoServer.getIdTransazione()+"'";
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
								String msg = prefix+", con un esito '"+esito+"' (ConsegnaMultiplaCompletata) già gestita da un altro nodo";
								logCore.debug(msg);
							}
						}
						else {
							coreException = new CoreException(prefix+", con un esito '"+esito+"' non atteso");
						}

						
					}
				}

				transazioneService.disableSelectForUpdate();
				
				// Chiusura Transazione
				connectionDB.commit();

				// ID Costruito
				updateEffettuato = true;

			} catch(Throwable e) {
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {
					// ignore
				}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {
					// ignore
				}
				try{
					connectionDB.rollback();
				} catch(Exception er) {
					// ignore
				}
			}

			if(!updateEffettuato){
				// Per aiutare ad evitare conflitti
				try{
					Utilities.sleep(getRandom().nextInt(gestioneSerializableDBCheckInterval)); // random da 0ms a checkIntervalms
				}catch(Exception eRandom){
					// ignore
				}
			}
		}
		// Ripristino Transazione
		try{
			connectionDB.setTransactionIsolation(oldTransactionIsolation);
			/** già effettuato fuori dal metodo connectionDB.setAutoCommit(true);*/
		} catch(Exception er) {
			throw new CoreException("(ripristinoIsolation) "+er.getMessage(),er);
		}

		if(coreException!=null) {
			throw coreException;
		}
	}
}
