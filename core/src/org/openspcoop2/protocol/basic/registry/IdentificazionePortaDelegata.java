/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaPorteDelegate;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.slf4j.Logger;


/**
 * Classe utilizzabile per identificare la porta delegata richiesta.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IdentificazionePortaDelegata extends AbstractIdentificazionePorta {



	/* ---- Porta Delegata ---- */
    private PortaDelegata pd = null;
    
    /* ---- Porta Delegata (Azione specifica) ---- */
    private HashMap<String, PortaDelegata> pdDelegatedByAction = new HashMap<String, PortaDelegata>();
    private HashMap<String, IDPortaDelegata> idPdDelegatedByAction = new HashMap<String, IDPortaDelegata>();
    
	
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
    /*public IdentificazionePortaDelegata(Logger log, IProtocolFactory<?> protocolFactory, org.openspcoop2.protocol.sdk.state.IState state,
    		PortaDelegata pd)
			throws ProtocolException {
		super(log, protocolFactory, state);
		this.pd = pd;
		IDPortaDelegata idPD = new IDPortaDelegata();
		idPD.setNome(this.pd.getNome());
		this.identificativoPorta = idPD;
	}*/
    public IdentificazionePortaDelegata(Logger log, IProtocolFactory<?> protocolFactory, 
    		RegistroServiziManager registroServiziManager, Object configurazioneManager,
    		PortaDelegata pd)
			throws ProtocolException {
		super(log, protocolFactory, registroServiziManager, configurazioneManager);
		this.pd = pd;
		IDPortaDelegata idPD = new IDPortaDelegata();
		idPD.setNome(this.pd.getNome());
		this.identificativoPorta = idPD;
	}
    public IdentificazionePortaDelegata(Logger log, IProtocolFactory<?> protocolFactory, 
    		IRegistryReader registryReader, IConfigIntegrationReader configIntegrationReader,
    		PortaDelegata pd)
			throws ProtocolException {
		super(log, protocolFactory, registryReader, configIntegrationReader);
		this.pd = pd;
		IDPortaDelegata idPD = new IDPortaDelegata();
		idPD.setNome(this.pd.getNome());
		this.identificativoPorta = idPD;
	}


	@Override
	protected Object getIDPorta(String porta) throws RegistryNotFound, RegistryException{
		return this.configIntegrationReader.getIdPortaDelegata(porta, this.protocolFactory);
	}
	
	@Override
	protected String enrichPorta(String porta) throws RegistryException{
		try {
			return this.porteNamingUtils.enrichPD(porta);
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
			
			// Verifico che l'azione non sia in modalità delegatedBy
			if(this.pd.getAzione()!=null && PortaDelegataAzioneIdentificazione.DELEGATED_BY.equals(this.pd.getAzione().getIdentificazione())) {
				this.erroreIntegrazione = 
						ErroriIntegrazione.ERRORE_441_PORTA_NON_INVOCABILE_DIRETTAMENTE.
							getErrore441_PortaNonInvocabileDirettamente(idPD.getNome(),this.urlCompleta);
				return false;
			}

			return true;

		}catch(Exception e){
			this.log.error("Identificazione porta delegata non riuscita location["+this.location+"] urlInvocazione["+this.urlCompleta+"]",e);
			try{
				this.erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PORTA);
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

	/**
	 * Avvia il processo di identificazione della porta per l'azione specifica.
	 *
	 * @return true in caso di identificazione con successo, false altrimenti.
	 * 
	 */
	public boolean find(String action) {
		try {
			
			if(this.pd.getRicercaPortaAzioneDelegata()!=null && StatoFunzionalita.ABILITATO.equals(this.pd.getRicercaPortaAzioneDelegata())) {
				
				FiltroRicercaPorteDelegate filtroPD = new FiltroRicercaPorteDelegate();
				filtroPD.setNomePortaDelegante(getIDPortaDelegata().getNome());
				filtroPD.setAzione(action);
				List<IDPortaDelegata> list = null;
				IDPortaDelegata idPD = null;
				try {
					list = this.configIntegrationReader.findIdPorteDelegate(filtroPD);
				}catch(RegistryNotFound notFound) {}
				if(list!=null && list.size()>0) {
					if(list.size()>1) {
						throw new Exception("Trovate più porte ("+list.size()+") che corrisponde al filtro?? ("+filtroPD+")");
					}
					idPD = list.get(0);
					this.idPdDelegatedByAction.put(action, idPD);
					
					try{
						PortaDelegata pd = this.configIntegrationReader.getPortaDelegata(idPD);
						this.pdDelegatedByAction.put(action, pd);
					}catch(RegistryNotFound notFound){
						this.erroreIntegrazione = 
								ErroriIntegrazione.ERRORE_401_PORTA_INESISTENTE.
									getErrore401_PortaInesistente(notFound.getMessage(),idPD.getNome(),this.urlCompleta);
						return false;
					}
				}
				
			}
			
			return true;
			
		}catch(Exception e){
			this.log.error("Identificazione porta delegata non riuscita per azione specifica ["+action+"], porta delegata di base["+getIDPortaDelegata().getNome()+"]",e);
			try{
				this.erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_502_IDENTIFICAZIONE_PORTA);
			}catch(Exception eError){
				throw new RuntimeException(eError.getMessage(), eError);
			}
			return false;
		}
	}
	
	public IDPortaDelegata getIDPortaDelegata(String action){
		if(this.idPdDelegatedByAction.containsKey(action)) {
			return this.idPdDelegatedByAction.get(action);
		}
		return null;
	}

	public PortaDelegata getPortaDelegata(String action) {
		if(this.pdDelegatedByAction.containsKey(action)) {
			return this.pdDelegatedByAction.get(action);
		}
		return null;
	} 
}

