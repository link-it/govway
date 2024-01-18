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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.lang.CharEncoding;

import com.google.common.io.CharStreams;

/**
 * Libreria contenente metodi per copiare gli stream di stringhe (Reader e Writer)
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CopyCharStream {


	
	/** Copy Sring */
	
	public static void copy(Reader reader, OutputStream out) throws UtilsException{
		copy(CopyStreamMethod.AUTO, reader, out);
	}
	public static void copy(CopyStreamMethod method, Reader reader, OutputStream out) throws UtilsException{
		try {
			Writer writer = new OutputStreamWriter(out);
			CopyCharStream.copy(method, reader, writer);
			out.flush();
			writer.close();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void copy(Reader reader, Writer writer) throws UtilsException{
		copy(CopyStreamMethod.AUTO, reader, writer);
	}
	public static void copy(CopyStreamMethod method, Reader reader,Writer writer) throws UtilsException{
		switch (method) {
		case JAVA:
			copyBuffer(reader,writer);
			break;
		case JAVA_TRANSFER_TO:
			transferTo(reader,writer);
			break;
		case JAVA_NIO:
			copyChannels(reader,writer);
			break;
		case GUAVA:
			copyGuava(reader,writer);
			break;
		case COMMONS_IO:
			copyCommonsIO(reader,writer);
			break;
		case AUTO:
//			if(reader instanceof java.io.FileReader || writer instanceof java.io.FileWriter) {
//				copyChannels(reader,writer);	
//			}
//			else {
//				transferTo(reader,writer);
//			}
			// Nel caso di char e' sempre piu' efficiente il transferTo
			transferTo(reader,writer);
			break;
		}
	}
	
	public static void copyBuffer(Reader reader,Writer writer) throws UtilsException{
		try{
			char [] buffer = new char[Utilities.DIMENSIONE_BUFFER];
			int letti = 0;
			while( (letti=reader.read(buffer)) != -1 ){
				writer.write(buffer, 0, letti);
			}
			writer.flush();
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void transferTo(Reader reader,Writer writer) throws UtilsException{
		try{
			reader.transferTo(writer);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void copyGuava(Reader reader,Writer writer) throws UtilsException{
		try{
			CharStreams.copy(reader, writer);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void copyCommonsIO(Reader reader,Writer writer) throws UtilsException{
		try{
			IOUtils.copy(reader, writer);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static void copyChannels(final Reader reader, final Writer writer) throws UtilsException {
		try {
			try(
					ReaderInputStream src = new ReaderInputStream(reader, CharEncoding.UTF_8);
					WriterOutputStream dest = new WriterOutputStream(writer, CharEncoding.UTF_8);
				){
			final ReadableByteChannel inputChannel = Channels.newChannel(src);
			final WritableByteChannel outputChannel = Channels.newChannel(dest);
			copyChannels(inputChannel, outputChannel);
			}
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
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
	
	public static void copy(File from,Writer writer) throws UtilsException{
		try{
			try(
					WriterOutputStream dest = new WriterOutputStream(writer, CharEncoding.UTF_8);
				){
				Files.copy(from.toPath(), dest);
			}
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
	public static void copy(Reader reader, File to) throws UtilsException{
		try{
			try(
					ReaderInputStream from = new ReaderInputStream(reader, CharEncoding.UTF_8);
				){
				Files.copy(from, to.toPath());
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static void copy(Reader reader, File to, CopyOption ... options) throws UtilsException{
		try{
			try(
					ReaderInputStream from = new ReaderInputStream(reader, CharEncoding.UTF_8);
				){
				Files.copy(from, to.toPath(), options);
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
