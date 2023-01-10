/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.security.message.engine;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.SubErrorCodeSecurity;


/**
 * Classe per la gestione della Sicurezza (role:Receiver)
 *
 * @author Spadafora Marcello (Ma.Spadafora@finsiel.it)
 * @author Montebove Luciano (L.Montebove@finsiel.it)
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

	protected abstract boolean process(OpenSPCoop2Message message,Busta busta,org.openspcoop2.utils.Map<Object> ctx);
		
	
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

}
