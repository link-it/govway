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

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.protocol.sdk.state.StatelessMessage;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSemanticaResult;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Classe utilizzata per effettuare un controllo di registrazione dei soggetti di una busta nel registro dei servizi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class ValidazioneSemantica  {

	/** Se IState e' un'istanza di StatefulMessage possiede una Connessione SQL in autoCommit mode su cui effettuare query 
	 *  Altrimenti, e' un'istanza di StatelessMessage e nn necessita di connessioni  */
	protected IState state;
	/** Errori di validazione riscontrati sulla busta */
	protected java.util.Vector<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	protected java.util.Vector<Eccezione> erroriProcessamento;
	/** Busta */
	protected Busta busta;
	/** Validazione ID completa */
	protected boolean validazioneIdentificativiCompleta = false;
	/** ServizioCorrelato */
	protected String servizioCorrelato;
	/** Tipo ServizioCorrelato */
	protected String tipoServizioCorrelato;
	/** Azione Correlata */
	protected String azioneCorrelata;
	/** Reader Registro */
	protected RegistroServiziManager registroServiziReader;
	/** informazioni Servizio */
	protected Servizio infoServizio = null;
	/** Logger utilizzato per debug. */
	protected Logger log = null;
	private IProtocolFactory protocolFactory;
	public IProtocolFactory getProtocolFactory(){
		return this.protocolFactory;
	}

	/**
	 * Costruttore.
	 *
	 * @param aBusta Busta da validare.
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	 
	public ValidazioneSemantica(Busta aBusta, IState state, boolean validazioneIdentificativiCompleta, IProtocolFactory protocolFactory){
		this(aBusta,state,validazioneIdentificativiCompleta,Configurazione.getLibraryLog(), protocolFactory);
	}
	
	/**
	 * Costruttore.
	 *
	 * @param aBusta Busta da validare.
	 * @param state Oggetto che rappresenta lo stato di una busta
	 * 
	 */
	 
	public ValidazioneSemantica(Busta aBusta, IState state, boolean validazioneIdentificativiCompleta, Logger alog, IProtocolFactory protocolFactory){
		this.busta = aBusta;
		this.state = state;
		this.registroServiziReader = RegistroServiziManager.getInstance(state);
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(ValidazioneSemantica.class);
		}
		this.validazioneIdentificativiCompleta = validazioneIdentificativiCompleta;
		this.protocolFactory = protocolFactory;
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
	 * Metodo che effettua la validazione dei soggetti di una busta, controllando la loro registrazione nel registro dei servizi. 
	 *
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *   {@link Eccezione}, e viene inserito nel Vector <var>errors</var>.
	 *
	 * @param proprietaValidazione tipo di Validazione
	 * 
	 */
	public void valida(OpenSPCoop2Message msg, ProprietaValidazione proprietaValidazione,RuoloBusta tipoBusta,String versioneProtocollo){
		try {
			
			proprietaValidazione.setValidazioneIDCompleta(this.validazioneIdentificativiCompleta);
			proprietaValidazione.setVersioneProtocollo(versioneProtocollo);
			ValidazioneSemanticaResult result = this.protocolFactory.createValidazioneSemantica().valida(msg, this.busta, this.state, proprietaValidazione, tipoBusta);
			this.infoServizio = result.getInfoServizio();
			this.servizioCorrelato = result.getServizioCorrelato();
			this.tipoServizioCorrelato = result.getTipoServizioCorrelato();
			this.erroriProcessamento = result.getErroriProcessamento();
			if(this.erroriProcessamento == null) 
				this.erroriProcessamento = new java.util.Vector<Eccezione>();
			this.erroriValidazione = result.getErroriValidazione();
			if(this.erroriValidazione == null) 
				this.erroriValidazione = new java.util.Vector<Eccezione>();
			
			// Controllo correlazione alla richiesta per buste contenenti Risposte o ricevute 
			//System.out.println("TIPO BUSTA ["+tipoBusta+"]");
			if(this.erroriValidazione.size()==0 && this.erroriProcessamento.size()==0 && this.busta!=null ){
				if( RuoloBusta.BUSTA_DI_SERVIZIO.equals(tipoBusta.toString()) == false &&
						RuoloBusta.RICHIESTA.equals(tipoBusta.toString()) == false){ 
					//log.info("Validazione correlazione...");
					validazioneCorrelazione(tipoBusta);
				}
			}
			
			// Controllo riferimentoMessaggio
			if(this.erroriValidazione.size()==0 && this.erroriProcessamento.size()==0 && this.busta!=null && this.busta.getRiferimentoMessaggio()!=null){
				validazioneRiferimentoMessaggio(tipoBusta);
			}
			
			// Riconoscimento collaborazione
			if(this.erroriValidazione.size()==0 && this.erroriProcessamento.size()==0 && this.busta!=null && this.busta.getProfiloDiCollaborazione()!=null ){
				// Controllo Collaborazione (validazioneID e Riconoscimento)
				// Controllo che non deve essere effettuato se ho ricevuto un Fault
				if( RuoloBusta.BUSTA_DI_SERVIZIO.equals(tipoBusta.toString()) == false ){ 
					//log.info("Validazione Collaborazione...");
					IProtocolVersionManager protocolVersioneManager = this.protocolFactory.createProtocolVersionManager(versioneProtocollo);
					StatoFunzionalitaProtocollo modalitaGestioneCollaborazione = protocolVersioneManager.getCollaborazione(this.busta.getProfiloDiCollaborazione());
					if(StatoFunzionalitaProtocollo.ABILITATA.equals(modalitaGestioneCollaborazione) || 
							(StatoFunzionalitaProtocollo.REGISTRO.equals(modalitaGestioneCollaborazione) && (this.infoServizio!=null) && this.infoServizio.getCollaborazione() ) ){
						riconoscimentoCollaborazione(tipoBusta);		
					}
				}
			}
			
		} catch (ProtocolException e) {
			this.log.error("ProtocolException error",e);
		}
	}

	public static String riconoscimentoVersioneProtocolloServizioErogato(Busta busta, org.openspcoop2.protocol.sdk.config.ITraduttore costanti, RuoloBusta tipoBusta, IState state) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		// Normalmente prendo il soggetto destinatario come erogatore del servizio.
		// Devono essere gestiti i seguenti casi particolari, in cui devo invece prendere il mittente
		// - risposta sincrona
		// - ricevute asincrone asimmetriche 
		// 
		// Caso speciale è il profilo asincrono simmetrico
		// - richiesta l'erogatore è il destinatario 
		// - ricevuta alla richiesta è il mittente
		// - risposta è il destinatario, però se per il servizio non è definito un profilo, poi devo vedere il fruitore, ancora per il destinatario
		// - ricevuta alla risposta è il mittente, però se per il servizio non è definito un profilo, poi devo vedere il fruitore, ancora per il mittente
		//
		// Inoltre deve essere gestito il caso in cui la busta arrivata non contiene un servizio.
		// In tal caso mi base sul soggetto mittente, se questo ha profilo che supporta i riscontri, può darsi che mi sta inviando una busta di servizio.
		// Altrimenti, questo non puo' succedere e verrà segnalato un errore

		IDSoggetto idSoggettoFruitoreProfiloGestito = null;
		if(busta.getTipoServizio()!=null && busta.getServizio()!=null){
			IDServizio idServizioProfiloGestito = new IDServizio();
			idServizioProfiloGestito.setTipoServizio(busta.getTipoServizio());
			idServizioProfiloGestito.setServizio(busta.getServizio());
			idServizioProfiloGestito.setAzione(busta.getAzione());
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(busta.getProfiloDiCollaborazione()) && RuoloBusta.RISPOSTA.equals(tipoBusta.toString())){
				idServizioProfiloGestito.setSoggettoErogatore(busta.getTipoMittente(), busta.getMittente());
				idSoggettoFruitoreProfiloGestito = new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario());
			}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione()) && (RuoloBusta.RICHIESTA.equals(tipoBusta.toString())==false) ){
				if(RuoloBusta.RICEVUTA_RICHIESTA.equals(tipoBusta.toString())){
					idServizioProfiloGestito.setSoggettoErogatore(busta.getTipoMittente(), busta.getMittente());
					idSoggettoFruitoreProfiloGestito = new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario());
				}else if(RuoloBusta.RISPOSTA.equals(tipoBusta.toString())){
					idServizioProfiloGestito.setSoggettoErogatore(busta.getTipoDestinatario(), busta.getDestinatario());
					idSoggettoFruitoreProfiloGestito = new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario());
				}else { // RICEVUTA_RISPOSTA
					idServizioProfiloGestito.setSoggettoErogatore(busta.getTipoMittente(), busta.getMittente());
					idSoggettoFruitoreProfiloGestito = new IDSoggetto(busta.getTipoMittente(), busta.getMittente());
				}
			}else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(busta.getProfiloDiCollaborazione()) &&
					(RuoloBusta.RICEVUTA_RICHIESTA.equals(tipoBusta.toString()) || RuoloBusta.RICEVUTA_RISPOSTA.equals(tipoBusta.toString())) ){
				idServizioProfiloGestito.setSoggettoErogatore(busta.getTipoMittente(), busta.getMittente());
				idSoggettoFruitoreProfiloGestito = new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario());
			}else{
				idServizioProfiloGestito.setSoggettoErogatore(busta.getTipoDestinatario(), busta.getDestinatario());
				idSoggettoFruitoreProfiloGestito = new IDSoggetto(busta.getTipoMittente(), busta.getMittente());
			}
			return RegistroServiziManager.getInstance(state).getProfiloGestioneErogazioneServizio(idSoggettoFruitoreProfiloGestito, idServizioProfiloGestito, null);
		}
		else{
			idSoggettoFruitoreProfiloGestito = new IDSoggetto(busta.getTipoMittente(), busta.getMittente());
			return RegistroServiziManager.getInstance(state).getProfiloGestioneSoggetto(idSoggettoFruitoreProfiloGestito, null);
		}
	}


	public static RuoloBusta getTipoBustaDaValidare(Busta busta, org.openspcoop2.protocol.sdk.IProtocolFactory protocolFactory, boolean validazioneRispostaHttpReply, IState state, Logger log) throws ProtocolException{

		RuoloBusta tipo = RuoloBusta.BUSTA_DI_SERVIZIO;

		if(busta.getProfiloDiCollaborazione()==null || 
				busta.getServizio()==null ||
				busta.getTipoServizio()==null){	
			tipo = RuoloBusta.BUSTA_DI_SERVIZIO;
		} 

		// OneWay
		// X Interoperabilita'
		else if(busta.getProfiloDiCollaborazione().equals(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY) &&
				busta.getRiferimentoMessaggio()!=null)
			tipo = RuoloBusta.BUSTA_DI_SERVIZIO;
		else if(busta.getProfiloDiCollaborazione().equals(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY))
			tipo = RuoloBusta.RICHIESTA;

		// Sincrono
		else if(busta.getProfiloDiCollaborazione().equals(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO)){
			if(busta.getRiferimentoMessaggio() == null)
				tipo = RuoloBusta.RICHIESTA;
			else
				tipo = RuoloBusta.RISPOSTA;
		}
		
		// Asincrono Simmetrico e Asimmetrico
		else if( (busta.getProfiloDiCollaborazione().equals(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO)) ||
				(busta.getProfiloDiCollaborazione().equals(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)) ){
			// NOTA:
			//   una busta asincrona RISPOSTA puo avere riferimentoMessaggio o collaborazione
		
			if( (busta.getRiferimentoMessaggio() == null) && (busta.getCollaborazione()==null) ){
				tipo = RuoloBusta.RICHIESTA;
			} else {
				ProfiloDiCollaborazione profilo = new ProfiloDiCollaborazione(state, log, protocolFactory);
				if(profilo.asincrono_isRicevutaRichiesta(busta.getRiferimentoMessaggio())){
					tipo = RuoloBusta.RICEVUTA_RICHIESTA;	
				}else if(profilo.asincrono_isRicevutaRisposta(busta.getRiferimentoMessaggio())){
					tipo = RuoloBusta.RICEVUTA_RISPOSTA;	
				}else if( (validazioneRispostaHttpReply==false) && (profilo.asincrono_isRisposta(busta)) ){
					tipo = RuoloBusta.RISPOSTA;
				}else{
					tipo = RuoloBusta.RICHIESTA;
				}
			}
		} 
		return tipo;
	}

 
	/**
	 * Metodo che si occupa di validare l'identificativo <var>id</var> secondo la specifica.
	 *
	 * @param id identificativo da validare
	 * @return il valore boolean true se l'identificativo fornito e' conforme alla specifica. Altrimenti false.
	 * @throws ProtocolException 
	 * 
	 */
	public boolean validazioneID(String id) throws ProtocolException{
		return validazioneID(id,null,null,null);
	}

	/**
	 * Metodo che si occupa di validare l'identificativo <var>id</var> secondo la specifica.
	 * L'identificativo e' formato da :
	 * codAmministrazione_codPortaDominio_num.progressivo_data_ora
	 * <p>
	 * Il codice Amministrazione e' preso da <var>codAmm</var>. 
	 * Il codice Amministrazione deve contenere l'identificativo parte del mittente, quando deve essere validato
	 * un identificativo del messaggio od una collaborazione di una richiesta 
	 * (oneWay, richiestaSincrona , richiesta/ricevutaRisposta AsincronaSimmetrica , 
	 *  richiesta/richiestaStato AsincronaAsimmetrica), poiche' e' lui che lo ha spedito 
	 * e che ha quindi creato l'identificatore.
	 * Deve contenere invece il codice del destinatario, quando deve essere validato un RiferimentoMessaggio, od una
	 * collaborazione di una risposta (Sincrona , ricevutaRichiesta/Risposta AsincronaSimmetrica ,
	 * ricevutaRichiesta/ricevutaRichiestaStato AsincronaAsimmetrica),
	 * poiche' la creazione dell'identificatore venne fatta dal destinatario quando creo' la richiesta.
	 * <p>
	 * Il codice amministrativo <var>codAmm</var>,  sara' utilizzato anche per ottenere dal 
	 * registro dei servizi di openspcoop la validazione del codice della Porta di Dominio presente nell'identificativo.
	 * 
	 * Prima di invocare questo metodo deve essere stata effettuata l'inizializzazione 
	 * dell'engine utilizzato per leggere all'interno del registro dei servizi, 
	 * attraverso il metodo 'initialize' della classe {@link org.openspcoop2.protocol.registry.RegistroServiziReader}
	 *
	 * @param id identificativo da validare
	 * @param codAmm Identificativo Parte mittente o destinatario di una busta.
	 * @param codDominio Identificativo del dominio del codice di amministrazione
	 * @return il valore boolean true se l'identificativo fornito e' conforme alla specifica. Altrimenti false.
	 * @throws ProtocolException 
	 * 
	 */
	private boolean validazioneID(String id, String tipoCodAmm, String codAmm, String codDominio) throws ProtocolException{
		ProprietaValidazione proprietaValidazione = new ProprietaValidazione();
		proprietaValidazione.setValidazioneIDCompleta(this.validazioneIdentificativiCompleta);
		IDSoggetto dominio = new IDSoggetto(tipoCodAmm, codAmm, codDominio);
		return this.protocolFactory.createValidazioneSemantica().validazioneID(id, dominio, proprietaValidazione);
	}

	
	/**
	 * Metodo che si occupa di validare il riferimento del messaggio
	 *
	 * @param tipoBusta tipo di Busta da validare
	 * 
	 */
	private void validazioneRiferimentoMessaggio(RuoloBusta tipoBusta)throws ProtocolException{
		if(this.state instanceof StatefulMessage) {
			StatefulMessage stateful = (StatefulMessage)this.state;

			//OneWay: non importa il riferimentoMessaggio...

			org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione profiloDiCollaborazione = 
				new org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione(stateful, this.log, this.protocolFactory);
			boolean error = false;

			// Sincrono
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.busta.getProfiloDiCollaborazione())){
				if(profiloDiCollaborazione.sincrono_validazioneRiferimentoMessaggio(this.busta.getRiferimentoMessaggio())==false){
					error = true;
				}
			}   

			// Asincrono Simmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.busta.getProfiloDiCollaborazione())){
				if(profiloDiCollaborazione.asincronoSimmetrico_validazioneRiferimentoMessaggio(this.busta.getRiferimentoMessaggio())==false){
					error = true;
				}
			} 

			//	Asincrono Asimmetrico
			else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.busta.getProfiloDiCollaborazione())){
				if(RuoloBusta.RISPOSTA.equals(tipoBusta.toString())){
					// richiestaStato
					if(profiloDiCollaborazione.asincronoAsimmetrico_validazioneRiferimentoMessaggio_richiestaStato(this.busta.getRiferimentoMessaggio())==false){
						error = true;
					}
				}else {
					// ricevuta
					if(profiloDiCollaborazione.asincronoAsimmetrico_validazioneRiferimentoMessaggio_ricevuta(this.busta.getRiferimentoMessaggio())==false){
						error = true;
					}
				}
			} 

			if(error){
				Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO.getErroreCooperazione(), this.protocolFactory);
				this.erroriValidazione.add(ecc);
			}
		}else if (this.state instanceof StatelessMessage){
			// Lo stateless gestisce solo i profili oneway e sincrono
			// Il oneway non ha una busta correlata
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.busta.getProfiloDiCollaborazione())){
				StatelessMessage stateless = (StatelessMessage)this.state;
				Busta bustaCorrelata = stateless.getBustaCorrelata();
				if(bustaCorrelata!=null){
					if(this.busta.getRiferimentoMessaggio()!=null && !(this.busta.getRiferimentoMessaggio().equals(bustaCorrelata.getID())) ){
						Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO.getErroreCooperazione(), this.protocolFactory);
						this.erroriValidazione.add(ecc);
					}
				}
			}
		}else{
			throw new ProtocolException("Metodo invocato con IState non valido");
		}
	}
	
	private void riconoscimentoCollaborazione(RuoloBusta tipoBusta) throws ProtocolException{
		
		//log.info("Validazione mittente...");
		// Mittente: check registrazione nel Registro dei Servizi
		IDSoggetto soggMitt = new IDSoggetto(this.busta.getTipoMittente(),this.busta.getMittente());
		String dominioMittente = null;
		try{
			dominioMittente = this.registroServiziReader.getDominio(soggMitt,null,this.protocolFactory); //null=allRegistri 
			if(dominioMittente==null)
				throw new Exception("Dominio non definito");
		}catch(DriverRegistroServiziNotFound es){
			Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MITTENTE_SCONOSCIUTO.getErroreCooperazione(), this.protocolFactory);
			this.log.debug("Identificazione mittente fallita:"+es.getMessage());
			this.erroriValidazione.add(ecc);
			return;
		}catch(Exception es){
			Eccezione ecc = Eccezione.getEccezioneProcessamento(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.
					getErroreProcessamento("Identificazione mittente non riuscita: errore di processamento"), this.protocolFactory);
			this.log.error("Identificazione mittente non riuscita",es);
			this.erroriProcessamento.add(ecc);
			return;
		}

		//log.info("Validazione destinatario...");
		// Destinatario: check registrazione nel Registro dei Servizi
		IDSoggetto soggDest = new IDSoggetto(this.busta.getTipoDestinatario(),this.busta.getDestinatario());
		String dominioDestinatario = null;
		try{
			dominioDestinatario = this.registroServiziReader.getDominio(soggDest,null,this.protocolFactory); // null=allRegistri
			if(dominioDestinatario==null)
				throw new Exception("Dominio non definito");
		}catch(DriverRegistroServiziNotFound es){
			Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.DESTINATARIO_SCONOSCIUTO.getErroreCooperazione(), this.protocolFactory);
			this.log.debug("Identificazione destinatario fallita: "+es.getMessage());
			this.erroriValidazione.add(ecc);
			return;
		}catch(Exception es){
			Eccezione ecc = Eccezione.getEccezioneProcessamento(ErroriCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO.
					getErroreProcessamento("Identificazione destinatario non riuscita: errore di processamento"), this.protocolFactory);
			this.log.error("Identificazione destinatario non riuscita",es);
			this.erroriProcessamento.add(ecc);
			return;
		}
		
		
		// La collaborazione deve appartenere o al mittente, o al destinatario della busta.
		if(this.busta.getCollaborazione()!=null){
			if( (validazioneID(this.busta.getCollaborazione(),this.busta.getTipoMittente(),this.busta.getMittente(),dominioMittente) == false) &&
					(validazioneID(this.busta.getCollaborazione(),this.busta.getTipoDestinatario(),this.busta.getDestinatario(),dominioDestinatario) == false) ){
				Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.COLLABORAZIONE_NON_VALIDA.getErroreCooperazione(), this.protocolFactory);
				this.erroriValidazione.add(ecc);
				return;
			}
		}
		
		if(this.state instanceof StatefulMessage) {
			StatefulMessage stateful = (StatefulMessage)this.state;
			if(this.busta.getCollaborazione()!=null){

				org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione profiloDiCollaborazione = 
					new org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione(stateful, this.log, this.protocolFactory);
				
				// Validazione Collaborazione.
				boolean error = false;

				// Check per profili non oneway
				if(!org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(this.busta.getProfiloDiCollaborazione())){

					//	Sulla busta di richiesta non posso effettuare alcun controllo.
					if(tipoBusta.equals("Richiesta") == false){

						// buste di risposta/ricevuta
						//	Sincrono
						if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.busta.getProfiloDiCollaborazione())){
							if(RuoloBusta.RISPOSTA.equals(tipoBusta.toString())){ 
								if(profiloDiCollaborazione.sincrono_validazioneCollaborazione(this.busta.getRiferimentoMessaggio(),this.busta.getCollaborazione())==false){
									error = true;
								}
							}
						}
						// Asincrono Simmetrico
						else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.busta.getProfiloDiCollaborazione())){
							// Risposta
							if(RuoloBusta.RISPOSTA.equals(tipoBusta.toString())){
								if(profiloDiCollaborazione.asincronoSimmetrico_validazioneCollaborazione_risposta(this.busta.getRiferimentoMessaggio(),this.busta.getCollaborazione())==false){
									error = true;
								}
							}else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(tipoBusta.toString()) || RuoloBusta.RICEVUTA_RISPOSTA.equals(tipoBusta.toString())){
								if(profiloDiCollaborazione.asincronoSimmetrico_validazioneCollaborazione_ricevuta(this.busta.getRiferimentoMessaggio(),this.busta.getCollaborazione())==false){
									error = true;
								}
							}
						}
						// Asincrono Asimmetrico
						else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.busta.getProfiloDiCollaborazione())){
							// richiestaStato
							if(RuoloBusta.RISPOSTA.equals(tipoBusta.toString())){
								if(profiloDiCollaborazione.asincronoAsimmetrico_validazioneCollaborazione_richiestaStato(this.busta.getRiferimentoMessaggio(),this.busta.getCollaborazione())==false){
									error = true;
								}
							}else if(RuoloBusta.RICEVUTA_RICHIESTA.equals(tipoBusta.toString()) || RuoloBusta.RICEVUTA_RISPOSTA.equals(tipoBusta.toString())){
								if(profiloDiCollaborazione.asincronoAsimmetrico_validazioneCollaborazione_ricevuta(this.busta.getRiferimentoMessaggio(),this.busta.getCollaborazione())==false){
									error = true;
								}
							}
						}	
					}
				}

				// check per profilo oneway
				else{

					org.openspcoop2.protocol.engine.driver.ConsegnaInOrdine consegna = new org.openspcoop2.protocol.engine.driver.ConsegnaInOrdine(stateful,this.log,this.protocolFactory);
					Eccezione ecc = consegna.validazioneDatiConsegnaInOrdine(this.busta, this.protocolFactory);
					if(ecc!=null){
						this.erroriValidazione.add(ecc);
						return;
					}

				}

				// se errori sono stati riscontrati in un profilo non oneWay
				if(error){
					Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.COLLABORAZIONE_SCONOSCIUTA.getErroreCooperazione(), this.protocolFactory);
					this.erroriValidazione.add(ecc);
					return;
				}
			}

			return;
		}else if (this.state instanceof StatelessMessage){
			// Lo stateless gestisce solo i profili oneway e sincrono
			
			StatelessMessage stateless = (StatelessMessage)this.state;
			
			// Check per profili oneway
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(this.busta.getProfiloDiCollaborazione())){
			
				org.openspcoop2.protocol.engine.driver.ConsegnaInOrdine consegna = new org.openspcoop2.protocol.engine.driver.ConsegnaInOrdine(stateless,this.log,this.protocolFactory);
				Eccezione ecc = consegna.validazioneDatiConsegnaInOrdine(this.busta, this.protocolFactory);
				if(ecc!=null){
					this.erroriValidazione.add(ecc);
					return;
				}
				
			}
			
			return;
		}else{
			throw new ProtocolException("Metodo invocato con IState non valido");
		}
	}
	
	/**
	 * Effettua la validazione della correlazione con la precedente richiesta
	 * 
	 * @param tipoBusta tipo di Busta da validare
	 * 
	 */
	private void validazioneCorrelazione(RuoloBusta tipoBusta) throws ProtocolException{
		if(this.state instanceof StatefulMessage) {
			StatefulMessage stateful = (StatefulMessage)this.state;
			org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione profiloDiCollaborazione = 
				new org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione(stateful, this.log, this.protocolFactory);
			Eccezione ecc = null;
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.busta.getProfiloDiCollaborazione())){
				ecc = profiloDiCollaborazione.sincrono_validazioneCorrelazione(this.busta, this.protocolFactory);
			}
			else if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.busta.getProfiloDiCollaborazione()) ){
				if( RuoloBusta.RICEVUTA_RICHIESTA.equals(tipoBusta.toString()) ){
					ecc = profiloDiCollaborazione.asincronoSimmetrico_validazioneCorrelazione_ricevutaRichiesta(this.busta, this.protocolFactory);
				}else if( RuoloBusta.RISPOSTA.equals(tipoBusta.toString()) ){
					ecc = profiloDiCollaborazione.asincronoSimmetrico_validazioneCorrelazione_risposta(this.busta, this.protocolFactory);
				}else if( RuoloBusta.RICEVUTA_RISPOSTA.equals(tipoBusta.toString()) ){
					ecc = profiloDiCollaborazione.asincronoSimmetrico_validazioneCorrelazione_ricevutaRisposta(this.busta, this.protocolFactory);
				}
			}
			else if( org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.busta.getProfiloDiCollaborazione()) ){
				if( RuoloBusta.RICEVUTA_RICHIESTA.equals(tipoBusta.toString()) ){
					ecc = profiloDiCollaborazione.asincronoAsimmetrico_validazioneCorrelazione_ricevutaRichiesta(this.busta, this.protocolFactory);
				}else if( RuoloBusta.RISPOSTA.equals(tipoBusta.toString()) ){
					ecc = profiloDiCollaborazione.asincronoAsimmetrico_validazioneCorrelazione_richiestaStato(this.busta, this.protocolFactory);
				}else if( RuoloBusta.RICEVUTA_RISPOSTA.equals(tipoBusta.toString()) ){
					ecc = profiloDiCollaborazione.asincronoAsimmetrico_validazioneCorrelazione_ricevutaRichiestaStato(this.busta, this.protocolFactory);
				}
			}	
			if(ecc!=null){
				this.erroriValidazione.add(ecc);
				return;
			}
		}else if (this.state instanceof StatelessMessage){
			// Lo stateless gestisce solo i profili oneway e sincrono
			// Il oneway non ha una busta correlata
			if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.SINCRONO.equals(this.busta.getProfiloDiCollaborazione())){
				StatelessMessage stateless = (StatelessMessage)this.state;
				Busta bustaCorrelata = stateless.getBustaCorrelata();
				if(bustaCorrelata!=null){
										
					if (this.busta.getTipoDestinatario().equals(bustaCorrelata.getTipoMittente())==false){
						Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.TIPO_MITTENTE_NON_VALIDO.
								getErroreCooperazione("Tipo del mittente diverso da quello atteso nella gestione del profilo di collaborazione Sincrono"), this.protocolFactory);
						this.erroriValidazione.add(ecc);
						return;
					}
					if (this.busta.getDestinatario().equals(bustaCorrelata.getMittente())==false){
						Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MITTENTE_NON_VALIDO.
								getErroreCooperazione("Mittente diverso da quello atteso nella gestione del profilo di collaborazione Sincrono"), this.protocolFactory);
						this.erroriValidazione.add(ecc);
						return;
					}
					if (this.busta.getTipoMittente().equals(bustaCorrelata.getTipoDestinatario())==false){
						Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.TIPO_DESTINATARIO_NON_VALIDO.
								getErroreCooperazione("Tipo del destinatario diverso da quello atteso nella gestione del profilo di collaborazione Sincrono"), this.protocolFactory);
						this.erroriValidazione.add(ecc);
						return;
					}
					if (this.busta.getMittente().equals(bustaCorrelata.getDestinatario())==false){
						Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.DESTINATARIO_NON_VALIDO.
								getErroreCooperazione("Destinatario diverso da quello atteso nella gestione del profilo di collaborazione Sincrono"), this.protocolFactory);
						this.erroriValidazione.add(ecc);
						return;
					}

					if(this.busta.getServizio()!=null && bustaCorrelata.getTipoServizio()!=null){
						if (this.busta.getTipoServizio().equals(bustaCorrelata.getTipoServizio())==false){
							Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.TIPO_SERVIZIO_NON_VALIDO.
									getErroreCooperazione("Tipo di servizio diverso da quello atteso nella gestione del profilo di collaborazione Sincrono"), this.protocolFactory);
							this.erroriValidazione.add(ecc);
							return;
						}
						if (this.busta.getServizio().equals(bustaCorrelata.getServizio())==false){
							Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.SERVIZIO_NON_VALIDO.
									getErroreCooperazione("Servizio diverso da quello atteso nella gestione del profilo di collaborazione Sincrono"), this.protocolFactory);
							this.erroriValidazione.add(ecc);
							return;
						}
						if(this.busta.getAzione()!=null){
							if (this.busta.getAzione().equals(bustaCorrelata.getAzione()) == false){
								Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.AZIONE_NON_VALIDA.
										getErroreCooperazione("Azione diversa da quella atteso nella gestione del profilo di collaborazione Sincrono"), this.protocolFactory);
								this.erroriValidazione.add(ecc);
								return;
							}
						}
					}
				}
			}
		}else{
			throw new ProtocolException("Metodo invocato con IState non valido");
		}
	}
	
	/**
	 * Metodo che si occupa di validare il riferimento del messaggio
	 *
	 * @param tipoBusta tipo di Busta da validare
	 * 
	 */
	
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
	public String getAzioneCorrelata() {
		return this.azioneCorrelata;
	}
	public void setAzioneCorrelata(String azioneCorrelata) {
		this.azioneCorrelata = azioneCorrelata;
	}
	public void setValidazioneIdentificativiCompleta(boolean v) {
		this.validazioneIdentificativiCompleta = v;
	}

}
