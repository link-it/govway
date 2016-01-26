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


package org.openspcoop2.security.message.engine;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.security.message.authorization.IMessageSecurityAuthorization;
import org.openspcoop2.utils.resources.Loader;


/**
 * Classe per la gestione della Sicurezza (role:Receiver)
 *
 * @author Spadafora Marcello <Ma.Spadafora@finsiel.it>
 * @author Montebove Luciano <L.Montebove@finsiel.it>
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public abstract class MessageSecurityReceiver{

	/** Eventuale messaggio di errore avvenuto durante il processo di validazione */
	protected String msgErrore;
	/** Eventuale codice di errore avvenuto durante il processo di validazione  */
	protected CodiceErroreCooperazione codiceErrore;
	/** Eventuale subCodici di errore */
	protected List<SubErrorCodeSecurity> listaSubCodiceErrore = new ArrayList<SubErrorCodeSecurity>();
	/** Contiene il message Context */
	protected MessageSecurityContext messageSecurityContext;
	/** Certificato del client */
	protected String subject;

	protected MessageSecurityReceiver(MessageSecurityContext messageSecurityContext) {
		this.messageSecurityContext = messageSecurityContext;
	}

	protected abstract boolean process(OpenSPCoop2Message message,Busta busta);
		
	
	/**
	 * In caso di avvenuto errore durante il processo di validazione, 
	 * questo metodo ritorna il motivo dell'errore.
	 *
	 * @return motivo dell'errore (se avvenuto).
	 * 
	 */
	protected String getMsgErrore(){
		return this.msgErrore;
	}
	
	public List<SubErrorCodeSecurity> getListaSubCodiceErrore() {
		return this.listaSubCodiceErrore;
	}

	/**
	 * In caso di avvenuto errore, questo metodo ritorna il codice dell'errore.
	 *
	 * @return codice dell'errore (se avvenuto).
	 * 
	 */
	protected CodiceErroreCooperazione getCodiceErrore(){
		return this.codiceErrore;
	}
	
	protected String getActor() {
		return this.messageSecurityContext.getActor();
	}
    
	protected String getSubject() {
		return this.subject;
	}

	public MessageSecurityContext getMessageSecurityContext() {
		return this.messageSecurityContext;
	}

	protected boolean authorize(String authClass,String principal,Busta busta) throws Exception {
		boolean status=false;
		try {
			IMessageSecurityAuthorization auth = (IMessageSecurityAuthorization)Loader.getInstance().newInstance(authClass);
			status = auth.authorize(principal,busta);
			return status;
		}
		catch (Exception e) {
			this.messageSecurityContext.getLog().error("Errore durante il check di autorizzazione (MessageSecurity): " + e.getMessage(),e);
			throw new Exception("Errore durante il check di autorizzazione (MessageSecurity): " + e.getMessage());
		}
		
	}

}
