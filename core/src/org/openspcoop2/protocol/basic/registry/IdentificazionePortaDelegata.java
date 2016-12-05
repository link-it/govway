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



package org.openspcoop2.protocol.basic.registry;

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.slf4j.Logger;


/**
 * Classe utilizzabile per identificare la porta delegata richiesta.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */

public class IdentificazionePortaDelegata extends AbstractIdentificazionePorta {


    /* ---- Porta Delegata ---- */
    private PortaDelegata pd = null;
	
	/**
	 * Costruttore
	 *
	 * @param urlProtocolContext Parametri identificativi della porta delegata.
	 * @throws ProtocolException 
	 * 
	 */
	public IdentificazionePortaDelegata(TransportRequestContext urlProtocolContext, Logger log,
			boolean portaUrlBased, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IProtocolFactory<?> protocolFactory) throws ProtocolException {
		super(urlProtocolContext, log, portaUrlBased, registryReader, configIntegrationReader, protocolFactory);
	}


	@Override
	protected Object getIDPorta(String porta) throws RegistryNotFound{
		return this.configIntegrationReader.getIdPortaDelegata(porta, this.protocolFactory);
	}
	
	/**
	 * Avvia il processo di identificazione.
	 *
	 * @return true in caso di identificazione con successo, false altrimenti.
	 * 
	 */
	@Override
	public boolean process() {

		try{

			if(super.process()==false){
				return false;
			}
			
			IDPortaDelegata idPD = this.getIDPortaDelegata();
			
			// Get Porta Delegata
			try{
				this.pd = this.configIntegrationReader.getPortaDelegata(idPD);
			}catch(RegistryNotFound notFound){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
							getErrore401_PortaInesistente(notFound.getMessage(),idPD.getNome(),this.urlCompleta);
				return false;
			}
			
			// tipo di Autenticazione
			this.tipoAutenticazione = this.pd.getAutenticazione();

			// tipo di Autorizzazione
			this.tipoAutorizzazione = this.pd.getAutorizzazione();
			
			// tipo di Autorizzazione per contenuto
			this.tipoAutorizzazioneContenuto = this.pd.getAutorizzazioneContenuto();

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

	public IDPortaDelegata getIDPortaDelegata(){
		return (IDPortaDelegata) this.identificativoPorta;
	}

	public PortaDelegata getPortaDelegata() {
		return this.pd;
	} 

}

