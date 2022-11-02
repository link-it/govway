/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.engine.builder;


import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.slf4j.Logger;

/**
 * Classe utilizzata per Sbustare un SOAPEnvelope dell'header del protocollo.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Sbustamento  {

	private IProtocolFactory<?> protocolFactory;
	//private Logger log;
	private IState state;

	public Sbustamento(IProtocolFactory<?> protocolFactory,IState state){
		this(Configurazione.getLibraryLog(), protocolFactory, state);
	}
	
	public Sbustamento(Logger log, IProtocolFactory<?> protocolFactory,IState state){
		if(log==null) log = Configurazione.getLibraryLog();
		//this.log = log;
		this.protocolFactory = protocolFactory;
		this.state = state;
	}
	
	public IProtocolFactory<?> getProtocolFactory(){
		return this.protocolFactory;
	}
	
	/**
	 * Effettua lo sbustamento
	 *  
	 * @param msg Messaggio in cui deve essere estratto un header.
	 * @param gestioneManifest Indicazione se deve essere gestito il manifest degli attachments	
	 * 
	 */
	public ProtocolMessage sbustamento(OpenSPCoop2Message msg, Context context,
			Busta busta,
			RuoloMessaggio ruoloMessaggio, boolean gestioneManifest,ProprietaManifestAttachments proprietaManifestAttachments,
			FaseSbustamento faseSbustamento, RequestInfo requestInfo) throws ProtocolException{
		if(proprietaManifestAttachments==null){
			proprietaManifestAttachments = new ProprietaManifestAttachments();
		}
		proprietaManifestAttachments.setGestioneManifest(gestioneManifest);
		return this.protocolFactory.createBustaBuilder(this.state).sbustamento( msg, context,
				busta, ruoloMessaggio, proprietaManifestAttachments,
				faseSbustamento,requestInfo.getIntegrationServiceBinding(),requestInfo.getBindingConfig());
	}
}





