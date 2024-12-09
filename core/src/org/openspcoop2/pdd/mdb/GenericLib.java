/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.pdd.mdb;

import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.dynamic.PddPluginLoader;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateException;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.connector.IAsyncResponseCallback;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 *
 * Libreria generica per le funzionalita' di MDB 
 *
 *
 * @author Tronci Fabio (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class GenericLib {

	/* **** PASSATI DALL' MDB O DAL THREAD DELEGATO *******************/
	/** Logger utilizzato per debug. */
	protected Logger log = null;
	/** OpenSPCoop Properties */
	protected OpenSPCoop2Properties propertiesReader;
	/** Loader */
	protected Loader loader;
	/** PluginLoader */
	protected PddPluginLoader pluginLoader;
	
	protected String idModulo;
	
	protected ProtocolFactoryManager protocolFactoryManager = null;

	protected boolean inizializzazioneUltimata = false;
	public boolean getInizializzazioneUltimata(){ return this.inizializzazioneUltimata;}

	protected synchronized void inizializza() throws GenericLibException{
		if(!this.inizializzazioneUltimata){
			
			try{
			
				this.propertiesReader = OpenSPCoop2Properties.getInstance();
				this.loader = Loader.getInstance();
				this.pluginLoader = PddPluginLoader.getInstance();
			
				this.protocolFactoryManager = ProtocolFactoryManager.getInstance();
						
				boolean error = false;
								
				this.inizializzazioneUltimata = !error;
			}catch(Exception e){
				throw new GenericLibException(e.getMessage(),e);
			}
		}		
	}
	
	public GenericLib(String idModulo, Logger log){
		this.idModulo = idModulo;
		this.log = log;
	}
	

	protected MsgDiagnostico initMsgDiagnostico(ConfigurazionePdDManager configurazionePdDManager) throws OpenSPCoopStateException {
		try {
			return MsgDiagnostico.newInstance(this.idModulo,configurazionePdDManager);
		} catch (Exception e) {
			this.log.error("Riscontrato Errore durante l'inizializzazione del MsgDiagnostico",e);
			/**if(this.msgDiag!=null)
				this.msgDiag.logErroreGenerico(e.getMessage(),"MsgDiagnostico.new()");*/
			throw new OpenSPCoopStateException("Riscontrato Errore durante l'inizializzazione del MsgDiagnostico");
		}
	}


	protected IAsyncResponseCallback asyncResponseCallback;
	public EsitoLib onMessage(IOpenSPCoopState openspcoopState, RegistroServiziManager registroServiziManager, ConfigurazionePdDManager configurazionePdDManager, 
			IAsyncResponseCallback asyncResponseCallback) throws OpenSPCoopStateException{
		this.asyncResponseCallback = asyncResponseCallback;
		return this.onMessage(openspcoopState, registroServiziManager, configurazionePdDManager);
	}
	
	public EsitoLib onMessage(IOpenSPCoopState openspcoopState, RegistroServiziManager registroServiziManager, ConfigurazionePdDManager configurazionePdDManager) throws OpenSPCoopStateException{
		if(registroServiziManager!=null && configurazionePdDManager!=null) {
			return _onMessage(openspcoopState, registroServiziManager, configurazionePdDManager,
					initMsgDiagnostico(configurazionePdDManager));
		}
		else {
			return onMessage(openspcoopState);
		}
	}
	public EsitoLib onMessage(IOpenSPCoopState openspcoopState, ConfigurazionePdDManager configurazionePdDManager) throws OpenSPCoopStateException{
		if(configurazionePdDManager!=null && configurazionePdDManager.getRegistroServiziManager()!=null) {
			return _onMessage(openspcoopState, configurazionePdDManager.getRegistroServiziManager(), configurazionePdDManager,
					initMsgDiagnostico(configurazionePdDManager));
		}
		else {
			return onMessage(openspcoopState);
		}
	}
	public EsitoLib onMessage(IOpenSPCoopState iopenspcoopState) throws OpenSPCoopStateException{
		
		RegistroServiziManager registroServiziManager = null;
		ConfigurazionePdDManager configurazionePdDManager = null;
		
		if(iopenspcoopState instanceof OpenSPCoopState openspcoopState){
			registroServiziManager = RegistroServiziManager.getInstance(openspcoopState.getStatoRichiesta(),openspcoopState.getStatoRisposta());
			configurazionePdDManager = ConfigurazionePdDManager.getInstance(openspcoopState.getStatoRichiesta(),openspcoopState.getStatoRisposta());
		}
		else{
			registroServiziManager = RegistroServiziManager.getInstance();
			configurazionePdDManager = ConfigurazionePdDManager.getInstance();
		}
	
		return _onMessage(iopenspcoopState, registroServiziManager, configurazionePdDManager,
				initMsgDiagnostico(configurazionePdDManager));
	}
	
	public abstract EsitoLib _onMessage(IOpenSPCoopState openspcoopState,
			RegistroServiziManager registroServiziManager,ConfigurazionePdDManager configurazionePdDManager, MsgDiagnostico msgDiag) throws OpenSPCoopStateException;
	
}
