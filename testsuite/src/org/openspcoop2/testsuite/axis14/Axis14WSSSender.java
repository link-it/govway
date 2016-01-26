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


package org.openspcoop2.testsuite.axis14;

import java.util.Hashtable;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.ws.axis.security.WSDoAllSender;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;


/**
 * Classe per la gestione della WS-Security (WSDoAllSender).
 *
 * @author Spadafora Marcello <Ma.Spadafora@finsiel.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class Axis14WSSSender {

    /** Eventuale messaggio di errore avvenuto durante il processo di validazione */
    private String msgErrore;
    /** Eventuale codice di errore avvenuto durante il processo di validazione  */
    private CodiceErroreCooperazione codiceErrore;
    /** Contiene il message Context */
    private Axis14WSSBaseUtils baseUtilsWSS;

    public Axis14WSSSender(Hashtable<?,?> wssProperties,Axis14WSSBaseUtils baseWSS) {
	this.baseUtilsWSS = baseWSS;
        this.baseUtilsWSS.setMessageContext(wssProperties);	
    }

    public boolean process(Message axisMessage) {
	try{
	    if (this.baseUtilsWSS.getMessageContext() != null) {
		this.baseUtilsWSS.getMessageContext().setCurrentMessage(axisMessage);
		WSDoAllSender sender = new WSDoAllSender();
		sender.invoke(this.baseUtilsWSS.getMessageContext());
	    }
	}catch (AxisFault af) {
	    this.msgErrore =  "Generatosi errore durante il processamento WS-Security(Sender) [code: "
		+af.getFaultCode()+"]\n"+af.getFaultString();
	    this.codiceErrore = CodiceErroreCooperazione.SICUREZZA;
	    this.baseUtilsWSS.getLog().error(this.msgErrore,af);
	    return false;
	} catch (Exception e) {
		e.printStackTrace(System.out);
	    this.msgErrore =  "Generatosi errore durante il processamento WS-Security(Sender): "+e.getMessage();
	    this.codiceErrore = CodiceErroreCooperazione.SICUREZZA;
	    this.baseUtilsWSS.getLog().error(this.msgErrore,e);
	    return false;
	}
	
	return true;
    }

    /**
     * In caso di avvenuto errore durante il processo di validazione, 
     * questo metodo ritorna il motivo dell'errore.
     *
     * @return motivo dell'errore (se avvenuto).
     * 
     */
    public String getMsgErrore(){
	return this.msgErrore;
    }

    /**
     * In caso di avvenuto errore, questo metodo ritorna il codice dell'errore.
     *
     * @return codice dell'errore (se avvenuto).
     * 
     */
    public CodiceErroreCooperazione getCodiceErrore(){
    	return this.codiceErrore;
    }
}
