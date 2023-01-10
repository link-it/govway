/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdi.builder;

import it.gov.fatturapa.sdi.messaggi.v1_0.constants.TipiMessaggi;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.builder.BustaBuilder;
import org.openspcoop2.protocol.sdi.SDIBustaRawContent;
import org.openspcoop2.protocol.sdi.config.SDIProperties;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviFile;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviNotifica;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRicezioneFatture;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioTrasmissioneFatture;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * Classe che implementa, in base al protocollo SdI, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IBustaBuilder} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIBustaBuilder extends BustaBuilder<SOAPElement> {

	/** Properties */
	protected SDIProperties sdiProperties;
	/** SDIImbustamento */
	private SDIImbustamento sdiImbustamento;
	/** SDISbustamento */
	private SDISbustamento sdiSbustamento;
	
	public SDIBustaBuilder(IProtocolFactory<SOAPElement> factory, IState state) throws ProtocolException {
		super(factory, state);
		this.sdiProperties = SDIProperties.getInstance(this.log);
		this.sdiImbustamento = new SDIImbustamento(this);
		this.sdiSbustamento = new SDISbustamento(this);
	}

	@Override
	public ProtocolMessage imbustamento(OpenSPCoop2Message msg, Context context,
			Busta busta, Busta bustaRichiesta,
			RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments,
			FaseImbustamento faseImbustamento)
			throws ProtocolException {
		
		if(FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO.equals(faseImbustamento)) {
			ProtocolMessage protocolMessage = new ProtocolMessage();
			protocolMessage.setPhaseUnsupported(true);
			return protocolMessage;
		}
		
		ProtocolMessage protocolMessage = super.imbustamento(msg, context,
				busta, bustaRichiesta,
				ruoloMessaggio, proprietaManifestAttachments, faseImbustamento);
		
		// Modifico il messaggio per aggiungere la struttura SDI in base all'azione invocata e al ruolo (Richiesta/Risposta) e al fatto che non vi sono errori.
		// TODO: il body attuale lo devo inserire come valore codificato in base 64 tramite la struttura Corretta
		// Forse è utile in questo contesto generare gli elementi con jaxb.
		// Dopo configuro il packaging di MTOM
		
		// Check Destinatario
		if(busta.getTipoDestinatario().equals(this.sdiProperties.getTipoSoggettoSDI())==false){
			throw new ProtocolException("TipoDestinatario["+busta.getTipoDestinatario()+"] differente da quello atteso per il Sistema di Interscambio ["+this.sdiProperties.getTipoSoggettoSDI()+"]");
		}
		if(busta.getDestinatario().equals(this.sdiProperties.getNomeSoggettoSDI())==false){
			boolean whiteList = false;
			if(busta.getDestinatario()!=null && this.sdiProperties.getSoggettiWhiteList().contains(busta.getDestinatario())){
				this.log.debug("Destinatario ["+busta.getDestinatario()+"] in white list");
				whiteList = true;
			}
			if(!whiteList){
				throw new ProtocolException("NomeDestinatario["+busta.getDestinatario()+"] differente da quello atteso per il Sistema di Interscambio ["+this.sdiProperties.getNomeSoggettoSDI()+"]");
			}
		}
		
		SOAPElement element = null;
		
		if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
	
			// Servizio
			if(SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE.equals(busta.getServizio())
					&& SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE.equals(busta.getAzione())){
				element = this.sdiImbustamento.creaRichiesta_ServizioSdIRiceviFile_AzioneRiceviFile(this.protocolFactory, this.state, busta, msg);
			}
			else if(SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA.equals(busta.getServizio())
					&& SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO.equals(busta.getAzione())){
				element = this.sdiImbustamento.creaRichiesta_ServizioSdIRiceviNotifica_AzioneNotificaEsito(this.protocolFactory,this.state,busta,msg,
						this.sdiProperties.isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov(),
						this.sdiProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov());
			}
			else{
				boolean whiteList = false;
				if(busta.getServizio()!=null && this.sdiProperties.getServiziWhiteList().contains(busta.getServizio())){
					if(busta.getAzione()!=null && this.sdiProperties.getAzioniWhiteList().contains(busta.getAzione())){
						this.log.debug("Servizio ["+busta.getServizio()+"] e Azione ["+busta.getAzione()+"] in white list");
						whiteList = true;
					}
				}
				if(!whiteList){
					throw new ProtocolException("Servizio["+busta.getServizio()+"] e Azione["+busta.getAzione()+"] non gestite dal protocollo durante la fase di richiesta");
				}
			}
			
		}
		else{
			if(busta.sizeListaEccezioni()>0){
				
				// le eccezioni vengono tornate anche per gli errori di processamento poiche' in TrasparenteProtocolVersionManager
				// e' stato cablato il metodo isGenerazioneListaEccezioniErroreProcessamento al valore 'true'
				
				// Per quanto riguarda la generazione dei codici SOAPFault personalizzati e/o la generazione dell'elemento errore-applicativo
				// la scelta e' delegata a due proprieta' nel file di proprieta'.
				//
				// Infine la scelta della presenza o meno dell'elemento OpenSPCoop2Details lo stesso viene pilotata dalle proprieta' presenti nel file di proprieta'
				
				boolean ignoraEccezioniNonGravi = this.protocolFactory.createProtocolManager().isIgnoraEccezioniNonGravi();
				if(ignoraEccezioniNonGravi){
					if(busta.containsEccezioniGravi() ){
						this.enrichFault(msg, busta, ignoraEccezioniNonGravi,
								this.sdiProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault(),
								this.sdiProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo());
					}	
				}
				else{
					this.enrichFault(msg, busta, ignoraEccezioniNonGravi,
							this.sdiProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault(),
							this.sdiProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo());
				}
			}
			
			try{
				OpenSPCoop2SoapMessage soapMsg = msg.castAsSoap();
				if(soapMsg.getSOAPBody()!=null && soapMsg.getSOAPBody().hasFault()){
					return null;
				}
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
			
			// Servizio
			if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE.equals(busta.getServizio())
					&& SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_RICEVI_FATTURE.equals(busta.getAzione())){
				element = this.sdiImbustamento.creaRisposta_ServizioRicezioneFatture_AzioneRiceviFatture(this.protocolFactory,this.state,busta,msg);
			}
			else{
				boolean whiteList = false;
				if(busta.getServizio()!=null && this.sdiProperties.getServiziWhiteList().contains(busta.getServizio())){
					if(busta.getAzione()!=null && this.sdiProperties.getAzioniWhiteList().contains(busta.getAzione())){
						this.log.debug("Servizio ["+busta.getServizio()+"] e Azione ["+busta.getAzione()+"] in white list");
						whiteList = true;
					}
				}
				if(!whiteList){
					throw new ProtocolException("Servizio["+busta.getServizio()+"] e Azione["+busta.getAzione()+"] non gestite dal protocollo durante la fase di risposta");
				}
			}
			
		}

		if(element!=null) {
			protocolMessage.setBustaRawContent(new SDIBustaRawContent(element));
		}
		return protocolMessage;
		
	}
	
	@Override
	public ProtocolMessage sbustamento(OpenSPCoop2Message msg, Context context,
			Busta busta,
			RuoloMessaggio ruoloMessaggio, ProprietaManifestAttachments proprietaManifestAttachments,
			FaseSbustamento faseSbustamento, ServiceBinding integrationServiceBinding, ServiceBindingConfiguration serviceBindingConfiguration) throws ProtocolException{
		
		try{
		
			ProtocolMessage protocolMessage = new ProtocolMessage();
			protocolMessage.setMessage(msg);
			
			if(FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RICHIESTA.equals(faseSbustamento) == false &&
					FaseSbustamento.POST_VALIDAZIONE_SEMANTICA_RISPOSTA.equals(faseSbustamento) == false){
				
				// Lo sbustamento effettivo in sdi viene ritardato fino alla consegna del servizio applicativo
				// il servizio applicativo può richiederlo di non effettuarlo
				SOAPElement se = null;
						
				if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
					
					// Servizio
					if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE.equals(busta.getServizio())){
						if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_RICEVUTA_CONSEGNA.equals(busta.getAzione())){
							se = this.sdiSbustamento.sbustamentoRichiesta_ServizioTrasmissioneFatture_Notifiche(TipiMessaggi.RC, busta, msg);
						}
						else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_MANCATA_CONSEGNA.equals(busta.getAzione())){
							se = this.sdiSbustamento.sbustamentoRichiesta_ServizioTrasmissioneFatture_Notifiche(TipiMessaggi.MC, busta, msg);
						}
						else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_SCARTO.equals(busta.getAzione())){
							se = this.sdiSbustamento.sbustamentoRichiesta_ServizioTrasmissioneFatture_Notifiche(TipiMessaggi.NS, busta, msg);
						}
						else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_ESITO.equals(busta.getAzione())){
							se = this.sdiSbustamento.sbustamentoRichiesta_ServizioTrasmissioneFatture_Notifiche(TipiMessaggi.NE, busta, msg);
						}
						else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_NOTIFICA_DECORRENZA_TERMINI.equals(busta.getAzione())){
							se = this.sdiSbustamento.sbustamentoRichiesta_ServizioTrasmissioneFatture_Notifiche(TipiMessaggi.DT, busta, msg);
						}
						else if(SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_AZIONE_ATTESTAZIONE_TRASMISSIONE_FATTURA.equals(busta.getAzione())){
							se = this.sdiSbustamento.sbustamentoRichiesta_ServizioTrasmissioneFatture_Notifiche(TipiMessaggi.AT, busta, msg);
						}
						else{
							throw new ProtocolException("Servizio["+busta.getServizio()+"] con Azione["+busta.getAzione()+"] non gestita dal protocollo durante la fase di richiesta");
						}
					}
					else if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE.equals(busta.getServizio())){
						if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_RICEVI_FATTURE.equals(busta.getAzione())){
							se = this.sdiSbustamento.sbustamentoRichiesta_ServizioRicezioneFatture_AzioneRiceviFatture(busta, msg);
						}
						else if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_NOTIFICA_DECORRENZA_TERMINI.equals(busta.getAzione())){
							se = this.sdiSbustamento.sbustamentoRichiesta_ServizioTrasmissioneFatture_Notifiche(TipiMessaggi.DT, busta, msg); // e' uguale a ricezione
						}
						else{
							throw new ProtocolException("Servizio["+busta.getServizio()+"] con Azione["+busta.getAzione()+"] non gestita dal protocollo durante la fase di richiesta");
						}
					}
					else{
						boolean whiteList = false;
						if(busta.getServizio()!=null && this.sdiProperties.getServiziWhiteList().contains(busta.getServizio())){
							if(busta.getAzione()!=null && this.sdiProperties.getAzioniWhiteList().contains(busta.getAzione())){
								this.log.debug("Servizio ["+busta.getServizio()+"] e Azione ["+busta.getAzione()+"] in white list");
								whiteList = true;
							}
						}
						if(!whiteList){
							throw new ProtocolException("Servizio["+busta.getServizio()+"] non gestite dal protocollo durante la fase di richiesta");
						}
					}
					
				}
				else{
		
					// Servizio
					if(SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE.equals(busta.getServizio())
							&& SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE.equals(busta.getAzione())){
						se = this.sdiSbustamento.sbustamentoRisposta_ServizioSdIRiceviFile_AzioneRiceviFile(busta, msg);
					}
					else if(SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA.equals(busta.getServizio())
							&& SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO.equals(busta.getAzione())){
						se = this.sdiSbustamento.sbustamentoRisposta_ServizioSdIRiceviNotifica_AzioneNotificaEsito(busta, msg);
					}
					else{
						boolean whiteList = false;
						if(busta.getServizio()!=null && this.sdiProperties.getServiziWhiteList().contains(busta.getServizio())){
							if(busta.getAzione()!=null && this.sdiProperties.getAzioniWhiteList().contains(busta.getAzione())){
								this.log.debug("Servizio ["+busta.getServizio()+"] e Azione ["+busta.getAzione()+"] in white list");
								whiteList = true;
							}
						}
						if(!whiteList){
							throw new ProtocolException("Servizio["+busta.getServizio()+"] e Azione["+busta.getAzione()+"] non gestite dal protocollo durante la fase di risposta");
						}
					}
					
				}
				
				if(se!=null) {
					protocolMessage.setBustaRawContent(new SDIBustaRawContent(se));
				}
				protocolMessage.setMessage(msg);
			}
			
			return protocolMessage;
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
	}
	
}
