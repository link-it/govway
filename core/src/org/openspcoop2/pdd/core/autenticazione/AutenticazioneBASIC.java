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



package org.openspcoop2.pdd.core.autenticazione;

import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.state.IState;


/**
 * Classe che implementa una autenticazione BASIC.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutenticazioneBASIC extends AbstractCore implements IAutenticazione{


	/* ---- Servizio Applicativo --- */
	private IDServizioApplicativo servizioApplicativo;

	/* ---- Eventuale errore --- */
	private ErroreIntegrazione errore;
	
	/* ---- Eventuale eccezione --- */
	private Exception exception;





	/**
	 * Avvia il processo di autenticazione.
	 *
	 * @return true in caso di autenticazione con successo, false altrimenti.
	 * 
	 */
	@Override
	public boolean process(InfoConnettoreIngresso infoConnettoreIngresso,IDPortaDelegata idPD,IState state) {

		Credenziali credenziali = infoConnettoreIngresso.getCredenziali();
		
		String user = credenziali.getUsername();
		String password = credenziali.getPassword();

		// Controllo credenziali fornite
		if( (user==null) || ("".equals(user)) || (password==null) || ("".equals(password)) ){
			this.errore = ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallita("Credenziali non fornite",user,password);
			return false;
		}

		try{
			this.servizioApplicativo = ConfigurazionePdDManager.getInstance(state).autenticazioneHTTP(idPD.getSoggettoFruitore(),idPD.getLocationPD(),user,password);
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneBasic non riuscita",e);
			this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
			this.exception = e;
			return false;
		}
		
		if(this.servizioApplicativo == null){
			//this.msgError = CostantiPdD.MSG_402_AUTENTICAZIONE_FALLITA + " username["+user+"] password["+password+"]";
			this.errore = ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallita("Credenziali fornite non corrette",user,password);
			return false;
		}
		else{
			if(this.servizioApplicativo.getIdSoggettoProprietario()!=null && this.servizioApplicativo.getIdSoggettoProprietario().getCodicePorta()==null){
				try{
					this.servizioApplicativo.getIdSoggettoProprietario().setCodicePorta(RegistroServiziManager.getInstance(state).
							getDominio(this.servizioApplicativo.getIdSoggettoProprietario(), null, this.getProtocolFactory()));
				}catch(Exception e){
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneBasic non riuscita (Identificazione dominio del soggetto)",e);
					this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					this.exception = e;
					return false;
				}
			}
		}

		return true;
	}

	
	/**
     * Avvia il processo di autenticazione.
     *
     * @return true in caso di autenticazione con successo, false altrimenti.
     * 
     */
    @Override
	public boolean process(InfoConnettoreIngresso infoConnettoreIngresso,IState state){
    
    	Credenziali credenziali = infoConnettoreIngresso.getCredenziali();
    	
    	String user = credenziali.getUsername();
		String password = credenziali.getPassword();

		// Controllo credenziali fornite
		if( (user==null) || ("".equals(user)) || (password==null) || ("".equals(password)) ){
			this.errore = ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallita("Credenziali non fornite",user,password);
			return false;
		}
		
		try{
			this.servizioApplicativo = ConfigurazionePdDManager.getInstance(state).autenticazioneHTTP(user,password);
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneBasic non riuscita",e);
			this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
			this.exception = e;
			return false;
		}
		
		if(this.servizioApplicativo == null){
			//this.msgError = CostantiPdD.MSG_402_AUTENTICAZIONE_FALLITA + " username["+user+"] password["+password+"]";
			this.errore = ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallita("Credenziali fornite non corrette",user,password);
			return false;
		}
		else{
			if(this.servizioApplicativo.getIdSoggettoProprietario()!=null && this.servizioApplicativo.getIdSoggettoProprietario().getCodicePorta()==null){
				try{
					this.servizioApplicativo.getIdSoggettoProprietario().setCodicePorta(RegistroServiziManager.getInstance(state).
							getDominio(this.servizioApplicativo.getIdSoggettoProprietario(), null, this.getProtocolFactory()));
				}catch(Exception e){
					OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneBasic non riuscita (Identificazione dominio del soggetto)",e);
					this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					this.exception = e;
					return false;
				}
			}
		}

		return true;
    }
	
	/**
	 * Ritorna il servizio applicativo che ha invocato la porta delegata. 
	 *
	 * @return servizio applicativo.
	 * 
	 */
	@Override
	public IDServizioApplicativo getServizioApplicativo(){
		return this.servizioApplicativo;
	}
	
	/**
	 * In caso di avvenuto errore, questo metodo ritorna il motivo dell'errore.
	 *
	 * @return motivo dell'errore (se avvenuto).
	 * 
	 */
	@Override
	public ErroreIntegrazione getErrore(){
		return this.errore;
	}
	
	/**
     * In caso di avvenuto errore, questo metodo ritorna l'eccezione
     *
     * @return motivo dell'errore l'eccezione (se avvenuto).
     * 
     */
    @Override
	public Exception getException(){
    	return this.exception;
    }
}

