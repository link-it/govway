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
package org.openspcoop2.utils.io.notifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;

import com.sun.xml.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.messaging.saaj.packaging.mime.internet.ParseException;

/**
 * NotifierInputStream
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class NotifierInputStream extends InputStream {

	
	/* ****** VARIABLE ******* */
	
	/**
	 * Original Input Stream
	 */
	private InputStream is;	
	/**
	 * Original Input Stream Consumed
	 */
	private boolean isOriginalInputStreamConsumed;
	
	/**
	 * Current position in this input stream
	 */
	private int currentReadPositionFromStream = 0;
	
	/**
	 * Content type of the stream
	 */
	private ContentType contentType = null;
	
	/**
	 * Indication if the buffer is enabled
	 */
	private boolean bufferEnabled = false;
	public boolean isBufferEnabled() {
		return this.bufferEnabled;
	}

	/**
	 * Buffer
	 */
	private ByteArrayOutputStream buffer = null;
	/**
	 * Bytes present in the stream after the completion of reading
	 */
	private byte[] contentCompleteReadedFromStream = null;	
	
	/**
	 * StreamingHandler
	 */
	private Hashtable<String,StreamingHandler> streamingHandlers = new Hashtable<String, StreamingHandler>();
	private List<String> streamingHandlersIds = new ArrayList<String>(); // Per preservare l'ordine di inserimento

	
	/**
	 * throwStreamingHandlerException
	 */
	private boolean throwStreamingHandlerException = false;
	
	/**
	 * InputStream is closed
	 */
	private boolean isClosed = false;
	
	
	/**
	 * Logger
	 */
	private Logger log = null;
	
	
	/* ****** CONSTRUCTOR ******* */
	
	public NotifierInputStream(InputStream is,String contentType,NotifierInputStreamParams params) throws IOException, ParseException{
		
		// Set content type of the stream
		//try{
		if(contentType==null){
			throw new ParseException("ContentType not defined in args");
		}
		this.contentType = new ContentType(contentType);
		//}catch(Exception e){
		//	throw new IOException(e.getMessage(),e);
		//}
		
		// Set original input stream
		if(is==null){
			throw new ParseException("Original InputStream not defined in args");
		}
		this.is = is;
		//System.out.println("@@@@@@@@ NotifierInputStream: "+is.getClass().getName());
				
		// Initialize Streaming Handler List
		if(params.sizeStreamingHandlers()>0){
			for (String streamingHandlerId : params.getStreamingHandlerIds()) {
				try{
					//System.out.println("@@@@@@@@ INIT HANDLERS");
					this.streamingHandlers.put(streamingHandlerId, params.getStreamingHandler(streamingHandlerId));
					this.streamingHandlersIds.add(streamingHandlerId);
				}catch(Exception e){
					throw new IOException("Streaming Handler initialization failed (id:"+streamingHandlerId+")"); 
				}
			}
		}
		
		// throwStreamingHandlerException
		this.throwStreamingHandlerException = params.isThrowStreamingHandlerException();
		
		// Log
		this.log = params.getLog();
		
		// Buffering
		if(params.isBufferEnabled()){
			this.setONBuffering();
		}
		
	}
	
	
	
	
	
	
	/* ****** GET ******* */

	public ContentType getContentType() {
		return this.contentType;
	}


	
	
	
	
	/* ******* INPUT STREAM INTERFACE METHODS ******* */
	
	@Override
	public int read(byte[] b) throws IOException {
		return this.read(b, 0, b.length);
	}
	
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException { 
		return this.read_engine(b, off, len, true);
	}
	
