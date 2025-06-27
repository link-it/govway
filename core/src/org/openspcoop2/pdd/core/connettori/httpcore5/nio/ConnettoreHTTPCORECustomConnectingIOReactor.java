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
package org.openspcoop2.pdd.core.connettori.httpcore5.nio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionListener;
import org.slf4j.Logger;

/**
 * ConnettoreHTTPCoreCustomConnectingIOReactor
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORECustomConnectingIOReactor extends org.apache.hc.core5.reactor.DefaultConnectingIOReactor {

	// classe riferita nel commento del ConnettoreHTTPCOREConnectionManager ma rimane da capire come si aggancia
	
	private ExecutorService executorService;
	private Logger log;
	
	public ConnettoreHTTPCORECustomConnectingIOReactor(IOEventHandlerFactory eventHandlerFactory,
			IOReactorConfig config, Callback<IOSession> sessionShutdownCallback, Logger log) {
		super(eventHandlerFactory, config, sessionShutdownCallback);
		init(log);
	}

	/**public ConnettoreHTTPCORECustomConnectingIOReactor(IOEventHandlerFactory eventHandlerFactory, IOReactorConfig config,
			ThreadFactory threadFactory, Decorator<IOSession> ioSession, Callback<Exception> callbackException, IOSessionListener sessionListener,
			Callback<IOSession> sessionCallback, Logger log) {
		super(eventHandlerFactory, config, threadFactory, ioSession, callbackException, sessionListener, sessionCallback);
		init(log);
	}*/

	public ConnettoreHTTPCORECustomConnectingIOReactor(IOEventHandlerFactory eventHandlerFactory, Logger log) {
		super(eventHandlerFactory);
		init(log);
	}
	
	private void init(Logger log) {
		int size = Runtime.getRuntime().availableProcessors();
		this.executorService = java.util.concurrent.Executors.newFixedThreadPool(size);
		this.log = log;
	}

	public void startReactor() {
        this.executorService.submit(() -> {
            try {
                this.start(); // Avvia il ciclo del reactor
            } catch (Exception e) {
            	this.log.error(e.getMessage(),e);
            }
        });
    }

    public void shutdownReactor() {
        try {
            this.executorService.shutdown();
        } catch (Exception e) {
        	this.log.error(e.getMessage(),e);
        }
    }
}
