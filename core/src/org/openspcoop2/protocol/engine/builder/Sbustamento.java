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



package org.openspcoop2.protocol.engine.builder;


import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.engine.Configurazione;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * Classe utilizzata per Sbustare un SOAPEnvelope dell'header del protocollo.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Sbustamento  {

	private IProtocolFactory protocolFactory;
	//private Logger log;

	public Sbustamento(IProtocolFactory protocolFactory){
		this(Configurazione.getLibraryLog(), protocolFactory);
	}
	
	public Sbustamento(Logger log, IProtocolFactory protocolFactory){
		if(log==null) log = Configurazione.getLibraryLog();
		//this.log = log;
		this.protocolFactory = protocolFactory;
	}
	
	public IProtocolFactory getProtocolFactory(){
		return this.protocolFactory;
	}
	
	/**
	 * Effettua lo sbustamento
	 *  
	 * @param msg Messaggio in cui deve essere estratto un header.
	 * @param gestioneManifest Indicazione se deve essere gestito il manifest degli attachments	
	 * 
	 */
	public SOAPElement sbustamento(IState state, OpenSPCoop2Message msg,Busta busta,
			boolean isRichiesta, boolean gestioneManifest,ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{
		if(proprietaManifestAttachments==null){
			proprietaManifestAttachments = new ProprietaManifestAttachments();
		}
		proprietaManifestAttachments.setGestioneManifest(gestioneManifest);
		return this.protocolFactory.createBustaBuilder().sbustamento(state, msg, busta, isRichiesta, proprietaManifestAttachments);
	}
}





