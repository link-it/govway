/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.transazioni;


import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.utils.CredenzialiMittente;
import org.openspcoop2.core.transazioni.utils.TempiElaborazione;
import org.openspcoop2.monitor.engine.config.TransactionResource;
import org.openspcoop2.monitor.engine.config.TransactionServiceLibrary;
import org.openspcoop2.pdd.core.token.InformazioniToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.protocol.sdk.dump.Messaggio;

/**     
 * Transaction
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Transaction {

	private boolean gestioneStateful = false;
	private String id = null;
	private String originator = null;
	public Transaction(String id, String originator, boolean gestioneStateful){
		this.id = id;
		this.originator = originator;
		this.gestioneStateful = gestioneStateful;
	}
	
	/** Indicazione se la transazione e' stata gestita (e quindi non piu' ulteriormente arricchibile) tramite le set e add */
	private final Integer semaphore = 1;
	private Boolean deleted = false;
	public void setDeleted() {
		synchronized (this.semaphore) {
			this.deleted = true;	
		}
	}
	
	/** RequestInfo */
	private RequestInfo requestInfo;
	
	/** urlInvocazione */
	private String urlInvocazione;
	
	/** traccia richiesta */
	private Traccia tracciaRichiesta;
	
	/** traccia risposta */
	private Traccia tracciaRisposta;
	
	/** Messaggi diagnostici */
	private List<MsgDiagnostico> msgDiagnostici = new ArrayList<MsgDiagnostico>();
	
	/** DumpMessaggi */
	private List<Messaggio> messaggi = new ArrayList<Messaggio>();
	private List<TipoMessaggio> messaggi_onlyLogFileTrace = new ArrayList<TipoMessaggio>();
	
	/** Scenario di cooperazione */
	private String scenarioCooperazione;
	
	/** Codice trasporto richiesta */
	private String codiceTrasportoRichiesta;
	
	/** Location */
	private String location;
	
	/** Tipo di connettore utilizzato */
	private String tipoConnettore;
	
	/** Credenziali */
	private String credenziali;
	
	/** SOAPFault */
	private String faultIntegrazione;
	private String formatoFaultIntegrazione;
	private String faultCooperazione;
	private String formatoFaultCooperazione;
	
	/** CorrelazioneApplicativaRisposta */
	private String correlazioneApplicativaRisposta;
	
	/** ServizioApplicativoErogatore */
	private List<String> serviziApplicativiErogatore = new ArrayList<String>();
	
	/** Tempo di accettazione del messaggio iniziale */
	private Date dataAccettazioneRichiesta;
	/** Tempo in ingresso del messaggio iniziale */
	private Date dataIngressoRichiesta;
	/** Tempo in uscita del messaggio iniziale */
	private Date dataUscitaRichiesta;
	/** Tempo di accettazione del messaggio di risposta */
	private Date dataAccettazioneRisposta;
	/** Tempo in ingresso del messaggio di risposta */
	private Date dataIngressoRisposta;
	/** Tempo in uscita del messaggio di risposta */
	private Date dataUscitaRisposta;

	/** Indicazione se la busta e' duplicata */
	private Vector<String> idProtocolloDuplicati = new Vector<String>();
	
	/** Stato */
	private String stato = null;
	
	/** Contenuti della Transazione */
	private Vector<TransactionResource> risorse = new Vector<TransactionResource>();
	private TransactionServiceLibrary transactionServiceLibrary;
	
	/** EventiGestione */
	private List<String> eventiGestione = new ArrayList<String>();
	
	/** InformazioniToken */
	private InformazioniToken informazioniToken;
	
	/** CredenzialiMittente */
	private CredenzialiMittente credenzialiMittente;
	
	/** TempiElaborazione */
	private TempiElaborazione tempiElaborazione = new TempiElaborazione();
	

	/** GET */
	
	public RequestInfo getRequestInfo() {
		return this.requestInfo;
	}
	
	public String getUrlInvocazione() {
		return this.urlInvocazione;
	}
	
	public Traccia getTracciaRichiesta() {
		return this.tracciaRichiesta;
	}
	
	public Traccia getTracciaRisposta() {
		return this.tracciaRisposta;
	}

	public List<MsgDiagnostico> getMsgDiagnostici() {
		return this.msgDiagnostici;
	}
	
	public int sizeMsgDiagnostici(){
		return this.msgDiagnostici.size();
	}
	
	public MsgDiagnostico getMsgDiagnostico(int index){
		return this.msgDiagnostici.get(index);
	}
	
	public MsgDiagnostico removeMsgDiagnostico(int index) {
		return this.msgDiagnostici.remove(index);
	}
	
	public List<Messaggio> getMessaggi() {
		return this.messaggi;
	}
	public List<TipoMessaggio> getMessaggi_onlyLogFileTrace() {
		return this.messaggi_onlyLogFileTrace;
	}
	
	public int sizeMessaggi(){
		return this.messaggi.size();
	}
	
	public Messaggio getMessaggio(int index){
		return this.messaggi.get(index);
	}
	
	public Messaggio removeMessaggio(int index) {
		return this.messaggi.remove(index);
	}
	
	public String getScenarioCooperazione() {
		return this.scenarioCooperazione;
	}
	
	public String getCodiceTrasportoRichiesta() {
		return this.codiceTrasportoRichiesta;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public String getTipoConnettore() {
		return this.tipoConnettore;
	}
	
	public String getCredenziali() {
		return this.credenziali;
	}
	
	public String getFaultIntegrazione() {
		return this.faultIntegrazione;
	}
	public String getFormatoFaultIntegrazione() {
		return this.formatoFaultIntegrazione;
	}
	public String getFaultCooperazione() {
		return this.faultCooperazione;
	}
	public String getFormatoFaultCooperazione() {
		return this.formatoFaultCooperazione;
	}
	
	public Date getDataAccettazioneRichiesta() {
		return this.dataAccettazioneRichiesta;
	}
	
	public Date getDataIngressoRichiesta() {
		return this.dataIngressoRichiesta;
	}
	
	public Date getDataUscitaRichiesta() {
		return this.dataUscitaRichiesta;
	}
	
	public Date getDataAccettazioneRisposta() {
		return this.dataAccettazioneRisposta;
	}
	
	public Date getDataIngressoRisposta() {
		return this.dataIngressoRisposta;
	}
	
	public Date getDataUscitaRisposta() {
		return this.dataUscitaRisposta;
	}
	
	public String getCorrelazioneApplicativaRisposta() {
		return this.correlazioneApplicativaRisposta;
	}
	
	public List<String> getServiziApplicativiErogatore() {
		return this.serviziApplicativiErogatore;
	}
	
	public List<String> getEventiGestione() {
		return this.eventiGestione;
	}
	
	public boolean containsEventoGestione(String evento){
		return this.eventiGestione.contains(evento);
	}
	
	public InformazioniToken getInformazioniToken() {
		return this.informazioniToken;
	}
	
	public CredenzialiMittente getCredenzialiMittente() {
		return this.credenzialiMittente;
	}
	
	// NOTA: Per adesso la funzionalità dei tempi di elaborazione vale solamente in comunicazioni stateless
	public TempiElaborazione getTempiElaborazione() {
		return this.tempiElaborazione;
	}
	
	
	/** SET 
	 * @throws TransactionDeletedException */
	
	public void setRequestInfo(RequestInfo requestInfo) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.requestInfo = requestInfo;
			}
		}else{
			this.requestInfo = requestInfo;
		}
	}
	
	public void setUrlInvocazione(String urlInvocazione) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.urlInvocazione = urlInvocazione;
			}
		}else{
			this.urlInvocazione = urlInvocazione;
		}
	}
	
	public void setTracciaRichiesta(Traccia tracciaRichiesta) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.tracciaRichiesta = tracciaRichiesta;
			}
		}else{
			this.tracciaRichiesta = tracciaRichiesta;
		}
	}

	public void setTracciaRisposta(Traccia tracciaRisposta) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.tracciaRisposta = tracciaRisposta;
			}
		}else{
			this.tracciaRisposta = tracciaRisposta;
		}
	}
	
	public void addMsgDiagnostico(MsgDiagnostico msgDiag) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.msgDiagnostici.add(msgDiag);
			}
		}else{
			this.msgDiagnostici.add(msgDiag);
		}
	}
	
	public void addMessaggio(Messaggio messaggio, boolean onlyLogFileTrace) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.messaggi.add(messaggio);
				if(onlyLogFileTrace) {
					this.messaggi_onlyLogFileTrace.add(messaggio.getTipoMessaggio());
				}
			}
		}else{
			this.messaggi.add(messaggio);
			if(onlyLogFileTrace) {
				this.messaggi_onlyLogFileTrace.add(messaggio.getTipoMessaggio());
			}
		}
	}

	public void setScenarioCooperazione(String scenarioCooperazione) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.scenarioCooperazione = scenarioCooperazione;
			}
		}else{
			this.scenarioCooperazione = scenarioCooperazione;
		}
	}
	
	public void setCodiceTrasportoRichiesta(String codiceTrasportoRichiesta) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.codiceTrasportoRichiesta = codiceTrasportoRichiesta;
			}
		}else{
			this.codiceTrasportoRichiesta = codiceTrasportoRichiesta;
		}
	}
	
	public void setLocation(String location) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.location = location;
			}
		}else{
			this.location = location;
		}
	}
	
	public void setTipoConnettore(String tipoConnettore) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.tipoConnettore = tipoConnettore;
			}
		}else{
			this.tipoConnettore = tipoConnettore;
		}
	}
	
	public void setCredenziali(String credenziali) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.credenziali = credenziali;
			}
		}else{
			this.credenziali = credenziali;
		}
	}

	public void setFaultIntegrazione(String fault) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.faultIntegrazione = fault;
			}
		}else{
			this.faultIntegrazione = fault;
		}
	}
	
	public void setFormatoFaultIntegrazione(String formatoFault) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.formatoFaultIntegrazione = formatoFault;
			}
		}else{
			this.formatoFaultIntegrazione = formatoFault;
		}
	}
	
	public void setFaultCooperazione(String fault) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.faultCooperazione = fault;
			}
		}else{
			this.faultCooperazione = fault;
		}
	}
	
	public void setFormatoFaultCooperazione(String formatoFault) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.formatoFaultCooperazione = formatoFault;
			}
		}else{
			this.formatoFaultCooperazione = formatoFault;
		}
	}

	public void setDataAccettazioneRichiesta(Date dataAccettazioneRichiesta) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.dataAccettazioneRichiesta = dataAccettazioneRichiesta;
			}
		}else{
			this.dataAccettazioneRichiesta = dataAccettazioneRichiesta;
		}
	}
	
	public void setDataIngressoRichiesta(Date dataIngressoRichiesta) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.dataIngressoRichiesta = dataIngressoRichiesta;
			}
		}else{
			this.dataIngressoRichiesta = dataIngressoRichiesta;
		}
	}

	public void setDataUscitaRichiesta(Date dataUscitaRichiesta) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.dataUscitaRichiesta = dataUscitaRichiesta;
			}
		}else{
			this.dataUscitaRichiesta = dataUscitaRichiesta;
		}
	}

	public void setDataAccettazioneRisposta(Date dataAccettazioneRisposta) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.dataAccettazioneRisposta = dataAccettazioneRisposta;
			}
		}else{
			this.dataAccettazioneRisposta = dataAccettazioneRisposta;
		}
	}
	
	public void setDataIngressoRisposta(Date dataIngressoRisposta) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.dataIngressoRisposta = dataIngressoRisposta;
			}
		}else{
			this.dataIngressoRisposta = dataIngressoRisposta;
		}
	}

	public void setDataUscitaRisposta(Date dataUscitaRisposta) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.dataUscitaRisposta = dataUscitaRisposta;
			}
		}else{
			this.dataUscitaRisposta = dataUscitaRisposta;
		}
	}

	public void setCorrelazioneApplicativaRisposta(
			String correlazioneApplicativaRisposta) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.correlazioneApplicativaRisposta = correlazioneApplicativaRisposta;
			}
		}else{
			this.correlazioneApplicativaRisposta = correlazioneApplicativaRisposta;
		}
	}
	
	public void addServizioApplicativoErogatore(String servizioApplicativoErogatore) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.serviziApplicativiErogatore.add(servizioApplicativoErogatore);
			}
		}else{
			this.serviziApplicativiErogatore.add(servizioApplicativoErogatore);
		}
	}
	
	public void addEventoGestione(String evento) throws TransactionDeletedException {
		if(evento.contains(",")){
			throw new RuntimeException("EventoGestione ["+evento+"] non deve contenere il carattere ','");
		}
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.eventiGestione.add(evento);
			}
		}else{
			this.eventiGestione.add(evento);
		}
	}
	
	public void setInformazioniToken(InformazioniToken informazioniToken) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.informazioniToken = informazioniToken;
			}
		}else{
			this.informazioniToken = informazioniToken;
		}
	}
	
	public void setCredenzialiMittente(CredenzialiMittente credenzialiMittente) throws TransactionDeletedException {
		if(this.gestioneStateful){
			synchronized (this.semaphore) {
				if(this.deleted){
					throw new TransactionDeletedException("Transaction eliminata");
				}
				this.credenzialiMittente = credenzialiMittente;
			}
		}else{
			this.credenzialiMittente = credenzialiMittente;
		}
	}
	
	public void addIdProtocolloDuplicato(String idBusta){
		this.idProtocolloDuplicati.add(idBusta);
	}
	
	public boolean containsIdProtocolloDuplicato(String idBusta){
		return this.idProtocolloDuplicati.contains(idBusta);
	}

	public String getStato() {
		return this.stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public Vector<TransactionResource> getRisorse() {
		return this.risorse;
	}

	public void addRisorsa(TransactionResource r){
		this.risorse.add(r);
	}
	
	public TransactionServiceLibrary getTransactionServiceLibrary() {
		return this.transactionServiceLibrary;
	}

	public void setTransactionServiceLibrary(
			TransactionServiceLibrary transactionServiceLibrary) {
		this.transactionServiceLibrary = transactionServiceLibrary;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("idTransazione: ").append(this.id);
		sb.append("\n").append("originator: ").append(this.originator);
		sb.append("\n").append("gestioneStateful: ").append(this.gestioneStateful);
		sb.append("\n").append("deleted: ").append(this.deleted);
		
		if(this.dataAccettazioneRichiesta!=null) {
			sb.append("\n").append("dataAccettazioneRichiesta: ").append(DateUtils.getSimpleDateFormatMs().format(this.dataAccettazioneRichiesta));
		}
		if(this.dataIngressoRichiesta!=null) {
			sb.append("\n").append("dataIngressoRichiesta: ").append(DateUtils.getSimpleDateFormatMs().format(this.dataIngressoRichiesta));
		}
		if(this.dataUscitaRichiesta!=null) {
			sb.append("\n").append("dataUscitaRichiesta: ").append(DateUtils.getSimpleDateFormatMs().format(this.dataUscitaRichiesta));
		}
		if(this.dataAccettazioneRisposta!=null) {
			sb.append("\n").append("dataAccettazioneRisposta: ").append(DateUtils.getSimpleDateFormatMs().format(this.dataAccettazioneRisposta));
		}
		if(this.dataIngressoRisposta!=null) {
			sb.append("\n").append("dataIngressoRisposta: ").append(DateUtils.getSimpleDateFormatMs().format(this.dataIngressoRisposta));
		}
		if(this.dataUscitaRisposta!=null) {
			sb.append("\n").append("dataUscitaRisposta: ").append(DateUtils.getSimpleDateFormatMs().format(this.dataUscitaRisposta));
		}
		
		if(this.urlInvocazione!=null) {
			sb.append("\n").append("urlInvocazione: ").append(this.urlInvocazione);
		}
		if(this.requestInfo!=null) {
			sb.append("\n").append(this.requestInfo.toString("requestInfo."));
		}
		
		if(this.tracciaRichiesta!=null) {
			sb.append("\n").append("tracciaRichiesta: presente");
			if(this.tracciaRichiesta.getBusta()!=null) {
				sb.append("\n").append("tracciaRichiesta.busta.ID: ").append(this.tracciaRichiesta.getBusta().getID());
			}
			if(this.tracciaRichiesta.getBusta().getCollaborazione()!=null) {
				sb.append("\n").append("tracciaRichiesta.busta.collaborazione: ").append(this.tracciaRichiesta.getBusta().getCollaborazione());
			}
			if(this.tracciaRichiesta.getBusta().getRiferimentoMessaggio()!=null) {
				sb.append("\n").append("tracciaRichiesta.busta.riferimentoMessaggio: ").append(this.tracciaRichiesta.getBusta().getRiferimentoMessaggio());
			}
			if(this.tracciaRichiesta.getCorrelazioneApplicativa()!=null) {
				sb.append("\n").append("tracciaRichiesta.correlazioneApplicativa: ").append(this.tracciaRichiesta.getCorrelazioneApplicativa());
			}
			if(this.tracciaRichiesta.getCorrelazioneApplicativaRisposta()!=null) {
				sb.append("\n").append("tracciaRichiesta.correlazioneApplicativaRisposta: ").append(this.tracciaRichiesta.getCorrelazioneApplicativaRisposta());
			}
			if(this.tracciaRichiesta.getEsitoElaborazioneMessaggioTracciato()!=null) {
				sb.append("\n").append("tracciaRichiesta.esitoElaborazione: ").append(this.tracciaRichiesta.getEsitoElaborazioneMessaggioTracciato());
			}
		}
		if(this.tracciaRisposta!=null) {
			sb.append("\n").append("tracciaRisposta: presente");
			if(this.tracciaRisposta.getBusta()!=null) {
				sb.append("\n").append("tracciaRisposta.busta.ID: ").append(this.tracciaRisposta.getBusta().getID());
			}
			if(this.tracciaRisposta.getBusta().getCollaborazione()!=null) {
				sb.append("\n").append("tracciaRisposta.busta.collaborazione: ").append(this.tracciaRisposta.getBusta().getCollaborazione());
			}
			if(this.tracciaRisposta.getBusta().getRiferimentoMessaggio()!=null) {
				sb.append("\n").append("tracciaRisposta.busta.riferimentoMessaggio: ").append(this.tracciaRisposta.getBusta().getRiferimentoMessaggio());
			}
			if(this.tracciaRisposta.getCorrelazioneApplicativa()!=null) {
				sb.append("\n").append("tracciaRisposta.correlazioneApplicativa: ").append(this.tracciaRisposta.getCorrelazioneApplicativa());
			}
			if(this.tracciaRisposta.getCorrelazioneApplicativaRisposta()!=null) {
				sb.append("\n").append("tracciaRisposta.correlazioneApplicativaRisposta: ").append(this.tracciaRisposta.getCorrelazioneApplicativaRisposta());
			}
			if(this.tracciaRisposta.getEsitoElaborazioneMessaggioTracciato()!=null) {
				sb.append("\n").append("tracciaRisposta.esitoElaborazione: ").append(this.tracciaRisposta.getEsitoElaborazioneMessaggioTracciato());
			}
		}
		
		if(this.msgDiagnostici!=null && !this.msgDiagnostici.isEmpty()) {
			for (int i = 0; i < this.msgDiagnostici.size(); i++) {
				MsgDiagnostico msgDiag = this.msgDiagnostici.get(i);
				StringBuilder sbDiagnostico = new StringBuilder();
				if(msgDiag.getGdo()!=null) {
					sbDiagnostico.append("<").append(DateUtils.getSimpleDateFormatMs().format(msgDiag.getGdo())).append("> ");
				}
				if(msgDiag.getIdFunzione()!=null) {
					sbDiagnostico.append("(").append(msgDiag.getIdFunzione()).append(") ");
				}
				if(msgDiag.getCodice()!=null) {
					sbDiagnostico.append("codice:").append(msgDiag.getCodice()).append(" ");
				}
				sbDiagnostico.append("severita:").append(msgDiag.getSeverita()).append(" ");
				sbDiagnostico.append("messaggio: ").append(msgDiag.getMessaggio());
				sb.append("\n").append("diagnostico[").append(i).append("]: ").append(sbDiagnostico.toString());
			}
		}
		
		if(this.messaggi!=null && !this.messaggi.isEmpty()) {
			sb.append("\n").append("messaggi: ").append(this.messaggi.size());
		}
		if(this.messaggi_onlyLogFileTrace!=null && !this.messaggi_onlyLogFileTrace.isEmpty()) {
			sb.append("\n").append("messaggi_onlyLogFileTrace: ").append(this.messaggi_onlyLogFileTrace.size());
		}
		
		if(this.scenarioCooperazione!=null) {
			sb.append("\n").append("scenarioCooperazione: ").append(this.scenarioCooperazione);
		}
		if(this.codiceTrasportoRichiesta!=null) {
			sb.append("\n").append("codiceTrasportoRichiesta: ").append(this.codiceTrasportoRichiesta);
		}
		if(this.location!=null) {
			sb.append("\n").append("location: ").append(this.location);
		}
		if(this.tipoConnettore!=null) {
			sb.append("\n").append("tipoConnettore: ").append(this.tipoConnettore);
		}
		if(this.credenziali!=null) {
			sb.append("\n").append("credenziali: ").append(this.credenziali);
		}
				
		if(this.formatoFaultIntegrazione!=null) {
			sb.append("\n").append("formatoFaultIntegrazione: ").append(this.formatoFaultIntegrazione);
		}
		if(this.faultIntegrazione!=null) {
			sb.append("\n").append("faultIntegrazione: ").append(this.faultIntegrazione);
		}
		if(this.formatoFaultCooperazione!=null) {
			sb.append("\n").append("formatoFaultCooperazione: ").append(this.formatoFaultCooperazione);
		}
		if(this.faultCooperazione!=null) {
			sb.append("\n").append("faultCooperazione: ").append(this.faultCooperazione);
		}
		
		if(this.correlazioneApplicativaRisposta!=null) {
			sb.append("\n").append("correlazioneApplicativaRisposta: ").append(this.correlazioneApplicativaRisposta);
		}
		
		if(this.serviziApplicativiErogatore!=null) {
			sb.append("\n").append("serviziApplicativiErogatore: ").append(this.serviziApplicativiErogatore);
		}
		
		if(this.idProtocolloDuplicati!=null && !this.idProtocolloDuplicati.isEmpty()) {
			StringBuilder sbDuplicati = new StringBuilder();
			for (String id : this.idProtocolloDuplicati) {
				if(sbDuplicati.length()>0) {
					sbDuplicati.append(", ");
				}
				sbDuplicati.append(id);
			}
			sb.append("\n").append("idProtocolloDuplicati: ").append(sbDuplicati.toString());
		}
		
		if(this.stato!=null) {
			sb.append("\n").append("stato: ").append(this.stato);
		}
		
		if(this.risorse!=null && !this.risorse.isEmpty()) {
			sb.append("\n").append("risorse: ").append(this.risorse.size());
		}
		
		if(this.eventiGestione!=null && !this.eventiGestione.isEmpty()) {
			StringBuilder sbEventi = new StringBuilder();
			for (String id : this.eventiGestione) {
				if(sbEventi.length()>0) {
					sbEventi.append(", ");
				}
				sbEventi.append(id);
			}
			sb.append("\n").append("eventiGestione: ").append(sbEventi.toString());
		}
		

		if(this.informazioniToken!=null) {
			sb.append("\n").append("informazioniToken: presente");
			if(this.informazioniToken.getClientId()!=null) {
				sb.append("\n").append("informazioniToken.clientId: ").append(this.informazioniToken.getClientId());
			}
			if(this.informazioniToken.getUsername()!=null) {
				sb.append("\n").append("informazioniToken.username: ").append(this.informazioniToken.getUsername());
			}
			if(this.informazioniToken.getSub()!=null) {
				sb.append("\n").append("informazioniToken.sub: ").append(this.informazioniToken.getSub());
			}
			if(this.informazioniToken.getIss()!=null) {
				sb.append("\n").append("informazioniToken.iss: ").append(this.informazioniToken.getIss());
			}
		}
		
		if(this.credenzialiMittente!=null) {
			sb.append("\n").append("credenzialiMittente: presente");
			if(this.credenzialiMittente.getTrasporto()!=null) {
				sb.append("\n").append("credenzialiMittente.trasporto (id:"+this.credenzialiMittente.getTrasporto().getId()+
						") (tipo:"+this.credenzialiMittente.getTrasporto().getTipo()+
						"): ").append(this.credenzialiMittente.getTrasporto().getCredenziale());
			}
			if(this.credenzialiMittente.getToken_issuer()!=null) {
				sb.append("\n").append("credenzialiMittente.issuer (id:"+this.credenzialiMittente.getToken_issuer().getId()+
						") (tipo:"+this.credenzialiMittente.getToken_issuer().getTipo()+
						"): ").append(this.credenzialiMittente.getToken_issuer().getCredenziale());
			}
			if(this.credenzialiMittente.getToken_subject()!=null) {
				sb.append("\n").append("credenzialiMittente.subject (id:"+this.credenzialiMittente.getToken_subject().getId()+
						") (tipo:"+this.credenzialiMittente.getToken_subject().getTipo()+
						"): ").append(this.credenzialiMittente.getToken_subject().getCredenziale());
			}
			if(this.credenzialiMittente.getToken_clientId()!=null) {
				sb.append("\n").append("credenzialiMittente.clientId (id:"+this.credenzialiMittente.getToken_clientId().getId()+
						") (tipo:"+this.credenzialiMittente.getToken_clientId().getTipo()+
						"): ").append(this.credenzialiMittente.getToken_clientId().getCredenziale());
			}
			if(this.credenzialiMittente.getToken_username()!=null) {
				sb.append("\n").append("credenzialiMittente.username (id:"+this.credenzialiMittente.getToken_username().getId()+
						") (tipo:"+this.credenzialiMittente.getToken_username().getTipo()+
						"): ").append(this.credenzialiMittente.getToken_username().getCredenziale());
			}
			if(this.credenzialiMittente.getToken_eMail()!=null) {
				sb.append("\n").append("credenzialiMittente.eMail (id:"+this.credenzialiMittente.getToken_eMail().getId()+
						") (tipo:"+this.credenzialiMittente.getToken_eMail().getTipo()+
						"): ").append(this.credenzialiMittente.getToken_eMail().getCredenziale());
			}
		}
		
		if(this.tempiElaborazione!=null) {
			sb.append("\n").append("tempiElaborazione: presente");
		}
		
		
		return sb.toString();
	}

}
