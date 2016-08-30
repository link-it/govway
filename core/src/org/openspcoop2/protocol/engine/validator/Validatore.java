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



package org.openspcoop2.protocol.engine.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.utils.LoggerWrapperFactory;




/**
 * Classe utilizzata per effettuare la validazione di una busta
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Validatore  {


	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Se IState e' un'istanza di StatefulMessage possiede una Connessione SQL in autoCommit mode su cui effettuare query 
	 *  Altrimenti, e' un'istanza di StatelessMessage e nn necessita di connessioni  */
	private IState state;
	
	
	/** Messaggio. */
	private OpenSPCoop2Message msg;
	/** Proprieta di Validazione. */
	private ProprietaValidazione proprietaValidazione;
	/** Eventuale errore avvenuto durante il processo di validazione */
	ErroreCooperazione errore;
	/** Errori di validazione riscontrati sulla busta */
	private java.util.Vector<Eccezione> erroriValidazione = new Vector<Eccezione>();
	/** Errori di processamento riscontrati sulla busta */
	private java.util.Vector<Eccezione> erroriProcessamento = new Vector<Eccezione>();
	/** Indicazione se la busta validata e' un messaggio errore */
	private boolean isMessaggioErrore;
	private boolean isMessaggioErroreIntestazione;
	private boolean isMessaggioErroreProcessamento;
	/** Indicazione se la busta validata e' una busta di servizio.
	 * Una busta e' una busta di servizio, se:
	 * - non possiede un servizio (non e' quindi una richiesta)
	 * - non possiede un riferimentoMsg (non e' quindi una risposta) 
	 **/
	private boolean isBustaDiServizio;
	/** Oggetto Busta contenente i valori della busta ricevuta */
	private Busta busta;
	/** Soggetto Mittente della busta */
	private IDSoggetto mittente;
	/** Servizio richiesto nella busta */
	private IDServizio servizio;
	/** ValidazioneSintattica */
	private ValidazioneSintattica validatoreSintattico;
	public ValidazioneSintattica getValidatoreSintattico() {
		return this.validatoreSintattico;
	}
	public void setValidatoreSintattico(ValidazioneSintattica validator) {
		this.validatoreSintattico = validator;
	}
	
	/** ServizioCorrelato */
	private String servizioCorrelato;
	/** Tipo ServizioCorrelato */
	private String tipoServizioCorrelato;
	/** informazioni Servizio */
	private Servizio infoServizio = null;
	/** Profilo di Gestione */
	private String versioneProtocollo = null;
	/** RuoloBusta */
	private RuoloBusta ruoloBustaRicevuta = null;
	/** SecurityInfo */
	private SecurityInfo securityInfo = null;
	
	/** Indicazione se leggere gli attributi qualificati */
	private boolean readQualifiedAttribute;

	
	/** Logger utilizzato per debug. */
	private org.slf4j.Logger log = null;
	private IProtocolFactory protocolFactory;
	private IProtocolManager protocolManager;
	/** bustaErroreHeaderIntestazione: generata solo quando la busta arrivata non contiene gli elementi principali */
	private Busta bustaErroreHeaderIntestazione = null;
	public Busta getBustaErroreHeaderIntestazione() {
		return this.bustaErroreHeaderIntestazione;
	}

	/**
	 * Costruttore
	 *
	 * @param aMsg Messaggio da validare.
	 * @param aValidazione tipo di validazione.
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * @throws ProtocolException 
	 * 
	 */
	public Validatore(OpenSPCoop2Message aMsg,ProprietaValidazione aValidazione, IState state,boolean readQualifiedAttribute, IProtocolFactory protocolFactory) throws ProtocolException {
		this(aMsg,aValidazione,state,Configurazione.getLibraryLog(),readQualifiedAttribute, protocolFactory);
	}
	/**
	 * Costruttore
	 *
	 * @param aMsg Messaggio da validare.
	 * @param aValidazione tipo di validazione.
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * @throws ProtocolException 
	 * 
	 */
	public Validatore(OpenSPCoop2Message aMsg,ProprietaValidazione aValidazione,IState state,Logger alog,boolean readQualifiedAttribute, IProtocolFactory protocolFactory) throws ProtocolException {
		this.msg = aMsg;
		if(aValidazione == null)
			this.proprietaValidazione = new ProprietaValidazione();
		else
			this.proprietaValidazione = aValidazione;
		this.state = state;
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(Validatore.class);
		}
		this.readQualifiedAttribute = readQualifiedAttribute;
		this.protocolFactory = protocolFactory;
		this.protocolManager = this.protocolFactory.createProtocolManager();
	}

	/**
	 * Costruttore
	 *
	 * @param aMsg Messaggio da validare.
	 * @throws ProtocolException 
	 * 
	 */
	public Validatore(OpenSPCoop2Message aMsg,IState state,Logger alog, IProtocolFactory protocolFactory) throws ProtocolException {
		this(aMsg,null,state,alog,false, protocolFactory);
	}

	
	
	/**
	 * Avvia il processo di lettura di una busta ricevuta.
	 * Viene effettuata una validazione sintattica
	 * Vengono effettuati i seguenti passi:
	 * <ul>
	 * <li> Controllo header
	 * </ul>
	 * 
	 *
	 * @return true in caso di identificazione con successo, false altrimenti.
	 * 
	 */
	public boolean validazioneSintattica() {
		return validazioneSintattica(null, null);
	}
	
	/**
	 * Per la validazione puo' essere necessaria la busta qualora le informazioni di cooperazione non siano dentro al messaggio.
	 * 
	 * @param busta
	 * @return true se la struttura del messaggio e' corretta
	 */
	
	public boolean validazioneSintattica(Busta busta, Boolean isRichiesta) {

		try{
			/** (Controllo presenza Busta) */
			this.validatoreSintattico = new ValidazioneSintattica(this.state,this.msg, busta, isRichiesta, this.log,this.readQualifiedAttribute, this.protocolFactory);
			if (this.validatoreSintattico.valida() == false){
				this.errore = this.validatoreSintattico.getErrore();
				this.bustaErroreHeaderIntestazione = this.validatoreSintattico.getBustaErroreHeaderIntestazione();
				return false;
			}
			this.busta = this.validatoreSintattico.getBusta();
			IValidatoreErrori validatoreErrori = this.protocolFactory.createValidatoreErrori();
			ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
			pValidazioneErrori.setIgnoraEccezioniNonGravi(this.protocolManager.isIgnoraEccezioniNonGravi());
			this.isMessaggioErrore = validatoreErrori.isBustaErrore(this.busta,this.msg,pValidazioneErrori);
			this.isMessaggioErroreIntestazione = validatoreErrori.isBustaErroreIntestazione(this.busta, this.msg,pValidazioneErrori);
			this.isMessaggioErroreProcessamento = validatoreErrori.isBustaErroreProcessamento(this.busta, this.msg,pValidazioneErrori);
				
			this.isBustaDiServizio = this.protocolFactory.createProtocolManager().isBustaServizio(this.busta);

			if(this.isMessaggioErrore){
				addListaEccezioni(this.validatoreSintattico.getErroriTrovatiSullaListaEccezioni(),this.erroriValidazione);
			}
			else{
				addListaEccezioni(this.validatoreSintattico.getEccezioniValidazione(),this.erroriValidazione);
			}
			addListaEccezioni(this.validatoreSintattico.getEccezioniProcessamento(),this.erroriProcessamento);
			
			// Se comunque e' arrivata un busta di servizio, che non possiede nemmeno uno delle seguenti cose:
			// - lista eccezioni
			// - lista riscontri
			// Allora non si tratta di una busta di servizio bensi' di una busta malformata
			if((this.isBustaDiServizio) && (this.busta.sizeListaEccezioni()<=0) && (this.busta.sizeListaRiscontri()<=0)){
				Eccezione ec = Eccezione.getEccezioneValidazione(ErroriCooperazione.SERVIZIO_SCONOSCIUTO.getErroreCooperazione(), this.protocolFactory);
				this.erroriValidazione.add(ec);
				this.isBustaDiServizio = false;
			}
			
			// Costruzione IDMittente e IDServizio
			this.mittente = new IDSoggetto(this.busta.getTipoMittente(),this.busta.getMittente());
			this.mittente.setCodicePorta(this.busta.getIdentificativoPortaMittente());
			this.servizio = new IDServizio(this.busta.getTipoDestinatario(),this.busta.getDestinatario(),
					this.busta.getTipoServizio(),this.busta.getServizio(),
					this.busta.getAzione());
			this.servizio.getSoggettoErogatore().setCodicePorta(this.busta.getIdentificativoPortaDestinatario());
			
			//	Se la lettura precedente ha riscontrato anomalie, ho gia' finito
			if( ( (this.erroriValidazione!=null) && (this.erroriValidazione.size()>0) ) 
					|| 
				( (this.erroriProcessamento!=null) && (this.erroriProcessamento.size()>0) )
				)
				return true; // riscontrati errori durante la validazione sintattica.
			
			return true;
		    
		}catch(Exception e){
			this.log.error("validazioneSintattica",e);
			this.eccezioneProcessamentoValidazioneSintattica = e;
			this.errore = ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreProcessamento("Errore di processamento: " +e.getMessage());
			return false;
		}	
	}
	private Exception eccezioneProcessamentoValidazioneSintattica = null;
	public Exception getEccezioneProcessamentoValidazioneSintattica() {
		return this.eccezioneProcessamentoValidazioneSintattica;
	}


	/**
	 * Avvia il processo di validazione.
	 * Vengono effettuati i seguenti passi:
	 * <ul>
	 * <li> Message-Security
	 * <li> Controllo eventuale SOAPFault di un messaggio Errore
	 * <li> Controllo eventuale di un manifest degli attachments
	 * <li> Controllo con schema xsd
	 * <li> Validazione Semantica
	 * </ul>
	 *
	 * @param messageSecurityContext MessageSecurityContext utilizzato per l'applicazione di un header di MessageSecurity
	 * @return true in caso di validazione effettuata (con o senza errori), false in caso di errori di processamento
	 * 
	 */
	private boolean rilevatiErroriDuranteValidazioneSemantica = false;
	public boolean isRilevatiErroriDuranteValidazioneSemantica() {
		return this.rilevatiErroriDuranteValidazioneSemantica;
	}
	public boolean validazioneSemantica_beforeMessageSecurity(boolean rispostaConnectionReply,String profiloGestione) {
		try{
			this.rilevatiErroriDuranteValidazioneSemantica = true; // in fondo se arrivo corretto lo re-imposto a false
			
			if(this.validatoreSintattico == null){
				this.errore = ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreProcessamento("Errore di processamento: Analisi sintattica richiesta e validatoreSintattico non risulta inizializzato");
				return false;
			}
		
			// Tipo Busta (Servizio,Richiesta,Risposta,Ricevuta)
			if(this.ruoloBustaRicevuta==null)
				this.ruoloBustaRicevuta = ValidazioneSemantica.getTipoBustaDaValidare(this.busta, this.protocolFactory, rispostaConnectionReply,this.state,this.log);
			//log.info("Validazione tipoBusta ["+tipoBusta+"]...");
		
			// Riconoscimento Profilo di Gestione per servizio erogato.
			try{
				if(rispostaConnectionReply){
					this.versioneProtocollo = profiloGestione;
				}else{
					this.versioneProtocollo = ValidazioneSemantica.riconoscimentoVersioneProtocolloServizioErogato(this.busta, this.protocolFactory.createTraduttore(), this.ruoloBustaRicevuta, this.state);
				}
			}catch(Exception e){
				// L'eventuale errore, e' dovuto al non riconoscimento nel registro dei servizi del soggetto mittente o destinatario.
				// L'errore verra' riportato poi nel resto della validazione se il messaggio non era un messaggio Errore
				// Se invece era gia' stato marcato come messaggio Errore, molto probabilmente uno dei motivi era quello!
				this.log.error("Riconoscimento profilo di gestione non riuscito",e);
			}


			if(this.protocolFactory.createProtocolVersionManager(this.versioneProtocollo).isEccezioniLivelloInfoAbilitato()){
				// Eventuali eccezioni trovate in buste errore, di livello INFO, non marcano il msg come BustaErrore, rieffettuo il test
				if(this.isMessaggioErrore){
					IValidatoreErrori validatoreErrori = this.protocolFactory.createValidatoreErrori();
					ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
					pValidazioneErrori.setIgnoraEccezioniNonGravi(this.protocolManager.isIgnoraEccezioniNonGravi());
					pValidazioneErrori.setVersioneProtocollo(this.versioneProtocollo);
					this.isMessaggioErrore = validatoreErrori.isBustaErrore(this.busta,this.msg,pValidazioneErrori);
					this.isMessaggioErroreIntestazione = validatoreErrori.isBustaErroreIntestazione(this.busta, this.msg,pValidazioneErrori);
					this.isMessaggioErroreProcessamento = validatoreErrori.isBustaErroreProcessamento(this.busta, this.msg,pValidazioneErrori);
				}
			}

			// Se la lettura precedente (readBusta) ha riscontrato anomalie, ho gia' finito
			boolean ignoraEccezioniLivelloNonGravi = this.protocolFactory.createProtocolVersionManager(this.versioneProtocollo).isIgnoraEccezioniLivelloNonGrave();
			int sizeErroriValidazione = countErrori(ignoraEccezioniLivelloNonGravi, this.erroriValidazione);
			int sizeErroriProcessamento = countErrori(ignoraEccezioniLivelloNonGravi, this.erroriProcessamento);				
			if( sizeErroriValidazione>0 || sizeErroriProcessamento>0 )
				return true; // riscontrati errori durante la validazione sintattica.

		}catch(Exception e){
			this.log.error("validazioneSemantica (validazioneSemantica_beforeMessageSecurity)",e);
			this.eccezioneProcessamentoValidazioneSemantica = e;
			this.errore = ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione();
			return false;
		}

		this.rilevatiErroriDuranteValidazioneSemantica = false;
		return true;	
	}
	
	public boolean validazioneSemantica_messageSecurity_readSecurityInfo(MessageSecurityContext messageSecurityContext) {
		try{
			this.rilevatiErroriDuranteValidazioneSemantica = true; // in fondo se arrivo corretto lo re-imposto a false
				
			/** Leggo contesto sicurezza prima di processare la parte MessageSecurity */
			if(messageSecurityContext!= null && messageSecurityContext.getDigestReader()!=null){
				this.securityInfo = this.protocolFactory.createValidazioneSemantica().readSecurityInformation(messageSecurityContext.getDigestReader(),
						this.msg, this.msg.getSOAPPart().getEnvelope(), this.validatoreSintattico.getProtocolHeader());
			}		

		}catch(Exception e){
			this.log.error("validazioneSemantica (validazioneSemantica_messageSecurity_readSecurityInfo)",e);
			this.eccezioneProcessamentoValidazioneSemantica = e;
			this.errore = ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione();
			return false;
		}

		this.rilevatiErroriDuranteValidazioneSemantica = false;
		return true;	
	}
	
	public boolean validazioneSemantica_messageSecurity_process(MessageSecurityContext messageSecurityContext, StringBuffer errore) {
		try{
			this.rilevatiErroriDuranteValidazioneSemantica = true; // in fondo se arrivo corretto lo re-imposto a false
				

			/** Applicazione Message-Security (eventualmente per decriptare il body applicativo: utile anche per il SoapFault del MessaggioErrore) */
			if(messageSecurityContext!= null && messageSecurityContext.getIncomingProperties() != null && messageSecurityContext.getIncomingProperties().size() > 0){
				boolean existsHeaderMessageSecurity = messageSecurityContext.existsSecurityHeader(this.msg, messageSecurityContext.getActor());
				if(messageSecurityContext.processIncoming(this.msg,this.busta) == false){  
					List<Eccezione> eccezioniSicurezza = new ArrayList<Eccezione>();
					if(messageSecurityContext.getListaSubCodiceErrore()!=null && messageSecurityContext.getListaSubCodiceErrore().size()>0){
						List<SubErrorCodeSecurity> subCodiciErrore = messageSecurityContext.getListaSubCodiceErrore();
						for (Iterator<?> iterator = subCodiciErrore.iterator(); iterator.hasNext();) {
							SubErrorCodeSecurity subCodiceErrore = (SubErrorCodeSecurity) iterator.next();
							Eccezione ecc = new Eccezione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(subCodiceErrore.getMsgErrore(), messageSecurityContext.getCodiceErrore()),true,null, this.protocolFactory);
							ecc.setSubCodiceEccezione(subCodiceErrore);
							eccezioniSicurezza.add(ecc);
						}
					}else{
						Eccezione ecc = new Eccezione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(messageSecurityContext.getMsgErrore(),messageSecurityContext.getCodiceErrore()), true, null, this.protocolFactory);
						eccezioniSicurezza.add(ecc);
					}
					if(this.isMessaggioErrore){
						// NOTA: il message security header puo' non essere presente se l'altra porta di dominio, ha generato un messaggio Busta Errore.
						//       La porta di dominio riesce ad aggiungere l'header di sicurezza solo se riesce ad identificare la PortaApplicativa/Delegata,
						//       altrimenti genera un msg di Busta Errore chiaramente senza header di sicurezza.
						if(existsHeaderMessageSecurity){
							for (Iterator<?> iterator = eccezioniSicurezza.iterator(); iterator.hasNext();) {
								Eccezione ecc = (Eccezione) iterator.next();
								this.erroriValidazione.add(ecc);	
								
								if(errore.length()>0){
									errore.append("\n");
								}
								errore.append(ecc.getDescrizione(this.protocolFactory));
							}
							return true; // riscontrati errori durante la validazione Message Security
						}
						else{
							StringBuffer bf = new StringBuffer();
							for (Iterator<?> iterator = eccezioniSicurezza.iterator(); iterator.hasNext();) {
								Eccezione ecc = (Eccezione) iterator.next();
								if(bf.length()>0){
									bf.append(" ; ");
								}
								bf.append(ecc.toString(this.protocolFactory));
							}
							this.log.debug("MessageSecurityHeader not exists: "+bf.toString());
						}
					}else{
						for (Iterator<?> iterator = eccezioniSicurezza.iterator(); iterator.hasNext();) {
							Eccezione ecc = (Eccezione) iterator.next();
							this.erroriValidazione.add(ecc);
							
							if(errore.length()>0){
								errore.append("\n");
							}
							errore.append(ecc.getDescrizione(this.protocolFactory));
						}
						return true; // riscontrati errori durante la validazione Message Security
					}
				}
			}

		}catch(Exception e){
			
			if(errore.length()>0){
				errore.append("\n");
			}
			errore.append(e.getMessage());
			
			this.log.error("validazioneSemantica (validazioneSemantica_messageSecurity_process)",e);
			this.eccezioneProcessamentoValidazioneSemantica = e;
			this.errore = ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione();
			return false;
		}

		this.rilevatiErroriDuranteValidazioneSemantica = false;
		return true;	
	}
	
	public boolean validazioneSemantica_afterMessageSecurity(ProprietaManifestAttachments proprietaManifestAttachments,boolean validazioneIdentificativiCompleta) {
		try{
			this.rilevatiErroriDuranteValidazioneSemantica = true; // in fondo se arrivo corretto lo re-imposto a false

			/** Se ho ricevuto un messaggio Busta Errore, devo controllare il SOAPFault ricevuto 
			 * Deve essere fatta dopo Message-Security per il Body (attachments manifest)
			 */
			if(this.isMessaggioErrore){
				this.validatoreSintattico.validazioneFault(this.msg.getSOAPBody());
				addListaEccezioni(this.validatoreSintattico.getErroriTrovatiSullaListaEccezioni(),this.erroriValidazione);
				addListaEccezioni(this.validatoreSintattico.getEccezioniProcessamento(),this.erroriProcessamento);
				if( ( (this.erroriValidazione!=null) && (this.erroriValidazione.size()>0) ) 
						|| 
					( (this.erroriProcessamento!=null) && (this.erroriProcessamento.size()>0) )
					)
					return true; // riscontrati errori durante la validazione
			}

			/** Se ho ricevuto un messaggio con Attachments devo controllare il manifest
			 * Deve essere fatta dopo Message-Security per il Body (attachments manifest)
			 */
			// Questa validazione non deve essere effettuata per messaggi Busta Errore
			if(this.isMessaggioErrore==false){
				if(this.proprietaValidazione.isValidazioneManifestAttachments() &&  this.msg.countAttachments()>0){
					this.validatoreSintattico.validazioneManifestAttachments(this.msg,proprietaManifestAttachments);
					addListaEccezioni(this.validatoreSintattico.getEccezioniValidazione(),this.erroriValidazione);
					addListaEccezioni(this.validatoreSintattico.getEccezioniProcessamento(),this.erroriProcessamento);
					if( ( (this.erroriValidazione!=null) && (this.erroriValidazione.size()>0) ) 
							|| 
						( (this.erroriProcessamento!=null) && (this.erroriProcessamento.size()>0) )
						)
						return true; // riscontrati errori durante la validazione 
				}
			}

			/** Validazione semantica */
			// Questa validazione non deve essere effettuata per messaggi Busta Errore
			if(this.isMessaggioErrore==false){
				ValidazioneSemantica registryValidator = new ValidazioneSemantica(this.busta,this.state,validazioneIdentificativiCompleta,this.log, this.protocolFactory);
				registryValidator.valida(this.msg,this.proprietaValidazione,this.ruoloBustaRicevuta,this.versioneProtocollo);
				addListaEccezioni(registryValidator.getEccezioniValidazione(),this.erroriValidazione);
				addListaEccezioni(registryValidator.getEccezioniProcessamento(),this.erroriProcessamento);
				this.servizioCorrelato = registryValidator.getServizioCorrelato();
				this.tipoServizioCorrelato = registryValidator.getTipoServizioCorrelato();
				this.infoServizio = registryValidator.getInfoServizio();
				if( ( (this.erroriValidazione!=null) && (this.erroriValidazione.size()>0) ) 
						|| 
					( (this.erroriProcessamento!=null) && (this.erroriProcessamento.size()>0) )
					)
					return true; // riscontrati errori durante la validazione sul registro.
			}

			/** Validazione con SCHEMA (Opzionale): deve essere fatta dopo Message-Security per il Body (attachments manifest)*/
			// Questa validazione deve essere effettuata anche per messaggi Busta Errore
			if( this.proprietaValidazione.isValidazioneConSchema() ){
				javax.xml.soap.SOAPBody body = this.msg.getSOAPBody();
				ValidazioneConSchema schemaValidator = new ValidazioneConSchema(this.msg,this.validatoreSintattico.getProtocolHeader(), body,
						this.isMessaggioErroreProcessamento, this.isMessaggioErroreIntestazione,this.proprietaValidazione.isValidazioneManifestAttachments(), this.log, this.protocolFactory);
				schemaValidator.valida((this.msg.countAttachments()>0));
				addListaEccezioni(schemaValidator.getEccezioniValidazione(),this.erroriValidazione);
				addListaEccezioni(schemaValidator.getEccezioniProcessamento(),this.erroriProcessamento);
				if( ( (this.erroriValidazione!=null) && (this.erroriValidazione.size()>0) ) 
						|| 
					( (this.erroriProcessamento!=null) && (this.erroriProcessamento.size()>0) )
					)
					return true; // riscontrati errori durante la validazione
			}


		}catch(Exception e){
			this.log.error("validazioneSemantica (validazioneSemantica_afterMessageSecurity)",e);
			this.eccezioneProcessamentoValidazioneSemantica = e;
			this.errore = ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.getErroreCooperazione();
			return false;
		}

		this.rilevatiErroriDuranteValidazioneSemantica = false;
		return true;	
	}
	
	private Exception eccezioneProcessamentoValidazioneSemantica = null;
	public Exception getEccezioneProcessamentoValidazioneSemantica() {
		return this.eccezioneProcessamentoValidazioneSemantica;
	}

	/**
	 * In caso di avvenuto errore durante il processo di validazione, 
	 * questo metodo ritorna l'errore.
	 *
	 * @return errore (se avvenuto).
	 * 
	 */
	public ErroreCooperazione getErrore(){
		return this.errore;
	}

	/**
	 * Indicazione se la validazione della busta ha riscontrato eccezioni
	 *
	 * @return true in caso la validazione abbia riscontrato eccezioni.
	 * 
	 */
	public boolean validazioneConEccezioni(){
		if( ( (this.erroriValidazione!=null) && (this.erroriValidazione.size()>0) ) 
				|| 
			( (this.erroriProcessamento!=null) && (this.erroriProcessamento.size()>0) )
			)
			return true;
		else
			return false;
	}

	/**
	 * Ritorna un vector contenente eventuali eccezioni di validazione riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.Vector<Eccezione> getEccezioniValidazione(){
		if(this.erroriValidazione!=null)
			return this.erroriValidazione;
		else
			return new java.util.Vector<Eccezione>();
	}
	/**
	 * Ritorna un vector contenente eventuali eccezioni di processamento riscontrate nella busta.   
	 *
	 * @return Eccezioni riscontrate nella busta.
	 * 
	 */
	public java.util.Vector<Eccezione> getEccezioniProcessamento(){
		if(this.erroriProcessamento!=null)
			return this.erroriProcessamento;
		else
			return new java.util.Vector<Eccezione>();
	}

	/**
	 * Indicazione se la busta processata e' un messaggio Errore.
	 *
	 * @return true se la busta e' un messaggio errore.
	 * 
	 */
	public boolean isErroreProtocollo(){
		return this.isMessaggioErrore;
	}
	public boolean isMessaggioErroreIntestazione() {
		return this.isMessaggioErroreIntestazione;
	}
	public boolean isMessaggioErroreProcessamento() {
		return this.isMessaggioErroreProcessamento;
	}

	/**
	 * Indicazione se la busta processata e' una busta di servizio
	 *
	 * @return true se la busta e' una busta di servizio
	 * 
	 */
	public boolean isBustaDiServizio(){
		return this.isBustaDiServizio;
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
	 * Soggetto Mittente.
	 *
	 * @return soggetto Mittente.
	 * 
	 */
	public IDSoggetto getSoggettoMittente(){
		return this.mittente;
	}

	/**
	 * Identificativo del servizio presente nella busta.
	 *
	 * @return identificativo del servizio richiesto.
	 * 
	 */
	public IDServizio getIDServizio(){
		return this.servizio;
	}

	/**
	 * Eventuale servizio correlato associato al servizio presente nella busta.
	 *
	 * @return Eventuale servizio correlato associato al servizio presente nella busta.
	 * 
	 */
	public String getServizioCorrelato() {
		return this.servizioCorrelato;
	}

	/**
	 * Eventuale tipo di servizio correlato associato al servizio presente nella busta.
	 *
	 * @return Eventuale tipo di servizio correlato associato al servizio presente nella busta.
	 * 
	 */
	public String getTipoServizioCorrelato() {
		return this.tipoServizioCorrelato;
	}
	
	public Servizio getInfoServizio() {
		return this.infoServizio;
	}

	public String getProfiloGestione() {
		return this.versioneProtocollo;
	}
	public void setProfiloGestione(String profiloGestione) {
		this.versioneProtocollo = profiloGestione;
	}

	public RuoloBusta getRuoloBustaRicevuta(boolean rispostaConnectionReply) throws ProtocolException {
		if(this.ruoloBustaRicevuta==null)
			this.ruoloBustaRicevuta= ValidazioneSemantica.getTipoBustaDaValidare(this.busta, this.protocolFactory, rispostaConnectionReply,this.state,this.log);
		return this.ruoloBustaRicevuta;
	}

	public void setProprietaValidazione(ProprietaValidazione proprietaValidazione) {
		this.proprietaValidazione = proprietaValidazione;
	}
	
	public SOAPElement getHeaderProtocollo() {
		return this.validatoreSintattico.getProtocolHeader();
	}
	
	public SOAPElement getHeaderProtocollo_senzaControlli() throws ProtocolException{
		this.validatoreSintattico = new ValidazioneSintattica(this.state,this.msg,this.log, this.protocolFactory);
		return this.validatoreSintattico.getHeaderProtocollo_senzaControlli();
	}

	private void addListaEccezioni(Vector<Eccezione> from,Vector<Eccezione> to){
		if(from!=null){
			to.addAll(from);
		}
	}
	public SecurityInfo getSecurityInfo() {
		return this.securityInfo;
	}
	
	
	private int countErrori(boolean ignoraEccezioniNonGravi,List<Eccezione> list){
		int size = 0;
		if(list!=null){
			for (Eccezione eccezione : list) {
				if(eccezione.getRilevanza()!=null && ignoraEccezioniNonGravi){
					if(LivelloRilevanza.isEccezioneLivelloGrave(eccezione.getRilevanza())){
						size++;
					}
				}
				else{
					size++;
				}
			}
		}
		return size;
	}
}






