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

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.MessageSecurityDigestReader;
import org.openspcoop2.security.message.constants.SecurityType;
import org.openspcoop2.utils.digest.IDigestReader;


/**
 * MessageSecurityFactory
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageSecurityFactory {
	
	public static String messageSecurityContextImplClass = "org.openspcoop2.security.message.engine.MessageSecurityContext_impl";
	public static String messageSecurityDigestReaderImplClass = "org.openspcoop2.security.message.engine.MessageSecurityDigestReader_impl";
	
	public static void setMessageSecurityContextClassName(String messageSecurityContextImplClass){
		if(messageSecurityContextImplClass != null)
			MessageSecurityFactory.messageSecurityContextImplClass = messageSecurityContextImplClass;
	}

	public static void setMessageSecurityDigestReaderClassName(String messageSecurityDigestReader) {
		if(messageSecurityDigestReader != null)
			MessageSecurityFactory.messageSecurityDigestReaderImplClass = messageSecurityDigestReader;
	}
	
	public MessageSecurityContext getMessageSecurityContext(MessageSecurityContextParameters messageSecurityContextParameters) throws SecurityException {
		try{
			Constructor<?> constructor = Class.forName(MessageSecurityFactory.messageSecurityContextImplClass).getConstructor(MessageSecurityContextParameters.class);
			return (MessageSecurityContext) constructor.newInstance(messageSecurityContextParameters);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
	public static IDigestReader getMessageSecurityDigestReader(OpenSPCoop2MessageFactory messageFactory, SecurityType securityType, Logger log) throws SecurityException{
		try{
			Constructor<?> constructor = Class.forName(MessageSecurityFactory.messageSecurityDigestReaderImplClass).getConstructor(Logger.class);
			MessageSecurityDigestReader digestReader = (MessageSecurityDigestReader) constructor.newInstance(log);
			return digestReader.getDigestReader(messageFactory, securityType);
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
}
