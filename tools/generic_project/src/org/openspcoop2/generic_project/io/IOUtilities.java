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
package org.openspcoop2.generic_project.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IOUtilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IOUtilities {

	public static void copy(String in, String out) 
	throws IOException {
		FileInputStream fin = null;
		FileOutputStream fout = null;
		try{
			fin = new FileInputStream(in);
			fout = new FileOutputStream(out);
			IOUtilities.copy(fin,fout);
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
		IOUtilities.copyDirectory(new File(srcPath), new File(dstPath));
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
					IOUtilities.copyDirectory(new File(srcPath, files[i]), 
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
	
	
	public static void writeFile(File f,byte[] contenuto)throws Exception{
		FileOutputStream fos =new FileOutputStream(f);
		fos.write(contenuto);
		fos.flush();
		fos.close();
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
	
	public static void copyFileAndReplaceAllKeywords(File read,File write,String keyword,String values) throws Exception{
		String[]k = new String[1];
		k[0] = keyword;
		String[]v = new String[1];
		v[0] = values;
		IOUtilities.copyFileAndReplaceAllKeywords(read, write, k, v);
	}
	/*public static void copyFileAndReplaceAllKeywords(File read,File write,String[] keyword,String[] values) throws Exception{
		String file = IOUtilities.readFile(read);
		for(int i=0; i<keyword.length; i++){
			
			while(file.contains(keyword[i])){
				file = file.replace(keyword[i], values[i]);
			}
		}
		IOUtilities.writeFile(write, file.getBytes());
	}*/
	public static void copyFileAndReplaceAllKeywords(File read,File write,String[] keyword,String[] values) throws Exception{
		String file = IOUtilities.readFile(read);
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
		IOUtilities.writeFile(write, file.getBytes());
	}
	
	public static void copyFileAndReplaceKeywords(File read,File write,String keyword,String values) throws Exception{
		String[]k = new String[1];
		k[0] = keyword;
		String[]v = new String[1];
		v[0] = values;
		IOUtilities.copyFileAndReplaceKeywords(read, write, k, v);
	}
	public static void copyFileAndReplaceKeywords(File read,File write,String[] keyword,String[] values) throws Exception{
		String file = IOUtilities.readFile(read);
		for(int i=0; i<keyword.length; i++){
		    file = file.replace(keyword[i], values[i]);
		}
		IOUtilities.writeFile(write, file.getBytes());
	}


    
    
    public static boolean deleteDir(File dir) {
    	 if (dir.isDirectory()) {
             String[] children = dir.list();
             for (int i=0; i<children.length; i++) {
                 boolean success = IOUtilities.deleteDir(new File(dir, children[i]));
                 if (!success) {
                     return false;
                 }
             }
         }

         // The directory is now empty so now it can be smoked
         boolean delete = dir.delete();
         return delete;
    }

    
    public static void moveToDir(String src,String destDir) throws Exception{
    	 // File (or directory) to be moved
        File file = new File(src);
        
        // Destination directory
        File dir = new File(destDir);
        
        IOUtilities.moveToDir(file,dir);
    }
    public static void moveToDir(File src,File destDir) throws Exception{

        // Move file to new directory
    	IOUtilities.copy(src.getAbsolutePath(), destDir.getAbsolutePath()+File.separatorChar+src.getName());
		if(src.isDirectory()){
			if(IOUtilities.deleteDir(src)==false){
				throw new Exception("Directory ["+src.getAbsolutePath()+"] non eliminabile");
			}
		}else{
			if(src.delete()==false){
				throw new Exception("File ["+src.getAbsolutePath()+"] non eliminabile");
			}
		}
    }
    
    public static void moveToFile(String src,String destFile)throws Exception{
   	 // File (or directory) to be moved
       File file = new File(src);
       
       // Destination directory
       File dir = new File(destFile);
       
       IOUtilities.moveToFile(file,dir);
   }
   public static void moveToFile(File src,File destFile)throws Exception{

       // Move file to new directory
	   IOUtilities.copy(src.getAbsolutePath(), destFile.getAbsolutePath());
	   if(src.isDirectory()){
			if(IOUtilities.deleteDir(src)==false){
				throw new Exception("Directory ["+src.getAbsolutePath()+"] non eliminabile");
			}
		}else{
			if(src.delete()==false){
				throw new Exception("File ["+src.getAbsolutePath()+"] non eliminabile");
			}
		}
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
			IOUtilities.mkdirParentDirectory(p.getParentFile().getAbsolutePath());
			if(p.getParentFile().mkdir()==false){
				throw new Exception("Directory ["+p.getParentFile().getAbsolutePath()+"] non esistente e creazione non riuscita");
			}
		}catch(Exception e){
			throw new Exception("mkdirParentDirectory non riuscito: "+e.getMessage(),e);
		}
	}
}
