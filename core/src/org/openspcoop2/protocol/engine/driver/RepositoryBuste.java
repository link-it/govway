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
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.engine.LetturaParametriBusta;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.engine.driver.repository.IGestoreRepository;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StateMessage;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;


/**
 * Sono inclusi i metodi per la gestione delle buste inviate/ricevute.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RepositoryBuste  {

	/** Logger utilizzato per debug. */
	private Logger log = null;

	/** Se IState e' un'istanza di StatefulMessage possiede una Connessione SQL in autoCommit mode su cui effettuare query
	 *  Altrimenti, e' un'istanza di StatelessMessage e nn necessita di connessioni
	 * */
	private IState state;

	/** Identifica se questo repository lavora su buste di richiesta o risposta */
	private boolean isRichiesta=true;
	
	public boolean isRichiesta() {
		return this.isRichiesta;
	}
	public void setRichiesta(boolean isRichiesta) {
		this.isRichiesta = isRichiesta;
	}


	/** GestoreRepository */
	private IGestoreRepository gestoreRepositoryBuste;

	private IProtocolFactory protocolFactory;

	
	/* ********  C O S T R U T T O R E  ******** */

	//public RepositoryBuste(IState state){
	//	this(state, Configurazione.getLibraryLog(), true,null);
	//}
	public RepositoryBuste(IState state, boolean isRichiesta,IProtocolFactory protocolFactory){
		this(state, Configurazione.getLibraryLog(), isRichiesta,protocolFactory);
	}
	public RepositoryBuste(IState state, Logger alog,IProtocolFactory protocolFactory){
		this(state, alog, true,protocolFactory);
	}
	public RepositoryBuste(IState state,IProtocolFactory protocolFactory){
		this(state, Configurazione.getLibraryLog(), true,protocolFactory);
	}
	/**
	 * Costruttore.
	 *
	 * @param state Oggetto che rappresenta lo stato di una busta
	 *
	 */
	public RepositoryBuste(IState state, Logger alog, boolean isRichiesta,IProtocolFactory protocolFactory){
		this.state = state;
		this.gestoreRepositoryBuste = Configurazione.getGestoreRepositoryBuste();
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(RepositoryBuste.class.getName());
		}
		this.isRichiesta = isRichiesta;
		this.protocolFactory = protocolFactory;
	}
	
	

	/**
	 * Imposta lo stato
	 *
	 * @param state Stato
	 *
	 */
	public void updateState(IState state){
		this.state = state;
	}




	/* ********  B U S T E     O U T B O X  ******** */
	/**
	 * Metodo che si occupa di registrare una busta in uscita.
	 *
	 * @param busta Contiene le informazioni su di una busta in uscita (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void registraBustaIntoOutBox(Busta busta,long scadenza) throws ProtocolException{
		registraBusta(busta,Costanti.OUTBOX,null,scadenza);
	}
	/**
	 * Metodo che si occupa di registrare una busta in uscita con errori.
	 *
	 * @param busta Contiene le informazioni su di una busta in uscita (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 * @param errors un oggetto Vector<{@link org.openspcoop2.protocol.sdk.Eccezione}> se contenente errori di validazione.
	 *
	 */
	public void registraBustaIntoOutBox(Busta busta,Vector<Eccezione> errors,long scadenza) throws ProtocolException{
		registraBusta(busta,Costanti.OUTBOX,errors,scadenza);
	}
	/**
	 * Metodo che si occupa di registrare una busta in uscita.
	 *
	 * @param id Identificativo della Richiesta
	 * @param soggettoFruitore Soggetto fruitore
	 * @param servizio Servizio invocato
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void registraBustaIntoOutBox(String id,IDSoggetto soggettoFruitore,IDServizio servizio,long scadenza,
			org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione profiloCollaborazione,boolean confermaRicezione, Inoltro inoltro) throws ProtocolException{
		Busta busta = new Busta(this.protocolFactory.getProtocol());
		busta.setID(id);
		busta.setTipoMittente(soggettoFruitore.getTipo());
		busta.setMittente(soggettoFruitore.getNome());
		busta.setIdentificativoPortaMittente(soggettoFruitore.getCodicePorta());
		busta.setTipoDestinatario(servizio.getSoggettoErogatore().getTipo());
		busta.setDestinatario(servizio.getSoggettoErogatore().getNome());
		busta.setIdentificativoPortaDestinatario(servizio.getSoggettoErogatore().getCodicePorta());
		busta.setTipoServizio(servizio.getTipoServizio());
		busta.setServizio(servizio.getServizio());
		busta.setVersioneServizio(servizio.getVersioneServizio());
		busta.setAzione(servizio.getAzione());
		busta.setProfiloDiCollaborazione(profiloCollaborazione);
		busta.setConfermaRicezione(confermaRicezione);
		busta.setInoltro(inoltro,this.protocolFactory.createTraduttore().toString(inoltro));
		registraBusta(busta,Costanti.OUTBOX,null,scadenza);
	}

	public void aggiornaProprietaBustaIntoOutBox(Hashtable<String, String> properties, String idBusta)throws ProtocolException{
		this.aggiornaProprietaBusta(properties, idBusta, Costanti.OUTBOX);
	}
	
	/**
	 * Aggiorna l'informazione di duplicati della busta.
	 *
	 * @param id identificativo della busta da ritornare.
	 *
	 */
	public void aggiornaDuplicatiIntoOutBox(String id)throws ProtocolException{
		this.aggiornaDuplicati(id, Costanti.OUTBOX);
	}

	/**
	 * Aggiorna le informazioni di integrazione della busta in uscita.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param infoIntegrazione Informazioni di integrazione
	 *
	 */
	public void aggiornaInfoIntegrazioneIntoOutBox(String id,Integrazione infoIntegrazione)throws ProtocolException{
		aggiornaInfoIntegrazione(id,Costanti.OUTBOX,infoIntegrazione);
	}

	/**
	 * Aggiorna il servizio applicativo di integrazione nel repositoryBuste della busta in uscita.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param servizioApplicativo Servizio Applicativo
	 *
	 */
	public void aggiornaInfoIntegrazioneIntoOutBox_ServizioApplicativo(String id,String servizioApplicativo)throws ProtocolException{
		aggiornaInfoIntegrazione_ServizioApplicativo(id,Costanti.OUTBOX,servizioApplicativo);
	}

	/**
	 * Aggiorna lo scenario di integrazione del Msg gestito da OpenSPCoop della busta in uscita.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param scenario Scenario di integrazione
	 *
	 */
	public void aggiornaInfoIntegrazioneIntoOutBox_Scenario(String id,String scenario)throws ProtocolException{
		aggiornaInfoIntegrazione_Scenario(id,Costanti.OUTBOX,scenario);
	}

	/**
	 * Metodo che si occupa di aggiornare i dati di una busta in uscita precedentemente inviata.
	 *
	 * @param busta Contiene le informazioni su di una busta in uscita (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void aggiornaBustaIntoOutBox(Busta busta,long scadenza) throws ProtocolException{
		aggiornaBusta(busta,Costanti.OUTBOX,scadenza,null);
	}
	/**
	 * Metodo che si occupa di aggiornare i dati di una busta in uscita precedentemente inviata.
	 *
	 * @param busta Contiene le informazioni su di una busta in entrata (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param errors un oggetto Vector<{@link org.openspcoop2.protocol.sdk.Eccezione}> se contenente errori di validazione.
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void aggiornaBustaIntoOutBox(Busta busta,long scadenza,Vector<Eccezione> errors) throws ProtocolException{
		aggiornaBusta(busta,Costanti.OUTBOX,scadenza,errors);
	}

	/**
	 * Metodo che si occupa di aggiornare una busta in uscita.
	 *
	 * @param id Identificativo della Richiesta
	 * @param soggettoFruitore Soggetto fruitore
	 * @param servizio Servizio invocato
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void aggiornaBustaIntoOutBox(String id,IDSoggetto soggettoFruitore,IDServizio servizio,long scadenza,
			org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione profiloCollaborazione,boolean confermaRicezione,Inoltro inoltro) throws ProtocolException{
		Busta bustaDaAggiornare = new Busta(this.protocolFactory.getProtocol());
		bustaDaAggiornare.setID(id);
		bustaDaAggiornare.setTipoMittente(soggettoFruitore.getTipo());
		bustaDaAggiornare.setMittente(soggettoFruitore.getNome());
		bustaDaAggiornare.setIdentificativoPortaMittente(soggettoFruitore.getCodicePorta());
		bustaDaAggiornare.setTipoDestinatario(servizio.getSoggettoErogatore().getTipo());
		bustaDaAggiornare.setDestinatario(servizio.getSoggettoErogatore().getNome());
		bustaDaAggiornare.setIdentificativoPortaDestinatario(servizio.getSoggettoErogatore().getCodicePorta());
		bustaDaAggiornare.setTipoServizio(servizio.getTipoServizio());
		bustaDaAggiornare.setServizio(servizio.getServizio());
		bustaDaAggiornare.setVersioneServizio(servizio.getVersioneServizio());
		bustaDaAggiornare.setAzione(servizio.getAzione());
		bustaDaAggiornare.setProfiloDiCollaborazione(profiloCollaborazione);
		bustaDaAggiornare.setConfermaRicezione(confermaRicezione);
		bustaDaAggiornare.setInoltro(inoltro,this.protocolFactory.createTraduttore().toString(inoltro));
		aggiornaBusta(bustaDaAggiornare,Costanti.OUTBOX, scadenza, null);
	}

	/**
	 * Metodo che si occupa di aggiornare la collaborazione di una busta.
	 *
	 * @param id  Identificativo di una busta
	 * @param collaborazione ID di Collaborazione di una busta
	 *
	 */
	public void aggiornaCollaborazioneIntoOutBox(String id,String collaborazione) throws ProtocolException{
		aggiornaCollaborazione(id,Costanti.OUTBOX,collaborazione);
	}

	/**
	 * Metodo che si occupa di aggiornare il numero di sequenza di una busta in uscita.
	 *
	 * @param id  Identificativo di una busta
	 * @param sequenza Numero di sequenza di una busta
	 *
	 */
	public void aggiornaSequenzaIntoOutBox(String id,long sequenza) throws ProtocolException{
		aggiornaSequenza(id,Costanti.OUTBOX,sequenza);
	}

	/**
	 * Ritorna l'informazione se una busta in uscita e' gia stata precedentemente registrata.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @return true se e' presente la busta richiesta.
	 *
	 */
	public boolean isRegistrataIntoOutBox(String id) throws ProtocolException{
		return isRegistrata(id,Costanti.OUTBOX);
	}

	/**
	 * Metodo che si occupa di ritornare un oggetto {@link org.openspcoop2.protocol.sdk.Busta} contenente
	 * le informazioni di una busta in uscita, precedentemente salvata,
	 * prelevandola dalla tabella delle buste. Alla busta, al momento della registrazione,
	 * e' stato associato l'identificativo della busta, quindi attraverso esso sara' possibile trovarlo nella History.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @return un oggetto {@link org.openspcoop2.protocol.sdk.Busta} se e' presente la busta richiesta.
	 *
	 */
	public Busta getBustaFromOutBox(String id) throws ProtocolException{
		return getBusta(id,Costanti.OUTBOX);
	}

	/**
	 * Metodo che si occupa di ritornare un oggetto contenente errori di validazione
	 * {@link org.openspcoop2.protocol.sdk.Eccezione} avvenuti durante la validazione di una busta.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @return un oggetto Vector<{@link org.openspcoop2.protocol.sdk.Eccezione}> se e' presente la busta richiesta.
	 *
	 */
	public Vector<Eccezione> getErrorsFromOutBox(String id, IProtocolFactory protocolFactory) throws ProtocolException{
		return getErrors(id, Costanti.OUTBOX);
	}

	/**
	 * Ritorna alcuni campi associata alla busta.
	 *
	 * @param id identificativo della busta.
	 * @param parametri Indicazione sul tipo di busta inviata/ricevuta
	 * @return alcuni valori della busta
	 *
	 */
	public Busta getSomeValuesFromOutBox(String id,LetturaParametriBusta parametri) throws ProtocolException{
		return getSomeValues(id, Costanti.OUTBOX, parametri, false);
	}
	public Busta getSomeValuesFromOutBox(String id,LetturaParametriBusta parametri,boolean forzaLetturaDatabase) throws ProtocolException{
		return getSomeValues(id, Costanti.OUTBOX, parametri, forzaLetturaDatabase);
	}

	/**
	 * Ritorna la collaborazione associata alla busta.
	 *
	 * @param id identificativo della busta.
	 * @return Collaborazione associata alla busta
	 *
	 */
	public String getCollaborazioneFromOutBox(String id) throws ProtocolException{
		return getCollaborazione(id,Costanti.OUTBOX);
	}

	/**
	 * Ritorna il profilo di collaborazione associata alla busta.
	 *
	 * @param id identificativo della busta.
	 * @return Collaborazione associata alla busta
	 *
	 */
	public org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione getProfiloCollaborazioneFromOutBox(String id) throws ProtocolException{
		return getProfiloCollaborazione(id,Costanti.OUTBOX);
	}
	
	public String getProfiloCollaborazioneValueFromOutBox(String id) throws ProtocolException{
		return getProfiloCollaborazioneValue(id,Costanti.OUTBOX);
	}

	/**
	 * Ritorna la sequenza associata alla busta in uscita.
	 *
	 * @param id identificativo della busta.
	 * @return Sequenza associata alla busta
	 *
	 */
	public long getSequenzaFromOutBox(String id) throws ProtocolException{
		return getSequenza(id,Costanti.OUTBOX);
	}

	/**
	 * Ritorna le informazioni di integrazione associate alla busta in uscita.
	 *
	 * @param id identificativo della busta.
	 * @return Informazioni di integrazione associate alla busta
	 *
	 */
	public Integrazione getInfoIntegrazioneFromOutBox(String id) throws ProtocolException{
		return getInfoIntegrazione(id,Costanti.OUTBOX);
	}

	/**
	 * Metodo che si occupa di eliminare l'indicazione di utilizzo della busta dalla PdD
	 *
	 * @param id identificativo della busta.
	 *
	 */
	public void eliminaUtilizzoPdDFromOutBox(String id) throws ProtocolException{
		this.eliminaUtilizzoPdD(id,Costanti.OUTBOX,false);
	}
	public void eliminaUtilizzoPdDFromOutBox(String id,boolean forzaUpdateDB) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.OUTBOX,forzaUpdateDB);
	}
	public void eliminaUtilizzoPdDFromOutBox(String id,boolean forzaUpdateDB,boolean savePreparedStatement) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.OUTBOX,forzaUpdateDB,savePreparedStatement);
	}
	/**
	 * Metodo che si occupa di eliminare una busta precedentemente salvata.
	 * Alla busta, al momento della registrazione,
	 * e' stato associato l'identificativo della busta, quindi attraverso esso sara' possibile trovarlo nella History.
	 *
	 * @param id identificativo della busta.
	 *
	 */
	public void eliminaBustaFromOutBox(String id) throws ProtocolException{
		eliminaBusta(id,Costanti.OUTBOX);
	}

	public void eliminaBustaStatelessFromOutBox(String id) throws ProtocolException{
		eliminaBustaStateless(id,Costanti.OUTBOX);
	}

	/**
	 * Metodo che si occupa di impostare l'indicazione di utilizzo della busta da parte della PdD
	 *
	 * @param id identificativo della busta.
	 *
	 */
	public void impostaUtilizzoPdDIntoOutBox(String id) throws ProtocolException{
		impostaUtilizzoPdD(id,Costanti.OUTBOX);
	}
	
	






	/* ********  B U S T E     R I C E V U T E  ******** */
	/**
	 * Metodo che si occupa di registrare una busta in entrata.
	 *
	 * @param busta Contiene le informazioni su di una busta in entrata (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void registraBustaIntoInBox(Busta busta,long scadenza) throws ProtocolException{
		registraBusta(busta,Costanti.INBOX,null,scadenza);
	}
	/**
	 * Metodo che si occupa di registrare una busta in uscita con errori.
	 *
	 * @param busta Contiene le informazioni su di una busta in uscita (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param errors un oggetto Vector<{@link org.openspcoop2.protocol.sdk.Eccezione}> se contenente errori di validazione.
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void registraBustaIntoInBox(Busta busta,Vector<Eccezione> errors,long scadenza) throws ProtocolException{
		registraBusta(busta,Costanti.INBOX,errors,scadenza);
	}
	/**
	 * Metodo che si occupa di registrare una busta in entrata.
	 *
	 * @param id Identificativo della Richiesta
	 * @param soggettoFruitore Soggetto fruitore
	 * @param servizio Servizio invocato
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void registraBustaIntoInBox(String id,IDSoggetto soggettoFruitore,IDServizio servizio,long scadenza,
			org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione profiloCollaborazione,boolean confermaRicezione,Inoltro inoltro) throws ProtocolException{
		Busta busta = new Busta(this.protocolFactory.getProtocol());
		busta.setID(id);
		busta.setTipoMittente(soggettoFruitore.getTipo());
		busta.setMittente(soggettoFruitore.getNome());
		busta.setIdentificativoPortaMittente(soggettoFruitore.getCodicePorta());
		busta.setTipoDestinatario(servizio.getSoggettoErogatore().getTipo());
		busta.setDestinatario(servizio.getSoggettoErogatore().getNome());
		busta.setIdentificativoPortaDestinatario(servizio.getSoggettoErogatore().getCodicePorta());
		busta.setTipoServizio(servizio.getTipoServizio());
		busta.setServizio(servizio.getServizio());
		busta.setVersioneServizio(servizio.getVersioneServizio());
		busta.setAzione(servizio.getAzione());
		busta.setProfiloDiCollaborazione(profiloCollaborazione);
		busta.setConfermaRicezione(confermaRicezione);
		busta.setInoltro(inoltro,this.protocolFactory.createTraduttore().toString(inoltro));
		registraBusta(busta,Costanti.INBOX,null,scadenza);
	}

	public void aggiornaProprietaBustaIntoInBox(Hashtable<String, String> properties, String idBusta)throws ProtocolException{
		this.aggiornaProprietaBusta(properties, idBusta, Costanti.INBOX);
	}
	
	/**
	 * Aggiorna l'informazione di duplicati della busta.
	 *
	 * @param id identificativo della busta da ritornare.
	 *
	 */
	public void aggiornaDuplicatiIntoInBox(String id)throws ProtocolException{
		this.aggiornaDuplicati(id, Costanti.INBOX);
	}

	/**
	 * Aggiorna le informazioni di integrazione della busta in entrata.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param infoIntegrazione Informazioni di integrazione
	 *
	 */
	public void aggiornaInfoIntegrazioneIntoInBox(String id,Integrazione infoIntegrazione)throws ProtocolException{
		aggiornaInfoIntegrazione(id,Costanti.INBOX,infoIntegrazione);
	}

	/**
	 * Aggiorna il servizio applicativo di integrazione nel repositoryBuste della busta in entrata.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param servizioApplicativo Servizio Applicativo
	 *
	 */
	public void aggiornaInfoIntegrazioneIntoInBox_ServizioApplicativo(String id,String servizioApplicativo)throws ProtocolException{
		aggiornaInfoIntegrazione_ServizioApplicativo(id,Costanti.INBOX,servizioApplicativo);
	}

	/**
	 * Aggiorna lo scenario di integrazione del Msg gestito da OpenSPCoop della busta in entrata.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param scenario Scenario di integrazione
	 *
	 */
	public void aggiornaInfoIntegrazioneIntoInBox_Scenario(String id,String scenario)throws ProtocolException{
		aggiornaInfoIntegrazione_Scenario(id,Costanti.INBOX,scenario);
	}

	/**
	 * Metodo che si occupa di aggiornare i dati di una busta in entrata precedentemente inviata.
	 *
	 * @param busta Contiene le informazioni su di una busta in entrata (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void aggiornaBustaIntoInBox(Busta busta,long scadenza) throws ProtocolException{
		aggiornaBusta(busta,Costanti.INBOX,scadenza,null);
	}
	/**
	 * Metodo che si occupa di aggiornare i dati di una busta in entrata precedentemente inviata.
	 *
	 * @param busta Contiene le informazioni su di una busta in entrata (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param errors un oggetto Vector<{@link org.openspcoop2.protocol.sdk.Eccezione}> se contenente errori di validazione.
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void aggiornaBustaIntoInBox(Busta busta,long scadenza,Vector<Eccezione> errors) throws ProtocolException{
		aggiornaBusta(busta,Costanti.INBOX,scadenza,errors);
	}

	/**
	 * Metodo che si occupa di aggiornare una busta in entrata.
	 *
	 * @param id Identificativo della Richiesta
	 * @param soggettoFruitore Soggetto fruitore
	 * @param servizio Servizio invocato
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void aggiornaBustaIntoInBox(String id,IDSoggetto soggettoFruitore,IDServizio servizio,long scadenza,
			org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione profiloCollaborazione,boolean confermaRicezione,Inoltro inoltro) throws ProtocolException{
		Busta bustaDaAggiornare = new Busta(this.protocolFactory.getProtocol());
		bustaDaAggiornare.setID(id);
		bustaDaAggiornare.setTipoMittente(soggettoFruitore.getTipo());
		bustaDaAggiornare.setMittente(soggettoFruitore.getNome());
		bustaDaAggiornare.setIdentificativoPortaMittente(soggettoFruitore.getCodicePorta());
		bustaDaAggiornare.setTipoDestinatario(servizio.getSoggettoErogatore().getTipo());
		bustaDaAggiornare.setDestinatario(servizio.getSoggettoErogatore().getNome());
		bustaDaAggiornare.setIdentificativoPortaDestinatario(servizio.getSoggettoErogatore().getCodicePorta());
		bustaDaAggiornare.setTipoServizio(servizio.getTipoServizio());
		bustaDaAggiornare.setServizio(servizio.getServizio());
		bustaDaAggiornare.setVersioneServizio(servizio.getVersioneServizio());
		bustaDaAggiornare.setAzione(servizio.getAzione());
		bustaDaAggiornare.setProfiloDiCollaborazione(profiloCollaborazione);
		bustaDaAggiornare.setConfermaRicezione(confermaRicezione);
		bustaDaAggiornare.setInoltro(inoltro,this.protocolFactory.createTraduttore().toString(inoltro));
		aggiornaBusta(bustaDaAggiornare,Costanti.INBOX, scadenza, null);
	}

	/**
	 * Metodo che si occupa di aggiornare la collaborazione di una busta.
	 *
	 * @param id  Identificativo di una busta
	 * @param collaborazione ID di Collaborazione di una busta
	 *
	 */
	public void aggiornaCollaborazioneIntoInBox(String id,String collaborazione) throws ProtocolException{
		aggiornaCollaborazione(id,Costanti.INBOX,collaborazione);
	}

	/**
	 * Metodo che si occupa di aggiornare il numero di sequenza di una busta in entrata.
	 *
	 * @param id  Identificativo di una busta
	 * @param sequenza Numero di sequenza di una busta
	 *
	 */
	public void aggiornaSequenzaIntoInBox(String id,long sequenza) throws ProtocolException{
		aggiornaSequenza(id,Costanti.INBOX,sequenza);
	}

	/**
	 * Ritorna l'informazione se una busta in entrata e' gia stata precedentemente registrata.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @return true se e' presente la busta richiesta.
	 *
	 */
	public boolean isRegistrataIntoInBox(String id) throws ProtocolException{
		return isRegistrata(id,Costanti.INBOX);
	}

	/**
	 * Metodo che si occupa di ritornare un oggetto {@link org.openspcoop2.protocol.sdk.Busta} contenente
	 * le informazioni di una busta in entrata, precedentemente salvata,
	 * prelevandola dalla tabella delle buste. Alla busta, al momento della registrazione,
	 * e' stato associato l'identificativo della busta, quindi attraverso esso sara' possibile trovarlo nella History.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @return un oggetto {@link org.openspcoop2.protocol.sdk.Busta} se e' presente la busta richiesta.
	 *
	 */
	public Busta getBustaFromInBox(String id) throws ProtocolException{
		return getBusta(id,Costanti.INBOX);
	}

	/**
	 * Metodo che si occupa di ritornare un oggetto contenente errori di validazione
	 * {@link org.openspcoop2.protocol.sdk.Eccezione} avvenuti durante la validazione di una busta.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @return un oggetto Vector<{@link org.openspcoop2.protocol.sdk.Eccezione}> se e' presente la busta richiesta.
	 *
	 */
	public Vector<Eccezione> getErrorsFromInBox(String id) throws ProtocolException{
		return getErrors(id,Costanti.INBOX);
	}

	/**
	 * Ritorna alcuni campi associata alla busta.
	 *
	 * @param id identificativo della busta.
	 * @param parametri Indicazione sul tipo di busta inviata/ricevuta
	 * @return alcuni valori della busta
	 *
	 */
	public Busta getSomeValuesFromInBox(String id,LetturaParametriBusta parametri) throws ProtocolException{
		return getSomeValues(id,Costanti.INBOX,parametri,false);
	}
	public Busta getSomeValuesFromInBox(String id,LetturaParametriBusta parametri,boolean forzaLetturaDatabase) throws ProtocolException{
		return getSomeValues(id,Costanti.INBOX,parametri,forzaLetturaDatabase);
	}
	
	/**
	 * Ritorna la collaborazione associata alla busta.
	 *
	 * @param id identificativo della busta.
	 * @return Collaborazione associata alla busta
	 *
	 */
	public String getCollaborazioneFromInBox(String id) throws ProtocolException{
		return getCollaborazione(id,Costanti.INBOX);
	}

	/**
	 * Ritorna il profilo di collaborazione associata alla busta.
	 *
	 * @param id identificativo della busta.
	 * @return Collaborazione associata alla busta
	 *
	 */
	public org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione getProfiloCollaborazioneFromInBox(String id) throws ProtocolException{
		return getProfiloCollaborazione(id,Costanti.INBOX);
	}
	
	public String getProfiloCollaborazioneValueFromInBox(String id) throws ProtocolException{
		return getProfiloCollaborazioneValue(id,Costanti.INBOX);
	}

	/**
	 * Ritorna la sequenza associata alla busta in entrata.
	 *
	 * @param id identificativo della busta.
	 * @return Sequenza associata alla busta
	 *
	 */
	public long getSequenzaFromInBox(String id) throws ProtocolException{
		return getSequenza(id,Costanti.INBOX);
	}

	/**
	 * Ritorna le informazioni di integrazione associate alla busta in entrata.
	 *
	 * @param id identificativo della busta.
	 * @return Informazioni di integrazione associate alla busta
	 *
	 */
	public Integrazione getInfoIntegrazioneFromInBox(String id) throws ProtocolException{
		return getInfoIntegrazione(id,Costanti.INBOX);
	}

	/**
	 * Metodo che si occupa di eliminare l'indicazione di utilizzo della busta da parte della PdD
	 *
	 * @param id identificativo della busta.
	 *
	 */
	public void eliminaUtilizzoPdDFromInBox(String id) throws ProtocolException{
		this.eliminaUtilizzoPdD(id,Costanti.INBOX,false);
	}
	public void eliminaUtilizzoPdDFromInBox(String id,boolean forzaUpdateDB) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.INBOX,forzaUpdateDB);
	}
	public void eliminaUtilizzoPdDFromInBox(String id,boolean forzaUpdateDB,boolean savePreparedStatement) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.INBOX,forzaUpdateDB,savePreparedStatement);
	}

	/**
	 * Metodo che si occupa di eliminare una busta precedentemente salvata.
	 * Alla busta, al momento della registrazione,
	 * e' stato associato l'identificativo della busta, quindi attraverso esso sara' possibile trovarlo nella History.
	 *
	 * @param id identificativo della busta.
	 *
	 */
	public void eliminaBustaFromInBox(String id) throws ProtocolException{
		eliminaBusta(id,Costanti.INBOX);
	}

	public void eliminaBustaStatelessFromInBox(String id) throws ProtocolException{
		eliminaBustaStateless(id,Costanti.INBOX);
	}

	/**
	 * Metodo che si occupa di impostare l'indicazione di utilizzo della busta da parte della PdD
	 *
	 * @param id identificativo della busta.
	 *
	 */
	public void impostaUtilizzoPdDIntoInBox(String id) throws ProtocolException{
		impostaUtilizzoPdD(id,Costanti.INBOX);
	}















	/* ********  G E S T I O N E   B U S T E  ******** */
	/**
	 * Metodo che si occupa di registrare una busta.
	 *
	 * @param busta Contiene le informazioni su di una busta  (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param scadenza Scadenza Scadenza in minuti associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void registraBustaIntoInboxForHistory(Busta busta,long scadenza) throws ProtocolException{
		
		StateMessage stateMSG = (StateMessage)this.state;
				
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmtBusta = null;
		
		try{

			//	Impostazione scadenza
			Date scadenzaBusta = busta.getScadenza();
			if(scadenzaBusta == null){
				scadenzaBusta = new Date(DateManager.getTimeMillis()+(scadenza*60*1000));
			}
			Timestamp scadenzaT = new Timestamp( scadenzaBusta.getTime()  );


			/* Busta */

			// Registrazione Busta
			StringBuffer query = new StringBuffer();
			query.append("INSERT INTO  ");
			query.append(Costanti.REPOSITORY);
			query.append(" (ID_MESSAGGIO,TIPO,MITTENTE,IDPORTA_MITTENTE,TIPO_MITTENTE,IND_TELEMATICO_MITT,DESTINATARIO,IDPORTA_DESTINATARIO,TIPO_DESTINATARIO,IND_TELEMATICO_DEST");
			query.append(",VERSIONE_SERVIZIO,SERVIZIO,TIPO_SERVIZIO,AZIONE,PROFILO_DI_COLLABORAZIONE,SERVIZIO_CORRELATO,TIPO_SERVIZIO_CORRELATO");
			query.append(",COLLABORAZIONE,SEQUENZA,INOLTRO_SENZA_DUPLICATI,CONFERMA_RICEZIONE,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE");
			query.append(",RIFERIMENTO_MESSAGGIO,SCADENZA_BUSTA,DUPLICATI,LOCATION_PD,SERVIZIO_APPLICATIVO,MODULO_IN_ATTESA,SCENARIO,PROTOCOLLO,");
			query.append(this.gestoreRepositoryBuste.createSQLFieldHistory());
			query.append(") ");
			query.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
			query.append(this.gestoreRepositoryBuste.getSQLValueHistory(true));
			query.append(")");
			
			pstmtBusta = connectionDB.prepareStatement(query.toString());
			pstmtBusta.setString(1,busta.getID());
			pstmtBusta.setString(2,Costanti.INBOX);
			pstmtBusta.setString(3,busta.getMittente());
			pstmtBusta.setString(4,busta.getIdentificativoPortaMittente());
			pstmtBusta.setString(5,busta.getTipoMittente());
			pstmtBusta.setString(6,busta.getIndirizzoMittente());
			pstmtBusta.setString(7,busta.getDestinatario());
			pstmtBusta.setString(8,busta.getIdentificativoPortaDestinatario());
			pstmtBusta.setString(9,busta.getTipoDestinatario());
			pstmtBusta.setString(10,busta.getIndirizzoDestinatario());
			pstmtBusta.setInt(11,busta.getVersioneServizio());
			pstmtBusta.setString(12,busta.getServizio());
			pstmtBusta.setString(13,busta.getTipoServizio());
			pstmtBusta.setString(14,busta.getAzione());
			pstmtBusta.setString(15,busta.getProfiloDiCollaborazione().getEngineValue());
			pstmtBusta.setString(16,busta.getServizioCorrelato());
			pstmtBusta.setString(17,busta.getTipoServizioCorrelato());
			pstmtBusta.setString(18,busta.getCollaborazione());
			pstmtBusta.setLong(19, busta.getSequenza() );
			if(Inoltro.SENZA_DUPLICATI.equals(busta.getInoltro()))
				pstmtBusta.setInt(20,1);
			else
				pstmtBusta.setInt(20,0);
			if(busta.isConfermaRicezione())
				pstmtBusta.setInt(21,1);
			else
				pstmtBusta.setInt(21,0);
			Timestamp oraRec = null;
			TipoOraRegistrazione tipoOraRec = null;
			if(busta.getOraRegistrazione()!=null && busta.getTipoOraRegistrazione()!=null){
				oraRec = new Timestamp( busta.getOraRegistrazione().getTime() );
				tipoOraRec = busta.getTipoOraRegistrazione();
			}else{
				// poi sara' aggiornato.
				oraRec = DateManager.getTimestamp();
				tipoOraRec = TipoOraRegistrazione.SINCRONIZZATO;
			}
			pstmtBusta.setTimestamp(22,oraRec);
			pstmtBusta.setString(23,tipoOraRec.getEngineValue());
			pstmtBusta.setString(24,busta.getRiferimentoMessaggio());
			pstmtBusta.setTimestamp(25,scadenzaT);
			pstmtBusta.setInt(26,0);
			pstmtBusta.setString(27,null); // locationPD
			pstmtBusta.setString(28,null); // servizioApplicativo
			pstmtBusta.setString(29,null); // moduloInAttesa
			pstmtBusta.setString(30,null); // scenario
			if(busta.getProtocollo()!=null)
				pstmtBusta.setString(31,busta.getProtocollo()); // protocollo
			else if(this.protocolFactory!=null){
				pstmtBusta.setString(31,this.protocolFactory.getProtocol()); // protocollo
			}
			else{
				throw new ProtocolException("Protocol unknow");
			}
				

			// Add PreparedStatement
			stateMSG.getPreparedStatement().put("INSERT RegistrazioneBustaForHistoryINBOX_"+busta.getID(),pstmtBusta);

			
			// Update Accesso Per History
			/* INEFFICENTE
			query.delete(0,query.length());
			query.append("UPDATE ");
			query.append(Costanti.REPOSITORY_BUSTE);
			query.append(" SET ");
			query.append(this.gestorerepositoryBuste.createSQLSet_History(true));
			query.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
			pstmtUpdateAccessoHistory = connectionDB.prepareStatement(query.toString());
			pstmtUpdateAccessoHistory.setString(1,busta.getID());
			pstmtUpdateAccessoHistory.setString(2,Costanti.INBOX);
			// Add PreparedStatement
			stateMSG.getPreparedStatement().put("UPDATE RegistrazioneBustaAccessoPdDForHistoryINBOX_"+busta.getID(),pstmtUpdateAccessoHistory);
			*/
			
		} catch(Exception e) {
			String id = busta.getID();
			String errorMsg = "REPOSITORY_BUSTE, Errore di registrazione per History INBOX/"+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if(pstmtBusta != null)
					pstmtBusta.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}
	}
	/**
	 * Metodo che si occupa di registrare una busta.
	 *
	 * @param busta Contiene le informazioni su di una busta  (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @param errors Eventuali errori di validazione
	 * @param scadenza Scadenza Scadenza in minuti associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void registraBusta(Busta busta,String tipoBusta,Vector<Eccezione> errors,long scadenza) throws ProtocolException{
		
		if (!this.isRichiesta && this.state instanceof StatelessMessage) {
			((StatelessMessage)this.state).setBusta(busta);
			return;
		}
		
		
		
		StateMessage stateMSG = (StateMessage)this.state;
				
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmtBusta = null;
		PreparedStatement pstmtUpdateAccessoPdD = null;
		PreparedStatement pstmtListaRiscontri = null;
		PreparedStatement pstmtListaTrasmissioni = null;
		PreparedStatement pstmtListaEccezioni = null;
		PreparedStatement pstmtListaEccezioniValidazione = null;
		PreparedStatement pstmtListaExtInfo = null;

		try{

			//	Impostazione scadenza
			Date scadenzaBusta = busta.getScadenza();
			if(scadenzaBusta == null){
				scadenzaBusta = new Date(DateManager.getTimeMillis()+(scadenza*60*1000));
			}
			Timestamp scadenzaT = new Timestamp( scadenzaBusta.getTime()  );


			/* Busta */

			// Registrazione Busta
			StringBuffer query = new StringBuffer();
			query.append("INSERT INTO  ");
			query.append(Costanti.REPOSITORY);
			query.append(" (ID_MESSAGGIO,TIPO,MITTENTE,IDPORTA_MITTENTE,TIPO_MITTENTE,IND_TELEMATICO_MITT,DESTINATARIO,IDPORTA_DESTINATARIO,TIPO_DESTINATARIO,IND_TELEMATICO_DEST");
			query.append(",VERSIONE_SERVIZIO,SERVIZIO,TIPO_SERVIZIO,AZIONE,PROFILO_DI_COLLABORAZIONE,SERVIZIO_CORRELATO,TIPO_SERVIZIO_CORRELATO");
			query.append(",COLLABORAZIONE,SEQUENZA,INOLTRO_SENZA_DUPLICATI,CONFERMA_RICEZIONE,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE");
			query.append(",RIFERIMENTO_MESSAGGIO,SCADENZA_BUSTA,DUPLICATI,LOCATION_PD,SERVIZIO_APPLICATIVO,MODULO_IN_ATTESA,SCENARIO,PROTOCOLLO) ");
			query.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmtBusta = connectionDB.prepareStatement(query.toString());
			pstmtBusta.setString(1,busta.getID());
			pstmtBusta.setString(2,tipoBusta);
			pstmtBusta.setString(3,busta.getMittente());
			pstmtBusta.setString(4,busta.getIdentificativoPortaMittente());
			pstmtBusta.setString(5,busta.getTipoMittente());
			pstmtBusta.setString(6,busta.getIndirizzoMittente());
			pstmtBusta.setString(7,busta.getDestinatario());
			pstmtBusta.setString(8,busta.getIdentificativoPortaDestinatario());
			pstmtBusta.setString(9,busta.getTipoDestinatario());
			pstmtBusta.setString(10,busta.getIndirizzoDestinatario());
			pstmtBusta.setInt(11,busta.getVersioneServizio());
			pstmtBusta.setString(12,busta.getServizio());
			pstmtBusta.setString(13,busta.getTipoServizio());
			pstmtBusta.setString(14,busta.getAzione());
			pstmtBusta.setString(15,busta.getProfiloDiCollaborazione() == null ? null : busta.getProfiloDiCollaborazione().getEngineValue());
			pstmtBusta.setString(16,busta.getServizioCorrelato());
			pstmtBusta.setString(17,busta.getTipoServizioCorrelato());
			pstmtBusta.setString(18,busta.getCollaborazione());
			pstmtBusta.setLong(19, busta.getSequenza() );
			if(Inoltro.SENZA_DUPLICATI.equals(busta.getInoltro()))
				pstmtBusta.setInt(20,1);
			else
				pstmtBusta.setInt(20,0);
			if(busta.isConfermaRicezione())
				pstmtBusta.setInt(21,1);
			else
				pstmtBusta.setInt(21,0);
			Timestamp oraRec = null;
			TipoOraRegistrazione tipoOraRec = null;
			if(busta.getOraRegistrazione()!=null && busta.getTipoOraRegistrazione()!=null){
				oraRec = new Timestamp( busta.getOraRegistrazione().getTime() );
				tipoOraRec = busta.getTipoOraRegistrazione();
			}else{
				// poi sara' aggiornato.
				oraRec = DateManager.getTimestamp();
				tipoOraRec = TipoOraRegistrazione.SINCRONIZZATO;
			}
			pstmtBusta.setTimestamp(22,oraRec);
			pstmtBusta.setString(23,tipoOraRec.getEngineValue());
			pstmtBusta.setString(24,busta.getRiferimentoMessaggio());
			pstmtBusta.setTimestamp(25,scadenzaT);
			pstmtBusta.setInt(26,0);
			pstmtBusta.setString(27,null); // locationPD
			pstmtBusta.setString(28,null); // servizioApplicativo
			pstmtBusta.setString(29,null); // moduloInAttesa
			pstmtBusta.setString(30,null); // scenario
			if(busta.getProtocollo()!=null)
				pstmtBusta.setString(31,busta.getProtocollo()); // protocollo
			else if(this.protocolFactory!=null){
				pstmtBusta.setString(31,this.protocolFactory.getProtocol()); // protocollo
			}
			else{
				throw new ProtocolException("Protocol unknow");
			}

			// Add PreparedStatement
			stateMSG.getPreparedStatement().put("INSERT RegistrazioneBusta"+tipoBusta+"_"+busta.getID(),pstmtBusta);

			
			// Update Accesso da Pdd
			query.delete(0,query.length());
			query.append("UPDATE ");
			query.append(Costanti.REPOSITORY);
			query.append(" SET ");
			query.append(this.gestoreRepositoryBuste.createSQLSet_PdD(true));
			query.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
			pstmtUpdateAccessoPdD = connectionDB.prepareStatement(query.toString());
			pstmtUpdateAccessoPdD.setString(1,busta.getID());
			pstmtUpdateAccessoPdD.setString(2,tipoBusta);
			// Add PreparedStatement
			//stateMSG.getPreparedStatement().put("UPDATE RegistrazioneBustaAccessoPdD"+tipoBusta+"_"+busta.getID(),pstmtUpdateAccessoPdD);
			stateMSG.getPreparedStatement().put("INSERT RegistrazioneBusta"+tipoBusta+"_"+busta.getID()+" UPDATE RegistrazioneBustaAccessoPdD",pstmtUpdateAccessoPdD);
			
			
			if (this.state instanceof StatefulMessage ) {


				/* Lista Riscontri */
				for(int i=0; i<busta.sizeListaRiscontri(); i++){
					Riscontro riscontro = busta.getRiscontro(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_RISCONTRI);
					query.append(" (ID_MESSAGGIO,TIPO,ID_RISCONTRO,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE)");
					query.append(" VALUES (?,?,?,?,?)");
					pstmtListaRiscontri = connectionDB.prepareStatement(query.toString());
					pstmtListaRiscontri.setString(1,busta.getID());
					pstmtListaRiscontri.setString(2,tipoBusta);
					pstmtListaRiscontri.setString(3,riscontro.getID());
					Timestamp oraRecRiscontro = null;
					if(riscontro.getOraRegistrazione()!=null ){
						oraRecRiscontro = new Timestamp( riscontro.getOraRegistrazione().getTime() );
					}
					pstmtListaRiscontri.setTimestamp(4,oraRecRiscontro);
					pstmtListaRiscontri.setString(5,riscontro.getTipoOraRegistrazione().getEngineValue());

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT RegistrazioneListaRiscontri_riscontro["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaRiscontri);
				}

				/* Lista Trasmissioni */
				for(int i=0; i<busta.sizeListaTrasmissioni(); i++){

					Trasmissione trasmissione = busta.getTrasmissione(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_TRASMISSIONI);
					query.append(" (ID_MESSAGGIO,TIPO,ORIGINE,TIPO_ORIGINE,DESTINAZIONE,TIPO_DESTINAZIONE,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?,?)");
					pstmtListaTrasmissioni = connectionDB.prepareStatement(query.toString());
					pstmtListaTrasmissioni.setString(1,busta.getID());
					pstmtListaTrasmissioni.setString(2,tipoBusta);
					pstmtListaTrasmissioni.setString(3,trasmissione.getOrigine());
					pstmtListaTrasmissioni.setString(4,trasmissione.getTipoOrigine());
					pstmtListaTrasmissioni.setString(5,trasmissione.getDestinazione());
					pstmtListaTrasmissioni.setString(6,trasmissione.getTipoDestinazione());
					Timestamp oraRecTrasmissione = null;
					if( trasmissione.getOraRegistrazione()!=null ){
						oraRecTrasmissione = new Timestamp( trasmissione.getOraRegistrazione().getTime()  );
					}
					pstmtListaTrasmissioni.setTimestamp(7,oraRecTrasmissione);
					pstmtListaTrasmissioni.setString(8,trasmissione.getTempo().getEngineValue());

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT RegistrazioneListaTrasmissioni_trasmissione["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaTrasmissioni);
				}

				/* Lista Eccezioni */
				for(int i=0; i<busta.sizeListaEccezioni(); i++){
					Eccezione eccezione = busta.getEccezione(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_ECCEZIONI);
					query.append(" (ID_MESSAGGIO,TIPO,VALIDAZIONE,CONTESTO,CODICE,RILEVANZA,POSIZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?)");
					pstmtListaEccezioni = connectionDB.prepareStatement(query.toString());
					pstmtListaEccezioni.setString(1,busta.getID());
					pstmtListaEccezioni.setString(2,tipoBusta);
					pstmtListaEccezioni.setInt(3,0); // eccezione proprietaria delle busta
					pstmtListaEccezioni.setString(4,eccezione.getContestoCodifica().getEngineValue());
					pstmtListaEccezioni.setInt(5,eccezione.getCodiceEccezione().getCodice());
					pstmtListaEccezioni.setString(6,eccezione.getRilevanza().getEngineValue());
					pstmtListaEccezioni.setString(7,eccezione.getDescrizione(this.protocolFactory));

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT RegistrazioneListaEccezioni_eccezioneBusta["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaEccezioni);
				}

				/* Lista Eccezioni Validazione */
				if(errors!=null){
					for(int i=0; i<errors.size(); i++){
						Eccezione eccezione = errors.get(i);
						query.delete(0,query.length());
						query.append("INSERT INTO  ");
						query.append(Costanti.LISTA_ECCEZIONI);
						query.append(" (ID_MESSAGGIO,TIPO,VALIDAZIONE,CONTESTO,CODICE,RILEVANZA,POSIZIONE)");
						query.append(" VALUES (?,?,?,?,?,?,?)");
						pstmtListaEccezioniValidazione = connectionDB.prepareStatement(query.toString());
						pstmtListaEccezioniValidazione.setString(1,busta.getID());
						pstmtListaEccezioniValidazione.setString(2,tipoBusta);
						pstmtListaEccezioniValidazione.setInt(3,1); // eccezione arrivata dopo la validazione
						pstmtListaEccezioniValidazione.setString(4,eccezione.getContestoCodifica().getEngineValue());
						pstmtListaEccezioniValidazione.setInt(5,eccezione.getCodiceEccezione().getCodice());
						pstmtListaEccezioniValidazione.setString(6,eccezione.getRilevanza().getEngineValue());
						pstmtListaEccezioniValidazione.setString(7,eccezione.getDescrizione(this.protocolFactory));

						// Add PreparedStatement
						stateMSG.getPreparedStatement().put("INSERT RegistrazioneListaEccezioni_eccezioneValidazione["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaEccezioniValidazione);
					}
				}

				/* Lista ExtProtocolInfo */
				if(busta.sizeProperties()>0){
					String[] names = busta.getPropertiesNames();
					if(names!=null && names.length>0){
						for (int i = 0; i < names.length; i++) {
							String name = names[i];
							String value = busta.getProperty(name);
							
							query.delete(0,query.length());
							query.append("INSERT INTO  ");
							query.append(Costanti.LISTA_EXT_INFO);
							query.append(" (ID_MESSAGGIO,TIPO,NOME,VALORE)");
							query.append(" VALUES (?,?,?,?)");
							pstmtListaExtInfo = connectionDB.prepareStatement(query.toString());
							pstmtListaExtInfo.setString(1,busta.getID());
							pstmtListaExtInfo.setString(2,tipoBusta);
							pstmtListaExtInfo.setString(3,name);
							pstmtListaExtInfo.setString(4,value);

							// Add PreparedStatement
							stateMSG.getPreparedStatement().put("INSERT RegistrazioneListaExtInfoProtocol_info["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaExtInfo);
						}
					}
				}
				
			}
		} catch(Exception e) {
			String id = busta.getID();
			String errorMsg = "REPOSITORY_BUSTE, Errore di registrazione "+tipoBusta+"/"+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if(pstmtBusta != null)
					pstmtBusta.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtUpdateAccessoPdD!= null)
					pstmtUpdateAccessoPdD.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtListaRiscontri != null)
					pstmtListaRiscontri.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtListaTrasmissioni != null)
					pstmtListaTrasmissioni.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtListaEccezioni != null)
					pstmtListaEccezioni.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtListaEccezioniValidazione != null)
					pstmtListaEccezioniValidazione.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtListaExtInfo != null)
					pstmtListaExtInfo.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

		// tengo la busta anche nello state message per recuperarla senza passare dal db        
		if (stateMSG instanceof StatelessMessage) ((StatelessMessage)stateMSG).setBusta(busta);
	}

	
	public void aggiornaProprietaBusta(Hashtable<String, String> properties, String idBusta,String tipoBusta) throws ProtocolException{
				
		StateMessage stateMSG = (StateMessage)this.state;
				
		Connection connectionDB = stateMSG.getConnectionDB();

		if(connectionDB==null){
			return ;
		}
		
		PreparedStatement pstmtDeleteListaExtInfo = null;
		PreparedStatement pstmtListaExtInfo = null;

		try{

			//	elimino lista ext-info
			StringBuffer queryDelete = new StringBuffer();
			queryDelete.delete(0,queryDelete.length());
			queryDelete.append("DELETE FROM ");
			queryDelete.append(Costanti.LISTA_EXT_INFO);
			queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
			pstmtDeleteListaExtInfo = connectionDB.prepareStatement(queryDelete.toString());
			pstmtDeleteListaExtInfo.setString(1,idBusta);
			pstmtDeleteListaExtInfo.setString(2,tipoBusta);

			//	Add PreparedStatement
			stateMSG.getPreparedStatement().put("DELETE(UPDATE) AggiornamentoBusta_listaExtInfo"+tipoBusta+"_"+idBusta,pstmtDeleteListaExtInfo);
			
			/* Lista ExtProtocolInfo */
			if(properties.size()>0){
				Enumeration<String> keys = properties.keys();
				int index = 0;
				while (keys.hasMoreElements()) {
					String name = (String) keys.nextElement();
					String value = properties.get(name);
						
					StringBuffer query = new StringBuffer();
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_EXT_INFO);
					query.append(" (ID_MESSAGGIO,TIPO,NOME,VALORE)");
					query.append(" VALUES (?,?,?,?)");
					pstmtListaExtInfo = connectionDB.prepareStatement(query.toString());
					pstmtListaExtInfo.setString(1,idBusta);
					pstmtListaExtInfo.setString(2,tipoBusta);
					pstmtListaExtInfo.setString(3,name);
					pstmtListaExtInfo.setString(4,value);

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT(UPDATE) RegistrazioneListaExtInfoProtocol_info["+index+"]_"+tipoBusta+"_"+idBusta,pstmtListaExtInfo);
					
					index++;
				}
			}
				
		} catch(Exception e) {
			String errorMsg = "REPOSITORY_BUSTE, Errore di aggiornamento proprieta' "+tipoBusta+"/"+idBusta+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if(pstmtDeleteListaExtInfo != null)
					pstmtDeleteListaExtInfo.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtListaExtInfo != null)
					pstmtListaExtInfo.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

	}
	
	/**
	 * Metodo che si occupa di aggiornare i dati di una busta (non aggiorna le liste).
	 *
	 * @param busta Contiene le informazioni su di una busta  (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 * @param errors Eventuali errori di validazione
	 *
	 */
	public void aggiornaBusta(Busta busta,String tipoBusta,long scadenza,Vector<Eccezione> errors) throws ProtocolException{
		
		if (!this.isRichiesta && this.state instanceof StatelessMessage) {
			((StatelessMessage)this.state).setBusta(busta);
			return;
		}
		
		StateMessage stateMSG = (StateMessage)this.state;
		
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmtBusta = null;
		PreparedStatement pstmtDeleteListaRiscontri = null;
		PreparedStatement pstmtDeleteListaTrasmissioni = null;
		PreparedStatement pstmtDeleteListaEccezioni = null; // vale sia per validazione che per non...
		PreparedStatement pstmtDeleteListaExtInfo = null;
		PreparedStatement pstmtListaRiscontri = null;
		PreparedStatement pstmtListaTrasmissioni = null;
		PreparedStatement pstmtListaEccezioni = null;
		PreparedStatement pstmtListaEccezioniValidazione = null;
		PreparedStatement pstmtListaExtInfo = null;
		try{

			// Impostazione scadenza
			Date scadenzaBusta = busta.getScadenza();
			if(scadenzaBusta == null){
				scadenzaBusta = new Date(DateManager.getTimeMillis()+(scadenza*60*1000));
			}
			Timestamp scadenzaT = new Timestamp( scadenzaBusta.getTime()  );

			/* Busta */

			// Registrazione Busta
			StringBuffer query = new StringBuffer();
			query.append("UPDATE  ");
			query.append(Costanti.REPOSITORY);
			query.append(" SET ");
			query.append("MITTENTE = ? ,");
			query.append("IDPORTA_MITTENTE = ? ,");
			query.append("TIPO_MITTENTE = ? ,");
			query.append("IND_TELEMATICO_MITT = ? ,");
			query.append("DESTINATARIO = ? ,");
			query.append("IDPORTA_DESTINATARIO = ? ,");
			query.append("TIPO_DESTINATARIO = ? ,");
			query.append("IND_TELEMATICO_DEST = ? ,");
			query.append("VERSIONE_SERVIZIO = ? ,");
			query.append("SERVIZIO = ? ,");
			query.append("TIPO_SERVIZIO = ? ,");
			query.append("AZIONE = ? ,");
			query.append("PROFILO_DI_COLLABORAZIONE = ? ,");
			query.append("SERVIZIO_CORRELATO = ? ,");
			query.append("TIPO_SERVIZIO_CORRELATO = ? ,");
			query.append("COLLABORAZIONE = ? ,");
			query.append("SEQUENZA = ? ,");
			query.append("INOLTRO_SENZA_DUPLICATI = ? ,");
			query.append("CONFERMA_RICEZIONE = ? ,");
			query.append("ORA_REGISTRAZIONE = ? ,");
			query.append("TIPO_ORA_REGISTRAZIONE = ? ,");
			query.append("RIFERIMENTO_MESSAGGIO = ? ,");
			query.append("SCADENZA_BUSTA = ? ");
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");
			pstmtBusta = connectionDB.prepareStatement(query.toString());
			pstmtBusta.setString(1,busta.getMittente());
			pstmtBusta.setString(2,busta.getIdentificativoPortaMittente());
			pstmtBusta.setString(3,busta.getTipoMittente());
			pstmtBusta.setString(4,busta.getIndirizzoMittente());
			pstmtBusta.setString(5,busta.getDestinatario());
			pstmtBusta.setString(6,busta.getIdentificativoPortaDestinatario());
			pstmtBusta.setString(7,busta.getTipoDestinatario());
			pstmtBusta.setString(8,busta.getIndirizzoDestinatario());
			pstmtBusta.setInt(9,busta.getVersioneServizio());
			pstmtBusta.setString(10,busta.getServizio());
			pstmtBusta.setString(11,busta.getTipoServizio());
			pstmtBusta.setString(12,busta.getAzione());
			pstmtBusta.setString(13,busta.getProfiloDiCollaborazione() == null ? null : busta.getProfiloDiCollaborazione().getEngineValue());
			pstmtBusta.setString(14,busta.getServizioCorrelato());
			pstmtBusta.setString(15,busta.getTipoServizioCorrelato());
			pstmtBusta.setString(16,busta.getCollaborazione());
			pstmtBusta.setLong(17, busta.getSequenza());
			if(Inoltro.SENZA_DUPLICATI.equals(busta.getInoltro()))
				pstmtBusta.setInt(18,1);
			else
				pstmtBusta.setInt(18,0);
			if(busta.isConfermaRicezione())
				pstmtBusta.setInt(19,1);
			else
				pstmtBusta.setInt(19,0);
			Timestamp oraRec = null;
			TipoOraRegistrazione tipoOraRec = null;
			if(busta.getOraRegistrazione()!=null && busta.getTipoOraRegistrazione()!=null){
				oraRec = new Timestamp( busta.getOraRegistrazione().getTime() );
				tipoOraRec = busta.getTipoOraRegistrazione();
			}
			pstmtBusta.setTimestamp(20,oraRec);
			pstmtBusta.setString(21,tipoOraRec.getEngineValue());
			pstmtBusta.setString(22,busta.getRiferimentoMessaggio());
			pstmtBusta.setTimestamp(23,scadenzaT);
			pstmtBusta.setString(24,busta.getID());
			pstmtBusta.setString(25,tipoBusta);

			// Add PreparedStatement
			stateMSG.getPreparedStatement().put("UPDATE AggiornamentoBusta"+tipoBusta+"_"+busta.getID(),pstmtBusta);

			if(errors!=null){

				// cancella liste
				//	elimino lista trasmiossioni
				StringBuffer queryDelete = new StringBuffer();
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_TRASMISSIONI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDeleteListaRiscontri = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDeleteListaRiscontri.setString(1,busta.getID());
				pstmtDeleteListaRiscontri.setString(2,tipoBusta);

				//	Add PreparedStatement
				stateMSG.getPreparedStatement().put("DELETE(UPDATE) AggiornamentoBusta_listaRiscontri"+tipoBusta+"_"+busta.getID(),pstmtDeleteListaRiscontri);

				//	elimino lista riscontri
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_RISCONTRI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDeleteListaTrasmissioni = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDeleteListaTrasmissioni.setString(1,busta.getID());
				pstmtDeleteListaTrasmissioni.setString(2,tipoBusta);

				//	Add PreparedStatement
				stateMSG.getPreparedStatement().put("DELETE(UPDATE) AggiornamentoBusta_listaTrasmissioni"+tipoBusta+"_"+busta.getID(),pstmtDeleteListaTrasmissioni);

				//	elimino lista eccezioni
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_ECCEZIONI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDeleteListaEccezioni = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDeleteListaEccezioni.setString(1,busta.getID());
				pstmtDeleteListaEccezioni.setString(2,tipoBusta);

				//	Add PreparedStatement
				stateMSG.getPreparedStatement().put("DELETE(UPDATE) AggiornamentoBusta_listaEccezioni"+tipoBusta+"_"+busta.getID(),pstmtDeleteListaEccezioni);
				
				//	elimino lista ext-info
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_EXT_INFO);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDeleteListaExtInfo = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDeleteListaExtInfo.setString(1,busta.getID());
				pstmtDeleteListaExtInfo.setString(2,tipoBusta);

				//	Add PreparedStatement
				stateMSG.getPreparedStatement().put("DELETE(UPDATE) AggiornamentoBusta_listaExtInfo"+tipoBusta+"_"+busta.getID(),pstmtDeleteListaExtInfo);
				

				// Inserisco nuove liste

				/* Lista Riscontri */
				for(int i=0; i<busta.sizeListaRiscontri(); i++){
					Riscontro riscontro = busta.getRiscontro(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_RISCONTRI);
					query.append(" (ID_MESSAGGIO,TIPO,ID_RISCONTRO,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE)");
					query.append(" VALUES (?,?,?,?,?)");
					pstmtListaRiscontri = connectionDB.prepareStatement(query.toString());
					pstmtListaRiscontri.setString(1,busta.getID());
					pstmtListaRiscontri.setString(2,tipoBusta);
					pstmtListaRiscontri.setString(3,riscontro.getID());
					Timestamp oraRecRiscontro = null;
					if(riscontro.getOraRegistrazione()!=null ){
						oraRecRiscontro = new Timestamp( riscontro.getOraRegistrazione().getTime() );
					}
					pstmtListaRiscontri.setTimestamp(4,oraRecRiscontro);
					pstmtListaRiscontri.setString(5,riscontro.getTipoOraRegistrazione().getEngineValue());

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT(UPDATE) RegistrazioneListaRiscontri_riscontro["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaRiscontri);
				}

				/* Lista Trasmissioni */
				for(int i=0; i<busta.sizeListaTrasmissioni(); i++){
					Trasmissione trasmissione = busta.getTrasmissione(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_TRASMISSIONI);
					query.append(" (ID_MESSAGGIO,TIPO,ORIGINE,TIPO_ORIGINE,DESTINAZIONE,TIPO_DESTINAZIONE,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?,?)");
					pstmtListaTrasmissioni = connectionDB.prepareStatement(query.toString());
					pstmtListaTrasmissioni.setString(1,busta.getID());
					pstmtListaTrasmissioni.setString(2,tipoBusta);
					pstmtListaTrasmissioni.setString(3,trasmissione.getOrigine());
					pstmtListaTrasmissioni.setString(4,trasmissione.getTipoOrigine());
					pstmtListaTrasmissioni.setString(5,trasmissione.getDestinazione());
					pstmtListaTrasmissioni.setString(6,trasmissione.getTipoDestinazione());
					Timestamp oraRecTrasmissione = null;
					if( trasmissione.getOraRegistrazione()!=null ){
						oraRecTrasmissione = new Timestamp( trasmissione.getOraRegistrazione().getTime()  );
					}
					pstmtListaTrasmissioni.setTimestamp(7,oraRecTrasmissione);
					pstmtListaTrasmissioni.setString(8,trasmissione.getTempo().getEngineValue());

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT(UPDATE) RegistrazioneListaTrasmissioni_trasmissione["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaTrasmissioni);
				}

				/* Lista Eccezioni */
				for(int i=0; i<busta.sizeListaEccezioni(); i++){
					Eccezione eccezione = busta.getEccezione(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_ECCEZIONI);
					query.append(" (ID_MESSAGGIO,TIPO,VALIDAZIONE,CONTESTO,CODICE,RILEVANZA,POSIZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?)");
					pstmtListaEccezioni = connectionDB.prepareStatement(query.toString());
					pstmtListaEccezioni.setString(1,busta.getID());
					pstmtListaEccezioni.setString(2,tipoBusta);
					pstmtListaEccezioni.setInt(3,0); // eccezione proprietaria delle busta
					pstmtListaEccezioni.setString(4,eccezione.getContestoCodifica().getEngineValue());
					pstmtListaEccezioni.setInt(5,eccezione.getCodiceEccezione().getCodice());
					pstmtListaEccezioni.setString(6,eccezione.getRilevanza().getEngineValue());
					pstmtListaEccezioni.setString(7,eccezione.getDescrizione(this.protocolFactory));

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT(UPDATE) RegistrazioneListaEccezioni_eccezioneBusta["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaEccezioni);
				}

				/* Lista Eccezioni Validazione */
				for(int i=0; i<errors.size(); i++){
					Eccezione eccezione = errors.get(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_ECCEZIONI);
					query.append(" (ID_MESSAGGIO,TIPO,VALIDAZIONE,CONTESTO,CODICE,RILEVANZA,POSIZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?)");
					pstmtListaEccezioniValidazione = connectionDB.prepareStatement(query.toString());
					pstmtListaEccezioniValidazione.setString(1,busta.getID());
					pstmtListaEccezioniValidazione.setString(2,tipoBusta);
					pstmtListaEccezioniValidazione.setInt(3,1); // eccezione arrivata dopo la validazione
					pstmtListaEccezioniValidazione.setString(4,eccezione.getContestoCodifica().getEngineValue());
					pstmtListaEccezioniValidazione.setInt(5,eccezione.getCodiceEccezione().getCodice());
					pstmtListaEccezioniValidazione.setString(6,eccezione.getRilevanza().getEngineValue());
					pstmtListaEccezioniValidazione.setString(7,eccezione.getDescrizione(this.protocolFactory));

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT(UPDATE) RegistrazioneListaEccezioni_eccezioneValidazione["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaEccezioniValidazione);
				}
				
				/* Lista ExtProtocolInfo */
				if(busta.sizeProperties()>0){
					String[] names = busta.getPropertiesNames();
					if(names!=null && names.length>0){
						for (int i = 0; i < names.length; i++) {
							String name = names[i];
							String value = busta.getProperty(name);
							
							query.delete(0,query.length());
							query.append("INSERT INTO  ");
							query.append(Costanti.LISTA_EXT_INFO);
							query.append(" (ID_MESSAGGIO,TIPO,NOME,VALORE)");
							query.append(" VALUES (?,?,?,?)");
							pstmtListaExtInfo = connectionDB.prepareStatement(query.toString());
							pstmtListaExtInfo.setString(1,busta.getID());
							pstmtListaExtInfo.setString(2,tipoBusta);
							pstmtListaExtInfo.setString(3,name);
							pstmtListaExtInfo.setString(4,value);

							// Add PreparedStatement
							stateMSG.getPreparedStatement().put("INSERT(UPDATE) RegistrazioneListaExtInfoProtocol_info["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaExtInfo);
						}
					}
				}
			}


		} catch(Exception e) {
			String id = busta.getID();
			String errorMsg = "REPOSITORY_BUSTE, Errore di aggiornamento "+tipoBusta+"/"+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if(pstmtBusta != null)
					pstmtBusta.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtDeleteListaRiscontri!= null)
					pstmtDeleteListaRiscontri.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtDeleteListaTrasmissioni!= null)
					pstmtDeleteListaTrasmissioni.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtDeleteListaEccezioni!= null)
					pstmtDeleteListaEccezioni.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtDeleteListaExtInfo!= null)
					pstmtDeleteListaExtInfo.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtListaRiscontri != null)
					pstmtListaRiscontri.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtListaTrasmissioni != null)
					pstmtListaTrasmissioni.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtListaEccezioni != null)
					pstmtListaEccezioni.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtListaEccezioniValidazione != null)
					pstmtListaEccezioniValidazione.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmtListaExtInfo != null)
					pstmtListaExtInfo.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

		if (stateMSG instanceof StatelessMessage)   ((StatelessMessage)this.state).setBusta(busta);

	}

	/**
	 * Aggiorna l'informazione di duplicati della busta.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 *
	 */
	private void aggiornaDuplicati(String id,String tipoBusta)throws ProtocolException{

		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmt = null;
		try{

			// Costruzione Query
			StringBuffer query = new StringBuffer();
			query.append("UPDATE ");
			query.append(Costanti.REPOSITORY);
			query.append(" SET DUPLICATI=(DUPLICATI+1) WHERE  ID_MESSAGGIO = ? AND TIPO=?");
			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,tipoBusta);

			// Add PreparedStatement
			stateMSG.getPreparedStatement().put("UPDATE aggiornaDuplicati "+tipoBusta+"_"+id,pstmt);

		} catch(Exception e) {
			String errorMsg = "REPOSITORY_BUSTE, Errore di aggiornamento informazioni pacchetti duplicati "+tipoBusta+"/"+id+": "+e.getMessage();
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {}
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}
	}

	/**  Aggiorna le informazioni di integrazione della busta.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @param infoIntegrazione Informazioni di integrazione
	 *
	 */
	public void aggiornaInfoIntegrazione(String id,String tipoBusta,Integrazione infoIntegrazione)throws ProtocolException{

		boolean registrazioneInfoIntegrazione = false;
		
		if( this.state instanceof StatefulMessage) {
			registrazioneInfoIntegrazione = true;
		}else if(this.isRichiesta && Costanti.OUTBOX.equals(tipoBusta)){
			StatelessMessage stateless = (StatelessMessage)this.state;
			Busta busta = stateless.getBusta();
			if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(busta.getProfiloDiCollaborazione()) &&
					busta.isConfermaRicezione()){
				// Le informazioni di integrazione servono per i riscontri
				registrazioneInfoIntegrazione = true;
			}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione()) &&
					busta.getRiferimentoMessaggio()==null){
				// Le informazioni di integrazione servono per consegnare la risposta
				registrazioneInfoIntegrazione = true;
			}
		}
		
		if( registrazioneInfoIntegrazione ) {

			StateMessage stateMSG = (StateMessage)this.state;
			Connection connectionDB = stateMSG.getConnectionDB();

			PreparedStatement pstmt = null;
			try{

				// Costruzione Query
				StringBuffer query = new StringBuffer();
				query.append("UPDATE ");
				query.append(Costanti.REPOSITORY);
				query.append(" SET LOCATION_PD=? , SERVIZIO_APPLICATIVO=? , MODULO_IN_ATTESA=? , SCENARIO=? WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,infoIntegrazione.getLocationPD());
				pstmt.setString(2,infoIntegrazione.getServizioApplicativo());
				pstmt.setString(3,infoIntegrazione.getIdModuloInAttesa());
				pstmt.setString(4,infoIntegrazione.getScenario());
				pstmt.setString(5,id);
				pstmt.setString(6,tipoBusta);

				// Add PreparedStatement
				stateMSG.getPreparedStatement().put("UPDATE aggiornaInformazioniIntegrazione "+tipoBusta+"_"+id,pstmt);

			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore di aggiornamento informazioni di Integrazione "+tipoBusta+"/"+id+": "+e.getMessage();
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}
		}else{
			//NOP
		}
	}

	/**
	 * Aggiorna il servizio applicativo di integrazione nel repositoryBuste.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @param servizioApplicativo Servizio Applicativo
	 *
	 */
	private void aggiornaInfoIntegrazione_ServizioApplicativo(String id,String tipoBusta,String servizioApplicativo)throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			try{

				// Costruzione Query
				StringBuffer query = new StringBuffer();
				query.append("UPDATE ");
				query.append(Costanti.REPOSITORY);
				query.append(" SET SERVIZIO_APPLICATIVO=? WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,servizioApplicativo);
				pstmt.setString(2,id);
				pstmt.setString(3,tipoBusta);

				// Add PreparedStatement
				stateful.getPreparedStatement().put("UPDATE aggiornaInformazioniIntegrazione_ServizioApplicativo_"+tipoBusta+"_"+id,pstmt);

			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore di aggiornamento informazioni di Integrazione "+tipoBusta+"/"+id+" (ServizioApplicativo): "+e.getMessage();
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}
		}else{

			// NOP
		}

	}

	/**
	 * Aggiorna lo scenario di integrazione del Msg gestito da OpenSPCoop.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @param scenario Scenario di integrazione
	 *
	 */
	private void aggiornaInfoIntegrazione_Scenario(String id,String tipoBusta,String scenario)throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			try{

				// Costruzione Query
				StringBuffer query = new StringBuffer();
				query.append("UPDATE ");
				query.append(Costanti.REPOSITORY);
				query.append(" SET SCENARIO=? WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,scenario);
				pstmt.setString(2,id);
				pstmt.setString(3,tipoBusta);

				// Add PreparedStatement
				stateful.getPreparedStatement().put("UPDATE aggiornaInformazioniIntegrazione_Scenario_"+tipoBusta+"_"+id,pstmt);

			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore di aggiornamento informazioni di Integrazione "+tipoBusta+"/"+id+" (Scenario): "+e.getMessage();
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}
		}else{
			// NOP
		}
	}

	/**
	 * Metodo che si occupa di aggiornare la collaborazione di una busta.
	 *
	 * @param id  Identificativo di una busta
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @param collaborazione ID di Collaborazione di una busta
	 *
	 */
	private void aggiornaCollaborazione(String id,String tipoBusta,String collaborazione) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmtBusta = null;
			try{
				// Registrazione Sequenza
				StringBuffer query = new StringBuffer();
				query.append("UPDATE  ");
				query.append(Costanti.REPOSITORY);
				query.append(" SET ");
				query.append("COLLABORAZIONE = ?");
				query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");
				pstmtBusta = connectionDB.prepareStatement(query.toString());
				pstmtBusta.setString(1, collaborazione);
				pstmtBusta.setString(2,id);
				pstmtBusta.setString(3,tipoBusta);

				// Add PreparedStatement
				stateful.getPreparedStatement().put("UPDATE AggiornamentoCollaborazione"+tipoBusta+"_"+id,pstmtBusta);

			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore di aggiornamento collaborazione "+tipoBusta+"/"+id+": "+e.getMessage();
				this.log.error(errorMsg,e);
				try{
					if(pstmtBusta != null)
						pstmtBusta.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				throw new ProtocolException(errorMsg,e);
			}
		}else{

			StatelessMessage statelessMsg = ((StatelessMessage)this.state);
			statelessMsg.getBusta().setCollaborazione(collaborazione);
		}

	}

	/**
	 * Metodo che si occupa di aggiornare il numero di sequenza di una busta.
	 *
	 * @param id  Identificativo di una busta
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @param sequenza Numero di sequenza di una busta
	 *
	 */
	private void aggiornaSequenza(String id,String tipoBusta,long sequenza) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmtBusta = null;
			try{
				// Registrazione Sequenza
				StringBuffer query = new StringBuffer();
				query.append("UPDATE  ");
				query.append(Costanti.REPOSITORY);
				query.append(" SET ");
				query.append("SEQUENZA = ?");
				query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");
				pstmtBusta = connectionDB.prepareStatement(query.toString());
				pstmtBusta.setLong(1, sequenza);
				pstmtBusta.setString(2,id);
				pstmtBusta.setString(3,tipoBusta);

				// Add PreparedStatement
				stateful.getPreparedStatement().put("UPDATE AggiornamentoSequenza"+tipoBusta+"_"+id,pstmtBusta);

			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore di aggiornamento sequenza "+tipoBusta+"/"+id+": "+e.getMessage();
				this.log.error(errorMsg,e);
				try{
					if(pstmtBusta != null)
						pstmtBusta.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				throw new ProtocolException(errorMsg,e);
			}

		}else{

			StatelessMessage statelessMsg = ((StatelessMessage)this.state);
			statelessMsg.getBusta().setSequenza(sequenza);
		}

	}

	/**
	 * Ritorna l'informazione se una busta e' gia stata precedentemente registrata.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @return true se e' presente la busta richiesta.
	 *
	 */
	public boolean isRegistrata(String id, String tipoBusta) throws ProtocolException {

		Connection connectionDB = ((StateMessage) this.state).getConnectionDB();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {	// busta

			StringBuffer query = new StringBuffer();
			query.append("select ID_MESSAGGIO from ");
			query.append(Costanti.REPOSITORY);
			query.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
			pstmt = connectionDB.prepareStatement(query.toString());
			pstmt.setString(1, id);
			pstmt.setString(2, tipoBusta);

			// Esecuzione comando SQL
			rs = pstmt.executeQuery();
			if (rs == null) {
				throw new ProtocolException("RS Null?");
			}
			boolean value = rs.next();

			rs.close();
			pstmt.close();

			return value;


		} catch (Exception e) {
			String errorMsg = "REPOSITORY_BUSTE, Errore durante isRegistrata " + tipoBusta + "/" + id + ": " + e.getMessage();
			this.log.error(errorMsg, e);
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception er) {
				// Eccezione SQL.
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg, e);
		}


	}





	/**
	 * Metodo che si occupa di ritornare un oggetto {@link org.openspcoop2.protocol.sdk.Busta} contenente
	 * le informazioni di una busta, precedentemente salvata,
	 * prelevandola dalla tabella delle buste. Alla busta, al momento della registrazione,
	 * e' stato associato l'identificativo della busta, quindi attraverso esso sara' possibile trovarlo nella History.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @return un oggetto {@link org.openspcoop2.protocol.sdk.Busta} se e' presente la busta richiesta.
	 *
	 */
	private Busta getBusta(String id, String tipoBusta) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{

				// busta

				StringBuffer query = new StringBuffer();
				query.append("select * from ");
				query.append(Costanti.REPOSITORY);
				query.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");

				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,id);
				pstmt.setString(2,tipoBusta);

				// Esecuzione comando SQL
				rs = pstmt.executeQuery();
				if(rs == null) {
					throw new ProtocolException("RS Null?");
				}
				if (rs.next() == false) {
					throw new ProtocolException("Busta non trovata");
				}

				// Lettura parametri busta
				Busta busta = new Busta(rs.getString("PROTOCOLLO"));
				if(this.protocolFactory==null){
					if(busta.getProtocollo()!=null){
						this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(busta.getProtocollo());
					}
					else{
						throw new ProtocolException("Protocol unknown");
					}
				}
				busta.setID(rs.getString("ID_MESSAGGIO"));
				busta.setTipoMittente(rs.getString("TIPO_MITTENTE"));
				busta.setMittente(rs.getString("MITTENTE"));
				busta.setIdentificativoPortaMittente(rs.getString("IDPORTA_MITTENTE"));
				busta.setIndirizzoMittente(rs.getString("IND_TELEMATICO_MITT"));
				//log.info("TipoMitt["+busta.getTipoMittente()+"] Mitt["+ busta.getMittente()+"]");
				busta.setTipoDestinatario(rs.getString("TIPO_DESTINATARIO"));
				busta.setDestinatario(rs.getString("DESTINATARIO"));
				busta.setIdentificativoPortaDestinatario(rs.getString("IDPORTA_DESTINATARIO"));
				busta.setIndirizzoDestinatario(rs.getString("IND_TELEMATICO_DEST"));
				//log.info("TipoDest["+busta.getTipoDestinatario()+"] DEst["+ busta.getDestinatario()+"]");
				busta.setVersioneServizio(rs.getInt("VERSIONE_SERVIZIO"));
				busta.setServizio(rs.getString("SERVIZIO"));
				busta.setTipoServizio(rs.getString("TIPO_SERVIZIO"));
				busta.setAzione(rs.getString("AZIONE"));
				//log.info("Servizio["+busta.getServizio()+"] Azione["+ busta.getAzione()+"]");
				String profiloCollaborazione = rs.getString("PROFILO_DI_COLLABORAZIONE");
				if(profiloCollaborazione!=null){
					busta.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.toProfiloDiCollaborazione(profiloCollaborazione));
				}
				busta.setServizioCorrelato(rs.getString("SERVIZIO_CORRELATO"));
				busta.setTipoServizioCorrelato(rs.getString("TIPO_SERVIZIO_CORRELATO"));
				busta.setCollaborazione(rs.getString("COLLABORAZIONE"));
				busta.setSequenza(rs.getLong("SEQUENZA"));
				//log.info("COLL["+ busta.getCollaborazione()+"] SEQ["+busta.getSequenza()+"]");

				if(rs.getInt("INOLTRO_SENZA_DUPLICATI") != 0)
					busta.setInoltro(Inoltro.SENZA_DUPLICATI,this.protocolFactory.createTraduttore().toString(Inoltro.SENZA_DUPLICATI));
				else
					busta.setInoltro(Inoltro.CON_DUPLICATI,this.protocolFactory.createTraduttore().toString(Inoltro.CON_DUPLICATI));
				//log.info("INOLTRO["+busta.getInoltro()+"]");
				if(rs.getInt("CONFERMA_RICEZIONE") != 0)
					busta.setConfermaRicezione(true);
				else
					busta.setConfermaRicezione(false);
				//log.info("ConfermaRicezione["+busta.getConfermaRicezione()+"]");
				busta.setOraRegistrazione(rs.getTimestamp("ORA_REGISTRAZIONE"));
				TipoOraRegistrazione tipoOraRegistrazione = TipoOraRegistrazione.toTipoOraRegistrazione(rs.getString("TIPO_ORA_REGISTRAZIONE")); 
				busta.setTipoOraRegistrazione(tipoOraRegistrazione,this.protocolFactory.createTraduttore().toString(tipoOraRegistrazione));
				busta.setRiferimentoMessaggio(rs.getString("RIFERIMENTO_MESSAGGIO"));
				busta.setScadenza(rs.getTimestamp("SCADENZA_BUSTA"));

				rs.close();
				pstmt.close();


				// Lista Tramsmissioni
				query.delete(0,query.length());
				query.append("select * from ");
				query.append(Costanti.LISTA_TRASMISSIONI);
				query.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");

				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,id);
				pstmt.setString(2,tipoBusta);

				// Esecuzione comando SQL
				rs = pstmt.executeQuery();
				if(rs == null) {
					throw new ProtocolException("RS Null?");
				}


				// Lettura parametri busta
				while(rs.next()){
					Trasmissione trasmissione = new Trasmissione();
					trasmissione.setOrigine(rs.getString("ORIGINE"));
					trasmissione.setTipoOrigine(rs.getString("TIPO_ORIGINE"));
					trasmissione.setDestinazione(rs.getString("DESTINAZIONE"));
					trasmissione.setTipoDestinazione(rs.getString("TIPO_DESTINAZIONE"));
					trasmissione.setOraRegistrazione(rs.getTimestamp("ORA_REGISTRAZIONE"));
					trasmissione.setTempo(TipoOraRegistrazione.toTipoOraRegistrazione(rs.getString("TIPO_ORA_REGISTRAZIONE")));
					busta.addTrasmissione(trasmissione);
				}

				rs.close();
				pstmt.close();


				//	Lista Riscontri
				query.delete(0,query.length());
				query.append("select * from ");
				query.append(Costanti.LISTA_RISCONTRI);
				query.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");

				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,id);
				pstmt.setString(2,tipoBusta);

				// Esecuzione comando SQL
				rs = pstmt.executeQuery();
				if(rs == null) {
					throw new ProtocolException("RS Null?");
				}


				// Lettura parametri busta
				while(rs.next()){
					Riscontro riscontro = new Riscontro();
					riscontro.setID(rs.getString("ID_RISCONTRO"));
					riscontro.setOraRegistrazione(rs.getTimestamp("ORA_REGISTRAZIONE"));
					riscontro.setTipoOraRegistrazione(TipoOraRegistrazione.toTipoOraRegistrazione(rs.getString("TIPO_ORA_REGISTRAZIONE")));
					busta.addRiscontro(riscontro);
				}

				rs.close();
				pstmt.close();


				// Lista Eccezioni
				query.delete(0,query.length());
				query.append("select * from ");
				query.append(Costanti.LISTA_ECCEZIONI);
				query.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ? AND VALIDAZIONE=0");

				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,id);
				pstmt.setString(2,tipoBusta);

				// Esecuzione comando SQL
				rs = pstmt.executeQuery();
				if(rs == null) {
					throw new ProtocolException("RS Null?");
				}


				// Lettura parametri busta
				while(rs.next()){
					Eccezione eccezione = Eccezione.newEccezione();
					eccezione.setContestoCodifica(ContestoCodificaEccezione.toContestoCodificaEccezione(rs.getString("CONTESTO")));
					eccezione.setCodiceEccezione(CodiceErroreCooperazione.toCodiceErroreCooperazione((rs.getInt("CODICE"))));
					eccezione.setRilevanza(LivelloRilevanza.toLivelloRilevanza(rs.getString("RILEVANZA")));
					eccezione.setDescrizione(rs.getString("POSIZIONE"));
					busta.addEccezione(eccezione);
				}

				rs.close();
				pstmt.close();

				
				
				// Lista ExtInfo
				query.delete(0,query.length());
				query.append("select * from ");
				query.append(Costanti.LISTA_EXT_INFO);
				query.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");

				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,id);
				pstmt.setString(2,tipoBusta);

				// Esecuzione comando SQL
				rs = pstmt.executeQuery();
				if(rs == null) {
					throw new ProtocolException("RS Null?");
				}


				// Lettura parametri busta
				while(rs.next()){
					busta.addProperty(rs.getString("NOME"), rs.getString("VALORE"));
				}

				rs.close();
				pstmt.close();
				
				
				

				return busta;


			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore durante la getBusta "+tipoBusta+"/"+id+": "+e.getMessage();
				this.log.error(errorMsg,e);
				try{
					if(rs!=null)
						rs.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				throw new ProtocolException(errorMsg,e);
			}
		}else{
			StatelessMessage statelessMsg = ((StatelessMessage)this.state);
			return statelessMsg.getBusta();
		}

	}

	/**
	 * Metodo che si occupa di ritornare un oggetto contenente errori di validazione
	 * {@link org.openspcoop2.protocol.sdk.Eccezione} avvenuti durante la validazione di una busta.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @return un oggetto Vector<{@link org.openspcoop2.protocol.sdk.Eccezione}> se e' presente la busta richiesta.
	 *
	 */
	private Vector<Eccezione> getErrors(String id,String tipoBusta) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			Vector<Eccezione> errors = new Vector<Eccezione>();
			try{

				// Costruzione Query
				StringBuffer query = new StringBuffer();
				query.append("select * from ");
				query.append(Costanti.LISTA_ECCEZIONI);
				query.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ? AND VALIDAZIONE=1");

				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,id);
				pstmt.setString(2,tipoBusta);

				// Esecuzione comando SQL
				rs = pstmt.executeQuery();
				if(rs == null) {
					throw new ProtocolException("RS Null?");
				}


				// Lettura parametri busta
				while(rs.next()){
					Eccezione eccezione = Eccezione.newEccezione();
					eccezione.setContestoCodifica(ContestoCodificaEccezione.toContestoCodificaEccezione(rs.getString("CONTESTO")));
					eccezione.setCodiceEccezione(CodiceErroreCooperazione.toCodiceErroreCooperazione(rs.getInt("CODICE")));
					eccezione.setRilevanza(LivelloRilevanza.toLivelloRilevanza(rs.getString("RILEVANZA")));
					eccezione.setDescrizione(rs.getString("POSIZIONE"));
					errors.add(eccezione);
				}

				rs.close();
				pstmt.close();

				return errors;


			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore durante la getErrors "+tipoBusta+"/"+id+": "+e.getMessage();
				this.log.error(errorMsg,e);
				try{
					if(rs!=null)
						rs.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				throw new ProtocolException(errorMsg,e);
			}
		}else{
			// NOP
			return null;
		}

	}

	/**
	 * Ritorna alcuni campi associata alla busta.
	 *
	 * @param id identificativo della busta.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @return alcuni valori della busta
	 *
	 */
	private Busta getSomeValues(String id,String tipoBusta,	LetturaParametriBusta parametri,boolean forzaLetturaSuDatabase) throws ProtocolException{

		if((this.state instanceof StatefulMessage) || (forzaLetturaSuDatabase)) {

			StateMessage state = (StateMessage)this.state;
			Connection connectionDB = state.getConnectionDB();

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String protocollo = null;
			if(this.protocolFactory!=null){
				protocollo = this.protocolFactory.getProtocol();
			}
					
			Busta busta = new Busta(protocollo);
			try{
				// Costruzione Query
				StringBuffer bf = new StringBuffer();
				bf.append("SELECT ");
				bf.append("PROTOCOLLO");
				boolean first = false;
				if(parametri.isMittente()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("MITTENTE,TIPO_MITTENTE,IDPORTA_MITTENTE");
					first = false;
				}
				if(parametri.isDestinatario()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("DESTINATARIO,TIPO_DESTINATARIO,IDPORTA_DESTINATARIO");
				}
				if(parametri.isIndirizziTelematici()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("IND_TELEMATICO_MITT,IND_TELEMATICO_DEST");
				}
				if(parametri.isServizio()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("SERVIZIO,TIPO_SERVIZIO,VERSIONE_SERVIZIO");
				}
				if(parametri.isAzione()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("AZIONE");
				}
				if(parametri.isProfiloDiCollaborazione()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("PROFILO_DI_COLLABORAZIONE");
				}
				if(parametri.isServizioCorrelato()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("SERVIZIO_CORRELATO,TIPO_SERVIZIO_CORRELATO");
				}
				if(parametri.isCollaborazione()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("COLLABORAZIONE");
				}
				if(parametri.isSequenza()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("SEQUENZA");
				}
				if(parametri.isProfiloTrasmissione()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("INOLTRO_SENZA_DUPLICATI,CONFERMA_RICEZIONE");
				}
				if(parametri.isOraRegistrazione()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE");
				}
				if(parametri.isRiferimentoMessaggio()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("RIFERIMENTO_MESSAGGIO");
				}
				if(parametri.isScadenza()){
					if(first==false)
						bf.append(",");
					else
						first = false;
					bf.append("SCADENZA_BUSTA");
				}
				bf.append(" FROM ");
				bf.append(Costanti.REPOSITORY);
				bf.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");

				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(bf.toString());
				pstmt.setString(1,id);
				pstmt.setString(2,tipoBusta);
				rs = pstmt.executeQuery();
				if(rs == null) {
					throw new ProtocolException("RS Null?");
				}
				if(rs.next()){
					String protocolloRead = rs.getString("PROTOCOLLO");
					if(busta.getProtocollo()==null){
						busta.setProtocollo(protocolloRead);
					}
					if(this.protocolFactory==null){
						this.protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocolloRead);
					}
					if(parametri.isMittente()){
						busta.setTipoMittente(rs.getString("TIPO_MITTENTE"));
						busta.setMittente(rs.getString("MITTENTE"));
						busta.setIdentificativoPortaMittente(rs.getString("IDPORTA_MITTENTE"));
					}
					if(parametri.isDestinatario()){
						busta.setTipoDestinatario(rs.getString("TIPO_DESTINATARIO"));
						busta.setDestinatario(rs.getString("DESTINATARIO"));
						busta.setIdentificativoPortaDestinatario(rs.getString("IDPORTA_DESTINATARIO"));
					}
					if(parametri.isIndirizziTelematici()){
						busta.setIndirizzoMittente(rs.getString("IND_TELEMATICO_MITT"));
						busta.setIndirizzoDestinatario(rs.getString("IND_TELEMATICO_DEST"));
					}
					if(parametri.isServizio()){
						busta.setTipoServizio(rs.getString("TIPO_SERVIZIO"));
						busta.setServizio(rs.getString("SERVIZIO"));
						busta.setVersioneServizio(rs.getInt("VERSIONE_SERVIZIO"));
					}
					if(parametri.isAzione()){
						busta.setAzione(rs.getString("AZIONE"));
					}
					if(parametri.isProfiloDiCollaborazione()){
						busta.setProfiloDiCollaborazione(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.toProfiloDiCollaborazione(rs.getString("PROFILO_DI_COLLABORAZIONE")));
					}
					if(parametri.isServizioCorrelato()){
						busta.setTipoServizioCorrelato(rs.getString("TIPO_SERVIZIO_CORRELATO"));
						busta.setServizioCorrelato(rs.getString("SERVIZIO_CORRELATO"));
					}
					if(parametri.isCollaborazione()){
						busta.setCollaborazione(rs.getString("COLLABORAZIONE"));
					}
					if(parametri.isSequenza()){
						busta.setSequenza(rs.getLong("SEQUENZA"));
					}
					if(parametri.isProfiloTrasmissione()){
						if(rs.getInt("INOLTRO_SENZA_DUPLICATI") != 0)
							busta.setInoltro(Inoltro.SENZA_DUPLICATI,this.protocolFactory.createTraduttore().toString(Inoltro.SENZA_DUPLICATI));
						else
							busta.setInoltro(Inoltro.CON_DUPLICATI,this.protocolFactory.createTraduttore().toString(Inoltro.CON_DUPLICATI));
						//log.info("INOLTRO["+busta.getInoltro()+"]");
						if(rs.getInt("CONFERMA_RICEZIONE") != 0)
							busta.setConfermaRicezione(true);
						else
							busta.setConfermaRicezione(false);
					}
					if(parametri.isOraRegistrazione()){
						busta.setOraRegistrazione(rs.getTimestamp("ORA_REGISTRAZIONE"));
						TipoOraRegistrazione tipoOraRegistrazione = TipoOraRegistrazione.toTipoOraRegistrazione(rs.getString("TIPO_ORA_REGISTRAZIONE")); 
						busta.setTipoOraRegistrazione(tipoOraRegistrazione,this.protocolFactory.createTraduttore().toString(tipoOraRegistrazione));
					}
					if(parametri.isRiferimentoMessaggio()){
						busta.setRiferimentoMessaggio(rs.getString("RIFERIMENTO_MESSAGGIO"));
					}
					if(parametri.isScadenza()){
						busta.setScadenza(rs.getTimestamp("SCADENZA_BUSTA"));
					}
				}else{
					throw new ProtocolException("Busta non trovata");
				}
				rs.close();
				pstmt.close();

				return busta;

			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore  getSomeValues "+tipoBusta+"/"+id+": "+e.getMessage();
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}
		}else{
			StatelessMessage statelessMsg = ((StatelessMessage)this.state);
			Busta busta = statelessMsg.getBusta();
			if(parametri.isMittente()==false){
				busta.setTipoMittente(null);
				busta.setMittente(null);
			}
			if(parametri.isDestinatario()==false){
				busta.setTipoDestinatario(null);
				busta.setDestinatario(null);
			}
			if(parametri.isIndirizziTelematici()==false){
				busta.setIndirizzoMittente(null);
				busta.setIndirizzoDestinatario(null);
			}
			if(parametri.isServizio()==false){
				busta.setTipoServizio(null);
				busta.setServizio(null);
			}
			if(parametri.isAzione()==false){
				busta.setAzione(null);
			}
			if(parametri.isProfiloDiCollaborazione()==false){
				busta.setProfiloDiCollaborazione(null);
			}
			if(parametri.isServizioCorrelato()==false){
				busta.setTipoServizioCorrelato(null);
				busta.setServizioCorrelato(null);
			}
			if(parametri.isCollaborazione()==false){
				busta.setCollaborazione(null);
			}
			if(parametri.isSequenza()==false){
				busta.setSequenza(-1);
			}
			if(parametri.isProfiloTrasmissione()==false){
				busta.setInoltro(null,null);
				busta.setConfermaRicezione(false);
			}
			if(parametri.isOraRegistrazione()==false){
				busta.setOraRegistrazione(null);
				busta.setTipoOraRegistrazione(null,null);
			}
			if(parametri.isRiferimentoMessaggio()==false){
				busta.setRiferimentoMessaggio(null);
			}
			if(parametri.isScadenza()==false){
				busta.setScadenza(null);
			}
			for(int i=0;i<busta.sizeListaEccezioni();i++){
				busta.removeEccezione(i);
			}
			for(int i=0;i<busta.sizeListaTrasmissioni();i++){
				busta.removeTrasmissione(i);
			}
			for(int i=0;i<busta.sizeListaRiscontri();i++){
				busta.removeRiscontro(i);
			}
			if(busta.sizeProperties()>0){
				String [] pNames = busta.getPropertiesNames();
				for(int i=0; i<pNames.length; i++){
					busta.removeProperty(pNames[i]);
				}
			}
			
			
			return busta;
		}
	}

	/**
	 * Ritorna la collaborazione associata alla busta.
	 *
	 * @param id identificativo della busta.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @return Collaborazione associata alla busta
	 *
	 */
	private String getCollaborazione(String id,String tipoBusta) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{
				// Costruzione Query
				String query = "SELECT COLLABORAZIONE FROM "+Costanti.REPOSITORY+" WHERE ID_MESSAGGIO=? AND TIPO=?";
				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(query);
				pstmt.setString(1,id);
				pstmt.setString(2,tipoBusta);
				rs = pstmt.executeQuery();
				if(rs == null) {
					throw new ProtocolException("RS Null?");
				}
				String collaborazione = null;
				if(rs.next()){
					collaborazione = rs.getString("COLLABORAZIONE");
				}
				rs.close();
				pstmt.close();

				return collaborazione;

			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore  getCollaborazione "+tipoBusta+"/"+id+": "+e.getMessage();
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}
		}else{
			StatelessMessage statelessMsg = ((StatelessMessage)this.state);
			Busta busta = statelessMsg.getBusta();
			return busta.getCollaborazione();
		}
	}

	/**
	 * Ritorna la collaborazione associata alla busta.
	 *
	 * @param id identificativo della busta.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @return Collaborazione associata alla busta
	 *
	 */
	private org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione getProfiloCollaborazione(String id,String tipoBusta) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{
				// Costruzione Query
				String query = "SELECT PROFILO_DI_COLLABORAZIONE FROM "+Costanti.REPOSITORY+" WHERE ID_MESSAGGIO=? AND TIPO=?";
				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(query);
				pstmt.setString(1,id);
				pstmt.setString(2,tipoBusta);
				rs = pstmt.executeQuery();
				if(rs == null) {
					throw new ProtocolException("RS Null?");
				}
				org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione profiloCollaborazione = null;
				if(rs.next()){
					profiloCollaborazione = org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.toProfiloDiCollaborazione(rs.getString("PROFILO_DI_COLLABORAZIONE"));
				}
				rs.close();
				pstmt.close();

				return profiloCollaborazione;

			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore  getProfiloCollaborazione "+tipoBusta+"/"+id+": "+e.getMessage();
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}
		}	else{
			StatelessMessage statelessMsg = ((StatelessMessage)this.state);
			Busta busta = statelessMsg.getBusta();
			return busta.getProfiloDiCollaborazione();
		}
	}
	
	/**
	 * Ritorna la collaborazione associata alla busta.
	 *
	 * @param id identificativo della busta.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @return Collaborazione associata alla busta
	 *
	 */
	private String getProfiloCollaborazioneValue(String id,String tipoBusta) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			return null; // info non disponibile, poiche' non salvata nel repository

		}	else{
			StatelessMessage statelessMsg = ((StatelessMessage)this.state);
			Busta busta = statelessMsg.getBusta();
			return busta.getProfiloDiCollaborazioneValue();
		}
	}

	/**
	 * Ritorna la sequenza associata alla busta.
	 *
	 * @param id identificativo della busta.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @return Sequenza associata alla busta
	 *
	 */
	private long getSequenza(String id,String tipoBusta) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{
				// Costruzione Query
				String query = "SELECT SEQUENZA FROM "+Costanti.REPOSITORY+" WHERE ID_MESSAGGIO=? AND TIPO=?";
				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(query);
				pstmt.setString(1,id);
				pstmt.setString(2,tipoBusta);
				rs = pstmt.executeQuery();
				if(rs == null) {
					throw new ProtocolException("RS Null?");
				}
				long sequenza = -1;
				if(rs.next()){
					sequenza = rs.getLong("SEQUENZA");
				}
				rs.close();
				pstmt.close();

				return sequenza;

			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore  getSequenza "+tipoBusta+"/"+id+": "+e.getMessage();
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {}
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}
		}else{

			StatelessMessage statelessMsg = ((StatelessMessage)this.state);
			Busta busta = statelessMsg.getBusta();
			return busta.getSequenza();
		}
	}

	/**
	 * Ritorna le informazioni di integrazione associate alla busta.
	 *
	 * @param id identificativo della busta.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @return Informazioni di integrazione associate alla busta
	 *
	 */
	private Integrazione getInfoIntegrazione(String id,String tipoBusta) throws ProtocolException{

		StateMessage stateful = (StateMessage)this.state;
		Connection connectionDB = stateful.getConnectionDB();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			// Costruzione Query
			String query = "SELECT LOCATION_PD,SERVIZIO_APPLICATIVO,MODULO_IN_ATTESA,SCENARIO FROM "+Costanti.REPOSITORY+" WHERE ID_MESSAGGIO=? AND TIPO=?";
			//log.debug("Query: "+query);
			pstmt = connectionDB.prepareStatement(query);
			pstmt.setString(1,id);
			pstmt.setString(2,tipoBusta);
			rs = pstmt.executeQuery();
			if(rs == null) {
				throw new ProtocolException("RS Null?");
			}
			Integrazione infoIntegrazione = new Integrazione();
			if(rs.next()){
				infoIntegrazione.setIdModuloInAttesa(rs.getString("MODULO_IN_ATTESA"));
				infoIntegrazione.setLocationPD(rs.getString("LOCATION_PD"));
				infoIntegrazione.setScenario(rs.getString("SCENARIO"));
				infoIntegrazione.setServizioApplicativo(rs.getString("SERVIZIO_APPLICATIVO"));
			}
			rs.close();
			pstmt.close();

			return infoIntegrazione;

		} catch(Exception e) {
			String errorMsg = "REPOSITORY_BUSTE, Errore  getInfoIntegrazione "+tipoBusta+"/"+id+": "+e.getMessage();
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {}
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {}
			this.log.error(errorMsg,e);
			throw new ProtocolException(errorMsg,e);
		}
	}

	/**
	 * Metodo che si occupa di eliminare l'indicazione di utilizzo della busta dalla PdD
	 *
	 * @param id identificativo della busta.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 *
	 */
	private void eliminaUtilizzoPdD(String id,String tipoBusta,boolean forzaEliminazioneDB) throws ProtocolException{
		this.eliminaUtilizzoPdD(id, tipoBusta, forzaEliminazioneDB, true);
	}
	private void eliminaUtilizzoPdD(String id,String tipoBusta,boolean forzaEliminazioneDB, boolean savePreparedStatement) throws ProtocolException{

		if(this.state instanceof StatefulMessage || forzaEliminazioneDB) {

			StateMessage stateMSG = (StateMessage)this.state;
			Connection connectionDB = stateMSG.getConnectionDB();

			PreparedStatement pstmtUpdate = null;
			try{

				StringBuffer queryUpdate = new StringBuffer();
				queryUpdate.append("UPDATE ");
				queryUpdate.append(Costanti.REPOSITORY);
				queryUpdate.append(" SET ");
				queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_PdD(false));
				queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
				pstmtUpdate.setString(1,id);
				pstmtUpdate.setString(2,tipoBusta);

				// Add PreparedStatement
				if(savePreparedStatement)
					stateMSG.getPreparedStatement().put("UPDATE AggiornamentoBusta"+tipoBusta+"_"+id,pstmtUpdate);
				else
					pstmtUpdate.executeUpdate();

			} catch(Exception e) {
				String errorMsg = "REPOSITORY_BUSTE, Errore di cancellazione utilizzo from PdD "+tipoBusta+"/"+id+": "+e.getMessage();
				this.log.error(errorMsg,e);
				try{
					if(pstmtUpdate != null)
						pstmtUpdate.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				throw new ProtocolException(errorMsg,e);
			}
		}

	}


	/**
	 * Metodo che si occupa di eliminare l'indicazione di utilizzo della busta dalla PdD
	 *
	 * @param id identificativo della busta.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 *
	 */
	private void eliminaBustaStateless(String id,String tipoBusta) throws ProtocolException{

		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmtUpdate = null;
		try{

			StringBuffer queryUpdate = new StringBuffer();
			queryUpdate.append("UPDATE ");
			queryUpdate.append(Costanti.REPOSITORY);
			queryUpdate.append(" SET ");
			queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_PdD(false));
			queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
			pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
			pstmtUpdate.setString(1,id);
			pstmtUpdate.setString(2,tipoBusta);

			// Add PreparedStatement
			stateMSG.getPreparedStatement().put("UPDATE AggiornamentoBusta"+tipoBusta+"_"+id,pstmtUpdate);
			
		} catch(Exception e) {
			String errorMsg = "REPOSITORY_BUSTE, Errore di cancellazione utilizzo from PdD "+tipoBusta+"/"+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if(pstmtUpdate != null)
					pstmtUpdate.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

	}	
	
	
	/**
	 * Metodo che si occupa di eliminare una busta, precedentemente registrata.
	 * Alla busta, al momento della registrazione e' stato associato
	 * l'identificativo della busta, quindi attraverso esso sara' possibile trovarlo
	 *
	 * @param id identificativo della busta da eliminare.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 *
	 */
	private void eliminaBusta(String id, String tipoBusta) throws ProtocolException{

		StateMessage state = (StateMessage)this.state;
		Connection connectionDB = state.getConnectionDB();
		PreparedStatement pstmtDelete = null;
		try{

			// Eliminazione dati applicativi

			// eliminazione di richieste delegata
			if(Costanti.OUTBOX.equals(tipoBusta)){

				// Eliminazione ricezione riscontro
				if(this.state instanceof StatefulMessage) {
					Riscontri riscontri = new Riscontri(state,this.log);
					riscontri.validazioneRiscontroRicevuto(id);
					state.executePreparedStatement();
				}

				// Eliminazione invio asincrono
				ProfiloDiCollaborazione profilo = new ProfiloDiCollaborazione(state, this.log, this.protocolFactory);
				profilo.asincrono_eliminaRegistrazione(id, Costanti.OUTBOX);
				state.executePreparedStatement();

			}
			// eliminazione di richieste applicativa
			else if(Costanti.INBOX.equals(tipoBusta)){

				//	Eliminazione ricezione asincrono
				ProfiloDiCollaborazione profilo = new ProfiloDiCollaborazione(state, this.log, this.protocolFactory);
				profilo.asincrono_eliminaRegistrazione(id, Costanti.INBOX);
				state.executePreparedStatement();
			}


			if(this.state instanceof StatefulMessage) {
			
				// elimino lista trasmissioni
				StringBuffer queryDelete = new StringBuffer();
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_TRASMISSIONI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDelete = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDelete.setString(1,id);
				pstmtDelete.setString(2,tipoBusta);
				pstmtDelete.execute();
				pstmtDelete.close();
	
				//	elimino lista riscontri
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_RISCONTRI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDelete = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDelete.setString(1,id);
				pstmtDelete.setString(2,tipoBusta);
				pstmtDelete.execute();
				pstmtDelete.close();
	
				//	elimino lista eccezioni
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_ECCEZIONI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDelete = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDelete.setString(1,id);
				pstmtDelete.setString(2,tipoBusta);
				pstmtDelete.execute();
				pstmtDelete.close();
				
				// elimino lista ext
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_EXT_INFO);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDelete = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDelete.setString(1,id);
				pstmtDelete.setString(2,tipoBusta);
				pstmtDelete.execute();
				pstmtDelete.close();
			}

			// Elimino busta
			StringBuffer queryDelete = new StringBuffer();
			queryDelete.append("DELETE FROM ");
			queryDelete.append(Costanti.REPOSITORY);
			queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
			pstmtDelete = connectionDB.prepareStatement(queryDelete.toString());
			pstmtDelete.setString(1,id);
			pstmtDelete.setString(2,tipoBusta);
			pstmtDelete.execute();
			pstmtDelete.close();

		} catch(Exception e) {
			String errorMsg = "REPOSITORY_BUSTE, Errore di cancellazione from "+Costanti.REPOSITORY+" "+tipoBusta+"/"+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if( pstmtDelete != null )
					pstmtDelete.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}
	}













	/**
	 * Ritorna le buste salvate scadute o inutilizzate (tutti gli accessi uguale a 0) nella INBOX
	 *
	 * @return vector di stringhe contenenti gli ID delle buste scadute e/o inutilizzate con il tipo passato come parametro.
	 *
	 */
	public Vector<String> getBusteDaEliminareFromInBox(int limit,boolean logQuery,boolean forceIndex,boolean filtraBustaScadetureRispettoOraRegistrazione,boolean orderBy) throws ProtocolException{
		return this.getBusteDaEliminare(Costanti.INBOX,limit,logQuery,forceIndex,filtraBustaScadetureRispettoOraRegistrazione,orderBy);
	}

	/**
	 * Ritorna le buste salvate scadute o inutilizzate (tutti gli accessi uguale a 0) nella OUTBOX
	 *
	 * @return vector di stringhe contenenti gli ID delle buste scadute e/o inutilizzate con il tipo passato come parametro.
	 *
	 */
	public Vector<String> getBusteDaEliminareFromOutBox(int limit,boolean logQuery,boolean forceIndex,boolean filtraBustaScadetureRispettoOraRegistrazione,boolean orderBy) throws ProtocolException{
		return this.getBusteDaEliminare(Costanti.OUTBOX,limit,logQuery,forceIndex,filtraBustaScadetureRispettoOraRegistrazione,orderBy);
	}


	/**
	 * Ritorna le buste salvate scadute o inutilizzate (tutti gli accessi uguale a 0)
	 *
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @return vector di stringhe contenenti gli ID delle buste scadute e/o inutilizzate con il tipo passato come parametro.
	 *
	 */
	private Vector<String> getBusteDaEliminare(String tipoBusta,int limit,boolean logQuery,boolean forceIndex,boolean filtraBustaScadetureRispettoOraRegistrazione,boolean orderBy) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			Vector<String> idBuste = new Vector<String>();
			IGestoreRepository gestorerepositoryBuste = Configurazione.getGestoreRepositoryBuste();
			String queryString = null;
			try{

				// Le buste da eliminare sono quelle che
				// -1 sono scadute e non possiedono l'accesso da pdd o l'accesso da profilo
				// -2 non possiedono piu' accessi da nessuna fonte
				//      se possiedono ancora l'accesso da pdd, vuole dire che un msg e' in rollback,
				//        e sara' eliminato l'accesso una volta consegnato con successo il msg,
				//        o una volta scaduto il msg (rollback)
				//      se possiedono ancora l'accesso da profilo, vuole dire che un msg e' in rollback,
				//        e sara' eliminato una volta terminato il profilo,
				//        o una volta scaduto il msg (rollback)

				
				// 1 sono scadute e non possiedono l'accesso da pdd o l'accesso da profilo 
				// Viene effettuato tale filtro SOLO se il filtro delle buste scadute rispetto all'ora di registrazione è attivo. 
				// Se invece tale opzione viene disabilitata, le informazioni per effettuare il filtro duplicati 
				// vengono mantenute all'infinito.
				// Sara' necessaria una manutenzione sistemistica del database per prevenire una crescita
				// del database che comporta un esaurimento di risorse
				if(filtraBustaScadetureRispettoOraRegistrazione){
					if(Configurazione.getSqlQueryObjectType()==null){
						StringBuffer query = new StringBuffer();
						query.append("SELECT ");
						if(forceIndex){
							query.append("/*+ index("+Costanti.REPOSITORY+" "+Costanti.REPOSITORY_INDEX_SCADENZA_SEARCH+") */");
						}
						query.append(" ID_MESSAGGIO FROM ");
						query.append(Costanti.REPOSITORY);
						query.append(" WHERE SCADENZA_BUSTA < ? AND TIPO=? AND ");
						// Inefficente. Si usa il nuovo metodo dell'interfaccia
						//query.append(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
						//query.append(" AND ");
						//query.append(gestorerepositoryBuste.createSQLCondition_PdD(false));
						query.append(gestorerepositoryBuste.createSQLCondition_enableOnlyHistory());
						queryString = query.toString();
					}else{
						ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());
						if(forceIndex){
							sqlQueryObject.addSelectForceIndex(Costanti.REPOSITORY, Costanti.REPOSITORY_INDEX_SCADENZA_SEARCH);
						}
						sqlQueryObject.addSelectField("ID_MESSAGGIO");
						sqlQueryObject.addSelectField("ORA_REGISTRAZIONE");
						sqlQueryObject.addSelectField("SCADENZA_BUSTA");
						sqlQueryObject.addSelectField("TIPO");
						sqlQueryObject.addSelectField(gestorerepositoryBuste.createSQLFields());
						sqlQueryObject.addFromTable(Costanti.REPOSITORY);
						sqlQueryObject.addWhereCondition("SCADENZA_BUSTA < ?");
						sqlQueryObject.addWhereCondition("TIPO=?");
						// Inefficente. Si usa il nuovo metodo dell'interfaccia
						//sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
						//sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_PdD(false));
						sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_enableOnlyHistory());
						sqlQueryObject.setANDLogicOperator(true);
						if(orderBy){
							//sqlQueryObject.addOrderBy("ORA_REGISTRAZIONE");
							sqlQueryObject.addOrderBy("SCADENZA_BUSTA"); // almeno si usa l'indice
							sqlQueryObject.setSortType(true);
						}
						sqlQueryObject.setLimit(limit);
						queryString = sqlQueryObject.createSQLQuery();
					}
	
					Timestamp now = DateManager.getTimestamp();
					//System.out.println("STRING QUERY REPOSITORY1 ["+queryString+"] 1["+now+"] 2["+tipoBusta+"]");
					pstmt = connectionDB.prepareStatement(queryString);
					pstmt.setTimestamp(1,now);
					pstmt.setString(2,tipoBusta);
	
					long startDateSQLCommand = DateManager.getTimeMillis();
					if(logQuery)
						this.log.debug("[QUERY] (repositoryBuste.busteScadute) ["+queryString+"] 1["+now+"] 2["+tipoBusta+"]...");
					rs = pstmt.executeQuery();
					long endDateSQLCommand = DateManager.getTimeMillis();
					long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
					if(logQuery)
						this.log.debug("[QUERY] (repositoryBuste.busteScadute) ["+queryString+"] 1["+now+"] 2["+tipoBusta+"] effettuata in "+secondSQLCommand+" secondi");
	
					int countLimit = 0;
					while(rs.next()){
						idBuste.add(rs.getString("ID_MESSAGGIO"));
						// LIMIT Applicativo
						if(Configurazione.getSqlQueryObjectType()==null){
							countLimit++;
							if(countLimit==limit)
								break;
						}
					}
					rs.close();
					pstmt.close();
				}

				queryString = null;
				if(Configurazione.getSqlQueryObjectType()==null){
					StringBuffer query = new StringBuffer();
					query.append("SELECT ");
					if(forceIndex){
						query.append("/*+ index("+Costanti.REPOSITORY+" "+Costanti.REPOSITORY_INDEX_TIPO_SEARCH+") */");
					}
					query.append(" ID_MESSAGGIO FROM ");
					query.append(Costanti.REPOSITORY);
					query.append(" WHERE TIPO=? AND ");
					// Inefficente. Si usa il nuovo metodo dell'interfaccia
//					query.append(gestorerepositoryBuste.createSQLCondition_History(false));
//					query.append(" AND ");
//					query.append(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
//					query.append(" AND ");
//					query.append(gestorerepositoryBuste.createSQLCondition_PdD(false));
					query.append(gestorerepositoryBuste.createSQLCondition_disabledAll());
					queryString = query.toString();
				}else{
					ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(Configurazione.getSqlQueryObjectType());
					if(forceIndex){
						sqlQueryObject.addSelectForceIndex(Costanti.REPOSITORY, Costanti.REPOSITORY_INDEX_TIPO_SEARCH);
					}
					sqlQueryObject.addSelectField("ID_MESSAGGIO");
					sqlQueryObject.addSelectField("ORA_REGISTRAZIONE");
					sqlQueryObject.addSelectField("TIPO");
					sqlQueryObject.addSelectField(gestorerepositoryBuste.createSQLFields());
					sqlQueryObject.addFromTable(Costanti.REPOSITORY);
					sqlQueryObject.addWhereCondition("TIPO=?");
					// Inefficente. Si usa il nuovo metodo dell'interfaccia
//					sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_History(false));
//					sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
//					sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_PdD(false));
					sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_disabledAll());
					sqlQueryObject.setANDLogicOperator(true);
					if(orderBy){
						//sqlQueryObject.addOrderBy("ORA_REGISTRAZIONE");
						sqlQueryObject.addOrderBy("SCADENZA_BUSTA"); // almeno si usa l'indice
						sqlQueryObject.setSortType(true);
					}
					sqlQueryObject.setLimit(limit);
					queryString = sqlQueryObject.createSQLQuery();
				}
				//System.out.println("STRING QUERY REPOSITORY2 ["+queryString+"] 1["+tipoBusta+"]");
				pstmt = connectionDB.prepareStatement(queryString.toString());
				pstmt.setString(1,tipoBusta);

				long startDateSQLCommand = DateManager.getTimeMillis();
				if(logQuery)
					this.log.debug("[QUERY] (repositoryBuste.busteCancellateLogicamente) ["+queryString+"] 1["+tipoBusta+"]...");
				rs = pstmt.executeQuery();
				long endDateSQLCommand = DateManager.getTimeMillis();
				long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
				if(logQuery)
					this.log.debug("[QUERY] (repositoryBuste.busteCancellateLogicamente) ["+queryString+"] 1["+tipoBusta+"] effettuata in "+secondSQLCommand+" secondi");

				int countLimit = 0;
				while(rs.next()){
					idBuste.add(rs.getString("ID_MESSAGGIO"));
					// LIMIT Applicativo
					if(Configurazione.getSqlQueryObjectType()==null){
						countLimit++;
						if(countLimit==limit)
							break;
					}
				}
				rs.close();
				pstmt.close();

				return idBuste;

			} catch(Exception e) {
				String errorMsg = "[repositoryBuste.getBusteDaEliminare] errore, queryString["+queryString+"]: "+e.getMessage();
				this.log.error(errorMsg,e);
				try{
					if(rs != null)
						rs.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {
					// Eccezione SQL.
				}
				throw new ProtocolException(errorMsg,e);
			}
		}else{
			throw new ProtocolException("Metodo non invocabile in stateless mode");
		}
	}

	
	
	/**
	 * Metodo che si occupa di impostare l'indicazione di utilizzo della busta dalla PdD
	 *
	 * @param id identificativo della busta.
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 *
	 */
	public void impostaUtilizzoPdD(String id,String tipoBusta) throws ProtocolException{

		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmtUpdate = null;
		try{

			StringBuffer queryUpdate = new StringBuffer();
			queryUpdate.append("UPDATE ");
			queryUpdate.append(Costanti.REPOSITORY);
			queryUpdate.append(" SET ");
			queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_PdD(true));
			queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=?");
			pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
			pstmtUpdate.setString(1,id);
			pstmtUpdate.setString(2,tipoBusta);

			// Add PreparedStatement
			stateMSG.getPreparedStatement().put("UPDATE AggiornamentoBusta_setUtilizzoPdD"+tipoBusta+"_"+id,pstmtUpdate);

		} catch(Exception e) {
			String errorMsg = "REPOSITORY_BUSTE, Errore di impostazione utilizzo from PdD "+tipoBusta+"/"+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if(pstmtUpdate != null)
					pstmtUpdate.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

	}

	
	
	public boolean exists(String id,String tipoBusta) throws ProtocolException{

		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{

			StringBuffer queryUpdate = new StringBuffer();
			queryUpdate.append("SELECT ID_MESSAGGIO FROM ");
			queryUpdate.append(Costanti.REPOSITORY);
			queryUpdate.append(" WHERE  ID_MESSAGGIO = ? AND TIPO=? AND (");
			queryUpdate.append(this.gestoreRepositoryBuste.createSQLCondition_History(true));
			queryUpdate.append(" OR ");
			queryUpdate.append(this.gestoreRepositoryBuste.createSQLCondition_PdD(true));
			queryUpdate.append(" OR ");
			queryUpdate.append(this.gestoreRepositoryBuste.createSQLCondition_ProfiloCollaborazione(true));
			queryUpdate.append(" ) ");
			pstmt = connectionDB.prepareStatement(queryUpdate.toString());
			pstmt.setString(1,id);
			pstmt.setString(2,tipoBusta);
			rs = pstmt.executeQuery();
			if(rs==null){
				throw new Exception("Result set is null??");
			}
			boolean value = rs.next();
			rs.close();
			pstmt.close();
			return value;

		} catch(Exception e) {
			String errorMsg = "REPOSITORY_BUSTE, Errore durante l'utilizzo di exists "+tipoBusta+"/"+id+": "+e.getMessage();
			this.log.error(errorMsg,e);
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {
				// Eccezione SQL.
			}
			throw new ProtocolException(errorMsg,e);
		}

	}
}


