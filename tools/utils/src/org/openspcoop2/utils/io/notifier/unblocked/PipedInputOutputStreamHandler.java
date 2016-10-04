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
package org.openspcoop2.utils.io.notifier.unblocked;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.openspcoop2.utils.io.notifier.StreamingHandler;

/**
 * PipedInputOutputStreamHandler
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PipedInputOutputStreamHandler implements StreamingHandler {

	// Thread che si occupa di consumare i bytes disponibili forniti all'handler
	// Implementa anche la gestione dello stream tra producer e consumer
	private AbstractStreamingHandler streamingHandler;
	
	// Submit ritornato dell'esecuzione del thread
	private Future<ResultStreamingHandler> submitThreadExecution;
	
	// Risultato dell'esecuzione del thread
	private boolean retrieveResult = false;
	private ResultStreamingHandler resultThreadExecution;
	private String errorMessageThreadExecution;
	private Throwable exceptionThreadExecution;
	
	// Log
	private Logger log;
	
	// Esecutore del Thread
	private ExecutorService executor;
	
	// Informazione se lo stream is closed
	private boolean closed;
	
	// ID classe streamingHandler
	private String idStreamingHandler;
	
	public PipedInputOutputStreamHandler(String id, AbstractStreamingHandler streamingHandler, Logger log) throws Exception {
		
		this.log = log;
		
		//inizializzo la pipe di stream attraverso la quale eseguire la validazione
		//this.out = new PipedOutputStream();
		this.streamingHandler = streamingHandler;
		this.idStreamingHandler = id;
		
		// Creo esecutore del thread
		this.executor = Executors.newSingleThreadExecutor();
		
		// Avvio il thread
		this.submitThreadExecution = this.executor.submit(this.streamingHandler);
	}
	
	
	
	// ** Metodi interfaccia StreamingHandler **
	
	@Override
	public String getID(){
		return this.idStreamingHandler;
	}
	
	@Override
	public void feed(byte b) throws IOException {
		byte[]buffer = new byte[1];
		this.feed(buffer);
	}

	@Override
	public void feed(byte[] b) throws IOException {
		
		//System.out.println("@@PIPE@@ feed ["+b.length+"] bytes ...");
		
		try{
			if(!this.closed) {
				// in.isPrematureEnd() ci dice se il processo attuato dallo streaming handler e' gia' terminato
				// b == -1 significa che lo stream è finito
				if(this.streamingHandler.isPrematureEnd()){
					//System.out.println("@@PIPE@@ feed ["+b.length+"] bytes: END");
					this.end();
				}else {
					//System.out.println("@@PIPE@@ feed ["+b.length+"] bytes: WRITE");
					this.streamingHandler.write(b);
				}
			}
		}catch(Throwable e){
			this.log.error("["+this.idStreamingHandler+"] feed error",e);
			throw new IOException(e.getMessage());
		}
	}
	
	@Override
	public void end() throws IOException {
		
		//System.out.println("@@PIPE@@ END");
		closeResources();
		
	}
		
	@Override
	public void closeResources() throws IOException {
		
		//System.out.println("@@PIPE@@ CLOSE RESOURCES ...");
		IOException ioException = null;
		
		if(!this.closed) {
		
			try {
			
				// Chiudo input stream
				this.streamingHandler.close();
				
				// Fermo esecutore del thread
				this.executor.shutdown();				
				try {
	
					 if (!this.executor.awaitTermination(20, TimeUnit.SECONDS)) {
						 this.executor.shutdownNow();
					 }
				 } catch (InterruptedException pCaught) {	
					 this.executor.shutdownNow();
					 Thread.currentThread().interrupt();	
				 }
								
			}catch(Throwable e){
				this.log.error("["+this.idStreamingHandler+"] end error",e);
				ioException = new IOException(e.getMessage());
			}
		}
		
		//System.out.println("@@PIPE@@ RETRIVE RESULT ...");
		retrieveResult();
		//System.out.println("@@PIPE@@ RETRIVED RESULT");
		
		//System.out.println("@@PIPE@@ Release resource...");
		this.releaseResource(); 
		//System.out.println("@@PIPE@@ Release resource ok");
		
		if(ioException!=null){
			throw ioException;
		}
		
	}

	private void retrieveResult() {
		
		if(this.retrieveResult == false){
			try {
								
				// Recupero risultato
				if(this.submitThreadExecution!=null){
					//System.out.println("@@PIPE@@ RETRIVE RESULT submitThreadExecution.get()...");
					this.resultThreadExecution = this.submitThreadExecution.get();
					//System.out.println("@@PIPE@@ RETRIVE RESULT submitThreadExecution.get() ok");
				}
				
				// Recupero eventuali errori non lanciati nella exception del metodo call() (Utile negli scenari in cui l'errore viene capito in streaming all'interno del thread)
				this.errorMessageThreadExecution = this.streamingHandler.getError();
				this.exceptionThreadExecution = this.streamingHandler.getException();
								
			}catch(Throwable e){
				
				// Errori generati da this.submitThreadExecution.get()
				// Cosi' facendo recupero eventuali errori lanciati nella exception del metodo call()
				this.errorMessageThreadExecution = e.getMessage();
				this.exceptionThreadExecution = e;
				
				this.log.error("["+this.idStreamingHandler+"] end error",e);
				// Non devo rilanciare gli errori, la logica e' rimandata a chi usa gli handler.
				// Devo solo salvare gli errori
				// throw new IOException(e.getMessage());
			}
			finally{
				// Risultati letti
				this.retrieveResult = true;
			}
		}
	}
	
	private void releaseResource() throws IOException{
		try {
			if(!this.closed) {
				//this.out = null;
				this.streamingHandler.close();
				this.streamingHandler = null;
				this.submitThreadExecution = null;
				this.closed = true;
			}
		}catch(Exception e){
			this.log.error("["+this.idStreamingHandler+"] closeResources error",e);
			throw new IOException(e.getMessage());
		}
	}
	
	
	
	
	
	// ** Metodi Recuperare informazioni elaborate dallo streaming handler **
	
	private void finalizeResult() throws IOException{
		// La close puo' non essere ancora stata effettuato:
		// - puo' succedere che non sia stato chiamato l'end perche' il messaggio e' piccolo e viene gestito in una unica feed.
		// - inoltre il metodo closeResources puo' essere invocato alla fine di tutto (es. dopo aver ritornato la risposta)
		if(!this.closed) {
			closeResources();
		}
	}
	
	public ResultStreamingHandler getResult() throws IOException {
		this.finalizeResult();
		return this.resultThreadExecution;
	}

	public String getError() throws IOException {
		this.finalizeResult();
		return this.errorMessageThreadExecution;
	}
	
	public Throwable getException() throws IOException {
		this.finalizeResult();
		return this.exceptionThreadExecution;
	}

	



}
