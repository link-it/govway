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



package org.openspcoop2.pdd.services.core;

import java.util.Date;
import java.util.Map;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.utils.id.UniqueIdentifierException;
import org.openspcoop2.utils.id.UniqueIdentifierManager;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

/**
 * Contesto di attivazione dei servizi
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractContext implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Data di accettazione richiesta */
	protected Date dataAccettazioneRichiesta;
	public Date getDataAccettazioneRichiesta() {
		return this.dataAccettazioneRichiesta;
	}
	
	/** Data di ingresso richiesta */
	protected Date dataIngressoRichiesta;
	public void setDataIngressoRichiesta(Date dataIngressoRichiesta) {
		this.dataIngressoRichiesta = dataIngressoRichiesta;
	}
	public Date getDataIngressoRichiesta() {
		return this.dataIngressoRichiesta;
	}

	/** PdDContext pddContext */
	protected PdDContext pddContext;
	public PdDContext getPddContext() {
		return this.pddContext;
	}
	
	/** RequestInfo */
	protected RequestInfo requestInfo;
	
	/** URLProtocolContext */
	private URLProtocolContext urlProtocolContext;
	
	/** ID */
	private String idMessage;
	
	/** ID Modulo Richiedente */
	private IDService idModuloAsIDService;
	private String idModulo;
	
	/** identitaPdD */
	protected IDSoggetto identitaPdD;
	
	/** Messaggio di Richiesta */
	private OpenSPCoop2Message messageRequest;
	
	/** Messaggio di Risposta */
	private OpenSPCoop2Message messageResponse;
	
	/** GestioneRisposta */
	private boolean gestioneRisposta;
	
	/** Credenziali */
	private Credenziali credenziali ;
	/** LocationSource */
	protected String fromLocation;
	/** Informazioni protocollo */
	private ProtocolContext protocol;
	/** Informazioni di integrazione */
	private IntegrationContext integrazione;
	/** Tipologia di Porta*/
	private TipoPdD tipoPorta;
	/** MsgDiagnostico */
	private MsgDiagnostico msgDiagnostico;

	/** Header di trasporto per l'Integrazione */
	private Map<String, String> headerIntegrazioneRisposta;

	/** NotifierInputStreamParameters */
	private NotifierInputStreamParams notifierInputStreamParams;
	
	/** Costruttore */
	public AbstractContext(IDService idModuloAsIDService, Date dataAccettazioneRichiesta,RequestInfo requestInfo) throws UniqueIdentifierException{
		this.pddContext = new PdDContext();
		this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE,UniqueIdentifierManager.newUniqueIdentifier().getAsString());
		this.dataAccettazioneRichiesta = dataAccettazioneRichiesta;
		this.identitaPdD = requestInfo.getIdentitaPdD();
		this.idModuloAsIDService = idModuloAsIDService;
		this.requestInfo = requestInfo;
	}
	protected AbstractContext(IDService idModuloAsIDService){
		this.idModuloAsIDService = idModuloAsIDService;
	}
	
	public IDService getIdModuloAsIDService() {
		return this.idModuloAsIDService;
	}
	
	/**
	 * ID Modulo Richiedente
	 * 
	 * @return ID Modulo Richiedente
	 */
	public String getIdModulo() {
		return this.idModulo;
	}

	/**
	 * Imposta l'ID Modulo Richiedente
	 * 
	 * @param idModulo ID Modulo Richiedente
	 */
	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}

	
	/**
	 * Messaggio di Richiesta
	 * 
	 * @return Messaggio di Richiesta
	 */
	public OpenSPCoop2Message getMessageRequest() {
		return this.messageRequest;
	}
	/**
	 * Messaggio di Risposta
	 * 
	 * @return Messaggio di Risposta
	 */
	public OpenSPCoop2Message getMessageResponse() {
		return this.messageResponse;
	}

	/**
	 * Imposta il Messaggio di Richiesta
	 * 
	 * @param message Messaggio di Richiesta
	 */
	public void setMessageRequest(OpenSPCoop2Message message) {
		this.messageRequest = message;
	}
	/**
	 * Imposta il Messaggio di Risposta
	 * 
	 * @param messageResponse Messaggio di Risposta
	 */
	public void setMessageResponse(OpenSPCoop2Message messageResponse) {
		this.messageResponse = messageResponse;
	}

	/**
	 * Credenziali
	 * 
	 * @return Credenziali
	 */
	public Credenziali getCredenziali() {
		return this.credenziali;
	}
	
	/**
	 * Imposta le credenziali
	 * 
	 * @param credenziali Credenziali da impostare
	 */
	public void setCredenziali(Credenziali credenziali) {
		this.credenziali = credenziali;
	}
	/**
	 * Indicazione se deve essere aspettata una risposta applicativa
	 * 
	 * @return Indicazione se deve essere aspettata una risposta applicativa
	 */
	public boolean isGestioneRisposta() {
		return this.gestioneRisposta;
	}

	/**
	 * Imposta l'indicazione se deve essere aspettata una risposta applicativa
	 * 
	 * @param gestioneRisposta indicazione se deve essere aspettata una risposta applicativa
	 */
	public void setGestioneRisposta(boolean gestioneRisposta) {
		this.gestioneRisposta = gestioneRisposta;
	}

	public String getSourceLocation() {
		return this.requestInfo!=null && 
				this.requestInfo.getProtocolContext()!=null ? 
					this.requestInfo.getProtocolContext().getSource(): null;
	}
	
	public MsgDiagnostico getMsgDiagnostico() {
		return this.msgDiagnostico;
	}
	public void setMsgDiagnostico(MsgDiagnostico msgDiagnostico) {
		this.msgDiagnostico = msgDiagnostico;
	}
	
	public String getIdMessage() {
		return this.idMessage;
	}

	public void setIdMessage(String id) {
		this.idMessage = id;
	}

	public ProtocolContext getProtocol() {
		return this.protocol;
	}

	public void setProtocol(ProtocolContext p) {
		this.protocol = p;
	}

	public IntegrationContext getIntegrazione() {
		return this.integrazione;
	}

	public void setIntegrazione(IntegrationContext integrazione) {
		this.integrazione = integrazione;
	}

	public TipoPdD getTipoPorta() {
		return this.tipoPorta;
	}

	public void setTipoPorta(TipoPdD tipoPorta) {
		this.tipoPorta = tipoPorta;
	}


	/**
	 * @return the headerIntegrazione
	 */
	public Map<String, String> getHeaderIntegrazioneRisposta() {
		return this.headerIntegrazioneRisposta;
	}

	/**
	 * @param headerIntegrazione the headerIntegrazione to set
	 */
	public void setHeaderIntegrazioneRisposta(Map<String, String> headerIntegrazione) {
		this.headerIntegrazioneRisposta = headerIntegrazione;
	}

	public IDSoggetto getIdentitaPdD() {
		return this.identitaPdD;
	}

	public void setIdentitaPdD(IDSoggetto identitaPdD) {
		this.identitaPdD = identitaPdD;
	}
	public URLProtocolContext getUrlProtocolContext() {
		return this.urlProtocolContext;
	}
	public void setUrlProtocolContext(URLProtocolContext urlProtocolContext) {
		this.urlProtocolContext = urlProtocolContext;
	}

	public NotifierInputStreamParams getNotifierInputStreamParams() {
		return this.notifierInputStreamParams;
	}
	public void setNotifierInputStreamParams(
			NotifierInputStreamParams notifierInputStreamParams) {
		this.notifierInputStreamParams = notifierInputStreamParams;
	}
	
	public RequestInfo getRequestInfo() {
		return this.requestInfo;
	}
}

