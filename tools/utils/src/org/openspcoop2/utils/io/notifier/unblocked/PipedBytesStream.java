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
package org.openspcoop2.utils.io.notifier.unblocked;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.openspcoop2.utils.Utilities;
import org.slf4j.Logger;

/**
 * PipedBytesStream
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PipedBytesStream extends IPipedUnblockedStream {
	private static final int MAX_QUEUE = 2;

	protected Logger log = null;
	private long sizeBuffer;
	private int timeoutMs;
	
	@Override
	public void init(Logger log, long sizeBuffer, int timeoutMs, String source) {
		this.log = log;
		// In memoria esistono 2 buffer, 
		// - [bytesReceived] uno che contiene i bytes gia' consolidati pronti a essere consumati
		// - [bytesReading] buffer utilizzato per scrivere i dati
		// Quindi puo' succedere che entrambi i buffer siano "pieni". La dimensione massima richiesta in memoria viene quindi divisa per 2.
		// NOTA: La dimensione di ogni buffer potra' essere this.sizeBuffer + eventualmente ;a dimensione del byte[] fornita con l'ultima write che ha superato il controllo di waitSpaceAvailable 
		if(sizeBuffer<=0) {
			sizeBuffer = Utilities.DIMENSIONE_BUFFER;
		}
		this.sizeBuffer = sizeBuffer / 2; 
		this.timeoutMs = timeoutMs;
		this.source = source;
		initReadingBuffer();
	}
	@Override
	public void setTimeout(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}
	
	//private final Integer semaphore = 1;
	private final org.openspcoop2.utils.Semaphore lockPIPE = new org.openspcoop2.utils.Semaphore("PipedBytesStream");
	private byte [] bytesReading = null;
	private volatile int indexNextByteReceivedForWrite = 0;
	private List<byte[]> chunkList = new ArrayList<byte[]>();
	private byte [] bytesReceived = null;
	private volatile int indexNextByteReceivedForRead = -1;
	
	private boolean stop = false;

	private boolean useThreadSleep = false;
	private static final int ITERAZIONI_WAIT = 128;
	private CompletableFuture<Boolean> asyncReadTask = null;
	private CompletableFuture<Boolean> asyncWriteTask = null;

	private void initReadingBuffer() {
		this.bytesReading = new byte[ (int)this.sizeBuffer ];
		this.indexNextByteReceivedForWrite = 0;
	}

	private String source = null;
	public String getPrefixSource() {
		return this.source!=null ? "["+this.source+"] " : "";
	}

	private boolean readBytesPending() {
		return (this.indexNextByteReceivedForWrite > 0 || this.chunkList.size() > 0);
	}

	// INPUT STREAM
	

	private void readWaitBytes() throws IOException{
		try {
			if(this.useThreadSleep) {
				int i = 0;
				while(this.stop==false && !readBytesPending() && i<ITERAZIONI_WAIT){
					Utilities.sleep((i+1));
					i = i + i;
				}
				if(i>=ITERAZIONI_WAIT){
					throw new IOException(getPrefixSource()+"Timeout, no bytes available for read");
				}
			} else {
				boolean wait = false;
				this.lockPIPE.acquireThrowRuntime("readWaitBytes");
				try {
					if(this.stop==false && !readBytesPending()) {
						this.asyncReadTask = new CompletableFuture<Boolean>();
						wait = true;
					}
				}finally {
					this.lockPIPE.release("readWaitBytes");
				}
				
				if(wait) {
					try {
	//					System.out.println("["+this.source+"] ASPETTO READ...");
						if(this.timeoutMs>0) {
							this.asyncReadTask.get(this.timeoutMs,TimeUnit.MILLISECONDS );
						}
						else {
							this.asyncReadTask.get();
						}
	//					System.out.println("["+this.source+"] READ OK");
					}catch(Exception timeout) {
						throw new IOException(getPrefixSource()+"Timeout, no bytes available for read: "+timeout.getMessage(),timeout);
					}
				}
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
	public int read(byte[] b) throws IOException {
		
		return this.read(b, 0, b.length);
		
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		
		try {
			
	//		System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] .....");
			//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] .....");
			
			if(this.bytesReceived==null){
				if (this.stop) {		
					if(!readBytesPending()){
	//					System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] STOP BOUT NULL return -1");
						if(this.asyncWriteTask!=null) {
	//						System.out.println("["+this.source+"] READ for WRITE COMPLETE 1");
							this.asyncWriteTask.complete(true);
						}
						return -1;
					}
				} else {
					if(!readBytesPending()){
	//					System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] WAIT BYTES ...");
						readWaitBytes();
						if (!readBytesPending()) {
							// Viene reso null dal metodo close() che puo' essere chiamato mentre la read e' in corso
	//						System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] WAIT BYTES FOUND BOUT NULL ON EXIT");
							if(this.asyncWriteTask!=null) {
	//							System.out.println("["+this.source+"] READ for WRITE COMPLETE 3");
								this.asyncWriteTask.complete(true);
							}
							return -1;
						}
					}
				}
			}
			
			//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] BYTES AVAILABLE ...");
			
			if(this.bytesReceived==null){
				if(this.stop){
					if(!readBytesPending()){
	//					System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] BYTES AVAILABLE RETURN -1");
						if(this.asyncWriteTask!=null) {
	//						System.out.println("["+this.source+"] READ for WRITE COMPLETE 4");
							this.asyncWriteTask.complete(true);
						}
						return -1;
					}
				}
			}
			
					
			
			
			if(this.bytesReceived==null){
	//			System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] BYTES AVAILABLE FROM PRECEDENT BUFFERING IS NULL ...");
	//			System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC ...");
				//synchronized (this.semaphore) {
				this.lockPIPE.acquireThrowRuntime("read");
				try {
	//				System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] CHUNKS ... " + this.chunkList.size() );
	//				System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] Next-Byte for write ... " + this.indexNextByteReceivedForWrite );
					if ( this.chunkList.size() > 0 ) {
						this.bytesReceived = this.chunkList.remove(0);
					} else
					if ( this.indexNextByteReceivedForWrite > 0 ) {
						this.bytesReceived = new byte[ this.indexNextByteReceivedForWrite ];
						System.arraycopy( this.bytesReading, 0, this.bytesReceived, 0, this.indexNextByteReceivedForWrite );
						this.indexNextByteReceivedForWrite = 0;
					}
					this.indexNextByteReceivedForRead = 0;
					if(this.asyncWriteTask!=null) {
	//					System.out.println("["+this.source+"] READ for WRITE COMPLETE IN SEMAPHORE");
						this.asyncWriteTask.complete(true);
					}
				} finally {
					this.lockPIPE.release("read");
				}
	//			System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC OK");
			}
			
			int bytesAvailableForRead = this.bytesReceived.length - this.indexNextByteReceivedForRead;
			if ( bytesAvailableForRead == len ) {
				System.arraycopy( this.bytesReceived, this.indexNextByteReceivedForRead, b, off, bytesAvailableForRead );
				this.bytesReceived = null;
				this.indexNextByteReceivedForRead = -1;
	//			System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] NEXT INDEX["+this.indexNextByteReceivedForRead+"] RETURN LETTI("+len+") EXIT A");
				return len;
			} else
			if ( bytesAvailableForRead > len ) {
				System.arraycopy( this.bytesReceived, this.indexNextByteReceivedForRead, b, off, len );
				this.indexNextByteReceivedForRead += len;
	//			System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] NEXT INDEX["+this.indexNextByteReceivedForRead+"] RETURN LETTI("+len+") EXIT B");
				return len;
			} else {
				System.arraycopy( this.bytesReceived, this.indexNextByteReceivedForRead, b, off, bytesAvailableForRead );
				this.bytesReceived = null;
				this.indexNextByteReceivedForRead = -1;
	//			System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] NEXT INDEX["+this.indexNextByteReceivedForRead+"] RETURN LETTI("+bytesAvailableForRead+") EXIT C");
				return bytesAvailableForRead;
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
	public int read() throws IOException {
		byte[]b = new byte[1];
		int len = this.read(b);
		if ( len == 1 )
			return b[0];
		if ( len == -1 )
			return -1;
		throw new IOException( "Cannot read single byte" );
	}

	
	@Override
	public void close() throws IOException {
		try {
			if ( this.stop == false ) {
	
				this.lockPIPE.acquireThrowRuntime("close");
				try {
					if ( this.indexNextByteReceivedForWrite > 0 ) {
						byte[] lastChunk = new byte[ this.indexNextByteReceivedForWrite ];
						System.arraycopy( this.bytesReading, 0, lastChunk, 0, this.indexNextByteReceivedForWrite );
						this.chunkList.add( lastChunk );
						this.indexNextByteReceivedForWrite = 0;
					}
					this.bytesReading = null;
					this.stop = true;
				}finally{
					this.lockPIPE.release("close");
				}
			}
			if(this.asyncWriteTask!=null) {
	//			System.out.println("["+this.source+"] CLOSE for WRITE COMPLETE");
				this.asyncWriteTask.complete(true);
			}
			if(this.asyncReadTask!=null) {
	//			System.out.println("["+this.source+"] CLOSE for READ COMPLETE");
				this.asyncReadTask.complete(true);
			}
		}
		catch(Throwable t) {
			throw new IOException(t.getMessage(),t);
		}
	}

	
	// ALIMENTAZIONE
	
	private void writeWaitEmptyBuffer() throws IOException{
		try {
			if ( this.useThreadSleep ) {
				if ( readBytesPending() && this.chunkList.size() >= MAX_QUEUE ) {
					int i = 0;
					while ( this.stop == false && i < ITERAZIONI_WAIT ) {
						Utilities.sleep((i+1));
						i = i + i;
					}
					if ( i >= ITERAZIONI_WAIT ) {
						throw new IOException(getPrefixSource()+"Timeout, no buffer available for write");
					}
				}
			} else {
				boolean wait = false;
				this.lockPIPE.acquireThrowRuntime("writeWaitEmptyBuffer");
				try {
					if ( readBytesPending() && this.chunkList.size() >= MAX_QUEUE ) {
						if ( this.stop == false ) {
							this.asyncWriteTask = new CompletableFuture<Boolean>();
							wait = true;
						}
					}
				}finally{
					this.lockPIPE.release("writeWaitEmptyBuffer");
				}
				if(wait) {
					try {
	//					System.out.println("["+this.source+"] ASPETTO WRITE ...");
						if(this.timeoutMs>0) {
							this.asyncWriteTask.get(this.timeoutMs,TimeUnit.MILLISECONDS );
						}
						else {
							this.asyncWriteTask.get();
						}
	//					System.out.println("["+this.source+"] WRITE OK");
					}catch(Exception timeout) {
						throw new IOException(getPrefixSource()+"Timeout, no bytes available for read: "+timeout.getMessage(),timeout);
					}
				}
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
	public void write(byte b) throws IOException{
		
		try {
		
			//this.log.debug("########### WRITE byte .....");
			
			if ( this.bytesReading == null ) {
				throw new IOException(getPrefixSource()+"Stream already closed");
			}
			
			this.writeWaitEmptyBuffer();
					
			//this.log.debug("########### WRITE byte SYNC ...");
			//synchronized (this.semaphore) {
			this.lockPIPE.acquireThrowRuntime("write(b)");
			try {
				this.bytesReading[ this.indexNextByteReceivedForWrite++ ] = b;
				if ( this.indexNextByteReceivedForWrite == this.bytesReading.length ) {
					this.chunkList.add( this.bytesReading );
					initReadingBuffer();
				}
				if(this.asyncReadTask!=null) {
	//				System.out.println("["+this.source+"] WRITE for READ COMPLETE 1");
					this.asyncReadTask.complete(true);
				}
				//this.log.debug("########### WRITE byte SYNC OK");
	//			System.out.println("########### WRITE byte SYNC OK ADD[1] SIZE_ATTUALE["+ this.indexNextByteReceivedForWrite + " - " + this.chunkList.size()+"]");
			}finally{
				this.lockPIPE.release("write(b)");
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
		
			//this.log.debug("########### WRITE byte ["+b.length+"] .....");
			
			if ( this.bytesReading == null ) {
				throw new IOException(getPrefixSource()+"Stream already closed");
			}
			
			this.writeWaitEmptyBuffer();
					
			//this.log.debug("########### WRITE byte ["+b.length+"] SYNC ...");
			//synchronized (this.semaphore) {
			this.lockPIPE.acquireThrowRuntime("write(b[])");
			try {
				int offset = 0;
				int bytesNum = b.length;
				while ( bytesNum > 0 ) {
					int freeSpace = this.bytesReading.length - this.indexNextByteReceivedForWrite;
					int chunkLen = ( bytesNum <= freeSpace ? bytesNum : freeSpace );
					System.arraycopy( b, offset, this.bytesReading, this.indexNextByteReceivedForWrite, chunkLen );
					this.indexNextByteReceivedForWrite += chunkLen;
					if ( this.indexNextByteReceivedForWrite == this.bytesReading.length ) {
						this.chunkList.add( this.bytesReading );
						initReadingBuffer();
					}
					bytesNum -= chunkLen;
					offset += chunkLen;
				}
				if(this.asyncReadTask!=null) {
	//				System.out.println("["+this.source+"] WRITE for READ COMPLETE 2");
					this.asyncReadTask.complete(true);
				}
				//this.log.debug("########### WRITE byte ["+b.length+"] SYNC OK");
	//			System.out.println("########### WRITE byte SYNC OK ADD["+b.length+"] SIZE_ATTUALE["+ this.indexNextByteReceivedForWrite + " - " + this.chunkList.size()+"]");
			}finally {
				this.lockPIPE.release("write(b[])");
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
		
			//this.log.debug("########### WRITE byte ["+b.length+"] off:"+off+" len:"+len+" .....");
			
			if ( this.bytesReading == null ) {
				throw new IOException(getPrefixSource()+"Stream already closed");
			}
			
			this.writeWaitEmptyBuffer();
					
			//this.log.debug("########### WRITE byte ["+b.length+"] off:"+off+" len:"+len+" SYNC ...");
			//synchronized (this.semaphore) {
			this.lockPIPE.acquireThrowRuntime("write(b[],off,len)");
			try {
				int offset = off;
				int bytesNum = len;
				while ( bytesNum > 0 ) {
					int freeSpace = this.bytesReading.length - this.indexNextByteReceivedForWrite;
					int chunkLen = ( bytesNum <= freeSpace ? bytesNum : freeSpace );
					System.arraycopy( b, offset, this.bytesReading, this.indexNextByteReceivedForWrite, chunkLen );
					this.indexNextByteReceivedForWrite += chunkLen;
					if ( this.indexNextByteReceivedForWrite == this.bytesReading.length ) {
						this.chunkList.add( this.bytesReading );
						initReadingBuffer();
					}
					bytesNum -= chunkLen;
					offset += chunkLen;
				}
				if(this.asyncReadTask!=null) {
	//				System.out.println("["+this.source+"] WRITE for READ COMPLETE 3");
					this.asyncReadTask.complete(true);
				}
				//this.log.debug("########### WRITE byte ["+b.length+"] off:"+off+" len:"+len+" SYNC OK");
	//			System.out.println("########### WRITE byte SYNC OK ADD["+b.length+"] SIZE_ATTUALE["+ this.indexNextByteReceivedForWrite + " - " + this.chunkList.size()+"]");
			}finally {
				this.lockPIPE.release("write(b[],off,len)");
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
