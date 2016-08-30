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
package org.openspcoop2.utils.io.notifier.unblocked;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;

/**
 * PipedUnblockedStream
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PipedUnblockedStream extends InputStream {

	// Classe che implementa un pipe dove la scrittura non e' bloccante, mentre la lettura si.
	// Se non ci sono dati da leggere la read rimane in wait.
	
	protected Logger log = null;
	private long sizeBuffer;
	
	public PipedUnblockedStream(Logger log, long sizeBuffer){
		this.log = log;
		// In memoria esistono 2 buffer, 
		// - [bytesReceived] uno che contiene i bytes gia' consolidati pronti a essere consumati
		// - [bout] buffer utilizzato per scrivere i dati
		// Quindi puo' succedere che entrambi i buffer siano "pieni". La dimensione massima richiesta in memoria viene quindi divisa per 2.
		// NOTA: La dimensione di ogni buffer potra' essere this.sizeBuffer + eventualmente ;a dimensione del byte[] fornita con l'ultima write che ha superato il controllo di waitSpaceAvailable 
		this.sizeBuffer = sizeBuffer / 2; 
	}
	
	private ByteArrayOutputStream bout = new ByteArrayOutputStream();
	private byte [] bytesReceived = null;
	private int indexNextByteReceivedForRead = -1;
	
	private boolean stop = false;
	
	private static final int ITERAZIONI_WAIT = 128;
	
	// INPUT STREAM
	
	private void readWaitBytes() throws IOException{
		int i = 0;
		while(this.stop==false && this.bout.size()==0 && i<ITERAZIONI_WAIT){
			try{
				Thread.sleep((i+1));
				i = i + i;
			}catch(Exception e){}
		}
		if(i>=ITERAZIONI_WAIT){
			throw new IOException("Timeout, no bytes available for read");
		}
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		
		return this.read(b, 0, b.length);
		
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		
		//System.out.println("########### READ b["+b.length+"] off["+off+"] len["+len+"] .....");
		//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] .....");
		
		if(this.bytesReceived==null){
			if(this.stop){		
				if(this.bout==null){
					//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] STOP BOUT NULL return -1");
					return -1;
				}
				if(this.bout.size()<=0){
					this.bout.close();
					this.bout = null;
					//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] STOP BOUT SIZE<0 return -1");
					return -1;
				}
			}
			else{
				if(this.bout.size()==0){
					//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] WAIT BYTES ...");
					readWaitBytes();
				}
			}
		}
		
		//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] BYTES AVAILABLE ...");
		
		if(this.bytesReceived==null){
			if(this.stop){			
				if(this.bout==null){
					//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] BYTES AVAILABLE RETURN -1");
					return -1;
				}
				if(this.bout.size()<=0){
					this.bout.close();
					this.bout = null;
					//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] BYTES AVAILABLE RETURN -1 (CASO B)");
					return -1;
				}
			}
		}
		
				
		
		
		if(this.bytesReceived==null){
			//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] BYTES AVAILABLE FROM PRECEDENT BUFFERING IS NULL ...");
			//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC ...");
			synchronized (this.bout) {
				//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A1 ...");
				this.bout.flush();
				//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A2 ...");
				this.bytesReceived = this.bout.toByteArray();
				//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A3 ...");
				this.indexNextByteReceivedForRead = 0;
				//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A4 ...");
				//System.out.println("########### RESET ATTUALE DIMENSIONE IN MEMORIA ["+this.bytesReceived.length+"]");
				this.bout.reset();
			}
			//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC OK");
		}
		
		int bytesAvailableForRead = this.bytesReceived.length - this.indexNextByteReceivedForRead;
		if(bytesAvailableForRead==len){			
			for (int i = 0; i < len; i++) {
				b[off+i]=this.bytesReceived[this.indexNextByteReceivedForRead];
				this.indexNextByteReceivedForRead++;
			}	
			this.bytesReceived = null;
			this.indexNextByteReceivedForRead = -1;
			//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] NEXT INDEX["+this.indexNextByteReceivedForRead+"] RETURN LETTI("+len+") EXIT A");
			return len;
		}
		else if(bytesAvailableForRead>len){
			int i = 0;
			for (i = 0; i < len; i++) {
				b[off+i]=this.bytesReceived[this.indexNextByteReceivedForRead];
				this.indexNextByteReceivedForRead++;
			}
			//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] NEXT INDEX["+this.indexNextByteReceivedForRead+"] RETURN LETTI("+len+") EXIT B");
			return len;
		}
		else{
			for (int i = 0; i < bytesAvailableForRead; i++) {
				b[off+i]=this.bytesReceived[this.indexNextByteReceivedForRead];
				this.indexNextByteReceivedForRead++;
			}
			this.bytesReceived = null;
			this.indexNextByteReceivedForRead = -1;
			//this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] NEXT INDEX["+this.indexNextByteReceivedForRead+"] RETURN LETTI("+bytesAvailableForRead+") EXIT C");
			return bytesAvailableForRead;
		}
		
		
		
//		this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC ...");
//		byte[] buffer = null;
//		synchronized (this.bout) {
//			
//			this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A1 ...");
//			
//			this.bout.flush();
//			
//			this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A2 ...");
//			
//			buffer = this.bout.toByteArray();
//			
//			this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A3 ...");
//			
//			this.bout.reset();
//			
//			this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A4 ...");
//			
//			// Se il buffer possiede piu' bytes di quanti richiesti, vengono risalvati quelli che non verranno ritornati con questa chiamata.
//			if(buffer.length>len){
//				this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A5 ...");
//				this.bout.write(buffer, len, buffer.length-len);
//			}
//			this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC A6 ...");
//		}
//			 
//		this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] SYNC OK");
//		
//		if(buffer.length==len){			
//			for (int i = 0; i < buffer.length; i++) {
//				b[off+i]=buffer[i];
//			}		
//			this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] EXIT A");
//			return b.length;
//		}
//		else if(buffer.length>len){
//			int i = 0;
//			for (i = 0; i < len; i++) {
//				b[off+i]=buffer[i];
//			}
//			this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] EXIT B");
//			return b.length;
//		}
//		else{
//			for (int i = 0; i < buffer.length; i++) {
//				b[off+i]=buffer[i];
//			}
//			this.log.debug("########### READ b["+b.length+"] off["+off+"] len["+len+"] EXIT C");
//			return buffer.length;
//		}
//			
		
	}

	@Override
	public int read() throws IOException {
		
		byte[]b = new byte[1];
		return this.read(b);
		
	}

	
	@Override
	public void close() throws IOException {
		if(this.stop==false){
			if(! (this.bout.size()>0) ){
				this.bout.close();
				this.bout = null;
			}
			this.stop = true;
		}
	}

	
	// ALIMENTAZIONE
	
	private void writeWaitEmptyBuffer() throws IOException{
		int i = 0;
		while(this.stop==false && this.bout.size()>0 && i<ITERAZIONI_WAIT){
			try{
				Thread.sleep((i+1));
				i = i + i;
			}catch(Exception e){}
		}
		if(i>=ITERAZIONI_WAIT){
			throw new IOException("Timeout, no buffer available for write");
		}
	}
	
	public void write(byte b) throws IOException{
		
		//this.log.debug("########### WRITE byte .....");
		
		if(this.bout==null){
			throw new IOException("Stream already closed");
		}
		
		if(this.bout.size()>this.sizeBuffer){
			this.writeWaitEmptyBuffer();
		}
		
		//this.log.debug("########### WRITE byte SYNC ...");
		synchronized (this.bout) {
			this.bout.write(b);
			//this.log.debug("########### WRITE byte SYNC OK");
			//System.out.println("########### WRITE byte SYNC OK ADD[1] SIZE_ATTUALE["+this.bout.size()+"]");
		}

	}
	
	public void write(byte [] b) throws IOException{
		
		//this.log.debug("########### WRITE byte ["+b.length+"] .....");
		
		if(this.bout==null){
			throw new IOException("Stream already closed");
		}
		
		if(this.bout.size()>this.sizeBuffer){
			this.writeWaitEmptyBuffer();
		}
		
		//this.log.debug("########### WRITE byte ["+b.length+"] SYNC ...");
		synchronized (this.bout) {
			this.bout.write(b);
			//this.log.debug("########### WRITE byte ["+b.length+"] SYNC OK");
			//System.out.println("########### WRITE byte SYNC OK ADD["+b.length+"] SIZE_ATTUALE["+this.bout.size()+"]");
		}

	}

}
