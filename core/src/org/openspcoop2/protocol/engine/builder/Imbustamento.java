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



package org.openspcoop2.protocol.engine.builder;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;


/**
 * Classe utilizzata per costruire una Busta, o parti di essa.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Tronci Fabio (tronci@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Imbustamento  {

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Logger utilizzato per debug. */
	@SuppressWarnings("unused")
	private Logger log = null;
	private org.openspcoop2.protocol.sdk.IProtocolFactory<?> protocolFactory;
	private IState state;
	
	public Imbustamento(Logger aLog, org.openspcoop2.protocol.sdk.IProtocolFactory<?> protocolFactory, IState state) throws ProtocolException{
		if(aLog!=null)
			this.log = aLog;
		else
			this.log = LoggerWrapperFactory.getLogger(Imbustamento.class);
		this.protocolFactory = protocolFactory;
		this.state = state;
	}

	public org.openspcoop2.protocol.sdk.IProtocolFactory<?> getProtocolFactory(){
		return this.protocolFactory;
	}

	/** LunghezzaPrefisso */
	public static int prefixLenght = 0;

	
	/* ********  Metodi per la costruzione di parti della busta  ******** */ 

	//public String buildID(IDSoggetto idSoggetto, String idTransazione, Boolean isRichiesta) throws ProtocolException{
	//	return this.buildID(idSoggetto, idTransazione, Configurazione.getAttesaAttiva(), Configurazione.getCheckInterval(), isRichiesta);
	//}

	public String buildID(IDSoggetto idSoggetto, String idTransazione, long attesaAttiva,
			int checkInterval, RuoloMessaggio ruoloMessaggio) throws ProtocolException {
		try{
			return this.protocolFactory.createBustaBuilder(this.state).newID(idSoggetto, idTransazione, ruoloMessaggio);	
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	



	/* ----------------  Metodi per la costruzione di una busta  -------------------- */

	public ProtocolMessage imbustamento(OpenSPCoop2Message msg,Busta busta,Integrazione integrazione,ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{	
		return this.imbustamento(msg, busta,integrazione, false, RuoloMessaggio.RISPOSTA, false, proprietaManifestAttachments);
	}


	public ProtocolMessage imbustamento(OpenSPCoop2Message msg,Busta busta,Integrazione integrazione,
			boolean gestioneManifest,RuoloMessaggio ruoloMessaggio,boolean scartaBody,
			ProprietaManifestAttachments proprietaManifestAttachments) throws ProtocolException{	
		if(proprietaManifestAttachments==null){
			proprietaManifestAttachments = new ProprietaManifestAttachments();
		}
		proprietaManifestAttachments.setGestioneManifest(gestioneManifest);
		proprietaManifestAttachments.setScartaBody(scartaBody);
		return this.protocolFactory.createBustaBuilder(this.state).imbustamento(msg, busta, ruoloMessaggio, proprietaManifestAttachments);
	}


	/**
	 * Metodo che si occupa di costruire un elemento Trasmissione
	 *
	 * @param trasmissione Trasmissione
	 * 
	 */

	public ProtocolMessage addTrasmissione(OpenSPCoop2Message message,Trasmissione trasmissione,boolean readQualifiedAttribute) throws ProtocolException{
		return this.protocolFactory.createBustaBuilder(this.state).addTrasmissione(message, trasmissione);
	}


}