//	java.io.FileOutputStream fout = null;
//	java.io.File f = null;
//	private void debug(byte[] b, int off, int letti){
//		try{
//			if(this.fout==null){
//				this.f = java.io.File.createTempFile("NotifierInputStream", "tmp");
//				this.fout = new java.io.FileOutputStream(this.f);
//				this.fout.write(b, off, letti);
//			}
//		}catch(Exception e){
//			System.out.println("@@@@@@@@ DEBUG ERROR:"+e.getMessage());
//		}
//	}
//	private void printFile(){
//		try{
//			this.fout.flush();
//			this.fout.close();
//		}catch(Exception e){
//			System.out.println("@@@@@@@@ FILE ERROR:"+e.getMessage());
//		}
//		System.out.println("@@@@@@@@ FILE["+this.f.getAbsolutePath()+"]");
//	}
	
	private int read_engine(byte[] b, int off, int len, boolean incrementCurrentReadPositionFromStream) throws IOException {
		
//		if(!incrementCurrentReadPositionFromStream){
//			System.out.println("@@@@@@@@ READ FROM SERIALIZE b["+b.length+"] offset["+off+"] length["+len+"]");
//		}
//		else{
//			System.out.println("@@@@@@@@ READ b["+b.length+"] offset["+off+"] length["+len+"]");
//		}
		
		int readBytes = 0;
		int offset = off;
		try{
			
			if(this.isOriginalInputStreamConsumed){
			
				//System.out.println("@@@@@@@@ IS COMPLITED.... ");
				
				// The stream was completely consumed and the bytes are stored in the variable 'contentReadFromStream'
				if(this.contentCompleteReadedFromStream!=null){
					
//					System.out.println("@@@@@@@@ IS COMPLITED currentReadPositionFromStream["+this.currentReadPositionFromStream
//							+"] contentCompleteReadedFromStream["+this.contentCompleteReadedFromStream.length+"].... ");
					
					if(this.currentReadPositionFromStream>=this.contentCompleteReadedFromStream.length){
						// stream completed
//						System.out.println("@@@@@@@@ IS COMPLITED RETURN -1");
//						this.printFile();
						return -1; 
					}
					else{
						int position = this.currentReadPositionFromStream;
						while( (position<this.contentCompleteReadedFromStream.length) && (readBytes<len) ){
							
							if(offset>=b.length){
								throw new IndexOutOfBoundsException("Offset: "+offset+" , byte[] length: "+b.length);
							}
							
							b[offset] = this.contentCompleteReadedFromStream[position];
							readBytes++;
							offset++;
						}
//						System.out.println("@@@@@@@@ IS COMPLITED RETURN "+readBytes+" BYTES");
//						this.debug(b, offset, readBytes);
						return readBytes;
					}
					
				} 
				
				// The stream has already been completely read. The error may be due to the use of the methods serialize without buffering enabled
				else{
					//throw new IOException("The stream has already been completely read. The error may be due to the use of the methods serialize without buffering enabled");
					// NOTE: the engine can call read method more times ...
					return -1;
				}
				
			}
			
			// Reading in the progress of the stream
			else{
								
				// performRead: enable dispatching to streaming handlers
				readBytes = performReadBytes(b,off,len);
				
				// stream completed
				if(readBytes == -1){
					//System.out.println("@@@@@@@@ IS complite ");
					this.isOriginalInputStreamConsumed = true;
				}
				
				// if enabled buffering, the byte read is saved
				if(this.bufferEnabled){
					if(this.isOriginalInputStreamConsumed){
						//System.out.println("@@@@@@@@ IS finalize! ");
						this.finalizeBuffer();
					}else{
						//System.out.println("@@@@@@@@ write offset["+offset+"] readBytes["+readBytes+"] ");
						this.buffer.write(b, offset, readBytes);
					}
				}
				
//				// Check if exists more bytes (questi codice non dovrebbe servire)
//				int bytesMaxRead = len-off;
//				if(readBytes<bytesMaxRead){
//					//System.out.println("@@@@@@@@ Check if exists more bytes ...");
//					int byteRead = read_engine(true);
//					//System.out.println("@@@@@@@@ Check if exists more bytes, return: "+byteRead);
//					if(byteRead!=-1){
//						//System.out.println("@@@@@@@@  Exists more byte, set return at position ["+readBytes+"]");
//						b[readBytes]=(byte)byteRead;
//						readBytes++;
//					}
//				}
				
				//System.out.println("@@@@@@@@ Return dopo Perform "+readBytes);
//				if(readBytes == -1){
//					this.printFile();
//				}else{
//					this.debug(b, offset, readBytes);
//				}			
				return readBytes;
			}
		}finally{
			if(incrementCurrentReadPositionFromStream){
				this.currentReadPositionFromStream=this.currentReadPositionFromStream+readBytes;
			}
		}
	}
	
	private int performReadBytes(byte[] b, int off, int len) throws IOException {
		
		//System.out.println("@@@@@@@@ performReadBytes ...");
		
		int readBytes = 0;
		
		if(this.is==null){
			//System.out.println("@@@@@@@@ return -1 Stream is null ...");
			readBytes = -1;
		}
		else{
			readBytes = this.is.read(b,off,len);
		}
		//System.out.println("@@@@@@@@ ["+readBytes+"] bytes read ...");
		
		// enable dispatching to streaming handlers
		ByteArrayOutputStream bout = null;
		for(String streamingHandlerId : this.streamingHandlersIds) {
			//System.out.println("@@@@@@@@ Streaming handler ["+this.streamingHandlersIds.size()+"]");
			StreamingHandler streamingHandler = this.streamingHandlers.get(streamingHandlerId);
			try{
				if(readBytes==-1){
					//System.out.println("@@@@@@@@ return -1 Dispatching end...");
					streamingHandler.end();
				}
				else{
					if(bout==null){
						bout = new ByteArrayOutputStream();
						bout.write(b, off, readBytes);
						bout.flush();
						bout.close();
					}
					//System.out.println("@@@@@@@@ return bytes "+bout.size()+" Dispatching ...");
					streamingHandler.feed(bout.toByteArray());
				}
			}catch(Throwable e){
				if(this.log!=null){
					this.log.error("["+streamingHandlerId+"] error occurs: "+e.getMessage(),e);
				}
				if(this.throwStreamingHandlerException){
					throw new IOException("["+streamingHandlerId+"] "+e.getMessage());
				}
			}
		}
				
		return readBytes;
	}
	


	
	@Override
	public int read() throws IOException {
		return this.read_engine(true);
	}
	private int read_engine(boolean incrementCurrentReadPositionFromStream) throws IOException {
		try{
			
//			if(!incrementCurrentReadPositionFromStream){
//				System.out.println("@@@@@@@@ READ FROM SERIALIZE");
//			}
//			else{
//				System.out.println("@@@@@@@@ READ");
//			}
			
			if(this.isOriginalInputStreamConsumed){
			
				//System.out.println("@@@@@@@@ IS COMPLITED SINGLE BYTE.... ");
				
				// The stream was completely consumed and the bytes are stored in the variable 'contentReadFromStream'
				if(this.contentCompleteReadedFromStream!=null){
					
//					System.out.println("@@@@@@@@ IS COMPLITED SINGLE BYTE currentReadPositionFromStream["+this.currentReadPositionFromStream
//							+"] contentCompleteReadedFromStream["+this.contentCompleteReadedFromStream.length+"].... ");
					
					if(this.currentReadPositionFromStream>=this.contentCompleteReadedFromStream.length){
						// stream completed
//						System.out.println("@@@@@@@@ IS COMPLITED SINGLE BYTE RETURN -1");
//						this.printFile();
						return -1; 
					}
					else{
//						System.out.println("@@@@@@@@ IS COMPLITED RETURN SINGLE BYTE ("+this.contentCompleteReadedFromStream[this.currentReadPositionFromStream]+") ");
//						this.debug(new byte[]{this.contentCompleteReadedFromStream[this.currentReadPositionFromStream]}, 0, 1);
						return this.contentCompleteReadedFromStream[this.currentReadPositionFromStream];
					}
					
				} 
				
				// The stream has already been completely read. The error may be due to the use of the methods serialize without buffering enabled
				else{
					//throw new IOException("The stream has already been completely read. The error may be due to the use of the methods serialize without buffering enabled");
					// NOTE: the engine can call read method more times ...
					return -1;
				}
				
			}
			
			// Reading in the progress of the stream
			else{
								
				// performRead: enable dispatching to streaming handlers
				int byteRead = performRead();
			
				// stream completed
				if(byteRead == -1){
					//System.out.println("@@@@@@@@ IS complite SINGLE BYTE ");
					this.isOriginalInputStreamConsumed = true;
				}
				
				// if enabled buffering, the byte read is saved
				if(this.bufferEnabled){
					if(this.isOriginalInputStreamConsumed){
						//System.out.println("@@@@@@@@ IS finalize! SINGLE BYTE");
						this.finalizeBuffer();
					}else{
						//System.out.println("@@@@@@@@ write SINGLE BYTE["+byteRead+"] ");
						this.buffer.write(byteRead);
					}
				}
				
				//System.out.println("@@@@@@@@ Return dopo Perform SINGLE BYTE "+byteRead);
//				if(byteRead == -1){
//					this.printFile();
//				}else{
//					this.debug(new byte[]{(byte)byteRead}, 0, 1);
//				}
				return byteRead;
			}
		}finally{
			if(incrementCurrentReadPositionFromStream){
				this.currentReadPositionFromStream++;
			}
		}
	}
	
	private int performRead() throws IOException {
		int b = this.is.read();

		// enable dispatching to streaming handlers
		for(String streamingHandlerId : this.streamingHandlersIds) {
			StreamingHandler streamingHandler = this.streamingHandlers.get(streamingHandlerId);
			try{
				if(b==-1){
					//System.out.println("@@@@@@@@ return -1 Dispatching end ...");
					streamingHandler.end();
				}
				else{
					//System.out.println("@@@@@@@@ return un byte Dispatching ...");
					streamingHandler.feed((byte)b);
				}
			}catch(Throwable e){
				if(this.log!=null){
					this.log.error("["+streamingHandlerId+"] error occurs: "+e.getMessage(),e);
				}
				if(this.throwStreamingHandlerException){
					throw new IOException("["+streamingHandlerId+"] "+e.getMessage());
				}
			}
		}
		return b;
	}
	
	@Override
	public void close() throws IOException {
		
		if(this.isClosed==false){
		
			if(this.is!=null)
				this.is.close();
			
			IOException streamingHandlerException = null;
			
			// enable dispatching to streaming handlers
			for(String streamingHandlerId : this.streamingHandlersIds) {
				StreamingHandler streamingHandler = this.streamingHandlers.get(streamingHandlerId);
				try{
					streamingHandler.closeResources();
				}catch(Throwable e){
					if(this.log!=null){
						this.log.error("["+streamingHandlerId+"] error occurs: "+e.getMessage(),e);
					}
					if(this.throwStreamingHandlerException){
						if(streamingHandlerException==null){
							// throw the first exception occurs
							streamingHandlerException = new IOException("["+streamingHandlerId+"] "+e.getMessage());
						}
					}
				}
			}
					
			this.isClosed = true;
			
			if(streamingHandlerException!=null){
				throw streamingHandlerException;
			}
		}
	}
	
	
	
	
	
	
	/* ******* HANDLERS ******* */
	
	/**
	 * adds a Streaming handler to our list
	 * @param handler handler to add
	 * @throws IOException 
	 */
	public void addStreamingHandler(StreamingHandler handler) throws IOException {
		
		//System.out.println("@@@@@@@@ addStreamingHandler currentReadPositionFromStream["+this.currentReadPositionFromStream+"] COMPLITED["+this.isCompleted+"]... ");
		
		// the option of buffering can be enabled only if the stream has not yet been accessed
		if(this.currentReadPositionFromStream>0){
			if(this.bufferEnabled==false)
				throw new IOException("You can not add handler after the stream has been accessed with buffering disabled");
			else {
				
				String id = handler.getID();
				if(this.streamingHandlersIds.contains(id)){
					throw new IOException("StreamingHandler with id ["+id+"] already exists");
				}
				this.streamingHandlers.put(id, handler);
				this.streamingHandlersIds.add(id);
								
				if(this.isOriginalInputStreamConsumed){
					//System.out.println("@@@@@@@@ addStreamingHandler FEED ["+this.contentCompleteReadedFromStream.length+"]");
					handler.feed(this.contentCompleteReadedFromStream);
					
					//System.out.println("@@@@@@@@ addStreamingHandler END");
					handler.end();
				}
				else{
					//System.out.println("@@@@@@@@ addStreamingHandler FEED ["+this.buffer.size()+"]");
					handler.feed(this.buffer.toByteArray());
				}
			}
		}
		
	}
	
	/**
	 * Given a StreamingHandler class, returns the corresponding handler, if one
	 * Useful in case of more handlers registered to this class
	 * @param clazz - the class which the needed handler belongs
	 * @return corresponding handler if there is one, otherwise null
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public <T extends StreamingHandler> T getFirstStreamingHandlerByType(Class<T> clazz) throws IOException {
		for(String streamingHandlerId : this.streamingHandlersIds) {
			StreamingHandler streamingHandler = this.streamingHandlers.get(streamingHandlerId);
			if (streamingHandler.getClass().equals(clazz)) 
				return (T) streamingHandler;
		}
		throw new IOException("StreamingHandler with type ["+clazz.getName()+"] not exists");
	}

	public StreamingHandler getStreamingHandler(String id) throws IOException{
		if(this.streamingHandlersIds.contains(id)){
			return this.streamingHandlers.get(id);
		}
		else{
			throw new IOException("StreamingHandler with id ["+id+"] not exists");
		}
	}
	

	
	

	
	
	/* ******* BUFFER ******* */
	
	public void setONBuffering() throws IOException {
		
		// the option of buffering can be enabled only if the stream has not yet been accessed
		if(this.currentReadPositionFromStream>0){
			throw new IOException("You can not enable buffering after the stream has been accessed");
		}

		if(this.bufferEnabled){
			throw new IOException("The buffering is already enabled");
		}
		
		// enable buffering
		this.bufferEnabled = true;
		this.buffer = new ByteArrayOutputStream();

	}
	
	public void setOFFBuffering() throws IOException {
		this.setOFFBuffering(true);
	}
	public void setOFFBuffering(boolean releaseBufferReaded) throws IOException {
		
		//System.out.println("@@@@@@@@ setOFFBuffering("+releaseBufferReaded+")");
		
		if(this.bufferEnabled==false){
			throw new IOException("The buffering is not enabled");
		}
		
		// disable buffer
		this.bufferEnabled = false;
		if(releaseBufferReaded){
			//System.out.println("@@@@@@@@ setOFFBuffering("+releaseBufferReaded+") rilascio");
			this.contentCompleteReadedFromStream = null;
			if(this.buffer!=null){
				this.buffer.close();
				this.buffer = null;
				//System.out.println("@@@@@@@@ setOFFBuffering("+releaseBufferReaded+") rilasciato");
			}
		}

	}
	
	private void finalizeBuffer() throws IOException{
		
		if(this.bufferEnabled==false){
			throw new IOException("BufferMode is not enabled");
		}
		
		if(this.contentCompleteReadedFromStream!=null){
			throw new IOException("bufferingComplete already invoked");
		}
		
		this.buffer.flush();
		this.buffer.close();
		this.contentCompleteReadedFromStream=this.buffer.toByteArray();
		this.buffer=null; // G.C.
		
	}
	
	
	
	
	
	
	
	
	
	
	/* ******* SERIALIZE ******* */
	
	public void serialize(OutputStream out) throws IOException{
		this.serializeEngine(out,false);
	}
	
	public void serializeAndConsume(OutputStream out) throws IOException{
		this.serializeEngine(out,true);
	}
	
	public byte[] serialize() throws IOException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.serializeEngine(bout,false);
		bout.flush();
		bout.close();
		return bout.toByteArray();
	}
	
	public byte[] serializeAndConsume() throws IOException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.serializeEngine(bout,true);
		bout.flush();
		bout.close();
		return bout.toByteArray();
	}
	
	private void serializeEngine(OutputStream out,boolean consume) throws IOException{
		
		//System.out.println("@@@@@@@@ serializeEngine");
		
		// If the stream is already saved return byte array
		//if(this.bufferEnabled && this.contentCompleteReadedFromStream!=null){
		if(this.contentCompleteReadedFromStream!=null){
			out.write(this.contentCompleteReadedFromStream);
			return;
		}
		
		// If buffering is enabled, but the stream is not completely consumed, write the bytes stored in the buffer in the output stream
		//if(this.bufferEnabled && this.contentCompleteReadedFromStream==null){
		if(this.buffer!=null){
			this.buffer.flush();
			if(this.buffer.size()>0){
				out.write(this.buffer.toByteArray());
			}
		}
		
		if(consume){
			if(this.bufferEnabled){
				this.setOFFBuffering();
			}
			else if(this.buffer!=null){
				this.buffer.close();
				this.buffer = null;
			}
		}
		
		// Conclude to consume the stream remaining
		// If buffering is enabled all bytes read will stored in the buffer
		byte[] buffer = new byte[1024];
		int byteRead = this.read_engine(buffer, 0, buffer.length, false);
		while ( byteRead != -1 ){
			out.write(buffer,0,byteRead);
			byteRead = this.read_engine(buffer, 0, buffer.length, false);
		}
	}
	

	
	

}
