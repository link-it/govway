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
package org.openspcoop2.pdd.core.handlers.notifier;

import java.util.List;

import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.utils.io.notifier.NotifierInputStream;
import org.openspcoop2.utils.io.notifier.StreamingHandler;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.message.OpenSPCoop2Message;

/**
 * NotifierUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierUtilities {

	private static INotifierCallback notifierCallback = null;
	private static boolean notifierCallbackInitialized = false;
	private static synchronized void initNotifierCallback() throws Exception{
		if(notifierCallback==null){
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
			String notifierInputStreamCallback = properties.getNotifierInputStreamCallback();
			if(notifierInputStreamCallback!=null){
				ClassNameProperties classNameProperties = ClassNameProperties.getInstance();
				notifierCallback = (INotifierCallback) Loader.getInstance().newInstance(classNameProperties.getNotifierCallback(notifierInputStreamCallback));
			}
			notifierCallbackInitialized = true;
		}
	}
	private static INotifierCallback getNotifierCallback() throws Exception{
		if(notifierCallbackInitialized==false){
			initNotifierCallback();
		}
		return notifierCallback;
	}
	
	public static void updateNotifierState(OpenSPCoop2Message message, NotifierType notifierType, Object context) throws Exception{
		
		if(message!=null && message.getNotifierInputStream()!=null){
			
			NotifierInputStream nis = message.getNotifierInputStream();
			INotifierCallback notifierCallback = getNotifierCallback(); // viene inizializzato
			
			if(notifierCallback!=null){			
			
				// Notify
				NotifierResult notifierResult = notifierCallback.notify(notifierType, context);
				
				// Nuovi Streaming Handler (possono essere aggiunti in corsa solo se il buffer e' abilitato)
				if(nis.isBufferEnabled()){
				
					List<StreamingHandler> streamingHandlers = notifierResult.getStreamingHandlers();
					if(streamingHandlers!=null){
						for (StreamingHandler streamingHandler : streamingHandlers) {
							nis.addStreamingHandler(streamingHandler);
						}
					}
					
				}
									
				// Situazione Buffer
				if(nis.isBufferEnabled()){
						
					NotifierBufferState nuovoStatoBuffer = notifierResult.getBufferState();
					if(NotifierBufferState.DISABLE_AND_RELEASE_BUFFER_READED.equals(nuovoStatoBuffer)){
						// puo' succedere che durante la callback, sia stato chiamata il serializeAndConsume. Questo rilascia anche il buffer. 
						// E' quindi inutile rilasciarlo nuovamente.
						if(nis.isBufferEnabled()){
							nis.setOFFBuffering(true);
						}
					}
					else if(NotifierBufferState.DISABLE.equals(nuovoStatoBuffer)){
						// puo' succedere che durante la callback, sia stato chiamata il serializeAndConsume. Questo rilascia anche il buffer. 
						// E' quindi inutile rilasciarlo nuovamente.
						if(nis.isBufferEnabled()){
							nis.setOFFBuffering(false);
						}
					}
					
				}
			}
			
		}
		
	}
	
}
