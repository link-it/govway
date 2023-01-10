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

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.security.message.MessageSecurityContext;



/**
 * Classe per la gestione della Sicurezza (role:Sender)
 *
 * @author Spadafora Marcello (Ma.Spadafora@finsiel.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class MessageSecuritySender {
	
    //private boolean mtomEnabled; //???
	protected boolean mustUnderstandTrue = false;
    /** Eventuale messaggio di errore avvenuto durante il processo di validazione */
    protected String msgErrore;
    /** Eventuale codice di errore avvenuto durante il processo di validazione  */
    protected CodiceErroreCooperazione codiceErrore;
    /** Contiene il message Context */
    protected MessageSecurityContext messageSecurityContext;

    protected MessageSecuritySender(MessageSecurityContext securityContext) {
	    this.messageSecurityContext = securityContext;
    }

   

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

    /**
     * In caso di avvenuto errore, questo metodo ritorna il codice dell'errore.
     *
     * @return codice dell'errore (se avvenuto).
     * 
     */
    protected CodiceErroreCooperazione getCodiceErrore(){
    	return this.codiceErrore;
    }
    
    protected abstract boolean process(OpenSPCoop2Message message, org.openspcoop2.utils.Map<Object> ctx);
}





