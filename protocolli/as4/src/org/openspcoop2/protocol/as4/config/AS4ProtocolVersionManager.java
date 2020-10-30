/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package org.openspcoop2.protocol.as4.config;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.basic.config.BasicVersionManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Servizio;
import org.openspcoop2.protocol.sdk.constants.StatoFunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;
import org.slf4j.Logger;

/**
 * Classe che implementa, in base al protocollo AS4, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolVersionManager} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4ProtocolVersionManager extends BasicVersionManager {
	
	protected AS4Properties as4Properties = null;
	protected Logger logger = null;
	protected String versione;
	protected AS4ProtocolManager protocolManager = null;
	public AS4ProtocolVersionManager(IProtocolFactory<?> protocolFactory,String versione) throws ProtocolException{
		super(protocolFactory);
		this.versione = versione;
		this.logger = this.getProtocolFactory().getLogger();
		this.as4Properties = AS4Properties.getInstance();
		this.protocolManager = (AS4ProtocolManager) protocolFactory.createProtocolManager();
	}
	
	@Override
	public StatoFunzionalitaProtocollo getCollaborazione(Busta busta){
		return StatoFunzionalitaProtocollo.REGISTRO;
	}
	@Override
	public StatoFunzionalitaProtocollo getCollaborazione(Servizio infoServizio){
		return StatoFunzionalitaProtocollo.REGISTRO;
	}
	
	@Override
	public StatoFunzionalitaProtocollo getIdRiferimentoRichiesta(Busta busta){
		return StatoFunzionalitaProtocollo.REGISTRO;
	}
	@Override
	public StatoFunzionalitaProtocollo getIdRiferimentoRichiesta(Servizio infoServizio){
		return StatoFunzionalitaProtocollo.REGISTRO;
	}
	
	@Override
	public Boolean isAggiungiDetailErroreApplicativo_FaultApplicativo() {
		return this.protocolManager.isAggiungiDetailErroreApplicativo_FaultApplicativo();
	}

	@Override
	public Boolean isAggiungiDetailErroreApplicativo_FaultPdD() {
		return this.protocolManager.isAggiungiDetailErroreApplicativo_FaultPdD();
	}

	@Override
	public OpenSPCoop2Message updateOpenSPCoop2MessageResponse(OpenSPCoop2Message msg, Busta busta, 
    		NotifierInputStreamParams notifierInputStreamParams, 
    		TransportRequestContext transportRequestContext, TransportResponseContext transportResponseContext,
    		IRegistryReader registryReader,
    		boolean integration) throws ProtocolException{
		return this.protocolManager.updateOpenSPCoop2MessageResponse(msg, busta, notifierInputStreamParams, transportRequestContext, transportResponseContext, registryReader, integration);
	}
	
	@Override
	public boolean isStaticRoute() throws ProtocolException {
		return this.protocolManager.isStaticRoute();
	}
	
	@Override
	public org.openspcoop2.core.registry.Connettore getStaticRoute(IDSoggetto idSoggettoMittente, IDServizio idServizio,
			IRegistryReader registryReader) throws ProtocolException{
		return this.protocolManager.getStaticRoute(idSoggettoMittente, idServizio, registryReader);
	}
	
}
