/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.engine.validator;

import jakarta.xml.soap.SOAPHeader;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSintatticaResult;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * Classe utilizzata per la validazione sintattica di una busta.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ValidazioneSintattica {

	/** Messaggio. */
	protected OpenSPCoop2Message msg;
	/** HeaderSOAP */
	protected SOAPHeader headerSOAP = null;
	/** Header */
	protected BustaRawContent<?> headerProtocollo;
	
	public BustaRawContent<?> getProtocolHeader(){
		return this.headerProtocollo;
	}
	
	public void setHeaderSOAP(SOAPHeader headerSOAP) {
		this.headerSOAP = headerSOAP;
	}
	/** Errori di validazione riscontrati sulla busta */
	protected java.util.List<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	protected java.util.List<Eccezione> erroriProcessamento;
	private String erroreProcessamento_internalMessage;	
	/** Errors riscontrati sulla lista eccezioni */
	protected java.util.List<Eccezione> errorsTrovatiSullaListaEccezioni;
	/** Busta */
	protected Busta busta;
	protected Boolean isRichiesta;
	/** Eventuale errore avvenuto durante il processo di validazione */
	protected ErroreCooperazione errore;
	protected IntegrationFunctionError errore_integrationFunctionError;
	/** Indicazione se leggere gli attributi qualificati */
	protected boolean readQualifiedAttribute;

	/** Logger utilizzato per debug. */
	protected org.slf4j.Logger log = null;

	protected boolean segnalazioneElementoPresentePiuVolte = false;
	private IProtocolFactory<?> protocolFactory;
	private IProtocolManager protocolManager;
	/** Indicazione se la busta risulta scaduta */
	protected boolean messaggioScaduto = false;
	
	/** bustaErroreHeaderIntestazione: generata solo quando la busta arrivata non contiene gli elementi principali */
	protected Busta bustaErroreHeaderIntestazione = null;
	public Busta getBustaErroreHeaderIntestazione() {
		return this.bustaErroreHeaderIntestazione;
	}
	
	/** Stato */
	private IState state;
	
	/** Context */
	private Context context;
	
	/**
	 * Costruttore
	 *
	 * @param aMsg Messaggio da validare.
	 * @throws ProtocolException 
	 * 
	 */
	public ValidazioneSintattica(Context context, IState state,OpenSPCoop2Message aMsg,boolean readQualifiedAttribute, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		this(context,state,aMsg,Configurazione.getLibraryLog(),readQualifiedAttribute, protocolFactory);
	}
	public ValidazioneSintattica(Context context, IState state,OpenSPCoop2Message aMsg, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		this(context,state,aMsg,Configurazione.getLibraryLog(),false, protocolFactory);
	}
	public ValidazioneSintattica(Context context, IState state,OpenSPCoop2Message aMsg,Logger alog, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		this(context, state,aMsg,alog,false, protocolFactory);
	}
	
	/**
	 * Costruttore
	 *
	 * @param aMsg Messaggio da validare.
	 * @throws ProtocolException 
	 * 
	 */
	public ValidazioneSintattica(Context context, IState state,OpenSPCoop2Message aMsg, Busta busta, Boolean isRichiesta, Logger alog, boolean readQualifiedAttribute, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		this(context, state,aMsg, alog, readQualifiedAttribute, protocolFactory);
		this.busta = busta;
		this.isRichiesta = isRichiesta;
	}
	
	public ValidazioneSintattica(Context context, IState state,OpenSPCoop2Message aMsg,Logger alog,boolean readQualifiedAttribute, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		this.state = state;
		this.context = context;
		this.msg = aMsg;
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(ValidazioneSintattica.class.getName());
		}
		this.readQualifiedAttribute = readQualifiedAttribute;
		this.protocolFactory = protocolFactory;
		this.protocolManager = this.protocolFactory.createProtocolManager();
	}


	public void updateMsg(OpenSPCoop2Message msg) throws ProtocolException {
		String msgClassName = msg.getClass().getName()+ "";
		boolean update = !msgClassName.equals(this.msg.getClass().getName());
		if(update){
			this.msg = msg;
			if(this.headerProtocollo!=null){
				org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica<?> validazioneSintattica = this.protocolFactory.createValidazioneSintattica(this.state);
				this.headerProtocollo = validazioneSintattica.getBustaRawContent_senzaControlli(this.msg);
			}
		}
	}

	public IProtocolFactory<?> getProtocolFactory(){
		return this.protocolFactory;
	}
	
	/**
	 * Oggetto Busta contenente i valori della busta ricevuta.
	 *
	 * @return busta.
	 * 
	 */
	public Busta getBusta(){
		return this.busta;
	}

	/**
	 * Ritorna un List contenente eventuali eccezioni di validazione riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.List<Eccezione> getEccezioniValidazione(){
		return this.erroriValidazione;
	}
	/**
	 * Ritorna un List contenente eventuali eccezioni di processamento riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.List<Eccezione> getEccezioniProcessamento(){
		return this.erroriProcessamento;
	}
	public String getErroreProcessamento_internalMessage() {
		return this.erroreProcessamento_internalMessage;
	}
	
	/**
	 * Ritorna un List contenente eventuali eccezioni riscontrate nella busta durante il processo di validazione.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.List<Eccezione> getErroriTrovatiSullaListaEccezioni(){
		return this.errorsTrovatiSullaListaEccezioni;
	}

	/**
	 * In caso di avvenuto errore durante il processo di validazione, 
	 * questo metodo ritorna il motivo dell'errore.
	 *
	 * @return motivo dell'errore (se avvenuto).
	 * 
	 */
	public ErroreCooperazione getErrore(){
		return this.errore;
	}
	public IntegrationFunctionError getErrore_integrationFunctionError() {
		return this.errore_integrationFunctionError;
	}


	/**
	 * Metodo che effettua una validazione sintattica di una busta. 
	 * Il metodo, oltre ad effettuare la validazione della busta, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *   {@link Eccezione}, e viene inserito nel List <var>errors</var>.
	 *
	 * @return return true in caso di validazione sintattica riuscita (con o senza anomalie), false in caso di errori di processamento.
	 * 
	 */
	public boolean valida(){
		// Recupero l'oggetto Busta
		
		
		ValidazioneSintatticaResult<?> result = null;
		org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica<?> validazioneSintattica = null;
		boolean protocolException = false;
		try {
			validazioneSintattica = this.protocolFactory.createValidazioneSintattica(this.state);
			validazioneSintattica.setContext(this.context);
			ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
			pValidazioneErrori.setIgnoraEccezioniNonGravi(this.protocolManager.isIgnoraEccezioniNonGravi());
			if(this.isRichiesta==null || this.isRichiesta){
				result = validazioneSintattica.validaRichiesta(this.msg, this.busta, pValidazioneErrori);
			}
			else{
				result = validazioneSintattica.validaRisposta(this.msg, this.busta, pValidazioneErrori);
			}
			
		} catch (ProtocolException e) {
			protocolException = true;
		} 
		
		if(result != null){
			if(result.getBusta() != null)
				this.busta = result.getBusta();
			
			if(result.getBustaErrore()!= null)
				this.bustaErroreHeaderIntestazione = result.getBustaErrore();
			
			this.errore = result.getErrore();
			this.errore_integrationFunctionError = result.getErrore_integrationFunctionError();
			
			this.erroriProcessamento = result.getErroriProcessamento();
			if(this.erroriProcessamento == null) 
				this.erroriProcessamento = new java.util.ArrayList<Eccezione>();
			this.erroreProcessamento_internalMessage = result.getErroreProcessamento_internalMessage();
			
			this.erroriValidazione = result.getErroriValidazione();
			if(this.erroriValidazione == null) 
				this.erroriValidazione = new java.util.ArrayList<Eccezione>();
			
			this.errorsTrovatiSullaListaEccezioni = result.getErrorsTrovatiSullaListaEccezioni();
			if(this.errorsTrovatiSullaListaEccezioni == null) 
				this.errorsTrovatiSullaListaEccezioni = new java.util.ArrayList<Eccezione>();
			
			if(result.getBustaRawContent() != null)
				this.headerProtocollo = result.getBustaRawContent();
			
			return result.isValido();
		}
		else {
			if(protocolException) {
				if(this.isRichiesta==null || this.isRichiesta){
					this.errore_integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				else {
					this.errore_integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
				}
			}
		}
		
		return false;
		
	}

	
	
	
	/**
	 * Controlla che esista una busta nell'header
	 * 
	 * @return true se esiste un protocollo, false altrimenti
	 */
	public boolean verifyProtocolPresence(TipoPdD tipoPdD, ProfiloDiCollaborazione profilo, RuoloMessaggio ruoloMessaggio) throws Exception{
		return this.protocolFactory.createValidazioneSintattica(this.state).verifyProtocolPresence(tipoPdD, profilo, ruoloMessaggio, this.msg);
	}
	

	/**
	 * Metodo che si occupa di validare il Fault presente in un Messaggio Errore.
	 *
	 * @param msg Messaggio contenente il Fault.
	 * @throws ProtocolException 
	 * 
	 */
	public void validazioneFault(OpenSPCoop2Message msg) throws ProtocolException{
		ValidazioneSintatticaResult<?> result = this.protocolFactory.createValidazioneSintattica(this.state).validazioneFault(msg);
		if(result != null){
			if(result.getBusta() != null)
				this.busta = result.getBusta();
			if(result.getBustaErrore()!= null)
				this.bustaErroreHeaderIntestazione = result.getBustaErrore();
			
			this.errore = result.getErrore();
			
			this.erroriProcessamento = result.getErroriProcessamento();
			if(this.erroriProcessamento == null) 
				this.erroriProcessamento = new java.util.ArrayList<Eccezione>();
			this.erroreProcessamento_internalMessage = result.getErroreProcessamento_internalMessage();
			
			this.erroriValidazione = result.getErroriValidazione();
			if(this.erroriValidazione == null) 
				this.erroriValidazione = new java.util.ArrayList<Eccezione>();
			
			this.errorsTrovatiSullaListaEccezioni = result.getErrorsTrovatiSullaListaEccezioni();
			if(this.errorsTrovatiSullaListaEccezioni == null) 
				this.errorsTrovatiSullaListaEccezioni = new java.util.ArrayList<Eccezione>();

			if(result.getBustaRawContent() != null)
				this.headerProtocollo = result.getBustaRawContent();
		}
	}

	/**
	 * Metodo che si occupa di validare il Manifest di un SoapBody.
	 *
	 * @param msg SOAPMessage contenente il Manifest.
	 * @throws ProtocolException 
	 * 
	 */
	public void validazioneManifestAttachments(OpenSPCoop2Message msg,ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{
		ValidazioneSintatticaResult<?> result = this.protocolFactory.createValidazioneSintattica(this.state).validazioneManifestAttachments(msg, proprietaManifestAttachments);
		if(result != null){
			if(result.getBusta() != null)
				this.busta = result.getBusta();
			if(result.getBustaErrore()!= null)
				this.bustaErroreHeaderIntestazione = result.getBustaErrore();
			
			this.errore = result.getErrore();
			
			this.erroriProcessamento = result.getErroriProcessamento();
			if(this.erroriProcessamento == null) 
				this.erroriProcessamento = new java.util.ArrayList<Eccezione>();
			this.erroreProcessamento_internalMessage = result.getErroreProcessamento_internalMessage();
			
			this.erroriValidazione = result.getErroriValidazione();
			if(this.erroriValidazione == null) 
				this.erroriValidazione = new java.util.ArrayList<Eccezione>();
			
			this.errorsTrovatiSullaListaEccezioni = result.getErrorsTrovatiSullaListaEccezioni();
			if(this.errorsTrovatiSullaListaEccezioni == null) 
				this.errorsTrovatiSullaListaEccezioni = new java.util.ArrayList<Eccezione>();
	
			if(result.getBustaRawContent() != null)
				this.headerProtocollo = result.getBustaRawContent();
		}
	}
	
	public BustaRawContent<?> getHeaderProtocollo_senzaControlli() throws ProtocolException{
		return this.protocolFactory.createValidazioneSintattica(this.state).getBustaRawContent_senzaControlli(this.msg);
	}

}
