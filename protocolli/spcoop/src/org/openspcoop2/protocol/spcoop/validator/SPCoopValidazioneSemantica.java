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



package org.openspcoop2.protocol.spcoop.validator;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.protocol.registry.RisultatoValidazione;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSemanticaResult;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostantiPosizioneEccezione;
import org.openspcoop2.utils.digest.IDigestReader;



/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica}
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SPCoopValidazioneSemantica implements IValidazioneSemantica {

	/** Se IState e' un'istanza di StatefulMessage possiede una Connessione SQL in autoCommit mode su cui effettuare query 
	 *  Altrimenti, e' un'istanza di StatelessMessage e nn necessita di connessioni  */
	@SuppressWarnings("unused")
	private IState state;

	/** Errori di validazione riscontrati sulla busta */
	private java.util.Vector<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	private java.util.Vector<Eccezione> erroriProcessamento;
	/** Busta */
	private Busta busta;
	/** Validazione IDEGov completa */
	private boolean validazioneIDEGovCompleta = false;


	/** ServizioCorrelato */
	private String servizioCorrelato;
	/** Tipo ServizioCorrelato */
	private String tipoServizioCorrelato;
	/** Azione Correlata */
	private String azioneCorrelata;
	/** Reader Registro */
	private RegistroServiziManager registroServiziReader;
	/** informazioni Servizio */
	private Servizio infoServizio = null;
	
	/** Logger utilizzato per debug. */
	private Logger log = null;
	private IProtocolFactory protocolFactory;
	
	

	/**
	 * Costruttore.
	 *
	 * @param protocolFactory ProtocolFactory
	 * 
	 */
	public SPCoopValidazioneSemantica(IProtocolFactory protocolFactory){
		this.log = protocolFactory.getLogger();
		this.protocolFactory = protocolFactory;
	}


	/**
	 * Metodo che effettua la validazione dei soggetti di una busta, controllando la loro registrazione nel registro dei servizi. 
	 *
	 * Mano mano che sono incontrati errori di validazione, viene creato un oggetto 
	 *   {@link Eccezione}, e viene inserito nel Vector <var>errors</var>.
	 *
	 * @param tipoValidazione tipo di Validazione
	 * 
	 */
	public void valida(ProprietaValidazione tipoValidazione, RuoloBusta tipoBusta, String profiloGestione){

		try{
			this.erroriValidazione = new java.util.Vector<Eccezione>();
			this.erroriProcessamento = new java.util.Vector<Eccezione>();
			//log.info("Validazione semantica...");			
			
			/* Accesso al registro */
			if( RegistroServiziReader.isInitialize()==false || this.registroServiziReader==null){
				this.log.error("Registro dei Servizi non inizializzato.");
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione("Registro dei Servizi non disponibile");
				this.erroriProcessamento.add(ecc);
				return;
			}
			

			//log.info("Validazione mittente...");
			// Mittente: check registrazione nel Registro dei Servizi
			IDSoggetto soggMitt = new IDSoggetto(this.busta.getTipoMittente(),this.busta.getMittente());
			String dominioMittente = null;
			boolean mittenteSconosciuto = false;
			try{
				dominioMittente = this.registroServiziReader.getDominio(soggMitt,null,this.protocolFactory); //null=allRegistri 
				if(dominioMittente==null)
					throw new Exception("Dominio non definito");
				else
					this.busta.setIdentificativoPortaMittente(dominioMittente);
			}catch(DriverRegistroServiziNotFound es){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString());
				this.log.debug("Identificazione mittente fallita:"+es.getMessage());
				this.erroriValidazione.add(ecc);
				mittenteSconosciuto = true;
			}catch(Exception es){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione("Identificazione mittente non riuscita: errore di processamento");
				this.log.error("Identificazione mittente non riuscita",es);
				this.erroriProcessamento.add(ecc);
				mittenteSconosciuto = true;
			}

			//log.info("Validazione destinatario...");
			// Destinatario: check registrazione nel Registro dei Servizi
			IDSoggetto soggDest = new IDSoggetto(this.busta.getTipoDestinatario(),this.busta.getDestinatario());
			String dominioDestinatario = null;
			boolean destinatarioSconosciuto = false;
			try{
				dominioDestinatario = this.registroServiziReader.getDominio(soggDest,null,this.protocolFactory); // null=allRegistri
				if(dominioDestinatario==null)
					throw new Exception("Dominio non definito");
				else
					this.busta.setIdentificativoPortaDestinatario(dominioDestinatario);
			}catch(DriverRegistroServiziNotFound es){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString());
				this.log.debug("Identificazione destinatario fallita: "+es.getMessage());
				this.erroriValidazione.add(ecc);
				destinatarioSconosciuto = true;
			}catch(Exception es){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione("Identificazione destinatario non riuscita: errore di processamento");
				this.log.error("Identificazione destinatario non riuscita",es);
				this.erroriProcessamento.add(ecc);
				destinatarioSconosciuto = true;
			}

			// Riconoscimento servizio-azione: viene effettuato per Richiesta/Risposta/Ricevuta
			// Per risposte e ricevute, viene controllato che esista servizio e azione associate al mittente della busta
			// per richieste, viene controllato che esista servizio e azione associate al destinatario della busta
			RisultatoValidazione risultatoValidazioneServizio = null;
			if( RuoloBusta.BUSTA_DI_SERVIZIO.equals(tipoBusta.toString()) == false ){ 
				//log.info("Validazione servizio azione...");
				risultatoValidazioneServizio = riconoscimentoServizioAzione(tipoBusta,mittenteSconosciuto,destinatarioSconosciuto);
			}
			// Controllo correlazione alla richiesta per buste contenenti Risposte o ricevute 
			//System.out.println("TIPO BUSTA ["+tipoBusta+"]");
			// LOGICA SPOSTATA NELL'ENGINE VALIDATOR
//			if( RuoloBusta.BUSTA_DI_SERVIZIO.equals(tipoBusta.toString()) == false &&
//					RuoloBusta.RICHIESTA.equals(tipoBusta.toString()) == false){ 
//				//log.info("Validazione correlazione...");
//				validazioneCorrelazione(tipoBusta);
//			}

			// boolean indicazione se il servizio e' stato riconosciuto in base a mittente, destinatario servizio e azione
			boolean servizioBustaValido = false;
			if( (RuoloBusta.BUSTA_DI_SERVIZIO.equals(tipoBusta.toString()) == false ) &&
					(destinatarioSconosciuto==false) &&
					(mittenteSconosciuto==false) ){ 
				//this.log.info("Indicazione servizio...");
				if(risultatoValidazioneServizio!=null && risultatoValidazioneServizio.getServizioRegistrato()){
					if( (this.busta.getAzione()==null) && (risultatoValidazioneServizio.getAccessoSenzaAzione()) ){
						servizioBustaValido = true;
					}else if(this.busta.getAzione()!=null){
						if(risultatoValidazioneServizio.getAzioni()!=null){
							for(int j=0; j<risultatoValidazioneServizio.sizeAzioni();j++){
								if(this.busta.getAzione().equals(risultatoValidazioneServizio.getAzioni()[j])){
									servizioBustaValido = true;
									break;
								}
							}
						}
					}
				} 
				//this.log.info("Raccolta info servizio...");
				if(servizioBustaValido){
					IDSoggetto idSoggettoFruitore = new IDSoggetto();
					IDSoggetto idSoggettoErogatore = new IDSoggetto();
					this.impostaFruitoreErogatoreRealiServizio(idSoggettoFruitore,idSoggettoErogatore,tipoBusta);
					
					IDServizio idServizio = new IDServizio(idSoggettoErogatore,this.busta.getTipoServizio(),this.busta.getServizio(),this.busta.getAzione());
					try{
						this.infoServizio = this.registroServiziReader.getInfoServizio(idSoggettoFruitore,idServizio,null,false);//null=allRegistri
					}catch(DriverRegistroServiziNotFound dnot){}
					if(this.infoServizio==null){
						try{
							this.infoServizio = this.registroServiziReader.getInfoServizioCorrelato(soggMitt, idServizio, null);//null=allRegistri
						}catch(DriverRegistroServiziNotFound dnot){}
					}
					if(this.infoServizio==null){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						ecc.setDescrizione("Raccolta informazioni servizio: servizio sconosciuto");
						this.erroriValidazione.add(ecc);
						servizioBustaValido = false;
					}
				}
				//this.log.info("Raccolta info servizio effettuata");
			}
		
			
			
			// Riconoscimento profilo di collaborazione: viene controllato che sia usato il profilo di collaborazione registrato
			// Per risposte e ricevute, viene controllato che esista servizio e azione associate al mittente della busta (fruitore e' destinatario)
			// per richieste, viene controllato che esista servizio e azione associate al destinatario della busta (fruitore e' destinatario)
			if( (servizioBustaValido) && (tipoValidazione.isValidazioneProfiloCollaborazione()) && (RuoloBusta.RICHIESTA.equals(tipoBusta.toString())) ){
				riconoscimentoProfiloCollaborazione(tipoBusta);
			}

			// Identificativo Messaggio: viene controllato che l'identificativo porta sia quello del mittente della busta
			//log.info("Validazione ID...");
			if(mittenteSconosciuto==false){
				if( validazioneID_engine(this.busta.getID(),this.busta.getMittente(),dominioMittente) == false ){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_VALIDO_POSIZIONE.toString());
					this.erroriValidazione.add(ecc);
				}
			}

			// Riferimento Messaggio: viene controllato che l'identificativo porta sia quello del mittente della busta
			//                        viene inoltro controllato che il riferimento messaggio sia quello aspettato per il profilo gestito
			if(this.busta.getRiferimentoMessaggio()!=null){
				//log.info("Validazione RifMsg... ["+tipoBusta+"] ["+this.busta.getRiferimentoMessaggio()+"]");
				boolean validazioneIDRiferimentoMessaggio = true;
				if(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.busta.getProfiloDiCollaborazione()) &&
						RuoloBusta.RISPOSTA.equals(tipoBusta.toString())){
					// per il profilo asincrono asimmetrico devo usare come dominio/nome per il rifMsg il mittente, invece che il destinatario
					if(mittenteSconosciuto==false){
						validazioneIDRiferimentoMessaggio = validazioneID_engine(this.busta.getRiferimentoMessaggio(),this.busta.getMittente(),dominioMittente);
					}
				}else{
					if(destinatarioSconosciuto==false){
						validazioneIDRiferimentoMessaggio = validazioneID_engine(this.busta.getRiferimentoMessaggio(),this.busta.getDestinatario(),dominioDestinatario);
					}
				}
				if(validazioneIDRiferimentoMessaggio  == false ){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_NON_VALIDO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_VALIDO_POSIZIONE.toString());
					this.erroriValidazione.add(ecc);
				}else{
					//log.info("Validazione RifMsg specifico...");
					// LOGICA SPOSTATA NELL'ENGINE VALIDATOR
					//validazioneRiferimentoMessaggio(tipoBusta);
				}
			}

			// Controllo Collaborazione (validazioneID e Riconoscimento)
			// Controllo che non deve essere effettuato se ho ricevuto un Fault
			// LOGICA DI RICONOSCIMENTO COLLABORAZIONE SPOSTATA NELL'ENGINE VALIDATOR.
			// Qua viene controllato solo la struttura dell'id
			if( RuoloBusta.BUSTA_DI_SERVIZIO.equals(tipoBusta.toString()) == false ){ 
				//log.info("Validazione Collaborazione...");
				validazioneCollaborazione(tipoBusta,dominioMittente,dominioDestinatario);
			}

			// Validazione Sequenza: se presente, ci deve essere anche una collaborazione valida (se si arriva qua, e' garantito) 
			// ed un profiloTrasmissione con 'inoltro= ALPIUUNAVOLTA' e 'conferma=TRUE'
			//log.info("Validazione Sequenza...");
			if( (SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(profiloGestione)==false) &&  (this.busta.getSequenza() != -1) ){
				if( (this.busta.getCollaborazione() == null) ||
						(this.busta.getInoltro() == null)  ){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_VALIDA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					if( (this.busta.getCollaborazione() == null) && (this.busta.getInoltro() == null))
						ecc.setDescrizione("Sequenza non gestibile, elementi Collaborazione e ProfiloTrasmissione non presenti");
					else if( this.busta.getCollaborazione() == null ){
						ecc.setDescrizione("Sequenza non gestibile, elemento Collaborazione non presente");
					}else{
						ecc.setDescrizione("Sequenza non gestibile, elemento ProfiloTrasmissione non presente");
					}
					this.erroriValidazione.add(ecc);
				}else{ 
					if( (Inoltro.SENZA_DUPLICATI.equals(this.busta.getInoltro())==false)||
							(this.busta.isConfermaRicezione()==false) ){
						Eccezione ecc = new Eccezione();
						ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
						ecc.setCodiceEccezione(CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_GESTIBILE);
						ecc.setRilevanza(LivelloRilevanza.ERROR);
						if((Inoltro.SENZA_DUPLICATI.equals(this.busta.getInoltro())==false) && (this.busta.isConfermaRicezione()==false)){
							ecc.setDescrizione("Sequenza non gestibile, attributi dell'elemento ProfiloTrasmissione non validi con la sequenza");
						}else if(this.busta.isConfermaRicezione()==false){
							ecc.setDescrizione("Sequenza non gestibile, attributo confermaRicezione dell'elemento ProfiloTrasmissione non valido con la sequenza");
						}else{
							ecc.setDescrizione("Sequenza non gestibile, attributo inoltro dell'elemento ProfiloTrasmissione non valido con la sequenza");
						}
						this.erroriValidazione.add(ecc);
					}
				}
			}

			// Riscontri (ID)
			//log.info("Validazione Riscontri...");
			for(int i=0; i<this.busta.sizeListaRiscontri();i++){
				Riscontro r = this.busta.getRiscontro(i);
				if(validazioneID_engine(r.getID()) == false){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_IDENTIFICATIVO_MESSAGGIO_NON_VALIDO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE.toString());
					this.erroriValidazione.add(ecc);
				}
			}

			// Trasmissione (Origine e Destinazione)
			//log.info("Validazione Trasmissione...");
			for(int i=0; i<this.busta.sizeListaTrasmissioni();i++){
				Trasmissione t = this.busta.getTrasmissione(i);

				IDSoggetto soggOrig = new IDSoggetto(t.getTipoOrigine(),t.getOrigine());
				String dominioOrig = null;
				try{
					dominioOrig = this.registroServiziReader.getDominio(soggOrig,null,this.protocolFactory); // null=allRegistri
					if(dominioOrig==null)
						throw new Exception("Dominio non definito");
					else
						t.setIdentificativoPortaOrigine(dominioOrig);
				}catch(DriverRegistroServiziNotFound es){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_ORIGINE_NON_VALIDA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE.toString());
					this.log.debug("Identificazione origine trasmissione fallita:"+es.getMessage());
					this.erroriValidazione.add(ecc);
				}catch(Exception es){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione("Identificazione origine della trasmissione non riuscita: errore di processamento");
					this.log.error("Identificazione origine trasmissione non riuscita",es);
					this.erroriProcessamento.add(ecc);
				}

				IDSoggetto soggDestTr = new IDSoggetto(t.getTipoDestinazione(),t.getDestinazione());
				String dominioDestinatarioTr = null;
				try{
					dominioDestinatarioTr = this.registroServiziReader.getDominio(soggDestTr,null,this.protocolFactory); // null=allRegistri
					if(dominioDestinatarioTr==null)
						throw new Exception("Dominio non definito");
					else
						t.setIdentificativoPortaDestinazione(dominioDestinatarioTr);
				}catch(DriverRegistroServiziNotFound es){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_DESTINAZIONE_NON_VALIDA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE.toString());
					this.log.debug("Identificazione destinazione trasmissione fallita:"+es.getMessage());
					this.erroriValidazione.add(ecc);
				}catch(Exception es){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione("Identificazione destinazione della trasmissione non riuscita: errore di processamento");
					this.log.error("Identificazione destinazione trasmissione non riuscita",es);
					this.erroriProcessamento.add(ecc);
				}
			}

			
			// Validazione LineeGuida1.1
			if(SPCoopCostanti.PROFILO_CNIPA_LINEE_GUIDA_11_BUSTA_EGOV_11.equals(profiloGestione)){
				// log.info("Validazione LineeGuida1.1...");
				this.validazioneLineeGuida11();
			}

		} catch(Exception e) {
			this.log.error("ValidazioneSemantica: riscontrato errore durante la validazione",e);
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione("Errore di processamento durante la validazione semantica.");
			this.erroriProcessamento.add(ecc);
		}

	}


	

	private void validazioneCollaborazione(RuoloBusta tipoBusta,String dominioMittente,String dominioDestinatario) throws ProtocolException{
		
		// La collaborazione deve appartenere o al mittente, o al destinatario della busta.
		if(this.busta.getCollaborazione()!=null){
			if( (validazioneID_engine(this.busta.getCollaborazione(),this.busta.getMittente(),dominioMittente) == false) &&
					(validazioneID_engine(this.busta.getCollaborazione(),this.busta.getDestinatario(),dominioDestinatario) == false) ){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString());
				this.erroriValidazione.add(ecc);
				return;
			}
		}
	
	}
	
 
	private void impostaFruitoreErogatoreRealiServizio(IDSoggetto fruitore,IDSoggetto erogatore,RuoloBusta tipoBusta){
		if( (RuoloBusta.RICHIESTA.equals(tipoBusta.toString())) || 
					((RuoloBusta.RISPOSTA.equals(tipoBusta.toString())) && (ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.busta.getProfiloDiCollaborazione()))) ||
					((RuoloBusta.RISPOSTA.equals(tipoBusta.toString())) && (ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.busta.getProfiloDiCollaborazione()))) ){ 
			if(erogatore!=null){	
				erogatore.setTipo(this.busta.getTipoDestinatario());
				erogatore.setNome(this.busta.getDestinatario());
			}
			if(fruitore!=null){
				fruitore.setTipo(this.busta.getTipoMittente());
				fruitore.setNome(this.busta.getMittente());
			}
		}else if((RuoloBusta.RISPOSTA.equals(tipoBusta.toString())) || (RuoloBusta.RICEVUTA_RICHIESTA.equals(tipoBusta.toString())) || (RuoloBusta.RICEVUTA_RISPOSTA.equals(tipoBusta.toString()))){
			if(erogatore!=null){	
				erogatore.setTipo(this.busta.getTipoMittente());
				erogatore.setNome(this.busta.getMittente());
			}
			if(fruitore!=null){
				fruitore.setTipo(this.busta.getTipoDestinatario());
				fruitore.setNome(this.busta.getDestinatario());
			}
		}
		

	}
	
	/**
	 * Metodo che si occupa di validare l'elemento 'Servizio' e 'Azione', controllando 
	 * che siano riconosciuti all'interno del registro dei servizi di riferimento (es. registro UDDI)
	 *
	 * @param tipoBusta tipo di Busta da validare
	 * 
	 */
	private RisultatoValidazione riconoscimentoServizioAzione(RuoloBusta tipoBusta,boolean mittenteSconosciuto,boolean destinatarioSconosciuto) throws ProtocolException,DriverRegistroServiziException{

		if(mittenteSconosciuto)
			return null;
		if(destinatarioSconosciuto)
			return null;
		IDSoggetto idSoggettoErogatore = new IDSoggetto();
		IDSoggetto idSoggettoFruitore = null;
		// Per richiesta asincrono simmetrico serve anche il fruitore per la localizzazione del servizio correlato
		if( (ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.busta.getProfiloDiCollaborazione())) &&
					(RuoloBusta.RICHIESTA.equals(tipoBusta.toString())) ){
			idSoggettoFruitore = new IDSoggetto();
		}else if( (ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.busta.getProfiloDiCollaborazione())) ){
			idSoggettoFruitore = new IDSoggetto();
		}
		this.impostaFruitoreErogatoreRealiServizio(idSoggettoFruitore,idSoggettoErogatore,tipoBusta);
		

		// 1) Validazione Servizio
		RisultatoValidazione validazione = null;
		if(this.busta.getServizio()!=null && this.busta.getTipoServizio()!=null){
			IDServizio idService = 
				new IDServizio(idSoggettoErogatore,this.busta.getTipoServizio(),
						this.busta.getServizio(),this.busta.getAzione());
			try{
				validazione = this.registroServiziReader.validaServizio(idSoggettoFruitore,idService,null); //null=allRegistri	
			}catch(Exception e){
				this.log.error("Errore durante l'accesso al registro per la validazione del servizio e dell'azione",e);
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione("Riconoscimento Servizio/Azione: errore di processamento");
				this.erroriProcessamento.add(ecc);
				return null;
			}
			if(validazione.getServizioRegistrato()==false){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString());
				this.erroriValidazione.add(ecc);
				return validazione;
			}
			this.servizioCorrelato = validazione.getServizioCorrelato();
			this.tipoServizioCorrelato = validazione.getTipoServizioCorrelato();
			this.azioneCorrelata = validazione.getAzioneCorrelata();
		}



		// 2) Validazione Azione (solo se c'e' un servizio)
		if(this.busta.getServizio()!=null && this.busta.getTipoServizio()!=null){
			//log.info("Validazione Azione...");
			if(this.busta.getAzione() == null){
				// Controllo che sia valida una invocazione senza azione
				if(validazione.getAccessoSenzaAzione() == false){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.INVOCAZIONE_SENZA_AZIONE_NON_PERMESSA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString());
					this.erroriValidazione.add(ecc);
					return validazione;
				}
			}else{
				String[]azioni = validazione.getAzioni();
				boolean azioneTrovata = false;
				if(azioni!=null){
					for(int i=0;i<azioni.length;i++){
						if(this.busta.getAzione().equals(azioni[i])){
							azioneTrovata=true;
							break;
						}
					}
				}
				if(azioneTrovata==false){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.AZIONE_SCONOSCIUTA);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString());
					this.erroriValidazione.add(ecc);
					return validazione;
				}
			}
		}

		
		// 3) Validazione elementi ServizioCorrelato e TipoServizioCorrelato
		if(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.busta.getProfiloDiCollaborazione()) 
				&& RuoloBusta.RICHIESTA.equals(tipoBusta.toString())){
			/* Con la certificazione CNIPA, questi attributi non sono piu' obbligatori */
			/*if( (this.busta.getServizioCorrelato()==null) || (this.busta.getTipoServizioCorrelato()==null) ||
					(this.busta.getTipoServizioCorrelato().equals(validazione.getTipoServizioCorrelato())== false) ||
					(this.busta.getServizioCorrelato().equals(validazione.getServizioCorrelato())== false) ){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(Costanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				ecc.setCodiceEccezione(Costanti.ECCEZIONE_SERVIZIO_SCONOSCIUTO);
				ecc.setRilevanza(Costanti.GRAVE);
				ecc.setDescrizione("Servizio Correlato non valido (o non presente) nella richiesta asincrona simmetrica");
				this.erroriValidazione.add(ecc);
				return;
			}*/
			// NEW ALGORITM:
			if( 
					(this.busta.getServizioCorrelato()!=null) && 
					(this.busta.getServizioCorrelato().equals(validazione.getServizioCorrelato())== false)
			){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.SERVIZIO_CORRELATO_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_SERVIZIO_CORRELATO+" diverso da quello atteso (richiesta asincrona simmetrica)");
				this.erroriValidazione.add(ecc);
				return validazione;
			}
			if( 
					(this.busta.getTipoServizioCorrelato()!=null) && 
					(this.busta.getTipoServizioCorrelato().equals(validazione.getTipoServizioCorrelato())== false)
			){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_SERVIZIO_CORRELATO_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO+" diverso da quello atteso (richiesta asincrona simmetrica)");
				this.erroriValidazione.add(ecc);
				return validazione;
			}

		}
		if(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.busta.getProfiloDiCollaborazione()) 
				&& RuoloBusta.RICEVUTA_RICHIESTA.equals(tipoBusta.toString())  ){
			/* Con la certificazione CNIPA, questi attributi non sono piu' obbligatori */
			/*
			if( (this.busta.getServizioCorrelato()==null) || (this.busta.getTipoServizioCorrelato()==null) ||
					(this.busta.getTipoServizioCorrelato().equals(validazione.getTipoServizioCorrelato())== false) ||
					(this.busta.getServizioCorrelato().equals(validazione.getServizioCorrelato())== false) ){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(Costanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				ecc.setCodiceEccezione(Costanti.ECCEZIONE_SERVIZIO_SCONOSCIUTO);
				ecc.setRilevanza(Costanti.GRAVE);
				ecc.setDescrizione("Servizio Correlato non valido (o non presente) nella ricevuta della richiesta asincrona asimmetrica");
				this.erroriValidazione.add(ecc);
				return;
			}*/
			// NEW ALGORITM:
			if( 
					(this.busta.getServizioCorrelato()!=null) && 
					(this.busta.getServizioCorrelato().equals(validazione.getServizioCorrelato())== false)
			){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.SERVIZIO_CORRELATO_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_SERVIZIO_CORRELATO+" diverso da quello atteso (ricevuta della richiesta asincrona simmetrica)");
				this.erroriValidazione.add(ecc);
				return validazione;
			}
			if( 
					(this.busta.getTipoServizioCorrelato()!=null) && 
					(this.busta.getTipoServizioCorrelato().equals(validazione.getTipoServizioCorrelato())== false)
			){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_SERVIZIO_CORRELATO_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO+" diverso da quello atteso (ricevuta della richiesta asincrona simmetrica)");
				this.erroriValidazione.add(ecc);
				return validazione;
			}
		}

		
		return validazione;
	}
	
	


	/**
	 * Metodo che si occupa di validare l'elemento 'Servizio' e 'Azione', controllando 
	 * che siano riconosciuti all'interno del registro dei servizi di riferimento (es. registro UDDI)
	 *
	 * @param tipoBusta tipo di Busta da validare
	 * 
	 */
	private void riconoscimentoProfiloCollaborazione(RuoloBusta tipoBusta){

		// Viene effettuato solo per la richiesta.
		try{
			if(this.infoServizio.getProfiloDiCollaborazione().equals(this.busta.getProfiloDiCollaborazione())==false){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_NON_VALIDO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE+" diverso da quanto specificato nel registro");
					this.erroriValidazione.add(ecc);
			}
		}catch(Exception e){
			this.log.error("Errore durante l'accesso al registro per la validazione del profilo di collaborazione",e);
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione("Riconoscimento Profilo di Collaborazione: errore di processamento");
			this.erroriProcessamento.add(ecc);
		}

	}

	
	/**
	 * Metodo che si occupa di validare l'identificativo <var>id</var> secondo la specifica eGov.
	 *
	 * @param id identificativo eGov da validare
	 * @return il valore boolean true se l'identificativo fornito e' conforme alla specifica eGov. Altrimenti false.
	 * 
	 */
	public boolean validazioneID_engine(String id){
		return validazioneID_engine(id,null,null);
	}

	/**
	 * Metodo che si occupa di validare l'identificativo <var>id</var> secondo la specifica eGov.
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
	 *
	 * @param id identificativo eGov da validare
	 * @param codAmm Identificativo Parte mittente o destinatario di una busta.
	 * @param codDominio Identificativo del dominio del codice di amministrazione
	 * @return il valore boolean true se l'identificativo fornito e' conforme alla specifica eGov. Altrimenti false.
	 * 
	 */
	public boolean validazioneID_engine(String id, String codAmm, String codDominio){

		try{

			// Ottengo parti dell'identificatore
			String [] split = id.split("_");
			if(split == null)
				return false;
			if(split.length != 5)
				return false;

			if(codAmm!=null && this.validazioneIDEGovCompleta){
				// Check codice amministrazione
				if(!split[0].equals(codAmm))
					return false;
				byte [] checkID = split[0].getBytes();
				for(int j=0;j<checkID.length;j++){
					if( !Character.isLetterOrDigit((char)checkID[j]))
						return false;
				} 
			}

			if(codDominio!=null && this.validazioneIDEGovCompleta){
				// Check codice porta di dominio	    
				if(!split[1].equalsIgnoreCase(codDominio))
					return false;
				byte [] checkPDD = split[1].getBytes();
				for(int j=0;j<checkPDD.length;j++){
					if( !Character.isLetterOrDigit((char)checkPDD[j]))
						return false;
				} 

			} 

			// Check num progressivo
			if(split[2].length() != 7)
				return false;
			try{
				Integer test = new Integer(split[2]);
				test.intValue();
			} catch(Exception e){
				return false;
			} 

			// Check data aaaa-mm-gg
			String [] date = split[3].split("-");
			if(date == null)
				return false;
			if(date.length != 3)
				return false;
			if(date[0].length() != 4)
				return false;
			try{
				Integer test = new Integer(date[0]);
				if(test.intValue()>2100)
					return false;
			} catch(Exception e){
				return false;
			} 
			if(date[1].length() != 2)
				return false;
			if(date[2].length() != 2)
				return false;
			try{
				Integer mese = new Integer(date[1]);
				if(mese.intValue()>12 || mese.intValue() < 0)
					return false;
				Integer giorno = new Integer(date[2]);
				if(giorno.intValue() < 0)
					return false;
				if(giorno.intValue()>29 && mese.intValue() == 2)
					return false;
				else if(giorno.intValue()>30 && 
						(  (mese.intValue() == 4) ||
								(mese.intValue() == 6) ||
								(mese.intValue() == 9) ||
								(mese.intValue() == 11) ) )
					return false;
				else if(giorno.intValue()>31)
					return false;
			} catch(Exception e){
				return false;
			} 


			// Check ora hh:mm
			String [] ora = split[4].split(":");
			if(ora == null)
				return false;
			if(ora.length != 2)
				return false;
			if(ora[0].length() != 2)
				return false;
			try{
				Integer test = new Integer(ora[0]);
				if(test.intValue() > 23 || test.intValue() < 0)
					return false;
			} catch(Exception e){
				return false;
			} 
			if(ora[1].length() != 2)
				return false;
			try{
				Integer test = new Integer(ora[1]);
				if(test.intValue() > 59 || test.intValue() < 0)
					return false;
			} catch(Exception e){
				return false;
			} 

			return true;

		}catch(Exception error){
			return false;
		}
	}

	private void validazioneLineeGuida11() throws ProtocolException{
		
		//	  mittente
		if(SPCoopCostanti.SERVIZIO_SPC.equals(this.busta.getTipoMittente())==false){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_MITTENTE_NON_VALIDO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString());
			this.erroriValidazione.add(ecc);
		}
		
		//	  destinatario
		if(SPCoopCostanti.SERVIZIO_SPC.equals(this.busta.getTipoDestinatario())==false){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_DESTINATARIO_NON_VALIDO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString());
			this.erroriValidazione.add(ecc);
		}
		
		// Servizio e' obbligatorio in questo profilo (queste sono eccezioni GRAVI!)
		if(this.busta.getTipoServizio()==null){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_SERVIZIO_NON_PRESENTE);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO.toString());
			this.erroriValidazione.add(ecc);
		}
		if(this.busta.getServizio()==null){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.SERVIZIO_NON_PRESENTE);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
		}
		
		//		  servizio
		if(SPCoopCostanti.SERVIZIO_SPC.equals(this.busta.getTipoServizio())==false){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_SERVIZIO_NON_VALIDO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO.toString());
			this.erroriValidazione.add(ecc);
		}
		
		// Azione e' obbligatoria (queste sono eccezioni GRAVI!)
		if(this.busta.getAzione()==null){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.AZIONE_NON_PRESENTE);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
		}
		
		// Profilo di Collaborazione
		if(this.busta.getProfiloDiCollaborazione()==null){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_NON_PRESENTE);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString());
			this.erroriValidazione.add(ecc);
		}

		// Collaborazione (valida solo per profili asincroni)
		if(  ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.busta.getProfiloDiCollaborazione()) 
				||
			 ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.busta.getProfiloDiCollaborazione()) ){
			if(this.busta.getCollaborazione()==null){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.COLLABORAZIONE_NON_VALIDA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString());
				this.erroriValidazione.add(ecc);
			}
		} 
		
		// OraRegistrazione
		if(TipoOraRegistrazione.SINCRONIZZATO.equals(this.busta.getTipoOraRegistrazione())==false){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_ORA_REGISTRAZIONE_NON_VALIDA);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE_TEMPO.toString());
			this.erroriValidazione.add(ecc);
		}
		
		// Profilo di Trasmissione (queste sono eccezioni GRAVI!)
		if(this.busta.isConfermaRicezione()==true){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE_CONFERMA_RICEZIONE_NON_VALIDO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_CONFERMA_RICEZIONE.toString());
			this.erroriValidazione.add(ecc);
		}
		if(Inoltro.CON_DUPLICATI.equals(this.busta.getInoltro())){
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.PROFILO_TRASMISSIONE_FILTRO_DUPLICATI_NON_VALIDO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_INOLTRO.toString());
			this.erroriValidazione.add(ecc);
		}
				
		for(int i=0; i<this.busta.sizeListaTrasmissioni(); i++){
			Trasmissione tr = this.busta.getTrasmissione(i);
			if(SPCoopCostanti.SERVIZIO_SPC.equals(tr.getTipoOrigine())==false){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_TIPO_ORIGINE_NON_VALIDA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_TIPO.toString());
				this.erroriValidazione.add(ecc);
			}
			if(SPCoopCostanti.SERVIZIO_SPC.equals(tr.getTipoDestinazione())==false){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_TIPO_DESTINAZIONE_NON_VALIDA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_TIPO.toString());
				this.erroriValidazione.add(ecc);
			}
			if(TipoOraRegistrazione.SINCRONIZZATO.equals(tr.getTempo())==false){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_TIPO_ORA_REGISTRAZIONE_NON_VALIDA);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE_TEMPO.toString());
				this.erroriValidazione.add(ecc);
			}
		}
		
		// Lista riscontri
		for(int i=0; i<this.busta.sizeListaRiscontri(); i++){
			Riscontro r = this.busta.getRiscontro(i);
			if(TipoOraRegistrazione.SINCRONIZZATO.equals(r.getTipoOraRegistrazione())==false){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.RISCONTRO_TIPO_ORA_REGISTRAZIONE_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE_TEMPO.toString());
				this.erroriValidazione.add(ecc);
			}
		}
		
		
		
		
		// Segnalo errori di tipo INFO SOLO SE non ci sono stati gia errori di tipo GRAVE
		if(this.erroriValidazione.size()==0){
			
			// Indirizzo telematico mittente
			if(this.busta.getIndirizzoMittente()!=null){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.INDIRIZZO_MITTENTE_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.INFO);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
				this.erroriValidazione.add(ecc);
			}
			
			// Indirizzo telematico destinatario
			if(this.busta.getIndirizzoDestinatario()!=null){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.INDIRIZZO_DESTINATARIO_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.INFO);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
				this.erroriValidazione.add(ecc);
			}
			
			// Profilo di Collaborazione
			if(this.busta.getTipoServizioCorrelato()!=null){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.TIPO_SERVIZIO_CORRELATO_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.INFO);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO.toString());
				this.erroriValidazione.add(ecc);
			}
			if(this.busta.getServizioCorrelato()!=null){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.SERVIZIO_CORRELATO_NON_VALIDO);
				ecc.setRilevanza(LivelloRilevanza.INFO);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_SERVIZIO_CORRELATO.toString());
				this.erroriValidazione.add(ecc);
			}
			
			// Collaborazione valida solo per gli asincroni
			if(  (ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(this.busta.getProfiloDiCollaborazione())==false)  
					&&
				 (ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(this.busta.getProfiloDiCollaborazione())==false) ){
				if(this.busta.getCollaborazione()!=null){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.COLLABORAZIONE_NON_VALIDA);
					ecc.setRilevanza(LivelloRilevanza.INFO);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString());
					this.erroriValidazione.add(ecc);
				}
			}
			
			// Sequenza
			if(this.busta.getSequenza()!=-1){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_SUPPORTATA);
				ecc.setRilevanza(LivelloRilevanza.INFO);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_TRASPARENZA_TEMPORALE_NON_SUPPORTATA_POSIZIONE.toString());
				this.erroriValidazione.add(ecc);
			}
			
			// Lista trasmissioni
			/* L'hanno riammessa.
			if(this.busta.sizeListaTrasmissioni()>0){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(Costanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				ecc.setCodiceEccezione(Costanti.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO);
				ecc.setRilevanza(LivelloRilevanza.INFO);
				ecc.setDescrizione(Costanti.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE);
				this.erroriValidazione.add(ecc);
			}*/
			for(int i=0; i<this.busta.sizeListaTrasmissioni(); i++){
				Trasmissione tr = this.busta.getTrasmissione(i);
				if(tr.getIndirizzoOrigine()!=null){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_INDIRIZZO_ORIGINE_NON_VALIDA);
					ecc.setRilevanza(LivelloRilevanza.INFO);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
					this.erroriValidazione.add(ecc);
				}
				if(tr.getIndirizzoDestinazione()!=null){
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.TRASMISSIONE_INDIRIZZO_DESTINAZIONE_NON_VALIDA);
					ecc.setRilevanza(LivelloRilevanza.INFO);
					ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString());
					this.erroriValidazione.add(ecc);
				}
			}
			
			// Lista riscontri
			if(this.busta.sizeListaRiscontri()>0){
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA);
				ecc.setRilevanza(LivelloRilevanza.INFO);
				ecc.setDescrizione(SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE.toString());
				this.erroriValidazione.add(ecc);
			}
		}
	}
	

	public String getAzioneCorrelata() {
		return this.azioneCorrelata;
	}
	public void setAzioneCorrelata(String azioneCorrelata) {
		this.azioneCorrelata = azioneCorrelata;
	}


	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}


	@Override
	public boolean validazioneID(String id, IDSoggetto dominio, ProprietaValidazione proprietaValidazione) {
		this.validazioneIDEGovCompleta = proprietaValidazione.isValidazioneIDCompleta();
		if(dominio!=null){
			return this.validazioneID_engine(id, dominio.getNome(), dominio.getCodicePorta());
		}else{
			return this.validazioneID_engine(id, null, null);
		}
	}


	@Override
	public ValidazioneSemanticaResult valida(OpenSPCoop2Message msg, Busta busta, IState state,
			ProprietaValidazione proprietaValidazione, RuoloBusta tipoBusta)
			throws ProtocolException {
		this.busta = busta;
		this.state = state;
		this.registroServiziReader = RegistroServiziManager.getInstance(state);
		this.validazioneIDEGovCompleta = proprietaValidazione.isValidazioneIDCompleta();
		this.valida(proprietaValidazione, tipoBusta, proprietaValidazione.getVersioneProtocollo());
		ValidazioneSemanticaResult result = new ValidazioneSemanticaResult(this.erroriValidazione, this.erroriProcessamento, this.servizioCorrelato, this.tipoServizioCorrelato, this.infoServizio);
		return result;
	}

	
	@Override
	public SecurityInfo readSecurityInformation(IDigestReader digestReader, OpenSPCoop2Message msg,SOAPEnvelope soapEnvelope,SOAPElement protocolHeader) throws ProtocolException{
		return null;
	}
	@Override
	public SecurityInfo readSecurityInformation(IDigestReader digestReader, OpenSPCoop2Message msg) throws ProtocolException{
		return null;
	}
}
