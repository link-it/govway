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



package org.openspcoop2.pdd.services;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.SoapUtilsBuildParameter;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
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

	private static final OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Data di ingresso al servizio */
	protected Date dataIngressoRichiesta;
	public Date getDataIngressoRichiesta() {
		return this.dataIngressoRichiesta;
	}

	/** PdDContext pddContext */
	protected PdDContext pddContext;
	public PdDContext getPddContext() {
		return this.pddContext;
	}
	
	/** URLProtocolContext */
	private URLProtocolContext urlProtocolContext;
	
	/** ID */
	private String idMessage;
	
	/** ID Modulo Richiedente */
	private String idModulo;
	/** identitaPdD */
	protected IDSoggetto identitaPdD;
	
	/** Messaggio di Richiesta */
	private OpenSPCoop2Message messageRequest;
	/** Messaggio di Richiesta inserito come byte */
	private byte[] messageRequest_ByteArray;
	
	/** Messaggio di Risposta */
	private OpenSPCoop2Message messageResponse;
	/** Messaggio di Risposta inserito come byte */
	private byte[] messageResponse_ByteArray;
	/** GestioneRisposta */
	private boolean gestioneRisposta;
	
	/** Credenziali */
	private Credenziali credenziali ;
	/** LocationSource */
	private String fromLocation;
	/** SOAP Action */
	private String soapAction = null;
	/** Informazioni protocollo */
	private ProtocolContext protocol;
	/** Informazioni di integrazione */
	private IntegrationContext integrazione;
	/** Tipologia di Porta*/
	private TipoPdD tipoPorta;
	/** MsgDiagnostico */
	private MsgDiagnostico msgDiagnostico;

	/** Header di trasporto per l'Integrazione */
	private java.util.Properties headerIntegrazioneRisposta;

	/** NotifierInputStreamParameters */
	private NotifierInputStreamParams notifierInputStreamParams;
	
	private IDService idModuloAsIDService;
	
	/** Costruttore */
	public AbstractContext(IDService idModuloAsIDService, Date dataIngressoRichiesta,IDSoggetto identitaPdD) throws UniqueIdentifierException{
		this.pddContext = new PdDContext();
		this.pddContext.addObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID,UniqueIdentifierManager.newUniqueIdentifier().getAsString());
		this.dataIngressoRichiesta = dataIngressoRichiesta;
		this.identitaPdD = identitaPdD;
		this.idModuloAsIDService = idModuloAsIDService;
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
	
	/**
	 * Ritorna il messaggio costruito attraverso l'interpretazione dei byte salvati precedentemente
	 * con il metodo setMessageRequestAsByte
	 * @return Ritorna il messaggio costruito attraverso l'interpretazione dei byte salvati precedentemente
	 * con il metodo setMessageRequestAsByte
	 */
	public OpenSPCoop2Message getMessageRequestFromByte() throws Exception {
		OpenSPCoop2Message requestSOAPMessage = null;
		try{
			OpenSPCoop2MessageParseResult pr = SoapUtils.build(new SoapUtilsBuildParameter(this.messageRequest_ByteArray,false,
					AbstractContext.openspcoopProperties.isDeleteInstructionTargetMachineXml(), 
					AbstractContext.openspcoopProperties.isFileCacheEnable(), 
					AbstractContext.openspcoopProperties.getAttachmentRepoDir(), 
					AbstractContext.openspcoopProperties.getFileThreshold()),
					this.getNotifierInputStreamParams());
			if(pr.getParseException()!=null){
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
			}
			requestSOAPMessage = pr.getMessage_throwParseException();
			return requestSOAPMessage;
		}catch(Exception e){
			throw new Exception("Struttura del messaggio di richiesta Soap, per la ricostruzione, non valida: "+e.getMessage());
		}	
	}
	/**
	 * Ritorna il messaggio in byte
	 * 
	 * @return Ritorna il messaggio in byte
	 */
	public byte[] getMessageRequestAsByte() throws Exception {
		return this.messageRequest_ByteArray;
	}
	
	/**
	 * Imposta il messaggio in byte
	 * 
	 */
	public void setMessageRequestAsByte(byte[] messageRequestByte) {
		this.messageRequest_ByteArray = messageRequestByte;
	}
	
	/**
	 * Imposta il messaggio nei byte
	 * 
	 */
	public void setMessageRequestAsByte(OpenSPCoop2Message messageRequestByte) throws Exception{
		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			messageRequestByte.writeTo(out,false);
			this.messageRequest_ByteArray = out.toByteArray();
		}catch(Exception e){
			throw new Exception("Struttura del messaggio di richiesta Soap, per il salvataggio, non valida: "+e.getMessage());
		}		
	}

	/**
	 * Ritorna il messaggio costruito attraverso l'interpretazione dei byte salvati precedentemente
	 * con il metodo setMessageResponseAsByte
	 * @return Ritorna il messaggio costruito attraverso l'interpretazione dei byte salvati precedentemente
	 * con il metodo setMessageResponseAsByte
	 */
	public OpenSPCoop2Message getMessageResponseFromByte() throws Exception {
		OpenSPCoop2Message responseSOAPMessage = null;
		try{
			OpenSPCoop2MessageParseResult pr = SoapUtils.build(new SoapUtilsBuildParameter(this.messageResponse_ByteArray,false,
					AbstractContext.openspcoopProperties.isDeleteInstructionTargetMachineXml(), 
					AbstractContext.openspcoopProperties.isFileCacheEnable(), 
					AbstractContext.openspcoopProperties.getAttachmentRepoDir(), 
					AbstractContext.openspcoopProperties.getFileThreshold()),
					this.getNotifierInputStreamParams());
			if(pr.getParseException()!=null){
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
			}
			responseSOAPMessage = pr.getMessage_throwParseException();
			return responseSOAPMessage;
		}catch(Exception e){
			throw new Exception("Struttura del messaggio di risposta Soap, per la ricostruzione non valida: "+e.getMessage());
		}		
	}
	/**
	 * Ritorna il messaggio in byte
	 * 
	 * @return Ritorna il messaggio in byte
	 */
	public byte[] getMessageResponseAsByte() throws Exception {
		return this.messageResponse_ByteArray;
	}
	
	/**
	 * Imposta il messaggio in byte
	 * 
	 */
	public void setMessageResponseAsByte(byte[] messageResponseByte) {
		this.messageResponse_ByteArray = messageResponseByte;
	}
	
	/**
	 * Imposta il messaggio nei byte
	 * 
	 */
	public void setMessageResponseAsByte(OpenSPCoop2Message messageResponseByte) throws Exception{
		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			messageResponseByte.writeTo(out,false);
			this.messageResponse_ByteArray = out.toByteArray();
		}catch(Exception e){
			throw new Exception("Struttura del messaggio di risposta Soap, per il salvataggio, non valida: "+e.getMessage());
		}		
	}

	/**
	 * @return the fromLocation
	 */
	public String getFromLocation() {
		return this.fromLocation;
	}

	/**
	 * @param fromLocation the fromLocation to set
	 */
	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	
	public String getSoapAction() {
		return this.soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
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
	public java.util.Properties getHeaderIntegrazioneRisposta() {
		return this.headerIntegrazioneRisposta;
	}

	/**
	 * @param headerIntegrazione the headerIntegrazione to set
	 */
	public void setHeaderIntegrazioneRisposta(java.util.Properties headerIntegrazione) {
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
}

