/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.services.connector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.ConnectorOutMessage;
import org.openspcoop2.pdd.services.connector.messages.HttpServletConnectorAsyncInMessage;
import org.openspcoop2.pdd.services.connector.messages.HttpServletConnectorAsyncOutMessage;
import org.openspcoop2.pdd.services.connector.messages.HttpServletConnectorInMessage;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.service.IRicezioneService;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.utils.TimeoutIOException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedStream;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**
 * AbstractRicezioneConnectorAsync
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */



public abstract class AbstractRicezioneConnectorAsync {

	protected abstract IDService getIdService();
	protected abstract String getIdModulo();
	
	protected abstract AbstractErrorGenerator getErrorGenerator(Logger logCore, RequestInfo requestInfo) throws Exception;
	protected abstract void doError(RequestInfo requestInfo, AbstractErrorGenerator generatoreErrore, 
			ErroreIntegrazione erroreIntegrazione, IntegrationFunctionError integrationFunctionError,
			Throwable t, HttpServletResponse res,  Logger logCore);
	protected abstract ConnectorDispatcherErrorInfo doError(RequestInfo requestInfo, AbstractErrorGenerator generatoreErrore, 
			ErroreIntegrazione erroreIntegrazione, IntegrationFunctionError integrationFunctionError, 
			Throwable t, ParseException parseException,
			ConnectorOutMessage res, Logger log, boolean clientError) throws ConnectorException;
	
	protected abstract void emitTransaction(Logger logCore, ConnectorInMessage req, PdDContext pddContext, Date dataAccettazioneRichiesta, ConnectorDispatcherInfo info);
	
	protected abstract long getTimeout();
	
	protected abstract IRicezioneService newRicezioneService(AbstractErrorGenerator errorGenerator);
	
	public void doEngine(RequestInfo requestInfo, 
			HttpServletRequest req, HttpServletResponse res, HttpRequestMethod method) throws ServletException, IOException {
	
		Date dataAccettazioneRichiesta = DateManager.getDate();
		
		
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		
		AbstractErrorGenerator generatoreErrore = null;
		try{
			generatoreErrore = getErrorGenerator(logCore, requestInfo);
		}catch(Exception e){
			String msg = "Inizializzazione Generatore Errore fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			logCore.error(msg,e);
			doError(requestInfo, generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_501_PDD_NON_INIZIALIZZATA), 
					IntegrationFunctionError.GOVWAY_NOT_INITIALIZED, e, res, logCore);
			return;
		}
		
