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


package org.openspcoop2.utils.resources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Classe utilizzabile per raccogliere utility su operazioni effettuate su file system
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileSystemUtilities {

	public static void copy(File in, File out) throws IOException {
		FileSystemUtilities.copy(in.getAbsolutePath(),out.getAbsolutePath());
	}
	public static void copy(String in, String out) 
	throws IOException {
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try{
			fin = new FileInputStream(in);
			fout = new FileOutputStream(out);
			FileSystemUtilities.copy(fin,fout);
			fout.flush();
		}finally{
			try{
				if(fin!=null)
					fin.close();
			}catch(Exception e){}
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception e){}
		}
	}
	public static void copy(InputStream in, OutputStream out) 
	throws IOException {

		// do not allow other threads to read from the
		// input or write to the output while copying is
		// taking place

		synchronized (in) {
			synchronized (out) {

				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					if (bytesRead == -1) break;
					out.write(buffer, 0, bytesRead);
				}
			}
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

			String files[] = srcPath.list();

			for(int i = 0; i < files.length; i++){
				if(".svn".equals(files[i])==false){
					FileSystemUtilities.copyDirectory(new File(srcPath, files[i]), 
							new File(dstPath, files[i]));
				}
			}
		}
		else{
			if(!srcPath.exists()){
				//System.out.println("File or directory does not exist.");
				return;
			}
			else {
				InputStream in = new FileInputStream(srcPath);
				OutputStream out = new FileOutputStream(dstPath); 
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		}
		//System.out.println("Directory copied.");
	}
	
	
	public static String readFile(String f) throws Exception{
		return FileSystemUtilities.readFile(new File(f));
	}
	public static String readFile(File f) throws Exception{
		FileInputStream fis =new FileInputStream(f);
		ByteArrayOutputStream byteInputBuffer = new ByteArrayOutputStream();
		byte [] readB = new byte[8192];
		int readByte = 0;
		while((readByte = fis.read(readB))!= -1){
			byteInputBuffer.write(readB,0,readByte);
		}
		fis.close();
		byteInputBuffer.flush();
		byteInputBuffer.close();
		
		return  byteInputBuffer.toString();
	}
	
	public static byte[] readBytesFromFile(String f) throws Exception{
		return FileSystemUtilities.readBytesFromFile(new File(f));
	}
	public static byte[] readBytesFromFile(File f) throws Exception{
		FileInputStream fis =new FileInputStream(f);
		ByteArrayOutputStream byteInputBuffer = new ByteArrayOutputStream();
		byte [] readB = new byte[8192];
		int readByte = 0;
		while((readByte = fis.read(readB))!= -1){
			byteInputBuffer.write(readB,0,readByte);
		}
		fis.close();
		byteInputBuffer.flush();
		byteInputBuffer.close();
		
		return  byteInputBuffer.toByteArray();
	}
	
	
	public static void writeFile(String f,byte[] contenuto)throws Exception{
		FileSystemUtilities.writeFile(new File(f), contenuto);
	}
	public static void writeFile(File f,byte[] contenuto)throws Exception{
		FileOutputStream fos =new FileOutputStream(f);
		fos.write(contenuto);
		fos.flush();
		fos.close();
	}
	public static void writeFile(String f,byte[] ... args)throws Exception{
		FileSystemUtilities.writeFile(new File(f), args);
	}
	public static void writeFile(File f,byte[] ... args)throws Exception{
		if(args!=null){
			FileOutputStream fos =new FileOutputStream(f);
			for(int i=0; i<args.length; i++){
				fos.write(args[i]);
			}
			fos.flush();
			fos.close();
		}
	}
	
	public static void copyFileAndReplaceAllKeywords(String read,String write,String keyword,String values) throws Exception{
		FileSystemUtilities.copyFileAndReplaceAllKeywords(new File(read),new File(write),keyword,values);
	}
	public static void copyFileAndReplaceAllKeywords(File read,File write,String keyword,String values) throws Exception{
		String[]k = new String[1];
		k[0] = keyword;
		String[]v = new String[1];
		v[0] = values;
		FileSystemUtilities.copyFileAndReplaceAllKeywords(read, write, k, v);
	}
	public static void copyFileAndReplaceAllKeywords(String read,String write,String[] keyword,String[] values) throws Exception{
		FileSystemUtilities.copyFileAndReplaceAllKeywords(new File(read),new File(write),keyword,values);
	}
	public static void copyFileAndReplaceAllKeywords(File read,File write,String[] keyword,String[] values) throws Exception{
		String file = FileSystemUtilities.readFile(read);
		for(int i=0; i<keyword.length; i++){
			
			//System.out.println("FILE ["+file+"] contains ["+keyword[i]+"] (value:"+file.indexOf(keyword[i])+")");
			int indexOf = file.indexOf(keyword[i]);
			while(indexOf>=0){
				//System.out.println("REPLACE ["+keyword[i]+"] ["+values[i]+"]");
				file = file.replace(keyword[i], values[i]);
				indexOf = file.indexOf(keyword[i],indexOf+values[i].length());
				//System.out.println("INDEX OF ["+indexOf+"]");
			}
		}
		FileSystemUtilities.writeFile(write, file.getBytes());
	}
	
	public static void copyFileAndReplaceKeywords(String read,String write,String keyword,String values) throws Exception{
		FileSystemUtilities.copyFileAndReplaceKeywords(new File(read),new File(write),keyword,values);
	}
	public static void copyFileAndReplaceKeywords(File read,File write,String keyword,String values) throws Exception{
		String[]k = new String[1];
		k[0] = keyword;
		String[]v = new String[1];
		v[0] = values;
		FileSystemUtilities.copyFileAndReplaceKeywords(read, write, k, v);
	}
	
	public static void copyFileAndReplaceKeywords(String read,String write,String[] keyword,String[] values) throws Exception{
		FileSystemUtilities.copyFileAndReplaceKeywords(new File(read),new File(write),keyword,values);
	}
	public static void copyFileAndReplaceKeywords(File read,File write,String[] keyword,String[] values) throws Exception{
		String file = FileSystemUtilities.readFile(read);
		for(int i=0; i<keyword.length; i++){
		    file = file.replace(keyword[i], values[i]);
		}
		FileSystemUtilities.writeFile(write, file.getBytes());
	}


	public static boolean deleteDir(String dir) {
		File d = new File(dir);
		if(d.exists()==false){
			return true;
		}
		return FileSystemUtilities.deleteDir(d);
	}
	
    public static boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = FileSystemUtilities.deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so now it can be smoked
        return dir.delete();
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
        boolean success = src.renameTo(new File(destDir, src.getName()));
        return success;
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
       boolean success = src.renameTo(destFile);
       return success;
   }
   
   public static void mkdirParentDirectory(File file) throws Exception {
	   FileSystemUtilities.mkdirParentDirectory(file.getAbsolutePath());
   }
   public static void mkdirParentDirectory(String file) throws Exception {
		try{
			File p = new File(file);
			if(p.getParentFile()==null){
				return;
			}
			if(p.getParentFile().exists()){
				return;
			}
			FileSystemUtilities.mkdirParentDirectory(p.getParentFile().getAbsolutePath());
			if(p.getParentFile().mkdir()==false){
				throw new Exception("Directory ["+p.getParentFile().getAbsolutePath()+"] non esistente e creazione non riuscita");
			}
		}catch(Exception e){
			throw new Exception("mkdirParentDirectory non riuscito: "+e.getMessage(),e);
		}
	}
   
}
