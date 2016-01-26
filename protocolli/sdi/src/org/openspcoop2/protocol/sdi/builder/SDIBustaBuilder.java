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

package org.openspcoop2.protocol.sdi.builder;

import it.gov.fatturapa.sdi.messaggi.v1_0.constants.TipiMessaggi;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.basic.builder.BustaBuilder;
import org.openspcoop2.protocol.sdi.config.SDIProperties;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviFile;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviNotifica;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRicezioneFatture;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioTrasmissioneFatture;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * Classe che implementa, in base al protocollo SdI, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IBustaBuilder} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIBustaBuilder extends BustaBuilder {

	/** Properties */
	protected SDIProperties sdiProperties;
	/** SDIImbustamento */
	private SDIImbustamento sdiImbustamento;
	/** SDISbustamento */
	private SDISbustamento sdiSbustamento;
	
	public SDIBustaBuilder(IProtocolFactory factory) throws ProtocolException {
		super(factory);
		this.sdiProperties = SDIProperties.getInstance(this.log);
		this.sdiImbustamento = new SDIImbustamento(this);
		this.sdiSbustamento = new SDISbustamento(this);
	}

	@Override
	public SOAPElement imbustamento(IState state, OpenSPCoop2Message msg, Busta busta,
			boolean isRichiesta,
			ProprietaManifestAttachments proprietaManifestAttachments)
			throws ProtocolException {
		
		super.imbustamento(state, msg, busta, isRichiesta, proprietaManifestAttachments);
		
		// Modifico il messaggio per aggiungere la struttura SDI in base all'azione invocata e al ruolo (Richiesta/Risposta) e al fatto che non vi sono errori.
		// TODO: il body attuale lo devo inserire come valore codificato in base 64 tramite la struttura Corretta
		// Forse è utile in questo contesto generare gli elementi con jibx o jaxb.
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
		
		if(isRichiesta){
	
			// Servizio
			if(SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE.equals(busta.getServizio())
					&& SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE.equals(busta.getAzione())){
				element = this.sdiImbustamento.creaRichiesta_ServizioSdIRiceviFile_AzioneRiceviFile(this.protocolFactory, state, busta, msg);
			}
			else if(SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA.equals(busta.getServizio())
					&& SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_NOTIFICA_ESITO_AZIONE_NOTIFICA_ESITO.equals(busta.getAzione())){
				element = this.sdiImbustamento.creaRichiesta_ServizioSdIRiceviNotifica_AzioneNotificaEsito(this.protocolFactory,state,busta,msg,
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
				boolean ignoraEccezioniNonGravi = this.protocolFactory.createProtocolManager().isIgnoraEccezioniNonGravi();
				if(ignoraEccezioniNonGravi){
					if(busta.containsEccezioniGravi() ){
						this.addEccezioniInFault(msg, busta, ignoraEccezioniNonGravi);
					}	
				}
				else{
					this.addEccezioniInFault(msg, busta, ignoraEccezioniNonGravi);
				}
			}
			
			try{
				if(msg.getSOAPBody()!=null && msg.getSOAPBody().hasFault()){
					return null;
				}
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
			
			// Servizio
			if(SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE.equals(busta.getServizio())
					&& SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_AZIONE_RICEVI_FATTURE.equals(busta.getAzione())){
				element = this.sdiImbustamento.creaRisposta_ServizioRicezioneFatture_AzioneRiceviFatture(this.protocolFactory,state,busta,msg);
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

		return element;
		
	}
	
	@Override
	public SOAPElement sbustamento(IState state, OpenSPCoop2Message msg, Busta busta,
			boolean isRichiesta, ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{
		
		try{
		
			SOAPElement se = null;
						
			if(isRichiesta){
				
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
			
			return se; 
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
	}
	
}
