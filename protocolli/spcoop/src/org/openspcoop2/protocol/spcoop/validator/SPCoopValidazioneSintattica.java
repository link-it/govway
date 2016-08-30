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



package org.openspcoop2.protocol.spcoop.validator;


import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.protocol.sdk.validator.StrutturaBustaException;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSintatticaResult;
import org.openspcoop2.protocol.spcoop.config.SPCoopProperties;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostantiPosizioneEccezione;
import org.openspcoop2.protocol.spcoop.utils.SPCoopUtils;
import org.openspcoop2.utils.date.DateManager;
import org.w3c.dom.Node;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.validator.IValidazioneSintattica}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopValidazioneSintattica  implements IValidazioneSintattica{

	/** Messaggio. */
	private SOAPMessage msg;
	public void setMsg(SOAPMessage msg) {
		this.msg = msg;
	}
	/** HeaderSOAP */
	private SOAPHeader headerSOAP = null;
	/** Header SPCoop */
	private SOAPHeaderElement headerEGov;
	public void setHeaderSOAP(SOAPHeader headerSOAP) {
		this.headerSOAP = headerSOAP;
	}
	/** Errori di validazione riscontrati sulla busta */
	private java.util.Vector<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	private java.util.Vector<Eccezione> erroriProcessamento;
	/** Errors riscontrati sulla lista eccezioni */
	private java.util.Vector<Eccezione> errorsTrovatiSullaListaEccezioni;
	/** Busta */
	private Busta busta;
	/** Eventuale messaggio di errore avvenuto durante il processo di validazione */
	private String msgErrore;
	/** Eventuale codice di errore avvenuto durante il processo di validazione  */
	private CodiceErroreCooperazione codiceErrore;
	/** Indicazione se leggere gli attributi qualificati */
	private boolean readQualifiedAttribute;
	public void setReadQualifiedAttribute(boolean readQualifiedAttribute) {
		this.readQualifiedAttribute = readQualifiedAttribute;
	}
	private IProtocolFactory protocolFactory;
	/** Logger utilizzato per debug. */
	private org.slf4j.Logger log = null;

	private boolean segnalazioneElementoPresentePiuVolte = false;
	
	/** Indicazione se la busta risulta scaduta */
	private boolean messaggioScaduto = false;
	
	/** bustaErroreHeaderIntestazione: generata solo quando la busta egov arrivata non contiene gli elementi principali */
	private Busta bustaErroreHeaderIntestazione = null;
	
	private List<String> tipiSoggetti = null;
	private List<String> tipiServizi = null;

	private ITraduttore traduttore = null;
	
	private SPCoopProperties spcoopProperties = null;
	
	
	/**
	 * Costruttore
	 *
	 * @param protocolFactory ProtocolFactory
	 * @throws ProtocolException 
	 * 
	 */
	public SPCoopValidazioneSintattica(IProtocolFactory protocolFactory) throws ProtocolException{
		this.log = protocolFactory.getLogger();
		this.protocolFactory = protocolFactory;
		if(this.errorsTrovatiSullaListaEccezioni == null)
			this.errorsTrovatiSullaListaEccezioni = new java.util.Vector<Eccezione>();
		if(this.erroriProcessamento == null)
			this.erroriProcessamento = new java.util.Vector<Eccezione>();
		if(this.erroriValidazione == null)
			this.erroriValidazione = new java.util.Vector<Eccezione>();
		
		this.tipiSoggetti = this.protocolFactory.createProtocolConfiguration().getTipiSoggetti();
		this.tipiServizi = this.protocolFactory.createProtocolConfiguration().getTipiServizi();
		
		this.traduttore = this.protocolFactory.createTraduttore();
		
		this.spcoopProperties = SPCoopProperties.getInstance(this.log);
	}


	public SOAPHeaderElement getHeaderEGov(SOAPMessage aMsg, boolean readQualifiedAttribute) throws ProtocolException{
		this.msg = aMsg;
		this.readQualifiedAttribute = readQualifiedAttribute;
		return getHeaderEGov();
	}
	/**
	 * Ritorna l'header SPCoop.
	 *
	 * @return header SPCoop.
	 * 
	 */
	public SOAPHeaderElement getHeaderEGov() throws ProtocolException{
		try{
			if(this.headerEGov!=null){
				return this.headerEGov;
			}else{
				if(this.headerSOAP==null){
					this.headerSOAP = this.msg.getSOAPHeader();
				}
				this.headerEGov = this.getHeaderEGov(this.headerSOAP);
				return this.headerEGov;
			}
		}catch(Exception e){
			this.log.error("ValidazioneSintattica.headerEGovAsBytes error: "+e.getMessage(),e);
			throw new ProtocolException("ValidazioneSintattica.headerEGovAsBytes error: "+e.getMessage(),e);
		}
	}



	/**
	 * Oggetto Busta SPCoop contenente i valori della busta ricevuta.
	 *
	 * @return busta SPCoop.
	 * 
	 */
	public Busta getBusta(){
		return this.busta;
	}

	/**
	 * Ritorna un vector contenente eventuali eccezioni di validazione riscontrate nella busta SPCoop.   
	 *
	 * @return Eccezioni riscontrate nella busta SPCoop.
	 * 
	 */
	public java.util.Vector<Eccezione> getEccezioniValidazione(){
		return this.erroriValidazione;
	}
	/**
	 * Ritorna un vector contenente eventuali eccezioni di processamento riscontrate nella busta SPCoop.   
	 *
	 * @return Eccezioni riscontrate nella busta SPCoop.
	 * 
	 */
	public java.util.Vector<Eccezione> getEccezioniProcessamento(){
		return this.erroriProcessamento;
	}

	/**
	 * Ritorna un vector contenente eventuali eccezioni riscontrate nella busta SPCoop durante il processo di validazione.   
	 *
	 * @return Eccezioni riscontrate nella busta SPCoop.
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
	public String getMsgErrore(){
		return this.msgErrore;
	}

	/**
	 * In caso di avvenuto errore, questo metodo ritorna il codice dell'errore.
	 *
	 * @return codice dell'errore (se avvenuto).
	 * 
	 */
	public CodiceErroreCooperazione getCodiceErrore(){
		return this.codiceErrore;
	}

	/**
	 * Ritorna l'header SPCoop.
	 *
	 * @return header SPCoop.
	 * 
	 */
	public SOAPHeaderElement getHeaderSPCoop(){
		return this.headerEGov;
	}

	/**
	 * Metodo che effettua una validazione sintattica di una busta SPCoop. 
	 * Il metodo, oltre ad effettuare la validazione della busta, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *   {@link Eccezione}, e viene inserito nel Vector <var>errors</var>.
	 *
	 * @return return true in caso di validazione sintattica riuscita (con o senza anomalie), false in caso di errori di processamento.
	 * 
	 */
	public boolean valida(){

		try{

			/** ------ CONTROLLO PRESENZA BUSTA SPCOOP ----- */

			// Estraggo header
			if(this.headerSOAP==null)
				this.headerSOAP = this.msg.getSOAPHeader();
			if(this.headerSOAP==null){
				this.msgErrore =  "Analizzato un messaggio senza header";
				this.codiceErrore = CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO;
				return false;
			}	
			// Estraggo busta SPCoop
			if(this.headerEGov==null)
				this.headerEGov = getHeaderEGov(this.headerSOAP);
			if(this.headerEGov==null){
				this.msgErrore =  "Analizzato un messaggio senza busta SPCoop";
				this.codiceErrore = CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO;
				return false;
			}	    

		} catch(StrutturaBustaException e) {
			this.log.error("Struttura della busta non corretta: "+e.getMessage(),e);
			this.msgErrore =  e.getMessage();
			this.codiceErrore = CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO;
			return false; 
		} catch(Exception e) {
			this.msgErrore = "(Validazione sintattica) "+e.getMessage();
			this.codiceErrore = CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO;
			this.busta = null;
			return false;
		}


		try{

			/** ------ VALIDAZIONE SINTATTICA -- */

			// Busta da ritornare
			Busta aBusta = new Busta(this.protocolFactory.getProtocol());
			this.busta = aBusta;
			this.erroriValidazione = new java.util.Vector<Eccezione>();
			this.erroriProcessamento = new java.util.Vector<Eccezione>();
			this.errorsTrovatiSullaListaEccezioni = new java.util.Vector<Eccezione>();

			// Prefix eGov
			String xmlns = getPrefix();

			// Controllo Actor
			//log.info("Controllo attributo Actor["+this.header.getActor()+"] ...");
			if(SPCoopCostanti.ACTOR_EGOV.equals(this.headerEGov.getActor()) == false){
				if(this.spcoopProperties.isGenerazioneBustaErrore_actorScorretto()==false){
					throw new StrutturaBustaException("Header egov con actor scorretto","Actor");
				}else{
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_ACTOR.toString());
					this.erroriValidazione.add(ecc);
				}
			}
			
			// Controllo MustUnderstand
			//log.info("Controllo attributo MustUnderstand["+this.header.getMustUnderstand()+"] ...");
			if(this.headerEGov.getMustUnderstand() == false){ 
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MUST_UNDERSTAND.toString());
				this.erroriValidazione.add(ecc);
			}
			Vector<Node> list = SoapUtils.getNotEmptyChildNodes(this.headerEGov);

			//	Controllo value prefix
			if(SPCoopCostanti.NAMESPACE_EGOV.equals(this.headerEGov.getNamespaceURI())==false){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_NAMESPACE.toString());
				this.erroriValidazione.add(ecc);
			}

			// intestazione messaggio
			Node intestazioneMsg = list.get(0);
			Vector<Node> headerMsg = SoapUtils.getNotEmptyChildNodes(intestazioneMsg);
			boolean mittenteGiaTrovato = false;
			boolean destinatarioGiaTrovato = false;
			boolean profiloCollaborazioneGiaTrovato = false;
			boolean collaborazioneGiaTrovato = false;
			boolean servizioGiaTrovato = false;
			boolean azioneGiaTrovato = false;
			boolean messaggioGiaTrovato = false;
			boolean profiloTrasmissioneGiaTrovato = false;
			boolean sequenzaGiaTrovato = false;
			for(int i =0; i<headerMsg.size();i++){
				Node child = headerMsg.get(i);
				//if((child.getNodeName().equals(xmlns+"Mittente"))){
				if(SoapUtils.matchLocalName(child, "Mittente", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
					if(mittenteGiaTrovato==false){
						validazioneMittente(child,xmlns);
						mittenteGiaTrovato = true;
					}else{
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento Mittente","Mittente");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.MITTENTE_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE.toString());
							this.erroriValidazione.add(ecc);
						}
					}
				}
				//else if((child.getNodeName().equals(xmlns+"Destinatario"))){
				else if(SoapUtils.matchLocalName(child, "Destinatario", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
					if(destinatarioGiaTrovato==false){
						validazioneDestinatario(child,xmlns);
						destinatarioGiaTrovato = true;
					}else{
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento Destinatario","Destinatario");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.DESTINATARIO_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE.toString());
							this.erroriValidazione.add(ecc);
						}
					}
				}
				//else if((child.getNodeName().equals(xmlns+"ProfiloCollaborazione"))){
				else if(SoapUtils.matchLocalName(child, "ProfiloCollaborazione", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
					if(profiloCollaborazioneGiaTrovato==false){
						validazioneProfiloCollaborazione(child,xmlns);
						profiloCollaborazioneGiaTrovato = true;
					}else{
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento ProfiloCollaborazione","ProfiloCollaborazione");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString());
							this.erroriValidazione.add(ecc);
						}
					}
				}
				//else if((child.getNodeName().equals(xmlns+"Collaborazione"))){
				else if(SoapUtils.matchLocalName(child, "Collaborazione", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
					if(collaborazioneGiaTrovato==false){
						validazioneCollaborazione(child);
						collaborazioneGiaTrovato = true;
					}else{
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento Collaborazione","Collaborazione");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.COLLABORAZIONE_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString());
							this.erroriValidazione.add(ecc);
						}
					}
				}
				//else if((child.getNodeName().equals(xmlns+"Servizio"))){
				else if(SoapUtils.matchLocalName(child, "Servizio", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
					if(servizioGiaTrovato==false){
						validazioneServizio(child,xmlns);
						servizioGiaTrovato = true;
					}else{
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento Servizio","Servizio");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.SERVIZIO_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString());
							this.erroriValidazione.add(ecc);
						}
					}
				}
				//else if((child.getNodeName().equals(xmlns+"Azione"))){
				else if(SoapUtils.matchLocalName(child, "Azione", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
					if(azioneGiaTrovato==false){
						validazioneAzione(child);
						azioneGiaTrovato = true;
					}else{
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento Azione","Azione");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.AZIONE_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString());
							this.erroriValidazione.add(ecc);
						}
					}
				}
				//else if((child.getNodeName().equals(xmlns+"Messaggio"))){
				else if(SoapUtils.matchLocalName(child, "Messaggio", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
					if(messaggioGiaTrovato==false){
						validazioneMessaggio(child,xmlns);
						messaggioGiaTrovato = true;
					}else{
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento Messaggio","Messaggio");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MESSAGGIO+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MESSAGGIO.toString());
							this.erroriValidazione.add(ecc);
						}
					}
				}
				//else if((child.getNodeName().equals(xmlns+"ProfiloTrasmissione"))){
				else if(SoapUtils.matchLocalName(child, "ProfiloTrasmissione", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
					if(profiloTrasmissioneGiaTrovato==false){
						validazioneProfiloTrasmissione(child,xmlns);
						profiloTrasmissioneGiaTrovato = true;
					}else{
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento ProfiloTrasmissione","ProfiloTrasmissione");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE.toString());
							this.erroriValidazione.add(ecc);
						}
					}
				}
				//else if((child.getNodeName().equals(xmlns+"Sequenza"))){
				else if(SoapUtils.matchLocalName(child, "Sequenza", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
					if(sequenzaGiaTrovato==false){
						validazioneSequenza(child,xmlns);
						sequenzaGiaTrovato = true;
					}else{
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento Sequenza","Sequenza");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE.toString());
							this.erroriValidazione.add(ecc);
						}
					}
				}else{
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione("Messaggio/child["+child.getNodeName()+"]");
					this.erroriValidazione.add(ecc);
				}
			}

			
			// Controllo scadenza messaggio rispetto ora registrazione
			if(this.spcoopProperties.isRepositoryBusteFiltraBusteScaduteRispettoOraRegistrazione() && (this.messaggioScaduto==false)){
				
				// Check su OraRegistrazione
				// Se correttamente formato, altrimenti segnalero' poi il problema
				Date oraRegistrazioneMinimaAccettata = new Date(DateManager.getTimeMillis()-(this.spcoopProperties.getIntervalloScadenzaBuste()*60*1000));
				/*System.out.println("BUSTA ["+this.busta.getOraRegistrazione().toString()
						+"]   MINIMA ["+oraRegistrazioneMinimaAccettata.toString()+"]");*/
				if(this.busta.getOraRegistrazione()!=null){
					if( this.busta.getOraRegistrazione().before(oraRegistrazioneMinimaAccettata)){
						this.log.error("OraRegistrazione portata nella busta ("+SPCoopUtils.getDate_eGovFormat(this.busta.getOraRegistrazione())+") più vecchia della data minima attesa ("+SPCoopUtils.getDate_eGovFormat(oraRegistrazioneMinimaAccettata)+") e scadenza non presente");
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
					}
				}
				
				// Check su id egov
				// Se correttamente formato, altrimenti segnalero' poi il problema
				if(this.busta.getID()!=null){
					String [] split = this.busta.getID().split("_");
					if(split != null && split.length == 5){
						Date dataIntoIDEgov = null;
						try{
							SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm"); // SimpleDateFormat non e' thread-safe
							dataIntoIDEgov = dateformat.parse(split[3]+"_"+split[4]);
						}catch(Exception e){}
						if(dataIntoIDEgov!=null){
							//System.out.println("DATA IDEGOV BUSTA ["+dataIntoIDEgov.toString()
							//		+"]   MINIMA ["+oraRegistrazioneMinimaAccettata.toString()+"]");
							if( dataIntoIDEgov.before(oraRegistrazioneMinimaAccettata)){
								this.log.error("Data presente nell'idEgov della busta ("+SPCoopUtils.getDate_eGovFormat(dataIntoIDEgov)+") più vecchia della data minima attesa ("+SPCoopUtils.getDate_eGovFormat(oraRegistrazioneMinimaAccettata)+") e scadenza non presente");
								Eccezione ecc = new Eccezione();
								ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
								ecc.setCodiceEccezione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO);
								ecc.setRilevanza(LivelloRilevanza.ERROR);
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE.toString());
								this.erroriValidazione.add(ecc);
							}
						}
					}
				}
			}

			
			// Altri Elementi
			if(list.size()>1){
				boolean listaRiscontriGiaPresente = false;
				boolean listaTrasmissioniGiaPresente = false;
				boolean listaEccezioniGiaPresente = false;
				for(int i =1; i<list.size();i++){
					Node child = list.get(i);
					if(SoapUtils.matchLocalName(child, "IntestazioneMessaggio", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento IntestazioneMessaggio","IntestazioneMessaggio");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_INTESTAZIONE_MESSAGGIO+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_INTESTAZIONE_MESSAGGIO.toString());
							this.erroriValidazione.add(ecc);
						}
					}
					//else if((child.getNodeName().equals(xmlns+"ListaRiscontri"))){
					else if(SoapUtils.matchLocalName(child, "ListaRiscontri", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
						if(listaRiscontriGiaPresente==false){
							validazioneListaRiscontri(child,xmlns);
							listaRiscontriGiaPresente = true;
						}else{
							if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
								throw new StrutturaBustaException("Header egov con più di un elemento ListaRiscontri","ListaRiscontri");
							}else{
								Eccezione ecc = new Eccezione();
								ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
								ecc.setCodiceEccezione(CodiceErroreCooperazione.LISTA_RISCONTRI_PRESENTE_PIU_VOLTE);
								ecc.setRilevanza(LivelloRilevanza.ERROR);
								if(this.segnalazioneElementoPresentePiuVolte)
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
								else
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE.toString());
								this.erroriValidazione.add(ecc);
							}
						}
					}
					//else if((child.getNodeName().equals(xmlns+"ListaTrasmissioni"))){
					else if(SoapUtils.matchLocalName(child, "ListaTrasmissioni", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
						if(listaTrasmissioniGiaPresente==false){
							validazioneListaTrasmissioni(child,xmlns);
							listaTrasmissioniGiaPresente = true;
						}else{
							if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
								throw new StrutturaBustaException("Header egov con più di un elemento ListaTrasmissioni","ListaTrasmissioni");
							}else{
								Eccezione ecc = new Eccezione();
								ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
								ecc.setCodiceEccezione(CodiceErroreCooperazione.LISTA_TRASMISSIONI_PRESENTE_PIU_VOLTE);
								ecc.setRilevanza(LivelloRilevanza.ERROR);
								if(this.segnalazioneElementoPresentePiuVolte)
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
								else
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE.toString());
								this.erroriValidazione.add(ecc);
							}
						}
					}
					//else if((child.getNodeName().equals(xmlns+"ListaEccezioni"))){
					else if(SoapUtils.matchLocalName(child, "ListaEccezioni", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
						if(listaEccezioniGiaPresente==false){
							validazioneListaEccezioni(child,xmlns);
							listaEccezioniGiaPresente = true;
						}else{
							if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
								throw new StrutturaBustaException("Header egov con più di un elemento ListaEccezioni","ListaEccezioni");
							}else{
								Eccezione ecc = new Eccezione();
								ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
								ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
								ecc.setRilevanza(LivelloRilevanza.ERROR);
								if(this.segnalazioneElementoPresentePiuVolte)
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
								else
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE.toString());
								this.errorsTrovatiSullaListaEccezioni.add(ecc);
							}
						}
					}else{
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione("Intestazione/child["+child.getNodeName()+"]");
						this.erroriValidazione.add(ecc);
					}
				}
			} 

			
			if(this.spcoopProperties.isCheckTolleranzaDateFutureAttivo()){
				if(this.erroriValidazione.size()==0){
					// controllo che non vi siano date future
					validazioneDatePresentiRispettoDateFuture();
				}
			}

		} catch(StrutturaBustaException e) {
			this.log.error("Struttura della busta non corretta: "+e.getMessage(),e);
			this.msgErrore =  e.getMessage();
			this.codiceErrore = CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO;
			return false; 
		} catch(Exception e) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione("Errore di processamento durante la validazione sintattica");
			this.erroriProcessamento.add(ecc);
			this.log.error("Errore di processamento durante la validazione sintattica",e);
		}

		// una volta letto il messaggio e' possibile utilizzarlo.
		return true;
	}

	
	public SOAPHeaderElement getHeaderEGov_senzaControlli() throws ProtocolException{
		try{	
			// Estraggo header
			if(this.headerSOAP==null)
				this.headerSOAP = this.msg.getSOAPHeader();
			if(this.headerSOAP==null){
				throw new Exception("Analizzato un messaggio senza header");
			}	
			
			// cerco la busta per l'actor
			java.util.Iterator<?> it = this.headerSOAP.examineAllHeaderElements();
			SOAPHeaderElement headerElementEGov = null;
			while( it.hasNext()  ){

				// Test Header Element
				SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();

				//Controllo Actor
				if( SPCoopCostanti.ACTOR_EGOV.equals(headerElement.getActor()) == false  ){
					continue;
				}
				headerElementEGov = headerElement;
			}
			
			if(headerElementEGov==null){
				// cerco la busta per il namespace
				it = this.headerSOAP.examineAllHeaderElements();
				while( it.hasNext()  ){

					// Test Header Element
					SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();

					//Controllo Namespace
					if( SPCoopCostanti.NAMESPACE_EGOV.equals(headerElement.getNamespaceURI()) == false  ){
						continue;
					}
					headerElementEGov = headerElement;
				}
			}
			
			if(headerElementEGov==null){
				// header egov non trovato
				throw new Exception("Header eGov non presente");
			}else{
				return headerElementEGov;
			}
		}catch(Exception e){
			throw new ProtocolException("Lettura non riuscita: "+e.getMessage(),e);
		}
	}
	
	/**
	 * Metodo che si occupa di verificare che sia presente un header eGov nel SOAPHeader <var>SOAPHeader</var>.
	 * Per essere presente, un header eGov deve possedere almeno i seguenti elementi obbligatori :
	 * Intestazione con :
	 * <p>
	 * <ul>
	 * <li> IntestazioneMessaggio-Mittente-IdentificativoParte con tipo e valore
	 * <li> IntestazioneMessaggio-Destinatario-IdentificativoParte con tipo e valore
	 * <li> IntestazioneMessaggio-Messaggio-....
	 * </ul>
	 * <p>
	 * Se la verifica non ha successo, il metodo ritorna null, altrimenti l'header eGov presente (non estratto).
	 *
	 * @param header Header Soap in cui deve essere verificato la presenza di un header eGov.
	 * @return null se la verifica ha successo, altrimenti l'header eGov presente (non estratto).
	 * 
	 */
	public SOAPHeaderElement getHeaderEGov(SOAPHeader header) throws ProtocolException,StrutturaBustaException{
		try{	

			if(header == null)
				return null;

			
			
			// cerco la busta per l'actor
			java.util.Iterator<?> it = header.examineAllHeaderElements();
			SOAPHeaderElement headerElementEGov = null;
			StringBuffer bf = new StringBuffer();
			while( it.hasNext()  ){

				// Test Header Element
				SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();

				//Controllo Actor
				try{
					bf.append("Check ACTOR Atteso["+SPCoopCostanti.ACTOR_EGOV+"]==["+headerElement.getActor()+"]=["+SPCoopCostanti.ACTOR_EGOV.equals(headerElement.getActor())+"]\n");
				}catch(Exception e){}

				if( SPCoopCostanti.ACTOR_EGOV.equals(headerElement.getActor()) == false  ){
					continue;
				}
				bf.append("FIND!\n");
				headerElementEGov = headerElement;
			}
			
			if(headerElementEGov==null){
				bf.append("NOT FOUND TRAMITE ACTOR!\n");
				// cerco la busta per il namespace
				it = header.examineAllHeaderElements();
				while( it.hasNext()  ){

					// Test Header Element
					SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();

					//Controllo Namespace
					try{
						bf.append("Check NAMESPACE Atteso["+SPCoopCostanti.NAMESPACE_EGOV+"]==["+headerElement.getNamespaceURI()+"]=["+SPCoopCostanti.NAMESPACE_EGOV.equals(headerElement.getNamespaceURI())+"]\n");
					}catch(Exception e){}
					if( SPCoopCostanti.NAMESPACE_EGOV.equals(headerElement.getNamespaceURI()) == false  ){
						continue;
					}
					bf.append("FIND!\n");
					headerElementEGov = headerElement;
				}
			}
			
			if(headerElementEGov==null){
				// header egov non trovato
				String msgHeader = "";
				try{
					msgHeader = " header-soap: "+OpenSPCoop2MessageFactory.getMessageFactory().createEmptySOAPMessage(SOAPVersion.SOAP11).getAsString(header, false);
				}catch(Exception e){}
				throw new Exception("Header eGov non presente ("+bf.toString()+")"+msgHeader);
			}
			
			// Preparazione busta di default per eventuale eccezione
			Busta errore = new Busta(this.protocolFactory.getProtocol());
			errore.setTipoMittente(this.spcoopProperties.getKeywordTipoMittenteSconosciuto());
			errore.setMittente(this.spcoopProperties.getKeywordMittenteSconosciuto());
			errore.setIdentificativoPortaMittente(this.traduttore.getIdentificativoPortaDefault(new IDSoggetto(errore.getTipoMittente(), errore.getMittente())));
			errore.setTipoDestinatario(this.spcoopProperties.getKeywordTipoMittenteSconosciuto());
			errore.setDestinatario(this.spcoopProperties.getKeywordMittenteSconosciuto());
			errore.setIdentificativoPortaDestinatario(this.traduttore.getIdentificativoPortaDefault(new IDSoggetto(errore.getTipoDestinatario(), errore.getDestinatario())));
			
			// Controllo presenza più di un header eGov
			if(this.existsMoreHeaderEGov()){
				errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.FORMATO_NON_CORRETTO, 
						"Più di un header e-Gov presente",
						this.protocolFactory));
				this.bustaErroreHeaderIntestazione = errore;
				throw new Exception("Più di un header e-Gov presente"); 
			}
			
			// Prefix eGov
			String xmlns = headerElementEGov.getPrefix();
			if(xmlns == null){ 
				xmlns = "";
			}else if(!xmlns.equals("")){
				xmlns = xmlns + ":";
			}
			
			// Controllo presenza elementi obbligatori che contraddistingono un header eGov: Intestazione
			//if(headerElementEGov.getNodeName().equals(xmlns+"Intestazione") == false){
			if(SoapUtils.matchLocalName(headerElementEGov, "Intestazione", xmlns, SPCoopCostanti.NAMESPACE_EGOV) == false ){
				this.bustaErroreHeaderIntestazione = errore;
				this.bustaErroreHeaderIntestazione.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.FORMATO_NON_CORRETTO, 
						SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_NON_CORRETTO_POSIZIONE.toString(),this.protocolFactory));
				throw new Exception("Header eGov senza header intestazione");
			}

			// Controllo elementi che contraddistingono un header eGov : Intestazione Messaggio
			Vector<Node> list = SoapUtils.getNotEmptyChildNodes(headerElementEGov);
			if(list==null || list.size() == 0){ 
				this.bustaErroreHeaderIntestazione = errore;
				this.bustaErroreHeaderIntestazione.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO, 
						SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_INTESTAZIONE_MESSAGGIO.toString(),this.protocolFactory));
				throw new Exception("Header eGov con header intestazione senza elementi interni tra cui IntestazioneMessaggio");
			} 
			Node intestazioneMsg = list.get(0);
			//if( (intestazioneMsg==null) ||  (!(intestazioneMsg.getNodeName().equals(xmlns+"IntestazioneMessaggio")))  ) {
			if( (intestazioneMsg==null) ||   (!SoapUtils.matchLocalName(intestazioneMsg, "IntestazioneMessaggio", xmlns, SPCoopCostanti.NAMESPACE_EGOV)) ) {
				this.bustaErroreHeaderIntestazione = errore;
				this.bustaErroreHeaderIntestazione.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO, 
						SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_INTESTAZIONE_MESSAGGIO.toString(),this.protocolFactory));
				throw new Exception("Header eGov con header intestazione che possiede first child ["+intestazioneMsg.getNodeName()+"] diverso da IntestazioneMessaggio");
			}
			
			Vector<Node> intestMsgChild = SoapUtils.getNotEmptyChildNodes(intestazioneMsg);
			
			// Controllo elementi principali
			Node mittente = null;
			Node destinatario = null;
			Node messaggio = null;
			for(int i=0;i<intestMsgChild.size();i++){
				Node child = intestMsgChild.get(i);
				if(child==null){
					this.log.info("Elemento null");
				}else if(child.getNodeName()==null){
					this.log.info("Elemento child null");
				}
				if(child!=null && child.getNodeName()!=null){
					//if( (mittente==null) && ((child.getNodeName().equals(xmlns+"Mittente")))){
					if( (mittente==null) && (SoapUtils.matchLocalName(child, "Mittente", xmlns, SPCoopCostanti.NAMESPACE_EGOV))){
						mittente = child; 
					}
					//else if( (destinatario==null) && ((child.getNodeName().equals(xmlns+"Destinatario")))){
					else if( (destinatario==null) && (SoapUtils.matchLocalName(child, "Destinatario", xmlns, SPCoopCostanti.NAMESPACE_EGOV))){
						destinatario = child;
					}
					//else if( (messaggio==null) && ((child.getNodeName().equals(xmlns+"Messaggio")))){
					else if( (messaggio==null) && (SoapUtils.matchLocalName(child, "Messaggio", xmlns, SPCoopCostanti.NAMESPACE_EGOV))){
						messaggio = child;
					}
				}
			}
			
			boolean eccezioneStrutturaMittente = false;
			boolean eccezioneStrutturaDestinatario = false;
			boolean eccezioneStrutturaMessaggio = false;
			
			// controllo mittente
			if(mittente==null){
				errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, 
						SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE.toString(),this.protocolFactory));
				eccezioneStrutturaMittente = true;
			}else{
				Vector<Node> headerMittente = SoapUtils.getNotEmptyChildNodes(mittente); // identificativoParte mitt
				if( (headerMittente==null) || (headerMittente.size() == 0) ){
					errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, 
							SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString(),this.protocolFactory));
					eccezioneStrutturaMittente = true;
				} else {
					for(int i=0; i<headerMittente.size(); i++){
						Node idParteMittente = headerMittente.get(i);
						//if( !(idParteMittente.getNodeName().equals(xmlns+"IdentificativoParte"))  ){ 
						if( !(SoapUtils.matchLocalName(idParteMittente, "IdentificativoParte", xmlns, SPCoopCostanti.NAMESPACE_EGOV))  ){ 
							errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_PRESENTE, 
									SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString(),this.protocolFactory));
							eccezioneStrutturaMittente = true;
							break;
						}
						Vector<Node> valueIDParteMitt = SoapUtils.getNotEmptyChildNodes(idParteMittente);
						if( (valueIDParteMitt==null) || (valueIDParteMitt.size() == 0) || (valueIDParteMitt.size() > 1) ){ 
							errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO, 
									SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString(),this.protocolFactory));
							eccezioneStrutturaMittente = false;
							break;
						} 
						String mittenteValue = null;
						try{
							mittenteValue = valueIDParteMitt.get(0).getNodeValue();
							if(mittenteValue==null)
								throw new Exception("mittente is null");
							//log.info("Value identificativo parte ["+valueIDParteMitt.item(0).getNodeValue()+"]");
						} catch(Exception e) {
							errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO, 
									SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString(),this.protocolFactory));
							eccezioneStrutturaMittente = false;
							break;
						}
						String tipoMittente = null;
						Node hrefFindMitt = null;
						if(this.readQualifiedAttribute){
							hrefFindMitt = SoapUtils.getQualifiedAttributeNode(idParteMittente, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFindMitt = SoapUtils.getAttributeNode(idParteMittente, "tipo");
						}
						if(hrefFindMitt!=null){
							try{
								tipoMittente = hrefFindMitt.getNodeValue();
								if(tipoMittente == null)
									throw new Exception("tipo non definito");
								//log.info("Value identificativo parte ["+hrefFindMitt.getNodeValue()+"]");
							} catch(Exception e) {
								errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.TIPO_MITTENTE_NON_VALIDO, 
										SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString(),this.protocolFactory));
								eccezioneStrutturaMittente = true;
								break;
							}
						}else{
							errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.TIPO_MITTENTE_NON_PRESENTE, 
									SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString(),this.protocolFactory));
							eccezioneStrutturaMittente = true;
							break;
						}   
						if(i==0){
							errore.setTipoMittente(tipoMittente);
							errore.setMittente(mittenteValue);
						}else{
							//errore.addEccezione(Eccezione.getEccezioneValidazione(Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO, 
							//		Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":"+tipoMittente+"/"+mittenteValue+" (busta con più di un mittente non gestita)" ));
							errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.MITTENTE_PRESENTE_PIU_VOLTE, 
											SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString(),this.protocolFactory));
							eccezioneStrutturaMittente = false;
						}
					}
				}
			}
			
			// controllo destinatario
			if(destinatario==null){
				errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_NON_PRESENTE, 
						SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE.toString(),this.protocolFactory));
				eccezioneStrutturaDestinatario = true;
			}else{
				Vector<Node> headerDestinatario = SoapUtils.getNotEmptyChildNodes(destinatario); // identificativoParte dest
				if( (headerDestinatario==null) || (headerDestinatario.size() == 0) ){
					errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_NON_PRESENTE, 
							SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString(),this.protocolFactory));
					eccezioneStrutturaDestinatario = true;
				} else {
					for(int i=0; i<headerDestinatario.size(); i++){
						Node idParteDestinatario = headerDestinatario.get(i);
						//if( !(idParteDestinatario.getNodeName().equals(xmlns+"IdentificativoParte"))  ){
						if( !(SoapUtils.matchLocalName(idParteDestinatario, "IdentificativoParte", xmlns, SPCoopCostanti.NAMESPACE_EGOV))  ){ 
							errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_NON_VALIDO, 
									SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString(),this.protocolFactory));
							eccezioneStrutturaDestinatario = true;
							break;
						}
						Vector<Node> valueIDParteDest = SoapUtils.getNotEmptyChildNodes(idParteDestinatario);
						if( (valueIDParteDest==null) || (valueIDParteDest.size() == 0) || (valueIDParteDest.size() > 1) ){ 
							errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_NON_VALIDO, 
									SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString(),this.protocolFactory));
							eccezioneStrutturaDestinatario = false;
							break;
						} 
						String destinatarioValue = null;
						try{
							destinatarioValue = valueIDParteDest.get(0).getNodeValue();
							if(destinatarioValue==null)
								throw new Exception("destinatario is null");
							//log.info("Value identificativo parte ["+valueIDParteDest.item(0).getNodeValue()+"]");
						} catch(Exception e) {
							errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_NON_VALORIZZATO, 
									SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString(),this.protocolFactory));
							eccezioneStrutturaDestinatario = false;
							break;
						}
						String tipoDestinatario = null;
						Node hrefFindDest = null;
						if(this.readQualifiedAttribute){
							hrefFindDest = SoapUtils.getQualifiedAttributeNode(idParteDestinatario, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFindDest = SoapUtils.getAttributeNode(idParteDestinatario, "tipo");
						}
						if(hrefFindDest!=null){
							try{
								tipoDestinatario = hrefFindDest.getNodeValue();
								if(tipoDestinatario == null)
									throw new Exception("tipo non definito");
								//log.info("Value identificativo parte ["+hrefFindDest.getNodeValue()+"]");
							} catch(Exception e) {
								errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.TIPO_DESTINATARIO_NON_VALIDO, 
										SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString(),this.protocolFactory));
								eccezioneStrutturaDestinatario = true;
								break;
							}
						}else{
							errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.TIPO_DESTINATARIO_NON_PRESENTE, 
									SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString(),this.protocolFactory));
							eccezioneStrutturaDestinatario = true;
							break;
						}   
						if(i==0){
							errore.setTipoDestinatario(tipoDestinatario);
							errore.setDestinatario(destinatarioValue);
						}else{
							//errore.addEccezione(Eccezione.getEccezioneValidazione(Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO, 
							//		Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":"+tipoDestinatario+"/"+destinatarioValue+" (busta con più di un destinatario non gestita)" ));
							errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.DESTINATARIO_PRESENTE_PIU_VOLTE, 
									SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString(),this.protocolFactory));
							eccezioneStrutturaDestinatario = false;
						}
					}
				}
			}
			
			// controllo messaggio
			if(messaggio==null){
				errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO, 
						SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MESSAGGIO.toString(),this.protocolFactory));
				eccezioneStrutturaMessaggio = true;
			}else{
				Vector<Node> contenutoMsg = SoapUtils.getNotEmptyChildNodes(messaggio);
				if( (contenutoMsg.size() <= 0)){
					errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE, 
							SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString(),this.protocolFactory));
					eccezioneStrutturaMessaggio = true;
					
				}else{
					String identificatore = null;
					boolean identificatoreTrovato = false;
					for(int j =0; j<contenutoMsg.size();j++){
						Node childMsg = contenutoMsg.get(j);
						//if((childMsg.getNodeName().equals(xmlns+"Identificatore"))){
						if(SoapUtils.matchLocalName(childMsg, "Identificatore", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
							identificatoreTrovato = true;
							if (SoapUtils.getNotEmptyChildNodes(childMsg).size() != 1){
								errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALORIZZATO, 
										SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString(),this.protocolFactory));
							}else{
								try{
									identificatore = SoapUtils.getNotEmptyChildNodes(childMsg).get(0).getNodeValue();
								} catch(Exception e) {}
							}
							break;
						}
					}
					if(identificatoreTrovato==false){
						errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE, 
								SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString(),this.protocolFactory));
						eccezioneStrutturaMessaggio = true;
					}else if(identificatore==null){
						errore.addEccezione(Eccezione.getEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE, 
								SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString(),this.protocolFactory));
						eccezioneStrutturaMessaggio = false;
					}else{
						errore.setID(identificatore);
					}
				}
			}
			
				
			
			// report
			if(errore.sizeListaEccezioni()>0){
				
				if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false && 
						((eccezioneStrutturaMittente) || (eccezioneStrutturaDestinatario) || (eccezioneStrutturaMessaggio))
					){
					Eccezione ec = errore.getEccezione(0);
					throw new StrutturaBustaException("Header egov con struttura errata, "+ec.getDescrizione(this.protocolFactory),ec.getDescrizione(this.protocolFactory).replaceAll("/", ""));
				}	
				
				// Leggo eventuali altri valori presenti
				Node servizio = null;
				Node azione = null;
				Node profiloCollaborazione = null;
				Node collaborazione = null;
				Node profiloTrasmissione = null;
				Node sequenza = null;
				for(int i=0;i<intestMsgChild.size();i++){
					Node child = intestMsgChild.get(i);
					//if( (mittente==null) && ((child.getNodeName().equals(xmlns+"Mittente")))){
					if( (mittente==null) && ((SoapUtils.matchLocalName(child, "Mittente", xmlns, SPCoopCostanti.NAMESPACE_EGOV)))){
						mittente = child; 
					}
					//else if( (destinatario==null) && ((child.getNodeName().equals(xmlns+"Destinatario")))){
					else if( (destinatario==null) && ((SoapUtils.matchLocalName(child, "Destinatario", xmlns, SPCoopCostanti.NAMESPACE_EGOV)))){
						destinatario = child;
					}
					//else if( (servizio==null) && ((child.getNodeName().equals(xmlns+"Servizio")))){
					else if( (servizio==null) && ((SoapUtils.matchLocalName(child, "Servizio", xmlns, SPCoopCostanti.NAMESPACE_EGOV)))){
						servizio = child;
					}
					//else if( (azione==null) && ((child.getNodeName().equals(xmlns+"Azione")))){
					else if( (azione==null) && ((SoapUtils.matchLocalName(child, "Azione", xmlns, SPCoopCostanti.NAMESPACE_EGOV)))){
						azione = child;
					}
					//else if( (profiloCollaborazione==null) && ((child.getNodeName().equals(xmlns+"ProfiloCollaborazione")))){
					else if( (profiloCollaborazione==null) && ((SoapUtils.matchLocalName(child, "ProfiloCollaborazione", xmlns, SPCoopCostanti.NAMESPACE_EGOV)))){
						profiloCollaborazione = child;
					}
					//else if( (collaborazione==null) && ((child.getNodeName().equals(xmlns+"Collaborazione")))){
					else if( (collaborazione==null) && ((SoapUtils.matchLocalName(child, "Collaborazione", xmlns, SPCoopCostanti.NAMESPACE_EGOV)))){
						collaborazione = child;
					}
					//else if( (profiloTrasmissione==null) && ((child.getNodeName().equals(xmlns+"ProfiloTrasmissione")))){
					else if( (profiloTrasmissione==null) && ((SoapUtils.matchLocalName(child, "ProfiloTrasmissione", xmlns, SPCoopCostanti.NAMESPACE_EGOV)))){
						profiloTrasmissione = child;
					}
					//else if( (sequenza==null) && ((child.getNodeName().equals(xmlns+"Sequenza")))){
					else if( (sequenza==null) && ((SoapUtils.matchLocalName(child, "Sequenza", xmlns, SPCoopCostanti.NAMESPACE_EGOV)))){
						sequenza = child;
					}
				}
				
				// Mittente
				if(mittente!=null){
					try{
						Vector<Node> headerMittente = SoapUtils.getNotEmptyChildNodes(mittente);
						Node idParteMittente = headerMittente.get(0);
						errore.setMittente(SoapUtils.getNotEmptyChildNodes(idParteMittente).get(0).getNodeValue());
					}catch(Exception e){}
					try{
						Vector<Node> headerMittente = SoapUtils.getNotEmptyChildNodes(mittente);
						Node idParteMittente = headerMittente.get(0);
						Node hrefFindMitt = null;
						if(this.readQualifiedAttribute){
							hrefFindMitt = SoapUtils.getQualifiedAttributeNode(idParteMittente, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFindMitt = SoapUtils.getAttributeNode(idParteMittente, "tipo");
						}
						String itValue = hrefFindMitt.getNodeValue();
						errore.setTipoMittente(itValue);
					}catch(Exception e){}
					try{
						Vector<Node> headerMittente = SoapUtils.getNotEmptyChildNodes(mittente);
						Node idParteMittente = headerMittente.get(0);
						Node hrefFindMitt = null;
						if(this.readQualifiedAttribute){
							hrefFindMitt = SoapUtils.getQualifiedAttributeNode(idParteMittente, "indirizzoTelematico", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFindMitt = SoapUtils.getAttributeNode(idParteMittente, "indirizzoTelematico");
						}
						String itValue = hrefFindMitt.getNodeValue();
						errore.setIndirizzoMittente(itValue);
					}catch(Exception e){}
				}
				
				// Destinatario
				if(destinatario!=null){
					try{
						Vector<Node> headerDest = SoapUtils.getNotEmptyChildNodes(destinatario);
						Node idParteDest = headerDest.get(0);
						errore.setDestinatario(SoapUtils.getNotEmptyChildNodes(idParteDest).get(0).getNodeValue());
					}catch(Exception e){}
					try{
						Vector<Node> headerDest = SoapUtils.getNotEmptyChildNodes(destinatario);
						Node idParteDest = headerDest.get(0);
						Node hrefFindDest = null;
						if(this.readQualifiedAttribute){
							hrefFindDest = SoapUtils.getQualifiedAttributeNode(idParteDest, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFindDest = SoapUtils.getAttributeNode(idParteDest, "tipo");
						}
						String itValue = hrefFindDest.getNodeValue();
						errore.setTipoDestinatario(itValue);
					}catch(Exception e){}
					try{
						Vector<Node> headerDest = SoapUtils.getNotEmptyChildNodes(destinatario);
						Node idParteDest = headerDest.get(0);
						Node hrefFindDest = null;
						if(this.readQualifiedAttribute){
							hrefFindDest = SoapUtils.getQualifiedAttributeNode(idParteDest, "indirizzoTelematico", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFindDest = SoapUtils.getAttributeNode(idParteDest, "indirizzoTelematico");
						}
						String itValue = hrefFindDest.getNodeValue();
						errore.setIndirizzoDestinatario(itValue);
					}catch(Exception e){}
				}
				
				// Servizio
				if(servizio!=null){
					try{
						errore.setServizio(SoapUtils.getNotEmptyChildNodes(servizio).get(0).getNodeValue());
					}catch(Exception e){}
					try{
						Node hrefFind = null;
						if(this.readQualifiedAttribute){
							hrefFind = SoapUtils.getQualifiedAttributeNode(servizio, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFind = SoapUtils.getAttributeNode(servizio, "tipo");
						}
						errore.setTipoServizio(hrefFind.getNodeValue());
					}catch(Exception e){}
				}
				
				// Azione
				if(azione!=null){
					try{
						errore.setAzione(SoapUtils.getNotEmptyChildNodes(azione).get(0).getNodeValue());
					}catch(Exception e){}
				}
				
				// ProfiloDiCollaborazione
				if(profiloCollaborazione!=null){
					try{
						String profilovalue = SoapUtils.getNotEmptyChildNodes(profiloCollaborazione).get(0).getNodeValue();
						errore.setProfiloDiCollaborazione(toProfilo(profilovalue), profilovalue);
					}catch(Exception e){}
					try{
						Node hrefFind = null;
						if(this.readQualifiedAttribute){
							hrefFind = SoapUtils.getQualifiedAttributeNode(profiloCollaborazione, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFind = SoapUtils.getAttributeNode(profiloCollaborazione, "tipo");
						}
						errore.setTipoServizioCorrelato(hrefFind.getNodeValue());
					}catch(Exception e){}
					try{
						Node hrefFind = null;
						if(this.readQualifiedAttribute){
							hrefFind = SoapUtils.getQualifiedAttributeNode(profiloCollaborazione, "servizioCorrelato", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFind = SoapUtils.getAttributeNode(profiloCollaborazione, "servizioCorrelato");
						}
						errore.setServizioCorrelato(hrefFind.getNodeValue());
					}catch(Exception e){}
				}
				
				// Collaborazione
				if(collaborazione!=null){
					try{
						errore.setCollaborazione(SoapUtils.getNotEmptyChildNodes(collaborazione).get(0).getNodeValue());
					}catch(Exception e){}
				}
				
				// Messaggio
				if(messaggio!=null){
					try{
						Vector<Node> contenutoMsg = SoapUtils.getNotEmptyChildNodes(messaggio);
						for(int j =0; j<contenutoMsg.size();j++){
							Node childMsg = contenutoMsg.get(j);
							//if((childMsg.getNodeName().equals(xmlns+"RiferimentoMessaggio"))){
							if(SoapUtils.matchLocalName(childMsg, "RiferimentoMessaggio", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
								try{
									errore.setRiferimentoMessaggio(SoapUtils.getNotEmptyChildNodes(childMsg).get(0).getNodeValue());
								}catch(Exception e){}
							}
							//else if((childMsg.getNodeName().equals(xmlns+"Scadenza"))){
							else if(SoapUtils.matchLocalName(childMsg, "Scadenza", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
								try{
									String scadenza = SoapUtils.getNotEmptyChildNodes(childMsg).get(0).getNodeValue();
									Date scadenzaDate = validazioneData(scadenza);
									errore.setScadenza(scadenzaDate);
								}catch(Exception e){}
							}
							//else if((childMsg.getNodeName().equals(xmlns+"OraRegistrazione"))){
							else if(SoapUtils.matchLocalName(childMsg, "OraRegistrazione", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
								try{
									String oraRegistrazione = SoapUtils.getNotEmptyChildNodes(childMsg).get(0).getNodeValue();
									Date oraDate = validazioneData(oraRegistrazione);
									errore.setOraRegistrazione(oraDate);
								}catch(Exception e){}
								try{
									Node hrefFind = null;
									if(this.readQualifiedAttribute){
										hrefFind = SoapUtils.getQualifiedAttributeNode(childMsg, "tempo", SPCoopCostanti.NAMESPACE_EGOV);
									}else{
										hrefFind = SoapUtils.getAttributeNode(childMsg, "tempo");
									}
									String tipoOraRegistrazioneValue = hrefFind.getNodeValue();
									errore.setTipoOraRegistrazione(toTipoOra(tipoOraRegistrazioneValue), tipoOraRegistrazioneValue);
								}catch(Exception e){}
							}
						}
					}catch(Exception e){}
				}
				
				// Profilo Trasmissione
				if(profiloTrasmissione!=null){
					try{
						Node hrefFind = null;
						if(this.readQualifiedAttribute){
							hrefFind = SoapUtils.getQualifiedAttributeNode(profiloTrasmissione, "confermaRicezione", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFind = SoapUtils.getAttributeNode(profiloTrasmissione, "confermaRicezione");
						}
						if(hrefFind.getNodeValue().equals("true"))
							errore.setConfermaRicezione(true);
						else
							errore.setConfermaRicezione(false);
					}catch(Exception e){}
					try{
						Node hrefFind = null;
						if(this.readQualifiedAttribute){
							hrefFind = SoapUtils.getQualifiedAttributeNode(profiloTrasmissione, "inoltro", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFind = SoapUtils.getAttributeNode(profiloTrasmissione, "inoltro");
						}
						String inoltro = hrefFind.getNodeValue();
						errore.setInoltro(toInoltro(inoltro), inoltro);
					}catch(Exception e){}
				}
				
				// Sequenza
				if(sequenza!=null){
					try{
						Node hrefFind = null;
						if(this.readQualifiedAttribute){
							hrefFind = SoapUtils.getQualifiedAttributeNode(sequenza, "numeroProgressivo", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFind = SoapUtils.getAttributeNode(sequenza, "numeroProgressivo");
						}
						Long test = new Long(hrefFind.getNodeValue());
						errore.setSequenza(test.longValue());
					}catch(Exception e){}
				}
				
				this.bustaErroreHeaderIntestazione = errore;
				throw new Exception("Header eGov senza alcuni elementi principali: presenza mittente["+(mittente!=null)+"] destinatario["+(destinatario!=null)+"] messaggio["+(messaggio!=null)+"] eccezioni-riscontrate: ["+errore.toStringListaEccezioni(this.getProtocolFactory())+"]"); 
			}
			
			return headerElementEGov;
			
		}catch(StrutturaBustaException e){
			throw e;
		}catch(Exception e){
			throw new ProtocolException("Lettura non riuscita: "+e.getMessage(),e);
		}
	}


	/**
	 * Controlla che esista una busta SPCoop nell'header
	 * 
	 * @return true se l'header esiste, false altrimenti
	 */
	public boolean existsHeaderEGov(OpenSPCoop2Message msg){
		this.msg = (SOAPMessage) msg;
		try{
			if(this.headerEGov!=null){
				return true;
			}else{
				if(msg==null){
					return false;
				}
				if(this.headerSOAP==null)
					this.headerSOAP = this.msg.getSOAPHeader();
				if(this.headerSOAP == null){
					return false;
				}
				
				// cerco la busta per l'actor
				java.util.Iterator<?> it = this.headerSOAP.examineAllHeaderElements();
				SOAPHeaderElement headerElementEGov = null;
				while( it.hasNext()  ){

					// Test Header Element
					SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();

					//Controllo Actor
					if( SPCoopCostanti.ACTOR_EGOV.equals(headerElement.getActor()) == false  ){
						continue;
					}
					headerElementEGov = headerElement;
				}
				
				if(headerElementEGov==null){
					// cerco la busta per il namespace
					it = this.headerSOAP.examineAllHeaderElements();
					while( it.hasNext()  ){

						// Test Header Element
						SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();

						//Controllo Namespace
						if( SPCoopCostanti.NAMESPACE_EGOV.equals(headerElement.getNamespaceURI()) == false  ){
							continue;
						}
						headerElementEGov = headerElement;
					}
				}
				
				return headerElementEGov!=null;
			}
			

		}catch(Exception e){
			this.log.error("existsHeaderEGov error: "+e.getMessage(), e);
			return false;
		}
	}
	
	/**
	 * Controlla che esista più di una busta SPCoop nell'header
	 * 
	 * @return true se l'header esiste, false altrimenti
	 */
	public boolean existsMoreHeaderEGov(){
		try{
			if(this.headerSOAP==null)
				this.headerSOAP = this.msg.getSOAPHeader();
			if(this.headerSOAP == null){
				return false;
			}
				
			boolean headerGiaTrovato = false;
			
			// cerco la busta per l'actor
			java.util.Iterator<?> it = this.headerSOAP.examineAllHeaderElements();
			while( it.hasNext()  ){
				
				// Test Header Element
				SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();

				//Controllo Actor
				if( SPCoopCostanti.ACTOR_EGOV.equals(headerElement.getActor()) == false  ){
					continue;
				}
				if(headerGiaTrovato)
					return true;
				else
					headerGiaTrovato = true;
			}
				
			if(headerGiaTrovato==false){
				// cerco la busta per il namespace
				it = this.headerSOAP.examineAllHeaderElements();
				while( it.hasNext()  ){

					// Test Header Element
					SOAPHeaderElement headerElement = (SOAPHeaderElement) it.next();

					//Controllo Namespace
					if( SPCoopCostanti.NAMESPACE_EGOV.equals(headerElement.getNamespaceURI()) == false  ){
						continue;
					}
					
					if(headerGiaTrovato)
						return true;
					else
						headerGiaTrovato = true;
				}
				
			}
			
			return false;
			

		}catch(Exception e){
			this.log.error("existsMoreHeaderEGov error: "+e.getMessage());
			return false;
		}
	}


	/**
	 * Metodo che ritorna il prefissso eGov
	 * 
	 * @return Prefisso
	 * 
	 */
	public String getPrefix(){

		// Prefix eGov
		String xmlns = this.headerEGov.getPrefix();
		if(xmlns == null){ 
			xmlns = "";
		}
		if(!xmlns.equals(""))
			xmlns = xmlns + ":";
		return xmlns;
	}



	/**
	 * Metodo che si occupa di validare l'elemento 'Mittente' <var>child</var> secondo specifica eGov.
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto :
	 *    {@link Eccezione}, e viene inserito in errors.
	 *
	 * @param child nodo su cui effettuare la validazione.
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneMittente(Node child,String prefix) throws ProtocolException{

		//log.info("Validazione Mittente...");

		Vector<Node> headerMittente = SoapUtils.getNotEmptyChildNodes(child);
		Node idParte = headerMittente.get(0);
		//log.info("esamino ["+idParte.getNodeName()+"]"); 
		Vector<Node> valueIDParte = SoapUtils.getNotEmptyChildNodes(idParte);
		String value =  valueIDParte.get(0).getNodeValue();
		//log.info("Value identificativo parte Mittente ["+value+"]");
		Node hrefFind = null;
		if(this.readQualifiedAttribute){
			hrefFind = SoapUtils.getQualifiedAttributeNode(idParte, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
		}else{
			hrefFind = SoapUtils.getAttributeNode(idParte, "tipo");
		}
		String tipo = hrefFind.getNodeValue();
		//log.info("Tipo identificativo parte Mittente ["+tipo+"]");
		
		// Controllo tipo
		if(this.tipiSoggetti.contains(tipo)==false){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_MITTENTE_NON_VALIDO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString());
			this.erroriValidazione.add(ecc);
		}
		
		this.busta.setTipoMittente(tipo);
		this.busta.setMittente(value);
		String indTelematico = null;
		// indirizzo Telematico
		try{
			Node hrefFindT = null;
			if(this.readQualifiedAttribute){
				hrefFindT = SoapUtils.getQualifiedAttributeNode(idParte, "indirizzoTelematico", SPCoopCostanti.NAMESPACE_EGOV);
			}else{
				hrefFindT = SoapUtils.getAttributeNode(idParte, "indirizzoTelematico");
			}
			if(hrefFindT != null)
				indTelematico = hrefFindT.getNodeValue();
			//log.info("IndirizzoTelematicoMittente ["+indTelematico+"]");
		} catch(Exception e) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.INDIRIZZO_MITTENTE_NON_PRESENTE );
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
			this.erroriValidazione.add(ecc);
		}
		if(indTelematico != null && (indTelematico.equals("")==false)){ 
			URL urlCheck = null;
			try{	
				urlCheck = new URL( indTelematico );
				this.busta.setIndirizzoMittente(urlCheck.toString());
			} catch(Exception e) {
				this.busta.setIndirizzoMittente(indTelematico); // comunque lo imposto!
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.INDIRIZZO_MITTENTE_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
				this.erroriValidazione.add(ecc);
			}
		}	
		

	}

	/**
	 * Metodo che si occupa di validare l'elemento 'Destinatario' <var>child</var> secondo specifica eGov.
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *    {@link Eccezione}, e viene inserito in errors.
	 *
	 * @param child nodo su cui effettuare la validazione
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneDestinatario(Node child,String prefix) throws ProtocolException{

		//log.info("Validazione Destinatario...");

		Vector<Node> headerDestinatario = SoapUtils.getNotEmptyChildNodes(child);
		Node idParte = headerDestinatario.get(0);
		//log.info("esamino ["+idParte.getNodeName()+"]"); 
		Vector<Node> valueIDParte = SoapUtils.getNotEmptyChildNodes(idParte);
		String value =  valueIDParte.get(0).getNodeValue();
		//log.info("Value identificativo parte Destinatario ["+value+"]");
		Node hrefFind = null;
		if(this.readQualifiedAttribute){
			hrefFind = SoapUtils.getQualifiedAttributeNode(idParte, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
		}else{
			hrefFind = SoapUtils.getAttributeNode(idParte, "tipo");
		}
		String tipo = hrefFind.getNodeValue();
		//log.info("Tipo identificativo parte Destinatario ["+tipo+"]");
		
		// Controllo tipo
		if(this.tipiSoggetti.contains(tipo)==false){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_DESTINATARIO_SCONOSCIUTO );
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString());
			this.erroriValidazione.add(ecc);
		}
		
		this.busta.setTipoDestinatario(tipo);
		this.busta.setDestinatario(value);

		String indTelematico = null;
		// indirizzo Telematico
		try{
			Node hrefFindT = null;
			if(this.readQualifiedAttribute){
				hrefFindT = SoapUtils.getQualifiedAttributeNode(idParte, "indirizzoTelematico", SPCoopCostanti.NAMESPACE_EGOV);
			}else{
				hrefFindT = SoapUtils.getAttributeNode(idParte, "indirizzoTelematico");
			}
			if(hrefFindT != null)
				indTelematico = hrefFindT.getNodeValue();
			//log.info("IndirizzoTelematicoMittente ["+indTelematico+"]");
		} catch(Exception e) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.INDIRIZZO_DESTINATARIO_NON_PRESENTE );
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
			this.erroriValidazione.add(ecc);
		}
		if(indTelematico != null && (indTelematico.equals("")==false)){ 
			URL urlcheck = null;
			try{	
				urlcheck = new URL( indTelematico );
				this.busta.setIndirizzoDestinatario(urlcheck.toString());
			} catch(Exception e) {
				this.busta.setIndirizzoDestinatario(indTelematico); // comunque lo imposto!
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.INDIRIZZO_DESTINATARIO_NON_VALIDO );
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
				this.erroriValidazione.add(ecc);
			}
		}	
		


	}


	/**
	 * Metodo che si occupa di validare l'elemento 'ProfiloCollaborazione' <var>child</var> secondo specifica eGov.
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *    {@link Eccezione}, e viene inserito in errors.
	 *
	 * @param child nodo su cui effettuare la validazione
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneProfiloCollaborazione(Node child,String prefix) throws ProtocolException{

		//log.info("Validazione Profilo di Collaborazione...");
		String profilo = null;

		Vector<Node> valueProfiloDiCollaborazione = SoapUtils.getNotEmptyChildNodes(child);
		if( valueProfiloDiCollaborazione.size() == 0 || valueProfiloDiCollaborazione.size() > 1  ){ 
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_NON_VALIDO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
		}else {
			try{
				profilo = valueProfiloDiCollaborazione.get(0).getNodeValue();
				//log.info("Value Profilo di Collaborazione ["+valueProfiloDiCollaborazione.item(0).getNodeValue()+"]");
				if( (profilo.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY) == false) && 
						(profilo.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO) == false) && 
						(profilo.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO) == false) && 
						(profilo.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO) == false)){ 
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString());
					this.erroriValidazione.add(ecc);
				}
				this.busta.setProfiloDiCollaborazione(toProfilo(profilo), profilo);
			} catch(Exception e) {
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString());
				this.erroriValidazione.add(ecc); 
			}	    
		}

		// Controllo servizioCorrelato e Tipo
		String tipo = null;
		try{
			Node hrefFind = null;
			if(this.readQualifiedAttribute){
				hrefFind = SoapUtils.getQualifiedAttributeNode(child, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
			}else{
				hrefFind = SoapUtils.getAttributeNode(child, "tipo");
			}
			if(hrefFind != null)
				tipo = hrefFind.getNodeValue();
			//log.info("Tipo Servizio Correlato ["+tipo+"]");
		} catch(Exception e) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_SERVIZIO_CORRELATO_NON_VALIDO );
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO.toString());
			this.erroriValidazione.add(ecc);
		}
		if(tipo != null){ 
			// Controllo tipo
			if(this.tipiServizi.contains(tipo)==false){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_SERVIZIO_CORRELATO_SCONOSCIUTO );
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO.toString());
				this.erroriValidazione.add(ecc);
			}
			/* OLD CODICE 			
			if(!tipo.equals(Costanti.SERVIZIO_CORRELATO_SPC) &&
					!tipo.equals(Costanti.SERVIZIO_CORRELATO_TEST) &&
					!tipo.equals(Costanti.SERVIZIO_CORRELATO_URL) &&
					!tipo.equals(Costanti.SERVIZIO_CORRELATO_WSDL) &&
					!tipo.equals(Costanti.SERVIZIO_CORRELATO_LDAP) &&
					!tipo.equals(Costanti.SERVIZIO_CORRELATO_UDDI) &&
					!tipo.equals(Costanti.SERVIZIO_CORRELATO_XML_REGISTRY) ){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(Costanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				ecc.setCodiceEccezione(Costanti.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO );
				ecc.setRilevanza(LivelloRilevanza.GRAVE);
				ecc.setDescrizione(Costanti.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO);
				this.erroriValidazione.add(ecc);
			}	
			*/
		}


		String servizioCorrelato = null;
		try{
			Node hrefFind = null;
			if(this.readQualifiedAttribute){
				hrefFind = SoapUtils.getQualifiedAttributeNode(child, "servizioCorrelato", SPCoopCostanti.NAMESPACE_EGOV);
			}else{
				hrefFind = SoapUtils.getAttributeNode(child, "servizioCorrelato");
			}
			if(hrefFind != null)
				servizioCorrelato = hrefFind.getNodeValue();
			//log.info("Servizio Correlato ["+servizioCorrelato+"]");
		} catch(Exception e) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.SERVIZIO_CORRELATO_NON_VALIDO );
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_SERVIZIO_CORRELATO.toString());
			this.erroriValidazione.add(ecc);
		}
		this.busta.setTipoServizioCorrelato(tipo);
		this.busta.setServizioCorrelato(servizioCorrelato);


		// MessaggioSingoloOneWay e Sincrono
		if (   servizioCorrelato != null  && 
				(  profilo.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY) == true || 
						profilo.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO) == true    )) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.SERVIZIO_CORRELATO_NON_VALIDO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_SERVIZIO_CORRELATO.toString());
			this.erroriValidazione.add(ecc);
		}
		if (  (tipo!=null) && (this.tipiServizi.contains(tipo)) && 
				(  profilo.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY) == true || 
						profilo.equals(SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO) == true    )) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_SERVIZIO_CORRELATO_NON_VALIDO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO.toString());
			this.erroriValidazione.add(ecc);
		}

	}

	/**
	 * Metodo che si occupa di validare l'elemento 'Collaborazione' <var>child</var> secondo specifica eGov.
	 * <p>
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *    {@link Eccezione}, e viene inserito in errors.
	 *
	 * @param child nodo su cui effettuare la validazione
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneCollaborazione(Node child) throws ProtocolException{

		//log.info("Validazione Collaborazione...");
		if ( SoapUtils.getNotEmptyChildNodes(child).size() != 1 ){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.COLLABORAZIONE_NON_VALIDA);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
			return;
		}
		String coll = null;
		try{
			coll = SoapUtils.getNotEmptyChildNodes(child).get(0).getNodeValue();
		} catch(Exception e) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.COLLABORAZIONE_NON_VALORIZZATA);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
		}
		//log.info("Collaborazione["+coll+"]");
		this.busta.setCollaborazione(coll);
	}

	/**
	 * Metodo che si occupa di validare l'elemento 'Servizio' <var>child</var> secondo specifica eGov.
	 * <p>
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *    {@link Eccezione}, e viene inserito in errors.
	 *
	 * @param child nodo su cui effettuare la validazione
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneServizio(Node child,String prefix) throws StrutturaBustaException, ProtocolException{

		//log.info("Validazione servizio...");
		if (SoapUtils.getNotEmptyChildNodes(child).size() != 1){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.SERVIZIO_NON_VALIDO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
			return;
		}

		String serv = null;
		try{
			serv = SoapUtils.getNotEmptyChildNodes(child).get(0).getNodeValue();
		} catch(Exception e) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.SERVIZIO_NON_VALORIZZATO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
		}

		//log.info("Servizio["+serv+"]");
		this.busta.setServizio(serv);


		// Tipo
		String tipo = null;
		try{
			Node hrefFind = null;
			if(this.readQualifiedAttribute){
				hrefFind = SoapUtils.getQualifiedAttributeNode(child, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
			}else{
				hrefFind = SoapUtils.getAttributeNode(child, "tipo");
			}
			if(hrefFind != null)
				tipo = hrefFind.getNodeValue();
			//log.info("Servizio_tipo ["+tipo+"]");
			
			if (tipo == null){
				if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
					throw new StrutturaBustaException("Header egov con elemento Servizio senza tipo","Servizio");
				} else {
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_SERVIZIO_NON_PRESENTE );
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO.toString());
					this.erroriValidazione.add(ecc);
				}
			}else{
			
				// Controllo tipo
				if(this.tipiServizi.contains(tipo)==false){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_SERVIZIO_SCONOSCIUTO );
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO.toString());
					this.erroriValidazione.add(ecc);
				}
			}
			
		}catch(StrutturaBustaException e){
			throw e;
		} catch(Exception e) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_SERVIZIO_NON_VALIDO );
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO.toString());
			this.erroriValidazione.add(ecc);
		}
		
		
		this.busta.setTipoServizio(tipo);

	}

	/**
	 * Metodo che si occupa di validare l'elemento 'Azione' <var>child</var> secondo specifica eGov.
	 * <p>
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *    {@link Eccezione}, e viene inserito in errors.
	 *
	 * @param child nodo su cui effettuare la validazione
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneAzione(Node child) throws ProtocolException{

		//log.info("Validazione azione...");

		if (SoapUtils.getNotEmptyChildNodes(child).size() != 1){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.AZIONE_NON_VALIDA);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
			return;
		}

		String azione = null;
		try{
			azione = SoapUtils.getNotEmptyChildNodes(child).get(0).getNodeValue();
		} catch(Exception e) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.AZIONE_NON_VALORIZZATA);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
		}

		//log.info("Azione["+azione+"]");
		this.busta.setAzione(azione);
	}

	/**
	 * Metodo che si occupa di validare l'elemento 'Messaggio' <var>child</var> secondo specifica eGov.
	 * <p>
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *    {@link Eccezione}, e viene inserito in errors.
	 *
	 * @param child nodo su cui effettuare la validazione
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneMessaggio(Node child,String prefix) throws StrutturaBustaException, ProtocolException{

		// Prefix eGov
		String xmlns = getPrefix();

		//log.info("Validazione Messaggio...");

		Vector<Node> contenutoMsg = SoapUtils.getNotEmptyChildNodes(child);
		if( (contenutoMsg.size() == 0) || (contenutoMsg.size() > 4)){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione("IntestazioneMessaggio/Messaggio childsize:"+contenutoMsg.size());
			this.erroriValidazione.add(ecc);
		}

		boolean presenzaID = false;
		boolean presenzaOra = false;
		boolean presenzaScadenza = false;
		boolean presenzaRifMessaggio = false;
		
		for(int j =0; j<contenutoMsg.size();j++){
			Node childMsg = contenutoMsg.get(j);

			//if((childMsg.getNodeName().equals(xmlns+"Identificatore"))){
			if(SoapUtils.matchLocalName(childMsg, "Identificatore", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
				
				
				if(presenzaID){
					if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
						throw new StrutturaBustaException("Header egov con più di un elemento Identificatore","MessaggioIdentificatore");
					}else{
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_PRESENTE_PIU_VOLTE);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						if(this.segnalazioneElementoPresentePiuVolte)
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
						else
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
						continue;
					}
				}else{
				
					//log.info("Validazione identificatore...");
					presenzaID = true;
	
					if (SoapUtils.getNotEmptyChildNodes(childMsg).size() != 1){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALORIZZATO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
						continue;
					}
					String id = null;
					try{
						id = SoapUtils.getNotEmptyChildNodes(childMsg).get(0).getNodeValue();
					} catch(Exception e) {
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALORIZZATO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
					}
	
					if(id==null){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALORIZZATO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
					}
	
					//log.info("identificatore["+id+"]");
					this.busta.setID(id);
				}

			//}else if((childMsg.getNodeName().equals(xmlns+"OraRegistrazione"))){
			}else if(SoapUtils.matchLocalName(childMsg, "OraRegistrazione", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
				
				if(presenzaOra){
					if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
						throw new StrutturaBustaException("Header egov con più di un elemento OraRegistrazione","MessaggioOraRegistrazione");
					}else{
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_PRESENTE_PIU_VOLTE);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						if(this.segnalazioneElementoPresentePiuVolte)
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
						else
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
						continue;
					}
				}else{
				
					//log.info("Validazione Ora Registrazione...");
					presenzaOra = true;
	
					if (SoapUtils.getNotEmptyChildNodes(childMsg).size() != 1){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_PRESENTE);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
						continue;
					}
					String ora = null;
					try{
						ora = SoapUtils.getNotEmptyChildNodes(childMsg).get(0).getNodeValue();
					} catch(Exception e) {
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALORIZZATA);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
						continue;
					}
					Date oraDate = null;  
					if(ora != null){
						oraDate = validazioneData(ora);
					}
					if( (ora==null) || (oraDate == null)){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALORIZZATA);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
					}
	
					//log.info("OraRegistrazione["+oraDate+"]");
					this.busta.setOraRegistrazione(oraDate);
	
					// tempo ...
					String tipoOra = null;
					try{
						Node hrefFind = null;
						if(this.readQualifiedAttribute){
							hrefFind = SoapUtils.getQualifiedAttributeNode(childMsg, "tempo", SPCoopCostanti.NAMESPACE_EGOV);
						}else{
							hrefFind = SoapUtils.getAttributeNode(childMsg, "tempo");
						}
						tipoOra = hrefFind.getNodeValue();
					} catch(Exception e) {
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov senza elemento OraRegistrazione tempo","OraRegistrazioneTempo");
						} else {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_ORA_REGISTRAZIONE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE_TEMPO.toString());
							this.erroriValidazione.add(ecc);
						}
					}
					if(tipoOra!=null){
						if(!tipoOra.equals(SPCoopCostanti.TIPO_TEMPO_LOCALE) && !tipoOra.equals(SPCoopCostanti.TIPO_TEMPO_SPC)){
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_ORA_REGISTRAZIONE_SCONOSCIUTA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE_TEMPO.toString());
							this.erroriValidazione.add(ecc);
						}
					}
					this.busta.setTipoOraRegistrazione(toTipoOra(tipoOra), tipoOra);
				}

			//}else if((childMsg.getNodeName().equals(xmlns+"RiferimentoMessaggio"))){
			}else if(SoapUtils.matchLocalName(childMsg, "RiferimentoMessaggio", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){	
				//log.info("Validazione RiferimentoMessaggio...");

				if(presenzaRifMessaggio){
					if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
						throw new StrutturaBustaException("Header egov con più di un elemento RiferimentoMessaggio","MessaggioRiferimentoMessaggio");
					}else{
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_PRESENTE_PIU_VOLTE);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						if(this.segnalazioneElementoPresentePiuVolte)
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_DEFINITO_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
						else
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
						continue;
					}
				}else{
				
					presenzaRifMessaggio = true;
					
					if (SoapUtils.getNotEmptyChildNodes(childMsg).size() != 1){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALORIZZATO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
						continue;
					}
					String rifID = null;
					try{
						rifID = SoapUtils.getNotEmptyChildNodes(childMsg).get(0).getNodeValue();
					} catch(Exception e) {
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALORIZZATO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
					}
	
					if(rifID==null){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALORIZZATO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
					}
	
					//log.info("RiferimentoMessaggio["+rifID+"]");
					this.busta.setRiferimentoMessaggio(rifID);
				}

			//}else if((childMsg.getNodeName().equals(xmlns+"Scadenza"))){
			}else if(SoapUtils.matchLocalName(childMsg, "Scadenza", xmlns, SPCoopCostanti.NAMESPACE_EGOV)){
				//log.info("Validazione Scadenza...");

				if(presenzaScadenza){
					if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
						throw new StrutturaBustaException("Header egov con più di un elemento Scadenza","MessaggioScadenza");
					}else{
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.SCADENZA_PRESENTE_PIU_VOLTE);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						if(this.segnalazioneElementoPresentePiuVolte)
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
						else
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
						continue;
					}
				}else{
					
					presenzaScadenza = true;
					
					if (SoapUtils.getNotEmptyChildNodes(childMsg).size() != 1){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.SCADENZA_NON_PRESENTE);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
						continue;
					}
					String scadenza = null;
					try{
						scadenza = SoapUtils.getNotEmptyChildNodes(childMsg).get(0).getNodeValue();
					} catch(Exception e) {
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.SCADENZA_NON_VALIDA);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
						continue;
					}
					Date scadenzaDate = null; 
					if(scadenza != null){
						scadenzaDate = validazioneData(scadenza);
					}
					if( (scadenza==null) || (scadenzaDate == null)){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.SCADENZA_NON_VALORIZZATA);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
						continue;
					}
					if( dataScaduta(scadenza) == true){
						this.messaggioScaduto = true;
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.MESSAGGIO_SCADUTO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
					}
	
					//log.info("Scadenza["+scadenzaDate+"]");
					this.busta.setScadenza(scadenzaDate);
				}

			}else{ 
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione("IntestazioneMessaggio/Messaggio/child["+childMsg.getNodeName()+"]");
				this.erroriValidazione.add(ecc);
			}
		}

		if(presenzaID == false){ 
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
		}
		if(presenzaOra == false){ 
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_PRESENTE);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
		}
	}



	/**
	 * Metodo che si occupa di validare l'elemento 'ProfiloTrasmissione' <var>child</var> secondo specifica eGov.
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *    {@link Eccezione}, e viene inserito in errors.
	 *
	 * @param child nodo su cui effettuare la validazione
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneProfiloTrasmissione(Node child,String prefix) throws ProtocolException{

		//log.info("Validazione profilo Trasmissione...");

		if (SoapUtils.getNotEmptyChildNodes(child).size() != 0){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE+" child size:"+SoapUtils.getNotEmptyChildNodes(child).size());
			this.erroriValidazione.add(ecc);
		}
		String inoltro = null;
		String confermaRicezione = null;
		try{
			Node hrefFind = null;
			if(this.readQualifiedAttribute){
				hrefFind = SoapUtils.getQualifiedAttributeNode(child, "inoltro", SPCoopCostanti.NAMESPACE_EGOV);
			}else{
				hrefFind = SoapUtils.getAttributeNode(child, "inoltro");
			}
			inoltro = hrefFind.getNodeValue();
			//log.info("Inoltro ["+inoltro+"]");
		} catch(Exception e) {}
		try{
			Node hrefFind = null;
			if(this.readQualifiedAttribute){
				hrefFind = SoapUtils.getQualifiedAttributeNode(child, "confermaRicezione", SPCoopCostanti.NAMESPACE_EGOV);
			}else{
				hrefFind = SoapUtils.getAttributeNode(child, "confermaRicezione");
			}
			confermaRicezione = hrefFind.getNodeValue();
			//log.info("confermaRicezione ["+confermaRicezione+"]");
		} catch(Exception e) {}

		// Inoltro...
		if(inoltro == null ){
			// Default: EGOV_IT_PIUDIUNAVOLTA
			inoltro = SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI;
		} else {
			if( (inoltro.equals(SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI) == false ) &&
					(inoltro.equals(SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI) == false )  ){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE_FILTRO_DUPLICATI_SCONOSCIUTO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_INOLTRO.toString());
				this.erroriValidazione.add(ecc);
			}
		}

		// ConfermaRicezione...
		if(confermaRicezione == null ){
			// Default: false
			confermaRicezione = "false";
		}else{
			if( (confermaRicezione.equals("true") == false ) &&
					(confermaRicezione.equals("false") == false )  ){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE_SCONOSCIUTO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_CONFERMA_RICEZIONE.toString());
				this.erroriValidazione.add(ecc);
			}
		}

		// set
		this.busta.setInoltro(toInoltro(inoltro), inoltro);
		if(confermaRicezione.equals("true"))
			this.busta.setConfermaRicezione(true);
		else
			this.busta.setConfermaRicezione(false);

	}

	/**
	 * Metodo che si occupa di validare l'elemento 'Sequenza' <var>child</var> secondo specifica eGov.
	 * <p>
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *    {@link Eccezione}, e viene inserito in errors.
	 *
	 * @param child nodo su cui effettuare la validazione
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneSequenza(Node child,String prefix) throws StrutturaBustaException, ProtocolException{

		//log.info("Validazione sequenza...");

		if (SoapUtils.getNotEmptyChildNodes(child).size() != 0){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_VALIDA);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
		}
		String seq = null;
		try{
			Node hrefFind = null;
			if(this.readQualifiedAttribute){
				hrefFind = SoapUtils.getQualifiedAttributeNode(child, "numeroProgressivo", SPCoopCostanti.NAMESPACE_EGOV);
			}else{
				hrefFind = SoapUtils.getAttributeNode(child, "numeroProgressivo");
			}
			seq = hrefFind.getNodeValue();
			//log.info("Sequenza ["+seq+"]");
		} catch(Exception e) {}

		if(seq == null ){
			if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
				throw new StrutturaBustaException("Header egov senza elemento Sequenza numeroProgressivo","SequenzaNumeroProgressivo");
			} else {
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_VALORIZZATA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_NUMERO_PROGRESSIVO.toString());
				this.erroriValidazione.add(ecc);
				return;
			}
		}

		try{
			Long test = new Long(seq);
			this.busta.setSequenza(test.longValue());
		} catch(Exception e) {
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_VALIDA);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_NUMERO_PROGRESSIVO.toString());
			this.erroriValidazione.add(ecc);
		}


	}

	/**
	 * Metodo che si occupa di validare la lista Riscontri <var>child</var> secondo specifica eGov.
	 * <p>
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *    {@link Eccezione}, e viene inserito in errors.
	 *
	 * @param child nodo su cui effettuare la validazione
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneListaRiscontri(Node child,String prefix) throws StrutturaBustaException, ProtocolException{

		//log.info("Validazione Lista Riscontri...");
		Vector<Node> riscontri = SoapUtils.getNotEmptyChildNodes(child);

		if(riscontri.size() <= 0 ){		   
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALORIZZATA);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO.toString());
			this.erroriValidazione.add(ecc);
			return;
		}
		
		for(int i=0; i<riscontri.size();i++){

			boolean errorFind = false;

			Node riscontro = riscontri.get(i);
			//log.info("esamino["+child.getNodeName()+"]");
			//if(!(riscontro.getNodeName().equals(xmlns+"Riscontro"))){
			if(!(SoapUtils.matchLocalName(riscontro, "Riscontro", prefix, SPCoopCostanti.NAMESPACE_EGOV))){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE+"/childNode["+riscontro.getNodeName()+"]");
				this.erroriValidazione.add(ecc);
				continue;
			}

			Vector<Node> childsRiscontro = SoapUtils.getNotEmptyChildNodes(riscontro);
			/*if(childsRiscontro.getLength() != 2){		   
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(Costanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				ecc.setCodiceEccezione(Costanti.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA);
				ecc.setRilevanza(LivelloRilevanza.GRAVE);
				ecc.setDescrizione(Costanti.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO+" child size:"+childsRiscontro.getLength());
				this.erroriValidazione.add(ecc);
				continue;
			}*/

			boolean presenzaIdentificatore = false;
			boolean presenzaOraRegistrazione = false;
			
			String id = null;
			boolean findID = false;
			String ora = null;
			Date oraDate = null;
			boolean findOra = false;
			String tipoOra = null;
			boolean findTipoOra = false;
			for(int j=0; j<childsRiscontro.size();j++){
				Node elem = childsRiscontro.get(j);
				//if(elem.getNodeName().equals(xmlns+"Identificatore")){
				if(SoapUtils.matchLocalName(elem, "Identificatore", prefix, SPCoopCostanti.NAMESPACE_EGOV)){
					
					if(presenzaIdentificatore){
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento RiscontroIdentificatore","RiscontroIdentificatore");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_IDENTIFICATIVO_MESSAGGIO_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE.toString());
							this.erroriValidazione.add(ecc);
							continue;
						}
					}else{
					
						presenzaIdentificatore = true;
					
						try{
							id = SoapUtils.getNotEmptyChildNodes(elem).get(0).getNodeValue();
							findID = true;
						} catch(Exception e) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_IDENTIFICATIVO_MESSAGGIO_NON_VALIDO);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE.toString());
							this.erroriValidazione.add(ecc);
							errorFind = true;
						}
					}
				//}else if(elem.getNodeName().equals(xmlns+"OraRegistrazione")){
				}else if(SoapUtils.matchLocalName(elem, "OraRegistrazione", prefix, SPCoopCostanti.NAMESPACE_EGOV)){
					if(presenzaOraRegistrazione){
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento RiscontroOraRegistrazione","RiscontroOraRegistrazione");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_ORA_REGISTRAZIONE_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE.toString());
							this.erroriValidazione.add(ecc);
							continue;
						}
					}else{
						presenzaOraRegistrazione = true;
					
						try{
							ora = SoapUtils.getNotEmptyChildNodes(elem).get(0).getNodeValue();
							findOra = true;
						} catch(Exception e) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_ORA_REGISTRAZIONE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE.toString());
							this.erroriValidazione.add(ecc);
							errorFind = true;
						}
						oraDate = null;
						if(ora!=null){
							oraDate = validazioneData(ora);
						}
						if( (ora == null) || (oraDate==null)){
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_ORA_REGISTRAZIONE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE.toString());
							this.erroriValidazione.add(ecc);
							errorFind = true;
						}
	
						// tempo ...
						try{
							Node hrefFind = null;
							if(this.readQualifiedAttribute){
								hrefFind = SoapUtils.getQualifiedAttributeNode(elem, "tempo", SPCoopCostanti.NAMESPACE_EGOV);
							}else{
								hrefFind = SoapUtils.getAttributeNode(elem, "tempo");
							}
							tipoOra = hrefFind.getNodeValue();
							findTipoOra = true;
						} catch(Exception e) {
							if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
								throw new StrutturaBustaException("Header egov senza RiscontroOraRegistrazioneTempo","RiscontroOraRegistrazioneTempo");
							}else{
								Eccezione ecc = new Eccezione();
								ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
								ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_TIPO_ORA_REGISTRAZIONE_NON_VALIDO);
								ecc.setRilevanza(LivelloRilevanza.ERROR);
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE_TEMPO.toString());
								this.erroriValidazione.add(ecc);
								errorFind = true;
							}
						}
						if(tipoOra!=null){
							if(!tipoOra.equals(SPCoopCostanti.TIPO_TEMPO_LOCALE) && !tipoOra.equals(SPCoopCostanti.TIPO_TEMPO_SPC)){
								Eccezione ecc = new Eccezione();
								ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
								ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_TIPO_ORA_REGISTRAZIONE_SCONOSCIUTO);
								ecc.setRilevanza(LivelloRilevanza.ERROR);
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE_TEMPO.toString());
								this.erroriValidazione.add(ecc);
								errorFind = true;
							}
						}
					}
				}else {
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO+"/child["+elem.getNodeName()+"]");
					this.erroriValidazione.add(ecc);
					errorFind = true;
				}
			}

			if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
				if(presenzaIdentificatore == false){
					throw new StrutturaBustaException("Header egov senza RiscontroIdentificatore","RiscontroIdentificatore");
				}
				if(presenzaOraRegistrazione == false){
					throw new StrutturaBustaException("Header egov senza RiscontroOraRegistrazione","RiscontroOraRegistrazione");
				}
			}
			
			if(errorFind==false){
				if(findID != true){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE.toString());
					this.erroriValidazione.add(ecc);
				}
				if(findOra != true){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_ORA_REGISTRAZIONE_NON_PRESENTE);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE.toString());
					this.erroriValidazione.add(ecc);
				}else{
					if(findTipoOra != true){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_TIPO_ORA_REGISTRAZIONE_NON_PRESENTE);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE_TEMPO.toString());
						this.erroriValidazione.add(ecc);
					}
				}
			}
			

			//if(id!=null && ora!=null && tipoOra !=null) {
			if(id!=null || ora!=null || tipoOra !=null) {
				Riscontro r = new Riscontro();
				r.setID(id);		
				//log.info("ORA REC["+oraDate+"]");
				r.setOraRegistrazione(oraDate);
				r.setTipoOraRegistrazioneValue(tipoOra);
				r.setTipoOraRegistrazione(this.traduttore.toTipoOraRegistrazione(tipoOra));
				this.busta.addRiscontro(r);
			}
		}

	}


	/**
	 * Metodo che si occupa di validare la lista Trasmissioni <var>child</var> secondo specifica eGov.
	 * <p>
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *    {@link Eccezione}, e viene inserito in errors.
	 *
	 * @param child nodo su cui effettuare la validazione
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneListaTrasmissioni(Node child,String prefix) throws StrutturaBustaException, ProtocolException{
		
		//	log.info("Validazione Lista Trasmissioni...");
		Vector<Node> trasmissioni = SoapUtils.getNotEmptyChildNodes(child);

		if(trasmissioni.size()<=0){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALORIZZATA);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE.toString());
			this.erroriValidazione.add(ecc);
			return;
		}
		
		for(int i=0; i<trasmissioni.size();i++){

			boolean errorFind = false;

			Node trasmissione = trasmissioni.get(i);
			//log.info("esamino["+child.getNodeName()+"]");
			//if(!(trasmissione.getNodeName().equals(xmlns+"Trasmissione"))){
			if(!(SoapUtils.matchLocalName(trasmissione, "Trasmissione", prefix, SPCoopCostanti.NAMESPACE_EGOV))){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_NON_PRESENTE);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE+"/child["+trasmissione.getNodeName()+"]");
				this.erroriValidazione.add(ecc);
				continue;
			}
			Vector<Node> childsTrasmissione = SoapUtils.getNotEmptyChildNodes(trasmissione);
			/*if(childsTrasmissione.getLength() != 3){		   
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(Costanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				ecc.setCodiceEccezione(Costanti.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA);
				ecc.setRilevanza(LivelloRilevanza.GRAVE);
				ecc.setDescrizione(Costanti.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE+" child size:"+childsTrasmissione.getLength());
				this.erroriValidazione.add(ecc);
				continue;
			}*/

			boolean presenzaOrigine = false;
			boolean presenzaDestinazione = false;
			boolean presenzaOraRegistrazione = false;
			
			String origine = null;
			String tipoOrigine = null;
			String indTelematicoOrigine = null;
			boolean findOrigine = false;
			boolean findTipoOrigine = false;
			String destinazione = null;
			String tipoDestinazione = null;
			String indTelematicoDestinazione = null;
			boolean findDestinazione = false;
			boolean findTipoDestinazione = false;
			String ora = null;
			Date oraDate = null;
			boolean findOra = false;
			String tipoOra = null;
			boolean findTipoOra = false;
			for(int j=0; j<childsTrasmissione.size();j++){
				Node elem = childsTrasmissione.get(j);

				//if(elem.getNodeName().equals(xmlns+"Origine")){
				if(SoapUtils.matchLocalName(elem, "Origine", prefix, SPCoopCostanti.NAMESPACE_EGOV)){
					
					if(presenzaOrigine){
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento TrasmissioneOrigine","TrasmissioneOrigine");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORIGINE_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE.toString());
							this.erroriValidazione.add(ecc);
							continue;
						}
					}else{
					
						presenzaOrigine = true;
						
						if(SoapUtils.getNotEmptyChildNodes(elem).size() != 1){ 
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORIGINE_NON_PRESENTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(SoapUtils.getNotEmptyChildNodes(elem).size() < 1){ 
								if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
									throw new StrutturaBustaException("Header egov senza TrasmissioneOrigine IdentificativoParte","TrasmissioneOrigineIdentificativoParte");
								}else{
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE.toString());
								}
							}else{
								if(this.segnalazioneElementoPresentePiuVolte)
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
								else
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE.toString());
							}
							this.erroriValidazione.add(ecc);
							errorFind = true;
							continue;
						}
						Node idParte = SoapUtils.getNotEmptyChildNodes(elem).get(0);
						try{ 
							Vector<Node> valueIDParte = SoapUtils.getNotEmptyChildNodes(idParte);
							origine =  valueIDParte.get(0).getNodeValue();
							findOrigine = true;
						} catch(Exception e) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORIGINE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE.toString());
							this.erroriValidazione.add(ecc);
							errorFind = true;
						}
						try{ 
							Node hrefFind = null;
							if(this.readQualifiedAttribute){
								hrefFind = SoapUtils.getQualifiedAttributeNode(idParte, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
							}else{
								hrefFind = SoapUtils.getAttributeNode(idParte, "tipo");
							}
							tipoOrigine = hrefFind.getNodeValue();
							
							// Controllo tipo
							if(tipoOrigine==null){
								if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
									if(findTipoOrigine==false){
										throw new StrutturaBustaException("Header egov senza TrasmissioneOrigine IdentificativoParteTipo","TrasmissioneOrigineIdentificativoParteTipo");
									}
								}else{
									throw new Exception("Tipo non definito");
								}
							}
							
							if(this.tipiSoggetti.contains(tipoOrigine)==false){
								throw new Exception("Tipo non valido");
							}
							
							findTipoOrigine = true;
						} catch (StrutturaBustaException se){
							throw se;
						} catch(Exception e) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_TIPO_ORIGINE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_TIPO.toString());
							this.erroriValidazione.add(ecc);
							errorFind = true;
						}
	
						// indirizzo Telematico
						try{
							Node hrefFindT = null;
							if(this.readQualifiedAttribute){
								hrefFindT = SoapUtils.getQualifiedAttributeNode(idParte, "indirizzoTelematico", SPCoopCostanti.NAMESPACE_EGOV);
							}else{
								hrefFindT = SoapUtils.getAttributeNode(idParte, "indirizzoTelematico");
							}
							if(hrefFindT != null)
								indTelematicoOrigine = hrefFindT.getNodeValue();
							//log.info("IndirizzoTelematicoOrigine ["+indTelematico+"]");
						} catch(Exception e) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_INDIRIZZO_ORIGINE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
							this.erroriValidazione.add(ecc);
							errorFind = true;
						}
						if(errorFind==false && indTelematicoOrigine != null && (indTelematicoOrigine.equals("")==false)){ 
							URL urlcheck = null;
							try{	
								urlcheck = new URL( indTelematicoOrigine );
								indTelematicoOrigine = urlcheck.toString();
							} catch(Exception e) {
								Eccezione ecc = new Eccezione();
								ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
								ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_INDIRIZZO_ORIGINE_NON_VALIDA);
								ecc.setRilevanza(LivelloRilevanza.ERROR);
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
								this.erroriValidazione.add(ecc);
								errorFind = true;
							}
						}
					}

				//}else if(elem.getNodeName().equals(xmlns+"Destinazione")){
				}else if(SoapUtils.matchLocalName(elem, "Destinazione", prefix, SPCoopCostanti.NAMESPACE_EGOV)){

					if(presenzaDestinazione){
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento TrasmissioneDestinazione","TrasmissioneDestinazione");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_DESTINAZIONE_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE.toString());
							this.erroriValidazione.add(ecc);
							continue;
						}
					}else{
					
						presenzaDestinazione = true;
					
						if(SoapUtils.getNotEmptyChildNodes(elem).size() != 1){ 
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_DESTINAZIONE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(SoapUtils.getNotEmptyChildNodes(elem).size() < 1){ 
								if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
									throw new StrutturaBustaException("Header egov senza TrasmissioneDestinazione IdentificativoParte","TrasmissioneDestinazioneIdentificativoParte");
								}else{
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE.toString());
								}
							}else{
								if(this.segnalazioneElementoPresentePiuVolte)
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
								else
									ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE.toString());
							}
							this.erroriValidazione.add(ecc);
							errorFind = true;
							continue;
						}
						Node idParte = SoapUtils.getNotEmptyChildNodes(elem).get(0);
						try{ 
							Vector<Node> valueIDParte = SoapUtils.getNotEmptyChildNodes(idParte);
							destinazione =  valueIDParte.get(0).getNodeValue();
							findDestinazione = true;
						} catch(Exception e) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_DESTINAZIONE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE.toString());
							this.erroriValidazione.add(ecc);
							errorFind = true;
						}
						try{ 
							Node hrefFind = null;
							if(this.readQualifiedAttribute){
								hrefFind = SoapUtils.getQualifiedAttributeNode(idParte, "tipo", SPCoopCostanti.NAMESPACE_EGOV);
							}else{
								hrefFind = SoapUtils.getAttributeNode(idParte, "tipo");
							}
							tipoDestinazione = hrefFind.getNodeValue();
							
							// Controllo tipo
							if(tipoDestinazione==null){
								if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
									if(findTipoOrigine==false){
										throw new StrutturaBustaException("Header egov senza TrasmissioneDestinazione IdentificativoParteTipo","TrasmissioneDestinazioneIdentificativoParteTipo");
									}
								}else{
									throw new Exception("Tipo non definito");
								}
							}
							if(this.tipiSoggetti.contains(tipoDestinazione)==false){
								throw new Exception("Tipo non valido");
							}
							
							findTipoDestinazione = true;
						} catch (StrutturaBustaException se){
							throw se;
						} catch(Exception e) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_TIPO_DESTINAZIONE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_TIPO.toString());
							this.erroriValidazione.add(ecc);
							errorFind = true;
						}
	
						// indirizzo Telematico
						try{
							Node hrefFindT = null;
							if(this.readQualifiedAttribute){
								hrefFindT = SoapUtils.getQualifiedAttributeNode(idParte, "indirizzoTelematico", SPCoopCostanti.NAMESPACE_EGOV);
							}else{
								hrefFindT = SoapUtils.getAttributeNode(idParte, "indirizzoTelematico");
							}
							if(hrefFindT != null)
								indTelematicoDestinazione = hrefFindT.getNodeValue();
							//log.info("IndirizzoTelematicoDestinazione ["+indTelematico+"]");
						} catch(Exception e) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_INDIRIZZO_DESTINAZIONE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
							this.erroriValidazione.add(ecc);
							errorFind = true;
						}
						if(errorFind==false && indTelematicoDestinazione != null && (indTelematicoDestinazione.equals("")==false)){ 
							URL urlcheck = null;
							try{	
								urlcheck = new URL( indTelematicoDestinazione );
								indTelematicoDestinazione = urlcheck.toString();
							} catch(Exception e) {
								Eccezione ecc = new Eccezione();
								ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
								ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_INDIRIZZO_DESTINAZIONE_NON_VALIDA);
								ecc.setRilevanza(LivelloRilevanza.ERROR);
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
								this.erroriValidazione.add(ecc);
								errorFind = true;
							}
						}
					}

				//}else if(elem.getNodeName().equals(xmlns+"OraRegistrazione")){
				}else if(SoapUtils.matchLocalName(elem, "OraRegistrazione", prefix, SPCoopCostanti.NAMESPACE_EGOV)){

					if(presenzaOraRegistrazione){
						if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
							throw new StrutturaBustaException("Header egov con più di un elemento TrasmissioneOraRegistrazione","TrasmissioneOraRegistrazione");
						}else{
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORA_REGISTRAZIONE_PRESENTE_PIU_VOLTE);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							if(this.segnalazioneElementoPresentePiuVolte)
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
							else
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE.toString());
							this.erroriValidazione.add(ecc);
							continue;
						}
					}else{
					
						presenzaOraRegistrazione = true;
					
						// ora ...
						try{
							ora = SoapUtils.getNotEmptyChildNodes(elem).get(0).getNodeValue();
							findOra = true;
						} catch(Exception e) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORA_REGISTRAZIONE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE.toString());
							this.erroriValidazione.add(ecc);
							errorFind = true;
						}
						oraDate = null;
						if(ora != null)
							oraDate = validazioneData(ora);
						if( (ora == null) || (oraDate == null)){
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORA_REGISTRAZIONE_NON_VALIDA);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE.toString());
							this.erroriValidazione.add(ecc);
							errorFind = true;
						}
	
						// tempo ...
						try{
							Node hrefFind = null;
							if(this.readQualifiedAttribute){
								hrefFind = SoapUtils.getQualifiedAttributeNode(elem, "tempo", SPCoopCostanti.NAMESPACE_EGOV);
							}else{
								hrefFind = SoapUtils.getAttributeNode(elem, "tempo");
							}
							tipoOra = hrefFind.getNodeValue();
							findTipoOra = true;
						} catch(Exception e) {
							if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
								if(findTipoOrigine==false){
									throw new StrutturaBustaException("Header egov senza Trasmissione OraRegistrazioneTempo","TrasmissioneOraRegistrazioneTempo");
								}
							}else{
								Eccezione ecc = new Eccezione();
								ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
								ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_NON_VALIDA);
								ecc.setRilevanza(LivelloRilevanza.ERROR);
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE_TEMPO.toString());
								this.erroriValidazione.add(ecc);
								errorFind = true;
							}
						}
						if(tipoOra!=null){
							if(!tipoOra.equals(SPCoopCostanti.TIPO_TEMPO_LOCALE) && !tipoOra.equals(SPCoopCostanti.TIPO_TEMPO_SPC)){
								Eccezione ecc = new Eccezione();
								ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
								ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_SCONOSCIUTA);
								ecc.setRilevanza(LivelloRilevanza.ERROR);
								ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE_TEMPO.toString());
								this.erroriValidazione.add(ecc);
								errorFind = true;
							}
						}
					}
				}else {
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE+"/child["+elem.getNodeName()+"]");
					this.erroriValidazione.add(ecc);
					errorFind = true;
				}

			}
			
			
			// Check elementi principali
			if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
				if(presenzaOrigine==false){
					throw new StrutturaBustaException("Header egov senza TrasmissioneOrigine","TrasmissioneOrigine");
				}
				if(presenzaDestinazione==false){
					throw new StrutturaBustaException("Header egov senza TrasmissioneDestinazione","TrasmissioneDestinazione");
				}
				if(presenzaOraRegistrazione==false){
					throw new StrutturaBustaException("Header egov senza TrasmissioneOraRegistrazione","TrasmissioneOraRegistrazione");
				}
			}
			
			if(errorFind == false){
				if(presenzaOrigine==false){
					errorFind = true;
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORIGINE_NON_PRESENTE);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE.toString());
					this.erroriValidazione.add(ecc);
				}
				if(presenzaDestinazione==false){
					errorFind = true;
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_DESTINAZIONE_NON_PRESENTE);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE.toString());
					this.erroriValidazione.add(ecc);
				}
				if(presenzaOraRegistrazione==false){
					errorFind = true;
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORA_REGISTRAZIONE_NON_PRESENTE);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE.toString());
					this.erroriValidazione.add(ecc);
				}
			}
			
			if(errorFind == false){
				if(findOrigine != true){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORIGINE_NON_PRESENTE);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE.toString());
					this.erroriValidazione.add(ecc);
				}else{
					if(findTipoOrigine != true){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_TIPO_ORIGINE_NON_PRESENTE);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_TIPO.toString());
						this.erroriValidazione.add(ecc);
					}
				}
				if(findDestinazione != true){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_DESTINAZIONE_NON_PRESENTE);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE.toString());
					this.erroriValidazione.add(ecc);
				}else{
					if(findTipoDestinazione != true){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_TIPO_DESTINAZIONE_NON_PRESENTE);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_TIPO.toString());
						this.erroriValidazione.add(ecc);
					}
				}
				if(findOra != true){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORA_REGISTRAZIONE_NON_PRESENTE);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE.toString());
					this.erroriValidazione.add(ecc);
				}else{
					if(findTipoOra != true){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_NON_PRESENTE);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE_TEMPO.toString());
						this.erroriValidazione.add(ecc);
					}
				}
			}
			
			//if(origine!=null && tipoOrigine!=null && tipoDestinazione!=null && destinazione!=null && 
			//		ora!=null && tipoOra != null) {
			if(origine!=null || tipoOrigine!=null || tipoDestinazione!=null || destinazione!=null || 
					ora!=null || tipoOra != null) {
				//log.info("add Trasmissione....");
				Trasmissione tr = new Trasmissione();
				tr.setOrigine(origine);
				tr.setTipoOrigine(tipoOrigine);
				tr.setIndirizzoOrigine(indTelematicoOrigine);
				tr.setDestinazione(destinazione);
				tr.setTipoDestinazione(tipoDestinazione);
				tr.setIndirizzoDestinazione(indTelematicoDestinazione);
				//log.info("ORA REC["+oraDate+"]");
				tr.setOraRegistrazione(oraDate);
				tr.setTempoValue(tipoOra);
				tr.setTempo(this.traduttore.toTipoOraRegistrazione(tipoOra));
				this.busta.addTrasmissione(tr);
			}
		}

	}

	/**
	 * Metodo che si occupa di validare la lista Eccezioni <var>child</var> secondo specifica eGov.
	 * <p>
	 * Il metodo, oltre ad effettuare la validazione, si occupa anche di leggerne il contenuto e 
	 * di impostarlo all'interno dell'oggetto <var>busta</var>.
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *    {@link Eccezione}, e viene inserito in un errorsEccezione.
	 *
	 * @param child nodo su cui effettuare la validazione
	 * @throws ProtocolException 
	 * 
	 */
	private void validazioneListaEccezioni(Node child,String prefix) throws StrutturaBustaException, ProtocolException{

		//log.info("Validazione Lista Eccezioni...");
		Vector<Node> eccezioni = SoapUtils.getNotEmptyChildNodes(child);

		for(int i=0; i<eccezioni.size();i++){

			boolean errorFind = false;

			Node eccezione = eccezioni.get(i);
			//log.info("esamino["+child.getNodeName()+"]");
			//if(!(eccezione.getNodeName().equals(xmlns+"Eccezione"))){
			if(!(SoapUtils.matchLocalName(eccezione, "Eccezione", prefix, SPCoopCostanti.NAMESPACE_EGOV))){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE+"/child["+eccezione.getNodeName()+"]");
				this.errorsTrovatiSullaListaEccezioni.add(ecc);
				errorFind = true;
			}

			String contestoCodifica = null;
			String codiceEccezione = null;
			String rilevanza = null;
			String posizione = null;
			

			// ContestoCodifica
			boolean contestoCodificaError = false;
			try{
				Node hrefFind = null;
				if(this.readQualifiedAttribute){
					hrefFind = SoapUtils.getQualifiedAttributeNode(eccezione, "contestoCodifica", SPCoopCostanti.NAMESPACE_EGOV);
				}else{
					hrefFind = SoapUtils.getAttributeNode(eccezione, "contestoCodifica");
				}
				contestoCodifica = hrefFind.getNodeValue();
			} catch(Exception e) {
				contestoCodificaError = true;
			}
			if(contestoCodifica == null)
				contestoCodificaError = true;
			if(contestoCodificaError){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_CONTESTO_CODIFICA.toString());
				this.errorsTrovatiSullaListaEccezioni.add(ecc);
				errorFind = true;
			}

			// codiceEccezione
			boolean codiceEccezioneError = false;
			try{
				Node hrefFind = null;
				if(this.readQualifiedAttribute){
					hrefFind = SoapUtils.getQualifiedAttributeNode(eccezione, "codiceEccezione", SPCoopCostanti.NAMESPACE_EGOV);
				}else{
					hrefFind = SoapUtils.getAttributeNode(eccezione, "codiceEccezione");
				}
				codiceEccezione = hrefFind.getNodeValue();
			} catch(Exception e) {
				codiceEccezioneError = true;
			}
			if(validazioneCodiceEccezione(codiceEccezione) == false)
				codiceEccezioneError = true;
			if(codiceEccezioneError){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_CODICE_ECCEZIONE.toString());
				this.errorsTrovatiSullaListaEccezioni.add(ecc);
				errorFind = true;
			}

			// Rilevanza
			boolean rilevanzaError = false;
			try{
				Node hrefFind = null;
				if(this.readQualifiedAttribute){
					hrefFind = SoapUtils.getQualifiedAttributeNode(eccezione, "rilevanza", SPCoopCostanti.NAMESPACE_EGOV);
				}else{
					hrefFind = SoapUtils.getAttributeNode(eccezione, "rilevanza");
				}
				rilevanza = hrefFind.getNodeValue();
			} catch(Exception e) {
				rilevanzaError = true;
			}
			LivelloRilevanza livRilevanza = validazioneRilevanza(rilevanza);
			if(livRilevanza.equals(LivelloRilevanza.UNKNOWN))
				rilevanzaError = true;
			if(rilevanzaError){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_RILEVANZA.toString());
				this.errorsTrovatiSullaListaEccezioni.add(ecc);
				errorFind = true;
			}

			// Posizione
			boolean posizioneError = false;
			try{
				Node hrefFind = null;
				if(this.readQualifiedAttribute){
					hrefFind = SoapUtils.getQualifiedAttributeNode(eccezione, "posizione", SPCoopCostanti.NAMESPACE_EGOV);
				}else{
					hrefFind = SoapUtils.getAttributeNode(eccezione, "posizione");
				}
				posizione = hrefFind.getNodeValue();
			} catch(Exception e) {
				posizioneError = true;
			}
			if(posizione == null)
				posizioneError = true;
			if(posizioneError){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_POSIZIONE.toString());
				this.errorsTrovatiSullaListaEccezioni.add(ecc);
				errorFind = true;
			}

			if(this.spcoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo()==false){
				if(contestoCodifica == null){
					throw new StrutturaBustaException("Header egov senza Eccezione contestoCodifica","EccezioneContestoCodifica");
				}
				if(codiceEccezione == null){
					throw new StrutturaBustaException("Header egov senza Eccezione codiceEccezione","EccezioneCodiceEccezione");
				}
				if(rilevanza == null){
					throw new StrutturaBustaException("Header egov senza Eccezione rilevanza","EccezioneRilevanza");
				}
				if(posizione == null){
					throw new StrutturaBustaException("Header egov senza Eccezione posizione","EccezionePosizione");
				}
			}
			
			// Check finale
			if(errorFind == false){
				if(contestoCodifica == null){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_CONTESTO_CODIFICA.toString());
					this.errorsTrovatiSullaListaEccezioni.add(ecc);
				}
				if(codiceEccezione == null){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_CODICE_ECCEZIONE.toString());
					this.errorsTrovatiSullaListaEccezioni.add(ecc);
				}
				if(rilevanza == null){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_RILEVANZA.toString());
					this.errorsTrovatiSullaListaEccezioni.add(ecc);
				}
				if(posizione == null){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.INTESTAZIONE_NON_CORRETTA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_POSIZIONE.toString());
					this.errorsTrovatiSullaListaEccezioni.add(ecc);
				}
			}
			
			if(contestoCodifica!=null && codiceEccezione!=null && rilevanza !=null && posizione!=null) {
				Eccezione eccLetta = new Eccezione();
				
				eccLetta.setContestoCodificaValue(contestoCodifica);
				eccLetta.setContestoCodifica(this.traduttore.toContestoCodificaEccezione(contestoCodifica));
				
				eccLetta.setCodiceEccezioneValue(codiceEccezione);
				eccLetta.setCodiceEccezione(this.traduttore.toCodiceErroreCooperazione(codiceEccezione));
								
				eccLetta.setRilevanza(livRilevanza);
				eccLetta.setRilevanzaValue(rilevanza);
				
				eccLetta.setDescrizione(posizione);
				
				this.busta.addEccezione(eccLetta);
			}
		}

	}

	/**
	 * Metodo che si occupa di validare un codice di rilevanza associato ad una Eccezione. 
	 *
	 * @param codice Codice della rilevanza da validare.
	 * @return true se il codice e' valido, false altrimenti
	 * @throws ProtocolException 
	 * 
	 */
	private LivelloRilevanza validazioneRilevanza(String codice) throws ProtocolException{
		return this.traduttore.toLivelloRilevanza(codice);
	}

	/**
	 * Metodo che si occupa di validare un codice di errore associato ad una Eccezione. 
	 *
	 * @param codice Codice dell'eccezione da validare.
	 * @return true se il codice e' valido, false altrimenti
	 * 
	 */

	public boolean validazioneCodiceEccezione(CodiceErroreCooperazione codice){
		String v = this.traduttore.toString(codice);
		return v!=null;
	}
	
	public boolean validazioneCodiceEccezione(String codicetxt) throws ProtocolException{
		CodiceErroreCooperazione codice = CodiceErroreCooperazione.toCodiceErroreCooperazione(this.protocolFactory, codicetxt);
		return validazioneCodiceEccezione(codice);
	}

	/**
	 * Metodo che si occupa di validare il Fault presente in un Messaggio SPCoop Errore.
	 *
	 * @param body SOAPBody contenente il SOAPFault.
	 * 
	 */
	public void validazioneFaultEGov(SOAPBody body){

		try{
			//Traduttore trasl = this.protocolFactory.createTraduttore();

			//log.info("VALIDAZIONE FAULT");
			if(body.hasFault() == false){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT.toString());
				this.errorsTrovatiSullaListaEccezioni.add(ecc);
			} else {
				SOAPFault fault = body.getFault();
				boolean valoriOK = true;

				// Controllo sul Fault
				String faultS = fault.getFaultString();
				if(faultS!=null)
					faultS = faultS.trim();
				if( !(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO.equals(faultS)) && 
						!(SPCoopCostanti.FAULT_STRING_VALIDAZIONE.equals(faultS))  ){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT_STRING.toString());
					this.errorsTrovatiSullaListaEccezioni.add(ecc);
					valoriOK = false;
				}
				Name codeS = fault.getFaultCodeAsName();
				
				if( 
						!(
								("Client".equals(codeS.getLocalName()) || "Server".equals(codeS.getLocalName()) ) && 
								Costanti.SOAP_ENVELOPE_NAMESPACE.equalsIgnoreCase(codeS.getURI()) 
						)
				)
				{
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT_CODE.toString());
					this.errorsTrovatiSullaListaEccezioni.add(ecc);
					valoriOK = false;
				}
				if(valoriOK){
					if ("Client".equals(codeS.getLocalName()) ){
						boolean detailPresente = false;
						Detail d = fault.getDetail();
						if(d!=null){
							if(d.getDetailEntries().hasNext())
								detailPresente = true;
						}	
						// Con soap:Client, non ci deveno essere Detail, e ci deve essere FAULT_STRING_VALIDAZIONE_SPCOOP!
						if ( !(SPCoopCostanti.FAULT_STRING_VALIDAZIONE.equals(faultS)) ) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT_STRING.toString());
							this.errorsTrovatiSullaListaEccezioni.add(ecc);
						}
						if (detailPresente) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT_DETAILS_PRESENTI.toString());
							this.errorsTrovatiSullaListaEccezioni.add(ecc);
						}
					}
					if ("Server".equals(codeS.getLocalName()) ){
						// Con soap:Server, ci deve essere FAULT_STRING_PROCESSAMENTO_SPCOOP!
						if ( !(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO.equals(faultS)) ) {
							Eccezione ecc = new Eccezione();
							ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
							ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO);
							ecc.setRilevanza(LivelloRilevanza.ERROR);
							ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT_STRING.toString());
							this.errorsTrovatiSullaListaEccezioni.add(ecc);
						}
					}
				}
			}
		}catch(Exception e){
			this.log.error("Errore durante la validazione del SoapFault",e);
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione("Validazione SoapFault: errore di processamento");
			this.erroriProcessamento.add(ecc);
			return;
		}
	}



	/**
	 * Metodo che si occupa di validare il Manifest di un SoapBody.
	 *
	 * @param msg SOAPMessage contenente il Manifest.
	 * 
	 */
	public void validazioneManifestAttachmentsEGov(OpenSPCoop2Message msg,ProprietaManifestAttachments proprietaManifestAttachments){
		//log.info("VALIDAZIONE MANIFEST");
		SOAPBody soapBody = null;
		try{
			java.util.Vector<String> contentID = new java.util.Vector<String>();
			java.util.Vector<String> contentLocation = new java.util.Vector<String>();
			java.util.Iterator<?> it = msg.getAttachments();
			
			while(it.hasNext()){
				AttachmentPart ap = (AttachmentPart) it.next();
				contentID.add(msg.getContentID(ap));
				contentLocation.add(ap.getContentLocation());
				//log.info("Attachments con ID["+ap.getContentId()+"] e Location["+ap.getContentLocation()+"]");
			}



			boolean isRichiesta = false;
			boolean isRisposta = false;

			soapBody = msg.getSOAPBody();
			Vector<Node> soapBodyList = SoapUtils.getNotEmptyChildNodes(soapBody);
			if(soapBodyList.size()!=1){
				// Il body deve possedere esattamente un elemento
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE+" child size:"+soapBodyList.size());
				this.erroriValidazione.add(ecc);
				return;
			}

			//log.info("Validazione Descrizione...");
			Node descrizione = soapBodyList.get(0);
			String prefixManifest = descrizione.getPrefix();
			if(prefixManifest== null)
				prefixManifest = "";
			if("".equals(prefixManifest)==false)
				prefixManifest = prefixManifest + ":";
			//log.info("esamino["+descrizione.getNodeName()+"]");
			//if(!(descrizione.getNodeName().equals(prefixManifest+"Descrizione"))){
			if(!(SoapUtils.matchLocalName(descrizione, "Descrizione", prefixManifest, SPCoopCostanti.NAMESPACE_EGOV))){		
				// Elemento Descrizione non esistente
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE.toString());
				this.erroriValidazione.add(ecc);
				return;
			}
			if(SPCoopCostanti.NAMESPACE_EGOV.equals(descrizione.getNamespaceURI())==false){
				// Elemento Descrizione non esistente
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_NAMESPACE.toString());
				this.erroriValidazione.add(ecc);
				return;
			}




			Vector<Node> descrizioneMessaggi = SoapUtils.getNotEmptyChildNodes(descrizione);
			if(descrizioneMessaggi.size()<=0){
				//	Elemento DescrizioneMessaggio non esistente
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE+" child size:"+descrizioneMessaggi.size());
				this.erroriValidazione.add(ecc);
				return;
			}
			for(int i=0; i<descrizioneMessaggi.size();i++){

				//log.info("Validazione DescrizioneMessaggio...");
				Node descrizioneMessaggio = descrizioneMessaggi.get(i);
				//log.info("esamino["+descrizioneMessaggio.getNodeName()+"]");
				//if(!(descrizioneMessaggio.getNodeName().equals(prefixManifest+SPCoopCostanti.LOCAL_NAME_MANIFEST_EGOV_DESCRIZIONE_MESSAGGIO))){
				if(!(SoapUtils.matchLocalName(descrizioneMessaggio, SPCoopCostanti.LOCAL_NAME_MANIFEST_EGOV_DESCRIZIONE_MESSAGGIO, prefixManifest, SPCoopCostanti.NAMESPACE_EGOV))){
					//	Elemento DescrizioneMessaggio non esistente
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE+"/child["+descrizioneMessaggio.getNodeName()+"]");
					this.erroriValidazione.add(ecc);
					continue;
				}

				//log.info("Validazione Riferimento...");
				Vector<Node> riferimentoList = SoapUtils.getNotEmptyChildNodes(descrizioneMessaggio);
				if(riferimentoList.size()!=1){
					//	Elemento Riferimento non esistente
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO +" child size:"+riferimentoList.size());
					this.erroriValidazione.add(ecc);
					continue;
				}
				Node riferimento = riferimentoList.get(0);
				//log.info("esamino["+riferimento.getNodeName()+"]");
				//if(!(riferimento.getNodeName().equals(prefixManifest+"Riferimento"))){
				if(!(SoapUtils.matchLocalName(riferimento, "Riferimento", prefixManifest, SPCoopCostanti.NAMESPACE_EGOV))){
					//	Elemento Riferimento non esistente
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI_RIFERIMENTO_NON_PRESENTE);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO+"/child["+riferimento.getNodeName()+"]");
					this.erroriValidazione.add(ecc);
					continue;
				}

				// href
				String href = null;
				boolean hrefError = false;
				try{
					Node hrefFind = null;
					if(this.readQualifiedAttribute){
						hrefFind = SoapUtils.getQualifiedAttributeNode(riferimento, "href", SPCoopCostanti.NAMESPACE_EGOV);
					}else{
						hrefFind = SoapUtils.getAttributeNode(riferimento, "href");
					}
					href = hrefFind.getNodeValue();
				} catch(Exception e) {
					hrefError = true;
				}
				if(href == null)
					hrefError = true;
				if(hrefError){
					//	Elemento Riferimento non esistente
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI_RIFERIMENTO_NON_VALIDO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO_HREF.toString());
					this.erroriValidazione.add(ecc);
					continue;
				}
				// VALIDAZIONE HREF
				
				
				
				boolean findHRef = false;
				for(int k=0; k<contentID.size(); k++){
					if(href.compareTo(contentID.get(k) + "") == 0 || 
							href.compareTo("cid:"+contentID.get(k) + "") == 0||
							href.compareTo(contentLocation.get(k) + "") == 0){
						contentID.remove(k);
						contentLocation.remove(k);
						findHRef = true;
						break;
					}
				}
				if(findHRef == false){
					//	RiferimentoAttachment non esistente
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATO_NON_PRESENTE);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO_HREF+"["+href+"]");
					this.erroriValidazione.add(ecc);
				}


				// role
				String role = null;
				boolean roleError = false;
				try{
					Node roleFind = null;
					if(this.readQualifiedAttribute){
						roleFind = SoapUtils.getQualifiedAttributeNode(riferimento, "role", SPCoopCostanti.NAMESPACE_EGOV);
					}else{
						roleFind = SoapUtils.getAttributeNode(riferimento, "role");
					}
					role = roleFind.getNodeValue();
				} catch(Exception e) {
					roleError = true;
				}
				if(role == null)
					roleError = true;
				if(roleError){
					//	Elemento Riferimento non esistente
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO_ROLE.toString());
					this.erroriValidazione.add(ecc);
					continue;
				}

				// CHECK Semantica Ruolo
				if(this.spcoopProperties.getRoleRichiestaManifest().equalsIgnoreCase(role) ||
						this.spcoopProperties.getRoleRispostaManifest().equalsIgnoreCase(role)	){
					if(isRichiesta || isRisposta){
						//	Doppio elemento 'Richiesta/Risposta'
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						// uso il riferiemnto non valido per farlo tradurre con codice 118 e quindi farlo gestire bene ad InoltroBuste, per non far gestire il manifest in fase di sbustamento
						ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI_RIFERIMENTO_NON_VALIDO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						// (Manifest degli attachments) Esistono più di una descrizione di messaggio
						// con 'role' discriminante ('"+Costanti.ATTACHMENTS_MANIFEST_RICHIESTA+"'/'"+Costanti.ATTACHMENTS_MANIFEST_RISPOSTA+"').
						String posizioneErrore = SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO_ROLE_PRINCIPALE_DUPLICATO.toString();
						posizioneErrore = posizioneErrore.replace(SPCoopCostanti.MANIFEST_KEY_ROLE_RICHIESTA, this.spcoopProperties.getRoleRichiestaManifest());
						posizioneErrore = posizioneErrore.replace(SPCoopCostanti.MANIFEST_KEY_ROLE_RISPOSTA, this.spcoopProperties.getRoleRispostaManifest());
						ecc.setDescrizione(posizioneErrore);
						this.erroriValidazione.add(ecc);
						continue;
					}
					if(this.spcoopProperties.getRoleRichiestaManifest().equalsIgnoreCase(role))
						isRichiesta = true;
					else
						isRisposta = true;
				}


				// id
				String id = null;
				boolean idError = false;
				try{
					// Deve essere sempre preso come qualificato.
					Node idFind = SoapUtils.getQualifiedAttributeNode(riferimento, "id", SPCoopCostanti.NAMESPACE_EGOV);
					id = idFind.getNodeValue();
				} catch(Exception e) {
					idError = true;
				}
				if(id == null)
					idError = true;
				if(idError){
					//	Elemento Riferimento non esistente
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO_ID.toString());
					this.erroriValidazione.add(ecc);
					continue;
				}


				// check elementi Schema e Titolo
				Vector<Node> elementiRiferimento = SoapUtils.getNotEmptyChildNodes(riferimento);
				if(elementiRiferimento.size()!=2){
					//	Elemento Schema non esistente
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO+" child size:"+elementiRiferimento.size());
					this.erroriValidazione.add(ecc);
					continue;
				}

				Node child = elementiRiferimento.get(0);
				Node schema = null;
				Node titolo = null;
				//if(child.getNodeName().equals(prefixManifest+"Schema")){
				if(SoapUtils.matchLocalName(child, "Schema", prefixManifest, SPCoopCostanti.NAMESPACE_EGOV)){
					schema = child;
					titolo = elementiRiferimento.get(1);				
				//}else if(child.getNodeName().equals(prefixManifest+"Titolo")){
				}else if(SoapUtils.matchLocalName(child, "Titolo", prefixManifest, SPCoopCostanti.NAMESPACE_EGOV)){	
					titolo = child;
					schema = elementiRiferimento.get(1);
				}else{
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO+"/child["+child.getNodeName()+"]");
					this.erroriValidazione.add(ecc);
					continue;
				}


				//log.info("Validazione Schema...");			
				//log.info("esamino["+schema.getNodeName()+"]");
				//if(!(schema.getNodeName().equals(prefixManifest+"Schema"))){
				if(!(SoapUtils.matchLocalName(schema, "Schema", prefixManifest, SPCoopCostanti.NAMESPACE_EGOV))){
					//	Elemento Riferimento non esistente
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO_SCHEMA.toString());
					this.erroriValidazione.add(ecc);
					continue;
				}

				// posizione
				String posizione = null;
				boolean posizioneError = false;
				try{
					Node posizioneFind = null;
					if(this.readQualifiedAttribute){
						posizioneFind = SoapUtils.getQualifiedAttributeNode(schema, "posizione", SPCoopCostanti.NAMESPACE_EGOV);
					}else{
						posizioneFind = SoapUtils.getAttributeNode(schema, "posizione");
					}
					posizione = posizioneFind.getNodeValue();
				} catch(Exception e) {
					posizioneError = true;
				}
				if(posizione == null)
					posizioneError = true;
				if(posizioneError){
					//	Elemento Riferimento non esistente
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO_SCHEMA_POSIZIONE.toString());
					this.erroriValidazione.add(ecc);
					continue;
				}



				//log.info("Validazione Titolo...");
				//log.info("esamino["+titolo.getNodeName()+"]");
				//if(!(titolo.getNodeName().equals(prefixManifest+"Titolo"))){
				if(!(SoapUtils.matchLocalName(titolo, "Titolo", prefixManifest, SPCoopCostanti.NAMESPACE_EGOV))){
					//	Elemento Riferimento non esistente
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO_TITOLO.toString());
					this.erroriValidazione.add(ecc);
					continue;
				}

				// Lingua
				String Lingua = null;
				try{
					Node LinguaFind = null;
					if(this.readQualifiedAttribute){
						LinguaFind = SoapUtils.getQualifiedAttributeNode(titolo, "Lingua", SPCoopCostanti.NAMESPACE_EGOV);
					}else{
						LinguaFind = SoapUtils.getAttributeNode(titolo, "Lingua");
					}
					Lingua = LinguaFind.getNodeValue();
				} catch(Exception e) {}
				if(Lingua == null)
					Lingua = "it"; // default is it

				String valoreTitolo = null;
				try{
					valoreTitolo = SoapUtils.getNotEmptyChildNodes(titolo).get(0).getNodeValue();
					if(valoreTitolo == null)
						throw new Exception("valore non presente");
				} catch(Exception e) {
					//	Elemento Titolo con scorretto valore
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO_TITOLO.toString());
					this.erroriValidazione.add(ecc);
					continue;
				}
			}

			// Validazione role Richiesta/Risposta
			if( isRichiesta==false && isRisposta ==false){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				// uso il riferiemnto non valido per farlo tradurre con codice 118 e quindi farlo gestire bene ad InoltroBuste, per non far gestire il manifest in fase di sbustamento
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATI_RIFERIMENTO_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				String posizioneErrore = SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO_ROLE_PRINCIPALE_ASSENTE.toString();
				posizioneErrore = posizioneErrore.replace(SPCoopCostanti.MANIFEST_KEY_ROLE_RICHIESTA, this.spcoopProperties.getRoleRichiestaManifest());
				posizioneErrore = posizioneErrore.replace(SPCoopCostanti.MANIFEST_KEY_ROLE_RISPOSTA, this.spcoopProperties.getRoleRispostaManifest());
				ecc.setDescrizione(posizioneErrore);
				this.erroriValidazione.add(ecc);
				return;
			}


			// Validazione href
			while(contentID.size()>0){
				//	RiferimentoAttachment non definito in manifest
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ALLEGATO_NON_DEFINITO_MANIFEST);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				String href = contentID.remove(0);
				if(href==null)
					href = contentLocation.remove(0);
				else
					contentLocation.remove(0);
				// (Manifest degli attachments) Allegato con id/location ["+href+"] non riferito attraverso opportuno elemento nel Manifest degli attachments.
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MANIFEST_POSIZIONE_DESCRIZIONE_DESCRIZIONE_MESSAGGIO_RIFERIMENTO_HREF+"["+href+"] non presente");
				this.erroriValidazione.add(ecc);
			}
			return;

		}catch(Exception e){
			this.log.error("Errore durante la validazione del Manifest degli attachments",e);
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione("Manifest degli attachments: errore di processamento");
			this.erroriProcessamento.add(ecc);
			return;
		}
		finally{
			// *** GB ***
			soapBody = null;
			// *** GB ***
		}

	}




	/**
	 * Metodo che si occupa di validare la data <var>aDate</var>
	 * secondo la specifica eGov.
	 * La Data deve essere formato da :
	 * aaaa-mm-ggThh:mm:ss[.SSS]    (es. il 23/03/2005 alle 13:56:01 viene registrato come 2005-03-23T13:56:01)
	 * In caso la validazione abbia successo, viene ritornato un oggetto Date inizializzato alla data <var>aDate</var>.
	 *
	 * @param aDate data eGov da validare
	 * @return un oggetto Date inizializzato  alla <var>aDate</var>, se essa e' conforme alla specifica eGov, null altrimenti.
	 * 
	 */
	private Date validazioneData(String aDate){

		try{

			String [] split = aDate.split("T");
			if(split == null)
				return null;
			if(split.length != 2)
				return null;

			// Check data aaaa-mm-gg
			String [] date = split[0].split("-");
			if(date == null)
				return null;
			if(date.length != 3)
				return null;
			if(date[0].length() != 4)
				return null;
			try{
				Integer test = new Integer(date[0]);
				if(test.intValue()>2100)
					return null;
			} catch(Exception e){
				return null;
			} 
			if(date[1].length() != 2)
				return null;
			if(date[2].length() != 2)
				return null;
			try{
				Integer mese = new Integer(date[1]);
				if(mese.intValue()>12 || mese.intValue() < 0)
					return null;
				Integer giorno = new Integer(date[2]);
				if(giorno.intValue() < 0)
					return null;
				if(giorno.intValue()>29 && mese.intValue() == 2)
					return null;
				else if(giorno.intValue()>30 && 
						(  (mese.intValue() == 4) ||
								(mese.intValue() == 6) ||
								(mese.intValue() == 9) ||
								(mese.intValue() == 11) ) )
					return null;
				else if(giorno.intValue()>31)
					return null;
			} catch(Exception e){
				return null;
			} 

			// Check ora hh:mm:ss
			String [] ora = split[1].split(":");
			if(ora == null)
				return null;
			if(ora.length < 3)
				return null;

			// Check ora
			if(ora[0].length() != 2)
				return null;
			try{
				Integer test = new Integer(ora[0]);
				if(test.intValue() > 23 || test.intValue() < 0)
					return null;
			} catch(Exception e){
				return null;
			} 

			if(ora[1].length() != 2)
				return null;
			try{
				Integer test = new Integer(ora[1]);
				if(test.intValue() > 59 || test.intValue() < 0)
					return null;
			} catch(Exception e){
				return null;
			} 


			// Secondi...
			String secondi = null;
			if(ora[2].length() < 2)
				return null;
			try{
				secondi = "" + ora[2].charAt(0) + ora[2].charAt(1);
				Integer test = new Integer(secondi);
				if(test.intValue() > 59 || test.intValue() < 0)
					return null;
			} catch(Exception e){
				return null;
			} 

			GregorianCalendar calendar = null;
			try{
				calendar = 
					new GregorianCalendar( (new Integer(date[0])).intValue(),
							((new Integer(date[1])).intValue()-1), // il mese is 0-Based
							(new Integer(date[2])).intValue(),
							(new Integer(ora[0])).intValue(),
							(new Integer(ora[1])).intValue(),
							(new Integer(secondi)).intValue());
			} catch(Exception e){
				return null;
			} 

			long value = (calendar.getTime()).getTime(); //+ (1000 * 60 *60);
			return new Date(value);

		}catch(Exception error){
			return null;
		}
	}



	/**
	 * Metodo che si occupa di verificare che la data <var>scadenza</var> descritta con un formato
	 * conforme alla specifica eGov, non sia scaduta.
	 * La Data deve essere formato da :
	 * aaaa-mm-ggThh:mm:ss    (es. il 23/03/2005 alle 13:56:01 viene registrato come 2005-03-23T13:56:01)
	 *
	 * @param scadenza data eGov da controllare se e' scaduta o meno.
	 * @return un boolean true se la data non e' scaduta. Altrimenti false.
	 * 
	 */
	private boolean dataScaduta(String scadenza){
		Date now=DateManager.getDate();
		SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy MM dd HH mm ss"); // SimpleDateFormat non e' thread-safe
		String nowFormat = dateformat.format(now);
		String scadenzaFormat = ((scadenza.replace('-',' ')).replace(':',' ')).replace('T',' ');
		String [] splitNow = nowFormat.split(" ");
		String [] splitScadenza = scadenzaFormat.split(" ");


		if (validazioneData(scadenza)  == null)
			return false;

		try{
			Integer testNowAnno = new Integer(splitNow[0]);
			Integer testScadenzaAnno = new Integer(splitScadenza[0]);
			Integer testNowMese = new Integer(splitNow[1]);
			Integer testScadenzaMese = new Integer(splitScadenza[1]);
			Integer testNowGiorno = new Integer(splitNow[2]);
			Integer testScadenzaGiorno = new Integer(splitScadenza[2]);
			Integer testNowOra = new Integer(splitNow[3]);
			Integer testScadenzaOra = new Integer(splitScadenza[3]);
			Integer testNowMinuti = new Integer(splitNow[4]);
			Integer testScadenzaMinuti = new Integer(splitScadenza[4]);
			Integer testNowSecondi = new Integer(splitNow[5]);
			Integer testScadenzaSecondi = new Integer(splitScadenza[5].charAt(0) + splitScadenza[5].charAt(1));
			if(testNowAnno.intValue() > testScadenzaAnno.intValue())
				return true;
			else if ( (testNowAnno.intValue() == testScadenzaAnno.intValue()) &&
					(testNowMese.intValue() > testScadenzaMese.intValue()) )
				return true;
			else if ( (testNowAnno.intValue() == testScadenzaAnno.intValue()) &&
					(testNowMese.intValue() == testScadenzaMese.intValue()) &&
					(testNowGiorno.intValue() > testScadenzaGiorno.intValue()) )
				return true;
			else if ( (testNowAnno.intValue() == testScadenzaAnno.intValue()) &&
					(testNowMese.intValue() == testScadenzaMese.intValue()) &&
					(testNowGiorno.intValue() == testScadenzaGiorno.intValue()) &&
					(testNowOra.intValue() > testScadenzaOra.intValue()) )
				return true;
			else if ( (testNowAnno.intValue() == testScadenzaAnno.intValue()) &&
					(testNowMese.intValue() == testScadenzaMese.intValue()) &&
					(testNowGiorno.intValue() == testScadenzaGiorno.intValue()) &&
					(testNowOra.intValue() == testScadenzaOra.intValue()) &&
					(testNowMinuti.intValue() > testScadenzaMinuti.intValue()) )
				return true;
			else if ( (testNowAnno.intValue() == testScadenzaAnno.intValue()) &&
					(testNowMese.intValue() == testScadenzaMese.intValue()) &&
					(testNowGiorno.intValue() == testScadenzaGiorno.intValue()) &&
					(testNowOra.intValue() == testScadenzaOra.intValue()) &&
					(testNowMinuti.intValue() == testScadenzaMinuti.intValue()) &&
					(testNowSecondi.intValue() > testScadenzaSecondi.intValue()) )
				return true;

		} catch(Exception e){
			return true;
		} 

		return false;
	}
	
		
	private void validazioneDatePresentiRispettoDateFuture() throws ProtocolException{
		
		int intervalloTolleranza = this.spcoopProperties.getIntervalloMinutiTolleranzaDateFuture();
		
		Date sistema = DateManager.getDate();
		Date dataFuturaMassimaAccettata = new Date(sistema.getTime()+(intervalloTolleranza*60*1000));
		
		
		// Ora Registrazione, altrimenti segnalero' il problema
		if(this.busta.getOraRegistrazione()!=null){
			if( this.busta.getOraRegistrazione().after(dataFuturaMassimaAccettata)){
				this.log.error("Data portata nell'ora di registrazione ["+SPCoopUtils.getDate_eGovFormat(this.busta.getOraRegistrazione())+"] futura rispetto all'ora attuale di sistema ("+
						SPCoopUtils.getDate_eGovFormat(sistema)+") e l'intervallo di tolleranza impostato a "+intervalloTolleranza+" minuti");
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE.toString());
				this.erroriValidazione.add(ecc);
			}
		}
		
		// Check su id egov
		// Se correttamente formato, altrimenti segnalero' il problema
		if(this.busta.getID()!=null){
			String [] split = this.busta.getID().split("_");
			if(split != null && split.length == 5){
				Date dataIntoIDEgov = null;
				try{
					SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm"); // SimpleDateFormat non e' thread-safe
					dataIntoIDEgov = dateformat.parse(split[3]+"_"+split[4]);
				}catch(Exception e){}
				if(dataIntoIDEgov!=null){
					//System.out.println("DATA IDEGOV BUSTA ["+dataIntoIDEgov.toString()
					//		+"]   MINIMA ["+oraRegistrazioneMinimaAccettata.toString()+"]");
					if( dataIntoIDEgov.after(dataFuturaMassimaAccettata)){
						this.log.error("Data portata nell'identificativo egov ["+this.busta.getID()+"]["+SPCoopUtils.getDate_eGovFormat(dataIntoIDEgov)+"] futura rispetto all'ora attuale di sistema ("+
								SPCoopUtils.getDate_eGovFormat(sistema)+") e l'intervallo di tolleranza impostato a "+intervalloTolleranza+" minuti");
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_VALIDO_POSIZIONE.toString());
						this.erroriValidazione.add(ecc);
					}
				}
			}
		}
		
		// Ora Registrazione nei riscontri, altrimenti segnalero' il problema
		for(int i=0; i<this.busta.sizeListaRiscontri(); i++){
			Riscontro riscontro = this.busta.getRiscontro(i);
			if(riscontro.getOraRegistrazione()!=null){
				if( riscontro.getOraRegistrazione().after(dataFuturaMassimaAccettata)){
					this.log.error("Data portata nell'ora di registrazione del riscontro con id ["+riscontro.getID()+"] ["+SPCoopUtils.getDate_eGovFormat(riscontro.getOraRegistrazione())
							+"] futura rispetto all'ora attuale di sistema ("+
							SPCoopUtils.getDate_eGovFormat(sistema)+") e l'intervallo di tolleranza impostato a "+intervalloTolleranza+" minuti");
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_ORA_REGISTRAZIONE_NON_VALIDA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE.toString());
					this.erroriValidazione.add(ecc);
				}
			}
		}
		
		// Ora Registrazione nelle trasmissioni, altrimenti segnalero' il problema
		for(int i=0; i<this.busta.sizeListaTrasmissioni(); i++){
			Trasmissione trasmissione = this.busta.getTrasmissione(i);
			if(trasmissione.getOraRegistrazione()!=null){
				if( trasmissione.getOraRegistrazione().after(dataFuturaMassimaAccettata)){
					this.log.error("Data portata nell'ora di registrazione di una trasmissione ["+SPCoopUtils.getDate_eGovFormat(trasmissione.getOraRegistrazione())
							+"] futura rispetto all'ora attuale di sistema ("+
							SPCoopUtils.getDate_eGovFormat(sistema)+") e l'intervallo di tolleranza impostato a "+intervalloTolleranza+" minuti");
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORA_REGISTRAZIONE_NON_VALIDA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE.toString());
					this.erroriValidazione.add(ecc);
				}
			}
		}
	}

	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	@Override
	public ValidazioneSintatticaResult validaRichiesta(IState state, OpenSPCoop2Message msg,  Busta datiBustaLettiURLMappingProperties, ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException{
		this.msg = (SOAPMessage) msg;
		boolean isValido = this.valida();
		ErroreCooperazione errore = null;
		if(this.msgErrore!=null && this.codiceErrore!=null){
			errore = new ErroreCooperazione(this.msgErrore, this.codiceErrore);
		}
		ValidazioneSintatticaResult result = new ValidazioneSintatticaResult(this.erroriValidazione, this.erroriProcessamento, this.errorsTrovatiSullaListaEccezioni, this.busta, errore, this.bustaErroreHeaderIntestazione, this.headerEGov, isValido);
		return result;
	}
	
	@Override
	public ValidazioneSintatticaResult validaRisposta(IState state, OpenSPCoop2Message msg, Busta bustaRichiesta, ProprietaValidazioneErrori proprietaValidazioneErrori) throws ProtocolException{
		this.msg = (SOAPMessage) msg;
		boolean isValido = this.valida();
		ErroreCooperazione errore = null;
		if(this.msgErrore!=null && this.codiceErrore!=null){
			errore = new ErroreCooperazione(this.msgErrore, this.codiceErrore);
		}
		ValidazioneSintatticaResult result = new ValidazioneSintatticaResult(this.erroriValidazione, this.erroriProcessamento, this.errorsTrovatiSullaListaEccezioni, this.busta, errore, this.bustaErroreHeaderIntestazione, this.headerEGov, isValido);
		return result;
	}
	
	@Override
	public boolean verifyProtocolPresence(TipoPdD tipoPdD, ProfiloDiCollaborazione profilo, boolean isRichiesta,
			OpenSPCoop2Message msg) throws ProtocolException{
		return this.existsHeaderEGov(msg);
	}

	@Override
	public ValidazioneSintatticaResult validazioneManifestAttachments(
			OpenSPCoop2Message msg,
			ProprietaManifestAttachments proprietaManifestAttachments) {
		this.validazioneManifestAttachmentsEGov(msg, proprietaManifestAttachments);
		ErroreCooperazione errore = null;
		if(this.msgErrore!=null && this.codiceErrore!=null){
			errore = new ErroreCooperazione(this.msgErrore, this.codiceErrore);
		}
		ValidazioneSintatticaResult result = new ValidazioneSintatticaResult(this.erroriValidazione, this.erroriProcessamento, this.errorsTrovatiSullaListaEccezioni, this.busta, errore, this.bustaErroreHeaderIntestazione, this.headerEGov, true);
		return result;
	}

	@Override
	public SOAPHeaderElement getHeaderProtocollo_senzaControlli(
			OpenSPCoop2Message msg) throws ProtocolException {
		this.msg = (SOAPMessage) msg;
		return this.getHeaderEGov_senzaControlli();
	}

	@Override
	public ValidazioneSintatticaResult validazioneFault(SOAPBody body) {
		this.validazioneFaultEGov(body);
		ErroreCooperazione errore = null;
		if(this.msgErrore!=null && this.codiceErrore!=null){
			errore = new ErroreCooperazione(this.msgErrore, this.codiceErrore);
		}
		ValidazioneSintatticaResult result = new ValidazioneSintatticaResult(this.erroriValidazione, this.erroriProcessamento, this.errorsTrovatiSullaListaEccezioni, this.busta, errore, this.bustaErroreHeaderIntestazione, this.headerEGov, true);
		return result;
	}
	@Override
	public SOAPHeaderElement getHeaderProtocollo(Busta busta) {
		return null;
	}
	
	public ProfiloDiCollaborazione toProfilo(String profilo){
		if(SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY.equals(profilo))
			return ProfiloDiCollaborazione.ONEWAY;
		if(SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO.equals(profilo))
			return ProfiloDiCollaborazione.SINCRONO;
		if(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO.equals(profilo))
			return ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO;
		if(SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO.equals(profilo))
			return ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO;
		return ProfiloDiCollaborazione.UNKNOWN;
	}
	public TipoOraRegistrazione toTipoOra(String tipoora){
		if(SPCoopCostanti.TIPO_TEMPO_LOCALE.equals(tipoora))
			return TipoOraRegistrazione.LOCALE;
		if(SPCoopCostanti.TIPO_TEMPO_SPC.equals(tipoora))
			return TipoOraRegistrazione.SINCRONIZZATO;
		return TipoOraRegistrazione.UNKNOWN;
	}
	public Inoltro toInoltro(String inoltro){
		if(SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI.equals(inoltro))
			return Inoltro.CON_DUPLICATI;
		if(SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI.equals(inoltro))
			return Inoltro.SENZA_DUPLICATI;
		return Inoltro.UNKNOWN;
	}
}
