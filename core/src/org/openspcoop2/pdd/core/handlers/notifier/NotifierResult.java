/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.handlers.notifier;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.notifier.StreamingHandler;
import org.openspcoop2.utils.io.notifier.unblocked.AbstractStreamingHandler;
import org.openspcoop2.utils.io.notifier.unblocked.PipedInputOutputStreamHandler;

/**
 * NotifierResult
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierResult {

	/**
	 * StreamingHandler da aggiungere
	 */
	List<StreamingHandler> streamingHandlers = new ArrayList<StreamingHandler>();
	
	/**
	 * Nuovo stato del buffer
	 */
	NotifierBufferState bufferState = NotifierBufferState.UNMODIFIED;


	
	public List<StreamingHandler> getStreamingHandlers() {
		return this.streamingHandlers;
	}

	public void setStreamingHandlers(List<StreamingHandler> streamingHandlers) {
		this.streamingHandlers = streamingHandlers;
	}
	public void addStreamingHandler(StreamingHandler streamingHandler) throws UtilsException{
		this.streamingHandlers.add(streamingHandler);
	}
	public void addStreamingHandler(String idStreamingHandler,AbstractStreamingHandler stremingHandler,Logger log) throws UtilsException{
		try{
			PipedInputOutputStreamHandler handler = new PipedInputOutputStreamHandler(idStreamingHandler, stremingHandler, log);
			this.streamingHandlers.add(handler);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	public NotifierBufferState getBufferState() {
		return this.bufferState;
	}

	public void setBufferState(NotifierBufferState bufferState) {
		this.bufferState = bufferState;
	}
	
}
