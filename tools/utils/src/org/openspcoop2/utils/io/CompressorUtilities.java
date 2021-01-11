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

package org.openspcoop2.utils.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
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
		
		testCompressor(null);
		testArchive(null);
		
	}
	
	public static void testCompressor(CompressorType tipo) throws Exception{
		
		String test = "<prova xmlns=\"www.test.it\">PROVA</prova>";
		byte[]testB = test.getBytes();
		
		if(tipo==null || CompressorType.DEFLATER.equals(tipo)) {
			System.out.println("\n\n=== DEFLATER ===");
			byte [] compress = compress(testB, CompressorType.DEFLATER);
			System.out.println("Compresso, dimensione: "+compress.length);
			System.out.println("Compresso, in stringa: "+new String(compress));
			String decompresso = new String(decompress(compress, CompressorType.DEFLATER));
			System.out.println("De-Compresso, in stringa: "+decompresso);
			if(!decompresso.equals(test)) {
				throw new Exception("Informazione decompressa non uguale al sorgente");
			}
		}
		
		if(tipo==null || CompressorType.GZIP.equals(tipo)) {
			System.out.println("\n\n=== GZIP ===");
			byte [] compress = compress(testB, CompressorType.GZIP);
			System.out.println("Compresso, dimensione: "+compress.length);
			System.out.println("Compresso, in stringa: "+new String(compress));
			String decompresso = new String(decompress(compress, CompressorType.GZIP));
			System.out.println("De-Compresso, in stringa: "+decompresso);
			if(!decompresso.equals(test)) {
				throw new Exception("Informazione decompressa non uguale al sorgente");
			}
		}
			
		if(tipo==null || CompressorType.ZIP.equals(tipo)) {
			System.out.println("\n\n=== ZIP ===");
			byte [] compress = compress(testB, CompressorType.ZIP);
			System.out.println("Compresso, dimensione: "+compress.length);
			System.out.println("Compresso, in stringa: "+new String(compress));
			String decompresso = new String(decompress(compress, CompressorType.ZIP));
			System.out.println("De-Compresso, in stringa: "+decompresso);
			if(!decompresso.equals(test)) {
				throw new Exception("Informazione decompressa non uguale al sorgente");
			}
		}
		
	}
	
	public static void testArchive(ArchiveType tipo) throws Exception{
		
		String entryName = "entry1";
		String test = "<prova xmlns=\"www.test.it\">PROVA</prova>";
		byte[]testB = test.getBytes();
		
		String entryName2 = "dir/subdir/entry2";
		String test2 = "<prova xmlns=\"www.test.it\">PROVA2</prova>";
		byte[]test2B = test2.getBytes();
		
		List<Entry> entries = new ArrayList<>();
		entries.add(new Entry(entryName,testB));
		entries.add(new Entry(entryName2,test2B));
		
		if(tipo==null || ArchiveType.ZIP.equals(tipo)) {
			System.out.println("\n\n=== ZIP ===");
			byte [] compress = archive(entries, ArchiveType.ZIP);
			System.out.println("Compresso, dimensione: "+compress.length);
			List<Entry> entriesRead = read(compress, ArchiveType.ZIP);
			System.out.println("De-Compresso: "+entriesRead.size());
			if(entriesRead.size()!=2) {
				throw new Exception("Informazione decompressa non uguale al sorgente (size)");
			}
			Entry entryRead1 = entriesRead.get(0);
			Entry entryRead2 = entriesRead.get(1);
			if(!entryName.equals(entryRead1.getName())) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry1 name:"+entryRead1.getName()+" atteso:"+entryName+")");
			}
			if(!entryName2.equals(entryRead2.getName())) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry2 name:"+entryRead2.getName()+" atteso:"+entryName2+")");
			}
			if(!test.equals(new String(entryRead1.getContent()))) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry1 contenuto differente)");
			}
			if(!test2.equals(new String(entryRead2.getContent()))) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry1 contenuto differente)");
			}
			System.out.println("De-Compresso: test di comparazione completati con successo");
		}
		
		if(tipo==null || ArchiveType.TAR.equals(tipo)) {
			System.out.println("\n\n=== TAR ===");
			byte [] compress = archive(entries, ArchiveType.TAR);
			System.out.println("Compresso, dimensione: "+compress.length);
			List<Entry> entriesRead = read(compress, ArchiveType.TAR);
			System.out.println("De-Compresso: "+entriesRead.size());
			if(entriesRead.size()!=2) {
				throw new Exception("Informazione decompressa non uguale al sorgente (size)");
			}
			Entry entryRead1 = entriesRead.get(0);
			Entry entryRead2 = entriesRead.get(1);
			if(!entryName.equals(entryRead1.getName())) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry1 name:"+entryRead1.getName()+" atteso:"+entryName+")");
			}
			if(!entryName2.equals(entryRead2.getName())) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry2 name:"+entryRead2.getName()+" atteso:"+entryName2+")");
			}
			if(!test.equals(new String(entryRead1.getContent()))) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry1 contenuto differente)");
			}
			if(!test2.equals(new String(entryRead2.getContent()))) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry1 contenuto differente)");
			}
			System.out.println("De-Compresso: test di comparazione completati con successo");
		}
		
		if(tipo==null || ArchiveType.TGZ.equals(tipo)) {
			System.out.println("\n\n=== TGZ ===");
			byte [] compress = archive(entries, ArchiveType.TGZ);
			System.out.println("Compresso, dimensione: "+compress.length);
			List<Entry> entriesRead = read(compress, ArchiveType.TGZ);
			System.out.println("De-Compresso: "+entriesRead.size());
			if(entriesRead.size()!=2) {
				throw new Exception("Informazione decompressa non uguale al sorgente (size)");
			}
			Entry entryRead1 = entriesRead.get(0);
			Entry entryRead2 = entriesRead.get(1);
			if(!entryName.equals(entryRead1.getName())) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry1 name:"+entryRead1.getName()+" atteso:"+entryName+")");
			}
			if(!entryName2.equals(entryRead2.getName())) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry2 name:"+entryRead2.getName()+" atteso:"+entryName2+")");
			}
			if(!test.equals(new String(entryRead1.getContent()))) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry1 contenuto differente)");
			}
			if(!test2.equals(new String(entryRead2.getContent()))) {
				throw new Exception("Informazione decompressa non uguale al sorgente (entry1 contenuto differente)");
			}
			System.out.println("De-Compresso: test di comparazione completati con successo");
		}

		
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
    
    public static byte[] archive(List<Entry> entries, ArchiveType type) throws UtilsException{
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			org.apache.commons.compress.archivers.ArchiveOutputStream  out = null;
			org.apache.commons.compress.compressors.CompressorOutputStream compressorOut = null;
            switch (type) {
            case TAR:
				out = new TarArchiveOutputStream(bout);
				break;
			case TGZ:
				compressorOut = new GzipCompressorOutputStream(bout);
				out = new TarArchiveOutputStream(compressorOut);
				break;
			case ZIP:
				out = new ZipArchiveOutputStream(bout);
				break;
			}
			for (Entry entry : entries) {
				String name = entry.getName();
				ArchiveEntry archiveEntry = null;
				switch (type) {
	            case TAR:
				case TGZ:
					archiveEntry = new TarArchiveEntry(name);
					((TarArchiveEntry)archiveEntry).setSize(entry.getContent().length); 
					break;
				case ZIP:
					archiveEntry = new ZipArchiveEntry(name);
					break;
				}
				out.putArchiveEntry(archiveEntry);
				out.write(entry.getContent());
				out.closeArchiveEntry();
			}
			
			out.flush();
			out.close();
			if(compressorOut!=null) {
				compressorOut.flush();
				compressorOut.close();
			}
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new UtilsException("Errore durante la comprensione '"+type+"': "+e.getMessage(),e);
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
	
    public static List<Entry> read(byte[] archiveContent, ArchiveType type) throws UtilsException{
		try {
			List<Entry> list = new ArrayList<>();
			
			ByteArrayInputStream bin = new ByteArrayInputStream(archiveContent);
			ArchiveInputStream in = null;
			CompressorInputStream compressorIn = null;
			switch (type) {
			case TAR:
				in = new TarArchiveInputStream(bin);
				break;
			case TGZ:
				compressorIn = new GzipCompressorInputStream(bin);
				in = new TarArchiveInputStream(compressorIn);
				break;
			case ZIP:
				in = new ZipArchiveInputStream(bin);
				break;
			}
			ArchiveEntry entry;
		    while ((entry = in.getNextEntry()) != null) {
		        String name = entry.getName();
		    	//System.out.println(name);
		        
		        if(name!=null && !( name.endsWith("/") || name.endsWith("\\") ) ){
		        	Entry zentry = new Entry();
	        		zentry.setName(name);
	        		
	        		ByteArrayOutputStream bout = new ByteArrayOutputStream();
	        		byte contents[] = new byte[4096];
	        		int direct;
	        		while ((direct = in.read(contents, 0, contents.length)) >= 0) {
	        			//System.out.println("Read " + direct + "bytes content.");
	        			bout.write(contents, 0, direct);
	        		}
	        		bout.flush();
	        		bout.close();
	        		
	        		zentry.setContent(bout.toByteArray());
	        		list.add(zentry);
		        }
		        
		       // in.closeEntry();
		    }
		    if(compressorIn!=null) {
		    	compressorIn.close();
		    }
		    in.close();
		    bin.close();

	        return list;
	        
		}catch(Exception e){
			throw new UtilsException("Errore durante la comprensione zip: "+e.getMessage(),e);
		}
	}
}


