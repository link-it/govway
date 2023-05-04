/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.engine.driver;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.DBUtils;
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
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.slf4j.Logger;


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

	private IProtocolFactory<?> protocolFactory;

	
	/* ********  C O S T R U T T O R E  ******** */

	//public RepositoryBuste(IState state){
	//	this(state, Configurazione.getLibraryLog(), true,null);
	//}
	public RepositoryBuste(IState state, boolean isRichiesta,IProtocolFactory<?> protocolFactory){
		this(state, Configurazione.getLibraryLog(), isRichiesta,protocolFactory);
	}
	public RepositoryBuste(IState state, Logger alog,IProtocolFactory<?> protocolFactory){
		this(state, alog, true,protocolFactory);
	}
	public RepositoryBuste(IState state,IProtocolFactory<?> protocolFactory){
		this(state, Configurazione.getLibraryLog(), true,protocolFactory);
	}
	/**
	 * Costruttore.
	 *
	 * @param state Oggetto che rappresenta lo stato di una busta
	 *
	 */
	public RepositoryBuste(IState state, Logger alog, boolean isRichiesta,IProtocolFactory<?> protocolFactory){
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
	 * @param errors un oggetto List&lt;{@link org.openspcoop2.protocol.sdk.Eccezione}&gt; se contenente errori di validazione.
	 *
	 */
	public void registraBustaIntoOutBox(Busta busta,List<Eccezione> errors,long scadenza) throws ProtocolException{
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
		busta.setTipoServizio(servizio.getTipo());
		busta.setServizio(servizio.getNome());
		busta.setVersioneServizio(servizio.getVersione());
		busta.setAzione(servizio.getAzione());
		busta.setProfiloDiCollaborazione(profiloCollaborazione);
		busta.setConfermaRicezione(confermaRicezione);
		busta.setInoltro(inoltro,this.protocolFactory.createTraduttore().toString(inoltro));
		registraBusta(busta,Costanti.OUTBOX,null,scadenza);
	}

	public void aggiornaProprietaBustaIntoOutBox(Map<String, String> properties, String idBusta)throws ProtocolException{
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
	 * @param errors un oggetto List&lt;{@link org.openspcoop2.protocol.sdk.Eccezione}&gt; se contenente errori di validazione.
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void aggiornaBustaIntoOutBox(Busta busta,long scadenza,List<Eccezione> errors) throws ProtocolException{
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
		bustaDaAggiornare.setTipoServizio(servizio.getTipo());
		bustaDaAggiornare.setServizio(servizio.getNome());
		bustaDaAggiornare.setVersioneServizio(servizio.getVersione());
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
	 * @return un oggetto List&lt;{@link org.openspcoop2.protocol.sdk.Eccezione}&gt; se e' presente la busta richiesta.
	 *
	 */
	public List<Eccezione> getErrorsFromOutBox(String id, IProtocolFactory<?> protocolFactory) throws ProtocolException{
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
		return getSomeValues(id, Costanti.OUTBOX, parametri, false, null);
	}
	public Busta getSomeValuesFromOutBox(String id,LetturaParametriBusta parametri,boolean forzaLetturaDatabase) throws ProtocolException{
		return getSomeValues(id, Costanti.OUTBOX, parametri, forzaLetturaDatabase, null);
	}
	public Busta getSomeValuesFromOutBox(String id,LetturaParametriBusta parametri, Date dataRegistrazione) throws ProtocolException{
		return getSomeValues(id, Costanti.OUTBOX, parametri, false, dataRegistrazione);
	}
	public Busta getSomeValuesFromOutBox(String id,LetturaParametriBusta parametri,boolean forzaLetturaDatabase, Date dataRegistrazione) throws ProtocolException{
		return getSomeValues(id, Costanti.OUTBOX, parametri, forzaLetturaDatabase, dataRegistrazione);
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
		this.eliminaUtilizzoPdD(id,Costanti.OUTBOX,false, null);
	}
	public void eliminaUtilizzoPdDFromOutBox(String id,boolean forzaUpdateDB) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.OUTBOX,forzaUpdateDB, null);
	}
	public void eliminaUtilizzoPdDFromOutBox(String id,boolean forzaUpdateDB,boolean savePreparedStatement) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.OUTBOX,forzaUpdateDB,savePreparedStatement, null);
	}
	public void eliminaUtilizzoPdDFromOutBox(String id, Date dataRegistrazione) throws ProtocolException{
		this.eliminaUtilizzoPdD(id,Costanti.OUTBOX,false, dataRegistrazione);
	}
	public void eliminaUtilizzoPdDFromOutBox(String id,boolean forzaUpdateDB, Date dataRegistrazione) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.OUTBOX,forzaUpdateDB, dataRegistrazione);
	}
	public void eliminaUtilizzoPdDFromOutBox(String id,boolean forzaUpdateDB,boolean savePreparedStatement, Date dataRegistrazione) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.OUTBOX,forzaUpdateDB,savePreparedStatement, dataRegistrazione);
	}
	/**
	 * Metodo che si occupa di eliminare una busta precedentemente salvata.
	 * Alla busta, al momento della registrazione,
	 * e' stato associato l'identificativo della busta, quindi attraverso esso sara' possibile trovarlo nella History.
	 *
	 * @param id identificativo della busta.
	 *
	 */
	public void eliminaBustaFromOutBox(String id, Date data) throws ProtocolException{
		eliminaBusta(id,Costanti.OUTBOX, data);
	}

	public void eliminaBustaStatelessFromOutBox(String id) throws ProtocolException{
		eliminaBustaStateless(id,Costanti.OUTBOX, null);
	}
	public void eliminaBustaStatelessFromOutBox(String id, Date dataRegistrazione) throws ProtocolException{
		eliminaBustaStateless(id,Costanti.OUTBOX, dataRegistrazione);
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
	public void registraBustaIntoInBox(Busta busta,long scadenza, boolean saveServizioApplicativoFruitore) throws ProtocolException{
		registraBusta(busta,Costanti.INBOX,null,scadenza, saveServizioApplicativoFruitore);
	}
	/**
	 * Metodo che si occupa di registrare una busta in uscita con errori.
	 *
	 * @param busta Contiene le informazioni su di una busta in uscita (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param errors un oggetto List&lt;{@link org.openspcoop2.protocol.sdk.Eccezione}&gt; se contenente errori di validazione.
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void registraBustaIntoInBox(Busta busta,List<Eccezione> errors,long scadenza) throws ProtocolException{
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
		busta.setTipoServizio(servizio.getTipo());
		busta.setServizio(servizio.getNome());
		busta.setVersioneServizio(servizio.getVersione());
		busta.setAzione(servizio.getAzione());
		busta.setProfiloDiCollaborazione(profiloCollaborazione);
		busta.setConfermaRicezione(confermaRicezione);
		busta.setInoltro(inoltro,this.protocolFactory.createTraduttore().toString(inoltro));
		registraBusta(busta,Costanti.INBOX,null,scadenza);
	}

	public void aggiornaProprietaBustaIntoInBox(Map<String, String> properties, String idBusta)throws ProtocolException{
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
	public void aggiornaBustaIntoInBox(Busta busta,long scadenza, boolean saveServizioApplicativoFruitore) throws ProtocolException{
		aggiornaBusta(busta,Costanti.INBOX,scadenza,null,saveServizioApplicativoFruitore);
	}
	/**
	 * Metodo che si occupa di aggiornare i dati di una busta in entrata precedentemente inviata.
	 *
	 * @param busta Contiene le informazioni su di una busta in entrata (tipo {@link org.openspcoop2.protocol.sdk.Busta})
	 * @param errors un oggetto List&lt;{@link org.openspcoop2.protocol.sdk.Eccezione}&gt; se contenente errori di validazione.
	 * @param scadenza Scadenza Scadenza associata ad una busta, se non possiede essa stessa una scadenza
	 *
	 */
	public void aggiornaBustaIntoInBox(Busta busta,long scadenza,List<Eccezione> errors) throws ProtocolException{
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
		bustaDaAggiornare.setTipoServizio(servizio.getTipo());
		bustaDaAggiornare.setServizio(servizio.getNome());
		bustaDaAggiornare.setVersioneServizio(servizio.getVersione());
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
	public Busta getBustaFromInBox(String id, boolean readServizioApplicativoFruitore) throws ProtocolException{
		return getBusta(id,Costanti.INBOX, readServizioApplicativoFruitore);
	}

	/**
	 * Metodo che si occupa di ritornare un oggetto contenente errori di validazione
	 * {@link org.openspcoop2.protocol.sdk.Eccezione} avvenuti durante la validazione di una busta.
	 *
	 * @param id identificativo della busta da ritornare.
	 * @return un oggetto List&lt;{@link org.openspcoop2.protocol.sdk.Eccezione}&gt; se e' presente la busta richiesta.
	 *
	 */
	public List<Eccezione> getErrorsFromInBox(String id) throws ProtocolException{
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
		return getSomeValues(id,Costanti.INBOX,parametri,false, null);
	}
	public Busta getSomeValuesFromInBox(String id,LetturaParametriBusta parametri,boolean forzaLetturaDatabase) throws ProtocolException{
		return getSomeValues(id,Costanti.INBOX,parametri,forzaLetturaDatabase, null);
	}
	public Busta getSomeValuesFromInBox(String id,LetturaParametriBusta parametri, Date dataRegistrazione) throws ProtocolException{
		return getSomeValues(id,Costanti.INBOX,parametri,false,dataRegistrazione);
	}
	public Busta getSomeValuesFromInBox(String id,LetturaParametriBusta parametri,boolean forzaLetturaDatabase, Date dataRegistrazione) throws ProtocolException{
		return getSomeValues(id,Costanti.INBOX,parametri,forzaLetturaDatabase,dataRegistrazione);
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
		this.eliminaUtilizzoPdD(id,Costanti.INBOX,false, null);
	}
	public void eliminaUtilizzoPdDFromInBox(String id,boolean forzaUpdateDB) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.INBOX,forzaUpdateDB, null);
	}
	public void eliminaUtilizzoPdDFromInBox(String id,boolean forzaUpdateDB,boolean savePreparedStatement) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.INBOX,forzaUpdateDB,savePreparedStatement, null);
	}
	public void eliminaUtilizzoPdDFromInBox(String id, Date dataRegistrazione) throws ProtocolException{
		this.eliminaUtilizzoPdD(id,Costanti.INBOX,false, dataRegistrazione);
	}
	public void eliminaUtilizzoPdDFromInBox(String id,boolean forzaUpdateDB, Date dataRegistrazione) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.INBOX,forzaUpdateDB, dataRegistrazione);
	}
	public void eliminaUtilizzoPdDFromInBox(String id,boolean forzaUpdateDB,boolean savePreparedStatement, Date dataRegistrazione) throws ProtocolException{
		eliminaUtilizzoPdD(id,Costanti.INBOX,forzaUpdateDB,savePreparedStatement, dataRegistrazione);
	}

	/**
	 * Metodo che si occupa di eliminare una busta precedentemente salvata.
	 * Alla busta, al momento della registrazione,
	 * e' stato associato l'identificativo della busta, quindi attraverso esso sara' possibile trovarlo nella History.
	 *
	 * @param id identificativo della busta.
	 *
	 */
	public void eliminaBustaFromInBox(String id, Date data) throws ProtocolException{
		eliminaBusta(id,Costanti.INBOX, data);
	}

	public void eliminaBustaStatelessFromInBox(String id) throws ProtocolException{
		eliminaBustaStateless(id,Costanti.INBOX, null);
	}
	public void eliminaBustaStatelessFromInBox(String id, Date dataRegistrazione) throws ProtocolException{
		eliminaBustaStateless(id,Costanti.INBOX, dataRegistrazione);
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

			Timestamp dataRegistrazione = DateManager.getTimestamp();
			
			//	Impostazione scadenza
			Date scadenzaBusta = busta.getScadenza();
			if(scadenzaBusta == null){
				scadenzaBusta = new Date(DateManager.getTimeMillis()+(scadenza*60*1000));
			}
			Timestamp scadenzaT = new Timestamp( scadenzaBusta.getTime()  );


			/* Busta */

			// Registrazione Busta
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO  ");
			query.append(Costanti.REPOSITORY);
			query.append(" (ID_MESSAGGIO,TIPO,MITTENTE,IDPORTA_MITTENTE,TIPO_MITTENTE,IND_TELEMATICO_MITT,DESTINATARIO,IDPORTA_DESTINATARIO,TIPO_DESTINATARIO,IND_TELEMATICO_DEST");
			query.append(",VERSIONE_SERVIZIO,SERVIZIO,TIPO_SERVIZIO,AZIONE,PROFILO_DI_COLLABORAZIONE,SERVIZIO_CORRELATO,TIPO_SERVIZIO_CORRELATO");
			query.append(",COLLABORAZIONE,SEQUENZA,INOLTRO_SENZA_DUPLICATI,CONFERMA_RICEZIONE,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE");
			query.append(",RIFERIMENTO_MESSAGGIO,SCADENZA_BUSTA,DUPLICATI,LOCATION_PD,SERVIZIO_APPLICATIVO,MODULO_IN_ATTESA,SCENARIO,PROTOCOLLO,DATA_REGISTRAZIONE,");
			query.append(this.gestoreRepositoryBuste.createSQLFieldHistory());
			query.append(") ");
			query.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,");
			query.append(this.gestoreRepositoryBuste.getSQLValueHistory(true));
			query.append(")");
			
			pstmtBusta = connectionDB.prepareStatement(query.toString());
			int index = 1;
			pstmtBusta.setString(index++,busta.getID());
			pstmtBusta.setString(index++,Costanti.INBOX);
			pstmtBusta.setString(index++,busta.getMittente());
			pstmtBusta.setString(index++,busta.getIdentificativoPortaMittente());
			pstmtBusta.setString(index++,busta.getTipoMittente());
			pstmtBusta.setString(index++,busta.getIndirizzoMittente());
			pstmtBusta.setString(index++,busta.getDestinatario());
			pstmtBusta.setString(index++,busta.getIdentificativoPortaDestinatario());
			pstmtBusta.setString(index++,busta.getTipoDestinatario());
			pstmtBusta.setString(index++,busta.getIndirizzoDestinatario());
			pstmtBusta.setInt(index++,busta.getVersioneServizio());
			pstmtBusta.setString(index++,busta.getServizio());
			pstmtBusta.setString(index++,busta.getTipoServizio());
			pstmtBusta.setString(index++,busta.getAzione());
			pstmtBusta.setString(index++,busta.getProfiloDiCollaborazione().getEngineValue());
			pstmtBusta.setString(index++,busta.getServizioCorrelato());
			pstmtBusta.setString(index++,busta.getTipoServizioCorrelato());
			pstmtBusta.setString(index++,busta.getCollaborazione());
			pstmtBusta.setLong(index++, busta.getSequenza() );
			if(Inoltro.SENZA_DUPLICATI.equals(busta.getInoltro()))
				pstmtBusta.setInt(index++,1);
			else
				pstmtBusta.setInt(index++,0);
			if(busta.isConfermaRicezione())
				pstmtBusta.setInt(index++,1);
			else
				pstmtBusta.setInt(index++,0);
			Timestamp oraRec = null;
			TipoOraRegistrazione tipoOraRec = null;
			if(busta.getOraRegistrazione()!=null && busta.getTipoOraRegistrazione()!=null){
				oraRec = new Timestamp( busta.getOraRegistrazione().getTime() );
				tipoOraRec = busta.getTipoOraRegistrazione();
			}else{
				// poi sara' aggiornato.
				oraRec = dataRegistrazione;
				tipoOraRec = TipoOraRegistrazione.SINCRONIZZATO;
			}
			pstmtBusta.setTimestamp(index++,oraRec);
			pstmtBusta.setString(index++,tipoOraRec.getEngineValue());
			pstmtBusta.setString(index++,busta.getRiferimentoMessaggio());
			pstmtBusta.setTimestamp(index++,scadenzaT);
			pstmtBusta.setInt(index++,0);
			pstmtBusta.setString(index++,null); // locationPD
			pstmtBusta.setString(index++,null); // servizioApplicativo
			pstmtBusta.setString(index++,null); // moduloInAttesa
			pstmtBusta.setString(index++,null); // scenario
			if(busta.getProtocollo()!=null)
				pstmtBusta.setString(index++,busta.getProtocollo()); // protocollo
			else if(this.protocolFactory!=null){
				pstmtBusta.setString(index++,this.protocolFactory.getProtocol()); // protocollo
			}
			else{
				throw new ProtocolException("Protocol unknow");
			}
			pstmtBusta.setTimestamp(index++,dataRegistrazione);
				

			// Add PreparedStatement
			stateMSG.getPreparedStatement().put("INSERT RegistrazioneBustaForHistoryINBOX_"+busta.getID(),pstmtBusta);

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
	public void registraBusta(Busta busta,String tipoBusta,List<Eccezione> errors,long scadenza) throws ProtocolException{
		registraBusta(busta,tipoBusta,errors,scadenza,false);
	}
	public void registraBusta(Busta busta,String tipoBusta,List<Eccezione> errors,long scadenza, boolean saveServizioApplicativoFruitore) throws ProtocolException{
		
		if (!this.isRichiesta && this.state instanceof StatelessMessage) {
			((StatelessMessage)this.state).setBusta(busta);
			return;
		}
		
		Timestamp dataRegistrazione = DateManager.getTimestamp();
		
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
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO  ");
			query.append(Costanti.REPOSITORY);
			query.append(" (ID_MESSAGGIO,TIPO,MITTENTE,IDPORTA_MITTENTE,TIPO_MITTENTE,IND_TELEMATICO_MITT,DESTINATARIO,IDPORTA_DESTINATARIO,TIPO_DESTINATARIO,IND_TELEMATICO_DEST");
			query.append(",VERSIONE_SERVIZIO,SERVIZIO,TIPO_SERVIZIO,AZIONE,PROFILO_DI_COLLABORAZIONE,SERVIZIO_CORRELATO,TIPO_SERVIZIO_CORRELATO");
			query.append(",COLLABORAZIONE,SEQUENZA,INOLTRO_SENZA_DUPLICATI,CONFERMA_RICEZIONE,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE");
			query.append(",RIFERIMENTO_MESSAGGIO,SCADENZA_BUSTA,DUPLICATI,LOCATION_PD,SERVIZIO_APPLICATIVO,MODULO_IN_ATTESA,SCENARIO,PROTOCOLLO,DATA_REGISTRAZIONE) ");
			query.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pstmtBusta = connectionDB.prepareStatement(query.toString());
			int index = 1;
			pstmtBusta.setString(index++,busta.getID());
			pstmtBusta.setString(index++,tipoBusta);
			pstmtBusta.setString(index++,busta.getMittente());
			pstmtBusta.setString(index++,busta.getIdentificativoPortaMittente());
			pstmtBusta.setString(index++,busta.getTipoMittente());
			pstmtBusta.setString(index++,busta.getIndirizzoMittente());
			pstmtBusta.setString(index++,busta.getDestinatario());
			pstmtBusta.setString(index++,busta.getIdentificativoPortaDestinatario());
			pstmtBusta.setString(index++,busta.getTipoDestinatario());
			pstmtBusta.setString(index++,busta.getIndirizzoDestinatario());
			pstmtBusta.setInt(index++,busta.getVersioneServizio());
			pstmtBusta.setString(index++,busta.getServizio());
			pstmtBusta.setString(index++,busta.getTipoServizio());
			pstmtBusta.setString(index++,busta.getAzione());
			pstmtBusta.setString(index++,busta.getProfiloDiCollaborazione() == null ? null : busta.getProfiloDiCollaborazione().getEngineValue());
			pstmtBusta.setString(index++,busta.getServizioCorrelato());
			pstmtBusta.setString(index++,busta.getTipoServizioCorrelato());
			pstmtBusta.setString(index++,busta.getCollaborazione());
			pstmtBusta.setLong(index++, busta.getSequenza() );
			if(Inoltro.SENZA_DUPLICATI.equals(busta.getInoltro()))
				pstmtBusta.setInt(index++,1);
			else
				pstmtBusta.setInt(index++,0);
			if(busta.isConfermaRicezione())
				pstmtBusta.setInt(index++,1);
			else
				pstmtBusta.setInt(index++,0);
			Timestamp oraRec = null;
			TipoOraRegistrazione tipoOraRec = null;
			if(busta.getOraRegistrazione()!=null && busta.getTipoOraRegistrazione()!=null){
				oraRec = new Timestamp( busta.getOraRegistrazione().getTime() );
				tipoOraRec = busta.getTipoOraRegistrazione();
			}else{
				// poi sara' aggiornato.
				oraRec = dataRegistrazione;
				tipoOraRec = TipoOraRegistrazione.SINCRONIZZATO;
			}
			pstmtBusta.setTimestamp(index++,oraRec);
			pstmtBusta.setString(index++,tipoOraRec.getEngineValue());
			pstmtBusta.setString(index++,busta.getRiferimentoMessaggio());
			pstmtBusta.setTimestamp(index++,scadenzaT);
			pstmtBusta.setInt(index++,0);
			pstmtBusta.setString(index++,null); // locationPD
			if(saveServizioApplicativoFruitore) { // servizioApplicativo
				pstmtBusta.setString(index++,busta.getServizioApplicativoFruitore()); 
			}
			else {
				pstmtBusta.setString(index++,null);
			}
			pstmtBusta.setString(index++,null); // moduloInAttesa
			pstmtBusta.setString(index++,null); // scenario
			if(busta.getProtocollo()!=null)
				pstmtBusta.setString(index++,busta.getProtocollo()); // protocollo
			else if(this.protocolFactory!=null){
				pstmtBusta.setString(index++,this.protocolFactory.getProtocol()); // protocollo
			}
			else{
				throw new ProtocolException("Protocol unknow");
			}
			pstmtBusta.setTimestamp(index++,dataRegistrazione);

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
			index = 1;
			pstmtUpdateAccessoPdD.setString(index++,busta.getID());
			pstmtUpdateAccessoPdD.setString(index++,tipoBusta);
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
					query.append(" (ID_MESSAGGIO,TIPO,ID_RISCONTRO,RICEVUTA,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE,DATA_REGISTRAZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?)");
					pstmtListaRiscontri = connectionDB.prepareStatement(query.toString());
					index = 1;
					pstmtListaRiscontri.setString(index++,busta.getID());
					pstmtListaRiscontri.setString(index++,tipoBusta);
					pstmtListaRiscontri.setString(index++,riscontro.getID());
					pstmtListaRiscontri.setString(index++,riscontro.getRicevuta());
					Timestamp oraRecRiscontro = null;
					if(riscontro.getOraRegistrazione()!=null ){
						oraRecRiscontro = new Timestamp( riscontro.getOraRegistrazione().getTime() );
					}
					pstmtListaRiscontri.setTimestamp(index++,oraRecRiscontro);
					pstmtListaRiscontri.setString(index++,riscontro.getTipoOraRegistrazione().getEngineValue());
					pstmtListaRiscontri.setTimestamp(index++,dataRegistrazione);

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT RegistrazioneListaRiscontri_riscontro["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaRiscontri);
				}

				/* Lista Trasmissioni */
				for(int i=0; i<busta.sizeListaTrasmissioni(); i++){

					Trasmissione trasmissione = busta.getTrasmissione(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_TRASMISSIONI);
					query.append(" (ID_MESSAGGIO,TIPO,ORIGINE,TIPO_ORIGINE,DESTINAZIONE,TIPO_DESTINAZIONE,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE,DATA_REGISTRAZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?,?,?)");
					pstmtListaTrasmissioni = connectionDB.prepareStatement(query.toString());
					index = 1;
					pstmtListaTrasmissioni.setString(index++,busta.getID());
					pstmtListaTrasmissioni.setString(index++,tipoBusta);
					pstmtListaTrasmissioni.setString(index++,trasmissione.getOrigine());
					pstmtListaTrasmissioni.setString(index++,trasmissione.getTipoOrigine());
					pstmtListaTrasmissioni.setString(index++,trasmissione.getDestinazione());
					pstmtListaTrasmissioni.setString(index++,trasmissione.getTipoDestinazione());
					Timestamp oraRecTrasmissione = null;
					if( trasmissione.getOraRegistrazione()!=null ){
						oraRecTrasmissione = new Timestamp( trasmissione.getOraRegistrazione().getTime()  );
					}
					pstmtListaTrasmissioni.setTimestamp(index++,oraRecTrasmissione);
					pstmtListaTrasmissioni.setString(index++,trasmissione.getTempo().getEngineValue());
					pstmtListaTrasmissioni.setTimestamp(index++,dataRegistrazione);

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT RegistrazioneListaTrasmissioni_trasmissione["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaTrasmissioni);
				}

				/* Lista Eccezioni */
				for(int i=0; i<busta.sizeListaEccezioni(); i++){
					Eccezione eccezione = busta.getEccezione(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_ECCEZIONI);
					query.append(" (ID_MESSAGGIO,TIPO,VALIDAZIONE,CONTESTO,CODICE,RILEVANZA,POSIZIONE,DATA_REGISTRAZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?,?)");
					pstmtListaEccezioni = connectionDB.prepareStatement(query.toString());
					index = 1;
					pstmtListaEccezioni.setString(index++,busta.getID());
					pstmtListaEccezioni.setString(index++,tipoBusta);
					pstmtListaEccezioni.setInt(index++,0); // eccezione proprietaria delle busta
					pstmtListaEccezioni.setString(index++,eccezione.getContestoCodifica().getEngineValue());
					pstmtListaEccezioni.setInt(index++,eccezione.getCodiceEccezione().getCodice());
					pstmtListaEccezioni.setString(index++,eccezione.getRilevanza().getEngineValue());
					pstmtListaEccezioni.setString(index++,eccezione.getDescrizione(this.protocolFactory));
					pstmtListaEccezioni.setTimestamp(index++,dataRegistrazione);

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
						query.append(" (ID_MESSAGGIO,TIPO,VALIDAZIONE,CONTESTO,CODICE,RILEVANZA,POSIZIONE,DATA_REGISTRAZIONE)");
						query.append(" VALUES (?,?,?,?,?,?,?,?)");
						pstmtListaEccezioniValidazione = connectionDB.prepareStatement(query.toString());
						index = 1;
						pstmtListaEccezioniValidazione.setString(index++,busta.getID());
						pstmtListaEccezioniValidazione.setString(index++,tipoBusta);
						pstmtListaEccezioniValidazione.setInt(index++,1); // eccezione arrivata dopo la validazione
						pstmtListaEccezioniValidazione.setString(index++,eccezione.getContestoCodifica().getEngineValue());
						pstmtListaEccezioniValidazione.setInt(index++,eccezione.getCodiceEccezione().getCodice());
						pstmtListaEccezioniValidazione.setString(index++,eccezione.getRilevanza().getEngineValue());
						pstmtListaEccezioniValidazione.setString(index++,eccezione.getDescrizione(this.protocolFactory));
						pstmtListaEccezioniValidazione.setTimestamp(index++,dataRegistrazione);

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
							query.append(" (ID_MESSAGGIO,TIPO,NOME,VALORE,DATA_REGISTRAZIONE)");
							query.append(" VALUES (?,?,?,?,?)");
							pstmtListaExtInfo = connectionDB.prepareStatement(query.toString());
							index = 1;
							pstmtListaExtInfo.setString(index++,busta.getID());
							pstmtListaExtInfo.setString(index++,tipoBusta);
							pstmtListaExtInfo.setString(index++,name);
							pstmtListaExtInfo.setString(index++,value);
							pstmtListaExtInfo.setTimestamp(index++,dataRegistrazione);

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

	
	public void aggiornaProprietaBusta(Map<String, String> properties, String idBusta,String tipoBusta) throws ProtocolException{
				
		StateMessage stateMSG = (StateMessage)this.state;
				
		Connection connectionDB = stateMSG.getConnectionDB();

		if(connectionDB==null){
			return ;
		}
		
		PreparedStatement pstmtDeleteListaExtInfo = null;
		PreparedStatement pstmtListaExtInfo = null;

		try{

			//	elimino lista ext-info
			StringBuilder queryDelete = new StringBuilder();
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
				int index = 0;
				for (String name : properties.keySet()) {
					String value = properties.get(name);
						
					StringBuilder query = new StringBuilder();
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
	public void aggiornaBusta(Busta busta,String tipoBusta,long scadenza,List<Eccezione> errors) throws ProtocolException{
		aggiornaBusta(busta, tipoBusta, scadenza, errors, false);
	}
	public void aggiornaBusta(Busta busta,String tipoBusta,long scadenza,List<Eccezione> errors, boolean saveServizioApplicativoFruitore) throws ProtocolException{
		
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

			Timestamp dataRegistrazione = DateManager.getTimestamp();
			
			// Impostazione scadenza
			Date scadenzaBusta = busta.getScadenza();
			if(scadenzaBusta == null){
				scadenzaBusta = new Date(DateManager.getTimeMillis()+(scadenza*60*1000));
			}
			Timestamp scadenzaT = new Timestamp( scadenzaBusta.getTime()  );

			/* Busta */

			// Registrazione Busta
			StringBuilder query = new StringBuilder();
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
			query.append("SCADENZA_BUSTA = ? ,");
			query.append("DATA_REGISTRAZIONE = ? ");
			if(saveServizioApplicativoFruitore) {
				query.append("SERVIZIO_APPLICATIVO = ? ");
			}
			query.append(" WHERE ID_MESSAGGIO=? AND TIPO=?");
			pstmtBusta = connectionDB.prepareStatement(query.toString());
			int index = 1;
			pstmtBusta.setString(index++,busta.getMittente());
			pstmtBusta.setString(index++,busta.getIdentificativoPortaMittente());
			pstmtBusta.setString(index++,busta.getTipoMittente());
			pstmtBusta.setString(index++,busta.getIndirizzoMittente());
			pstmtBusta.setString(index++,busta.getDestinatario());
			pstmtBusta.setString(index++,busta.getIdentificativoPortaDestinatario());
			pstmtBusta.setString(index++,busta.getTipoDestinatario());
			pstmtBusta.setString(index++,busta.getIndirizzoDestinatario());
			pstmtBusta.setInt(index++,busta.getVersioneServizio());
			pstmtBusta.setString(index++,busta.getServizio());
			pstmtBusta.setString(index++,busta.getTipoServizio());
			pstmtBusta.setString(index++,busta.getAzione());
			pstmtBusta.setString(index++,busta.getProfiloDiCollaborazione() == null ? null : busta.getProfiloDiCollaborazione().getEngineValue());
			pstmtBusta.setString(index++,busta.getServizioCorrelato());
			pstmtBusta.setString(index++,busta.getTipoServizioCorrelato());
			pstmtBusta.setString(index++,busta.getCollaborazione());
			pstmtBusta.setLong(index++, busta.getSequenza());
			if(Inoltro.SENZA_DUPLICATI.equals(busta.getInoltro()))
				pstmtBusta.setInt(index++,1);
			else
				pstmtBusta.setInt(index++,0);
			if(busta.isConfermaRicezione())
				pstmtBusta.setInt(index++,1);
			else
				pstmtBusta.setInt(index++,0);
			Timestamp oraRec = null;
			TipoOraRegistrazione tipoOraRec = null;
			if(busta.getOraRegistrazione()!=null && busta.getTipoOraRegistrazione()!=null){
				oraRec = new Timestamp( busta.getOraRegistrazione().getTime() );
				tipoOraRec = busta.getTipoOraRegistrazione();
			}
			pstmtBusta.setTimestamp(index++,oraRec);
			pstmtBusta.setString(index++,tipoOraRec!=null ? tipoOraRec.getEngineValue() : null);
			pstmtBusta.setString(index++,busta.getRiferimentoMessaggio());
			pstmtBusta.setTimestamp(index++,scadenzaT);
			pstmtBusta.setTimestamp(index++,dataRegistrazione);
			if(saveServizioApplicativoFruitore) {
				pstmtBusta.setString(index++,busta.getServizioApplicativoFruitore());
			}
			
			pstmtBusta.setString(index++,busta.getID());
			pstmtBusta.setString(index++,tipoBusta);

			// Add PreparedStatement
			stateMSG.getPreparedStatement().put("UPDATE AggiornamentoBusta"+tipoBusta+"_"+busta.getID(),pstmtBusta);

			if(errors!=null){

				// cancella liste
				//	elimino lista trasmiossioni
				StringBuilder queryDelete = new StringBuilder();
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_TRASMISSIONI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDeleteListaRiscontri = connectionDB.prepareStatement(queryDelete.toString());
				index = 1;
				pstmtDeleteListaRiscontri.setString(index++,busta.getID());
				pstmtDeleteListaRiscontri.setString(index++,tipoBusta);

				//	Add PreparedStatement
				stateMSG.getPreparedStatement().put("DELETE(UPDATE) AggiornamentoBusta_listaRiscontri"+tipoBusta+"_"+busta.getID(),pstmtDeleteListaRiscontri);

				//	elimino lista riscontri
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_RISCONTRI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDeleteListaTrasmissioni = connectionDB.prepareStatement(queryDelete.toString());
				index = 1;
				pstmtDeleteListaTrasmissioni.setString(index++,busta.getID());
				pstmtDeleteListaTrasmissioni.setString(index++,tipoBusta);

				//	Add PreparedStatement
				stateMSG.getPreparedStatement().put("DELETE(UPDATE) AggiornamentoBusta_listaTrasmissioni"+tipoBusta+"_"+busta.getID(),pstmtDeleteListaTrasmissioni);

				//	elimino lista eccezioni
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_ECCEZIONI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDeleteListaEccezioni = connectionDB.prepareStatement(queryDelete.toString());
				index = 1;
				pstmtDeleteListaEccezioni.setString(index++,busta.getID());
				pstmtDeleteListaEccezioni.setString(index++,tipoBusta);

				//	Add PreparedStatement
				stateMSG.getPreparedStatement().put("DELETE(UPDATE) AggiornamentoBusta_listaEccezioni"+tipoBusta+"_"+busta.getID(),pstmtDeleteListaEccezioni);
				
				//	elimino lista ext-info
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_EXT_INFO);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				pstmtDeleteListaExtInfo = connectionDB.prepareStatement(queryDelete.toString());
				index = 1;
				pstmtDeleteListaExtInfo.setString(index++,busta.getID());
				pstmtDeleteListaExtInfo.setString(index++,tipoBusta);

				//	Add PreparedStatement
				stateMSG.getPreparedStatement().put("DELETE(UPDATE) AggiornamentoBusta_listaExtInfo"+tipoBusta+"_"+busta.getID(),pstmtDeleteListaExtInfo);
				

				// Inserisco nuove liste

				/* Lista Riscontri */
				for(int i=0; i<busta.sizeListaRiscontri(); i++){
					Riscontro riscontro = busta.getRiscontro(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_RISCONTRI);
					query.append(" (ID_MESSAGGIO,TIPO,ID_RISCONTRO,RICEVUTA,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE,DATA_REGISTRAZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?)");
					pstmtListaRiscontri = connectionDB.prepareStatement(query.toString());
					index = 1;
					pstmtListaRiscontri.setString(index++,busta.getID());
					pstmtListaRiscontri.setString(index++,tipoBusta);
					pstmtListaRiscontri.setString(index++,riscontro.getID());
					pstmtListaRiscontri.setString(index++,riscontro.getRicevuta());
					Timestamp oraRecRiscontro = null;
					if(riscontro.getOraRegistrazione()!=null ){
						oraRecRiscontro = new Timestamp( riscontro.getOraRegistrazione().getTime() );
					}
					pstmtListaRiscontri.setTimestamp(index++,oraRecRiscontro);
					pstmtListaRiscontri.setString(index++,riscontro.getTipoOraRegistrazione().getEngineValue());
					pstmtListaRiscontri.setTimestamp(index++,dataRegistrazione);

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT(UPDATE) RegistrazioneListaRiscontri_riscontro["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaRiscontri);
				}

				/* Lista Trasmissioni */
				for(int i=0; i<busta.sizeListaTrasmissioni(); i++){
					Trasmissione trasmissione = busta.getTrasmissione(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_TRASMISSIONI);
					query.append(" (ID_MESSAGGIO,TIPO,ORIGINE,TIPO_ORIGINE,DESTINAZIONE,TIPO_DESTINAZIONE,ORA_REGISTRAZIONE,TIPO_ORA_REGISTRAZIONE,DATA_REGISTRAZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?,?,?)");
					pstmtListaTrasmissioni = connectionDB.prepareStatement(query.toString());
					index = 1;
					pstmtListaTrasmissioni.setString(index++,busta.getID());
					pstmtListaTrasmissioni.setString(index++,tipoBusta);
					pstmtListaTrasmissioni.setString(index++,trasmissione.getOrigine());
					pstmtListaTrasmissioni.setString(index++,trasmissione.getTipoOrigine());
					pstmtListaTrasmissioni.setString(index++,trasmissione.getDestinazione());
					pstmtListaTrasmissioni.setString(index++,trasmissione.getTipoDestinazione());
					Timestamp oraRecTrasmissione = null;
					if( trasmissione.getOraRegistrazione()!=null ){
						oraRecTrasmissione = new Timestamp( trasmissione.getOraRegistrazione().getTime()  );
					}
					pstmtListaTrasmissioni.setTimestamp(index++,oraRecTrasmissione);
					pstmtListaTrasmissioni.setString(index++,trasmissione.getTempo().getEngineValue());
					pstmtListaTrasmissioni.setTimestamp(index++,dataRegistrazione);

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT(UPDATE) RegistrazioneListaTrasmissioni_trasmissione["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaTrasmissioni);
				}

				/* Lista Eccezioni */
				for(int i=0; i<busta.sizeListaEccezioni(); i++){
					Eccezione eccezione = busta.getEccezione(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_ECCEZIONI);
					query.append(" (ID_MESSAGGIO,TIPO,VALIDAZIONE,CONTESTO,CODICE,RILEVANZA,POSIZIONE,DATA_REGISTRAZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?,?)");
					pstmtListaEccezioni = connectionDB.prepareStatement(query.toString());
					index = 1;
					pstmtListaEccezioni.setString(index++,busta.getID());
					pstmtListaEccezioni.setString(index++,tipoBusta);
					pstmtListaEccezioni.setInt(index++,0); // eccezione proprietaria delle busta
					pstmtListaEccezioni.setString(index++,eccezione.getContestoCodifica().getEngineValue());
					pstmtListaEccezioni.setInt(index++,eccezione.getCodiceEccezione().getCodice());
					pstmtListaEccezioni.setString(index++,eccezione.getRilevanza().getEngineValue());
					pstmtListaEccezioni.setString(index++,eccezione.getDescrizione(this.protocolFactory));
					pstmtListaEccezioni.setTimestamp(index++,dataRegistrazione);

					// Add PreparedStatement
					stateMSG.getPreparedStatement().put("INSERT(UPDATE) RegistrazioneListaEccezioni_eccezioneBusta["+i+"]_"+tipoBusta+"_"+busta.getID(),pstmtListaEccezioni);
				}

				/* Lista Eccezioni Validazione */
				for(int i=0; i<errors.size(); i++){
					Eccezione eccezione = errors.get(i);
					query.delete(0,query.length());
					query.append("INSERT INTO  ");
					query.append(Costanti.LISTA_ECCEZIONI);
					query.append(" (ID_MESSAGGIO,TIPO,VALIDAZIONE,CONTESTO,CODICE,RILEVANZA,POSIZIONE,DATA_REGISTRAZIONE)");
					query.append(" VALUES (?,?,?,?,?,?,?,?)");
					pstmtListaEccezioniValidazione = connectionDB.prepareStatement(query.toString());
					index = 1;
					pstmtListaEccezioniValidazione.setString(index++,busta.getID());
					pstmtListaEccezioniValidazione.setString(index++,tipoBusta);
					pstmtListaEccezioniValidazione.setInt(index++,1); // eccezione arrivata dopo la validazione
					pstmtListaEccezioniValidazione.setString(index++,eccezione.getContestoCodifica().getEngineValue());
					pstmtListaEccezioniValidazione.setInt(index++,eccezione.getCodiceEccezione().getCodice());
					pstmtListaEccezioniValidazione.setString(index++,eccezione.getRilevanza().getEngineValue());
					pstmtListaEccezioniValidazione.setString(index++,eccezione.getDescrizione(this.protocolFactory));
					pstmtListaEccezioniValidazione.setTimestamp(index++,dataRegistrazione);

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
							query.append(" (ID_MESSAGGIO,TIPO,NOME,VALORE,DATA_REGISTRAZIONE)");
							query.append(" VALUES (?,?,?,?,?)");
							pstmtListaExtInfo = connectionDB.prepareStatement(query.toString());
							index = 1;
							pstmtListaExtInfo.setString(index++,busta.getID());
							pstmtListaExtInfo.setString(index++,tipoBusta);
							pstmtListaExtInfo.setString(index++,name);
							pstmtListaExtInfo.setString(index++,value);
							pstmtListaExtInfo.setTimestamp(index++,dataRegistrazione);

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
			StringBuilder query = new StringBuilder();
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
			} catch(Exception er) {
				// close
			}
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
				StringBuilder query = new StringBuilder();
				query.append("UPDATE ");
				query.append(Costanti.REPOSITORY);
				query.append(" SET LOCATION_PD=? , SERVIZIO_APPLICATIVO=? , MODULO_IN_ATTESA=? , SCENARIO=? WHERE  ID_MESSAGGIO = ? AND TIPO=?");
				pstmt = connectionDB.prepareStatement(query.toString());
				pstmt.setString(1,infoIntegrazione.getNomePorta());
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
				} catch(Exception er) {
					// close
				}
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
				StringBuilder query = new StringBuilder();
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
				} catch(Exception er) {
					// close
				}
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
				StringBuilder query = new StringBuilder();
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
				} catch(Exception er) {
					// close
				}
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
				StringBuilder query = new StringBuilder();
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
				StringBuilder query = new StringBuilder();
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

			StringBuilder query = new StringBuilder();
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
		return getBusta(id, tipoBusta, false);
	}
	private Busta getBusta(String id, String tipoBusta, boolean readServizioApplicativoFruitore) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try{

				// busta

				StringBuilder query = new StringBuilder();
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
				
				if(readServizioApplicativoFruitore) {
					busta.setServizioApplicativoFruitore(rs.getString("SERVIZIO_APPLICATIVO"));
				}

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
					riscontro.setRicevuta(rs.getString("RICEVUTA"));
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
	 * @return un oggetto List&lt;{@link org.openspcoop2.protocol.sdk.Eccezione}&gt; se e' presente la busta richiesta.
	 *
	 */
	private List<Eccezione> getErrors(String id,String tipoBusta) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();

			PreparedStatement pstmt = null;
			ResultSet rs = null;
			List<Eccezione> errors = new ArrayList<>();
			try{

				// Costruzione Query
				StringBuilder query = new StringBuilder();
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
	private Busta getSomeValues(String id,String tipoBusta,	LetturaParametriBusta parametri,boolean forzaLetturaSuDatabase, Date dataRegistrazione) throws ProtocolException{

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
				StringBuilder bf = new StringBuilder();
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
				bf.append(" WHERE ");
				if(dataRegistrazione!=null) {
					bf.append("(DATA_REGISTRAZIONE BETWEEN ? AND ?) AND ");
				}
				bf.append("ID_MESSAGGIO=? AND TIPO=?");

				//log.debug("Query: "+query);
				pstmt = connectionDB.prepareStatement(bf.toString());
				int index = 1;
				
				Timestamp leftValue = null;
				Timestamp rightValue = null;
				if(dataRegistrazione!=null) {
					leftValue = new Timestamp(dataRegistrazione.getTime() - (1000*60*5));
					rightValue = new Timestamp(dataRegistrazione.getTime() + (1000*60*5));
					pstmt.setTimestamp(index++,leftValue);
					pstmt.setTimestamp(index++,rightValue);
				}
				
				pstmt.setString(index++,id);
				pstmt.setString(index++,tipoBusta);
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
				} catch(Exception er) {
					// close
				}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {
					// close
				}
				this.log.error(errorMsg,e);
				throw new ProtocolException(errorMsg,e);
			}
		}else{
			StatelessMessage statelessMsg = ((StatelessMessage)this.state);
			Busta busta = statelessMsg.getBusta().newInstance(); // Per evitare che venga modificato l'oggetto richiesta durante il salvataggio.
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
				} catch(Exception er) {
					// close
				}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {
					// close
				}
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
				} catch(Exception er) {
					// close
				}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {
					// close
				}
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
				} catch(Exception er) {
					// close
				}
				try{
					if(pstmt != null)
						pstmt.close();
				} catch(Exception er) {
					// close
				}
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
				infoIntegrazione.setNomePorta(rs.getString("LOCATION_PD"));
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
			} catch(Exception er) {
				// close
			}
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {
				// close
			}
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
	private void eliminaUtilizzoPdD(String id,String tipoBusta,boolean forzaEliminazioneDB, Date dataRegistrazione) throws ProtocolException{
		this.eliminaUtilizzoPdD(id, tipoBusta, forzaEliminazioneDB, true, dataRegistrazione);
	}
	private void eliminaUtilizzoPdD(String id,String tipoBusta,boolean forzaEliminazioneDB, boolean savePreparedStatement, Date dataRegistrazione) throws ProtocolException{

		if(this.state instanceof StatefulMessage || forzaEliminazioneDB) {

			StateMessage stateMSG = (StateMessage)this.state;
			Connection connectionDB = stateMSG.getConnectionDB();

			PreparedStatement pstmtUpdate = null;
			try{

				StringBuilder queryUpdate = new StringBuilder();
				queryUpdate.append("UPDATE ");
				queryUpdate.append(Costanti.REPOSITORY);
				queryUpdate.append(" SET ");
				queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_PdD(false));
				queryUpdate.append(" WHERE ");
				if(dataRegistrazione!=null) {
					queryUpdate.append("(DATA_REGISTRAZIONE BETWEEN ? AND ?) AND ");
				}
				queryUpdate.append("ID_MESSAGGIO = ? AND TIPO=?");
							
				pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
				int index = 1;
				
				Timestamp leftValue = null;
				Timestamp rightValue = null;
				if(dataRegistrazione!=null) {
					leftValue = new Timestamp(dataRegistrazione.getTime() - (1000*60*5));
					rightValue = new Timestamp(dataRegistrazione.getTime() + (1000*60*5));
					pstmtUpdate.setTimestamp(index++,leftValue);
					pstmtUpdate.setTimestamp(index++,rightValue);
				}
				
				pstmtUpdate.setString(index++,id);
				pstmtUpdate.setString(index++,tipoBusta);

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
	private void eliminaBustaStateless(String id,String tipoBusta, Date dataRegistrazione) throws ProtocolException{

		StateMessage stateMSG = (StateMessage)this.state;
		Connection connectionDB = stateMSG.getConnectionDB();

		PreparedStatement pstmtUpdate = null;
		try{

			StringBuilder queryUpdate = new StringBuilder();
			queryUpdate.append("UPDATE ");
			queryUpdate.append(Costanti.REPOSITORY);
			queryUpdate.append(" SET ");
			queryUpdate.append(this.gestoreRepositoryBuste.createSQLSet_PdD(false));
			queryUpdate.append(" WHERE ");
			if(dataRegistrazione!=null) {
				queryUpdate.append("(DATA_REGISTRAZIONE BETWEEN ? AND ?) AND ");
			}
			queryUpdate.append("ID_MESSAGGIO = ? AND TIPO=?");
					
			pstmtUpdate = connectionDB.prepareStatement(queryUpdate.toString());
			int index = 1;
			
			Timestamp leftValue = null;
			Timestamp rightValue = null;
			if(dataRegistrazione!=null) {
				leftValue = new Timestamp(dataRegistrazione.getTime() - (1000*60*5));
				rightValue = new Timestamp(dataRegistrazione.getTime() + (1000*60*5));
				pstmtUpdate.setTimestamp(index++,leftValue);
				pstmtUpdate.setTimestamp(index++,rightValue);
			}
			
			pstmtUpdate.setString(index++,id);
			pstmtUpdate.setString(index++,tipoBusta);

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
	private void eliminaBusta(String id, String tipoBusta, Date data) throws ProtocolException{

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
				profilo.asincrono_eliminaRegistrazione(id, Costanti.OUTBOX, data);
				state.executePreparedStatement();

			}
			// eliminazione di richieste applicativa
			else if(Costanti.INBOX.equals(tipoBusta)){

				//	Eliminazione ricezione asincrono
				ProfiloDiCollaborazione profilo = new ProfiloDiCollaborazione(state, this.log, this.protocolFactory);
				profilo.asincrono_eliminaRegistrazione(id, Costanti.INBOX, data);
				state.executePreparedStatement();
			}

			java.sql.Timestamp nowT = null;
			if(data!=null) {
				nowT = new java.sql.Timestamp(data.getTime());
			}

			if(this.state instanceof StatefulMessage) {
			
				// elimino lista trasmissioni
				StringBuilder queryDelete = new StringBuilder();
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_TRASMISSIONI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				if(data!=null) {
					queryDelete.append(" AND DATA_REGISTRAZIONE<=?");
				}
				pstmtDelete = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDelete.setString(1,id);
				pstmtDelete.setString(2,tipoBusta);
				if(data!=null) {
					pstmtDelete.setTimestamp(3,nowT);
				}
				pstmtDelete.execute();
				pstmtDelete.close();
	
				//	elimino lista riscontri
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_RISCONTRI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				if(data!=null) {
					queryDelete.append(" AND DATA_REGISTRAZIONE<=?");
				}
				pstmtDelete = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDelete.setString(1,id);
				pstmtDelete.setString(2,tipoBusta);
				if(data!=null) {
					pstmtDelete.setTimestamp(3,nowT);
				}
				pstmtDelete.execute();
				pstmtDelete.close();
	
				//	elimino lista eccezioni
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_ECCEZIONI);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				if(data!=null) {
					queryDelete.append(" AND DATA_REGISTRAZIONE<=?");
				}
				pstmtDelete = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDelete.setString(1,id);
				pstmtDelete.setString(2,tipoBusta);
				if(data!=null) {
					pstmtDelete.setTimestamp(3,nowT);
				}
				pstmtDelete.execute();
				pstmtDelete.close();
				
				// elimino lista ext
				queryDelete.delete(0,queryDelete.length());
				queryDelete.append("DELETE FROM ");
				queryDelete.append(Costanti.LISTA_EXT_INFO);
				queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
				if(data!=null) {
					queryDelete.append(" AND DATA_REGISTRAZIONE<=?");
				}
				pstmtDelete = connectionDB.prepareStatement(queryDelete.toString());
				pstmtDelete.setString(1,id);
				pstmtDelete.setString(2,tipoBusta);
				if(data!=null) {
					pstmtDelete.setTimestamp(3,nowT);
				}
				pstmtDelete.execute();
				pstmtDelete.close();
			}

			// Elimino busta
			StringBuilder queryDelete = new StringBuilder();
			queryDelete.append("DELETE FROM ");
			queryDelete.append(Costanti.REPOSITORY);
			queryDelete.append(" WHERE ID_MESSAGGIO = ? AND TIPO = ?");
			if(data!=null) {
				queryDelete.append(" AND DATA_REGISTRAZIONE<=?");
			}
			pstmtDelete = connectionDB.prepareStatement(queryDelete.toString());
			pstmtDelete.setString(1,id);
			pstmtDelete.setString(2,tipoBusta);
			if(data!=null) {
				pstmtDelete.setTimestamp(3,nowT);
			}
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





	
	
	
	/* ------------- UTILITY PER LETTURA MESSAGGI DA ELIMINARE -------------- */
	
	public static Date getDataRegistrazioneBustaMassima(Connection connectionDB , String tipoDatabase, boolean logQuery, Logger logger) throws ProtocolException{
		return _getDataRegistrazioneBusta(false, connectionDB , tipoDatabase, logQuery, logger);
	}
	public static Date getDataRegistrazioneBustaMinima(Connection connectionDB , String tipoDatabase, boolean logQuery, Logger logger) throws ProtocolException{
		return _getDataRegistrazioneBusta(true, connectionDB , tipoDatabase, logQuery, logger);
	}
	private static Date _getDataRegistrazioneBusta(boolean min, Connection connectionDB , String tipoDatabase, boolean logQuery, Logger logger) throws ProtocolException{
	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String queryString = null;
		try{	
		
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			if(min) {
				sqlQueryObject.addSelectMinField(Costanti.REPOSITORY_COLUMN_DATA_REGISTRAZIONE, "check_data");
			}
			else {
				sqlQueryObject.addSelectMaxField(Costanti.REPOSITORY_COLUMN_DATA_REGISTRAZIONE, "check_data");
			}
			sqlQueryObject.addFromTable(Costanti.REPOSITORY);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			if(logQuery) {
				logger.debug("Esecuzione query ["+queryString+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
			}
			
			pstmt = connectionDB.prepareStatement(queryString);
			rs = pstmt.executeQuery();
			
			if(logQuery) {
				logger.debug("Esecuzione query ["+queryString+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata");
			}
			
			if(rs.next()) {
				return rs.getTimestamp("check_data");
			}
			return null;
		
		} catch(Exception e) {
			String errorMsg = "[RepositoryBuste.getDataRegistrazioneBusta] errore, queryString["+queryString+"]: "+e.getMessage();
			throw new ProtocolException(errorMsg,e);
		}
		finally {
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {
				// close
			}
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {
				// close
			}
		}
	}
	
	public static Date getDataScadenzaBustaMassima(Connection connectionDB , String tipoDatabase, boolean logQuery, Logger logger) throws ProtocolException{
		return _getDataScadenzaBusta(false, connectionDB , tipoDatabase, logQuery, logger);
	}
	public static Date getDataScadenzaBustaMinima(Connection connectionDB , String tipoDatabase, boolean logQuery, Logger logger) throws ProtocolException{
		return _getDataScadenzaBusta(true, connectionDB , tipoDatabase, logQuery, logger);
	}
	private static Date _getDataScadenzaBusta(boolean min, Connection connectionDB , String tipoDatabase, boolean logQuery, Logger logger) throws ProtocolException{
	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String queryString = null;
		try{	
		
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			if(min) {
				sqlQueryObject.addSelectMinField(Costanti.REPOSITORY_COLUMN_SCADENZA, "check_data");
			}
			else {
				sqlQueryObject.addSelectMaxField(Costanti.REPOSITORY_COLUMN_SCADENZA, "check_data");
			}
			sqlQueryObject.addFromTable(Costanti.REPOSITORY);
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			if(logQuery) {
				logger.debug("Esecuzione query ["+queryString+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
			}
			
			pstmt = connectionDB.prepareStatement(queryString);
			rs = pstmt.executeQuery();
			
			if(logQuery) {
				logger.debug("Esecuzione query ["+queryString+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata");
			}
			
			if(rs.next()) {
				return rs.getTimestamp("check_data");
			}
			return null;
		
		} catch(Exception e) {
			String errorMsg = "[RepositoryBuste.getDataScadenzaBusta] errore, queryString["+queryString+"]: "+e.getMessage();
			throw new ProtocolException(errorMsg,e);
		}
		finally {
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {
				// close
			}
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {
				// close
			}
		}
	}
	
	public static int countBusteInutiliIntoInBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			IGestoreRepository repository,
			Date leftDate, Date rightDate, boolean useDataRegistrazione) throws ProtocolException{
		return _countBusteIntoBox(true, connectionDB, tipoDatabase, logQuery, logger, false, repository, leftDate, rightDate, useDataRegistrazione);
	}
	public static int countBusteInutiliIntoOutBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			IGestoreRepository repository,
			Date leftDate, Date rightDate, boolean useDataRegistrazione) throws ProtocolException{
		return _countBusteIntoBox(false, connectionDB, tipoDatabase, logQuery, logger, false, repository, leftDate, rightDate, useDataRegistrazione);
	}
	public static int countBusteScaduteIntoInBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			IGestoreRepository repository) throws ProtocolException{
		return _countBusteIntoBox(true, connectionDB, tipoDatabase, logQuery, logger, true, repository, null, null, false);
	}
	public static int countBusteScaduteIntoOutBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			IGestoreRepository repository) throws ProtocolException{
		return _countBusteIntoBox(false, connectionDB, tipoDatabase, logQuery, logger, true, repository, null, null, false);
	}
	private static int _countBusteIntoBox(boolean searchIntoInbox, Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			boolean scadenzaMsg, IGestoreRepository repository,
			Date leftDate, Date rightDate, boolean useDataRegistrazione) throws ProtocolException{
		
		// Le buste da eliminare sono quelle che
		// -1 sono scadute e non possiedono l'accesso da pdd o l'accesso da profilo
		// -2 non possiedono piu' accessi da nessuna fonte
		//      se possiedono ancora l'accesso da pdd, vuole dire che un msg e' in rollback,
		//        e sara' eliminato l'accesso una volta consegnato con successo il msg,
		//        o una volta scaduto il msg (rollback)
		//      se possiedono ancora l'accesso da profilo, vuole dire che un msg e' in rollback,
		//        e sara' eliminato una volta terminato il profilo,
		//        o una volta scaduto il msg (rollback)
		
		String tipo = null; 
		if(searchIntoInbox)
			tipo = Costanti.INBOX;
		else
			tipo = Costanti.OUTBOX;
	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String queryString = null;
		try{	
		
			// Query per Ricerca messaggi scaduti
			java.sql.Timestamp nowT = null;
			if(scadenzaMsg) {
				nowT = DateManager.getTimestamp();
			}
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addSelectCountField("totale_msg");
			sqlQueryObject.addFromTable(Costanti.REPOSITORY);
			
			if(leftDate!=null) {
				sqlQueryObject.addWhereCondition((useDataRegistrazione ? Costanti.REPOSITORY_COLUMN_DATA_REGISTRAZIONE : Costanti.REPOSITORY_COLUMN_SCADENZA)+">=?");
			}
			if(rightDate!=null) {
				sqlQueryObject.addWhereCondition((useDataRegistrazione ? Costanti.REPOSITORY_COLUMN_DATA_REGISTRAZIONE : Costanti.REPOSITORY_COLUMN_SCADENZA)+"<=?");
			}
			if(scadenzaMsg) {
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY_COLUMN_SCADENZA+" < ?");
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
				// Inefficiente. Si usa il nuovo metodo dell'interfaccia
				//sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
				//sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_PdD(false));
				sqlQueryObject.addWhereCondition(repository.createSQLCondition_enableOnlyHistory());
			}
			else {
				sqlQueryObject.addWhereCondition(Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
			// Inefficiente. Si usa il nuovo metodo dell'interfaccia
	//			sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_History(false));
	//			sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
	//			sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_PdD(false));
				sqlQueryObject.addWhereCondition(repository.createSQLCondition_disabledAll());
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();

			pstmt = connectionDB.prepareStatement(queryString);
			int index = 1;
			List<Object> objects = new ArrayList<>();
			if(leftDate!=null) {
				java.sql.Timestamp leftDateT = new java.sql.Timestamp(leftDate.getTime());
				pstmt.setTimestamp(index++, leftDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(leftDateT));
				}
			}
			if(rightDate!=null) {
				java.sql.Timestamp rightDateT = new java.sql.Timestamp(rightDate.getTime());
				pstmt.setTimestamp(index++, rightDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(rightDateT));
				}
			}
			if(scadenzaMsg) {
				pstmt.setTimestamp(index++,nowT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(nowT));
				}
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			else {
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			
			String query = null;
			if(logQuery) {
				query = DBUtils.formatSQLString(queryString, objects.toArray());
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
			}
			rs = pstmt.executeQuery();
			
			int res = 0;
			if(rs.next()) {
				res = rs.getInt("totale_msg");
			}
			
			if(logQuery) {
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata; trovati "+res+" risultati");
			}
			
			return res;
		
		} catch(Exception e) {
			String errorMsg = "[RepositoryBuste.countBusteIntoBox] errore, queryString["+queryString+"]: "+e.getMessage();
			throw new ProtocolException(errorMsg,e);
		}
		finally {
			try{
				if(rs != null)
					rs.close();
			} catch(Exception er) {
				// close
			}
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {
				// close
			}
		}
	}
	
	
	
	
	public static SortedMap<Integer> deleteBusteInutiliIntoInBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			IGestoreRepository repository,
			Date leftDate, Date rightDate, boolean useDataRegistrazione) throws ProtocolException{
		return _deleteBusteIntoBox(true, connectionDB, tipoDatabase, logQuery, logger, false, repository, leftDate, rightDate, useDataRegistrazione);
	}
	public static SortedMap<Integer> deleteBusteInutiliIntoOutBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			IGestoreRepository repository,
			Date leftDate, Date rightDate, boolean useDataRegistrazione) throws ProtocolException{
		return _deleteBusteIntoBox(false, connectionDB, tipoDatabase, logQuery, logger, false, repository, leftDate, rightDate, useDataRegistrazione);
	}
	public static SortedMap<Integer> deleteBusteScaduteIntoInBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			IGestoreRepository repository) throws ProtocolException{
		return _deleteBusteIntoBox(true, connectionDB, tipoDatabase, logQuery, logger, true, repository, null, null, false);
	}
	public static SortedMap<Integer> deleteBusteScaduteIntoOutBox(Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			IGestoreRepository repository) throws ProtocolException{
		return _deleteBusteIntoBox(false, connectionDB, tipoDatabase, logQuery, logger, true, repository, null, null, false);
	}
	private static SortedMap<Integer> _deleteBusteIntoBox(boolean searchIntoInbox, Connection connectionDB , String tipoDatabase, 
			boolean logQuery, Logger logger,
			boolean scadenzaMsg, IGestoreRepository repository,
			Date leftDate, Date rightDate, boolean useDataRegistrazione) throws ProtocolException{
		
		SortedMap<Integer> mapTabelleRigheEliminate = new SortedMap<Integer>();
		
		String tipo = null; 
		if(searchIntoInbox)
			tipo = Costanti.INBOX;
		else
			tipo = Costanti.OUTBOX;
		
		PreparedStatement pstmt = null;
		String deleteString = null;
		try{	
		
			// Query per Ricerca messaggi scaduti
			java.sql.Timestamp nowT = null;
			if(scadenzaMsg) {
				nowT = DateManager.getTimestamp();
			}

			// Le buste da eliminare sono quelle che
			// -1 sono scadute e non possiedono l'accesso da pdd o l'accesso da profilo
			// -2 non possiedono piu' accessi da nessuna fonte
			//      se possiedono ancora l'accesso da pdd, vuole dire che un msg e' in rollback,
			//        e sara' eliminato l'accesso una volta consegnato con successo il msg,
			//        o una volta scaduto il msg (rollback)
			//      se possiedono ancora l'accesso da profilo, vuole dire che un msg e' in rollback,
			//        e sara' eliminato una volta terminato il profilo,
			//        o una volta scaduto il msg (rollback)
				
			// lista 
			ISQLQueryObject sqlQueryObjectRepositoryBusteJoin = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectRepositoryBusteJoin.addSelectField(org.openspcoop2.protocol.engine.constants.Costanti.REPOSITORY_COLUMN_ID_MESSAGGIO);
			sqlQueryObjectRepositoryBusteJoin.addFromTable(Costanti.REPOSITORY);
			if(leftDate!=null) {
				sqlQueryObjectRepositoryBusteJoin.addWhereCondition((useDataRegistrazione ? Costanti.REPOSITORY_COLUMN_DATA_REGISTRAZIONE : Costanti.REPOSITORY_COLUMN_SCADENZA)+">=?");
			}
			if(rightDate!=null) {
				sqlQueryObjectRepositoryBusteJoin.addWhereCondition((useDataRegistrazione ? Costanti.REPOSITORY_COLUMN_DATA_REGISTRAZIONE : Costanti.REPOSITORY_COLUMN_SCADENZA)+"<=?");
			}
			if(scadenzaMsg) {
				sqlQueryObjectRepositoryBusteJoin.addWhereCondition(Costanti.REPOSITORY_COLUMN_SCADENZA+" < ?");
				sqlQueryObjectRepositoryBusteJoin.addWhereCondition(Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
				// Inefficiente. Si usa il nuovo metodo dell'interfaccia
				//sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
				//sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_PdD(false));
				sqlQueryObjectRepositoryBusteJoin.addWhereCondition(repository.createSQLCondition_enableOnlyHistory());
			}
			else {
				sqlQueryObjectRepositoryBusteJoin.addWhereCondition(Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
			// Inefficiente. Si usa il nuovo metodo dell'interfaccia
	//			sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_History(false));
	//			sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
	//			sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_PdD(false));
				sqlQueryObjectRepositoryBusteJoin.addWhereCondition(repository.createSQLCondition_disabledAll());
			}
			sqlQueryObjectRepositoryBusteJoin.setANDLogicOperator(true);
			
			
			_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_RISCONTRI, 
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_RISCONTRI_COLUMN_TIPO_MESSAGGIO, 
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_RISCONTRI_COLUMN_ID_MESSAGGIO,
					tipo, nowT, sqlQueryObjectRepositoryBusteJoin,
					leftDate, rightDate,
					mapTabelleRigheEliminate);
			
			_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_TRASMISSIONI, 
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_TRASMISSIONI_COLUMN_TIPO_MESSAGGIO, 
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_TRASMISSIONI_COLUMN_ID_MESSAGGIO,
					tipo, nowT, sqlQueryObjectRepositoryBusteJoin,
					leftDate, rightDate,
					mapTabelleRigheEliminate);
			
			_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_ECCEZIONI, 
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_ECCEZIONI_COLUMN_TIPO_MESSAGGIO, 
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_ECCEZIONI_COLUMN_ID_MESSAGGIO,
					tipo, nowT, sqlQueryObjectRepositoryBusteJoin,
					leftDate, rightDate,
					mapTabelleRigheEliminate);
			
			_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_EXT_INFO, 
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_EXT_INFO_COLUMN_TIPO_MESSAGGIO, 
					org.openspcoop2.protocol.engine.constants.Costanti.LISTA_EXT_INFO_COLUMN_ID_MESSAGGIO,
					tipo, nowT, sqlQueryObjectRepositoryBusteJoin,
					leftDate, rightDate,
					mapTabelleRigheEliminate);
			
			_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
					org.openspcoop2.protocol.engine.constants.Costanti.PROFILO_ASINCRONO, 
					org.openspcoop2.protocol.engine.constants.Costanti.PROFILO_ASINCRONO_COLUMN_TIPO_MESSAGGIO, 
					org.openspcoop2.protocol.engine.constants.Costanti.PROFILO_ASINCRONO_COLUMN_ID_MESSAGGIO,
					tipo, nowT, sqlQueryObjectRepositoryBusteJoin,
					leftDate, rightDate,
					mapTabelleRigheEliminate);
			
			if(!searchIntoInbox){
				_deleteListaRepositoryBuste(tipoDatabase, connectionDB, logQuery, logger,
						org.openspcoop2.protocol.engine.constants.Costanti.RISCONTRI_DA_RICEVERE, 
						null, 
						org.openspcoop2.protocol.engine.constants.Costanti.RISCONTRI_COLUMN_ID_MESSAGGIO,
						tipo, nowT, sqlQueryObjectRepositoryBusteJoin,
						leftDate, rightDate,
						mapTabelleRigheEliminate);
			}
				
			
			// Eliminazione busta reale
			
			ISQLQueryObject sqlQueryObjectRepositoryBuste = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectRepositoryBuste.addDeleteTable(Costanti.REPOSITORY);
			if(leftDate!=null) {
				sqlQueryObjectRepositoryBuste.addWhereCondition((useDataRegistrazione ? Costanti.REPOSITORY_COLUMN_DATA_REGISTRAZIONE : Costanti.REPOSITORY_COLUMN_SCADENZA)+">=?");
			}
			if(rightDate!=null) {
				sqlQueryObjectRepositoryBuste.addWhereCondition((useDataRegistrazione ? Costanti.REPOSITORY_COLUMN_DATA_REGISTRAZIONE : Costanti.REPOSITORY_COLUMN_SCADENZA)+"<=?");
			}
			if(scadenzaMsg) {
				sqlQueryObjectRepositoryBuste.addWhereCondition(Costanti.REPOSITORY_COLUMN_SCADENZA+" < ?");
				sqlQueryObjectRepositoryBuste.addWhereCondition(Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
				// Inefficiente. Si usa il nuovo metodo dell'interfaccia
				//sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
				//sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_PdD(false));
				sqlQueryObjectRepositoryBuste.addWhereCondition(repository.createSQLCondition_enableOnlyHistory());
			}
			else {
				sqlQueryObjectRepositoryBuste.addWhereCondition(Costanti.REPOSITORY_COLUMN_TIPO_MESSAGGIO+"=?");
			// Inefficiente. Si usa il nuovo metodo dell'interfaccia
	//			sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_History(false));
	//			sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
	//			sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_PdD(false));
				sqlQueryObjectRepositoryBuste.addWhereCondition(repository.createSQLCondition_disabledAll());
			}
			sqlQueryObjectRepositoryBuste.setANDLogicOperator(true);
			
			deleteString = sqlQueryObjectRepositoryBuste.createSQLDelete();
			pstmt = connectionDB.prepareStatement(deleteString);
			int index = 1;
			List<Object> objects = new ArrayList<>();
			if(leftDate!=null) {
				java.sql.Timestamp leftDateT = new java.sql.Timestamp(leftDate.getTime());
				pstmt.setTimestamp(index++, leftDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(leftDateT));
				}
			}
			if(rightDate!=null) {
				java.sql.Timestamp rightDateT = new java.sql.Timestamp(rightDate.getTime());
				pstmt.setTimestamp(index++, rightDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(rightDateT));
				}
			}
			if(scadenzaMsg) {
				pstmt.setTimestamp(index++,nowT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(nowT));
				}
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			else {
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			
			String query = null;
			if(logQuery) {
				query = DBUtils.formatSQLString(deleteString, objects.toArray());
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
			}
			int result = pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			
			if(logQuery) {
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata; "+result+" righe eliminate");
			}
			mapTabelleRigheEliminate.add(org.openspcoop2.protocol.engine.constants.Costanti.REPOSITORY, result);
			
			
			return mapTabelleRigheEliminate;
		
		} catch(Exception e) {
			String errorMsg = "[RepositoryBuste.deleteBusteIntoBox] errore, deleteString["+deleteString+"]: "+e.getMessage();
			throw new ProtocolException(errorMsg,e);
		} finally {
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {	
				// close
			}
		}
	}
	
	
	private static void _deleteListaRepositoryBuste(String tipoDatabase, Connection connectionDB, boolean logQuery, Logger logger,
			String nomeTabella, String nomeColonnaTipoMessaggio, String nomeColonnaIdMessaggio,
			String tipo, Timestamp scandenzaT, ISQLQueryObject sqlQueryObjectRepositoryBusteJoin,
			Date leftDate, Date rightDate,
			SortedMap<Integer> mapTabelleRigheEliminate) throws Exception {
		
		// Eliminazione busta reale
		
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
		sqlQueryObject.addDeleteTable(nomeTabella);
		if(nomeColonnaTipoMessaggio!=null) {
			sqlQueryObject.addWhereCondition(nomeColonnaTipoMessaggio+"=?");
		}
		sqlQueryObject.addWhereINSelectSQLCondition(false, nomeColonnaIdMessaggio, sqlQueryObjectRepositoryBusteJoin);
		sqlQueryObject.setANDLogicOperator(true);
		
		String deleteString = sqlQueryObject.createSQLDelete();
		PreparedStatement pstmt = null;
		try{	
			pstmt = connectionDB.prepareStatement(deleteString);
			int index = 1;
			List<Object> objects = new ArrayList<>();
			if(nomeColonnaTipoMessaggio!=null) {
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			
			if(leftDate!=null) {
				java.sql.Timestamp leftDateT = new java.sql.Timestamp(leftDate.getTime());
				pstmt.setTimestamp(index++, leftDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(leftDateT));
				}
			}
			if(rightDate!=null) {
				java.sql.Timestamp rightDateT = new java.sql.Timestamp(rightDate.getTime());
				pstmt.setTimestamp(index++, rightDateT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(rightDateT));
				}
			}
			if(scandenzaT!=null) {
				pstmt.setTimestamp(index++,scandenzaT);
				if(logQuery) {
					objects.add(DateUtils.getSimpleDateFormatMs().format(scandenzaT));
				}
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			else {
				pstmt.setString(index++,tipo);
				if(logQuery) {
					objects.add(tipo);
				}
			}
			
			String query = null;
			if(logQuery) {
				query = DBUtils.formatSQLString(deleteString, objects.toArray());
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") ...");
			}
			int result = pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;
			
			if(logQuery) {
				logger.debug("Esecuzione query ["+query+"] ("+DateUtils.getSimpleDateFormatMs().format(DateManager.getDate())+") completata; "+result+" righe eliminate");
			}
			mapTabelleRigheEliminate.add(nomeTabella, result);
		} finally {
			try{
				if(pstmt != null)
					pstmt.close();
			} catch(Exception er) {	
				// close
			}
		}
	}
	
	








	/**
	 * Ritorna le buste salvate scadute o inutilizzate (tutti gli accessi uguale a 0) nella INBOX
	 *
	 * @return List di stringhe contenenti gli ID delle buste scadute e/o inutilizzate con il tipo passato come parametro.
	 *
	 */
	public List<String> getBusteDaEliminareFromInBox(int limit,boolean logQuery,boolean forceIndex,boolean filtraBustaScadetureRispettoOraRegistrazione,boolean orderBy, Date data) throws ProtocolException{
		return this.getBusteDaEliminare(Costanti.INBOX,limit,logQuery,forceIndex,filtraBustaScadetureRispettoOraRegistrazione,orderBy, data);
	}

	/**
	 * Ritorna le buste salvate scadute o inutilizzate (tutti gli accessi uguale a 0) nella OUTBOX
	 *
	 * @return List di stringhe contenenti gli ID delle buste scadute e/o inutilizzate con il tipo passato come parametro.
	 *
	 */
	public List<String> getBusteDaEliminareFromOutBox(int limit,boolean logQuery,boolean forceIndex,boolean filtraBustaScadetureRispettoOraRegistrazione,boolean orderBy, Date data) throws ProtocolException{
		return this.getBusteDaEliminare(Costanti.OUTBOX,limit,logQuery,forceIndex,filtraBustaScadetureRispettoOraRegistrazione,orderBy, data);
	}


	/**
	 * Ritorna le buste salvate scadute o inutilizzate (tutti gli accessi uguale a 0)
	 *
	 * @param tipoBusta Indicazione sul tipo di busta inviata/ricevuta
	 * @return List di stringhe contenenti gli ID delle buste scadute e/o inutilizzate con il tipo passato come parametro.
	 *
	 */
	private List<String> getBusteDaEliminare(String tipoBusta,int limit,boolean logQuery,boolean forceIndex,boolean filtraBustaScadetureRispettoOraRegistrazione,boolean orderBy, Date data) throws ProtocolException{

		if(this.state instanceof StatefulMessage) {

			StatefulMessage stateful = (StatefulMessage)this.state;
			Connection connectionDB = stateful.getConnectionDB();

			java.sql.Timestamp nowT = null;
			if(data!=null) {
				nowT = new java.sql.Timestamp(data.getTime());
			}
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			List<String> idBuste = new ArrayList<>();
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
						StringBuilder query = new StringBuilder();
						query.append("SELECT ");
						if(forceIndex){
							query.append("/*+ index("+Costanti.REPOSITORY+" "+Costanti.REPOSITORY_INDEX_SCADENZA_SEARCH+") */");
						}
						query.append(" ID_MESSAGGIO FROM ");
						query.append(Costanti.REPOSITORY);
						query.append(" WHERE SCADENZA_BUSTA < ? AND TIPO=? AND ");
						// Inefficiente. Si usa il nuovo metodo dell'interfaccia
						//query.append(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
						//query.append(" AND ");
						//query.append(gestorerepositoryBuste.createSQLCondition_PdD(false));
						query.append(gestorerepositoryBuste.createSQLCondition_enableOnlyHistory());
						if(data!=null) {
							query.append(" AND DATA_REGISTRAZIONE<=?");
						}
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
						// Inefficiente. Si usa il nuovo metodo dell'interfaccia
						//sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
						//sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_PdD(false));
						sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_enableOnlyHistory());
						if(data!=null) {
							sqlQueryObject.addWhereCondition("DATA_REGISTRAZIONE<=?");
						}
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
					if(data!=null) {
						pstmt.setTimestamp(3,nowT);
					}
	
					long startDateSQLCommand = DateManager.getTimeMillis();
					if(logQuery)
						this.log.debug("[QUERY] (repositoryBuste.busteScadute) ["+queryString+"] 1["+now+"] 2["+tipoBusta+"] 3["+nowT+"]...");
					rs = pstmt.executeQuery();
					long endDateSQLCommand = DateManager.getTimeMillis();
					long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
					if(logQuery)
						this.log.debug("[QUERY] (repositoryBuste.busteScadute) ["+queryString+"] 1["+now+"] 2["+tipoBusta+"] 3["+nowT+"] effettuata in "+secondSQLCommand+" secondi");
	
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
					StringBuilder query = new StringBuilder();
					query.append("SELECT ");
					if(forceIndex){
						query.append("/*+ index("+Costanti.REPOSITORY+" "+Costanti.REPOSITORY_INDEX_TIPO_SEARCH+") */");
					}
					query.append(" ID_MESSAGGIO FROM ");
					query.append(Costanti.REPOSITORY);
					query.append(" WHERE TIPO=? AND ");
					// Inefficiente. Si usa il nuovo metodo dell'interfaccia
//					query.append(gestorerepositoryBuste.createSQLCondition_History(false));
//					query.append(" AND ");
//					query.append(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
//					query.append(" AND ");
//					query.append(gestorerepositoryBuste.createSQLCondition_PdD(false));
					query.append(gestorerepositoryBuste.createSQLCondition_disabledAll());
					if(data!=null) {
						query.append(" AND DATA_REGISTRAZIONE<=?");
					}
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
					// Inefficiente. Si usa il nuovo metodo dell'interfaccia
//					sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_History(false));
//					sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_ProfiloCollaborazione(false));
//					sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_PdD(false));
					sqlQueryObject.addWhereCondition(gestorerepositoryBuste.createSQLCondition_disabledAll());
					if(data!=null) {
						sqlQueryObject.addWhereCondition("DATA_REGISTRAZIONE<=?");
					}
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
				if(data!=null) {
					pstmt.setTimestamp(2,nowT);
				}

				long startDateSQLCommand = DateManager.getTimeMillis();
				if(logQuery)
					this.log.debug("[QUERY] (repositoryBuste.busteCancellateLogicamente) ["+queryString+"] 1["+tipoBusta+"] 2["+nowT+"]...");
				rs = pstmt.executeQuery();
				long endDateSQLCommand = DateManager.getTimeMillis();
				long secondSQLCommand = (endDateSQLCommand - startDateSQLCommand) / 1000;
				if(logQuery)
					this.log.debug("[QUERY] (repositoryBuste.busteCancellateLogicamente) ["+queryString+"] 1["+tipoBusta+"] 2["+nowT+"] effettuata in "+secondSQLCommand+" secondi");

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

			StringBuilder queryUpdate = new StringBuilder();
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

			StringBuilder queryUpdate = new StringBuilder();
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
	
	public boolean isRegistrazioneInCorso() {
		if(this.state!=null) {
			StateMessage stateMSG = (StateMessage)this.state;
			if(stateMSG.getPreparedStatement()!=null && stateMSG.getPreparedStatement().size()>0) {
				List<String> keys = stateMSG.getPreparedStatement().keys();
				if(keys!=null && !keys.isEmpty()) {
					for (String key : keys) {
						if(key!=null && key.startsWith("INSERT")) {
							return true;
						}
						else if(key!=null && key.startsWith("UPDATE")) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}


