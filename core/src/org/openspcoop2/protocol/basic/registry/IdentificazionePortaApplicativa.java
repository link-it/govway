/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.basic.registry;

import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPorteApplicative;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.slf4j.Logger;


/**
 * Classe utilizzabile per identificare la porta applicativa richiesta.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IdentificazionePortaApplicativa extends AbstractIdentificazionePorta {


    /* ---- Porta Applicativa ---- */
    private PortaApplicativa pa = null;
    
    /* ---- Porta Applicativa (Azione specifica) ---- */
    private HashMap<String, PortaApplicativa> paDelegatedByAction = new HashMap<String, PortaApplicativa>();
    private HashMap<String, IDPortaApplicativa> idPaDelegatedByAction = new HashMap<String, IDPortaApplicativa>();
    
	
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
    public IdentificazionePortaApplicativa(Logger log, IProtocolFactory<?> protocolFactory, IState state,
    		PortaApplicativa pa)
			throws ProtocolException {
		super(log, protocolFactory, state);
		this.pa = pa;
		IDPortaApplicativa idPA = new IDPortaApplicativa();
		idPA.setNome(this.pa.getNome());
		this.identificativoPorta = idPA;
	}

	@Override
	protected Object getIDPorta(String porta) throws RegistryNotFound, RegistryException{
		return this.configIntegrationReader.getIdPortaApplicativa(porta, this.protocolFactory);
	}

	@Override
	protected String enrichPorta(String porta) throws RegistryException{
		try {
			return this.porteNamingUtils.enrichPA(porta);
		}catch(Exception e) {
			throw new RegistryException(e.getMessage(),e);
		}
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
			
			
			// Verifico che l'azione non sia in modalità delegatedBy
			if(this.pa.getAzione()!=null && PortaApplicativaAzioneIdentificazione.DELEGATED_BY.equals(this.pa.getAzione().getIdentificazione())) {
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_441_PORTA_NON_INVOCABILE_DIRETTAMENTE.
							getErrore441_PortaNonInvocabileDirettamente(idPA.getNome(),this.urlCompleta);
				return false;
			}

			return true;

		}catch(Exception e){
			this.log.error("Identificazione porta applicativa non riuscita location["+this.location+"] urlInvocazione["+this.urlCompleta+"]",e);
			try{
				this.erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PORTA);
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

	/**
	 * Avvia il processo di identificazione della porta per l'azione specifica.
	 *
	 * @return true in caso di identificazione con successo, false altrimenti.
	 * 
	 */
	public boolean find(String action) {
		try {
			
			if(this.pa.getRicercaPortaAzioneDelegata()!=null && StatoFunzionalita.ABILITATO.equals(this.pa.getRicercaPortaAzioneDelegata())) {
				
				FiltroRicercaPorteApplicative filtroPA = new FiltroRicercaPorteApplicative();
				filtroPA.setNomePortaDelegante(getIDPortaApplicativa().getNome());
				filtroPA.setAzione(action);
				List<IDPortaApplicativa> list = null;
				IDPortaApplicativa idPA = null;
				try {
					list = this.configIntegrationReader.findIdPorteApplicative(filtroPA);
				}catch(RegistryNotFound notFound) {}
				if(list!=null && list.size()>0) {
					if(list.size()>1) {
						throw new Exception("Trovate più porte ("+list.size()+") che corrisponde al filtro?? ("+filtroPA+")");
					}
					idPA = list.get(0);
					this.idPaDelegatedByAction.put(action, idPA);
					
					try{
						PortaApplicativa pa = this.configIntegrationReader.getPortaApplicativa(idPA);
						this.paDelegatedByAction.put(action, pa);
					}catch(RegistryNotFound notFound){
						this.erroreIntegrazione = 
								ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
									getErrore401_PortaInesistente(notFound.getMessage(),idPA.getNome(),this.urlCompleta);
						return false;
					}
				}
				
			}
			
			return true;
			
		}catch(Exception e){
			this.log.error("Identificazione porta applicativa non riuscita per azione specifica ["+action+"], porta applicativa di base["+getIDPortaApplicativa().getNome()+"]",e);
			try{
				this.erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PORTA);
			}catch(Exception eError){
				throw new RuntimeException(eError.getMessage(), eError);
			}
			return false;
		}
	}
	
	public IDPortaApplicativa getIDPortaApplicativa(String action){
		if(this.idPaDelegatedByAction.containsKey(action)) {
			return this.idPaDelegatedByAction.get(action);
		}
		return null;
	}

	public PortaApplicativa getPortaApplicativa(String action) {
		if(this.paDelegatedByAction.containsKey(action)) {
			return this.paDelegatedByAction.get(action);
		}
		return null;
	} 
}

