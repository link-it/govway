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



package org.openspcoop2.protocol.engine.registry;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.slf4j.Logger;


/**
 * Classe utilizzabile per identificare il servizio applicativo e la porta delegata richiesta.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */

public abstract class AbstractIdentificazionePorta {


	/* ---- Location della Porta --- */
	protected String location;
	protected String urlCompleta;
	/* --- NomePorta ----*/
	protected String nomePortaIndivituata;
	/* ----- Individuazione Nome Porta UrlBased -------- */
	protected boolean nomePortaURLBased = true;
    	
	/* ---- messaggio di errore --- */
    protected ErroreIntegrazione erroreIntegrazione;

	/* ---- Soggetto identificato --- */
    protected IDSoggetto soggetto;

	/* ---- Tipo di Autenticazione --- */
    protected String tipoAutenticazione;

	/* --- Tipo di Autorizzazione --- */
    protected String tipoAutorizzazione;
	
	/* --- Tipo di Autorizzazione per Contenuto --- */
    protected String tipoAutorizzazioneContenuto;
	
    protected IProtocolFactory protocolFactory = null;

	protected IRegistryReader registryReader;

	/* ---- Log ----- */
	protected Logger log;
	
	
	/**
	 * Costruttore
	 *
	 * @param urlProtocolContext Parametri identificativi della porta delegata.
	 * @throws ProtocolException 
	 * 
	 */
	public AbstractIdentificazionePorta(TransportRequestContext urlProtocolContext, Logger log,
			boolean portaUrlBased, IRegistryReader registryReader,
			IProtocolFactory protocolFactory) throws ProtocolException {
		this.location = urlProtocolContext.getFunctionParameters();
		this.urlCompleta = urlProtocolContext.getUrlInvocazione_formBased();
		this.nomePortaURLBased = portaUrlBased;
		this.protocolFactory = protocolFactory;
		this.log = log;
		this.registryReader = registryReader;
	}



	protected abstract IDSoggetto getIDSoggettoProprietario(String porta) throws RegistryNotFound;
	

	/**
	 * Avvia il processo di identificazione.
	 *
	 * @return true in caso di identificazione con successo, false altrimenti.
	 * 
	 */
	public boolean process() {

		try{

			if(this.location==null || "".equals(this.location)){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
							getErrore401_PortaInesistente("nella url di invocazione alla Porta di Dominio non e' stata fornita il nome di una Porta");
				return false;
			}
			
			// Controllo Esistenza Porta appartenente ad un soggetto.
			// Viene controllata la url 'scalandola' di '/'
			if(this.nomePortaURLBased){
				String porta = new String(this.location);
				if(porta.endsWith("/"))
					porta = porta.substring(0,porta.length()-1);
				while(porta.contains("/")){
					//this.log.info("Cerco con nome porta delegata ["+porta+"]");
					try{
						this.soggetto = getIDSoggettoProprietario(porta);
					}catch(RegistryNotFound dNotFound){}
					if(this.soggetto!=null){
						//this.log.info("TROVATA porta delegata ["+porta+"]");
						this.nomePortaIndivituata = porta;
						break;
					}else{
						int indexCut = -1;
						for(int i=(porta.length()-1); i>=0 ;i--){
							if(porta.charAt(i) == '/'){
								indexCut = i;
								break;
							}
						}
						//this.log.info("indexCut = "+indexCut);
						//this.log.info("elimino parte '/'");
						porta = porta.substring(0,indexCut);
						//this.log.info("dopo eliminazione: "+porta);
					}
				}
				// Provo ad effettuare ultima ricerca
				//this.log.info("ULTIMA... Cerco con nome porta delegata ["+porta+"]");
				try{
					this.soggetto = getIDSoggettoProprietario(porta);
				}catch(RegistryNotFound dNotFound){}
				if(this.soggetto!=null){
					//this.log.info("ULTIMA....TROVATA porta delegata ["+porta+"]");
					this.nomePortaIndivituata = porta;
				}
			}
			
			if(this.nomePortaIndivituata==null){
				//log.info("Cerco con nome giusto porta delegata ["+this.location+"]");
				// check per nome PD univoco
				try{
					this.soggetto = getIDSoggettoProprietario(this.location);
				}catch(RegistryNotFound dNotFound){
					this.erroreIntegrazione = 
							ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
								getErrore401_PortaInesistente("verificare i parametri di accesso utilizzati",this.location,this.urlCompleta);
					this.log.error(this.erroreIntegrazione.getDescrizione(this.protocolFactory)+": "+dNotFound.getMessage());
					return false;
				}	
				if(this.soggetto!=null){
					//log.info("TROVATO con nome giusto porta delegata ["+this.location+"]");
					this.nomePortaIndivituata = this.location;
				}
			}
			
			
			if(this.soggetto == null){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
							getErrore401_PortaInesistente("verificare i parametri di accesso utilizzati",this.location,this.urlCompleta);
				return false;
			}

			return true;

		}catch(Exception e){
			this.log.error("Identificazione porta non riuscita location["+this.location+"] urlInvocazione["+this.urlCompleta+"]",e);
			try{
				this.erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PD);
			}catch(Exception eError){
				throw new RuntimeException(eError.getMessage(), eError);
			}
			return false;
		}
	}


	/**
	 * Ritorna l'identificativo del Soggetto che include la porta delegata richiesta. 
	 *
	 * @return l'identificativo del Soggetto
	 * 
	 */
	public IDSoggetto getSoggetto(){
		return this.soggetto;
	}


	public CodiceErroreIntegrazione getCodiceErrore(){
		return this.erroreIntegrazione.getCodiceErrore();
	}
	
	public String getMessaggioErrore() throws ProtocolException {
		return this.erroreIntegrazione.getDescrizione(this.protocolFactory);
	}
	
	public ErroreIntegrazione getErroreIntegrazione() {
		return this.erroreIntegrazione;
	}
	

	/**
	 * Ritorna il tipo di autenticazione associato alla porta delegata.
	 *
	 * @return tipo di autenticazione.
	 * 
	 */
	public String getTipoAutenticazione(){
		return this.tipoAutenticazione;
	}

	/**
	 * Ritorna il tipo di autorizzazione associato alla porta delegata.
	 *
	 * @return tipo di autorizzazione.
	 * 
	 */
	public String getTipoAutorizzazione(){
		return this.tipoAutorizzazione;
	}

	/**
	 * Ritorna il tipo di autorizzazione per contenuto associato alla porta delegata.
	 *
	 * @return tipo di autorizzazione per contenuto
	 * 
	 */
	public String getTipoAutorizzazioneContenuto() {
		return this.tipoAutorizzazioneContenuto;
	}
	
	public String getNomePDIndivituata() {
		return this.nomePortaIndivituata;
	}


}

