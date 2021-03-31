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

package org.openspcoop2.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;

import com.google.common.io.ByteStreams;

/**
 * Libreria contenente metodi per copiare gli stream
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CopyStream {


	
	/** Copy Stream */
	
	public static void copy(InputStream is,OutputStream os) throws UtilsException{
		copy(CopyStreamMethod.AUTO, is, os);
	}
	public static void copy(CopyStreamMethod method, InputStream is,OutputStream os) throws UtilsException{
		switch (method) {
		case JAVA:
			copyBuffer(is,os);
			break;
		case JAVA_TRANSFER_TO:
			transferTo(is,os);
			break;
		case JAVA_NIO:
			copyChannels(is,os);
			break;
		case GUAVA:
			copyGuava(is,os);
			break;
		case COMMONS_IO:
			copyCommonsIO(is,os);
			break;
		case AUTO:
			if(is instanceof FileInputStream || os instanceof FileOutputStream) {
				copyChannels(is,os);	
			}
			else {
				transferTo(is,os);
			}
			break;
		}
	}
	
	public static void copyBuffer(InputStream is,OutputStream os) throws UtilsException{
		try{
			byte [] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
			int letti = 0;
			while( (letti=is.read(buffer)) != -1 ){
				os.write(buffer, 0, letti);
			}
			os.flush();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void transferTo(InputStream is,OutputStream os) throws UtilsException{
		try{
			is.transferTo(os);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void copyGuava(InputStream is,OutputStream os) throws UtilsException{
		try{
			ByteStreams.copy(is, os);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void copyCommonsIO(InputStream is,OutputStream os) throws UtilsException{
		try{
			IOUtils.copy(is, os);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void copyChannels(final InputStream src, final OutputStream dest) throws UtilsException {
		final ReadableByteChannel inputChannel = Channels.newChannel(src);
		final WritableByteChannel outputChannel = Channels.newChannel(dest);
		copyChannels(inputChannel, outputChannel);
	}
	public static void copyChannels(final ReadableByteChannel src, final WritableByteChannel dest) throws UtilsException {
		try{
			final ByteBuffer buffer = ByteBuffer.allocateDirect(Utilities.DIMENSIONE_BUFFER);
		        
			while(src.read(buffer) != -1) {
				buffer.flip();
				dest.write(buffer);
				buffer.compact();
			}
		        
			buffer.flip();
			
			while(buffer.hasRemaining()) {
				dest.write(buffer);
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void copy(File from,OutputStream os) throws UtilsException{
		try{
			Files.copy(from.toPath(), os);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static void copy(File from,File to) throws UtilsException{
		try{
			Files.copy(from.toPath(), to.toPath());
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static void copy(InputStream from, File to) throws UtilsException{
		try{
			Files.copy(from, to.toPath());
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}

enum CopyStreamMethod{
	
	JAVA, JAVA_TRANSFER_TO, JAVA_NIO, GUAVA, COMMONS_IO, AUTO
	
}
