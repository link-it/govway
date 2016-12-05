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

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativa;
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
 * Classe utilizzabile per identificare la porta applicativa richiesta.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */

public class IdentificazionePortaApplicativa extends AbstractIdentificazionePorta {


    /* ---- Porta Applicativa ---- */
    private PortaApplicativa pa = null;
	
	/**
	 * Costruttore
	 *
	 * @param urlProtocolContext Parametri identificativi della porta applicativa.
	 * @throws ProtocolException 
	 * 
	 */
	public IdentificazionePortaApplicativa(TransportRequestContext urlProtocolContext, Logger log,
			boolean portaUrlBased, IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
			IProtocolFactory<?> protocolFactory) throws ProtocolException {
		super(urlProtocolContext, log, portaUrlBased, registryReader, configIntegrationReader, protocolFactory);
	}

	@Override
	protected Object getIDPorta(String porta) throws RegistryNotFound{
		return this.configIntegrationReader.getIdPortaApplicativa(porta, this.protocolFactory);
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
			
			IDPortaApplicativa idPA = this.getIDPortaApplicativa();
			
			// Get Porta Applicativa
			try{
				this.pa = this.configIntegrationReader.getPortaApplicativa(idPA);
			}catch(RegistryNotFound notFound){
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
							getErrore401_PortaInesistente(notFound.getMessage(),idPA.getNome(),this.urlCompleta);
				return false;
			}
			
//			// tipo di Autenticazione
//			this.tipoAutenticazione = this.pa.getAutenticazione();
//
//			// tipo di Autorizzazione
//			this.tipoAutorizzazione = this.pa.getAutorizzazione();
			
			// tipo di Autorizzazione per contenuto
			this.tipoAutorizzazioneContenuto = this.pa.getAutorizzazioneContenuto();

			return true;

		}catch(Exception e){
			this.log.error("Identificazione porta applicativa non riuscita location["+this.location+"] urlInvocazione["+this.urlCompleta+"]",e);
			try{
				this.erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PD);
			}catch(Exception eError){
				throw new RuntimeException(eError.getMessage(), eError);
			}
			return false;
		}
	}

	public IDPortaApplicativa getIDPortaApplicativa(){
		return (IDPortaApplicativa) this.identificativoPorta;
	}

	public PortaApplicativa getPortaApplicativa() {
		return this.pa;
	} 

}

