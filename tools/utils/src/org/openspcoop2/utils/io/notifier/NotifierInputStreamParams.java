/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils.io.notifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.notifier.unblocked.AbstractStreamingHandler;
import org.openspcoop2.utils.io.notifier.unblocked.PipedInputOutputStreamHandler;



/**
 * NotifierInputStreamParams
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierInputStreamParams {

	private boolean bufferEnabled = false;
	private Map<String,StreamingHandler> streamingHandlers = new HashMap<String, StreamingHandler>();
	private List<String> streamingHandlersIds = new ArrayList<>(); // Per preservare l'ordine di inserimento
	private boolean throwStreamingHandlerException = true;
	private Logger log = null;
	
	public boolean isBufferEnabled() {
		return this.bufferEnabled;
	}
	public void setBufferEnabled(boolean bufferEnabled) {
		this.bufferEnabled = bufferEnabled;
	}

	public boolean isThrowStreamingHandlerException() {
		return this.throwStreamingHandlerException;
	}
	public void setThrowStreamingHandlerException(
			boolean throwStreamingHandlerException) {
		this.throwStreamingHandlerException = throwStreamingHandlerException;
	}
	
	public void addStreamingHandler(StreamingHandler streamingHandler) throws UtilsException{
		String idStreamingHandler = streamingHandler.getID();
		if(this.streamingHandlers.containsKey(idStreamingHandler)){
			throw new UtilsException("Streaming handler with id ["+idStreamingHandler+"] already registered");
		}
		else{
			this.streamingHandlers.put(idStreamingHandler, streamingHandler);
			this.streamingHandlersIds.add(idStreamingHandler);
		}
	}
	public void addStreamingHandler(String idStreamingHandler,AbstractStreamingHandler stremingHandler,Logger log) throws UtilsException{
		if(this.streamingHandlers.containsKey(idStreamingHandler)){
			throw new UtilsException("Streaming handler with id ["+idStreamingHandler+"] already registered");
		}
		else{
			try{
				PipedInputOutputStreamHandler handler = new PipedInputOutputStreamHandler(idStreamingHandler, stremingHandler, log);
				this.streamingHandlers.put(idStreamingHandler, handler);
				this.streamingHandlersIds.add(idStreamingHandler);
			}catch(Exception e){
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	public StreamingHandler getStreamingHandler(String idStreamingHandler) throws UtilsException{
		if(this.streamingHandlers.containsKey(idStreamingHandler)==false){
			throw new UtilsException("Streaming handler with id ["+idStreamingHandler+"] not exists");
		}
		else{
			return this.streamingHandlers.get(idStreamingHandler);
		}
	}
	public StreamingHandler removeStreamingHandler(String idStreamingHandler) throws UtilsException{
		if(this.streamingHandlers.containsKey(idStreamingHandler)==false){
			throw new UtilsException("Streaming handler with id ["+idStreamingHandler+"] not exists");
		}
		else{
			for (int i = 0; i < this.streamingHandlersIds.size(); i++) {
				if(this.streamingHandlersIds.get(i).equals(idStreamingHandler)){
					this.streamingHandlersIds.remove(i);
				}
			}
			return this.streamingHandlers.remove(idStreamingHandler);
		}
	}
	public List<String> getStreamingHandlerIds(){
		return this.streamingHandlersIds;
	}
	public int sizeStreamingHandlers(){
		return this.streamingHandlers.size();
	}
	public Map<String, StreamingHandler> getStreamingHandlers() {
		return this.streamingHandlers;
	}
	public void setStreamingHandlers(
			Map<String, StreamingHandler> streamingHandlers) {
		this.streamingHandlers = streamingHandlers;
	}
	
	public Logger getLog() {
		return this.log;
	}
	public void setLog(Logger log) {
		this.log = log;
	}

	
}
