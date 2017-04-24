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



package org.openspcoop2.pdd.core;

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDPortaDelegata;
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
import org.slf4j.Logger;


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
	private String invocationUrl;
	private String invocationUrlWithFormBasedParameters;
	/* ----- Individuazione Nome PortaDelegata UrlBased -------- */
    private boolean nomePortaDelegataURLBased = true;
	/* ---- IdPortaDelegata identificata --- */
	private IDPortaDelegata idPortaDelegata;
	/* ---- Porta Delegata ---- */
    private PortaDelegata pd = null;
	
	/* ---- messaggio di errore --- */
    private ErroreIntegrazione erroreIntegrazione;

	/* ---- Tipo di Autenticazione --- */
	private String tipoAutenticazione;
	private boolean autenticazioneOpzionale;

	/* --- Tipo di Autorizzazione --- */
	private String tipoAutorizzazione;
	
	/* --- Tipo di Autorizzazione per Contenuto --- */
	private String tipoAutorizzazioneContenuto;
	
	private IProtocolFactory<?> protocolFactory = null;




	/* ---- Log ----- */
	private Logger log;
	
	/**
	 * Costruttore
	 *
	 * @param urlProtocolContext Parametri identificativi della porta delegata.
	 * 
	 */
	public IdentificazionePortaDelegata(URLProtocolContext urlProtocolContext,IProtocolFactory<?> protocolFactory) {
		this.invocationUrl = urlProtocolContext.getFunctionParameters();
		this.invocationUrlWithFormBasedParameters = urlProtocolContext.getUrlInvocazione_formBased();
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

			if(this.invocationUrl==null || "".equals(this.invocationUrl)){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
							getErrore401_PortaInesistente("nella url di invocazione alla Porta di Dominio non e' stata fornita il nome di una PD");
				return false;
			}

			ConfigurazionePdDManager configurazionePdDReader = ConfigurazionePdDManager.getInstance(state);
			
			// Controllo Esistenza Porta Delegata appartenente ad un soggetto.
			// Viene controllata la url 'scalandola' di '/'
			if(this.nomePortaDelegataURLBased){
				String portaDelegata = new String(this.invocationUrl);
				if(portaDelegata.endsWith("/"))
					portaDelegata = portaDelegata.substring(0,portaDelegata.length()-1);
				while(portaDelegata.contains("/")){
					//this.log.info("Cerco con nome porta delegata ["+portaDelegata+"]");
					try{
						this.idPortaDelegata = configurazionePdDReader.getIDPortaDelegata(portaDelegata,this.protocolFactory);
					}catch(DriverConfigurazioneNotFound dNotFound){}
					if(this.idPortaDelegata!=null){
						//this.log.info("TROVATA porta delegata ["+portaDelegata+"]");
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
					this.idPortaDelegata = configurazionePdDReader.getIDPortaDelegata(portaDelegata,this.protocolFactory);
				}catch(DriverConfigurazioneNotFound dNotFound){}
				if(this.idPortaDelegata!=null){
					//this.log.info("ULTIMA....TROVATA porta delegata ["+portaDelegata+"]");
				}
			}
			
			if(this.idPortaDelegata==null){
				//log.info("Cerco con nome giusto porta delegata ["+this.location+"]");
				// check per nome PD univoco
				try{
					this.idPortaDelegata = configurazionePdDReader.getIDPortaDelegata(this.invocationUrl,this.protocolFactory);
				}catch(DriverConfigurazioneNotFound dNotFound){
					this.erroreIntegrazione = 
							ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
								getErrore401_PortaInesistente("verificare i parametri di accesso utilizzati",this.invocationUrl,this.invocationUrlWithFormBasedParameters);
					this.log.error(this.erroreIntegrazione.getDescrizione(this.protocolFactory)+": "+dNotFound.getMessage());
					return false;
				}	
				if(this.idPortaDelegata!=null){
					//log.info("TROVATO con nome giusto porta delegata ["+this.location+"]");
				}
			}
			
			
			if(this.idPortaDelegata == null){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
							getErrore401_PortaInesistente("verificare i parametri di accesso utilizzati",this.invocationUrl,this.invocationUrlWithFormBasedParameters);
				return false;
			}

					
			RichiestaDelegata richiestaDelegata = new RichiestaDelegata(this.idPortaDelegata);
		
			// Get Porta Delegata
			try{
				this.pd = configurazionePdDReader.getPortaDelegata(richiestaDelegata.getIdPortaDelegata());
			}catch(DriverConfigurazioneNotFound notFound){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
							getErrore401_PortaInesistente(notFound.getMessage(),this.idPortaDelegata.getNome(),this.invocationUrlWithFormBasedParameters);
				return false;
			}
			
			// tipo di Autenticazione
			try{
				this.tipoAutenticazione = configurazionePdDReader.getAutenticazione(this.pd);
				this.autenticazioneOpzionale = configurazionePdDReader.isAutenticazioneOpzionale(this.pd);
			}catch(DriverConfigurazioneNotFound notFound){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
							getErrore401_PortaInesistente("[lettura tipo autenticazione] "+notFound.getMessage(),this.idPortaDelegata.getNome(),this.invocationUrlWithFormBasedParameters);
				return false;
			}

			// tipo di Autorizzazione
			try{
				this.tipoAutorizzazione = configurazionePdDReader.getAutorizzazione(this.pd);
			}catch(DriverConfigurazioneNotFound notFound){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
							getErrore401_PortaInesistente("[lettura tipo autorizzazione] "+notFound.getMessage(),this.idPortaDelegata.getNome(),this.invocationUrlWithFormBasedParameters);
				return false;
			}
			
			// tipo di Autorizzazione per contenuto
			try{
				this.tipoAutorizzazioneContenuto = configurazionePdDReader.getAutorizzazioneContenuto(this.pd);
			}catch(DriverConfigurazioneNotFound notFound){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
							getErrore401_PortaInesistente("[lettura tipo autorizzazione contenuto] "+notFound.getMessage(),this.idPortaDelegata.getNome(),this.invocationUrlWithFormBasedParameters);
				return false;
			}

			return true;

		}catch(Exception e){
			this.log.error("Identificazione porta delegata non riuscita location["+this.invocationUrl+"] urlInvocazione["+this.invocationUrlWithFormBasedParameters+"]",e);
			try{
				this.erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PD);
			}catch(Exception eError){
				throw new RuntimeException(eError.getMessage(), eError);
			}
			return false;
		}
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

	public boolean isAutenticazioneOpzionale() {
		return this.autenticazioneOpzionale;
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


	public PortaDelegata getPd() {
		return this.pd;
	} 

    public IDPortaDelegata getIdPortaDelegata() {
		return this.idPortaDelegata;
	}
}

