/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.messages.HttpServletConnectorAsyncOutMessage;
import org.openspcoop2.pdd.services.connector.messages.HttpServletConnectorInMessage;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.pdd.services.service.RicezioneContenutiApplicativiService;
import org.openspcoop2.pdd.services.service.RicezioneContenutiApplicativiServiceUtils;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedStream;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.slf4j.Logger;

/**
 * RicezioneContenutiApplicativiConnector
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */



public class RicezioneContenutiApplicativiConnectorAsync {


	/** Variabile che indica il Nome del modulo dell'architettura di OpenSPCoop rappresentato da questa classe */
	public final static IDService ID_SERVICE = IDService.PORTA_DELEGATA_NIO;
	public final static String ID_MODULO = ID_SERVICE.getValue();

	
	public void doEngine(RequestInfo requestInfo, 
			HttpServletRequest req, HttpServletResponse res, HttpRequestMethod method) throws ServletException, IOException {
	
		Date dataAccettazioneRichiesta = DateManager.getDate();
		
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		
		RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore = null;
		try{
			generatoreErrore = 
					new RicezioneContenutiApplicativiInternalErrorGenerator(logCore, ID_MODULO, requestInfo);
		}catch(Exception e){
			String msg = "Inizializzazione Generatore Errore fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
			logCore.error(msg,e);
			ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
					ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO), 
					IntegrationError.INTERNAL_ERROR, e, res, logCore);
			return;
		}
		
		AsyncContext ac = req.startAsync();
		
		
		HttpServletConnectorInMessage httpIn = null;
		try{
			httpIn = new HttpServletConnectorInMessage(requestInfo, req, ID_SERVICE, ID_MODULO);
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
			httpOut = new HttpServletConnectorAsyncOutMessage(protocolFactory, ac, ID_SERVICE, ID_MODULO);
		}catch(Exception e){
			ConnectorUtils.getErrorLog().error("HttpServletConnectorOutMessage init error: "+e.getMessage(),e);
			throw new ServletException(e.getMessage(),e);
		}
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		boolean stream = op2Properties.isNIOConfig_asyncServer_doStream();
		int dimensione_buffer =  op2Properties.getNIOConfig_asyncServer_buffer();
		int timeout = (int) op2Properties.getNodeReceiverTimeoutRicezioneContenutiApplicativi();
		boolean applicativeThreadPool = op2Properties.isNIOConfig_asyncServer_applicativeThreadPoolEnabled();
		
		req.getInputStream().setReadListener( (new ReadListener() {
			private ServletInputStream is = null;
			private Logger log;
			private RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore;
			private HttpServletConnectorInMessage httpIn;
			private HttpServletConnectorAsyncOutMessage httpOut;
			
			private boolean applicativeThreadPool;
			
			private boolean stream;
			private ByteArrayOutputStream os = null; // soluzione che bufferizza tutta la richiesta
			private PipedUnblockedStream pipe; // soluzione stream
			
			public ReadListener init(Logger log, ServletInputStream is, boolean stream, int sizeBuffer,
					boolean applicativeThreadPool,
					RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrore, 
					HttpServletConnectorInMessage httpIn, HttpServletConnectorAsyncOutMessage httpOut ) {
				this.is = is;
				this.log = log;
				this.applicativeThreadPool = applicativeThreadPool;
				this.generatoreErrore = generatoreErrore;
				this.httpIn = httpIn;
				this.httpOut = httpOut;
				
				this.stream = stream;
				if(stream) {
					this.pipe = new PipedUnblockedStream(log, sizeBuffer);
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
					ConnectorDispatcherErrorInfo cInfo = ConnectorDispatcherUtils.doError(requestInfo, this.generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_509_READ_REQUEST_MSG), 
							IntegrationError.INTERNAL_ERROR, t, null, this.httpOut, this.log, ConnectorDispatcherUtils.GENERAL_ERROR);
					RicezioneContenutiApplicativiServiceUtils.emitTransaction(this.log, this.httpIn, null, dataAccettazioneRichiesta, cInfo);
				}catch(Exception e) {
					this.log.error("Gestione errore fallita: "+e.getMessage(),e);
				}finally {
					ac.complete();
				}
			}

			@Override
			public void onDataAvailable() throws IOException {
		        int len = -1;
		        byte b[] = new byte[1024];
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
					RicezioneContenutiApplicativiService ricezioneContenutiApplicativi = new RicezioneContenutiApplicativiService(this.generatoreErrore);
					this.httpIn.updateInputStream(new ByteArrayInputStream(this.os.toByteArray()));	
					if(this.applicativeThreadPool) {
						Runnable runnable = new Runnable() {
							
							private RicezioneContenutiApplicativiService ricezioneContenutiApplicativi;
							private HttpServletConnectorInMessage httpIn;
							private HttpServletConnectorAsyncOutMessage httpOut;
							
							public Runnable init(RicezioneContenutiApplicativiService ricezioneContenutiApplicativi,
									HttpServletConnectorInMessage httpIn,
									HttpServletConnectorAsyncOutMessage httpOut) {
								this.ricezioneContenutiApplicativi = ricezioneContenutiApplicativi;
								this.httpIn = httpIn;
								this.httpOut = httpOut;
								return this;
							}
							
							@Override
							public void run() {
								try{
									this.ricezioneContenutiApplicativi.process(this.httpIn, this.httpOut, dataAccettazioneRichiesta, ConnectorCostanti.ASYNC);
								}catch(Throwable e){
									ConnectorUtils.getErrorLog().error("NIO RicezioneContenutiApplicativi.process error: "+e.getMessage(),e);
								}
								
							}
						}.init(ricezioneContenutiApplicativi, this.httpIn, this.httpOut);
						AsyncThreadPool.execute(runnable);
					}
					else {
						try{
							ricezioneContenutiApplicativi.process(this.httpIn, this.httpOut, dataAccettazioneRichiesta, ConnectorCostanti.ASYNC);
						}catch(Throwable e){
							ConnectorUtils.getErrorLog().error("NIO RicezioneContenutiApplicativi.process error: "+e.getMessage(),e);
							throw new IOException("NIO RicezioneContenutiApplicativi.process error: "+e.getMessage(),e);
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
					this.httpOut.setNioException(new TimeoutConnectorException("Timeout '"+timeout+"' exceeded: "+event.getThrowable().getMessage(),event.getThrowable()));
				}
				else {
					this.httpOut.setNioException(new TimeoutConnectorException("Timeout '"+timeout+"' exceeded"));
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
					this.httpOut.setNioException(new TimeoutConnectorException("Async IO error"));
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
			RicezioneContenutiApplicativiService ricezioneContenutiApplicativi = new RicezioneContenutiApplicativiService(generatoreErrore);
			if(applicativeThreadPool) {
				Runnable runnable = new Runnable() {
				
					private RicezioneContenutiApplicativiService ricezioneContenutiApplicativi;
					private HttpServletConnectorInMessage httpIn;
					private HttpServletConnectorAsyncOutMessage httpOut;
					
					public Runnable init(RicezioneContenutiApplicativiService ricezioneContenutiApplicativi,
							HttpServletConnectorInMessage httpIn,
							HttpServletConnectorAsyncOutMessage httpOut) {
						this.ricezioneContenutiApplicativi = ricezioneContenutiApplicativi;
						this.httpIn = httpIn;
						this.httpOut = httpOut;
						return this;
					}
					
					@Override
					public void run() {
						try{
							this.ricezioneContenutiApplicativi.process(this.httpIn, this.httpOut, dataAccettazioneRichiesta, ConnectorCostanti.ASYNC);
						}catch(Throwable e){
							ConnectorUtils.getErrorLog().error("NIO RicezioneContenutiApplicativi.process error: "+e.getMessage(),e);
						}
						
					}
				}.init(ricezioneContenutiApplicativi, httpIn, httpOut);
				AsyncThreadPool.execute(runnable);
			}
			else {
				try{
					ricezioneContenutiApplicativi.process(httpIn, httpOut, dataAccettazioneRichiesta, ConnectorCostanti.ASYNC);
				}catch(Throwable e){
					String msg = "Inizializzazione Generatore Errore fallita: "+Utilities.readFirstErrorValidMessageFromException(e);
					logCore.error(msg,e);
					ConnectorDispatcherUtils.doError(requestInfo, generatoreErrore, // il metodo doError gestisce il generatoreErrore a null
							ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(msg,CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO), 
							IntegrationError.INTERNAL_ERROR, e, res, logCore);
					return;
				}
			}
		}
			
	}

	

}
