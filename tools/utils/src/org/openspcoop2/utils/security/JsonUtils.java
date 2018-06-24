/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

package org.openspcoop2.utils.security;

import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.rs.security.jose.jwe.JweException;
import org.apache.cxf.rs.security.jose.jws.JwsException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;

/**
 * JsonUtils
 * 
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JsonUtils {

	public static Message newMessage() {
		Message m = new MessageImpl();
		Exchange ex = new ExchangeImpl();
		m.setExchange(ex);
		return m;
	}
	
	public static boolean SIGNATURE = true;
	public static boolean ENCRYPT = false;
	public static boolean DECRYPT = false;
	public static boolean SENDER = true;
	public static boolean RECEIVER = false;
	public static UtilsException convert(JOSERepresentation representation, boolean signature, boolean roleSender, Throwable t) {
		
		StringBuffer bf = new StringBuffer();
		if(representation!=null) {
			bf.append("[").append(representation.name()).append("] ");
		}
		
		if(t instanceof JwsException) {
			JwsException exc = (JwsException) t;
			if(exc.getError()==null) {
				if(roleSender) {
					bf.append("Signature failure");
				}
				else {
					bf.append("Signature verification failure");
				}
			}			
			else {
				bf.append(exc.getError().name());
			}
			if(exc.getMessage()==null && exc.getCause()==null && exc.getLocalizedMessage()==null) {
				return new UtilsException(bf.toString(),t);
			}
		}
		else if(t instanceof JweException) {
			JweException exc = (JweException) t;
			if(exc.getError()==null) {
				if(roleSender) {
					bf.append("Encrypt failure");
				}
				else {
					bf.append("Decrypt failure");
				}
			}			
			else {
				bf.append(exc.getError().name());
			}
			if(exc.getMessage()==null && exc.getCause()==null && exc.getLocalizedMessage()==null) {
				return new UtilsException(bf.toString(),t);
			}
		}
		else if(signature) {
			if(roleSender) {
				bf.append("Signature failure");
			}
			else {
				bf.append("Signature verification failure");
			}
		}
		else {
			if(roleSender) {
				bf.append("Encrypt failure");
			}
			else {
				bf.append("Decrypt failure");
			}
		}
				
		String msg = Utilities.getInnerNotEmptyMessageException(t).getMessage();
		
		Throwable innerExc = Utilities.getLastInnerException(t);
		String innerMsg = null;
		if(innerExc!=null){
			innerMsg = innerExc.getMessage();
		}
		
		String messaggio = null;
		if(msg!=null && !"".equals(msg) && !"null".equals(msg)) {
			messaggio = new String(msg);
			if(innerMsg!=null && !"".equals(innerMsg) && !"null".equals(innerMsg) && !innerMsg.equals(msg)) {
				messaggio = messaggio + " ; " + innerMsg;
			}
		}
		else{
			if(innerMsg!=null && !"".equals(innerMsg) && !"null".equals(innerMsg)) {
				messaggio = innerMsg;
			}
		}
		
		if(messaggio!=null) {
			bf.append(": ");
			bf.append(messaggio);
		}
		return new UtilsException(bf.toString(),t);
	}
}
