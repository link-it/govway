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



package org.openspcoop2.pdd.core;

import org.slf4j.Logger;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.state.IState;


/**
 * Classe utilizzabile per identificare il servizio applicativo e la porta delegata richiesta.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IdentificazionePortaDelegata {

	//private static org.slf4j.Logger log = OpenSPCoopLogger.getLoggerOpenSPCoopCore();


	/* ---- Location della Porta Delegata --- */
	private String location;
	private String urlCompleta;
	/* --- NomePortaDelegata ----*/
	private String nomePDIndivituata;
	/* ----- Individuazione Nome PortaDelegata UrlBased -------- */
    private boolean nomePortaDelegataURLBased = true;
    /* ---- Porta Delegata ---- */
    private PortaDelegata pd = null;
	
	/* ---- messaggio di errore --- */
    private ErroreIntegrazione erroreIntegrazione;

	/* ---- Soggetto identificato --- */
	private IDSoggetto soggetto;

	/* ---- Tipo di Autenticazione --- */
	private String tipoAutenticazione;

	/* --- Tipo di Autorizzazione --- */
	private String tipoAutorizzazione;
	
	/* --- Tipo di Autorizzazione per Contenuto --- */
	private String tipoAutorizzazioneContenuto;
	
	private IProtocolFactory protocolFactory = null;




	/* ---- Log ----- */
	private Logger log;
	
	/**
	 * Costruttore
	 *
	 * @param urlProtocolContext Parametri identificativi della porta delegata.
	 * 
	 */
	public IdentificazionePortaDelegata(URLProtocolContext urlProtocolContext,IProtocolFactory protocolFactory) {
		this.location = urlProtocolContext.getFunctionParameters();
		this.urlCompleta = urlProtocolContext.getUrlInvocazione_formBased();
		this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(URLProtocolContext.IntegrationManager_FUNCTION.equals(urlProtocolContext.getFunction())){
			this.nomePortaDelegataURLBased = OpenSPCoop2Properties.getInstance().integrationManager_isNomePortaDelegataUrlBased();
		}
		else{
			this.nomePortaDelegataURLBased = true; 
		}
		this.protocolFactory = protocolFactory;
	}




	/**
	 * Avvia il processo di identificazione.
	 *
	 * @return true in caso di identificazione con successo, false altrimenti.
	 * 
	 */
	public boolean process(IState state) {

		try{

			if(this.location==null || "".equals(this.location)){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PD_INESISTENTE.
							getErrore401_PortaDelegataInesistente("nella url di invocazione alla Porta di Dominio non e' stata fornita il nome di una PD");
				return false;
			}

			ConfigurazionePdDManager configurazionePdDReader = ConfigurazionePdDManager.getInstance(state);
			
			// Controllo Esistenza Porta Delegata appartenente ad un soggetto.
			// Viene controllata la url 'scalandola' di '/'
			if(this.nomePortaDelegataURLBased){
				String portaDelegata = new String(this.location);
				if(portaDelegata.endsWith("/"))
					portaDelegata = portaDelegata.substring(0,portaDelegata.length()-1);
				while(portaDelegata.contains("/")){
					//this.log.info("Cerco con nome porta delegata ["+portaDelegata+"]");
					try{
						this.soggetto = configurazionePdDReader.getIDSoggetto(portaDelegata,this.protocolFactory);
					}catch(DriverConfigurazioneNotFound dNotFound){}
					if(this.soggetto!=null){
						//this.log.info("TROVATA porta delegata ["+portaDelegata+"]");
						this.nomePDIndivituata = portaDelegata;
						break;
					}else{
						int indexCut = -1;
						for(int i=(portaDelegata.length()-1); i>=0 ;i--){
							if(portaDelegata.charAt(i) == '/'){
								indexCut = i;
								break;
							}
						}
						//this.log.info("indexCut = "+indexCut);
						//this.log.info("elimino parte '/'");
						portaDelegata = portaDelegata.substring(0,indexCut);
						//this.log.info("dopo eliminazione: "+portaDelegata);
					}
				}
				// Provo ad effettuare ultima ricerca
				//this.log.info("ULTIMA... Cerco con nome porta delegata ["+portaDelegata+"]");
				try{
					this.soggetto = configurazionePdDReader.getIDSoggetto(portaDelegata,this.protocolFactory);
				}catch(DriverConfigurazioneNotFound dNotFound){}
				if(this.soggetto!=null){
					//this.log.info("ULTIMA....TROVATA porta delegata ["+portaDelegata+"]");
					this.nomePDIndivituata = portaDelegata;
				}
			}
			
			if(this.nomePDIndivituata==null){
				//log.info("Cerco con nome giusto porta delegata ["+this.location+"]");
				// check per nome PD univoco
				try{
					this.soggetto = configurazionePdDReader.getIDSoggetto(this.location,this.protocolFactory);
				}catch(DriverConfigurazioneNotFound dNotFound){
					this.erroreIntegrazione = 
							ErroriIntegrazione.ERRORE_401_PD_INESISTENTE.
								getErrore401_PortaDelegataInesistente("verificare i parametri di accesso utilizzati",this.location,this.urlCompleta);
					this.log.error(this.erroreIntegrazione.getDescrizione(this.protocolFactory)+": "+dNotFound.getMessage());
					return false;
				}	
				if(this.soggetto!=null){
					//log.info("TROVATO con nome giusto porta delegata ["+this.location+"]");
					this.nomePDIndivituata = this.location;
				}
			}
			
			
			if(this.soggetto == null){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PD_INESISTENTE.
							getErrore401_PortaDelegataInesistente("verificare i parametri di accesso utilizzati",this.location,this.urlCompleta);
				return false;
			}

					
			RichiestaDelegata richiestaDelegata = new RichiestaDelegata(this.soggetto,this.nomePDIndivituata);
		
			// Get Porta Delegata
			try{
				this.pd = configurazionePdDReader.getPortaDelegata(richiestaDelegata.getIdPortaDelegata());
			}catch(DriverConfigurazioneNotFound notFound){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PD_INESISTENTE.
							getErrore401_PortaDelegataInesistente(notFound.getMessage(),this.nomePDIndivituata,this.urlCompleta);
				return false;
			}
			
			// tipo di Autenticazione
			try{
				this.tipoAutenticazione = configurazionePdDReader.getAutenticazione(this.pd);
			}catch(DriverConfigurazioneNotFound notFound){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PD_INESISTENTE.
							getErrore401_PortaDelegataInesistente("[lettura tipo autenticazione] "+notFound.getMessage(),this.nomePDIndivituata,this.urlCompleta);
				return false;
			}

			// tipo di Autorizzazione
			try{
				this.tipoAutorizzazione = configurazionePdDReader.getAutorizzazione(this.pd);
			}catch(DriverConfigurazioneNotFound notFound){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PD_INESISTENTE.
							getErrore401_PortaDelegataInesistente("[lettura tipo autorizzazione] "+notFound.getMessage(),this.nomePDIndivituata,this.urlCompleta);
				return false;
			}
			
			// tipo di Autorizzazione per contenuto
			try{
				this.tipoAutorizzazioneContenuto = configurazionePdDReader.getAutorizzazioneContenuto(this.pd);
			}catch(DriverConfigurazioneNotFound notFound){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PD_INESISTENTE.
							getErrore401_PortaDelegataInesistente("[lettura tipo autorizzazione contenuto] "+notFound.getMessage(),this.nomePDIndivituata,this.urlCompleta);
				return false;
			}

			return true;

		}catch(Exception e){
			this.log.error("Identificazione porta delegata non riuscita location["+this.location+"] urlInvocazione["+this.urlCompleta+"]",e);
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
		return this.nomePDIndivituata;
	}




	public PortaDelegata getPd() {
		return this.pd;
	} 

}

