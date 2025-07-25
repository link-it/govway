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
package org.openspcoop2.utils.io.notifier.unblocked;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

/**
 * PipedUnblockedStream
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PipedUnblockedStream extends IPipedUnblockedStream {

	protected Logger log = null;
	private long sizeBuffer;
	private int timeoutMs;
	
	private static final String NO_BYTES_AVAILABLE_READ = "Timeout, no bytes available for read: ";
	private static final String STREAM_ALREADY_CLOSED = "Stream already closed";
	
	@Override
	public void init(Logger log, long sizeBuffer, int timeoutMs, String source) {
		this.log = log;
		// In memoria esistono 2 buffer, 
		// - [bytesReceived] uno che contiene i bytes gia' consolidati pronti a essere consumati
		// - [bout] buffer utilizzato per scrivere i dati
		// Quindi puo' succedere che entrambi i buffer siano "pieni". La dimensione massima richiesta in memoria viene quindi divisa per 2.
		// NOTA: La dimensione di ogni buffer potra' essere this.sizeBuffer + eventualmente ;a dimensione del byte[] fornita con l'ultima write che ha superato il controllo di waitSpaceAvailable 
		if(sizeBuffer<=0) {
			sizeBuffer = Utilities.DIMENSIONE_BUFFER;
		}
		this.sizeBuffer = sizeBuffer / 2; 
		this.timeoutMs = timeoutMs;
		this.source = source;
	}
	@Override
	public void setTimeout(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}
	
	private final org.openspcoop2.utils.Semaphore lockPIPE = new org.openspcoop2.utils.Semaphore("PipedUnblockedStream");
	private ByteArrayOutputStream bout = new ByteArrayOutputStream();
	private byte [] bytesReceived = null;
	private int indexNextByteReceivedForRead = -1;
	
	private boolean stop = false;

	private boolean useThreadSleep = false;
	private static final int ITERAZIONI_WAIT = 128;
	private CompletableFuture<Boolean> asyncReadTask = null;
	private CompletableFuture<Boolean> asyncWriteTask = null;
	
	private String source = null;
	public String getPrefixSource() {
		return this.source!=null ? "["+this.source+"] " : "";
	}
	
	// INPUT STREAM
	

	private void readWaitBytes() throws IOException{
		try {
			if(this.useThreadSleep) {
				int i = 0;
				while(!this.stop && this.bout!=null && this.bout.size()==0 && i<ITERAZIONI_WAIT){
					Utilities.sleep((i+1));
					i = i + i;
				}
				if(i>=ITERAZIONI_WAIT){
					throw new IOException(getPrefixSource()+"Timeout, no bytes available for read");
				}
			}
			else {
				boolean wait = false;
				SemaphoreLock lock = this.lockPIPE.acquireThrowRuntime("readWaitBytes");
				try {
					if(!this.stop && this.bout!=null && this.bout.size()==0) {
						this.asyncReadTask = new CompletableFuture<>();
						wait = true;
					}
				}finally {
					this.lockPIPE.release(lock, "readWaitBytes");
				}
				
				if(wait) {
					asyncReadGet();
				}
			}
		}
		catch(IOException io) {
			throw io;
		}
		catch(Throwable t) {
			if(t instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			throw new IOException(t.getMessage(),t);
		}
	}
	private void asyncReadGet() throws IOException {
		try {
			/**System.out.println("["+this.source+"] ASPETTO READ...");*/
			if(this.timeoutMs>0) {
				this.asyncReadTask.get(this.timeoutMs,TimeUnit.MILLISECONDS );
			}
			else {
				this.asyncReadTask.get();
			}
			/**System.out.println("["+this.source+"] READ OK");*/
		}catch(InterruptedException timeout) {
			Thread.currentThread().interrupt();
			throw new IOException(getPrefixSource()+NO_BYTES_AVAILABLE_READ+timeout.getMessage(),timeout);
		}
		catch(Exception timeout) {
			throw new IOException(getPrefixSource()+NO_BYTES_AVAILABLE_READ+timeout.getMessage(),timeout);
		}
	}
	
	
	@Override
	public int read(byte[] b) throws IOException {
		
		return this.read(b, 0, b.length);
		
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		
		try {
		
			/**System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] .....");*/
			/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] .....");*/
			
			if(this.bytesReceived==null){
				if(this.stop){		
					if(this.bout==null){
						/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] STOP BOUT NULL return -1");*/
						if(this.asyncWriteTask!=null) {
							/**System.out.println("["+this.source+"] READ for WRITE COMPLETE 1");*/
							this.asyncWriteTask.complete(true);
						}
						return -1;
					}
					if(this.bout.size()<=0){
						this.bout.close();
						this.bout = null;
						/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] STOP BOUT SIZE<0 return -1");*/
						if(this.asyncWriteTask!=null) {
							/**System.out.println("["+this.source+"] READ for WRITE COMPLETE 2");*/
							this.asyncWriteTask.complete(true);
						}
						return -1;
					}
				}
				else{
					if(this.bout.size()==0){
						/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] WAIT BYTES ...");*/
						readWaitBytes();
						if(this.bout==null) {
							// Viene reso null dal metodo close() che puo' essere chiamato mentre la read e' in corso
							/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] WAIT BYTES FOUND BOUT NULL ON EXIT");*/
							if(this.asyncWriteTask!=null) {
								/**System.out.println("["+this.source+"] READ for WRITE COMPLETE 3");*/
								this.asyncWriteTask.complete(true);
							}
							return -1;
						}
					}
				}
			}
			
			/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] BYTES AVAILABLE ...");*/
			
			/**if(this.bytesReceived==null &&
				this.stop){
				// garantita dal codice sopra essere not null quando si entra nell'if this.bytesReceived==null && this.stop
				if(this.bout==null){
					//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] BYTES AVAILABLE RETURN -1");
					if(this.asyncWriteTask!=null) {
						//System.out.println("["+this.source+"] READ for WRITE COMPLETE 4");
						this.asyncWriteTask.complete(true);
					}
					return -1;
				}
			}*/
			if(this.bytesReceived==null &&
					this.stop &&
					this.bout.size()<=0){
				this.bout.close();
				this.bout = null;
				/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] BYTES AVAILABLE RETURN -1 (CASO B)");*/
				if(this.asyncWriteTask!=null) {
					/**System.out.println("["+this.source+"] READ for WRITE COMPLETE 5");*/
					this.asyncWriteTask.complete(true);
				}
				return -1;
			}
			
					
			
			
			if(this.bytesReceived==null){
				/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] BYTES AVAILABLE FROM PRECEDENT BUFFERING IS NULL ...");*/
				/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC ...");*/
				SemaphoreLock lock = this.lockPIPE.acquireThrowRuntime("read");
				try {
					/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A1 ...");*/
					this.bout.flush();
					/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A2 ...");*/
					this.bytesReceived = this.bout.toByteArray();
					/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A3 ...");*/
					this.indexNextByteReceivedForRead = 0;
					/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A4 ...");*/
					/**System.out.println("########### RESET ATTUALE DIMENSIONE IN MEMORIA ["+this.bytesReceived.length+"]");*/
					this.bout.reset();
					if(this.asyncWriteTask!=null) {
						/**System.out.println("["+this.source+"] READ for WRITE COMPLETE IN SEMAPHORE");*/
						this.asyncWriteTask.complete(true);
					}
				}finally {
					this.lockPIPE.release(lock, "read");
				}
				/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC OK");*/
			}
			
			int bytesAvailableForRead = this.bytesReceived.length - this.indexNextByteReceivedForRead;
			if(bytesAvailableForRead==len){			
				for (int i = 0; i < len; i++) {
					b[off+i]=this.bytesReceived[this.indexNextByteReceivedForRead];
					this.indexNextByteReceivedForRead++;
				}	
				this.bytesReceived = null;
				this.indexNextByteReceivedForRead = -1;
				/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] NEXT INDEX["+this.indexNextByteReceivedForRead+"] RETURN LETTI("+len+") EXIT A");*/
				return len;
			}
			else if(bytesAvailableForRead>len){
				int i = 0;
				for (i = 0; i < len; i++) {
					b[off+i]=this.bytesReceived[this.indexNextByteReceivedForRead];
					this.indexNextByteReceivedForRead++;
				}
				/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] NEXT INDEX["+this.indexNextByteReceivedForRead+"] RETURN LETTI("+len+") EXIT B");*/
				return len;
			}
			else{
				for (int i = 0; i < bytesAvailableForRead; i++) {
					b[off+i]=this.bytesReceived[this.indexNextByteReceivedForRead];
					this.indexNextByteReceivedForRead++;
				}
				this.bytesReceived = null;
				this.indexNextByteReceivedForRead = -1;
				/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] NEXT INDEX["+this.indexNextByteReceivedForRead+"] RETURN LETTI("+bytesAvailableForRead+") EXIT C");*/
				return bytesAvailableForRead;
			}
			
			
			
			/**this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC ...");
			byte[] buffer = null;
			synchronized (this.semaphore) {
				
				this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A1 ...");
				
				this.bout.flush();
				
				this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A2 ...");
				
				buffer = this.bout.toByteArray();
				
				this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A3 ...");
				
				this.bout.reset();
				
				this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A4 ...");
				
				// Se il buffer possiede piu' bytes di quanti richiesti, vengono risalvati quelli che non verranno ritornati con questa chiamata.
				if(buffer.length>len){
					this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A5 ...");
					this.bout.write(buffer, len, buffer.length-len);
				}
				this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A6 ...");
			}
				 
			this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC OK");
			
			if(buffer.length==len){			
				for (int i = 0; i < buffer.length; i++) {
					b[off+i]=buffer[i];
				}		
				this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] EXIT A");
				return b.length;
			}
			else if(buffer.length>len){
				int i = 0;
				for (i = 0; i < len; i++) {
					b[off+i]=buffer[i];
				}
				this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] EXIT B");
				return b.length;
			}
			else{
				for (int i = 0; i < buffer.length; i++) {
					b[off+i]=buffer[i];
				}
				this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] EXIT C");
				return buffer.length;
			}*/
				
		}
		catch(IOException io) {
			throw io;
		}
		catch(Throwable t) {
			throw new IOException(t.getMessage(),t);
		}
	}

	@Override
	public int read() throws IOException {
		byte[]b = new byte[1];
		int len = this.read(b);
		if ( len == 1 )
			return b[0] & 0xFF;
		if ( len == -1 )
			return -1;
		throw new IOException( "Cannot read single byte" );
	}

	
	@Override
	public void close() throws IOException {	
		try {
			SemaphoreLock lock = this.lockPIPE.acquireThrowRuntime("close");
			try {
				if(!this.stop){
					if(this.bout.size()<=0 ){
						this.bout.flush();
						this.bout.close();
						//this.bout = null; se si annulla, la read lo trovera' null
					}
					this.stop = true;
				}
			}finally{
				this.lockPIPE.release(lock, "close");
			}
			if(this.asyncWriteTask!=null) {
				/**System.out.println("["+this.source+"] CLOSE for WRITE COMPLETE");*/
				this.asyncWriteTask.complete(true);
			}
			if(this.asyncReadTask!=null) {
				/**System.out.println("["+this.source+"] CLOSE for READ COMPLETE");*/
				this.asyncReadTask.complete(true);
			}
			
		}
		catch(IOException io) {
			throw io;
		}
		catch(Throwable t) {
			throw new IOException(t.getMessage(),t);
		}
	}

	
	// ALIMENTAZIONE
	
	private void writeWaitEmptyBuffer() throws IOException{
		try {
			if(this.useThreadSleep) {
				if(this.bout.size()>this.sizeBuffer){
					int i = 0;
					while(!this.stop && this.bout.size()>0 && i<ITERAZIONI_WAIT){
						Utilities.sleep((i+1));
						i = i + i;
					}
					if(i>=ITERAZIONI_WAIT){
						throw new IOException(getPrefixSource()+"Timeout, no buffer available for write");
					}
				}
			}
			else {
				boolean wait = false;
				SemaphoreLock lock = this.lockPIPE.acquireThrowRuntime("writeWaitEmptyBuffer");
				try {
					if(this.bout.size()>this.sizeBuffer &&
						!this.stop && this.bout.size()>0 ) {
						this.asyncWriteTask = new CompletableFuture<>();
						wait = true;
					}
				}finally{
					this.lockPIPE.release(lock, "writeWaitEmptyBuffer");
				}
				if(wait) {
					asyncWriteGet();
				}
			}
		}
		catch(IOException io) {
			throw io;
		}
		catch(Throwable t) {
			if(t instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			throw new IOException(t.getMessage(),t);
		}
	}
	private void asyncWriteGet() throws IOException {
		try {
			/**System.out.println("["+this.source+"] ASPETTO WRITE ...");*/
			if(this.timeoutMs>0) {
				this.asyncWriteTask.get(this.timeoutMs,TimeUnit.MILLISECONDS );
			}
			else {
				this.asyncWriteTask.get();
			}
			/**System.out.println("["+this.source+"] WRITE OK");*/
		}catch(InterruptedException timeout) {
			Thread.currentThread().interrupt();
			throw new IOException(getPrefixSource()+NO_BYTES_AVAILABLE_READ+timeout.getMessage(),timeout);
		}
		catch(Exception timeout) {
			throw new IOException(getPrefixSource()+NO_BYTES_AVAILABLE_READ+timeout.getMessage(),timeout);
		}
	}
	
	
	@Override
	public void write(byte b) throws IOException{
		
		try {
		
			/**this.log.debug("########### WRITE byte .....");*/
			
			if(this.bout==null){
				throw new IOException(getPrefixSource()+STREAM_ALREADY_CLOSED);
			}
			
			this.writeWaitEmptyBuffer();
					
			/**this.log.debug("########### WRITE byte SYNC ...");*/
			SemaphoreLock lock = this.lockPIPE.acquireThrowRuntime("write(b)");
			try {
				this.bout.write(b);
				if(this.asyncReadTask!=null) {
					/**System.out.println("["+this.source+"] WRITE for READ COMPLETE 1");*/
					this.asyncReadTask.complete(true);
				}
				/**this.log.debug("########### WRITE byte SYNC OK");*/
				/**System.out.println("########### WRITE byte SYNC OK ADD[1] SIZE_ATTUALE["+this.bout.size()+"]");*/
			}finally{
				this.lockPIPE.release(lock, "write(b)");
			}
			
		}
		catch(IOException io) {
			throw io;
		}
		catch(Throwable t) {
			throw new IOException(t.getMessage(),t);
		}

	}
	
	@Override
	public void write(byte [] b) throws IOException{
		
		try {
		
			/**this.log.debug("########### WRITE byte ["+b.length+"] .....");*/
			
			if(this.bout==null){
				throw new IOException(getPrefixSource()+STREAM_ALREADY_CLOSED);
			}
			
			this.writeWaitEmptyBuffer();
					
			/**this.log.debug("########### WRITE byte ["+b.length+"] SYNC ...");*/
			SemaphoreLock lock = this.lockPIPE.acquireThrowRuntime("write(b[])");
			try {
				this.bout.write(b);
				if(this.asyncReadTask!=null) {
					/**System.out.println("["+this.source+"] WRITE for READ COMPLETE 2");*/
					this.asyncReadTask.complete(true);
				}
				/**this.log.debug("########### WRITE byte ["+b.length+"] SYNC OK");*/
				/**System.out.println("########### WRITE byte SYNC OK ADD["+b.length+"] SIZE_ATTUALE["+this.bout.size()+"]");*/
			}finally {
				this.lockPIPE.release(lock, "write(b[])");
			}
			
		}
		catch(IOException io) {
			throw io;
		}
		catch(Throwable t) {
			throw new IOException(t.getMessage(),t);
		}

	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException{
		
		try {
		
			/**this.log.debug("########### WRITE byte ["+b.length+"] off:"+off+" len:"+len+" .....");*/
			
			if(this.bout==null){
				throw new IOException(getPrefixSource()+STREAM_ALREADY_CLOSED);
			}
			
			this.writeWaitEmptyBuffer();
					
			/**this.log.debug("########### WRITE byte ["+b.length+"] off:"+off+" len:"+len+" SYNC ...");*/
			SemaphoreLock lock = this.lockPIPE.acquireThrowRuntime("write(b[],off,len)");
			try {
				this.bout.write(b, off, len);
				if(this.asyncReadTask!=null) {
					/**System.out.println("["+this.source+"] WRITE for READ COMPLETE 3");*/
					this.asyncReadTask.complete(true);
				}
				/**this.log.debug("########### WRITE byte ["+b.length+"] off:"+off+" len:"+len+" SYNC OK");*/
				/**System.out.println("########### WRITE byte SYNC OK ADD["+b.length+"] SIZE_ATTUALE["+this.bout.size()+"]");*/
			}finally {
				this.lockPIPE.release(lock, "write(b[],off,len)");
			}
			
		}
		catch(IOException io) {
			throw io;
		}
		catch(Throwable t) {
			throw new IOException(t.getMessage(),t);
		}

	}

}
