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

package org.openspcoop2.utils.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * CompressorUtilities
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CompressorUtilities {

	public static void main(String [] args) throws Exception{
		
		String test = "<prova xmlns=\"www.test.it\">PROVA</prova>";
		byte[]testB = test.getBytes();
		
		System.out.println("\n\n=== DEFLATER ===");
		byte [] compress = compress(testB, CompressorType.DEFLATER);
		System.out.println("Compresso, dimensione: "+compress.length);
		System.out.println("Compresso, in stringa: "+new String(compress));
		System.out.println("De-Compresso, in stringa: "+new String(decompress(compress, CompressorType.DEFLATER)));
		
		System.out.println("\n\n=== GZIP ===");
		compress = compress(testB, CompressorType.GZIP);
		System.out.println("Compresso, dimensione: "+compress.length);
		System.out.println("Compresso, in stringa: "+new String(compress));
		System.out.println("De-Compresso, in stringa: "+new String(decompress(compress, CompressorType.GZIP)));
		
		System.out.println("\n\n=== ZIP ===");
		compress = compress(testB, CompressorType.ZIP);
		System.out.println("Compresso, dimensione: "+compress.length);
		System.out.println("Compresso, in stringa: "+new String(compress));
		System.out.println("De-Compresso, in stringa: "+new String(decompress(compress, CompressorType.ZIP)));
		
	}
	
    public static byte[] compress(byte[] content, CompressorType type) throws UtilsException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = null;
            switch (type) {
			case DEFLATER:
				out = new DeflaterOutputStream(baos);
				out.write(content);
				break;
			case GZIP:
				out = new GZIPOutputStream(baos);
				out.write(content);
				break;
			case ZIP:
				out = new ZipOutputStream(baos);
				((ZipOutputStream)out).putNextEntry(new ZipEntry("dat"));
				out.write(content);
				((ZipOutputStream)out).closeEntry();
				break;
			}
            out.flush();
            out.close();
            baos.flush();
            baos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new UtilsException(e.getMessage(),e);
        }
    }

    public static byte [] decompress(byte[] bytes, CompressorType type) throws UtilsException {
        InputStream in = null;
        ByteArrayInputStream bin = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
        	bin = new ByteArrayInputStream(bytes);
        	byte[] buffer = new byte[8192];
            int len;
        	switch (type) {
 			case DEFLATER:
 				in = new InflaterInputStream(bin);
 	            while((len = in.read(buffer))>0)
 	                baos.write(buffer, 0, len);
 				break;
 			case GZIP:
 				in = new GZIPInputStream(bin);
 	            while((len = in.read(buffer))>0)
 	                baos.write(buffer, 0, len);
 				break;
 			case ZIP:
 				File f = File.createTempFile("unzip", "zip");
 				ZipFile zf = null;
 				try{
 					FileSystemUtilities.writeFile(f, bytes);
 					zf = new ZipFile(f);
 					ZipEntry ze = zf.entries().nextElement();
 					in = zf.getInputStream(ze);
 	 	            while((len = in.read(buffer))>0)
 	 	                baos.write(buffer, 0, len);
 				}finally{
 					try{
 						f.delete();
 					}catch(Exception eClose){}
 					try{
 						if(zf!=null){
 							zf.close();
 						}
 					}catch(Exception eClose){}
 				}
 				break;
 			}
            baos.flush();
            baos.close();
            return baos.toByteArray();
        }  catch (Exception e) {
            throw new UtilsException(e.getMessage(),e);
        }
    }
	
}


