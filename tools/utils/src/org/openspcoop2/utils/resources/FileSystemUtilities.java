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


package org.openspcoop2.utils.resources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;


/**
 * Classe utilizzabile per raccogliere utility su operazioni effettuate su file system
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileSystemUtilities {
	
	private FileSystemUtilities() {}

	public static File createTempFile(String prefix, String suffix) throws IOException{
		Path p = createTempPath(prefix, suffix);
		if(p==null) {
			throw new IOException("Creation failed");
		}
		return p.toFile();
	}
	public static Path createTempPath(String prefix, String suffix) throws IOException{
		return Utilities.createTempPath(prefix, suffix);
	}
	
	public static File createTempFile(File dir, String prefix, String suffix) throws IOException{
		Path p = createTempPath(dir.toPath(), prefix, suffix);
		if(p==null) {
			throw new IOException("Creation failed");
		}
		return p.toFile();
	}
	public static Path createTempPath(Path dir, String prefix, String suffix) throws IOException{
		return Utilities.createTempPath(dir, prefix, suffix);
	}
	
	public static void copy(File in, File out) throws IOException {
		FileSystemUtilities.copy(in.getAbsolutePath(),out.getAbsolutePath());
	}
	public static void copy(String in, String out) 
	throws IOException {
		try(FileInputStream fin = new FileInputStream(in);
			FileOutputStream fout = new FileOutputStream(out);){
			FileSystemUtilities.copy(fin,fout);
			fout.flush();
		}
	}
	public static void copy(InputStream in, OutputStream out) 
	throws IOException {

		byte[] buffer = new byte[256];
		while (true) {
			int bytesRead = in.read(buffer);
			if (bytesRead == -1) break;
			out.write(buffer, 0, bytesRead);
		}

	} 

	public static void copyDirectory(String srcPath, String dstPath)
	throws IOException{
		FileSystemUtilities.copyDirectory(new File(srcPath), new File(dstPath));
	}
	public static void copyDirectory(File srcPath, File dstPath)
	throws IOException{

		if (srcPath.isDirectory()){
			if (!dstPath.exists()){
				dstPath.mkdir();
			}

			String[] files = srcPath.list();
			if(files!=null) {
				for(int i = 0; i < files.length; i++){
						FileSystemUtilities.copyDirectory(new File(srcPath, files[i]), 
								new File(dstPath, files[i]));
				}
			}
		}
		else{
			if(!srcPath.exists()){
				/** System.out.println("File or directory does not exist."); */
			}
			else {
				try (InputStream in = new FileInputStream(srcPath);
					OutputStream out = new FileOutputStream(dstPath);){ 
					// Transfer bytes from in to out
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.flush();
				}
			}
		}
		/** System.out.println("Directory copied."); */
	}
	
	
	public static String readFile(String f) throws UtilsException, FileNotFoundException{
		return FileSystemUtilities.readFile(new File(f));
	}
	public static String readFile(File f) throws UtilsException, FileNotFoundException{
		return readFileEngine(f, null, null);
	}
	public static String readFile(String f, String charsetName) throws UtilsException, FileNotFoundException{
		return FileSystemUtilities.readFile(new File(f),charsetName);
	}
	public static String readFile(File f, String charsetName) throws UtilsException, FileNotFoundException{
		return readFileEngine(f, charsetName, null);
	}
	public static String readFile(String f, Charset charset) throws UtilsException, FileNotFoundException{
		return FileSystemUtilities.readFile(new File(f), charset);
	}
	public static String readFile(File f, Charset charset) throws UtilsException, FileNotFoundException{
		return readFileEngine(f, null, charset);
	}
	private static String readFileEngine(File f, String charsetName, Charset charset) throws UtilsException, FileNotFoundException{
		try (FileInputStream fis =new FileInputStream(f);
			ByteArrayOutputStream byteInputBuffer = new ByteArrayOutputStream();){
			byte [] readB = new byte[8192];
			int readByte = 0;
			while((readByte = fis.read(readB))!= -1){
				byteInputBuffer.write(readB,0,readByte);
			}
			byteInputBuffer.flush();
			
			if(charsetName!=null) {
				return  byteInputBuffer.toString(charsetName);
			}
			else if(charset!=null) {
				return  byteInputBuffer.toString(charset);
			}
			else {
				return  byteInputBuffer.toString();
			}
		}
		catch(FileNotFoundException notFound) {
			throw notFound;
		}
		catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public static byte[] readBytesFromFile(String f) throws UtilsException, FileNotFoundException{
		return FileSystemUtilities.readBytesFromFile(new File(f));
	}
	public static byte[] readBytesFromFile(File f) throws UtilsException, FileNotFoundException{	
		try (FileInputStream fis =new FileInputStream(f);
			ByteArrayOutputStream byteInputBuffer = new ByteArrayOutputStream();){
			byte [] readB = new byte[8192];
			int readByte = 0;
			while((readByte = fis.read(readB))!= -1){
				byteInputBuffer.write(readB,0,readByte);
			}
			byteInputBuffer.flush();
			
			return  byteInputBuffer.toByteArray();
		}
		catch(FileNotFoundException notFound) {
			throw notFound;
		}
		catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	public static void writeFile(String f,byte[] contenuto)throws UtilsException{
		FileSystemUtilities.writeFile(new File(f), contenuto);
	}
	public static void writeFile(File f,byte[] contenuto)throws UtilsException{
		try (FileOutputStream fos = new FileOutputStream(f);){
			fos.write(contenuto);
			fos.flush();
		}
		catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public static void writeFile(String f,byte[] ... args)throws UtilsException{
		FileSystemUtilities.writeFile(new File(f), args);
	}
	public static void writeFile(File f,byte[] ... args)throws UtilsException{
		if(args!=null){
			try (FileOutputStream fos = new FileOutputStream(f);){
				for(int i=0; i<args.length; i++){
					fos.write(args[i]);
				}
				fos.flush();
			}
			catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
		}
	}
	
	public static void copyFileAndReplaceAllKeywords(String read,String write,String keyword,String values) throws UtilsException, FileNotFoundException{
		FileSystemUtilities.copyFileAndReplaceAllKeywords(new File(read),new File(write),keyword,values);
	}
	public static void copyFileAndReplaceAllKeywords(File read,File write,String keyword,String values) throws UtilsException, FileNotFoundException{
		String[]k = new String[1];
		k[0] = keyword;
		String[]v = new String[1];
		v[0] = values;
		FileSystemUtilities.copyFileAndReplaceAllKeywords(read, write, k, v);
	}
	public static void copyFileAndReplaceAllKeywords(String read,String write,String[] keyword,String[] values) throws UtilsException, FileNotFoundException{
		FileSystemUtilities.copyFileAndReplaceAllKeywords(new File(read),new File(write),keyword,values);
	}
	public static void copyFileAndReplaceAllKeywords(File read,File write,String[] keyword,String[] values) throws UtilsException, FileNotFoundException{
		String file = FileSystemUtilities.readFile(read);
		for(int i=0; i<keyword.length; i++){
			
			/** System.out.println("FILE ["+file+"] contains ["+keyword[i]+"] (value:"+file.indexOf(keyword[i])+")"); */
			int indexOf = file.indexOf(keyword[i]);
			while(indexOf>=0){
				/** System.out.println("REPLACE ["+keyword[i]+"] ["+values[i]+"]"); */
				file = file.replace(keyword[i], values[i]);
				indexOf = file.indexOf(keyword[i],indexOf+values[i].length());
				/** System.out.println("INDEX OF ["+indexOf+"]"); */
			}
		}
		FileSystemUtilities.writeFile(write, file.getBytes());
	}
	
	public static void copyFileAndReplaceKeywords(String read,String write,String keyword,String values) throws UtilsException, FileNotFoundException{
		FileSystemUtilities.copyFileAndReplaceKeywords(new File(read),new File(write),keyword,values);
	}
	public static void copyFileAndReplaceKeywords(File read,File write,String keyword,String values) throws UtilsException, FileNotFoundException{
		String[]k = new String[1];
		k[0] = keyword;
		String[]v = new String[1];
		v[0] = values;
		FileSystemUtilities.copyFileAndReplaceKeywords(read, write, k, v);
	}
	
	public static void copyFileAndReplaceKeywords(String read,String write,String[] keyword,String[] values) throws UtilsException, FileNotFoundException{
		FileSystemUtilities.copyFileAndReplaceKeywords(new File(read),new File(write),keyword,values);
	}
	public static void copyFileAndReplaceKeywords(File read,File write,String[] keyword,String[] values) throws UtilsException, FileNotFoundException{
		String file = FileSystemUtilities.readFile(read);
		for(int i=0; i<keyword.length; i++){
		    file = file.replace(keyword[i], values[i]);
		}
		FileSystemUtilities.writeFile(write, file.getBytes());
	}


	public static boolean emptyDir(String dir) {
		File d = new File(dir);
		if(!d.exists()){
			return true;
		}
		return FileSystemUtilities.emptyDir(d);
	}
	
    public static boolean emptyDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            if(children!=null && children.length>0) {
            	for (int i=0; i<children.length; i++) {
            		boolean success = FileSystemUtilities.deleteDir(new File(dir, children[i]));
            		if (!success) {
            			return false;
            		}
            	}
            }
        }
        
        return true;
    }
    
	public static boolean deleteDir(String dir) {
		File d = new File(dir);
		if(!d.exists()){
			return true;
		}
		return FileSystemUtilities.deleteDir(d);
	}
	
    public static boolean deleteDir(File dir) {

        if (!emptyDir(dir)) {
        	return false;
        }

        // The directory is now empty so now it can be smoked
        try {
        	Files.delete(dir.toPath());
        	return true;
        }catch(Exception e) {
        	return false;
        }
    }
    
    public static boolean moveToDir(String src,String destDir){
    	 // File (or directory) to be moved
        File file = new File(src);
        
        // Destination directory
        File dir = new File(destDir);
        
        return FileSystemUtilities.moveToDir(file,dir);
    }
    public static boolean moveToDir(File src,File destDir){

        // Move file to new directory
        return src.renameTo(new File(destDir, src.getName()));

    }
    
    public static boolean moveToFile(String src,String destFile){
   	 // File (or directory) to be moved
       File file = new File(src);
       
       // Destination directory
       File dir = new File(destFile);
       
       return FileSystemUtilities.moveToDir(file,dir);
   }
   public static boolean moveToFile(File src,File destFile){

       // Move file to new directory
       return src.renameTo(destFile);
   }
   
   public static void mkdirParentDirectory(File file) throws UtilsException {
	   FileSystemUtilities.mkdirParentDirectory(file.getAbsolutePath());
   }
   public static void mkdirParentDirectory(File file,
		   Boolean readable, Boolean readableOwnerOnly,
		   Boolean writable, Boolean writableOwnerOnly,
		   Boolean executable, Boolean executableOwnerOnly) throws UtilsException {
	   FileSystemUtilities.mkdirParentDirectory(file.getAbsolutePath(),
			   readable, readableOwnerOnly,
			   writable, writableOwnerOnly,
			   executable, executableOwnerOnly);
   }
   public static void mkdirParentDirectory(String file) throws UtilsException {
	   mkdirParentDirectory(file,
			   null, null,
			   null, null,
			   null, null);
   }
   public static void mkdirParentDirectory(String file,
		   Boolean readable, Boolean readableOwnerOnly,
		   Boolean writable, Boolean writableOwnerOnly,
		   Boolean executable, Boolean executableOwnerOnly) throws UtilsException {
		try{
			File p = new File(file);
			if(p.getParentFile()==null){
				return;
			}
			if(p.getParentFile().exists()){
				return;
			}
			FileSystemUtilities.mkdirParentDirectory(p.getParentFile().getAbsolutePath());
			if(!p.getParentFile().mkdir()){
				throw new UtilsException(DIRECTORY_PREFIX_MSG+p.getParentFile().getAbsolutePath()+"] non esistente e creazione non riuscita");
			}
			else {
				setRWX(p,
						readable, readableOwnerOnly,
						writable, writableOwnerOnly,
						executable, executableOwnerOnly);
			}
		}catch(Exception e){
			throw new UtilsException("mkdirParentDirectory non riuscito: "+e.getMessage(),e);
		}
   }
   private static void setRWX(File p,
		   Boolean readable, Boolean readableOwnerOnly,
		   Boolean writable, Boolean writableOwnerOnly,
		   Boolean executable, Boolean executableOwnerOnly) {
	   setParentR(p, readable, readableOwnerOnly);
	   setParentW(p, writable, writableOwnerOnly);
	   setParentX(p, executable, executableOwnerOnly);
   }
   private static void setParentR(File p, Boolean readable, Boolean readableOwnerOnly) {
	   if(readable!=null) {
			if(readableOwnerOnly!=null) {
				if(!p.getParentFile().setReadable(readable, readableOwnerOnly)) {
					// ignore
				}
			}
			else {
				if(!p.getParentFile().setReadable(readable)) {
					// ignore
				}
			}
	   }
   }
   private static void setParentW(File p, Boolean writable, Boolean writableOwnerOnly) {
	   if(writable!=null) {
			if(writableOwnerOnly!=null) {
				if(!p.getParentFile().setWritable(writable, writableOwnerOnly)) {
					// ignore
				}
			}
			else {
				if(!p.getParentFile().setWritable(writable)) {
					// ignore
				}
			}
		}
   }
   private static void setParentX(File p, Boolean executable, Boolean executableOwnerOnly) {
		if(executable!=null) {
			if(executableOwnerOnly!=null) {
				if(!p.getParentFile().setExecutable(executable, executableOwnerOnly)) {
					// ignore
				}
			}
			else {
				if(!p.getParentFile().setExecutable(executable)) {
					// ignore
				}
			}
		}
   }
   
   public static void mkdir(String f) throws UtilsException{
	   mkdir(f, new FileSystemMkdirConfig());
   }
   public static void mkdir(String f,
		   Boolean readable, Boolean readableOwnerOnly,
		   Boolean writable, Boolean writableOwnerOnly,
		   Boolean executable, Boolean executableOwnerOnly) throws UtilsException{
	   mkdir(f, new FileSystemMkdirConfig(),
			   new FileRWXConfig(readable, readableOwnerOnly,
					   writable, writableOwnerOnly,
					   executable, executableOwnerOnly));
   }
   public static void mkdir(String f,
		   FileRWXConfig rwxConfig) throws UtilsException{
	   mkdir(f, new FileSystemMkdirConfig(), rwxConfig);
   }
   public static void mkdir(String f, FileSystemMkdirConfig config) throws UtilsException{
	   File dir = new File(f);
	   mkdir(dir, config);
   }
   public static void mkdir(String f, FileSystemMkdirConfig config,
		   FileRWXConfig rwxConfig) throws UtilsException{
	   File dir = new File(f);
	   mkdir(dir, config, rwxConfig);
   }
   
   public static void mkdir(File dir) throws UtilsException{
	   mkdir(dir, new FileSystemMkdirConfig());
   }
   public static void mkdir(File dir,
		   Boolean readable, Boolean readableOwnerOnly,
		   Boolean writable, Boolean writableOwnerOnly,
		   Boolean executable, Boolean executableOwnerOnly) throws UtilsException{
	   mkdir(dir, new FileSystemMkdirConfig(),
			   new FileRWXConfig(readable, readableOwnerOnly,
					   writable, writableOwnerOnly,
					   executable, executableOwnerOnly));
   }
   public static void mkdir(File dir, FileRWXConfig rwxConfig) throws UtilsException{
	   mkdir(dir, new FileSystemMkdirConfig(), rwxConfig);
   }
   public static void mkdir(File dir, FileSystemMkdirConfig config) throws UtilsException{
	   mkdir(dir, config, null);
   }
   public static void mkdir(File dir, FileSystemMkdirConfig config,
		   FileRWXConfig rwxConfig) throws UtilsException{
	   
	   Boolean readable = null;
	   Boolean readableOwnerOnly = null;
	   Boolean writable = null;
	   Boolean writableOwnerOnly = null;
	   Boolean executable = null;
	   Boolean executableOwnerOnly = null;
	   if(rwxConfig!=null) {
		   readable = rwxConfig.getReadable();
		   readableOwnerOnly = rwxConfig.getReadableOwnerOnly();
		   writable = rwxConfig.getWritable();
		   writableOwnerOnly = rwxConfig.getWritableOwnerOnly();
		   executable = rwxConfig.getExecutable();
		   executableOwnerOnly = rwxConfig.getExecutableOwnerOnly();
	   }
	   
	   if(dir.exists()){
		   checkDir(dir, config);
		}
		else{
			if(dir.getParentFile()!=null) {
				File parent = dir.getParentFile();
				if(!parent.exists() &&
					config.crateParentIfNotExists) {
					mkdirParentDirectory(dir,
							readable, readableOwnerOnly,
							writable, writableOwnerOnly,
							executable, executableOwnerOnly);
				}
			}
			if(!dir.mkdir()){
				throw new UtilsException("Creazione directory ["+dir.getAbsolutePath()+"] non riuscita");
			}
			else {
				setR(dir, readable, readableOwnerOnly);
				setW(dir, writable, writableOwnerOnly);
				setX(dir, executable, executableOwnerOnly);
			}
		}
   }
   private static void checkDir(File dir, FileSystemMkdirConfig config) throws UtilsException {
	   if(!dir.isDirectory()){
			throw new UtilsException("File ["+dir.getAbsolutePath()+"] is not directory");
		}
		if(config.isCheckCanRead() &&
			!dir.canRead()){
			throw new UtilsException(DIRECTORY_PREFIX_MSG+dir.getAbsolutePath()+"] cannot read");
		}
		if(config.isCheckCanWrite() &&
			!dir.canWrite()){
			throw new UtilsException(DIRECTORY_PREFIX_MSG+dir.getAbsolutePath()+"] cannot write");
		}
		if(config.isCheckCanExecute() &&
			!dir.canExecute()){
			throw new UtilsException(DIRECTORY_PREFIX_MSG+dir.getAbsolutePath()+"] cannot execute");
		}
   }
   private static void setR(File dir, Boolean readable, Boolean readableOwnerOnly) {
	   if(readable!=null) {
			if(readableOwnerOnly!=null) {
				if(!dir.setReadable(readable, readableOwnerOnly)) {
					// ignore
				}
			}
			else {
				if(!dir.setReadable(readable)) {
					// ignore
				}
			}
		}
   }
   private static void setW(File dir, Boolean writable, Boolean writableOwnerOnly) {
	   if(writable!=null) {
			if(writableOwnerOnly!=null) {
				if(!dir.setWritable(writable, writableOwnerOnly)) {
					// ignore
				}
			}
			else {
				if(!dir.setWritable(writable)) {
					// ignore
				}
			}
		}
   }
   private static void setX(File dir, Boolean executable, Boolean executableOwnerOnly) {
	   if(executable!=null) {
			if(executableOwnerOnly!=null) {
				if(!dir.setExecutable(executable, executableOwnerOnly)) {
					// ignore
				}
			}
			else {
				if(!dir.setExecutable(executable)) {
					// ignore
				}
			}
		}
   }
   
   private static final String DIRECTORY_PREFIX_MSG = "Directory [";
}
