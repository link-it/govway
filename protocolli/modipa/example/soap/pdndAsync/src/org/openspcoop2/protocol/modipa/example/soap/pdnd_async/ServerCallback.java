/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.example.soap.pdnd_async;

import jakarta.xml.ws.Endpoint;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Server dell'API di callback SOAP di esempio (fruitore)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerCallback {

	public static void main(String[] args) throws Exception {
		
		LoggerWrapperFactory.setDefaultConsoleLogConfiguration(Level.ERROR);
		
		ExampleProperties properties = new ExampleProperties("Server.properties");
		String address = properties.getRequired("callback.endpoint");
		
		System.out.println("Avvio server dell'API di callback sull'indirizzo: "+address);
		Endpoint.publish(address, new CallbackProvider(properties.getOptional("header.conversationId", "GovWay-Conversation-ID")));
		System.out.println("Server ready...");
	}
}
