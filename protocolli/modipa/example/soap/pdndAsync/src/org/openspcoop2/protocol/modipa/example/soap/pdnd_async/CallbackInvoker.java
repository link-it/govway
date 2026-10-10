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

/**
 * Invocazione manuale della callback (fase callback_invocation) tramite la fruizione GovWay dell'API di callback.
 * 
 * Utilizza la stessa configurazione dell'invocazione automatica (Server.properties, proprietà 'eservice.callback.*'):
 * l'identificativo dell'interazione (GovWay-Conversation-ID restituito al client nella fase start_interaction) e il numero di entità
 * vengono forniti come argomenti.
 * 
 * Argomenti: conversationId [entityNumber] (default entityNumber: 10)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CallbackInvoker {

	public static void main(String[] args) throws Exception {
		String conversationId = args.length>0 && !args[0].startsWith("$") ? args[0] : null;
		if(conversationId==null || "".equals(conversationId.trim())) {
			throw new IllegalArgumentException("Indicare l'identificativo dell'interazione (-DconversationId=...)");
		}
		int entityNumber = args.length>1 && !args[1].startsWith("$") ? Integer.parseInt(args[1].trim()) : 10;
		
		ExampleProperties properties = new ExampleProperties("Server.properties");
		CallbackNotifier notifier = new CallbackNotifier(properties);
		InteractionStore.Interaction interaction = new InteractionStore.Interaction(conversationId.trim(), "invocazione manuale", entityNumber, null);
		int status = notifier.invoke(interaction);
		System.exit(status>=200 && status<=299 ? 0 : 1);
	}
}