		AsyncContext ac = req.startAsync();
		
		
		HttpServletConnectorAsyncInMessage httpIn = null;
		try{
			httpIn = new HttpServletConnectorAsyncInMessage(requestInfo, req, getIdService(), getIdModulo());
		}catch(Exception e){
			ConnectorUtils.getErrorLog().error("HttpServletConnectorInMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
		
		IProtocolFactory<?> protocolFactory = null;
		try{
			protocolFactory = httpIn.getProtocolFactory();
		}catch(Throwable e){}
		
		HttpServletConnectorAsyncOutMessage httpOut = null;
		try{
			httpOut = new HttpServletConnectorAsyncOutMessage(protocolFactory, ac, getIdService(), getIdModulo());
		}catch(Exception e){
			ConnectorUtils.getErrorLog().error("HttpServletConnectorOutMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		boolean stream = op2Properties.isNIOConfig_asyncServer_doStream();
		int dimensione_buffer =  op2Properties.getNIOConfig_asyncServer_buffer();
		long timeout = getTimeout();
		boolean applicativeThreadPool = op2Properties.isNIOConfig_asyncServer_applicativeThreadPoolEnabled();
		
		boolean TEST_SENZA_LISTENER = false;
		if(TEST_SENZA_LISTENER) {
			//System.out.println("INVOCAZIONE DIRETTA");
			try{
				IRicezioneService ricezioneService = newRicezioneService(generatoreErrore);
				ricezioneService.process(httpIn, httpOut, dataAccettazioneRichiesta, ConnectorCostanti.ASYNC);
				return;
			}catch(Throwable e){
				System.out.println("ERRORE TEST_SENZA_LISTENER");
				e.printStackTrace(System.out);
			}
		}
		
		req.getInputStream().setReadListener( (new ReadListener() {
			private ServletInputStream is = null;
			private Logger log;
			private AbstractErrorGenerator generatoreErrore;
			private HttpServletConnectorAsyncInMessage httpIn;
			private HttpServletConnectorAsyncOutMessage httpOut;
			
			private boolean applicativeThreadPool;
			
			private boolean stream;
			private ByteArrayOutputStream os = null; // soluzione che bufferizza tutta la richiesta
			private PipedUnblockedStream pipe; // soluzione stream
			
			public ReadListener init(Logger log, ServletInputStream is, boolean stream, int sizeBuffer,
					boolean applicativeThreadPool,
					AbstractErrorGenerator generatoreErrore, 
					HttpServletConnectorAsyncInMessage httpIn, HttpServletConnectorAsyncOutMessage httpOut ) {
				this.is = is;
				this.log = log;
				this.applicativeThreadPool = applicativeThreadPool;
				this.generatoreErrore = generatoreErrore;
				this.httpIn = httpIn;
				this.httpOut = httpOut;
				
				this.stream = stream;
				if(stream) {
					this.pipe = new PipedUnblockedStream(log, sizeBuffer);
					this.pipe.setSource("Request");
					this.httpIn.updateInputStream(this.pipe);
				}
				else {
					this.os = new ByteArrayOutputStream();
				}
				return this;
			}

			@Override
			public void onError(Throwable t) {
				String msg = "Avvenuto errore durante la lettura della richiesta: "+Utilities.readFirstErrorValidMessageFromException(t);
				this.log.error(msg,t);
				try {
					ConnectorDispatcherErrorInfo cInfo = doError(requestInfo, this.generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_509_READ_REQUEST_MSG),
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, t, null, this.httpOut, this.log, ConnectorDispatcherUtils.GENERAL_ERROR);
					emitTransaction(this.log, this.httpIn, null, dataAccettazioneRichiesta, cInfo);
				}catch(Exception e) {
					this.log.error("Gestione errore fallita: "+e.getMessage(),e);
				}finally {
					ac.complete();
				}
			}

			@Override
			public void onDataAvailable() throws IOException {
		        int len = -1;
		        byte b[] = new byte[Utilities.DIMENSIONE_BUFFER];
		        while ( this.is.isReady() && (len = this.is.read(b)) != -1) {
		        	if(this.stream) {
		        		this.pipe.write(b, 0, len);
		        	}
		        	else {
		        		this.os.write(b, 0, len);
		        	}
		        }
			}

			@Override
			public void onAllDataRead() throws IOException {
				
				if(this.stream) {
					this.pipe.close();
				}
				else {
					this.os.flush();
					this.os.close();
					this.is.close();
				
					// Avvio un thread su cui poi chiamare un wait / notify in fase di consegna NIO.
					IRicezioneService ricezioneService = newRicezioneService(this.generatoreErrore);
					this.httpIn.updateInputStream(new ByteArrayInputStream(this.os.toByteArray()));	
					if(this.applicativeThreadPool) {
						
						Runnable runnable = new Runnable() {
							
							private IRicezioneService ricezioneService;
							private HttpServletConnectorInMessage httpIn;
							private HttpServletConnectorAsyncOutMessage httpOut;
							
							public Runnable init(IRicezioneService ricezioneService,
									HttpServletConnectorInMessage httpIn,
									HttpServletConnectorAsyncOutMessage httpOut) {
								this.ricezioneService = ricezioneService;
								this.httpIn = httpIn;
								this.httpOut = httpOut;
								return this;
							}
							
							@Override
							public void run() {
								try{
									this.ricezioneService.process(this.httpIn, this.httpOut, dataAccettazioneRichiesta, ConnectorCostanti.ASYNC);
								}catch(Throwable e){
									ConnectorUtils.getErrorLog().error(getIdService().getValue()+".process error: "+e.getMessage(),e);
								}
								
							}
						}.init(ricezioneService, this.httpIn, this.httpOut);
						AsyncThreadPool.execute(runnable);
					}
					else {
						try{
							ricezioneService.process(this.httpIn, this.httpOut, dataAccettazioneRichiesta, ConnectorCostanti.ASYNC);
						}catch(Throwable e){
							ConnectorUtils.getErrorLog().error(getIdService().getValue()+".process error: "+e.getMessage(),e);
							throw new IOException(getIdService().getValue()+".process error: "+e.getMessage(),e);
						}
					}
				}
				
			}
		}).init(logCore, req.getInputStream(), stream, dimensione_buffer, 
				applicativeThreadPool,
				generatoreErrore,
				httpIn, httpOut) );

		ac.addListener(new AsyncListener() {
			
			private HttpServletConnectorAsyncOutMessage httpOut;
			
			public AsyncListener init(HttpServletConnectorAsyncOutMessage httpOut) {
				this.httpOut = httpOut;
				return this;
			}
			
			@Override
			public void onTimeout(AsyncEvent event) throws IOException {
				if(event.getThrowable()!=null) {
					this.httpOut.setNioException(new TimeoutIOException("Timeout '"+timeout+"' exceeded: "+event.getThrowable().getMessage(),event.getThrowable()));
				}
				else {
					this.httpOut.setNioException(new TimeoutIOException("Timeout '"+timeout+"' exceeded"));
				}
				//((HttpServletResponse)ac.getResponse()).setStatus(500);
			}

			@Override
			public void onStartAsync(AsyncEvent event) throws IOException {
			}

			@Override
			public void onError(AsyncEvent event) throws IOException {
				if(event.getThrowable()!=null) {
					this.httpOut.setNioException(new ConnectorException("Async IO error: "+event.getThrowable().getMessage(),event.getThrowable()));
				}
				else {
					this.httpOut.setNioException(new ConnectorException("Async IO error"));
				}
				//((HttpServletResponse)ac.getResponse()).setStatus(500);
			}

			@Override
			public void onComplete(AsyncEvent event) throws IOException {
				//((HttpServletResponse)ac.getResponse()).setStatus(500);
			}
		}.init(httpOut));
		
		ac.setTimeout(timeout);
			
		if(stream) {
			IRicezioneService ricezioneService = newRicezioneService(generatoreErrore);
			if(applicativeThreadPool) {
				Runnable runnable = new Runnable() {
				
					private IRicezioneService ricezioneService;
					private HttpServletConnectorInMessage httpIn;
					private HttpServletConnectorAsyncOutMessage httpOut;
					
					public Runnable init(IRicezioneService ricezioneService,
							HttpServletConnectorInMessage httpIn,
							HttpServletConnectorAsyncOutMessage httpOut) {
						this.ricezioneService = ricezioneService;
						this.httpIn = httpIn;
						this.httpOut = httpOut;
						return this;
					}
					
					@Override
					public void run() {
						try{
							this.ricezioneService.process(this.httpIn, this.httpOut, dataAccettazioneRichiesta, ConnectorCostanti.ASYNC);
						}catch(Throwable e){
							ConnectorUtils.getErrorLog().error("NIO RicezioneBuste.process error: "+e.getMessage(),e);
						}
						
					}
				}.init(ricezioneService, httpIn, httpOut);
				AsyncThreadPool.execute(runnable);
			}
			else {
				try{
					ricezioneService.process(httpIn, httpOut, dataAccettazioneRichiesta, ConnectorCostanti.ASYNC);
				}catch(Throwable e){
					String msg = "Inizializzazione Generatore Errore fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
					logCore.error(msg,e);
					doError(requestInfo, generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO), 
							IntegrationFunctionError.INTERNAL_REQUEST_ERROR, e, res, logCore);
					return;
				}
			}
		}
			
	}

	

}
