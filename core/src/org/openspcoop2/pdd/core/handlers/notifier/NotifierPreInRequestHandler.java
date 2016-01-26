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
package org.openspcoop2.pdd.core.handlers.notifier;

import java.util.List;

import org.openspcoop2.pdd.config.ClassNameProperties;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.core.handlers.PreInRequestHandler;
import org.openspcoop2.utils.resources.Loader;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.io.notifier.StreamingHandler;

/**
 * NotifierPreInRequestHandler
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierPreInRequestHandler implements PreInRequestHandler {

	@Override
	public void invoke(PreInRequestContext context) throws HandlerException {
		
		try{
		
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
			ClassNameProperties classNameProperties = ClassNameProperties.getInstance();
			String notifierInputStreamCallback = properties.getNotifierInputStreamCallback();
			if(notifierInputStreamCallback!=null){
				INotifierCallback notifierCallback = (INotifierCallback) Loader.getInstance().newInstance(classNameProperties.getNotifierCallback(notifierInputStreamCallback));
				if(notifierCallback.enableNotifierInputStream(NotifierType.PRE_IN_REQUEST, context)){
					
					NotifierInputStreamParams notifierInputStreamParams = new NotifierInputStreamParams();
					
					// Notify
					NotifierResult notifierResult = notifierCallback.notify(NotifierType.PRE_IN_REQUEST, context);
					
					notifierInputStreamParams.setBufferEnabled(NotifierBufferState.ENABLE.equals(notifierResult.getBufferState()));
					
					List<StreamingHandler> streamingHandlers = notifierResult.getStreamingHandlers();
					if(streamingHandlers!=null && streamingHandlers.size()>0){
						for (StreamingHandler streamingHandler : streamingHandlers) {
							notifierInputStreamParams.addStreamingHandler(streamingHandler);
						}
					}
					
					notifierInputStreamParams.setThrowStreamingHandlerException(notifierCallback.throwStreamingHandlerException(NotifierType.PRE_IN_REQUEST, context));
				
					notifierInputStreamParams.setLog(context.getLogCore());
					
					context.setNotifierInputStreamParams(notifierInputStreamParams);
				}
			}
			
		}catch(Exception e){
			throw new HandlerException(e.getMessage(),e);
		}
	}

}
