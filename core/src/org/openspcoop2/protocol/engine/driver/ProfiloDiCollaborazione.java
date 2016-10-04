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
import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.engine.LetturaParametriBusta;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.registry.RisultatoValidazione;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * Sono inclusi i metodi per la gestione del profilo di collaborazione.
 * Tutti i metodi hanno bisogno di una connessione ad un DB, precedentemente impostata
 * e passata attraverso l'apposito metodo.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ProfiloDiCollaborazione {
	
	/** Logger utilizzato per debug. */
	private Logger log = null;

	private IState state;

	/** GestoreRepository */
	private IGestoreRepository gestoreRepositoryBuste;
	
	private IProtocolFactory protocolFactory;
	private IProtocolConfiguration protocolConfiguration;
	private IProtocolManager protocolManager;
	private ITraduttore protocolTraduttore;
	
	private void initProtocolFactory(IProtocolFactory protocolFactory) throws ProtocolException{
		this.protocolFactory = protocolFactory;
		this.protocolManager = this.protocolFactory.createProtocolManager();
		this.protocolTraduttore = this.protocolFactory.createTraduttore();
		this.protocolConfiguration = this.protocolFactory.createProtocolConfiguration();
	}

	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Costruttore. 
	 *
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * @throws ProtocolException 
	 * 
	 */
	public ProfiloDiCollaborazione(IState state,IProtocolFactory protocolFactory) throws ProtocolException{
		this(state, Configurazione.getLibraryLog(),protocolFactory);
	}
	/**
	 * Costruttore. 
	 *
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * @throws ProtocolException 
	 * 
	 */
	public ProfiloDiCollaborazione(IState state, Logger alog, IProtocolFactory protocolFactory) throws ProtocolException{
		this.state = state;
		this.gestoreRepositoryBuste = Configurazione.getGestoreRepositoryBuste();
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(ProfiloDiCollaborazione.class.getName());
		}
		if(protocolFactory!=null)
			initProtocolFactory(protocolFactory);
	}



	/* ********  PROFILO GENERICO  ******** */

	/**
	 * Metodo che si occupa di salvare una busta, precedentemente registrata, 
	 * tra le buste usate per la gestione del Profilo di Collaborazione. 
	 * 
	 * @param id identificativo della busta da salvare.
	 * 
	 */
	public void registraBustaInviata(String id) throws ProtocolException{
		registraBusta(id,Costanti.OUTBOX);
	}
	/**
	 * Metodo che si occupa di eliminare una busta, precedentemente registrata, 
	 * tra le buste usate per la gestione del Profilo di Collaborazione. 
	 * 
	 * @param id identificativo della busta da eliminare.
	 * 
	 */
	public void eliminaBustaInviata(String id,boolean forzaEliminazioneDB) throws ProtocolException{
		eliminaBusta(id,Costanti.OUTBOX,forzaEliminazioneDB);
	}
	public void eliminaBustaInviata(String id) throws ProtocolException{
		eliminaBusta(id,Costanti.OUTBOX);
	}

	/**
	 * Metodo che si occupa di salvare una busta, precedentemente registrata, 
	 * tra le buste usate per la gestione del Profilo di Collaborazione. 
	 * 
	 * @param id identificativo della busta da salvare.
	 * 
	 */
	public void registraBustaRicevuta(String id) throws ProtocolException{
		registraBusta(id,Costanti.INBOX);
	}
	/**
	 * Metodo che si occupa di eliminare una busta, precedentemente registrata, 
	 * tra le buste usate per la gestione del Profilo di Collaborazione. 
	 * 
	 * @param id identificativo della busta da eliminare.
	 * 
	 */
	public void eliminaBustaRicevuta(String id,boolean forzaEliminazioneDB) throws ProtocolException{
		eliminaBusta(id,Costanti.INBOX,forzaEliminazioneDB);
	}
	public void eliminaBustaRicevuta(String id) throws ProtocolException{
		eliminaBusta(id,Costanti.INBOX);
	}

	/**
	 * Metodo che si occupa di salvare una busta, precedentemente registrata, 
	 * tra le buste usate per la gestione del Profilo di Collaborazione. 
	 * 
	 * @param id identificativo della busta da salvare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * 
	 */
	public void registraBusta(String id,String tipoBusta) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {
			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();

			PreparedStatement pstmtUpdate = null;
			try{	

				StringBuffer queryUpdate = new StringBuffer();
				queryUpdate.append("UPDATE ");
				queryUpdate.append(Costanti.REPOSITORY);
				queryUpdate.append(" SET ");
				queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_ProfiloCollaborazione(true));
				queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
				pstmtUpdate.setString(1,id);
				pstmtUpdate.setString(2,tipoBusta);

				// Add PreparedStatement
				stateful.getPreparedStatement().put("UPDATE saveBustaForProfilo"+tipoBusta+"_"+id,pstmtUpdate);

			} catch(Exception e) {
				String errorMsg = "PROFILO_DI_COLLABORAZIONE, Errore di registrazione "+tipoBusta+"/"+id+": "+e.getMessage();		
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
		else { 	//NOP
		}
	}
	/**
	 * Metodo che si occupa di eliminare una busta, precedentemente registrata, 
	 * tra le buste usate per la gestione del Profilo di Collaborazione.. 
	 * 
	 * @param id identificativo della busta da salvare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * 
	 */
	public void eliminaBusta(String id,String tipoBusta) throws ProtocolException{
		this.eliminaBusta(id, tipoBusta, false);
	}
	public void eliminaBusta(String id,String tipoBusta, boolean forzaEliminazioneDB) throws ProtocolException{
		if(this.state instanceof StatefulMessage  ||  forzaEliminazioneDB) {
			StateMessage state = (StateMessage)this.state;
			Connection connectionDB = state.getConnectionDB();

			PreparedStatement pstmtUpdate = null;
			try{	

				StringBuffer queryUpdate = new StringBuffer();
				queryUpdate.append("UPDATE ");
				queryUpdate.append(Costanti.REPOSITORY);
				queryUpdate.append(" SET ");
				queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_ProfiloCollaborazione(false));
				queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
				pstmtUpdate.setString(1,id);
				pstmtUpdate.setString(2,tipoBusta);

				// Add PreparedStatement
				state.getPreparedStatement().put("UPDATE eliminaBustaForProfilo"+tipoBusta+"_"+id,pstmtUpdate);

			} catch(Exception e) {
				String errorMsg = "PROFILO_DI_COLLABORAZIONE, Errore di cancellazione "+tipoBusta+"/"+id+": "+e.getMessage();		
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


	/* ********  PROFILO SINCRONO  ******** */

	/**
	 * Controlla se e' stato precedentemente registrata una richiesta sincrona.
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @return true se esiste una entry nella tabella apposita.
	 * 
	 */
	public boolean sincrono_validazioneRiferimentoMessaggio(String riferimentoMessaggio) throws ProtocolException{
		if(this.state instanceof StatefulMessage) {
			StatefulMessage stateful = (StatefulMessage)this.state;
			try{
				RepositoryBuste repositoryBuste = new RepositoryBuste(stateful, this.log, this.protocolFactory);
				if(repositoryBuste.isRegistrataIntoOutBox(riferimentoMessaggio)){
					return true;
				}else{
					return false;
				}
			}catch(Exception e){	
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_SINCRONO, Errore durante la sincrono_validazioneRiferimentoMessaggio "+riferimentoMessaggio+": "+e.getMessage();		
				this.log.error(errorMsg);
				return false;
			}
		}
		else {
			//TODO CHECKME
			return true;
		}
	}

	/**
	 * Valida la collaborazione utilizzata
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @return true se la collaborazione e' valida
	 * 
	 */
	public boolean sincrono_validazioneCollaborazione(String riferimentoMessaggio,String idCollaborazione) throws ProtocolException{
		if(this.state instanceof StatefulMessage) {
			StatefulMessage stateful = (StatefulMessage)this.state;

			try{
				RepositoryBuste repositoryBuste = new RepositoryBuste(stateful, this.log, this.protocolFactory);
				if(repositoryBuste.isRegistrataIntoOutBox(riferimentoMessaggio)){
					return idCollaborazione.equals(repositoryBuste.getCollaborazioneFromOutBox(riferimentoMessaggio));
				}else{
					return false;
				}
			}catch(Exception e){	
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_SINCRONO, Errore durante la sincrono_validazioneRiferimentoMessaggio "+riferimentoMessaggio+": "+e.getMessage();		
				this.log.error(errorMsg);
				return false;
			}
		}
		else{ //TODO CHECKME 
			return true;
		}
	}


	/**
	 * Controlla se e' stato precedentemente registrata una richiesta sincrona.
	 *
	 * @param id Identificativo.
	 * 
	 */
	public void sincrono_eliminaRichiestaInOutBox(String id) throws ProtocolException{
		try{
			this.eliminaBustaInviata(id);
		}catch(ProtocolException e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_SINCRONO, Errore durante la sincrono_eliminaRichiestaInOutBox "+id+": "+e.getMessage();		
			this.log.error(errorMsg);
			throw e;
		}
	}

	/**
	 * Costruisce una {@link org.openspcoop2.protocol.sdk.Busta} inizializzata con 
	 * tutte le informazioni necessarie per costruire la busta di ritorno in cui includere 
	 * il risultato applicativo da spedire alla porta di dominio mittente. 
	 * La costruire dell'apposita risposta comportera' vari aspetti come : rifMsg = id precedente , 
	 * Mitt e Dest scambiati , nuova OraRegistrazione e ID, ecc...
	 * <p>
	 * In caso in cui la busta di ritorno venga costruita con successo, l'entry verra' cancellata dalla tabella.
	 *
	 * @param id ID della busta contenente la richiesta sincrona.
	 * @return {@link org.openspcoop2.protocol.sdk.Busta} inizializzata per la costruzione della risposta sincrona in caso di successo, 
	 *         null altrimenti.
	 * 
	 */
	public Busta sincrono_generaBustaRisposta(String id,TipoOraRegistrazione tipoTempo) throws ProtocolException {

		StateMessage stateMSG = (StateMessage)this.state;
		Busta busta = null;
		try{
			RepositoryBuste repository = new RepositoryBuste(stateMSG, this.log, this.protocolFactory);
			LetturaParametriBusta parametri = new LetturaParametriBusta();
			parametri.setMittente(true);
			parametri.setDestinatario(true);
			parametri.setServizio(true);
			parametri.setAzione(true);
			parametri.setCollaborazione(true);
			parametri.setProfiloTrasmissione(true);
			parametri.setIndirizziTelematici(this.protocolConfiguration.isSupportoIndirizzoRisposta());
			busta = repository.getSomeValuesFromInBox(id, parametri);
		}catch(Exception e){
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_SINCRONO, Errore durante la getBusta dal repository "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}

		// inverto mitt con dest
		String mitt = busta.getMittente();
		String tipoMitt = busta.getTipoMittente();
		String portaMitt = busta.getIdentificativoPortaMittente();
		String indMitt = busta.getIndirizzoMittente();
		busta.setMittente(busta.getDestinatario());
		busta.setTipoMittente(busta.getTipoDestinatario());
		busta.setIdentificativoPortaMittente(busta.getIdentificativoPortaDestinatario());
		busta.setIndirizzoMittente(busta.getIndirizzoDestinatario());
		busta.setDestinatario(mitt);
		busta.setTipoDestinatario(tipoMitt);
		busta.setIdentificativoPortaDestinatario(portaMitt);
		busta.setIndirizzoDestinatario(indMitt);

		// ProfiloDiCollaborazione
		busta.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO);

		// ID
		busta.setRiferimentoMessaggio(id);

		// OraRegistrazione
		busta.setOraRegistrazione(DateManager.getDate());
		busta.setTipoOraRegistrazione(tipoTempo,this.protocolTraduttore.toString(tipoTempo));

		// la busta non serve piu' per il profilo di collaborazione
		this.eliminaBustaRicevuta(busta.getRiferimentoMessaggio());

		return busta;

	} 

	/**
	 * Effettua una validazione della busta di risposta che sia in correlazione con la richiesta.
	 * 
	 * @param bustaDaValidare Busta da controllare
	 * @return una Eccezione se la validazione fallisce, null altrimenti.
	 * 
	 */
	public Eccezione sincrono_validazioneCorrelazione(Busta bustaDaValidare, IProtocolFactory protocolFactory) throws ProtocolException {
		if(this.state instanceof StatefulMessage) {
			StatefulMessage stateful = (StatefulMessage)this.state;	

			Busta bustaRichiesta = null;
			try{
				RepositoryBuste repository = new RepositoryBuste(stateful, this.log, this.protocolFactory);
				LetturaParametriBusta parametri = new LetturaParametriBusta();
				parametri.setMittente(true);
				parametri.setDestinatario(true);
				parametri.setServizio(true);
				parametri.setAzione(true);
				bustaRichiesta = repository.getSomeValuesFromOutBox(bustaDaValidare.getRiferimentoMessaggio(), parametri);
			} catch(Exception e) {
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_SINCRONO, Errore durante la sincrono_validazioneCorrelazione, get dal repository "+bustaDaValidare.getRiferimentoMessaggio()+": "+e.getMessage();		
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}

			// validazione
			if (bustaRichiesta.getTipoDestinatario().equals(bustaDaValidare.getTipoMittente())==false){
				String msgErrore = "Tipo del mittente diverso da quello atteso nella gestione del profilo di collaborazione Sincrono";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_MITTENTE_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
			if (bustaRichiesta.getDestinatario().equals(bustaDaValidare.getMittente())==false){
				String msgErrore = "Mittente diverso da quello atteso nella gestione del profilo di collaborazione Sincrono";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.MITTENTE_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
			if (bustaRichiesta.getTipoMittente().equals(bustaDaValidare.getTipoDestinatario())==false){
				String msgErrore = "Tipo del destinatario diverso da quello atteso nella gestione del profilo di collaborazione Sincrono";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_DESTINATARIO_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
			if (bustaRichiesta.getMittente().equals(bustaDaValidare.getDestinatario())==false){
				String msgErrore = "Destinatario diverso da quello atteso nella gestione del profilo di collaborazione Sincrono";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.DESTINATARIO_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}

			if(bustaDaValidare.getServizio()!=null && bustaDaValidare.getTipoServizio()!=null){
				if (bustaRichiesta.getTipoServizio().equals(bustaDaValidare.getTipoServizio())==false){
					String msgErrore = "Tipo di servizio diverso da quello atteso nella gestione del profilo di collaborazione Sincrono";
					Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_SERVIZIO_NON_VALIDO.
							getErroreCooperazione(msgErrore),
							true,null,protocolFactory);
					this.log.error(msgErrore);
					return eccValidazione;
				}
				if (bustaRichiesta.getServizio().equals(bustaDaValidare.getServizio())==false){
					String msgErrore = "Servizio diverso da quello atteso nella gestione del profilo di collaborazione Sincrono";
					Eccezione eccValidazione = new Eccezione(ErroriCooperazione.SERVIZIO_NON_VALIDO.
							getErroreCooperazione(msgErrore),
							true,null,protocolFactory);
					this.log.error(msgErrore);
					return eccValidazione;
				}
				if(bustaDaValidare.getAzione()!=null){
					if (bustaDaValidare.getAzione().equals(bustaRichiesta.getAzione()) == false){
						String msgErrore = "Azione diversa da quello atteso nella gestione del profilo di collaborazione Sincrono";
						Eccezione eccValidazione = new Eccezione(ErroriCooperazione.AZIONE_NON_VALIDA.
								getErroreCooperazione(msgErrore),
								true,null,protocolFactory);
						this.log.error(msgErrore);
						return eccValidazione;
					}
				}
			}

			return null;
		}

		else{
			//CHECKME
			return null;
		}
	}


	/* ********  PROFILO ASINCRONO SIMMETRICO ******** */

	/**
	 * Registrazione di una richiesta inviata.
	 * Viene mantenuto nella tabella asincrona una entry con ID_MESSAGGIO, OUTBOX, dataInvio(serve per la respedizione) 
	 * e ServizioCorrelato inserito nella richiesta.
	 * 
	 * @param id Identificativo della richiesta
	 * @param tipoServizioCorrelato Tipo del Servizio Correlato
	 * @param servizioCorrelato Servizio Correlato
	 * @param ricevutaApplicativa Indicazione sull'abilitazione di una ricevuta applicativa
	 */
	public void asincronoSimmetrico_registraRichiestaInviata(String id,String idCollaborazione,String tipoServizioCorrelato,
			String servizioCorrelato,boolean ricevutaApplicativa,Integrazione integrazione,
			long scadenzaMessaggi) throws ProtocolException{
		
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();

		PreparedStatement pstmt = null;
		PreparedStatement pstmtUpdate = null;
		try{	

			java.sql.Timestamp oraInvio = DateManager.getTimestamp();

			StringBuffer query = new StringBuffer();
			query.append("INSERT INTO  ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" (ID_MESSAGGIO,TIPO,ORA_REGISTRAZIONE,RICEVUTA_ASINCRONA,TIPO_SERVIZIO_CORRELATO,SERVIZIO_CORRELATO");
			query.append(",IS_RICHIESTA,ID_ASINCRONO,ID_COLLABORAZIONE,RICEVUTA_APPLICATIVA) ");
			query.append(" VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? )");

			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,Costanti.OUTBOX);
			pstmt.setTimestamp(3,oraInvio);
			pstmt.setInt(4,0); // stato ricevuta: attesa
			pstmt.setString(5,tipoServizioCorrelato);
			pstmt.setString(6,servizioCorrelato);
			pstmt.setInt(7,1); // is richiesta
			pstmt.setString(8,id); // idasincrono utilizzato per identificare questa instanza di cooperazione asincrona
			pstmt.setString(9,idCollaborazione); // idcollaborazione
			if(ricevutaApplicativa){
				pstmt.setInt(10,1); // ricevuta applicativa abilitata
			}else{
				pstmt.setInt(10,0); // ricevuta applicativa non abilitata
			}

			//	Add PreparedStatement
			state.getPreparedStatement().put("INSERT save_RichiestaAsincronaSimmetricaInviata"+id,pstmt);

			if(this.state instanceof StatefulMessage) {
				
				// salvo busta per profilo
				this.registraBustaInviata(id);
				
			}else{
				
				StatelessMessage statelessMessage = (StatelessMessage) this.state;
				
				// Registro proprio la busta
				RepositoryBuste repository = new RepositoryBuste(this.state, this.log, true, this.protocolFactory);
				long scadenza = scadenzaMessaggi;
				if(scadenza<=0){
					scadenza = this.protocolManager.getIntervalloScadenzaBuste();
				}
				repository.registraBustaIntoOutBox(statelessMessage.getBusta(),scadenza);
				repository.aggiornaInfoIntegrazioneIntoOutBox(id,integrazione);
				this.registraBustaInviata(id);
				
				// salvo busta per profilo
				StringBuffer queryUpdate = new StringBuffer();
				queryUpdate.append("UPDATE ");
				queryUpdate.append(Costanti.REPOSITORY);
				queryUpdate.append(" SET ");
				queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_ProfiloCollaborazione(true));
				queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
				pstmtUpdate.setString(1,id);
				pstmtUpdate.setString(2,Costanti.OUTBOX);

				// Add PreparedStatement
				state.getPreparedStatement().put("UPDATE saveBustaForProfilo"+Costanti.OUTBOX+"_"+id,pstmtUpdate);
			}

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, Errore di registrazione richiesta inviata "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
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
	 * Controlla se e' stato precedentemente registrata una richiesta asincrona simmetrica,
	 * ritornando eventuali dati dell'integrazione per la consegna della risposta.
	 *
	 * @param riferimentoMessaggio RiferimentoMessaggio
	 * @return Dati di integrazione per la consegna della risposta
	 * 
	 */
	public Integrazione asincronoSimmetrico_getDatiConsegnaRisposta(String riferimentoMessaggio) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		try{	
			// I dati asincroni saranno eliminati quando scadra' la busta asincrona
			History history = new History(state,this.log);
			history.registraBustaInviata(riferimentoMessaggio);

			// Prendo i dati di integrazione
			RepositoryBuste repositoryBuste = new RepositoryBuste(state,  this.log, this.protocolFactory);
			Integrazione integrazione = repositoryBuste.getInfoIntegrazioneFromOutBox(riferimentoMessaggio);
			if(integrazione==null)
				throw new Exception("Dati di integrazione non trovati");
			else
				return integrazione;

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, Errore durante la asincronoSimmetrico_getDatiConsegnaRisposta "+riferimentoMessaggio+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}
	}

	/**
	 * Controlla se e' stato precedentemente registrata una richiesta asincrona simmetrica,
	 * ritornando eventuali dati dell'integrazione per la consegna della ricevuta.
	 *
	 * @return Dati di integrazione per la consegna della risposta
	 * 
	 */
	public Integrazione asincronoSimmetrico_getDatiConsegnaRicevuta(String riferimentoMessaggio) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		try{	
			// Prendo i dati di integrazione
			RepositoryBuste repositoryBuste = new RepositoryBuste(state,  this.log, this.protocolFactory);
			Integrazione integrazione = repositoryBuste.getInfoIntegrazioneFromOutBox(riferimentoMessaggio);
			if(integrazione==null)
				throw new Exception("Dati di integrazione non trovati");
			else
				return integrazione;

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, Errore durante la asincronoSimmetrico_getDatiConsegnaRicevuta "+riferimentoMessaggio+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}

	}

	/**
	 * Registrazione di una richiesta ricevuta.
	 * Viene mantenuto nella tabella asincrona una entry con ID_MESSAGGIO, INBOX 
	 * e ServizioCorrelato inserito nella richiesta.
	 * 
	 * @param id Identificativo della richiesta
	 * @param tipoServizioCorrelato Tipo del Servizio Correlato
	 * @param servizioCorrelato Servizio Correlato
	 * @param ricevutaApplicativa Indicazione sull'abilitazione di una ricevuta applicativa
	 */
	public void asincronoSimmetrico_registraRichiestaRicevuta(String id,String idCollaborazione, 
			String tipoServizioCorrelato,String servizioCorrelato,
			boolean ricevutaApplicativa,long scadenzaMessaggi)throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		PreparedStatement pstmtUpdate = null;
		ResultSet rs = null;
		try{	

			java.sql.Timestamp oraRicezione = DateManager.getTimestamp();

			StringBuffer querySearch = new StringBuffer();
			querySearch.append("SELECT ID_MESSAGGIO FROM ");
			querySearch.append(Costanti.PROFILO_ASINCRONO);
			querySearch.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");
			pstmt = connectionDB.prepareStatement(querySearch.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,Costanti.INBOX);
			rs = pstmt.executeQuery();
			boolean exists = rs.next();
			rs.close();
			pstmt.close();

			StringBuffer query = new StringBuffer();
			if(exists){
				query.append("UPDATE ");
				query.append(Costanti.PROFILO_ASINCRONO);
				query.append(" SET ID_MESSAGGIO=?,TIPO=?,ORA_REGISTRAZIONE=?,RICEVUTA_ASINCRONA=?,TIPO_SERVIZIO_CORRELATO=?,SERVIZIO_CORRELATO=?," +
				"IS_RICHIESTA=?,ID_ASINCRONO=?,ID_COLLABORAZIONE=?,RICEVUTA_APPLICATIVA=? WHERE ID_MESSAGGIO=? AND TIPO=?");
			}else{
				query.append("INSERT INTO  ");
				query.append(Costanti.PROFILO_ASINCRONO);
				query.append(" (ID_MESSAGGIO,TIPO,ORA_REGISTRAZIONE,RICEVUTA_ASINCRONA,TIPO_SERVIZIO_CORRELATO,SERVIZIO_CORRELATO");
				query.append(",IS_RICHIESTA,ID_ASINCRONO,ID_COLLABORAZIONE,RICEVUTA_APPLICATIVA) ");
				query.append(" VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? )");
			}


			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,Costanti.INBOX);
			pstmt.setTimestamp(3,oraRicezione);
			pstmt.setInt(4,1); // stato ricevuta: non attesa
			pstmt.setString(5,tipoServizioCorrelato);
			pstmt.setString(6,servizioCorrelato);
			pstmt.setInt(7,1); // is richiesta
			pstmt.setString(8,id); // idasincrono utilizzato per identificare questa instanza di cooperazione asincrona
			pstmt.setString(9,idCollaborazione); // idCollaborazione
			if(ricevutaApplicativa){
				pstmt.setInt(10,1); // ricevuta applicativa abilitata
			}else{
				pstmt.setInt(10,0); // ricevuta applicativa non abilitata
			}
			if(exists){
				pstmt.setString(11,id);
				pstmt.setString(12,Costanti.INBOX);
			}

			//	Add PreparedStatement
			if(exists){
				state.getPreparedStatement().put("UPDATE save_RichiestaAsincronaSimmetricaRicevuta"+id,pstmt);
			}else{
				state.getPreparedStatement().put("INSERT save_RichiestaAsincronaSimmetricaRicevuta"+id,pstmt);
			}

			
			if(this.state instanceof StatefulMessage) {
				
				// salvo busta per profilo
				this.registraBustaRicevuta(id);
				
			}else{
				
				StatelessMessage statelessMessage = (StatelessMessage) this.state;
				
				long scadenza = scadenzaMessaggi;
				if(scadenza<=0){
					scadenza = this.protocolManager.getIntervalloScadenzaBuste();
				}
				
				// Registro proprio la busta
				RepositoryBuste repository = new RepositoryBuste(this.state,  this.log, true, this.protocolFactory);
				String key = "INSERT RegistrazioneBustaForHistory" + Costanti.INBOX + "_" + statelessMessage.getBusta().getID();
				if(repository.isRegistrataIntoInBox(statelessMessage.getBusta().getID())){
					repository.aggiornaBustaIntoInBox(statelessMessage.getBusta(),
							scadenza);
				}else if(state.getPreparedStatement().containsKey(key)){
					repository.aggiornaBustaIntoInBox(statelessMessage.getBusta(),
							scadenza);
				}else{
					repository.registraBustaIntoInBox(statelessMessage.getBusta(),
							scadenza);
				}
				this.registraBustaRicevuta(id);
				
				
				// salvo busta per profilo
				StringBuffer queryUpdate = new StringBuffer();
				queryUpdate.append("UPDATE ");
				queryUpdate.append(Costanti.REPOSITORY);
				queryUpdate.append(" SET ");
				queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_ProfiloCollaborazione(true));
				queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
				pstmtUpdate.setString(1,id);
				pstmtUpdate.setString(2,Costanti.INBOX);

				// Add PreparedStatement
				state.getPreparedStatement().put("UPDATE saveBustaForProfilo" + Costanti.INBOX + "_" + id,pstmtUpdate);
			}
			
			

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, Errore di registrazione richiesta ricevuta " + id + ": " + e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
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
	 * Controlla se e' stato precedentemente registrata una richiesta asincrona simmetrica,
	 * ritornando eventuale busta da usare con la risposta applicativa.
	 *
	 * @param id Identificativo della richiesta.
	 * @param idRisposta Identificativo della risposta.
	 * @return Dati di integrazione per la consegna della risposta
	 * 
	 */
	public Busta asincronoSimmetrico_getBustaRisposta(String id, String idRisposta, long scadenzaMessaggi) throws ProtocolException{
		
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		// getBustaRicevuta
		Busta busta = null;
		try{
			RepositoryBuste repository = new RepositoryBuste(state,  this.log, this.protocolFactory);
			// exists: controlla anche il marcatore logico, per vedere che la busta cmq non sia eliminata
			if(repository.exists(id, Costanti.INBOX)){
				LetturaParametriBusta parametri = new LetturaParametriBusta();
				parametri.setMittente(true);
				parametri.setDestinatario(true);
				parametri.setServizioCorrelato(true);
				busta = repository.getSomeValuesFromInBox(id, parametri ,true); // Ultimo parametro forza la lettura su database
			}else{
				throw new Exception("Busta non trovata");
			}
		}catch(Exception e){
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, Errore durante la getBusta dal repository "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}

		// ProfiloDiCollaborazione
		busta.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);

		// check eventuali spedizione-risposta precedentemente effettuate
		PreparedStatement pstmtCheck = null;
		ResultSet rsCheck = null;
		String oldIDRisposta = null;
		String oldTipoRisposta = null;
		try{
			StringBuffer query = new StringBuffer();
			query.append("SELECT ID_MESSAGGIO,TIPO FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE BACKUP_ID_RICHIESTA=?");

			pstmtCheck = connectionDB.prepareStatement(query.toString());
			pstmtCheck.setString(1,id);
			rsCheck = pstmtCheck.executeQuery();		
			if(rsCheck == null) {
				throw new ProtocolException("RS Check Null?");			
			}
			if(rsCheck.next()){
				oldIDRisposta = rsCheck.getString("ID_MESSAGGIO");
				oldTipoRisposta = rsCheck.getString("TIPO");
			}		
			rsCheck.close();
			pstmtCheck.close();

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, asincronoSimmetrico_getBustaRisposta "+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( rsCheck != null )
					rsCheck.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if( pstmtCheck != null )
					pstmtCheck.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

		// Imposto parametri per la consegna della risposta
		PreparedStatement pstmt = null;
		try{	

			java.sql.Timestamp oraInvioRisposta = DateManager.getTimestamp();

			StringBuffer query = new StringBuffer();
			query.append("UPDATE ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" SET IS_RICHIESTA=0, RICEVUTA_ASINCRONA=0, ORA_REGISTRAZIONE=?, ID_MESSAGGIO=?, TIPO=?, BACKUP_ID_RICHIESTA=? WHERE  ID_MESSAGGIO = ? AND TIPO=?");

			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setTimestamp(1,oraInvioRisposta);
			pstmt.setString(2,idRisposta);
			pstmt.setString(3,Costanti.OUTBOX);
			pstmt.setString(4,id);
			if(oldIDRisposta!=null && oldTipoRisposta!=null){
				pstmt.setString(5,oldIDRisposta);
				pstmt.setString(6,oldTipoRisposta);
			}else{
				pstmt.setString(5,id);
				pstmt.setString(6,Costanti.INBOX);
			}

			//	Add PreparedStatement
			state.getPreparedStatement().put("UPDATE save_RispostaAsincronaSimmetricaInviata"+id,pstmt);

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, Errore di registrazione risposta inviata "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

		// Imposto tipo e nome servizio correlato
		if(busta.getTipoServizioCorrelato()==null || busta.getServizioCorrelato()==null){
			PreparedStatement pstmtServizioCorrelato = null;
			ResultSet rsServizioCorrelato = null;
			String tipoSC = null;
			String nomeSC = null;
			try{
				StringBuffer query = new StringBuffer();
				query.append("SELECT TIPO_SERVIZIO_CORRELATO,SERVIZIO_CORRELATO FROM ");
				query.append(Costanti.PROFILO_ASINCRONO);
				query.append(" WHERE ID_MESSAGGIO = ? AND TIPO=?");

				pstmtServizioCorrelato = connectionDB.prepareStatement(query.toString());
				if(oldIDRisposta!=null && oldTipoRisposta!=null){
					pstmtServizioCorrelato.setString(1,oldIDRisposta);
					pstmtServizioCorrelato.setString(2,oldTipoRisposta);
				}else{
					pstmtServizioCorrelato.setString(1,id);
					pstmtServizioCorrelato.setString(2,Costanti.INBOX);
				}
				rsServizioCorrelato = pstmtServizioCorrelato.executeQuery();		
				if(rsServizioCorrelato == null) {
					throw new ProtocolException("RS Check Null?");			
				}
				if(rsServizioCorrelato.next()){
					tipoSC = rsServizioCorrelato.getString("TIPO_SERVIZIO_CORRELATO");
					nomeSC = rsServizioCorrelato.getString("SERVIZIO_CORRELATO");
				}		
				rsServizioCorrelato.close();
				pstmtServizioCorrelato.close();

				if(tipoSC==null || nomeSC==null)
					throw new Exception("Tipo/Nome servizio correlato non trovato");

			}catch(Exception e){	
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, asincronoSimmetrico_getBustaRisposta "+id+": "+e.getMessage();
				this.log.error(errorMsg,e);
				try{
					if( rsServizioCorrelato != null )
						rsServizioCorrelato.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				try{
					if( pstmtServizioCorrelato != null )
						pstmtServizioCorrelato.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				throw new ProtocolException(errorMsg,e);
			}
			busta.setTipoServizioCorrelato(tipoSC);
			busta.setServizioCorrelato(nomeSC);
		}

		if(this.state instanceof StatefulMessage) {
		
			// Registro la busta con il nuovo id
			this.registraBustaInviata(idRisposta);
			
		}else{
		
			PreparedStatement pstmtUpdate = null;
			try{
			
				StatelessMessage statelessMessage = (StatelessMessage) this.state;
				
				// Registro proprio la busta
				RepositoryBuste repository = new RepositoryBuste(this.state,  this.log, true, this.protocolFactory);
				long scadenza = scadenzaMessaggi;
				if(scadenza<=0){
					scadenza = this.protocolManager.getIntervalloScadenzaBuste();
				}
				repository.registraBustaIntoOutBox(statelessMessage.getBusta(),scadenza);
				this.registraBustaInviata(idRisposta);
				
				// salvo busta per profilo
				StringBuffer queryUpdate = new StringBuffer();
				queryUpdate.append("UPDATE ");
				queryUpdate.append(Costanti.REPOSITORY);
				queryUpdate.append(" SET ");
				queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_ProfiloCollaborazione(true));
				queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
				pstmtUpdate.setString(1,idRisposta);
				pstmtUpdate.setString(2,Costanti.OUTBOX);
	
				// Add PreparedStatement
				state.getPreparedStatement().put("UPDATE saveBustaForProfilo"+Costanti.OUTBOX+"_"+idRisposta,pstmtUpdate);
			
			} catch(Exception e) {
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, Errore di aggiornamento busta di risposta "+idRisposta+": "+e.getMessage();		
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

		//	delete vecchia risposta
		if(oldIDRisposta!=null && oldTipoRisposta!=null){
			this.eliminaBusta(oldIDRisposta,oldTipoRisposta,true);
		}

		return busta;
	}

	/**
	 * Controlla se e' stato precedentemente registrata una richiesta asincrona simmetrica,
	 * ritornando eventuale busta da usare con la ricevuta applicativa.
	 *
	 * @param id Identificativo della richiesta asincrona.
	 * @param rifMsgRicevuta Riferimento Messaggio da applicare alla ricevuta.
	 * @param isRichiesta Indicazione se si desidera la busta per una ricevuta ad una richiesta o risposta
	 * @return Busta da utilizzare per la ricevuta
	 * 
	 */
	public Busta asincronoSimmetrico_getBustaRicevuta(String id,String rifMsgRicevuta,boolean isRichiesta,TipoOraRegistrazione tipoTempo) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		Busta busta = null;
		try{
			RepositoryBuste repository = new RepositoryBuste(state,  this.log, this.protocolFactory);
			LetturaParametriBusta parametri = new LetturaParametriBusta();
			parametri.setMittente(true);
			parametri.setDestinatario(true);
			parametri.setServizio(true);
			parametri.setServizioCorrelato(true);
			parametri.setAzione(true);
			parametri.setCollaborazione(true);
			parametri.setProfiloTrasmissione(true);
			parametri.setIndirizziTelematici(this.protocolConfiguration.isSupportoIndirizzoRisposta());

			if(isRichiesta){
				busta = repository.getSomeValuesFromInBox(id, parametri,true); // devo leggere i valori dal database
			}else{
				busta = repository.getSomeValuesFromOutBox(id, parametri,true); // devo leggere i valori dal database

				// elimino busta per profilo
				this.eliminaBustaInviata(id,true);
			}

			// costruzione busta di risposta 

			// ProfiloDiCollaborazione
			busta.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);

			// inverto mitt con dest solo se e' una ricevuta alla richiesta
			if(isRichiesta){
				String mitt = busta.getMittente();
				String tipoMitt = busta.getTipoMittente();
				String portaMitt = busta.getIdentificativoPortaMittente();
				String indMitt = busta.getIndirizzoMittente();
				busta.setMittente(busta.getDestinatario());
				busta.setTipoMittente(busta.getTipoDestinatario());
				busta.setIdentificativoPortaMittente(busta.getIdentificativoPortaDestinatario());
				busta.setIndirizzoMittente(busta.getIndirizzoDestinatario());
				busta.setDestinatario(mitt);
				busta.setTipoDestinatario(tipoMitt);
				busta.setIdentificativoPortaDestinatario(portaMitt);
				busta.setIndirizzoDestinatario(indMitt);
			}

			if(isRichiesta == false){

				String tipoServizioCorrelato = busta.getTipoServizioCorrelato();
				String servizioCorrelato = busta.getServizioCorrelato();

				if(tipoServizioCorrelato==null || servizioCorrelato==null){
					PreparedStatement pstmtServizioCorrelato = null;
					ResultSet rsServizioCorrelato = null;
					try{
						StringBuffer query = new StringBuffer();
						query.append("SELECT TIPO_SERVIZIO_CORRELATO,SERVIZIO_CORRELATO FROM ");
						query.append(Costanti.PROFILO_ASINCRONO);
						query.append(" WHERE ID_MESSAGGIO = ? AND TIPO=?");
						pstmtServizioCorrelato = connectionDB.prepareStatement(query.toString());
						pstmtServizioCorrelato.setString(1,id);
						pstmtServizioCorrelato.setString(2,Costanti.OUTBOX);
						rsServizioCorrelato = pstmtServizioCorrelato.executeQuery();		
						if(rsServizioCorrelato == null) {
							throw new ProtocolException("RS Check Null?");			
						}
						if(rsServizioCorrelato.next()){
							tipoServizioCorrelato = rsServizioCorrelato.getString("TIPO_SERVIZIO_CORRELATO");
							servizioCorrelato = rsServizioCorrelato.getString("SERVIZIO_CORRELATO");
						}		
						rsServizioCorrelato.close();
						pstmtServizioCorrelato.close();
						if(tipoServizioCorrelato==null || servizioCorrelato==null)
							throw new Exception("Tipo/Nome servizio correlato non trovato");

					}catch(Exception e){
						throw new Exception("Lettura Servizio correlato non riuscita "+e.getMessage(),e );
					}
				}

				busta.setTipoServizio(tipoServizioCorrelato);
				busta.setServizio(servizioCorrelato);
			}

			busta.setRiferimentoMessaggio(rifMsgRicevuta);
			busta.setOraRegistrazione(DateManager.getDate());
			busta.setTipoOraRegistrazione(tipoTempo,this.protocolTraduttore.toString(tipoTempo));

			busta.setTipoServizioCorrelato(null);
			busta.setServizioCorrelato(null);

		}catch(Exception e){
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, Errore durante la asincronoSimmetrico_getBustaRicevuta dal repository "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}
		return busta;
	}

	/**
	 * Validazione della busta, che sia coerente con la precedente richiesta asincrona simmetrica.
	 *
	 * @param bustaDaValidare Busta da controllare
	 * @return una Eccezione se la validazione fallisce, null altrimenti.
	 * 
	 */
	public Eccezione asincronoSimmetrico_validazioneCorrelazione_ricevutaRichiesta(Busta bustaDaValidare, IProtocolFactory protocolFactory) throws ProtocolException{
		return asincronoSimmetrico_validazioneCorrelazione(bustaDaValidare,true,false,false,protocolFactory);
	}
	/**
	 * Validazione della busta, che sia coerente con la precedente richiesta asincrona simmetrica.
	 *
	 * @param bustaDaValidare Busta da controllare
	 * @return una Eccezione se la validazione fallisce, null altrimenti.
	 * 
	 */
	public Eccezione asincronoSimmetrico_validazioneCorrelazione_risposta(Busta bustaDaValidare, IProtocolFactory protocolFactory) throws ProtocolException{
		return asincronoSimmetrico_validazioneCorrelazione(bustaDaValidare,false,true,false,protocolFactory);
	}
	/**
	 * Validazione della busta, che sia coerente con la precedente richiesta asincrona simmetrica.
	 *
	 * @param bustaDaValidare Busta da controllare
	 * @return una Eccezione se la validazione fallisce, null altrimenti.
	 * 
	 */
	public Eccezione asincronoSimmetrico_validazioneCorrelazione_ricevutaRisposta(Busta bustaDaValidare, IProtocolFactory protocolFactory) throws ProtocolException{
		return asincronoSimmetrico_validazioneCorrelazione(bustaDaValidare,false,false,true,protocolFactory);
	}
	/**
	 * Validazione della busta, che sia coerente con la precedente richiesta asincrona simmetrica.
	 *
	 * @param bustaDaValidare Busta da controllare
	 * @param isRicevutaRichiesta Ricevuta della Richiesta
	 * @param isRisposta Risposta
	 * @param isRicevutaRisposta Ricevuta della Risposta
	 * @return una Eccezione se la validazione fallisce, null altrimenti.
	 * 
	 */
	private Eccezione asincronoSimmetrico_validazioneCorrelazione(Busta bustaDaValidare,boolean isRicevutaRichiesta,boolean isRisposta,boolean isRicevutaRisposta, IProtocolFactory protocolFactory) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		Busta bustaRichiesta = null;
		try{
			RepositoryBuste repository = new RepositoryBuste(state,  this.log, this.protocolFactory);
			LetturaParametriBusta parametri = new LetturaParametriBusta();
			parametri.setMittente(true);
			parametri.setDestinatario(true);
			parametri.setServizio(true);
			parametri.setAzione(true);

			// NOTA:
			//   una busta asincrona RISPOSTA puo avere riferimentoMessaggio o collaborazione
			if(isRisposta && bustaDaValidare.getRiferimentoMessaggio()==null)
				bustaRichiesta = repository.getSomeValuesFromOutBox(bustaDaValidare.getCollaborazione(), parametri, true); // deve leggere sempre da database
			else
				bustaRichiesta = repository.getSomeValuesFromOutBox(bustaDaValidare.getRiferimentoMessaggio(), parametri, true); // deve leggere sempre da database

		}catch(Exception e){
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, Errore durante la validazioneCorrelazione, get dal repository "+bustaDaValidare.getRiferimentoMessaggio()+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}

		// Validazione Servizio (Tipo e Nome)
		// Obbligatoria per una risposta, opzionale per una ricevuta
		if( isRicevutaRichiesta ){
			if(bustaDaValidare.getServizio()!=null && bustaDaValidare.getTipoServizio()!=null){
				if (bustaRichiesta.getTipoServizio().equals(bustaDaValidare.getTipoServizio())==false){
					String msgErrore = "Tipo del servizio diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Simmetrico";
					Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_SERVIZIO_NON_VALIDO.
							getErroreCooperazione(msgErrore),
							true,null,protocolFactory);
					this.log.error(msgErrore);
					return eccValidazione;
				}
				if (bustaRichiesta.getServizio().equals(bustaDaValidare.getServizio())==false){
					String msgErrore = "Servizio diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Simmetrico";
					Eccezione eccValidazione = new Eccezione(ErroriCooperazione.SERVIZIO_NON_VALIDO.
							getErroreCooperazione(msgErrore),
							true,null,protocolFactory);
					this.log.error(msgErrore);
					return eccValidazione;
				}
			}
		}
		else if(  (isRisposta) || (isRicevutaRisposta && bustaDaValidare.getServizio()!=null && bustaDaValidare.getTipoServizio()!=null) ) {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{
				StringBuffer query = new StringBuffer();
				query.append("SELECT TIPO_SERVIZIO_CORRELATO,SERVIZIO_CORRELATO FROM ");
				query.append(Costanti.PROFILO_ASINCRONO);
				query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

				pstmt = connectionDB.prepareStatement(query.toString());

				// NOTA:
				//   una busta asincrona RISPOSTA puo avere riferimentoMessaggio o collaborazione
				if(isRisposta && bustaDaValidare.getRiferimentoMessaggio()==null)
					pstmt.setString(1,bustaDaValidare.getCollaborazione());
				else
					pstmt.setString(1,bustaDaValidare.getRiferimentoMessaggio());

				pstmt.setString(2,Costanti.OUTBOX);
				rs = pstmt.executeQuery();		
				if(rs == null) {
					throw new ProtocolException("RS Null?");			
				}
				String tipoServizioCorrelatoRegistrato = null;
				String servizioCorrelatoRegistrato = null;
				if(rs.next()){
					tipoServizioCorrelatoRegistrato = rs.getString("TIPO_SERVIZIO_CORRELATO");
					servizioCorrelatoRegistrato = rs.getString("SERVIZIO_CORRELATO");
				}

				rs.close();
				pstmt.close();

				if (bustaDaValidare.getTipoServizio().equals(tipoServizioCorrelatoRegistrato) == false){
					String msgErrore = "Tipo del servizio diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Simmetrico";
					Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_SERVIZIO_NON_VALIDO.
							getErroreCooperazione(msgErrore),
							true,null,protocolFactory);
					this.log.error(msgErrore);
					return eccValidazione;
				}
				if (bustaDaValidare.getServizio().equals(servizioCorrelatoRegistrato) == false){
					String msgErrore = "Servizio diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Simmetrico";
					Eccezione eccValidazione = new Eccezione(ErroriCooperazione.SERVIZIO_NON_VALIDO.
							getErroreCooperazione(msgErrore),
							true,null,protocolFactory);
					this.log.error(msgErrore);
					return eccValidazione;
				}

			}catch(Exception e){	
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, validazioneCorrelazioneRichiesta RifMsg("+bustaDaValidare.getRiferimentoMessaggio()+") Collab("+bustaDaValidare.getCollaborazione()+"): "+e.getMessage();
				this.log.error(errorMsg);
				try{
					if( rs != null )
						rs.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				try{
					if( pstmt != null )
						pstmt.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				throw new ProtocolException(errorMsg);
			}
		}

		// Validazione altri parametri busta
		// L'azione di una ricevuta deve essere la stessa di quella della richiesta/risposta
		if(bustaDaValidare.getAzione()!=null && isRisposta==false){
			if (bustaDaValidare.getAzione().equals(bustaRichiesta.getAzione()) == false){
				String msgErrore = "Azione diverso da quello attesa nella gestione del profilo di collaborazione Asincrono Simmetrico";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.AZIONE_NON_VALIDA.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
		}
		if (bustaRichiesta.getTipoDestinatario().equals(bustaDaValidare.getTipoMittente()) == false){
			String msgErrore = "Tipo del mittente diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Simmetrico";
			Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_MITTENTE_NON_VALIDO.
					getErroreCooperazione(msgErrore),
					true,null,protocolFactory);
			this.log.error(msgErrore);
			return eccValidazione;
		}
		if (bustaRichiesta.getDestinatario().equals(bustaDaValidare.getMittente()) == false){
			String msgErrore = "Mittente diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Simmetrico";
			Eccezione eccValidazione = new Eccezione(ErroriCooperazione.MITTENTE_NON_VALIDO.
					getErroreCooperazione(msgErrore),
					true,null,protocolFactory);
			this.log.error(msgErrore);
			return eccValidazione;
		}
		if (bustaRichiesta.getTipoMittente().equals(bustaDaValidare.getTipoDestinatario()) == false){
			String msgErrore = "Tipo del destinatario diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Simmetrico";
			Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_DESTINATARIO_NON_VALIDO.
					getErroreCooperazione(msgErrore),
					true,null,protocolFactory);
			this.log.error(msgErrore);
			return eccValidazione;
		}
		if (bustaRichiesta.getMittente().equals(bustaDaValidare.getDestinatario()) == false){
			String msgErrore = "Destinatario diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Simmetrico";
			Eccezione eccValidazione = new Eccezione(ErroriCooperazione.DESTINATARIO_NON_VALIDO.
					getErroreCooperazione(msgErrore),
					true,null,protocolFactory);
			this.log.error(msgErrore);
			return eccValidazione;
		}

		return null;
	}

	/**
	 * Validazione del riferimentoMessaggio di una busta asincrona simmetrica.
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @return true se esiste una entry nella tabella apposita.
	 * 
	 */
	public boolean asincronoSimmetrico_validazioneRiferimentoMessaggio(String riferimentoMessaggio) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			StringBuffer query = new StringBuffer();
			query.append("SELECT ID_MESSAGGIO FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,riferimentoMessaggio);
			pstmt.setString(2,Costanti.OUTBOX);
			rs = pstmt.executeQuery();		
			if(rs == null) {
				throw new ProtocolException("RS Null?");			
			}
			boolean value = rs.next();

			rs.close();
			pstmt.close();

			RepositoryBuste repositoryBuste = new RepositoryBuste(state,  this.log, this.protocolFactory);
			return value && repositoryBuste.isRegistrataIntoOutBox(riferimentoMessaggio);

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, validazioneRiferimentoMessaggio "+riferimentoMessaggio+": "+e.getMessage();
			this.log.error(errorMsg);
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
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
	 * Validazione della collaborazione di una busta asincrona.
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @param idCollaborazione Collaborazione
	 * @return true se la collaborazione e' valida
	 * 
	 */
	public boolean asincronoSimmetrico_validazioneCollaborazione_risposta(String riferimentoMessaggio,String idCollaborazione) throws ProtocolException{
		return asincronoSimmetrico_validazioneCollaborazione(riferimentoMessaggio,idCollaborazione,true);
	}

	/**
	 * Validazione della collaborazione di una busta asincrona.
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @param idCollaborazione Collaborazione
	 * @return true se la collaborazione e' valida
	 * 
	 */
	public boolean asincronoSimmetrico_validazioneCollaborazione_ricevuta(String riferimentoMessaggio,String idCollaborazione) throws ProtocolException{
		return asincronoSimmetrico_validazioneCollaborazione(riferimentoMessaggio,idCollaborazione,false);
	}

	/**
	 * Validazione della collaborazione di una busta asincrona.
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @param idCollaborazione Collaborazione
	 * @return true se la collaborazione e' valida
	 * 
	 */
	private boolean asincronoSimmetrico_validazioneCollaborazione(String riferimentoMessaggio,String idCollaborazione,boolean isRisposta) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			StringBuffer query = new StringBuffer();
			query.append("SELECT ID_COLLABORAZIONE FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

			pstmt = connectionDB.prepareStatement(query.toString());

			// NOTA:
			//   una busta asincrona RISPOSTA puo avere riferimentoMessaggio o collaborazione
			if(isRisposta && riferimentoMessaggio==null)
				pstmt.setString(1,idCollaborazione);
			else
				pstmt.setString(1,riferimentoMessaggio);
			pstmt.setString(2,Costanti.OUTBOX);
			rs = pstmt.executeQuery();		
			if(rs == null) {
				throw new ProtocolException("RS Null?");			
			}
			String value = null;
			if(rs.next()){
				value = rs.getString("ID_COLLABORAZIONE");
			}

			rs.close();
			pstmt.close();

			return idCollaborazione.equals(value);

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOSIMMETRICO, validazioneCollaborazione "+riferimentoMessaggio+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}
	}














	/* ********  PROFILO ASINCRONO ASIMMETRICO ******** */

	/**
	 * Registrazione di una richiesta inviata.
	 * Viene mantenuto nella tabella asincrona una entry con ID_MESSAGGIO, OUTBOX, dataInvio(serve per la respedizione) 
	 * 
	 * @param id Identificativo della richiesta
	 * @param ricevutaApplicativa Indicazione sull'abilitazione di una ricevuta applicativa
	 */
	public void asincronoAsimmetrico_registraRichiestaInviata(String id,String idCollaborazione,
			boolean ricevutaApplicativa,long scadenzaMessaggi)throws ProtocolException{
		
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		PreparedStatement pstmtUpdate = null;
		try{	

			java.sql.Timestamp oraInvio = DateManager.getTimestamp();

			StringBuffer query = new StringBuffer();
			query.append("INSERT INTO  ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" (ID_MESSAGGIO,TIPO,ORA_REGISTRAZIONE,RICEVUTA_ASINCRONA,TIPO_SERVIZIO_CORRELATO,SERVIZIO_CORRELATO");
			query.append(",IS_RICHIESTA,ID_ASINCRONO,ID_COLLABORAZIONE,RICEVUTA_APPLICATIVA) ");
			query.append(" VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ?)");


			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,Costanti.OUTBOX);
			pstmt.setTimestamp(3,oraInvio);
			pstmt.setInt(4,0); // stato ricevuta: attesa
			pstmt.setString(5,null); // servizioCorrelato non conosciuto a priori
			pstmt.setString(6,null); // servizioCorrelato non conosciuto a priori
			pstmt.setInt(7,1); // is richiesta
			pstmt.setString(8,id); // idasincrono utilizzato per identificare questa instanza di cooperazione asincrona
			pstmt.setString(9,idCollaborazione); // idcollaborazione
			if(ricevutaApplicativa){
				pstmt.setInt(10,1); // ricevuta applicativa abilitata
			}else{
				pstmt.setInt(10,0); // ricevuta applicativa non abilitata
			}

			//	Add PreparedStatement
			state.getPreparedStatement().put("INSERT save_RichiestaAsincronaAsimmetricaInviata"+id,pstmt);

			
			if(this.state instanceof StatefulMessage) {
				
				// salvo busta per profilo
				this.registraBustaInviata(id);
				
			}else{
				
				StatelessMessage statelessMessage = (StatelessMessage) this.state;
				
				// Registro proprio la busta
				RepositoryBuste repository = new RepositoryBuste(this.state,  this.log, true, this.protocolFactory);
				long scadenza = scadenzaMessaggi;
				if(scadenza<=0){
					scadenza = this.protocolManager.getIntervalloScadenzaBuste();
				}
				repository.registraBustaIntoOutBox(statelessMessage.getBusta(),scadenza);
				this.registraBustaInviata(id);
				
				// salvo busta per profilo
				StringBuffer queryUpdate = new StringBuffer();
				queryUpdate.append("UPDATE ");
				queryUpdate.append(Costanti.REPOSITORY);
				queryUpdate.append(" SET ");
				queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_ProfiloCollaborazione(true));
				queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
				pstmtUpdate.setString(1,id);
				pstmtUpdate.setString(2,Costanti.OUTBOX);

				// Add PreparedStatement
				state.getPreparedStatement().put("UPDATE saveBustaForProfilo"+Costanti.OUTBOX+"_"+id,pstmtUpdate);
			}	

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, Errore di registrazione richiesta inviata "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
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
	 * Controlla se e' stato precedentemente registrata una richiesta asincrona asimmetrica,
	 * ritornando eventuali dati del servizio di richiesta per la consegna della risposta.
	 *
	 * @param riferimentoMessaggio RiferimentoMessaggio
	 * @return Dati di integrazione per la consegna della risposta
	 * 
	 */
	public IDServizio asincronoAsimmetrico_getDatiConsegnaRisposta(String riferimentoMessaggio) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();


		//		 Imposto ricevuta applicativa abilitata: pooling
		PreparedStatement pstmtPooling = null;
		try{	

			StringBuffer query = new StringBuffer();
			query.append("UPDATE ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" SET RICEVUTA_APPLICATIVA=1 WHERE  ID_MESSAGGIO = ? AND TIPO=?");

			pstmtPooling = connectionDB.prepareStatement(query.toString());
			pstmtPooling.setString(1,riferimentoMessaggio);
			pstmtPooling.setString(2,Costanti.INBOX);

			//	Add PreparedStatement
			state.getPreparedStatement().put("UPDATE asincronoAsimmetrico_getDatiConsegnaRisposta "+riferimentoMessaggio,pstmtPooling);

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, Errore asincronoAsimmetrico_getDatiConsegnaRisposta, update RICEVUTA_APPLICATIVA "+riferimentoMessaggio+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if( pstmtPooling != null )
					pstmtPooling.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

		try{	
			// I dati asincroni saranno eliminati quando scadra' la busta asincrona
			History history = new History(state,this.log);
			history.registraBustaRicevuta(riferimentoMessaggio);

			// Prendo i dati di integrazione
			RepositoryBuste repositoryBuste = new RepositoryBuste(state,  this.log, this.protocolFactory);
			LetturaParametriBusta parametri = new LetturaParametriBusta();
			parametri.setServizio(true);
			parametri.setAzione(true);
			Busta busta = repositoryBuste.getSomeValuesFromInBox(riferimentoMessaggio, parametri, true); // leggo dati da database
			if(busta==null)
				throw new Exception("Dati non trovati");
			else{
				IDServizio id = new IDServizio();
				id.setTipoServizio(busta.getTipoServizio());
				id.setServizio(busta.getServizio());
				id.setAzione(busta.getAzione());
				return id;
			}

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, Errore durante la asincronoAsimmetrico_getDatiConsegnaRisposta "+riferimentoMessaggio+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}

	}

	/**
	 * Controlla se e' stato precedentemente registrata una richiesta asincrona asimmetrica,
	 * ritornando eventuali dati dell'integrazione per la consegna della ricevuta.
	 *
	 * @return Dati di integrazione per la consegna della risposta
	 * 
	 */
	public Integrazione asincronoAsimmetrico_getDatiConsegnaRicevuta(String riferimentoMessaggio) throws ProtocolException{
		if(this.state instanceof StatefulMessage) {
			StatefulMessage stateful = (StatefulMessage)this.state;
			try{	
				// Prendo i dati di integrazione
				RepositoryBuste repositoryBuste = new RepositoryBuste(stateful, this.log, this.protocolFactory);
				Integrazione integrazione = repositoryBuste.getInfoIntegrazioneFromOutBox(riferimentoMessaggio);
				if(integrazione==null)
					throw new Exception("Dati di integrazione non trovati");
				else
					return integrazione;

			} catch(Exception e) {
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, Errore durante la asincronoAsimmetrico_getDatiConsegnaRicevuta "+riferimentoMessaggio+": "+e.getMessage();		
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}
		}else{
			throw new ProtocolException("Metodo invocato con IState non valido");
		}
	}

	/**
	 * Registrazione di una richiesta ricevuta.
	 * Viene mantenuto nella tabella asincrona una entry con ID_MESSAGGIO, INBOX 
	 * e ServizioCorrelato inserito nella richiesta.
	 * 
	 * @param id Identificativo della richiesta
	 * @param tipoServizioCorrelato Tipo del Servizio Correlato
	 * @param servizioCorrelato Servizio Correlato
	 * @param ricevutaApplicativa Indicazione sull'abilitazione di una ricevuta applicativa
	 */
	public void asincronoAsimmetrico_registraRichiestaRicevuta(String id,String idCollaborazione,
			String tipoServizioCorrelato,String servizioCorrelato,
			boolean ricevutaApplicativa,long scadenzaMessaggi)throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmtUpdate = null;
		try{	

			java.sql.Timestamp oraRicezione = DateManager.getTimestamp();

			StringBuffer querySearch = new StringBuffer();
			querySearch.append("SELECT ID_MESSAGGIO FROM ");
			querySearch.append(Costanti.PROFILO_ASINCRONO);
			querySearch.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");
			pstmt = connectionDB.prepareStatement(querySearch.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,Costanti.INBOX);
			rs = pstmt.executeQuery();
			boolean exists = rs.next();
			rs.close();
			pstmt.close();


			StringBuffer query = new StringBuffer();
			if(exists){
				query.append("UPDATE ");
				query.append(Costanti.PROFILO_ASINCRONO);
				query.append(" SET ID_MESSAGGIO=?,TIPO=?,ORA_REGISTRAZIONE=?,RICEVUTA_ASINCRONA=?,TIPO_SERVIZIO_CORRELATO=?,SERVIZIO_CORRELATO=?," +
				"IS_RICHIESTA=?,ID_ASINCRONO=?,ID_COLLABORAZIONE=?,RICEVUTA_APPLICATIVA=? WHERE ID_MESSAGGIO=? AND TIPO=?");
			}else{
				query.append("INSERT INTO  ");
				query.append(Costanti.PROFILO_ASINCRONO);
				query.append(" (ID_MESSAGGIO,TIPO,ORA_REGISTRAZIONE,RICEVUTA_ASINCRONA,TIPO_SERVIZIO_CORRELATO,SERVIZIO_CORRELATO");
				query.append(",IS_RICHIESTA,ID_ASINCRONO,ID_COLLABORAZIONE,RICEVUTA_APPLICATIVA) ");
				query.append(" VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? , ? )");
			}


			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,Costanti.INBOX);
			pstmt.setTimestamp(3,oraRicezione);
			pstmt.setInt(4,1); // stato ricevuta: non attesa
			pstmt.setString(5,tipoServizioCorrelato);
			pstmt.setString(6,servizioCorrelato);
			pstmt.setInt(7,0); // passo nella fase di ricezione richiesta-stato
			pstmt.setString(8,id); // idasincrono utilizzato per identificare questa instanza di cooperazione asincrona
			pstmt.setString(9,idCollaborazione); // idCollaborazione
			if(ricevutaApplicativa){
				pstmt.setInt(10,1); // ricevuta applicativa abilitata
			}else{
				pstmt.setInt(10,0); // ricevuta applicativa non abilitata
			}
			if(exists){
				pstmt.setString(11,id);
				pstmt.setString(12,Costanti.INBOX);
			}

			//	Add PreparedStatement
			if(exists){
				state.getPreparedStatement().put("UPDATE save_RichiestaAsincronaAsimmetricaRicevuta"+id,pstmt);
			}else{
				state.getPreparedStatement().put("INSERT save_RichiestaAsincronaAsimmetricaRicevuta"+id,pstmt);
			}

			if(this.state instanceof StatefulMessage) {
				
				// salvo busta per profilo
				this.registraBustaRicevuta(id);
				
			}else{
				
				StatelessMessage statelessMessage = (StatelessMessage) this.state;
				
				long scadenza = scadenzaMessaggi;
				if(scadenza<=0){
					scadenza = this.protocolManager.getIntervalloScadenzaBuste();
				}
				
				// Registro proprio la busta
				RepositoryBuste repository = new RepositoryBuste(this.state,  this.log, true,this.protocolFactory);
				String key = "INSERT RegistrazioneBustaForHistory"+Costanti.INBOX+"_"+statelessMessage.getBusta().getID();
				if(repository.isRegistrataIntoInBox(statelessMessage.getBusta().getID())){
					repository.aggiornaBustaIntoInBox(statelessMessage.getBusta(),scadenza);
				}
				else if(state.getPreparedStatement().containsKey(key)){
					repository.aggiornaBustaIntoInBox(statelessMessage.getBusta(),scadenza);
				}else{
					repository.registraBustaIntoInBox(statelessMessage.getBusta(),scadenza);
				}
				this.registraBustaInviata(id);
				
				// salvo busta per profilo
				StringBuffer queryUpdate = new StringBuffer();
				queryUpdate.append("UPDATE ");
				queryUpdate.append(Costanti.REPOSITORY);
				queryUpdate.append(" SET ");
				queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_ProfiloCollaborazione(true));
				queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
				pstmtUpdate.setString(1,id);
				pstmtUpdate.setString(2,Costanti.INBOX);

				// Add PreparedStatement
				state.getPreparedStatement().put("UPDATE saveBustaForProfilo"+Costanti.INBOX+"_"+id,pstmtUpdate);
			}

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, Errore di registrazione richiesta ricevuta "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
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
	 * Controlla se e' stato precedentemente registrata una richiesta asincrona asimmetrica,
	 * ritornando eventuale busta da usare con la risposta applicativa.
	 *
	 * @param id Identificativo della richiesta.
	 * @param idRisposta Identificativo della risposta.
	 * @return Dati di integrazione per la consegna della risposta
	 * 
	 */
	public Busta asincronoAsimmetrico_getBustaRisposta(String id,String idRisposta,long scadenzaMessaggi) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		// getBustaRicevuta
		Busta busta = null;
		try{
			RepositoryBuste repository = new RepositoryBuste(state,  this.log, this.protocolFactory);
			LetturaParametriBusta parametri = new LetturaParametriBusta();
			parametri.setMittente(true);
			parametri.setDestinatario(true);
			parametri.setServizioCorrelato(true);
			busta = repository.getSomeValuesFromOutBox(id, parametri, true); // Ultimo parametro forza la lettura su database

		}catch(Exception e){
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, Errore durante la getBusta dal repository "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}

		// ProfiloDiCollaborazione
		busta.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO);

		// check eventuali spedizione-risposta precedentemente effettuate
		PreparedStatement pstmtCheck = null;
		ResultSet rsCheck = null;
		String oldIDRisposta = null;
		String oldTipoRisposta = null;
		try{
			StringBuffer query = new StringBuffer();
			query.append("SELECT ID_MESSAGGIO,TIPO FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE BACKUP_ID_RICHIESTA=?");

			pstmtCheck = connectionDB.prepareStatement(query.toString());
			pstmtCheck.setString(1,id);
			rsCheck = pstmtCheck.executeQuery();		
			if(rsCheck == null) {
				throw new ProtocolException("RS Check Null?");			
			}
			if(rsCheck.next()){
				oldIDRisposta = rsCheck.getString("ID_MESSAGGIO");
				oldTipoRisposta = rsCheck.getString("TIPO");
			}		
			rsCheck.close();
			pstmtCheck.close();

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, asincronoAsimmetrico_getBustaRisposta "+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( rsCheck != null )
					rsCheck.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if( pstmtCheck != null )
					pstmtCheck.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

		//		 leggo servizio correlato
		String tipoServizioCorrelato = null;
		String servizioCorrelato = null;
		PreparedStatement pstmtRead = null;
		ResultSet rsRead = null;
		try{
			StringBuffer query = new StringBuffer();
			query.append("SELECT TIPO_SERVIZIO_CORRELATO,SERVIZIO_CORRELATO FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

			pstmtRead = connectionDB.prepareStatement(query.toString());
			if(oldIDRisposta!=null && oldTipoRisposta!=null){
				pstmtRead.setString(1,oldIDRisposta);
				pstmtRead.setString(2,oldTipoRisposta);
			}else{
				pstmtRead.setString(1,id);
				pstmtRead.setString(2,Costanti.OUTBOX);
			}
			rsRead = pstmtRead.executeQuery();		
			if(rsRead == null) {
				throw new ProtocolException("RS Read Null?");			
			}
			if(rsRead.next()){
				tipoServizioCorrelato = rsRead.getString("TIPO_SERVIZIO_CORRELATO");
				servizioCorrelato = rsRead.getString("SERVIZIO_CORRELATO");
			}		
			rsRead.close();
			pstmtRead.close();

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, asincronoAsimmetrico_getBustaRisposta, raccolta servizio correlato, "+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( rsRead != null )
					rsRead.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if( pstmtRead != null )
					pstmtRead.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}
		busta.setTipoServizioCorrelato(tipoServizioCorrelato);
		busta.setServizioCorrelato(servizioCorrelato);

		// Imposto parametri per la consegna della risposta
		PreparedStatement pstmt = null;
		try{	

			java.sql.Timestamp oraInvioRisposta = DateManager.getTimestamp();

			StringBuffer query = new StringBuffer();
			query.append("UPDATE ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" SET IS_RICHIESTA=0, RICEVUTA_ASINCRONA=0, ORA_REGISTRAZIONE=?, ID_MESSAGGIO=?, TIPO=?, BACKUP_ID_RICHIESTA=?, RICEVUTA_APPLICATIVA=1 WHERE  ID_MESSAGGIO = ? AND TIPO=?");

			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setTimestamp(1,oraInvioRisposta);
			pstmt.setString(2,idRisposta);
			pstmt.setString(3,Costanti.OUTBOX);
			pstmt.setString(4,id);
			if(oldIDRisposta!=null && oldTipoRisposta!=null){
				pstmt.setString(5,oldIDRisposta);
				pstmt.setString(6,oldTipoRisposta);
			}else{
				pstmt.setString(5,id);
				pstmt.setString(6,Costanti.OUTBOX);
			}

			//	Add PreparedStatement
			state.getPreparedStatement().put("UPDATE save_RispostaAsincronaAsimmetricaInviata"+id,pstmt);

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, Errore di registrazione risposta inviata "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

		History history = new History(state,this.log);

		// Registro la richiesta nell'history 
		history.registraBustaInviata(id);

		// Registro la richiesta stato nell'history con il nuovo id
		if(this.state instanceof StatefulMessage) {
			
			// salvo busta per profilo
			this.registraBustaInviata(idRisposta);
			
		}else{
			
			StatelessMessage statelessMessage = (StatelessMessage) this.state;
			
			long scadenza = scadenzaMessaggi;
			if(scadenza<=0){
				scadenza = this.protocolManager.getIntervalloScadenzaBuste();
			}
			
			// Registro proprio la busta
			RepositoryBuste repository = new RepositoryBuste(this.state,  this.log, true,this.protocolFactory);
			repository.registraBustaIntoOutBox(statelessMessage.getBusta(),scadenza);
			this.registraBustaInviata(idRisposta);
			
			// salvo busta per profilo
			PreparedStatement pstmtUpdate = null;
			try{
				StringBuffer queryUpdate = new StringBuffer();
				queryUpdate.append("UPDATE ");
				queryUpdate.append(Costanti.REPOSITORY);
				queryUpdate.append(" SET ");
				queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_ProfiloCollaborazione(true));
				queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
				pstmtUpdate.setString(1,idRisposta);
				pstmtUpdate.setString(2,Costanti.OUTBOX);

				// Add PreparedStatement
				state.getPreparedStatement().put("UPDATE saveBustaForProfilo"+Costanti.OUTBOX+"_"+idRisposta,pstmtUpdate);
				
			} catch(Exception e) {
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, Errore di aggiornamento busta di risposta "+idRisposta+": "+e.getMessage();		
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
		

		//	I dati asincroni saranno eliminati quando scadra' la richiesta-stato asincrona
		history.registraBustaInviata(idRisposta);

		//	delete vecchia risposta
		if(oldIDRisposta!=null && oldTipoRisposta!=null){
			this.eliminaBusta(oldIDRisposta,oldTipoRisposta,true);
			history.eliminaBusta(oldIDRisposta,oldTipoRisposta,true);
		}

		return busta;
	}

	/**
	 * Controlla se e' stato precedentemente registrata una richiesta asincrona simmetrica,
	 * ritornando eventuale busta da usare con la ricevuta applicativa.
	 *
	 * @param id Identificativo della richiesta asincrona.
	 * @param rifMsgRicevuta Riferimento Messaggio da applicare alla ricevuta.
	 * @param isRichiesta Indicazione se si desidera la busta per una ricevuta ad una richiesta o risposta
	 * @return Busta da utilizzare per la ricevuta
	 * 
	 */
	public Busta asincronoAsimmetrico_getBustaRicevuta(String id,String rifMsgRicevuta,boolean isRichiesta,boolean generazioneAttributiAsincroni,TipoOraRegistrazione tipoTempo) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		// leggo servizio correlato
		String tipoServizioCorrelato = null;
		String servizioCorrelato = null;
		PreparedStatement pstmtRead = null;
		ResultSet rsRead = null;
		try{
			StringBuffer query = new StringBuffer();
			query.append("SELECT TIPO_SERVIZIO_CORRELATO,SERVIZIO_CORRELATO FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

			pstmtRead = connectionDB.prepareStatement(query.toString());
			pstmtRead.setString(1,id);
			pstmtRead.setString(2,Costanti.INBOX);
			rsRead = pstmtRead.executeQuery();		
			if(rsRead == null) {
				throw new ProtocolException("RS Read Null?");			
			}
			if(rsRead.next()){
				tipoServizioCorrelato = rsRead.getString("TIPO_SERVIZIO_CORRELATO");
				servizioCorrelato = rsRead.getString("SERVIZIO_CORRELATO");
			}		
			rsRead.close();
			pstmtRead.close();

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, asincronoAsimmetrico_getBustaRicevuta, raccolta servizio correlato, "+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( rsRead != null )
					rsRead.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if( pstmtRead != null )
					pstmtRead.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}



		Busta busta = null;
		try{
			RepositoryBuste repository = new RepositoryBuste(state,  this.log, this.protocolFactory);
			LetturaParametriBusta parametri = new LetturaParametriBusta();
			parametri.setMittente(true);
			parametri.setDestinatario(true);
			parametri.setServizio(true);
			parametri.setAzione(true);
			parametri.setCollaborazione(true);
			parametri.setProfiloTrasmissione(true);
			parametri.setIndirizziTelematici(this.protocolConfiguration.isSupportoIndirizzoRisposta());
			busta = repository.getSomeValuesFromInBox(id, parametri, true); // prendo dati dal database

			if(isRichiesta==false){

				// elimino busta per profilo
				this.eliminaBustaRicevuta(id, true);
			}

			// costruzione busta di risposta   

			// profilo di collaborazione
			busta.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO);

			// inverto mitt con dest 
			String mitt = busta.getMittente();
			String tipoMitt = busta.getTipoMittente();
			String portaMitt = busta.getIdentificativoPortaMittente();
			String indMitt = busta.getIndirizzoMittente();
			busta.setMittente(busta.getDestinatario());
			busta.setTipoMittente(busta.getTipoDestinatario());
			busta.setIdentificativoPortaMittente(busta.getIdentificativoPortaDestinatario());
			busta.setIndirizzoMittente(busta.getIndirizzoDestinatario());
			busta.setDestinatario(mitt);
			busta.setTipoDestinatario(tipoMitt);
			busta.setIdentificativoPortaDestinatario(portaMitt);
			busta.setIndirizzoDestinatario(indMitt);

			if(isRichiesta){
				// PRODUZIONE tipo e nome Servizio Correlato
				if(generazioneAttributiAsincroni){
					busta.setTipoServizioCorrelato(tipoServizioCorrelato);
					busta.setServizioCorrelato(servizioCorrelato);
				}
			}
			else{
				busta.setTipoServizio(tipoServizioCorrelato);
				busta.setServizio(servizioCorrelato);
			}

			busta.setRiferimentoMessaggio(rifMsgRicevuta);
			busta.setOraRegistrazione(DateManager.getDate());
			busta.setTipoOraRegistrazione(tipoTempo,this.protocolTraduttore.toString(tipoTempo));

		}catch(Exception e){
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, Errore durante la asincronoAsimmetrico_getBustaRicevuta dal repository "+id+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}

		return busta;

	}

	/**
	 * Validazione della busta, che sia coerente con la precedente richiesta asincrona asimmetrica.
	 *
	 * @param bustaDaValidare Busta da controllare
	 * @return una Eccezione se la validazione fallisce, null altrimenti.
	 * 
	 */
	public Eccezione asincronoAsimmetrico_validazioneCorrelazione_ricevutaRichiesta(Busta bustaDaValidare, IProtocolFactory protocolFactory) throws ProtocolException{
		return asincronoAsimmetrico_validazioneCorrelazione(bustaDaValidare,true,false,false,protocolFactory);
	}	

	/**
	 * Validazione della busta, che sia coerente con la precedente richiesta asincrona asimmetrica.
	 *
	 * @param bustaDaValidare Busta da controllare
	 * @return una Eccezione se la validazione fallisce, null altrimenti.
	 * 
	 */
	public Eccezione asincronoAsimmetrico_validazioneCorrelazione_richiestaStato(Busta bustaDaValidare, IProtocolFactory protocolFactory) throws ProtocolException{
		return asincronoAsimmetrico_validazioneCorrelazione(bustaDaValidare,false,true,false,protocolFactory);
	}	

	/**
	 * Validazione della busta, che sia coerente con la precedente richiesta asincrona asimmetrica.
	 *
	 * @param bustaDaValidare Busta da controllare
	 * @return una Eccezione se la validazione fallisce, null altrimenti.
	 * 
	 */
	public Eccezione asincronoAsimmetrico_validazioneCorrelazione_ricevutaRichiestaStato(Busta bustaDaValidare, IProtocolFactory protocolFactory) throws ProtocolException{
		return asincronoAsimmetrico_validazioneCorrelazione(bustaDaValidare,false,false,true,protocolFactory);
	}	


	/**
	 * Validazione della busta, che sia coerente con la precedente richiesta asincrona asimmetrica.
	 *
	 * @param bustaDaValidare Busta da controllare
	 * @param isRicevutaRichiesta Ricevuta della Richiesta
	 * @param isRichiestaStato RichiestaStato
	 * @param isRicevutaRichiestaStato Ricevuta della Risposta
	 * @return una Eccezione se la validazione fallisce, null altrimenti.
	 * 
	 */
	private Eccezione asincronoAsimmetrico_validazioneCorrelazione(Busta bustaDaValidare,boolean isRicevutaRichiesta,boolean isRichiestaStato,boolean isRicevutaRichiestaStato, IProtocolFactory protocolFactory) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		Busta bustaRichiesta = null;
		try{
			RepositoryBuste repository = new RepositoryBuste(state,  this.log, this.protocolFactory);
			LetturaParametriBusta parametri = new LetturaParametriBusta();
			parametri.setMittente(true);
			parametri.setDestinatario(true);
			parametri.setServizio(true);
			parametri.setAzione(true);
			if(isRichiestaStato){
				// NOTA:
				//   una busta asincrona RISPOSTA puo avere riferimentoMessaggio o collaborazione
				if(bustaDaValidare.getRiferimentoMessaggio()==null)
					bustaRichiesta = repository.getSomeValuesFromInBox(bustaDaValidare.getCollaborazione(),parametri,true); // deve leggere sempre da database
				else
					bustaRichiesta = repository.getSomeValuesFromInBox(bustaDaValidare.getRiferimentoMessaggio(),parametri,true); // deve leggere sempre da database
			}else
				bustaRichiesta = repository.getSomeValuesFromOutBox(bustaDaValidare.getRiferimentoMessaggio(),parametri,true); // deve leggere sempre da database
		}catch(Exception e){
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, Errore durante la validazioneCorrelazione, get dal repository RifMsg("+bustaDaValidare.getRiferimentoMessaggio()+") Collab("+bustaDaValidare.getCollaborazione()+"): "+e.getMessage();
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}

		// Validazione Servizio (Tipo e Nome)
		// Obbligatoria per una risposta, opzionale per una ricevuta
		if( isRicevutaRichiesta ){
			if(bustaDaValidare.getServizio()!=null && bustaDaValidare.getTipoServizio()!=null){
				if (bustaRichiesta.getTipoServizio().equals(bustaDaValidare.getTipoServizio())==false){
					String msgErrore = "Tipo di servizio diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
					Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_SERVIZIO_NON_VALIDO.
							getErroreCooperazione(msgErrore),
							true,null,protocolFactory);
					this.log.error(msgErrore);
					return eccValidazione;
				}
				if (bustaRichiesta.getServizio().equals(bustaDaValidare.getServizio())==false){
					String msgErrore = "Servizio diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
					Eccezione eccValidazione = new Eccezione(ErroriCooperazione.SERVIZIO_NON_VALIDO.
							getErroreCooperazione(msgErrore),
							true,null,protocolFactory);
					this.log.error(msgErrore);
					return eccValidazione;
				}
			}
		}
		else if(  (isRichiestaStato) || (isRicevutaRichiestaStato && bustaDaValidare.getServizio()!=null && bustaDaValidare.getTipoServizio()!=null) ) {

			// Validazione Servizio (Tipo e Nome)
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{
				StringBuffer query = new StringBuffer();
				query.append("SELECT TIPO_SERVIZIO_CORRELATO,SERVIZIO_CORRELATO FROM ");
				query.append(Costanti.PROFILO_ASINCRONO);
				query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

				pstmt = connectionDB.prepareStatement(query.toString());

				// NOTA:
				//   una busta asincrona RISPOSTA puo avere riferimentoMessaggio o collaborazione
				if(isRichiestaStato && bustaDaValidare.getRiferimentoMessaggio()==null)
					pstmt.setString(1,bustaDaValidare.getCollaborazione());
				else
					pstmt.setString(1,bustaDaValidare.getRiferimentoMessaggio());			

				if(isRichiestaStato)
					pstmt.setString(2,Costanti.INBOX);
				else
					pstmt.setString(2,Costanti.OUTBOX);
				rs = pstmt.executeQuery();		
				if(rs == null) {
					throw new ProtocolException("RS Null?");			
				}
				String tipoServizioCorrelatoRegistrato = null;
				String servizioCorrelatoRegistrato = null;
				if(rs.next()){
					tipoServizioCorrelatoRegistrato = rs.getString("TIPO_SERVIZIO_CORRELATO");
					servizioCorrelatoRegistrato = rs.getString("SERVIZIO_CORRELATO");
				}
				rs.close();
				pstmt.close();

				if (bustaDaValidare.getTipoServizio().equals(tipoServizioCorrelatoRegistrato) == false){
					String msgErrore = "Tipo di servizio diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
					Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_SERVIZIO_NON_VALIDO.
							getErroreCooperazione(msgErrore),
							true,null,protocolFactory);
					this.log.error(msgErrore);
					return eccValidazione;
				}
				if (bustaDaValidare.getServizio().equals(servizioCorrelatoRegistrato) == false){
					String msgErrore = "Servizio diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
					Eccezione eccValidazione = new Eccezione(ErroriCooperazione.SERVIZIO_NON_VALIDO.
							getErroreCooperazione(msgErrore),
							true,null,protocolFactory);
					this.log.error(msgErrore);
					return eccValidazione;
				}


			}catch(Exception e){	
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, validazioneCorrelazione RifMsg("+bustaDaValidare.getRiferimentoMessaggio()+") Collab("+bustaDaValidare.getCollaborazione()+"): "+e.getMessage();
				this.log.error(errorMsg,e);
				try{
					if( rs != null )
						rs.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				try{
					if( pstmt != null )
						pstmt.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				throw new ProtocolException(errorMsg,e);
			}
		}

		// Validazione altri parametri busta
		// L'azione di una ricevuta deve essere la stessa di quella della richiesta/richiestaStato
		if(bustaDaValidare.getAzione()!=null && isRichiestaStato==false){
			if (bustaDaValidare.getAzione().equals(bustaRichiesta.getAzione()) == false){
				String msgErrore = "Azione diversa da quello attesa nella gestione del profilo di collaborazione Asincrono Asimmetrico";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.AZIONE_NON_VALIDA.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
		}
		if(isRichiestaStato){
			if (bustaRichiesta.getTipoMittente().equals(bustaDaValidare.getTipoMittente()) == false){
				String msgErrore = "Tipo del mittente diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_MITTENTE_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
			if (bustaRichiesta.getMittente().equals(bustaDaValidare.getMittente()) == false){
				String msgErrore = "Mittente diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.MITTENTE_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
			if (bustaRichiesta.getTipoDestinatario().equals(bustaDaValidare.getTipoDestinatario()) == false){
				String msgErrore = "Tipo del destinatario diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_DESTINATARIO_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
			if (bustaRichiesta.getDestinatario().equals(bustaDaValidare.getDestinatario()) == false){
				String msgErrore = "Destinatario diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.DESTINATARIO_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
		}else{
			if (bustaRichiesta.getTipoDestinatario().equals(bustaDaValidare.getTipoMittente()) == false){
				String msgErrore = "Tipo del mittente diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_MITTENTE_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
			if (bustaRichiesta.getDestinatario().equals(bustaDaValidare.getMittente()) == false){
				String msgErrore = "Mittente diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.MITTENTE_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
			if (bustaRichiesta.getTipoMittente().equals(bustaDaValidare.getTipoDestinatario()) == false){
				String msgErrore = "Tipo del destinatario diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.TIPO_DESTINATARIO_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
			if (bustaRichiesta.getMittente().equals(bustaDaValidare.getDestinatario()) == false){
				String msgErrore = "Destinatario diverso da quello atteso nella gestione del profilo di collaborazione Asincrono Asimmetrico";
				Eccezione eccValidazione = new Eccezione(ErroriCooperazione.DESTINATARIO_NON_VALIDO.
						getErroreCooperazione(msgErrore),
						true,null,protocolFactory);
				this.log.error(msgErrore);
				return eccValidazione;
			}
		}
		return null;
	}

	/**
	 * Validazione del riferimentoMessaggio di una busta asincrona asimmetrica.
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @return true se esiste una entry nella tabella apposita.
	 * 
	 */
	public boolean asincronoAsimmetrico_validazioneRiferimentoMessaggio_richiestaStato(String riferimentoMessaggio) throws ProtocolException{
		return asincronoAsimmetrico_validazioneRiferimentoMessaggio(riferimentoMessaggio,true);
	}

	/**
	 * Validazione del riferimentoMessaggio di una busta asincrona asimmetrica.
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @return true se esiste una entry nella tabella apposita.
	 * 
	 */
	public boolean asincronoAsimmetrico_validazioneRiferimentoMessaggio_ricevuta(String riferimentoMessaggio) throws ProtocolException{
		return asincronoAsimmetrico_validazioneRiferimentoMessaggio(riferimentoMessaggio,false);
	}

	/**
	 * Validazione del riferimentoMessaggio di una busta asincrona asimmetrica.
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @param isRichiestaStato Indicazione se la busta da validare e' una richiesta stato
	 * @return true se esiste una entry nella tabella apposita.
	 * 
	 */
	private boolean asincronoAsimmetrico_validazioneRiferimentoMessaggio(String riferimentoMessaggio,boolean isRichiestaStato) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			StringBuffer query = new StringBuffer();
			query.append("SELECT ID_MESSAGGIO FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,riferimentoMessaggio);
			if(isRichiestaStato)
				pstmt.setString(2,Costanti.INBOX);
			else
				pstmt.setString(2,Costanti.OUTBOX);
			rs = pstmt.executeQuery();		
			if(rs == null) {
				throw new ProtocolException("RS Null?");			
			}
			boolean value = rs.next();

			rs.close();
			pstmt.close();

			RepositoryBuste repositoryBuste = new RepositoryBuste(state, this.log, this.protocolFactory);
			if(isRichiestaStato)
				return value && repositoryBuste.isRegistrataIntoInBox(riferimentoMessaggio);
			else
				return value && repositoryBuste.isRegistrataIntoOutBox(riferimentoMessaggio);

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, validazioneRiferimentoMessaggio "+riferimentoMessaggio+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
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
	 * Validazione della collaborazione di una busta asincrona.
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @param idCollaborazione Collaborazione
	 * @return true se la collaborazione e' valida
	 * 
	 */
	public boolean asincronoAsimmetrico_validazioneCollaborazione_richiestaStato(String riferimentoMessaggio,String idCollaborazione) throws ProtocolException{
		return asincronoAsimmetrico_validazioneCollaborazione(riferimentoMessaggio,idCollaborazione,true);
	}

	/**
	 * Validazione della collaborazione di una busta asincrona.
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @param idCollaborazione Collaborazione
	 * @return true se la collaborazione e' valida
	 * 
	 */
	public boolean asincronoAsimmetrico_validazioneCollaborazione_ricevuta(String riferimentoMessaggio,String idCollaborazione) throws ProtocolException{
		return asincronoAsimmetrico_validazioneCollaborazione(riferimentoMessaggio,idCollaborazione,false);
	}

	/**
	 * Validazione della collaborazione di una busta asincrona.
	 *
	 * @param riferimentoMessaggio Identificativo.
	 * @param idCollaborazione Collaborazione
	 * @param isRichiestaStato Indicazione se la busta da validare e' una richiesta stato
	 * @return true se la collaborazione e' valida
	 * 
	 */
	private boolean asincronoAsimmetrico_validazioneCollaborazione(String riferimentoMessaggio,String idCollaborazione,boolean isRichiestaStato) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			StringBuffer query = new StringBuffer();
			query.append("SELECT ID_COLLABORAZIONE FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

			pstmt = connectionDB.prepareStatement(query.toString());

			// NOTA:
			//   una busta asincrona RISPOSTA puo avere riferimentoMessaggio o collaborazione
			if(isRichiestaStato && riferimentoMessaggio==null)
				pstmt.setString(1,idCollaborazione);
			else
				pstmt.setString(1,riferimentoMessaggio);

			if(isRichiestaStato)
				pstmt.setString(2,Costanti.INBOX);
			else
				pstmt.setString(2,Costanti.OUTBOX);
			rs = pstmt.executeQuery();		
			if(rs == null) {
				throw new ProtocolException("RS Null?");			
			}
			String value = null;
			if(rs.next()){
				value = rs.getString("ID_COLLABORAZIONE");
			}

			rs.close();
			pstmt.close();

			return idCollaborazione.equals(value);

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONOASIMMETRICO, validazioneCollaborazione "+riferimentoMessaggio+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if( pstmt != null )
					pstmt.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}
	}


	
	
	
	
	
	
	
	
	

	/* ********  PROFILO ASINCRONO ******** */

	/**
	 * In caso esista nella tabella asincrona buste inviate che non sono state ringraziate da una ricevuta dopo un intervallo di tempo,
	 * si occupa di ritornare un array di buste da reinviare, aggiornando le loro date di registrazione.
	 *
	 * @param timeout Minuti dopo il quale una data risulta scaduta.
	 * @return un vector di {@link org.openspcoop2.protocol.sdk.Busta} contenente le informazioni necessarie per il re-invio delle buste, 
	 *         se esistono buste non ringraziate.
	 * @deprecated utilizzare la versione non serializable
	 */
	@Deprecated
	public Vector<Busta> asincrono_getBusteAsincronePerUlterioreInoltro_serializable(long timeout)throws ProtocolException{
		return this.asincrono_getBusteAsincronePerUlterioreInoltro_serializable(timeout,60l,100);
	}

	/**
	 * In caso esista nella tabella asincrona buste inviate che non sono state ringraziate da una ricevuta dopo un intervallo di tempo,
	 * si occupa di ritornare un array di buste da reinviare, aggiornando le loro date di registrazione.
	 *
	 * @param timeout Minuti dopo il quale una data risulta scaduta.
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * @return un vector di {@link org.openspcoop2.protocol.sdk.Busta} contenente le informazioni necessarie per il re-invio delle buste, 
	 *         se esistono buste non ringraziate.
	 * @deprecated utilizzare la versione non serializable 
	 */
	@Deprecated
	public Vector<Busta> asincrono_getBusteAsincronePerUlterioreInoltro_serializable(long timeout,long attesaAttiva,int checkInterval)throws ProtocolException{
		if(this.state instanceof StatefulMessage) {
			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();

			/*
			 * Viene realizzato con livello di isolamento SERIALIZABLE, per essere sicuri
			 *	di leggere solo le indicazioni di richieste asincrone committate,
			 * e comunque rispedira' anche le buste eliminate, ma non committate.
			 */
			// setAutoCommit e livello Isolamento
			int oldTransactionIsolation = -1;
			try{
				oldTransactionIsolation = connectionDB.getTransactionIsolation();
				connectionDB.setAutoCommit(false);
				JDBCUtilities.setTransactionIsolationSerializable(Configurazione.getSqlQueryObjectType(), connectionDB);
			} catch(Exception er) {
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, Errore durante la getBusteAsincronePerUlterioreInoltro(setIsolation): "+er.getMessage();		
				this.log.error(errorMsg,er);
				throw new ProtocolException(errorMsg,er);
			}

			boolean getBusteOK = false;
			java.util.Vector<Busta> busteNonRingraziate = new java.util.Vector<Busta>();	

			long scadenzaWhile = DateManager.getTimeMillis() + attesaAttiva;

			while(getBusteOK==false && DateManager.getTimeMillis() < scadenzaWhile){

				PreparedStatement pstmt = null;
				ResultSet rs = null;
				java.util.Vector<String> IDBuste = new java.util.Vector<String>();
				try{	

					long nowTime = DateManager.getTimeMillis() - (timeout * 60 * 1000);
					java.sql.Timestamp scadenzaRingraziamento = new java.sql.Timestamp(nowTime);

					StringBuffer query = new StringBuffer();
					query.append("SELECT ID_MESSAGGIO FROM ");
					query.append(Costanti.PROFILO_ASINCRONO);
					query.append(" WHERE ORA_REGISTRAZIONE < ? AND TIPO=? AND RICEVUTA_ASINCRONA=0 AND RICEVUTA_APPLICATIVA=0 FOR UPDATE");

					pstmt = connectionDB.prepareStatement(query.toString());
					pstmt.setTimestamp(1,scadenzaRingraziamento);	
					pstmt.setString(2,Costanti.OUTBOX);

					// Esecuzione comando SQL
					rs = pstmt.executeQuery();		
					if(rs == null) {
						pstmt.close();
						return null;
					}		
					while(rs.next()){
						String id = rs.getString("ID_MESSAGGIO");
						IDBuste.add(id);
					}
					rs.close();
					pstmt.close();
					if(IDBuste.size() > 0){
						RepositoryBuste repositoryBuste = new RepositoryBuste(stateful, this.log, this.protocolFactory);
						for(int i=0; i<IDBuste.size();i++){
							String id = IDBuste.get(i);
							try{
								Busta busta = repositoryBuste.getBustaFromOutBox(id);
								busteNonRingraziate.add(busta);
							}catch(Exception e){
								String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, Errore durante la getBusteAsincronePerUlterioreInoltro (read busta ["+id+"]): "+e.getMessage();		
								this.log.error(errorMsg);
							}
						}
					}
					// Chiusura Transazione
					connectionDB.commit();

					// ID Costruito
					getBusteOK = true;

				} catch(Exception e) {
					//log.error("ERROR GET BUSTA ASINCRONA PER ULTERIORE INOLTRO ["+e.getMessage()+"]");
					try{
						if( pstmt != null )
							pstmt.close();
					} catch(Exception er) {}
					try{
						if( rs != null )
							rs.close();
					} catch(Exception er) {}
					try{
						connectionDB.rollback();
					} catch(Exception er) {}
				}

				if(getBusteOK == false){
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
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, Errore durante la getBusteAsincronePerUlterioreInoltro(ripristinoIsolation): "+er.getMessage();		
				this.log.error(errorMsg,er);
				throw new ProtocolException(errorMsg,er);
			}
			return busteNonRingraziate;
		}else if (this.state instanceof StatelessMessage){
			//CHECKME
			throw new ProtocolException("Metodo non invocabile in modalita Stateless");
		}else{
			throw new ProtocolException("Metodo invocato con IState non valido");
		}
	}


	/**
	 * In caso esista nella tabella asincrona buste inviate che non sono state ringraziate da una ricevuta dopo un intervallo di tempo,
	 * si occupa di ritornare un array di buste da reinviare, aggiornando le loro date di registrazione.
	 *
	 * @param timeout Minuti dopo il quale una data risulta scaduta.
	 * @return un vector di {@link org.openspcoop2.protocol.sdk.Busta} contenente le informazioni necessarie per il re-invio delle buste, 
	 *         se esistono buste non ringraziate.
	 */
	public Vector<BustaNonRiscontrata> asincrono_getBusteAsincronePerUlterioreInoltro(long timeout,int limit,int offset,boolean logQuery)throws ProtocolException{
		if(this.state instanceof StatefulMessage) {
			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			java.util.Vector<String> IDBuste = new java.util.Vector<String>();
			String queryString = null;
			try{	

				long nowTime = DateManager.getTimeMillis()- (timeout * 60 * 1000);
				java.sql.Timestamp scadenzaRingraziamento = new java.sql.Timestamp(nowTime);
				IGestoreRepository gestoreRepositoryBuste = Configurazione.getGestoreRepositoryBuste();
				
				if(Configurazione.getSqlQueryObjectType()==null){
					StringBuffer query = new StringBuffer();
					query.append("SELECT ID_MESSAGGIO FROM ");
					query.append(Costanti.PROFILO_ASINCRONO);
					query.append(" WHERE ORA_REGISTRAZIONE < ? AND TIPO=? AND RICEVUTA_ASINCRONA=0 AND RICEVUTA_APPLICATIVA=0");
					// Nuovo AND per la gestione stateless
					// Le ricevute asincrone non devono cmq appartenere a buste ormai eliminate
					query.append(" AND NOT EXISTS (SELECT ID_MESSAGGIO FROM ");
					query.append(Costanti.REPOSITORY);
					query.append(" WHERE ");
					query.append(Costanti.REPOSITORY);
					query.append(".TIPO=");
					query.append(Costanti.PROFILO_ASINCRONO);
					query.append(".TIPO");
					query.append(" AND ");
					query.append(Costanti.REPOSITORY);
					query.append(".ID_MESSAGGIO=");
					query.append(Costanti.PROFILO_ASINCRONO);
					query.append(".ID_MESSAGGIO");
					query.append(" AND ");
					query.append(gestoreRepositoryBuste.createSQLCondition_PdD(false));
					query.append(" AND ");
					query.append(gestoreRepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
					query.append(" ) ");
					
					queryString = query.toString();
				}else{
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());
					sqlQueryObject.addSelectField("ID_MESSAGGIO");
					sqlQueryObject.addSelectField("ORA_REGISTRAZIONE");
					sqlQueryObject.addSelectField("TIPO");
					sqlQueryObject.addSelectField("RICEVUTA_ASINCRONA");
					sqlQueryObject.addSelectField("RICEVUTA_APPLICATIVA");
					sqlQueryObject.addFromTable(Costanti.PROFILO_ASINCRONO);
					sqlQueryObject.addWhereCondition("ORA_REGISTRAZIONE < ?");
					sqlQueryObject.addWhereCondition("TIPO=?");
					sqlQueryObject.addWhereCondition("RICEVUTA_ASINCRONA=0");
					sqlQueryObject.addWhereCondition("RICEVUTA_APPLICATIVA=0");
					
					// Nuovo AND per la gestione stateless
					// Le ricevute asincrone non devono cmq appartenere a buste ormai eliminate
					ISQLQueryObject sqlQueryObjectNotExists = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());
					sqlQueryObjectNotExists.addFromTable(Costanti.REPOSITORY);
					sqlQueryObjectNotExists.addSelectField(Costanti.REPOSITORY,"ID_MESSAGGIO");
					sqlQueryObjectNotExists.addWhereCondition(Costanti.REPOSITORY+".TIPO="+Costanti.PROFILO_ASINCRONO+".TIPO");
					sqlQueryObjectNotExists.addWhereCondition(Costanti.REPOSITORY+".ID_MESSAGGIO="+Costanti.PROFILO_ASINCRONO+".ID_MESSAGGIO");
					sqlQueryObjectNotExists.addWhereCondition(gestoreRepositoryBuste.createSQLCondition_PdD(false));
					sqlQueryObjectNotExists.addWhereCondition(gestoreRepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
					sqlQueryObjectNotExists.setANDLogicOperator(true);
					sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectNotExists);
					
					sqlQueryObject.setANDLogicOperator(true);
					sqlQueryObject.addOrderBy("ORA_REGISTRAZIONE"); // order by e' obbligatorio essendoci l'offset
					sqlQueryObject.setSortType(true);
					sqlQueryObject.setLimit(limit);
					sqlQueryObject.setOffset(offset);
					queryString = sqlQueryObject.createSQLQuery();
				}

				//System.out.println("QUERY ASINCRONO IS: ["+queryString+"] 1["+scadenzaRingraziamento+"] 2["+Costanti.OUTBOX+"]");

				pstmt = connectionDB.prepareStatement(queryString);
				pstmt.setTimestamp(1,scadenzaRingraziamento);	
				pstmt.setString(2,Costanti.OUTBOX);

				// Esecuzione comando SQL
				long startDateSQLCommand = DateManager.getTimeMillis();
				if(logQuery)
					this.log.debug("[QUERY] (RicevuteAsincrone) ["+queryString+"] 1["+scadenzaRingraziamento+"] 2["+Costanti.OUTBOX+"]...");
				rs = pstmt.executeQuery();	
				long endDateSQLCommand = DateManager.getTimeMillis();
				long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
				if(logQuery)
					this.log.debug("[QUERY] (RicevuteAsincrone) ["+queryString+"] 1["+scadenzaRingraziamento+"] 2["+Costanti.OUTBOX+"] effettuata in "+secondSQLCommand+" secondi");

				if(rs == null) {
					pstmt.close();
					return null;
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
				String errorMsg = "[ProfiloDiCollaborazione.asincrono_getBusteAsincronePerUlterioreInoltro] errore, queryString["+queryString+"]: "+e.getMessage();
				this.log.error(errorMsg,e);
				throw new  ProtocolException(errorMsg,e);
			}

			Vector<BustaNonRiscontrata> listaBustaNonRiscontrata = new Vector<BustaNonRiscontrata>();
			for (int i = 0; i < IDBuste.size(); i++) {
				BustaNonRiscontrata bustaNonRiscontrata = new BustaNonRiscontrata();
				bustaNonRiscontrata.setIdentificativo(IDBuste.get(i));
				PreparedStatement pstmtReadProfilo = null;
				ResultSet rsReadProfilo = null;
				try{
					
					if(Configurazione.getSqlQueryObjectType()==null){
						StringBuffer query = new StringBuffer();
						query.append("SELECT PROFILO_DI_COLLABORAZIONE FROM ");
						query.append(Costanti.REPOSITORY);
						query.append(" WHERE ID_MESSAGGIO=? AND TIPO=? ");
						queryString = query.toString();
					}
					else{
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());
						sqlQueryObject.addSelectField("PROFILO_DI_COLLABORAZIONE");
						sqlQueryObject.addFromTable(Costanti.REPOSITORY);
						sqlQueryObject.addWhereCondition("ID_MESSAGGIO=?");
						sqlQueryObject.addWhereCondition("TIPO=?");
						sqlQueryObject.setANDLogicOperator(true);
						queryString = sqlQueryObject.createSQLQuery();
					}
					
					pstmtReadProfilo = connectionDB.prepareStatement(queryString);
					pstmtReadProfilo.setString(1,bustaNonRiscontrata.getIdentificativo());	
					pstmtReadProfilo.setString(2,Costanti.OUTBOX);
					rsReadProfilo = pstmtReadProfilo.executeQuery();	
					
					if(rsReadProfilo.next())
						bustaNonRiscontrata.setProfiloCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.toProfiloDiCollaborazione(rsReadProfilo.getString("PROFILO_DI_COLLABORAZIONE")));
					
					listaBustaNonRiscontrata.add(bustaNonRiscontrata);
					
				} catch(Exception e) {
					String errorMsg = "[ProfiloDiCollaborazione.asincrono_getBusteAsincronePerUlterioreInoltro] errore, queryString["+queryString+"]: "+e.getMessage();
					this.log.error(errorMsg,e);
					throw new  ProtocolException(errorMsg,e);
				}
				finally{
					try{
						if( rsReadProfilo != null )
							rsReadProfilo.close();
					} catch(Exception er) {}
					try{
						if( pstmtReadProfilo != null )
							pstmtReadProfilo.close();
					} catch(Exception er) {}
				}
			}
			
			return listaBustaNonRiscontrata;
		}else if (this.state instanceof StatelessMessage){
			throw new ProtocolException("Metodo non invocabile in modalita stateless");
		}else{
			throw new ProtocolException("Metodo invocato con IState non valido");
		}
	}



	/**
	 * Indicazione se la busta e' una ricevuta asincrona di una richiesta
	 * 
	 * @param riferimentoMessaggio Riferimento Messaggio della ricevuta
	 */
	public boolean asincrono_isRicevutaRichiesta(String riferimentoMessaggio)throws ProtocolException{
		return asincrono_isRicevuta(riferimentoMessaggio,true);
	}

	/**
	 * Indicazione se la busta e' una ricevuta asincrona di una richiestaStato/Risposta
	 * 
	 * @param riferimentoMessaggio Riferimento Messaggio della ricevuta
	 */
	public boolean asincrono_isRicevutaRisposta(String riferimentoMessaggio)throws ProtocolException{
		return asincrono_isRicevuta(riferimentoMessaggio,false);
	}


	/**
	 * Indicazione se la busta e' una ricevuta asincrona
	 * 
	 * @param riferimentoMessaggio Riferimento Messaggio della ricevuta
	 * @param isRicevutaRichiesta Se il check deve controllare che sia una ricevuta di una richiesta
	 */
	private boolean asincrono_isRicevuta(String riferimentoMessaggio,boolean isRicevutaRichiesta)throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{	

			StringBuffer query = new StringBuffer();
			query.append("SELECT ID_ASINCRONO FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=? AND RICEVUTA_ASINCRONA=0");

			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,riferimentoMessaggio);
			pstmt.setString(2,Costanti.OUTBOX);
			rs = pstmt.executeQuery();		
			if(rs == null) {
				throw new ProtocolException("RS Null?");			
			}
			boolean value = false;
			if(rs.next()){
				if(riferimentoMessaggio.equals(rs.getString("ID_ASINCRONO"))){
					value = isRicevutaRichiesta;
				}else{
					value = !isRicevutaRichiesta;
				}
			}

			rs.close();
			pstmt.close();

			return value;

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, isRicevutaAsincrono "+riferimentoMessaggio+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
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
	 * Valida una ricevuta asincrona, identificato dall'identificativo della busta.
	 * In caso di validazione della ricevuta, l'entry nella tabella PROFILO Asincrono 
	 * viene aggiornata con RICEVUTA_ASINCRONA=1 se IS_RICHIESTA=1, altrimenti viene eliminata
	 *
	 * @param ricevuta busta da validare.
	 * @deprecated utilizzare la versione non serializable 
	 * 
	 */
	@Deprecated
	public void asincrono_valdazioneRicevuta_serializable(Busta ricevuta) throws ProtocolException{
		asincrono_validazioneRicevuta_serializable(ricevuta,60l,100);
	}


	/**
	 * Valida una ricevuta asincrona, identificato dall'identificativo della busta.
	 * In caso di validazione della ricevuta, l'entry nella tabella PROFILO Asincrono 
	 * viene aggiornata con RICEVUTA_ASINCRONA=1 se IS_RICHIESTA=1, altrimenti viene eliminata
	 *
	 * @param ricevuta busta da validare.
	 * @param attesaAttiva AttesaAttiva per la gestione del livello di serializable
	 * @param checkInterval Intervallo di check per la gestione  del livello di serializable
	 * @deprecated utilizzare la versione non serializable 
	 */
	@Deprecated
	public void asincrono_validazioneRicevuta_serializable(Busta ricevuta,long attesaAttiva,int checkInterval) throws ProtocolException{
		if(this.state instanceof StatefulMessage) {
			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();
			String idRicevuta = ricevuta.getRiferimentoMessaggio();

			/*
			 * Viene realizzato con livello di isolamento SERIALIZABLE, per essere sicuri
			 *	di leggere solo le indicazioni di richieste asincrone committate,
			 * e comunque rispedira' anche le buste eliminate, ma non committate.
			 */
			// setAutoCommit e livello Isolamento
			int oldTransactionIsolation = -1;
			try{
				oldTransactionIsolation = connectionDB.getTransactionIsolation();
				connectionDB.setAutoCommit(false);
				JDBCUtilities.setTransactionIsolationSerializable(Configurazione.getSqlQueryObjectType(), connectionDB);
			} catch(Exception er) {
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, Errore durante la validazioneRicevutaAsincrona "+idRicevuta+"(setIsolation): "+er.getMessage();		
				this.log.error(errorMsg,er);
				throw new ProtocolException(errorMsg,er);
			}


			boolean validazioneRicevutaOK = false;

			long scadenzaWhile = DateManager.getTimeMillis() + attesaAttiva;

			while(validazioneRicevutaOK==false && DateManager.getTimeMillis() < scadenzaWhile){

				PreparedStatement pstmtValidazione = null;
				PreparedStatement pstmt = null;
				ResultSet rs = null;
				try{

					// Check flusso asincrono: richiesta/ricevuta  o  risposta(richiestaStato)/ricevuta(Risposta)
					StringBuffer query = new StringBuffer();
					query.append("SELECT IS_RICHIESTA,RICEVUTA_ASINCRONA,ID_ASINCRONO FROM ");
					query.append(Costanti.PROFILO_ASINCRONO);
					query.append(" WHERE ID_MESSAGGIO=? AND TIPO=? FOR UPDATE");
					pstmt = connectionDB.prepareStatement(query.toString());
					pstmt.setString(1,idRicevuta);
					pstmt.setString(2,Costanti.OUTBOX);

					// Esecuzione comando SQL
					rs = pstmt.executeQuery();		
					if(rs == null) {
						throw new ProtocolException("RS Null?");	
					}
					boolean isRichiesta = false;
					boolean validaRicevuta = false;
					String idCollaborazione = null;
					if(rs.next()==false){
						rs.close();
						pstmt.close();
						return; // l'entry non e' presente...
					}else{
						if(rs.getInt("IS_RICHIESTA")==1)
							isRichiesta = true;
						if(rs.getInt("RICEVUTA_ASINCRONA")==0)
							validaRicevuta = true;
						idCollaborazione = rs.getString("ID_ASINCRONO");
					}
					rs.close();
					pstmt.close();

					if(validaRicevuta==false){
						String tipoRicevuta = "ricevuta di una richiesta asincrona";
						if(isRichiesta == false)
							tipoRicevuta = "ricevuta di una risposta asincrona";
						this.log.warn("Validazione "+tipoRicevuta+" non effettuata, poiche' inutile: ricevuta precedentemente gia' analizzata.");
					}

					if(isRichiesta){
						// Aggiorno valore ricevuta
						query.delete(0,query.length());
						query.append("UPDATE ");
						query.append(Costanti.PROFILO_ASINCRONO);
						if(ricevuta.getServizioCorrelato()!=null && org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(ricevuta.getProfiloDiCollaborazione())){
							query.append(" SET RICEVUTA_ASINCRONA=1,IS_RICHIESTA=0,TIPO_SERVIZIO_CORRELATO=?,SERVIZIO_CORRELATO=? WHERE  ID_MESSAGGIO = ? AND TIPO=?");
							pstmtValidazione = connectionDB.prepareStatement(query.toString());
							pstmtValidazione.setString(1,ricevuta.getTipoServizioCorrelato());
							pstmtValidazione.setString(2,ricevuta.getServizioCorrelato());
							pstmtValidazione.setString(3,idRicevuta);
							pstmtValidazione.setString(4,Costanti.OUTBOX);
						}else{
							query.append(" SET RICEVUTA_ASINCRONA=1,IS_RICHIESTA=0 WHERE  ID_MESSAGGIO = ? AND TIPO=?");
							pstmtValidazione = connectionDB.prepareStatement(query.toString());
							pstmtValidazione.setString(1,idRicevuta);
							pstmtValidazione.setString(2,Costanti.OUTBOX);
						}
						pstmtValidazione.execute();
						pstmtValidazione.close();
					}else{

						// la busta ricevuta/inviata, come richiesta, necessaria per l'invio della risposta, non serve piu'.
						if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(ricevuta.getProfiloDiCollaborazione())){
							this.eliminaBustaRicevuta(idCollaborazione);
						}else{
							this.eliminaBustaInviata(idCollaborazione);
						}	

						// Elimino entry solo per il profilo asincrono simmetrico
						if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(ricevuta.getProfiloDiCollaborazione())){
							query.delete(0,query.capacity());
							query.append("DELETE FROM ");
							query.append(Costanti.PROFILO_ASINCRONO);
							query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");
							pstmtValidazione = connectionDB.prepareStatement(query.toString());
							pstmtValidazione.setString(1,idRicevuta);
							pstmtValidazione.setString(2,Costanti.OUTBOX);
							pstmtValidazione.execute();
							pstmtValidazione.close();
						}

						// Eliminazione accesso_profilo per l'invio della busta di risposta/richiesta stato
						this.eliminaBustaInviata(idRicevuta);

						stateful.executePreparedStatement();
					}

					// Chiusura Transazione
					connectionDB.commit();

					// ID Costruito
					validazioneRicevutaOK = true;

				} catch(Exception e) {
					try{
						if( rs != null  )
							rs.close();
					} catch(Exception er) {}
					try{
						if( pstmt != null  )
							pstmt.close();
					} catch(Exception er) {}
					try{
						if( pstmtValidazione != null  )
							pstmtValidazione.close();
					} catch(Exception er) {}
					try{
						connectionDB.rollback();
					} catch(Exception er) {}
				}

				if(validazioneRicevutaOK == false){
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
				String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, Errore durante la validazioneRicevutaAsincrona "+idRicevuta+"(ripristinoIsolation): "+er.getMessage();		
				this.log.error(errorMsg,er);
				throw new ProtocolException(errorMsg,er);
			}
		}else{
			throw new ProtocolException("Metodo invocato con IState non valido");
		}
	}	

	/**
	 * Valida una ricevuta asincrona, identificato dall'identificativo della busta.
	 * In caso di validazione della ricevuta, l'entry nella tabella PROFILO Asincrono 
	 * viene aggiornata con RICEVUTA_ASINCRONA=1 se IS_RICHIESTA=1, altrimenti viene eliminata
	 *
	 * @param ricevuta busta da validare. 
	 * 
	 */
	public void asincrono_valdazioneRicevuta(Busta ricevuta) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		String idRicevuta = ricevuta.getRiferimentoMessaggio();

		PreparedStatement pstmtValidazione = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{

			// Check flusso asincrono: richiesta/ricevuta  o  risposta(richiestaStato)/ricevuta(Risposta)
			StringBuffer query = new StringBuffer();
			query.append("SELECT IS_RICHIESTA,RICEVUTA_ASINCRONA,ID_ASINCRONO FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");
			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,idRicevuta);
			pstmt.setString(2,Costanti.OUTBOX);

			// Esecuzione comando SQL
			rs = pstmt.executeQuery();		
			if(rs == null) {
				throw new ProtocolException("RS Null?");	
			}
			boolean isRichiesta = false;
			boolean validaRicevuta = false;
			String idCollaborazione = null;
			if(rs.next()==false){
				rs.close();
				pstmt.close();
				return; // l'entry non e' presente...
			}else{
				if(rs.getInt("IS_RICHIESTA")==1)
					isRichiesta = true;
				if(rs.getInt("RICEVUTA_ASINCRONA")==0)
					validaRicevuta = true;
				idCollaborazione = rs.getString("ID_ASINCRONO");
			}
			rs.close();
			pstmt.close();

			if(validaRicevuta==false){
				String tipoRicevuta = "ricevuta di una richiesta asincrona";
				if(isRichiesta == false)
					tipoRicevuta = "ricevuta di una risposta asincrona";
				this.log.warn("Validazione "+tipoRicevuta+" non effettuata, poiche' inutile: ricevuta precedentemente gia' analizzata.");
			}

			if(isRichiesta){
				// Aggiorno valore ricevuta
				query.delete(0,query.length());
				query.append("UPDATE ");
				query.append(Costanti.PROFILO_ASINCRONO);
				if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(ricevuta.getProfiloDiCollaborazione())){
					String tipoServizioCorrelato = ricevuta.getTipoServizioCorrelato();
					String servizioCorrelato = ricevuta.getServizioCorrelato();
					if(servizioCorrelato==null || tipoServizioCorrelato==null){
						IDSoggetto fruitore = new IDSoggetto(ricevuta.getTipoDestinatario(),ricevuta.getDestinatario());
						IDServizio servizio = new IDServizio(ricevuta.getTipoMittente(),ricevuta.getMittente(),ricevuta.getTipoServizio(),ricevuta.getServizio(),ricevuta.getAzione());
						RisultatoValidazione validazione = RegistroServiziManager.getInstance(state).validaServizio(fruitore,servizio,null);
						if( (validazione==null) || (validazione.getServizioRegistrato()==false))
							throw new Exception("Servizio ["+servizio.toString()+"] non esiste nel registro dei servizi");
						if( (validazione.getServizioCorrelato()==null) || (validazione.getTipoServizioCorrelato()==null) )
							throw new Exception("Servizio ["+servizio.toString()+"] non possiede un servizio correlato associato");
						tipoServizioCorrelato = validazione.getTipoServizioCorrelato();
						servizioCorrelato = validazione.getServizioCorrelato();
					}
					query.append(" SET RICEVUTA_ASINCRONA=1,IS_RICHIESTA=0,TIPO_SERVIZIO_CORRELATO=?,SERVIZIO_CORRELATO=? WHERE  ID_MESSAGGIO = ? AND TIPO=?");
					pstmtValidazione = connectionDB.prepareStatement(query.toString());
					pstmtValidazione.setString(1,tipoServizioCorrelato);
					pstmtValidazione.setString(2,servizioCorrelato);
					pstmtValidazione.setString(3,idRicevuta);
					pstmtValidazione.setString(4,Costanti.OUTBOX);
				}
				else{
					query.append(" SET RICEVUTA_ASINCRONA=1,IS_RICHIESTA=0 WHERE  ID_MESSAGGIO = ? AND TIPO=?");
					pstmtValidazione = connectionDB.prepareStatement(query.toString());
					pstmtValidazione.setString(1,idRicevuta);
					pstmtValidazione.setString(2,Costanti.OUTBOX);
				}

				//	Add PreparedStatement
				state.getPreparedStatement().put("UPDATE validazioneRicevutaRichiestaAsincrona_"+idRicevuta,pstmtValidazione);

			}else{

				// la busta ricevuta/inviata, come richiesta, necessaria per l'invio della risposta, non serve piu'.
				if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(ricevuta.getProfiloDiCollaborazione())){
					this.eliminaBustaRicevuta(idCollaborazione,true);
				}else{
					this.eliminaBustaInviata(idCollaborazione,true);
				}	

				// Elimino entry solo per il profilo asincrono simmetrico
				if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(ricevuta.getProfiloDiCollaborazione())){
					query.delete(0,query.capacity());
					query.append("DELETE FROM ");
					query.append(Costanti.PROFILO_ASINCRONO);
					query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");
					pstmtValidazione =  connectionDB.prepareStatement(query.toString());
					pstmtValidazione.setString(1,idRicevuta);
					pstmtValidazione.setString(2,Costanti.OUTBOX);

					//	Add PreparedStatement
					state.getPreparedStatement().put("UPDATE validazioneRicevutaRispostaAsincrona_"+idRicevuta,pstmtValidazione);
				}

				// Eliminazione accesso_profilo per l'invio della busta di risposta/richiesta stato
				this.eliminaBustaInviata(idRicevuta,true);

			}

		} catch(Exception e) {
			try{
				if( rs != null  )
					rs.close();
			} catch(Exception er) {}
			try{
				if( pstmt != null  )
					pstmt.close();
			} catch(Exception er) {}
			try{
				if( pstmtValidazione != null  )
					pstmtValidazione.close();
			} catch(Exception er) {}
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, Errore durante la validazioneRicevutaAsincrona "+idRicevuta+": "+e.getMessage();		
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}

	}


	/**
	 * Ritorna lo stato di una gestione asincrona, true se e' legittimo gestire una richiesta-stato/Risposta, false altrimenti.
	 *
	 * @param busta busta da controllare
	 * @return true se e' una risposta asincrona.
	 * 
	 */
	public boolean asincrono_isRisposta(Busta busta) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{

			// NOTA:
			//   una busta asincrona RISPOSTA puo avere riferimentoMessaggio o collaborazione

			if(busta.getRiferimentoMessaggio()==null && busta.getCollaborazione()==null)
				return false;

			StringBuffer query = new StringBuffer();
			query.append("SELECT * FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ");
			if(busta.getRiferimentoMessaggio()!=null && busta.getCollaborazione()!=null){
				query.append(" (ID_MESSAGGIO=? OR ID_MESSAGGIO=?) ");
			}else {
				query.append(" ID_MESSAGGIO=? ");
			}
			query.append(" AND TIPO=?");

			String tipo = null;
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione())){
				// asincrono simmetrico
				tipo=Costanti.OUTBOX;
			}else{
				// asincrono asimmetrico
				tipo=Costanti.INBOX;
			}

			pstmt = connectionDB.prepareStatement(query.toString());
			if(busta.getRiferimentoMessaggio()!=null && busta.getCollaborazione()!=null){
				pstmt.setString(1,busta.getRiferimentoMessaggio());
				pstmt.setString(2,busta.getCollaborazione());
				pstmt.setString(3,tipo);
			}else if(busta.getRiferimentoMessaggio()!=null){ 
				pstmt.setString(1,busta.getRiferimentoMessaggio());
				pstmt.setString(2,tipo);
			}else{
				pstmt.setString(1,busta.getCollaborazione());
				pstmt.setString(2,tipo);
			}

			rs = pstmt.executeQuery();	
			if(rs == null) {
				throw new ProtocolException("RS Null?");			
			}
			boolean value = false;
			if(rs.next()){
				if( (rs.getInt("IS_RICHIESTA")==0) &&
						(rs.getInt("RICEVUTA_ASINCRONA")==1) &&
						(rs.getString("TIPO_SERVIZIO_CORRELATO")!=null) &&
						(rs.getString("TIPO_SERVIZIO_CORRELATO").equals(busta.getTipoServizio())) &&
						(rs.getString("SERVIZIO_CORRELATO")!=null) &&
						(rs.getString("SERVIZIO_CORRELATO").equals(busta.getServizio()))
				)
					value = true;
			}

			rs.close();
			pstmt.close();

			return value;

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, asincrono_isRisposta "+busta.getRiferimentoMessaggio()+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
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
	 * Ritorna l'indicazione se la gestione di una ricevuta applicativa e' abilitata o meno
	 *
	 * @param id ID per la gestione del profilo Asincrono
	 * @return true se la ricevuta applicativa e' abilitato.
	 * 
	 */
	public boolean asincrono_ricevutaApplicativaAbilitata(String id) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			StringBuffer query = new StringBuffer();
			query.append("SELECT * FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,Costanti.OUTBOX);
			rs = pstmt.executeQuery();		
			if(rs == null) {
				throw new ProtocolException("RS Null?");			
			}
			if(rs.next()){
				if( rs.getInt("RICEVUTA_APPLICATIVA")==0 ){
					rs.close();
					pstmt.close();
					return false;
				}
				else{
					rs.close();
					pstmt.close();
					return true;
				}
			}

			throw new Exception("?Valore non trovato nella tabella asincrona?");

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, asincrono_ricevutaApplicativaAbilitata "+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
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
	 * Ritorna l'indicazione se e' in corso la gestione di una richiesta asincrona
	 *
	 * @param id ID per la gestione del profilo Asincrono
	 * @return true se la ricevuta applicativa e' abilitato.
	 * 
	 */
	public boolean asincrono_spedizioneRichiestaInCorso(String id) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			StringBuffer query = new StringBuffer();
			query.append("SELECT IS_RICHIESTA,RICEVUTA_ASINCRONA FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,Costanti.OUTBOX);
			rs = pstmt.executeQuery();		
			if(rs == null) {
				throw new ProtocolException("RS Null?");			
			}
			if(rs.next()){
				if( rs.getInt("IS_RICHIESTA")==1 &&
						rs.getInt("RICEVUTA_ASINCRONA")==0	){
					rs.close();
					pstmt.close();
					return true;
				}
				else{
					rs.close();
					pstmt.close();
					return false;
				}
			}

			rs.close();
			pstmt.close();
			return false;

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, asincrono_spedizioneRichiestaInCorso "+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
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
	 * Ritorna l'indicazione se e' in corso la gestione di una risposta asincrona
	 *
	 * @param id ID per la gestione del profilo Asincrono
	 * @return true se la ricevuta applicativa e' abilitato.
	 * 
	 */
	public boolean asincrono_spedizioneRispostaInCorso(String id) throws ProtocolException{

		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			StringBuffer query = new StringBuffer();
			query.append("SELECT IS_RICHIESTA,RICEVUTA_ASINCRONA FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,Costanti.OUTBOX);
			rs = pstmt.executeQuery();		
			if(rs == null) {
				throw new ProtocolException("RS Null?");			
			}
			if(rs.next()){
				if( rs.getInt("IS_RICHIESTA")==0 &&
						rs.getInt("RICEVUTA_ASINCRONA")==0	){
					rs.close();
					pstmt.close();
					return true;
				}
				else{
					rs.close();
					pstmt.close();
					return false;
				}
			}

			rs.close();
			pstmt.close();
			return false;

		}catch(Exception e){	
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, asincrono_spedizioneRispostaInCorso "+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( rs != null )
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
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
	 * Elimina i dati salvati per la gestione di un profilo asicrono.
	 *
	 * @param id Identificativo.
	 * @param tipoBusta INBOX/OUTBOX
	 * 
	 */
	public void asincrono_eliminaRegistrazione(String id,String tipoBusta) throws ProtocolException{
		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmt = null;
		try{	
			StringBuffer query = new StringBuffer();
			query.append("DELETE FROM ");
			query.append(Costanti.PROFILO_ASINCRONO);
			query.append(" WHERE ID_MESSAGGIO = ? AND TIPO=?");
			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,tipoBusta);

			//	Add PreparedStatement
			state.getPreparedStatement().put("DELETE delete_datiAsincroni"+id,pstmt);

		} catch(Exception e) {
			String errorMsg = "PROFILO_DI_COLLABORAZIONE_ASINCRONO, Errore durante l'asincrono_eliminaRegistrazione "+id+": "+e.getMessage();		
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

}





