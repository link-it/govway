/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.connettori;

import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.handlers.OutRequestContext;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * Classe utilizzata per rappresentare le informazioni utilizzata da un generico connettore.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreMsg  {


	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Tipo di Connettore */
	private String tipoConnector;
	/** Messaggio da spedire */
	private OpenSPCoop2Message request;
	/** Proprieta' del connettore */
	private java.util.Hashtable<String,String> properties;


	/** Indicazione su di un eventuale sbustamento SOAP */
	private boolean sbustamentoSoap;
	/** Indicazione su di un eventuale sbustamento delle informazioni di protocollo */
	private boolean sbustamentoInformazioniProtocollo; 
	/** Proprieta' di trasporto che deve utilizzare il connettore */
	private java.util.Properties propertiesTrasporto;
	/** Proprieta' urlBased che deve utilizzare il connettore */
	private java.util.Properties propertiesUrlBased;
	/** Tipo di Autenticazione */
	private String tipoAutenticazione;
	/** Credenziali per l'autenticazione */
	private InvocazioneCredenziali credenziali;
	/** Busta */
	private Busta busta;
	/** Identificativo Modulo che utilizza il connettore */
	private String idModulo;
	/** ProtocolFactory */
	private IProtocolFactory<?> protocolFactory;
	/** Attributi per lo sbustamento del protocollo */
	private boolean gestioneManifest = false;
	private ProprietaManifestAttachments proprietaManifestAttachments = null;
	/** Eventuale header sbustato contenente le informazioni di protocollo */
	private BustaRawContent<?> soapProtocolInfo = null;
	/** Informazione sulla modalita LocalForward */
	private boolean localForward = false;
	/** Indicazione se il messaggio in consegna riguarda una richiesta o una risposta
	 * Il set di questo field, potrà essere usato per la risposta di una invocazione sincrona con consegna su I.M. */
	private RuoloMessaggio ruoloMessaggio = RuoloMessaggio.RICHIESTA;
	
	/** OutRequestContext */
	private OutRequestContext outRequestContext;
	/** MessaggioDiagnostico */
	private MsgDiagnostico msgDiagnostico;
	/** IState */
	private IState state;
	


	/* ********  C O S T R U T T O R E  ******** */
	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public ConnettoreMsg(){}
	/**
	 * Costruttore. 
	 *
	 * @param type Tipo di Connettore
	 * @param r Messaggio di Richiesta
	 * @param p Proprieta' del Connettore
	 * 
	 */
	public ConnettoreMsg(String type,OpenSPCoop2Message r,
			Property[] p){
		java.util.Hashtable<String,String> pr = new java.util.Hashtable<String,String>();
		if(p!=null){
			for(int i=0; i<p.length; i++){
				if(p[i]!=null){
					pr.put(p[i].getNome(),p[i].getValore());
				}
			}
		}
		this.tipoConnector = type;
		this.request = r;
		this.properties = pr;
	}
	/**
	 * Costruttore. 
	 *
	 * @param type Tipo di Connettore
	 * @param r Messaggio di Richiesta
	 * @param p Proprieta' del Connettore
	 * 
	 */
	public ConnettoreMsg(String type,OpenSPCoop2Message r,
			java.util.Hashtable<String,String> p){
		this(type,r,p,false,null,null,true);
	}
	/**
	 * Costruttore. 
	 *
	 * @param type Tipo di Connettore
	 * @param r Messaggio di Richiesta
	 * @param p Proprieta' del Connettore
	 * @param sb Indicazione su di un eventuale sbustamento SOAP
	 * @param tipoAuth Tipo di Autenticazione associata al connettore
	 * @param cr Credenziali Associate al connettore
	 * 
	 */
	public ConnettoreMsg(String type,OpenSPCoop2Message r,
			Property[] p,boolean sb,
			String tipoAuth,InvocazioneCredenziali cr,
			boolean isRichiesta){
		this(type,r,new java.util.Hashtable<String,String>() ,sb,tipoAuth,cr,isRichiesta);

		java.util.Hashtable<String,String> pr = new java.util.Hashtable<String,String>();
		if(p!=null){
			for(int i=0; i<p.length; i++){
				if(p[i]!=null){
					pr.put(p[i].getNome(),p[i].getValore());
				}
			}
		}
		this.properties = pr;
	}
	/**
	 * Costruttore. 
	 *
	 * @param type Tipo di Connettore
	 * @param r Messaggio di Richiesta
	 * @param p Proprieta' del Connettore
	 * @param sb Indicazione su di un eventuale sbustamento SOAP
	 * @param tipoAuth Tipo di Autenticazione associata al connettore
	 * @param cr Credenziali Associate al connettore
	 * 
	 */
	public ConnettoreMsg(String type,OpenSPCoop2Message r,
			java.util.Hashtable<String,String> p,boolean sb,
			String tipoAuth,InvocazioneCredenziali cr,
			boolean isRichiesta){

		this.tipoConnector = type;
		this.request = r;
		this.properties = p;
		this.sbustamentoSoap = sb;
		this.tipoAutenticazione = tipoAuth;
		this.credenziali = cr;
	}




	/* ********  S E T T E R   ******** */

	/**
	 * Imposta il tipo di connettore.
	 *
	 * @param tipo Tipo di Connettore.
	 * 
	 */    
	public void setTipoConnettore(String tipo) {
		this.tipoConnector = tipo;
	}
	/**
	 * Imposta il messaggio da spedire.
	 *
	 * @param r Messaggio da Spedire
	 * 
	 */   
	public void setRequestMessage(OpenSPCoop2Message r) {
		this.request = r;
	}
	/**
	 * Imposta le proprieta' del connettore.
	 *
	 * @param p Proprieta' del Connettore
	 * 
	 */   
	public void setConnectorProperties(java.util.Hashtable<String,String> p) {
		this.properties = p;
	}
	/**
	 * Imposta l'indicazione su di un eventuale sbustamento SOAP.
	 *
	 * @param sb indicazione su di un eventuale sbustamento SOAP.
	 * 
	 */    
	public void setSbustamentoSOAP(boolean sb) {
		this.sbustamentoSoap = sb;
	}
	
	/**
	 * Imposta il tipo di autenticazione associato al connettore
	 *
	 * @param tipoAuth tipo di autenticazione.
	 * 
	 */
	public void setAutenticazione(String tipoAuth){
		this.tipoAutenticazione = tipoAuth;
	}
	/**
	 * Imposta le credenziali associate all'autenticazione utilizzata dal connettore
	 *
	 * @param cr Credenziali
	 * 
	 */
	public void setCredenziali(InvocazioneCredenziali cr){
		this.credenziali = cr;
	}
	


	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna il tipo di connettore.
	 *
	 * @return Tipo di Connettore.
	 * 
	 */    
	public String getTipoConnettore() {
		return this.tipoConnector;
	}
	/**
	 * Ritorna il messaggio da spedire.
	 *
	 * @return Messaggio da Spedire
	 * 
	 */  
	private boolean sbustamentoProtocolInfoEffettuato = false;
	private boolean checkPresenzaHeaderPrimaSbustamento = false;
	public void setCheckPresenzaHeaderPrimaSbustamento(boolean checkPresenzaHeaderPrimaSbustamento) {
		this.checkPresenzaHeaderPrimaSbustamento = checkPresenzaHeaderPrimaSbustamento;
	}
	public OpenSPCoop2Message getRequestMessage(RequestInfo requestInfo) throws ProtocolException {
		if(!this.localForward && this.sbustamentoInformazioniProtocollo && !this.sbustamentoProtocolInfoEffettuato){
			boolean protocolloPresente = true;
			if(this.checkPresenzaHeaderPrimaSbustamento){
				try{
					protocolloPresente = this.protocolFactory.createValidazioneSintattica(this.state).
						verifyProtocolPresence(this.outRequestContext.getTipoPorta(), this.busta.getProfiloDiCollaborazione(), this.ruoloMessaggio, this.request);
				}catch(Exception e){}
			}
			if(protocolloPresente){
				org.openspcoop2.protocol.engine.builder.Sbustamento sbustatore = 
						new org.openspcoop2.protocol.engine.builder.Sbustamento(this.protocolFactory,this.state);
					ProtocolMessage protocolMessage = sbustatore.sbustamento(this.request,this.busta,
							this.ruoloMessaggio,this.gestioneManifest,this.proprietaManifestAttachments,
							FaseSbustamento.PRE_CONSEGNA_RICHIESTA, requestInfo);
					this.soapProtocolInfo = protocolMessage.getBustaRawContent();
					this.request = protocolMessage.getMessage(); // updated
					this.sbustamentoProtocolInfoEffettuato = true;
			}
		}
		return this.request;
	}
	/**
	 * Ritorna le proprieta' del connettore.
	 *
	 * @return Proprieta' del Connettore
	 * 
	 */   
	public java.util.Hashtable<String,String> getConnectorProperties() {
		return this.properties;
	}
	/**
	 * Ritorna l'indicazione su di un eventuale sbustamento SOAP.
	 *
	 * @return indicazione su di un eventuale sbustamento SOAP.
	 * 
	 */    
	public boolean isSbustamentoSOAP() {
		return this.sbustamentoSoap;
	}
	
	/**
	 * Ritorna il tipo di autenticazione associato al connettore
	 *
	 * @return tipo di autenticazione.
	 * 
	 */
	public String getAutenticazione(){
		return this.tipoAutenticazione;
	}
	/**
	 * Ritorna le credenziali associate all'autenticazione utilizzata dal connettore
	 *
	 * @return Credenziali
	 * 
	 */
	public InvocazioneCredenziali getCredenziali(){
		return this.credenziali;
	}
	/**
	 * @return the propertiesTrasporto
	 */
	public java.util.Properties getPropertiesTrasporto() {
		return this.propertiesTrasporto;
	}
	/**
	 * @return the propertiesUrlBased
	 */
	public java.util.Properties getPropertiesUrlBased() {
		return this.propertiesUrlBased;
	}
	/**
	 * @param propertiesTrasporto the propertiesTrasporto to set
	 */
	public void setPropertiesTrasporto(java.util.Properties propertiesTrasporto) {
		this.propertiesTrasporto = propertiesTrasporto;
	}
	/**
	 * @param propertiesUrlBased the propertiesUrlBased to set
	 */
	public void setPropertiesUrlBased(java.util.Properties propertiesUrlBased) {
		this.propertiesUrlBased = propertiesUrlBased;
	}
	/**
	 * @return the busta
	 */
	public Busta getBusta() {
		return this.busta;
	}
	/**
	 * @param busta the busta to set
	 */
	public void setBusta(Busta busta) {
		this.busta = busta;
	}

	public String getIdModulo() {
		return this.idModulo;
	}
	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}
	
	public boolean isSbustamentoInformazioniProtocollo() {
		return this.sbustamentoInformazioniProtocollo;
	}
	public void setSbustamentoInformazioniProtocollo(
			boolean sbustamentoInformazioniProtocollo) {
		this.sbustamentoInformazioniProtocollo = sbustamentoInformazioniProtocollo;
	}
	public RuoloMessaggio getRuoloMessaggio() {
		return this.ruoloMessaggio;
	}
	public void setRuoloMessaggio(RuoloMessaggio ruoloMessaggio) {
		this.ruoloMessaggio = ruoloMessaggio;
	}
	public void setProtocolFactory(IProtocolFactory<?> protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	
	public void setGestioneManifest(boolean gestioneManifest) {
		this.gestioneManifest = gestioneManifest;
	}
	public void setProprietaManifestAttachments(
			ProprietaManifestAttachments proprietaManifestAttachments) {
		this.proprietaManifestAttachments = proprietaManifestAttachments;
	}
	public BustaRawContent<?> getSoapProtocolInfo() {
		return this.soapProtocolInfo;
	}
	public boolean isLocalForward() {
		return this.localForward;
	}
	public void setLocalForward(boolean localForward) {
		this.localForward = localForward;
	}
	public OutRequestContext getOutRequestContext() {
		return this.outRequestContext;
	}
	public void setOutRequestContext(OutRequestContext outRequestContext) {
		this.outRequestContext = outRequestContext;
	}
	public MsgDiagnostico getMsgDiagnostico() {
		return this.msgDiagnostico;
	}
	public void setMsgDiagnostico(MsgDiagnostico msgDiagnostico) {
		this.msgDiagnostico = msgDiagnostico;
	}
	public IState getState() {
		return this.state;
	}
	public void setState(IState state) {
		this.state = state;
	}
}
