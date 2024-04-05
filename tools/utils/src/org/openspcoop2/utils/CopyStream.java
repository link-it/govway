/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import java.nio.file.CopyOption;
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
	
	/** Timeout Utilities */
	public static InputStream buildTimeoutInputStream(InputStream isParam, int timeout) throws UtilsException {
		InputStream is = isParam;
		if(timeout>0 && !(is instanceof TimeoutInputStream)) {
			try {
				is = new TimeoutInputStream(isParam, timeout);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		return is;
	}
	
	/** Limited Utilities */
	public static InputStream buildLimitedInputStream(InputStream isParam, long limitBytes) throws UtilsException {
		InputStream is = isParam;
		if(limitBytes>0 && !(is instanceof LimitedInputStream)) {
			try {
				is = new LimitedInputStream(isParam, limitBytes);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
		return is;
	}
	
	
	/** Copy Stream */
	
	public static void copy(InputStream is,OutputStream os) throws UtilsException{
		copy(CopyStreamMethod.AUTO, is, os, -1, -1);
	}
	public static void copy(CopyStreamMethod method, InputStream is,OutputStream os) throws UtilsException{
		copy(method, is, os, -1, -1);
	}
	
	public static void copy(InputStream is,OutputStream os, int timeout) throws UtilsException{
		copy(CopyStreamMethod.AUTO, is, os, timeout);
	}
	public static void copy(CopyStreamMethod method, InputStream isParam,OutputStream os, int timeout) throws UtilsException{
		copy(method, isParam, os, timeout, -1);
	}
	
	public static void copy(InputStream is,OutputStream os, long limitBytes) throws UtilsException{
		copy(CopyStreamMethod.AUTO, is, os, limitBytes);
	}
	public static void copy(CopyStreamMethod method, InputStream isParam,OutputStream os, long limitBytes) throws UtilsException{
		copy(method, isParam, os, -1, limitBytes);
	}
	
	public static void copy(InputStream isParam,OutputStream os, int timeout, long limitBytes) throws UtilsException{
		copy(CopyStreamMethod.AUTO, isParam, os, timeout, limitBytes);
	}
	public static void copy(CopyStreamMethod method, InputStream isParam,OutputStream os, int timeout, long limitBytes) throws UtilsException{
		
		InputStream is = isParam;
		if(limitBytes>0) {
			is = buildLimitedInputStream(is, limitBytes);
		}
		if(timeout>0) {
			is = buildTimeoutInputStream(is, timeout);
		}
		
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
			boolean timeoutInputStream = false;
			InputStream checkIs = is;
			OutputStream checkOs = os;
			if(is instanceof TimeoutInputStream) {
				checkIs = ((TimeoutInputStream) is).getIsWrapped();
				timeoutInputStream = true;
			}
			if(checkOs instanceof FileOutputStream) {
				//System.out.println("CHANNEL");
				copyChannels(is,os);	
			}
			else if(checkIs instanceof FileInputStream) {
				if(timeoutInputStream) {
					//System.out.println("TRANSFER");
					transferTo(is,os);
				}
				else {
					//System.out.println("CHANNEL");
					copyChannels(is,os);
				}
			}
			else {
				//System.out.println("TRANSFER");
				transferTo(is,os);
			}
			break;
		}
	}
	
	public static void copyBuffer(InputStream is,OutputStream os) throws UtilsException{
		copyBuffer(is,os, Utilities.DIMENSIONE_BUFFER);
	}
	public static void copyBuffer(InputStream is,OutputStream os, int sizeBuffer) throws UtilsException{
		try{
			byte [] buffer = new byte[sizeBuffer];
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
	public static void copy(File from,File to, CopyOption ... options) throws UtilsException{
		try{
			Files.copy(from.toPath(), to.toPath(), options);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static void copy(InputStream from, File to) throws UtilsException{
		copy(from, to, -1, -1);
	}
	public static void copy(InputStream from, File to, int timeout) throws UtilsException{
		copy(from, to, timeout, -1);
	}
	public static void copy(InputStream from, File to, long limitBytes) throws UtilsException{
		copy(from, to, -1, limitBytes);
	}
	public static void copy(InputStream from, File to, int timeout, long limitBytes) throws UtilsException{
		try{
			InputStream is = from;
			if(limitBytes>0) {
				is = buildLimitedInputStream(is, limitBytes);
			}
			if(timeout>0) {
				is = buildTimeoutInputStream(is, timeout);
			}
			Files.copy(is, to.toPath());
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static void copy(InputStream from, File to, CopyOption ... options) throws UtilsException{
		copy(from, to, -1, -1, options);
	}
	public static void copy(InputStream from, File to, int timeout, CopyOption ... options) throws UtilsException{
		copy(from, to, timeout, -1, options);
	}
	public static void copy(InputStream from, File to, long limitBytes, CopyOption ... options) throws UtilsException{
		copy(from, to, -1, limitBytes, options);
	}
	public static void copy(InputStream from, File to, int timeout, long limitBytes, CopyOption ... options) throws UtilsException{
		try{
			InputStream is = from;
			if(limitBytes>0) {
				is = buildLimitedInputStream(is, limitBytes);
			}
			if(timeout>0) {
				is = buildTimeoutInputStream(is, timeout);
			}
			Files.copy(is, to.toPath(), options);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

	
}
