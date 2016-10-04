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



package org.openspcoop2.protocol.engine.validator;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSintatticaResult;
import org.openspcoop2.utils.LoggerWrapperFactory;

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
	protected SOAPElement headerProtocollo;
	
	public SOAPElement getProtocolHeader(){
		return this.headerProtocollo;
	}
	
	public void setHeaderSOAP(SOAPHeader headerSOAP) {
		this.headerSOAP = headerSOAP;
	}
	/** Errori di validazione riscontrati sulla busta */
	protected java.util.Vector<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	protected java.util.Vector<Eccezione> erroriProcessamento;
	/** Errors riscontrati sulla lista eccezioni */
	protected java.util.Vector<Eccezione> errorsTrovatiSullaListaEccezioni;
	/** Busta */
	protected Busta busta;
	protected Boolean isRichiesta;
	/** Eventuale errore avvenuto durante il processo di validazione */
	protected ErroreCooperazione errore;
	/** Indicazione se leggere gli attributi qualificati */
	protected boolean readQualifiedAttribute;

	/** Logger utilizzato per debug. */
	protected org.slf4j.Logger log = null;

	protected boolean segnalazioneElementoPresentePiuVolte = false;
	private IProtocolFactory protocolFactory;
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
	
	/**
	 * Costruttore
	 *
	 * @param aMsg Messaggio da validare.
	 * @throws ProtocolException 
	 * 
	 */
	public ValidazioneSintattica(IState state,OpenSPCoop2Message aMsg,boolean readQualifiedAttribute, IProtocolFactory protocolFactory) throws ProtocolException {
		this(state,aMsg,Configurazione.getLibraryLog(),readQualifiedAttribute, protocolFactory);
	}
	public ValidazioneSintattica(IState state,OpenSPCoop2Message aMsg, IProtocolFactory protocolFactory) throws ProtocolException {
		this(state,aMsg,Configurazione.getLibraryLog(),false, protocolFactory);
	}
	public ValidazioneSintattica(IState state,OpenSPCoop2Message aMsg,Logger alog, IProtocolFactory protocolFactory) throws ProtocolException {
		this(state,aMsg,alog,false, protocolFactory);
	}
	
	/**
	 * Costruttore
	 *
	 * @param aMsg Messaggio da validare.
	 * @throws ProtocolException 
	 * 
	 */
	public ValidazioneSintattica(IState state,OpenSPCoop2Message aMsg, Busta busta, Boolean isRichiesta, Logger alog, boolean readQualifiedAttribute, IProtocolFactory protocolFactory) throws ProtocolException {
		this(state,aMsg, alog, readQualifiedAttribute, protocolFactory);
		this.busta = busta;
		this.isRichiesta = isRichiesta;
	}
	
	public ValidazioneSintattica(IState state,OpenSPCoop2Message aMsg,Logger alog,boolean readQualifiedAttribute, IProtocolFactory protocolFactory) throws ProtocolException {
		this.state = state;
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


	public IProtocolFactory getProtocolFactory(){
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
	 * Ritorna un vector contenente eventuali eccezioni di validazione riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.Vector<Eccezione> getEccezioniValidazione(){
		return this.erroriValidazione;
	}
	/**
	 * Ritorna un vector contenente eventuali eccezioni di processamento riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.Vector<Eccezione> getEccezioniProcessamento(){
		return this.erroriProcessamento;
	}

	/**
	 * Ritorna un vector contenente eventuali eccezioni riscontrate nella busta durante il processo di validazione.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.Vector<Eccezione> getErroriTrovatiSullaListaEccezioni(){
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

	/**
	 * Metodo che effettua una validazione sintattica di una busta. 
	 * Il metodo, oltre ad effettuare la validazione della busta, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *   {@link Eccezione}, e viene inserito nel Vector <var>errors</var>.
	 *
	 * @return return true in caso di validazione sintattica riuscita (con o senza anomalie), false in caso di errori di processamento.
	 * 
	 */
	public boolean valida(){
		// Recupero l'oggetto Busta
		
		
		ValidazioneSintatticaResult result = null;
		org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica validazioneSintattica = null;
		try {
			validazioneSintattica = this.protocolFactory.createValidazioneSintattica();
			ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
			pValidazioneErrori.setIgnoraEccezioniNonGravi(this.protocolManager.isIgnoraEccezioniNonGravi());
			if(this.isRichiesta==null || this.isRichiesta){
				result = validazioneSintattica.validaRichiesta(this.state,this.msg, this.busta, pValidazioneErrori);
			}
			else{
				result = validazioneSintattica.validaRisposta(this.state,this.msg, this.busta, pValidazioneErrori);
			}
			
		} catch (ProtocolException e) {
			return false;
		} finally {
			if(result != null){
				if(result.getBusta() != null)
					this.busta = result.getBusta();
				
				if(result.getBustaErrore()!= null)
					this.bustaErroreHeaderIntestazione = result.getBustaErrore();
				
				this.errore = result.getErrore();
				
				this.erroriProcessamento = result.getErroriProcessamento();
				if(this.erroriProcessamento == null) 
					this.erroriProcessamento = new java.util.Vector<Eccezione>();
				
				this.erroriValidazione = result.getErroriValidazione();
				if(this.erroriValidazione == null) 
					this.erroriValidazione = new java.util.Vector<Eccezione>();
				
				this.errorsTrovatiSullaListaEccezioni = result.getErrorsTrovatiSullaListaEccezioni();
				if(this.errorsTrovatiSullaListaEccezioni == null) 
					this.errorsTrovatiSullaListaEccezioni = new java.util.Vector<Eccezione>();
				
				if(result.getProtocolElement() != null)
					this.headerProtocollo = result.getProtocolElement();
				else
					this.headerProtocollo = validazioneSintattica.getHeaderProtocollo(this.busta);
				
				return result.isValido();
			}
		}
		
		return false;
		
	}

	
	
	
	/**
	 * Controlla che esista una busta nell'header
	 * 
	 * @return true se esiste un protocollo, false altrimenti
	 */
	public boolean verifyProtocolPresence(TipoPdD tipoPdD, ProfiloDiCollaborazione profilo, boolean isRichiesta) throws Exception{
		return this.protocolFactory.createValidazioneSintattica().verifyProtocolPresence(tipoPdD, profilo, isRichiesta, this.msg);
	}
	

	/**
	 * Metodo che si occupa di validare il Fault presente in un Messaggio Errore.
	 *
	 * @param body SOAPBody contenente il SOAPFault.
	 * @throws ProtocolException 
	 * 
	 */
	public void validazioneFault(SOAPBody body) throws ProtocolException{
		ValidazioneSintatticaResult result = this.protocolFactory.createValidazioneSintattica().validazioneFault(body);
		if(result != null){
			if(result.getBusta() != null)
				this.busta = result.getBusta();
			if(result.getBustaErrore()!= null)
				this.bustaErroreHeaderIntestazione = result.getBustaErrore();
			
			this.errore = result.getErrore();
			
			this.erroriProcessamento = result.getErroriProcessamento();
			if(this.erroriProcessamento == null) 
				this.erroriProcessamento = new java.util.Vector<Eccezione>();
			this.erroriValidazione = result.getErroriValidazione();
			if(this.erroriValidazione == null) 
				this.erroriValidazione = new java.util.Vector<Eccezione>();
			this.errorsTrovatiSullaListaEccezioni = result.getErrorsTrovatiSullaListaEccezioni();
			if(this.errorsTrovatiSullaListaEccezioni == null) 
				this.errorsTrovatiSullaListaEccezioni = new java.util.Vector<Eccezione>();

			if(result.getProtocolElement() != null)
				this.headerProtocollo = result.getProtocolElement();
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
		ValidazioneSintatticaResult result = this.protocolFactory.createValidazioneSintattica().validazioneManifestAttachments(msg, proprietaManifestAttachments);
		if(result != null){
			if(result.getBusta() != null)
				this.busta = result.getBusta();
			if(result.getBustaErrore()!= null)
				this.bustaErroreHeaderIntestazione = result.getBustaErrore();
			
			this.errore = result.getErrore();
			
			this.erroriProcessamento = result.getErroriProcessamento();
			if(this.erroriProcessamento == null) 
				this.erroriProcessamento = new java.util.Vector<Eccezione>();
			this.erroriValidazione = result.getErroriValidazione();
			if(this.erroriValidazione == null) 
				this.erroriValidazione = new java.util.Vector<Eccezione>();
			this.errorsTrovatiSullaListaEccezioni = result.getErrorsTrovatiSullaListaEccezioni();
			if(this.errorsTrovatiSullaListaEccezioni == null) 
				this.errorsTrovatiSullaListaEccezioni = new java.util.Vector<Eccezione>();
	
			if(result.getProtocolElement() != null)
				this.headerProtocollo = result.getProtocolElement();
		}
	}
	
	public SOAPElement getHeaderProtocollo_senzaControlli() throws ProtocolException{
		return this.protocolFactory.createValidazioneSintattica().getHeaderProtocollo_senzaControlli(this.msg);
	}

}
